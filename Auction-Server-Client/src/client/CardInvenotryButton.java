package client;

import client.CardImage.ImageLoader;
import com.jfoenix.controls.JFXButton;
import javafx.scene.control.Button;
import server.DefaultCard;

import javax.swing.*;
import java.awt.*;

public class CardInvenotryButton extends Button {
    private final  int  WIDTH = 113;
    private final  int  HEiGHT = 177;
    private DefaultCard defaultCard;

    public CardInvenotryButton(Integer cardID, DefaultCard defaultCard) {
        this.setPrefSize(WIDTH,HEiGHT);
        this.defaultCard = defaultCard;
        TimeLines.configureButton(this);
        ImageLoader.load(cardID,this);
    }


}
