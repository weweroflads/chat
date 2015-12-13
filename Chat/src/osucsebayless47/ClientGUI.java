package osucsebayless47;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/*
 * The Client with its GUI
 */
public class ClientGUI extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    // will first hold "Username:", later on "Enter message"
    private JLabel label;
    // to hold the Username and later on the messages
    private JTextField tf;
    // to hold the server address an the port number
    private JTextField tfServer, tfPort;
    // to Logout and get the list of the users
    private JButton login, logout, whoIsIn;
    // for the chat room
    private JTextArea ta;
    // if it is for connection
    private boolean connected;
    // the Client object
    private Client client;
    // the default port number
    private int defaultPort;
    private String defaultHost;
    private JPasswordField pf;

    // Constructor connection receiving a socket number
    ClientGUI(String host, int port) {

        super("Chat Client");
        this.defaultPort = port;
        this.defaultHost = host;

        // The NorthPanel with:
        JPanel northPanel = new JPanel(new GridLayout(3, 1));
        // the server name anmd the port number
        JPanel serverAndPort = new JPanel(new GridLayout(1, 5, 1, 3));
        // the two JTextField with default value for server address and port number
        this.tfServer = new JTextField(host);
        this.tfPort = new JTextField("" + port);
        this.tfPort.setHorizontalAlignment(SwingConstants.RIGHT);

        serverAndPort.add(new JLabel("Server Address:  "));
        serverAndPort.add(this.tfServer);
        serverAndPort.add(new JLabel("Port Number:  "));
        serverAndPort.add(this.tfPort);
        serverAndPort.add(new JLabel(""));
        // adds the Server an port field to the GUI
        northPanel.add(serverAndPort);

        // the Label and the TextField
        this.label = new JLabel("Enter your username below",
                SwingConstants.CENTER);
        northPanel.add(this.label);
        this.tf = new JTextField("Anonymous");
        this.tf.setBackground(Color.WHITE);
        northPanel.add(this.tf);
        this.add(northPanel, BorderLayout.NORTH);

        // the Label and the TextField
        this.label = new JLabel("Additionally, type your password here",
                SwingConstants.CENTER);
        northPanel.add(this.label);
        this.pf = new JPasswordField("");
        this.pf.setBackground(Color.WHITE);
        northPanel.add(this.pf);
        this.add(northPanel, BorderLayout.NORTH);

        // The CenterPanel which is the chat room
        this.ta = new JTextArea("Welcome to the Chat room\n", 80, 80);
        JPanel centerPanel = new JPanel(new GridLayout(1, 1));
        centerPanel.add(new JScrollPane(this.ta));
        this.ta.setEditable(false);
        this.add(centerPanel, BorderLayout.CENTER);

        // the 3 buttons
        this.login = new JButton("Login");
        this.login.addActionListener(this);
        this.logout = new JButton("Logout");
        this.logout.addActionListener(this);
        this.logout.setEnabled(false); // you have to login before being able to logout
        this.whoIsIn = new JButton("Who is in");
        this.whoIsIn.addActionListener(this);
        this.whoIsIn.setEnabled(false); // you have to login before being able to Who is in

        JPanel southPanel = new JPanel();
        southPanel.add(this.login);
        southPanel.add(this.logout);
        southPanel.add(this.whoIsIn);
        this.add(southPanel, BorderLayout.SOUTH);

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(600, 600);
        this.setVisible(true);
        this.tf.requestFocus();

    }

    // called by the Client to append text in the TextArea
    void append(String str) {
        this.ta.append(str);
        this.ta.setCaretPosition(this.ta.getText().length() - 1);
    }

    // called by the GUI is the connection failed
    // we reset our buttons, label, textfield
    void connectionFailed() {
        this.login.setEnabled(true);
        this.logout.setEnabled(false);
        this.whoIsIn.setEnabled(false);
        this.label.setText("Enter your username below");
        this.tf.setText("Anonymous");
        // reset port number and host name as a construction time
        this.pf.setVisible(true);
        this.pf.setText("");
        this.tfPort.setText("" + this.defaultPort);
        this.tfServer.setText(this.defaultHost);
        // let the user change them
        this.tfServer.setEditable(false);
        this.tfPort.setEditable(false);
        // don't react to a <CR> after the username
        this.tf.removeActionListener(this);
        this.connected = false;
    }

    /*
     * Button or JTextField clicked
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        // if it is the Logout button
        if (o == this.logout) {
            this.client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
            return;
        }
        // if it the who is in button
        if (o == this.whoIsIn) {
            this.client.sendMessage(new ChatMessage(ChatMessage.WHOISIN, ""));
            return;
        }

        // ok it is coming from the JTextField
        if (this.connected) {
            // just have to send the message
            this.client.sendMessage(
                    new ChatMessage(ChatMessage.MESSAGE, this.tf.getText()));
            this.tf.setText("");
            return;
        }

        if (o == this.login) {
            // ok it is a connection request
            String username = this.tf.getText().trim();
            // empty username ignore it
            if (username.length() == 0) {
                return;
            }
            // get the password
            String password = new String(this.pf.getPassword());

            // empty serverAddress ignore it
            String server = this.tfServer.getText().trim();
            if (server.length() == 0) {
                return;
            }
            // empty or invalid port numer, ignore it
            String portNumber = this.tfPort.getText().trim();
            if (portNumber.length() == 0) {
                return;
            }
            int port = 0;
            try {
                port = Integer.parseInt(portNumber);
            } catch (Exception en) {
                return; // nothing I can do if port number is not valid
            }

            // try creating a new Client with GUI
            this.client = new Client(server, port, username, password, this);
            // test if we can start the Client
            if (!this.client.start()) {
                return;
            }
            this.tf.setText("");
            this.pf.setText("");
            this.pf.setVisible(false);
            this.label.setText("Enter your message below");
            this.connected = true;

            // disable login button
            this.login.setEnabled(false);
            // enable the 2 buttons
            this.logout.setEnabled(true);
            this.whoIsIn.setEnabled(true);
            // disable the Server and Port JTextField
            this.tfServer.setEditable(false);
            this.tfPort.setEditable(false);
            // Action listener for when the user enter a message
            this.tf.addActionListener(this);
        }

    }

    // to start the whole thing the server
    public static void main(String[] args) {
        new ClientGUI("localhost", 1500);
    }

}
