import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;


public class GUI {
    //JFrame frame = Frame_setup();
    Market market; //init after user is signed in
    Server_Connector server;
    JLabel orgCreditQtyLabel = new JLabel();
    User user = null; //remove this and place inside the market place function
    //test arrays for testing before hooking up the GUI with the server/db
    //test user type
    private int userType = 1;
    //test user org as a string
    private final String userOrg = "Org 1";
    //test array for marketplace
    //TODO test array, i0=assetName, i1=assetQty, i2=price per unit,
    // i3=orderType(1==buy,2==sell), i4=posted by org, i5=dateposted, i6=tradeID
    private final String[][] testTradesArray = {{"Asset Type 1", "116", "2", "1", "Org 1", "12/12/2021","1"}
            ,{"Asset Type 1", "55", "3", "1", "Org 2", "12/12/2021","8"}
            ,{"Asset Type 1", "200", "1", "2", "Org 3", "12/12/2021","10"}
            ,{"Asset Type 2", "5", "115", "1", "Org 1", "12/12/2021","2"}
            ,{"Asset Type 3", "64", "4", "2", "Org 2", "12/12/2021","3"}
            ,{"Asset Type 4", "2", "1200", "1", "Org 1", "12/12/2021","4"}
            ,{"Asset Type 6", "172", "6", "1", "Org 4", "12/12/2021","5"}
            ,{"Asset Type 7", "23921", "14", "2", "Org 1", "12/12/2021","6"}
            ,{"Asset Type 8", "666", "6", "1", "Org 3", "12/12/2021","7"}
            ,{"Asset Type 10", "872", "5", "1", "Org 1", "12/12/2021","8"}};
    private final int noOfTrades = testTradesArray.length;
    //test price history array
    private final String [][] testPriceHistoryArray = {{"54","20", "23/02/21"},
            {"12","25", "29/05/21"},
            {"82","30", "15/03/21"},
            {"47","35", "01/04/21"},
            {"22","40", "22/05/21"}};
    //test org asset inventory
    private final String[][] testOrgAssetArray = {{"Asset Type 1", "100"}
            ,{"Asset Type 2", "5"}
            ,{"Asset Type 3", "64"}
            ,{"Asset Type 4", "2"}
            ,{"Asset Type 6", "172"}
            ,{"Asset Type 7", "23921"}
            ,{"Asset Type 8", "666"}
            ,{"Asset Type 10", "872"}};

    public GUI(Server_Connector server) throws NoSuchMethodException, InterruptedException {
        this.server = server;
        start_listen();
        Login Login_screen = new Login();
        //CreateAccountGUI createAccountGUI = new CreateAccountGUI();
        //HomePageGUI homePageGUI = new HomePageGUI(user.getRole());
        //PlatformControlGUI platformControlGUI = new PlatformControlGUI();


        /*JFrame frame = new JFrame();

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(720, 420, 10, 30));
        panel.setLayout(new GridLayout(0, 1));

        frame.add(panel, BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);*/
    }
    private void start_listen() throws InterruptedException {
        Thread thread = new Thread(() -> {
            try {
                while(true){
                    Thread.sleep(6000);
                    if (user != null) {
                        if (user.getOrg().getTrades() != null){
                            server.establish();
                            server.send(Server_Code.CHECK_TRADE.getId(),new String[] {user.getOrg().getTrades()});
                            if ((boolean) server.fetch()){
                                JPanel frame = new JPanel();
                                JOptionPane.showMessageDialog(frame, "A trade Order for your Organisation has found a compatable  complteted");
                            }
                        }
                        server.establish();
                        server.send(Server_Code.GET_MARKET.getId(), new String[]{String.valueOf(user.getOrgID())});
                        PackedMarketUser temp = (PackedMarketUser) server.fetch();
                        market = (Market) temp.getMarket();
                        user.OrgUpdate(temp.getorg());
                        market.setUser(user);
                        market.addServer(server);
                        System.out.println(market.getUser().getOrg().getCredits());
                        orgCreditQtyLabel.setText(String.format("Credits: %f",market.user.getOrg().getCredits()));
                        //orgCreditQtyLabel.update(this.getGraphics());
                    }
                    System.out.println("Thread running");
                }
            }  catch (IOException e) {
                JPanel frame = new JPanel();
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Server has unexpectedly been disconnected, please restart and try again later",
                        "SERVER ERROR", JOptionPane.ERROR_MESSAGE);
            }catch (ClassNotFoundException | InterruptedException e){
                JPanel frame = new JPanel();
                JOptionPane.showMessageDialog(frame, "Thread Has unexpectedly stopped",
                        "THREAD ERROR", JOptionPane.ERROR_MESSAGE);
            }

        });
        thread.start();
    }
