package ou.ist.de.protocol.routing.dsr;

import java.net.InetAddress;

import ou.ist.de.protocol.packet.Packet;
import ou.ist.de.protocol.routing.RoutingProtocol;

public class DSR extends RoutingProtocol {

	public DSR() {
		super();
	}
	
	@Override
	public void receivePacket(Packet p) {
		// TODO Auto-generated method stub
		if(p.getDest().equals(this.node.getAddress())) {
			
		}
		else {
			
		}
	}
	
	@Override
	protected Packet generateInitialRequestPacket(InetAddress dest) {
		// TODO Auto-generated method stub
		Packet p=new Packet();
		p.setDest(dest);
		p.setType((byte)0);
		p.setHops(1);
		p.setSrc(this.node.getAddress());
		p.setSndr(this.node.getAddress());
		p.setNext(node.getBroadcastAddress());
		RouteInfo ri=new RouteInfo();
		ri.addNode(this.node.getAddress());
		p.setOption(ri.toBytes());
		return p;
	}

	@Override
	protected Packet generateInitialReplyPacket(Packet p) {
		// TODO Auto-generated method stub
		
		return null;
	}

	@Override
	protected Packet generateForwaringPacket(Packet p) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
