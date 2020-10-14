package client;


import client.LogInFXML.LogController;
import com.jfoenix.controls.JFXButton;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import server.Server;
import server.User;

public class Controller {

    @FXML
    private ResourceBundle resources;

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


    @FXML
    void initialize() {
       ConnectedCountListener();
       initializeDoConnect();
       initializeConnectItem();
       initializeDisconnectItem();
       connected(false);

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

    @FXML
    public void transferLogData(String login,String password){
        if(login!=null)this.login = login;
        if(password!=null)this.password = password;
        System.out.println(this.login + " transfer data = " + this.password);
    }

    private void openSocket() {
        try {
            System.out.println(this.login + " socket = " + this.password);
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
                            connected(true);
                        if(str[1].equals("FAILED"))
                             connected(false);
                    }if(str[0].equals("USERSONLINE")) {
                        usersOnline.set(str[1]);
                    }if(str[0].equals("DISCONNECT")) {
                        connected(false);
                    }if(str[0].equals("WARNING")) {
                       Platform.runLater(() ->
                       {
                           StringBuilder sb = new StringBuilder();
                           for (int i = 1; i <str.length ; i++) { //Arrays.toString( Arrays.copyOfRange(str,1,str.length));
                               sb.append(str[i] + " ");
                           }
                           alertWindow( sb.toString());
                       });
                    }
                }
            } catch (IOException ignore) {

            }
        }).start();


    }

    private void logIN() {
        String data = "LOGIN " +this.login + " " + this.password;
        System.out.println(data);
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

    private void ConnectedCountListener() {
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