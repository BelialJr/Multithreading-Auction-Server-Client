package server;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    public static final Logger logger = Logger.getLogger(Server.class.getName());
    private DbHandler dbHandler;


    public Server(DbHandler dbHandler) {
        this.dbHandler = DbHandler.getInstance();

    }

}
