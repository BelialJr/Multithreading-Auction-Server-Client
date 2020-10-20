package client;

public class SaleStory {
    private String status;
    private  String  sale_ID ;
    private  String  cardName ;
    private  String  saleLogin ;
    private  String  buyLogin ;
    private  String  price  ;

    public String getSale_ID() {
        return sale_ID;
    }

    public void setSale_ID(String sale_ID) {
        this.sale_ID = sale_ID;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getSaleLogin() {
        return saleLogin;
    }

    public void setSaleLogin(String saleLogin) {
        this.saleLogin = saleLogin;
    }

    public String getBuyLogin() {
        return buyLogin;
    }

    public void setBuyLogin(String buyLogin) {
        this.buyLogin = buyLogin;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public SaleStory(String sale_ID, String cardName, String saleLogin, String buyLogin, String price) {
        this.sale_ID = sale_ID;
        this.cardName = cardName;
        this.saleLogin = saleLogin;
        this.buyLogin = buyLogin;
        this.price = price;
        this.status = generateStatus();
    }

    private String generateStatus() {
        if(Controller.login.equals(this.saleLogin))
            return "SOLD";
        else
            return "BOUGHT";
    }

    public SaleStory(String wrt) {
        String str[] = wrt.split(",");
        this.sale_ID = str[0];
        this.cardName = str[1];
        this.saleLogin = str[2];
        this.buyLogin = str[3];
        this.price = str[4];
        this.status = generateStatus();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
