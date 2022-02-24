import java.io.*;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * User class that stores the orginsation that the user is apart of and other imporant facts about the current logged in user like their role and name
 */
public class User implements Externalizable {
    private static final long serialVersionUID = -2856152753719086524L;
    private int Userid =0;
    private User_Organisation Org;
    private String Name;
    private String UserName;
    private int UserRole;

    /**
     * Inti user
     */

    public User(){
    }

    /**
     * get the users role
     * @return int role of user 1= admin, 2=member
     */
    public int getRole() {
        return UserRole;
    }

    /**
     * get the id of the org the user belongs to
     * @return int, org id the user is apart of
     */
    public int getOrgID(){
        return this.Org.getID();
    }

    /**
     * get the name of the org the user is apart of
     * @return String, org name
     */
    public String getOrgName(){return this.Org.getOrgName();}
    public int getId(){
        return this.Userid;
    }

    /**
     * construct a user based of inputs given
     * @param userid int, id of user
     * @param name string, first and last name of user
     * @param uname string, username of user
     * @param role int, user's role in the program
     * @param org User_Organisation class for the org the user is apart of
     */
    public User(int userid, String name, String uname, int role, User_Organisation org){
        this.Userid = userid;
        this.Name = name;
        this.UserName = uname;
        this.UserRole = role;
        this.Org = org;
        System.out.println("Construct");
    }
    public String getUsername(){
        return this.UserName;
    }
    public void OrgUpdate(User_Organisation org){
        this.Org  = org;
    }
    public User_Organisation getOrg(){
        return Org;
    }
    /**
     * passes the users login information to the server, to test against database
     * updates the user class with the correct information if correct
     * @param Username, username entered by user
     * @param password, password for corresponding username
     */
    public void Login(String Username, String password) throws SQLException, IOException, ClassNotFoundException {
        DB DBconn = new DB();
        //checks if the username is in the database
        DBconn.query("SELECT * FROM users WHERE Username = '"+Username+"'");
        DBconn.isempty();
        if (!DBconn.isempty()) {
            DBconn.FirstResult();
            //checks if the hashed password and given password are the same
            System.out.println(BCrypt.checkpw(password,DBconn.getColumn("Password")));
            if (BCrypt.checkpw(password,DBconn.getColumn("Password"))){
                //if correct pass the user information from the database to the class
                UserName = DBconn.getColumn("Username");
                Name = DBconn.getColumn("FirstName") + " "+ DBconn.getColumn("LastName");
                this.Userid = Integer.parseInt(DBconn.getColumn("User_id"));
                this.UserRole =Integer.parseInt(DBconn.getColumn("Role"));
                System.out.println(this.UserRole);
                int org_id = Integer.parseInt(DBconn.getColumn("Organisation_id"));
                System.out.println(org_id);
                DBconn.Close();
                this.Org  = new User_Organisation(org_id);
            }
            DBconn.Close();
        } else{
            this.Userid = 0;
        }
    }


    /**
     * prints out the user object in a readable form
     */
    public void print(){
        System.out.println(String.format("username: %s, Full name %s",this.UserName, this.Name));
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(this.Userid);
        out.writeUTF(this.Name);
        out.writeUTF(this.UserName);
        out.writeInt(this.UserRole);
        System.out.println(this.UserRole);
        out.writeObject(this.Org);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.Userid=in.readInt();
        this.Name=in.readUTF();
        this.UserName=in.readUTF();
        this.UserRole=in.readInt();
        this.Org= (User_Organisation) in.readObject();
    }
}
