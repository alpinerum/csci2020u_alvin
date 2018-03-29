package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;

public class ClientConnectionHandler {
    private Socket socket       = null;
    protected PrintWriter out     = null;
    protected BufferedReader in   = null;

    protected String[] strUserIDs   = {"rfortier", "jdoe"};
    protected String[] strPasswords = {"cs367", "johnny"};

    protected boolean bLoggedIn   = false;
    protected String strUserID    = null;
    protected String strPassword  = null;

    protected Vector messages     = null;
    public ClientConnectionHandler(Socket socket) {
        super();
        this.socket = socket;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.err.println("IOEXception while opening a read/write connection");
        }
    }
    public void start() {

    }
}
