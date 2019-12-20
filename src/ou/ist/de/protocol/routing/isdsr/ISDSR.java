package ou.ist.de.protocol.routing.isdsr;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.ElementPowPreProcessing;
import it.unisa.dia.gas.jpbc.Pairing;
import ou.ist.de.protocol.packet.Packet;
import ou.ist.de.protocol.routing.RoutingProtocol;
import ou.ist.de.protocol.routing.isdsr.ibsas.ISK;
import ou.ist.de.protocol.routing.isdsr.ibsas.MPK;
import ou.ist.de.protocol.routing.isdsr.ibsas.MSK;
import ou.ist.de.protocol.routing.isdsr.ibsas.MasterKey;
import ou.ist.de.protocol.routing.isdsr.ibsas.MasterKey.Elements;

public class ISDSR extends RoutingProtocol {
	protected Pairing pairing;
	protected MPK mpk;
	protected MSK msk;
	protected ISK isk;
	protected ElementPowPreProcessing mpkg1;
	protected ElementPowPreProcessing isksk1;
	@Override
	protected void initialize(HashMap<String, String> params) {
		// TODO Auto-generated method stub

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
	
	protected void setKeys(String keyParamFile, String indexstr) {
		try {
			int index = Integer.valueOf(indexstr);
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(keyParamFile));
			MasterKey mk = (MasterKey) ois.readObject();
			ois.close();
			mpk = new MPK();
			msk = new MSK();
			// ===== set up =====//
			mpk.g1 = pairing.getG1().newElementFromBytes(mk.ale[index].g);
			msk.a1 = pairing.getZr().newElementFromBytes(mk.ale[index].a1);
			msk.a2 = pairing.getZr().newElementFromBytes(mk.ale[index].a2);
			mpk.g2 = mpk.g1.duplicate().powZn(msk.a1);
			mpk.g3 = mpk.g1.duplicate().powZn(msk.a2);
			isk = new ISK();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public class MPK {
		protected Element g1;
		protected Element g2;
		protected Element g3;
		
		public String toString(){
			String ret="mpk.g1:"+g1.toString()+"\n";
			ret+="mpk.g2:"+g2.toString()+"\n";
			ret+="mpk.g3:"+g3.toString()+"\n";
			return ret;
		}
	}
	public class MSK {
		protected Element a1;
		protected Element a2;
		
		public String toString(){
			String ret="msk.a1:"+a1.toString()+"\n";
			ret+="msk.a2:"+a2.toString()+"\n";
			return ret;
		}
	}
	public class ISK {
		protected Element sk1;
		protected Element sk2;
		
		public String toString(){
			String ret="isk.sk1:"+sk1.toString()+"\n";
			ret+="isk.sk2:"+sk2.toString()+"\n";
			return ret;
		}
	}

	public class MasterKey implements Serializable{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 777499020697297341L;
		public String paramFile;
		public Elements[] ale;
		
		public MasterKey(){
			
		}
		public MasterKey(int num){
			ale=new Elements[num];
		}
		public class Elements implements Serializable{
			/**
			 * 
			 */
			private static final long serialVersionUID = 7795506650471853238L;
			public byte[] g;
			public byte[] a1;
			public byte[] a2;
		}
	}
	

}
