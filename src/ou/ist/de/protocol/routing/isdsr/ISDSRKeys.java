package ou.ist.de.protocol.routing.isdsr;

public class ISDSRKeys{

    protected ISDSRKey mpk;
    protected ISDSRKey msk;
    protected ISDSRKey isk;

    public ISDSRKeys(){
    }
    public ISDSRKeys(int mpkMembers,int mskMembers,int iskMembers){
        this.mpk=new ISDSRKey(mpkMembers);
        this.msk=new ISDSRKey(mskMembers);
        this.isk=new ISDSRKey(iskMembers);
    }
    public void setMPK(int index,byte[] key){
        this.mpk.set(index, key);
    }
    public byte[] getMPK(int index){
        return this.mpk.get(index);
    }
    public void setMSK(int index,byte[] key){
        this.msk.set(index, key);
    }
    public byte[] getMSK(int index){
        return this.msk.get(index);
    }
    public void setISK(int index,byte[] key){
        this.isk.set(index, key);
    }
    public byte[] getISK(int index){
        return this.isk.get(index);
    }

}