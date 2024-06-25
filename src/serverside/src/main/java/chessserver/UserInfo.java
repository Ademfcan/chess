package chessserver;

public class UserInfo {
    int userelo;
    String userName;
    String userEmail;
    String passwordHash;
    ProfilePicture profilePicture;



    public UserInfo(int userelo, String userName, String userEmail, String password, ProfilePicture profilePicture) {
        this.userelo = userelo;
        this.userName = userName;
        this.userEmail = userEmail;
        this.passwordHash = password;
        this.profilePicture = profilePicture;
    }

    public UserInfo(){
        // empty for objectmapper serialization
    }


    public int getUserelo() {
        return userelo;
    }

    public void adjustElo(int change) {
        this.userelo+=change;
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public ProfilePicture getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(ProfilePicture profilePicture) {
        this.profilePicture = profilePicture;
    }



}
