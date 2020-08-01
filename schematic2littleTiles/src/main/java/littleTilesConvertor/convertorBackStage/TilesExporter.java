package littleTilesConvertor.convertorBackStage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.flowpowered.nbt.ByteArrayTag;
import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.ListTag;
import com.flowpowered.nbt.ShortTag;
import com.flowpowered.nbt.StringTag;
import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.stream.NBTOutputStream;

public class TilesExporter {
	
	public static CompoundTag constructSchematic(BlockBuffer buffer) {
		int[] size = buffer.getSize();
		int w = size[0];
		int h = size[1];
		int l = size[2];
		int[][][] data = buffer.getBlockData();
		
		byte[] exblock = new byte[w*h*l];
		byte[] exmeta = new byte[w*h*l];
		
		for (int i=0;i<size[0];i++) 
			for (int j=0;j<size[1];j++) 
				for (int k=0;k<size[2];k++) {
					exblock[i+k*w+j*l*w] = (byte) (data[i][j][k]>>8);
					exmeta[i+k*w+j*l*w] = (byte) (data[i][j][k]&0xff);
				}
		
		
		ByteArrayTag scblock = new ByteArrayTag("Blocks", exblock);
		ByteArrayTag scmeta = new ByteArrayTag("Data",exmeta);
		StringTag materials = new StringTag("Materials","Alpha");
		ShortTag width = new ShortTag("Width",(short)w);
		ShortTag height = new ShortTag("Height",(short)h);
		ShortTag length = new ShortTag("Length",(short)l);
		ListTag nullEntities = new ListTag("Entities", CompoundTag.class, new ArrayList<Tag>());
		ListTag nullTileEntities = new ListTag("TileEntities", CompoundTag.class, new ArrayList<Tag>());
		
		CompoundMap scheMap = new CompoundMap();
		scheMap.put(scblock);
		scheMap.put(scmeta);
		scheMap.put(materials);
		scheMap.put(width);
		scheMap.put(height);
		scheMap.put(length);
		scheMap.put(nullEntities);
		scheMap.put(nullTileEntities);
		
		CompoundTag scheComp = new CompoundTag("Schematic", scheMap);
		
		return scheComp;
	}
	
	public static void writeSchematic(String filename, CompoundTag sche) throws IOException {
		NBTOutputStream out = new NBTOutputStream(new FileOutputStream(new File(filename)));
		out.writeTag(sche);
		out.close();
	}
	
	
	private static String writeNullBrace(String str) { // write {}
		str = "{"+str+"}";
		return str;
	}
	
	private static StringBuilder writeNullBrace(StringBuilder str) {
		str.insert(0, "{");
		str.append("}");
		return str;
	}
	
	private static String writeBoxPos(Box box) {// write something like '[I;0,0,0,16,8,8]'
		//well... the pos2 should +1 because the lt format is this
		//every one should add gridpos
		StringBuilder str = new StringBuilder("");
		
		str.append("[I;");
		int[] n = box.getPos();
		for (int i=0; i<n.length; i++) {
			str.append(i<3?n[i]:(n[i]+1));
			str.append(",");
		}
		str.deleteCharAt(str.length()-1);
		str.append("]");
		return str.toString();
	}
	
	private static String writeBoxTile(McTable table, int id) {// write something like 'minecraft:air:0'
		StringBuilder str = new StringBuilder("");
		str.append("tile:{block:\"");
		str.append(table.id2name.get(id));
		str.append("\"}");
		return str.toString();
	}
	
	private static String writeOtherMessage(int grid, int count, int[] size, int[] min) {
		// write like grid:X,count:X,size:[I,X,X,X],min[I,X,X,X]
		StringBuilder str = new StringBuilder("grid:");
		str.append(grid);
		str.append(",count:");
		str.append(count);
		str.append(",size:[I;");
		for (int i: size) {
			str.append(i);
			str.append(",");
		}
		str.deleteCharAt(str.length()-1);
		str.append("],");
		str.append("min:[I;");
		for (int i:min) {
			str.append(i);
			str.append(",");
		}
		str.deleteCharAt(str.length()-1);
		str.append("]");
		/*String str = "";
		
		str += "grid:"+grid;
		str += ","+"count:"+count;
		str += ",size:[I;";
		for (int i:size) {
			str += i+",";
		}
		str = str.substring(0,str.length()-1);
		str +="]";*/
		return str.toString();
	}
	
