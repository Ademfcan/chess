package chessserver;

import jakarta.websocket.Session;
import java.util.Objects;

public class Client implements Comparable<Client>{


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getElo() {
        return Elo;
    }

    public void setElo(int elo) {
        Elo = elo;
    }

    private String name;



    private int Elo;
    public Client(String name,int elo){
        this.name = name;
        this.Elo = elo;

    }

    public Client(){
        // for json serialization
    }





    @Override
    public int compareTo(Client other) {
        // Define comparison logic, e.g., based on ELO or another attribute
        return Integer.compare(this.Elo, other.Elo);
    }


}


