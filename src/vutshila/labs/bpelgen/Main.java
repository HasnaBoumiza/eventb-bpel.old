package vutshila.labs.bpelgen;

import vutshila.labs.bpelgen.core.translation.BPELwriter;

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
