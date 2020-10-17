package client;



import client.SellCardFXML.SellCardContoller;
import com.jfoenix.controls.JFXButton;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
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
    private boolean isConnected;
    private InputStream inputStream;
    BufferedReader bufferedReader ;
    private OutputStream outputStream;
    private Map<Integer, DefaultCard > userCardsInventory = new HashMap<>();
    Timeline cardAddAnimation = new Timeline();

    @FXML
    void initialize() {
       connectedCountListener();
       initializeDoConnect();
       initializeInventoryButton();
       initializeLobbyButton();
       initializeConnectItem();
       initializeDisconnectItem();
       connected(false);

    }

    private void initializeInventoryButton(){
            inventoryButton.setOnAction(e -> generateInventory());
    }
    private void initializeLobbyButton(){
            lobbiesButton.setOnAction(e -> generateLobby());
    }

    private void generateLobby() {
        if (this.isConnected) {
            this.cardAddAnimation.stop();
            flowPane.getChildren().clear();
        }
    }

    private void generateInventory(){
        if (this.isConnected) {
            this.cardAddAnimation.stop();
            flowPane.getChildren().clear();
            List<CardInvenotryButton> allCards = new ArrayList<>();
            userCardsInventory.forEach((k, v) -> {
                CardInvenotryButton cardInvenotryButton = new CardInvenotryButton(k, v);
                cardInvenotryButton.setOnAction(e->{invokeCardSelling(cardInvenotryButton);});
                allCards.add(cardInvenotryButton);
            });
                this.cardAddAnimation = new Timeline(new KeyFrame(Duration.seconds(0.1), new EventHandler<ActionEvent>() {
                int i = 0;
                @Override
                public void handle(ActionEvent event) {
                    flowPane.getChildren().add(allCards.get(i++));
                }
            }));
            this.cardAddAnimation.setCycleCount(allCards.size());
            this.cardAddAnimation.play();
        }
    }

    private void invokeCardSelling(CardInvenotryButton cardInvenotryButton) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SellCardFXML/SellCard.fxml"));
            SellCardContoller controller = new SellCardContoller();
            loader.setController(controller);
            Parent hbox = loader.load();
            Scene scene = new Scene(hbox, 514, 317);
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setScene(scene);
            controller.setData(cardInvenotryButton);
            stage.show();
        }catch (IOException ignore) {

        }
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
                Controller controller = loader.getController();
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Log in");
                stage.show();

            }catch (IOException ignore) {

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

                    if(str[0].equals("STANDART_PACKET_END")) {
                        Platform.runLater(() -> { generateInventory();});
                      //  Platform.runLater(() -> { generateLobby();});
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
            this.isConnected = true;
            setClientStatus("online");
            usersOnline.set("0");
            logInItem.setDisable(true);
            doConnect.setDisable(true);
            disconnectItem.setDisable(false);
            logger.log(Level.INFO,"User has been connected");
        }else {
           this.isConnected = false;
           this.cardAddAnimation.stop();
           this.flowPane.getChildren().clear();
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