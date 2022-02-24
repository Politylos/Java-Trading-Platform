import java.io.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;

public class User_Organisation implements Externalizable {
    private static final long serialVersionUID = -2854116343315985524L;
    private int Orgid;
    private String OrgName;
    private double OrgCredits;
    private Org_Asset[] assets;
    private int[] current_trades;


    public User_Organisation(){}
    public int getID(){
        return this.Orgid;
    }
    public String getOrgName(){return this.OrgName;}
        //super(new organisation());
    public User_Organisation(double credits, String name, int id) throws SQLException, IOException, ClassNotFoundException {
        this.OrgCredits=credits;
        this.OrgName=name;
        this.Orgid = id;
        DB dbconn = new DB();
        dbconn.query(String.format("SELECT * FROM assets, organisationassets WHERE assets.Asset_id = organisationassets.Asset_id AND organisationassets.Organisation_id = %d",this.Orgid));
        dbconn.printresult();
        assets = new Org_Asset[dbconn.count()];
        System.out.println(dbconn.mapresult());
        dbconn.close();
    }
    public Org_Asset[] getAssets(){
        return assets;
    }
    public boolean checkAsset(String Name, int amount){
        System.out.println(Name);
        for (Org_Asset asset : assets){
            System.out.println(asset.name());
            if ((asset.getAmount() >= amount) & (asset.name().equals(Name))){
                return true;
            }
        }
        return false;
    }
    /**
     * creates the organisation class, through fetching the user's
     * corresponding organisation stored in the servers database
     *
     * @param org_id int representing the user's id
     */
    public User_Organisation(int org_id) throws SQLException, IOException, ClassNotFoundException {
        DB DBconn = new DB();
        DBconn.query(String.format("SELECT * FROM organisations WHERE Organisation_id = %d",org_id));
        System.out.println(String.format("SELECT * FROM organisations WHERE Organisation_id = %d",org_id));
        if (!DBconn.isempty()) {
            DBconn.FirstResult();
            this.set(Integer.parseInt(DBconn.getColumn("Organisation_id")),DBconn.getColumn("Organisation_Name"), Double.parseDouble(DBconn.getColumn("Credits")));
            DB dbconn = new DB();
            dbconn.query(String.format("SELECT * FROM assets, organisationassets WHERE assets.Asset_id = organisationassets.Asset_id AND organisationassets.Organisation_id = %d",this.Orgid));
            assets = new Org_Asset[dbconn.count()];
            int pos = 0;
            while (dbconn.next()) {
                HashMap<String,String> row = dbconn.Pop();
                assets[pos] = new Org_Asset(Integer.parseInt(row.get("Asset_id")), row.get("Name"),Integer.parseInt(row.get("Amount")),row.get("Description"));
                pos++;
            }
            HashMap<String,String> row = dbconn.Pop();
            if (row != null) {
                assets[pos] = new Org_Asset(Integer.parseInt(row.get("Asset_id")), row.get("Name"), Integer.parseInt(row.get("Amount")), row.get("Description"));
            }
            String sql = String.format("SELECT * FROM Trades WHERE Organisation_id = %d", org_id);
            dbconn.query(sql);
            current_trades = new int[dbconn.count()];
            int i = 0;
            while (dbconn.next()){
                row = dbconn.Pop();
                current_trades[i] = Integer.parseInt(row.get("Trade_id"));
            }
            row = dbconn.Pop();
            if (row != null) {
                current_trades[i] = Integer.parseInt(row.get("Trade_id"));
            }
            dbconn.close();
        }
        DBconn.Close();

    }
    public void set(int orgid, String OrgName, double credits)
    {
        this.Orgid = orgid;
        this.OrgName = OrgName;
        this.OrgCredits = credits;
    }
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeDouble(this.OrgCredits);
        out.writeUTF(this.OrgName);
        out.writeInt(this.Orgid);
        out.writeObject(this.assets);


    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.OrgCredits = in.readDouble();
        this.OrgName = in.readUTF();
        this.Orgid = in.readInt();
        this.assets = (Org_Asset[]) in.readObject();

    }
    public double getCredits(){
        return OrgCredits;
    }
    public boolean checkCredits(double price) {
        if (OrgCredits >= price){
            return true;
        }
        return false;
    }

    public String getTrades() {
        StringBuilder trades = new StringBuilder();
    if (current_trades != null) {
        for (int i = 0; i < current_trades.length; i++) {
            trades.append(String.valueOf(i));
            if (i != current_trades.length - 1) {
                trades.append("|");
            }
        }
        return trades.toString();
    }
    return null;
    }
}
