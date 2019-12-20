package ou.ist.de.protocol.routing.isdsr;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

public class IBSASKeyGen {
	public void generateKeys(String paramFile, String outFile, int repeat) {
		Pairing pairing = PairingFactory.getPairing(paramFile);

		MasterKey mk = new MasterKey();
		mk.paramFile = paramFile;
		mk.ale = new MasterKey.Elements[repeat];
		for (int i = 0; i < repeat; i++) {
			mk.ale[i] = mk.new Elements();
			mk.ale[i].g = pairing.getG1().newRandomElement().toBytes();
			mk.ale[i].a1 = pairing.getZr().newRandomElement().toBytes();
			mk.ale[i].a2 = pairing.getZr().newRandomElement().toBytes();
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
}
