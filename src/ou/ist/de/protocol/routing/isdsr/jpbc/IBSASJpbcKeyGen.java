package ou.ist.de.protocol.routing.isdsr.jpbc;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import ou.ist.de.protocol.routing.isdsr.ISDSRKey;
import ou.ist.de.protocol.routing.isdsr.MasterKey;

public class IBSASJpbcKeyGen {
	public void generateKeys(String paramFile, String outFile, int repeat) {
		Pairing pairing = PairingFactory.getPairing(paramFile);

		MasterKey mk = new MasterKey(repeat);
		mk.paramFile = paramFile;
		for (int i = 0; i < repeat; i++) {
			mk.keys[i] = new ISDSRKey(3);
			mk.keys[i].set(0,pairing.getG1().newRandomElement().toBytes());
			mk.keys[i].set(1,pairing.getZr().newRandomElement().toBytes());
			mk.keys[i].set(2,pairing.getZr().newRandomElement().toBytes());
		}
		try {
			FileOutputStream fos = new FileOutputStream(outFile);
			ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(fos));
			oos.writeObject(mk);
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		IBSASJpbcKeyGen kg=new IBSASJpbcKeyGen();
		kg.generateKeys("a.properties", "a.keys", 100);
		kg.generateKeys("a1.properties", "a1.keys", 100);
		kg.generateKeys("e.properties", "e.keys", 100);
	}
}
