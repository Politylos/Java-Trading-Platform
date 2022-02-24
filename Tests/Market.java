//import org.graalvm.compiler.nodes.calc.UnpackEndianHalfNode;

import java.io.IOException;
import java.io.Serial;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 *Market contains all the methods necessary for the server to interact with the database and update the database
 * @throws SQLException
 * @throws ClassNotFoundException
 * @throws IOException
 */

public class Market extends Parent_Market{
    @Serial
    private static final long serialVersionUID = -2856333223719086524L;
    public Market() throws SQLException, IOException, ClassNotFoundException, ParseException {
        fetchTrades();
        fetchAssetes();
        fetchOrganisations();
        print();
    }
    /**
     * performs checks for buy and sell trades that are compatible via looping through all the buy trades posted and comparing them to the sell trades
     * @throws SQLException, when the sql server is unable to pull data or unexpected return type
     * @throws IOException, server is down, server cant be reached
     * @throws ClassNotFoundException, cant cast object
     */
    public void PreformTransaction() throws SQLException, IOException, ClassNotFoundException {
        String sqlB = String.format("SELECT * FROM trades WHERE Trade_Type = %d", Trade_Type.BUY.type());
        DB db_buy = new DB();
        db_buy.query(sqlB);
        //loop through all buys
        while(db_buy.next()){
            HashMap<String,String>buy =  db_buy.Pop();
            //check for a compatible sell trade for the current buy trade returns true if trade is completed
            String tradeid = buy.get("Trade_id");
            if (checkForSell(Integer.parseInt(buy.get("Organisation_id")),Integer.parseInt(buy.get("Amount")),
                    Double.parseDouble(buy.get("Cost")),Integer.parseInt(buy.get("Asset_id")),tradeid)){
                //if trade is completed remove from database
                String sql = String.format("DELETE FROM trades WHERE Trade_id = %d", Integer.parseInt(buy.get("Trade_id")));
                DB db = new DB();
                db.insert(sql);
                db.close();
            }
        }
        HashMap<String,String>buy =  db_buy.Pop();
        //get last trade if it exists
        String tradeid = buy.get("Trade_id");
        if (buy != null){
            if (checkForSell(Integer.parseInt(buy.get("Organisation_id")),Integer.parseInt(buy.get("Amount")),
                    Double.parseDouble(buy.get("Cost")),Integer.parseInt(buy.get("Asset_id")),tradeid)){
                String sql = String.format("DELETE FROM trades WHERE Trade_id = %d", Integer.parseInt(buy.get("Trade_id")));
                db_buy.insert(sql);
            }
        }
        db_buy.Close();
    }
    /**
     * checks the org if they contain the asset
     * @param asset, asset_id of an asset
     * @param org, org_id of an organisation
     * @param amount, amount of assets
     * @return Boolean, provides feedback to user whether is has worked or not
     * @throws SQLException, when the sql server is unable to pull data or unexpected return type
     * @throws IOException, server is down, server cant be reached
     * @throws ClassNotFoundException, cant cast object
     */
    public boolean check_org_asset(int asset, int org, int amount) throws SQLException, IOException, ClassNotFoundException {
        DB db = new DB();
        String sql = String.format("SELECT * FROM organisationassets WHERE Asset_id = %d AND Organisation_id = %d",asset,org);
        db.query(sql);
        HashMap<String,String> asset_db = db.getSingle();

        if(asset_db != null){
            int real_amount = Integer.parseInt(asset_db.get("Amount"));
            if (real_amount >= amount){
                System.out.println("Good enough assets");
                db.close();
                return true;
            }
        }
        db.close();
        return false;
    }
    /**
     * checks to see if there is a corresponding sell trade for a th given buy conditions
     * @param org_id, organisation_id of organisation
     * @param amount, amount of assets
     * @param cost, cost per asset
     * @param asset, asset_id of asset
     * @return
     * @throws SQLException, when the sql server is unable to pull data or unexpected return type
     * @throws IOException, server is down, server cant be reached
     * @throws ClassNotFoundException, cant cast object
     */
    public boolean checkForSell(int org_id, int amount, double cost, int asset,String tradeid) throws SQLException, IOException, ClassNotFoundException {
        String sqlS = String.format("SELECT * FROM trades WHERE Trade_Type = %d and Asset_id = %d", Trade_Type.SELL.type(),asset);
        System.out.println(sqlS);
        DB db_sell = new DB();
        db_sell.query(sqlS);
        String sql = String.format("SELECT * FROM organisations WHERE Organisation_id = %d", org_id);
        DB db = new DB();
        db.query(sql);
        HashMap<String,String> org = db.getSingle();
        //checks that an org is returned
        if((org.get("Credits") != null)){
            double credits = Double.parseDouble(org.get("Credits"));
            db_sell.BeforeFirstResult();
            //getting all trade sells where the trade type is sell and is selling the wanted asset
            while (db_sell.next()) {
                System.out.println("looking for trade");
                HashMap<String, String> trade = db_sell.Pop();
                //check if the org selling and buying is diffrent
                if (Integer.parseInt(trade.get("Organisation_id")) != org_id) {

                    sql = String.format("SELECT * FROM organisations WHERE Organisation_id = %d", Integer.parseInt(trade.get("Organisation_id")));
                    db.query(sql);
                    HashMap<String,String> org_sell = db.getSingle();
                    Double sell_cost = Double.parseDouble(trade.get("Cost"));
                    int sell_amount = Integer.parseInt(trade.get("Amount"));
                    int sell_left = sell_amount - amount;
                    //checks  if the price of the asset being sold is lower or equal to the wanted buy price
                    if ((sell_cost <= cost)) {
                        double total_cost = 0;
                        DB db_insert = new DB();
                        sql = String.format("SELECT * FROM organisationassets WHERE Organisation_id = %d AND Asset_id = %d", org_id, asset);
                        db.query(sql);
                        HashMap<String, String> org_ass = db.getSingle();
                        sql = String.format("SELECT * FROM organisationassets WHERE Organisation_id = %d AND Asset_id = %d", Integer.parseInt(trade.get("Organisation_id")), asset);
                        System.out.println(sql);
                        db.query(sql);
                        HashMap<String, String> org_ass_sell = db.getSingle();
                        int toappend = 0;
                        //check if the sell order has any assets left
                        //removes the order if no assets are left other wise update the sell amount
                        if (sell_left > 0) {
                            System.out.println("enough");
                            total_cost = amount * sell_cost;
                            sql = String.format("UPDATE trades SET Amount = %d WHERE Trade_id = %d", sell_left, Integer.parseInt(trade.get("Trade_id")));
                            db_insert.insert(sql);
                            db_insert.insert(sql);
                            //check if a new orgasset id needs tobe creataed or if it can be added to an existing id
                            if (org_ass != null) {
                                toappend = Integer.parseInt(org_ass.get("Amount")) + amount;
                                sql = String.format("UPDATE organisationassets SET Amount = %d WHERE Organisation_id = %d AND Asset_id = %d", toappend, org_id, asset);
                                db_insert.insert(sql);
                            } else {
                                toappend = amount;
                                sql = String.format("INSERT INTO organisationassets (Organisation_id, Asset_id, Amount) VALUES (%d, %d,%d)", org_id,asset,toappend);
                                System.out.println(sql);
                                db_insert.insert(sql);
                            }
                            //all assets have been brought from this sell
                        } else {
                            total_cost = sell_amount * sell_cost;
                            sql = String.format("INSERT INTO tradehistory (Trade_id, Trade_type, Asset_id, Organisation_id, Amount,Cost,Post_date) VALUES (%s,%s,%s,%s,%s,%s,'%s')",
                                    trade.get("Trade_id"), trade.get("Trade_type"),  trade.get("Asset_id"), trade.get("Organisation_id"), trade.get("Amount"),
                                    trade.get("Cost"), trade.get("Post_date"));
                            db_insert.insert(sql);
                            db_insert.insert(sql);
                            sql = String.format("DELETE FROM trades WHERE Trade_id = %d", Integer.parseInt(trade.get("Trade_id")));
                            db_insert.insert(sql);
                            //check if a new orgasset id needs tobe creataed or if it can be added to an existing id
                            if (org_ass != null) {
                                toappend = Integer.parseInt(org_ass.get("Amount")) + sell_amount;
                                sql = String.format("UPDATE organisationassets SET Amount = %d WHERE Organisation_id = %d AND Asset_id = %d", toappend, org_id, asset);
                                db_insert.insert(sql);
                            } else {
                                toappend = sell_amount;
                                sql = String.format("INSERT INTO organisationassets (Organisation_id, Asset_id, Amount) VALUES (%d, %d,%d)", org_id, asset,toappend);
                                System.out.println(sql);
                                db_insert.insert(sql);
                            }

                        }
                        sql = String.format("UPDATE organisations SET Credits = %f WHERE Organisation_id = %d", Double.parseDouble(org_sell.get("Credits")) + total_cost, Integer.parseInt(trade.get("Organisation_id")));
                        db_insert.insert(sql);
                        amount = -sell_amount + amount;
                        db_insert.Close();
                        //returns trrue if all assets have been brought
                        if (amount <= 0) {
                            db_sell.Close();
                            db.Close();
                            return true;
                            //else make buy smaller for next check
                        } else  {
                            sql = String.format("UPDATE trades SET Amount = %d WHERE Trade_id = %s", amount, tradeid);
                            db.insert(sql);
                        }
                    }
                }
            }
        }
        db_sell.Close();
        db.Close();
        return false;
    }
    /**
     * creates a new buy trade and checks for compatiable sell trades
     * @param org_id, organisation_id of the organisation
     * @param asset_id, the asset_id of an asset
     * @param amount, the amount of asset wanting to be bought
     * @param cost, the cost per asset
     * @return Boolean, checking whether it has succeeded or not
     * @throws SQLException, when the sql server is unable to pull data or unexpected return type
     * @throws IOException, server is down, server cant be reached
     * @throws ClassNotFoundException, cant cast object
     */
    public boolean NewBuy(int org_id, int asset_id, int amount, double cost) throws SQLException, IOException, ClassNotFoundException {

        String sql = String.format("SELECT * FROM organisations WHERE Organisation_id = %d", org_id);
        DB db = new DB();
        db.query(sql);
        HashMap<String,String> org = db.getSingle();
        //check if the org is valid
        if(org != null){
            //check if the org has enough criedts to place the order
            if (Double.parseDouble(org.get("Credits")) >= cost*amount){
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                //place the trade
                sql = String.format("INSERT INTO trades (Trade_type, Asset_id, Organisation_id, Amount,Cost,Post_date) VALUES (%s,%s,%s,%s,%s,'%s')",
                        Trade_Type.BUY.type(),asset_id,org_id,amount,cost,dtf.format(now));
                System.out.println(sql);
                db.insert(sql);
                sql = String.format("SELECT * FROM trades WHERE Trade_type=%s AND Asset_id=%s AND Organisation_id=%s AND Amount=%s AND Cost=%s AND Post_date='%s'",Trade_Type.BUY.type(),asset_id,org_id,amount,cost,dtf.format(now));
                db.query(sql);
                db.FirstResult();
                String tradeid = db.getColumn("Trade_id");
                //remove the creidits from the org to store in the trade and freeze them for now
                sql = String.format("UPDATE organisations SET Credits = %f WHERE Organisation_id = %d",Double.parseDouble(org.get("Credits")) - cost*amount, org_id);
                db.insert(sql);
                //checks if any sell trade is what the buy trade is lookng for
                if (checkForSell(org_id, amount,cost,asset_id,tradeid)){
                    //if all assets have been brought remove the buy trade and add it to history
                    sql = String.format("SELECT * FROM trades WHERE Post_date='%s' AND Organisation_id = %s AND Asset_id = %s",dtf.format(now),org_id,asset_id);
                    db.query(sql);
                    HashMap<String, String> trade = db.getSingle();
                    sql = String.format("INSERT INTO tradehistory (Trade_id, Trade_type, Asset_id, Organisation_id, Amount,Cost,Post_date) VALUES (%s,%s,%s,%s,%s,%s,'%s')",
                            trade.get("Trade_id"),trade.get("Trade_type"),trade.get("Asset_id"),trade.get("Organisation_id"),trade.get("Amount"),
                            trade.get("Cost"), trade.get("Post_date"));
                    db.insert(sql);
                    sql = String.format("DELETE FROM trades WHERE Trade_id = %d", Integer.parseInt(trade.get("Trade_id")));
                    db.insert(sql);
                }
                db.Close();
                return true;
            }
        }
        db.Close();
        return false;
    }
    /**
     * creates a new sell trade and checks for a buy trade
     * @param org_id, organisation_id of the organisation
     * @param asset_id, the asset_id of an asset
     * @param amount, the amount of asset wanting to be bought
     * @param cost, the cost per asset
     * @return Boolean, tells the user whether it has succeeded or not
     * @throws SQLException, when the sql server is unable to pull data or unexpected return type
     * @throws IOException, server is down, server cant be reached
     * @throws ClassNotFoundException, cant cast object
     */
    public boolean NewSell(int org_id, int asset_id, int amount, double cost) throws SQLException, IOException, ClassNotFoundException {
        String sql = String.format("SELECT * FROM organisationassets WHERE Organisation_id = %d AND Asset_id = %d", org_id, asset_id);
        DB db = new DB();
        db.query(sql);
        HashMap<String,String> org_ass = db.getSingle();
        if (org_ass != null){
            //checks if the org has enough assets
            if (Integer.parseInt(org_ass.get("Amount"))>= amount){
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                //add the trade to the database
                sql = String.format("INSERT INTO trades (Trade_type, Asset_id, Organisation_id, Amount,Cost,Post_date) VALUES (%s,%s,%s,%s,%s,'%s')",
                        Trade_Type.SELL.type(),asset_id,org_id,amount,cost,dtf.format(now));
                db.insert(sql);
                //checks if the orgasset id needs to be removed at 0 for total amount
                if (!(Integer.parseInt(org_ass.get("Amount"))== amount)) {
                    sql = String.format("UPDATE organisationassets SET Amount = %d WHERE Organisation_id = %d AND Asset_id = %d", Integer.parseInt(org_ass.get("Amount"))-amount, org_id, asset_id);
                } else {
                    sql = String.format("DELETE FROM organisationassets WHERE Organisation_id = %d AND Asset_id = %d", org_id, asset_id);
                }
                System.out.println(sql);
                db.insert(sql);
                PreformTransaction();
                return true;
            }
        }
        db.Close();
        return false;
    }

