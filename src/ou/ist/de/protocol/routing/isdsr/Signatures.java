package ou.ist.de.protocol.routing.isdsr;

import ou.ist.de.protocol.routing.isdsr.ISDSRKey;

public class Signatures extends ISDSRKey {
	
	/**
	 *
	 */
	private static final long serialVersionUID = 5188857567105531575L;

	public Signatures(int sigMembers) {
		super(sigMembers);
		super.setParameterNames(new String[] {"sig1","sig2","sig3","sig4"});
	}
	public void clear(){
		for(int i=0;i<keys.length;i++){
			keys[i]=null;
		}
	}
	public int totalSigLength(){
		int ret=0;
		for(byte[] sig:this.keys){
			ret+=sig.length;
		}
		return ret;
	}
}
