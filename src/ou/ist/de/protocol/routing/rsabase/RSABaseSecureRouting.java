package ou.ist.de.protocol.routing.rsabase;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.security.KeyPair;
import java.security.Signature;
import java.util.HashMap;

import ou.ist.de.protocol.Constants;
import ou.ist.de.protocol.packet.Packet;
import ou.ist.de.protocol.routing.RoutingProtocol;
import ou.ist.de.protocol.routing.dsr.RouteInfo;

public class RSABaseSecureRouting extends RoutingProtocol {
	protected KeyPair kp;
	protected Signature sig;
	protected int keySize;
	protected int sigBitLength;
	protected RouteInfo ri;
	protected Signatures sigs;
	protected boolean verifyAll;
	protected SignatureOperation so;

	public RSABaseSecureRouting() {
	}

	public RSABaseSecureRouting(HashMap<String, String> params) {
		super(params);
	}

	@Override
	protected void initialize(HashMap<String, String> params) {
		// TODO Auto-generated method stub
		if(params.containsKey("-sigbitlen")) {
			this.sigBitLength = Integer.valueOf(params.get("-sigbitlen"));
		}
		else {
			this.sigBitLength=2048;
		}
		this.setKeys("rsa"+this.sigBitLength+"_100keys.properties", "4", ""+this.sigBitLength);
		ri = new RouteInfo();
		sigs = new Signatures(this.sigBitLength);
		this.verifyAll=false;
		so=new SignatureOperation(params);
	}

	@Override
	protected Packet operateRequestPacket(Packet p) {
		// TODO Auto-generated method stub
		this.sigs.clear();
		this.ri.clear();
		this.ri.addNode(this.node.getAddress());
		//System.out.println("request in RSA "+p.toString());
		Packet pkt=this.sign(p);
		//System.out.println("verify:"+this.verify(p) );
		return pkt;
	}

	@Override
	protected Packet operateReplyPacket(Packet p) {
		// TODO Auto-generated method stub
		this.separateOption(p);
		if (this.ri.isContained(this.node.getAddress())) {
			return null;
		}
		if(!this.verify(p)) {
			System.out.println("verification: false");
			return null;
		}
		System.out.println("verification: true");
		ri.addNode(this.node.getAddress());
		p.setNext(ri.get(ri.size() - 2));
		return this.sign(p);
	}

	@Override
	protected Packet operateRequestForwardingPacket(Packet p) {
		// TODO Auto-generated method stub
		this.separateOption(p);

		if (this.ri.isContained(this.node.getAddress())) {
			return null;
		}
		if(this.verifyAll) {
			if(!this.verify(p)) {
				return null;
			}
		}
		this.ri.addNode(this.node.getAddress());
		p.setHops(p.getHops() + 1);
		p.setSndr(this.node.getAddress());
		return this.sign(p);
	}

	@Override
	protected Packet operateReplyForwardingPacket(Packet p) {
		// TODO Auto-generated method stub
		this.separateOption(p);
		
		if (!this.ri.isContained(this.node.getAddress())) {
			System.out.println(" not contained");
			return null;
		}
		for (int i = 0; i < this.ri.size(); i++) {
			if (this.ri.get(i).equals(this.node.getAddress())) {
				p.setNext(this.ri.get(i - 1));
				break;
			}
		}
		p.setSndr(this.node.getAddress());
		p.setHops(p.getHops() + 1);
		
		return p;
	}

	protected void separateOption(Packet p) {
		ri.clear();
		sigs.clear();
		if (p.getOption() == null) {
			return;
		}
		ri.fromBytes(p.getOption());
		sigs.fromOption(p.getOption());

	}

	public Packet sign(Packet p) {
		this.sigs.add(this.sign(this.generateTargetData(this.ri, this.sigs)));

		ByteBuffer bb = ByteBuffer.allocate(this.ri.byteLength() + sigs.byteLength());
		bb.put(this.ri.toBytes());
		bb.put(this.sigs.toBytes());
		p.setOption(bb.array());
		return p;
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

	

	public boolean verify(Packet pkt) {
		boolean ret = true;
		System.out.println("signature length ="+this.sigs.size());
		for (int i = 1; ret && (i <= (this.sigs.size())); i++) {
			ret &= verify(this.ri, this.sigs, i);
			System.out.println(i + ":" + ret);
		}
		return ret;
	}

	protected boolean verify(RouteInfo ri, Signatures sigs, int limit) {
		if (limit <= 0) {
			return false;
		}
		byte[] riBytes=ri.toBytes();
		//System.out.println("varify data riBytes");
		//this.printByteArray(riBytes);
		byte[] sigBytes=sigs.toBytes();
		int riLength=limit*Constants.InetAddressLength;
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
			int keySize = Integer.valueOf(strkeysize);
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(keyFile));
			KeyPair[] kparray = ((KeyPair[]) ois.readObject());
			ois.close();
			kp = kparray[index];
			this.keySize = keySize / 8;
			sig = Signature.getInstance("MD5WithRSA");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	protected void printByteArray(byte[] ba) {
		if(ba==null) {
			System.out.println("[null]");
		}
		String s="["+ba[0];
		for(int i=1;i<ba.length;i++) {
			s+=(","+ba[i]);
		}
		System.out.println(s+"]");
	}

}
