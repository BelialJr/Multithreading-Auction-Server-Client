package server;
import java.io.File;
import java.sql.*;
import java.util.logging.Level;

public class DbHandler {
    private  static Connection connection;
    private static DbHandler dbHandler;
    private final String DB_FILE_PATH = System.getProperty("user.dir")+"\\"+"db\\"+"SQLiteDB.db";

    private DbHandler(){
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + DB_FILE_PATH);
            File file = new File(DB_FILE_PATH);
            if(file.exists()){
                Server.logger.log(Level.INFO,"Database file has been found" );
                Server.logger.log(Level.FINE,"Successfully connected to database" );
            }else{
                Server.logger.log(Level.INFO,"Failed to find a database file" );
                createDb();

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
                    "    user_ID integer  NOT NULL,\n" +
                    "    FOREIGN KEY (user_ID)  REFERENCES User (user_ID) \n" +
                    ");");
            Server.logger.log(Level.FINE,"Database was successfully generated");
        } catch (SQLException e) {
            Server.logger.log(Level.SEVERE,"Create failed for Database",e);
        }
    }
    private void addStandartUser(){
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
            Server.logger.log(Level.FINE,"Database was successfully generated");
        } catch (SQLException e) {
            Server.logger.log(Level.SEVERE,"Create failed for Database",e);
        }
    }

}
