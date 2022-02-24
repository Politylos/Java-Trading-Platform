//import com.sun.security.ntlm.Server;
import org.junit.Test;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap; // import the HashMap class
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class Server_Test {
    private static final long serialVersionUID = -2856333223719086524L;
    private Server_Server server = new Server_Server();
    private Market market = new Market();
    private DB dbconn = new DB();
    public Server_Test() throws ClassNotFoundException, SQLException, ParseException, IOException {
    }
    @Test
    public void Constructors() throws ClassNotFoundException, SQLException, ParseException, IOException {//this method creates an instance of every object to check if they error or not
        Market marketTest = new Market();
        Server_Server ServerTest = new Server_Server();
        DB dbTest = new DB();
        User_Organisation user_organisationTest = new User_Organisation(10.0,"User_Org1",1);
        User userTest = new User(1,"User1","Username1",1,user_organisationTest);
        Date date = new Date();
        Trade TradeTest = new Trade(1,"Asset1",10,10.0,1,"Org1",1,"Asset1 is good",date, 1);
        HashMap<Date, Double> History = new HashMap<>();
        Asset AssestTest = new Asset(1,"Asset1","Asset1 is good",History);
        Org_Asset Org_AssetTest = new Org_Asset(1,"Asset1",10,"Asset1 is good");
        organisation organisationTest = new organisation(1,"Org1",10.0);
    }
    @Test
    public void RemoveUserExist() throws SQLException, IOException, ClassNotFoundException {//removing a user if they exist, should return true
        market.AddNewUser("Nick","Meurant","n10485571@qut.edu.au","Nick",1,1,"Password");
        assertEquals(market.RemoveUser("Nick"),true);
        market.RemoveUser("Nick");
    }
    @Test
    public void RemoveUserDontExist() throws SQLException, IOException, ClassNotFoundException {//removing a user if they dont exist, should return false
        assertEquals(market.RemoveUser("Nick"),false);
    }
    @Test
    public void AddNewUserUnique() throws SQLException, IOException, ClassNotFoundException, ParseException {//testing adding unique user to database
        market.RemoveUser("Nick");
        assertEquals(market.AddNewUser("Nick","Meurant","n10485571@qut.edu.au","Nick",1,1,"Password"),true);
        market.RemoveUser("Nick");
    }
    @Test
    public void AddNewUserAlreadyExist() throws SQLException, IOException, ClassNotFoundException {//adding a user that already exists, should return false
        market.AddNewUser("Nick","Meurant","n10485571@qut.edu.au","Nick",1,1,"Password");
        assertEquals(market.AddNewUser("Nick","Meurant","n10485571@qut.edu.au","Nick",1,1,"Password"),false);
        market.RemoveUser("Nick");
    }
    @Test
    public void ChangePasswordWrongPassword() throws SQLException, IOException, ClassNotFoundException {//changing password, inputting wrong "old password" should return false
        market.AddNewUser("Nick","Meurant","n10485571@qut.edu.au","Nick",1,1,"Password");
        assertEquals(market.ChangePassword("Nick","Password123","Password"),false);
        market.RemoveUser("Nick");
    }
    @Test
    public void ChangePasswordCorrectPassword() throws SQLException, IOException, ClassNotFoundException {
        market.AddNewUser("Nick","Meurant","n10485571@qut.edu.au","Nick",1,1,"Password");
        assertEquals(market.ChangePassword("Nick","Password","Password123"),true);
        market.RemoveUser("Nick");
    }
    @Test
    public void ChangingPasswordNonExistingUser() throws SQLException, IOException, ClassNotFoundException {//testing changing password of user that doesnt exist,should return false
        market.RemoveUser("Nick");
        assertEquals(market.ChangePassword("Nick","Password","Password123"),false);
    }
    @Test
    public void ChangingPasswordAdmin() throws SQLException, IOException, ClassNotFoundException {//this method is for an admin changing a users password, should return true
        market.AddNewUser("Nick","Meurant","n10485571@qut.edu.au","Nick",1,1,"Password");
        assertEquals(market.ChangePassword("Nick","Password"),true);
        market.RemoveUser("Nick");
    }
    @Test
    public void AddNewOrganisationUnique() throws SQLException, IOException, ClassNotFoundException {//adding a unique organisation, should return true;
        market.RemoveOrganisation("Org1");
        assertEquals(market.AddNewOrganisation("Org1",1000),true);
        market.RemoveOrganisation("Org1");
    }
    @Test
    public void AddNewOrganisationAlreadyExist() throws SQLException, IOException, ClassNotFoundException {//adding a org that already exists, should return false
        market.AddNewOrganisation("Org1",1000);
        assertEquals(market.AddNewOrganisation("Org1",1000),false);
        market.RemoveOrganisation("Org1");
    }
    @Test
    public void AddNewAssetUnique() throws SQLException, IOException, ClassNotFoundException {//adding a unique asset, should return true
        market.DeleteAsset("Asset1");
        assertEquals(market.AddNewAsset("Asset1","Asset1 is good"),true);
        market.DeleteAsset("Asset1");
    }
    @Test
    public void AddNewAssetNotUnique() throws SQLException, IOException, ClassNotFoundException {//adding a new asset that already exists, should return false
        market.AddNewAsset("Asset1","Asset 1 is good");
        assertEquals(market.AddNewAsset("Asset1","Asset1 is good"),false);
        market.DeleteAsset("Asset1");
    }
    @Test
    public void AddCreditExistingOrg() throws SQLException, IOException, ClassNotFoundException {//adding credit to an org that exists, should return true and shows updated credit
        market.AddNewOrganisation("Org1",0);
        assertEquals(market.AddMoneyOrganisation("Org1",110),true);
        String sql = "SELECT * FROM organisations WHERE Organisation_Name = 'Org1'";
        dbconn.query(sql);
        HashMap<String,String> Organisation = dbconn.getSingle();
        System.out.println("Org 1 contains " + Organisation.get("Credits") + " credits");
        sql = "DELETE FROM organisations WHERE Organisation_Name = 'Org1'";
        dbconn.insert(sql);
    }
    @Test
    public void AddCreditNonExistingOrg() throws SQLException, IOException, ClassNotFoundException {//adding credit to an org that doesnt exist, should return false
        assertEquals(market.AddMoneyOrganisation("Org1",1000),false);
    }
    @Test
    public void ChangeUsernameExist() throws SQLException, IOException, ClassNotFoundException {
        market.AddNewUser("Nick","Meurant","n10485571@qut.edu.au","Nick",1,1,"Password");
        assertEquals(market.ChangeUsername("Nick","Nick1"),true);
        market.RemoveUser("Nick1");
    }
    @Test
    public void ChangeUsernameDoesntExist() throws SQLException, IOException, ClassNotFoundException {//changing username of user that doesnt exist
        market.RemoveUser("Nick");
        assertEquals(market.ChangeUsername("Nick","Nick1"),false);
    }
    @Test
    public void DeleteOrganisationExist() throws SQLException, IOException, ClassNotFoundException {//deletes organisation that exist, also deletes all users with same org_id,
        // trades with same or_id, organisationassets with same org_id
        market.AddNewOrganisation("org1",10);
        assertEquals(market.RemoveOrganisation("org1"),true);
        //since organisation_id is a primary within the sql database, it wont allow it to be partially deleted from tables without throwing errors
    }
    @Test
    public void DeleteOrganisationNotExist() throws SQLException, IOException, ClassNotFoundException {//deletes an org that doesnt exist, should return false
        assertEquals(market.RemoveOrganisation("Org1"),false);
    }
    @Test
    public void DeleteAssetOrganisation() throws SQLException, IOException, ClassNotFoundException {//deletes an asset from an organisation that the organisation owns, should return true
        market.AddNewAsset("Asset1","Asset1 is good");
        market.AddNewOrganisation("Org1",10000);
        market.AddAssetOrg("Asset1","Org1",10);
        assertEquals(market.RemoveAssetFromOrg("Asset1","Org1"),true);
        market.DeleteAsset("Asset1");
        market.RemoveOrganisation("Org1");
    }
    @Test
    public void DeleteAssetOrganisationNotExist() throws SQLException, IOException, ClassNotFoundException {//deletes an asset from an organsiation that the organisation doenst own, should return false
        assertEquals(market.RemoveAssetFromOrg("Asset1","Org1"),false);
    }
    @Test
    public void AddAssetOrg() throws SQLException, IOException, ClassNotFoundException {
        market.AddNewOrganisation("Org1",10);
        market.AddNewAsset("Asset1","Asset1 is good");
        assertEquals(market.AddAssetOrg("Asset1","Org1",10),true);
        market.RemoveOrganisation("Org1");
        market.DeleteAsset("Asset1");
    }
    @Test
    public void DeleteAssetExist() throws SQLException, IOException, ClassNotFoundException {//deletes an asset than exists, should return true
        market.AddNewAsset("Asset1","Asset1 is good");
        assertEquals(market.DeleteAsset("Asset1"),true);
    }
    @Test
    public void DeleteAssetNotExist() throws SQLException, IOException, ClassNotFoundException {//deletes an asset that doesnt exist, should return false
        assertEquals(market.DeleteAsset("Asset1"),false);
    }
    @Test
    public void RemoveAssetOrgExist() throws SQLException, IOException, ClassNotFoundException {//remove asset from organisation that exists, should return true
        market.AddNewOrganisation("Org1",10000);
        market.AddNewAsset("Asset1","Asset 1 is good");
        market.AddAssetOrg("Asset1","Org1",10);
        assertEquals(market.RemoveAssetFromOrg("Asset1","Org1"),true);
        market.DeleteAsset("Asset1");
        market.RemoveOrganisation("Org1");
    }
    @Test
    public void RemoveOrgAssetNotExist() throws SQLException, IOException, ClassNotFoundException {//remove org assets from an org that doesnt exist
        market.RemoveOrganisation("Org1");
        market.AddNewAsset("Asset1","Asset 1 is good");
        assertEquals(market.RemoveAssetFromOrg("Asset1","Org1"),false);
        market.DeleteAsset("Asset1");
    }
    @Test
    public void OrgPurchasingAssetEnough() throws SQLException, IOException, ClassNotFoundException {//org puchasing an asset with enough money, should return true
        String sql = "INSERT INTO organisations (Organisation_id,Organisation_Name,Credits) VALUES ('200','Org1','10000')";
        dbconn.insert(sql);
        sql = "INSERT INTO assets (Asset_id,Name,Description) VALUES ('200','Asset1','Asset1 is good')";
        dbconn.insert(sql);
        assertEquals(market.NewBuy(200,200,10000,1),true);
        dbconn.query("SELECT trade_id FROM trades WHERE Organisation_id = 200 and Asset_id = 200");
        HashMap<String,String> TradeId = dbconn.Pop();
        market.RemoveTrade("Org1",TradeId.get("Trade_id"));
        market.DeleteAsset("Asset1");
        market.RemoveOrganisation("Org1");
    }
    @Test
    public void OrgPurchasingAssetNotEnough() throws SQLException, IOException, ClassNotFoundException {//org puchasing an asset without enough money, should return false
        String sql = "INSERT INTO organisations (Organisation_id,Organisation_Name,Credits) VALUES ('200','Org1','1000')";
        dbconn.insert(sql);
        sql = "INSERT INTO assets (Asset_id,Name,Description) VALUES ('200','Asset1','Asset1 is good')";
        dbconn.insert(sql);
        assertEquals(market.NewBuy(200,200,10000,1),false);
        market.DeleteAsset("Asset1");
        market.RemoveOrganisation("Org1");
    }
    @Test
    public void OrgSellingAssetEnough() throws SQLException, IOException, ClassNotFoundException {//org selling an asset that they own and have enough to sell
        String sql = "INSERT INTO organisations (Organisation_id,Organisation_Name,Credits) VALUES ('200','Org1','10000')";
        dbconn.insert(sql);
        sql = "INSERT INTO assets (Asset_id,Name,Description) VALUES ('200','Asset1','Asset1 is good')";
        dbconn.insert(sql);
        market.AddAssetOrg("Asset1","Org1",10);
        assertEquals(market.NewSell(200,200,10,10),true);
        sql = "DELETE FROM trades WHERE Organisation_id = 200 and Asset_id = 200";
        dbconn.insert(sql);
        market.RemoveAssetFromOrg("Asset1","Org1");
        market.DeleteAsset("Asset1");
        market.RemoveOrganisation("Org1");
    }
    @Test
    public void OrgSellingAssetNotEnough() throws SQLException, IOException, ClassNotFoundException {//org selling an amount of asset that they dont have, should return false
        String sql = "INSERT INTO organisations (Organisation_id,Organisation_Name,Credits) VALUES ('200','Org1','10000')";
        dbconn.insert(sql);
        sql = "INSERT INTO assets (Asset_id,Name,Description) VALUES ('200','Asset1','Asset1 is good')";
        dbconn.insert(sql);
        market.AddAssetOrg("Asset1","Org1",10);
        assertEquals(market.NewSell(200,200,12,10),false);
        market.RemoveAssetFromOrg("Asset1","Org1");
        market.DeleteAsset("Asset1");
        market.RemoveOrganisation("Org1");
    }
    @Test
    public void RemoveTradeExist() throws SQLException, IOException, ClassNotFoundException {//removes a trade that exists, should return true
        //will only be able
        String sql = "INSERT INTO organisations (Organisation_id,Organisation_Name,Credits) VALUES ('200','Org1','10000')";
        dbconn.insert(sql);
        sql = "INSERT INTO assets (Asset_id,Name,Description) VALUES ('200','Asset1','Asset1 is good')";
        dbconn.insert(sql);
        sql = "INSERT INTO trades (Trade_id, Trade_Type, Asset_id, Organisation_id, Amount, Cost, Post_Date) VALUES ('200','1','200','200','10','10','2021-05-19 14:53:26')";
        dbconn.insert(sql);
        assertEquals(market.RemoveTrade("Org1","200"),true);
        market.RemoveAssetFromOrg("Asset1","Org1");
        market.RemoveOrganisation("Org1");
        market.DeleteAsset("Asset1");
    }
    @Test
    public void ChangeUserOrg() throws SQLException, IOException, ClassNotFoundException {//changing the organisation of a user, should return true and will print out change to console
        String sql = "INSERT INTO organisations (Organisation_id,Organisation_Name,Credits) VALUES ('200','Org1','10000')";
        dbconn.insert(sql);
        sql = "INSERT INTO organisations (Organisation_id,Organisation_Name,Credits) VALUES ('201','Org2','10000')";
        dbconn.insert(sql);
        market.AddNewUser("Nick","Meurant","n10485571@qut.edu.au","User1",1,200,"Password");
        assertEquals(market.ChangeUserOrg("User1","Org2"),true);
        dbconn.query("SELECT * FROM users WHERE Username = 'User1'");
        HashMap<String,String> user = dbconn.Pop();
        System.out.println("The user org has been changed to " + user.get("Organisation_id") + " instead of 200");
        market.RemoveUser("User1");
        market.RemoveOrganisation("Org1");
        market.RemoveOrganisation("Org2");
    }
    @Test
    public void ChangerUserRole() throws SQLException, IOException, ClassNotFoundException {//changes a users role, should return true if called on valid user
        String sql = "INSERT INTO organisations (Organisation_id,Organisation_Name,Credits) VALUES ('200','Org1','10000')";
        dbconn.insert(sql);
        market.AddNewUser("Nick","Meurant","n10485571@qut.edu.au","User1",1,200,"Password");
        assertEquals(market.ChangeUserRole("User1","MEMBER"),true);
        dbconn.query("SELECT * FROM users WHERE Username = 'User1'");
        HashMap<String,String> user = dbconn.Pop();
        System.out.println("The user role has been to " + user.get("Role") + " instead of 1");
        market.RemoveUser("User1");
        market.RemoveOrganisation("Org1");
    }
    @Test
    public void GetAllUsers() throws SQLException, IOException, ClassNotFoundException {//returns all the users that are currently in the users database to an array, will print to console
        //the output will include all the users that are currently in the database at the time aswell
        String sql = "INSERT INTO organisations (Organisation_id,Organisation_Name,Credits) VALUES ('200','Org1','10000')";
        dbconn.insert(sql);
        market.AddNewUser("Nick","Meurant","n10485571@qut.edu.au","User1",1,200,"Password");
        market.AddNewUser("Nick","Meurant","n10485571@qut.edu.au","User2",1,200,"Password");
        market.AddNewUser("Nick","Meurant","n10485571@qut.edu.au","User3",1,200,"Password");
        dbconn.query("SELECT * FROM users");
        ArrayList<HashMap<String,String>> users = dbconn.fullmap();
        for(HashMap<String,String> user : users){
            System.out.println(user.get("Username"));
        }
        market.RemoveUser("user1");
        market.RemoveUser("user2");
        market.RemoveUser("user3");
        market.RemoveOrganisation("Org1");
    }

}
