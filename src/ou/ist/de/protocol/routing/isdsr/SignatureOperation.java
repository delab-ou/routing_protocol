package ou.ist.de.protocol.routing.isdsr;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.security.MessageDigest;
import java.util.HashMap;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.ElementPowPreProcessing;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import ou.ist.de.protocol.routing.dsr.RouteInfo;
import ou.ist.de.protocol.routing.isdsr.ISK;
import ou.ist.de.protocol.routing.isdsr.MPK;
import ou.ist.de.protocol.routing.isdsr.MSK;

public class SignatureOperation {
	
	public Pairing pairing;
	protected MPK mpk;
	protected MSK msk;
	protected ISK isk;
	protected ElementPowPreProcessing mpkg1;
	protected ElementPowPreProcessing isksk1;

	enum HashType {
		H1, H2, H3
	};
	public SignatureOperation() {
		
	}
	public SignatureOperation(HashMap<String,String> params, String uid) {
		String keyfile="a.keys";
		String paramFile="a.properties";
		if(params.containsKey("-keyfile")) {
			keyfile=params.get("-keyfile");
		}
		PairingFactory.getInstance().setUsePBCWhenPossible(true);
		System.out.println("use pbc:"+PairingFactory.getInstance().isPBCAvailable());
		this.setPairing(paramFile);
		this.setKeys(keyfile, "5");
		this.keyDerivation(uid);
	}
	
	protected void setPairing(String paramFile) {
		pairing = PairingFactory.getPairing(paramFile);
	}
	public byte[] sign(RouteInfo ri, Signatures sigs) {
		String uid=ri.get(ri.size()-1).toString();
		String msg=ri.getAddrSequence();
		
		//System.out.println("sign:uid=" + uid + " msg=" + msg);
		Element r, x;
		// ElementPowPreProcessing pppmpkg1=mpk.g1.getElementPowPreProcessing();
		// Element immpkg1=mpk.g1.getImmutable();
		r = pairing.getZr().newRandomElement();
		x = pairing.getZr().newRandomElement();
		Element s1,s2,s3;
		if(sigs.getSIG1()==null) {
			s1=pairing.getG1().newElement().setToOne();
		}
		else {
			s1=pairing.getG1().newElementFromBytes(sigs.getSIG1());
		}
		if(sigs.getSIG2()==null) {
			s2=pairing.getG1().newElement().setToOne();
		}
		else {
			s2=pairing.getG1().newElementFromBytes(sigs.getSIG2());
		}
		if(sigs.getSIG3()==null) {
			s3=pairing.getG1().newElement().setToOne();
		}
		else {
			s3=pairing.getG1().newElementFromBytes(sigs.getSIG3());
		}
		Element t1 = mpkg1.powZn(x);
		Element t2 = mpkg1.powZn(r);
		Element t3 = s3.duplicate().powZn(r);// r*sig3

		s3.add(t1);
		s2.add(t2);

		Element t4 = s2.duplicate();
		t4.powZn(x);// x*sig2'
		Element t5 = Hash(HashType.H3, (uid + msg));// Hash3((uid +
													// msg).getBytes());//H3(ID||msg)

		// Element t6 = isk.sk1.duplicate().powZn(t5);// H3(ID||msg)a1H1(ID)
		Element t6 = isksk1.powZn(t5);
		s1.add(t3);
		s1.add(t4);
		Element sk2=pairing.getG1().newElementFromBytes(isk.getISK2());
		s1.add(sk2);
		s1.add(t6);
		sigs.setSIG1(s1.toBytes());
		sigs.setSIG2(s2.toBytes());
		sigs.setSIG3(s3.toBytes());
		System.out.println("verification = "+this.verify(ri, sigs));
		return sigs.toBytes();
	}
	
