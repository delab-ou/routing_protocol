package ou.ist.de.protocol.routing.dsr;

import java.util.HashMap;

import ou.ist.de.protocol.packet.Packet;
import ou.ist.de.protocol.routing.RoutingProtocol;

public class DSR extends RoutingProtocol {

	protected RouteInfo ri;
	public DSR() {
		super();
	}
	
	public DSR(HashMap<String,String> params) {
		super(params);
		ri=new RouteInfo();
		
	}
	@Override
	protected Packet operateRequestPacket(Packet p) {
		// TODO Auto-generated method stub
		ri.clear();
		ri.addNode(this.node.getAddress());
		p.setOption(ri.toBytes());
		return p;
	}

	@Override
	protected Packet operateReplyPacket(Packet p) {
		// TODO Auto-generated method stub
		ri.clear();
		this.separateOption(p);
		if (ri.isContained(this.node.getAddress())) {
			return null;
		}
		ri.addNode(this.node.getAddress());
		p.setNext(ri.get(ri.size()-2));
		p.setOption(ri.toBytes());
		return p;
	}
	
	@Override
	protected Packet operateRequestForwardingPacket(Packet p) {
		// TODO Auto-generated method stub
		ri.clear();
		this.separateOption(p);
		if (ri.isContained(this.node.getAddress())) {
			return null;
		}
		ri.addNode(this.node.getAddress());
		p.setHops(p.getHops() + 1);
		p.setSndr(this.node.getAddress());
		p.setOption(ri.toBytes());
		return p;
	}

	@Override
	protected Packet operateReplyForwardingPacket(Packet p) {
		// TODO Auto-generated method stub
		ri.clear();
		this.separateOption(p);
		if(p.getDest().equals(this.node.getAddress())) {
			return null;
		}
		if (!ri.isContained(this.node.getAddress())) {
			System.out.println(" not contained");
			return null;
		}
		for (int i = 0; i < ri.size(); i++) {
			if (ri.get(i).equals(this.node.getAddress())) {
				p.setNext(ri.get(i - 1));
				break;
			}
		}
		p.setSndr(this.node.getAddress());
		p.setHops(p.getHops() + 1);
		p.setOption(ri.toBytes());
		
		return p;
	}
	
	
	@Override
	protected void initialize(HashMap<String, String> params) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void separateOption(Packet p) {
		// TODO Auto-generated method stub
		this.ri.fromBytes(p.getOption());
	}

}
