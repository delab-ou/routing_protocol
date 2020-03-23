package ou.ist.de.protocol.routing.isdsr;

public class MSK extends ISDSRKey{
	
	
	public MSK() {
		super(2);
	}
	
	@Override
	protected void setParameterNames() {
		// TODO Auto-generated method stub
		names=new String[] {"msk.a1","msk.a2"};
	}
	public byte[] getMSK1() {
		return super.get(0);
	}
	public void setMSK1(byte[] msk1) {
		super.set(0, msk1);
	}
	public byte[] getMSK2() {
		return super.get(1);
	}
	public void setMSK2(byte[] msk2) {
		super.set(1, msk2);
	}
}
