import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class Market extends Parent_Market{
    @Serial
    private static final long serialVersionUID = -2856333223719086524L;
    //    protected HashMap<Integer,Trade> trades = new HashMap<Integer,Trade>();
    //    protected HashMap<String,organisation> orgs = new HashMap<String,organisation>();
    //    protected HashMap<String, Asset> assets = new HashMap<String, Asset>();
    User user;
    Server_Connector server;
    public Market() {
        super();
    }
    /**
     *
     * @param t, hashmap of trades
     * @param orgs, hashmap fo organisations
     * @param assets, hashmap of assets
     * @throws SQLException, when the sql server is unable to pull data or unexpected return type
     * @throws IOException, server is down, server cant be reached
     * @throws ClassNotFoundException, cant cast object
     * @throws ParseException, cant parse object
     */
    public Market(HashMap<Integer,Trade> t,HashMap<String,organisation> orgs, HashMap<String, Asset> assets ) throws SQLException, IOException, ClassNotFoundException, ParseException {
        this.trades = t;
        this.assets = assets;
        this.orgs = orgs;
    }

    /**
     * implements the user class within the server via getting a user input to set the market user to
     * @param user, a User class object to add to the server
     * @throws SQLException error with the database and sql code
     * @throws IOException error with server connection
     * @throws ClassNotFoundException  error with finding a given class file
     * @throws ParseException error with prasing the cast object type
     */
    public Market(User user) throws SQLException, IOException, ClassNotFoundException, ParseException {
        this.user = user;
    }

    /**
     * gts a hashmap of all the Trade classes on the market currently
     * @return HashMap<Integer, Trade> of all the trades
     */
    public HashMap<Integer, Trade> getTrades(){
        return trades;
    }

    /**
     * returns a hash map containing all the orgs on the market place
     * @return HashMap<String,organisation> returning the org variable from Market
     */
    public HashMap<String,organisation> getOrg(){
        return orgs;
    }

    /**
     * getting all the asset types on the market in a hashmap
     * @return HashMap<String, Asset> from Market class's variable assets
     */
    public HashMap<String, Asset> getAsset(){
        return assets;
    }

    /**
     * set the current user that is logged into the market place
     * @param u User class to set logged in user to
     */
    public void setUser(User u){
        this.user = u;
    }

    /**
     * return the current logged in user
     * @return User object, returning the market's User variable
     */
    public User getUser(){
        return user;
    }

    /**
     * add a Server_connector class to enable the market to interface with the server through the server_connector class
     * @param s Sever_connector object to allow the maket to access the server
     */
    public void addServer(Server_Connector s){
        server = s;
    }
    /**
     * Array of asset names
     * @return Array of asset names
     */
    public String[] getAssetnames(){
        String[] names = new String[assets.size()];
        int i = 0;
        for (Map.Entry<String, Asset> set :
                assets.entrySet()) {
            names[i]= set.getKey();
            i++;
        }
        return names;
    }
    /**
     * places a sell trade
     * @param asset, asset wanting to sell
     * @param amount, amount of asset wanting to sell
     * @param price, price per asset
     * @return Boolean, provides feedback whether successful or not
     * @throws IOException, server is down, server cant be reached
     * @throws ClassNotFoundException, cant cast object
     */
    public Boolean PlaceSell(Asset asset, int amount,double price) throws IOException, ClassNotFoundException {
        if (user.getOrg().checkAsset(asset.getName(), amount)){
            server.establish();
            server.send(Server_Code.SELL.getId(), new String[] {String.valueOf(user.getOrgID()), String.valueOf(asset.getId()),String.valueOf(amount), String.valueOf(price)});
            boolean result  = (boolean) server.fetch();
            server.close();
            return result;
        } else{
            System.out.println("Does not exist or not enough");
        }
        return false;
    }
    /**
     * places a buy trade
     * @param asset, asset wanting to buy
     * @param amount, amount of assets wanting to buy
     * @param price, price per asset
     * @return Boolean, provides feedback whether successful or not
     * @throws IOException, server is down, server cant be reached
     * @throws ClassNotFoundException, cant cast object
     */
    public Boolean PlaceBuy(Asset asset,int amount, double price) throws IOException, ClassNotFoundException {
        if (user.getOrg().checkCredits(price)) {
            server.establish();
            server.send(Server_Code.BUY.getId(), new String[] {String.valueOf(user.getOrgID()),String.valueOf(asset.getId()),String.valueOf(amount), String.valueOf(price)});
            boolean result  = (boolean) server.fetch();
            server.close();
            return result;
        } else{
            System.out.println("not enough money");
        }
        return false;
    }
    /**
     * Allows for the removal of a trade form the database through sending the trade id and orgid to the server for checking and deletion
     * @param trade, trade wanting to remove
     * @return Boolean, provides feedback whether successful or not
     * @throws IOException, server is down, server cant be reached
     * @throws ClassNotFoundException, cant cast object
     */
    public Boolean RemoveTrade(Trade trade) throws IOException, ClassNotFoundException {
        if (trade.getOrg() == user.getOrgID()) {
            server.establish();
            server.send(Server_Code.REMOVE_TRADE.getId(), new String[] {String.valueOf(user.getOrgID()),String.valueOf(trade.getId())});
            boolean result  = (boolean) server.fetch();
            server.close();
            return result;
        }
        return false;
    }
    /**
     * Creates a trade request to be sent to the server
     * @param asset_name, name of asset
     * @param cost, cost per asset
     * @param amount, amount of assets
     * @param trade, the type of trade, eg 1 = sell, 2 = buy
     * @return Boolean provides feedback whether successful or not
     * @throws IOException, server is down, server cant be reached
     * @throws ClassNotFoundException, cant cast object
     */
    public Boolean Trade(String asset_name, double cost, int amount, Trade_Type trade) throws IOException, ClassNotFoundException {
        Asset asset = assets.get(asset_name);
        boolean went = false;
        if (trade == Trade_Type.BUY){
            went = PlaceBuy(asset,amount,cost);
        } else if (trade == Trade_Type.SELL){
            went  = PlaceSell(asset,amount,cost);
        }
        return went;
    }
    /**
     * Creates a trade command to be sent to the server
     * @param org_id, organisation_id of organisation
     * @param asset_id, asset_id of asset
     * @param amount, amount of asset
     * @param cost_per_asset, cost per asset
     * @param type, type of trade eg 1 = sell, 2 = buy
     * @return Boolean provides feedback whether successful or not
     * @throws IOException, server is down, server cant be reached
     */
    public Boolean Add_Trade(int org_id, int asset_id, int amount, double cost_per_asset, int type) throws IOException {
        if(org_id == user.getOrgID()) {
            String[] Information = {String.valueOf(asset_id), String.valueOf(amount), String.valueOf(cost_per_asset), String.valueOf(type)};
            new pack(25, Information);
            server.establish();
            server.send(25, Information);
            try{
                if ((boolean)server.fetch()) {
                    System.out.println("Trade created");
                    return true;
                }
                return false;
            } catch (Exception e){
                System.out.println("Trade not created");
                return false;
            }
        }
        return false;
    }
    /**
     * returns an array of org names from the org hashmap
     * @return Array of organisation names
     */
    public String[] getOrgName() {
        String[] array = new String[orgs.size()];
        int i = 0;
        for (Map.Entry<String, organisation> set :
                orgs.entrySet()) {
            array[i]= set.getKey();
            i++;
        }
        return array;
    }
}
