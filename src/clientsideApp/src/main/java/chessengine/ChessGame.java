package chessengine;

import chessserver.INTENT;
import javafx.application.Platform;

import java.util.*;

public class ChessGame{


    private String gameType;
    private  WebSocketClient client;

    public boolean isWebGame() {
        return isWebGame;
    }

    public boolean isWebGameInitialized() {
        return isWebGameInitialized;
    }

    public void setWebGameInitialized(boolean webGameInitialized) {
        isWebGameInitialized = webGameInitialized;
    }

    private final boolean isWebGame;

    private boolean isWebGameInitialized;

    public boolean isVsComputer() {
        return isVsComputer;
    }


    private final boolean isVsComputer;

    private final String player1name;

    private String player2name;

    private final int player1Elo;

    private int player2Elo;

    public String getGameHash() {
        return gameHash;
    }

    private final String gameHash;

    public String getPlayer1name() {
        return player1name;
    }

    public String getPlayer2name() {
        return player2name;
    }

    public int getPlayer1Elo() {
        return player1Elo;
    }

    public int getPlayer2Elo() {
        return player2Elo;
    }


    public boolean isPlayer1Turn() {
        return isPlayer1Turn;
    }

    public void setPlayer1Turn(boolean player1Turn) {
        isPlayer1Turn = player1Turn;
    }

    private boolean isPlayer1Turn;

    private final List<ChessPosition> moves;

    // castling etc
    public final ChessStates gameStates;

    public ChessPosition currentPosition;

    private ChessCentralControl centralControl;
    private boolean isMainGame = false;

    public int curMoveIndex = -1;
    public int maxIndex = -1;

    public String getGameName() {
        return gameName;
    }

    private String gameName;


    private final boolean firstTurnDefault = true; // true means player 1 goes first by default

    public void leaveWebGame(){
        if(isWebGame){
            client.sendRequest(INTENT.LEAVEGAME,"");
        }
        else{
            ChessConstants.mainLogger.error("trying to access webgame, without being one");
        }
    }

    public ChessGame(String gameType){
        moves = new ArrayList<>();
        this.gameHash = String.valueOf(this.hashCode());
        isVsComputer = false;
        this.player1name = App.userManager.getUserName();
        this.player1Elo = App.userManager.getUserElo();
        this.client = App.getWebclient();
        client.setLinkedGame(this);
        this.player2name = "";
        this.player2Elo = 0;
        isWebGame = true;
        this.gameStates = new ChessStates();
        this.gameType = gameType;
        currentPosition = getPos(curMoveIndex);
    }

    public void initWebGame(String player2name,int player2Elo){
        this.player2name = player2name;
        this.player2Elo = player2Elo;
        if(isMainGame){
            Platform.runLater(() ->{
                App.mainScreenController.setPlayerLabels(App.userManager.getUserName(),App.userManager.getUserElo(), player2name,player2Elo);
            });
        }


    }

    public ChessGame(String pgn,String gameName,String player1name,String player2name,int player1Elo,int player2Elo,boolean isVsComputer,String gameHash){
        this.gameStates = new ChessStates();
        if(pgn.trim().isEmpty()){
            moves = new ArrayList<>();
        }
        else{
            String[] splitPgn = PgnFunctions.splitPgn(pgn);
            String moveText = splitPgn[1];
            moveText = moveText.replaceAll("\\n", " ");
            moves = parseMoveText(moveText);
        }
        curMoveIndex = -1;
        maxIndex = moves.size()-1;
        currentPosition = getPos(curMoveIndex);
        this.gameName = gameName;
        this.player1name = player1name;
        this.player2name = isVsComputer ? "Computer" : player2name;
        this.player1Elo = player1Elo;
        this.player2Elo = player2Elo;
        this.isPlayer1Turn = firstTurnDefault;
        this.isVsComputer = isVsComputer;
        this.gameHash = gameHash;
        this.isWebGame =false;

    }

