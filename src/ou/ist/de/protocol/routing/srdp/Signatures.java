package ou.ist.de.protocol.routing.srdp;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import ou.ist.de.protocol.Constants;

public class Signatures {

	protected int sigLength;
	protected ArrayList<byte[]> sigs;

	public Signatures() {
		sigs = new ArrayList<byte[]>();
	}
	public Signatures(int sigBitLength) {
		this.sigLength = sigBitLength / 8;
		sigs = new ArrayList<byte[]>();
	}

	public Signatures(int sigBitLength, byte[] bs) {
		this.sigLength = sigBitLength / 8;
		sigs = new ArrayList<byte[]>();
		this.fromBytes(bs);
	}
	protected void fromOption(byte[] opt) {
		sigs.clear();
		ByteBuffer bb=ByteBuffer.wrap(opt);
		int num=bb.getInt();
		bb.position(Integer.BYTES+num*Constants.InetAddressLength);
		byte[] sig=null;
		while(bb.limit()>bb.position()) {
			//System.out.println("limit="+bb.limit()+" pos="+bb.position()+" siglength="+this.sigLength);
			sig=new byte[sigLength];
			bb.get(sig);
			sigs.add(sig);
		}
	}
	
	protected void fromBytes(byte[] bs) {
		sigs.clear();
		if(bs==null) {
			return;
		}
		int cnt = bs.length / this.sigLength;
		byte[] b = null;

		ByteBuffer bb = ByteBuffer.wrap(bs);
		for (int i = 0; i < cnt; i++) {
			b = new byte[this.sigLength];
			bb.get(b);
			sigs.add(b);
		}
	}

	public void add(byte[] s) {
		sigs.add(s);
	}
	public byte[] get(int index) {
		if(index>=sigs.size()) {
			return null;
		}
		return sigs.get(index);
	}

	public int byteLength() {
		return sigs.size() * this.sigLength;
	}

	public byte[] toBytes() {
		if(this.sigs.size()==0) {
			return null;
		}
		ByteBuffer bb = ByteBuffer.allocate(this.byteLength());
		for (int i = 0; i < sigs.size(); i++) {
			bb.put(sigs.get(i));
		}
		return bb.array();
	}

	public int getSigLength() {
		return this.sigLength;
	}
	public void setSigLength(int sigLength) {
		this.sigLength = sigLength;
	}
	public int size() {
		return this.sigs.size();
	}
	public void clear() {
		this.sigs.clear();
	}
}