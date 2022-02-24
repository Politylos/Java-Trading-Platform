import java.awt.*;

/**
 * enum and related functions that make it easier to read what trade type each trade is
 */
public enum Trade_Type {
    BUY(1,"Buy Order"),
    SELL(2, "Sell Order");

    private final int id;
    private final String label;

    Trade_Type(int i, String s) {
        this.id = i;
        this.label =s;
    }
    public String print(){
        return label;
    }
    public int type(){
        return id;
    }

    /**
     * gets an array of trade types to print out to the gui in string format
     * @return String[] all trade type names
     */
    public static String[] array(){
        return new String[]{BUY.print(),SELL.print()};
    }

    /**
     * get the trade type from the corrisponding string
     * @param s string, string to check what trade type it is
     * @return trade_type trade type that that string belongs to
     */
    public static Trade_Type getType(String s){
        if (BUY.print().equals(s)){
            return BUY;
        } else if(SELL.print().equals(s)){
            return SELL;
        }
        return null;
    }

    /**
     * used via the gui to dynamically colour sell and buy requests for easier viewing and reading
     * @param t trade_type, trade type to get the colour for
     * @return Color, colour to set the gui to
     */
    public static Color getColour(Trade_Type t){
        if (t == BUY){
            return new Color(77, 38, 0);
        } else if (t == SELL){
            return new Color(0, 51, 102);
        }
        return Color.BLACK;
    }

    /**
     * used via the gui to dynamically colour sell and buy requests for easier viewing and reading
     * @param t int, id of the trade type that you want the colour of
     * @return Color, colour to set the gui to
     */
    public static Color getColour(int t){
        if (t == BUY.id){
            return new Color(77, 38, 0);
        } else if (t == SELL.id){
            return new Color(0, 51, 102);
        }
        return Color.BLACK;
    }
}
