package ou.ist.de.protocol.routing.isdsr_re;


public class ISK extends ISDSRKey{

	
	public ISK() {
		super(2);
	}
	
	@Override
	protected void setParameterNames() {
		// TODO Auto-generated method stub
		this.names=new String[] {"isk.sk1","isk.sk2"};
	}

	public byte[] getISK1() {
		return super.get(0);
	}
	public void setISK1(byte[] isk1) {
		super.set(0, isk1);
	}


	public byte[] getISK2() {
		return super.get(1);
	}


	public void setISK2(byte[] isk2) {
		super.set(1, isk2);
	}
}
