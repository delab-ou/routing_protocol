package ou.ist.de.protocol.routing.isdsr;

import java.security.MessageDigest;
import java.util.HashMap;

import ou.ist.de.protocol.routing.dsr.RouteInfo;

public abstract class SignatureOperation {

	protected ISDSRKeys keys;
	
	protected SignatureOperation() {
		
	}
	public SignatureOperation(HashMap<String, String> params, String uid) {
		this.initialize(params, uid);
		
	}

	public abstract void initialize(HashMap<String, String> params, String uid);

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
