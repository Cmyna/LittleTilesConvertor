package littleTilesConvertor.convertorBackStage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class McTable {
	//id:meta -- name:meta
	public Map<Integer,String> id2name;
	public Map<String,Integer> name2id;
	
	public static McTable getMCMap(String filename) throws FileNotFoundException{
		return new McTable(new File(filename));
	}
	
	
	
	private McTable(File file) throws FileNotFoundException {
		id2name = new HashMap<Integer,String>();
		name2id = new HashMap<String,Integer>();
		
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = null;
		try {
			while((line=br.readLine())!=null) {
				String[] item = line.split(",");
				/*mcID id = new mcID(Integer.parseInt(item[3]),Integer.parseInt(item[2]));
				mcName name = new mcName(Integer.parseInt(item[3]),item[1]);
				id2name.put(Integer.parseInt(item[2]), name);
				name2id.put(item[1], id);*/
				String name = item[1]+":"+item[3];
				int id = Integer.parseInt(item[3])+(Integer.parseInt(item[2])<<8);
				id2name.put(id, name);
				name2id.put(name, id);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	class mcID {
		private int meta;
		private int id;
		
		protected mcID(int m, int i) {
			meta=m;
			id=i;
		}
		
		public int getmeta() {
			return meta;
		}
		
		public int getID() {
			return id;
		}
	}
	
	class mcName {
		private int meta;
		private String name;
		
		protected mcName(int m, String n) {
			meta=m;
			name=n;
		}
		
		public int getmeta() {
			return meta;
		}
		
		public String getName() {
			return name;
		}
	}
}
