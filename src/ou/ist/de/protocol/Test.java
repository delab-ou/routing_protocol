package ou.ist.de.protocol;

import com.herumi.mcl.Fr;
import com.herumi.mcl.G1;
import com.herumi.mcl.G2;
import com.herumi.mcl.Mcl;

public class Test {
	static {
		String lib = "mcljava";
		String libName = System.mapLibraryName(lib);
		System.out.println("libName : " + libName);
		System.loadLibrary(lib);
	}
	public static void main(String args[]) {
		Mcl.SystemInit(Mcl.BLS12_381);
		G1 g1=new G1();
		System.out.println("g1 init="+g1);
		G1 gtmp=new G1();
		gtmp.setStr("0 0 0");
		System.out.println("g1 = 1: "+g1);
		Mcl.hashAndMapToG1(g1, "123456789".getBytes());
		
		G2 g2=new G2();
		g2.setStr("0 0 ");
		System.out.println("g2 "+ g2);
		Mcl.hashAndMapToG2(g2, "123456789".getBytes());
		System.out.println("g1="+g1.toString(16));
		System.out.println("g2="+g2.toString(16));
	}
}
