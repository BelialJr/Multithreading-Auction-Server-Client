package server;
import server.StarWarsAPI.StarWarsApiV2;
import server.TableViewCLasses.TableViewCard;
import server.TableViewCLasses.TableViewUser;

//import javax.smartcardio.Card;
import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;


public class DbHandler {
    private  static Connection connection;
    private static DbHandler dbHandler;
    private final String DB_FILE_PATH = System.getProperty("user.dir")+"\\"+"db\\"+"SQLiteDB.db";
    private DefaultCard defaultCards[];

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
                generateDefaultUsers();
                generateDefaultCards();
                generateDefaultHistory();
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
            statement.execute("PRAGMA foreign_keys = ON;");
            statement.execute("CREATE TABLE User (\n" +
                    "    user_ID integer  NOT NULL PRIMARY KEY,\n" +
                    "    login varchar2  NOT NULL,\n" +
                    "    password varchar2  NOT NULL,\n" +
                    "    bank integer  NOT NULL\n" +
                    ");" );
            statement.execute(       "CREATE TABLE Card (\n" +
                    "    card_ID integer NOT NULL PRIMARY KEY,\n" +
                    "    name varchar2  NOT NULL,\n" +
                    "    height varchar2  NOT NULL,\n" +
                    "    skin_color varchar2  NOT NULL,\n" +
                    "    birth_year varchar2  NOT NULL,\n" +
                    "    gender varchar2  NOT NULL,\n" +
                    "    user_ID integer  ,\n" +
                    "    FOREIGN KEY (user_ID)  REFERENCES User (user_ID) \n" +
                    ");");
            statement.execute(    "CREATE TABLE SaleHistory (\n" +
                    "sale_ID integer,\n" +
                    "sellUser_ID integer,\n" +
                    "buyUser_ID integer,\n" +
                    "card_ID integer,\n" +
                    "price integer NOT NULL,\n" +
                    "PRIMARY KEY(sale_ID,sellUser_ID,buyUser_ID,card_ID),\n" +
                    "FOREIGN KEY(sellUser_ID) REFERENCES User (user_ID),\n" +
                    "FOREIGN KEY(card_ID)     REFERENCES Card (card_ID),\n" +
                    "FOREIGN KEY(buyUser_ID) REFERENCES User (user_ID)\n" +
                    ");");
            Server.logger.log(Level.INFO,"Database was successfully generated");
        } catch (SQLException e) {
            Server.logger.log(Level.SEVERE,"Create failed for Database",e);
        }
    }


    private void generateDefaultUsers(){
        for (int i = 1; i < 11 ; i++) {
            String index = String.valueOf(i);
            addNewUser(index, "user"+index, index, String.valueOf(1000));
        }
    }
    private void generateDefaultCards() {
        this.defaultCards = StarWarsApiV2.getDefaultCards(80);
        for (int i = 0; i < this.defaultCards .length ; i++) {
            String index = String.valueOf(i + 1);
            addNewCard(index,this.defaultCards [i],((i%10)+1));
        }
    }
    private void generateDefaultHistory(){
        Random random = new Random();
        int lowestBet = 100;
        int biggestBet = 950;
        int index = 1;
        int price  = 0;
        for (int card_ID = 1; card_ID < this.defaultCards.length+1; card_ID++) {
            for (int j = 0; j < 10; j++) {
                price = random.nextInt(biggestBet-lowestBet) + lowestBet;
                addNewSaleHistory(String.valueOf(index++),String.valueOf( card_ID),String.valueOf(price)) ;
            }
        }
    }

    private void addNewSaleHistory(String sale_ID, String card_ID, String price) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO SaleHistory values(?,?,?,?,?)")) {
            statement.setString(1,sale_ID);
            statement.setString(2,null);
            statement.setString(3,null);
            statement.setString(4,card_ID);
            statement.setString(5,price);
            statement.execute();
            Server.logger.log(Level.INFO,"SaleHistory: [Sale_ID: "+  sale_ID  + " Card_ID: "+ card_ID + " Price: "+ price + " ] was successfully generated");
        } catch (SQLException e) {
            Server.logger.log(Level.SEVERE,"Failed in generate new salehistory to DataBase",e);
            e.printStackTrace();
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
                "INSERT INTO Card values(?,?,?,?,?,?,?)")) {
            statement.setString(1,id);
            statement.setString(2,card.getName());
            statement.setString(3,card.getHeight());
            statement.setString(4,card.getSkin_color());
            statement.setString(5,card.getBirth_year());
            statement.setString(6,card.getGender());
            statement.setString(7,String.valueOf(user_id));
            statement.execute();
            Server.logger.log(Level.INFO,"Card: [ID: "+  id  + " Name: "+ card.getName() +"] was successfully generated");
        } catch (SQLException e) {
            Server.logger.log(Level.SEVERE,"Failed in add new card to DataBase " + card.toStringV2(),e);
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
                        + " , " + "height : " + resultSet.getString("height")
                        + " , " + "skin_color : " + resultSet.getString("skin_color")
                        + " , " + "birth_year : " + resultSet.getString("birth_year")
                        + " , " + "gender : " + resultSet.getString("gender")
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
        try (Statement statement =connection.createStatement()) {
            String query = String.format("SELECT * from User WHERE LOGIN = \"%s\" AND PASSWORD = \"%s\"",login,password);
            ResultSet resultSet = statement.executeQuery(query);
            if(resultSet.next()){
                return true;
            }
            return false;
        } catch (SQLException e) {
            Server.logger.log(Level.SEVERE,"Failed to log user",e);
            e.printStackTrace();
            return false;
        }
    }



    public static void main(String[] args) {
        DbHandler dbHandler = DbHandler.getInstance();
        dbHandler.getAllUsers().forEach(System.out::println);
        dbHandler.getAllCards().forEach(System.out::println);

    }

    public String getUserBank(String login) {
        try (Statement statement =connection.createStatement()) {
            String query = String.format("SELECT * from User WHERE LOGIN = \"%s\" ",login);
            ResultSet resultSet = statement.executeQuery(query);
            if(resultSet.next()){
                return resultSet.getString("bank");
            }
            return "0";
        } catch (SQLException e) {
            Server.logger.log(Level.SEVERE,"Failed to get user bank",e);
            e.printStackTrace();
            return "0";
        }
    }

    public Map<Integer,DefaultCard> getUserInventory(String login) {
        Map<Integer,DefaultCard> resultInvenory= new HashMap<>();
        try (Statement statement =connection.createStatement()) {
            String query = String.format("SELECT * from User WHERE LOGIN = \"%s\" ",login);
            ResultSet resultSet1 = statement.executeQuery(query);
            String user_ID = null;
            if(resultSet1.next())
            {
              user_ID =  resultSet1.getString("user_ID");
            }
            ResultSet resultSet2 = statement.executeQuery(String.format("SELECT * from CARD WHERE  USER_ID =\"%s\"  ",user_ID));
            while(resultSet2.next())
            {
                Integer cardID = Integer.parseInt(resultSet2.getString("card_ID"));
                DefaultCard defaultCard =  new DefaultCard(resultSet2.getString("name"),
                        resultSet2.getString("height"),
                        resultSet2.getString("skin_color"),
                        resultSet2.getString("birth_year"),
                        resultSet2.getString("gender"));
                resultInvenory.put(cardID,defaultCard);

            }
        } catch (SQLException e) {
            Server.logger.log(Level.SEVERE,"Failed to get user inventory",e);
            e.printStackTrace();
            return Collections.EMPTY_MAP;
        }
        return  resultInvenory;

    }


    public String getUserId(String login) {
        try (Statement statement = connection.createStatement()) {
            String query = String.format("SELECT * from User WHERE LOGIN = \"%s\" ", login);
            ResultSet resultSet1 = statement.executeQuery(query);
            String user_ID = null;
            if (resultSet1.next()) {
                user_ID = resultSet1.getString("user_ID");
            }
            return user_ID;
        } catch (SQLException e) {
            e.printStackTrace();
            return "0";
        }
    }

    public List<TableViewUser> getTableViewUsers(){
        try (Statement statement =connection.createStatement()) {
            List<TableViewUser> users = new ArrayList<TableViewUser>();
            ResultSet resultSet = statement.executeQuery("SELECT * from User");
            while (resultSet.next()) {
                users.add(new TableViewUser(
                        resultSet.getString("user_ID") ,
                        resultSet.getString("login") ,
                        resultSet.getString("password"),
                        resultSet.getString("bank")) );
            }
            return users;
        } catch (SQLException e) {
            Server.logger.log(Level.SEVERE,"Failed to get all users from DataBase",e);
            e.printStackTrace();
            return Collections.emptyList();
        }
    }


    public List<TableViewCard> getTableViewCards(){
        try (Statement statement =connection.createStatement()) {
            List<TableViewCard> users = new ArrayList<TableViewCard>();
            ResultSet resultSet = statement.executeQuery("SELECT * from Card");
            while (resultSet.next()) {
                users.add( new TableViewCard( resultSet.getString("card_ID") ,
                        resultSet.getString("name"),
                        resultSet.getString("height"),
                        resultSet.getString("skin_color"),
                        resultSet.getString("birth_year"),
                        resultSet.getString("gender"),
                        resultSet.getString("user_ID")) );
            }
            return users;

        } catch (SQLException e) {
            Server.logger.log(Level.SEVERE,"Failed to get all cards from DataBase",e);
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    public String getCardName(String card_ID){
        try (Statement statement =connection.createStatement()) {
            String query = String.format("SELECT name from Card\n" +
                    "WHERE card_ID = \"%s\" ",card_ID);
            ResultSet resultSet = statement.executeQuery(query);
            if(resultSet.next())
            {
               return  resultSet.getString("name");

            }
            return "";
        } catch (SQLException e) {
            Server.logger.log(Level.SEVERE,"Failed to get card name",e);
            e.printStackTrace();
            return "";
        }
    }

    public String getUserHistory(String userID){
        StringBuilder stringBuilder = new StringBuilder();

        try (Statement statement =connection.createStatement()) {
            String query = String.format("SELECT sale_ID,Card.name as \"cardName\",saleColumn.login as \"saleLogin\",buyColumn.login as \"buyLogin\",price  from SaleHistory\n" +
                    "Inner Join Card on Card.card_ID = SaleHistory.card_ID\n" +
                    "INNER JOIN User saleColumn on saleColumn.user_ID = SaleHistory.sellUser_ID \n" +
                    "INNER Join USer buyColumn on  buyColumn.user_ID = SaleHistory.buyUser_ID " +
                    "WHERE SaleHistory.sellUser_ID = \"%s\" OR SaleHistory.buyUser_ID = \"%s\"",userID,userID);
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next())
            {
                String  sale_ID = resultSet.getString("sale_ID");
                String  cardName = resultSet.getString("cardName");
                String  saleLogin =        resultSet.getString("saleLogin");
                String  buyLogin =       resultSet.getString("buyLogin");
                String  price =       resultSet.getString("price");
                stringBuilder.append(sale_ID+","+cardName+","+saleLogin+","+buyLogin+","+price+"#");
            }

            return  stringBuilder.toString();
        } catch (SQLException e) {
            Server.logger.log(Level.SEVERE,"Failed to get user history",e);
            e.printStackTrace();
            return "";
        }
    }

    public String getCardHistory(String userID){
        StringBuilder stringBuilder = new StringBuilder();

        try (Statement statement =connection.createStatement()) {
            String query = String.format("Select sale_ID ,CARD.name as \"cardName\",price from SaleHistory \n" +
                    "INNER JOIN CARD on Card.card_ID = SaleHistory.card_ID \n" +
                    "WHERE SaleHistory.card_ID =  \"%s\";",userID);
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next())
            {
                String  sale_ID = resultSet.getString("sale_ID");
                String  cardName = resultSet.getString("cardName");
                String  price =       resultSet.getString("price");
                stringBuilder.append(sale_ID+","+cardName+","+price+"#");
            }

            return  stringBuilder.toString();
        } catch (SQLException e) {
            Server.logger.log(Level.SEVERE,"Failed to get user history",e);
            e.printStackTrace();
            return "";
        }
    }
}
