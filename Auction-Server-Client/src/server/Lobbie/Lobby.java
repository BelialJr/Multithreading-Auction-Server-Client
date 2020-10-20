package server.Lobbie;

import server.DbHandler;

public class Lobby {
    private String userID;
    private String card_ID;
    private String price;
    private DbHandler dbHandler;
    public Lobby(String id, String userID, String card_ID, String price) {
        this.userID = userID;
        this.card_ID = card_ID;
        this.price = price;
        this.dbHandler = DbHandler.getInstance();
    }


    public void shutDown() {

    }
}
