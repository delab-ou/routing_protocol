package ou.ist.de.protocol.routing.secure;

import java.util.HashMap;

import ou.ist.de.protocol.packet.Packet;
import ou.ist.de.protocol.routing.RoutingProtocol;
import ou.ist.de.protocol.routing.dsr.RouteInfo;

public abstract class SecureRoutingProtocol extends RoutingProtocol {

	protected RouteInfo ri;
	protected Signatures sigs;
	protected SigOperation so;
	
	public SecureRoutingProtocol() {
		
	}
	public SecureRoutingProtocol(HashMap<String,String> params) {
		initialize(params);
	}
	
	@Override
	protected void initialize(HashMap<String, String> params) {
		// TODO Auto-generated method stub
		this.initializeSignatureOperation(params);
	}

	@Override
	protected Packet operateRequestPacket(Packet p) {
		// TODO Auto-generated method stub
		return null;
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
	public  void setSignatureOperation(SigOperation so) {
		this.so=so;
	}
	protected abstract void initializeSignatureOperation(HashMap<String,String> params);

}
