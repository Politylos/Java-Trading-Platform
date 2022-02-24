import java.io.*;
import java.net.*;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Scanner; // Import the Scanner class to read text files
import java.util.HashMap; // import the HashMap class
import java.util.Set;

/**
 * The class is the implementation of the IO functions that allow the server to interface with the cleint
 * Within this class a server start,
 */
public class Server_Server {
    private int Port;
    private String IP;
    private ServerSocket Ser_Sock;
    private HashMap<String,Socket> Connections =new HashMap<String, Socket>();
    private boolean Open = false;
    private Market global_market;
    public boolean isRunning(){
        return Open;
    }
    public Server_Server() throws IOException, ClassNotFoundException, SQLException, ParseException {
        //global_market = new Market();
        //ConfigRead();
        //Start();
        //Connections = new HashMap<String, Socket>();
        //run();
    }
    /**
     * main Function for server, used to allow the server to accept new connections and pass data back to the client
     * @throws IOException can't conenct to server
     */
    public void closeSocket(Socket socket) throws IOException {
        Connections.remove(socket.toString());
        socket.close();
    }

    /**
     * Run() method reads the config and then start initialises a socket, it is then in a loops reading data that gets sent to it
     * @throws SQLException, when the sql server is unable to pull data or unexpected return type
     * @throws IOException, server is down, server cant be reached
     * @throws ClassNotFoundException, cant cast object
     * @throws ParseException  cant parse object
     */

    public void run() throws IOException, ClassNotFoundException, SQLException, ParseException {
        ConfigRead();
        Start();
        Open = true;
        while (Open){
            Socket socket = Ser_Sock.accept();
            System.out.println(socket.toString());
            Connections.put(socket.toString(),socket);
            InputStream inputstream = socket.getInputStream();
            ObjectInputStream obj_stream = new ObjectInputStream(inputstream);
            pack command =  (pack) obj_stream.readObject();
            PassCommand(command.getType(),command.getArgs(),Connections.get(socket.toString()));
        }
    }

