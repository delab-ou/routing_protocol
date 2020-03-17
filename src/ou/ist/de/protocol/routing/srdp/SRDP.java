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
	protected RouteInfo ri;
	protected Signature sig;
	protected PublicKeyPairs pkp;
	protected boolean verifyAll;
	protected SignatureOperation so;
	
	public SRDP(HashMap<String,String> params) {
		super(params);
		
		
	}
	@Override
	protected void initialize(HashMap<String, String> params) {
		// TODO Auto-generated method stub
		
		this.parameterCheck(params);
		System.out.println("sigbit "+params.get(Constants.ARG_SIG_BIT_LENGTH));
		ri = new RouteInfo();
		sig=new Signature(Integer.valueOf(params.get(Constants.ARG_SIG_BIT_LENGTH)));
		pkp=new PublicKeyPairs();
		this.verifyAll = false;
		so = new SignatureOperation(params);
		rcvCache=new HashMap<String,ArrayList<String>>();
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
		this.ri.clear();
		this.pkp.clear();
		ri.fromBytes(p.getOption());
		ri.addNode(this.node.getAddress());
		//System.out.println(ri.toString());
		p.setNext(ri.get(ri.size() - 2));
		pkp.add(so.getPublicKeyPair());
		System.out.println("public key pair="+so.getPublicKeyPair());
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
		//System.out.println("in srdp op reply "+p.toString());
		
		this.separateOption(p);
		boolean v=this.verifyingPacket(p);
		if (!this.ri.isContained(this.node.getAddress())) {
			//System.out.println(" not contained");
			return null;
		}

		if (p.getDest().equals(this.node.getAddress())) {
			
			if(!v) {
				
			}
			System.out.println("reply verification:" + v);
			
			//System.out.println(p.toString());
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
		
		/*
		if(!v) {
			System.out.println("----- forwarding false -----");
			return null;
		}
		*/
		pkp.add(so.getPublicKeyPair());
		p.setSndr(this.node.getAddress());
		p.setHops(p.getHops() + 1);
		
		return this.signingPacket(p);
	}
	
	protected void separateOption(Packet p) {
		ri.clear();
		pkp.clear();
		if (p.getOption() == null) {
			return;
		}
		ri.fromBytes(p.getOption());
		if(p.getType()==Constants.REP) {
			System.out.println("received packet is REP");
		sig.fromBytes(p.getOption());
		pkp.fromBytes(p.getOption());
		}

	}

	public Packet signingPacket(Packet p) {
		Signature s= so.sign(this.ri, this.sig, this.pkp);
		byte[] ribytes=ri.toBytes();
		byte[] sigbytes=s.toBytes();
		byte[] pkpbytes=pkp.toBytes();
		ByteBuffer bb = ByteBuffer.allocate(ribytes.length+sigbytes.length+pkpbytes.length);
		bb.put(ribytes);
		System.out.println("ri length="+ribytes.length);
		bb.put(sigbytes);
		System.out.println("sig length="+sigbytes.length);
		bb.put(pkpbytes);
		System.out.println("pkp length="+pkpbytes.length);
		p.setOption(bb.array());
		System.out.println("set option length="+bb.capacity());
		return p;
	}

	public boolean verifyingPacket(Packet p) {
		return so.verify(this.ri, this.sig,this.pkp);
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
			if(value.startsWith(s)) {
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