	public boolean verify(RouteInfo ri, Signatures sigs) {
		boolean ret=false;
		System.out.println("start verification");
		String[] uid = ri.getAddrArray();
		String msg = "";
		int num=ri.size();

		Element s1=pairing.getG1().newElementFromBytes(sigs.getSIG1());
		Element s2=pairing.getG1().newElementFromBytes(sigs.getSIG2());
		Element s3=pairing.getG1().newElementFromBytes(sigs.getSIG3());
		Element mpk1=pairing.getG1().newElementFromBytes(mpk.getMPK1());
		Element mpk2=pairing.getG1().newElementFromBytes(mpk.getMPK2());
		Element mpk3=pairing.getG1().newElementFromBytes(mpk.getMPK3());
		
		Element t1 = pairing.pairing(s1, mpk1);// e(sig1,g)
		Element t2 = pairing.pairing(s2, s3);// e(sig2,sig3)
		Element t3 = pairing.getG1().newElement().setToZero();
		Element t4, t5, t6;
		Element t7 = pairing.getG1().newElement().setToZero();
		Element t8, t9;
		
		String m1 = msg;
		for (int i = 0; i < num; i++) {
			t4 = Hash(HashType.H2, uid[i]);// Hash2(uid[i].getBytes());
			t3.add(t4);
			t5 = Hash(HashType.H1, uid[i]);// Hash1(uidByte);
			m1 = m1 + uid[i];
			t6 = Hash(HashType.H3, (uid[i] + m1));// Hash3(m2.getBytes());
			t5.powZn(t6);
			t7.add(t5);
		}
		t8 = pairing.pairing(t3, mpk3);
		t9 = pairing.pairing(t7, mpk2);
		t2.mul(t8);
		t2.mul(t9);

		if (t1.isEqual(t2)) {
			ret = true;
		} else {
			if (t1.invert().isEqual(t2)) {
				ret = true;
			}
		}
		return ret;
	}
	
	
	public void keyDerivation(String uid) {

		//System.out.println("key derivation uid=" + uid);
		Element e1 = Hash(HashType.H1, uid);// Hash1(uidByte); G1
		Element e2 = Hash(HashType.H2, uid);// Hash2(uidByte); G1
		// System.out.println("generate sk " + uid);
		
		//isk.sk1 = e1.powZn(msk.a1);
		//isk.sk2 = e2.powZn(msk.a2);
		
		isk.setISK1(e1.powZn(pairing.getZr().newElementFromBytes(msk.getMSK1())).toBytes());
		isk.setISK2(e2.powZn(pairing.getZr().newElementFromBytes(msk.getMSK2())).toBytes());
		
		//mpkg1 = mpk.g1.getElementPowPreProcessing();
		//isksk1 = isk.sk1.getElementPowPreProcessing();
		mpkg1= pairing.getG1().newElementFromBytes(mpk.getMPK1()).getElementPowPreProcessing();
		isksk1 = pairing.getZr().newElementFromBytes(isk.getISK1()).getElementPowPreProcessing();
	}

	protected Element HashFromField(Field f, byte[] src) {
		return f.newElementFromHash(src, 0, src.length);
	}

	protected byte[] sha256Generator(byte[] src) {
		byte[] ret = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(src);
			ret = md.digest();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	
	protected Element Hash(HashType ht, String str) {

		byte[] src = null;
		Field f = null;
		switch (ht) {
		case H1: {
			src = str.getBytes();
			f = pairing.getG1();
			break;
		}
		case H2: {
			src = sha256Generator(str.getBytes());
			f = pairing.getG1();
			break;
		}
		case H3: {
			src = sha256Generator(str.getBytes());
			f = pairing.getZr();
			break;
		}
		}
		return HashFromField(f, src);
	}
	protected void setKeys(String keyParamFile, String indexstr) {
		try {
			int index = Integer.valueOf(indexstr);
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(keyParamFile));
			MasterKey mk = (MasterKey) ois.readObject();
			ois.close();
			mpk = new MPK();
			msk = new MSK();
			// ===== set up =====//
			//mpk.g1 = pairing.getG1().newElementFromBytes(mk.ale[index].g);
			//msk.a1 = pairing.getZr().newElementFromBytes(mk.ale[index].a1);
			//msk.a2 = pairing.getZr().newElementFromBytes(mk.ale[index].a2);
			//mpk.g2 = mpk.g1.duplicate().powZn(msk.a1);
			//mpk.g3 = mpk.g1.duplicate().powZn(msk.a2);
			
			//mpk.g1 = pairing.getG1().newElementFromBytes(mk.ale[index].g);
			mpk.setMPK1(mk.ale[index].g);
			//msk.a1 = pairing.getZr().newElementFromBytes(mk.ale[index].a1);
			msk.setMSK1(mk.ale[index].a1);
			//msk.a2 = pairing.getZr().newElementFromBytes(mk.ale[index].a2);
			msk.setMSK2(mk.ale[index].a2);
			//mpk.g2 = mpk.g1.duplicate().powZn(msk.a1);
			Element e=pairing.getG1().newElementFromBytes(mpk.getMPK1());
			mpk.setMPK2(e.duplicate().powZn(pairing.getZr().newElementFromBytes(msk.getMSK1())).toBytes());
			//mpk.g3 = mpk.g1.duplicate().powZn(msk.a2);
			mpk.setMPK3(e.duplicate().powZn(pairing.getG1().newElementFromBytes(msk.getMSK2())).toBytes());
			isk = new ISK();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	protected Element getG1Element(byte[] data) {
		if(data==null) {
			return pairing.getG1().newElement().setToOne();
		}
		else {
			return pairing.getG1().newElementFromBytes(data);
		}
	}
	
}
