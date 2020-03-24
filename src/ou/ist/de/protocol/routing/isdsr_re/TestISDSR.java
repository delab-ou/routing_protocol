package ou.ist.de.protocol.routing.isdsr_re;

import java.net.InetAddress;
import java.util.HashMap;

import ou.ist.de.protocol.routing.dsr.RouteInfo;
import ou.ist.de.protocol.routing.isdsr_re.jpbc.JPBCSignatureOperation;

public class TestISDSR {

	public static void main(String args[]) {

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("-keyfile", "a.keys");
		params.put("-paramfile", "a.properties");
		
		RouteInfo ri = new RouteInfo();
		InetAddress a1, a2, a3, a4, a5;
		try {
			a1 = InetAddress.getByName("10.0.0.1");
			a2 = InetAddress.getByName("10.0.0.2");
			a3 = InetAddress.getByName("10.0.0.3");
			a4 = InetAddress.getByName("10.0.0.4");
			a5 = InetAddress.getByName("10.0.0.5");
			SignatureOperation so = new JPBCSignatureOperation(params, a1.toString());
			ri.addNode(a1);
			Signatures sigs=new Signatures();
			so.sign(ri, sigs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
