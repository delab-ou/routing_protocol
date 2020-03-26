package ou.ist.de.protocol.routing.isdsr;

import java.io.Serializable;
import java.math.BigInteger;
import java.nio.ByteBuffer;

public class ISDSRKey implements Serializable{
	
	
	private static final long serialVersionUID = -557954243350900825L;
	protected byte[][] keys;
	protected String[] names;
	
	protected ISDSRKey() {
		keys=null;
		
	}
	public ISDSRKey(int num) {
		keys=new byte[num][];
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
		for(int i=0;i<keys.length;i++) {
			ret+=((names==null)?"":names[i])+" = "+getAsString(i)+"\n";
		}
		return ret;
	}
	protected void setParameterNames(String[] names){
		this.names=names;
	}
	public int getNumberOfMembers(){
		return keys.length;
	}
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
