package client;


import com.jfoenix.controls.JFXButton;
import javafx.scene.control.Button;
import javafx.scene.text.TextAlignment;
import server.DefaultCard;

import javax.swing.*;
import java.awt.*;

public class CardInvenotryButton extends Button {
    private final  int  WIDTH = 113;
    private final  int  HEiGHT = 177;
    private DefaultCard defaultCard;

    public int getWIDTH() {
        return WIDTH;
    }

    public int getHEiGHT() {
        return HEiGHT;
    }

    public DefaultCard getDefaultCard() {
        return defaultCard;
    }

    public void setDefaultCard(DefaultCard defaultCard) {
        this.defaultCard = defaultCard;
    }

    public CardInvenotryButton(Integer cardID, DefaultCard defaultCard) {
        this.wrapTextProperty().setValue(true);
        this.textAlignmentProperty().set(TextAlignment.CENTER);
        this.setPrefSize(WIDTH,HEiGHT);
        this.setText(defaultCard.getName());
        this.defaultCard = defaultCard;
        TimeLines.configureButton(this);

    }


}
