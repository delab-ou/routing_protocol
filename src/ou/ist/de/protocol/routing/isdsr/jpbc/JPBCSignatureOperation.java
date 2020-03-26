package ou.ist.de.protocol.routing.isdsr.jpbc;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.ElementPowPreProcessing;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import ou.ist.de.protocol.routing.dsr.RouteInfo;
import ou.ist.de.protocol.routing.isdsr.ISDSRKeys;
import ou.ist.de.protocol.routing.isdsr.MasterKey;
import ou.ist.de.protocol.routing.isdsr.SignatureOperation;
import ou.ist.de.protocol.routing.isdsr.Signatures;
public class JPBCSignatureOperation extends SignatureOperation{
	
	public Pairing pairing;
	protected ElementPowPreProcessing mpkg1;
	protected ElementPowPreProcessing isksk1;

	enum HashType {
		H1, H2, H3
	};
	public JPBCSignatureOperation(HashMap<String,String> params, String uid) {
		super(params,uid);
	}
	
	protected void setPairing(String paramFile) {
		pairing = PairingFactory.getPairing(paramFile);
	}

	@Override
	public byte[] sign(RouteInfo ri, Signatures sigs) {
		String uid=ri.get(ri.size()-1).toString();
		String msg=ri.getAddrSequence();
		
		//System.out.println("sign:uid=" + uid + " msg=" + msg);
		Element r, x;
		// ElementPowPreProcessing pppmpkg1=mpk.g1.getElementPowPreProcessing();
		// Element immpkg1=mpk.g1.getImmutable();
		Signatures ret=sigs;
		if (sigs==null){
			ret=new Signatures(3);
		}
		r = pairing.getZr().newRandomElement();
		x = pairing.getZr().newRandomElement();
		Element s[]=new Element[3];
		byte[] tmp=null;
		for(int i=0;i<s.length;i++){
			tmp=ret.get(i);
			if(tmp==null){
				s[i]=pairing.getG1().newElement().setToOne();
			}
			else{
				s[i]=pairing.getG1().newElementFromBytes(ret.get(i));
			}
		}

		Element t1 = mpkg1.powZn(x);
		Element t2 = mpkg1.powZn(r);
		Element t3 = s[2].duplicate().powZn(r);// r*sig3

		s[2].add(t1);
		s[1].add(t2);

		
		Element t4 = s[1].duplicate().powZn(x);// x*sig2'
		Element t5 = Hash(HashType.H3, (uid + msg));// Hash3((uid +
													// msg).getBytes());//H3(ID||msg)

		// Element t6 = isk.sk1.duplicate().powZn(t5);// H3(ID||msg)a1H1(ID)
		Element t6 = isksk1.powZn(t5);
		
		
		s[0].add(t3);
		s[0].add(t4);
		Element sk2=pairing.getG1().newElementFromBytes(keys.getISK(1));
		
		
		s[0].add(sk2);
		s[0].add(t6);
		
		
		
		ret.set(0,s[0].toBytes());
		ret.set(1,s[1].toBytes());
		ret.set(2,s[2].toBytes());
		//System.out.println("verification = "+this.verify(ri, ret));
		return ret.toBytes();
	}

	@Override
	public boolean verify(RouteInfo ri, Signatures sigs) {
		boolean ret=false;
		System.out.println("start verification");
		String[] uid = ri.getAddrArray();
		String msg = "";
		int num=ri.size();
		Element s[]=new Element[3];
		Element mpk[]=new Element[3];

		for(int i=0;i<s.length;i++){
			s[i]=pairing.getG1().newElementFromBytes(sigs.get(i));
			mpk[i]=pairing.getG1().newElementFromBytes(keys.getMPK(i));
		}
		Element t1 = pairing.pairing(s[0], mpk[0]);// e(sig1,g)
		Element t2 = pairing.pairing(s[1], s[2]);// e(sig2,sig3)
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
		t8 = pairing.pairing(t3, mpk[2]);
		t9 = pairing.pairing(t7, mpk[1]);
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
		keys.setISK(0,e1.powZn(pairing.getZr().newElementFromBytes(keys.getMSK(0))).toBytes());
		keys.setISK(1,e2.powZn(pairing.getZr().newElementFromBytes(keys.getMSK(1))).toBytes());
		
		mpkg1= pairing.getG1().newElementFromBytes(keys.getMPK(0)).getElementPowPreProcessing();
		isksk1 = pairing.getG1().newElementFromBytes(keys.getISK(0)).getElementPowPreProcessing();
	}

	protected Element HashFromField(Field f, byte[] src) {
		return f.newElementFromHash(src, 0, src.length);
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
	
	@Override
	public void initialize(HashMap<String, String> params, String uid) {

		keys=new ISDSRKeys(3, 2, 2);
		String keyfile = "a.keys";
		String paramfile = "a.properties";
		if (params.containsKey("-keyfile")) {
			keyfile = params.get("-keyfile");
		}
		if (params.containsKey("-paramfile")) {
			paramfile = params.get("-paramfile");
		}
		PairingFactory.getInstance().setUsePBCWhenPossible(true);
		System.out.println("use pbc:"+PairingFactory.getInstance().isPBCAvailable());
		this.setPairing(paramfile);
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
			keys.setMPK(0,mk.keys[index].get(0));
			keys.setMSK(0,mk.keys[index].get(1));
			keys.setMSK(1,mk.keys[index].get(2));

			Element e=pairing.getG1().newElementFromBytes(keys.getMPK(0));
			keys.setMPK(1,e.duplicate().powZn(pairing.getZr().newElementFromBytes(keys.getMSK(0))).toBytes());
			keys.setMPK(2,e.duplicate().powZn(pairing.getZr().newElementFromBytes(keys.getMSK(1))).toBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
}
