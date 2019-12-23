package ou.ist.de.protocol.routing.srdp;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.security.KeyPair;
import java.security.Signature;
import java.util.HashMap;

import ou.ist.de.protocol.Constants;
import ou.ist.de.protocol.routing.dsr.RouteInfo;
import ou.ist.de.protocol.routing.srdp.Signatures;

public class SignatureOperation {
	
	protected KeyPair kp;
	protected Signature sig;
	
	public SignatureOperation() {
		
	}
	public SignatureOperation(HashMap<String,String> params) {
		String sigBitLength="2048";
		String index="10";
	
		
		if(params.containsKey("-sigBitLength")) {
			sigBitLength=params.get("-sigBitLength");
		}
		if(params.containsKey("-keyIndex")) {
			index=params.get("-keyIndex");
		}
		String keyFile="rsa"+sigBitLength+"_100keys.properties";
		this.setKeys(keyFile, index, sigBitLength);
	}
	
	public byte[] sign(RouteInfo ri, Signatures sigs) {
		return this.sign(this.generateTargetData(ri, sigs));
	}
	
	protected byte[] sign(byte[] data) {
		//System.out.println("sign data");
		//this.printByteArray(data);
		try {
			sig.initSign(kp.getPrivate());
			sig.update(data);
			return sig.sign();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	protected byte[] generateTargetData(RouteInfo ri, Signatures ss) {
		byte[] riBytes = ri.toBytes();
		byte[] sigBytes = ss.toBytes();
		ByteBuffer bb = ByteBuffer.allocate(riBytes.length - Integer.BYTES + ((sigBytes == null) ? 0 : sigBytes.length));
		bb.put(riBytes,Integer.BYTES,(riBytes.length-Integer.BYTES));
		if (sigBytes != null) {
			bb.put(sigBytes);
		}
		return bb.array();

	}
	public boolean verify(RouteInfo ri, Signatures sigs) {
		boolean ret = true;
		System.out.println("signature length ="+sigs.size());
		for (int i = 1; ret && (i <= (sigs.size())); i++) {
			ret &= verify(ri, sigs, i);
			System.out.println(i + ":" + ret);
		}
		return ret;
	}
	
	protected boolean verify(RouteInfo ri, Signatures sigs, int limit) {
		
		//
		if (limit <= 0) {
			return false;
		}
		byte[] riBytes=ri.toBytes();
		//System.out.println("varify data riBytes");
		//this.printByteArray(riBytes);
		byte[] sigBytes=sigs.toBytes();
		int riLength=ri.size()*Constants.InetAddressLength;
		int sigLength=(limit-1)*sigs.sigLength;
		//System.out.println("---rilen="+riLength+" siglen="+sigLength);
		ByteBuffer bb=ByteBuffer.allocate(riLength+sigLength);
		bb.put(riBytes,Integer.BYTES,riLength);
		if(sigLength != 0) {
			bb.put(sigBytes,0,(limit-1)*sigs.sigLength);
		}
		return verify(bb.array(), sigs.get(limit - 1));
	}

	protected boolean verify(byte[] data, byte[] sign) {
		boolean ret = false;
		//System.out.println("varify data");
		//this.printByteArray(data);
		//System.out.println("verify sig");
		//this.printByteArray(sign);
		
		try {
			sig.initVerify(kp.getPublic());
			sig.update(data);
			ret = sig.verify(sign);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	protected void setKeys(String keyFile, String strindex, String strkeysize) {
		try {
			int index = Integer.valueOf(strindex);
			System.out.println("key size=" + strkeysize);
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(keyFile));
			KeyPair[] kparray = ((KeyPair[]) ois.readObject());
			ois.close();
			kp = kparray[index];
			sig = Signature.getInstance("MD5WithRSA");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}