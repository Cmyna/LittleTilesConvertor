package schematicToLittleTiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class LittleTiles {
	
	private int grid;
	
	public LittleTiles(String json) {
		JsonObject root = JsonParser.parseString(json).getAsJsonObject();
		grid = root.get("grid").getAsInt();
	}
	
	public static void main (String args[]) {
		String lt = "";
		String test = "D:\\out.txt";
		File f = new File(test);
		
		try {
			
			FileInputStream in=new FileInputStream(f);
			int size=in.available();
			byte[] buffer=new byte[size];
			in.read(buffer);
	        in.close();
	        lt=new String(buffer,"GB2312");
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JsonElement root = JsonParser.parseString(lt);
		System.out.println(root.toString());
		System.out.println();
		JsonElement a = root.getAsJsonObject().get("tiles").getAsJsonArray().get(0);
		System.out.println(a.toString());
		String s = a.getAsJsonObject().get("boxes").getAsJsonArray().get(0).getAsJsonArray().get(4).toString();
		System.out.println(s);
		
		int grid = root.getAsJsonObject().get("grid").getAsInt();
		System.out.println(grid);
		
		JsonArray ss = root.getAsJsonObject().getAsJsonArray("tiles").get(0).getAsJsonObject().getAsJsonArray("bBox");
		if (ss == null) System.out.println("ss is null");
		else System.out.println(ss.toString());
	}
	
}
