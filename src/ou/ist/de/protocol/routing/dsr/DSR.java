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
		Packet pkt = null;
		if (p.getDest().equals(this.node.getAddress())) {
			if (p.getType() == 0) {
				pkt = this.generateInitialReplyPacket(p);
			}
			else {
				System.out.println("route is established");
			}
		} else {
			pkt = this.generateForwaringPacket(p);
		}
		if (pkt != null) {
			this.s.send(pkt);
		}
	}

	@Override
	protected Packet generateInitialRequestPacket(InetAddress dest) {
		// TODO Auto-generated method stub
		Packet p = new Packet();
		p.setDest(dest);
		p.setType((byte) 0);
		p.setHops(1);
		p.setSrc(this.node.getAddress());
		p.setSndr(this.node.getAddress());
		p.setNext(node.getBroadcastAddress());
		RouteInfo ri = new RouteInfo();
		ri.addNode(this.node.getAddress());
		p.setOption(ri.toBytes());
		return p;
	}

	@Override
	protected Packet generateInitialReplyPacket(Packet p) {
		// TODO Auto-generated method stub
		RouteInfo ri = new RouteInfo(p.getOption(), p.getHops());
		if (ri.isContained(this.node.getAddress())) {
			return null;
		}
		ri.addNode(this.node.getAddress());
		p.setType((byte) 1);
		p.setDest(p.getSrc());
		p.setSrc(this.node.getAddress());
		p.setSndr(this.node.getAddress());
		p.setNext(ri.get(p.getHops() - 1));
		p.setHops(1);
		p.setOption(ri.toBytes());
		return p;
	}

	@Override
	protected Packet generateForwaringPacket(Packet p) {
		// TODO Auto-generated method stub
		RouteInfo ri = new RouteInfo(p.getOption(), p.getHops());
		if (p.getType() == 0) {
			if (ri.isContained(this.node.getAddress())) {
				return null;
			}
			ri.addNode(this.node.getAddress());
			p.setHops(p.getHops() + 1);
			p.setSndr(this.node.getAddress());
			p.setOption(ri.toBytes());
			return p;
		}
		if (p.getType() == 1) {
			if (!ri.isContained(this.node.getAddress())) {
				return null;
			}
			for (int i = 0; i < ri.size(); i++) {
				if (ri.get(i).equals(this.node.getAddress())) {
					p.setNext(ri.get(i - 1));
					break;
				}
			}
			p.setSndr(this.node.getAddress());
			p.setHops(p.getHops()+1);
			p.setOption(ri.toBytes());
			return p;
		}

		return null;
	}

}
