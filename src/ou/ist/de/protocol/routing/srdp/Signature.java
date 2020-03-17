package ou.ist.de.protocol.routing.srdp;

import java.math.BigInteger;
import java.nio.ByteBuffer;

import ou.ist.de.protocol.Constants;

public class Signature {
	protected BigInteger sig;
	protected int siglen;
	protected int bitlen;
	
	public Signature() {
		
	}
	public Signature(int bitlen) {
		this.bitlen=bitlen;
	}
	public Signature(byte[] sigbyte) {
		this.sig=new BigInteger(sigbyte);
		this.siglen=sigbyte.length;
	}
	public Signature(BigInteger sig) {
		this.sig=sig;
	}
	public int byteLength() {
		return sig.toByteArray().length;
	}
	public int fromBytes(byte[] b) {
		ByteBuffer bb=ByteBuffer.wrap(b);
		int num=bb.getInt();
		bb.position(Integer.BYTES+num*Constants.InetAddressLength);
		int siglen=bb.getInt();
		byte[] sigbytes=new byte[siglen];
		bb.get(sigbytes);
		this.sig=new BigInteger(sigbytes);
		return bb.position();
	}
	public byte[] toBytes() {
		ByteBuffer bb=ByteBuffer.allocate(Integer.BYTES+sig.toByteArray().length);
		bb.putInt(sig.toByteArray().length);
		bb.put(sig.toByteArray());
		return bb.array();
	}
	
}
