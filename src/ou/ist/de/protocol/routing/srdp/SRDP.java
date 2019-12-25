package ou.ist.de.protocol.routing.srdp;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import ou.ist.de.protocol.Constants;
import ou.ist.de.protocol.packet.Packet;
import ou.ist.de.protocol.routing.RoutingProtocol;
import ou.ist.de.protocol.routing.dsr.RouteInfo;

public class SRDP extends RoutingProtocol {
	
	protected HashMap<String,ArrayList<String>> rcvCache;
	protected int sigBitLength;
	protected RouteInfo ri;
	protected Signatures sigs;
	protected boolean verifyAll;
	protected SignatureOperation so;
	
	public SRDP(HashMap<String,String> params) {
		super(params);
		
		
	}
	@Override
	protected void initialize(HashMap<String, String> params) {
		// TODO Auto-generated method stub
		if (params.containsKey("-sigbitlen")) {
			this.sigBitLength = Integer.valueOf(params.get("-sigbitlen"));
		} else {
			this.sigBitLength = 2048;
		}
		ri = new RouteInfo();
		sigs = new Signatures(this.sigBitLength);
		this.verifyAll = false;
		so = new SignatureOperation(params);
		rcvCache=new HashMap<String,ArrayList<String>>();
	}

	@Override
	protected Packet operateRequestPacket(Packet p) {
		// TODO Auto-generated method stub
		this.sigs.clear();
		this.ri.clear();
		this.ri.addNode(this.node.getAddress());
		// System.out.println("request in RSA "+p.toString());
		this.storeReqCache(p);
		p.setOption(ri.toBytes());
		// System.out.println("verify:"+this.verify(p) );
		return p;
	}

	@Override
	protected Packet operateReplyPacket(Packet p) {
		// TODO Auto-generated method stub
		this.sigs.clear();
		this.ri.clear();
		ri.fromBytes(p.getOption());
		ri.addNode(this.node.getAddress());
		p.setNext(ri.get(ri.size() - 2));
		return this.signingPacket(p);
	}

	@Override
	protected Packet operateRequestForwardingPacket(Packet p) {
		// TODO Auto-generated method stub
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
		System.out.println("in srdp op reply "+p.toString());
		this.separateOption(p);
		if (!this.ri.isContained(this.node.getAddress())) {
			System.out.println(" not contained");
			return null;
		}

		if (p.getDest().equals(this.node.getAddress())) {
			System.out.println("reply verification:" + this.verifyingPacket(p));
			System.out.println(p.toString());
			return null;
		}
		if(this.checkReqCache(p)) {
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
		
		return this.signingPacket(p);
	}
	
	protected void separateOption(Packet p) {
		ri.clear();
		sigs.clear();
		if (p.getOption() == null) {
			return;
		}
		ri.fromBytes(p.getOption());
		sigs.fromOption(p.getOption());

	}

	public Packet signingPacket(Packet p) {
		byte[] s = so.sign(this.ri, this.sigs);
		sigs.add(s);
		ByteBuffer bb = ByteBuffer.allocate(this.ri.byteLength() + sigs.byteLength());
		bb.put(this.ri.toBytes());
		bb.put(this.sigs.toBytes());
		p.setOption(bb.array());
		return p;
	}

	public boolean verifyingPacket(Packet p) {
		return so.verify(this.ri, this.sigs);
	}
	protected boolean checkReqCache(Packet p) {
		
		String key=null;
		String value=null;
		ArrayList<String> cache=null;
		if(p.getType()!=Constants.REP) {
			return false;
		}
		key=p.getDest().toString()+p.getSrc().toString()+p.getSeq();
		if(!this.rcvCache.containsKey(key)) {
			return false;
		}
		cache=this.rcvCache.get(key);
		value=ri.getAddrSequence();
		
		for(String s:cache) {
			if(s.startsWith(s)) {
				return true;
			}
		}
		return false;
	}
	protected void storeReqCache(Packet p) {
		String key=null;
		String value=null;
		ArrayList<String> cache=null;
		if(p.getType()!=Constants.REQ) {
			return;
		}
		key=p.getSrc().toString()+p.getDest().toString()+p.getSeq();
		
		if(rcvCache.containsKey(key)) {
			cache=rcvCache.get(key);
		}
		else {
			cache=new ArrayList<String>();
			rcvCache.put(key, cache);
		}
		value=ri.getAddrSequence();
		
		if(!cache.contains(value)) {
			cache.add(value);
		}
	}
}
