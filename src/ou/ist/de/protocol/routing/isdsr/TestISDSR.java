package ou.ist.de.protocol.routing.isdsr;

import java.net.InetAddress;
import java.util.HashMap;

import ou.ist.de.protocol.routing.dsr.RouteInfo;
import ou.ist.de.protocol.routing.isdsr.mcl.MCLSignatureOperation;
import ou.ist.de.protocol.routing.isdsr.jpbc.JPBCSignatureOperation;

public class TestISDSR {

	public static void main(String args[]) {

		TestISDSR test=new TestISDSR();
		//test.testJPBC(10);
		test.testMCL(10);
		
	}
	public void testMCL(int num){

		System.out.println("---- test mcl ----");
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("-keyfile", "bls12_381.keys");
		long t=0;
		RouteInfo ri = new RouteInfo();
		Signatures sigs=new Signatures(4);
		try {
			SignatureOperation so[]=new SignatureOperation[num];
			InetAddress addrs[]=new InetAddress[num];

			t=System.currentTimeMillis();
			for(int i=0;i<num;i++){
				addrs[i]=InetAddress.getByName("10.0.0."+String.valueOf((i+1)));
				so[i]=new MCLSignatureOperation(params, addrs[i].toString());
			}
			SignatureOperation somcl = new MCLSignatureOperation(params, InetAddress.getByName("10.0.0."+String.valueOf(num)).toString());
			System.out.println("preparation:"+(System.currentTimeMillis()-t)+" [ms]");
			t=System.currentTimeMillis();
			for(int i=0;i<num;i++){
				ri.addNode(addrs[i]);
				System.out.println("signed by "+addrs[i].toString());
				sigs.fromBytes(so[i].sign(ri, sigs),0);
			}
			System.out.println("signing by "+ num+ "nodes for "+(System.currentTimeMillis()-t)+" [ms]");
			t=System.currentTimeMillis();
			boolean v=somcl.verify(ri, sigs);
			System.out.println("verified "+v+" by 10.0.0."+num+" for : "+(System.currentTimeMillis()-t)+" [ms]");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void testJPBC(int num){
		System.out.println("---- test jpbc ----");
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("-keyfile", "a.keys");
		params.put("-paramfile", "a.properties");

		long t=0;
		RouteInfo ri = new RouteInfo();
		Signatures sigs=new Signatures(3);
		try {
			SignatureOperation so[]=new SignatureOperation[num];
			InetAddress addrs[]=new InetAddress[num];

			t=System.currentTimeMillis();
			for(int i=0;i<num;i++){
				addrs[i]=InetAddress.getByName("10.0.0."+String.valueOf((i+1)));
				so[i]=new JPBCSignatureOperation(params, addrs[i].toString());
			}
			SignatureOperation somcl = new JPBCSignatureOperation(params, InetAddress.getByName("10.0.0."+String.valueOf(num)).toString());
			System.out.println("preparation:"+(System.currentTimeMillis()-t)+" [ms]");
			t=System.currentTimeMillis();
			for(int i=0;i<num;i++){
				ri.addNode(addrs[i]);
				System.out.println("signed by "+addrs[i].toString());
				sigs.fromBytes(so[i].sign(ri, sigs),0);
			}
			System.out.println("signing by "+ num+ "nodes for "+(System.currentTimeMillis()-t)+" [ms]");
			t=System.currentTimeMillis();
			boolean v=somcl.verify(ri, sigs);
			System.out.println("verified "+v+" by 10.0.0."+num+" for : "+(System.currentTimeMillis()-t)+" [ms]");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
