package ou.ist.de.protocol.routing.secure;

import java.nio.ByteBuffer;
import java.util.HashMap;

import ou.ist.de.protocol.Constants;

public abstract class SigOperation {

		public SigOperation() {
			
		}
		public SigOperation(HashMap<String,String> params) {
			initialize(params);
		}
		
		public void fromOption(byte[] opt) {
			ByteBuffer bb=ByteBuffer.wrap(opt);
			int num=bb.getInt();
			int offset=Integer.BYTES+num*Constants.InetAddressLength;
			bb.position(offset);
			byte[] tmp=new byte[(opt.length-offset)];
			bb.get(tmp);
			this.fromBytes(tmp);
		}
		public abstract byte[] toBytes();
		public abstract void fromBytes(byte[] b);
		public abstract void initialize(HashMap<String,String> params);
}
