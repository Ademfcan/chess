package chessserver.Enums;

public enum INTENT {
    /** requires a response action**/
    GETUSER(true),
    /** does not require a response action**/
    PUTUSER(true),
    /** does not require a response action**/
    CHANGEPASSWORD(true),
    /** does not require a response action**/
    SENDRECOVERYEMAIL(true),
    /** does not require a response action**/
    UPDATEUSER(true),
    /** does not require a response action**/
    DELETEUSER(true),
    /** requires a response action**/
    CHECKUSERNAME(true),
    /** requires a response action**/
    CREATEGAME(false),
    /** does not require a response action**/
    MAKEMOVE(false),
    /** does not require a response action**/
    LEAVEGAME(false),
    SENDCHAT(false),
    /** requires a response action**/
    PULLTOTALPLAYERCOUNT(false),
    /** requires a response action**/
    GETNUMBEROFPOOLERS(false),
    /** requires a response action**/
    GETPOOLERS(false),
    /** does not require a response action**/
    CLOSESESS(false),
    /** requires a response action**/
    GetCurrentUUID(true),
    /** does not require a response action**/
    IncrementUUID(true),
    /** requires a response action**/
    SENDFRIENDREQUEST(true),
    /** requires a response action**/
    GETUUIDS(true),
    /** requires a response action**/
    GETUSERNAMES(true),
    /** requires a response action**/
    READINCOMINGFRIENDREQUESTS(true),
    /** requires a response action**/
    GETRANK(true),
    /** requires a response action**/
    MATCHALLUSERNAMES(true),
    /** requires a response action**/
    SENDACCEPTEDFRIENDREQUEST(true),
    /** requires a response action**/
    READACCEPTEDFRIENDREQUESTS(true),
    /** requires a response action**/
    GETFRIENDDATA(true);
    public boolean isDbRelated;
    private INTENT(boolean isDbRelated){
        this.isDbRelated = isDbRelated;
    }
}
