package server.TableViewCLasses;

public class TableViewCard {
    private String  cardId;
    private String  name ;
    private String  height ;
    private String  skinColor;
    private String  birthYear ;
    private String  gender ;
    private String  userID;

    public TableViewCard(String cardId, String name, String height, String skinColor, String birthYear, String gender, String userID) {
        this.cardId = cardId;
        this.name = name;
        this.height = height;
        this.skinColor = skinColor;
        this.birthYear = birthYear;
        this.gender = gender;
        this.userID = userID;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getSkinColor() {
        return skinColor;
    }

    public void setSkinColor(String skinColor) {
        this.skinColor = skinColor;
    }

    public String getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(String birthYear) {
        this.birthYear = birthYear;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }


}
