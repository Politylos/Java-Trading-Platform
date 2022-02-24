import java.io.IOException;

public class myapp {
    public static void main(String[] args) throws NoSuchMethodException, IOException, InterruptedException {
        Server_Connector connect = new Server_Connector();
        new GUI(connect);
    }
}
