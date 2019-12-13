package ou.ist.de.protocol.routing;

import java.net.InetAddress;

import ou.ist.de.protocol.node.Node;
import ou.ist.de.protocol.node.Sender;
import ou.ist.de.protocol.packet.Packet;

public class FloodingRouting extends RoutingProtocol{

	public FloodingRouting() {
		super();
	}
	public FloodingRouting(Node node, Sender s) {
		super(node, s);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void receivePacket(Packet p) {
		// TODO Auto-generated method stub
		if(p.getDest().equals(this.node.getAddress())) {
			p.setHops(1);
			p.setSndr(node.getAddress());
			p.setDest(p.getSrc());
			p.setSrc(node.getAddress());
			p.setType((byte)1);
			System.out.println("replying "+p.toString());
			this.s.send(p);
		}
		else {
			if(p.getSndr().equals(this.node.getAddress())) {
				System.out.println("receive a packet from itself");
				return;
			}
			p.setHops(p.getHops()+1);
			p.setSndr(node.getAddress());
			System.out.println("forwarding "+p.toString());
			this.s.send(p);
		}
	}
	@Override
	public Packet generateInitialRequestPacket(InetAddress dest) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
