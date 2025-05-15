package chessengine.ChessRepresentations;

import chessengine.App;
import chessengine.Audio.Effect;
import chessengine.CentralControlComponents.ChessCentralControl;
import chessengine.Enums.MainScreenState;
import chessengine.Misc.Constants;
import chessserver.ChessRepresentations.ChessGame;
import chessserver.ChessRepresentations.ChessMove;
import chessserver.ChessRepresentations.ChessPosition;
import chessserver.Enums.Gametype;
import chessserver.Enums.INTENT;
import chessserver.Enums.ProfilePicture;
import chessserver.Functions.AdvancedChessFunctions;
import chessserver.Functions.GeneralChessFunctions;
import chessserver.Functions.PgnFunctions;
import chessserver.Misc.ChessConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientsideChessGameWrapper {
    private final static Logger logger = LogManager.getLogger("ChessGame_Gui");
    private ChessGame game;
    private ChessCentralControl centralControl;
    private boolean isMainGame = true;


    /**
     * These variables and methods all are only used in online games
     **/

    private boolean isWebGame;

    public boolean isWebGame() {
        return isWebGame;
    }

    public Gametype getWebGameType() {
        return webGameType;
    }

    public boolean isWebGameInitialized() {
        return isWebGameInitialized;
    }

    private Gametype webGameType;
    private boolean isWebGameInitialized;
    private boolean isWebGameFinished;

    public boolean isActiveWebGame() {
        return isWebGame && (isWebGameInitialized && !isWebGameFinished);
    }

    public void setOnlineGameFinished() {
        isWebGameFinished = true;
    }

    public boolean isCurrentWebGameInitialized() {
        return isWebGameInitialized;
    }


    public void leaveWebGame() {
        if (isWebGame) {
            App.sendRequest(INTENT.LEAVEGAME, "",null,true);
        } else {
            logger.error("trying to access webgame, without being one");
        }
    }

    public void initWebGame(String onlinePlayerName, int onlineElo, String onlinePfpUrl, boolean isClientWhite) {
        isWebGameInitialized = true;
        game.updateInfoFromPartialInit(onlinePlayerName,onlineElo,onlinePfpUrl,isClientWhite);
        if (isMainGame) {
            // now do a full init
            loadGameGraphics();
            centralControl.mainScreenController.setPlayerLabels(game.getWhitePlayerName(),game.getWhiteElo(), game.getBlackPlayerName(), game.getBlackElo(), game.isWhiteOriented());
            centralControl.mainScreenController.setPlayerIcons(game.getWhitePlayerPfpUrl(), game.getBlackPlayerPfpUrl(), game.isWhiteOriented());
            centralControl.chessActionHandler.makeBackendUpdate(centralControl.mainScreenController.currentState,false,true);
        }


    }

    public ClientsideChessGameWrapper(ChessCentralControl centralControl){
        this.centralControl = centralControl;
    }

    public void loadInNewGame(ChessGame gameToWrap,boolean isWebGame,Gametype gametype){

        this.game = gameToWrap;
        this.isWebGame = isWebGame;
        this.webGameType = gametype;
        loadGameGraphics();
    }

    private void loadGameGraphics(){
        moveToMoveIndexAbsolute(game.getMinIndex(), false);
        centralControl.chessBoardGUIHandler.reloadNewBoard(game.getPos(game.getCurMoveIndex()), game.isWhiteOriented());
        centralControl.mainScreenController.setMoveLabels(game.getCurMoveIndex(), game.getMaxIndex());
        if (!isWebGameInitialized && isWebGame && centralControl.mainScreenController.currentState== MainScreenState.ONLINE) {
            // since we dont have any info on the second player, we only do a basic setup of the UI
            // also send request for online match here

            centralControl.mainScreenController.setPlayerIcons(game.getWhitePlayerPfpUrl(), ProfilePicture.DEFAULT.urlString, game.isWhiteOriented());
            centralControl.mainScreenController.setPlayerLabels(game.getWhitePlayerName(), game.getWhiteElo(), "Loading...", 0, game.isWhiteOriented());


        } else {
            // in this case all the info is here so we can do a full Ui setup
            // now set player icons
            centralControl.mainScreenController.setPlayerIcons(game.getWhitePlayerPfpUrl(), game.getBlackPlayerPfpUrl(), game.isWhiteOriented());
            centralControl.mainScreenController.setPlayerLabels(game.getWhitePlayerName(), game.getWhiteElo(), game.getBlackPlayerName(), game.getBlackElo(), game.isWhiteOriented());
            if (game.getMaxIndex() > game.getMinIndex()) {
                // this game has some moves we need to add to moves played
                String[] pgns = game.gameToPgnArr();
                for (String movePgn : pgns) {
                    centralControl.chessActionHandler.addToMovesPlayed(movePgn);
                }
            }
        }
    }

    public void sendMessageToInfo(String message) {
        if (isMainGame) {
            centralControl.chessActionHandler.appendNewMessageToChat(message);
        } else {
            logger.error("Trying to write message when not main game");
        }
    }

    public void moveToEndOfGame(boolean animateIfPossible) {
        centralControl.partialReset(game.isWhiteOriented());
        if (game.getMaxIndex() != game.getCurMoveIndex()) {
            int dir = game.getMaxIndex()-game.getCurMoveIndex();
            changeToDifferentMove(dir, animateIfPossible,true);
        } else {
            logger.debug("Already at end of game");
        }
    }

    public void moveToMoveIndexAbsolute(int absIndex, boolean animateIfPossible) {
        centralControl.partialReset(game.isWhiteOriented());
        if (absIndex <= game.getMaxIndex() && absIndex != game.getCurMoveIndex()) {
            if (animateIfPossible) {
                // we will move one back then we will move forward to give the impresison that you are animating the move that created that positon
                int dirWithOneMore = absIndex - game.getCurMoveIndex() - 1;
                if (game.getCurMoveIndex() + dirWithOneMore >= game.getMinIndex()) {
                    // means we wont go too far back so we can do it
                    changeToDifferentMove(dirWithOneMore, true,true);
                    // this call will animate it
                    changeToDifferentMove(1, false,true);
                } else {
                    changeToDifferentMove(absIndex - game.getCurMoveIndex(), false,true);
                }
            } else {
                changeToDifferentMove(absIndex - game.getCurMoveIndex(), true,true);
            }
            game.moveToMoveIndexAbsolute(absIndex);
        } else {
            logger.error("absolute index provided out of bounds!");
        }
    }
    public void changeToDifferentMove(int dir, boolean noAnimate) {
        changeToDifferentMove(dir,noAnimate,false);
    }

    private void changeToDifferentMove(int dir, boolean noAnimate,boolean internalCall) {
        if(!internalCall){
            centralControl.partialReset(game.isWhiteOriented());
        }
        int newIndex = game.getCurMoveIndex()+dir;
        if (dir != 0 && newIndex >= game.getMinIndex() && newIndex <= game.getMaxIndex() && (!isMainGame || !centralControl.chessBoardGUIHandler.inTransition || !centralControl.asyncController.simTask.isMakingMove())) {
            if (isMainGame) {
                if (newIndex >= 0) {
                    centralControl.chessActionHandler.highlightMovesPlayedLine(newIndex);
                } else {
                    centralControl.chessActionHandler.clearMovesPlayedHighlight();
                }
            }
            ChessPosition newPos = game.getPos(newIndex);

            if (Math.abs(dir) > 1 || (isMainGame && centralControl.mainScreenController.currentState == MainScreenState.SANDBOX) || noAnimate) {
                // cannot try to animate move (actually can but not neccesary)
                if (isMainGame) {
                    centralControl.chessBoardGUIHandler.updateChessBoardGui(newPos, game.getCurrentPosition(),game.isWhiteOriented());
                    if(!newPos.getMoveThatCreatedThis().equals(ChessConstants.startMove)){
                        centralControl.chessBoardGUIHandler.highlightMove(newPos.getMoveThatCreatedThis(), game.isWhiteOriented());
                    }
                }
            } else {
                boolean isReverse = dir < 0;
                if (isMainGame) {
                    ChessMove move;
                    if (isReverse) {
                        move = game.getCurrentPosition().getMoveThatCreatedThis().reverseMove();
                        if (newIndex != game.getMinIndex() && !move.isCustomMove()) {
                            // always highlight the move that created the current pos
                            centralControl.chessBoardGUIHandler.highlightMove(newPos.getMoveThatCreatedThis(), game.isWhiteOriented());
                        }
                    } else {
                        move = newPos.getMoveThatCreatedThis();

                    }
                    if (!move.isCustomMove()) {
                        centralControl.chessBoardGUIHandler.makeChessMove(move, isReverse, game.getCurrentPosition(), newPos,game.isWhiteOriented());
                    } else {
                        centralControl.chessBoardGUIHandler.updateChessBoardGui(newPos, game.getCurrentPosition(),game.isWhiteOriented());
                    }

                }


            }
            // actual backend change here
            game.changeToDifferentMove(dir);

            // setting eval bar if checkmated
            if(centralControl.mainScreenController.currentState == MainScreenState.PUZZLE){
                System.out.println(game.isAtEndOfGame() + " ?");
                // show game over at end of puzzle
                if(game.isAtEndOfGame()){
                    centralControl.mainScreenController.showGameOver("Puzzle Complete");
                }
                else{
                    centralControl.mainScreenController.hideGameOver();
                }
            }
            else{
                if (game.getGameState().isCheckMated()[0]) {
                    int eval = game.getGameState().isCheckMated()[1] ? 1000000 : -1000000;
                    centralControl.mainScreenController.setEvalBar(eval, -1, true);
                } else if (game.getGameState().isStaleMated()) {
                    centralControl.mainScreenController.setEvalBar(0, -1, true);
                } else {
                    centralControl.mainScreenController.hideGameOver();
                }
            }


            // playing sound effects
                if (!newPos.getMoveThatCreatedThis().equals(ChessConstants.startMove)) {
                    // this essentialy is checking to make sure you are not at the very start of the game as the start position does not have an actual move that created it
                    ChessMove move = newPos.getMoveThatCreatedThis();
                    App.soundPlayer.playMoveEffect(move, AdvancedChessFunctions.isChecked(!move.isWhite(), newPos.board), game.getGameState().isGameOver());

                } else {
                    // else play simple move sound
                    App.soundPlayer.playEffect(Effect.MOVE);
                }



        } else {
            // wait for transition
            logger.error("Dir zero or no transition, so did not move to new index");
        }
    }



    public void reset() {
        centralControl.fullReset(game.isWhiteOriented());
        centralControl.chessBoardGUIHandler.updateChessBoardGui(game.getPos(game.getMinIndex()), game.getCurrentPosition(),game.isWhiteOriented());
        game.reset();
    }

    public void clearIndx(boolean updateStates) {
        // this is for if you undo moves and create a new branch by making a move
        game.clearIndx(updateStates);
        if (isMainGame) {
            centralControl.chessActionHandler.clearMovesPlayedUpToIndex(game.getCurMoveIndex());
        }


    }

    // only used for web move
    public void makePgnMove(String pgn, boolean isWebMove,boolean animateIfPossible) {
        moveToEndOfGame(animateIfPossible);
        ChessMove move = game.pgnToMove(pgn, game.getPos(game.getCurMoveIndex()), game.isWhiteTurn());
        ChessPosition newPos = new ChessPosition(game.getPos(game.getCurMoveIndex()), game.getGameState(), move);
        MakeMove(newPos, move, isWebMove, false);
    }

    // one called for all local moves
    public void makeNewMove(ChessMove move, boolean isComputerMove, boolean isDragMove,boolean animateIfPossible,boolean comingFromPuzzle) {
        if(!comingFromPuzzle && centralControl.mainScreenController.currentState == MainScreenState.PUZZLE){
            // for puzzles we dont actually make the move, we just check if its the right move or a checkmate and then move on
            centralControl.puzzleGuiManager.handleMoveCheck(move,isDragMove,animateIfPossible);
            return;
        }
        if (!isComputerMove) {
            // clear any entries, you are branching off
            centralControl.clearForNewBranch(game.getCurMoveIndex() + 1);
            if (game.getCurMoveIndex() != game.getMaxIndex()) {
                clearIndx(true);
                if (isMainGame) {
                    // for campaign where you have limited redos based on difficulty
                    centralControl.chessActionHandler.incrementNumRedos();
                }
            }
        } else {
            if (game.getCurMoveIndex() != game.getMaxIndex()) {
                moveToEndOfGame(animateIfPossible);
            }

        }
        ChessPosition newPos = new ChessPosition(game.getCurrentPosition(), game.getGameState(), move);
        MakeMove(newPos, move, false, isDragMove);


    }

    // only for sandbox
    public void makeCustomMoveSandbox(ChessPosition newPos) {
        MakeMove(newPos, newPos.getMoveThatCreatedThis(), false, true);
    }

    private void MakeMove(ChessPosition newPosition, ChessMove move, boolean isWebMove, boolean isDragMove) {
        centralControl.partialReset(game.isWhiteOriented());
        ChessPosition currentPosition = game.getCurrentPosition();
        game.MakeMove(newPosition,move);
        if(isMainGame && centralControl.mainScreenController.currentState != MainScreenState.SANDBOX){
            if (game.getGameState().isStaleMated()) {
                logger.debug("stalemate");
                centralControl.mainScreenController.setEvalBar(0, -1, true);
                App.soundPlayer.playEffect(Effect.GAMEOVER);
            } else if (game.getGameState().isCheckMated()[0]) {
                logger.debug("checkmate");
                centralControl.mainScreenController.setEvalBar(move.isWhite() ? ChessConstants.WHITECHECKMATEVALUE : ChessConstants.BLACKCHECKMATEVALUE, -1, true);
                App.soundPlayer.playEffect(Effect.GAMEOVER);
            }
        }

        if (isMainGame) {
            //  chessboard gui make move stuff
            if (isWebMove) {
//                System.out.println("Web move: now supposed to update position");
                centralControl.chessBoardGUIHandler.makeChessMove(move, false, currentPosition, newPosition,game.isWhiteOriented());
            } else {
                if (isDragMove) {
                    // need to higlight move explicitly as below method does not
                    centralControl.chessBoardGUIHandler.highlightMove(move, game.isWhiteOriented());
                    centralControl.chessBoardGUIHandler.updateChessBoardGui(newPosition, currentPosition,game.isWhiteOriented());
                } else {
                    centralControl.chessBoardGUIHandler.makeChessMove(move, false, currentPosition, newPosition,game.isWhiteOriented());
                }

            }
            sendMessageToInfo("Move: " + PgnFunctions.moveToPgn(move, newPosition, game.getGameState()));
            // side panel stuff
            centralControl.mainScreenController.updateSimpleAdvantageLabels();
            centralControl.chessActionHandler.makeBackendUpdate(centralControl.mainScreenController.currentState, true, false);
            centralControl.mainScreenController.setMoveLabels(game.getCurMoveIndex(), game.getMaxIndex());
            // move effects
            App.soundPlayer.playMoveEffect(move, AdvancedChessFunctions.isChecked(!move.isWhite(), newPosition.board), game.getGameState().isGameOver());
            if (isWebGame && !isWebMove) {
                App.sendRequest(INTENT.MAKEMOVE, PgnFunctions.moveToPgn(move, newPosition, game.getGameState()), null, true);

            }
        }
    }


    public ChessGame getGame() {
        return game;
    }

    public void clearGame() {
        game = null;
        webGameType = null;
        isWebGame = false;
        isWebGameInitialized = false;
        isWebGameFinished = false;
    }
}
