package server;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import java.io.IOException;

public class Contoller {

    @FXML
    private Tab ServerlaunchTab;

    @FXML
    private SplitPane SplitPane;

    @FXML
    public ListView<User> UsersList;

    @FXML
    private Label HostLabel;

    @FXML
    private Label PortLabel;

    @FXML
    private Label UsersLabel;

    @FXML
    private TextField HostTextField;

    @FXML
    private TextField PortTextField;

    @FXML
    private TextField UsersTextField;

    @FXML
    private Button StartButton;

    @FXML
    private Label StatusLabel;

    @FXML
    private Label StatusText;

    @FXML
    private Label ConnectedLabel;

    @FXML
    public Label ConnectedText;

    @FXML
    private Button StopButton;

    @FXML
    private Tab DataBaseTab;

    @FXML
    private ListView<User> DataBaseListView;

    private boolean serverIsStarted = false;
    private Integer port;
    private Server  server = Server.getInstance();


    @FXML
    void initialize() {
        HostTextField.setEditable(false);
        setServerStatus("offline");
        initializeStartButton();
        initializeStopButton();
        connectedCountListener();
    }

    private void initializeStartButton() {
        StartButton.setOnAction(event -> {
            if(!serverIsStarted)
            {
                this.serverIsStarted = true;
                this.port = getPort();
                int maxUsers = getMaxUsersCount();
                PortTextField.setDisable(true);
                UsersTextField.setDisable(true);
                try {
                    server.startServer(port, maxUsers, this);
                }catch (IOException e){
                    e.printStackTrace();
                }

            }else{
                alertWindow("Server is already started");
            }
        });
    }

    private void initializeStopButton() {
        StopButton.setOnAction(event -> {
            if(serverIsStarted)
            {
                serverIsStarted = false;
                setServerStatus("offline");
                PortTextField.setDisable(false);
                UsersTextField.setDisable(false);
                UsersList.getItems().clear();
                server.stopServer();
            }
        });
    }

    private Integer getPort(){
        Integer result = null;
        try {
            result = Integer.parseInt(PortTextField.getText());
        }catch (NumberFormatException e ){
            alertWindow("Can not start server in this port");
            serverIsStarted = false;

            return null;
        }
        if(result > 65535){PortTextField.setText("65535");return 65535; }
        return  result;
    }

    private int getMaxUsersCount(){
        int res = 0;
        try {
            res = Integer.parseInt(UsersTextField.getText());
        }catch (NumberFormatException e ){
            alertWindow("Can not start server with max count "+ UsersTextField.getText());
            serverIsStarted = false;

            return 0;
        }
        if(res < 0 ){UsersTextField.setText("1");return 1;}
        if(res > 20){UsersTextField.setText("20");return 20;}
        return res;
    }

    private void alertWindow(String contentText){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Alert");
        alert.setHeaderText(null);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
    public void setServerStatus(String str){
        if(str.equals("offline")){
            StatusText.setTextFill(Color.RED);
            StatusText.setText("offline");
        }else if(str.equals("online")){
            StatusText.setText("online");
            StatusText.setTextFill(Color.GREEN);
        }
    }
    private void connectedCountListener() {
        AnimationTimer animationTimer=new AnimationTimer() {
            @Override
            public void handle(long now) {
                ConnectedText.setText(String.valueOf(UsersList.getItems().size()));
            }
        };
        animationTimer.start();
    }



}
