package client.UxCardButtons;

import client.TimeLines;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.time.LocalTime;

public class LobbyButton extends Button {
    private final  int  WIDTH = 113;
    private final  int  HEiGHT = 177;
    private   DefaultLobby defaultLobby;

    public LobbyButton(Integer lobbyID , DefaultLobby lobby) {
        this.defaultLobby = lobby;
        this.wrapTextProperty().setValue(true);
        this.textAlignmentProperty().set(TextAlignment.CENTER);
        this.setPrefSize(WIDTH,HEiGHT);
        String cardName = lobby.getCardName();
        String biggestBet = "BIGGEST BET "+ "\n" + "0";
        TimeLines.configureButton(this);

        LocalTime currentTime = lobby.getCurrentTime();
        LocalTime futureStop = lobby.getFutureStop();
        LocalTime variableToPrint = lobby.getVariableToPrint();

        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalTime localTime = LocalTime.now();
            if (( (futureStop.withNano(0)).compareTo(localTime.withNano(0))) >= 0) {
                LocalTime var1 =  variableToPrint.minus(java.time.Duration.ofHours(localTime.getHour()));
                LocalTime var2 =  var1.minus(java.time.Duration.ofMinutes(localTime.getMinute()));
                LocalTime var3 =  var2.minus(java.time.Duration.ofSeconds(localTime.getSecond()));
                this.setText( "---Lobbie---\n\n\n"+cardName + "\n" +  var3.getHour()+ ":"+var3.getMinute()  +":" + var3.getSecond() + "\n\n" + biggestBet);
            }
        }),
                new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    public DefaultLobby getDefaultLobby() {
        return defaultLobby;
    }
}
