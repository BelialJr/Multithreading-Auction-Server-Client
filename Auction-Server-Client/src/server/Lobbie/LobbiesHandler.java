package server.Lobbie;



import server.DbHandler;
import server.UserConnection;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;

public  class LobbiesHandler implements Handler {
    private Map<Integer, Lobby> lobbiesMap ;
    private ScheduledExecutorService executorService;
    private List<UserConnection> userConnections;
    private final Integer defaultLobbyTime  = 1;
    private static LobbiesHandler lobbiesHandler;
    private DbHandler dbHandler ;
    private  final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
    private UserConnection userConnection;

    private LobbiesHandler() {
        this.userConnections = new ArrayList<>();
        this.lobbiesMap  = new HashMap<>();
        this.executorService = Executors.newScheduledThreadPool(5);
        this.dbHandler = DbHandler.getInstance();
    }


    @Override
    synchronized public void  addNewLobby(UserConnection userConnection,String card_ID,String price) {
        this.userConnection = userConnection;
        int lobbyKey = getNextKey() ;
        String startTime = LocalTime.now().format(dtf);
        String cardName = this.dbHandler.getCardName(card_ID).replace(" ", "&");
        lobbiesMap.put(lobbyKey,new Lobby(lobbyKey,userConnection,userConnections,userConnection.userID,card_ID,price,cardName,startTime));
        userConnection.sendToAllLogedUsers.accept("LOBBY_NEW " + lobbyKey + " " + card_ID + " " + cardName + " " + price + " " + startTime);
        executorService.schedule(generateTask(lobbyKey,userConnection),((defaultLobbyTime*60)+2), TimeUnit.SECONDS);
    }
    @Override
    public void makeBET(String lobby_Id, String bet, String userID) {
        Integer lobby_ID = Integer.parseInt(lobby_Id);
        Lobby targerLobby = lobbiesMap.entrySet().stream().filter(e -> e.getKey() == lobby_ID).map(Map.Entry::getValue).findFirst().get();
        if (hasEnoughMoney(lobby_Id,bet,userID)) {
            targerLobby.makeBet(lobby_Id, bet, userID);
        }
    }

    private boolean hasEnoughMoney(String lobby_Id, String bet, String user_ID) {
        Integer lobbyID = Integer.parseInt(lobby_Id);
        Integer userBet = Integer.parseInt(bet);
        Integer userID =  Integer.parseInt(user_ID);
        Integer userBanK = Integer.parseInt(dbHandler.getUserBank(userID));
        System.out.println(lobby_Id + "," + userBet+ "," + user_ID+ "," + userBanK);
        Integer sum = 0;
        List<String> bets = new ArrayList<>();
        // Should count sum  of all bets besides this lobby and check if user has enough money

        for(Map.Entry<Integer, Lobby> entry : lobbiesMap.entrySet()){
            if(entry.getKey() != lobbyID){
                int lobbyBet =  entry.getValue().getUsersBiggestBet(userID);
                bets.add("Lobby_ID  : " + entry.getKey() + " Your BET : "+lobbyBet );
                sum += entry.getValue().getUsersBiggestBet(userID);
            }
        }
        if(sum + userBet >  userBanK) {
            this.userConnection.send("WARNING " +
                    "Your bank account balance is " + userBanK + "$ , and you have " + sum + "$ already bet in another lobbie." + bets+
                    "Your max available bet is " + (userBanK - sum) );
            return false;
        }else {
            return true;
        }
    }

    @Override
    public Callable<Void> generateTask(Integer key,UserConnection u) {
        Callable<Void> task = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Lobby lobbie =  lobbiesMap.get(key);
                lobbie.shutDown();// handle Db etc
                lobbiesMap.remove(key);
                u.sendToAllLogedUsers.accept("LOBBY_DEL " + key);
                return null;
            }
        };
        return task;
    }
    public String getLobbysBets() {
        StringBuilder stringBuilder = new StringBuilder();
        if(!lobbiesMap.isEmpty()) {
            lobbiesMap.forEach((k, v) -> {
                if(!v.getBetsInfo() .isEmpty()) {
                    stringBuilder.append(k + "::");
                    for (String s : v.getBetsInfo()) {
                        stringBuilder.append(s + ";");
                    }
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                    stringBuilder.append("#");
                }
            });
        }
        return  stringBuilder.toString();
    }
    public String getLobbyInfo() {
        StringBuilder stringBuilder = new StringBuilder();
        lobbiesMap.forEach( (k,v)->{
            stringBuilder.append(k +"::" + v.getCard_ID()+","+v.getCardName()+","+v.getStatPrice()+","+v.getStartTime()+"#");
        });
        return stringBuilder.toString();
    }

    public void addUserConnection(UserConnection userConnection) {
        userConnections.add(userConnection);
    }
    public void removeUserConnection(UserConnection userConnection) {
        userConnections.remove(userConnection);
    }

    private Integer getNextKey() {
        if(lobbiesMap.isEmpty()) {
            return 1;
        }
        else {
            return lobbiesMap.keySet().stream().max(Integer::compareTo).get() + 1;
        }
    }
    public static LobbiesHandler getInstance(){
        if(lobbiesHandler == null){
            lobbiesHandler =  new LobbiesHandler();
        }
        return lobbiesHandler;
    }


}
interface Handler{
    public void makeBET(String lobby_Id, String bet, String userID);
    public void addNewLobby(UserConnection userConnection,String card_ID,String price) ;
    public Callable<Void> generateTask(Integer key,UserConnection u);
}

