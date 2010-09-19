package za.vutshilalabs.bpelgen;

import za.vutshilalabs.bpelgen.core.translation.BPELwriter;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BPELwriter bmap = new BPELwriter();
		bmap.init(null);
		System.out.println(bmap.documentString());
	}

}
