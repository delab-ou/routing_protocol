package ou.ist.de.protocol.routing;

import java.net.InetAddress;
import java.util.HashMap;

import ou.ist.de.protocol.node.Node;
import ou.ist.de.protocol.node.Sender;
import ou.ist.de.protocol.packet.Packet;

public abstract class RoutingProtocol {
	protected Node node;
	protected Sender s;
	
	public RoutingProtocol() {
		node=null;
		s=null;
	}
	public RoutingProtocol(HashMap<String,String> params) {
		super();
		this.initialize(params);
	}
	public RoutingProtocol(Node node, Sender s) {
		this.node=node;
		this.s=s;
	}
	public void setNode(Node node) {
		this.node=node;
	}
	public void setSender(Sender s) {
		this.s=s;
	}
	public void startRouteEstablishment(InetAddress dest) {
		//System.out.println("In RoutingProtocol start route establishment to "+dest);
		Packet p=this.generateInitialRequestPacket(dest);
		//System.out.println("In RoutingProtocol sending packet is "+p.toString());
		this.s.send(p);
	}
	public abstract void receivePacket(Packet p);
	protected abstract void initialize(HashMap<String,String> params);
	protected abstract Packet generateInitialRequestPacket(InetAddress dest);
	protected abstract Packet generateInitialReplyPacket(Packet p);
	protected abstract Packet generateForwaringPacket(Packet p);
	
}
