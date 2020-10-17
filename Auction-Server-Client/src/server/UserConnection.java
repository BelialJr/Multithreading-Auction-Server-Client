package server;


import javafx.beans.property.SimpleIntegerProperty;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;
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
    private String login;
    private String password;
    private String userID; //DB user ID


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
                        login = str[1];
                        password = str[2];
                        this.userID = dbHandler.getUserId(login);
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
        sendBank();
        sendInventory();
        sendLobbies();
        send("STANDART_PACKET_END");
    }

    private void sendBank() {
          String  result = dbHandler.getUserBank(this.login);
          send("BANK " + result);
    }

    private void sendLobbies() {
        send("LOBBIES " );
    }

    private void sendInventory() {
        Map<Integer,DefaultCard> inventory =  dbHandler.getUserInventory(this.login); // INVENTORY id::"Obi-Wan Kenobi","182","fair","57BBY","male"#...
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("INVENTORY ");
        inventory.forEach((key,card)->{stringBuilder.append(key+"::"+card.toStringV2()+ "#");});
        send(stringBuilder.toString());
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
