package chessengine.Enums;

public enum MainScreenState {
    VIEWER,
    ONLINE,
    SANDBOX,
    LOCAL,
    SIMULATION,
    CAMPAIGN,
    PUZZLE;

    public static boolean isSaveableState(MainScreenState state){
        return state == LOCAL || state == ONLINE || state == CAMPAIGN;
    }
    public static boolean isEvalAllowed(MainScreenState state){
        return state == VIEWER || state == SIMULATION || state == SANDBOX;
    }


}
