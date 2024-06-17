package chessengine;

public enum ThemesGlobal {
    Light("/CSSFiles/nord-light.css",true){
        @Override
        String getCssLocation() {
            return this.cssLocation;
        }
    },
    Dark("/CSSFiles/nord-dark.css",false){
        @Override
        String getCssLocation() {
            return this.cssLocation;
        }
    };
    abstract String getCssLocation();
    final String cssLocation;
    final Boolean isLight;
    private ThemesGlobal(String cssLocation,boolean isLight){
        this.cssLocation = cssLocation;
        this.isLight = isLight;
    }
}

