package client.SellCardFXML;

import client.CardInvenotryButton;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.stage.Stage;
import server.DefaultCard;

public class SellCardContoller {

    @FXML
    public Button cardButton ;

    @FXML
    private JFXButton backButton;

    @FXML
    private JFXButton sellButton;

    @FXML
    private ListView<String> cardDataList;

    @FXML
    private ListView<?> lastPricesList;

    @FXML
    public void initialize() {

    }
    public void doData(String s1){

       this.cardButton.setText(s1);
    }

    public void setData(CardInvenotryButton cardInvenotryButton) {
        this.cardButton.setText(cardInvenotryButton.getDefaultCard().getName());
        generateDataList(cardInvenotryButton);
        initializeBackButton();
        initializeSellButton();
    }

    private void initializeSellButton() {
        this.sellButton.setOnAction(e->{

        });
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
        for(String str : defaultCard.generateListView()){
            cardDataList.getItems().add(str);
        }
    }
}