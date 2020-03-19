package ou.ist.de.protocol;

import java.net.InetAddress;
import java.util.HashMap;

import ou.ist.de.protocol.node.ExpNode;
import ou.ist.de.protocol.node.Node;
import ou.ist.de.protocol.routing.RoutingProtocol;
import ou.ist.de.protocol.routing.dsr.DSR;
import ou.ist.de.protocol.routing.isdsr.ISDSR;
import ou.ist.de.protocol.routing.rsabase.RSABaseSecureRouting;
import ou.ist.de.protocol.routing.rsabaseindividualkey.RSABaseIndividualSecureRouting;
import ou.ist.de.protocol.routing.srdp.SRDP;

public class Main {

	protected HashMap<String, String> params;
	protected RoutingProtocol rp;

	public Main() {
		params = new HashMap<String, String>();
	}

	public void setArgs(String[] args) {
		for (int i = 0; i < args.length; i++) {
			String[] t = args[i].split(":");
			params.put(t[0], t[1]);
		}
	}

	public String getParameter(String key) {
		return params.get(key);
	}

	public void initialize() {
		
		Constants.PORT = Integer.valueOf(this.checkParameters(params, Constants.ARG_PORT_NUM, Constants.DEFAULT_PORT_NUM));
		Constants.INIT_SEQ = Integer.valueOf(this.checkParameters(params, Constants.ARG_INITIAL_SEQUENCE_NUM, Constants.DEFAULT_INITIAL_SEQUENCE_NUM));
		Constants.REPEAT= Integer.valueOf(this.checkParameters(params, Constants.ARG_REPEAT, Constants.DEFAULT_REPEAT_TIMES));
		Constants.FSIZE = Integer.valueOf(this.checkParameters(params, Constants.ARG_FRAGMENTATION_SIZE, Constants.DEFAULT_FRAGMENTATION_SIZE));
		Constants.SignatureBitLength = Integer.valueOf(this.checkParameters(params, Constants.ARG_SIG_BIT_LENGTH, Constants.DEFAULT_RSA_SIG_BIT_LENGTH));
		
		
		String protocol = this.getParameter("-protocol");
		System.out.println("Protocol: " + protocol);
		rp = this.setRoutingProtocol(protocol);
		if (rp == null) {
			System.out.println("No protocol was set or the protocol name was wrong.");
			System.exit(0);
		}
	}
	protected String checkParameters(HashMap<String,String> params,String key,String value) {
		if (!params.containsKey(key)) {
			params.put(key, value);
			return value;
		}
		else {
			return params.get(key);
		}
	}
	public RoutingProtocol setRoutingProtocol(String name) {
		System.out.println("target protocol is " + name);
		if (name.equalsIgnoreCase("DSR")) {
			return new DSR(params);
		}
		if (name.equalsIgnoreCase("RSA")) {
			return new RSABaseSecureRouting(params);
		}
		if (name.equalsIgnoreCase("ISDSR")) {
			return new ISDSR(params);
		}
		if (name.equalsIgnoreCase("SRDP")) {
			return new SRDP(params);
		}
		if (name.equalsIgnoreCase("RSAIndividual")) {
			return new RSABaseIndividualSecureRouting(params);
		}
		return null;
	}

	public void run() {

		Node node = new Node(params);
		node.setRoutingProtocol(rp);
		node.start();
		if (params.containsKey("-dest")) {
			try {
				node.startRouteEstablishment(InetAddress.getByName(params.get("-dest")));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void runRepeat() {
		ExpNode.interval_milisec=1000;
		ExpNode node = new ExpNode(params);
		node.setRepeatTimes(Integer.valueOf(params.get("-repeat")));
		node.setRoutingProtocol(rp);
		node.start();
		
		if (params.containsKey("-dest")) {
			try {
				node.startRouteEstablishment(InetAddress.getByName(params.get("-dest")));
				Thread.sleep(60000);
				node.writeResults();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Main m = new Main();

		// java ou.ist.de.protocol.Main -protocol:DSR -port:000 -dest:10.0.0.0
		// -frag:1000
		if (args.length < 2) {
			System.out.println(
					"usage java ou.ist.de.protocol.Main -protocol:{DSR|ISDSR|SRDP|RSA} -port:portnum -frag:size of fragmentation -dest:destination ip");
			System.exit(0);
		}
		m.setArgs(args);
		m.initialize();
		if (m.params.containsKey("-repeat")) {
			m.runRepeat();
		} else {
			m.run();
		}

	}

}
