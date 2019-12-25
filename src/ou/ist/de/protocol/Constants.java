package ou.ist.de.protocol;

public class Constants {
	public static int InetAddressLength=4;
	public static int PORT=30000;
	public static int RCVBUFFER=20000;
	public static String BROAD_CAST_ADDR="10.255.255.255";
	public static byte REQ=0;
	public static byte REP=1;
	public static byte ERR=2;
	public static byte DATA=3;
	public static long timer;
	public static boolean wait;
	public static int FSIZE=500;
	public static int[] network=new int[] {10,0,0};
	public static int REPEAT=1;
	public static long TIMEOUT=10000;
}
