package ou.ist.de.protocol;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

public class RSAKeyGen {

	public static void main(String args[]) {
		try {
			KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
			gen.initialize(4096);
			KeyPair[] keys = new KeyPair[100];
			System.out.println("key generation");
			for (int i = 0; i < 100; i++) {
				if((i%10)==0) {
					System.out.println();
				}
				System.out.print("key "+i+" ");
				keys[i] = gen.generateKeyPair();
			}
			File f = new File("rsa4096_100keys.properties");
			ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream(f));
			System.out.println("start writing");
			oos.writeObject(keys);
			oos.close();
			} catch (Exception e) {
			return;
		}

	}
}
