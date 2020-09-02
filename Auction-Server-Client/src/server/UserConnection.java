package server;

import javafx.beans.property.SimpleIntegerProperty;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserConnection{
    private boolean hasBennLogedIn = false;
    private InputStream is;
    private OutputStream os;
    private Socket socket;
    private Consumer<String> sendToAllLogedUsers ;
    private DbHandler dbHandler;
    private User user;
    SimpleIntegerProperty usersOnline = new SimpleIntegerProperty(0);



    public UserConnection( User user , Socket socket,  DbHandler dbHandler,SimpleIntegerProperty usersOnline,Consumer<String> sendToAllLogedUsers) throws IOException {
        this.socket = socket;
        this.is = this.socket.getInputStream();
        this.os = this.socket.getOutputStream();
        this.sendToAllLogedUsers = sendToAllLogedUsers;
        this.user = user;
        this.dbHandler = dbHandler;
        initializeUsersOnlineListener();
        this.usersOnline.bind(usersOnline);

    }


    public void startReading() throws ClassNotFoundException  , IOException {
        BufferedReader bf  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        while(!socket.isClosed()){
            String line  = bf.readLine();
            String[] str = line.split(" ");
            Server.logger.log(Level.FINE,"SOCKET INPUT :  " +  user.toString() + " : " + line);
            if(str[0].equals("LOGIN")){
                if(checkLoggedIn(str[1],str[2])){
                    send("LOGIN PASSED");
                    sendStandartDataPackage();
                }else{
                    send("LOGIN FAILED");
                    send("DISCONNECT");
                }
            }//if(str[0].equals())
        }
        throw new ClassNotFoundException();
    }

    private void sendStandartDataPackage(){
        send("USERSONLINE "+usersOnline.getValue());
    }

    private boolean checkLoggedIn(String login , String password){
        hasBennLogedIn = true;
        Server.logger.log(Level.FINE,   user.toString() + " has been logged in" );
        return  hasBennLogedIn;
    }
    private void initializeUsersOnlineListener() {
        this.usersOnline.addListener(e->{
            this.sendToAllLogedUsers.accept("USERSONLINE "+usersOnline.getValue());
        });
    }

    public boolean hasBennLogedIn(){
        return hasBennLogedIn;
    }
    public void send(String string){
        if(hasBennLogedIn){
            try {
                os.write((string+ "\n").getBytes());
                os.flush();
                Server.logger.log(Level.FINE,"Message sent to" +  user.toString() + " : " + string);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
