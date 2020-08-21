package client;

import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.FlowPane;

public class Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private MenuItem connectItem;

    @FXML
    private MenuItem disconnectItem;

    @FXML
    private MenuItem quitItem;

    @FXML
    private JFXButton historyButton;

    @FXML
    private JFXButton inventoryButton;

    @FXML
    private JFXButton lobbiesButton;

    @FXML
    private JFXButton bankButton;

    @FXML
    private FlowPane flowPane;

    @FXML
    private Label userStatusLabel;

    @FXML
    private Label usersOnlineLabel;

    @FXML
    void initialize() {

    }
}