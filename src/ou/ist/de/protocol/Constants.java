package ou.ist.de.protocol;

public class Constants {
	public static int InetAddressLength = 4;
	public static int PORT = 30000;
	public static int RCVBUFFER = 20000;
	public static String BROAD_CAST_ADDR = "10.255.255.255";
	public static byte REQ = 0;
	public static byte REP = 1;
	public static byte ERR = 2;
	public static byte DATA = 3;
	public static long timer;
	public static boolean wait;
	public static int FSIZE = 1000;
	public static int REPEAT=1;
	public static int INIT_SEQ=1;
	public static int[] network = new int[] { 10, 0, 0 };
	public static long TIMEOUT = 10000000;
	
	public static String DEFAULT_RSA_SIG_BIT_LENGTH="1024";
	public static String DEFAULT_RSA_KEY_INDEX="10";
	public static String DEFAULT_INTERVAL_MILISEC="1000";
	public static String DEFAULT_REPEAT_TIMES="1";
	public static String DEFAULT_INITIAL_SEQUENCE_NUM="1";
	public static String DEFAULT_PORT_NUM="30000";
	public static String DEFAULT_FRAGMENTATION_SIZE="1000";
	
	public static String ARG_SIG_BIT_LENGTH = "-sigbitlength";
	public static String ARG_KEY_INDEX="-keyindex";
	public static String ARG_PORT_NUM = "-port";
	public static String ARG_FRAGMENTATION_SIZE = "-frag";
	public static String ARG_DESTINATION = "-dest";
	public static String ARG_PROTO_DSR="DSR";
	public static String ARG_PROTO_ISDSR="ISDSR";
	public static String ARG_PROTO_RSA="RSA";
	public static String ARG_PROTO_SRDP="SRDP";
	public static String ARG_REPEAT="-repeat";
	public static String ARG_INTERVAL="-interval";
	public static String ARG_INITIAL_SEQUENCE_NUM="-seq";
}
