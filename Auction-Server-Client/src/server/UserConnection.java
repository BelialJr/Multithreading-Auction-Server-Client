package server;

import javafx.beans.property.SimpleIntegerProperty;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;
import java.util.logging.Level;


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

            if(!hasBennLogedIn){
                if(str[0].equals("LOGIN")){
                    if(checkLoggedIn(str[1],str[2])){
                        send("LOGIN PASSED");
                        sendStandartDataPackage();
                    }else{
                        send("WARNING Failed to log in");
                        send("LOGIN FAILED");
                    }
                }
            }
            else
            {

            }
        }
        throw new ClassNotFoundException();
    }

    private void sendStandartDataPackage(){
        send("USERSONLINE "+usersOnline.getValue());
    }

    private boolean checkLoggedIn(String login , String password){
        if(dbHandler.tryToLogIn(login,password))
        {
            hasBennLogedIn = true;
            Server.logger.log(Level.INFO, user.toString() + " has been logged in");
        }
        else
         {
            hasBennLogedIn = false;
            Server.logger.log(Level.INFO, user.toString() + " has not been logged in");
        }
        return  hasBennLogedIn;
    }

    private void initializeUsersOnlineListener() {
        this.usersOnline.addListener(e->{
            //this.sendToAllLogedUsers.accept("USERSONLINE "+usersOnline.getValue()); //anyway is binded to every users property
            this.send("USERSONLINE "+usersOnline.getValue());
        });
    }

    public boolean hasBennLogedIn(){
        return hasBennLogedIn;
    }
    public void send(String string){
            try {
                if(!socket.isClosed()) {
                    os.write((string + "\n").getBytes());
                    os.flush();
                    Server.logger.log(Level.FINE, "Message sent to" + user.toString() + " : " + string);
                }
             } catch (IOException ignore) {

            }

    }

}
