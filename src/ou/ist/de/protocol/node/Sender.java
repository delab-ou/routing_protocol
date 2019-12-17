package ou.ist.de.protocol.node;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

import ou.ist.de.protocol.Constants;
import ou.ist.de.protocol.packet.FragmentedPacket;
import ou.ist.de.protocol.packet.Packet;
import ou.ist.de.protocol.packet.PacketFrag;

public class Sender {
	protected DatagramSocket ds;
	protected PacketFrag pf;
	protected ArrayList<FragmentedPacket> alfp;

	public Sender(DatagramSocket ds) {
		this.ds = ds;
		alfp = new ArrayList<FragmentedPacket>();
		pf = new PacketFrag();
	}

	protected void send(DatagramPacket dp) {
		try {
			ds.send(dp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void send(Packet p) {
		alfp.clear();
		pf.packetFragmentation(alfp, p);
		//System.out.println("In Sender send size is "+alfp.size());
		for (FragmentedPacket fp : alfp) {
			byte[] data = fp.toBytes();
			DatagramPacket dp = new DatagramPacket(data, data.length, fp.getNext(), Constants.PORT);
			//System.err.println("send packet to "+dp.getAddress());
			this.send(dp);
		}

	}

}