	/*private static StringBuilder writeOtherMessage(StringBuilder str, int grid, int count, int[] size) {
		
		
		return str;
	}*/
	
	/*private static String writeBoxes(List<Box> list, McTable table) {
		// write like {boxes:[&BOX,&BOX,&BOX],&TILE}
		// or like bbox:&BOX,&TILE
		String str = "";
		
		
		int size = list.size();
		if (size==1) {
			str += "bBox:";
			str += writeBoxPos(list.get(0));
			str += ",";
			str += writeBoxTile(table, list.get(0).getBlockID());
			return writeNullBrace(str);
		}
		if (size==0) return str;
		
		str += "boxes:[";
		for (Box b : list) {
			str += writeBoxPos(b) + ",";
		}
		str = str.substring(0,str.length()-1);
		str += "],";
		str += writeBoxTile(table, list.get(0).getBlockID());
		return writeNullBrace(str);
	}*/
	
	public static String writeBoxes( List<Box> list, McTable table) {
		// write like {boxes:[&BOX,&BOX,&BOX],&TILE}
		// or like bbox:&BOX,&TILE
		StringBuilder str = new StringBuilder("");
		
		int size = list.size();
		if (size==1) {
			str.append("bBox:");
			str.append(writeBoxPos(list.get(0)));
			str.append(",");
			str.append(writeBoxTile(table, list.get(0).getBlockID()));
			return writeNullBrace(str).toString();
		}
		if (size==0) return str.toString();
		
		str.append("boxes:[");
		for (Box b : list) {
			str.append(writeBoxPos(b));
			str.append(",");
		}
		str.deleteCharAt(str.length()-1);
		str.append("],");
		str.append(writeBoxTile(table, list.get(0).getBlockID()));
		
		return writeNullBrace(str).toString();
	}
	
	private static void writeBlockTiles(StringBuilder str, Map<Integer,List<Box>> map, McTable table) {
		// write like tiles:[&boxes,&boxes,&boxes]
		//StringBuilder str = new StringBuilder("");
		str.append("tiles:[");

		Set<Entry<Integer, List<Box>>> entries = map.entrySet();
		for (Entry<Integer, List<Box>> e : entries) {
			str.append(writeBoxes(e.getValue(),table));
			str.append(",");
		}
		str.deleteCharAt(str.length()-1);
		
		str.append("]");
		
		//return str.toString();
	}
	
	
	public static String writeLT(Map<Integer,List<Box>> map, McTable table, int grid, int[] size, int[] min) {
		//write like {&tiles,&othermessage}
		StringBuilder str = new StringBuilder("");
		
		int c = 0;
		Set<Entry<Integer, List<Box>>> entries = map.entrySet();
		for (Entry<Integer, List<Box>> e : entries) {
			c += e.getValue().size();
		}
		
		//str += writeBlockTiles(map, table) + ",";
		//str += writeOtherMessage(grid,c,size);
		writeBlockTiles(str, map, table);
		str.append(",");
		str.append(writeOtherMessage(grid,c,size,min));
		
		return writeNullBrace(str).toString();
		//return str.toString();
	}
	
	public static void main(String args[]) {
		String sample = SchemTesting.getResources("/output/out.txt");
		File f = new File(sample);
		
		FileInputStream in;
		try {
			
			in = new FileInputStream(f);
			int size=in.available();
			byte[] buffer=new byte[size];
			in.read(buffer);
	        in.close();
	        String lt=new String(buffer,"GB2312");
	        BlockBuffer bb = new BlockBuffer(lt,SchemTesting.getTableV112());
	        bb.LTtoSchem();
	        
	        writeSchematic(SchemTesting.getResources("/output/out.schematic"), constructSchematic(bb));
	        
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
