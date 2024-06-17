package chessengine;

public enum ThemesChessboard {
    Traditional("Traditional") {
        @Override
        String[] getColorValue() {
            return ThemesChessboard.getColorStr(this.themeName);
        }
    },
    Ice("Ice") {
        @Override
        String[] getColorValue() {
            return ThemesChessboard.getColorStr(this.themeName);
        }
    },
    Halloween("Halloween") {
        @Override
        String[] getColorValue() {
            return ThemesChessboard.getColorStr(this.themeName);
        }
    },
    Summer("Summer") {
        @Override
        String[] getColorValue() {
            return ThemesChessboard.getColorStr(this.themeName);
        }
    },
    Cherry("Cherry"){
        @Override
        String[] getColorValue() {
            return ThemesChessboard.getColorStr(this.themeName);
        }
    };
    abstract String[] getColorValue();

    public final String themeName;
    private ThemesChessboard(String themeName){
        this.themeName = themeName;
    }

    private static String[] getColorStr(String colortype) {
        return switch (colortype) {
            case "Ice" -> new String[]{"#7FDEFF", "#4F518C"};
            case "Traditional" -> new String[]{"#9e7a3a", "#2e120b"};
            case "Halloween" -> new String[]{"#ff6619", "#241711"};
            case "Summer" -> new String[]{"#f7cc0a", "#22668D"};
            case "Cherry" -> new String[]{"#f7b2ad", "#8c2155"};
            default -> null;
        };
    }
}
