package ou.ist.de.protocol.routing.srdp;

import java.util.HashMap;

import ou.ist.de.protocol.Constants;
import ou.ist.de.protocol.packet.Packet;
import ou.ist.de.protocol.routing.RoutingProtocol;
import ou.ist.de.protocol.routing.dsr.RouteInfo;

public class SRDP extends RoutingProtocol {
	
	protected HashMap<String,String> rcvCache;
	protected int sigBitLength;
	protected RouteInfo ri;
	protected Signatures sigs;
	protected boolean verifyAll;
	protected SignatureOperation so;
	
	public SRDP(HashMap<String,String> params) {
		super(params);
		rcvCache=new HashMap<String,String>();
		
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
	}

	@Override
	protected Packet operateRequestPacket(Packet p) {
		// TODO Auto-generated method stub
		this.sigs.clear();
		this.ri.clear();
		this.ri.addNode(this.node.getAddress());
		// System.out.println("request in RSA "+p.toString());
		p.setOption(ri.toBytes());
		// System.out.println("verify:"+this.verify(p) );
		return p;
	}

	@Override
	protected Packet operateReplyPacket(Packet p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Packet operateRequestForwardingPacket(Packet p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Packet operateReplyForwardingPacket(Packet p) {
		// TODO Auto-generated method stub
		return null;
	}
	protected void storeReqCache(Packet p) {
		String key=null;
		String value=null;
		if(p.getType()!=Constants.REQ) {
			return;
		}
		key=p.getSrc().toString()+p.getDest().toString()+p.getSeq();
		value="ri";
		for(int i=0;i<this.ri.size();i++) {
			value+=":"+ri.get(i).toString();
		}
		
		
	}
	protected void storeCache(Packet p) {
		String cache=null;
		String key=null;
		String value=null;
		if(p.getType()==Constants.REQ) {
			key= ""+p.getType()+p.getSrc().toString()+p.getDest().toString()+p.getSeq();
			
		}
		else if(p.getType()==Constants.REP) {
			
		}
		else {
			return;
		}
		
	}
}
