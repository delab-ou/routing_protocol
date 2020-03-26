package ou.ist.de.protocol.routing.isdsr.mcl;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.util.HashMap;

import com.herumi.mcl.Fr;
import com.herumi.mcl.G1;
import com.herumi.mcl.G2;
import com.herumi.mcl.GT;
import com.herumi.mcl.Mcl;

import ou.ist.de.protocol.routing.dsr.RouteInfo;
import ou.ist.de.protocol.routing.isdsr.ISDSRKeys;
import ou.ist.de.protocol.routing.isdsr.MasterKey;
import ou.ist.de.protocol.routing.isdsr.SignatureOperation;
import ou.ist.de.protocol.routing.isdsr.Signatures;

public class MCLSignatureOperation extends SignatureOperation {
	static {
		String lib = "mcljava";
		String libName = System.mapLibraryName(lib);
		System.out.println("libName : " + libName);
		System.loadLibrary(lib);
	}

	public MCLSignatureOperation(HashMap<String, String> params, String uid){
		super(params,uid);
	}
	@Override
	public void initialize(HashMap<String, String> params, String uid) {
		/*
		 * mpk1 G1 and G2 mpk2 G2 mpk3 G2
		 * 
		 * msk1 Fr msk2 Fr
		 * 
		 * isk1 G1 isk2 G1
		 * 
		 * sig1 G1 sig2 G1 sig3 G1 and G2
		 */
		this.keys=new ISDSRKeys(4, 2, 2);
		String keyfile = "bls12_381.keys";
		int curve=Mcl.BLS12_381;
		if (params.containsKey("-keyfile")) {
			keyfile = params.get("-keyfile");
		}
		if(keyfile.toLowerCase().startsWith("bn254")){
			keyfile="bn254.keys";
			curve=Mcl.BN254;
		}
		Mcl.SystemInit(curve);
		this.setup(keyfile, "5");
		this.keyDerivation(uid);
	}

