package ou.ist.de.protocol.routing;

import java.net.InetAddress;

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
	public abstract void receivePacket(Packet p);
	public abstract Packet generateInitialRequestPacket(InetAddress dest);
	
}
