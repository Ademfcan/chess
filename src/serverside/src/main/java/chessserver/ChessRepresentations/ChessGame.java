package chessserver.ChessRepresentations;

import chessserver.Enums.ProfilePicture;
import chessserver.Functions.AdvancedChessFunctions;
import chessserver.Functions.GeneralChessFunctions;
import chessserver.Functions.PgnFunctions;
import chessserver.Misc.ChessConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ChessGame {
    private static final Logger logger = LogManager.getLogger("Chess_Game_Logger");

    private final boolean firstTurnDefault = true; // true means player 1 goes first by default
    // castling etc
    private ChessGameState gameState;
    private ChessPosition currentPosition;
    private int curMoveIndex = -1;
    private int maxIndex = -1;
    public String blackPlayerName;
    private String whitePlayerName;
    private String whitePlayerPfpUrl;
    private String blackPlayerPfpUrl;
    private int whiteElo;
    private int blackElo;
    private String gameHash;
    private boolean isWhiteTurn;
    private boolean isWhiteOriented = true;
    private boolean isVsComputer;
    private List<ChessPosition> moves;
    private final boolean isMainGame = false;
    private String gameName;


    private ChessGame() {
        // empty constructor
        // public constructors are all static
    }

    /**
     * Constructor for generating a game from a saved pgn (This version is exclusively used loading games from save
     **/
    public static ChessGame createGameFromSaveLoad(String pgn, String gameName, String whitePlayerName, String blackPlayerName, int whiteElo, int blackElo, String whitePlayerPfpUrl, String blackPlayerPfpUrl, boolean isVsComputer,boolean isWhiteOriented, String gameHash) {
        ChessGame game = new ChessGame();
        game.gameState = new ChessGameState();
        if (pgn.trim().isEmpty()) {
            game.moves = new ArrayList<>();
        } else {
            String[] splitPgn = PgnFunctions.splitPgn(pgn);
            String moveText = splitPgn[1];
            moveText = moveText.replaceAll("\\n", " ");
            game.moves = game.parseMoveText(moveText);
        }
        game.isWhiteOriented = isWhiteOriented;
        game.isVsComputer = isVsComputer;
        game.curMoveIndex = -1;
        game.maxIndex = game.moves.size() - 1;
        game.currentPosition = game.getPos(game.getCurMoveIndex());
        game.gameName = gameName;
        game.whitePlayerName = whitePlayerName;
        game.blackPlayerName = isVsComputer ? "Computer" : blackPlayerName;
        game.whiteElo = whiteElo;
        game.blackElo = blackElo;
        game.whitePlayerPfpUrl = whitePlayerPfpUrl;
        game.blackPlayerPfpUrl = blackPlayerPfpUrl;
        game.isWhiteTurn = game.firstTurnDefault;
        game.gameHash = gameHash;
        game.setGameStateToAbsIndex(game.getCurMoveIndex());
        System.out.println(game.gameState.cloneState().toString());

        return game;
    }

    /**
     * This Constructor is only part of creating a online game. The latter half is created after a second client is connected
     **/
    public static ChessGame getOnlinePreInit(String gameType, String whitePlayerName, int whiteElo, String whitePlayerPfpUrl) {
        ChessGame game = new ChessGame();
        game.curMoveIndex = -1;
        game.maxIndex = -1;
        game.moves = new ArrayList<>();
        game.gameName = "Online " + gameType;
        game.gameHash = String.valueOf(game.hashCode());
        game.whitePlayerName = whitePlayerName;
        game.whiteElo = whiteElo;
        game.whitePlayerPfpUrl = whitePlayerPfpUrl;
        game.blackPlayerName = "";
        game.blackElo = 0;
        game.gameState = new ChessGameState();
        game.currentPosition = game.getPos(game.getCurMoveIndex());
        return game;
    }

    /**
     * This constructor creates a simple game with a name provided as a string
     **/

    public static ChessGame createSimpleGameWithName(String gameName, String whitePlayerName, String blackPlayerName, int whiteElo, int blackElo, String whitePlayerPfpUrl, String blackPlayerPfpUrl, boolean isVsComputer,boolean isWhiteOriented) {
        ChessGame game = new ChessGame();
        game.moves = new ArrayList<>();
        game.gameState = new ChessGameState();
        game.curMoveIndex = -1;
        game.maxIndex = -1;
        game.currentPosition = game.getPos(game.getCurMoveIndex());
        game.isVsComputer = isVsComputer;
        game.isWhiteOriented = isWhiteOriented;
        game.gameName = gameName;
        game.whitePlayerName = whitePlayerName;
        game.blackPlayerName = blackPlayerName;
        game.whiteElo = whiteElo;
        game.blackElo = blackElo;
        game.whitePlayerPfpUrl = whitePlayerPfpUrl;
        game.blackPlayerPfpUrl = blackPlayerPfpUrl;
        game.isWhiteTurn = game.firstTurnDefault;
        game.gameHash = String.valueOf(game.hashCode());

        return game;

    }

    /**
     * This constructor creates a simple game with no name provided. Instead, the name is {Player1Name} + ","  + {Player2Name}
     **/

    public static ChessGame createSimpleGame(String whitePlayerName, String blackPlayerName, int whiteElo, int blackElo, String whitePlayerPfpUrl, String blackPlayerPfpUrl, boolean isVsComputer,boolean isWhiteOriented) {
        ChessGame game = new ChessGame();
        game.moves = new ArrayList<>();
        game.gameState = new ChessGameState();
        game.curMoveIndex = -1;
        game.maxIndex = -1;
        game.currentPosition = game.getPos(game.getCurMoveIndex());
        game.isVsComputer = isVsComputer;
        game.isWhiteOriented = isWhiteOriented;
        game.gameName = whitePlayerName + " vs " + blackPlayerName;
        game.whitePlayerName = whitePlayerName;
        game.blackPlayerName = blackPlayerName;
        game.whiteElo = whiteElo;
        game.blackElo = blackElo;
        game.whitePlayerPfpUrl = whitePlayerPfpUrl;
        game.blackPlayerPfpUrl = blackPlayerPfpUrl;
        game.isWhiteTurn = game.firstTurnDefault;
        game.gameHash = String.valueOf(game.hashCode());

        return game;

    }

    /**
     * This constructor creates a simple game with a name and pgn.
     **/

    public static ChessGame createSimpleGameWithNameAndPgn(String pgn, String gameName, String whitePlayerName, String blackPlayerName, int whiteElo, int blackElo, String whitePlayerPfpurl, String blackPlayerPfpUrl, boolean isVsComputer, boolean isWhiteOriented) {
        ChessGame game = new ChessGame();
        game.gameState = new ChessGameState();
        if (pgn.trim().isEmpty()) {
            game.moves = new ArrayList<>();
        } else {
            String[] splitPgn = PgnFunctions.splitPgn(pgn);
            String moveText = splitPgn[1];
            moveText = moveText.replaceAll("\\n", " ");
            game.moves = game.parseMoveText(moveText);
        }
        game.curMoveIndex = -1;
        game.maxIndex = game.moves.size() - 1;
        game.currentPosition = game.getPos(game.getCurMoveIndex());
        game.isVsComputer = isVsComputer;
        game.isWhiteOriented = isWhiteOriented;
        game.gameName = gameName;
        game.whitePlayerName = whitePlayerName;
        game.blackPlayerName = blackPlayerName;
        game.whiteElo = whiteElo;
        game.blackElo = blackElo;
        game.whitePlayerPfpUrl = whitePlayerPfpurl;
        game.blackPlayerPfpUrl = blackPlayerPfpUrl;
        game.isWhiteTurn = game.firstTurnDefault;
        game.gameHash = String.valueOf(game.hashCode());
        game.setGameStateToAbsIndex(game.getCurMoveIndex());

        return game;

    }

    /**
     * This constructor creates a game from pgn and simple things known about the player (Needs to be changed as true pgn is is only represented as a string. Right now it is only parsing pgn as movetext
     **/
    public static ChessGame gameFromPgnLimitedInfo(String pgn, String gameName, String whitePlayerName, int whiteElo, String whitePfpUrl, boolean isVsComputer, boolean isWhiteOriented) {
        ChessGame game = new ChessGame();
        game.gameState = new ChessGameState();
        if (pgn.trim().isEmpty()) {
            game.moves = new ArrayList<>();
        } else {
            String[] splitPgn = PgnFunctions.splitPgn(pgn);
            String moveText = splitPgn[1];
            moveText = moveText.replaceAll("\\n", " ");
            game.moves = game.parseMoveText(moveText);
        }
        game.isVsComputer = isVsComputer;
        game.isWhiteOriented = isWhiteOriented;
        game.curMoveIndex = -1;
        game.maxIndex = game.moves.size() - 1;
        game.currentPosition = game.getPos(game.getCurMoveIndex());
        game.gameName = gameName;
        game.isWhiteTurn = game.firstTurnDefault;
        game.whitePlayerName = whitePlayerName;
        game.blackPlayerName = isVsComputer ? "Computer" : "Player 2";
        game.whiteElo = whiteElo;
        game.blackElo = 0;
        game.whitePlayerPfpUrl = whitePfpUrl;
        game.blackPlayerPfpUrl = isVsComputer ? ProfilePicture.ROBOT.urlString : whitePfpUrl;
        game.gameHash = String.valueOf(game.hashCode());
        game.setGameStateToAbsIndex(game.getCurMoveIndex());
        return game;

    }

    /**
     * This method is pretty much exclusively called from tests. All vars with user information/Graphical Features are not expected to be used
     **/
    public static ChessGame createTestGame(String pgn) {
        ChessGame game = new ChessGame();
        game.gameState = new ChessGameState();
        if (pgn.trim().isEmpty()) {
            game.moves = new ArrayList<>();
        } else {
            String[] splitPgn = PgnFunctions.splitPgn(pgn);
            String moveText = splitPgn[1];
            moveText = moveText.replaceAll("\\n", " ");
            game.moves = game.parseMoveText(moveText);
        }
        game.isVsComputer = false;
        game.isWhiteOriented = true;
        game.curMoveIndex = -1;
        game.maxIndex = game.moves.size() - 1;
        game.currentPosition = game.getPos(game.getCurMoveIndex());
        game.gameName = "Test Game";
        game.isWhiteTurn = game.firstTurnDefault;
        game.whitePlayerName = "Test Player";
        game.blackPlayerName = "Test Player";
        game.whiteElo = 0;
        game.blackElo = 0;
        game.whitePlayerPfpUrl = ProfilePicture.DEFAULT.urlString;
        game.blackPlayerPfpUrl = ProfilePicture.DEFAULT.urlString;
        game.gameHash = String.valueOf(game.hashCode());
        game.setGameStateToAbsIndex(game.getCurMoveIndex());

        return game;
    }

    /**
     * This method is pretty much exclusively called from tests. All vars with user information/Graphical Features are not expected to be used
     **/
    public static ChessGame createServerFollowerGame() {
        ChessGame game = new ChessGame();
        game.gameState = new ChessGameState();
        game.moves = new ArrayList<>();
        game.isVsComputer = false;
        game.isWhiteOriented = true;
        game.curMoveIndex = -1;
        game.maxIndex = -1;
        game.currentPosition = game.getPos(game.getCurMoveIndex());
        game.gameName = "Server follower game";
        game.isWhiteTurn = game.firstTurnDefault;
        game.whitePlayerName = "P1";
        game.blackPlayerName = "P2";
        game.whiteElo = 0;
        game.blackElo = 0;
        game.whitePlayerPfpUrl = ProfilePicture.DEFAULT.urlString;
        game.blackPlayerPfpUrl = ProfilePicture.DEFAULT.urlString;
        game.gameHash = String.valueOf(game.hashCode());
        game.setGameStateToAbsIndex(game.getCurMoveIndex());

        return game;
    }

    /**
     * This method is pretty much exclusively called for explorer
     */

    public static ChessGame createEmptyExplorer() {
        ChessGame game = new ChessGame();
        game.isVsComputer = false;
        game.isWhiteOriented = true;
        game.gameState = new ChessGameState();
        game.moves = new ArrayList<>();
        game.curMoveIndex = -1;
        game.maxIndex = -1;
        game.currentPosition = game.getPos(game.getCurMoveIndex());
        game.gameName = "Explorer Game";
        game.isWhiteTurn = game.firstTurnDefault;
        game.whitePlayerName = "Explorer";
        game.blackPlayerName = "Explorer";
        game.whiteElo = 0;
        game.blackElo = 0;
        game.whitePlayerPfpUrl = ProfilePicture.DEFAULT.urlString;
        game.blackPlayerPfpUrl = ProfilePicture.DEFAULT.urlString;
        game.gameHash = String.valueOf(game.hashCode());

        return game;
    }

    /**
     * Method used exclusively for cloning chessgame instances Note: The game created is set to the start of the game
     **/
    private static ChessGame getClonedGame(List<ChessPosition> moves, ChessGameState gameStates, String gameName, String whitePlayerName, String blackPlayerName, int whiteElo, int blackElo, String whitePlayerPfpUrl, String blackPlayerPfpUrl, boolean isVsComputer,boolean isWhiteOriented, int maxIndex) {
        ChessGame game = new ChessGame();
        game.moves = moves;
        game.gameState = gameStates;
        game.curMoveIndex = -1;
        game.maxIndex = maxIndex;
        game.currentPosition = game.getPos(game.getCurMoveIndex());
        game.isVsComputer = isVsComputer;
        game.isWhiteOriented = isWhiteOriented;
        game.gameName = gameName;
        game.isWhiteTurn = game.firstTurnDefault;
        game.whitePlayerName = whitePlayerName;
        game.blackPlayerName = blackPlayerName;
        game.whiteElo = whiteElo;
        game.blackElo = blackElo;
        game.whitePlayerPfpUrl = whitePlayerPfpUrl;
        game.blackPlayerPfpUrl = blackPlayerPfpUrl;
        game.gameHash = String.valueOf(game.hashCode());
        game.setGameStateToAbsIndex(game.getCurMoveIndex());

        return game;

    }

    public boolean isWhiteOriented() {
        return isWhiteOriented;
    }

    public boolean isVsComputer() {
        return isVsComputer;
    }



    public String getGameName() {
        return gameName;
    }

    public String getWhitePlayerName() {
        return whitePlayerName;
    }

    public String getBlackPlayerName() {
        return blackPlayerName;
    }

    public int getWhiteElo() {
        return whiteElo;
    }

    public int getBlackElo() {
        return blackElo;
    }

    public String getWhitePlayerPfpUrl() {
        return whitePlayerPfpUrl;
    }

    public String getBlackPlayerPfpUrl() {
        return blackPlayerPfpUrl;
    }

    public String getGameHash() {
        return gameHash;
    }

    public boolean isWhiteTurn() {
        return isWhiteTurn;
    }

    public void setWhiteTurn(boolean whiteTurn) {
        isWhiteTurn = whiteTurn;
    }

    public boolean isWhiteTurn(int index) {
        int change = index - curMoveIndex;
        return isWhiteTurn == (change % 2 == 0);
    }


    public ChessGame cloneGame() {
        List<ChessPosition> clonedMoves = new ArrayList<>(moves.stream().map(ChessPosition::clonePosition).toList());
        return ChessGame.getClonedGame(clonedMoves, gameState.cloneState(), gameName, whitePlayerName, blackPlayerName, whiteElo, blackElo, whitePlayerPfpUrl, blackPlayerPfpUrl, isVsComputer,isWhiteOriented, maxIndex);
    }


    public void moveToEndOfGame() {
        if (maxIndex != curMoveIndex) {
            changeToDifferentMove(maxIndex - curMoveIndex);
        } else {
            logger.debug("Already at end of game");
        }
    }

    public void moveToMoveIndexAbsolute(int absIndex) {
        if (absIndex <= maxIndex && absIndex != curMoveIndex) {
            changeToDifferentMove(absIndex - curMoveIndex);
        } else {
            logger.error("absolute index provided out of bounds!");
        }
    }

    public void changeToDifferentMove(int dir) {
//        System.out.println(GeneralChessFunctions.getBoardDetailedString(currentPosition.board));
        int newIndex = curMoveIndex+dir;
        if(dir != 0 &&  newIndex >= -1 && newIndex <= maxIndex){
            int moveChange = Math.abs(dir % 2);
            // if not an even number the turn flips
            if (moveChange == 1) {
                isWhiteTurn = !isWhiteTurn;
            }
            int tempForGameStates = curMoveIndex;
            curMoveIndex += dir;

            logger.debug("New curIndex: " + curMoveIndex);
            ChessPosition newPos = getPos(curMoveIndex);

            if (Math.abs(dir) > 1) {
                // gamestates update
                updateGameStates(dir, tempForGameStates);
            } else {
                boolean isReverse = dir < 0;
                // update gamestates
                if (isReverse) {
                    gameState.moveBackward(currentPosition);
                } else {
                    gameState.moveForward(newPos);
                }
            }

            currentPosition = newPos;
        }
        else{
            logger.error("Invalid move index provided: " + dir);
        }
    }

    private void updateGameStates(int dir, int moveIndexBeforeChange) {
//        System.out.println("In Iter, dir: " + dir + " mibfc: " + moveIndexBeforeChange);
        boolean isRev = dir < 0;
        int absDir = Math.abs(dir);
        for (int i = 0; i < absDir; i++) {
            if (isRev) {
                gameState.moveBackward(getPos(moveIndexBeforeChange));
                moveIndexBeforeChange--;
            } else {
                moveIndexBeforeChange++;
                ChessPosition newPos = getPos(moveIndexBeforeChange);
                gameState.moveForward(newPos);
            }
            // highlight new move index


        }
    }

    public ChessGameState getGameStateAtPos(int index) {
        int moveIndexBeforeChange = curMoveIndex;
        int dir = index - curMoveIndex;
        ChessGameState cloned = gameState.cloneState();
        boolean isRev = dir < 0;
        int absDir = Math.abs(dir);
        for (int i = 0; i < absDir; i++) {
            if (isRev) {
                cloned.moveBackward(getPos(moveIndexBeforeChange));
                moveIndexBeforeChange--;
            } else {
                moveIndexBeforeChange++;
                ChessPosition newPos = getPos(moveIndexBeforeChange);
                cloned.moveForward(newPos);
            }
            // highlight new move index


        }

        return cloned;
    }

    private void setGameStateToAbsIndex(int absIndex) {
        int dir = absIndex - gameState.getCurrentIndex();
        updateGameStates(dir, gameState.getCurrentIndex());
    }

    public void reset() {
        this.isWhiteTurn = firstTurnDefault;
        curMoveIndex = -1;
        maxIndex = -1;
        gameState.reset();
        currentPosition = getPos(-1);;
        clearIndx(false);


    }

    public List<ChessPosition> getPositions() {
        return moves;
    }





    public ChessPosition getPos(int moveIndex) {
        if (moveIndex >= 0 && moveIndex < moves.size()) {
            ChessPosition newPos = moves.get(moveIndex);
            if (newPos == null) {
                logger.error("NewPosNull");
            }
            return newPos;
        } else if (moveIndex == -1) {
            // intial board state
            return ChessConstants.startBoardState;
        } else {
            logger.error("Boardwrapper get move index out of range: " + moveIndex);
            return null;
        }

    }

    public void clearIndx(boolean updateStates) {
        // this is for if you undo moves and create a new branch by making a move
        maxIndex = curMoveIndex;
        int to = moves.size();
        if (to > curMoveIndex + 1) {
            logger.debug(String.format("Clearing board entries from %d", curMoveIndex + 1));

            moves.subList(curMoveIndex + 1, to).clear();
        }
        if (updateStates) {
            gameState.clearIndexes(curMoveIndex);
        }



    }

    // only used for web move
    public void makePgnMove(String pgn) {
        moveToEndOfGame();
        ChessMove move = pgnToMove(pgn, getPos(curMoveIndex), isWhiteTurn);
        ChessPosition newPos = new ChessPosition(getPos(curMoveIndex), gameState, move);
        MakeMove(newPos, move);
    }

    // one called for all local moves
    public void makeNewMove(ChessMove move, boolean isComputerMove) {
        System.out.println(move.toString());
        if (!isComputerMove) {
            // clear any entries, you are branching off
            if (curMoveIndex != maxIndex) {
                clearIndx(true);

            }
        } else {
            if (curMoveIndex != maxIndex) {
                moveToEndOfGame();
            }

        }
        ChessPosition newPos = new ChessPosition(currentPosition, gameState, move);
        MakeMove(newPos, move);


    }

    // only for sandbox
    public void makeCustomMoveSandbox(ChessPosition newPos) {
        MakeMove(newPos, newPos.getMoveThatCreatedThis());
    }

    public void MakeMove(ChessPosition newPosition, ChessMove move) {
        GeneralChessFunctions.printBoardDetailed(newPosition.board);
        isWhiteTurn = !isWhiteTurn;
        maxIndex++;
        curMoveIndex++;
        moves.add(newPosition);
        gameState.makeNewMoveAndCheckDraw(newPosition);
        if (AdvancedChessFunctions.isAnyNotMovePossible(!move.isWhite(), newPosition, gameState)) {
            if (AdvancedChessFunctions.isCheckmated(!move.isWhite(), newPosition, gameState)) {
                gameState.setCheckMated(move.isWhite(), ChessConstants.EMPTYINDEX);

            } else {
                gameState.setStaleMated();
                logger.debug("stalemate");


            }
        }

        currentPosition = newPosition;
    }

    // turning position to pgn
    public ChessMove pgnToMove(String pgn, ChessPosition currentPosition, boolean isWhiteMove) {
//        System.out.println(currentPosition.getMoveThatCreatedThis());

        if (pgn.equals("O-O") || pgn.equals("0-0")) {
            // short castle
            int dir = 2;
            XYcoord kingLocation = isWhiteMove ? currentPosition.board.getWhiteKingLocation() : currentPosition.board.getBlackKingLocation();
            // move the king
            int oldX = kingLocation.x;
            int oldY = kingLocation.y;
            int newX = kingLocation.x + dir;
            int newY = kingLocation.y;
            return new ChessMove(oldX, oldY, newX, newY, ChessConstants.EMPTYINDEX, ChessConstants.KINGINDEX, isWhiteMove, true, false, ChessConstants.EMPTYINDEX, false, false);

        }
        if (pgn.equals("O-O-O") || pgn.equals("0-0-0")) {
            // long castle
            int dir = -2;
            XYcoord kingLocation = isWhiteMove ? currentPosition.board.getWhiteKingLocation() : currentPosition.board.getBlackKingLocation();
            // move the king
            int oldX = kingLocation.x;
            int oldY = kingLocation.y;
            int newX = kingLocation.x + dir;
            int newY = kingLocation.y;
            return new ChessMove(oldX, oldY, newX, newY, ChessConstants.EMPTYINDEX, ChessConstants.KINGINDEX, isWhiteMove, true, false, ChessConstants.EMPTYINDEX, false, false);

        }

        int x;
        int y;
        int pieceType = PgnFunctions.turnPgnPieceToPieceIndex(pgn.charAt(0));
        int start = pieceType == ChessConstants.PAWNINDEX ? 0 : 1;
        boolean isEating = false;
        // store the x values found. At most there will be two with the first one being the ambiguity char, and the other being the move x coord
        // when there is only 1 that means that the first one is the x coord with no ambiguity char
        int digYCount = 0;
        int[] digValsY = new int[2];
        int ambgX = ChessConstants.EMPTYINDEX;
        // same as above except for y values
        int strXCount = 0;
        int[] strValsX = new int[2];
        int ambgY = ChessConstants.EMPTYINDEX;


        int promoIndex = ChessConstants.EMPTYINDEX;
        // remove check and checmate symbols
        String simplefiedPgn = pgn.replace("+", "").replace("#", "");
        if (simplefiedPgn.length() == 2) {
            // simple pawn move
            if (pgn.contains("#")) {
                gameState.setCheckMated(isWhiteMove, gameState.getCurrentIndex() + 1);
            }


            x = PgnFunctions.turnFileStrToInt(pgn.charAt(0));
            y = Integer.parseInt(String.valueOf((pgn.charAt(1)))) - 1;
            // flip y as pgn board is inverted to my board
            y = 7 - y;


            int OldY = AdvancedChessFunctions.getPawnColumnGivenFile(x, y, isWhiteMove, isWhiteMove ? currentPosition.board.getWhitePiecesBB()[pieceType] : currentPosition.board.getBlackPiecesBB()[pieceType]);
            return new ChessMove(x, OldY, x, y, ChessConstants.EMPTYINDEX, pieceType, isWhiteMove, false, false, ChessConstants.EMPTYINDEX, false, false);


        } else {
            for (int i = start; i < pgn.length(); i++) {
                char c = pgn.charAt(i);
                if (c == 'x') {
                    isEating = true;
                } else if (c == '=') {
                    // Indicates a promotion
                    promoIndex = PgnFunctions.turnPgnPieceToPieceIndex(pgn.charAt(i + 1)); // Get the promoted piece type
                } else if (c == '+') {
                    // Indicates a check
                } else if (c == '#') {
                    // Indicates a checkmate
                    gameState.setCheckMated(isWhiteMove, gameState.getCurrentIndex() + 1);
                } else if (Character.isDigit(c)) {
                    // If the character is a digit, it denotes the destination square
                    // Update x and y coordinates accordingly
                    // #1 subtract 1 because pgn is not zero indexed
                    // #2 7- the num because pgn is inverted compared to my setup
                    digValsY[digYCount] = 7 - (Integer.parseInt(String.valueOf(c)) - 1);
                    digYCount++;
                } else if (Character.isLowerCase(c)) {
                    strValsX[strXCount] = PgnFunctions.turnFileStrToInt(c);
                    strXCount++;
                    // If the character is an uppercase letter, it denotes the piece being moved
                    // Determine the column/file of the piece (if needed) or handle any other special cases
                } else {
                    // Handle any other cases as needed


                }
            }
            // if > 1 then that means there was an ambiguous coord
            if (strXCount > 1) {
                x = strValsX[1];
                ambgX = strValsX[0];

            } else {
                x = strValsX[0];
            }

            if (digYCount > 1) {
                y = digValsY[1];
                ambgY = digValsY[0];
            } else {
                y = digValsY[0];
            }
            // need to flip coordinates because i use a top down coordinate system,
            // so x is fine but y needs flip

            if (isEating && pieceType == ChessConstants.PAWNINDEX) {
                // could be en passant
                int backdir = isWhiteMove ? 1 : -1;
                // check if its an en passant move
                if (GeneralChessFunctions.isValidCoord(x, y + backdir) && GeneralChessFunctions.checkIfContains(x, y + backdir, !isWhiteMove, currentPosition.board)) {
                    // if so this means that the pawn can have possibly gotten here from an en passant move
                    if (!currentPosition.equals(ChessConstants.startBoardState)) {
                        // means we arent at the very beginning as there is not an actual move for the start position
                        ChessMove moveThatCreated = currentPosition.getMoveThatCreatedThis();
                        if (moveThatCreated.getBoardIndex() == ChessConstants.PAWNINDEX) {
                            // pawn move so possibilty of enpassant
                            if (Math.abs(moveThatCreated.getOldY() - moveThatCreated.getNewY()) > 1) {
                                // jumped 2 so means that there is a possibilty of en passant
                                int midY = (moveThatCreated.getOldY() + moveThatCreated.getNewY()) / 2;
                                if (y == midY && x == moveThatCreated.getNewX()) {
                                    // en passant
                                    int oldX = AdvancedChessFunctions.getEnPassantOriginX(x, y, isWhiteMove, isWhiteMove ? currentPosition.board.getWhitePiecesBB()[pieceType] : currentPosition.board.getBlackPiecesBB()[pieceType]);
                                    return new ChessMove(oldX, y + backdir, x, y, ChessConstants.EMPTYINDEX, pieceType, isWhiteMove, false, false, ChessConstants.EMPTYINDEX, true, false);

                                }


                            }
                        }
                    }
                }
            }


            XYcoord oldCoords = AdvancedChessFunctions.findOldCoordinates(x, y, pieceType, ambgX, ambgY, isWhiteMove, isEating, currentPosition, gameState);


            int eatingIndex = GeneralChessFunctions.getBoardWithPiece(x, y, !isWhiteMove, currentPosition.board);
            ChessMove pgnMove = new ChessMove(oldCoords.x, oldCoords.y, x, y, promoIndex, pieceType, isWhiteMove, false, isEating, eatingIndex, false, false);
            gameState.updateRightsBasedOnMove(pgnMove);

            return pgnMove;

        }
    }

    private ChessPosition makeNewMovePGN(String move, ChessPosition currentPos, boolean isWhiteMove) {
        ChessMove movePgn = pgnToMove(move, currentPos, isWhiteMove);
        return new ChessPosition(currentPos, gameState, movePgn);

    }

    public String gameToPgn() {
        return gameToPgn(maxIndex);
    }

    public String gameToPgn(int maxIndex) {
        if (maxIndex == -1) {
            // empty game
            return "";
        }
        StringBuilder sb = new StringBuilder(maxIndex);
        for (int i = 0; i < maxIndex + 1; i++) {
            // need to update gamestates as we move along through the game
            setGameStateToAbsIndex(i);
            if (i % 2 == 0) {
                int moveNum = (i / 2) + 1;
                sb.append(moveNum).append(".");
            }
            ChessPosition p = getPos(i);

            sb.append(PgnFunctions.moveToPgn(p, gameState)).append(" ");

        }
        // reset gamestates
        setGameStateToAbsIndex(curMoveIndex);
        return sb.toString();
    }

    public List<ChessMove> getMoves() {
        List<ChessMove> moves = new ArrayList<>();
        for (int i = 0; i <= maxIndex; i++) {
            moves.add(getPos(i).getMoveThatCreatedThis());
        }
        return moves;
    }

    public String[] gameToPgnArr() {
        if (maxIndex == -1) {
            // empty game
            return new String[]{};
        }
        String[] pgn = new String[maxIndex + 1];
        for (int i = 0; i < maxIndex + 1; i++) {
            // need to update gamestates as we move along through the game
            setGameStateToAbsIndex(i);
            ChessPosition p = getPos(i);

            pgn[i] = PgnFunctions.moveToPgn(p, gameState);

        }
        // reset gamestated
        setGameStateToAbsIndex(curMoveIndex);
        return pgn;
    }

    public List<ChessPosition> parseMoveText(String pgnMoveText) {
        List<ChessPosition> positions = new LinkedList<>();
        ChessPosition currentState = ChessConstants.startBoardState;

        boolean WhiteMove = true;
        pgnMoveText = trimExtra(pgnMoveText);

        for (String s : pgnMoveText.split(" ")) {
            int dotIndex = s.indexOf(".");
            if (dotIndex != -1) {
                s = s.substring(dotIndex + 1);
            }
            currentState = makeNewMovePGN(s, currentState, WhiteMove);
            boolean isDraw = gameState.makeNewMoveAndCheckDraw(currentState);
            positions.add(currentState);
            if (isDraw) {
                System.out.println("Isdraw: " + gameState.getCurrentIndex());
                gameState.setStaleMated();
                break;
            }
            if (AdvancedChessFunctions.isCheckmated(currentState, gameState)) {
                gameState.setCheckMated(WhiteMove, ChessConstants.EMPTYINDEX);
                break;
            }
            WhiteMove = !WhiteMove;
        }


        return positions;
    }

    private String trimExtra(String movetext) {
        StringBuilder sb = new StringBuilder(movetext.trim());
        for (int i = 1; i < sb.length() - 1; i++) {
            char c = sb.charAt(i);
            char prev = sb.charAt(i - 1);
            char post = sb.charAt(i + 1);
            if (c == ' ') {
                if (prev == '.' || prev == ' ' || post == ' ') {
                    sb.deleteCharAt(i);
                }
            }
        }
        return sb.toString();
    }

    public String getCurrentFen() {
        return PgnFunctions.positionToFEN(currentPosition, gameState, isWhiteTurn);
    }

    public int getCurMoveIndex() {
        return curMoveIndex;

    }

    public int getMaxIndex() {
        return maxIndex;

    }

    public ChessPosition getCurrentPosition() {
        return currentPosition;
    }

    public ChessGameState getGameState() {
        return gameState;
    }

    public void updateInfoFromPartialInit(String onlinePlayerName, int onlineElo, String onlinePfpUrl, boolean isClientWhite) {
        // white fields used as temporary storage until online match was found
        String tempClientName = whitePlayerName;
        int tempClientElo = whiteElo;
        String tempClientPfpUrl = whitePlayerPfpUrl;

        whitePlayerName = isClientWhite ? tempClientName : onlinePlayerName;
        whiteElo = isClientWhite ? tempClientElo : onlineElo;
        whitePlayerPfpUrl = isClientWhite ? tempClientPfpUrl : onlinePfpUrl;

        blackPlayerName = isClientWhite ? onlinePlayerName : tempClientName;
        blackElo = isClientWhite ? onlineElo : tempClientElo;
        blackPlayerPfpUrl = isClientWhite ? onlinePfpUrl : tempClientPfpUrl;

        this.isWhiteTurn = true;
        this.isWhiteOriented = isClientWhite;

    }
}
