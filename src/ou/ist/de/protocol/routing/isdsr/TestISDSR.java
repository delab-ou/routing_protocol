package ou.ist.de.protocol.routing.isdsr;

import java.net.InetAddress;
import java.util.HashMap;

import com.herumi.mcl.Fr;
import com.herumi.mcl.G1;
import com.herumi.mcl.Mcl;

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
		Mcl.SystemInit(Mcl.BLS12_381);
		Fr f=null;
		for(int i=0;i<300;i++){
			System.out.println("count="+(i+1));
			f=new Fr(5);
			f.setInt(4);
			//f.setStr("0 0 0");
			f.delete();
		}
		
		TestISDSR test=new TestISDSR();
		//test.testJPBC(10);
		test.testMCL(40);
		
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
			SignatureOperation somcl = new MCLSignatureOperation(params, InetAddress.getByName("10.0.0."+String.valueOf(num+1)).toString());
			System.out.println("preparation:"+(System.currentTimeMillis()-t)+" [ms]");
			t=System.currentTimeMillis();
			for(int i=0;i<num;i++){
				ri.addNode(addrs[i]);
				System.out.println("signed by "+addrs[i].toString());
				sigs.fromBytes(so[i].sign(ri, sigs),0);
			}
			System.out.println("signing by "+ num+ " nodes for "+(System.currentTimeMillis()-t)+" [ms]");
			t=System.currentTimeMillis();
			boolean v=somcl.verify(ri, sigs);
			System.out.println("verified "+v+" by 10.0.0."+InetAddress.getByName("10.0.0."+String.valueOf(num+1)).toString()+" for : "+(System.currentTimeMillis()-t)+" [ms]");
			
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
