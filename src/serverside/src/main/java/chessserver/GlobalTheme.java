package chessserver;

public enum GlobalTheme {
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
    public final String cssLocation;
    final Boolean isLight;
    private GlobalTheme(String cssLocation, boolean isLight){
        this.cssLocation = cssLocation;
        this.isLight = isLight;
    }
}