    public ChessGame(String pgn,String gameName,String player1name,String player2name,int player1Elo,int player2Elo,boolean isVsComputer){
        this.gameStates = new ChessStates();
        if(pgn.trim().isEmpty()){
            moves = new ArrayList<>();
        }
        else{
            String[] splitPgn = PgnFunctions.splitPgn(pgn);
            String moveText = splitPgn[1];
            moveText = moveText.replaceAll("\\n", " ");
            moves = parseMoveText(moveText);
        }
        curMoveIndex = -1;
        maxIndex = moves.size()-1;
        currentPosition = getPos(curMoveIndex);
        this.gameName = gameName;
        this.player1name = player1name;
        this.player2name = isVsComputer ? "Computer" : player2name;
        this.player1Elo = player1Elo;
        this.player2Elo = player2Elo;
        this.isPlayer1Turn = firstTurnDefault;
        this.isVsComputer = isVsComputer;
        this.gameHash = String.valueOf(this.hashCode());
        this.isWebGame = false;

    }

    public ChessGame(String gameName,String player1name,String player2name,int player1Elo,int player2Elo,boolean isVsComputer){
        this.moves = new ArrayList<>();
        this.gameStates = new ChessStates();
        curMoveIndex = -1;
        maxIndex = -1;
        currentPosition = getPos(curMoveIndex);

        this.gameName = gameName;
        this.player1name = player1name;
        this.player2name = isVsComputer ? "Computer" : player2name;
        this.player1Elo = player1Elo;
        this.player2Elo = player2Elo;
        this.isPlayer1Turn = firstTurnDefault;
        this.isVsComputer = isVsComputer;
        this.gameHash = String.valueOf(this.hashCode());
        this.isWebGame = false;

    }




    public ChessGame(String pgn,String gameName,boolean isVsComputer){
        this.gameStates = new ChessStates();
        if(pgn.trim().isEmpty()){
            moves = new ArrayList<>();
        }
        else{
            String[] splitPgn = PgnFunctions.splitPgn(pgn);
            String moveText = splitPgn[1];
            moveText = moveText.replaceAll("\\n", " ");
            moves = parseMoveText(moveText);
        }
        curMoveIndex = -1;
        maxIndex = moves.size()-1;
        currentPosition = getPos(curMoveIndex);
        this.gameName = gameName;
        this.isPlayer1Turn = firstTurnDefault;
        this.player1name = "Player 1";
        this.player2name = isVsComputer ? "Computer" : "Player 2";
        this.player1Elo = 0;
        this.player2Elo = 0;
        this.isVsComputer = isVsComputer;
        this.gameHash = String.valueOf(this.hashCode());
        this.isWebGame = false;



    }

    public ChessGame(String gameName,boolean isVsComputer){
        this.moves = new ArrayList<>();
        this.gameStates = new ChessStates();
        curMoveIndex = -1;
        maxIndex = -1;
        currentPosition = getPos(curMoveIndex);

        this.gameName = gameName;
        this.isPlayer1Turn = firstTurnDefault;
        this.player1name = "Player 1";
        this.player2name = isVsComputer ? "Computer" : "Player 2";
        this.player1Elo = 0;
        this.player2Elo = 0;
        this.isVsComputer = isVsComputer;
        this.gameHash = String.valueOf(this.hashCode());
        this.isWebGame = false;



    }

    public ChessGame(List<ChessPosition> moves,ChessStates gameStates,String gameName,String player1name,String player2name,int player1Elo,int player2Elo,boolean isVsComputer){
        this.moves = moves;
        this.gameStates = gameStates;
        curMoveIndex = -1;
        maxIndex = -1;
        currentPosition = getPos(curMoveIndex);

        this.gameName = gameName;
        this.isPlayer1Turn = firstTurnDefault;
        this.player1name = "Player 1";
        this.player2name = isVsComputer ? "Computer" : "Player 2";
        this.player1Elo = 0;
        this.player2Elo = 0;
        this.isVsComputer = isVsComputer;
        this.gameHash = String.valueOf(this.hashCode());
        this.isWebGame = false;



    }

