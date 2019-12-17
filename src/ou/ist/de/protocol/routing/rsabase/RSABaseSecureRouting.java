package ou.ist.de.protocol.routing.rsabase;

import java.net.InetAddress;
import java.util.HashMap;

import ou.ist.de.protocol.packet.Packet;
import ou.ist.de.protocol.routing.RoutingProtocol;

public class RSABaseSecureRouting extends RoutingProtocol {
	public static int RSA_KEY_LENGTH=4098;
	
	@Override
	public void receivePacket(Packet p) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initialize(HashMap<String, String> params) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected Packet generateInitialRequestPacket(InetAddress dest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Packet generateInitialReplyPacket(Packet p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Packet generateForwaringPacket(Packet p) {
		// TODO Auto-generated method stub
		return null;
	}

}
