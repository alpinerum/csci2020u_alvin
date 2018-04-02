import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;

public class FileClient extends Application {
    private static Socket socket = null;
    private BufferedReader in = null;
    private PrintWriter networkOut = null;
    private BufferedReader networkIn = null;

    public  static String SERVER_ADDRESS = "localhost";
    public  static int    SERVER_PORT = 16789;

    public FileClient() {
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + SERVER_ADDRESS);
        } catch (IOException e) {
            System.err.println("IOEXception while connecting to server: " + SERVER_ADDRESS);
        }
        if (socket == null) {
            System.err.println("socket is null");
        }
        try {
            networkOut = new PrintWriter(socket.getOutputStream(), true);
            networkIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.err.println("IOEXception while opening a read/write connection");
        }
        in = new BufferedReader(new InputStreamReader(System.in));
    }

//    protected boolean processUserInput() {
//        String input = null;
//
//        // print the menu
//        System.out.println("Commands: ");
//        System.out.println("DIR - Show list of contents in server");
//        System.out.println("UPLOAD 'filename' - uploads file into the server");
//        System.out.println("DOWNLOAD 'filename' - download file from the server");
//        System.out.print("Command> ");
//
//        try {
//            input = in.readLine();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if (input.equals("DIR")) {
//            listAllFiles();
//        } else if (input.contains("UPLOAD")) {
//            uploadFiles();
//        } else if (input.contains("DOWNLOAD")) {
//            downloadFiles();
//        }
//        return true;
//    }
    public static List<String> listAllFiles() {
        File folder = new File("./src/client");
        File[] listFile = folder.listFiles();
        //listFile = folder.list();
        //initialize array list for filenames
        List<String> fNameList = new ArrayList<>();
        //iterate through file list and add all filenames to list
        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {
                //if (listFile[i].isFile()) {
                System.out.println(listFile[i].getName());
                fNameList.add(listFile[i].getName());
                //}
            }
        }
        return fNameList;
    }

    public static void uploadFiles(String file) {
        try {
            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            FileReader reader = new FileReader(file);
            char[] buffer = new char[4096];
            reader.read(buffer);
            //write to file one character at a time
            for (char c: buffer) {
                //output.flush();
                output.write(c);
            }
            //close streams
            reader.close();
            output.close();
        } catch (FileNotFoundException e) {
            System.err.println("File not Found");
        } catch (IOException e) {
            System.err.println("IOException");
        }

    }

    public static void downloadFiles(String file) {
        int Read;
        int curr = 0;
        try {
            FileOutputStream output = new FileOutputStream(file);
            //CharArrayWriter buffer = new CharArrayWriter();
            BufferedOutputStream buffer = new BufferedOutputStream(output);
            //InputStreamReader input = new InputStreamReader();
            InputStream input = socket.getInputStream();
            //char[] b = new char[2048];
            byte[] b = new byte[2048];
            //StringBuffer b = new StringBuffer();
            //reads up to the length of the byte array
            Read = input.read(b,0,b.length);
            curr = Read;
            //read file first before checking condition
            do {
                Read = input.read(b, curr, (b.length-curr));
                if (Read >= 0) {
                    curr += Read;
                }
            }
            while(Read > -1);

            //Write to new file
            buffer.write(b, 0, curr);
            //flush to stream
            buffer.flush();
            //close streams once done
            if (output != null) {output.close();}
            if (buffer != null) {buffer.close();}
        }
        catch (IOException e) {
            System.err.println("IOException");
        }
    }
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader((getClass().getClassLoader().getResource("./ClientInterface.fxml")));
        Parent root = loader.load();
        stage.setTitle("File Sharer v1.0");
        stage.setScene(new Scene(root, 800, 500));
        stage.show();
    }
    public static void main(String[] args) {launch(args);}
}
