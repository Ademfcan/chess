package chessserver.Enums;

public enum ProfilePicture {
    DEFAULT("PlayerIcons/defaultpfp.png"),
    ACTRESS("PlayerIcons/actress.png"),
    ARTIST("PlayerIcons/artist.png"),
    BARISTA("PlayerIcons/barista.png"),
    BUISNESSMAN("PlayerIcons/business-man.png"),
    CHEF("PlayerIcons/chef.png"),
    CUSTOMER_SERVICE("PlayerIcons/customer-service.png"),
    DANCER("PlayerIcons/dancer.png"),
    DIVER("PlayerIcons/diver.png"),
    DOCTOR("PlayerIcons/doctor.png"),
    FARMER("PlayerIcons/farmer.png"),
    GUARD("PlayerIcons/guard.png"),
    JUDGE("PlayerIcons/judge.png"),
    MAGICIAN("PlayerIcons/magician.png"),
    POSTMAN("PlayerIcons/postman.png"),
    PROFESSOR("PlayerIcons/professor.png"),
    ROBOT("PlayerIcons/robot.png"),
    SOLDIER("PlayerIcons/soldier.png"),
    SPORTSMAN("PlayerIcons/sportsman.png"),
    STEWARDESS("PlayerIcons/stewardess.png"),
    TEACHER("PlayerIcons/teacher.png");
    public String urlString;

    ProfilePicture(String urlString) {
        this.urlString = urlString;
    }
}
