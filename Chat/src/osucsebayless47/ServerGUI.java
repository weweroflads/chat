package osucsebayless47;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/*
 * The server as a GUI
 */
public class ServerGUI extends JFrame
        implements ActionListener, WindowListener {

    private static final long serialVersionUID = 1L;
    // the stop and start buttons
    private JButton stopStart;
    // JTextArea for the chat room and the events
    private JTextArea chat, event;
    // The port number
    private JTextField tPortNumber;
    // my server
    private Server server;

    // server constructor that receive the port to listen to for connection as parameter
    ServerGUI(int port) {
        super("Chat Server");
        this.server = null;
        // in the NorthPanel the PortNumber the Start and Stop buttons
        JPanel north = new JPanel();
        north.add(new JLabel("Port number: "));
        this.tPortNumber = new JTextField("  " + port);
        north.add(this.tPortNumber);
        // to stop or start the server, we start with "Start"
        this.stopStart = new JButton("Start");
        this.stopStart.addActionListener(this);
        north.add(this.stopStart);
        this.add(north, BorderLayout.NORTH);

        // the event and chat room
        JPanel center = new JPanel(new GridLayout(2, 1));
        this.chat = new JTextArea(80, 80);
        this.chat.setEditable(false);
        this.appendRoom("Chat room.\n");
        center.add(new JScrollPane(this.chat));
        this.event = new JTextArea(80, 80);
        this.event.setEditable(false);
        this.appendEvent("Events log.\n");
        center.add(new JScrollPane(this.event));
        this.add(center);

        // need to be informed when the user click the close button on the frame
        this.addWindowListener(this);
        this.setSize(400, 600);
        this.setVisible(true);
    }

    // append message to the two JTextArea
    // position at the end
    void appendRoom(String str) {
        this.chat.append(str);
        this.chat.setCaretPosition(this.chat.getText().length() - 1);
    }

    void appendEvent(String str) {
        this.event.append(str);
        this.event.setCaretPosition(this.chat.getText().length() - 1);

    }

    // start or stop where clicked
    @Override
    public void actionPerformed(ActionEvent e) {
        // if running we have to stop
        if (this.server != null) {
            this.server.stop();
            this.server = null;
            this.tPortNumber.setEditable(true);
            this.stopStart.setText("Start");
            return;
        }
        // OK start the server
        int port;
        try {
            port = Integer.parseInt(this.tPortNumber.getText().trim());
        } catch (Exception er) {
            this.appendEvent("Invalid port number");
            return;
        }
        // ceate a new Server
        this.server = new Server(port, this);
        // and start it as a thread
        new ServerRunning().start();
        this.stopStart.setText("Stop");
        this.tPortNumber.setEditable(false);
    }

    // entry point to start the Server
    public static void main(String[] arg) {
        // start server default port 1500
        new ServerGUI(1500);
    }

    /*
     * If the user click the X button to close the application I need to close
     * the connection with the server to free the port
     */
    @Override
    public void windowClosing(WindowEvent e) {
        // if my Server exist
        if (this.server != null) {
            try {
                this.server.stop(); // ask the server to close the conection
            } catch (Exception eClose) {
            }
            this.server = null;
        }
        // dispose the frame
        this.dispose();
        System.exit(0);
    }

    // I can ignore the other WindowListener method
    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    /*
     * A thread to run the Server
     */
    class ServerRunning extends Thread {
        @Override
        public void run() {
            ServerGUI.this.server.start(); // should execute until if fails
            // the server failed
            ServerGUI.this.stopStart.setText("Start");
            ServerGUI.this.tPortNumber.setEditable(true);
            ServerGUI.this.appendEvent("Server crashed\n");
            ServerGUI.this.server = null;
        }
    }

}
