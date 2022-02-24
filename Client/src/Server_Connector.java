import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * The Server_cnnector class enables the client to interface with the server
 * this is done through the class opening and closing a object socket which it can than send a receive data through that socket
 */
public class Server_Connector {
    private String IP;
    private int Port;
    private ServerSocket Connection;
    private ObjectOutputStream objstream;
    private Socket socket;
    private ObjectInputStream obj_in_stream;

    /**
     * creates the server connector object and reads the config file to get the required ip and port
     * Connects to the main server via fetching IP and port from cnf file
     * @throws IOException, server is down, server cant be reached
     */
    
    public Server_Connector() throws IOException {
        ConfigRead();
    }

    /**
     * fetches the input object stream from the server and close down all sockets in use
     * @return returns an object from thee input stream
     * @throws ClassNotFoundException, cant cast object
     */
    public Object fetch() throws IOException, ClassNotFoundException {
        InputStream inputstream = socket.getInputStream();
        obj_in_stream = new ObjectInputStream(inputstream);
        Object obj = obj_in_stream.readObject();
        inputstream.close();
        obj_in_stream.close();
        socket.close();
        return obj;

    }

    /**
     * sends a command to the server within the pack class through the objectstream
     * @param command, the command eg command 36 = remove org
     * @throws IOException, server is down, server cant be reached
     */
    public void send(int command) throws IOException {
        send(command, new String[] {null});
    }

    /**
     * sends a command and a string containing information for said command within the pack class through the objectstream
     * @param command, command for server eg command 36 = remove org
     * @param args, string array containing information needed for command
     * @throws IOException, server is down, server cant be reached
     */

    public void send(int command, String[] args) throws IOException {
        System.out.println(command);
        System.out.println(args[0]);
        System.out.println("Trying to send");
        objstream.writeObject(new pack(command,args));

    }
    public void send(pack p) throws IOException {
        objstream.writeObject(p);

    }

    /**
     * Closing the input stream connection from client to server
     * @throws IOException, server is down, server cant be reached
     */
    public void close() throws IOException {
        objstream.close();
        obj_in_stream.close();
        socket.close();
    }

    /**
     * Establishes a connection from client to server
     * @throws IOException, server is down, server cant be reached
     */
    public void establish() throws IOException {
            socket = new Socket(IP,Port);
            OutputStream outstream = socket.getOutputStream();
            objstream = new ObjectOutputStream(outstream);

    }

    /**
     * This Function is used to update the IP adress and Port of the server from the config.cfg file
     * @throws FileNotFoundException file was unable to be found and opened
     */
    public void ConfigRead() throws FileNotFoundException {

        try {
            File ConfigFile = new File("Client/config.cfg");
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
}
