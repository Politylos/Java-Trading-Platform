import java.io.IOException;
import java.sql.Connection;
import java.sql.*;
import java.sql.ResultSetMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.*;

/**
 * This class is used to access the database with all commands need to access and get data from the database being present here
 * with this class handeling connections
 * passing sql code to the server
 * fetching the data rows from the db server
 */
public class DB {

    private Connection conn;
    private ResultSet res;
    private ResultSetMetaData resmd;
    private String SQL;
    private String[] Columns;
    private int count;
    private boolean firstpos = true;
    private boolean empty = true;

    /**
     * init the dbmanager class
     * @throws SQLException ?
     * @throws IOException ?
     * @throws ClassNotFoundException ?
     */
    public DB() throws SQLException, IOException, ClassNotFoundException {
        createConnection();
    }

    public  void close() {
        Close();
    }

    public int count() throws SQLException {
        int count =0;
        while(res.next()){
            count++;
        }
        FirstResult();
        return count;
    }
    public boolean next() throws SQLException {
        boolean isnext = res.next();
        res.previous();
        System.out.println((isnext | firstpos) & !empty);
        return (isnext | firstpos) & !empty;
    }

    /**
     * creates a connection to the external database server
     * uses the DBConfig.cfg file to get:<br>
     * host address = jdbc:{server type}://{server address}/{databasename}?usePipelineAuth=false<br>
     * username  = {valid database manager user}<br>
     * password = {corresponding password for username}<br>
     * driver = org.mariadb.jdbc.Driver {for mariadb db only check your own}<br>
     * @throws IOException ?
     * @throws ClassNotFoundException ?
     * @throws SQLException ?
     */
    public void createConnection() throws IOException, ClassNotFoundException, SQLException {
        Properties prop = new Properties();
        String host;
        String username;
        String password;
        String driver;
        try {
            prop.load(new java.io.FileInputStream("DBConfig.cfg"));

            host = prop.getProperty("host").toString();
            username = prop.getProperty("username").toString();
            password = prop.getProperty("password").toString();
            driver = prop.getProperty("driver").toString();
        } catch (IOException e) {
            System.out.println("Unable to find mydb.cfg in " + System.getProperty("user.home") + "\n Please make sure that configuration file created in this folder.");

            host = "Unknown HOST";
            username = "Unknown USER";
            password = "Unknown PASSWORD";
            driver = "Unknown DRIVER";
        }


        Class.forName(driver);

        try{
            Connection connection = DriverManager.getConnection(host, username, password);
            //System.out.println("CONNECTION: " + connection);
            System.out.println("Connected to database");
            conn = connection;
        } catch (Exception ex){
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }



    }
    public void insert(String SQL) throws SQLException {
        Statement statement = conn.createStatement();
        statement.executeUpdate(SQL);
    }
    /**
     * Query is used to pass a sql statement to the connected database, which than saves the resulting fetched data to a local ResultSet called res
     * @param SQL: valid sql string statement
     */
    public void query(String SQL) {
        this.SQL = SQL;
        try {
            Statement statement = conn.createStatement();
            res = statement.executeQuery(SQL);
            //System.out.println(String.format("Full Name %s %s",res.getString(2), res.getString(3)));
            resmd = res.getMetaData();
            this.Columns = ColumnNames();
            empty = isempty();
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }

    }

    /**
     *Query is used to pass a sql statement with the ability to add variables into the SQL statement through passing a list of variables, along with placing '?'
     *  within the SQL string corresponding to where you want the variables inserted.
     * This function than constructs the completed SQL statement and pass it to the database,
     * which than saves the resulting fetched data to a local ResultSet called res
     * @param SQL: valid sql string statement place '?' where you want a variable inserted
     * @param variables: array of variables you want to insert into your SQL statement
     */
    public void query(String SQL,Object[] variables) {
        for (Object var : variables){
            SQL=SQL.replaceFirst("/?",String.valueOf(var));
        }
        this.SQL = SQL;
        System.out.println("New variable line");
        System.out.println(this.SQL);
        try {
            Statement statement = conn.createStatement();
            res = statement.executeQuery(SQL);
            //System.out.println(String.format("Full Name %s %s",res.getString(2), res.getString(3)));
            resmd = res.getMetaData();
            this.Columns = ColumnNames();
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }

    }

    /**
     * This function returns the heading of the columns returnted from the last passed SQL query
     * @return Columns: A String array of the column heading from the current sql query statement
     */
    public String[] Columnsreturn(){
        return Columns;
    }

    /**
     * checks if the query fetched valid data from the database
     * @return Boolean: <b>True</b> - if result is empty / <b>false</b> - if result has data
     * @throws SQLException ?
     */
    public Boolean isempty() throws SQLException {
        BeforeFirstResult();
        count=0;
        while((res.next()) & (count<1)){
            count++;
        }
        System.out.println(count);
        BeforeFirstResult();
        if (count == 0){
            return true;
        }
        return false;
    }
    public int countr(){
        return count;
    }

