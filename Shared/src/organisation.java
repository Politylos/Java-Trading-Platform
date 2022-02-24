import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

public class organisation implements Externalizable {
    private int Orgid;
    private String OrgName;
    private double OrgCredits;
    protected HashMap<String,Integer> assets;
    @Serial
    private static final long serialVersionUID = -2850956911315686524L;

    /**
     * creates the organisation class, through fetching the user's
     * corresponding organisation stored in the servers database
     */
    public organisation(){}

    public organisation(organisation organisation) {

    }
    public void set(int orgid, String OrgName, double credits)
    {
        this.Orgid = orgid;
        this.OrgName = OrgName;
        this.OrgCredits = credits;
    }
    public organisation(int orgid, String OrgName, double credits) {
        this.Orgid = orgid;
        this.OrgName = OrgName;
        this.OrgCredits = credits;
        System.out.println("ORG ID");
        System.out.println(Orgid);
    }
    /**
     * Returns all org names in DB as a TreeSet, to be used to populate the client side ComboBox
     * in the create new User GUI
     * @return all Org Names in the DB as a set
     * @throws SQLException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Set<String> getOrgNameList() throws SQLException, IOException, ClassNotFoundException {
        DB DBConn = new DB();
        Set<String> orgNames = new TreeSet<>();
        ResultSet resSet = null;
        DBConn.query("SELECT Organisation_Name FROM organisations");
        resSet = DBConn.result();
        while (resSet.next()) {
            orgNames.add(resSet.getString("Organisation_Name"));
        }
        System.out.println(orgNames);
        DBConn.close();
        return orgNames;
    }
    /**
     * retuens all the assets and amount of each asset
     * @return hashmap of String int representing asset name and asset amount
     */
    public HashMap<String,Integer> getassets(){

        return assets;
    }

    /**
     * returns a specified asset name and amount
     * @param asset, asset to fetch from hashmap
     * @return HashMap of String,Integer representing asset name and asset amount
     */
    public HashMap<String,Integer> getasset(String asset){
        HashMap<String, Integer> asset_r = new HashMap<String, Integer>();
        asset_r.put(asset,assets.get(asset));
        return asset_r;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(this.Orgid);
        out.writeUTF(this.OrgName);
        out.writeDouble(this.OrgCredits);
        out.writeObject(this.assets);

    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.Orgid = in.readInt();
        this.OrgName = in.readUTF();
        this.OrgCredits = in.readDouble();
        this.assets = (HashMap<String,Integer>) in.readObject();
    }

    public int getId() {
        return this.Orgid;
    }
}
