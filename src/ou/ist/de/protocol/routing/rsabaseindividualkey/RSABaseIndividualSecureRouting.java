package ou.ist.de.protocol.routing.rsabaseindividualkey;

import java.nio.ByteBuffer;
import java.util.HashMap;

import ou.ist.de.protocol.Constants;
import ou.ist.de.protocol.packet.Packet;
import ou.ist.de.protocol.routing.RoutingProtocol;
import ou.ist.de.protocol.routing.dsr.RouteInfo;

public class RSABaseIndividualSecureRouting extends RoutingProtocol {
	protected RouteInfo ri;
	protected Signatures sigs;
	protected PublicKeyPairs pkp;
	protected boolean verifyAll;
	protected SignatureOperation so;

	public RSABaseIndividualSecureRouting() {
	}

	public RSABaseIndividualSecureRouting(HashMap<String, String> params) {
		super(params);
	}

	@Override
	protected void initialize(HashMap<String, String> params) {
		// TODO Auto-generated method stub
		
		this.parameterCheck(params);
		System.out.println("sigbit "+params.get(Constants.ARG_SIG_BIT_LENGTH));
		ri = new RouteInfo();
		sigs = new Signatures(Integer.valueOf(params.get(Constants.ARG_SIG_BIT_LENGTH)));
		pkp=new PublicKeyPairs();
		this.verifyAll = false;
		so = new SignatureOperation(params);
	}
	protected void parameterCheck(HashMap<String,String> params) {
		if (!params.containsKey(Constants.ARG_SIG_BIT_LENGTH)) {
			params.put(Constants.ARG_SIG_BIT_LENGTH, String.valueOf(Constants.DEFAULT_RSA_SIG_BIT_LENGTH));
		} 
		if(!params.containsKey(Constants.ARG_KEY_INDEX)) {
			params.put(Constants.ARG_KEY_INDEX, Constants.DEFAULT_RSA_KEY_INDEX);
		}
	}
	@Override
	protected Packet operateRequestPacket(Packet p) {
		// TODO Auto-generated method stub
		this.sigs.clear();
		this.ri.clear();
		this.pkp.clear();
		this.ri.addNode(this.node.getAddress());
		// System.out.println("request in RSA "+p.toString());
		Packet pkt = this.signingPacket(p);
		//System.out.println("request = "+pkt.toString());
		//System.out.println("verify:"+this.verifyingPacket(p) );
		return pkt;
	}

	@Override
	protected Packet operateReplyPacket(Packet p) {
		// TODO Auto-generated method stub
		//System.out.println("received packet = "+p.toString());
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
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		this.separateOption(p);

		if (!this.ri.isContained(this.node.getAddress())) {
			System.out.println(" not contained");
			return null;
		}

		if (p.getDest().equals(this.node.getAddress())) {
			System.err.println("in operate reply packet ri lenght="+ri.byteLength()+" sig length="+sigs.byteLength());
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
		sigs.clear();
		
		if (p.getOption() == null) {
			return;
		}
		ri.fromBytes(p.getOption());
		sigs.fromBytes(p.getOption());
		pkp.fromBytes(p.getOption());

	}

	public Packet signingPacket(Packet p) {
		byte[] s =null;
		while(s==null) {
			s = so.sign(this.ri, this.sigs);
			//System.err.println("in signingPacket signature is null-----------");
		}
		sigs.add(s);
		pkp.add(so.getKeyPair());
		byte[] ribytes=this.ri.toBytes();
		System.out.println("ri length="+ribytes.length);
		byte[] sigbytes=this.sigs.toBytes();
		System.out.println("sig length="+sigbytes.length);
		byte[] pkpbytes=this.pkp.toBytes();
		System.out.println("pkp length="+pkpbytes.length);
		ByteBuffer bb = ByteBuffer.allocate(ribytes.length+sigbytes.length+pkpbytes.length);
		bb.put(ribytes);
		bb.put(sigbytes);
		bb.put(pkpbytes);
		System.out.println("option length="+bb.capacity());
		p.setOption(bb.array());
		return p;
	}

	public boolean verifyingPacket(Packet p) {
		return so.verify(this.ri, this.sigs,this.pkp);
	}

	protected void printByteArray(byte[] ba) {
		if (ba == null) {
			System.out.println("[null]");
		}
		String s = "[" + ba[0];
		for (int i = 1; i < ba.length; i++) {
			s += ("," + ba[i]);
		}
		System.out.println(s + "]");
	}

}
