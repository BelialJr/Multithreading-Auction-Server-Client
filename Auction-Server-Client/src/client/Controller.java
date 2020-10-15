package client;



import com.jfoenix.controls.JFXButton;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import server.DefaultCard;
import server.Server;

public class Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private Label bankLabel;

    @FXML
    private URL location;

    @FXML
    private MenuItem logInItem;

    @FXML
    private MenuItem doConnect;

    @FXML
    private MenuItem disconnectItem;

    @FXML
    private MenuItem quitItem;

    @FXML
    private JFXButton historyButton;

    @FXML
    private JFXButton inventoryButton;

    @FXML
    private JFXButton lobbiesButton;

    @FXML
    private JFXButton bankButton;

    @FXML
    private FlowPane flowPane;

    @FXML
    private Label userStatusLabel;

    @FXML
    private Label usersOnlineLabel;

    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private SimpleStringProperty usersOnline = new SimpleStringProperty();
    public static  String login = "$";
    public static  String password = "$";
    private final int  port = 3440 ;
    private Socket socket;
    private InputStream inputStream;
    BufferedReader bufferedReader ;
    private OutputStream outputStream;
    private Map<Integer, DefaultCard > userCardsInventory = new HashMap<>();

    @FXML
    void initialize() {
       connectedCountListener();
       initializeDoConnect();
       initializeInventoryButton();
       initializeConnectItem();
       initializeDisconnectItem();
       connected(false);

    }

    private void initializeInventoryButton(){
            inventoryButton.setOnAction(e -> generateInventory());
    }

    private void generateInventory(){
     //   flowPane.getChildren().clear();
        List<CardInvenotryButton> allCards = new ArrayList<>();
        userCardsInventory.forEach((k,v)->{ allCards.add(new CardInvenotryButton(k,v)); });
        Platform.runLater(() ->{  flowPane.getChildren().addAll(allCards);});
    }

    private void initializeDoConnect() {
        doConnect.setOnAction(event -> {
        openSocket();
        connected(true);
        });
    }

    private void initializeConnectItem() {
        logInItem.setOnAction(e-> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("LogInFXML/logInSample.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Log in");
                stage.show();
            }catch (IOException ex) {
              ex.printStackTrace();
            }
        });
    }

   /* @FXML
    public void transferLogData(String login,String password){
        if(login!=null)this.login = login;
        if(password!=null)this.password = password;
        System.out.println(this.login + " transfer data = " + this.password);
    }*/

    private void openSocket() {
        try {
            this.socket = new Socket("localhost", port);
            this.inputStream = socket.getInputStream();
            this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            this.outputStream = socket.getOutputStream();
            startReadingInput();
            logIN();
        } catch (IOException e) {
            logger.log(Level.INFO,"Failed to connect to the server");
            alertWindow("Can not connect to the server ");
        }
    }

    private void startReadingInput() {
        new Thread(()->{
            try {
                while(!socket.isClosed()) {
                    String line = bufferedReader.readLine();
                    logger.log(Level.INFO,"SOCKET INPUT : " + line);
                    String[] str = line.split(" ");

                    if (str[0].equals("LOGIN")) {
                        if(str[1].equals("PASSED"))
                            Platform.runLater(() -> {connected(true);});
                        if(str[1].equals("FAILED"))
                            Platform.runLater(() -> {connected(false);});
                     }

                    if(str[0].equals("INVENTORY")) {
                         line = line.substring(10);
                         List<String> cardsList = Arrays.asList( line.split("#"));
                         for(String cardStr :cardsList){
                             Integer cardID = Integer.parseInt(cardStr.split("::")[0]);
                             userCardsInventory.put(cardID,DefaultCard.castToCard(cardStr.split("::")[1]));
                         }
                    }

                    if(str[0].equals("LOBBIES")) {

                    }

                    if(str[0].equals("USERSONLINE")) {
                        Platform.runLater(() -> {usersOnline.set(str[1]);});
                    }

                    if(str[0].equals("BANK")) {
                        Platform.runLater(() -> {bankLabel.setText(str[1]);});
                    }

                    if(str[0].equals("DISCONNECT")) {
                        Platform.runLater(() -> {connected(false);});
                    }

                    if(str[0].equals("WARNING")) {
                       Platform.runLater(() -> {
                           StringBuilder sb = new StringBuilder();
                           for (int i = 1; i <str.length ; i++) {
                               sb.append(str[i] + " ");
                           }
                           alertWindow( sb.toString()); });
                    }
                }
            } catch (IOException ignore) {

            }
        }).start();


    }

    private void logIN() {
        sendToServer("LOGIN " +this.login + " " + this.password);
    }

    private void initializeDisconnectItem(){
        disconnectItem.setOnAction( e->{
            connected(false);
        } );
    }

    private void sendToServer(String s){
        try {
            outputStream.write((s + "\n").getBytes());
            outputStream.flush();
            logger.log(Level.INFO,"SOCKET OUTPUT : " + s);
        }catch (IOException e){
            e.printStackTrace();
        }
    }




    private void alertWindow(String contentText){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Alert");
        alert.setHeaderText(null);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
    private void setClientStatus(String str){
        Platform.runLater(()->{
        if(str.equals("offline")){
            userStatusLabel.setTextFill(Color.RED);
            userStatusLabel.setText("offline");
        }else if(str.equals("online")){
            userStatusLabel.setText("online");
            userStatusLabel.setTextFill(Color.GREEN);
        }
    });
    }

    private void connectedCountListener() {
        AnimationTimer animationTimer=new AnimationTimer() {
            @Override
            public void handle(long now) {
                usersOnlineLabel.setText(usersOnline.getValue());
            }
        };
        animationTimer.start();
    }


    private void connected(boolean b){
       if(b){
            setClientStatus("online");
            usersOnline.set("0");
            logInItem.setDisable(true);
            doConnect.setDisable(true);
            disconnectItem.setDisable(false);
            logger.log(Level.INFO,"User has been connected");
        }else {
            try {
                if(socket != null && (!socket.isClosed())) {
                    socket.close();
                    logger.log(Level.INFO,"User has been disconnected");
                }
                setClientStatus("offline");
                //Platform.runLater(() -> {bankLabel.setText("0");});
                bankLabel.setText("0");
                usersOnline.set("0");
                logInItem.setDisable(false);
                doConnect.setDisable(false);
                disconnectItem.setDisable(true);

            }catch (IOException e1) {
                alertWindow("Can not disconnect from the server");
            }
        }
    }



}