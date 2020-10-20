package server.TableViewCLasses;

public class TableViewUser {
    private String userId;
    private String login;
    private String password;
    private String bank;

    public TableViewUser(String userId, String login, String password, String bank) {
        this.userId = userId;
        this.login = login;
        this.password = password;
        this.bank = bank;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    @Override
    public String toString() {
        return "TableViewUser{" +
                "userId='" + userId + '\'' +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", bank='" + bank + '\'' +
                '}';
    }
}
