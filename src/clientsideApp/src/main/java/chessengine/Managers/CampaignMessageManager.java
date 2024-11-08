package chessengine.Managers;

import chessengine.CentralControlComponents.ChessCentralControl;
import chessengine.Enums.MainScreenState;
import chessserver.Functions.GeneralChessFunctions;
import chessserver.Enums.CampaignTier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class CampaignMessageManager {
    private static final Logger logger = LogManager.getLogger("Campaign_Message_Manager");
    private final ChessCentralControl centralControl;
    private final Random random = new Random(1784673287);

    // Introduction messages for both player and computer
    private final String[] messageStarts = new String[]{
            "Hello, my name is",
            "Howdy, I'm",
            "Greetings, chess master! I'm",
            "Welcome to the board! I'm",
            "Hello, strategist! I'm",
            "Pleased to meet you! I'm",
            "Greetings, challenger! I'm",
            "Hi there! I'm",
            "Hello! I'm"
    };
    private final String[] messageEnds = new String[]{
            ". Nice to meet you!",
            ".",
            ". Ready for our match?",
            ". Let's see if your moves can outsmart mine.",
            ". Ready to make your next move?",
            ". Shall we begin the game?",
            ". Let's start this chess battle.",
            ". Ready to engage in a game of wits?",
            ". The board is set, shall we play?",
            ". Let's see how you handle my opening.",
            ". Ready to turn your pawns into queens?",
            ". Let's get this game of kings underway."
    };
    private final String[] messageStartsPlayer = new String[]{
            "Your turn to start, strategist!",
            "Ready for your first move?",
            "It's time to begin the battle!"
    };

    // todo make these actually good
    private final String[] messageStartsComputer = new String[]{
            "Hello, my name is",
            "Greetings! I'm",
            "Hi, I'm",
            "Welcome to the board! I'm"
    };
    private final String[] messageEndsComputer = new String[]{
            ". I'll start the game.",
            ". Let's see if your moves can outsmart mine.",
            ". Ready to face a challenge?",
            ". I’ll go first. Watch closely!"
    };
    // Check message variations for player and computer
    private final String[] checkMessageStartsPlayer = new String[]{
            "Nice attack! My king is under threat.",
            "Good job, you've put me in check!",
            "Your pressure is mounting, my king's in danger!"
    };
    private final String[] checkMessageStartsComputer = new String[]{
            "Uh oh, your king is under attack!",
            "Watch out! Your king's in trouble!",
            "Your king is in check, what's your move?"
    };
    private final String[] checkMessageEnds = new String[]{
            " How will you respond?",
            " Can you escape?",
            " What's your plan?"
    };
    // Checkmate message variations for player and computer
    private final String[] checkmateMessageStartsPlayer = new String[]{
            "Well played, you checkmated me!",
            "You've won this match, congratulations!",
            "Impressive, that's checkmate! Great job."
    };
    private final String[] checkmateMessageStartsComputer = new String[]{
            "Checkmate! I won this time.",
            "Game over, I’ve secured a checkmate!",
            "Checkmate! Good effort, but I got you."
    };
    private final String[] checkmateMessageEnds = new String[]{
            " Let's play again soon.",
            " Ready for a rematch?",
            " Next time, I'll be prepared!"
    };
    // Stalemate message variations for player and computer
    private final String[] stalemateMessageStartsPlayer = new String[]{
            "A draw! We were evenly matched this time.",
            "Neither of us could win, it's a stalemate!",
            "It’s a standstill. Well fought!"
    };
    private final String[] stalemateMessageStartsComputer = new String[]{
            "It's a draw! Neither of us could claim victory.",
            "A stalemate! Well played.",
            "We’ve reached a deadlock. Let's call it a draw."
    };
    private final String[] stalemateMessageEnds = new String[]{
            " How about we try again?",
            " Want to go for another match?",
            " That was intense! Let's play again soon."
    };
    // Eating message variations for player and computer
    private final String[] eatingMessageStartsPlayer = new String[]{
            "You captured my",
            "Ouch, you took my",
            "Nice move! You got my"
    };
    private final String[] eatingMessageStartsComputer = new String[]{
            "I took your",
            "I captured your",
            "I claimed your"
    };
    private final String[] eatingMessageEnds = new String[]{
            ". Well played!",
            ". That will give me an advantage.",
            ". Let's see how you recover."
    };
    // Promotion message variations for player and computer
    private final String[] promoMessageStartsPlayer = new String[]{
            "Well done! You promoted a",
            "You’ve upgraded your piece to a",
            "You’ve got a new"
    };
    private final String[] promoMessageStartsComputer = new String[]{
            "I’ve promoted my pawn to a",
            "I’ve earned myself a new",
            "Promotion! I now have a"
    };
    private final String[] promoMessageEnds = new String[]{
            ". This will be interesting!",
            ". The board just shifted!",
            ". Let's see how this plays out."
    };
    // Castling message variations for player and computer
    private final String[] castleMessageStartsPlayer = new String[]{
            "Good move, castling to safety!",
            "You’ve shielded your king with a smart castle.",
            "Nice choice, castling for defense!"
    };
    private final String[] castleMessageStartsComputer = new String[]{
            "I'm castling to protect my king.",
            "I’ve castled, securing my king.",
            "I’ve moved my king to safety with a castle."
    };
    private final String[] castleMessageEnds = new String[]{
            " Let’s see what comes next.",
            " A solid defense.",
            " Now the game is getting interesting!"
    };
    // General move messages for player and computer
    private final String[] moveMessageStartsPlayer = new String[]{
            "Great move!",
            "You’re getting stronger!",
            "Nice thinking!"
    };
    private final String[] moveMessageStartsComputer = new String[]{
            "I’ve made my move.",
            "Your turn now.",
            "My move is done. Let's see what you do."
    };
    private final String[] moveMessageEndsPlayer = new String[]{
            " My turn!",
            " Let's see how I respond.",
            " What’s your my move?"
    };
    private final String[] moveMessageEndsComputer = new String[]{
            " But can you handle this?",
            " The pressure's on!",
            " Let’s see what you do now."
    };

    public CampaignMessageManager(ChessCentralControl centralControl) {
        this.centralControl = centralControl;
    }

    private String randomlyFormatIntro(String name) {
        String start = getRandom(messageStarts);
        String end = getRandom(messageEnds);
        return String.format("%s %s%s", start, name, end);
    }

    private String getName() {
        if (centralControl.gameHandler.currentlyGameActive() && centralControl.mainScreenController.currentState.equals(MainScreenState.CAMPAIGN)) {
            CampaignTier currentTier = centralControl.gameHandler.getCampaignTier();
            int currentTierLevel = centralControl.gameHandler.getLevelOfCampaignTier();
            return currentTier.levelNames[currentTierLevel];
        } else {
            logger.error("Not in a campaign game, cannot create message");
            return "";
        }
    }

    private <T> T getRandom(T[] options) {
        return options[random.nextInt(options.length)];
    }

    // Use linked random for compatible starts and ends
    private String getLinkedRandomMessage(String[] starts, String[] ends) {
        int sharedIndex = random.nextInt(Math.min(starts.length,ends.length));
        return starts[sharedIndex] + ends[sharedIndex];
    }

    private String formatNameMessage(String name, String message) {
        return "(" + name + "): " + message;
    }

    public String getIntroductionMessage() {
        return randomlyFormatIntro(getName());
    }

    public String getCheckMessage(boolean isWhiteCheck,boolean isPlayerWhite) {
        String name = getName();
        if (isWhiteCheck == isPlayerWhite) {
            return formatNameMessage(name, getLinkedRandomMessage(checkMessageStartsPlayer, checkMessageEnds));
        } else {
            return formatNameMessage(name, getLinkedRandomMessage(checkMessageStartsComputer, checkMessageEnds));
        }
    }

    public String getCheckmateMessage(boolean isWhiteCheckMate,boolean isPlayerWhite) {
        String name = getName();
        if (isWhiteCheckMate == isPlayerWhite) {
            return formatNameMessage(name, getLinkedRandomMessage(checkmateMessageStartsPlayer, checkmateMessageEnds));
        } else {
            return formatNameMessage(name, getLinkedRandomMessage(checkmateMessageStartsComputer, checkmateMessageEnds));
        }
    }

    public String getStalemateMessage() {
        String name = getName();
        return formatNameMessage(name, getLinkedRandomMessage(stalemateMessageStartsComputer, stalemateMessageEnds));
    }

    public String getEatingMessage(int eatenPieceIndex, boolean isWhiteMove,boolean isPlayerWhite) {
        String name = getName();
        String piece = GeneralChessFunctions.getPieceType(eatenPieceIndex);
        if (isWhiteMove == isPlayerWhite) {
            return formatNameMessage(name, getRandom(eatingMessageStartsPlayer) + " " + piece + getRandom(eatingMessageEnds));
        } else {
            return formatNameMessage(name, getRandom(eatingMessageStartsComputer) + " " + piece + getRandom(eatingMessageEnds));
        }
    }

    public String getPromoMessage(int promoIndex, boolean isWhiteMove,boolean isPlayerWhite) {
        String name = getName();
        String newPiece = GeneralChessFunctions.getPieceType(promoIndex);
        if (isWhiteMove == isPlayerWhite) {
            return formatNameMessage(name, getRandom(promoMessageStartsPlayer) + " " + newPiece + getRandom(promoMessageEnds));
        } else {
            return formatNameMessage(name, getRandom(promoMessageStartsComputer) + " " + newPiece + getRandom(promoMessageEnds));
        }
    }

    public String getCastleMessage(boolean isWhiteMove,boolean isPlayerWhite) {
        String name = getName();
        if (isWhiteMove == isPlayerWhite) {
            return formatNameMessage(name, getLinkedRandomMessage(castleMessageStartsPlayer, castleMessageEnds));
        } else {
            return formatNameMessage(name, getLinkedRandomMessage(castleMessageStartsComputer, castleMessageEnds));
        }
    }

    public String getMoveMessage(int simpleEval, boolean isWhiteMove,boolean isPlayerWhite) {
        // todo asjust message based on eval
        String name = getName();
        if (isWhiteMove == isPlayerWhite) {
            return formatNameMessage(name, getRandom(moveMessageStartsPlayer) + getRandom(moveMessageEndsPlayer));
        } else {
            return formatNameMessage(name, getRandom(moveMessageStartsComputer) + getRandom(moveMessageEndsComputer));
        }
    }
}
