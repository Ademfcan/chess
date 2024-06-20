package chessserver;

public enum ChessboardTheme {
    TRADITIONAL("#9e7a3a", "#2e120b") {
        @Override
        String getLightColor() {
            return this.lightColor;
        }

        @Override
        String getDarkColor() {
            return this.darkColor;
        }
    },
    ICE("#7FDEFF", "#4F518C") {
        @Override
        String getLightColor() {
            return this.lightColor;
        }

        @Override
        String getDarkColor() {
            return this.darkColor;
        }
    },

    HALLOWEEN("#ff6619", "#241711") {
        @Override
        String getLightColor() {
            return this.lightColor;
        }

        @Override
        String getDarkColor() {
            return this.darkColor;
        }
    },
    SUMMER("#f7cc0a", "#22668D") {
        @Override
        String getLightColor() {
            return this.lightColor;
        }

        @Override
        String getDarkColor() {
            return this.darkColor;
        }
    },
    CHERRY("#f7b2ad", "#8c2155") {
        @Override
        String getLightColor() {
            return this.lightColor;
        }

        @Override
        String getDarkColor() {
            return this.darkColor;
        }
    };
    abstract String getLightColor();
    abstract String getDarkColor();
    public String lightColor;
    public String darkColor;
    private ChessboardTheme(String lightColor,String darkColor){
        this.lightColor = lightColor;
        this.darkColor = darkColor;
    }

    public static ChessboardTheme getCorrespondingTheme(String themeString){
        for(ChessboardTheme t : ChessboardTheme.values()){
            if(t.toString().equals(themeString)){
                return t;
            }
        }
//        ChessConstants.mainLogger.error("ChessboardTheme incorrect themestring provided");
        return null;
    }

}
