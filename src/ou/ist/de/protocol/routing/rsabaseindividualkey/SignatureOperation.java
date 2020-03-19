package ou.ist.de.protocol.routing.rsabaseindividualkey;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;

import ou.ist.de.protocol.Constants;
import ou.ist.de.protocol.routing.dsr.RouteInfo;

public class SignatureOperation {

	protected BigInteger secExp;
	protected PublicKeyPair pk;

	public SignatureOperation() {

	}

	public SignatureOperation(HashMap<String, String> params) {
		String sigBitLength = params.get(Constants.ARG_SIG_BIT_LENGTH);
		try {
			KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
			gen.initialize(Integer.valueOf(sigBitLength));
			KeyPair kp = gen.generateKeyPair();
			secExp = ((RSAPrivateKey) kp.getPrivate()).getPrivateExponent();
			pk = new PublicKeyPair();
			pk.pubExp = ((RSAPublicKey) kp.getPublic()).getPublicExponent();
			pk.modulus = ((RSAPublicKey) kp.getPublic()).getModulus();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public PublicKeyPair getKeyPair() {
		return pk;
	}

	public byte[] sign(RouteInfo ri, Signatures sigs) {
		byte[] data = this.generateTargetData(ri, sigs);
		BigInteger hash = new BigInteger(this.hashCalc(data)).mod(pk.modulus);
		System.out.println("hash=" + hash);
		hash = hash.modPow(this.secExp, pk.modulus);
		System.out.println("keys="+pk.toString());
		System.out.println("signing sig="+hash);
		System.out.println("sig length="+hash.toByteArray().length);
		return hash.toByteArray();
	}

	protected byte[] generateTargetData(RouteInfo ri, Signatures ss) {
		byte[] riBytes = ri.toBytes();
		byte[] sigBytes = ss.toBytes();
		ByteBuffer bb = ByteBuffer
				.allocate(riBytes.length - Integer.BYTES + ((sigBytes == null) ? 0 : sigBytes.length));
		bb.put(riBytes, Integer.BYTES, (riBytes.length - Integer.BYTES));
		if (sigBytes != null) {
			bb.put(sigBytes);
		}
		return bb.array();

	}

	public boolean verify(RouteInfo ri, Signatures sigs, PublicKeyPairs pkp) {
		boolean ret = true;
		System.out.println("in verify ri sigs, signature length =" + sigs.size());
		RouteInfo tmpri=new RouteInfo();
		Signatures tmpsigs=new Signatures();
		PublicKeyPairs tmppkp=new PublicKeyPairs();
		PublicKeyPair tmppk=null;
		byte[] s=null;
		BigInteger tmps=null;
		BigInteger hash=null;
		for(int i=0;i<ri.size();i++) {
			System.out.println("i="+i);
			tmpri.addNode(ri.get(i));
			tmppk=pkp.get(i);
			System.out.println("keys="+tmppk.toString());
			s=sigs.get(i);
			tmps=new BigInteger(s);
			System.out.println("sig="+tmps);
			byte[] h=this.generateTargetData(tmpri, tmpsigs);
			hash=new BigInteger(this.hashCalc(h, tmppk.modulus.toByteArray(), tmppk.pubExp.toByteArray())).mod(tmppk.modulus);
			System.out.println("verify hash="+hash);
			tmps=tmps.modPow(tmppk.pubExp, tmppk.modulus);
			System.out.println("hash="+hash);
			ret=ret&& (BigInteger.ZERO.compareTo(hash.subtract(tmps))==0);
			if(!ret) {
				System.out.println("------ verification false -----");
			}
			tmpsigs.add(sigs.get(i));
			
		}
		return ret;
	}

	protected void printByteArray(byte[] ba) {
		if (ba == null) {
			System.out.println("[null]");
		}
		String s = "[" + ba[0];
		for (int i = 1; i < ba.length; i++) {
			s += ("," + ba[i]);
		}
		System.out.println(s + "]");
	}

	public byte[] hashCalc(byte[] data) {
		return this.hashCalc(data, pk.modulus.toByteArray(), pk.pubExp.toByteArray());
	}

	public byte[] hashCalc(byte[] data, byte[] modulus, byte[] pubExp) {
		ByteBuffer bb = ByteBuffer.allocate(data.length + modulus.length + pubExp.length);
		bb.put(data);
		bb.put(modulus);
		bb.put(pubExp);

		byte[] ret = null;
		try {
			MessageDigest sha = MessageDigest.getInstance("SHA-256");
			sha.update(bb.array());

			BigInteger tmp = new BigInteger(sha.digest());
			if (tmp.compareTo(BigInteger.ZERO) < 0) {
				tmp = tmp.negate();
			}
			ret = tmp.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;

	}
}
