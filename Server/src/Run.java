import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

public class Run {
    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException, ParseException, NoSuchMethodException {
        DB DBconn = new DB();
        Server_Server server = new Server_Server();
        ServerGUI serverGUI = new ServerGUI(server);
    }
}

