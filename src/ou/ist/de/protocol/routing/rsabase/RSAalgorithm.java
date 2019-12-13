package ou.ist.de.srp.algo.rsa;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.security.KeyPair;
import java.security.Signature;
import java.util.ArrayList;
import java.util.HashMap;

import ou.ist.de.srp.Constants;
import ou.ist.de.srp.algo.AbstractAlgorithm;
import ou.ist.de.srp.packet.Packet;
import ou.ist.de.srp.packet.RouteInfo;

public class RSAalgorithm extends AbstractAlgorithm {
	protected KeyPair kp;
	protected Signature sig;
	protected int keySize;
	protected ArrayList<byte[]> signs;
	
	public RSAalgorithm(HashMap<String,String> params) {
		super(params);
		String keyFile=params.get("keyFile");
		String keySize=params.get("keySize");
		String index=params.get("index");
		setKeys(keyFile,index,keySize);
	}
	protected void setKeys(String keyFile,String strindex,String strkeysize) {
		try {
			int index=Integer.valueOf(strindex);
			System.out.println("key size="+strkeysize);
			int keySize=Integer.valueOf(strkeysize);
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(keyFile));
			KeyPair[] kparray = ((KeyPair[]) ois.readObject());
			kp = kparray[index];
			this.keySize = keySize/8;
			sig = Signature.getInstance("MD5WithRSA");
			signs = new ArrayList<byte[]>();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	protected boolean verify(RouteInfo ri, ArrayList<byte[]> signs, int limit) {
		if (limit <= 0) {
			return false;
		}
		byte[] ribyte = ri.toBytes();
		ByteBuffer bb;
		int dataLength = limit * Constants.InetAddressLength + ((limit - 1) * keySize);
		bb = ByteBuffer.allocate(dataLength);
		bb.put(ribyte, 0, limit * Constants.InetAddressLength);
		for (int i = 0; i < limit - 1; i++) {
			bb.put(signs.get(i));
		}
		return verify(bb.array(), signs.get(limit - 1));
	}

	protected boolean verify(byte[] data, byte[] sign) {
		boolean ret = false;
		try {
			sig.initVerify(kp.getPublic());
			sig.update(data);
			ret = sig.verify(sign);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	@Override
	public byte[] sign(Packet pkt) {
		// TODO Auto-generated method stub
		byte[] ribyte = pkt.getRi().toBytes();
		byte[] sigbyte=pkt.getSig();
		ByteBuffer bb = ByteBuffer.allocate(ribyte.length+((sigbyte==null)?0:sigbyte.length));
		if(sigbyte==null) {
			bb.put(ribyte);
		}
		else {
			generateSignatureFromBytes(sigbyte);
			bb.put(ribyte);
			bb.put(sigbyte);
		}
		
		byte[] data = bb.array();
		try {
			sig.initSign(kp.getPrivate());
			sig.update(data);
			signs.add(sig.sign());
		} catch (Exception e) {
			e.printStackTrace();
		}
		bb=ByteBuffer.allocate(signs.size()*keySize);
		for(int i=0;i<signs.size();i++) {
			System.out.println("sign "+i+" length="+signs.get(i).length);
			bb.put(signs.get(i));
		}
		
		return bb.array();
	}
	protected Packet checkPacketFormat(Packet pkt) {
		int length = pkt.getRi().getRiLength();
		ByteBuffer bb=ByteBuffer.wrap(pkt.getSig());
		byte[] sigbyte=new byte[length*keySize];
		bb.get(sigbyte);
		pkt.setSig(sigbyte);
		return pkt;
	}
	@Override
	public boolean verify(Packet pkt) {
		boolean ret = true;
		checkPacketFormat(pkt);
		generateSignatureFromBytes(pkt.getSig());
		System.out.println(signs.size()-1);
		for (int i = 1; ret && (i < (signs.size()-1)); i++) {
			ret &= verify(pkt.getRi(), signs, i);
			System.out.println(i + ":" + ret);
		}
		return ret;
	}
	public void generateSignatureFromBytes(byte[] bytes) {
		// TODO Auto-generated method stub
		ByteBuffer bb = ByteBuffer.wrap(bytes);
		signs.clear();
		byte[] sign = null;
		while (bb.hasRemaining()) {
			sign = new byte[keySize];
			bb.get(sign);
			signs.add(sign);
		}
	}
}