    public ChessGame cloneGame(){
        List<ChessPosition> clonedMoves = moves.stream().map(ChessPosition::clonePosition).toList();
        return new ChessGame(clonedMoves,gameStates.cloneState(),gameName,player1name,player2name,player1Elo,player2Elo,isVsComputer);
    }



    public void setMainGame(ChessCentralControl centralControl){
        this.centralControl = centralControl;
        this.isMainGame = true;
        centralControl.chessBoardGUIHandler.reloadNewBoard(getPos(curMoveIndex));
        if(isWebGame){
            // send request for online match here
            client.sendRequest(INTENT.CREATEGAME,gameType);

        }
    }

    public void clearMainGame(){
        this.centralControl = null;
        this.isMainGame = false;
    }

    public void sendMessageToInfo(String message){
        if(isMainGame){
            if(isWebGame){
                Platform.runLater(() ->{
                    centralControl.chessActionHandler.appendNewMessageToChat(message);
                });
            }
            else{
                centralControl.chessActionHandler.appendNewMessageToChat(message);
            }
        }
        else{
            ChessConstants.mainLogger.error("Trying to write message when not main game");
        }
    }

    private void moveToEndOfGame(){
        if(maxIndex != curMoveIndex){
            changeToDifferentMove(maxIndex-curMoveIndex);
        }
    }
    public void changeToDifferentMove(int dir){
        if(!centralControl.chessBoardGUIHandler.inTransition){
            int moveChange = Math.abs(dir%2);
            // if not an even number the turn flips
            if(moveChange == 1){
                isPlayer1Turn = !isPlayer1Turn;
            }
            curMoveIndex += dir;
            ChessConstants.mainLogger.debug("New curIndex: " + curMoveIndex);
            ChessPosition newPos = getPos(curMoveIndex);
            if(isMainGame){
                if(Math.abs(dir) > 1 || App.mainScreenController.currentState.equals(MainScreenState.SANDBOX)){
                    // cannot try to animate move
                    updateChessBoardGui(newPos, currentPosition);
                }
                else{

                        boolean isReverse = dir < 0;
                        ChessMove move;
                        if(isReverse){
                            centralControl.chessActionHandler.clearLocalInfoLine();
                            move = currentPosition.getMoveThatCreatedThis().reverseMove();
                            if(curMoveIndex != -1 && !move.isCustomMove()){
                                // always highlight the move that created the current pos
                                centralControl.chessBoardGUIHandler.highlightMove(newPos.getMoveThatCreatedThis());
                            }
                        }
                        else{
                            move = newPos.getMoveThatCreatedThis();
                            if(!move.isCustomMove()){
                                centralControl.chessActionHandler.addToLocalInfo(PgnFunctions.moveToPgn(newPos));
                            }
                        }
                        if(!move.isCustomMove()){
                            chessBoardGuiMakeMoveFromCurrent(move,isReverse,currentPosition,newPos);
                        }
                        else{
                            updateChessBoardGui(newPos, currentPosition);
                        }

                }
            }
            gameStates.updateAllStates(curMoveIndex);
            if(gameStates.isCheckMated()){
                App.mainScreenController.setEvalBar(ChessConstants.generalComp.getFullEval(newPos.board,gameStates,false,false),-1,false,true);
            }
            currentPosition = newPos;
        }
        else{
            // wait for transition
//            App.messager.sendMessageQuick("Wait",false);
        }
    }

    public void reset(){
        this.isPlayer1Turn = firstTurnDefault;
        curMoveIndex = -1;
        ChessPosition newPos = getPos(curMoveIndex);
        updateChessBoardGui(newPos, currentPosition);
        gameStates.updateAllStates(curMoveIndex);
        currentPosition = newPos;
        clearIndx();
        centralControl.chessActionHandler.clearLocalInfo();

    }

