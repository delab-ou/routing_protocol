package ou.ist.de.protocol.routing;

import java.net.InetAddress;
import java.util.HashMap;

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
			System.out.println("replying "+p.toString());
			Packet pkt=this.generateInitialReplyPacket(p);
			this.s.send(pkt);
		}
		else {
			Packet pkt=this.generateForwaringPacket(p);
			System.out.println("forwarding "+p.toString());
			this.s.send(pkt);
		}
	}
	@Override
	protected Packet generateInitialRequestPacket(InetAddress dest) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	protected Packet generateInitialReplyPacket(Packet p) {
		// TODO Auto-generated method stub
		p.setHops(1);
		p.setSndr(node.getAddress());
		p.setDest(p.getSrc());
		p.setSrc(node.getAddress());
		p.setType((byte)1);
		System.out.println("replying "+p.toString());
		return p;
	}
	@Override
	protected Packet generateForwaringPacket(Packet p) {
		// TODO Auto-generated method stub
		if(p.getSndr().equals(this.node.getAddress())) {
			System.out.println("receive a packet from itself");
			return null;
		}
		p.setHops(p.getHops()+1);
		p.setSndr(node.getAddress());
		System.out.println("forwarding "+p.toString());
		return p;
	}
	@Override
	protected void initialize(HashMap<String, String> params) {
		// TODO Auto-generated method stub
		
	}
	

}
