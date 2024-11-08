package chessserver.User;

public class Client implements Comparable<Client> {


    @Override
    public String toString() {
        return "Client{" +
                "info=" + info +
                '}';
    }

    UserInfo info;


    public Client(UserInfo info) {
        this.info = info;

    }

    public Client() {
        // for json serialization
    }

    public UserInfo getInfo() {
        return info;
    }

    public void setInfo(UserInfo info) {
        this.info = info;
    }


    @Override
    public int compareTo(Client other) {
        return Integer.compare(this.info.getUserelo(), other.info.getUserelo());
    }


}


