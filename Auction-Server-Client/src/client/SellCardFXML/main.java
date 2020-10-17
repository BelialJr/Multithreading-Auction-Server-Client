package client.SellCardFXML;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("SellCard.fxml"));
        primaryStage.setTitle("Auction Client");
        primaryStage.setScene(new Scene(root, 514, 317));
        primaryStage.getIcons().add(new Image("file:" + System.getProperty("user.dir")+"\\"+"source\\"+"icon2.png"));
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}