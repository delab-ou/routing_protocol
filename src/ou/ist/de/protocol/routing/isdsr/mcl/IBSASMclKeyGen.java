package ou.ist.de.protocol.routing.isdsr.mcl;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

import ou.ist.de.protocol.routing.isdsr.ISDSRKey;
import ou.ist.de.protocol.routing.isdsr.MasterKey;

import com.herumi.mcl.G1;
import com.herumi.mcl.G2;
import com.herumi.mcl.Fr;
import com.herumi.mcl.Mcl;

public class IBSASMclKeyGen {
	static {
        String lib = "mcljava";
        String libName = System.mapLibraryName(lib);
        System.out.println("libName : " + libName);
        System.loadLibrary(lib);
	}
	public void generateKeys(String paramFile, String outFile, int repeat) {
		//System.out.println(java.library.path);
		int param=Mcl.BN254;
		if(paramFile.equalsIgnoreCase("bls12_381")) {
			param=Mcl.BLS12_381;
		}
		else if(paramFile.equalsIgnoreCase("bn254")) {
			param=Mcl.BN254;
		}
		else {
			System.out.println("parameter "+paramFile+" is not supported");
			return ;
		}
		
		//System.loadLibrary(System.mapLibraryName("mcljava"));
		Mcl.SystemInit(param); // curveType = Mcl.BN254 or Mcl.BLS12_381
		
		MasterKey mk = new MasterKey(repeat);
		mk.paramFile = paramFile;
		Random rnd = new Random();	
		G1 g1=new G1();
		G2 g2=new G2();
		Fr fr1=null;
		Fr fr2=null;
		
		String rndv=null;
		for (int i = 0; i < repeat; i++) {
			rndv=String.valueOf(rnd.nextInt());
			Mcl.hashAndMapToG1(g1, rndv.getBytes());
			Mcl.hashAndMapToG2(g2, rndv.getBytes());

			fr1=new Fr();
			fr1.setByCSPRNG();
			fr2=new Fr();
			fr2.setByCSPRNG();

			mk.keys[i]=new ISDSRKey(4);
			mk.keys[i].set(0,g1.serialize());
			mk.keys[i].set(1,g2.serialize());
			mk.keys[i].set(2,fr1.serialize());
			mk.keys[i].set(3,fr2.serialize());

			System.out.println("gen No. "+i+" key");
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
		IBSASMclKeyGen kg=new IBSASMclKeyGen();
		kg.generateKeys("bn254", "bn254.keys", 100);
		kg.generateKeys("bls12_381", "bls12_381.keys", 100);
	}
}
