package ou.ist.de.protocol.node;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import ou.ist.de.protocol.packet.FragmentedPacket;
import ou.ist.de.protocol.packet.Packet;
import ou.ist.de.protocol.packet.PacketDefrag;
import ou.ist.de.protocol.routing.RoutingProtocol;
import ou.ist.de.protocol.Constants;

public class Receiver implements Runnable{

	protected DatagramSocket ds;
	protected boolean loop;
	protected PacketDefrag pd;
	protected RoutingProtocol rp;
	
	public Receiver(DatagramSocket ds) {
		this.ds=ds;
		pd=new PacketDefrag();
	}
	public Receiver(DatagramSocket ds,RoutingProtocol rp) {
		this.ds=ds;
		this.rp=rp;
		pd=new PacketDefrag();
	}
	public void setLoopTrue() {
		this.loop=true;
	}
	public void setLoopFalse() {
		this.loop=false;
	}
	@Override
	public void run() {
		DatagramPacket dp=null;
		while(loop) {
			try {
				//System.out.println("In Receiver run start receiving");
				dp = new DatagramPacket(new byte[Constants.RCVBUFFER], Constants.RCVBUFFER);
				ds.receive(dp);
				//System.out.println(dp+"   "+dp);
				byte[] data=dp.getData();
				//System.out.println("receved datagram data = "+data.length);
				FragmentedPacket fp=new FragmentedPacket(data);
				//System.out.println(fp.toString());
				Packet p=pd.packetDefragmentation(fp);
				//System.out.println("In Receiver run "+((p!=null)?p.toString():"null"));
				if(p==null) {
					continue;
				}
				rp.receivePacket(p);
				
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		
	}

}
