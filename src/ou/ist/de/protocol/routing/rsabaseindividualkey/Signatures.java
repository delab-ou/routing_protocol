package ou.ist.de.protocol.routing.rsabaseindividualkey;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import ou.ist.de.protocol.Constants;

public class Signatures {

	protected int sigLength;
	protected ArrayList<byte[]> sigs;
	protected int currentSize;
	
	public Signatures() {
		sigs = new ArrayList<byte[]>();
		currentSize=0;
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
	protected void moveSignature(byte[] sig1, byte[] sig2) {
		for(int i=0;i<sig1.length;i++) {
			sig1[i]=sig2[i];
		}
	}
	protected byte[] move(byte[] ba1, byte[] ba2, int offset, int length) {
		if(ba2.length<(offset+length)) {
			return null;
		}
		for(int i=0;i<length;i++) {
			ba1[i]=ba2[i+offset];
		}
		return ba1;
		
	}
	
	protected void fromBytes(byte[] bs) {
		sigs.clear();
		
		if (bs == null) {
			return;
		}
		ByteBuffer bb=ByteBuffer.wrap(bs);
		int num=bb.getInt();
		bb.position(Integer.BYTES+num*Constants.InetAddressLength);
		//int total=bb.getInt();
		for(int i=0;i<num;i++) {
			int len=bb.getInt();
			byte[] s=new byte[len];
			bb.get(s);
			sigs.add(s);
		}
	}

	public void add(byte[] s) {
		sigs.add(s);
	}

	public byte[] get(int index) {
		if (index >= sigs.size()) {
			return null;
		}
		return sigs.get(index);
	}

	public int byteLength() {
		return sigs.size() * this.sigLength;
	}

	public byte[] toBytes() {
		if (this.sigs.size() == 0) {
			return null;
		}
		//System.err.println("siglength = "+this.sigLength);
		int length=Integer.BYTES*(sigs.size()+1);//total length and each length
		for(int i=0;i<sigs.size();i++) {
			length+=sigs.get(i).length;
		}
		
		ByteBuffer bb = ByteBuffer.allocate(length);
		
		try {
			//System.err.println("in Signatures toBytes bytelength=" + this.byteLength() + " bb=" + bb + " sigs=" + sigs);
			System.out.println("signature total length="+length);
			bb.putInt(length);
			for (int i = 0; i < sigs.size(); i++) {
				byte[] b=this.sigs.get(i);
				bb.putInt(b.length);
				bb.put(b);
				System.out.println("sigs["+i+"]="+b.length);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Catch Exeption in Signatures toBytes bytelength=" + this.byteLength() + " bb=" + bb+ " sigs=" + sigs);
			System.exit(1);
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
		//return this.currentSize;
	}

	public void clear() {
		this.sigs.clear();
		//this.currentSize=0;
	}
}
