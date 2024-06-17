package chessserver;

public enum Gametype {
    REGULAR10(10,Gamemode.REGULAR,"reg10") {
        @Override
        String stringValue() {
            return this.getStrVersion();
        }
    },
    REGULAR30(30,Gamemode.REGULAR,"reg30") {
        @Override
        String stringValue() {
            return this.getStrVersion();
        }
    },
    REGULARUNLIMITED(10000,Gamemode.REGULAR,"regUn") {
        @Override
        String stringValue() {
            return this.getStrVersion();
        }
    };

    abstract String stringValue();
    private int length; // in minutes
    private Gamemode mode;

    private String strVersion;

    private Gametype(int length, Gamemode mode,String strVersion) {
        this.length = length;
        this.mode = mode;
        this.strVersion = strVersion;
    }




    public int getLength() {
        return length;
    }

    public Gamemode getMode() {
        return mode;
    }

    public String getStrVersion(){
        return strVersion;
    }

    public static Gametype getType(String string){
        for(Gametype g : Gametype.values()){
            if(g.getStrVersion().equals(string)){
                return g;
            }
        }
        return null;

    }
}
