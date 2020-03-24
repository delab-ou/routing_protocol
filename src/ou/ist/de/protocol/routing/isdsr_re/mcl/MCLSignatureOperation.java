package ou.ist.de.protocol.routing.isdsr_re.mcl;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.math.BigInteger;

import com.herumi.mcl.Fr;
import com.herumi.mcl.G1;
import com.herumi.mcl.Mcl;

import ou.ist.de.protocol.routing.dsr.RouteInfo;
import ou.ist.de.protocol.routing.isdsr.MasterKey;
import ou.ist.de.protocol.routing.isdsr_re.SignatureOperation;
import ou.ist.de.protocol.routing.isdsr_re.Signatures;
public class MCLSignatureOperation extends SignatureOperation{

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		/*
		 * mpk1 G2 
		 * mpk2 G2
		 * mpk3 G2
		 * 
		 * msk1 Fr
		 * msk2 Fr
		 * 
		 * isk1 G1
		 * isk2 G1
		 * 
		 * sig1 G1
		 * sig2 G1
		 * sig3 G2
		 */
	}

	@Override
	public void setup(String keyfile, String indexstr) {
		// TODO Auto-generated method stub
		try {
			int index = Integer.valueOf(indexstr);
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(keyfile));
			MasterKey mk = (MasterKey) ois.readObject();
			ois.close();
			mpk.setMPK1(mk.ale[index].g);
			msk.setMSK1(mk.ale[index].a1);
			msk.setMSK2(mk.ale[index].a2);
			G1 mpk1=new G1();
			mpk1.deserialize(mpk.getMPK1());
			G1 mpk2=new G1();
			Fr msk1=new Fr();
			msk1.deserialize(msk.getMSK1());
			Mcl.mul(mpk2, mpk1, msk1);
			mpk.setMPK2(mpk2.serialize());
			
			G1 mpk3=new G1();
			Fr msk2=new Fr();
			msk2.deserialize(msk.getMSK2());
			Mcl.mul(mpk3, mpk1, msk2);
			mpk.setMPK3(mpk3.serialize());
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void keyDerivation(String uid) {
		// TODO Auto-generated method stub
		//System.out.println("key derivation uid=" + uid);
		G1 hash1=this.H1(uid);//H1(uidByte); G1
		G1 hash2=this.H2(uid);//H2(uidByte); G1
				// System.out.println("generate sk " + uid);
		Fr msk1=new Fr();
		msk1.deserialize(msk.getMSK1());
		Fr msk2=new Fr();
		msk2.deserialize(msk.getMSK2());
		Mcl.mul(hash1, hash1, msk1);
		Mcl.mul(hash2, hash2, msk2);
		isk.setISK1(hash1.serialize());
		isk.setISK2(hash2.serialize());
				
	}

	@Override
	public byte[] sign(RouteInfo ri, Signatures sigs) {
		// TODO Auto-generated method stub
		String uid=ri.get(ri.size()-1).toString();
		String msg=ri.getAddrSequence();
		
		//System.out.println("sign:uid=" + uid + " msg=" + msg);
		Fr r=new Fr();
		Fr x=new Fr();
		r.setByCSPRNG();
		x.setByCSPRNG();
		// ElementPowPreProcessing pppmpkg1=mpk.g1.getElementPowPreProcessing();
		// Element immpkg1=mpk.g1.getImmutable();
		
		//r = pairing.getZr().newRandomElement();
		//x = pairing.getZr().newRandomElement();
		G1 sig1=sigFromBytes(sigs.getSIG1());
		G1 sig2=sigFromBytes(sigs.getSIG2());
		G1 sig3=sigFromBytes(sigs.getSIG3());
		G1 mpk1=new G1();
		
		mpk1.deserialize(mpk.getMPK1());
		
		G1 newsig1=new G1();
		G1 newsig2=new G1();
		G1 newsig3=new G1();
		Mcl.mul(newsig3, mpk1, x); // tmpsigs[2]=xg
		Mcl.add(newsig3, newsig3, sig3);//tmpsigs[2]=xg+sig[2] sig3' is done
		Mcl.mul(newsig2, mpk1, r); // tmpsigs[1]=rg
		Mcl.add(newsig2, newsig2, sig2); //tmpsigs[1]=rg+sig[1] sig2' is done
		
		G1 tmp=new G1();
		Mcl.mul(tmp, sig3, r);// r*sig3
		Mcl.add(newsig1, sig1, tmp);//tmpsigs[0] = r*sig+sig1
		Mcl.mul(tmp, newsig2, x);// tmp = sig2'*x
		Mcl.add(newsig1, newsig1, tmp);//tmpsigs[0]=tmpsigs[0] + sig2' * x
		G1 tmpisk=new G1();
		tmpisk.deserialize(isk.getISK2());// sk2:alpha2*H2(ID)
		Mcl.add(newsig1, newsig1, tmpisk);//tmpsigs[0]=tmpsigs[0]+sk2
		tmpisk.deserialize(isk.getISK1());//sk1:alpha1*H1(ID)
		Fr hash=H3(uid+msg);//H3(ID || m)
		Mcl.mul(tmp, tmpisk, hash);//sk1 * H3(ID||m);
		Mcl.add(newsig1, newsig1, tmp);
		
		sigs.setSIG1(newsig1.serialize());
		sigs.setSIG2(newsig2.serialize());
		sigs.setSIG3(newsig3.serialize());
		System.out.println("verification = "+this.verify(ri, sigs));
		return sigs.toBytes();
	}

	@Override
	public boolean verify(RouteInfo ri, Signatures sigs) {
		// TODO Auto-generated method stub
		boolean ret=false;
		System.out.println("start verification");
		String[] uid = ri.getAddrArray();
		String msg = "";
		int num=ri.size();
		
		G1 sig1=sigFromBytes(sigs.getSIG1());
		G1 sig2=sigFromBytes(sigs.getSIG2());
		G1 sig3=sigFromBytes(sigs.getSIG3());

		G1 mpk1=new G1();
		mpk1.deserialize(mpk.getMPK1());
		G1 mpk2=new G1();
		mpk2.deserialize(mpk.getMPK2());
		G1 mpk3=new G1();
		mpk3.deserialize(mpk.getMPK3());
		
		GT e1=new GT();
		Mcl.p
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
		return false;
	}
	
	protected G1 H1(String str) {
		G1 ret = new G1();
		Mcl.hashAndMapToG1(ret, str.getBytes());
		return ret;
		
	}
	protected G1 H2(String str) {
		G1 ret=new G1();
		byte[] src=super.sha256Generator(str.getBytes());
		Mcl.hashAndMapToG1(ret, src);
		return ret;
	}
	protected Fr H3(String str) {
		BigInteger hash=new BigInteger(super.sha256Generator(str.getBytes()));
		Fr ret=new Fr(hash.toString());
		return ret;
	}
	protected G1 sigFromBytes(byte[] b) {
		G1 ret=new G1();
		if(b==null) {
			ret.setStr("1");
		}
		else {
			ret.deserialize(b);
		}
		return ret;
	}
}
