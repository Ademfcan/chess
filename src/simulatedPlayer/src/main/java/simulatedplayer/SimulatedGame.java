package simulatedplayer;

import chessengine.App;
import chessserver.ChessRepresentations.BackendChessPosition;
import chessserver.ChessRepresentations.ChessGame;
import chessserver.ChessRepresentations.ChessMove;
import chessserver.Enums.ComputerDifficulty;
import chessserver.Enums.Gametype;
import chessserver.Enums.INTENT;
import chessserver.Enums.ProfilePicture;
import chessserver.Functions.PgnFunctions;
import chessserver.Misc.RandomUtils;
import chessserver.User.FrontendClient;
import chessserver.User.UserInfo;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.function.Function;

public class SimulatedGame {
    private ChessGame currentGame;
    private ComputerDifficulty gameDifficulty;
    private Function<BackendChessPosition, ChessMove> getComputerMove;
    private Runnable gameOver;
    private SimulatedWS gameClient;
    private FrontendClient simulatedClient;
    public SimulatedGame(Gametype gametype, ComputerDifficulty gameDifficulty,
                         Function<BackendChessPosition, ChessMove> getComputerMove, Runnable gameOver) {
        this.getComputerMove = getComputerMove;
        this.gameOver = gameOver;
        this.gameDifficulty = gameDifficulty;
        this.simulatedClient = getFakeClient();
        this.currentGame = ChessGame.getOnlinePreInit("", "", 0, "");

        this.gameClient = new SimulatedWS(this, simulatedClient);
        this.gameClient.enterGameRequest(gametype.getStrVersion());
    }

    private static String[] names = {
            "James", "Mary", "John", "Patricia", "Robert", "Jennifer", "Michael", "Linda", "William", "Elizabeth",
            "David", "Barbara", "Richard", "Susan", "Joseph", "Jessica", "Thomas", "Sarah", "Charles", "Karen",
            "Christopher", "Nancy", "Daniel", "Lisa", "Matthew", "Betty", "Anthony", "Margaret", "Mark", "Sandra",
            "Donald", "Ashley", "Steven", "Kimberly", "Paul", "Emily", "Andrew", "Donna", "Joshua", "Michelle",
            "Kenneth", "Dorothy", "Kevin", "Carol", "Brian", "Amanda", "George", "Melissa", "Edward", "Deborah",
            "Ronald", "Stephanie", "Timothy", "Rebecca", "Jason", "Sharon", "Jeffrey", "Laura", "Ryan", "Cynthia",
            "Jacob", "Kathleen", "Gary", "Amy", "Nicholas", "Shirley", "Eric", "Angela", "Stephen", "Helen",
            "Jonathan", "Anna", "Larry", "Brenda", "Justin", "Pamela", "Scott", "Nicole", "Brandon", "Emma",
            "Benjamin", "Samantha", "Samuel", "Katherine", "Gregory", "Christine", "Frank", "Debra", "Alexander", "Rachel",
            "Raymond", "Catherine", "Patrick", "Carolyn", "Jack", "Janet", "Dennis", "Ruth", "Jerry", "Maria"
    };

    private FrontendClient getFakeClient() {
        UserInfo fakeUser = new UserInfo();

        fakeUser.setUserelo(gameDifficulty.eloRange);
        String name = RandomUtils.getRandomElement(names);
        fakeUser.setUserName(name);
        fakeUser.setUserEmail(name + "@example.com");
        fakeUser.setProfilePicture(ProfilePicture.random());

        return new FrontendClient(fakeUser);
    }

    public void enteringGame(boolean isPlayer1White) {
        currentGame.updateInfoFromPartialInit("", 0, "", isPlayer1White);
    }

    public void inputMove(String movePgn){
        currentGame.makePgnMove(movePgn);
        playNextMove();
    }

    public void playNextMove(){
        try {
            ChessMove nextMove = getComputerMove.apply(currentGame.getCurrentPosition()
                    .toBackend(currentGame.getGameState(), currentGame.isWhiteTurn()));
            currentGame.makeNewMove(nextMove, true);
            gameClient.sendRequest(INTENT.MAKEMOVE, PgnFunctions.moveToPgn(nextMove,
                    currentGame.getCurrentPosition(), currentGame.getGameState()), null);

        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public void considerRespondingChat(String opponentChat) {
        // ....
    }

    public void endGame() {
        try {
            gameClient.close();
        }
        catch(IOException e){
            throw new RuntimeException(e);
        }

        gameOver.run();
    }

    public void considerDraw() {

    }
}
