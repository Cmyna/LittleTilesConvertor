package schematicToLittleTiles;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class LTTransform {
	
	
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
		System.out.println(id+" "+table.id2name.get(id));
		return str;
	}
	
	private static String writeOtherMessage(int grid, int count, int[] size) {// write like grid:X,count:X,size:[I,X,X,X]
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
}
