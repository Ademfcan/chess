package chessengine;

import chessserver.INTENT;
import chessserver.ProfilePicture;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ChessGame {


    private final boolean firstTurnDefault = true; // true means player 1 goes first by default
    // castling etc
    public ChessStates gameState;
    public ChessPosition currentPosition;
    public int curMoveIndex = -1;
    public int maxIndex = -1;
    private boolean isVsComputer;
    private String whitePlayerName;
    private String whitePlayerPfpUrl;
    private String blackPlayerName;
    private String blackPlayerPfpUrl;
    private int whiteElo;
    private int blackElo;
    private String gameHash;
    private boolean isWhiteTurn;
    private boolean isWhiteOriented = true;
    private ChessCentralControl centralControl;
    private List<ChessPosition> moves;
    private boolean isMainGame = false;
    private String gameName;
    /**
     * These variables and methods all are only used in online games
     **/

    private String gameType;
    private WebSocketClient client;
    private boolean isWebGame;
    private boolean isWebGameInitialized;

    private ChessGame() {
        // empty constructor
        // public constructors are all static
    }

    /**
     * Constructor for generating a game from a saved pgn (This version is exclusively used loading games from save
     **/
    public static ChessGame createGameFromSaveLoad(String pgn, String gameName, String whitePlayerName, String blackPlayerName, int whiteElo, int blackElo, String whitePlayerPfpUrl, String blackPlayerPfpUrl, boolean isVsComputer, boolean isWhiteOriented, String gameHash) {
        ChessGame game = new ChessGame();
        game.gameState = new ChessStates();
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
        game.currentPosition = game.getPos(game.curMoveIndex);
        game.gameName = gameName;
        game.whitePlayerName = whitePlayerName;
        game.blackPlayerName = isVsComputer ? "Computer" : blackPlayerName;
        game.whiteElo = whiteElo;
        game.blackElo = blackElo;
        game.whitePlayerPfpUrl = whitePlayerPfpUrl;
        game.blackPlayerPfpUrl = blackPlayerPfpUrl;
        game.isWhiteTurn = game.firstTurnDefault;
        game.isVsComputer = isVsComputer;
        game.gameHash = gameHash;
        game.isWebGame = false;
        game.isWhiteOriented = isWhiteOriented;
        game.setGameStateToAbsIndex(game.curMoveIndex);
        return game;
    }

    /**
     * This Constructor is only part of creating a online game. The latter half is created after a second client is connected
     **/
    public static ChessGame getOnlinePreInit(String gameType, boolean isWhiteOriented) {
        ChessGame game = new ChessGame();
        game.moves = new ArrayList<>();
        game.gameName = "Online " + gameType;
        game.gameHash = String.valueOf(game.hashCode());
        game.isVsComputer = false;
        game.whitePlayerName = App.userManager.getUserName();
        game.whiteElo = App.userManager.getUserElo();
        game.whitePlayerPfpUrl = App.userManager.getUserPfpUrl();
        game.client = App.getWebclient();
        game.client.setLinkedGame(game);
        game.blackPlayerName = "";
        game.blackElo = 0;
        game.isWebGame = true;
        game.gameState = new ChessStates();
        game.gameType = gameType;
        game.currentPosition = game.getPos(game.curMoveIndex);
        game.isWhiteOriented = isWhiteOriented;
        game.isWebGameInitialized = false;
        return game;
    }

    /**
     * This constructor creates a simple game with a name provided as a string
     **/

    public static ChessGame createSimpleGameWithName(String gameName, String whitePlayerName, String blackPlayerName, int whiteElo, int blackElo, String whitePlayerPfpUrl, String blackPlayerPfpUrl, boolean isVsComputer, boolean isWhiteOriented) {
        ChessGame game = new ChessGame();
        game.moves = new ArrayList<>();
        game.gameState = new ChessStates();
        game.curMoveIndex = -1;
        game.maxIndex = -1;
        game.currentPosition = game.getPos(game.curMoveIndex);

        game.gameName = gameName;
        game.whitePlayerName = whitePlayerName;
        game.blackPlayerName = blackPlayerName;
        game.whiteElo = whiteElo;
        game.blackElo = blackElo;
        game.whitePlayerPfpUrl = whitePlayerPfpUrl;
        game.blackPlayerPfpUrl = blackPlayerPfpUrl;
        game.isWhiteTurn = game.firstTurnDefault;
        game.isVsComputer = isVsComputer;
        game.gameHash = String.valueOf(game.hashCode());
        game.isWebGame = false;
        game.isWhiteOriented = isWhiteOriented;

        return game;

    }

    /**
     * This constructor creates a simple game with no name provided. Instead, the name is {Player1Name} + ","  + {Player2Name}
     **/

    public static ChessGame createSimpleGame(String whitePlayerName, String blackPlayerName, int whiteElo, int blackElo, String whitePlayerPfpUrl, String blackPlayerPfpUrl, boolean isVsComputer, boolean isWhiteOriented) {
        ChessGame game = new ChessGame();
        game.moves = new ArrayList<>();
        game.gameState = new ChessStates();
        game.curMoveIndex = -1;
        game.maxIndex = -1;
        game.currentPosition = game.getPos(game.curMoveIndex);

        game.gameName = whitePlayerName + " vs " + blackPlayerName;
        game.whitePlayerName = whitePlayerName;
        game.blackPlayerName = blackPlayerName;
        game.whiteElo = whiteElo;
        game.blackElo = blackElo;
        game.whitePlayerPfpUrl = whitePlayerPfpUrl;
        game.blackPlayerPfpUrl = blackPlayerPfpUrl;
        game.isWhiteTurn = game.firstTurnDefault;
        game.isVsComputer = isVsComputer;
        game.gameHash = String.valueOf(game.hashCode());
        game.isWebGame = false;
        game.isWhiteOriented = isWhiteOriented;

        return game;

    }

    /**
     * This constructor creates a simple game with a name and pgn.
     **/

    public static ChessGame createSimpleGameWithNameAndPgn(String pgn, String gameName, String whitePlayerName, String blackPlayerName, int whiteElo, int blackElo, String whitePlayerPfpurl, String blackPlayerPfpUrl, boolean isVsComputer, boolean isWhiteOriented) {
        ChessGame game = new ChessGame();
        game.gameState = new ChessStates();
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
        game.currentPosition = game.getPos(game.curMoveIndex);

        game.gameName = gameName;
        game.whitePlayerName = whitePlayerName;
        game.blackPlayerName = blackPlayerName;
        game.whiteElo = whiteElo;
        game.blackElo = blackElo;
        game.whitePlayerPfpUrl = whitePlayerPfpurl;
        game.blackPlayerPfpUrl = blackPlayerPfpUrl;
        game.isWhiteTurn = game.firstTurnDefault;
        game.isVsComputer = isVsComputer;
        game.gameHash = String.valueOf(game.hashCode());
        game.isWebGame = false;
        game.isWhiteOriented = isWhiteOriented;
        game.setGameStateToAbsIndex(game.curMoveIndex);

        return game;

    }

    /**
     * This constructor creates a game from pgn and simple things known about the player (Needs to be changed as true pgn is is only represented as a string. Right now it is only parsing pgn as movetext
     **/
    public static ChessGame gameFromPgnLimitedInfo(String pgn, String gameName, String whitePlayerName, int whiteElo, String whitePfpUrl, boolean isVsComputer, boolean isWhiteOriented) {
        ChessGame game = new ChessGame();
        game.gameState = new ChessStates();
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
        game.currentPosition = game.getPos(game.curMoveIndex);
        game.gameName = gameName;
        game.isWhiteTurn = game.firstTurnDefault;
        game.whitePlayerName = whitePlayerName;
        game.blackPlayerName = isVsComputer ? "Computer" : "Player 2";
        game.whiteElo = whiteElo;
        game.blackElo = 0;
        game.whitePlayerPfpUrl = whitePfpUrl;
        game.blackPlayerPfpUrl = isVsComputer ? ProfilePicture.ROBOT.urlString : whitePfpUrl;
        game.isVsComputer = isVsComputer;
        game.gameHash = String.valueOf(game.hashCode());
        game.isWebGame = false;
        game.isWhiteOriented = isWhiteOriented;
        game.setGameStateToAbsIndex(game.curMoveIndex);

        return game;

    }

    /**
     * This method is pretty much exclusively called from tests. All vars with user information/Graphical Features are not expected to be used
     **/
    public static ChessGame createTestGame(String pgn, boolean isVsComputer) {
        ChessGame game = new ChessGame();
        game.gameState = new ChessStates();
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
        game.currentPosition = game.getPos(game.curMoveIndex);
        game.gameName = "Test Game";
        game.isWhiteTurn = game.firstTurnDefault;
        game.whitePlayerName = "Test Player";
        game.blackPlayerName = "Test Player";
        game.whiteElo = 0;
        game.blackElo = 0;
        game.whitePlayerPfpUrl = ProfilePicture.DEFAULT.urlString;
        game.blackPlayerPfpUrl = ProfilePicture.DEFAULT.urlString;
        game.isVsComputer = isVsComputer;
        game.gameHash = String.valueOf(game.hashCode());
        game.isWebGame = false;
        game.isWhiteOriented = true;
        game.setGameStateToAbsIndex(game.curMoveIndex);

        return game;
    }

    /**
     * This method is pretty much exclusively called for explorer
     */

    public static ChessGame createEmptyExplorer() {
        ChessGame game = new ChessGame();
        game.gameState = new ChessStates();
        game.moves = new ArrayList<>();
        game.curMoveIndex = -1;
        game.maxIndex = -1;
        game.currentPosition = game.getPos(game.curMoveIndex);
        game.gameName = "Explorer Game";
        game.isWhiteTurn = game.firstTurnDefault;
        game.whitePlayerName = "Explorer";
        game.blackPlayerName = "Explorer";
        game.whiteElo = 0;
        game.blackElo = 0;
        game.whitePlayerPfpUrl = ProfilePicture.DEFAULT.urlString;
        game.blackPlayerPfpUrl = ProfilePicture.DEFAULT.urlString;
        game.isVsComputer = false;
        game.gameHash = String.valueOf(game.hashCode());
        game.isWebGame = false;
        game.isWhiteOriented = true;

        return game;
    }

    /**
     * Method used exclusively for cloning chessgame instances Note: The game created is set to the start of the game
     **/
    private static ChessGame getClonedGame(List<ChessPosition> moves, ChessStates gameStates, String gameName, String whitePlayerName, String blackPlayerName, int whiteElo, int blackElo, String whitePlayerPfpUrl, String blackPlayerPfpUrl, boolean isVsComputer, boolean isWebGame, boolean isWhiteOriented, int maxIndex) {
        ChessGame game = new ChessGame();
        game.moves = moves;
        game.gameState = gameStates;
        game.curMoveIndex = -1;
        game.maxIndex = maxIndex;
        game.currentPosition = game.getPos(game.curMoveIndex);

        game.gameName = gameName;
        game.isWhiteTurn = game.firstTurnDefault;
        game.whitePlayerName = whitePlayerName;
        game.blackPlayerName = blackPlayerName;
        game.whiteElo = whiteElo;
        game.blackElo = blackElo;
        game.whitePlayerPfpUrl = whitePlayerPfpUrl;
        game.blackPlayerPfpUrl = blackPlayerPfpUrl;
        game.isVsComputer = isVsComputer;
        game.gameHash = String.valueOf(game.hashCode());
        game.isWebGame = isWebGame;
        game.isWhiteOriented = isWhiteOriented;
        game.setGameStateToAbsIndex(game.curMoveIndex);
        return game;

    }

    public boolean isWhiteOriented() {
        return isWhiteOriented;
    }

    public String getGameName() {
        return gameName;
    }

    public boolean isVsComputer() {
        return isVsComputer;
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

    public String getGameType() {
        return gameType;
    }

    public boolean isWebGame() {
        return isWebGame;
    }

    public boolean isWebGameInitialized() {
        return isWebGameInitialized;
    }

    public void setWebGameInitialized(boolean webGameInitialized) {
        isWebGameInitialized = webGameInitialized;
    }

    public void leaveWebGame() {
        if (isWebGame) {
            App.sendRequest(INTENT.LEAVEGAME, "");
        } else {
            ChessConstants.mainLogger.error("trying to access webgame, without being one");
        }
    }

    public void initWebGame(String blackPlayerName, int blackElo, String player2PfpUrl, boolean isWhiteOriented) {
        this.blackPlayerName = blackPlayerName;
        this.blackElo = blackElo;
        this.blackPlayerPfpUrl = player2PfpUrl;
        this.isWhiteOriented = isWhiteOriented;
        this.isWhiteTurn = true;
        if (isMainGame) {
            Platform.runLater(() -> {
                // now do a full init
                centralControl.mainScreenController.setPlayerLabels(whitePlayerName, whiteElo, blackPlayerName, blackElo,isWhiteOriented);
                centralControl.mainScreenController.setPlayerIcons(whitePlayerPfpUrl, player2PfpUrl,isWhiteOriented);
                centralControl.mainScreenController.setupWithGame(this, MainScreenState.ONLINE, true);
            });
        }


    }

    public ChessGame cloneGame() {
        List<ChessPosition> clonedMoves = moves.stream().map(ChessPosition::clonePosition).toList();
        return ChessGame.getClonedGame(clonedMoves, gameState.cloneState(), gameName, whitePlayerName, blackPlayerName, whiteElo, blackElo, whitePlayerPfpUrl, blackPlayerPfpUrl, isVsComputer, isWebGame, isWhiteOriented, maxIndex);
    }

    public void setMainGame(ChessCentralControl centralControl) {
        this.centralControl = centralControl;
        this.isMainGame = true;
        moveToMoveIndexAbsolute(-1, false, false);
        centralControl.chessBoardGUIHandler.reloadNewBoard(getPos(curMoveIndex), isWhiteOriented);
        centralControl.mainScreenController.setMoveLabels(curMoveIndex, maxIndex);
        if (!isWebGameInitialized && isWebGame && centralControl.mainScreenController.currentState.equals(MainScreenState.ONLINE)) {
            // since we dont have any info on the second player, we only do a basic setup of the UI
            // also send request for online match here
            client.sendRequest(INTENT.CREATEGAME, gameType);
            centralControl.mainScreenController.setPlayerIcons(whitePlayerPfpUrl, ProfilePicture.DEFAULT.urlString,isWhiteOriented);
            centralControl.mainScreenController.setPlayerLabels(whitePlayerName, whiteElo, "Loading...", 0,isWhiteOriented);


        } else {
            // in this case all the info is here so we can do a full Ui setup
            // now set player icons
            centralControl.mainScreenController.setPlayerIcons(whitePlayerPfpUrl, blackPlayerPfpUrl,isWhiteOriented);
            centralControl.mainScreenController.setPlayerLabels(whitePlayerName, whiteElo, blackPlayerName, blackElo,isWhiteOriented);
            if (maxIndex > -1) {
                // this game has some moves we need to add to moves played
                String[] pgns = gameToPgnArr();
                for (String movePgn : pgns) {
                    centralControl.chessActionHandler.addToMovesPlayed(movePgn);
                }
            }

        }
    }

    public void clearMainGame() {
        this.centralControl = null;
        this.isMainGame = false;
    }

    public void sendMessageToInfo(String message) {
        if (isMainGame) {
            if (isWebGame) {
                Platform.runLater(() -> {
                    centralControl.chessActionHandler.appendNewMessageToChat(message);
                });
            } else {
                centralControl.chessActionHandler.appendNewMessageToChat(message);
            }
        } else {
            ChessConstants.mainLogger.error("Trying to write message when not main game");
        }
    }

    public void moveToEndOfGame(boolean isNotAppThread) {
        if (maxIndex != curMoveIndex) {
            changeToDifferentMove(maxIndex - curMoveIndex, isNotAppThread, true);
        } else {
            ChessConstants.mainLogger.debug("Already at end of game");
        }
    }

    public void moveToMoveIndexAbsolute(int absIndex, boolean isNotAppThread, boolean animateIfPossible) {
        if (absIndex <= maxIndex && absIndex != curMoveIndex) {
            if (isMainGame) {
                centralControl.chessBoardGUIHandler.clearAllHighlights();
            }
            if (animateIfPossible) {
                // we will move one back then we will move forward to give the impresison that you are animating the move that created that positon
                int dirWithOneMore = absIndex - curMoveIndex - 1;
                if (curMoveIndex + dirWithOneMore >= -1) {
                    // means we wont go too far back so we can do it
                    changeToDifferentMove(dirWithOneMore, isNotAppThread, true);
                    // this call will animate it
                    changeToDifferentMove(1, isNotAppThread, false);
                } else {
                    changeToDifferentMove(absIndex - curMoveIndex, isNotAppThread, false);
                }
            } else {
                changeToDifferentMove(absIndex - curMoveIndex, isNotAppThread, true);
            }
        } else {
            ChessConstants.mainLogger.error("absolute index provided out of bounds!");
        }
    }

    public void changeToDifferentMove(int dir, boolean isNotAppThread, boolean noAnimate) {
        if (dir != 0 && (!isMainGame || !centralControl.chessBoardGUIHandler.inTransition || !centralControl.asyncController.simTask.isMakingMove())) {
            int moveChange = Math.abs(dir % 2);
            // if not an even number the turn flips
            if (moveChange == 1) {
                isWhiteTurn = !isWhiteTurn;
            }
            int tempForGameStates = curMoveIndex;
            curMoveIndex += dir;
            if (isMainGame) {
                if (isWebGame || isNotAppThread) {
                    Platform.runLater(() -> {
                        if (curMoveIndex >= 0) {
                            centralControl.chessActionHandler.highlightMovesPlayedLine(curMoveIndex);
                        } else {
                            centralControl.chessActionHandler.clearMovesPlayedHighlight();
                        }
                    });
                } else {
                    if (curMoveIndex >= 0) {
                        centralControl.chessActionHandler.highlightMovesPlayedLine(curMoveIndex);
                    } else {
                        centralControl.chessActionHandler.clearMovesPlayedHighlight();
                    }
                }

            }
            ChessConstants.mainLogger.debug("New curIndex: " + curMoveIndex);
            ChessPosition newPos = getPos(curMoveIndex);

            if (Math.abs(dir) > 1 || (isMainGame && centralControl.mainScreenController.currentState.equals(MainScreenState.SANDBOX)) || noAnimate) {
                // cannot try to animate move (actually can but not neccesary)
                if (isMainGame) {
                    if (isWebGame || isNotAppThread) {
                        Platform.runLater(() -> {
                            updateChessBoardGui(newPos, currentPosition);

                        });
                    } else {
                        updateChessBoardGui(newPos, currentPosition);
                    }
                }
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

                if (isMainGame) {
                    ChessMove move;
                    if (isReverse) {
                        move = currentPosition.getMoveThatCreatedThis().reverseMove();
                        if (curMoveIndex != -1 && !move.isCustomMove()) {
                            // always highlight the move that created the current pos
                            if (isWebGame || isNotAppThread) {
                                Platform.runLater(() -> {
                                    centralControl.chessBoardGUIHandler.highlightMove(newPos.getMoveThatCreatedThis(), isWhiteOriented);

                                });
                            } else {
                                centralControl.chessBoardGUIHandler.highlightMove(newPos.getMoveThatCreatedThis(), isWhiteOriented);

                            }
                        }
                    } else {
                        move = newPos.getMoveThatCreatedThis();

                    }
                    if (isWebGame || isNotAppThread) {
                        Platform.runLater(() -> {
                            if (!move.isCustomMove()) {
                                chessBoardGuiMakeMoveFromCurrent(move, isReverse, currentPosition, newPos);
                            } else {
                                updateChessBoardGui(newPos, currentPosition);
                            }
                        });
                    } else {

                        if (!move.isCustomMove()) {
                            chessBoardGuiMakeMoveFromCurrent(move, isReverse, currentPosition, newPos);
                        } else {
                            updateChessBoardGui(newPos, currentPosition);
                        }
                    }
                }


            }
            // setting eval bar if checkmated
            if (isMainGame) {
                if (isWebGame || isNotAppThread) {
                    Platform.runLater(() -> {
                        if (gameState.isCheckMated()[0]) {
                            int eval = gameState.isCheckMated()[1] ? 1000000 : -1000000;
                            centralControl.mainScreenController.setEvalBar(eval, -1, false, true);
                        } else {
                            centralControl.mainScreenController.hideGameOver();
                        }
                    });
                } else {
                    if (gameState.isCheckMated()[0]) {
                        int eval = gameState.isCheckMated()[1] ? 1000000 : -1000000;
                        centralControl.mainScreenController.setEvalBar(eval, -1, false, true);
                    } else {
                        centralControl.mainScreenController.hideGameOver();
                    }
                }
            }

            // playing sound effects
            if (isMainGame) {
                if (newPos != ChessConstants.startBoardState) {
                    // this essentialy is checking to make sure you are not at the very start of the game as the start position does not have an actual move that created it
                    ChessMove move = newPos.getMoveThatCreatedThis();
                    App.soundPlayer.playMoveEffect(move, AdvancedChessFunctions.isChecked(!move.isWhite(), newPos.board), gameState.isCheckMated()[0] || gameState.isStaleMated());

                } else {
                    // else play simple move sound
                    App.soundPlayer.playEffect(Effect.MOVE);
                }
            }

            currentPosition = newPos;
        } else {
            // wait for transition
            ChessConstants.mainLogger.error("Dir zero or no transition, so did not move to new index");
//            App.messager.sendMessageQuick("Wait",false);
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

    private void setGameStateToAbsIndex(int absIndex) {
        int dir = absIndex - gameState.getCurrentIndex();
        updateGameStates(dir, gameState.getCurrentIndex());
    }

    public void reset() {
        this.isWhiteTurn = firstTurnDefault;
        curMoveIndex = -1;
        maxIndex = -1;
        ChessPosition newPos = getPos(curMoveIndex);
        gameState.reset();
        currentPosition = newPos;
        clearIndx(false);
        if (isMainGame) {
            updateChessBoardGui(newPos, currentPosition);
            centralControl.chessActionHandler.reset();
            centralControl.chessBoardGUIHandler.resetEverything(isWhiteOriented);

        }

    }

    public List<ChessPosition> getPositions() {
        return moves;
    }

    private void chessBoardGuiMakeMoveFromCurrent(ChessMove move, boolean isReverse, ChessPosition currentPosition, ChessPosition newPos) {
        System.out.println("Animating");
        GeneralChessFunctions.printBoardDetailed(newPos.board);
        if (isMainGame) {

            if (move.isEating() && !isReverse) {
                // needs to be before move
                int eatenAddIndex = move.getEatingIndex();
                centralControl.chessBoardGUIHandler.addToEatenPieces(eatenAddIndex, !move.isWhite(), isWhiteOriented);
                centralControl.chessBoardGUIHandler.removeFromChessBoard(move.getNewX(), move.getNewY(), !move.isWhite(), isWhiteOriented);
            }
            if (move.isEnPassant()) {
                if (!isReverse) {
                    int backDir = move.isWhite() ? 1 : -1;
                    int eatY = move.getNewY() + backDir;
                    int eatenAddIndex = GeneralChessFunctions.getBoardWithPiece(move.getNewX(), eatY, !move.isWhite(), currentPosition.board);
                    centralControl.chessBoardGUIHandler.addToEatenPieces(eatenAddIndex, !move.isWhite(), isWhiteOriented);
                    centralControl.chessBoardGUIHandler.removeFromChessBoard(move.getNewX(), eatY, !move.isWhite(), isWhiteOriented);
                } else {
                    int backDir = move.isWhite() ? 1 : -1;
                    int eatY = move.getOldY() + backDir;
                    int eatenAddIndex = GeneralChessFunctions.getBoardWithPiece(move.getOldX(), eatY, !move.isWhite(), newPos.board);
                    centralControl.chessBoardGUIHandler.removeFromEatenPeices(eatenAddIndex, !move.isWhite() == isWhiteOriented);
                    centralControl.chessBoardGUIHandler.addToChessBoard(move.getOldX(), eatY, eatenAddIndex, !move.isWhite(), isWhiteOriented);
                }

            }
            if (move.isCastleMove()) {
                // shortcastle is +x dir longcastle = -2x dir
                if (isReverse) {
                    int dirFrom = move.getOldX() == 6 ? 1 : -2;
                    int dirTo = move.getOldX() == 6 ? -1 : 1;
                    // uncastle
                    centralControl.chessBoardGUIHandler.movePieceOnBoard(move.getOldX() + dirTo, move.getOldY(), move.getOldX() + dirFrom, move.getNewY(), move.isWhite(), isWhiteOriented);
                } else {
                    int dirFrom = move.getNewX() == 6 ? 1 : -2;
                    int dirTo = move.getNewX() == 6 ? -1 : 1;
                    centralControl.chessBoardGUIHandler.movePieceOnBoard(move.getNewX() + dirFrom, move.getOldY(), move.getNewX() + dirTo, move.getNewY(), move.isWhite(), isWhiteOriented);
                }

            }
            // this is where the piece actually moves
            if (!move.isPawnPromo()) {
                // in pawn promo we need to handle differently as the piece changes
                centralControl.chessBoardGUIHandler.movePieceOnBoard(move.getOldX(), move.getOldY(), move.getNewX(), move.getNewY(), move.isWhite(), isWhiteOriented);

            }
            // move
            else {
                if (isReverse) {
                    centralControl.chessBoardGUIHandler.removeFromChessBoard(move.getOldX(), move.getOldY(), move.isWhite(), isWhiteOriented);
                    centralControl.chessBoardGUIHandler.moveNewPieceOnBoard(move.getOldX(), move.getOldY(), move.getNewX(), move.getNewY(), ChessConstants.PAWNINDEX, move.isWhite(), isWhiteOriented);

                } else {
                    centralControl.chessBoardGUIHandler.removeFromChessBoard(move.getOldX(), move.getOldY(), move.isWhite(), isWhiteOriented);
                    centralControl.chessBoardGUIHandler.moveNewPieceOnBoard(move.getOldX(), move.getOldY(), move.getNewX(), move.getNewY(), move.getPromoIndx(), move.isWhite(), isWhiteOriented);


                }
            }
            if (move.isEating() && isReverse) {
                // need to create a piece there to undo eating
                // must be after moving
                int pieceIndex = move.getEatingIndex();
                centralControl.chessBoardGUIHandler.addToChessBoard(move.getOldX(), move.getOldY(), pieceIndex, !move.isWhite(), isWhiteOriented);
                centralControl.chessBoardGUIHandler.removeFromEatenPeices(pieceIndex, !move.isWhite() == isWhiteOriented);

            }
            if (!isReverse) {
                centralControl.chessBoardGUIHandler.highlightMove(move, isWhiteOriented);
            }
        } else {
            ChessConstants.mainLogger.error("Trying to change gui make move when not main game");
        }


    }

    private void updateChessBoardGui(ChessPosition newPos, ChessPosition currentPos) {
        GeneralChessFunctions.printBoardDetailed(newPos.board);
        if (isMainGame) {
            List<String>[] changes = AdvancedChessFunctions.getChangesNeeded(currentPos.board, newPos.board);
            List<String> thingsToAdd = changes[0];
            List<String> thingsToRemove = changes[1];


            // all of this is to update the pieces on the gui

            int i = 0;
            int z = 0;

            while (z < thingsToRemove.size()) {
                // edge case where you need to remove more to the board
                String[] Delinfo = thingsToRemove.get(z).split(",");
                int OldX = Integer.parseInt(Delinfo[0]);
                int OldY = Integer.parseInt(Delinfo[1]);
                boolean isWhite = Delinfo[2].equals("w");
                int brdRmvIndex = Integer.parseInt(Delinfo[3]);
                centralControl.chessBoardGUIHandler.removeFromChessBoard(OldX, OldY, isWhite, isWhiteOriented);
                centralControl.chessBoardGUIHandler.addToEatenPieces(brdRmvIndex, isWhite, isWhiteOriented);


                z++;

            }
            while (i < thingsToAdd.size()) {
                // edge case where you need to add more to the board
                String[] Moveinfo = thingsToAdd.get(i).split(",");
                int NewX = Integer.parseInt(Moveinfo[0]);
                int NewY = Integer.parseInt(Moveinfo[1]);
                int brdAddIndex = Integer.parseInt(Moveinfo[3]);
                boolean isWhite = Moveinfo[2].equals("w");
                centralControl.chessBoardGUIHandler.addToChessBoard(NewX, NewY, brdAddIndex, isWhite, isWhiteOriented);
                centralControl.chessBoardGUIHandler.removeFromEatenPeices(Integer.parseInt(Moveinfo[3]), isWhite == isWhiteOriented);


                i++;


            }
        } else {
            ChessConstants.mainLogger.error("change move called on a chessgame that is not main");
        }


    }

    public ChessPosition getPos(int moveIndex) {
        if (moveIndex >= 0 && moveIndex < moves.size()) {
            ChessPosition newPos = moves.get(moveIndex);
            if (newPos == null) {
                ChessConstants.mainLogger.error("NewPosNull");
            }
            return newPos;
        } else if (moveIndex == -1) {
            // intial board state
            return ChessConstants.startBoardState;
        } else {
            ChessConstants.mainLogger.error("Boardwrapper get move index out of range");
            return null;
        }

    }

    public void clearIndx(boolean updateStates) {
        // this is for if you undo moves and create a new branch by making a move
        maxIndex = curMoveIndex;
        int to = moves.size();
        if (to > curMoveIndex + 1) {
            ChessConstants.mainLogger.debug(String.format("Clearing board entries from %d", curMoveIndex + 1));

            moves.subList(curMoveIndex + 1, to).clear();
        }
        if (updateStates) {
            gameState.clearIndexes(curMoveIndex);
        }
        if (isMainGame) {
            centralControl.chessActionHandler.clearMovesPlayedUpToIndex(curMoveIndex);
        }


    }

    // only used for web move
    public void makePgnMove(String pgn, boolean isWebMove) {
        if (curMoveIndex != maxIndex) {
            moveToEndOfGame(isWebMove);
        }

        ChessMove move = pgnToMove(pgn, getPos(curMoveIndex), isWhiteTurn);
        System.out.println("Movepgn: " + pgn + " movestr: " + move);
        ChessPosition newPos = new ChessPosition(getPos(curMoveIndex), gameState, move);
        MakeMove(newPos, move, isWebMove, false);
    }

    // one called for all local moves
    public void makeNewMove(ChessMove move, boolean isComputerMove, boolean isDragMove) {
        if (!isComputerMove) {
            // clear any entries, you are branching off
            if (curMoveIndex != maxIndex) {
                clearIndx(true);
                if (isMainGame) {
                    // for campaign where you have limited redos based on difficulty
                    centralControl.chessActionHandler.incrementNumRedos();
                }
            }
        } else {
            // jump to front
            moveToEndOfGame(isComputerMove);
        }
        ChessPosition newPos = new ChessPosition(currentPosition, gameState, move);
        MakeMove(newPos, move, isComputerMove, isDragMove);


    }

    // only for sandbox
    public void makeCustomMoveSandbox(ChessPosition newPos) {
        MakeMove(newPos, newPos.getMoveThatCreatedThis(), false, true);
    }

    private void MakeMove(ChessPosition newPosition, ChessMove move, boolean isWebMove, boolean isDragMove) {
        isWhiteTurn = !isWhiteTurn;
        maxIndex++;
        curMoveIndex++;
        moves.add(newPosition);
        boolean isDraw = gameState.makeNewMoveAndCheckDraw(newPosition);
        if (!isMainGame || !centralControl.mainScreenController.currentState.equals(MainScreenState.SANDBOX)) {
            if (isDraw) {
                gameState.setStaleMated();
                if (isMainGame) {
                    ChessConstants.mainLogger.debug("stalemate");
                    if (isWebMove) {
                        Platform.runLater(() -> {
                            centralControl.mainScreenController.setEvalBar(0, -1, false, true);
                        });

                    } else {
                        centralControl.mainScreenController.setEvalBar(0, -1, false, true);

                    }
                    App.soundPlayer.playEffect(Effect.GAMEOVER);
                }
            } else if (AdvancedChessFunctions.isAnyNotMovePossible(!move.isWhite(), newPosition, gameState)) {
                if (AdvancedChessFunctions.isCheckmated(!move.isWhite(), newPosition, gameState)) {
                    gameState.setCheckMated(move.isWhite());
                    if (isMainGame) {
                        ChessConstants.mainLogger.debug("checkmate");
                        if (isWebMove) {
                            Platform.runLater(() -> {
                                centralControl.mainScreenController.setEvalBar(move.isWhite() ? ChessConstants.WHITECHECKMATEVALUE : ChessConstants.BLACKCHECKMATEVALUE, -1, false, true);
                            });

                        } else {
                            centralControl.mainScreenController.setEvalBar(move.isWhite() ? ChessConstants.WHITECHECKMATEVALUE : ChessConstants.BLACKCHECKMATEVALUE, -1, false, true);

                        }
                    }
                } else {
                    gameState.setStaleMated();
                    if (isMainGame) {
                        ChessConstants.mainLogger.debug("stalemate");
                        if (isWebMove) {
                            Platform.runLater(() -> {
                                centralControl.mainScreenController.setEvalBar(0, -1, false, true);
                            });

                        } else {
                            centralControl.mainScreenController.setEvalBar(0, -1, false, true);

                        }
                    }

                }
                if (isMainGame) {
                    App.soundPlayer.playEffect(Effect.GAMEOVER);
                }
            }
        }
        if (isMainGame) {
            //  chessboard gui make move stuff

            if (isWebMove) {
//                System.out.println("Web move: now supposed to update position");
                Platform.runLater(() -> {
                    chessBoardGuiMakeMoveFromCurrent(move, false, currentPosition, newPosition);
                });
            } else {

                if (isDragMove) {
                    // need to higlight move explicitly as below method does not
                    centralControl.chessBoardGUIHandler.highlightMove(move, isWhiteOriented);
                    updateChessBoardGui(newPosition, currentPosition);
                } else {
                    chessBoardGuiMakeMoveFromCurrent(move, false, currentPosition, newPosition);
                }

            }
            sendMessageToInfo("Move: " + PgnFunctions.moveToPgn(move, newPosition, gameState));
        }
        currentPosition = newPosition;
        // side panel stuff
        if (isMainGame && isWebMove) {
            Platform.runLater(() -> {
                centralControl.mainScreenController.updateSimpleAdvantageLabels();
                centralControl.chessActionHandler.makeBackendUpdate(centralControl.mainScreenController.currentState, true, false);
                centralControl.mainScreenController.setMoveLabels(curMoveIndex, maxIndex);
                if (centralControl.mainScreenController.currentState.equals(MainScreenState.CAMPAIGN)) {
                    centralControl.asyncController.simTask.setMakingMoveFalse();
                }
            });
        } else if (isMainGame) {
            if (centralControl.mainScreenController.currentState.equals(MainScreenState.VIEWER)) {
                centralControl.chessActionHandler.updateViewerSuggestions();
            }

            centralControl.mainScreenController.updateSimpleAdvantageLabels();
            centralControl.chessActionHandler.makeBackendUpdate(centralControl.mainScreenController.currentState, true, false);
            centralControl.mainScreenController.setMoveLabels(curMoveIndex, maxIndex);
        }
        if (isMainGame) {
            App.soundPlayer.playMoveEffect(move, AdvancedChessFunctions.isChecked(!move.isWhite(), newPosition.board), gameState.isCheckMated()[0] || gameState.isStaleMated());
        }
        if (isWebGame && !isWebMove) {
            // todo with other things: add time
            client.sendRequest(INTENT.MAKEMOVE, PgnFunctions.moveToPgn(move, newPosition, gameState) + ",10");

        }
        System.out.println(gameState.toString());
    }

    // turning position to pgn
    private ChessMove pgnToMove(String pgn, ChessPosition currentPosition, boolean isWhiteMove) {
//        GeneralChessFunctions.printBoardDetailed(currentPosition.board);
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
                gameState.setCheckMated(isWhiteMove);
            }


            x = PgnFunctions.turnFileStrToInt(pgn.charAt(0));
            y = Integer.parseInt(String.valueOf((pgn.charAt(1)))) - 1;
            // flip y as pgn board is inverted to my board
            y = 7 - y;


            int OldY = AdvancedChessFunctions.getColumnGivenFile(x, y, isWhiteMove, isWhiteMove ? currentPosition.board.getWhitePieces()[pieceType] : currentPosition.board.getBlackPieces()[pieceType]);
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
                    gameState.setCheckMated(isWhiteMove);
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
                                    int oldX = AdvancedChessFunctions.getEnPassantOriginX(x, y, isWhiteMove, isWhiteMove ? currentPosition.board.getWhitePieces()[pieceType] : currentPosition.board.getBlackPieces()[pieceType]);
                                    return new ChessMove(oldX, y + backdir, x, y, ChessConstants.EMPTYINDEX, pieceType, isWhiteMove, false, false, ChessConstants.EMPTYINDEX, true, false);

                                }


                            }
                        }
                    }
                }
            }


            XYcoord oldCoords = AdvancedChessFunctions.findOldCoordinates(x, y, pieceType, ambgX, ambgY, isWhiteMove, isEating, currentPosition, gameState);
            if (pieceType == ChessConstants.ROOKINDEX) {
                gameState.checkRemoveRookMoveRight(oldCoords.x, oldCoords.y, isWhiteMove);
            }

            int eatingIndex = GeneralChessFunctions.getBoardWithPiece(x, y, !isWhiteMove, currentPosition.board);

            return new ChessMove(oldCoords.x, oldCoords.y, x, y, promoIndex, pieceType, isWhiteMove, false, isEating, eatingIndex, false, false);

        }
    }

    private ChessPosition makeNewMovePGN(String move, ChessPosition currentPos, boolean isWhiteMove) {
        ChessMove movePgn = pgnToMove(move, currentPos, isWhiteMove);
        return new ChessPosition(currentPos, gameState, movePgn);

    }

    public String gameToPgn() {
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
            gameState.makeNewMoveAndCheckDraw(currentState);
            positions.add(currentState);
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

}
