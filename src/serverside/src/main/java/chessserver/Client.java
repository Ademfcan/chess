package chessserver;

import javax.websocket.Session;
import java.util.Objects;

public class Client implements Comparable<Client>{




    UserInfo info;



    public Client(UserInfo info){
        this.info = info;

    }

    public Client(){
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
        return Integer.compare(this.info.userelo, other.info.userelo);
    }


}


