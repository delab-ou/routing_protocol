package ou.ist.de.protocol.routing.isdsr_re;


public class Signatures extends ISDSRKey{
	
	protected Signatures() {
		super(3);
	}
	public void setSIG1(byte[] sig1) {
		super.set(0, sig1);
	}
	public byte[] getSIG1() {
		return super.get(0);
	}
	public void setSIG2(byte[] sig2) {
		super.set(1, sig2);
	}
	public byte[] getSIG2() {
		return super.get(1);
	}
	public void setSIG3(byte[] sig3) {
		super.set(2, sig3);
	}
	public byte[] getSIG3() {
		return super.get(2);
	}
	@Override
	protected void setParameterNames() {
		// TODO Auto-generated method stub
		names=new String[] {"sig1","sig2","sig3"};
	}

}
