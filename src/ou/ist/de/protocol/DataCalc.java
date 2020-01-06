package ou.ist.de.protocol;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class DataCalc {

	protected ArrayList<ArrayList<Row>> alr;

	public DataCalc() {
		alr = new ArrayList<ArrayList<Row>>();
	}

	public void read(String prefix, int times) {
		alr.clear();
		System.out.println("file="+prefix);
		for (int i = 0; i < times; i++) {
			this.read(prefix + "-" + i + ".csv");
		}
	}

	public void read(String file) {
		Row r = null;
		String s = null;
		ArrayList<Row> al=new ArrayList<Row>();
		try {
			// "SRC,DEST,seq,hops,time,rcvs,received\n";
			BufferedReader br = new BufferedReader(new FileReader(new File(file)));
			while ((s = br.readLine()) != null) {
				if(s.startsWith("SRC")) {
					continue;
				}
				r=new Row(s);
				al.add(r);
			}
			alr.add(al);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void calculate() {
		int reach=0;
		int t=0;
		int count=0;
		ArrayList<Row> al=null;
		for(int i=0;i<alr.size();i++) {
			al=alr.get(i);
			if(al.size()==0) {
				continue;
			}
			reach+=al.get(al.size()-1).rcv;
			for(Row r:alr.get(i)) {
				t+=r.time;
				count++;
			}
		}
		System.out.println("reach="+((double)reach)/alr.size()+" time="+((double)t)/count);
	}
	public static void main(String args[]) {
		DataCalc dc=new DataCalc();
		dc.read("RSA10x10-1024-i1-c1-f100000", 10);
		dc.calculate();
		//dc.read("SRDP10x10-1024-i1", 10);
		//dc.calculate();
		//dc.read("ISDSR10x10-1024-i1", 10);
		//dc.calculate();
	}

	public class Row {
		// "SRC,DEST,seq,hops,time,rcvs,received\n";
		String src,dest;
		int seq,hops,time,rcvs,rcv;
		
		public Row(String s) {
			String ss[]=s.split(",");
			src = ss[0];
			dest = ss[1];
			seq = Integer.valueOf(ss[2]);
			hops = Integer.valueOf(ss[3]);
			time = Integer.valueOf(ss[4]);
			rcvs = Integer.valueOf(ss[5]);
			rcv = Integer.valueOf(ss[6]);
		}
	}
}