    /**
     * printns out all rows from the last passed valid SQL query
     * @throws SQLException ?
     */
    public void printresult() throws SQLException {
        try{
            if(!isempty()){
                while(res.next()){
                    for (String Col : Columns){
                        System.out.println(String.format("%s: %s",Col,res.getString(Col)));
                    }
                }
            } else{
                System.out.println("no data");
            }
        } catch(SQLException ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
    }

    /**
     * returns res. Data fetched from SQL query variable
     * @return ResultSet res ?
     */
    public ResultSet result(){
        return res;
    }

    /**
     * getColumn returns the value from the selected column for the current selected row
     * @param name: String of a column name of the current returned data
     * @return String of a selected column from the current selected row
     * @throws SQLException ?
     */
    public String getColumn(String name) throws SQLException {
        return res.getString(name);
    }

    /**
     * getColumn returns the value from the selected column for the current selected row
     * @param name: String of a column name of the current returned data
     * @return int of a selected column from the current selected row
     * @throws SQLException ?
     */
    public int getColumnInt(String name) throws SQLException {
        return res.getInt(name);
    }
    public String[] resultArray() throws SQLException {
        String[] SQlarray = new String[resmd.getColumnCount()];
        for(int i=1; i<= resmd.getColumnCount(); i++){
            SQlarray[i-1]=res.getString(i);
        }
        return SQlarray;
    }

    /**
     * generates the current returned SQL column names
     * @return String[] list of all Column names currently selected
     * @throws SQLException ?
     */
    public String[] ColumnNames() throws SQLException {
        Columns = new String[resmd.getColumnCount()];
        for(int i=1; i<= resmd.getColumnCount(); i++){
            Columns[i-1]=resmd.getColumnName(i);
        }
        return Columns;
    }

    /**
     * creates a hashmap variable for currently selected row.
     * @return HashMap of String, String where the key is the column name and the data is the selected row's column data
     * @throws SQLException ?
     */
    public HashMap<String,String> mapresult() throws SQLException {
        if (res.isBeforeFirst()) {
            FirstResult();
        }
        HashMap<String,String> sqlresult = new HashMap<String,String>();
        for(String c : Columns) {
            sqlresult.put(c,getColumn(c));
        }
        return sqlresult;
    }

    /**
     * creates an arraylist of all rows and columns for the current SQL query
     * @return ArrayList of HashMap of String,String  where the array list holds every row within a HashMap pf String,String where the key is the column name and the data is the corresponding cell
     * @throws SQLException ?
     */
    public ArrayList<HashMap<String,String>> fullmap() throws SQLException {
        res.beforeFirst();
        ArrayList<HashMap<String,String>> sqlarray = new ArrayList<HashMap<String,String>>();
        while(res.next()){
            sqlarray.add(mapresult());
        }
        return sqlarray;
    }

    /**
     * returns the last selected row while also moving onto the next row
     * @return HashMap of String,String,  where the key is the column name and the data is the selected row's column data
     * @throws SQLException ?
     */
    public HashMap<String,String> Pop() throws SQLException {
        HashMap<String,String> last = null;
        try{
            last = mapresult();
            if (next()) {
                res.next();
                firstpos = false;
            }
        } catch (Exception e){
            System.out.println("No data");
        }
        return last;
    }
    public HashMap<String,String> getSingle() throws SQLException {
        if (!isempty()){
            FirstResult();
            return mapresult();
        }
        return null;
    }

    /**
     * creates an arraylist of all all rows and columns without the column names as keys but instead uses an array
     * @return ArrayList of String[]
     * @throws SQLException ?
     */
    public ArrayList<String[]> fullArray() throws SQLException {
        res =  res.getStatement().executeQuery(this.SQL);
        ArrayList<String[]> sqlarray = new ArrayList<String[]>();
        while(res.next()){
            sqlarray.add(resultArray());
        }
        return sqlarray;
    }

    /**
     * resets current row for res (sql query)
     * <b>Important:</b> Use if while loop is being implemented
     * @throws SQLException ?
     */
    public void BeforeFirstResult() throws SQLException {
        res.beforeFirst();
        firstpos=true;
    }
    /**
     * resets to first row for res (sql query)
     * <b>Important:</b> Use only if a while loop isn't being implemented
     * @throws SQLException ?
     */
    public void FirstResult() throws SQLException {
        res.first();
    }


    public void Close(){

        try {
            // Do stuff


        } finally {
            if (res != null) {
                try {
                    res.close();
                } catch (SQLException e) { /* Ignored */}
            }

            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) { /* Ignored */}
            }
        }
    }
}

