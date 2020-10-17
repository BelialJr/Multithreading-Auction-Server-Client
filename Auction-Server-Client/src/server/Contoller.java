package server;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import org.w3c.dom.events.Event;

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
    private JFXButton dbPrevButton;

    @FXML
    private JFXButton dbNextButton;

    @FXML
    private JFXTextField dbTextField;

    @FXML
    private TableView<String> dbTableView;
    private DbStatus dbStatus = DbStatus.USER;

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
        initializeDataBaseTab();
        initializeDBButtons();
    }

    private void initializeDBButtons() {
        EventHandler<ActionEvent> eventEventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                dbStatus = DbStatus.change(dbStatus);
                Platform.runLater(()->invokeDataBaseTab());
            }
        };
        dbPrevButton.setOnAction(eventEventHandler);
        dbNextButton.setOnAction(eventEventHandler);
    }



    private enum DbStatus{
        USER,CARD;
        public static DbStatus change(DbStatus dbStatus){
            if(dbStatus == USER)return CARD;
            else
            if(dbStatus == CARD)return USER;
            return null;
        }

    }


    private void initializeDataBaseTab() {
        dbTableView.setEditable(false);
        dbTextField.setEditable(false);
        dbTextField.setAlignment(Pos.CENTER);
        DataBaseTab.setOnSelectionChanged(event -> {
                invokeDataBaseTab();
        });
    }

    private void invokeDataBaseTab() {
        dbTableView.getColumns().clear();

        if(dbStatus == DbStatus.USER){
            TableColumn userIdcolumn = new TableColumn("User_ID");
            TableColumn lognColumn = new TableColumn("Login");
            TableColumn passwordColumn = new TableColumn("Password");
            TableColumn bankColumn = new TableColumn("Bank");
            dbTableView.getColumns().addAll(userIdcolumn, lognColumn, passwordColumn,bankColumn);
            dbTextField.setText("TABLE : USER");
        }else if(dbStatus == DbStatus.CARD){

            TableColumn cardIdColumn = new TableColumn("Card_ID");
            TableColumn nameColumn = new TableColumn("Name");
            TableColumn heightColumn = new TableColumn("Height");
            TableColumn skinColorcolumn = new TableColumn("Skin_Color");
            TableColumn birthYearColumn = new TableColumn("Birth_year");
            TableColumn genderColumn = new TableColumn("Gender");
            TableColumn userIdcolumn = new TableColumn("User_ID");
            dbTableView.getColumns().addAll(cardIdColumn, nameColumn, heightColumn,skinColorcolumn,birthYearColumn,genderColumn,userIdcolumn);
            dbTextField.setText("TABLE : CARD");
        }
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