    /**
     * prints out assets to the console
     */
    public void print(){
        if(!assets.isEmpty()) {
            Iterator it = assets.entrySet().iterator();
            while(it.hasNext()) {
                Map.Entry obj = (Map.Entry)it.next();
                System.out.println(obj.toString());
            }
        }
    }
    /**
     * fetches trades from database
     * @throws SQLException, when the sql server is unable to pull data or unexpected return type
     * @throws IOException, server is down, server cant be reached
     * @throws ClassNotFoundException, cant cast object
     * @throws ParseException, trying to parse an object that cant be parsed
     */

    public void fetchTrades() throws SQLException, IOException, ClassNotFoundException, ParseException {
        DB dbconn = new DB();
        dbconn.query("SELECT * FROM trades, assets, organisations WHERE organisations.Organisation_id = trades.Organisation_id AND assets.Asset_id = trades.Asset_id");

        while (dbconn.next()){
            HashMap<String,String> row = dbconn.Pop();
            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            trades.put(Integer.parseInt(row.get("Trade_id")),new Trade(Integer.parseInt(row.get("Trade_id")),row.get("Name"), Integer.parseInt(row.get("Amount")), Double.parseDouble(row.get("Cost")), Integer.parseInt(row.get("Trade_type")), row.get("Organisation_Name"),Integer.parseInt(row.get("Organisation_id")),row.get("Description"), format.parse(row.get("Post_date")),Integer.parseInt(row.get("Asset_id"))));
        }
        HashMap<String,String> row = dbconn.Pop();
        if (row != null){
            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            trades.put(Integer.parseInt(row.get("Trade_id")),new Trade(Integer.parseInt(row.get("Trade_id")),row.get("Name"), Integer.parseInt(row.get("Amount")), Double.parseDouble(row.get("Cost")), Integer.parseInt(row.get("Trade_type")), row.get("Organisation_Name"),Integer.parseInt(row.get("Organisation_id")),row.get("Description"), format.parse(row.get("Post_date") ),Integer.parseInt(row.get("Asset_id"))));
        }
    }
    /**
     * fetches asset
     * @throws SQLException, when the sql server is unable to pull data or unexpected return type
     * @throws IOException, server is down, server cant be reached
     * @throws ClassNotFoundException, cant cast object
     * @throws ParseException, trying to parse an object that cant be parsed
     */
    public void fetchAssetes() throws SQLException, IOException, ClassNotFoundException, ParseException {
        DB dbconn = new DB();
        dbconn.query("SELECT * FROM assets");
        while (dbconn.next()){
            HashMap<String,String> row = dbconn.Pop();
            assets.put(row.get("Name"), new Asset(Integer.parseInt(row.get("Asset_id")), row.get("Name"), row.get("Description")));
        }
        HashMap<String,String> row = dbconn.Pop();
        if (row != null) {
            assets.put(row.get("Name"), new Asset(Integer.parseInt(row.get("Asset_id")), row.get("Name"), row.get("Description")));
        }
    }
    /**
     * feteches organisations
     * @throws SQLException, when the sql server is unable to pull data or unexpected return type
     * @throws IOException, server is down, server cant be reached
     * @throws ClassNotFoundException, cant cast object
     */
    public void fetchOrganisations() throws SQLException, IOException, ClassNotFoundException {
        DB dbconn = new DB();
        dbconn.query("SELECT * FROM organisations");
        while (dbconn.next()){
            HashMap<String,String> row = dbconn.Pop();
            orgs.put(row.get("Organisation_Name"),new organisation(Integer.parseInt(row.get("Organisation_id")),row.get("Organisation_Name"),Double.parseDouble(row.get("Credits"))));
        }
        HashMap<String,String> row = dbconn.Pop();
        if (row != null) {
            orgs.put(row.get("Organisation_Name"), new organisation(Integer.parseInt(row.get("Organisation_id")), row.get("Organisation_Name"), Double.parseDouble(row.get("Credits"))));
        }
    }
    /**
     * gets a hashmap of all trades
     * @return Hashmap, returns a hashmap of all the trades
     */
    public HashMap<Integer, Trade> getTrades(){
        throw new UnsupportedOperationException();
    }
    /**
     * returns a trade object of trade
     * @param name, name of organisation
     * @return Trade, returns the trade
     */
    public Trade getTrades(String name){
        throw new UnsupportedOperationException();
    }
    /**
     * Remove trade from database
     * @param OrgName, name of organisation
     * @param Tradeid, id of trade to remove
     * @return Boolean, provides feedback to user whether successful or not
     * @throws SQLException, when the sql server is unable to pull data or unexpected return type
     * @throws IOException, server is down, server cant be reached
     * @throws ClassNotFoundException, cant cast object
     */
    public Boolean RemoveTrade(String OrgName, String Tradeid) throws SQLException, IOException, ClassNotFoundException {
        DB dbconn = new DB();
        dbconn.query(String.format("SELECT * FROM trades WHERE Trade_id = %s",Tradeid));
        HashMap<String,String> asset = dbconn.Pop();
        dbconn.query(String.format("SELECT * FROM organisations WHERE Organisation_Name = '%s'",OrgName));
        HashMap<String,String> org = dbconn.Pop();

        if (org.get("Organisation_id").equals(asset.get("Organisation_id"))) {
            if (Trade_Type.BUY.type() ==Integer.parseInt(asset.get("Trade_type"))){
                String sql = String.format("UPDATE organisations SET Credits = %f WHERE Organisation_id = %s",Double.parseDouble(org.get("Credits"))+Double.parseDouble(asset.get("Cost"))*Double.parseDouble(asset.get("Amount")), org.get("Organisation_id"));
                dbconn.insert(sql);
            } else{
                String sql = String.format("SELECT * FROM organisationassets WHERE Organisation_id = %s AND Asset_id = %s",org.get("Organisation_id"),asset.get("Asset_id"));
                dbconn.query(sql);
                HashMap<String,String> currentA = dbconn.getSingle();
                if (currentA != null){
                    sql = String.format("UPDATE organisationassets set Amount = %d WHERE Organisation_id = %s AND Asset_id = %s", Integer.parseInt(asset.get("Amount"))+Integer.parseInt(currentA.get("Amount")),org.get("Organisation_id"),asset.get("Asset_id"));
                } else{
                    sql = String.format("INSERT INTO organisationassets (Organisation_id, Asset_id, Amount) VALUES (%s, %s, %s)",org.get("Organisation_id"),asset.get("Asset_id") ,asset.get("Amount"));
                }
                dbconn.insert(sql);
            }
            String sql = String.format("DELETE FROM trades WHERE Trade_id = %s", Tradeid);
            dbconn.insert(sql);
            dbconn.close();
            return true;
        }
        return false;
    }
    /**
     * adds new user to the database
     * @param FirstName, firstname of user
     * @param LastName, lastname of user
     * @param Email, email of user
     * @param Username, username of user
     * @param role, role of user
     * @param OrgId, organisation_id of organisation
     * @param password, password of user
     * @return Boolean, provides feedback to user whether successful or not
     * @throws SQLException, when the sql server is unable to pull data or unexpected return type
     * @throws IOException, server is down, server cant be reached
     * @throws ClassNotFoundException, cant cast object
     */
    public Boolean AddNewUser(String FirstName, String LastName, String Email, String Username, int role, int OrgId, String password) throws SQLException, IOException, ClassNotFoundException {
        Boolean duplicate = false;
        DB dbconn = new DB();
        dbconn.query("SELECT Username FROM users");//gets all information from user table
        ArrayList<String[]> Usernames = dbconn.fullArray();
        for (String[] str : Usernames){
            for (String str1 : str){
                if(str1.equals(Username)){
                    duplicate = true;
                }
            }
        }
        if(!duplicate){
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            String pw_hash = BCrypt.hashpw(password, BCrypt.gensalt());
            String sql = String.format("INSERT INTO users (FirstName, LastName, email, Username, Role, Organisation_id, Password, Active, Last_Active) VALUES ('%s','%s','%s','%s',%d,%d,'%s',%s,'%s')",
                    FirstName,LastName,Email,Username,role,OrgId,String.valueOf(pw_hash),String.valueOf(0),dtf.format(now));
            System.out.println(sql);
            dbconn.insert(sql);
            return true;
        }
        else{
            return false;
        }
    }
    /**
     * changes username of valid user
     * @param OldUserName, Old username of user
     * @param NewUserName, New username of user
     * @return Boolean, provides feedback whether successful or not
     * @throws SQLException, when the sql server is unable to pull data or unexpected return type
     * @throws IOException, server is down, server cant be reached
     * @throws ClassNotFoundException, cant cast object
     */
    public Boolean ChangeUsername(String OldUserName, String NewUserName) throws SQLException, IOException, ClassNotFoundException {
        DB dbconn = new DB();
        dbconn.query("SELECT Username FROM users");
        ArrayList<String[]> Usernames = dbconn.fullArray();
        for(String[] Username : Usernames){
            for(String user : Username){
                if(user.equals(OldUserName)){
                    String sql = String.format("UPDATE users SET username = '%s' WHERE username = '%s'",
                            NewUserName,OldUserName);
                    dbconn.insert(sql);
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * Adds a new user to database
     * @param OrgName, name of organisation
     * @param credits, amount of credits organisation owns
     * @return Boolean, provides feedback whether successful or not
     * @throws SQLException, when the sql server is unable to pull data or unexpected return type
     * @throws IOException, server is down, server cant be reached
     * @throws ClassNotFoundException, cant cast object
     */
    public Boolean AddNewOrganisation(String OrgName, double credits) throws SQLException, IOException, ClassNotFoundException {
        Boolean Duplicate = false;
        DB dbconn = new DB();
        dbconn.query("SELECT Organisation_Name FROM organisations");
        ArrayList<HashMap<String, String>> OrganisationNames = dbconn.fullmap();
        for(HashMap<String,String> Organisation : OrganisationNames){
            if(Organisation.get("Organisation_Name").equals(OrgName)){
                Duplicate = true;
            }
        }
        if(!Duplicate){
            String sql = String.format("INSERT INTO `organisations` (`Organisation_Name`, `Credits`) VALUES ('%s',%f)",OrgName, credits);
            dbconn.insert(sql);
            return true;
        }
        else {
            return false;
        }
    }
    /**
     * adds a new asset to the database
     * @param Name, Name of asset
     * @param AssetDes, Description of asset
     * @return Boolean, provides feedback whether successful or not
     * @throws SQLException, when the sql server is unable to pull data or unexpected return type
     * @throws IOException, server is down, server cant be reached
     * @throws ClassNotFoundException, cant cast object
     */
    public Boolean AddNewAsset(String Name,String AssetDes) throws SQLException, IOException, ClassNotFoundException {
        Boolean Duplicate = false;
        DB dbconn = new DB();
        dbconn.query("SELECT Name FROM assets");
        ArrayList<HashMap<String,String>> Assets = dbconn.fullmap();
        for(HashMap<String,String> Asset : Assets){//loops through returned values from sql statement to see if asset with same name already exists
            if(Asset.get("Name").equals(Name)){
                Duplicate = true;
            }
        }
        if(!Duplicate){
            String sql = String.format("INSERT INTO assets (Name, Description) VALUES ('%s','%s')",Name, AssetDes);
            dbconn.insert(sql);
            return true;
        }
        else {
            return false;
        }
    }
    /**
     * adds money to an organisation
     * @param Name, Name of organisation
     * @param credits, amount of credits you wish to add
     * @return Boolean, provides feedback whether successful or not
     * @throws SQLException, when the sql server is unable to pull data or unexpected return type
     * @throws IOException, server is down, server cant be reached
     * @throws ClassNotFoundException, cant cast object
     */
    public Boolean AddMoneyOrganisation(String Name, int credits) throws SQLException, IOException, ClassNotFoundException {
        DB dbconn = new DB();
        dbconn.query("SELECT * FROM organisations");
        ArrayList<HashMap<String,String>> Organisations = dbconn.fullmap();
        for(HashMap<String,String> Organisation : Organisations){
            if(Organisation.get("Organisation_Name").equals(Name)){
                String sql = String.format("UPDATE organisations SET Credits = %d WHERE Organisation_Name = '%s'",Integer.parseInt(Organisation.get("Credits"))+credits,Name);
                dbconn.insert(sql);
                return true;
            }
        }
        return false;
    }
    /**
     * removes a user from the database
     * @param Username, username of user
     * @return Boolean, provides feedback whether successful or not
     * @throws SQLException, when the sql server is unable to pull data or unexpected return type
     * @throws IOException, server is down, server cant be reached
     * @throws ClassNotFoundException, cant cast object
     */
    public Boolean RemoveUser(String Username) throws SQLException, IOException, ClassNotFoundException {
        DB dbconn = new DB();
        dbconn.query("SELECT Username FROM users");
        ArrayList<HashMap<String,String>> Names = dbconn.fullmap();
        for(HashMap<String,String> Name : Names){
            if(Name.get("Username").equals(Username)){
                String sql = String.format("DELETE FROM users where Username = '%s'",Username);
                dbconn.insert(sql);
                return true;
            }
        }
        return false;
    }
    /**
     * changes the password of a user, this method is called when a user tries to change their password
     * @param Username, username of user
     * @param OldPassword, old password of user
     * @param NewPassword, new password you wish to change to
     * @return Boolean, provides feedback whether successful
     * @throws SQLException, when the sql server is unable to pull data or unexpected return type
     * @throws IOException, server is down, server cant be reached
     * @throws ClassNotFoundException, cant cast object
     */
    public Boolean ChangePassword(String Username, String OldPassword, String NewPassword) throws SQLException, IOException, ClassNotFoundException {
        DB dbconn = new DB();
        dbconn.query("SELECT * FROM users");
        ArrayList<HashMap<String,String>> Users = dbconn.fullmap();
        for(HashMap<String,String> User: Users){
            if(User.get("Username").equals(Username)){//checks to see if username matches the user trying to change password
                if(BCrypt.checkpw(OldPassword,User.get("Password"))){//checks to see if passwords match
                    String pw_hash = BCrypt.hashpw(NewPassword, BCrypt.gensalt());
                    String sql = String.format("UPDATE users SET Password = '%s' WHERE UserName = '%s'",
                            pw_hash,Username);
                    dbconn.insert(sql);
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * This method allows the changing of passwords for admins, thus is why they dont have to input the users old password
     * @param Username, the username of the user's password wanting to be changed
     * @param NewPassword, the new password
     * @return Boolean, provides feedback whether successful
     * @throws SQLException, when the sql server is unable to pull data or unexpected return type
     * @throws IOException, server is down, server cant be reached
     * @throws ClassNotFoundException, cant cast object
     */
    public Boolean ChangePassword(String Username, String NewPassword) throws SQLException, IOException, ClassNotFoundException {
        DB dbconn = new DB();
        dbconn.query("SELECT * FROM users");
        ArrayList<HashMap<String,String>> Users = dbconn.fullmap();
        for(HashMap<String,String> User: Users){
            if(User.get("Username").equals(Username)){//checks to see if username matches the user trying to change password
                String pw_hash = BCrypt.hashpw(NewPassword, BCrypt.gensalt());
                String sql = String.format("UPDATE users SET Password = '%s' WHERE UserName = '%s'",
                        pw_hash,Username);
                dbconn.insert(sql);
                return true;
            }
        }
        return false;
    }

    /**
     * This method is able to remove an organisation from the database
     * @param OrgName, the name of the organisation wanting to be removed
     * @return Boolean, provides feedback whether successful
     * @throws SQLException, when the sql server is unable to pull data or unexpected return type
     * @throws IOException, server is down, server cant be reached
     * @throws ClassNotFoundException, cant cast object
     */

    public Boolean RemoveOrganisation(String OrgName) throws SQLException, IOException, ClassNotFoundException {//removes organisation from database and users link with that organisation
        DB dbconn = new DB();
        dbconn.query("SELECT * FROM organisations");
        ArrayList<HashMap<String,String>> organisations = dbconn.fullmap();
        for(HashMap<String,String> organisation : organisations){
            if(organisation.get("Organisation_Name").equals(OrgName)){
                String sql = String.format("DELETE FROM organisationassets WHERE Organisation_id = '%s'",organisation.get("Organisation_id"));
                dbconn.insert(sql);
                sql = String.format("DELETE FROM users WHERE Organisation_id = '%s'",organisation.get("Organisation_id"));
                dbconn.insert(sql);
                sql = String.format("DELETE FROM tradehistory WHERE organisation_id = '%s'",organisation.get("Organisation_id"));
                dbconn.insert(sql);
                sql = String.format("DELETE FROM trades WHERE organisation_id = '%s'",organisation.get("Organisation_id"));
                dbconn.insert(sql);
                sql = String.format("DELETE FROM organisations WHERE Organisation_Name = '%s'",OrgName);//delete organisation in database
                dbconn.insert(sql);
                return true;
            }
        }
        return false;
    }
    /**
     * Removes an asset from an organisation
     * @param Asset, name of asset
     * @param OrgName, name of organisation
     * @return Boolean, provides feedback whether successful or not
     * @throws SQLException, when the sql server is unable to pull data or unexpected return type
     * @throws IOException, server is down, server cant be reached
     * @throws ClassNotFoundException, cant cast object
     */
    public Boolean RemoveAssetFromOrg(String Asset,String OrgName) throws SQLException, IOException, ClassNotFoundException {//removes asset from org
        String OrgId = "NotFound";
        DB dbconn = new DB();
        dbconn.query("SELECT * FROM assets");
        ArrayList<HashMap<String,String>> assets = dbconn.fullmap();
        dbconn.query("SELECT * FROM organisations");
        ArrayList<HashMap<String,String>> Organisations = dbconn.fullmap();
        for(HashMap<String,String> Organisation : Organisations){
            if(Organisation.get("Organisation_Name").equals(OrgName)){
                OrgId = Organisation.get("Organisation_id");
            }
        }
        for(HashMap<String,String> asset : assets){
            if((asset.get("Name").equals(Asset))&!(OrgId.equals("NotFound"))){
                String sql = String.format("DELETE FROM organisationassets WHERE Asset_id = '%s' AND Organisation_id = '%s'",asset.get("Asset_id"),OrgId);
                dbconn.insert(sql);
                return true;
            }
        }
        return false;
    }
    /**
     * deletes an asset from the entire database including if orgs are holding that asset currently
     * @param Asset, name of asset
     * @return Boolean, provides feedback whether successful or not
     ** @throws SQLException, when the sql server is unable to pull data or unexpected return type
     *      * @throws IOException, server is down, server cant be reached
     *      * @throws ClassNotFoundException, cant cast object
     */
    public Boolean DeleteAsset(String Asset) throws SQLException, IOException, ClassNotFoundException {//removes asset from entire database including all
        // orgs that are currently holding the asset and history of asset being traded
        DB dbconn = new DB();
        dbconn.query("SELECT * FROM assets");
        ArrayList<HashMap<String,String>> Assets = dbconn.fullmap();
        for(HashMap<String,String> asset : Assets){
            if(asset.get("Name").equals(Asset)){//checks if correct asset was found
                String sql = String.format("DELETE FROM tradehistory WHERE Asset_id = '%s'",asset.get("Asset_id"));
                dbconn.insert(sql);
                sql = String.format("DELETE FROM organisationassets WHERE Asset_id = '%s'",asset.get("Asset_id"));
                dbconn.insert(sql);
                sql = String.format("DELETE FROM assets WHERE Asset_id = '%s'",asset.get("Asset_id"));
                dbconn.insert(sql);
                return true;
            }
        }
        return false;
    }
    /**
     * adds an asset to an org
     * @param Asset, name of asset
     * @param Org, name of organisation
     * @param amount, amount of asset
     * @return Boolean, provides feedback whether successful or not
     * @throws SQLException, when the sql server is unable to pull data or unexpected return type
     * @throws IOException, server is down, server cant be reached
     * @throws ClassNotFoundException, cant cast object
     */
    public Boolean AddAssetOrg(String Asset, String Org, int amount) throws SQLException, IOException, ClassNotFoundException {
        DB dbconn = new DB();
        dbconn.query(String.format("SELECT * FROM assets WHERE Name = '%s'",Asset));
        System.out.println(String.format("SELECT * FROM assets WHERE Name = '%s'",Asset));
        HashMap<String,String> AssetId = dbconn.getSingle();;
        String Asset_id = AssetId.get("Asset_id");
        dbconn.query(String.format("SELECT * FROM organisations WHERE Organisation_Name = '%s'",Org));
        System.out.println("SELECT * FROM organisations WHERE Organisation_Name = '%s'");
        HashMap<String,String> OrgId= dbconn.getSingle();;
        String Org_id = OrgId.get("Organisation_id");
        if(!(AssetId.isEmpty()||OrgId.isEmpty())){//checks to see if both sql queries have returned values, if they have able to add assets to organisation
            String sql = String.format("SELECT * FROM organisationassets WHERE Organisation_id = %s AND Asset_id =%s", OrgId.get("Organisation_id"),AssetId.get("Asset_id"));
            dbconn.query(sql);
            HashMap<String,String> exist = dbconn.getSingle();
            if (exist == null){
                sql = String.format("INSERT INTO organisationassets (Organisation_id, Asset_id, Amount) VALUES ('%s','%s',%d)",Org_id,Asset_id,amount);
            } else{
                sql = String.format("UPDATE organisationassets set Amount = %d WHERE OA_id = %s",Integer.parseInt(exist.get("Amount"))+amount, exist.get("OA_id"));
            }
            dbconn.insert(sql);
            dbconn.close();
            return true;
        }
        dbconn.close();
        return false;
    }
    /**
     * This method checks if a trade id matches
     * @param trade_ids, array of trade_id's
     * @return Boolean, Provides feedback whether successful or not
     * @throws SQLException, when the sql server is unable to pull data or unexpected return type
     * @throws IOException, server is down, server cant be reached
     * @throws ClassNotFoundException, cant cast object
     */

    public boolean CheckTrades(int[] trade_ids) throws SQLException, IOException, ClassNotFoundException {
        DB dbconn = new DB();
        for (int i : trade_ids){
            String sql = String.format("SELECT * FROM Trades WHERE Trade_id = %d", i);
            dbconn.query(sql);
            if (dbconn.isempty()){
                dbconn.close();
                return true;
            }

        }
        System.out.println("return false");
        dbconn.close();
        return false;
    }
    /**
     * Changes a users organisation
     * @param User, a users username
     * @param OrgName, an organisations_Name
     * @return Boolean, provides feedback whether successful or not
     * @throws SQLException, when the sql server is unable to pull data or unexpected return type
     * @throws IOException, server is down, server cant be reached
     * @throws ClassNotFoundException, cant cast object
     */
    public boolean ChangeUserOrg(String User, String OrgName) throws SQLException, IOException, ClassNotFoundException {
        String sql = String.format("SELECT * FROM organisations WHERE Organisation_Name = '%s'", OrgName);
        DB db = new DB();
        db.query(sql);
        HashMap<String, String> org = db.getSingle();
        if (org != null){
            sql = String.format("UPDATE  Users set Organisation_id = %s WHERE Username =  '%s'", org.get("Organisation_id"),User);
            db.insert(sql);
            db.close();
            return true;
        }
        db.close();
        return false;
    }
    /**
     *
     * @param user, a users username
     * @param role, users role
     * @return Boolean, provides feedback whether successful or not
     * @throws SQLException, when the sql server is unable to pull data or unexpected return type
     * @throws IOException, server is down, server cant be reached
     * @throws ClassNotFoundException, cant cast object
     */
    public boolean ChangeUserRole(String user, String role) throws SQLException, IOException, ClassNotFoundException {
        String sql = String.format("UPDATE Users set Role = %d WHERE Username = '%s'", user_class.valueOf(role).getId(), user);
        DB db = new DB();
        System.out.println(sql);
        db.insert(sql);
        db.close();
        return true;
    }
    /**
     * This method places all users into an array
     * @return Array, the array contains all users in the database at the current time
     * @throws SQLException, when the sql server is unable to pull data or unexpected return type
     * @throws IOException, server is down, server cant be reached
     * @throws ClassNotFoundException, cant cast object
     */
    public String[] getallUsers() throws SQLException, IOException, ClassNotFoundException {
        String sql = String.format("SELECT * FROM Users");
        DB db = new DB();
        db.query(sql);
        String[] names = new String[db.count()];
        int i=0;
        while (db.next()){
            HashMap<String, String> row = db.Pop();
            names[i] = row.get("Username");
            i++;
        }
        HashMap<String, String> row = db.Pop();
        if (row != null) {
            names[i] = row.get("Username");
        }
        db.close();
        return names;
    }
}
