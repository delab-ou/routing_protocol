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
	protected HashMap<String,Long> cache;
	protected byte[] received;
	protected int rcv;
	
	public RoutingProtocol() {
		node=null;
		s=null;
		seqnum=Constants.INIT_SEQ;
	}
	public RoutingProtocol(HashMap<String,String> params) {
		this();
		cache=new HashMap<String,Long>();
		received=new byte[10000];
		rcv=0;
		this.initialize(params);
	}
	
	public void setNode(Node node) {
		this.node=node;
	}
	public void setSender(Sender s) {
		this.s=s;
	}
	public Packet startRouteEstablishment(InetAddress dest) {
		//System.out.println("In RoutingProtocol start route establishment to "+dest);
		Packet p=this.generateInitialRequest(dest);
		//System.out.println("In RoutingProtocol sending packet is "+p.toString());
		this.s.send(p);
		return p;
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
		if(received[p.getSeq()]==0) {
			received[p.getSeq()]=1;
			rcv++;
			System.out.println("rcv="+rcv+" seq="+p.getSeq());
		}
		return operateReplyPacket(p);
		
	}
	protected Packet generateForwardingPacket(Packet p) {
		Long t=null;
		if(p.getType()==Constants.REQ) {
			if(p.getSndr().equals(this.node.getAddress())) {
				return null;
			}
			if(p.getSrc().equals(this.node.getAddress())) {
				return null;
			}
			String reqCache=""+p.getType()+p.getSrc().toString()+p.getDest().toString()+p.getSeq();
			t=new Long(System.currentTimeMillis());
			if(cache.containsKey(reqCache)) {
				if((t-cache.get(reqCache))<Constants.TIMEOUT) {
					//System.out.println("cached");
					return null;
				}
			}
			cache.put(reqCache, t);
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
		long t=System.currentTimeMillis();
		Packet pkt = null;
		//System.out.println("receive from "+p.getSndr()+" seq:"+p.getSeq()+" hops:"+p.getHops()+" type:"+p.getType());
		if (p.getDest().equals(this.node.getAddress())) {
			if (p.getType() == Constants.REQ) {
				
				pkt = this.generateInitialReply(p);
			}
			else {
				this.generateForwardingPacket(p);
				node.routeEstablished(p);
			}
		} else {
			pkt = this.generateForwardingPacket(p);
		}
		if (pkt != null) {
			//System.out.println("In RoutingProtocol receivedPacket send "+pkt.toString());
			this.s.send(pkt);
		}
		//System.out.println(this.node.getAddress().toString().split("\\.")[3]+" processing time is "+
		//(System.currentTimeMillis()-t)+" packet type:"+p.getType()+" length="+p.getSize()+" seq:"+p.getSeq());
	}
	protected abstract void initialize(HashMap<String,String> params);
	protected abstract Packet operateRequestPacket(Packet p);
	protected abstract Packet operateReplyPacket(Packet p);
	protected abstract Packet operateRequestForwardingPacket(Packet p);
	protected abstract Packet operateReplyForwardingPacket(Packet p);
	protected abstract void separateOption(Packet p);
	
	
}
