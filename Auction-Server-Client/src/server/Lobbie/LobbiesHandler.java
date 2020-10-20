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
    private Consumer<String> sendToAllUsers;
    private Consumer<String> sendToUser;
    private ScheduledExecutorService executorService;
    private List<UserConnection> userConnections;
    private final Integer defaultLobbyTime  = 1;
    private static LobbiesHandler lobbiesHandler;
    private DbHandler dbHandler ;
    private  final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");

    private LobbiesHandler() {
        userConnections = new ArrayList<>();
        this.lobbiesMap  = new HashMap<>();
        this.executorService = Executors.newScheduledThreadPool(1);
        this.dbHandler = DbHandler.getInstance();
    }


    @Override
    public void handle(int key) {

    }

    @Override
    synchronized public void  addNewLobby(UserConnection userConnection,String card_ID,String price) {
        int lobbyKey = getNextKey() ;
        String startTime = LocalTime.now().format(dtf);
        lobbiesMap.put(lobbyKey,new Lobby(userConnection.userID,card_ID,price,startTime));
        userConnection.sendToAllLogedUsers.accept("LOBBY_NEW " + lobbyKey + " " +card_ID + " " +  this.dbHandler.getCardName(card_ID).replace(" ","#") +" "+ price + " " + startTime);
        executorService.schedule(generateTask(lobbyKey,userConnection),((defaultLobbyTime*60)+2), TimeUnit.SECONDS);
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
    public void handle(int key);
    public void addNewLobby(UserConnection userConnection,String card_ID,String price) ;
    public Callable<Void> generateTask(Integer key,UserConnection u);
}

