package ou.ist.de.protocol.routing.srdp;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import ou.ist.de.protocol.Constants;

public class PublicKeyPairs {

	protected ArrayList<PublicKeyPair> alpk;
	
	public PublicKeyPairs() {
		alpk=new ArrayList<PublicKeyPair>();
	}
	
	public void clear() {
		alpk.clear();
	}
	public int size() {
		return alpk.size();
	}
	public PublicKeyPair get(int index) {
		if(index>=alpk.size()) {
			return null;
		}
		return alpk.get(index);
	}
	public void add(PublicKeyPair pk) {
		alpk.add(pk);
	}
	public byte[] toBytes() {
		ArrayList<byte[]> alb=new ArrayList<byte[]>();
		int size=0;
		byte[] bd=null;
		for(int i=0;i<alpk.size();i++) {
			bd=alpk.get(i).toBytes();
			size+=bd.length;
			alb.add(bd);
		}
		ByteBuffer bb=ByteBuffer.allocate(size);
		for(int i=0;i<alb.size();i++) {
			bb.put(alb.get(i));
		}
		return bb.array();
	}
	public void fromBytes(byte[] b) {
		//System.out.println("frombytes public key pairs bytelength="+b.length);
		alpk.clear();
		ByteBuffer bb=ByteBuffer.wrap(b);
		int num=bb.getInt();
		//System.out.println("ri length="+num);
		bb.position(Integer.BYTES+num*Constants.InetAddressLength);
		//System.out.println("bb position="+bb.position());
		int siglen=bb.getInt();
		//System.out.println("sig length="+siglen);
		bb.position(bb.position()+siglen);
		//System.out.println("bb position="+bb.position());
		
		int elen=0,mlen=0;
		byte[] e=null,m=null;
		PublicKeyPair pkp=null;
		while(bb.position()<bb.capacity()) {
			elen=bb.getInt();
			e=new byte[elen];
			bb.get(e);
			mlen=bb.getInt();
			m=new byte[mlen];
			bb.get(m);
			pkp=new PublicKeyPair();
			pkp.setPublicKeyPair(e, m);
			pkp.flag=bb.get();
			//System.out.println("public key pairs pkp="+pkp);
			alpk.add(pkp);
		}
	}
	public String toString() {
		String ret="";
		for(int i=0;i<this.alpk.size();i++) {
			if(alpk.get(i)==null) {
				ret+="[null]";
			}
			else {
				ret+="["+alpk.get(i)+"]";
			}
		}
		
		return ret;
	}
}
