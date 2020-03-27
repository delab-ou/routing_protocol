package ou.ist.de.protocol.routing.isdsr;

import java.net.InetAddress;
import java.util.HashMap;


import ou.ist.de.protocol.routing.dsr.RouteInfo;
import ou.ist.de.protocol.routing.isdsr.mcl.MCLSignatureOperation;
import ou.ist.de.protocol.routing.isdsr.jpbc.JPBCSignatureOperation;

public class TestISDSR {
	static {
		String lib = "mcljava";
		String libName = System.mapLibraryName(lib);
		System.out.println("libName : " + libName);
		System.loadLibrary(lib);
	}

	
	public static void main(String args[]) {
		
		
		int num=0;
		String mclkey=null;
		String jpbckey=null;
		
		for(int i=0;i<args.length;i++){
			if(args[i].equalsIgnoreCase("-nodes")){
				num=Integer.valueOf(args[i+1]);
			}
			if(args[i].equalsIgnoreCase("-mclkey")){
				mclkey=args[i+1];
			}
			if(args[i].equalsIgnoreCase("-jpbckey")){
				jpbckey=args[i+1];
			}
		}
		
		TestISDSR test=new TestISDSR();
		test.testJPBC(num,jpbckey);
		test.testMCL(num,mclkey);
		
	}

	protected InetAddress getHost(int num){
		InetAddress ret=null;
		try{
			int pre=num/220;
			int suf=(num%220)+1;
			ret=InetAddress.getByName("10.0."+pre+"."+String.valueOf(suf));
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return ret;
	}
	public void testMCL(int num,String key){

		System.out.println("---- test mcl ----");
		HashMap<String, String> params = new HashMap<String, String>();

		if(key==null){
			params.put("-keyfile", "bls12_381.keys");
		}
		else{
			if(key.equalsIgnoreCase("bn254")){
				params.put("-keyfile", key+".keys");
			}
			else{
				params.put("-keyfile", "bls12_381.keys");
			}
		}
		System.out.println("key file = "+key);
		long t=0;
		RouteInfo ri = new RouteInfo();
		Signatures sigs=new Signatures(4);

		try {
			SignatureOperation so[]=new SignatureOperation[num];
			InetAddress addrs[]=new InetAddress[num];

			t=System.currentTimeMillis();
			for(int i=0;i<num;i++){
				addrs[i]=this.getHost(i);
				so[i]=new MCLSignatureOperation(params, addrs[i].toString());
			}
			InetAddress verinode=this.getHost(num+1);
			SignatureOperation somcl = new MCLSignatureOperation(params, verinode.toString());
			System.out.println("preparation:"+(System.currentTimeMillis()-t)+" [ms]");
			t=System.currentTimeMillis();
			for(int i=0;i<num;i++){
				ri.addNode(addrs[i]);
				//System.out.println("signed by "+addrs[i].toString());
				sigs.fromBytes(so[i].sign(ri, sigs),0);
			}
			System.out.println("signing by "+ num+ " nodes for "+(System.currentTimeMillis()-t)+" [ms]");
			t=System.currentTimeMillis();
			boolean v=somcl.verify(ri, sigs);
			System.out.println("verified "+v+" by "+verinode.toString()+" for : "+(System.currentTimeMillis()-t)+" [ms]");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void testJPBC(int num,String key){
		System.out.println("---- test jpbc ----");
		HashMap<String, String> params = new HashMap<String, String>();
		String keys=null;
		if(key==null){
			keys="a";
		}
		else{
			if(key.equalsIgnoreCase("e")){
				keys="e";
			}
			else if(key.equalsIgnoreCase("a1")){
				keys="a1";
			}
			else{
				keys="a";
			}
		}
		params.put("-keyfile", keys+".keys");
		params.put("-paramfile", keys+".properties");

		System.out.println("key file = "+keys);
		long t=0;
		RouteInfo ri = new RouteInfo();
		Signatures sigs=new Signatures(3);
		try {
			SignatureOperation so[]=new SignatureOperation[num];
			InetAddress addrs[]=new InetAddress[num];

			t=System.currentTimeMillis();
			for(int i=0;i<num;i++){
				addrs[i]=this.getHost(i);
				so[i]=new JPBCSignatureOperation(params, addrs[i].toString());
			}
			InetAddress verinode=this.getHost(num+1);
			SignatureOperation somcl = new JPBCSignatureOperation(params, verinode.toString());
			System.out.println("preparation:"+(System.currentTimeMillis()-t)+" [ms]");
			t=System.currentTimeMillis();
			for(int i=0;i<num;i++){
				ri.addNode(addrs[i]);
				//System.out.println("signed by "+addrs[i].toString());
				sigs.fromBytes(so[i].sign(ri, sigs),0);
			}
			System.out.println("signing by "+ num+ "nodes for "+(System.currentTimeMillis()-t)+" [ms]");
			t=System.currentTimeMillis();
			boolean v=somcl.verify(ri, sigs);
			System.out.println("verified "+v+" by "+verinode.toString()+" for : "+(System.currentTimeMillis()-t)+" [ms]");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
