package schematicToLittleTiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.flowpowered.nbt.ByteArrayTag;
import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.IntArrayTag;
import com.flowpowered.nbt.ListTag;
import com.flowpowered.nbt.ShortArrayTag;
import com.flowpowered.nbt.Tag;

public class SchemStructure {
	
	private int length;
	private int width;
	private int height;
	public int[][][] data;//[length][width][height]  data struture: block:meta // change to w,h,l
	private Map<Integer,List<Box>> littleTiles;
	public boolean[][][] mark;
	
	private List<Grid> divided;//we know w h l, then divided the whole struct into several block according grid size
	//always regard 0 0 0 at start point, if not enough space, add 0 element to combine a new block
	private int grid;
	private boolean sche;
	private boolean lt;
	
	public SchemStructure(int w,int h,int l,int[][][] blocks,int[][][] meta) {
		length = l;
		width = w;
		height = h;
		data = new int[w][h][l];
		mark = new boolean[w][h][l];
		littleTiles = new HashMap<Integer,List<Box>>();
		//divided = new LinkedList<int[][][]>();
		for (int i=0; i<width; i++) {
			for (int j=0; j<height; j++) {
				for (int k=0; k<length; k++) {
					data[i][j][k] = (meta[i][j][k]&0xff) + ((blocks[i][j][k]&0xff)<<8);
					//data[i][j][k] = meta[i][j][k] + (blocks[i][j][k]<<8);
					mark[i][j][k] = false;
					
				}
			}
		}
		lt = false;
		sche = true;
	}
	
	public SchemStructure(int w, int h, int l, String ltjson) {
		length = l;
		width = w;
		height = h;
		data = new int[w][h][l];
		mark = new boolean[w][h][l];
		littleTiles = new HashMap<Integer,List<Box>>();
	}
	
	//greedy meshing
	//if mark[i][j][k] = true, then means it has been counted, should not attempt future compute
	public void greedyMeshing() {
		
		for (int i=0; i<width; i++) {
			for (int j=0; j<height; j++) {
				for (int k=0; k<length; k++) {
					//System.out.println(i+" "+j+" "+k+" "+(data[i][j][k]>>8));
					//System.out.println(mark[i][j][k]);
					if (mark[i][j][k]) {
						continue;
					}
					if (data[i][j][k] == 0) {
						mark[i][j][k] = true;
						continue;
					}
					//if block is not marked, try to be a bigger box
					//only find front blocks, back block should has been marked
					int id = data[i][j][k];
					int z = i;
					int y = j;
					int x = k;
					
					System.out.printf("%d %d %d %d\n",i,j,k,data[i][j][k]);
					
					int[] p2 = expandBox(id,z,y,x);
					markBox(z,y,x,p2[0],p2[1],p2[2], true);
					int[] p1 = {z,y,x};
					Box box = new Box(p1,p2,id);
					System.out.println("id:"+(id>>8));
					System.out.println("box: "+p1[0]+","+p1[1]+","+p1[2]+","+" "+p2[0]+","+p2[1]+","+p2[2]);
					if (littleTiles.get(id)==null) {
						littleTiles.put(id,new ArrayList<Box>());
					}
					littleTiles.get(id).add(box);
					
				}
			}
		}
		
		lt = true;
		
	}
	
	private int[] expandBox(int id,int z, int y, int x) {
		int[] out = new int[3];
		int x2,z2,y2;
		x2 = x;
		z2 = z;
		y2 = y;
		
		//expand x--length
		while (true) {
			x2++;
			//System.out.printf("%d %d %d %d\n",z2,y2,x2,data[z2][y2][x2]);
			if (x2>=length||(id!=data[z2][y2][x2])||mark[z2][y2][x2]) {
				break;
			}
		}
		//System.out.println("y2");
		//expand y--height 
		boolean c = true;
		while(c) {
			y2++;
			//System.out.println(z2);
			for (int i=x; i<x2; i++) {
				//System.out.println(i);
				if (y2>=height||(id!=data[z2][y2][i])||mark[z2][y2][i]) {
					c=false;
					break;
				}
			}
		}
		//System.out.println("z2");
		//expand z--width
		c = true;
		while(c) {
			z2++;
			for (int i=y; i<y2; i++) {
				if (!c) break;
				for (int j=x; j<x2; j++) {
					//System.out.println(j+" "+i+" "+z2);
					if (z2>=width||(id!=data[z2][i][j])||mark[z2][i][j]) {
						c=false;
						break;
					}
				}
			}
		}
		//System.out.println("box get");
		out[0] = z2-1;
		out[1] = y2-1;
		out[2] = x2-1;
		return out;
	}
	
	private void markBox(int z1, int y1, int x1, int z2, int y2, int x2, boolean b) {
		for (int i=z1; i<z2+1; i++) {
			for (int j=y1; j<y2+1; j++) {
				for (int k=x1; k<x2+1; k++) {
					mark[i][j][k] = b;
					//System.out.println(i+" "+j+" "+k+" "+mark[i][j][k]);
				}
			}
		}
	}
	
	public Map<Integer,List<Box>> getLittleTilesMap() {
		return littleTiles;
	}
	
	// size[x,y,z]
	public int[] getSize() {
		int[] out = new int[3];
		out[0] = data.length;//w
		out[1] = data[0].length;//h
		out[2] = data[0][0].length;//l
		return out;
	}
	
	protected class Grid {
		protected int[][][] data;
		protected boolean[][][] mark;
		protected int[] gridPos;
		
		protected Grid(int[][][] d, boolean[][][] m, int[] gpos) {
			data = d;
			mark = m;
			gridPos = gpos;
		}
	}
}
