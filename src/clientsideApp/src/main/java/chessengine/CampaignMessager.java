package chessengine;

import chessserver.CampaignTier;

import java.util.Random;

public class CampaignMessager {


    private final ChessCentralControl centralControl;
    private final Random random = new Random(1784673287);
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

    public CampaignMessager(ChessCentralControl centralControl) {
        this.centralControl = centralControl;
    }

    private String getName() {
        if (centralControl.gameHandler.currentGame != null && centralControl.mainScreenController.currentState.equals(MainScreenState.CAMPAIGN)) {
            CampaignTier currentTier = centralControl.gameHandler.getCampaignTier();
            int currentTierLevel = centralControl.gameHandler.getLevelOfCampaignTier();
            return currentTier.levelNames[currentTierLevel];
        } else {
            ChessConstants.mainLogger.error("Not in a campaign game, cannot create message");
            return "";
        }

    }

    private String getRandom(String[] options) {
        return options[random.nextInt(options.length)];
    }

    private String randomlyFormatIntro(String name) {
        String start = getRandom(messageStarts);
        String end = getRandom(messageEnds);
        return String.format("%s %s%s", start, name, end);
    }

    private String formatNameMessage(String name, String message) {
        return "(" + name + "): " + message;
    }

    // todo make these actually good


    public String getIntroductionMessage() {
        return randomlyFormatIntro(getName());
    }

    public String getCheckMessage(boolean isWhiteCheck) {
        String name = getName();
        if (isWhiteCheck) {
            return formatNameMessage(name, "ooh scary!");
        } else {
            return formatNameMessage(name, "hahahaha!");
        }

    }

    public String getCheckmateMessage(boolean isWhiteCheckMate) {
        String name = getName();
        if (isWhiteCheckMate) {
            return formatNameMessage(name, "good game!");
        } else {
            return formatNameMessage(name, "next time you will win!");
        }

    }

    public String getStalemateMessage() {
        String name = getName();
        return formatNameMessage(name, "shall we call it a tie for now?");

    }

    public String getEatingMessage(int eatenPieceIndex, boolean isWhiteMove) {
        String name = getName();
        if (isWhiteMove) {
            return formatNameMessage(name, "Oh no! My" + GeneralChessFunctions.getPieceType(eatenPieceIndex) + " !");
        } else {
            return formatNameMessage(name, "haha i took your " + GeneralChessFunctions.getPieceType(eatenPieceIndex) + ";)");
        }
    }


    public String getPromoMessage(int promoIndex, boolean isWhiteMove) {
        String name = getName();
        if (isWhiteMove) {
            return formatNameMessage(name, "oh no! you have a new " + GeneralChessFunctions.getPieceType(promoIndex));
        } else {
            return formatNameMessage(name, "i will defeat you with my new " + GeneralChessFunctions.getPieceType(promoIndex));
        }
    }


    public String getCastleMessage(boolean isWhiteMove) {
        String name = getName();
        if (isWhiteMove) {
            return formatNameMessage(name, "Hiding your king i see!");
        } else {
            return formatNameMessage(name, "Bringing my king to safety!");
        }
    }

    public String getMoveMessage(int simpleEval, boolean isWhiteMove) {
        String name = getName();
        if (isWhiteMove) {
            return formatNameMessage(name, "lets see how you do!");
        } else {
            return formatNameMessage(name, "im coming for you! :]");
        }
    }
}