    public List<ChessPosition> getMoves() {
        return moves;
    }


    private void chessBoardGuiMakeMoveFromCurrent(ChessMove move,boolean isReverse,ChessPosition currentPosition,ChessPosition newPos){
            if (isMainGame) {
                if (move.isEating() && !isReverse) {
                    // needs to be before move
                    int eatenAddIndex = GeneralChessFunctions.getBoardWithPiece(move.getNewX(), move.getNewY(), !move.isWhite(), currentPosition.board);
                    centralControl.chessBoardGUIHandler.updateEatenPieces(eatenAddIndex, !move.isWhite());
                    centralControl.chessBoardGUIHandler.removeFromChessBoard(move.getNewX(), move.getNewY(), !move.isWhite());
                }
                if (move.isCastleMove()) {
                    // shortcastle is +x dir longcastle = -x dir
                    int dir = move.getNewX() == 6 ? 1 : -1;
                    if (isReverse) {
                        // uncastle
                        centralControl.chessBoardGUIHandler.movePieceOnBoard(move.getNewX() - dir, move.getOldY(), move.getNewX() + dir, move.getNewY(), move.isWhite());
                    } else {
                        centralControl.chessBoardGUIHandler.movePieceOnBoard(move.getNewX() + dir, move.getOldY(), move.getNewX() - dir, move.getNewY(), move.isWhite());
                    }

                }

                if (!move.isPawnPromo()) {
                    // in pawn promo we need to handle differently as the piece changes
                    centralControl.chessBoardGUIHandler.movePieceOnBoard(move.getOldX(), move.getOldY(), move.getNewX(), move.getNewY(), move.isWhite());

                }
                // move
                else {
                    if (isReverse) {
                        centralControl.chessBoardGUIHandler.removeFromChessBoard(move.getOldX(), move.getOldY(), move.isWhite());
                        centralControl.chessBoardGUIHandler.moveNewPieceOnBoard(move.getOldX(), move.getOldY(), move.getNewX(), move.getNewY(), ChessConstants.PAWNINDEX, move.isWhite());

                    } else {
                        centralControl.chessBoardGUIHandler.removeFromChessBoard(move.getOldX(), move.getOldY(), move.isWhite());
                        centralControl.chessBoardGUIHandler.moveNewPieceOnBoard(move.getOldX(), move.getOldY(), move.getNewX(), move.getNewY(), move.getPromoIndx(), move.isWhite());


                    }
                }
                if (move.isEating() && isReverse) {
                    // need to create a piece there to undo eating
                    // must be after moving
                    int pieceIndex = GeneralChessFunctions.getBoardWithPiece(move.getOldX(), move.getOldY(), !move.isWhite(), newPos.board);
                    centralControl.chessBoardGUIHandler.addToChessBoard(move.getOldX(), move.getOldY(), pieceIndex, !move.isWhite());
                    centralControl.chessBoardGUIHandler.removeFromEatenPeices(Integer.toString(pieceIndex), !move.isWhite());

                }
                if (!isReverse) {
                    centralControl.chessBoardGUIHandler.highlightMove(move);
                }
            } else {
                ChessConstants.mainLogger.error("Trying to change gui make move when not main game");
            }


    }


