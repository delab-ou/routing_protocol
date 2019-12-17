package ou.ist.de.protocol.routing.dsr;

import java.net.InetAddress;
import java.util.HashMap;

import ou.ist.de.protocol.packet.Packet;
import ou.ist.de.protocol.routing.RoutingProtocol;

public class DSR extends RoutingProtocol {
	public static int REQ=0;
	public static int REP=1;
	public static int ERR=2;
	public static int DATA=3;
	public DSR() {
		super();
	}

	@Override
	public void receivePacket(Packet p) {
		// TODO Auto-generated method stub
		System.out.println("In DSR receivedPacket "+p.toString());
		Packet pkt = null;
		
		if (p.getDest().equals(this.node.getAddress())) {
			if (p.getType() == DSR.REQ) {
				pkt = this.generateInitialReplyPacket(p);
			}
			else {
				System.out.println("route is established");
			}
		} else {
			pkt = this.generateForwaringPacket(p);
		}
		if (pkt != null) {
			System.out.println("In DSR receivedPacket send "+pkt.toString());
			this.s.send(pkt);
		}
	}
	
	protected Packet generateInitialRequestPacketBase(InetAddress dest) {
		Packet p = new Packet();
		p.setDest(dest);
		p.setType((byte) DSR.REQ);
		p.setHops(1);
		p.setSrc(this.node.getAddress());
		p.setSndr(this.node.getAddress());
		p.setNext(node.getBroadcastAddress());
		return p;
	}
	@Override
	protected Packet generateInitialRequestPacket(InetAddress dest) {
		// TODO Auto-generated method stub
		Packet p = generateInitialRequestPacketBase(dest);
		RouteInfo ri = new RouteInfo();
		ri.addNode(this.node.getAddress());
		p.setOption(ri.toBytes());
		//System.err.println("In DSR generateInitialRequestPAcket to "+dest +"\n"+p.toString());
		return p;
	}
	
	protected void generateInitialReplyPacketBase(Packet p, RouteInfo ri) {
		p.setType((byte) DSR.REP);
		p.setDest(p.getSrc());
		p.setSrc(this.node.getAddress());
		p.setSndr(this.node.getAddress());
		p.setNext(ri.get(p.getHops() - 1));
		p.setHops(1);
		
	}
	@Override
	protected Packet generateInitialReplyPacket(Packet p) {
		// TODO Auto-generated method stub
		RouteInfo ri = new RouteInfo(p.getOption());
		if (ri.isContained(this.node.getAddress())) {
			return null;
		}
		ri.addNode(this.node.getAddress());
		generateInitialReplyPacketBase(p,ri);
		p.setOption(ri.toBytes());
		return p;
	}

	@Override
	protected Packet generateForwaringPacket(Packet p) {
		// TODO Auto-generated method stub
		System.out.println("p is "+p);
		RouteInfo ri = new RouteInfo(p.getOption());
		System.out.println("route info "+ri.toString());
		if (p.getType() == DSR.REQ) {
			if (ri.isContained(this.node.getAddress())) {
				return null;
			}
			ri.addNode(this.node.getAddress());
			p.setHops(p.getHops() + 1);
			p.setSndr(this.node.getAddress());
			p.setOption(ri.toBytes());
			return p;
		}
		if (p.getType() == DSR.REP) {
			System.out.println("In DSR generateForwardingPAcket receved type 1");
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
			p.setHops(p.getHops()+1);
			p.setOption(ri.toBytes());
			System.out.println("In DSR generateForwardingPAcket receved type 1 "+p);
			return p;
		}

		return null;
	}

	@Override
	protected void initialize(HashMap<String, String> params) {
		// TODO Auto-generated method stub
		
	}
	
}
