package server.Lobbie;

import server.DbHandler;
import server.UserConnection;

import java.util.*;

public class Lobby {
    private UserConnection userConnection;
    private Integer host_UserID;
    private String card_ID;
    private Integer statPrice;
    private DbHandler dbHandler;
    private String cardName;
    private String startTime;
    private Map<Integer,List<Integer>> betsMap;
    private List<String> betsInfo;
    private Integer lobby_ID;
    private List<UserConnection> connectionsList;

    public Lobby(Integer lobby_KEY,UserConnection userConnection, List<UserConnection> list , String userID, String card_ID, String statPrice, String cardName, String startTime) {
        this.connectionsList = list;
        this.lobby_ID = lobby_KEY;
        this.userConnection = userConnection;
        this.host_UserID =Integer.parseInt(userID) ;
        this.card_ID = card_ID;
        this.statPrice = Integer.parseInt(statPrice);
        this.dbHandler = DbHandler.getInstance();
        this.cardName = cardName;
        this.startTime = startTime;
        betsInfo = new ArrayList<>();
        initializeBetsMap();
    }

    private void initializeBetsMap() {
        betsMap = new HashMap<>();
        betsMap.put(host_UserID,List.of(statPrice-1)); // the host makes his 1 and last bet which is less then start so if there is no bets he will win

    }
    public Integer getUsersBiggestBet(Integer userID){
        if(betsMap.get(userID) != null && userID != host_UserID) {
            return Collections.max(betsMap.get(userID));
        }
        return 0;
    }

    public void shutDown(){
        System.out.println("--------LOBBY["+lobby_ID+"]--------");
        System.out.println("--------BETS_INFO--------");
        betsInfo.forEach(k-> System.out.println(k.replace(","," ----> ")));
        System.out.println("----BETS MAP----");
        betsMap.forEach((k,v)-> System.out.println(k+":"+v));

        Integer bought_ID = 0;
        Integer bet = 0;

        for(Map.Entry<Integer, List<Integer>> list : betsMap.entrySet()){
            int collBiggestVal = Collections.max(list.getValue());
            if( bet < collBiggestVal){
                bought_ID =list.getKey();
                bet = collBiggestVal;
            }
        }

        if(host_UserID != bought_ID) {
            UserConnection boughtUserConnection = getUserConnection(bought_ID);
            dbHandler.handleSaleOperation(this.host_UserID,bought_ID,card_ID,bet); //+ bank operation
            userConnection.send("CARD_DEL " + card_ID);
            boughtUserConnection.send("CARD_NEW " + dbHandler.getCardData(card_ID));
            userConnection.sendHistory();
            boughtUserConnection.sendHistory();
            userConnection.sendBank();
            boughtUserConnection.sendBank();
        }else {
            userConnection.send("CARD_BACK " +card_ID);
        }
    }

    private UserConnection getUserConnection(Integer bought_id) {
        for(UserConnection s  : connectionsList)
            if(s.getUserID() == bought_id){
                return  s;
            }
        return null;
    }

    public void makeBet(String lobby_Id, String bet, String userID){
        Integer user_ID = Integer.parseInt(userID);
        Integer user_Bet = Integer.parseInt(bet);
        if(this.host_UserID == user_ID){
            this.userConnection.send("WARNING You cant participate in your own auction");
            return;
        }
        if(getTheBiggestBet() >= user_Bet){
            this.userConnection.send("WARNING You have to  bet more then " + getTheBiggestBet());
            return;
        }
        if(betsMap.entrySet().stream().anyMatch(e->e.getKey() == user_ID)) {
            List<Integer> userBets = new ArrayList<>();
            betsMap.entrySet().stream()
                   .filter(e -> e.getKey() == user_ID)
                   .map(Map.Entry::getValue)
                   .findFirst()
                   .get().forEach(e->userBets.add(e));
            userBets.add(user_Bet);
            betsMap.put(user_ID,userBets);


        }else{
            betsMap.put(user_ID, List.of(user_Bet));

        }
        String userName = dbHandler.getUserName(userID);
        betsInfo.add(userName + "," + bet+"$");
        userConnection.sendToAllLogedUsers.accept("LOBBY_BET " + lobby_Id + " " + bet + " " + userName);

    }
    public Integer getTheBiggestBet(){
        int maxBet = 0;
            for( Map.Entry <Integer,List<Integer>> entry : betsMap.entrySet() ){
                int currentSize = Collections.max(entry.getValue());
                maxBet = maxBet > currentSize ? maxBet : currentSize;
            }
            return maxBet;
    }
    public List<String> getBetsInfo(){
        return betsInfo;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }




    public String getCard_ID() {
        return card_ID;
    }

    public void setCard_ID(String card_ID) {
        this.card_ID = card_ID;
    }

    public Integer getHost_UserID() {
        return host_UserID;
    }

    public void setHost_UserID(Integer host_UserID) {
        this.host_UserID = host_UserID;
    }

    public Integer getStatPrice() {
        return statPrice;
    }

    public void setStatPrice(Integer statPrice) {
        this.statPrice = statPrice;
    }


}
