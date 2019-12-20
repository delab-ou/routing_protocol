package ou.ist.de.protocol.packet;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

public class PacketDefrag {
	
	protected HashMap<String,ArrayList<FragmentedPacket>> fragmentedPackets;
	
	public PacketDefrag() {
		fragmentedPackets=new HashMap<String,ArrayList<FragmentedPacket>>();
	}
	public Packet packetDefragmentation(FragmentedPacket fp) {
		String h=fp.baseInformation()+"totalLength:"+fp.totalLength+" totalCount:"+fp.totalCount;
		//System.out.println("key is "+h);
		ArrayList<FragmentedPacket> alfp=null;
		Packet ret=null;
		if(fp.totalLength==1) {
			return translate(fp);
		}
		if(fragmentedPackets.containsKey(h)) {
			alfp=fragmentedPackets.get(h);
		}
		else {
			alfp=new ArrayList<FragmentedPacket>();
			fragmentedPackets.put(h, alfp);
		}
		boolean dup=false;
		for(int i=0;i<alfp.size();i++) {
			if(fp.index==alfp.get(i).index) {
				dup=true;
			}
		}
		if(!dup) {
			alfp.add(fp);
		}
		if(alfp.size()==fp.totalCount) {
			alfp.sort((a,b)-> a.index-b.index);
			ByteBuffer bb=ByteBuffer.allocate(fp.totalLength);
			ret=new Packet();
			ret.copy(fp);
			for(int i=0;i<alfp.size();i++) {
				bb.put(alfp.get(i).fragmented);
			}
			ret.option=bb.array();
			alfp.clear();
			fragmentedPackets.remove(h);
		}
		
		return ret;
	}
	protected Packet translate(FragmentedPacket fp) {
		Packet ret=new Packet();
		ret.copy(fp);
		ret.option=new byte[fp.fragmented.length];
		for(int i=0;i<ret.option.length;i++) {
			ret.option[i]=fp.fragmented[i];
		}
		return ret;
	}
	
}
