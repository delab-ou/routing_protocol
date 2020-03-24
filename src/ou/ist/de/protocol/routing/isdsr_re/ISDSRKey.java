package ou.ist.de.protocol.routing.isdsr_re;

import java.math.BigInteger;
import java.nio.ByteBuffer;

public abstract class ISDSRKey {
	
	public enum type{MPK,MSK,ISK};
	protected byte[][] keys;
	
	protected String[] names;
	protected ISDSRKey() {
		keys=null;
		setParameterNames();
		
	}
	protected ISDSRKey(int num) {
		keys=new byte[num][];
		setParameterNames();
	}
	
	public byte[] get(int index) {
		return keys[index];
	}
	public void set(int index,byte[] b) {
		keys[index]=b;
	}
	
	public String getAsString(int index) {
		return new BigInteger(keys[index]).toString();
	}
	public String toString() {
		String ret="";
		for(int i=0;i<names.length;i++) {
			ret+=names[i]+" = "+getAsString(i)+"\n";
		}
		return ret;
	}
	protected abstract void setParameterNames();
	
	public byte[] toBytes() {
		int length=0;
		for(int i=0;i<keys.length;i++) {
			length+=keys[i].length;
		}
		ByteBuffer bb=ByteBuffer.allocate(length+Integer.BYTES*keys.length);
		for(int i=0;i<keys.length;i++) {
			bb.putInt(keys[i].length);
			bb.put(keys[i]);
		}
		return bb.array();
	}
	public void fromBytes(byte[] data, int offset) {
		ByteBuffer bb=ByteBuffer.wrap(data);
		bb.position(offset);
		for(int i=0;i<keys.length;i++) {
			int l=bb.getInt();
			keys[i]=new byte[l];
			bb.get(keys[i]);
		}
	}
}
