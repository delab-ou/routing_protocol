package ou.ist.de.protocol.routing.dsr;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import ou.ist.de.protocol.Constants;

public class RouteInfo {

	protected ArrayList<InetAddress> aladdr;

	public RouteInfo() {
		aladdr = new ArrayList<InetAddress>();
	}

	public RouteInfo(byte[] ba, int count) {
		this();
		ByteBuffer bb = ByteBuffer.wrap(ba);
		byte[] tmp = new byte[Constants.InetAddressLength];
		try {
			for (int i = 0; i < count; i++) {
				bb.get(tmp);
				aladdr.add(InetAddress.getByAddress(tmp));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addNode(InetAddress addr) {
		aladdr.add(addr);
	}

	public byte[] toBytes() {
		if (aladdr.isEmpty()) {
			return null;
		}
		int length = aladdr.size() * Constants.InetAddressLength;
		ByteBuffer bb = ByteBuffer.allocate(length);
		for (int i = 0; i < aladdr.size(); i++) {
			bb.put(aladdr.get(i).getAddress());
		}
		return bb.array();
	}

	public void setBytes(byte[] addrs) {
		InetAddress tmp = null;
		byte[] addr = new byte[Constants.InetAddressLength];
		if (aladdr == null) {
			aladdr = new ArrayList<InetAddress>();
		} else {
			aladdr.clear();
		}
		for (int i = 0; i < addrs.length; i++) {
			addr[i % Constants.InetAddressLength] = addrs[i];
			if ((i % Constants.InetAddressLength) == 3) {
				try {
					tmp = InetAddress.getByAddress(addr);
					aladdr.add(tmp);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	public boolean isContained(InetAddress addr) {
		
		for(InetAddress ia:this.aladdr) {
			if(ia.equals(addr)) {
				return true;
			}
		}
		return false;
	}
	public InetAddress get(int index) {
		if(index<this.aladdr.size()) {
			return this.aladdr.get(index);
		}
		return null;
	}
	public int size() {
		if(this.aladdr!=null) {
			return this.aladdr.size();
		}
		return -1;
	}
	public String[] getAddrArray() {
		if (aladdr.isEmpty()) {
			return null;
		}
		String[] ret = new String[aladdr.size()];
		for (int i = 0; i < aladdr.size(); i++) {
			ret[i] = aladdr.get(i).toString();
		}
		return ret;
	}

	public String getAddrSequence() {
		String ret = "";
		if (aladdr.isEmpty()) {
			return ret;
		}
		for (int i = 0; i < aladdr.size(); i++) {
			ret += aladdr.get(i).toString();
		}
		return ret;
	}

	public String toString() {
		String ret = "";

		if (aladdr.isEmpty()) {
			return null;
		}
		for (InetAddress ina : aladdr) {
			ret += ina.toString() + ":";
		}
		return ret;
	}
}
