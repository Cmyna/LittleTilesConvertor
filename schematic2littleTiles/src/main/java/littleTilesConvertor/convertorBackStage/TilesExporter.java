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
	
	private static String writeBracket(String str, String name, String content) {//write somethin like name:[]
		str += name+":[" + content + "]";
		return str;
	}
	
	private static String writeBoxPos(Box box) {// write something like '[I;0,0,0,16,8,8]'
		//well... the pos2 should +1 because the lt format is this
		//every one should add gridpos
		String str = "";
		str += "[I;";
		int[] n = box.getPos();
		for (int i=0; i<n.length; i++) {
			str += (i<3?n[i]:(n[i]+1)) + ",";
		}
		str = str.substring(0, str.length()-1);
		str += "]";
		return str;
	}
	
	private static String writeBoxTile(McTable table, int id) {// write something like 'minecraft:air:0'
		String str = "";
		str += "tile:{block:\"";
		str += table.id2name.get(id);
		str += "\"}";
		System.out.println("[to LT json]: "+id+" "+table.id2name.get(id));
		return str;
	}
	
	private static String writeOtherMessage(int grid, int count, int[] size) {
		// write like grid:X,count:X,size:[I,X,X,X]
		String str = "";
		
		str += "grid:"+grid;
		str += ","+"count:"+count;
		str += ",size:[I;";
		for (int i:size) {
			str += i+",";
		}
		str = str.substring(0,str.length()-1);
		str +="]";
		return str;
	}
	
	private static String writeBoxes(List<Box> list, McTable table) {
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
	}
	
	private static String writeBlockTiles(Map<Integer,List<Box>> map, McTable table) {
		// write like tiles:[&boxes,&boxes,&boxes]
		String str = "";

		Set<Entry<Integer, List<Box>>> entries = map.entrySet();
		String content = "";
		for (Entry<Integer, List<Box>> e : entries) {
			System.out.println(e.getValue());
			content += writeBoxes(e.getValue(),table) + ",";
		}
		//System.out.println(content);
		content = content.substring(0,content.length()-1);
		
		str = writeBracket(str,"tiles",content);
		
		
		return str;
	}
	
	public static String writeLT(Map<Integer,List<Box>> map, McTable table, int grid, int[] size) {
		//write like {&tiles,&othermessage}
		String str = "";
		
		int c = 0;
		Set<Entry<Integer, List<Box>>> entries = map.entrySet();
		for (Entry<Integer, List<Box>> e : entries) {
			c += e.getValue().size();
		}
		
		str += writeBlockTiles(map, table) + ",";
		str += writeOtherMessage(grid,c,size);
		
		return writeNullBrace(str);
	}
	
	public static void main(String args[]) {
		String sample = "D:\\out.txt";
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
	        
	        writeSchematic("D:\\out.schematic", constructSchematic(bb));
	        
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
