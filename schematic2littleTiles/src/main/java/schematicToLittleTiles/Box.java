package schematicToLittleTiles;

public class Box {
	private int[] pos1;
	private int[] pos2;
	private int block;
	
	public Box(int[] p1, int[] p2, int b) {
		pos1 = p1;
		pos2 = p2;
		block = b;
	}
	
	public int[] getPos() {
		int[] out = new int[pos1.length+pos2.length];
		for (int i=0; i<pos1.length; i++) {
			out[i] = pos1[i];
		}
		for (int i=pos1.length; i<pos1.length+pos2.length; i++) {
			out[i] = pos2[i-pos1.length];
		}
		
		return out;
	}
	
	public int getBlockID() {
		return block;
	}
}
