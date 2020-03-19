package ou.ist.de.protocol.routing.rsabaseindividualkey;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.interfaces.RSAPublicKey;

public class PublicKeyPair {
	protected BigInteger pubExp;
	protected BigInteger modulus;

	public PublicKeyPair() {

	}

	public PublicKeyPair(RSAPublicKey pk) {
		this.setPublicKeyPair(pk);
	}

	public void setPublicKeyPair(RSAPublicKey pk) {
		pubExp = pk.getPublicExponent();
		modulus = pk.getModulus();
	}

	public void setPublicKeyPair(byte[] e, byte[] m) {
		pubExp=new BigInteger(e);
		modulus=new BigInteger(m);
	}
	public byte[] toBytes() {
		if (pubExp == null) {
			return null;
		}
		ByteBuffer bb = ByteBuffer
				.allocate(this.pubExp.toByteArray().length + this.modulus.toByteArray().length + Integer.BYTES * 2);
		bb.putInt(this.pubExp.toByteArray().length);
		bb.put(this.pubExp.toByteArray());
		bb.putInt(this.modulus.toByteArray().length);
		bb.put(this.modulus.toByteArray());
		System.out.println("public key pair length"+bb.capacity());
		return bb.array();
	}
	public int fromBytes(byte[] b, int offset) {
		ByteBuffer bb = ByteBuffer.wrap(b);
		bb.position(offset);
		int publen=bb.getInt();
		byte[] pubbytes=new byte[publen];
		bb.get(pubbytes);
		int modlen=bb.getInt();
		byte[] modbytes=new byte[modlen];
		bb.get(modbytes);
		this.pubExp=new BigInteger(pubbytes);
		this.modulus=new BigInteger(modbytes);
		return bb.position();
	}
	public int totalSize() {
		return 1+this.pubExp.toByteArray().length + this.modulus.toByteArray().length + Integer.BYTES * 2;
	}
	
	public String toString() {
		return "pubExp="+this.pubExp+"\n"+"modulus="+this.modulus+"\n";
	}
}
