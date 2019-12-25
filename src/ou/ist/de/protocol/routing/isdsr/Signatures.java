package ou.ist.de.protocol.routing.isdsr;

import java.nio.ByteBuffer;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import ou.ist.de.protocol.Constants;

public class Signatures {
	protected Element sig1;
	protected Element sig2;
	protected Element sig3;
	
	protected Signatures() {
		
	}
	public Signatures(Pairing pairing) {
		this.initializeSignatures(pairing);
	}
	public Signatures(byte[] ba, Pairing pairing) {
		this.fromBytes(ba, pairing);
	}
	public byte[] toBytes() {
		byte[] s1 = sig1.toBytes();
		byte[] s2 = sig2.toBytes();
		byte[] s3 = sig3.toBytes();
		ByteBuffer bb = ByteBuffer.allocate(s1.length + s2.length + s3.length + Integer.BYTES * 3);
		bb.putInt(s1.length);
		bb.put(s1);
		bb.putInt(s2.length);
		bb.put(s2);
		bb.putInt(s3.length);
		bb.put(s3);
		// System.out.println("signature to byte size="+bb.capacity());
		return bb.array();
	}
	public void fromBytes(byte[] ba, Pairing pairing) {
		if (ba == null) {
			this.initializeSignatures(pairing);
			return;
		}
		ByteBuffer bb = ByteBuffer.wrap(ba);
		int length = 0;
		byte[] sigBytes = null;
		for (int i = 0; i < 3; i++) {
			length = bb.getInt();
			//System.out.println(length); //add
			sigBytes = new byte[length];
			bb.get(sigBytes);
			setSig(i, pairing.getG1().newElementFromBytes(sigBytes));
		}
	}
	public void fromOption(byte[] opt, Pairing pairing) {
		ByteBuffer bb=ByteBuffer.wrap(opt);
		int cnt=bb.getInt();
		bb.position(bb.position()+cnt*Constants.InetAddressLength);
		if(bb.position()==bb.limit()) {
			this.initializeSignatures(pairing);
			return;
		}
		byte[] sigs=new byte[bb.limit()-bb.position()];
		bb.get(sigs);
		this.fromBytes(sigs, pairing);
	}
	protected void initializeSignatures(Pairing pairing) {
		System.out.println("initial signature");
		sig1 = pairing.getG1().newElement().setToOne();
		sig2 = pairing.getG1().newElement().setToOne();
		sig3 = pairing.getG1().newElement().setToOne();
	}
	public void setSig(int index, Element e) {
		switch (index) {
		case 0: {
			sig1 = e;
			break;
		}
		case 1: {
			sig2 = e;
			break;
		}
		case 2: {
			sig3 = e;
			break;
		}
		}
	}

	public String toString() {
		String ret = "sig1:" + sig1.toString() + "\n";
		ret += "sig2:" + sig2.toString() + "\n";
		ret += "sig3:" + sig3.toString() + "\n";
		return ret;
	}

}
