package ou.ist.de.protocol.routing.isdsr;


import java.io.Serializable;

public class MasterKey implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 777499020697297341L;
	public String paramFile;
	public ISDSRKey keys[];

	public MasterKey(){
		
	}
	public MasterKey(int num){
		keys=new ISDSRKey[num];
	}
}
