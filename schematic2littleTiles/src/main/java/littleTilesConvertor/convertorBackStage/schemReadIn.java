package littleTilesConvertor.convertorBackStage;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;

import com.flowpowered.nbt.ByteArrayTag;
import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.ListTag;
import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.TagType;
import com.flowpowered.nbt.stream.NBTInputStream;

public class schemReadIn {
	
	public static int grid = 32;
	
	public static BlockBuffer read(File file) {
		NBTInputStream input;
		BlockBuffer bb = null;
		try {
			input = new NBTInputStream(new FileInputStream(file));
			CompoundTag comp = (CompoundTag)input.readTag();
			CompoundMap map = comp.getValue();
			
			int length,width,height;
			length=width=height=0;
			int[][][] blocks = new int[width][height][length];
			int[][][] data = new int[width][height][length];
			
			for (Tag<?> t : map.values()) {
				//System.out.println(t.getName());
				if (t.getName().contentEquals("Length")) length = (int)(short) t.getValue();
				if (t.getName().contentEquals("Width")) width = (int)(short) t.getValue();
				if (t.getName().contentEquals("Height")) height = (int)(short) t.getValue();
				
			}
			
			for (Tag<?> t : map.values()) {
				if (t.getName().contentEquals("Data")) {
					data = parseBlockArray(width,height,length,(byte[])t.getValue());
				}
				if (t.getName().contentEquals("Blocks")) {
					blocks = parseBlockArray(width,height,length,(byte[])t.getValue());
				}
				if (t.getName().contentEquals("Entities")) {
					String a = ((ListTag)t).getElementType().toString();
					System.out.println(a);
					((ListTag)t).getValue();
				}
			}
			
			bb = new BlockBuffer(width,height,length,blocks,data,16);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//read outside unamed compoundtag
		
		return bb;
	}
	
	public static void ScheToLTTesting(String filename) throws FileNotFoundException, IOException{
		List<Tag> tags = new ArrayList<Tag>();
		File file = new File(filename);
		NBTInputStream input = new NBTInputStream(new FileInputStream(file));
		//read outside unamed compoundtag
		CompoundTag comp = (CompoundTag)input.readTag();
		
		//a map that contains compound tag's sub tags
		System.out.println(comp.getName());
		CompoundMap map = comp.getValue();
		//find length,width,height and blocks,Data tags, and store
		int length,width,height;
		length=width=height=0;
		int[][][] blocks = new int[width][height][length];
		int[][][] data = new int[width][height][length];
		
		
		for (Tag<?> t : map.values()) {
			//System.out.println(t.getName());
			if (t.getName().contentEquals("Length")) length = (int)(short) t.getValue();
			if (t.getName().contentEquals("Width")) width = (int)(short) t.getValue();
			if (t.getName().contentEquals("Height")) height = (int)(short) t.getValue();
			
		}
		System.out.println("length:"+length+" width:"+width+" height:"+height);
		
		for (Tag<?> t : map.values()) {
			if (t.getName().contentEquals("Data")) {
				data = parseBlockArray(width,height,length,(byte[])t.getValue());
			}
			if (t.getName().contentEquals("Blocks")) {
				blocks = parseBlockArray(width,height,length,(byte[])t.getValue());
			}
			if (t.getName().contentEquals("Entities")) {
				String a = ((ListTag)t).getElementType().toString();
				System.out.println(a);
				((ListTag)t).getValue();
			}
		}
		long b1,b2;
		b1 = System.currentTimeMillis();
		BlockBuffer buffer = new BlockBuffer(width,height,length,blocks,data,16);
		b2 = System.currentTimeMillis();
		System.out.println(b2-b1);
		System.out.println("start greedy meshing");
		buffer.greedyMeshing();
		b1 = System.currentTimeMillis();
		System.out.println(b1-b2);
		toLTString(buffer);
		b2 = System.currentTimeMillis();
		System.out.println(b2-b1);
	}
	
	public static int[][][] parseBlockArray(int w, int h, int l, byte[] in) {
		int[][][] out = new int[w][h][l];
		for (int i=0; i<w; i++) {
			for (int j=0; j<h; j++) {
				for (int k=0; k<l; k++) {
					out[i][j][k] = in[i+k*w+j*l*w];//height>length>width w+l*w+h*l*w -> i+k*w+j*l*w
				}
			}
		}
		
		return out;
	}
	
	public static void toLTString(BlockBuffer buffer) {
		//table = McTable.getMCMap( SchemReader.tableDir+"minecraft1.12_blockIDMap.csv");
		McTable table = SchemTesting.getTableV112();
		String ans = buffer.getLTJson();
		//System.out.println(ans);
		WriteStringToFile(SchemTesting.getResources("/output/out.txt"),ans);
	}
	
	public static void WriteStringToFile(String filePath, String str) {
        try {
            File file = new File(filePath);
            PrintStream ps = new PrintStream(new FileOutputStream(file));
            ps.println(str);// 往文件里写入字符串
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
	
}
