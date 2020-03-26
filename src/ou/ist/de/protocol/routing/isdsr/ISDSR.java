package ou.ist.de.protocol.routing.isdsr;

import java.nio.ByteBuffer;
import java.util.HashMap;

import ou.ist.de.protocol.node.Node;
import ou.ist.de.protocol.packet.Packet;
import ou.ist.de.protocol.routing.RoutingProtocol;
import ou.ist.de.protocol.routing.dsr.RouteInfo;
import ou.ist.de.protocol.routing.isdsr.jpbc.JPBCSignatureOperation;
import ou.ist.de.protocol.routing.isdsr.mcl.MCLSignatureOperation;

public class ISDSR extends RoutingProtocol {
	protected RouteInfo ri;
	protected Signatures sigs;
	protected SignatureOperation so;
	protected boolean verifyAll;
	protected String siglib;

	public ISDSR(HashMap<String,String> params) {
		super(params);
	}
	@Override
	protected void initialize(HashMap<String, String> params) {
		ri = new RouteInfo();
		int siglen=3;
		siglib="jpbc";
		if(params.containsKey("-siglib")){
			this.siglib=params.get("-siglib");
			if(siglib.equalsIgnoreCase("mcl")){
				siglen=4;
			}
		}
		sigs = new Signatures(siglen);
		this.verifyAll = false;
	}
	@Override
	public void setNode(Node node) {
		this.node=node;
		System.out.println(this.node.toString()+":"+this.node.getAddress());
		if(siglib.equalsIgnoreCase("mcl")){
			so=new MCLSignatureOperation(this.node.getParams(), this.node.getAddress().toString());
		}
		else{
			so=new JPBCSignatureOperation(this.node.getParams(), this.node.getAddress().toString());
		}
	}
	@Override
	protected Packet operateRequestPacket(Packet p) {
		this.ri.clear();
		this.ri.addNode(this.node.getAddress());
		sigs.clear();
		//sigs.fromBytes(null, so.pairing);
		Packet pkt=signingPacket(p);
		//System.out.println("verify:"+this.verifyingPacket(pkt));
		return pkt;
	}

	@Override
	protected Packet operateReplyPacket(Packet p) {
		this.separateOption(p);
		if (this.ri.isContained(this.node.getAddress())) {
			return null;
		}
		if (!this.verifyingPacket(p)) {
			System.out.println("verification: false");
			return null;
		}
		System.out.println("verification: true");
		ri.addNode(this.node.getAddress());
		p.setNext(ri.get(ri.size() - 2));
		return this.signingPacket(p);
	}

	@Override
	protected Packet operateRequestForwardingPacket(Packet p) {
		this.separateOption(p);

		if (this.ri.isContained(this.node.getAddress())) {
			return null;
		}
		if (this.verifyAll) {
			if (!this.verifyingPacket(p)) {
				return null;
			}
		}
		this.ri.addNode(this.node.getAddress());
		p.setHops(p.getHops() + 1);
		p.setSndr(this.node.getAddress());
		return this.signingPacket(p);
	}

	@Override
	protected Packet operateReplyForwardingPacket(Packet p) {
		this.separateOption(p);

		if (!this.ri.isContained(this.node.getAddress())) {
			System.out.println(" not contained");
			return null;
		}
		if (p.getDest().equals(this.node.getAddress())) {
			System.out.println("reply verification:" + this.verifyingPacket(p));
			return null;
		}
		for (int i = 0; i < this.ri.size(); i++) {
			if (this.ri.get(i).equals(this.node.getAddress())) {
				p.setNext(this.ri.get(i - 1));
				break;
			}
		}
		p.setSndr(this.node.getAddress());
		p.setHops(p.getHops() + 1);

		return p;
	}

	protected void separateOption(Packet p) {
		ri.clear();
		if (p.getOption() == null) {
			return;
		}
		int offset=ri.fromBytes(p.getOption());
		sigs.fromBytes(p.getOption(), offset);

	}

	public Packet signingPacket(Packet p) {
		byte[] s = so.sign(this.ri, this.sigs);
		ByteBuffer bb = ByteBuffer.allocate(this.ri.byteLength() + s.length);
		bb.put(this.ri.toBytes());
		bb.put(s);
		p.setOption(bb.array());
		return p;
	}

	public boolean verifyingPacket(Packet p) {
		return so.verify(this.ri, this.sigs);
	}

}
