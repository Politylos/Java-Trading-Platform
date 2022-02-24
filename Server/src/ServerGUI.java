import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.text.ParseException;

/**
 * This GUI will run on the server side, and give IT administrators a way to
 * open and close the server, and tell if it is running currently.
 */
public class ServerGUI extends JFrame implements Runnable {
    Server_Server server;

    //JLabel will indicate whether server is running or not
    private JLabel serverStatusLabel;
    private final String SERVER_OPEN = "Server is open.";
    private final String SERVER_CLOSED = "Server is closed.";
    //JButton to refresh the above label and check if server is running or not
    private final Method refreshMethod = ServerGUI.class.getMethod("refreshPressed");
    private final JButton refreshBtn = button("Refresh", refreshMethod, this, null, 50, 30);
    //JButton to close the connection, also refresh status label
    private final Method closeMethod = ServerGUI.class.getMethod("serverClosePressed");
    private final JButton closeConnectionBtn = button("Close", closeMethod, this, null, 50, 30);
    //JButton to open the connection, also refresh status label
    private final Method openMethod = ServerGUI.class.getMethod("serverOpenPressed");
    private final JButton openConnectionBtn = button("Open", openMethod, this, null, 50, 30);

    public ServerGUI(Server_Server server) throws SQLException, IOException, ParseException, ClassNotFoundException, NoSuchMethodException {
        this.server = server;
        initUI();
    }

    /**
     * initialises the UI, makes all required panels for this GUI.
     */
    private void initUI() {
        Container contentPane = this.getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.add(Box.createVerticalStrut(30));
        contentPane.add(makeServerPanel());
        contentPane.add(Box.createVerticalStrut(30));
        setTitle("Server - Trading Platform");
        setMinimumSize(new Dimension(300, 300));
        pack();
        setVisible(true);
        updateServerStatusLabel();
    }

    /**
     * This makes the panel with all components for the server GUI.
     *
     * @return - JPanel containing the server status label, and server open, close, refresh buttons.
     */
    private JPanel makeServerPanel() {
        JPanel serverPanel = new JPanel();
        GroupLayout layout = new GroupLayout(serverPanel);
        serverPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        serverStatusLabel = new JLabel(serverStatusCheck());

        GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
        //left hand side group
        hGroup.addGroup(layout.createParallelGroup()
                .addComponent(serverStatusLabel)
                .addComponent(closeConnectionBtn));
        //right hand side group
        hGroup.addGroup(layout.createParallelGroup()
                .addComponent(refreshBtn)
                .addComponent(openConnectionBtn));
        layout.setHorizontalGroup(hGroup);

        GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
        //1st row group
        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(serverStatusLabel).addComponent(refreshBtn));
        //2nd row group
        vGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(closeConnectionBtn).addComponent(openConnectionBtn));
        layout.setVerticalGroup(vGroup);
        return serverPanel;
    }

    /**
     * Checks if server is running
     *
     * @return - String indicating whether the server is open or closed
     */
    private String serverStatusCheck() {
        //create new thread here to check server status
        String serverStatus = null;
        if (server.isRunning()) {
            serverStatus = SERVER_OPEN;
        } else if (!server.isRunning()) {
            serverStatus = SERVER_CLOSED;
        }
        return serverStatus;
    }

    /**
     * Updates the serverStatusLabel using the serverStatusCheck method
     */
    public void updateServerStatusLabel() {
        serverStatusLabel.setText(serverStatusCheck());
        if (serverStatusLabel.getText().equals(SERVER_OPEN)) {
            serverStatusLabel.setForeground(new Color(0, 153, 51));
        } else {
            serverStatusLabel.setForeground(Color.RED);
        }
    }

    /**
     * Runs when users press the close server button.
     */
    public void serverClosePressed() throws IOException, NullPointerException {
        server.Shutdown();
        updateServerStatusLabel();
    }

    /**
     * Runs when users press the open server button
     */
    public void serverOpenPressed() throws SQLException, IOException, ParseException, ClassNotFoundException {
        Thread thread = new Thread(() -> {
            try {
                server.run();
                serverStatusCheck();
                updateServerStatusLabel();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        updateServerStatusLabel();
    }

    /**
     * Runs when users press the refresh button
     */
    public void refreshPressed() {
        updateServerStatusLabel();
    }

    /**
     * Button factory with action listener in built
     * @param text    - button text
     * @param perform - method to perform when pressed
     * @param obj     - obj to invoke
     * @param pram    - any params for method
     * @param w       - min width of btn
     * @param h       - min height of btn
     * @return - JButton with ActionListen, performs param method when pressed.
     */
    private JButton button(String text, Method perform, Object obj, Object pram, int w, int h) {
        JButton btn = new JButton(text);
        btn.addActionListener(e -> {
            try {
                perform.invoke(obj);
            } catch (IllegalAccessException | InvocationTargetException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        });
        btn.setMinimumSize(new Dimension(w, h));
        return btn;
    }

    public void run() {
    }
}
