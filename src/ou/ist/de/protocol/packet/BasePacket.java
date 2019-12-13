package ou.ist.de.protocol.packet;

import java.net.InetAddress;
import java.nio.ByteBuffer;

public class BasePacket {
	protected byte type;
	protected int seq;
	protected InetAddress src;
	protected InetAddress dest;
	protected InetAddress sndr;
	protected InetAddress next;
	protected int hops;

	public BasePacket() {
		type = 0;
		seq = 0;
		src = null;
		dest = null;
		sndr = null;
		next = null;
		hops = 0;
	}

	public BasePacket(byte type, int seq, InetAddress src, InetAddress dest, InetAddress sndr, InetAddress next,
			int hops) {
		this.type = type;
		this.seq = seq;
		this.src = src;
		this.dest = dest;
		this.sndr = sndr;
		this.next = next;
		this.hops = hops;
	}

	public BasePacket(BasePacket bp) {
		this(bp.type, bp.seq, bp.src, bp.dest, bp.sndr, bp.next, bp.hops);
	}
	public BasePacket(ByteBuffer bb) {
		try {
			type = bb.get();
			seq = bb.getInt();
			byte[] bsrc = new byte[4];
			byte[] bdest = new byte[4];
			byte[] bsndr = new byte[4];
			byte[] bnext = new byte[4];
			bb.get(bsrc);
			bb.get(bdest);
			bb.get(bsndr);
			bb.get(bnext);
			src = InetAddress.getByAddress(bsrc);
			dest = InetAddress.getByAddress(bdest);
			sndr = InetAddress.getByAddress(bsndr);
			next = InetAddress.getByAddress(bnext);
			hops = bb.getInt();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public BasePacket(byte[] ba) {
		this(ByteBuffer.wrap(ba));
	}
	
	public int getHeaderSize() {
		if (src == null || dest == null || sndr == null || next == null) {
			return 0;
		}
		int ret = 0;
		ret = src.getAddress().length + dest.getAddress().length + sndr.getAddress().length + next.getAddress().length;
		ret += Byte.BYTES;// type 1
		ret += Integer.BYTES;// seq 4
		ret += Integer.BYTES;// hops 4
		return ret;
	}
	public int getSize() {
		if (src == null || dest == null || sndr == null || next == null) {
			return 0;
		}
		int ret = 0;
		ret = src.getAddress().length + dest.getAddress().length + sndr.getAddress().length + next.getAddress().length;
		ret += Byte.BYTES;// type 1
		ret += Integer.BYTES;// seq 4
		ret += Integer.BYTES;// hops 4
		return ret;
	}

	public void put(ByteBuffer bb) {
		bb.put(type);
		bb.putInt(seq);
		bb.put(src.getAddress());
		bb.put(dest.getAddress());
		bb.put(sndr.getAddress());
		bb.put(next.getAddress());
		bb.putInt(hops);
	}

	public byte[] toBytes() {

		ByteBuffer bb = ByteBuffer.allocate(this.getSize());
		this.put(bb);
		return bb.array();
	}
	public void copy(BasePacket bp) {
		this.type=bp.type;
		this.seq=bp.seq;
		this.src=bp.src;
		this.dest=bp.dest;
		this.sndr=bp.sndr;
		this.next=bp.next;
		this.hops=bp.hops;
	}
	public String baseInformation() {
		String ret = "type:" + type;
		ret += " src:" + src.toString();
		ret += " dest:" + dest.toString();
		ret += " next:" + ((next == null) ? "null" : next.toString());
		ret += " sndr:" + ((sndr == null) ? "null" : sndr.toString());
		ret += " hops:" + hops + " seq:" + seq + "\n";
		return ret;
	}
	public String toString() {
		return baseInformation();
	}
	public boolean equals(BasePacket bp) {
		if(this.type!=bp.type) {
			return false;
		}
		if(this.seq!=bp.seq) {
			return false;
		}
		if(this.hops!=bp.hops) {
			return false;
		}
		if(!this.src.equals(bp.src)) {
			return false;
		}
		if(!this.dest.equals(bp.dest)) {
			return false;
		}
		if(!this.next.equals(bp.next)) {
			return false;
		}
		if(!this.sndr.equals(bp.sndr)) {
			return false;
		}
		return true;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public InetAddress getSrc() {
		return src;
	}

	public void setSrc(InetAddress src) {
		this.src = src;
	}

	public InetAddress getDest() {
		return dest;
	}

	public void setDest(InetAddress dest) {
		this.dest = dest;
	}

	public InetAddress getSndr() {
		return sndr;
	}

	public void setSndr(InetAddress sndr) {
		this.sndr = sndr;
	}

	public InetAddress getNext() {
		return next;
	}

	public void setNext(InetAddress next) {
		this.next = next;
	}

	public int getHops() {
		return hops;
	}

	public void setHops(int hops) {
		this.hops = hops;
	}


}
