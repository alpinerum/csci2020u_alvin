
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Controller {
    @FXML private ListView<String> clientView;
    @FXML private ListView<String> serverView;
    @FXML private Button downloadButton;
    @FXML private Button uploadButton;
    ObservableList<String> files = FXCollections.observableArrayList();
    ObservableList<String> serverFiles = FXCollections.observableArrayList();
    public void initialize() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ClientInterface.fxml"));
        VBox vbox = new VBox();
        //Load fxml file
        URL path = FileClient.class.getResource("ClientInterface.fxml");
        loader.setController(new Controller());
        File file = new File(path.getFile());
        FileInputStream fxml = new FileInputStream(file.getCanonicalPath());
        vbox = (VBox) loader.load();
        //System.out.println(files == null);
        files.addAll(FileClient.listAllFiles());
        serverFiles.addAll(FileServer.listAllFiles());
        //files.forEach((file) -> { clientView.getItems().add(file);});
        //serverFiles.forEach((file) -> {serverView.getItems().add(file);});
        //clientView.getItems().add(files);
        //serverView.setItems(serverFiles);
        //splitpane for two list views
        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(clientView, serverView);
        clientView.setCellFactory(ComboBoxListCell.forListView(files));
        vbox.getChildren().add(clientView);
        //buttons for upload and download action
        final Button download = new Button();
        download.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    FileClient.downloadFiles("d.txt");
                    FileServer.sendFile("d.txt");
                } catch (IOException e) {
                    System.err.println("IOException");
                }
            }
        });
        final Button upload = new Button();
        upload.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileClient.uploadFiles("c.txt");
                FileServer.receiveFile("c.txt");
            }
        });
    }
}