    /**
     * This Function is used to update the IP adress and Port of the server from the config.con file
     * @throws FileNotFoundException ?
     */
    public void ConfigRead() throws FileNotFoundException {

        try {
            File ConfigFile = new File("Server/config.cfg");
            Scanner ConfigRead = new Scanner(ConfigFile);
            while (ConfigRead.hasNextLine()){
                String line = ConfigRead.nextLine();
                /* remove ip to client only*/
                if(line.indexOf("IPAdress") != - 1) {
                    String[] separated = line.split("\"");
                    IP = separated[1];
                    System.out.println(IP);
                    ;
                }
                if(line.indexOf("Port") != - 1) {
                    String[] separated = line.split("\"");
                    Port = Integer.parseInt(separated[1]);
                    System.out.println(Port);
                }
            }
            ConfigRead.close();
        }catch(FileNotFoundException e){
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    /**
     * The Start Function is used to open the server to a socket that is specifed by the Port variable
     * @throws IOException ?
     */
    public void Start() throws IOException {
        Ser_Sock = new ServerSocket(Port);
    }

    /**
     * this method add a new user client connection to the server, allowing for communication bettween the two
     * @throws IOException ?
     */
    public void NewConnection() throws IOException{
        Socket newCon = Ser_Sock.accept();
        InputStream istream = newCon.getInputStream();
        BufferedReader receiveRead = new BufferedReader(new InputStreamReader(istream));
        Connections.put(receiveRead.readLine(),newCon);
    }

    /**
     * This method shuts the server down safely
     * @throws IOException ?
     */
    public void Shutdown() throws IOException {
        Open = false;
        Ser_Sock.close();
        System.out.println("Shutting down Server");
    }

    /**
     * The send function enables the server to pass a serializable object to the cleint, through specifing the object to send and the
     * socket to send to
     * @param toPass an object that is serializable to send to the specified client socket
     * @param socket a Socket object that specifies the client to send the information to
     * @throws IOException
     */
    public void send(Object toPass, Socket socket) throws IOException {
        OutputStream outstream = socket.getOutputStream();
        ObjectOutputStream objstream = new ObjectOutputStream(outstream);
        objstream.writeObject(toPass);
        outstream.close();
        objstream.close();
    }

    /**
     * this function is used bhy the server when a packet object is received. This function
     * takes the packs command and argument, which this function is than able to interrupt
     * into constructing the correct object or passing the args to the relevant function
     * @param command an int variable that specifies the function to preform
     * @param args a list of Strings that are to be passed to the relevant command
     * @param socket A socket object that specifies the client that requested the following command
     * @throws SQLException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void PassCommand(int command, String[] args,Socket socket) throws SQLException, IOException, ClassNotFoundException, ParseException, IndexOutOfBoundsException {
        //login
        if (command == Server_Code.LOGIN.getId()){
            User user = new User();
            user.Login(args[0],args[1]);
            if (user.getId() > 0) {
                send(user, socket);
            } else{
                send(null, socket);
            }
        }
        //set new password
        else if (command == Server_Code.CHANGE_PASSWORD.getId()){
            send(global_market.ChangePassword(args[0],args[1],args[2]), socket);
        }
        //view assets
        else if (command == Server_Code.VIEW_INV.getId()){
        }
        // view market place
        else if (command == Server_Code.GET_MARKET.getId()){
            global_market = new Market();
            User_Organisation org = new User_Organisation(Integer.parseInt(args[0]));
            PackedMarketUser packed = new PackedMarketUser(org,(Parent_Market) global_market);
            send(packed, socket);
        }
        //sell request
        else if (command == Server_Code.SELL.getId()){
            send(global_market.NewSell(Integer.parseInt(args[0]),Integer.parseInt(args[1]),Integer.parseInt(args[2]),Double.parseDouble(args[3])),socket);
        }
        //buy request
        else if (command == Server_Code.BUY.getId()){
            send(global_market.NewBuy(Integer.parseInt(args[0]),Integer.parseInt(args[1]),Integer.parseInt(args[2]),Double.parseDouble(args[3])),socket);
        }
        //view asset
        else if (command == 23){}
        //view trade listing
        else if (command == 24){}
        //add trade
        else if (command == Server_Code.REMOVE_TRADE.getId()){
            send(global_market.RemoveTrade(args[0],args[1]),socket);
        }
        //add user
        else if (command == Server_Code.ADD_USER.getId()){
            System.out.println(String.format("%s,%s,%s,%s,%s,%s,%s",args[0],args[1],args[2],args[3],args[4],args[5],args[6]));
            boolean result = global_market.AddNewUser(args[1],args[2],args[3],args[0], Integer.parseInt(args[5]),Integer.parseInt(args[6]),args[4]);
        }
        //Add org
        else if (command == Server_Code.ADD_OU.getId()){
            send(global_market.AddNewOrganisation(args[0], Double.parseDouble(args[1])),socket);
        }
        //add asset
        else if (command == Server_Code.ADD_ASSET.getId()){
            send(global_market.AddNewAsset(args[0],args[1]),socket);

        }
        //add asset to org
        else if (command == Server_Code.ADD_OU_ASSET.getId()){
            System.out.println("print");
            send(global_market.AddAssetOrg(args[0],args[1],Integer.parseInt(args[2])),socket);
        }
        //add credit to org
        else if (command == Server_Code.OU_ADD_CREDITS.getId()){
            send(global_market.AddMoneyOrganisation(args[0],Integer.parseInt(args[1])),socket);
        }
        //remove user
        else if (command == Server_Code.REMOVE_USER.getId()){
            global_market.RemoveUser(args[0]);
        }
        //remove org
        else if (command == 36){
            global_market.RemoveOrganisation(args[0]);
        }
        //remove asset from org
        else if (command == 37){
            global_market.RemoveAssetFromOrg(args[0],args[1]);
        }
        //Delete asset from database
        else if (command == 38){
            global_market.DeleteAsset(args[0]);
        }
        //get credits
        else if (command == Server_Code.UPDATE_PASSWORD_ADMIN.getId()){
            send(global_market.ChangePassword(args[0],args[1]), socket);
        }
        //get assets
        else if (command == Server_Code.UPDATE_USER_ORG.getId()){
            send(global_market.ChangeUserOrg(args[0],args[1]),socket);
        }
        //get org assets
        else if (command == 42){
            send(global_market.ChangeUserRole(args[0],args[1]),socket);
        }
        //get org names
        else if (command == 43){
            organisation Org = new organisation();
            Set<String> orgNames = Org.getOrgNameList();
            send(orgNames,socket);
        }
        //close connection
        else if (command == Server_Code.FETCH_USERS.getId()){
            send(global_market.getallUsers(),socket);
        }
        //check trade
        else if (command == Server_Code.CHECK_TRADE.getId()){
            String[] strArray = args[0].split("|");
            int intArray[] = new int[strArray.length];
            for (int j = 0; j < intArray.length; j++) {
                intArray[j] = Integer.parseInt(strArray[j]);
            }
            send(global_market.CheckTrades(intArray),socket);
        }
        closeSocket(socket);
    }

}
