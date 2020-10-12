package server;


import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.Task;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    public static final Logger logger = Logger.getLogger(Server.class.getName());
    public static Server server;
    private Contoller contoller;
    private DbHandler dbHandler;
    private ServerSocket serverSocket;
    private boolean serverIsStarted = false;
    private List<UserConnection> connectionList = new ArrayList<>();
    private SimpleIntegerProperty usersOnline = new SimpleIntegerProperty(0);
    private static int  _token = 1;
    private static int currentToken ;
    private UserConnection connection;

    public Server() {
        this.dbHandler = DbHandler.getInstance();
    }

    public void startServer(Integer port,int maxUsers,Contoller contoller) throws IOException {
        this.contoller = contoller;
        openSocket(port, maxUsers);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception{

                while (serverIsStarted) {
                    Socket socket = serverSocket.accept();
                    currentToken = _token++;
                    User user = new User(socket.getInetAddress().getHostName(), socket.getPort(), currentToken);
                    Platform.runLater(()->{contoller.UsersList.getItems().add(user);});
                    logger.log(Level.INFO,user+" joined the server");
                    usersOnline.set(contoller.UsersList.getItems().size());
                    new Thread( () ->{
                        try {
                            connection = new UserConnection(user,socket,dbHandler,usersOnline,this::sendToAllLogedUsers);
                            connectionList.add(connection);
                            connection.startReading();

                        } catch (NullPointerException |IOException | ClassNotFoundException e) {
                            logger.log(Level.INFO,user +   " : just left the server" );
                            connectionList.remove(connection);
                            Platform.runLater(()->{contoller.UsersList.getItems().remove(user);});
                            logger.log(Level.INFO,"New size" + contoller.UsersList.getItems().size());
                            usersOnline.set(contoller.UsersList.getItems().size());
                        }
                    }).start();
                }
                return null;
            }

            public void sendToAllLogedUsers(String string) {
                logger.log(Level.INFO,"MESSAGE FOR ALL CONNECTED USERS : " + string);
                for(UserConnection con:connectionList){
                    if(con.hasBennLogedIn()) {
                        con.send(string);
                    }
                }
            }

        };
        new Thread(task).start();
    }



    private void openSocket(Integer port,int maxUsers) {
        try {
            serverSocket = new ServerSocket(port, maxUsers);
            logger.log(Level.INFO,"Server has been started in port : " + port + " ,with " + maxUsers + " max users");
            serverIsStarted = true;
            this.contoller.setServerStatus("online");
        } catch (IOException e) {
            logger.log(Level.INFO,"Server failed to start in port : " + port + " ,with " + maxUsers + " max users",e);
        }
    }



    public void stopServer(){
        serverIsStarted = false;
        this.contoller.setServerStatus("offline");
        try {
            if(!serverSocket.isClosed()) {

                for(UserConnection con:connectionList){
                    if(con.hasBennLogedIn()) {
                        con.send("WARNING The server has been stopped");
                        con.send("DISCONNECT");
                    }
                }
                serverIsStarted = false;
                this.serverSocket.close();
                logger.log(Level.INFO, "Server has been stopped");
            }
        } catch (IOException e) {
            logger.log(Level.INFO,"Server failed to stop",e);
        }
    }




    public static Server getInstance(){
        if(server == null){
            server = new Server();
        }
        return server;
    }

    private void removeFromUserList(User user){
        AnimationTimer animationTimer=new AnimationTimer() {
            @Override
            public void handle(long now) {
                contoller.UsersList.getItems().remove(user);
            }
        };
        animationTimer.start();
    }



}