    private void updateChessBoardGui(ChessPosition newPos, ChessPosition currentPos){
        if(isMainGame){
            List<String>[] changes = AdvancedChessFunctions.getChangesNeeded(currentPos.board,newPos.board);
            List<String> thingsToAdd = changes[0];
            List<String> thingsToRemove = changes[1];


            // all of this is to update the pieces on the gui

            int i = 0;
            int z = 0;

            while(z < thingsToRemove.size()){
                // edge case where you need to remove more to the board
                String[] Delinfo = thingsToRemove.get(z).split(",");
                int OldX = Integer.parseInt(Delinfo[0]);
                int OldY = Integer.parseInt(Delinfo[1]);
                boolean isWhite = Delinfo[2].equals("w");
                int brdRmvIndex = Integer.parseInt(Delinfo[3]);
                centralControl.chessBoardGUIHandler.removeFromChessBoard(OldX,OldY,isWhite);
                centralControl.chessBoardGUIHandler.updateEatenPieces(brdRmvIndex,isWhite);


                z++;

            }
            while(i < thingsToAdd.size()){
                // edge case where you need to add more to the board
                String[] Moveinfo = thingsToAdd.get(i).split(",");
                int NewX = Integer.parseInt(Moveinfo[0]);
                int NewY = Integer.parseInt(Moveinfo[1]);
                int brdAddIndex = Integer.parseInt(Moveinfo[3]);
                boolean isWhite = Moveinfo[2].equals("w");
                centralControl.chessBoardGUIHandler.addToChessBoard(NewX,NewY,brdAddIndex,isWhite);
                centralControl.chessBoardGUIHandler.removeFromEatenPeices(Moveinfo[3],isWhite);



                i++;


            }
            currentPos = newPos;
        }
        else{
            ChessConstants.mainLogger.error("change move called on a chessgame that is not main");
        }


    }



    public ChessPosition getPos(int moveIndex){
        if(moveIndex >= 0 && moveIndex < moves.size()){
            ChessPosition newPos =  moves.get(moveIndex);
            if(newPos == null){
                ChessConstants.mainLogger.error("NewPosNull");
            }
            return newPos;
        }
        else if(moveIndex == -1){
            // intial board state
            return ChessConstants.startBoardState;
        }
        else{
            ChessConstants.mainLogger.error("Boardwrapper get move index out of range");
            return null;
        }

    }

    public void clearIndx(){
        // this is for if you undo moves and create a new branch by making a move
        maxIndex = curMoveIndex;
        int to = moves.size();
        if (to > curMoveIndex + 1) {
            ChessConstants.mainLogger.debug(String.format("Clearing board entries from %d",curMoveIndex+1));

            moves.subList(curMoveIndex+1, to).clear();
        }


    }

    public void makeNewMove(String pgn){
        if(curMoveIndex != maxIndex){
            if(isWebGame){
                Platform.runLater(this::moveToEndOfGame);
            }
            else{
                moveToEndOfGame();
            }
        }

        ChessMove move = pgnToMove(pgn,getPos(curMoveIndex),isPlayer1Turn);
        ChessPosition newPos = new ChessPosition(getPos(curMoveIndex),gameStates,move);
        MakeMove(newPos,move,true,false);
    }

    public void makeNewMove(ChessMove move,boolean isComputerMove,boolean isDragMove){
        if(!isComputerMove){
            clearIndx();
            // clear any entries, you are branching off
        }
        else{
            // jump to front
            moveToEndOfGame();
        }
        ChessPosition newPos = new ChessPosition(currentPosition,gameStates,move);
        MakeMove(newPos,move,false,isDragMove);


    }

    public void makeCustomMoveSandbox(ChessPosition newPos,boolean isWhiteMove){
        MakeMove(newPos,new ChessMove(0,0,0,0,0,0,isWhiteMove,false,false,true),false,true);
    }

