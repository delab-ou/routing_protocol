package ou.ist.de.protocol.routing.isdsr_re;


public class MPK extends ISDSRKey{
	
	public MPK() {
		super(3);
	}
	
	
	@Override
	protected void setParameterNames() {
		// TODO Auto-generated method stub
		names=new String[] {"mpk.g1","mpk.g2","mpk.g3"};
	}


	public byte[] getMPK1() {
		return super.get(0);
	}


	public void setMPK1(byte[] mpk1) {
		super.set(0, mpk1);
	}


	public byte[] getMPK2() {
		return super.get(1);
	}


	public void setMPK2(byte[] mpk2) {
		super.set(1, mpk2);
	}


	public byte[] getMPK3() {
		return super.get(2);
	}


	public void setMPK3(byte[] mpk3) {
		super.set(2, mpk3);
	}
}
