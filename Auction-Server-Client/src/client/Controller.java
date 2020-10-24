package client;



import client.LobbyEnteredFXML.LobbyEnterContoller;
import client.SellCardFXML.SellCardContoller;
import client.UxCardButtons.CardInvenotryButton;
import client.UxCardButtons.DefaultLobby;
import client.UxCardButtons.LobbyButton;
import com.jfoenix.controls.JFXButton;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import com.jfoenix.controls.JFXSpinner;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import server.DefaultCard;
import server.Lobbie.LobbiesHandler;
import server.Server;

public class Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private Label bankLabel;

    @FXML
    private URL location;

    @FXML
    private MenuItem logInItem;

    @FXML
    private MenuItem doConnect;

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
    private ScrollPane ScrollPane;

    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private SimpleStringProperty usersOnline = new SimpleStringProperty();
    public static  String login = "$";
    public static  String password = "$";
    private final int  port = 3440 ;
    private Socket socket;
    private boolean isConnected;
    private InputStream inputStream;
    public  BufferedReader bufferedReader;
    private OutputStream outputStream;
    private Map<Integer, DefaultCard > userCardsInventory = new LinkedHashMap<>();
    public  Map<Integer, List<String>> userCardsInventoryHistory = new LinkedHashMap<>();
    public  Map<Integer, DefaultLobby> userLobbies = new LinkedHashMap<>();
    public  Map<Integer, List<String>> lobbiesBets = new LinkedHashMap<>();
    private List<SaleStory > userSalesHistory = new ArrayList<>();
    private Timeline cardAddAnimation = new Timeline();
    private Insets insets;




    enum Status{LOBBIES,INVENOTRY,HISTORY };
    private Status status = null;

    @FXML
    void initialize() {
       this.insets= flowPane.getPadding();
       connectedCountListener();
       initializeDoConnect();
       initializeInventoryButton();
       initializeLobbyButton();
       initializeHistoryButton();
       initializeConnectItem();
       initializeDisconnectItem();

       connected(false);

    }
    private void initializeHistoryButton() {
            historyButton.setOnAction(e-> {this.status = Status.HISTORY;generateHistory();});

    }

    private void initializeInventoryButton(){
            inventoryButton.setOnAction(e -> {this.status = Status.INVENOTRY; generateInventory(); } );
    }
    private void initializeLobbyButton(){
            lobbiesButton.setOnAction(e ->{ this.status = Status.LOBBIES;generateLobby();});
    }

    public void sendCardToServer(DefaultCard card,String price){
        Integer card_ID =getCard_ID(card);
        disableInventoryButton(card_ID,true);
        sendToServer("CARD_TO_SALE " + card_ID + " " + price);
    }

    public void sendBetToServer(DefaultLobby defaultLobby, String bet) {
        Integer lobby_ID = getLobby_ID(defaultLobby);
        sendToServer("LOBBY_BET " + lobby_ID + " " + bet);
    }


    private void disableInventoryButton(Integer card_ID,boolean b) {
        DefaultCard card = userCardsInventory.get(card_ID);
        card.setDisable(b);
        int index = 0;
        for(DefaultCard defaultCard:userCardsInventory.values()){
            if(defaultCard.hashCode() == card.hashCode())
                break;
            index++;
        }
        final int index1 = index;
        Platform.runLater(()->{
            flowPane.getChildren().get(index1).setDisable(b);});
    }


    public  Integer getCard_ID(DefaultCard defaultCard) {
        Optional<Integer> optional = userCardsInventory.entrySet().stream().filter(entry->entry.getValue() == defaultCard).map(Map.Entry::getKey).findFirst();
        return  optional.get();
    }

    private Integer getLobby_ID(DefaultLobby defaultLobby) {
        Optional<Integer> optional = userLobbies.entrySet().stream().filter(entry->entry.getValue() == defaultLobby).map(Map.Entry::getKey).findFirst();
        return  optional.get();
    }

    private void generateHistory() {
        if (this.isConnected) {
            this.cardAddAnimation.stop();
            flowPane.getChildren().clear();
            flowPane.setPadding(new Insets(0));
            TableView<SaleStory> tableView  = new TableView<SaleStory>();
            tableView.setPrefSize(flowPane.getPrefWidth(),flowPane.getHeight());
            TableColumn statusColumn = new TableColumn("Status");
            statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
            statusColumn.setCellFactory(factory ->new CustomCell());
            TableColumn saleIdColumn = new TableColumn("Sale_ID");
            saleIdColumn.setCellValueFactory(new PropertyValueFactory<>("sale_ID"));
            TableColumn cardNameColumn = new TableColumn("Card_Name");
            cardNameColumn.setCellValueFactory(new PropertyValueFactory<>("cardName"));
            TableColumn sellUserIdColumn = new TableColumn("UserSold");
            sellUserIdColumn.setCellValueFactory(new PropertyValueFactory<>("saleLogin"));
            TableColumn buyUserIDColumn = new TableColumn("UserBought");
            buyUserIDColumn.setCellValueFactory(new PropertyValueFactory<>("buyLogin"));
            TableColumn priceColumn = new TableColumn("Price");
            priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
            tableView.getColumns().addAll(statusColumn, saleIdColumn ,cardNameColumn,sellUserIdColumn,buyUserIDColumn,priceColumn);
            tableView.getItems().addAll(this.userSalesHistory);
            flowPane.getChildren().add(tableView);
        }
    }

    private  void addButtonToPaneAnimation(List<Button> allLobbies){
        this.cardAddAnimation = new Timeline(new KeyFrame(Duration.seconds(0.1), new EventHandler<ActionEvent>() {
            int index = 0;
            @Override
            public void handle(ActionEvent event) {
                flowPane.getChildren().add(allLobbies.get(index++));
            }
        }));
        this.cardAddAnimation.setCycleCount(allLobbies.size());
        this.cardAddAnimation.play();
    }

    //private List<Button>

    private void generateLobby() {
        if (this.isConnected) {
            this.cardAddAnimation.stop();
            this.cardAddAnimation.setOnFinished(e->{});
            flowPane.getChildren().clear();
            flowPane.setPadding(this.insets);

            if(!this.userLobbies.isEmpty()) {
                List<Button> allLobbies = new ArrayList<>();
                userLobbies.forEach((k, v) -> {

                    LobbyButton cardInvenotryButton = new LobbyButton(k, v);
                    cardInvenotryButton.setOnAction(e -> {
                        invokeLobbyEnter(cardInvenotryButton);
                    });
                    allLobbies.add(cardInvenotryButton);
                });
                this.addButtonToPaneAnimation(allLobbies);
            }
        }
    }



    private void generateInventory(){
        if (this.isConnected) {
            this.cardAddAnimation.stop();
            this.cardAddAnimation.setOnFinished(e->{});
            flowPane.getChildren().clear();
            flowPane.setPadding(this.insets);

            if (!this.userCardsInventory.isEmpty()) {
                List<Button> allCards = new ArrayList<>();
                userCardsInventory.forEach((k, v) -> {
                    CardInvenotryButton cardInvenotryButton = new CardInvenotryButton(k, v);
                    cardInvenotryButton.setOnAction(e -> {
                        invokeCardSelling(cardInvenotryButton);
                    });
                    allCards.add(cardInvenotryButton);
                });

                this.addButtonToPaneAnimation(allCards);
            }
        }
    }

    private void invokeLobbyEnter(LobbyButton lobbyButton) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LobbyEnteredFXML/LobbyEnter.fxml"));
            Parent hbox = loader.load();
            LobbyEnterContoller controller = loader.getController();
            Scene scene = new Scene(hbox, 757, 324);
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.getIcons().add(new Image("file:" + System.getProperty("user.dir")+"\\"+"source\\"+"lobbyIcon2.jpg"));
            stage.setScene(scene);
            stage.setTitle("Make Your Bets");
            controller.setData(lobbyButton,this);
            stage.show();

        }catch (IOException ignore) {

        }
    }

    private void invokeCardSelling(CardInvenotryButton cardInvenotryButton) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SellCardFXML/SellCard.fxml"));
            Parent hbox = loader.load();
            SellCardContoller controller = loader.getController();
            Scene scene = new Scene(hbox, 731, 324);
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.getIcons().add(new Image("file:" + System.getProperty("user.dir")+"\\"+"source\\"+"sellIcon.png"));
            stage.setScene(scene);
            stage.setTitle("Sell Card");
            Integer card_ID = getCard_ID(cardInvenotryButton.getDefaultCard());
            sendToServer("CARD_LAST_PRICES "+ card_ID);
            controller.setData(cardInvenotryButton,this);
            stage.show();

        }catch (IOException ignore) {

        }
    }



    private void initializeDoConnect() {
        doConnect.setOnAction(event -> {
        openSocket();
        connected(true);
        });
    }

    private void initializeConnectItem() {
        logInItem.setOnAction(e-> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("LogInFXML/logInSample.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.getIcons().add(new Image("file:" + System.getProperty("user.dir")+"\\"+"source\\"+"loginIcon.png"));
                stage.setScene(new Scene(root));
                stage.setTitle("Log in");
                stage.show();

            }catch (IOException ignore) {

            }
        });
    }


    private void openSocket() {
        try {
            this.socket = new Socket("localhost", port);
            this.inputStream = socket.getInputStream();
            this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            this.outputStream = socket.getOutputStream();
            startReadingInput();
            logIN();
        } catch (IOException e) {
            logger.log(Level.INFO,"Failed to connect to the server");
            alertWindow("Can not connect to the server ");
        }
    }

    private void startReadingInput() {
        new Thread(()->{
            try {
                while(!socket.isClosed()) {
                    String line = bufferedReader.readLine();
                    logger.log(Level.INFO,"SOCKET INPUT : " + line);
                    String[] str = line.split(" ");

                    if (str[0].equals("LOGIN")) {
                        if(str[1].equals("PASSED"))
                            Platform.runLater(() -> {connected(true);});
                        if(str[1].equals("FAILED"))
                            Platform.runLater(() -> {connected(false);});
                     }


                    if(str[0].equals("INVENTORY")) {
                        line = line.substring(10);
                        if(!line.equals("")) {
                            List<String> cardsList = Arrays.asList(line.split("#"));
                            for (String cardStr : cardsList) {
                                Integer cardID = Integer.parseInt(cardStr.split("::")[0]);
                                userCardsInventory.put(cardID, DefaultCard.castToCard(cardStr.split("::")[1]));
                            }
                        }
                    }

                    if(str[0].equals("HISTORY")) {
                        line = line.substring(8);
                        if(!line.equals("")) {
                            userSalesHistory.clear();
                            List<String> historyList = Arrays.asList(line.split("#"));
                            for (String historyStr : historyList) {
                                userSalesHistory.add(new SaleStory(historyStr));
                            }
                        }
                    }

                    if(str[0].equals("LOBBIES")) {
                        line = line.substring(8);
                        if(!line.equals("")){
                            List<String> lineLobby = Arrays.asList(line.split("#"));
                            for(String wrt : lineLobby){
                                Integer lobby_ID = Integer.parseInt(wrt.split("::")[0]);
                                userLobbies.put(lobby_ID,DefaultLobby.castToLobby(wrt.split("::")[1]));
                            }
                        }
                    }
                    if(str[0].equals("ALL_BETS")) {
                        line = line.substring(9);
                        if(!line.equals("")){
                            List<String> lobbyBets = Arrays.asList(line.split("#"));
                            for(String lobbyBet : lobbyBets){
                                List<String> result = new ArrayList<>();
                                Integer lobby_ID = Integer.parseInt(lobbyBet.split("::")[0]);
                                List<String> bets = Arrays.asList(lobbyBet.split("::")[1].split(";"));
                                for(String bet : bets) {
                                    String userName = bet.split(",")[0];
                                    String userBet = bet.split(",")[1];
                                    String resultStr = String.format("%-4s", "| UserName : " + userName + " -----> ") + String.format("%-5s", " Bet : " + userBet) + String.format("%-5s", "|");
                                    result.add(resultStr);
                                     }
                                lobbiesBets.put(lobby_ID,result);
                            }
                        }
                    }

                    if(str[0].equals("LOBBY_NEW")) {
                        handleNewLobby(str);
                    }
                    if(str[0].equals("CARD_NEW")) {
                        line = line.substring(9);
                        if(!line.equals("")) {
                            handleNewCard(line);
                        }
                    }
                    if(str[0].equals("CARD_BACK")) {
                        disableInventoryButton(Integer.parseInt(str[1]),false);
                    }

                    if(str[0].equals("CARD_DEL")) {
                        Platform.runLater(() -> deleteButtonFromPaneAndList(CardInvenotryButton.class , str[1]));
                    }
                    if(str[0].equals("LOBBY_DEL")) {
                        Platform.runLater(() -> deleteButtonFromPaneAndList(LobbyButton.class , str[1]));
                        lobbiesBets.remove(Integer.parseInt(str[1]));
                    }

                    if(str[0].equals("LOBBY_BET")){
                        Integer lobby_ID = Integer.parseInt(str[1]);
                        Integer bet = Integer.parseInt(str[2]);
                        String  userName = str[3];
                        List<String> lobbyBets = new ArrayList<>();
                        if(lobbiesBets.entrySet().stream().anyMatch(e -> e.getKey() == lobby_ID)) {
                            lobbiesBets.entrySet().stream()
                                    .filter(e -> e.getKey() == lobby_ID)
                                    .map(Map.Entry::getValue)
                                    .findFirst()
                                    .get().forEach(e -> lobbyBets.add(e));
                        }
                        String str1 = String.format("%-4s","| UserName : " + userName +" -----> ") + String.format("%-5s"," Bet : " + bet)+String.format("%-5s","|") ;
                        lobbyBets.add(str1);
                        lobbiesBets.put(lobby_ID,lobbyBets);

                    }
                    if(str[0].equals("CARD_PRICE_HISTORY")) {
                        Integer card_ID = Integer.parseInt(str[1]);
                        String[] data = (line.substring("CARD_PRICE_HISTORY".length()+3).trim()).split("#");
                        String singlePrice = null;
                        List<String> res = new ArrayList<>();
                        res.add(String.format("%-12s","")+String.format("%-4s","SALE_ID " )+ "| "   + String.format("%-10s","CARD_NAME " ) +  " | "  + String.format("%-3s","PRICE")+ "|");
                        for (String string : data){
                             String[] lineSplited = string.split(",");
                             singlePrice =String.format("%-14s","|")+String.format("%-8s","|"+lineSplited[0] )+ "|"   + String.format("%-15s",lineSplited[1] ) +  " | "  + String.format("%-4s",lineSplited[2])+ "|"+ "          |";
                             res.add(singlePrice);
                        }
                        userCardsInventoryHistory.put(card_ID,res);


                    }

                    if(str[0].equals("STANDART_PACKET_END")) {
                        if(userLobbies.isEmpty()) {
                            this.status = Status.INVENOTRY;
                            Platform.runLater(() -> {
                                generateInventory();
                            });
                        }else {
                            this.status = Status.LOBBIES;
                            Platform.runLater(() -> {
                                generateLobby();
                            });
                        }
                    }


                    if(str[0].equals("USERSONLINE")) {
                        Platform.runLater(() -> {usersOnline.set(str[1]);});
                    }

                    if(str[0].equals("BANK")) {
                        Platform.runLater(() -> {bankLabel.setText(str[1]);});
                    }

                    if(str[0].equals("DISCONNECT")) {
                        Platform.runLater(() -> {connected(false);});
                    }

                    if(str[0].equals("WARNING")) {
                       Platform.runLater(() -> {
                           StringBuilder sb = new StringBuilder();
                           for (int i = 1; i <str.length ; i++) {
                               sb.append(str[i] + " ");
                           }
                           alertWindow( sb.toString()); });
                    }
                }
            } catch (IOException ignore) {

            }
        }).start();


    }

    private void handleNewCard(String str) { //67::Dooku,193,fair,102BBY,male#
        Integer cardID = Integer.parseInt(str.split("::")[0]);
        DefaultCard defaultCard = DefaultCard.castToCard(str.split("::")[1]);
        userCardsInventory.put(cardID,defaultCard);
        if(this.status == Status.INVENOTRY){
            CardInvenotryButton cardInvenotryButton = new CardInvenotryButton(cardID,defaultCard);
            cardInvenotryButton.setOnAction(e->invokeCardSelling(cardInvenotryButton));
            Platform.runLater(()->{
                addButtonToPaneAnimation(List.of((Button)cardInvenotryButton));
            });
        }
    }

    private void handleNewLobby(String str[]) {
        DefaultLobby defaultLobby = new DefaultLobby(str[2],str[3].replace("&"," "),str[4],str[5]);
        userLobbies.put(Integer.parseInt(str[1]) ,defaultLobby);
        if(this.status == Status.LOBBIES) {
            LobbyButton lobbyButton = new LobbyButton(Integer.parseInt(str[1]),defaultLobby);
            lobbyButton.setOnAction(e->{invokeLobbyEnter(lobbyButton);});
            Platform.runLater(() -> {
                addButtonToPaneAnimation(List.of((Button) lobbyButton));
            });
        }
    }

    private  void deleteButtonFromPaneAndList(Class<?> classVrt, String id) {
        Integer button_ID = Integer.parseInt(id);
        Node flowNode = flowPane.getChildren().get(0);

        if (CardInvenotryButton.class == (classVrt)) {
           DefaultCard cardToDelte =  userCardsInventory.remove(button_ID);
           userCardsInventoryHistory.remove(button_ID);
           if(flowNode instanceof  CardInvenotryButton){
               for (Iterator<Node> it = flowPane.getChildren().iterator(); it.hasNext(); ) {
                   Node node = it.next();
                   if( ((CardInvenotryButton) node).getDefaultCard().hashCode() ==cardToDelte.hashCode()){
                       //flowPane.getChildren().remove(node);
                       deleteFromPaneAnimation(((CardInvenotryButton) node));
                   }
               }
           }
        }
        if (LobbyButton.class == (classVrt)) {
           DefaultLobby lobbyToRemove =  userLobbies.remove(button_ID);
            if(flowNode instanceof  LobbyButton){
                for (Iterator<Node> it = flowPane.getChildren().iterator(); it.hasNext(); ) {
                    Node node = it.next();
                    if( ((LobbyButton) node).getDefaultLobby().hashCode() == lobbyToRemove.hashCode()){
                       // flowPane.getChildren().remove(node);
                        deleteFromPaneAnimation(((LobbyButton) node));
                    }
                }
            }
        }

    }
    public  void deleteFromPaneAnimation(Button cardInvenotryButton1){
        while(true) {
            if (!(this.cardAddAnimation.getStatus() == Animation.Status.RUNNING)) {

                this.cardAddAnimation = new Timeline(new KeyFrame(Duration.ZERO, e -> {
                    cardInvenotryButton1.setDisable(true);
                }), new KeyFrame(Duration.seconds(2)));

                this.cardAddAnimation.setOnFinished(e -> {
                        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.01), new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            cardInvenotryButton1.setScaleX(cardInvenotryButton1.getScaleX() * 0.90);
                            cardInvenotryButton1.setScaleY(cardInvenotryButton1.getScaleY() * 0.90);
                        }
                    }));
                   timeline.setCycleCount(80);
                   timeline.setOnFinished(event -> {
                       this.flowPane.getChildren().remove(cardInvenotryButton1);
                   });
                   timeline.play();
                });
                this.cardAddAnimation.setCycleCount(1);
                this.cardAddAnimation.play();

            }
            break;
        }
    }

    private void logIN() {
        sendToServer("LOGIN " +this.login + " " + this.password);
    }

    private void initializeDisconnectItem(){
        disconnectItem.setOnAction( e->{
            connected(false);
        } );
    }

    public void sendToServer(String s){
        try {
            outputStream.write((s + "\n").getBytes());
            outputStream.flush();
            logger.log(Level.INFO,"SOCKET OUTPUT : " + s);
        }catch (IOException e){
            e.printStackTrace();
        }
    }




    private void alertWindow(String contentText){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
        alert.setTitle("Alert");
        alert.setHeaderText(null);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
    private void setClientStatus(String str){
        Platform.runLater(()->{
        if(str.equals("offline")){
            userStatusLabel.setTextFill(Color.RED);
            userStatusLabel.setText("offline");
        }else if(str.equals("online")){
            userStatusLabel.setText("online");
            userStatusLabel.setTextFill(Color.GREEN);
        }
    });
    }

    private void connectedCountListener() {
        AnimationTimer animationTimer=new AnimationTimer() {
            @Override
            public void handle(long now) {
                usersOnlineLabel.setText(usersOnline.getValue());
            }
        };
        animationTimer.start();
    }


    private void connected(boolean b){
       if(b){
            this.isConnected = true;
            setClientStatus("online");
            usersOnline.set("0");
            logInItem.setDisable(true);
            doConnect.setDisable(true);
            disconnectItem.setDisable(false);
            logger.log(Level.INFO,"User has been connected");
        }else {
           this.isConnected = false;
           this.cardAddAnimation.stop();
           this.flowPane.getChildren().clear();
            try {
                if(socket != null && (!socket.isClosed())) {
                    socket.close();
                    logger.log(Level.INFO,"User has been disconnected");
                }
                setClientStatus("offline");
                //Platform.runLater(() -> {bankLabel.setText("0");});
                bankLabel.setText("0");
                usersOnline.set("0");
                logInItem.setDisable(false);
                doConnect.setDisable(false);
                disconnectItem.setDisable(true);

            }catch (IOException e1) {
                alertWindow("Can not disconnect from the server");
            }
        }
    }



}