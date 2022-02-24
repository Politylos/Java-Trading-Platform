import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serial;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * This class stores a unquie asset from the database within a class with each variable corresponding to an important variable within the database regarding
 * that asset type
 */
public class Asset implements Asset_inter{
    @Serial
    private static final long serialVersionUID = -2852226688719086524L;
    String AssetName;
    int AssetId;
    String AssetDes;
    private double meanprice;
    private double minprice;
    private double maxprice;
    private HashMap<Date, Double> history = new HashMap<Date, Double>();
    public Asset(){ }
    public Asset(int Id, String Name,String AssetDes, HashMap<Date, Double> History) {
        AssetName = Name;
        AssetId = Id;
        this.AssetDes = AssetDes;
        this.history = History;
        calcAll();
    }

    /**
     *
     * @param Id int, asset id
     * @param Name String, asset Name
     * @param AssetDes String, description of the asset
     * @throws SQLException unable to fetch from database
     * @throws IOException unable to connect to server, output or input stream
     * @throws ClassNotFoundException unable to convert to class
     * @throws ParseException unable to convert to class
     */
    public Asset(int Id, String Name, String AssetDes) throws SQLException, IOException, ClassNotFoundException, ParseException {
        AssetName = Name;
        AssetId = Id;
        this.AssetDes = AssetDes;
        DB dbhistory = new DB();
        String SQL_history = String.format("SELECT * FROM  tradehistory WHERE tradehistory.Trade_type = '2' AND tradehistory.Asset_id = %d", Id);
        dbhistory.query(SQL_history);
        System.out.println(SQL_history);
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        while (dbhistory.next()){
            HashMap<String,String> row = dbhistory.Pop();
            history.put(format.parse(row.get("Post_date")), Double.parseDouble(row.get("Cost")));
        }
        HashMap<String,String> row = dbhistory.Pop();
        if (row != null) {
            history.put(format.parse(row.get("Post_date")), Double.parseDouble(row.get("Cost")));
        }
        calcAll();
        dbhistory.Close();
    }

    @Override
    public String send() {
        return null;
    }

    /**
     * Get the asset id
     * @return the asset id of the given asset
     */
    @Override
    public int getId() {
        return AssetId;
    }

    /**
     * returns the trade history of the asset type
     * @return HashMap<Date, Double> with the date of the trade and the cost of the trade
     */
    public HashMap<Date, Double> getHistory(){
        return history;
    }

    /**
     * calculate the min price the asset was sold for
     */
    public void calcMin(){
        minprice = Double.POSITIVE_INFINITY;
        for (Map.Entry<Date,Double> pair : history.entrySet()){
            if (minprice > pair.getValue()){
                minprice=pair.getValue();
            }
        }
    }

    /**
     * calculate the max price the asset was ever sold for
     */
    public void calMax(){
        maxprice = Double.NEGATIVE_INFINITY;
        for (Map.Entry<Date,Double> pair : history.entrySet()){
            if (maxprice < pair.getValue()){
                maxprice=pair.getValue();
            }
        }
    }

    /**
     * function to calculate the min max and mean all in one for loop
     */
    public void calcAll(){
        double count =0;
        double total =0;
        maxprice = Double.NEGATIVE_INFINITY;
        minprice = Double.POSITIVE_INFINITY;
        for (Map.Entry<Date,Double> pair : history.entrySet()){
            count++;
            total+=pair.getValue();
            if (maxprice < pair.getValue()){
                maxprice=pair.getValue();
            }
            if (minprice > pair.getValue()){
                minprice=pair.getValue();
            }
        }
        meanprice = total/count;
    }

    /**
     * Calculate the mean price the asset sells for
     */
    public void calMean(){
        double count =0;
        double total =0;
        for (Map.Entry<Date,Double> pair : history.entrySet()){
            count++;
            total+=pair.getValue();
        }
        meanprice = total/count;
    }

    /**
     *
     * @return the max price of an asset
     */
    public double getMax(){
        return maxprice;
    }

    /**
     *
     * @return the min price of the asset
     */
    public double getMin(){
    return minprice;
    }

    /**
     *
     * @return the mean price of an asset
     */
    public double getMean(){
        return meanprice;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeDouble(meanprice);
        out.writeDouble(minprice);
        out.writeDouble(maxprice);
        out.writeObject(history);
        out.writeInt(AssetId);
        out.writeUTF(AssetName);
        out.writeUTF(AssetDes);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        meanprice = in.readDouble();
        minprice = in.readDouble();
        maxprice = in.readDouble();
        history = (HashMap<Date, Double>) in.readObject();
        AssetId = in.readInt();
        AssetName = in.readUTF();
        AssetDes = in.readUTF();
    }

    public String getName() {
        return AssetName;
    }

    public String getDescription() {
        return AssetDes;
    }
}
