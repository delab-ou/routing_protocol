package ou.ist.de.protocol;

import java.net.InetAddress;
import java.util.HashMap;

import ou.ist.de.protocol.node.Node;
import ou.ist.de.protocol.routing.dsr.DSR;

public class Main {

	protected HashMap<String,String> params;
	
	public Main() {
		params=new HashMap<String,String>();
	}
	public void setArgs(String[] args) {
		for(int i=0;i<args.length;i++) {
			String[] t=args[i].split(":");
			params.put(t[0], t[1]);
		}
	}
	public String getParameter(String key) {
		return params.get(key);
	}
	public void runDSR() {
		String port=null;
		if(params.containsKey("-port")) {
			port=this.getParameter("-port");
		}
		Constants.PORT=Integer.valueOf(port);
		
		Node node=new Node(params);
		DSR dsr=new DSR();
		node.setRoutingProtocol(dsr);
		node.start();
		if(params.containsKey("-dest")) {
		try {
			node.startRouteEstablishment(InetAddress.getByName(params.get("-dest")));
		}catch(Exception e) {
			e.printStackTrace();
		}
		}
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Main m=new Main();
		
		//java ou.ist.de.protocol.Main -protocol:DSR -port:000 -dest:10.0.0.0 -frag:1000
		if(args.length<2) {
			System.out.println("usage java ou.ist.de.protocol.Main -protocol:{DSR|ISDSR|SRDP|RSA} -port:portnum -frag:size of fragmentation -dest:destination ip");
			System.exit(0);
		}
		m.setArgs(args);
		String protocol=m.getParameter("-protocol");
		if(protocol.equalsIgnoreCase("DSR")) {
			m.runDSR();
		}
		
	}

}
