import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    public TextField sendTextfield;
    @FXML
    public TextField clientNumText;
    @FXML
    public Button serverButton;
    @FXML
    public Button clientButton;
    @FXML
    public Button themeButton;
    @FXML
    public TextField privateMessage;
    @FXML
    public Button sendPrivButton;
    static Server serverConnection;
    static Client clientConnection;
    @FXML
    ListView<String> listItems, listItems2, activeClients;

    private boolean isDarkTheme = false;
    @FXML
    private AnchorPane background;
    @FXML
    public Text text1, text2, text3;

    @FXML
    public void setServerButton (ActionEvent e) throws IOException{
        System.out.println("server is clicked");
        Stage stage = (Stage) serverButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("serverGui.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();
        stage.setTitle("Server");
        Scene scene = new Scene(root);
        scene.getStylesheets().add("Style.css");
        stage.setScene(scene);
        stage.show();

        serverConnection = new Server(data -> {
            Platform.runLater(()->{
                messageInfo mi2 = (messageInfo) data;
                controller.listItems.getItems().add(mi2.data);
            });
        });

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
    }

    @FXML
    public void setClientButton (ActionEvent e) throws IOException{
        Stage stage2 = (Stage) serverButton.getScene().getWindow();
        FXMLLoader loader2 = new FXMLLoader(getClass().getResource("clientGui.fxml"));
        Parent root = loader2.load();
        Controller controller2 = loader2.getController();
        stage2.setTitle("Client");
        Scene scene2 = new Scene(root);
        scene2.getStylesheets().add("Style.css");
        stage2.setScene(scene2);
        stage2.show();

        System.out.println("client is clicked");

        clientConnection = new Client(data->{
            Platform.runLater(()->{
                messageInfo mi3 = (messageInfo) data;
                if (mi3.data != null) {
                    controller2.listItems2.getItems().add(mi3.data);
                }

                controller2.activeClients.getItems().clear();
                for(int i=0; i< mi3.onlineClients.size(); i++){
                    controller2.activeClients.getItems().add(String.valueOf(mi3.onlineClients.get(i)));
                }
            });
        });
        clientConnection.start();

        stage2.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
    }
    @FXML
    public void sendAll(ActionEvent e) throws IOException{
        messageInfo mi1 = new messageInfo();
        mi1.sendALl = true;
        System.out.println(sendTextfield.getText());
        mi1.data = sendTextfield.getText();
        clientConnection.send(mi1);
        sendTextfield.clear();
        mi1.sendALl = false;
    }

    @FXML
    public void setSendButton(ActionEvent e) throws IOException{
        messageInfo mi1 = new messageInfo();
        mi1.data = privateMessage.getText();
        mi1.sendALl = false;
        clientConnection.send(mi1);
        privateMessage.clear();
    }

    @FXML
    public void changeTheme(ActionEvent event)throws IOException{
        if (!isDarkTheme) {
            background.setStyle("-fx-background-color: #383838;");
            text1.setStyle("-fx-fill: white");
            text2.setStyle("-fx-fill: white");
            text3.setStyle("-fx-fill: white");
            themeButton.setText("Light\nMode");
            isDarkTheme = true;
        } else {
            background.setStyle("-fx-background-color: white;");
            text1.setStyle("-fx-fill: black");
            text2.setStyle("-fx-fill: black");
            text3.setStyle("-fx-fill: black");
            themeButton.setText("Dark\nMode");
            isDarkTheme = false;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO Auto-generated method stub

    }

}