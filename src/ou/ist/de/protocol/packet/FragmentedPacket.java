package ou.ist.de.protocol.packet;

import java.nio.ByteBuffer;

import ou.ist.de.protocol.Constants;

public class FragmentedPacket extends BasePacket{
	protected int totalLength;
	protected int totalCount;
	protected int index;
	protected byte[] fragmented;
	
	public FragmentedPacket() {
		super();
		this.totalCount=0;
		this.totalLength=0;
		this.index=0;
		this.fragmented=null;
	}
	public FragmentedPacket(BasePacket p) {
		super(p);
		this.totalCount=0;
		this.totalLength=0;
		this.index=0;
		this.fragmented=null;
	}
	public FragmentedPacket(ByteBuffer bb) {
		super(bb);
		try {
			this.totalLength=bb.getInt();
			this.totalCount=bb.getInt();
			this.index=bb.getInt();
			int datalength=Constants.FSIZE;
			if(this.totalCount==this.index+1) {
				datalength=totalLength%Constants.FSIZE;
			}
			this.fragmented=new byte[datalength];
			bb.get(fragmented);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public FragmentedPacket(byte[] ba) {
		this(ByteBuffer.wrap(ba));
	}
	@Override
	public String toString() {
		String ret=super.toString();
		ret+=" total length:"+this.totalLength;
		ret+=" total count:"+this.totalCount;
		ret+=" index:"+this.index;
		if(fragmented ==null) {
			ret+=" opt:null\n";
		}
		else {
			ret+="opt:[";
			for(byte b:fragmented) {
				ret+=b+" ";
			}
			ret+="]\n";
		}
		return ret;
	}
	public void put(ByteBuffer bb) {
		super.put(bb);
		bb.putInt(this.totalLength);
		bb.putInt(this.totalCount);
		bb.putInt(this.index);
		bb.put(this.fragmented);
	}
	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return super.getSize()+Integer.BYTES*3+this.fragmented.length;
	}
	@Override
	public byte[] getPacketAsByteArray() {
		// TODO Auto-generated method stub
		ByteBuffer bb = ByteBuffer.allocate(this.getSize());		this.put(bb);
		return bb.array();
	}
	
	
}