//    private JFrame Frame_setup(){
//        JFrame frame = Frame_setup("Trading Platform");
//        return frame;
//    }
//    private JFrame Frame_setup(String title){
//        JFrame frame = new JFrame();
//        frame.setSize(720,423);
//        frame.setTitle(title);
//        frame.addWindowListener(new java.awt.event.WindowAdapter() {
//            public void windowClosing(java.awt.event.WindowEvent e) {
//                System.exit(0);
//            }
//        });
//        frame.pack();
//        frame.setVisible(true);
//        return frame;
//    }
    /**
     * Button factory with action listener in built
     *
     * @param text    - button text
     * @param perform - method to perform when pressed
     * @param obj     - obj to invoke
     * @param pram    - any params for method
     * @param w       - min width of btn
     * @param h       - min height of btn
     * @return - JButton with ActionListen, performs param method when pressed.
     */
    private JButton button(String text,Method perform, Object obj, Object pram,int w, int h){
        JButton btn = new JButton(text);
        btn.addActionListener(e -> {
            try {
                perform.invoke(obj);
            } catch (IllegalAccessException | InvocationTargetException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        });
        btn.setMinimumSize(new Dimension(w,h));
        return btn;
    }
    private JTextField TextInput(int Columns){
        JTextField input = new JTextField(Columns);
        return input;
    }
    class TextInput {
        private String input = null;
        private JTextField textField;
        TextInput(){
            textField = new JTextField();
            textField.getDocument().addDocumentListener((new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    input =textField.getText();
                    System.out.println(input);
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    input =textField.getText();
                    System.out.println(input);
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    input =textField.getText();
                    System.out.println(input);
                }
            }));
        }
        public JTextField addfeild(){
            return textField;
        }
        public String var(){
            return input;
        }
    }

    /**
     * The Login GUI class
     */
    public class Login extends JFrame{
        //component fields
        private JTextField usernameTextField;
        private JPasswordField passwordField;
        private Method Submethod = Login.class.getMethod("submitLogin",null);
        private Method ClearFields = Login.class.getMethod("clearFields",null);
        private JButton loginButton = button("Login",Submethod,this,null,50,30);
        private JButton clearButton = button("Clear",ClearFields,this,null,50,30);

        /**
         * constructor inits UI
         * @throws NoSuchMethodException -
         */
        public Login() throws NoSuchMethodException {
            initUI("Login - Trading Platform", 400, 300);
        }

        /**
         * lays out the UI with input panel and button panel. Uses box layout
         * @param title - title of the window
         * @param minWidth - minimum width of the window
         * @param minHeight - minimum height of the window
         */
        private void initUI(String title, int minWidth, int minHeight) {
            Container contentPane = this.getContentPane();
            contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
            contentPane.add(Box.createVerticalStrut(30));
            contentPane.add(makeEntryPanel());
            contentPane.add(Box.createVerticalStrut(30));
            contentPane.add(makeButtonPanel());
            contentPane.add(Box.createVerticalStrut(30));
            setTitle(title);
            setMinimumSize(new Dimension(minWidth, minHeight));
            pack();
            setVisible(true);
        }

        /**
         * JPanel returns the enter username and password textfields
         * uses grouplayout
         * @return - JPanel with input fields
         */
        private JPanel makeEntryPanel() {
            JPanel entryPanel = new JPanel();
            GroupLayout layout = new GroupLayout(entryPanel);
            entryPanel.setLayout(layout);
            layout.setAutoCreateGaps(true);
            layout.setAutoCreateContainerGaps(true);

            JLabel usernameLabel = new JLabel("Username");
            JLabel passwordLabel = new JLabel("Password");
            usernameTextField = new JTextField(20);
            passwordField = new JPasswordField(20);

            GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

            hGroup.addGroup(layout.createParallelGroup()
                    .addComponent(usernameLabel)
                    .addComponent(passwordLabel));
            hGroup.addGroup(layout.createParallelGroup()
                    .addComponent(usernameTextField)
                    .addComponent(passwordField));
            layout.setHorizontalGroup(hGroup);

            GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

            vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(usernameLabel).addComponent(usernameTextField));
            vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(passwordLabel).addComponent(passwordField));
            layout.setVerticalGroup(vGroup);
            return entryPanel;
        }

        /**
         * JPanel uses boxlayout. Contains login and clear buttons
         * @return - JPanel with login and clear buttons
         */
        private JPanel makeButtonPanel() {
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
            buttonPanel.add(Box.createHorizontalStrut(50));
            buttonPanel.add(clearButton);
            buttonPanel.add(Box.createHorizontalStrut(50));
            buttonPanel.add(loginButton);
            return buttonPanel;
        }

        /**
         * Takes the input from login fields and sends them to server to validate login
         * @throws IOException -
         * @throws ClassNotFoundException -
         * @throws NoSuchMethodException -
         */
        public void submitLogin() throws IOException, ClassNotFoundException, NoSuchMethodException, InterruptedException {
            //String username = usernameTextField.getText();
            //String passwordStr = new String(passwordField.getPassword());
            //System.out.println("Username entered: " + username + ", Password entered: " + passwordStr);
            String[] login_info = {usernameTextField.getText(), new String(passwordField.getPassword())};
            try {
                server.establish();
                server.send(10, login_info);
            } catch (IOException e) {
                    JPanel frame = new JPanel();
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "unable to connect to server",
                            "SERVER CONNECTION ERROR", JOptionPane.ERROR_MESSAGE);
            }
            //Socket socket = server.getSocket();
            boolean login = false;
            //InputStream inputstream = socket.getInputStream();
            //ObjectInputStream serial = new ObjectInputStream(inputstream);
            try{
                System.out.println("found input");
                user = (User) server.fetch();
                user.print();
                login = true;

            } catch (Exception e){
                System.out.println("User not found");
                JOptionPane.showMessageDialog(null, "User was unable to be found. Please check your username or password");
            }
            if (login){
                server.establish();
                server.send(Server_Code.GET_MARKET.getId(), new String[] {String.valueOf(user.getOrgID())});
                PackedMarketUser temp  = (PackedMarketUser) server.fetch();
                market = (Market) temp.getMarket();
                market.addServer(server);
                market.setUser(user);
                try{
                    System.out.println("found input");

                    login = true;
                    System.out.println("Got market");
                } catch (Exception e){
                    System.out.println("Market not found");
                    login = false;
                    user = null;
                }
            }
            if (login == true) {
                this.dispose();
                System.out.println(user.getRole());
                HomePageGUI homePageGUI = new HomePageGUI(user.getRole());
            }
            //ObjectInputStream serial = server.get();
            /*if (serial.readObject() != null) {
                System.out.println("Complete");
            } else{
            }*/
        }
        /**
         * clears the username and password fields
         */
        public void clearFields() {
            usernameTextField.setText("");
            passwordField.setText("");
        }

    }

    /**
     * GUI class where admins can create a new account.
     */
    public class CreateAccountGUI extends JFrame {
        //fields for input components
        private Set<String> orgNames = new TreeSet<>();
        private final String[] userTypes = {"Generic User", "Administrator"};
        private JTextField setUsernameTextField;
        private JTextField setPasswordField;
        private JTextField setFNameField;
        private JTextField setLNameField;
        private JTextField setEmailField;
        private JComboBox<String> selectUserTypeCB;
        private JComboBox<String> selectOrganisationCB;
        private final Method confirmMethod = CreateAccountGUI.class.getMethod("confirmNewAccount");
        private final Method cancelMethod = CreateAccountGUI.class.getMethod("cancelNewAccount");
        private final JButton confirmButton = button("Confirm",confirmMethod,this,null,50,30);
        private final JButton cancelButton = button("Cancel",cancelMethod,this,null,50,30);

        /**
         * GUI constructor
         * @throws NoSuchMethodException -
         */
        public CreateAccountGUI() throws NoSuchMethodException {
            //fillComboBox();
            initUI();
        }

        /**
         * Init UI with input and button panel.
         */
        private void initUI() {
            Container contentPane = this.getContentPane();
            contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
            contentPane.add(Box.createVerticalStrut(30));
            contentPane.add(makeInputPanel());
            contentPane.add(Box.createVerticalStrut(30));
            contentPane.add(makeButtonPanel());
            contentPane.add(Box.createVerticalStrut(30));
            setTitle("New User - Trading Platform");
            setMinimumSize(new Dimension(400, 400));
            pack();
            setVisible(true);
        }

        /**
         * This is the Input Panel where an administrator
         * will input all of the new users information.
         * @return new user info input panel
         */
        private JPanel makeInputPanel() {
            JPanel inputPanel = new JPanel();
            GroupLayout layout = new GroupLayout(inputPanel);
            inputPanel.setLayout(layout);

            layout.setAutoCreateGaps(true);
            layout.setAutoCreateContainerGaps(true);

            JLabel setUsernameLabel = new JLabel("Set Username");
            JLabel setPasswordLabel = new JLabel("Set Password");
            JLabel setFNameLabel = new JLabel("Set First Name");
            JLabel setLNameLabel = new JLabel("Set Last Name");
            JLabel setEmailLabel = new JLabel("Set Email");
            JLabel setUserTypeLabel = new JLabel("User Type");
            JLabel setOrganisationLabel = new JLabel("Select Organisation");
            //text/pw fields
            setUsernameTextField = new JTextField(20);
            setPasswordField = new JTextField(20);
            setFNameField = new JTextField(20);
            setLNameField = new JTextField(20);
            setEmailField = new JTextField(20);
            //combo box
            selectUserTypeCB = new JComboBox<>(user_class.array());
            ComboBoxModel<String> model = new DefaultComboBoxModel<>(market.getOrgName());
            selectOrganisationCB = new JComboBox<>();
            selectOrganisationCB.setModel(model);

            GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
            //left side group (Labels)
            hGroup.addGroup(layout.createParallelGroup()
                    .addComponent(setUsernameLabel)
                    .addComponent(setFNameLabel)
                    .addComponent(setLNameLabel)
                    .addComponent(setEmailLabel)
                    .addComponent(setPasswordLabel)
                    .addComponent(setUserTypeLabel)
                    .addComponent(setOrganisationLabel));
            //right side group (input fields)
            hGroup.addGroup(layout.createParallelGroup()
                    .addComponent(setUsernameTextField)
                    .addComponent(setFNameField)
                    .addComponent(setLNameField)
                    .addComponent(setEmailField)
                    .addComponent(setPasswordField)
                    .addComponent(selectUserTypeCB)
                    .addComponent(selectOrganisationCB));
            layout.setHorizontalGroup(hGroup);

            GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
            //set username row
            vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(setUsernameLabel).addComponent(setUsernameTextField));
            //set fname row
            vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(setFNameLabel).addComponent(setFNameField));
            //set lname row
            vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(setLNameLabel).addComponent(setLNameField));
            //set email row
            vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(setEmailLabel).addComponent(setEmailField));
            //set pw row
            vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(setPasswordLabel).addComponent(setPasswordField));
            //set user role row
            vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(setUserTypeLabel).addComponent(selectUserTypeCB));
            //set org row
            vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(setOrganisationLabel).addComponent(selectOrganisationCB));
            layout.setVerticalGroup(vGroup);
            return inputPanel;
        }

        /**
         * Button panel with cancel and confirm buttons
         * @return - JPanel with two buttons
         */
        private JPanel makeButtonPanel() {
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
            buttonPanel.add(Box.createHorizontalStrut(50));
            buttonPanel.add(cancelButton);
            buttonPanel.add(Box.createHorizontalStrut(50));
            buttonPanel.add(confirmButton);
            return buttonPanel;
        }
        /**
         * Sends new account information to server as String array {Username, Password, UserType, Organisation}
         */
        public void confirmNewAccount() throws IOException {
            //checks if any fields are empty
            if (setUsernameTextField.getText().equals("")
                    || setFNameField.getText().equals("")
                    || setLNameField.getText().equals("")
                    || setEmailField.getText().equals("")
                    || setPasswordField.getText().equals("")){
                JOptionPane.showMessageDialog(rootPane, "Please make sure all fields have a value.");
            } else {
                String[] new_account_info = {
                        setUsernameTextField.getText(),
                        setFNameField.getText(),
                        setLNameField.getText(),
                        setEmailField.getText(),
                        setPasswordField.getText(),
                        String.valueOf(user_class.convert(String.valueOf(selectUserTypeCB.getSelectedItem())))
                        ,String.valueOf(market.getOrg().get(String.valueOf(selectOrganisationCB.getSelectedItem())).getId())
                        };
                System.out.println(market.getOrg());
                for (Map.Entry<String, organisation> set :
                        market.getOrg().entrySet()) {
                    System.out.println(set.getKey());
                    System.out.println(set.getValue().getId());
                }
                server.establish();
                System.out.println(new_account_info);
                server.send(Server_Code.ADD_USER.getId(), new_account_info);
            }
        }

        /**
         * method executes when users presses cancel button.
         * Closes current window and opens the HomePage.
         * @throws NoSuchMethodException -
         */
        public void cancelNewAccount() throws NoSuchMethodException {
            this.dispose();
            new HomePageGUI(user.getRole());
        }
    }

    /**
     * Home page; what every user will see after logging in
     * The button panel will change depending on whether the user is an admin or just generic user
     * user type specified by the param int userType
     */
    public class HomePageGUI extends JFrame {
        //Components for marketAccessPanel
        //private final String[] assetList = {"Asset Type 1", "Asset Type 2"};
        private JComboBox<String> selectAssetTypeCB;
        private final Method assetSearchMethod = HomePageGUI.class.getMethod("assetSearch");
        private final JButton assetSearchButton = button("Search",assetSearchMethod,this,null,50,30);
        private final Method viewAssetInfoMethod = HomePageGUI.class.getMethod("viewAssetInfo");
        private final JButton viewAssetInfoButton = button("View Asset Info",viewAssetInfoMethod,this,null,50,30);
        private final Method viewMarketMethod = HomePageGUI.class.getMethod("openMarketPlace");
        private final JButton viewMarketButton = button("View Market", viewMarketMethod,this,null,50,30);
        private final Method newOrderMethod = HomePageGUI.class.getMethod("newOrder");
        private final JButton newOrderButton = button("New Order", newOrderMethod,this,null,50,30);
        //Components for adminButtonPanel where userType == 1
        private final Method userControlMethod = HomePageGUI.class.getMethod("userControl");
        private final JButton userControlButton = button("User Control",userControlMethod,this,null,50,30);
        private final Method orgControlMethod = HomePageGUI.class.getMethod("orgControl");
        private final JButton orgControlButton = button("Org Control",orgControlMethod,this,null,50,30);
        private final Method platformControlMethod = HomePageGUI.class.getMethod("platformControl");
        private final JButton platformControlButton = button("Platform Control",platformControlMethod,this,null,50,30);
        private final Method newUserMethod = HomePageGUI.class.getMethod("newUser");
        private final JButton newUserButton = button("New User",newUserMethod,this,null,50,30);
        //Components for genericUserButtonPanel where userType == 2
        private final Method viewOrgInvMethod = HomePageGUI.class.getMethod("viewOrgAssets");
        private final JButton viewOrgInvButton = button("View Org Assets",viewOrgInvMethod,this,null,50,30);
        private final Method changePWMethod = HomePageGUI.class.getMethod("changePW");
        private final JButton changePWButton = button("Change PW",changePWMethod,this,null,50,30);

        /**
         * HomePage GUI constructor
         * @param role -  the role of the user, either admin or generic user
         * @throws NoSuchMethodException -
         */
        public HomePageGUI(int role) throws NoSuchMethodException {
            userType = role;
            initUI(userType);
        }

        /**
         * initialises the HomePageUI
         * @param userType - the role of the user, either admin or generic user
         */
        private void initUI(int userType) {
            Container contentPane = this.getContentPane();
            contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
            contentPane.add(Box.createVerticalStrut(30));
            contentPane.add(makeInfoPanel());
            contentPane.add(Box.createVerticalStrut(30));
            contentPane.add(makeMarketAccessPanel());
            contentPane.add(Box.createVerticalStrut(30));
            contentPane.add(makeButtonPanel(userType));
            contentPane.add(Box.createVerticalStrut(30));
            setTitle("Home - Trading Platform");
            setMinimumSize(new Dimension(400, 400));
            pack();
            setVisible(true);
        }

        /**
         * The panel displaying logged in users Username, Organisation, and Org Credits
         * @return JPanel with logged in users info
         */
        private JPanel makeInfoPanel() {
            //need to get logged in users Username as string
            JLabel userNameLabel = new JLabel(String.format("Username: %s",market.user.getUsername()));
            //need to get logged in users Org as string
            JLabel usersOrgLabel = new JLabel(String.format("Organisation: %s",market.user.getOrgName()));
            //need to get logged in users Orgs' credit qty as string
            orgCreditQtyLabel = new JLabel(String.format("Credits: %f",market.user.getOrg().getCredits()));

            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.X_AXIS));
            infoPanel.add(Box.createHorizontalStrut(50));
            infoPanel.add(userNameLabel);
            infoPanel.add(Box.createHorizontalStrut(50));
            infoPanel.add(usersOrgLabel);
            infoPanel.add(Box.createHorizontalStrut(50));
            infoPanel.add(orgCreditQtyLabel);
            infoPanel.add(Box.createHorizontalStrut(50));
            return infoPanel;
        }

        /**
         * Gives the user option to view the full market place,
         * search the marketplace for a specific asset,
         * or to create a new market order.
         * @return - JPanel market access panel
         */
        private JPanel makeMarketAccessPanel() {
            JPanel marketAccessPanel = new JPanel();
            GroupLayout layout = new GroupLayout(marketAccessPanel);
            marketAccessPanel.setLayout(layout);

            layout.setAutoCreateGaps(true);
            layout.setAutoCreateContainerGaps(true);

            selectAssetTypeCB = new JComboBox<>(market.getAssetnames());
            JLabel selectAssetLabel = new JLabel("Select asset: ");

            GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
            //left side column
            hGroup.addGroup(layout.createParallelGroup()
                    .addComponent(selectAssetLabel)
                    .addComponent(viewMarketButton));
            //middle column
            hGroup.addGroup(layout.createParallelGroup()
                    .addComponent(selectAssetTypeCB)
                    .addComponent(viewAssetInfoButton));
            //right side column
            hGroup.addGroup(layout.createParallelGroup()
                    .addComponent(assetSearchButton)
                    .addComponent(newOrderButton));
            layout.setHorizontalGroup(hGroup);

            GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
            //first row
            vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(selectAssetLabel)
                    .addComponent(selectAssetTypeCB)
                    .addComponent(assetSearchButton));
            //second row
            vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(viewMarketButton)
                    .addComponent(viewAssetInfoButton)
                    .addComponent(newOrderButton));
            layout.setVerticalGroup(vGroup);
            return marketAccessPanel;
        }

        /**
         * Opens a marketplace view showing only orders of the selected asset type
         * @throws NoSuchMethodException -
         */
        public void assetSearch() throws NoSuchMethodException {
            new MarketPlaceViewGUI((String) selectAssetTypeCB.getSelectedItem());
        }

        /**
         * opens an asset info panel for the currently selected asset
         * @throws NoSuchMethodException - a method is missing or private
         */
        public void viewAssetInfo() throws NoSuchMethodException {
            new ViewAssetInfoGUI((String) selectAssetTypeCB.getSelectedItem());
        }

        /**
         * Opens a marketplace view showing all currently listed trades
         * @throws NoSuchMethodException -
         */
        public void openMarketPlace() throws NoSuchMethodException {
            new MarketPlaceViewGUI();
        }

        /**
         * opens the new order GUI
         */
        public void newOrder(){
            try {
                new NewOrderGUI();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            //new NewOrderGUI();
        }

        /**
         * This panel will vary based on the Role of the user;
         * IF the user is an admin, they will have access to all admin function from here
         * Otherwise, generic users will be able to view their Orgs Asset Inventory, and change their own Password
         * @param userType - 1 == user is admin, 2 == user is generic user
         * @return button panel, either admin or generic user panel
         */
        private JPanel makeButtonPanel(int userType) {
            JPanel buttonPanel = new JPanel();
            //if userType == admin
            if (userType == 1) {
                GroupLayout layout = new GroupLayout(buttonPanel);
                buttonPanel.setLayout(layout);
                layout.setAutoCreateGaps(true);
                layout.setAutoCreateContainerGaps(true);
                GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
                hGroup.addGroup(layout.createParallelGroup()
                        .addComponent(userControlButton)
                        .addComponent(newUserButton));
                hGroup.addGroup(layout.createParallelGroup()
                        .addComponent(orgControlButton)
                        .addComponent(platformControlButton));
                layout.setHorizontalGroup(hGroup);
                GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
                vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(userControlButton).addComponent(orgControlButton));
                vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(newUserButton).addComponent(platformControlButton));
                layout.setVerticalGroup(vGroup);
            //else if userType == generic user
            } else if (userType == 2){
                buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
                buttonPanel.add(Box.createHorizontalStrut(50));
                buttonPanel.add(viewOrgInvButton);
                buttonPanel.add(Box.createHorizontalStrut(50));
                buttonPanel.add(changePWButton);
                buttonPanel.add(Box.createHorizontalStrut(50));
            } else{
                return null;
            }
            return buttonPanel;
        }

        /**
         * Shows the assets for the organisation associated with the logged in User.
         */
        public void viewOrgAssets(){
            new ViewOrgAssetsGUI();
        }

        public void priceHistory(String asset) {
            try {
                this.dispose();
                new PriceHistoryGUI(asset);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        /**
         * Gives a generic user the option to change their password.
         * Opens ChangePWGUI
         */
        public void changePW(){
            try {
                this.dispose();
                new ChangePasswordGUI();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        /**
         * Opens an admin control GUI to edit a users details
         */
        public void userControl(){
            try {
                this.dispose();
                new UserControlGUI();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        /**
         * Opens an admin control GUI to edit an organisation details
         */
        public void orgControl(){
            try {
                this.dispose();
                new OrganisationControlGUI();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        /**
         * Opens an admin control GUI to add a new user to the system
         */
        public void newUser(){
            try {
                this.dispose();
                new CreateAccountGUI();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        /**
         * Opens an admin control GUI to add a new Organisation or Asset type to the system
         */
        public void platformControl(){
            try {
                this.dispose();
                new PlatformControlOptionGUI();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Any logged in user should have access to this and be able to change their own password
     */
    public class ChangePasswordGUI extends JFrame {
        //Fields to hold components
        private JTextField inputOldPassword;
        private JTextField inputNewPassword;
        private JTextField inputConfirmNewPassword;
        private final Method confirmMethod = ChangePasswordGUI.class.getMethod("confirmNewPassword");
        private final Method cancelMethod = ChangePasswordGUI.class.getMethod("cancelNewPassword");
        private final JButton confirmButton = button("Confirm",confirmMethod,this,null,50,30);
        private final JButton cancelButton = button("Cancel",cancelMethod,this,null,50,30);

        /**
         * Initialises the Change Password UI
         * @throws NoSuchMethodException -
         */
        public ChangePasswordGUI() throws NoSuchMethodException {
            initUI();
        }

        /**
         * Creates the UI with Input and Button panels
         */
        private void initUI() {
            Container contentPane = this.getContentPane();
            contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
            contentPane.add(Box.createVerticalStrut(30));
            contentPane.add(makeInputPanel());
            contentPane.add(Box.createVerticalStrut(30));
            contentPane.add(makeButtonPanel());
            contentPane.add(Box.createVerticalStrut(30));
            setTitle("Change Password - Trading Platform");
            setMinimumSize(new Dimension(300, 200));
            pack();
            setVisible(true);
        }

        /**
         * Input panel contains input fields for old password, new password, and confirm new password
         * @return - JPanel
         */
        private JPanel makeInputPanel() {
            JPanel inputPanel = new JPanel();
            GroupLayout layout = new GroupLayout(inputPanel);
            inputPanel.setLayout(layout);

            layout.setAutoCreateGaps(true);
            layout.setAutoCreateContainerGaps(true);

            JLabel oldPasswordLabel = new JLabel("Enter Current Password");
            JLabel newPasswordLabel = new JLabel("Enter New Password");
            JLabel confirmNewPasswordLabel = new JLabel("Confirm New Password");

            inputOldPassword = new JTextField(20);
            inputNewPassword = new JTextField(20);
            inputConfirmNewPassword = new JTextField(20);

            GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

            hGroup.addGroup(layout.createParallelGroup()
                    .addComponent(oldPasswordLabel)
                    .addComponent(newPasswordLabel)
                    .addComponent(confirmNewPasswordLabel));
            hGroup.addGroup(layout.createParallelGroup()
                    .addComponent(inputOldPassword)
                    .addComponent(inputNewPassword)
                    .addComponent(inputConfirmNewPassword));
            layout.setHorizontalGroup(hGroup);

            GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

            vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(oldPasswordLabel).addComponent(inputOldPassword));
            vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(newPasswordLabel).addComponent(inputNewPassword));
            vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(confirmNewPasswordLabel).addComponent(inputConfirmNewPassword));
            layout.setVerticalGroup(vGroup);

            return inputPanel;
        }

        /**
         * Button panel containing cancel and confirm buttons.
         * @return JPanel buttonPanel
         */
        private JPanel makeButtonPanel() {
            //cancel button
            //confirm button (confirm method checks Old PW Matches, New PWs match, and then change to new PW in DB)
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
            buttonPanel.add(Box.createHorizontalStrut(50));
            buttonPanel.add(cancelButton);
            buttonPanel.add(Box.createHorizontalStrut(50));
            buttonPanel.add(confirmButton);
            return buttonPanel;
        }

        /**
         * Executes when user presses confirm button.
         * First, validates that old password input the users current password.
         * Then, checks new password input matches confirm new password.
         * If checks are true, updates the users password to new password input.
         */
        public void confirmNewPassword() throws IOException, ClassNotFoundException {
            if(inputNewPassword.getText().equals(inputConfirmNewPassword.getText())){
                server.establish();
                server.send(Server_Code.CHANGE_PASSWORD.getId(), new String[] {user.getUsername(),inputOldPassword.getText(),inputNewPassword.getText() });
                if ((boolean) server.fetch()){
                    JOptionPane.showMessageDialog(rootPane,"Password updated");
                } else{
                    JOptionPane.showMessageDialog(rootPane,"Password was unable to be updated please check your current password is correct");
                }
            } else {
                JOptionPane.showMessageDialog(rootPane,"New passwords do not match. Please confirm your new password.");
                clearTextFields();
            }
        }

        /**
         * Clears all input fields
         */
        public void clearTextFields(){
            inputOldPassword.setText("");
            inputNewPassword.setText("");
            inputConfirmNewPassword.setText("");
        }

        /**
         * returns the user to the HomepageGUI
         * @throws NoSuchMethodException -
         */
        public void cancelNewPassword() throws NoSuchMethodException{
            this.dispose();
            new HomePageGUI(user.getRole());
            //cancel new password functionalities
        }
    }

    /**
     * Allows Generic User to view the amount of Assets owned by their own Organisation
     */
    public class ViewOrgAssetsGUI extends JFrame {
        //returns how many asset types the Users Org owns.
        private final int noOfAssets = testOrgAssetArray.length;

        /**
         * Constructor inits UI.
         */
        public ViewOrgAssetsGUI() {
            initUI();
        }

        /**
         * Initialises main UI with Asset Info Panel.
         * Title gets the Users Org Name as a variable
         */
        private void initUI() {
            Container contentPane = this.getContentPane();
            contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
            contentPane.add(Box.createVerticalStrut(30));
            contentPane.add(makeAssetInfoPanel());
            contentPane.add(Box.createVerticalStrut(30));
            setTitle("View " + user.getOrgName() + "'s Assets - Trading Platform");
            setMinimumSize(new Dimension(400, 400));
            pack();
            setVisible(true);
        }

        /**
         * gives all different asset types owned by the users Org and their Qty's
         * @return - JScrollPane assetInfoPanel
         */
        private JScrollPane makeAssetInfoPanel() {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            JScrollPane assetInfoPanel = new JScrollPane(panel);
            assetInfoPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            panel.add(Box.createVerticalStrut(15));
            for (Org_Asset asset : market.user.getOrg().getAssets()) {
                panel.add(Box.createVerticalStrut(5));
                JPanel sub = new JPanel();
                sub.add(Box.createHorizontalStrut(10));
                sub.add(new JLabel(asset.name() + " - Qty: " + asset.getAmount()));
                sub.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));
                panel.add(sub);

            }
            panel.add(Box.createVerticalStrut(15));
            assetInfoPanel.setMinimumSize(new Dimension(200, 150));
            assetInfoPanel.setPreferredSize(new Dimension(300, 250));
            assetInfoPanel.setMaximumSize(new Dimension(300, 400));
            return assetInfoPanel;
        }
    }

    /**
     * The GUI class allowing users to create a new order in the marketplace
     */
    public class NewOrderGUI extends JFrame {
        private JComboBox<String> orderTypeOptionCB = new JComboBox<>(Trade_Type.array());
        private JComboBox<String> assetTypeOptionCB = new JComboBox<>(market.getAssetnames());
        private final Method confirmMethod = NewOrderGUI.class.getMethod("confirmNewOrder");
        private final Method cancelMethod = NewOrderGUI.class.getMethod("cancelNewOrder");
        private final JButton confirmButton = button("Confirm",confirmMethod,this,null,50,30);
        private final JButton cancelButton = button("Cancel",cancelMethod,this,null,50,30);
        private JSpinner Qty;
        private JSpinner pPerU;

        /**
         * Constructor Initialises the UI
         * @throws NoSuchMethodException -
         */
        public NewOrderGUI() throws NoSuchMethodException {
            initUI();
        }

        /**
         * Confirms the new order, if the users Org has enough credits/assets selected.
         * @throws NoSuchMethodException -
         */
        public void confirmNewOrder() throws NoSuchMethodException, IOException, ClassNotFoundException {
            String asset_name = String.valueOf(assetTypeOptionCB.getSelectedItem());
            Trade_Type type = Trade_Type.getType(String.valueOf(orderTypeOptionCB.getSelectedItem()));
            int amount = (Integer) Qty.getValue();
            Double cost = Double.valueOf((Integer) pPerU.getValue());
            System.out.println(amount);
            System.out.println(cost);
            boolean complete =  market.Trade(asset_name,cost,amount,type);
            if (complete){
                JOptionPane.showMessageDialog(rootPane, "Trade has been placed successfully");
            } else{
                JOptionPane.showMessageDialog(rootPane, "we have been unable to place your trade");
            }
        }

        /**
         * closes the new order window
         * @throws NoSuchMethodException -
         */
        public void cancelNewOrder() throws NoSuchMethodException{
            this.dispose();
        }

        /**
         * initialises the UI with Input and Button panels
         */
        private void initUI() {
            Container contentPane = this.getContentPane();
            contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
            contentPane.add(Box.createVerticalStrut(30));
            contentPane.add(makeInputPanel());
            contentPane.add(Box.createVerticalStrut(30));
            contentPane.add(makeButtonPanel());
            contentPane.add(Box.createVerticalStrut(30));
            setTitle("New Order - Trading Platform");
            setMinimumSize(new Dimension(400, 400));
            pack();
            setVisible(true);
        }

        /**
         * Panel containing input for creating a new order;
         * Select order type from a ComboBox
         * Select asset type from a ComboBox
         * Select asset Qty from Spinner
         * Select price per unit from Spinner
         * @return JPanel inputPanel
         */
        private JPanel makeInputPanel() {
            JPanel newOrderPanel = new JPanel();
            newOrderPanel.setLayout(new BoxLayout(newOrderPanel, BoxLayout.X_AXIS));
            GroupLayout layout = new GroupLayout(newOrderPanel);
            newOrderPanel.setLayout(layout);
            layout.setAutoCreateGaps(true);
            layout.setAutoCreateContainerGaps(true);

            final String[] orderOptions = new String[]{"PlaceHolder 1", "Placeholder 2"};
            final String[] assetOptions = new String[]{"PlaceHolder 1", "Placeholder 2"};

            orderTypeOptionCB = new JComboBox<>(Trade_Type.array());

            JLabel orderTypeLabel = new JLabel("Order Type");
            JLabel assetTypeLabel = new JLabel("Asset Type");

            newOrderPanel.add(Box.createHorizontalStrut(50));
            newOrderPanel.add(orderTypeOptionCB);
            newOrderPanel.add(Box.createHorizontalStrut(50));

            assetTypeOptionCB = new JComboBox<>(market.getAssetnames());

            newOrderPanel.add(Box.createHorizontalStrut(50));
            newOrderPanel.add(assetTypeOptionCB);
            newOrderPanel.add(Box.createHorizontalStrut(50));

            //Order Type ComboBox
            //Asset Type ComboBox
            JLabel QtyLabel = new JLabel("Quantity");
            Qty = new JSpinner();
            Qty.setBounds(70, 70, 50, 40);
            newOrderPanel.add(Qty);

            JLabel pPerULabel = new JLabel("Price per Unit");
            pPerU = new JSpinner();
            Qty.setBounds(70, 70, 50, 40);
            newOrderPanel.add(pPerU);
            //Qty JSpinner
            //$p/u JSpinner

            GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
            //left column (Labels)
            hGroup.addGroup(layout.createParallelGroup()
                    .addComponent(orderTypeLabel)
                    .addComponent(assetTypeLabel)
                    .addComponent(QtyLabel)
                    .addComponent(pPerULabel));
            //right column (Input fields)
            hGroup.addGroup(layout.createParallelGroup()
                    .addComponent(orderTypeOptionCB)
                    .addComponent(assetTypeOptionCB)
                    .addComponent(Qty)
                    .addComponent(pPerU));
            layout.setHorizontalGroup(hGroup);

            GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
            //order type row
            vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(orderTypeLabel).addComponent(orderTypeOptionCB));
            //asset type row
            vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(assetTypeLabel).addComponent(assetTypeOptionCB));
            //qty select row
            vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(QtyLabel).addComponent(Qty));
            //price per unit row
            vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(pPerULabel).addComponent(pPerU));
            layout.setVerticalGroup(vGroup);

            return newOrderPanel;
        }

        /**
         * Button panel to confirm or cancel the new order
         * @return - JPanel buttonPanel
         */
        private JPanel makeButtonPanel() {
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
            buttonPanel.add(Box.createHorizontalStrut(50));
            buttonPanel.add(cancelButton);
            buttonPanel.add(Box.createHorizontalStrut(50));
            buttonPanel.add(confirmButton);
            return buttonPanel;
        }
    }

    /**
     * This is a GUI class accessible by Admins
     * User is given a choice to create either a new Asset or new Organisation.
     * Once they have selected which to create, the main UI opens with different functions for each.
     */
    public class PlatformControlOptionGUI extends JFrame {
        //fields for the option UI.
        private JComboBox<String> selectOptionCB = new JComboBox<>();
        private final Method optionConfirmMethod = PlatformControlOptionGUI.class.getMethod("optionConfirm", null);
        private final JButton optionConfirmButton = button("Confirm", optionConfirmMethod, this, null, 50, 30);
        //cancel button
        private final Method cancelMethod = PlatformControlOptionGUI.class.getMethod("cancel", null);
        private final JButton cancelButton = button("Cancel", cancelMethod, this, null, 50, 30);

        /**
         * constructor inits UI
         * @throws NoSuchMethodException -
         */
        public PlatformControlOptionGUI() throws NoSuchMethodException {
            initOptionUI();
        }

        /**
         * initialises the UI where the user will select whether to open the New Asset or New Organisation UI
         */
        private void initOptionUI(){
            Container contentPane = this.getContentPane();
            contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
            contentPane.add(Box.createVerticalStrut(30));
            contentPane.add(makeOptionInputPanel());
            contentPane.add(Box.createVerticalStrut(30));
            contentPane.add(makeOptionButtonPanel());
            contentPane.add(Box.createVerticalStrut(30));
            setTitle("Platform Control - Trading Platform");
            setMinimumSize(new Dimension(300, 200));
            pack();
            setVisible(true);
        }

        /**
         * Panel that contains JCombo box
         * where the user can choose to make a new asset or new organisation
         * @return JPanel with choice between new asset or new org
         */
        private JPanel makeOptionInputPanel() {
            JPanel optionInputPanel = new JPanel();
            optionInputPanel.setLayout(new BoxLayout(optionInputPanel, BoxLayout.X_AXIS));
            final String[] platformControlOptions = new String[]{
                    "New Asset Type",
                    "New Organisation",
                    "Delete Asset Type"};
            selectOptionCB = new JComboBox<>(platformControlOptions);
            optionInputPanel.add(Box.createHorizontalStrut(50));
            optionInputPanel.add(selectOptionCB);
            optionInputPanel.add(Box.createHorizontalStrut(50));
            return optionInputPanel;
        }

        /**
         * Button panel for the "Middleman" GUI
         * @return panel containing confirm and cancel buttons
         */
        private JPanel makeOptionButtonPanel() {
            JPanel optionButtonPanel = new JPanel();
            optionButtonPanel.setLayout(new BoxLayout(optionButtonPanel, BoxLayout.X_AXIS));
            optionButtonPanel.add(Box.createHorizontalStrut(50));
            optionButtonPanel.add(cancelButton);
            optionButtonPanel.add(Box.createHorizontalStrut(50));
            optionButtonPanel.add(optionConfirmButton);
            optionButtonPanel.add(Box.createHorizontalStrut(50));
            return optionButtonPanel;
        }

        /**
         * method that gets executed when user clicks the cancel button
         * closes current window and opens the Homepage again.
         * @throws NoSuchMethodException ?
         */
        public void cancel() throws NoSuchMethodException {
            this.dispose();
            new HomePageGUI(user.getRole());
        }

        /**
         * method gets executed when user has selected whether to open the new org or new asset gui
         */
        public void optionConfirm(){
            //int to hold the selected option
            int platformControlOptionInt = selectOptionCB.getSelectedIndex();
            //open the main UI with selected option as param
            try {
                new PlatformControlMainGUI(platformControlOptionInt);
                this.dispose();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Opens after the PlatformControlOptionGUI
     * Will have different components based on if the user selected new asset or new org option
     */
    public class PlatformControlMainGUI extends JFrame {
        //fields for the New Asset UI
        private JTextField newAssetNameField;
        private JTextArea newAssetDescTextArea;
        private final Method newAssetConfirmMethod = PlatformControlMainGUI.class.getMethod("confirmNewAsset");
        private final JButton newAssetConfirmButton = button("Confirm", newAssetConfirmMethod, this, null, 50, 30);
        //fields for the New Organisation UI
        private JTextField newOrgNameField;
        private final Method newOrgConfirmMethod = PlatformControlMainGUI.class.getMethod("confirmNewOrg", null);
        private final JButton newOrgConfirmButton = button("Confirm", newOrgConfirmMethod, this, null, 50, 30);
        //cancel button; returns user to home page.
        private final Method cancelMethod = PlatformControlMainGUI.class.getMethod("cancel", null);
        private final JButton cancelButton = button("Cancel", cancelMethod, this, null, 50, 30);
        //delete asset components
        private final JComboBox<String> assetTypeOptionCB = new JComboBox<>(market.getAssetnames());
        private final Method deleteAssetMethod = PlatformControlMainGUI.class.getMethod("deleteAssetType");
        private final JButton deleteAssetButton = button("Delete Asset", deleteAssetMethod,this,null,50,30);

        /**
         * Constructor inits the main UI
         * @param selectedOption - Tells the GUI whether user selected new asset or new org
         * @throws NoSuchMethodException -
         */
        public PlatformControlMainGUI(int selectedOption) throws NoSuchMethodException {
            initUI(selectedOption);
        }

        /**
         * init the main gui, either new asset or new org based on user selection
         * @param optionSelected - comboBox index where 0 opens new asset gui, 1 opens new org gui
         */
        private void initUI(int optionSelected) {
            //Selected option == new asset type
            if (optionSelected == 0) {
                Container contentPane = this.getContentPane();
                contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
                contentPane.add(Box.createVerticalStrut(30));
                contentPane.add(makeNewAssetPanel());
                contentPane.add(Box.createVerticalStrut(30));
                setTitle("New Asset Type - Trading Platform");
                setMinimumSize(new Dimension(300, 300));
                pack();
                setVisible(true);
                //testing print out
                //System.out.println("Should now open new asset UI");
            //selected option == new organisation
            } else if (optionSelected == 1) {
                Container contentPane = this.getContentPane();
                contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
                contentPane.add(Box.createVerticalStrut(30));
                contentPane.add(makeNewOrgPanel());
                contentPane.add(Box.createVerticalStrut(30));
                setTitle("New Organisation - Trading Platform");
                setMinimumSize(new Dimension(300, 200));
                pack();
                setVisible(true);
                //testing print out
                //System.out.println("Should now open new org UI");
            //selected option == delete asset type
            } else if (optionSelected == 2) {
                Container contentPane = this.getContentPane();
                contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
                contentPane.add(Box.createVerticalStrut(30));
                contentPane.add(makeDeleteAssetPanel());
                contentPane.add(Box.createVerticalStrut(30));
                setTitle("Delete Asset Type - Trading Platform");
                setMinimumSize(new Dimension(200, 200));
                pack();
                setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "Error: Invalid Option Selected (somehow)");
            }
        }

        /**
         * Shows if user selected delete asset type
         * provides a combo box to select which asset to delete
         * and a button to confirm deletion of the selected asset type
         * Deletes all instances of the selected asset
         * @return - JPanel deleteAssetPanel
         */
        private JPanel makeDeleteAssetPanel() {
            JPanel deleteAssetPanel = new JPanel();
            String DELETE_ASSET_WARNING =
                    "Warning! Deleting an asset type will remove all instances of the selected asset type!";
            deleteAssetPanel.setLayout(new BoxLayout(deleteAssetPanel, BoxLayout.Y_AXIS));
            deleteAssetPanel.add(Box.createHorizontalStrut(50));
            deleteAssetPanel.add(new JLabel(DELETE_ASSET_WARNING));
            deleteAssetPanel.add(Box.createHorizontalStrut(50));
            deleteAssetPanel.add(assetTypeOptionCB);
            deleteAssetPanel.add(Box.createHorizontalStrut(50));
            deleteAssetPanel.add(deleteAssetButton);
            deleteAssetPanel.add(Box.createHorizontalStrut(50));
            deleteAssetPanel.add(cancelButton);
            deleteAssetPanel.add(Box.createHorizontalStrut(50));
            return deleteAssetPanel;
        }

        /**
         * deletes the selected asset type
         */
        public void deleteAssetType() {
            //TODO implement delete asset type
            //String asset_name = String.valueOf(assetTypeOptionCB.getSelectedItem());
        }

        /**
         * If the user selected new asset.
         * Provides input fields for new asset name and description.
         * @return - JPanel newAssetPanel
         */
        private JPanel makeNewAssetPanel() {
            JPanel newAssetPanel = new JPanel();
            GroupLayout layout = new GroupLayout(newAssetPanel);
            newAssetPanel.setLayout(layout);
            layout.setAutoCreateGaps(true);
            layout.setAutoCreateContainerGaps(true);

            JLabel newAssetNameLabel = new JLabel("Asset Name");
            JLabel newAssetDescLabel = new JLabel("Asset Description");
            newAssetNameField = new JTextField(20);
            newAssetDescTextArea = new JTextArea(1, 20);
            newAssetDescTextArea.setLineWrap(true);

            GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
            //left hand side group
            hGroup.addGroup(layout.createParallelGroup()
                    .addComponent(newAssetNameLabel)
                    .addComponent(newAssetDescLabel)
                    .addComponent(cancelButton));
            //right hand side group
            hGroup.addGroup(layout.createParallelGroup()
                    .addComponent(newAssetNameField)
                    .addComponent(newAssetDescTextArea)
                    .addComponent(newAssetConfirmButton));
            layout.setHorizontalGroup(hGroup);

            GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
            //1st row group
            vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(newAssetNameLabel).addComponent(newAssetNameField));
            //2nd row group
            vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(newAssetDescLabel).addComponent(newAssetDescTextArea));
            //3rd row group (buttons)
            vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton).addComponent(newAssetConfirmButton));
            layout.setVerticalGroup(vGroup);
            return newAssetPanel;
        }

        /**
         * If the user selected new Org
         * Provides inputs for new org name.
         * By default, org will have 0 credits.
         * To change, create the new org here, then edit credits using Org Control GUI
         * @return - JPanel newOrgPanel
         */
        private JPanel makeNewOrgPanel() {
            JPanel newOrgPanel = new JPanel();
            GroupLayout layout = new GroupLayout(newOrgPanel);
            newOrgPanel.setLayout(layout);

            layout.setAutoCreateGaps(true);

            layout.setAutoCreateContainerGaps(true);
            JLabel newOrgNameLabel = new JLabel("Organisation Name");
            newOrgNameField = new JTextField(20);

            GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

            //left hand side group
            hGroup.addGroup(layout.createParallelGroup()
                    .addComponent(newOrgNameLabel)
                    .addComponent(cancelButton));
            //right hand side group
            hGroup.addGroup(layout.createParallelGroup()
                    .addComponent(newOrgNameField)
                    .addComponent(newOrgConfirmButton));
            layout.setHorizontalGroup(hGroup);

            GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

            //1st row group
            vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(newOrgNameLabel).addComponent(newOrgNameField));
            //2nd row group (buttons)
            vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton).addComponent(newOrgConfirmButton));
            layout.setVerticalGroup(vGroup);
            return newOrgPanel;
        }

        /**
         * Method which is executed when users pressed confirm on the new asset GUI.
         * Sends the entered asset name and description to the server to be entered into db
         * will not accept new asset if an asset already exists with the same name.
         * Description is optional.
         */
        public void confirmNewAsset() throws IOException, ClassNotFoundException {
            String newAssetNameString;
            if (!newAssetNameField.getText().equals("")){
                newAssetNameString = newAssetNameField.getText();
                String newAssetDescriptionString = newAssetDescTextArea.getText();
                //handle invalid name (i.e. already exists in db) server side and pass back boolean to confirm.
                System.out.println("Should now add asset: " + newAssetNameString + " to db." +
                        "\nWith description: " + newAssetDescriptionString);
                //backend call to add new asset to db
                server.establish();
                server.send(Server_Code.ADD_ASSET.getId(),new String[] {newAssetNameString,newAssetDescriptionString});
                if ((boolean) server.fetch()){
                    JOptionPane.showMessageDialog(rootPane, "Asset has been added to the server");
                } else {
                    JOptionPane.showMessageDialog(rootPane, "Asset has unable to be added");
                }
            } else {
                JOptionPane.showMessageDialog(rootPane, "Please enter a name for this new asset.");
            }

        }

        /**
         * Method which is executed when user presses confirm on the new org GUI.
         * Sends the entered Org name to the server to create a new Org with that name with default 0 credits.
         * Also handles invalid org names (i.e. Org already exists with that name)
         */
        public void confirmNewOrg() throws IOException, ClassNotFoundException {
            String newOrgNameString;
            if (!newOrgNameField.getText().equals("")){
                newOrgNameString = newOrgNameField.getText();
                //handle invalid name (i.e. already exists in db) server side and pass back boolean to confirm.
                System.out.println("Should now add org: " + newOrgNameString + " to db.");
                //backend call to add new asset to db
                server.establish();
                server.send(Server_Code.ADD_OU.getId(),new String[] {newOrgNameString, String.valueOf(100)});
                if ((boolean) server.fetch()){
                    JOptionPane.showMessageDialog(rootPane, "organisation has been added to the server");
                } else {
                    JOptionPane.showMessageDialog(rootPane, "organisation has unable to be added");
                }
            } else {
                JOptionPane.showMessageDialog(rootPane, "Please enter a name for this new organisation.");
            }
        }

        /**
         * Closes the window and returns user to the homepage.
         * @throws NoSuchMethodException -
         */
        public void cancel() throws NoSuchMethodException {
            this.dispose();
            new HomePageGUI(user.getRole());
        }
    }

    /**
     * An admin only GUI to edit an existing Orgs details.
     */
    public class OrganisationControlGUI extends JFrame {
        //components for choosing which org to edit
        private JComboBox<String> orgToEditCB;
        //TODO show actual orgs credits
        ActionListener cbActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String orgSelected = (String) orgToEditCB.getSelectedItem();
                switch (Objects.requireNonNull(orgSelected)) {
                    case "Org 1" -> currentOrgCredits.setText("Org 1 Credits: ##");
                    case "Org 2" -> currentOrgCredits.setText("Org 2 Credits: ###");
                    case "Org 3" -> currentOrgCredits.setText("Org 3 Credits: ####");
                }
            }
        };
        //test arrays
        private final String[] testOrgToEditArray = {"Org 1", "Org 2", "Org 3"};
        private final String[] testAssetArray = {"Asset 1", "Asset 2", "Asset 3"};
        //edit assets fields
        private JComboBox<String> changeAssetCB;
        private JSpinner changeAssetQty;
        private JCheckBox changeAssetCheck;
        //edit credits fields
        private JSpinner changeCreditQty;
        private JLabel currentOrgCredits;
        private JCheckBox changeCreditsCheck;
        //buttons
        private final Method cancelMethod = OrganisationControlGUI.class.getMethod("cancel");
        private final JButton cancelButton = button("Cancel",cancelMethod,this,null,50,30);
        private final Method confirmMethod = OrganisationControlGUI.class.getMethod("confirm");
        private final JButton confirmButton = button("Confirm",confirmMethod,this,null,50,30);
        private final Method deleteOrgMethod = OrganisationControlGUI.class.getMethod("deleteOrg");
        private final JButton deleteOrgButton = button("Delete", deleteOrgMethod, this, null, 50,30);
        /**
         * Constructor initialises UI
         * @throws NoSuchMethodException -
         */
        public OrganisationControlGUI() throws NoSuchMethodException {
            initUI();
        }

        /**
         * Initialises the UI with Input and Button panel
         */
        private void initUI() {
            Container contentPane = this.getContentPane();
            contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
            contentPane.add(Box.createVerticalStrut(30));
            contentPane.add(makeInputPanel());
            contentPane.add(Box.createVerticalStrut(30));
            contentPane.add(makeButtonPanel());
            contentPane.add(Box.createVerticalStrut(30));
            setTitle("Organisation Control - Trading Platform");
            setMinimumSize(new Dimension(400, 250));
            pack();
            setVisible(true);
        }

        /**
         * Gives user input fields to choose the Org to edit,
         * edit an asset type/its quantity,
         * edit the orgs credits/quantity
         * @return - JPanel inputPanel
         */
        private JPanel makeInputPanel() {
            JPanel inputPanel = new JPanel();
            GroupLayout layout = new GroupLayout(inputPanel);
            inputPanel.setLayout(layout);
            layout.setAutoCreateGaps(true);
            layout.setAutoCreateContainerGaps(true);
            //labels
            JLabel orgToEditLabel = new JLabel("Organisation to edit");
            JLabel editAssetLabel = new JLabel("Edit Asset");
            JLabel editCreditsLabel = new JLabel("Edit credits");
            JLabel deleteOrgLabel = new JLabel("Delete Organisation");
            //input components
            //org to edit cb with action listener
            orgToEditCB = new JComboBox<>(market.getOrgName());
            orgToEditCB.addActionListener(cbActionListener);
            //asset to change
            changeAssetCB = new JComboBox<>(market.getAssetnames());
            changeAssetQty = new JSpinner();
            changeAssetCheck = new JCheckBox("");
            changeAssetCheck.setSelected(false);
            //credit edit components
            changeCreditQty = new JSpinner();
            currentOrgCredits = new JLabel("Select an organisation to edit...");
            changeCreditsCheck = new JCheckBox("");
            changeAssetCheck.setSelected(false);

            GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
            //far left side group (check boxes)
            hGroup.addGroup(layout.createParallelGroup()
                    .addComponent(changeAssetCheck)
                    .addComponent(changeCreditsCheck));
            //left side group (labels)
            hGroup.addGroup(layout.createParallelGroup()
                    .addComponent(currentOrgCredits)
                    .addComponent(orgToEditLabel)
                    .addComponent(editAssetLabel)
                    .addComponent(editCreditsLabel)
                    .addComponent(deleteOrgLabel));
            //middle group (inputs)
            hGroup.addGroup(layout.createParallelGroup()
                    .addComponent(orgToEditCB)
                    .addComponent(changeAssetCB)
                    .addComponent(changeCreditQty)
                    .addComponent(deleteOrgButton));
            //right side group (asset qty spinner)
            hGroup.addGroup(layout.createParallelGroup()
                    .addComponent(changeAssetQty));
            layout.setHorizontalGroup(hGroup);

            GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
            //org credits vgroup
            vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(currentOrgCredits));
            //select org to edit vgroup
            vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(orgToEditLabel)
                    .addComponent(orgToEditCB));
            //change asset vgroup
            vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(changeAssetCheck)
                    .addComponent(editAssetLabel)
                    .addComponent(changeAssetCB)
                    .addComponent(changeAssetQty));
            //change credit qty vgroup
            vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(changeCreditsCheck)
                    .addComponent(editCreditsLabel)
                    .addComponent(changeCreditQty));
            //Delete Org button vgroup
            vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(deleteOrgLabel)
                    .addComponent(deleteOrgButton));
            layout.setVerticalGroup(vGroup);
            return inputPanel;
        }

        /**
         * Button panel with cancel and confirm changes to org buttons
         * @return - JPanel buttonPanel
         */
        private JPanel makeButtonPanel() {
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
            buttonPanel.add(Box.createHorizontalStrut(50));
            buttonPanel.add(cancelButton);
            buttonPanel.add(Box.createHorizontalStrut(50));
            buttonPanel.add(confirmButton);
            return buttonPanel;
        }

        /**
         * closes the current window and takes user back to homepage
         */
        public void cancel(){
            try {
                this.dispose();
                new HomePageGUI(user.getRole());
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        /**
         * First, checks which Org has been selected to change the details of
         * then, checks which options the Admin has selected to edit (via checkbox inputs)
         * then, updates the desired details of the org and sends to server to be updated in DB
         */
        public void confirm() throws IOException, ClassNotFoundException {
            //store what org is selected
            String orgToEditString;
            orgToEditString = (String) orgToEditCB.getSelectedItem();
            //variables to store asset edit info
            String assetToChangeString;
            int assetChangeToQtyInt;
            //variables to store credit edit info
            int creditChangeToQtyInt;
            //check if user wants to change Asset qty
            System.out.println("what is selected");
            System.out.println(changeAssetCheck.isSelected());
            System.out.println(changeCreditsCheck.isSelected());
            if (changeAssetCheck.isSelected()){
                assetToChangeString = (String) changeAssetCB.getSelectedItem();
                assetChangeToQtyInt = (int) changeAssetQty.getValue();
                System.out.println("Changed " + orgToEditString
                        + "'s qty of " + assetToChangeString
                        + " to " + assetChangeToQtyInt);
                server.establish();
                server.send(Server_Code.ADD_OU_ASSET.getId(), new String[] {assetToChangeString,orgToEditString, String.valueOf(assetChangeToQtyInt)});
                if ((boolean) server.fetch()){
                    JOptionPane.showMessageDialog(rootPane, "organisation had it assets updated");
                } else {
                    JOptionPane.showMessageDialog(rootPane, "organisation was unable to have it's assets updated");
                }
            } else {
                System.out.println("Did not change " + orgToEditString + "'s assets.");
            }
            if (changeCreditsCheck.isSelected()){
                creditChangeToQtyInt = (int) changeCreditQty.getValue();
                System.out.println("Changed " + orgToEditString
                        + " credits amount to " + creditChangeToQtyInt);
                server.establish();
                server.send(Server_Code.OU_ADD_CREDITS.getId(), new String[] {orgToEditString, String.valueOf(creditChangeToQtyInt)});
                if ((boolean) server.fetch()){
                    JOptionPane.showMessageDialog(rootPane, "organisation had it credits uddated");
                } else {
                    JOptionPane.showMessageDialog(rootPane, "organisation was unable to have it's credits updated");
                }

            } else {
                System.out.println("Did not change " + orgToEditString + "'s credits.");
            }
        }

        /**
         * Deletes the selected organisation
         */
        public void deleteOrg() {
            //TODO Delete Organisation implementation
        }
    }

    /**
     * Admin only GUI to edit a Users detials
     */
    public class UserControlGUI extends JFrame {
        //input components
        private JComboBox<String> userToEditField;
        //change password components
        private JTextField changePWField;
        private JCheckBox changePWCheck;
        //change user type components
        private final String[] userTypeArray = {"Administrator", "Generic User"};
        private JComboBox<String> changeUserTypeCB;
        private JCheckBox changeUserTypeCheck;
        //change user org components
        //testing array for org CB
        private final String[] testUserOrgArray = {"Placeholder Org 1", "Placeholder Org 2"};
        private JComboBox<String> changeUserOrgCB;
        private JCheckBox changeUserOrgCheck;
        //buttons
        private final Method deleteAccountMethod = UserControlGUI.class.getMethod("deleteAccount");
        private final JButton deleteAccountButton = button("Delete",deleteAccountMethod,this,null,50,30);
        private final Method cancelMethod = UserControlGUI.class.getMethod("cancel");
        private final JButton cancelButton = button("Cancel",cancelMethod,this,null,50,30);
        private final Method confirmMethod = UserControlGUI.class.getMethod("confirm");
        private final JButton confirmButton = button("Confirm",confirmMethod,this,null,50,30);

        /**
         * constructor initialises the UI
         * @throws NoSuchMethodException -
         */
        public UserControlGUI() throws NoSuchMethodException, IOException, ClassNotFoundException {
            initUI();
        }

        /**
         * initialises the UI with Input and Button panels
         */
        private void initUI() throws IOException, ClassNotFoundException {
            Container contentPane = this.getContentPane();
            contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
            contentPane.add(Box.createVerticalStrut(30));
            contentPane.add(makeInputPanel());
            contentPane.add(Box.createVerticalStrut(30));
            contentPane.add(makeButtonPanel());
            contentPane.add(Box.createVerticalStrut(30));
            setTitle("User Control - Trading Platform");
            setMinimumSize(new Dimension(400, 400));
            pack();
            setVisible(true);
        }

        /**
         * gives user input fields to enter the username of the user to edit,
         * change the users password,
         * change, the users role (admin or member)
         * change the users organisation they belong to
         * or delete the account
         * @return - JPanel inputPanel
         */
        private JPanel makeInputPanel() throws IOException, ClassNotFoundException {
            JPanel inputPanel = new JPanel();
            GroupLayout layout = new GroupLayout(inputPanel);
            inputPanel.setLayout(layout);
            layout.setAutoCreateGaps(true);
            layout.setAutoCreateContainerGaps(true);
            //Labels
            JLabel userToEditLabel = new JLabel("Username to edit");
            JLabel changePWLabel = new JLabel("Change Password");
            JLabel changeUserTypeLabel = new JLabel("Change User Role");
            JLabel changeUserOrgLabel = new JLabel("Change User Org");
            JLabel deleteAccountLabel = new JLabel("Delete Account");
            //input components
            server.establish();
            server.send(Server_Code.FETCH_USERS.getId());
            String[] usernames =(String[]) server.fetch();
            userToEditField = new JComboBox<>(usernames);;
            changePWField = new JTextField(20);
            changeUserTypeCB = new JComboBox<>(user_class.array());
            changeUserOrgCB = new JComboBox<>(market.getOrgName());
            //check boxes
            changePWCheck = new JCheckBox("Edit?");
            changePWCheck.setSelected(false);
            changeUserTypeCheck = new JCheckBox("Edit?");
            changeUserTypeCheck.setSelected(false);
            changeUserOrgCheck = new JCheckBox("Edit?");
            changeUserOrgCheck.setSelected(false);

            GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
            //left side group (labels)
            hGroup.addGroup(layout.createParallelGroup()
                    .addComponent(userToEditLabel)
                    .addComponent(changePWLabel)
                    .addComponent(changeUserTypeLabel)
                    .addComponent(changeUserOrgLabel)
                    .addComponent(deleteAccountLabel));
            //middle group (inputs)
            hGroup.addGroup(layout.createParallelGroup()
                    .addComponent(userToEditField)
                    .addComponent(changePWField)
                    .addComponent(changeUserTypeCB)
                    .addComponent(changeUserOrgCB)
                    .addComponent(deleteAccountButton));
            //right side group (check boxes)
            hGroup.addGroup(layout.createParallelGroup()
                    .addComponent(changePWCheck)
                    .addComponent(changeUserTypeCheck)
                    .addComponent(changeUserOrgCheck));
            layout.setHorizontalGroup(hGroup);

            GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
            //enter username vgroup
            vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(userToEditLabel)
                    .addComponent(userToEditField));
            //change pw vgroup
            vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(changePWLabel)
                    .addComponent(changePWField)
                    .addComponent(changePWCheck));
            //change user type vgroup
            vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(changeUserTypeLabel)
                    .addComponent(changeUserTypeCB)
                    .addComponent(changeUserTypeCheck));
            //change user org vgroup
            vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(changeUserOrgLabel)
                    .addComponent(changeUserOrgCB)
                    .addComponent(changeUserOrgCheck));
            //delete account vgroup
            vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(deleteAccountLabel)
                    .addComponent(deleteAccountButton));
            layout.setVerticalGroup(vGroup);

            return inputPanel;
        }

        /**
         * Buttons to cancel or confirm changes to the account
         * @return - JPanel buttonPanel
         */
        private JPanel makeButtonPanel() {
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
            buttonPanel.add(Box.createHorizontalStrut(50));
            buttonPanel.add(cancelButton);
            buttonPanel.add(Box.createHorizontalStrut(50));
            buttonPanel.add(confirmButton);
            return buttonPanel;
        }

        /**
         * Checks if the username exists, then removes the User from the DB.
         */
        public void deleteAccount() throws IOException, ClassNotFoundException {
            //do the check if user exists, then delete account if it does
            server.establish();
            server.send(Server_Code.REMOVE_USER.getId(),new String[] {(String)userToEditField.getSelectedItem()});
            if ((boolean) server.fetch()){
                JOptionPane.showMessageDialog(rootPane, "User has been removed");
            } else {
                JOptionPane.showMessageDialog(rootPane, "User was unable to be removed");
            }
        }

        /**
         * Checks if the username entered is valid,
         * Then checks which fields the admin has intended to change (via checkbox inputs)
         * Then updates the details in the DB to those entered on this UI.
         */
        public void confirm() throws IOException, ClassNotFoundException {
            //first, check if the username exists in the DB.
            //if not have dialog box popup and clear username textfield
            //if yes, check which check boxes have been selected.
            //then perform functions for each selected check box using the input users username.
                //check if PW check is selected
                if (changePWCheck.isSelected()){
                    //check if PW field has anything entered
                    if (!changePWField.getText().equals("")){
                        server.establish();
                        server.send(Server_Code.UPDATE_PASSWORD_ADMIN.getId(),new String[] {(String)userToEditField.getSelectedItem(),changePWField.getText()});
                        if ((boolean) server.fetch()){
                            JOptionPane.showMessageDialog(rootPane, "User's passowrd has been updated");
                        } else {
                            JOptionPane.showMessageDialog(rootPane, "User's was unable to be updated");
                        }
                    } else {
                        JOptionPane.showMessageDialog(rootPane,"Please ensure a valid password has been entered.");
                    }
                } else {

                }
                //check if change user type check has been selected
                if (changeUserTypeCheck.isSelected()){
                    server.establish();
                    server.send(Server_Code.UPDATEUSER_TYPE.getId(),new String[] {(String)userToEditField.getSelectedItem(),(String)changeUserTypeCB.getSelectedItem()});
                    if ((boolean) server.fetch()){
                        JOptionPane.showMessageDialog(rootPane, "User's role has been updated");
                    } else {
                        JOptionPane.showMessageDialog(rootPane, "User's role was unable to be updated");
                    }
                } else {

                }
                //check if change user org check has been selected
                if (changeUserOrgCheck.isSelected()){
                    server.establish();
                    server.send(Server_Code.UPDATE_USER_ORG.getId(),new String[] {(String)userToEditField.getSelectedItem(),(String)changeUserOrgCB.getSelectedItem()});
                    if ((boolean) server.fetch()){
                        JOptionPane.showMessageDialog(rootPane, "User's passowrd has been updated");
                    } else {
                        JOptionPane.showMessageDialog(rootPane, "User's was unable to be updated");
                    }
                } else {

                }
        }

        /**
         * closes the current window and takes the user home
         */
        public void cancel(){
            try {
                this.dispose();
                new HomePageGUI(user.getRole());
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        //needs option UI with textfield to type in user name to edit
        //needs to check if username exists before opening main UI
    }

    /**
     * market place view, either have a view of all asset type listings
     * or if a specific asset has been selected view only listings of that asset
     */
    public class MarketPlaceViewGUI extends JFrame {
        /**
         * Constructor without params shows all active trade offers
         */
        public MarketPlaceViewGUI() throws NoSuchMethodException {
            initUI();
        }

        /**
         * Constructor takes param to display only active trade offers of a certain  asset type
         * @param assetName - The selected asset; only shows trades with this assetName.
         */
        public MarketPlaceViewGUI(String assetName) throws NoSuchMethodException {initUI(assetName);}

        /**
         * inits non param UI
         */
        private void initUI() {
            Container contentPane = this.getContentPane();
            contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
            contentPane.add(Box.createVerticalStrut(30));
            contentPane.add(makeMarketPlacePanel());
            contentPane.add(Box.createVerticalStrut(30));
            setTitle("Marketplace View - Trading Platform");
            setMinimumSize(new Dimension(400, 400));
            pack();
            setVisible(true);
        }

        /**
         * inits param UI
         * @param assetName - The selected asset; only shows trades with this assetName.
         */
        private void initUI(String assetName) {
            Container contentPane = this.getContentPane();
            contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
            contentPane.add(Box.createVerticalStrut(30));
            contentPane.add(makeMarketPlacePanel(assetName));
            contentPane.add(Box.createVerticalStrut(30));
            setTitle("Marketplace View: " + assetName + " - Trading Platform");
            setMinimumSize(new Dimension(400, 400));
            pack();
            setVisible(true);
        }

        /**
         * makes market place panel with all current trades
         * @return JScrollPane with all current trades
         */
        private JScrollPane makeMarketPlacePanel() {
            //panel layout stuff
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            JScrollPane marketPlacePanel = new JScrollPane(panel);
            marketPlacePanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            panel.add(Box.createVerticalStrut(15));
            //fields to hold trade info
            String assetName;
            int assetQty;
            double pricePerUnit;
            double totalPrice;
            String orderType = null;
            String postedBy;
            String datePosted;
            int tradeID;
            JButton viewTradeBtn;

            //iteratively adds all trades from the testTradesArray
            HashMap<Integer, Trade> trades = market.getTrades();
            for (Map.Entry<Integer, Trade> set :
                    trades.entrySet()) {
                Trade t = set.getValue();
                assetQty = t.getAmount();
                tradeID = t.getId();
                pricePerUnit = t.getPrice();
                totalPrice = pricePerUnit*assetQty;
                postedBy  = t.getOrgName();
                Trade_Type type = t.getType();
                datePosted = String.format("%s",t.getDate());
                assetName = t.getName();
                viewTradeBtn = new JButton("View Trade");
                viewTradeBtn.addActionListener(new ViewTradeActionListener(tradeID));
                orderType = type.print();
                panel.add(Box.createHorizontalStrut(5));
                panel.add((new JLabel("+-----------------------------+")));
                panel.add(Box.createHorizontalStrut(5));
                JLabel orderL =  new JLabel( orderType);
                orderL.setForeground(Trade_Type.getColour(type));
                panel.add(orderL);
                panel.add(new JLabel("Asset: " + assetName));
                panel.add(Box.createHorizontalStrut(5));
                panel.add(new JLabel("Qty: " + assetQty));
                panel.add(Box.createHorizontalStrut(5));
                panel.add(new JLabel("Price p/unit: " + pricePerUnit));
                panel.add(Box.createHorizontalStrut(5));
                panel.add(new JLabel("Total price: " + totalPrice));
                panel.add(Box.createHorizontalStrut(5));
                panel.add(Box.createHorizontalStrut(5));
                panel.add(new JLabel("Post by: " + postedBy));
                panel.add(Box.createHorizontalStrut(5));
                panel.add(new JLabel("Posted on: " + datePosted));
                panel.add(Box.createHorizontalStrut(5));
                panel.add(viewTradeBtn);
                panel.add(Box.createHorizontalStrut(5));
                panel.add((new JLabel("+-----------------------------+")));
                panel.add(Box.createVerticalStrut(30));
            }
            //more layout stuff
            marketPlacePanel.setMinimumSize(new Dimension(200, 150));
            marketPlacePanel.setPreferredSize(new Dimension(300, 250));
            marketPlacePanel.setMaximumSize(new Dimension(300, 400));
            return marketPlacePanel;
        }

        /**
         * makes the market place panel with only the queried asset type
         * @param assetNameQueried - shows only trades with this asset name
         * @return - JScrollPane with only queried assets
         */
        private JScrollPane makeMarketPlacePanel(String assetNameQueried) {
            //panel layout stuff
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            JScrollPane marketPlacePanel = new JScrollPane(panel);
            marketPlacePanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            panel.add(Box.createVerticalStrut(15));
            //fields to hold trade info
            String assetName;
            int assetQty;
            double pricePerUnit;
            double totalPrice;
            String orderType = null;
            String postedBy;
            String datePosted;
            int tradeID;
            JButton viewTradeBtn;
            //iteratively adds all trades from the testTradesArray
            HashMap<String, Asset> assets = market.getAsset();
            HashMap<Integer, Trade> trades = market.getTrades();
            for (Map.Entry<Integer, Trade> set :
                    trades.entrySet()) {
                Trade t = set.getValue();
                assetName = t.getName();
                if (assetName.equals(assetNameQueried)) {

                    assetQty = t.getAmount();
                    tradeID = t.getId();
                    pricePerUnit = t.getPrice();
                    totalPrice = pricePerUnit * assetQty;
                    postedBy = t.getOrgName();
                    Trade_Type type = t.getType();
                    datePosted = String.format("%s", t.getDate());
                    if (assets.get(assetName) != null) {
                        assetQty = t.getAmount();
                        tradeID = t.getId();
                        pricePerUnit = t.getPrice();
                        totalPrice = pricePerUnit * assetQty;
                        postedBy = t.getOrgName();
                        type = t.getType();
                        datePosted = String.format("%s", t.getDate());
                        assetName = t.getName();
                        viewTradeBtn = new JButton("View Trade");
                        viewTradeBtn.addActionListener(new ViewTradeActionListener(tradeID));
                        datePosted = String.format("%s", t.getDate());
                        assetName = t.getName();
                        panel.add(Box.createHorizontalStrut(5));
                        panel.add((new JLabel("+-----------------------------+")));
                        panel.add(Box.createHorizontalStrut(5));
                        panel.add(new JLabel("Asset: " + assetName));
                        panel.add(Box.createHorizontalStrut(5));
                        panel.add(new JLabel("Qty: " + assetQty));
                        panel.add(Box.createHorizontalStrut(5));
                        panel.add(new JLabel("Price p/unit: " + pricePerUnit));
                        panel.add(Box.createHorizontalStrut(5));
                        panel.add(new JLabel("Total price: " + totalPrice));
                        panel.add(Box.createHorizontalStrut(5));
                        panel.add(new JLabel(orderType));
                        panel.add(Box.createHorizontalStrut(5));
                        panel.add(new JLabel("Post by: " + postedBy));
                        panel.add(Box.createHorizontalStrut(5));
                        panel.add(new JLabel("Posted on: " + datePosted));
                        panel.add(Box.createHorizontalStrut(5));
                        panel.add(viewTradeBtn);
                        panel.add(Box.createHorizontalStrut(5));
                        panel.add((new JLabel("+-----------------------------+")));
                        panel.add(Box.createVerticalStrut(30));
                    }
                }
            }
            //more layout stuff
            marketPlacePanel.setMinimumSize(new Dimension(200, 150));
            marketPlacePanel.setPreferredSize(new Dimension(300, 250));
            marketPlacePanel.setMaximumSize(new Dimension(300, 400));
            return marketPlacePanel;
        }

        /**
         * Opens a new window allowing user to interact with the associated trade.
         * @param tradeID - the ID of the trade the users wants to interact with.
         */
        public void viewTrade(int tradeID){
            System.out.println("Should now open new window with details of tradeID " + tradeID);
            try {
                new MarketOrderViewGUI(tradeID);
            } catch (NoSuchMethodException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        /**
         * Action listener class that creates a button that takes the user to the correct view trade screen
         */
        public class ViewTradeActionListener implements ActionListener{
            private final int tradeID;

            public ViewTradeActionListener(int tradeID) {
                this.tradeID = tradeID;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                viewTrade(tradeID);
            }
        }
    }

    /**
     * Views a specific order, wth ability to interact with the order in various ways.
     */
    public class MarketOrderViewGUI extends JFrame {
        //opens from marketPlaceViewGUI
        //needs to take order ID as a param
        //needs to have different buttons based on if user is part of same Org and or if it's a buy/sell order
        //saves the info of the currently viewed order
        private String assetName;
        private double totalPrice;
        private int assetQty;
        private double pricePerUnit;
        private String orderType = null;
        private String postedBy;
        private String datePosted;
        //final label texts
        private final String SELL_LABEL = "Sell Qty: ";
        private final String BUY_LABEL = "Buy Qty: ";
        //this saves the i value where the correct trade was found in the trade array
        //so the correct value returned to when performing functions on the trade
        private Trade TSaved;
        private JSpinner qtySelectSpinner;
        //btns
        private final Method placeOrderMethod = MarketOrderViewGUI.class.getMethod("placeOrder");
        private final JButton placeOrderBtn = button("Place Order",placeOrderMethod,this,null,50,30);
        private final Method cancelOrderMethod = MarketOrderViewGUI.class.getMethod("cancelOrder");
        private final JButton cancelOrderBtn = button("Cancel Order",cancelOrderMethod,this,null,50,30);
        Class[] hisarg = new Class[] {String.class};
        private  final Method priceHistoryMethod = MarketOrderViewGUI.class.getMethod("priceHistory", hisarg);
        private JButton priceHistoryBtn;
        /**
         * Inits the UI of the specified TradeID
         * @param tradeID - The ID of the Trade to display
         * @throws NoSuchMethodException -
         */
        public MarketOrderViewGUI(int tradeID) throws NoSuchMethodException, ClassNotFoundException {
            initUI(tradeID);
        }

        /**
         * Initialises the UI with OrderInfoPanel and ButtonPanel
         * @param tradeID - the ID of the specified trade
         */
        private void initUI(int tradeID) throws NoSuchMethodException {
            Container contentPane = this.getContentPane();
            contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
            contentPane.add(Box.createVerticalStrut(30));
            contentPane.add(makeOrderInfoPanel(tradeID));
            contentPane.add(Box.createVerticalStrut(30));
            contentPane.add(makeButtonPanel());
            contentPane.add(Box.createVerticalStrut(30));
            setTitle("View trade: " + tradeID +  " - Trading Platform");
            setMinimumSize(new Dimension(400, 400));
            pack();
            setVisible(true);
        }

        /**
         * Shows the Info relating to the trade with the tradeID tradeID
         * @param tradeID - The ID of the trade to show details of
         * @return - JPanel orderInfoPanel
         */
        private JPanel makeOrderInfoPanel(int tradeID) throws NoSuchMethodException {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            HashMap<Integer, Trade> trades = market.getTrades();
            //iteratively adds all trades from the testTradesArray
            Trade trade = null;
            try {
                    trade = trades.get(tradeID);
                    TSaved =trade;
                } catch (Exception e){

                }
                //checks for matching assetNames to assetNameQueried
                if(trade != null){
                    assetName = trade.getName();
                    priceHistoryBtn = button("Price History",priceHistoryMethod,this,assetName,50,30);
                    assetQty = trade.getAmount();
                    pricePerUnit = trade.getPrice();
                    totalPrice = assetQty * pricePerUnit;
                    //tradeID = Integer.parseInt(testTradesArray[i][6]);
                    orderType = trade.getType().print();
                    postedBy = trade.getOrgName();
                    datePosted = String.valueOf(trade.getDate());
                    panel.add(Box.createHorizontalStrut(5));
                    panel.add((new JLabel("+-----------------------------+")));
                    panel.add(Box.createHorizontalStrut(5));
                    panel.add(new JLabel("Asset: " + assetName));
                    panel.add(Box.createHorizontalStrut(5));
                    panel.add(new JLabel("Qty: " + assetQty));
                    panel.add(Box.createHorizontalStrut(5));
                    panel.add(new JLabel("Price p/unit: " + pricePerUnit));
                    panel.add(Box.createHorizontalStrut(5));
                    panel.add(new JLabel("Total price: " + totalPrice));
                    panel.add(Box.createHorizontalStrut(5));
                    panel.add(new JLabel(orderType));
                    panel.add(Box.createHorizontalStrut(5));
                    panel.add(new JLabel("Post by: " + postedBy));
                    panel.add(Box.createHorizontalStrut(5));
                    panel.add(new JLabel("Posted on: " + datePosted));
                    panel.add(Box.createHorizontalStrut(5));
                    panel.add((new JLabel("+-----------------------------+")));
                    panel.add(Box.createVerticalStrut(30));
                }

            panel.setMinimumSize(new Dimension(200, 150));
            panel.setPreferredSize(new Dimension(300, 250));
            panel.setMaximumSize(new Dimension(300, 400));
            return panel;
        }

        /**
         * If the user is part of the same Org as the Org who posted the Trade,
         * User only has the option to cancel the trade.
         * Otherwise, checks whether the trade is a buy or sell order.
         * Then gives the user to either Buy or Sell a custom amount of the trades' asset.
         * @return - JPanel buttonPanel
         */
        private JPanel makeButtonPanel() {
            String labelText = "Something's wrong";
            qtySelectSpinner = new JSpinner();
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
            buttonPanel.add(Box.createHorizontalStrut(50));
            //first we need to check if the trade comes from the users organisation
            if (user.getOrg().getID() == TSaved.getOrg()){
                buttonPanel.add(cancelOrderBtn);
                buttonPanel.add(Box.createHorizontalStrut(50));
            } else {
                labelText =Trade_Type.BUY.print();
                if (TSaved.getType() ==Trade_Type.BUY ){
                    labelText =Trade_Type.SELL.print();
                }
                buttonPanel.add(new JLabel(labelText));
                buttonPanel.add(Box.createHorizontalStrut(50));
                buttonPanel.add(qtySelectSpinner);
                buttonPanel.add(Box.createHorizontalStrut(50));
                buttonPanel.add(placeOrderBtn);
                buttonPanel.add(Box.createHorizontalStrut(50));
            }


            return buttonPanel;
        }

        /**
         * Checks whether the trade is valid
         * (i.e. users' org has enough asset/credits to complete trade)
         * If valid, executes the trade with the input details
         */
        public void placeOrder() throws IOException, ClassNotFoundException {
            server.establish();
            server.send(TSaved.place_offer(user.getOrgID(),(int) qtySelectSpinner.getValue()));
            if ((boolean) server.fetch()){
                JOptionPane.showMessageDialog(null, "Order has been placed");
            } else {
                JOptionPane.showMessageDialog(null, "Order has been unable to be placed");
            }
            //do some error checking to check if there's enough credits/assets
            //then add qty of to new orgs account
            //probably also should add to the price history table here i guess
        }

        /**
         * Removes order from current trades.
         * Returns the assets or credits to the organisation the trade was posted by.
         */
        public void cancelOrder() throws IOException, ClassNotFoundException {
            //return the frozen credits/assets to the org the order originated from
            //check buy/sell order
            //if buy order you want to return the frozen credits
            server.establish();
            server.send(Server_Code.REMOVE_TRADE.getId(), new String[] {user.getOrgName(), String.valueOf(TSaved.getId())});
            if ((boolean) server.fetch()){
                JOptionPane.showMessageDialog(null, "Order has been removed");
            } else {
                JOptionPane.showMessageDialog(null, "Order has been unable to be removed");
            }
        }

        /**
         * Shows the history of all trades of the asset type.
         */
        public void priceHistory(String assest){
            try {
                new PriceHistoryGUI(assest);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * UI to show the history of all trades of a specific asset.
     */
    public class PriceHistoryGUI extends JFrame {
        //fields to hold components.
        //this int was used for testing, got the number of items in the test array.
        //private final int noOfHistory = testPriceHistoryArray.length;
        private final Method graphMethod = PriceHistoryGUI.class.getMethod("graph");
        private final JButton graphButton = button("Graph", graphMethod, this, null,50,50);
        private String assetN;

        /**
         * constructor initialises UI
         * @throws NoSuchMethodException -
         */
        public PriceHistoryGUI(String asset) throws NoSuchMethodException {
            assetN = asset;
            initUI();
        }

        /**
         * initialises the UI with Input and Button panel.
         */
        private void initUI() {
            Container contentPane = this.getContentPane();
            contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
            contentPane.add(Box.createVerticalStrut(30));
            contentPane.add(makeHistoryInfoPanel());
            contentPane.add(Box.createVerticalStrut(30));
            contentPane.add(makeButtonPanel());
            contentPane.add(Box.createVerticalStrut(30));
            setTitle("Price History - Trading Platform");
            setMinimumSize(new Dimension(400, 400));
            pack();
            setVisible(true);
        }

        /**
         * makes the panel containing the info of all previous trades of that asset type
         * with Qty, $per/unit, and date of trade.
         * @return - JScrollPane priceHistoryPanel
         */
        private JScrollPane makeHistoryInfoPanel(){
            System.out.println(assetN);
            System.out.println("looking at history");
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            JScrollPane priceHistoryPanel = new JScrollPane(panel);
            priceHistoryPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            panel.add(Box.createVerticalStrut(15));
            Asset assettype = market.getAsset().get(assetN);
            HashMap<Date, Double> history = assettype.getHistory();
            System.out.println(history.isEmpty());
            for (Map.Entry<Date, Double> set :
                    history.entrySet()) {
                JPanel sub = new JPanel();
                sub.setLayout(new BoxLayout(sub, BoxLayout.Y_AXIS));
                sub.add(Box.createHorizontalStrut(12));
                sub.add(new JLabel("Sold at: " + set.getValue()+"/per unit"));
                sub.add(new JLabel("  on: " + set.getKey()));
                sub.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(0, 64, 128)));
                sub.add(Box.createVerticalStrut(8));
                panel.add(sub);
            }
            priceHistoryPanel.setMinimumSize(new Dimension(200,150));
            priceHistoryPanel.setPreferredSize(new Dimension(300,250));
            priceHistoryPanel.setMaximumSize(new Dimension(300,400));

            return priceHistoryPanel;
        }

        /**
         * Button to show price history graph (under construction)
         * @return - JPanel buttonPanel.
         */
        private JPanel makeButtonPanel(){
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
            buttonPanel.add(Box.createHorizontalStrut(50));
            buttonPanel.add(graphButton);
            return buttonPanel;
        }

        public void graph(){
            System.out.println("Should open price history graph");
        }
        //opens from markerOrderViewGUI
        //shows previous completed orders of the same asset type
    }

    /**
     * Views the info relating to a specific asset type.
     */
    public class ViewAssetInfoGUI extends JFrame {
        //components
        private final String selectedAsset;
        private JTextArea assetDescTextArea;
        private final Method viewPriceHistoryMethod = ViewAssetInfoGUI.class.getMethod("viewPriceHistory");
        private final JButton viewPriceHistoryButton = button("View History",viewPriceHistoryMethod,this,null,50,30);

        /**
         * Constructor inits UI with selectedAsset
         * @param selectedAsset - the selected asset (i.e. the option selected in the CB on the homepage)
         * @throws NoSuchMethodException - a method is private or missing
         */
        public ViewAssetInfoGUI(String selectedAsset) throws NoSuchMethodException {
            this.selectedAsset = selectedAsset;
            initUI();
        }

        /**
         * inits ui view asset info
         */
        private void initUI() {
            Container contentPane = this.getContentPane();
            contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
            contentPane.add(Box.createVerticalStrut(30));
            contentPane.add(makeInfoPanel(selectedAsset));
            contentPane.add(Box.createVerticalStrut(30));
            setTitle("View " + selectedAsset + " info - Trading Platform");
            setMinimumSize(new Dimension(300, 200));
            pack();
            setVisible(true);
        }

        /**
         * Makes a JPanel with the asset name, description
         * average price, and a button to the price history
         * @param selectedAsset - the asset to show info of
         * @return - JPanel assetInfoPanel
         */
        private JPanel makeInfoPanel(String selectedAsset) {
            JPanel assetInfoPanel = new JPanel();
            GroupLayout layout = new GroupLayout(assetInfoPanel);
            assetInfoPanel.setLayout(layout);
            layout.setAutoCreateGaps(true);
            layout.setAutoCreateContainerGaps(true);
            //asset name/desc components
            JLabel assetNameLabel = new JLabel("Asset: " + selectedAsset);
            JLabel assetDescLabel = new JLabel("Asset Description: ");
            //asset description components
            assetDescTextArea = new JTextArea(2, 15);
            assetDescTextArea.setLineWrap(true);

            assetDescTextArea.insert(market.getAsset().get(selectedAsset).getDescription(), 0);
            assetDescTextArea.setWrapStyleWord(true);
            assetDescTextArea.setEnabled(false);
            //asset price avg/history components
            JLabel avgPriceLabel = new JLabel(String.format("Average Price: %f",market.getAsset().get(selectedAsset).getMean()));
            JLabel maxp = new JLabel(String.format("max Price: %f",market.getAsset().get(selectedAsset).getMax()));
            JLabel minp = new JLabel(String.format("Min Price: %f",market.getAsset().get(selectedAsset).getMin()));
            GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
            //left hand side group (Name and Desc)
            hGroup.addGroup(layout.createParallelGroup()
                    .addComponent(assetNameLabel)
                    .addComponent(assetDescLabel)
                    .addComponent(assetDescTextArea));
            //right hand side group (Price stuff
            hGroup.addGroup(layout.createParallelGroup()
                    .addComponent(avgPriceLabel)
                    .addComponent(viewPriceHistoryButton));
            layout.setHorizontalGroup(hGroup);

            GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
            //1st row group
            vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(assetNameLabel)
                    .addComponent(avgPriceLabel));
            //2nd row group
            vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(assetDescLabel));
            //3rd row group (buttons)
            vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(assetDescTextArea)
                    .addComponent(viewPriceHistoryButton));
            layout.setVerticalGroup(vGroup);
            return assetInfoPanel;
        }

        /**
         * opens price history window of selectedAsset
         */
        public void viewPriceHistory(){
            try {
                new PriceHistoryGUI(selectedAsset);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }
}
