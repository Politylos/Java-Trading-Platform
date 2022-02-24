import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;

/**
 * Trade class is used to store the important information about each trade within the database with the asset type, cost, org, amount all being saved here to allow it to interface with the client
 */
public class Trade implements Asset_inter {
    String AssetName;
    int TradeId;
    private int amount;
    private double price;
    private int type;
    private int org_id;
    private String org_name;
    private String AssetDes;
    private Date post_date;
    private int AssetId;
    private static final long serialVersionUID = -2856155523713386524L;

    public Trade(){}

    /**
     *
     * @param id int, trade id
     * @param AssetName string, name of the asset being traded
     * @param Amount int, ammount of the asset being traded
     * @param Price double, cost of each individual asset being traded
     * @param Type int, tpye of trade 1 for buy 2 for sell
     * @param org_name string, name of the org selling the asset
     * @param Org int, id of the org selling the asset
     * @param AssetDes string, description of the asset being sold
     * @param date date, data that the trade was posted
     * @param assetid int, id of the asset that is being traded
     */
    public Trade(int id,String AssetName, int Amount, double Price, int Type, String org_name,int Org,String AssetDes, Date date, int assetid){
        this.AssetId = assetid;
        this.post_date = date;
        this.AssetName = AssetName;
        this.TradeId = id;
        this.AssetDes = AssetDes;
        this.amount = Amount;
        this.price = Price;
        this.type = Type;
        this.org_id = Org;
        this.org_name = org_name;
    }
    public Date getDate(){
        return post_date;
    }

    /**
     * creates a corresponding buy or sell offer based off this trade
     * @param offer_org int, org that is making the corresponding offer
     * @param amount int, amount to buy from this trade
     * @return pack object, a packed sell or but command to send to the server
     */
    public pack place_offer(int offer_org, int amount){
        if (type == Trade_Type.SELL.type()){

            return new pack(Server_Code.BUY.getId(),new String[] {String.valueOf(offer_org), String.valueOf(this.AssetId), String.valueOf(amount), String.valueOf(this.price)});
        } else{

            return new pack(Server_Code.SELL.getId(),new String[] {String.valueOf(offer_org), String.valueOf(this.AssetId), String.valueOf(amount), String.valueOf(this.price)});
        }
    }

    public void update(){

    }

    /**
     * get the asset name for the trade
     * @return string asset name
     */
    public String getName(){
        return AssetName;
    }

    /**
     * gets the trade_type enum for the trade tpye of this class
     * @return trade_type enum either buy or sell, buy = 1 sell = 2
     */
    public Trade_Type getType(){
        if (type == 1){
            return Trade_Type.BUY;
        }
        return Trade_Type.SELL;
    }

    /**
     * get the amount of the asset being sold
     * @return in amount of asset
     */
    public int getAmount(){
        return amount;
    }

    /**
     * price for each asset being sold
     * @return double price of each asset in the trade order
     */
    public double getPrice(){
        return this.price;
    }

    @Override
    public String send() {
        return null;
    }

    @Override
    public int getId() {
        return this.TradeId;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(TradeId);
        out.writeUTF(AssetName);
        out.writeInt(amount);
        out.writeDouble(price);
        out.writeInt(type);
        out.writeInt(org_id);
        out.writeUTF(AssetDes);
        out.writeUTF(org_name);
        out.writeObject(post_date);
        out.writeInt(AssetId);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        TradeId=in.readInt();
        AssetName =in.readUTF();
        amount=in.readInt();
        price=in.readDouble();
        type=in.readInt();
        org_id = in.readInt();
        AssetDes = in.readUTF();
        org_name = in.readUTF();
        post_date = (Date) in.readObject();
        AssetId = in.readInt();
    }

    /**
     * get the org id of the organisation that posted the trade offer
     * @return int org_id
     */
    public int getOrg() {
        return this.org_id;
    }

    /**
     * gets the name of the organisation that posted the trade offer
     * @return string org name
     */
    public String getOrgName() {
        return org_name;
    }
}
