package ou.ist.de.protocol.packet;

import java.net.InetAddress;
import java.util.ArrayList;

import ou.ist.de.protocol.Constants;

public class PacketTest {

	public static void main(String[] args) {
		Packet p = new Packet();
		Packet p1 = new Packet();
		PacketFrag pf=new PacketFrag();
		PacketDefrag pd=new PacketDefrag();
		Constants.FSIZE=20;
		int p1size=605;
		int psize=505;
		try {
			p.dest = InetAddress.getByName("10.0.0.5");
			p.src= InetAddress.getByName("10.0.0.2");
			p.sndr= InetAddress.getByName("10.0.0.2");
			p.next= InetAddress.getByName("10.0.0.2");
			p.type=0;
			p.seq=1;
			p.hops=1;
			p.option=new byte[psize];
			
			p1.dest = InetAddress.getByName("10.0.0.5");
			p1.src= InetAddress.getByName("10.0.0.2");
			p1.sndr= InetAddress.getByName("10.0.0.6");
			p1.next= InetAddress.getByName("10.0.0.255");
			p1.type=0;
			p1.seq=1;
			p1.hops=1;
			p1.option=new byte[p1size];

			for(int i=0;i<psize;i++) {
				p.option[i]=(byte)(i/20);
			}
			for(int i=0;i<p1size;i++) {
				p1.option[i]=(byte)(i/20);
			}
			byte[] b=p.toBytes();
			System.out.println(p);
			System.out.println("packet byte length is "+b.length);
			Packet p2=new Packet(b);
			System.out.println(p2);

			ArrayList<FragmentedPacket> alp=new ArrayList<FragmentedPacket>();
			ArrayList<FragmentedPacket> alp1=new ArrayList<FragmentedPacket>();
			pf.packetFragmentation(alp, p2);
			pf.packetFragmentation(alp1, p1);
			for(FragmentedPacket fp:alp) {
				byte[] tmp=fp.toBytes();
				System.out.println(fp);
				System.out.println(new FragmentedPacket(tmp));
			}
			Packet tmpP=null,tmpP1=null;
			
			for(int i=alp1.size()-1;i>=0;i--) {
				if(i<alp.size()) {
				tmpP=pd.packetDefragmentation(alp.get(i));
				}
				tmpP1=pd.packetDefragmentation(alp1.get(i));
				if(tmpP!=null) {
					System.out.println("Packet was defragmented "+tmpP);
				}
				if(tmpP1!=null) {
					System.out.println("Packet1 was defragmented "+tmpP1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for(String s:args) {
			System.out.println(s);
		}
	}

}
