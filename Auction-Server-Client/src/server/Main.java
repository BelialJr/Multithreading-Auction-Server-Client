package server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("serverSample.fxml"));
        primaryStage.setTitle("Auction Server");
        primaryStage.setScene(new Scene(root, 542, 344));
        primaryStage.getIcons().add(new Image("file:" + System.getProperty("user.dir")+"\\"+"source\\"+"server.png"));
        primaryStage.setResizable(false);
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}