    private void MakeMove(ChessPosition newPosition,ChessMove move,boolean isWebMove,boolean isDragMove){
        isPlayer1Turn = !isPlayer1Turn;
        maxIndex++;
        curMoveIndex++;
        moves.add(newPosition);
        if(isMainGame)
        {
            App.mainScreenController.setMoveLabels(curMoveIndex,maxIndex);
            if(App.mainScreenController.currentState.equals(MainScreenState.VIEWER)){
                centralControl.chessActionHandler.updateViewerSuggestions();
            }
            if(isWebMove){
                System.out.println("Web move: now supposed to update position");
                Platform.runLater(() ->{
                    chessBoardGuiMakeMoveFromCurrent(move,false,currentPosition,newPosition);
                });
            }
            else
            {
                if(isDragMove){
                    updateChessBoardGui(newPosition,currentPosition);
                }
                else{
                    chessBoardGuiMakeMoveFromCurrent(move,false,currentPosition,newPosition);
                }

            }
            sendMessageToInfo("Move: " + PgnFunctions.moveToPgn(move,newPosition.board));
        }
        currentPosition = newPosition;
        gameStates.updateMoveIndex(curMoveIndex);
        if(!App.mainScreenController.currentState.equals(MainScreenState.SANDBOX)){
            if(AdvancedChessFunctions.isAnyNotMovePossible(!move.isWhite(),newPosition.board,gameStates)){
                if(AdvancedChessFunctions.isCheckmated(!move.isWhite(),newPosition.board,gameStates)){
                    ChessConstants.mainLogger.debug("checkmate");
                    gameStates.setCheckMated();
                    App.mainScreenController.setEvalBar(move.isWhite() ? ChessConstants.WHITECHECKMATEVALUE : ChessConstants.BLACKCHECKMATEVALUE,-1,false,true);
                }
                else{
                    ChessConstants.mainLogger.debug("stalemate");
                    gameStates.setStaleMated();
                    App.mainScreenController.setEvalBar(0,-1,false,true);

                }
                if(isMainGame){
                    App.soundPlayer.playEffect(Effect.GAMEOVER);
                }
            }
        }
        if(isMainGame){
            if(AdvancedChessFunctions.isChecked(!move.isWhite(),newPosition.board)){
                App.soundPlayer.playEffect(Effect.CHECK);
            }
            else if(move.isEating()){
                App.soundPlayer.playEffect(Effect.CAPTURE);

            }
            else if(move.isPawnPromo()){
                App.soundPlayer.playEffect(Effect.PROMOTE);
            }
            else if(move.isCastleMove()) {
                App.soundPlayer.playEffect(Effect.CASTLING);
            }
            else{
                App.soundPlayer.playEffect(Effect.MOVE);
            }
        }
        if(isWebGame && !isWebMove){
            // todo with other things: add time
            client.sendRequest(INTENT.MAKEMOVE,PgnFunctions.invertPgn(PgnFunctions.moveToPgn(move,newPosition.board)) + ",10");

        }
    }


