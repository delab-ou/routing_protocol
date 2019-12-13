package ou.ist.de.protocol;

import java.net.InetAddress;

import ou.ist.de.protocol.node.Node;
import ou.ist.de.protocol.packet.Packet;
import ou.ist.de.protocol.routing.FloodingRouting;
import ou.ist.de.protocol.routing.RoutingProtocol;

public class MainTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Constants.FSIZE=20;
		int p1size=605;
		int psize=505;
		try {
			Packet p0=new Packet(
					(byte)0,
					1,
					InetAddress.getByName("10.0.0.2"),
					InetAddress.getByName("10.0.0.5"),
					InetAddress.getByName("10.0.0.2"),
					InetAddress.getByName("192.168.100.255"),
					1,
					new byte[psize]
					);
			
			Packet p1=new Packet(
					(byte)0,
					1,
					InetAddress.getByName("10.0.0.2"),
					InetAddress.getByName("10.0.0.5"),
					InetAddress.getByName("10.0.0.2"),
					InetAddress.getByName("192.168.100.255"),
					1,
					new byte[p1size]
					);
			
			byte[] tmp=p0.getOption();
			
			for(int i=0;i<psize;i++) {
				tmp[i]=(byte)(i/20);
			}
			tmp=p1.getOption();
			for(int i=0;i<p1size;i++) {
				tmp[i]=(byte)(i/20);
			}
			Node node=new Node(10000,new int[] {192,168,100});
			RoutingProtocol rp=new FloodingRouting();
			node.setRoutingProtocol(rp);
			node.start();
			node.send(p1);
			node.send(p0);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

}
