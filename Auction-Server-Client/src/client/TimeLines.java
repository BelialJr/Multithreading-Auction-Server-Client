package client;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.util.Duration;


public class TimeLines {


    public static void configureButton(Button button){
        Timeline onAction = new Timeline();
        Timeline mouseEntered = new Timeline();
        Timeline mouseExited = new Timeline();

        configureOnACtion(button,onAction);
        button.setOnAction(e->{
          //  onAction.play();
        });
        configureMouseEntered(button,mouseEntered);
        button.setOnMouseEntered(e->{
            mouseEntered.play();
        });
        configureMouseExited(button,mouseExited);
        button.setOnMouseExited(e->{
            mouseExited.play();
        });
    }

    private static void configureOnACtion(Button button, Timeline timeline ) {
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.seconds(.07),
                        new KeyValue(button.scaleXProperty(), 1.5),
                        new KeyValue(button.scaleYProperty(), 1.5)));

    }

    private static void configureMouseEntered(Button button, Timeline timeline) {
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.seconds(.05),
                        new KeyValue(button.scaleXProperty(), button.getScaleX() + 0.15),
                        new KeyValue(button.scaleYProperty(), button.getScaleY() + 0.15)) );
    }

    private static void configureMouseExited(Button button, Timeline timeline) {
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.seconds(.05),
                        new KeyValue(button.scaleXProperty(), button.getScaleX()),
                        new KeyValue(button.scaleYProperty(), button.getScaleY())));
    }



}