    // turning position to pgn
    private ChessMove pgnToMove(String pgn, ChessPosition currentPosition,boolean isWhiteMove){
        if(pgn.equals("O-O") || pgn.equals("0-0")){
            // short castle
            int dir = 2;
            XYcoord kingLocation = isWhiteMove ? currentPosition.board.getWhiteKingLocation() : currentPosition.board.getBlackKingLocation();
            // move the king
            int oldX = kingLocation.x;
            int oldY = kingLocation.y;
            int newX = kingLocation.x+dir;
            int newY = kingLocation.y;
            return new ChessMove(oldX,oldY,newX,newY,ChessConstants.EMPTYINDEX,ChessConstants.KINGINDEX,isWhiteMove,true,false,false);

        }
        if(pgn.equals("O-O-O") || pgn.equals("0-0-0")){
            // long castle
            int dir = -3;
            XYcoord kingLocation = isWhiteMove ? currentPosition.board.getWhiteKingLocation() : currentPosition.board.getBlackKingLocation();
            // move the king
            int oldX = kingLocation.x;
            int oldY = kingLocation.y;
            int newX = kingLocation.x+dir;
            int newY = kingLocation.y;
            return new ChessMove(oldX,oldY,newX,newY,ChessConstants.EMPTYINDEX,ChessConstants.KINGINDEX,isWhiteMove,true,false,false);

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
        if(pgn.length() == 2){
            // simple pawn move
            x = PgnFunctions.turnFileStrToInt(pgn.charAt(0));
            y = Integer.parseInt(String.valueOf((pgn.charAt(1))))-1;
            // flip y
            y = 7-y;

            int OldY = AdvancedChessFunctions.getColumnGivenFile(x,y,isWhiteMove,isWhiteMove ? currentPosition.board.getWhitePieces()[pieceType] : currentPosition.board.getBlackPieces()[pieceType]);
            return new ChessMove(x,OldY,x,y,ChessConstants.EMPTYINDEX,pieceType,isWhiteMove,false,false,false);
        }
        else{
            for(int i = start;i<pgn.length();i++){
                char c = pgn.charAt(i);
                if (c == 'x') {
                    isEating = true;
                }
                else if (c == '=') {
                    // Indicates a promotion
                    promoIndex = PgnFunctions.turnPgnPieceToPieceIndex(pgn.charAt(i+1)); // Get the promoted piece type
                }
                else if (c == '+') {
                    // Indicates a check
                }
                else if (c == '#') {
                    // Indicates a checkmate
                    gameStates.setCheckMated();
                }
                else if (Character.isDigit(c)) {
                    // If the character is a digit, it denotes the destination square
                    // Update x and y coordinates accordingly
                    // #1 subtract 1 because pgn is not zero indexed
                    // #2 7- the num because pgn is inverted compared to my setup
                    digValsY[digYCount] = 7-(Integer.parseInt(String.valueOf(c))-1);
                    digYCount++;
                }
                else if (Character.isLowerCase(c)) {
                    strValsX[strXCount] = PgnFunctions.turnFileStrToInt(c);
                    strXCount++;
                    // If the character is an uppercase letter, it denotes the piece being moved
                    // Determine the column/file of the piece (if needed) or handle any other special cases
                } else {
                    // Handle any other cases as needed


                }
            }
            // if > 1 then that means there was an ambiguous coord
            if(strXCount > 1){
                x = strValsX[1];
                ambgX = strValsX[0];

            }
            else{
                x = strValsX[0];
            }

            if(digYCount > 1){
                y = digValsY[1];
                ambgY = digValsY[0];
            }
            else{
                y = digValsY[0];
            }
            // need to flip coordinates because i use a top down coordinate system,
            // so x is fine but y needs flip
            XYcoord oldCoords = AdvancedChessFunctions.findOldCoordinates(x,y,pieceType,ambgX,ambgY,isWhiteMove,isEating,currentPosition.board,gameStates);
            if(pieceType == ChessConstants.ROOKINDEX){
                gameStates.checkRemoveRookMoveRight(oldCoords.x,oldCoords.y,isWhiteMove);
            }
            return new ChessMove(oldCoords.x,oldCoords.y,x,y,promoIndex,pieceType,isWhiteMove,false,isEating,false);

        }
    }

    private ChessPosition makeNewMovePGN(String move, ChessPosition currentPos, boolean isWhiteMove){
        ChessMove movePgn = pgnToMove(move,currentPos,isWhiteMove);
        return new ChessPosition(currentPos,gameStates,movePgn);

    }

    public String gameToPgn(){
        if(maxIndex == -1){
            // empty game
            return "";
        }
        StringBuilder sb = new StringBuilder(maxIndex);
        for(int i = 0;i<maxIndex+1;i++){
            if(i%2 == 0){
                int moveNum =(i/2)+1;
                sb.append(moveNum).append(".");
            }
            ChessPosition p = getPos(i);

            sb.append(PgnFunctions.moveToPgn(p)).append(" ");

        }
        return sb.toString();
    }

    public List<ChessPosition> parseMoveText(String pgnMoveText){
        List<ChessPosition> moves = new LinkedList<>();
        ChessPosition currentState = ChessConstants.startBoardState;

        boolean WhiteMove = true;
        int moveIndex = -1;
        for(String s : pgnMoveText.split(" ")){
            int dotIndex= s.indexOf(".");
            if(dotIndex != -1){
                s = s.substring(dotIndex+1);
            }
            moveIndex++;
            gameStates.updateMoveIndex(moveIndex);
            currentState = makeNewMovePGN(s,currentState,WhiteMove);
            moves.add(currentState);
            WhiteMove = !WhiteMove;
        }



        return moves;
    }


}
