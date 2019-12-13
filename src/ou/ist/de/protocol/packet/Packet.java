package ou.ist.de.protocol.packet;

import java.net.InetAddress;
import java.nio.ByteBuffer;

public class Packet extends BasePacket{
	protected byte[] option;
	public Packet() {
		super();
		option = null;
	}
	
	public Packet(byte type, int seq, InetAddress src, InetAddress dest, InetAddress sndr, InetAddress next, int hops, byte[] option) {
		super(type,seq,src,dest,sndr,next,hops);
		this.option=option;
	}
	public Packet(Packet p) {
		super(p);
		this.option=p.option;
	}
	public Packet(ByteBuffer bb) {
		super(bb);
		try {
			this.option=new byte[bb.capacity()-bb.position()];
			bb.get(option);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public Packet(byte[] ba) {
		this(ByteBuffer.wrap(ba));
	}
	@Override
	public String toString() {
		String ret = super.toString();
		
		if(option ==null) {
			ret+=" opt:null\n";
		}
		else {
			ret+="opt:[";
			for(byte b:option) {
				ret+=b+" ";
			}
			ret+="]\n";
		}
		return ret;
	}
	@Override
	public int getSize() {
		int ret=super.getSize();
		return ret+ ((option == null) ? 1 : option.length);
	}
	@Override
	public void put(ByteBuffer bb) {
		super.put(bb);
		if (option == null) {
			option = new byte[] { 0 };
		}
		bb.put(option);
	}
	@Override
	public byte[] toBytes() {
		
		ByteBuffer bb = ByteBuffer.allocate(this.getSize());
		this.put(bb);
		return bb.array();
	}
	
	public void copyHeader(Packet p) {
		super.copy(p);
	}

	public byte[] getOption() {
		return option;
	}

	public void setOption(byte[] option) {
		this.option = option;
	}
	
	
}
