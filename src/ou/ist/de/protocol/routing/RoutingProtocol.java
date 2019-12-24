package ou.ist.de.protocol.routing;

import java.net.InetAddress;
import java.util.HashMap;

import ou.ist.de.protocol.Constants;
import ou.ist.de.protocol.node.Node;
import ou.ist.de.protocol.node.Sender;
import ou.ist.de.protocol.packet.Packet;

public abstract class RoutingProtocol {
	protected Node node;
	protected Sender s;
	protected int seqnum;
	
	public RoutingProtocol() {
		node=null;
		s=null;
		seqnum=1;
	}
	public RoutingProtocol(HashMap<String,String> params) {
		this();
		this.initialize(params);
	}
	
	public void setNode(Node node) {
		this.node=node;
	}
	public void setSender(Sender s) {
		this.s=s;
	}
	public void startRouteEstablishment(InetAddress dest) {
		//System.out.println("In RoutingProtocol start route establishment to "+dest);
		Packet p=this.generateInitialRequest(dest);
		//System.out.println("In RoutingProtocol sending packet is "+p.toString());
		this.s.send(p);
	}
	protected Packet generateInitialRequest(InetAddress dest) {
		Packet p=new Packet();
		p.setType(Constants.REQ);
		p.setSrc(this.node.getAddress());
		p.setDest(dest);
		p.setSndr(this.node.getAddress());
		p.setNext(this.node.getBroadcastAddress());
		p.setType(Constants.REQ);
		p.setHops(1);
		p.setSeq(this.seqnum);
		this.seqnum++;
		operateRequestPacket(p);
		return p;
	}
	protected Packet generateInitialReply(Packet p) {
		p.setType(Constants.REP);
		p.setHops(1);
		p.setDest(p.getSrc());
		p.setSrc(this.node.getAddress());
		p.setSndr(this.node.getAddress());
		return operateReplyPacket(p);
		
	}
	protected Packet generateForwardingPacket(Packet p) {
		if(p.getType()==Constants.REQ) {
			return operateRequestForwardingPacket(p);
		}
		else if(p.getType()==Constants.REP) {
			return operateReplyForwardingPacket(p);
		}
		else {
			return null;
		}
	}
	
	public void receivePacket(Packet p) {
		//System.out.println("In RoutingProtocol receivedPacket "+p.toString());
		Packet pkt = null;
		
		if (p.getDest().equals(this.node.getAddress())) {
			if (p.getType() == Constants.REQ) {
				pkt = this.generateInitialReply(p);
			}
			else {
				this.generateForwardingPacket(p);
				System.out.println("route is established");
			}
		} else {
			pkt = this.generateForwardingPacket(p);
		}
		if (pkt != null) {
			//System.out.println("In RoutingProtocol receivedPacket send "+pkt.toString());
			this.s.send(pkt);
		}
	}
	protected abstract void initialize(HashMap<String,String> params);
	protected abstract Packet operateRequestPacket(Packet p);
	protected abstract Packet operateReplyPacket(Packet p);
	protected abstract Packet operateRequestForwardingPacket(Packet p);
	protected abstract Packet operateReplyForwardingPacket(Packet p);
	
	
}
