package ou.ist.de.protocol.routing.isdsr_re;

import java.security.MessageDigest;
import java.util.HashMap;

import ou.ist.de.protocol.routing.dsr.RouteInfo;

public abstract class SignatureOperation {

	protected MPK mpk;
	protected MSK msk;
	protected ISK isk;
	protected String paramFile;

	
	protected SignatureOperation() {
		
	}
	public SignatureOperation(HashMap<String, String> params, String uid) {
		mpk = new MPK();
		msk = new MSK();
		isk = new ISK();
		String keyfile = "a.keys";
		String paramFile = "a.properties";
		if (params.containsKey("-keyfile")) {
			keyfile = params.get("-keyfile");
		}
		if (params.containsKey("-paramfile")) {
			this.paramFile = params.get("-paramfile");
		}

		this.initialize();
		this.setup(keyfile, "5");
		this.keyDerivation(uid);
	}

	public abstract void initialize();

	public abstract void setup(String keyfile, String indexstr);

	public abstract void keyDerivation(String uid);

	public abstract byte[] sign(RouteInfo ri, Signatures sigs);

	public abstract boolean verify(RouteInfo ri, Signatures sigs);

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
}
