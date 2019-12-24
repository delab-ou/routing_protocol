package ou.ist.de.protocol.node;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

import ou.ist.de.protocol.packet.Packet;

public class ExpNode extends Node {

	public static int interval_milisec;
	protected int repeat;
	protected HashMap<String, ArrayList<PacketData>> alpd;
	protected ArrayList<PacketData> alsnd;
	protected ArrayList<PacketData> alrcv;
	protected InetAddress dest;

	public ExpNode(HashMap<String, String> params) {
		super();
		this.params = params;
		alpd = new HashMap<String, ArrayList<PacketData>>();
	}

	public void setRepeatTimes(int repeat) {
		this.repeat = repeat;
	}

	public void startRouteEstablishment(InetAddress dest) {
		Packet p = null;
		String key = null;
		this.dest = dest;
		ArrayList<PacketData> al = null;
		for (int i = 0; i < repeat; i++) {
			p = this.rp.startRouteEstablishment(this.dest);
			key = "src:" + p.getSrc().toString() + ";dest:" + p.getDest().toString() + ";seq:" + p.getSeq();
			if (!alpd.containsKey(key)) {
				alpd.put(key, new ArrayList<PacketData>());
			}
			al = alpd.get(key);
			al.add(new PacketData(p.getType(), p.getSeq(), System.currentTimeMillis(), p.getHops()));
			System.out.println(
					"sent to " + p.getDest().toString() + " from " + p.getSrc().toString() + " seq=" + p.getSeq());
			try {
				Thread.sleep(interval_milisec);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void routeEstablished(Packet p) {
		System.out.println("route is established");
		String key = "src:" + p.getDest().toString() + ";dest:" + p.getSrc().toString() + ";seq:" + p.getSeq();
		System.out.println(key);
		if (alpd.containsKey(key)) {
			alpd.get(key).add(new PacketData(p.getType(), p.getSeq(), System.currentTimeMillis(), p.getHops()));
		}
	}

	public void writeResults() {

		String key = null;

		for (int i = 1; i <= repeat; i++) {
			key = "src:" + this.addr.toString() + ";dest:" + this.dest.toString() + ";seq:" + i;

		}
	}

	public class PacketData {
		protected int type;
		protected int seq;
		protected long time;
		protected int hops;

		public PacketData(int type, int s, long t, int hops) {
			this.type = type;
			this.seq = s;
			this.time = t;
			this.hops = hops;
		}
	}
}
