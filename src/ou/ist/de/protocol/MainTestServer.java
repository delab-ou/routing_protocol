package ou.ist.de.protocol;

import ou.ist.de.protocol.node.Node;
import ou.ist.de.protocol.routing.FloodingRouting;
import ou.ist.de.protocol.routing.RoutingProtocol;

public class MainTestServer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Node node=new Node();
		RoutingProtocol rp=new FloodingRouting();
		node.setRoutingProtocol(rp);
		node.start();
	}

}
