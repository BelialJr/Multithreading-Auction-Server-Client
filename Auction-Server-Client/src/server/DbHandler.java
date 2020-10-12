package server;
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
                addDefaultCardsToUsers();
            }
        } catch (ClassNotFoundException | SQLException e) {
            Server.logger.log(Level.SEVERE,"Failed to connect to database" , e);
        }

    }

    private void addDefaultCardsToUsers() {

    }

    private void addDefaultCards() {
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
                    "    user_ID integer  NOT NULL,\n" +
                    "    FOREIGN KEY (user_ID)  REFERENCES User (user_ID) \n" +
                    ");");
            Server.logger.log(Level.INFO,"Database was successfully generated");
        } catch (SQLException e) {
            Server.logger.log(Level.SEVERE,"Create failed for Database",e);
        }
    }
    public  List<String> getAllUsers(){
        try (Statement statement =connection.createStatement()) {
            List<String> users = new ArrayList<String>();
            ResultSet resultSet = statement.executeQuery("SELECT * from User");

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

    private void addDefaultUsers(){
        for (int i = 1; i < 10 ; i++) {
            String index = String.valueOf(i);
            addNewUser(index, "user"+index, index, String.valueOf(1000));
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

    public static void main(String[] args) {
        DbHandler dbHandler = DbHandler.getInstance();
        dbHandler.getAllUsers().forEach(System.out::println);
    }

}
