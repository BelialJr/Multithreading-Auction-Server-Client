package client.LogInFXML;

import client.Controller;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class LogController {
    @FXML
    private JFXTextField LoginField;

    @FXML
    private JFXPasswordField PasswordField;

    @FXML
    private JFXButton logInButton;


    @FXML
    void initialize(){
        Platform.runLater(()->{
        logInButton.setOnAction(event -> {
            try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/clientSample.fxml"));
            Parent root = (Parent) loader.load();
            Controller controller = loader.getController();
            Stage stage = (Stage) logInButton.getScene().getWindow();
            stage.close();
            controller.transferLogData(LoginField.getText(), PasswordField.getText());

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        });
    }
}



