import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;

public class Parent_Market implements Externalizable {
    protected HashMap<Integer,Trade> trades = new HashMap<Integer,Trade>();
    protected HashMap<String,organisation> orgs = new HashMap<String,organisation>();
    protected HashMap<String, Asset> assets = new HashMap<String, Asset>();
    protected static final long serialVersionUID = -2856333223719086524L;
    /*

     */
    public Parent_Market() {
    }
    public Parent_Market(HashMap<Integer,Trade> t,HashMap<String,organisation> orgs, HashMap<String, Asset> assets ) throws SQLException, IOException, ClassNotFoundException, ParseException {
        this.trades = t;
        this.assets = assets;
        this.orgs = orgs;
    }

    public void fetchTrades() throws SQLException, IOException, ClassNotFoundException, ParseException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(trades);
        out.writeObject(orgs);
        out.writeObject(assets);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        System.out.println("New market");
        trades = (HashMap<Integer,Trade>) in.readObject();
        orgs = (HashMap<String,organisation>) in.readObject();
        assets = (HashMap<String, Asset>) in.readObject();
        System.out.println("Getting");
    }
}
