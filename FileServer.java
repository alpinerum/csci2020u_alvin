import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class FileServer {
    protected static Socket clientSocket           = null;
    protected ServerSocket serverSocket     = null;
    protected server.ClientConnectionHandler[] threads    = null;
    protected int numClients                = 0;
    protected Vector messages               = new Vector();

    public static int SERVER_PORT = 16789;
    public static int MAX_CLIENTS = 25;

    public FileServer() {
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
            threads = new server.ClientConnectionHandler[MAX_CLIENTS];
            while(true) {
                clientSocket = serverSocket.accept();
                System.out.println("Client #"+(numClients+1)+" connected.");
                threads[numClients] = new server.ClientConnectionHandler(clientSocket);
                threads[numClients].start();
                numClients++;
            }
        } catch (IOException e) {
            System.err.println("IOException while creating server connection");
        }
    }
    public static List<String> listAllFiles() {
        File folder = new File("./src/server");
        String[] listFile = new String[20];
        listFile = folder.list();
        //Array to store list of file names
        List<String> fNameList = new ArrayList<>();
        //Iterates over directory to get list of file names
        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {
                //if (listFile[i].isFile()) {
                fNameList.add(listFile[i]);
                //}
            }
        }
        return fNameList;
    }

    public static void sendFile(String file) throws IOException {
        FileInputStream input = new FileInputStream(file);
        File file1 = new File(file);
        //make array size of file length
        byte [] array = new byte [(int)file1.length()];
        BufferedInputStream buffer = new BufferedInputStream(input);
        //read file
        buffer.read(array,0,array.length);
    }

    public static void receiveFile(String file) {
        int Read;
        int curr = 0;
        try {
            FileOutputStream output = new FileOutputStream(file);
            //CharArrayWriter buffer = new CharArrayWriter();
            BufferedOutputStream buffer = new BufferedOutputStream(output);
            //InputStreamReader input = new InputStreamReader();
            InputStream input = clientSocket.getInputStream();
            //char[] b = new char[2048];
            byte[] b = new byte[2048];
            //StringBuffer b = new StringBuffer();
            //reads up to the length of the byte array
            Read = input.read(b, 0, b.length);
            curr = Read;
            //read file first before checking condition
            do {
                Read = input.read(b, curr, (b.length - curr));
                if (Read >= 0) {
                    curr += Read;
                }
            }
            while (Read > -1);
            //write contents to new file
            buffer.write(b, 0, curr);
            //flush to stream
            buffer.flush();
            //close streams once done
            if (output != null) {
                output.close();
            }
            if (buffer != null) {
                buffer.close();
            }
        } catch (IOException e) {
            System.err.println("IOException");
        }
    }
    public static void main(String[] args) {
        FileServer app = new FileServer();
    }
}

