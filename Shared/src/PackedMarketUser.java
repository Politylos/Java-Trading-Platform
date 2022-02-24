import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.concurrent.RecursiveTask;

/**
 * packed user and market class with an user organisation to return to the user in a single object stream, for quicker updates
 */
public class PackedMarketUser implements Externalizable {
    User_Organisation org;
    Parent_Market market;
    public PackedMarketUser(){}
    public PackedMarketUser(User_Organisation o, Parent_Market m){
        this.org = o;
        this.market = m;

    }
    public User_Organisation getorg(){
        return org;
    }

    public Parent_Market getMarket() {
        return market;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(org);
        out.writeObject(market);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        org = (User_Organisation) in.readObject();
        market = (Parent_Market) in.readObject();

    }
}
