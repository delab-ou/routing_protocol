package ou.ist.de.protocol.packet;

import java.util.ArrayList;

import ou.ist.de.protocol.Constants;

public class PacketFrag {
	
	public void packetFragmentation(ArrayList<FragmentedPacket> alp, Packet p) {

		int num=p.option.length/Constants.FSIZE+((p.option.length%Constants.FSIZE)!=0?1:0);
		
		FragmentedPacket fp=null;
		int s=0,e=0;
		
		if(num==0) {
			fp=new FragmentedPacket(p);
			fp.totalLength=1;
			fp.totalCount=1;
			fp.index=1;
			fp.fragmented=new byte[p.option.length];
			alp.add(fp);
		}
		else {
			for(int i=0;i<num;i++) {
				fp=new FragmentedPacket();
				fp.copy(p);
				fp.totalLength=p.option.length;
				fp.totalCount=num;
				fp.index=i;
				s=e;
				e=e+Constants.FSIZE;
				if(e>=p.option.length) {
					e=p.option.length;
				}
				fp.fragmented=new byte[(e-s)];
				for(int j=0;j<(e-s);j++) {
					fp.fragmented[j]=p.option[s+j];
				}
				alp.add(fp);
			}
		}
	}
}
