package client.UxCardButtons;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import server.DefaultCard;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DefaultLobby {
    private String card_ID;
    private String cardName;
    private String price;
    private LocalTime currentTime;
    private LocalTime futureStop;
    private LocalTime variableToPrint;


    public DefaultLobby(String card_ID, String cardName, String price, String startTime) {

        this.card_ID = card_ID;
        this.cardName = cardName;
        this.price = price;
        this.currentTime = LocalTime.parse(startTime);
        this.futureStop = currentTime.plusMinutes(1).plusSeconds(1);
        this.variableToPrint = currentTime.plusMinutes(1).plusSeconds(1);
    }
    public static DefaultLobby castToLobby(String s){
        String[] data = s.split(",");
        DefaultLobby result = new DefaultLobby(data[0],data[1].replace("&"," "),data[2],data[3]);
        return result;
    }



    public String getCard_ID() {
        return card_ID;
    }

    public void setCard_ID(String card_ID) {
        this.card_ID = card_ID;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public LocalTime getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(LocalTime currentTime) {
        this.currentTime = currentTime;
    }

    public LocalTime getFutureStop() {
        return futureStop;
    }

    public void setFutureStop(LocalTime futureStop) {
        this.futureStop = futureStop;
    }

    public LocalTime getVariableToPrint() {
        return variableToPrint;
    }

    public void setVariableToPrint(LocalTime variableToPrint) {
        this.variableToPrint = variableToPrint;
    }
}
