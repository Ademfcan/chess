package chessserver;

public class UserInfo {
    int userelo;
    String userName;
    String userEmail;
    String password;

    public UserInfo(int userelo, String userName, String userEmail, String password) {
        this.userelo = userelo;
        this.userName = userName;
        this.userEmail = userEmail;
        this.password = password;
    }

    public UserInfo(){
        // empty for objectmapper serialization
    }


    public int getUserelo() {
        return userelo;
    }

    public void setUserelo(int userelo) {
        this.userelo = userelo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }




}
