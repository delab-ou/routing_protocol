package ou.ist.de.protocol;

import java.util.HashMap;

public class DSRNode {
	
	public static void main(String[] args) {
		
		HashMap<String,String> params=new HashMap<String,String>();
		
		//java DSRNode -port:000 -dest:10.0.0.0 -frag:1000
		if(args.length<2) {
			System.out.println("usage java ou.ist.de.protocol.DSRNode -port:portnum -frag:size of fragmentation -dest:destination ip");
			System.exit(0);
		}
		
	}
}
