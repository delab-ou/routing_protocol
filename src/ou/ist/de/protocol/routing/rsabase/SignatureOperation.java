package ou.ist.de.protocol.routing.rsabase;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.security.KeyPair;
import java.security.Signature;
import java.util.HashMap;

import ou.ist.de.protocol.Constants;
import ou.ist.de.protocol.routing.dsr.RouteInfo;

public class SignatureOperation {
	
	protected KeyPair kp;
	protected Signature sig;
	
	public SignatureOperation() {
		
	}
	public SignatureOperation(HashMap<String,String> params) {
		String sigBitLength=params.get(Constants.ARG_SIG_BIT_LENGTH);
		String index=params.get(Constants.ARG_KEY_INDEX);
	
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
			//System.err.println("sig="+sig);
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
		for (int i = 0; ret && (i <ri.size()); i++) {
			ret &= verify(ri, sigs, i);
			//System.out.println(i + ":" + ret);
		}
		return ret;
	}
	
	protected boolean verify(RouteInfo ri, Signatures sigs, int limit) {
		
		byte[] riBytes=ri.toBytes();
		System.out.println("varify data riBytes");
		this.printByteArray(riBytes);
		byte[] sigBytes=sigs.toBytes();
		this.printByteArray(sigBytes);
		
		
		
		
		System.out.println("ri.size = "+ri.size()+" sigs.size="+sigs.size());
		System.out.println(ri.toString());
		System.out.println(sigs.toString());
		int riLength=(limit+1)*Constants.InetAddressLength;
		int sigLength=limit*sigs.sigLength;
		System.err.println("---rilen="+riLength+" siglen="+sigLength+" limit="+limit+" riBytes="+riBytes.length);
		ByteBuffer bb=ByteBuffer.allocate(riLength+sigLength);
		bb.put(riBytes,Integer.BYTES,riLength);
		System.out.println("verify bb put ribytes "+bb);
		this.printByteArray(bb.array());
		if(sigs.get(limit)==null) {
			System.out.println("sig:"+limit+" is null");
			return false;
		}
		if(sigLength != 0) {
			bb.put(sigBytes,0,limit*sigs.sigLength);
		}
		System.out.println("sigs ["+limit+"]="+sigs.get(limit));
		return verify(bb.array(), sigs.get(limit));
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

}
