package ou.ist.de.protocol.node;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashMap;

import ou.ist.de.protocol.Constants;
import ou.ist.de.protocol.packet.Packet;
import ou.ist.de.protocol.routing.RoutingProtocol;

public class Node {

	protected String id;
	protected DatagramSocket ds;
	protected Receiver r;
	protected Sender s;
	protected InetAddress addr;
	protected InetAddress baddr;
	protected int port;
	protected RoutingProtocol rp;
	protected HashMap<String,String> params;

	public Node() {
		this(Constants.PORT,Constants.network);
	}
	public Node(HashMap<String,String> params) {
		this();
		this.params=params;
	}
	public Node(HashMap<String,String> params,int port, int[] IPprefix) {
		this(port,IPprefix);
		this.params=params;
		
	}
	public Node(int port, int[] IPprefix) {
		this.port = port;
		this.initializeAddress(IPprefix);
		this.initializeDatagramSocket();
		s = new Sender(ds);
		r = new Receiver(ds, rp);
	}

	public Node(int port, RoutingProtocol rp, int[] IPprefix) {
		this.port = port;
		this.initializeAddress(IPprefix);
		this.initializeDatagramSocket();
		this.s = new Sender(ds);
		this.rp = rp;
		rp.setNode(this);
		rp.setSender(s);
		this.r = new Receiver(ds, rp);
	}

	public void start() {
		r.setLoopTrue();
		new Thread(r).start();
	}

	public void stop() {
		r.setLoopFalse();
	}

	public InetAddress getAddress() {
		return addr;
	}
	public InetAddress getBroadcastAddress() {
		return baddr;
	}
	public void setRoutingProtocol(RoutingProtocol rp) {
		this.rp = rp;
		this.rp.setNode(this);
		this.rp.setSender(s);
		this.r.rp = this.rp;
		System.out.println("ip address is " + addr+" brd is "+baddr);
	}
	public void setLocalAddress(InetAddress addr) {
		this.addr=addr;
	}
	public void setBroadcastAddress(InetAddress baddr) {
		this.baddr=baddr;
	}
	public void startRouteEstablishment(InetAddress dest) {
		this.rp.startRouteEstablishment(dest);
	}
	protected void initializeDatagramSocket() {
		try {
			// dsR = new DatagramSocket(this.port);
			System.out.println("initialize datagram socket");
			// ds=new DatagramSocket(port,addr);
			ds = new DatagramSocket(port);
			ds.setBroadcast(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void initializeAddress(int[] prefix) {
		try {
			Enumeration<NetworkInterface> ifs = NetworkInterface.getNetworkInterfaces();
			if(ifs==null) {
				return;
			}
			else {
				while(ifs.hasMoreElements()) {
					NetworkInterface ni=ifs.nextElement();
					for(InterfaceAddress ia:ni.getInterfaceAddresses()) {
						InetAddress addr=ia.getAddress();
						byte[] b=addr.getAddress();
						boolean check=true;
						for(int i=0;i<prefix.length;i++) {
							check &= ((b[i]&0x00FF)==prefix[i]);
						}
						if(check) {
							this.addr=addr;							
							this.baddr=ia.getBroadcast();
							byte[] ba=this.baddr.getAddress();
							if((ba[0]==0)&&(ba[1]==0)&&(ba[2]==0)&&(ba[3]==0)) {
								ba[0]=(byte)addr.getAddress()[0];
								ba[1]=(byte)0xff;
								ba[2]=(byte)0xff;
								ba[3]=(byte)0xff;
								this.baddr=InetAddress.getByAddress(ba);
							}
							return;
						}
					}
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void send(Packet p) {
		this.s.send(p);
	}

}
