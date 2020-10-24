package client.LobbyEnteredFXML;

import client.Controller;
import client.TimeLines;
import client.UxCardButtons.DefaultLobby;
import client.UxCardButtons.LobbyButton;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LobbyEnterContoller {


    @FXML
    private Button cardButton;

    @FXML
    private Label timeLabel;

    @FXML
    private Label startBetLabel;

    @FXML
    private Label maxBetLabel;

    @FXML
    private JFXButton sellButton;

    @FXML
    private JFXButton backButton;

    @FXML
    private JFXTextField yourBetField;

    @FXML
    private JFXProgressBar progressBar;

    @FXML
    private ListView<String> betList;

    private List<Node> listNodes = new ArrayList<>();
    private  LobbyButton lobbyButton;
    private Timeline timelineLoading;
    private  Controller controller;
    boolean canSend = false;
    private DefaultLobby defaultLobby;

    public void setData(LobbyButton lobbyButton, Controller controller){

        this.lobbyButton = lobbyButton;
        this.progressBar.setVisible(false);
        this.controller = controller;
        this.defaultLobby =  lobbyButton.getDefaultLobby();
        this.maxBetLabel.setText("0$");
        this.startBetLabel.setText(defaultLobby.getPrice()+"$");
        this.cardButton.textAlignmentProperty().set(TextAlignment.CENTER);
        this.cardButton.setText("--Card--\n\n\n"+defaultLobby.getCardName()+"\n\n\n\n\n---------");
        initializeBackButton();
        initializeBetButton();
        initalizeFields();
        initlizeBetsList();

        listNodes.addAll(List.of(backButton,sellButton,cardButton,timeLabel,yourBetField,maxBetLabel,startBetLabel,betList));
        LocalTime currentTime = defaultLobby.getCurrentTime();
        LocalTime futureStop = defaultLobby.getFutureStop();
        LocalTime variableToPrint = defaultLobby.getVariableToPrint();

        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalTime localTime = LocalTime.now();
            if (( (futureStop.withNano(0)).compareTo(localTime.withNano(0))) >= 0) {
                LocalTime var1 =  variableToPrint.minus(java.time.Duration.ofHours(localTime.getHour()));
                LocalTime var2 =  var1.minus(java.time.Duration.ofMinutes(localTime.getMinute()));
                LocalTime var3 =  var2.minus(java.time.Duration.ofSeconds(localTime.getSecond()));
                this.timeLabel.setText(var3.getHour()+ ":"+var3.getMinute()  +":" + var3.getSecond());
            }else{
                Stage stage = (Stage) backButton.getScene().getWindow();
                stage.close();
            }
        }),
                new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    private void initlizeBetsList() {
        Integer lobby_Key = getKey(controller.userLobbies,this.defaultLobby);
        Timeline wrt = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            betList.getItems().clear();
            if(controller.lobbiesBets.get(lobby_Key) != null) {
                for (String line : controller.lobbiesBets.get(lobby_Key)) {
                    betList.getItems().add(line);
                }
            }
            if(!betList.getItems().isEmpty()) {
                String str = betList.getItems().get(betList.getItems().size() - 1);
                maxBetLabel.setText(str.split(":")[2].replace("|", "").replace(" ", "") + "$");
            }
            return;

        }),new KeyFrame(Duration.seconds(0.3)));

        wrt.setCycleCount(Animation.INDEFINITE);
        wrt.play();
    }

    public static <K, V> K getKey(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void initializeBetButton() {
        this.sellButton.setOnAction(e->{
            if(isInputValid()) {
                startAnimation();
            }
        });
    }
    private void sendBet(){
        controller.sendBetToServer(lobbyButton.getDefaultLobby(),yourBetField.getText());
    }

    private void startAnimation() {
        progressBar.setVisible(true);
        this.timelineLoading = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            for(Node node : listNodes){
                node.setDisable(true);
            }
        }),
                new KeyFrame(Duration.seconds(0.75)));

        this.timelineLoading.setOnFinished(event -> {
            progressBar.setVisible(false);
            for(Node node : listNodes){
                  node.setDisable(false);
            }
                sendBet();
                this.yourBetField.setText("");
                }

        );

        this.timelineLoading.setCycleCount(1);
        this.timelineLoading.play();
    }

    private boolean isInputValid() {
        try {
            int wrt = Integer.valueOf(yourBetField.getText());
            if(wrt >= 10){
                yourBetField.setUnFocusColor(Color.GREEN);
                return  true;
            }else{
                throw new NumberFormatException();
            }
        } catch(NumberFormatException e){
            yourBetField.setText("");
            yourBetField.setUnFocusColor(Color.RED);
            return false;
        }
    }
    private void initalizeFields() {
        yourBetField.textProperty().addListener((observable, oldValue, newValue) -> {
            yourBetField.setUnFocusColor(Color.GREEN);
        });

    }


    private void initializeBackButton() {
        this.backButton.setOnAction(e->{
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.close();
        });
    }

}