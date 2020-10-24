package client.SellCardFXML;

import client.Controller;
import com.jfoenix.controls.*;
import javafx.animation.Timeline;
import client.UxCardButtons.CardInvenotryButton;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import server.DefaultCard;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class SellCardContoller {
    @FXML
    private Button cardButton;

    @FXML
    private ListView<String> cardDataList;

    @FXML
    private JFXSpinner loadingAnimation;

    @FXML
    private  JFXTextField priceField;

    @FXML
    private JFXTimePicker minField ;

    @FXML
    private JFXButton sellButton;

    @FXML
    private JFXButton backButton;

    @FXML
    private Label startringLabel;

    @FXML
    private Label timeLabel;

    @FXML
    private JFXProgressBar progressBar;

    private DefaultCard defaultCard;

    private List<Node> listNodes;

    private Timeline timelineLoading;

    Controller controllerClient;
    private Integer card_ID;

    @FXML
    public void initialize() { }

    public void setData(CardInvenotryButton cardInvenotryButton, Controller controller) {
        progressBar.setVisible(false);
        startAnimation();
        this.card_ID = controller.getCard_ID(cardInvenotryButton.getDefaultCard());
        this.controllerClient = controller;
        this.defaultCard = cardInvenotryButton.getDefaultCard();
        this.cardButton.wrapTextProperty().setValue(true);
        this.cardButton.textAlignmentProperty().set(TextAlignment.CENTER);
        this.cardButton.setText(cardInvenotryButton.getText());
        generateDataList(cardInvenotryButton);
        initializeBackButton();
        initializeSellButton();
        initalizeFields();
        listNodes = new ArrayList<>();
        listNodes.addAll(List.of(cardButton,cardDataList,priceField,sellButton,backButton,minField,startringLabel,timeLabel));

    }

    private void initalizeFields() {
        priceField.textProperty().addListener((observable, oldValue, newValue) -> {
            priceField.setUnFocusColor(Color.GREEN);
        });

    }

    private void startAnimation() {
        this.timelineLoading = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            this.loadingAnimation.setVisible(true);
            this.cardDataList.setDisable(true);
        }),
                new KeyFrame(Duration.seconds(2))
        );
        this.timelineLoading.setOnFinished(event -> {
            this.cardDataList.getItems().add("--------------------SALE_STORY----------------------");

            List<String> saleData = (controllerClient.userCardsInventoryHistory.entrySet().stream().
                    filter(entry ->entry.getKey() == card_ID).map(Map.Entry::getValue).findFirst()).get();

            for(String string:saleData) {
                this.cardDataList.getItems().add(string);
            }
            this.loadingAnimation.setVisible(false);
            this.cardDataList.setDisable(false);
         }
        );
        this.timelineLoading.setCycleCount(1);
        this.timelineLoading.play();
    }

    private void startSellAnimation() {
        progressBar.setVisible(true);
        this.timelineLoading = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            for(Node node : listNodes){
                node.setDisable(true);
            }
        }),
                new KeyFrame(Duration.seconds(1)));

        this.timelineLoading.setOnFinished(event -> {
            Stage stage = (Stage) sellButton.getScene().getWindow();
            stage.close();
                }

        );

        this.timelineLoading.setCycleCount(1);
        this.timelineLoading.play();
    }


    private void initializeSellButton() {
        this.sellButton.setOnAction(e->{
            if(isInputValid()) {
                startSellAnimation();
                controllerClient.sendCardToServer(defaultCard,priceField.getText());
            }
        });
    }

    private boolean isInputValid() {
            try {
                int wrt = Integer.valueOf(priceField.getText());
                if(wrt >= 10){
                    priceField.setUnFocusColor(Color.GREEN);
                }else{
                    throw new NumberFormatException();
                }
            } catch(NumberFormatException e){
                priceField.setText("");
                priceField.setUnFocusColor(Color.RED);
                return false;
            }

            try {
                LocalTime time = minField.getValue();
                if(time.compareTo(LocalTime.of(1,0)) >= 0 ){
                    return true;
                }else{
                    throw new NullPointerException();
                }
            }catch(NullPointerException e){
                    minField.setValue(LocalTime.of(1,0));
                    return false;
                }


    }


    private void initializeBackButton() {
        this.backButton.setOnAction(e->{
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.close();
        });
    }

    private void generateDataList(CardInvenotryButton cardInvenotryButton) {
        StringBuilder stringBuilder = new StringBuilder();
        DefaultCard defaultCard = cardInvenotryButton.getDefaultCard();
        cardDataList.getItems().add("--------------------Card_Data----------------------");
        for(String str : defaultCard.generateListView()){
            {
                cardDataList.getItems().add(str);
            }
        }

    }
}