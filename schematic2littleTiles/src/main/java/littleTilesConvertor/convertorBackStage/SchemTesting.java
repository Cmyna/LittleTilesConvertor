package littleTilesConvertor.convertorBackStage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Paths;

public class SchemTesting {
	
	
	public static McTable table = getTableV112();
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SchemTesting r = new SchemTesting();
		String tableDir = r.getClass().getClassLoader().getResource("")+"idTable/";
		String sample = getResources("example/MediumTest.schematic");
		try {
			schemReadIn.ScheToLTTesting(sample);
			//System.out.println(table.id2name.get(0));
			//System.out.println(table.name2id.get("minecraft:red_flower:8"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static String reformURL(String in) throws UnsupportedEncodingException {
		return URLDecoder.decode(in.substring(5),"UTF-8");
	}
	
	public static McTable getTableV112() {
		SchemTesting r = new SchemTesting();
		String tableDir = r.getClass().getClassLoader().getResource("")+"idTable/";
		try {
			tableDir = reformURL(tableDir);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		McTable table = null;
		try {
			table = McTable.getMCMap(tableDir+"minecraft1.12_blockIDMap.csv");
			//if (table == null) System.out.println("table is null");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return table;
	}
	
	public static String getResources(String in) {
		SchemTesting r = new SchemTesting();
		String out = r.getClass().getClassLoader().getResource("") + in;
		try {
			out = reformURL(out);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return out;
	}
}
