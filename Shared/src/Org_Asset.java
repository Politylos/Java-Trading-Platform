import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class Org_Asset implements Asset_inter{
    private int amount;
    private int AssetId;
    private String AssetName;
    private String AssetDes;
    public Org_Asset(){}
    public Org_Asset(int AssetId, String AssetName, int amount,String AssetDes){
        this.AssetId = AssetId;
        this.AssetName = AssetName;
        this.amount = amount;
        this.AssetDes = AssetDes;
    }

    public void addAmount(int amount){
        this.amount += amount;
    }
    public int getAmount(){
        return this.amount;
    }

    public String name(){
        return AssetName;
    }
    @Override
    public String send() {
        return null;
    }

    @Override
    public int getId() {
        return this.AssetId;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(AssetId);
        out.writeInt(amount);
        out.writeUTF(AssetName);
        out.writeUTF(AssetDes);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        AssetId = in.readInt();
        amount = in.readInt();
        AssetName = in.readUTF();
        AssetDes = in.readUTF();
    }
}