	@Override
	public void setup(String keyfile, String indexstr) {
		try {
			int index = Integer.valueOf(indexstr);
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(keyfile));
			MasterKey mk = (MasterKey) ois.readObject();
			ois.close();
			keys.setMPK(0, mk.keys[index].get(0));// g G1
			keys.setMPK(1, mk.keys[index].get(1));// g G2
			keys.setMSK(0, mk.keys[index].get(2));// a1 Fr
			keys.setMSK(1, mk.keys[index].get(3));// a2 Fr

			G1 mpk1g1 = new G1();
			mpk1g1.deserialize(keys.getMPK(0));
			keys.setMPK(0, mpk1g1.serialize());
			G2 mpk1g2 =new G2();
			mpk1g2.deserialize(keys.getMPK(1));
			keys.setMPK(1, mpk1g2.serialize());

			G2 mpk2 =new G2();
			Fr msk1 = new Fr();
			msk1.deserialize(keys.getMSK(0));
			Mcl.mul(mpk2, mpk1g2, msk1);
			keys.setMPK(2,mpk2.serialize());// a1g G2

			G2 mpk3 = new G2();
			Fr msk2 = new Fr();
			msk2.deserialize(keys.getMSK(1));
			Mcl.mul(mpk3, mpk1g2, msk2);
			keys.setMPK(3,mpk3.serialize());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void keyDerivation(String uid) {
		// System.out.println("key derivation uid=" + uid);
		G1 hash1 = this.H1(uid);// H1(uidByte); G1
		G1 hash2 = this.H2(uid);// H2(uidByte); G1
		// System.out.println("generate sk " + uid);
		Fr msk1 = new Fr();
		msk1.deserialize(keys.getMSK(0));
		Fr msk2 = new Fr();
		msk2.deserialize(keys.getMSK(1));
		Mcl.mul(hash1, hash1, msk1);
		Mcl.mul(hash2, hash2, msk2);
		keys.setISK(0,hash1.serialize());
		keys.setISK(1,hash2.serialize());

	}

	@Override
	public byte[] sign(RouteInfo ri, Signatures sigs) {
		String uid = ri.get(ri.size() - 1).toString();
		String msg = ri.getAddrSequence();

		// System.out.println("sign:uid=" + uid + " msg=" + msg);
		Fr r = new Fr();
		Fr x = new Fr();
		r.setByCSPRNG();
		x.setByCSPRNG();
		Signatures ret= (sigs==null)?new Signatures(4):sigs;

		G1 sig1 = sigFromBytesG1(ret.get(0));
		G1 sig2 = sigFromBytesG1(ret.get(1));
		G1 sig3g1 = sigFromBytesG1(ret.get(2));
		G2 sig3g2 = sigFromBytesG2(ret.get(3));

		G1 mpk1g1 = new G1();
		G2 mpk1g2 = new G2();
		mpk1g1.deserialize(keys.getMPK(0));
		mpk1g2.deserialize(keys.getMPK(1));

		G1 newsig1 = new G1();
		G1 newsig2 = new G1();
		G1 newsig3g1 = new G1();
		G2 newsig3g2 = new G2();

		Mcl.mul(newsig3g1, mpk1g1, x); // newsig3g1=xg
		Mcl.mul(newsig3g2, mpk1g2, x); // newsig3g2=xg
		Mcl.add(newsig3g1, newsig3g1, sig3g1);// newsig3g1=xg+sig3g1 sig3' is done
		Mcl.add(newsig3g2, newsig3g2, sig3g2);// newsig3g2=xg+sig3g2 sig3' is done
		Mcl.mul(newsig2, mpk1g1, r); // newsig2=rg
		Mcl.add(newsig2, newsig2, sig2); // newsig2=rg+sig2 sig2' is done

		G1 tmp = new G1();
		Mcl.mul(tmp, sig3g1, r);// r*sig3
		Mcl.add(newsig1, sig1, tmp);// newsig1 = r*sig+sig1
		Mcl.mul(tmp, newsig2, x);// tmp = sig2'*x
		Mcl.add(newsig1, newsig1, tmp);// newsig1=newsig1 + sig2' * x
		G1 tmpisk = new G1();
		tmpisk.deserialize(keys.getISK(1));// sk2:alpha2*H2(ID)
		Mcl.add(newsig1, newsig1, tmpisk);// tmpsigs[0]=tmpsigs[0]+sk2
		tmpisk.deserialize(keys.getISK(0));// sk1:alpha1*H1(ID)
		Fr hash = H3(uid + msg);// H3(ID || m)
		Mcl.mul(tmp, tmpisk, hash);// sk1 * H3(ID||m);
		Mcl.add(newsig1, newsig1, tmp);

		ret.set(0,newsig1.serialize());
		ret.set(1,newsig2.serialize());
		ret.set(2,newsig3g1.serialize());
		ret.set(3,newsig3g2.serialize());
		System.out.println("verification = " + this.verify(ri, ret));
		return ret.toBytes();
	}

	@Override
	public boolean verify(RouteInfo ri, Signatures sigs) {
		boolean ret = false;
		System.out.println("start verification");
		String[] uid = ri.getAddrArray();
		String msg = "";
		int num = ri.size();

		G1 sig1 = sigFromBytesG1(sigs.get(0));
		G1 sig2 = sigFromBytesG1(sigs.get(1));
		G1 sig3g1 = sigFromBytesG1(sigs.get(2));
		G2 sig3g2 = sigFromBytesG2(sigs.get(3));

		G1 mpk1g1 = new G1();
		mpk1g1.deserialize(keys.getMPK(0));
		G2 mpk1g2 = new G2();
		mpk1g2.deserialize(keys.getMPK(1));
		G2 mpk2 = new G2();
		mpk2.deserialize(keys.getMPK(2));
		G2 mpk3 = new G2();
		mpk3.deserialize(keys.getMPK(3));

		GT t1 = new GT();
		GT t2 = new GT();
		Mcl.pairing(t1, sig1, mpk1g2);
		Mcl.pairing(t2, sig2, sig3g2);

		String m1 = msg;

		G1 t3=new G1();
		t3.clear();
		G1 t7=new G1();
		t7.clear();
		G1 t4=null,t5=null;
		Fr t6=null;
		for (int i = 0; i < num; i++) {
			t4= H2(uid[i]);// Hash2(uid[i].getBytes());
			Mcl.add(t3,t3,t4);
			t5 = H1(uid[i]);// Hash1(uidByte);
			m1 = m1 + uid[i];
			t6 = H3((uid[i] + m1));// Hash3(m2.getBytes());
			Mcl.mul(t5,t5,t6);
			Mcl.add(t7,t7,t5);
		}
		GT t8 =new GT();
		GT t9=new GT();
		Mcl.pairing(t8, t3, mpk3);
		Mcl.pairing(t9,t7,mpk2);
		
		Mcl.mul(t2, t2, t8);
		Mcl.mul(t2,t2,t9);
		
		
		if (t1.equals(t2)) {
			ret = true;
		} 
		return ret;
	}

	protected G1 H1(String str) {
		G1 ret = new G1();
		Mcl.hashAndMapToG1(ret, str.getBytes());
		return ret;

	}

	protected G1 H2(String str) {
		G1 ret = new G1();
		byte[] src = super.sha256Generator(str.getBytes());
		Mcl.hashAndMapToG1(ret, src);
		return ret;
	}

	protected Fr H3(String str) {
		BigInteger hash = new BigInteger(super.sha256Generator(str.getBytes()));
		Fr ret = new Fr(hash.toString());
		return ret;
	}

	protected G1 sigFromBytesG1(byte[] b) {
		G1 ret = new G1();
		if (b == null) {
			ret.clear();
			//ret.setStr("0 0 0");
		} else {
			ret.deserialize(b);
		}
		return ret;
	}
	protected G2 sigFromBytesG2(byte[] b) {
		G2 ret = new G2();
		if (b == null) {
			ret.clear();
			//ret.setStr("0 0 0");
		} else {
			ret.deserialize(b);
		}
		return ret;
	}

}
