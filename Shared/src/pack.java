import java.io.*;

/**
 * The pack class has been made to transfer across comands to the server from the client.
 * with the pack class being used to send a command type and args for the server to do
 */
public class pack implements Externalizable {
    private static final long serialVersionUID = -2856156319315086524L;
    private int command;
    private String[] args = null;

    /**
     * creates the
     * @param type, int server command for the server to preform
     * @param args, string[] list of arguments to pass to the server function in string form
     */
    public pack(int type, String[] args){
        command=type;
        this.args = args;
    }
    public pack(int type){
        command=type;
    }
    public pack(){}
    public int getType(){
        return command;
    }
    public String[] getArgs(){
        return args;
    }

    /**
     *
     * @param out, serializable data to send to server
     * @throws IOException thrown when a server connection cannot be found
     */
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        String argsout=null;
        for (int i=0;i< args.length;i++){
            if (i==0){
                argsout = "";
            }
            argsout+=args[i];
            if (i != args.length-1){
                argsout+="|";
            }
        }
        System.out.println(argsout);
        out.writeUTF(argsout);
        System.out.println(command);
        out.writeInt(command);
    }

    /**
     *
     * @param in serializable line to read when re construing this class
     * @throws IOException wthrown hen the input stream cannot be connected due to server connection errors
     * @throws ClassNotFoundException thrown when the wrong object type is trying to be parsed
     */
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        String argsCompressed = in.readUTF();
        this.command = in.readInt();

        System.out.println(argsCompressed);
        this.args = argsCompressed.split("\\|");
        for (String q : this.args) {
            System.out.println(q);
        }
    }

}
