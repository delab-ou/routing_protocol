package ou.ist.de.protocol.routing.isdsr;

import java.net.InetAddress;
import java.util.HashMap;

import ou.ist.de.protocol.routing.dsr.RouteInfo;
import ou.ist.de.protocol.routing.isdsr.mcl.MCLSignatureOperation;
import ou.ist.de.protocol.routing.isdsr.jpbc.JPBCSignatureOperation;

public class TestISDSR {

	public static void main(String args[]) {

		TestISDSR test=new TestISDSR();
		test.testJPBC(10);
		test.testMCL(10);
		
	}
	public void testMCL(int num){

		System.out.println("---- test mcl ----");
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("-keyfile", "bls12_381.keys");
		
		RouteInfo ri = new RouteInfo();
		Signatures sigs=new Signatures(4);
		try {
			SignatureOperation so[]=new SignatureOperation[num];

			for(int i=0;i<num;i++){
				InetAddress addr=InetAddress.getByName("10.0.0."+String.valueOf((i+1)));
				ri.addNode(addr);
				so[i]=new MCLSignatureOperation(params, addr.toString());
				System.out.println("signed by "+addr.toString());
				sigs.fromBytes(so[i].sign(ri, sigs),0);
			}

			SignatureOperation somcl = new MCLSignatureOperation(params, InetAddress.getByName("10.0.0."+String.valueOf(num)).toString());
			System.out.println("verified by 10.0.0."+num+": "+somcl.verify(ri, sigs));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void testJPBC(int num){
		System.out.println("---- test jpbc ----");
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("-keyfile", "a1.keys");
		params.put("-paramfile", "a1.properties");
		
		RouteInfo ri = new RouteInfo();
		InetAddress a1, a2, a3, a4, a5;
		Signatures sigs=new Signatures(3);
		SignatureOperation so[]=new SignatureOperation[num];
		try {
			for(int i=0;i<num;i++){
				InetAddress addr=InetAddress.getByName("10.0.0."+String.valueOf((i+1)));
				ri.addNode(addr);
				so[i]=new JPBCSignatureOperation(params, addr.toString());
				System.out.println("signed by "+addr.toString());
				sigs.fromBytes(so[i].sign(ri, sigs),0);
			}
			SignatureOperation sojpbc = new JPBCSignatureOperation(params, InetAddress.getByName("10.0.0."+String.valueOf(num)).toString());
			System.out.println("verified by 10.0.0."+num+": "+sojpbc.verify(ri, sigs));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
