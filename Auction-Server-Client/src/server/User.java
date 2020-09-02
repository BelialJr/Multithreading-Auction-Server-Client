package server;


public class User {
    private String hostName;
    private int port ;
    private int token;
    public User(String gostName, int port,int Token) {
        this.hostName = gostName;
        this.port = port;
        this.token = Token;
    }
    public String toString(){
        return "User [ ip: " + this.hostName + " : port: " + this. port + " ; Token: " + + this.token + "]";
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }
}