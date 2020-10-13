package server;
import server.StarWarsAPI.StarWarsApi;

//import javax.smartcardio.Card;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;


public class DbHandler {
    private  static Connection connection;
    private static DbHandler dbHandler;
    private final String DB_FILE_PATH = System.getProperty("user.dir")+"\\"+"db\\"+"SQLiteDB.db";

    private DbHandler(){
        try {
            Class.forName("org.sqlite.JDBC");
            File file = new File(DB_FILE_PATH);
            if(file.exists()){
                connection = DriverManager.getConnection("jdbc:sqlite:" + DB_FILE_PATH);
                Server.logger.log(Level.INFO,"Database file has been found" );
                Server.logger.log(Level.INFO,"Successfully connected to database" );
            }else{
                connection = DriverManager.getConnection("jdbc:sqlite:" + DB_FILE_PATH);
                Server.logger.log(Level.INFO,"Failed to find a database file" );
                createDb();
                addDefaultUsers();
                addDefaultCards();
            }
        } catch (ClassNotFoundException | SQLException e) {
            Server.logger.log(Level.SEVERE,"Failed to connect to database" , e);
        }

    }
    public static DbHandler getInstance(){
        if(dbHandler == null){
           dbHandler =  new DbHandler();
        }
        return dbHandler;
    }

    private void createDb(){
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute("CREATE TABLE User (\n" +
                    "    user_ID integer  NOT NULL PRIMARY KEY,\n" +
                    "    login varchar2  NOT NULL,\n" +
                    "    password varchar2  NOT NULL,\n" +
                    "    bank integer  NOT NULL\n" +
                    ");" );
            statement.execute(       "CREATE TABLE Card (\n" +
                    "    card_ID integer NOT NULL PRIMARY KEY,\n" +
                    "    name varchar2  NOT NULL,\n" +
                    "    user_ID integer  ,\n" +
                    "    FOREIGN KEY (user_ID)  REFERENCES User (user_ID) \n" +
                    ");");
            Server.logger.log(Level.INFO,"Database was successfully generated");
        } catch (SQLException e) {
            Server.logger.log(Level.SEVERE,"Create failed for Database",e);
        }
    }


    private void addDefaultUsers(){
        for (int i = 1; i < 11 ; i++) {
            String index = String.valueOf(i);
            addNewUser(index, "user"+index, index, String.valueOf(1000));
        }
    }
    private void addDefaultCards() {
        DefaultCard defaultCards[] = StarWarsApi.getDefaultCards();
        for (int i = 0; i < defaultCards.length ; i++) {
            String index = String.valueOf(i + 1);
            addNewCard(index,defaultCards[i],defaultCards.length- i );
        }
    }
    private void addNewUser(String id,String login,String password,String bank){
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO User values(?,?,?,?)")) {
            statement.setString(1,id);
            statement.setString(2,login);
            statement.setString(3,password);
            statement.setString(4,bank);
            statement.execute();
            Server.logger.log(Level.INFO,"User: [ID: "+  id  + " LOG: "+ login +"] was successfully generated");
        } catch (SQLException e) {
            Server.logger.log(Level.SEVERE,"Failed in add new user to DataBase",e);
            e.printStackTrace();
        }
    }
    private void addNewCard(String id,DefaultCard card,int user_id){
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO Card values(?,?,?)")) {
            statement.setString(1,id);
            statement.setString(2,card.getName());
            statement.setString(3,String.valueOf(user_id));
            statement.execute();
            Server.logger.log(Level.INFO,"Card: [ID: "+  id  + " Name: "+ card.getName() +"] was successfully generated");
        } catch (SQLException e) {
            Server.logger.log(Level.SEVERE,"Failed in add new card to DataBase",e);
            e.printStackTrace();
        }
    }


    public  List<String> getAllUsers(){
        try (Statement statement =connection.createStatement()) {
            List<String> users = new ArrayList<String>();
            ResultSet resultSet = statement.executeQuery("SELECT * from User");
            System.out.println("----------------------Users----------------------");
            while (resultSet.next()) {
                users.add("user_ID : " + resultSet.getString("user_ID") + " , " + "login : " +  resultSet.getString("login") + " , "
                        +"password : " +resultSet.getString("password")+ " , " +"bank  :"  + resultSet.getString("bank"));
            }

            return users;

        } catch (SQLException e) {
            Server.logger.log(Level.SEVERE,"Failed to get all users from DataBase",e);
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    public List<String>  getAllCards(){
        try (Statement statement =connection.createStatement()) {
            List<String> users = new ArrayList<String>();
            ResultSet resultSet = statement.executeQuery("SELECT * from Card");
            System.out.println("----------------------Cards----------------------");
            while (resultSet.next()) {
                users.add("card_ID : " + resultSet.getString("card_ID") + " , " + "name : " + resultSet.getString("name")
                        + " , " + "user_ID : " + resultSet.getString("user_ID"));
            }
            return users;

        } catch (SQLException e) {
            Server.logger.log(Level.SEVERE,"Failed to get all cards from DataBase",e);
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    public boolean tryToLogIn(String login , String password){
        return true;
    }

    public static void main(String[] args) {
        DbHandler dbHandler = DbHandler.getInstance();
        dbHandler.getAllUsers().forEach(System.out::println);
        dbHandler.getAllCards().forEach(System.out::println);
    }

}
