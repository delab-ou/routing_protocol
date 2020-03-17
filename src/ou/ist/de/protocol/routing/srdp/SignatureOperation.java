package ou.ist.de.protocol.routing.srdp;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.util.HashMap;
import java.security.interfaces.RSAPublicKey;
import java.security.interfaces.RSAPrivateKey;

import ou.ist.de.protocol.Constants;
import ou.ist.de.protocol.routing.dsr.RouteInfo;

public class SignatureOperation {

	protected BigInteger secExp;
	protected BigInteger pubExp;
	protected BigInteger modulus;
	protected PublicKeyPair pk;
	public SignatureOperation() {

	}

	public SignatureOperation(HashMap<String, String> params) {
		String sigBitLength = params.get(Constants.ARG_SIG_BIT_LENGTH);
		try {
			KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
			gen.initialize(Integer.valueOf(sigBitLength));
			KeyPair kp=gen.generateKeyPair();
			pubExp = ((RSAPublicKey) kp.getPublic()).getPublicExponent();
			secExp = ((RSAPrivateKey) kp.getPrivate()).getPrivateExponent();
			modulus = ((RSAPublicKey) kp.getPublic()).getModulus();
			pk=new PublicKeyPair();
			pk.pubExp=pubExp;
			pk.modulus=modulus;
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public PublicKeyPair getPublicKeyPair() {
		return pk;
	}
	public Signature sign(RouteInfo ri, Signature signature, PublicKeyPairs pkp) {
		byte[] data=this.generateTargetData(ri);
		BigInteger hash = new BigInteger(this.hashCalc(data));
		//System.out.println("hash=" + hash);
		boolean b = false;
		if (signature.sig != null) {
			
			if (signature.sig.compareTo(this.modulus) > 0) {// -1 pre < modulus, 0 pre==modulus, 1 pre>modulus
				signature.sig = signature.sig.subtract(this.modulus);
				//System.out.println("modulus=" + this.modulus);
				b = true;
				System.out.println("sig is larger than modulus");
				this.pk.flag=1;
			}
			hash = hash.add(signature.sig);
		}
		hash = hash.mod(this.modulus).modPow(this.secExp, this.modulus);
		//System.out.println("sig=" + hash);
		return new Signature(hash);
	}
	protected byte[] generateTargetData(RouteInfo ri) {
		byte[] riBytes = ri.toBytes();
		ByteBuffer bb = ByteBuffer
				.allocate(riBytes.length - Integer.BYTES);
		bb.put(riBytes, Integer.BYTES, (riBytes.length - Integer.BYTES));
		
		return bb.array();

	}

	public boolean verify(RouteInfo ri, Signature sig, PublicKeyPairs pkp) {
		boolean ret = true;
		// System.out.println("signature length ="+sigs.size());
		
		byte[] data=this.generateTargetData(ri);
		System.out.println("route "+ri);
		BigInteger tmp=sig.sig;
		//System.out.println("ri length="+ri.size()+" pkp length="+pkp.size());
		//System.out.println("pkp = "+pkp);
		//System.out.println("pkp size="+pkp.size());
		for(int i=pkp.size()-1;i>=0;i--) {
			PublicKeyPair pk=pkp.get(i);
			//System.out.println("pk="+pk);
			BigInteger h=new BigInteger(this.hashCalc(data, pk.modulus.toByteArray(), pk.pubExp.toByteArray()));
			tmp=tmp.modPow(pk.pubExp, pk.modulus);
			tmp=tmp.subtract(h.mod(pk.modulus));
			if(pk.isFlag()) {
				tmp=tmp.add(pk.modulus);
				System.out.println("flag is on");
			}
			//System.out.println("tmp="+tmp);
		}
		ret=(tmp.compareTo(BigInteger.ZERO)==0);
		return ret;
	}


	
	
	public byte[] hashCalc(byte[] data) {
		return this.hashCalc(data, this.modulus.toByteArray(), this.pubExp.toByteArray());
	}
	public byte[] hashCalc(byte[] data, byte[] modulus, byte[] pubExp) {
		ByteBuffer bb = ByteBuffer.allocate(data.length + modulus.length + pubExp.length);
		bb.put(data);
		bb.put(modulus);
		bb.put(pubExp);

		byte[] ret = null;
		try {
			MessageDigest sha = MessageDigest.getInstance("MD5");
			sha.update(bb.array());
			
			BigInteger tmp=new BigInteger(sha.digest());
			if(tmp.compareTo(BigInteger.ZERO)<0) {
				tmp=tmp.negate();
			}
			ret = tmp.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;

	}
}