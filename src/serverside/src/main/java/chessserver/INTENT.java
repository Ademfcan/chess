package chessserver;

public enum INTENT {
    GETUSER(true),
    PUTUSER(true),
    CHANGEPASSWORD(true),
    SENDRECOVERYEMAIL(true),
    CREATEGAME(false),
    MAKEMOVE(false),
    LEAVEGAME(false),
    SENDCHAT(false),
    PULLTOTALPLAYERCOUNT(false),
    GETNUMBEROFPOOLERS(false),
    GETPOOLERS(false),
    CLOSESESS(false),
    GAMEFINISHED(false);
    boolean isDbRelated;
    private INTENT(boolean isDbRelated){
        this.isDbRelated = isDbRelated;
    }
}
