package chessengine;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.util.List;


public class ChessActionHandler {
    private ChessCentralControl myControl;
    // viewer controls
    private VBox bestmovesBox;
    // local controls
    private TextArea localInfo;
    // sandbox controls
    private GridPane sandboxPieces;
    // online controls
    private TextArea chatArea;
    private TextField chatInput;
    private Button sendMessageButton;



    public ChessActionHandler(ChessCentralControl myControl, VBox bestmovesBox, TextArea localInfo, GridPane sandboxPieces, TextArea chatArea, TextField chatInput, Button sendMessageButton) {
        this.myControl = myControl;
        this.bestmovesBox = bestmovesBox;
        this.localInfo = localInfo;
        this.sandboxPieces = sandboxPieces;
        this.chatArea = chatArea;
        this.chatInput = chatInput;
        this.sendMessageButton = sendMessageButton;
        onlineInit();
        localInit();
        sandboxInit();
    }

    private void onlineInit(){
        sendMessageButton.setOnMouseClicked(e ->{
            if(!chatInput.getText().isEmpty() && App.mainScreenController.currentState.equals(MainScreenState.ONLINE)){
                App.mainScreenController.processChatInput();
            }
        });
        chatInput.setOnKeyPressed(e->{
            if (e.getCode() == KeyCode.ENTER  && App.mainScreenController.currentState.equals(MainScreenState.ONLINE)) {
                App.mainScreenController.processChatInput();
            }
        });
    }

    private void localInit(){
        localInfo.setStyle("-fx-text-fill: black");

    }


    //
    private StackPane lastSelected = null;



    private void sandboxInit(){
        for(int i = 0;i<2;i++){
            // white and black pieces
            boolean isWhite = i == 0;
            for(int j = 0;j<5;j++){
                int sandboxX = j%3;
                int sandboxY = j/3 + i*2;
                ImageView piece = new ImageView(myControl.chessBoardGUIHandler.createPiecePath(j,isWhite));
                StackPane pieceBg = new StackPane(piece);
                pieceBg.setUserData(i + "," + j);
                piece.fitWidthProperty().bind(App.mainScreenController.sidePanel.widthProperty().divide(5));
                piece.fitHeightProperty().bind(piece.fitWidthProperty());
                pieceBg.prefWidthProperty().bind(piece.fitWidthProperty());
                pieceBg.prefHeightProperty().bind(piece.fitWidthProperty());
                sandboxPieces.add(pieceBg,sandboxX,sandboxY);
                int pieceIndex = j;
                piece.setOnMouseClicked(e->{
                    ImageView source = (ImageView) e.getSource();
                    String bgUserData = source.getParent().getUserData().toString();
                    if(lastSelected != null && bgUserData.equals(lastSelected.getUserData().toString())){
                        // clicked same piece so just unselect
                        System.out.println("Unselecting piece");
                        clearPrevPiece(false);
                        lastSelected.setStyle("");
                        lastSelected = null;
                    }
                    else{
                        System.out.println("A " + (isWhite ? "white " : "black ") + GeneralChessFunctions.getPieceType(pieceIndex));
                        setPrevPeice(-10,-10,isWhite,false,pieceIndex,false);
                        pieceBg.setStyle("-fx-background-color: rgba(44, 212, 255, 0.25)");
                        if(lastSelected != null){
                            // clear border of any previous selected
                            lastSelected.setStyle("");

                        }
                        lastSelected = pieceBg;
                    }


                });

            }

        }

    }

    public void reset(){
        clearPrevPiece(true);
        bestmovesBox.getChildren().clear();
        chatArea.clear();
        chatInput.clear();
        localInfo.clear();
    }


    public void clearLocalInfoLine(){
        String text = localInfo.getText();
        String[] lines = text.split("\n");
        if (lines.length > 0) {
            lines[lines.length - 1] = "";
            String newText = String.join("\n", lines);
            localInfo.setText(newText);
        }
    }

    public void clearLocalInfo(){
        localInfo.clear();
    }


    public void addToLocalInfo(String pgn){
        localInfo.appendText(pgn + "\n");
    }


    public void appendNewMessageToChat(String message){
        chatArea.appendText(message + "\n");
    }


    public void updateViewerSuggestions(){
        bestmovesBox.getChildren().clear();
        updateNMovesTask();
        myControl.asyncController.nMovesTask.evalRequest();

    }

    public void updateSidePanel(MainScreenState currentState,boolean isNewMoveMade,String extraInfo){
        // method called every time the board changes. Could be a move, or maybe changing to a previous move
        // extrainfo contains possible stuff to add
        switch (currentState){
            case SANDBOX -> {
                // if both kings are on the board we do a regular eval check
                // if any king is off the board this breaks everything so instead we just say one side checkmated
                BitBoardWrapper board = myControl.gameHandler.currentGame.currentPosition.board;
                if(GeneralChessFunctions.getPieceCoords(board.getWhitePieces()[5]).isEmpty()){
                    // no white king but shouldnt trigger game over
                    App.mainScreenController.setEvalBar(-10000000,-1,false,false);
                }
                else if(GeneralChessFunctions.getPieceCoords(board.getBlackPieces()[5]).isEmpty()){
                    // no black king but shouldnt trigger game over
                    App.mainScreenController.setEvalBar(10000000,-1,false,false);
                }
                else{
                    // both kings on the board so we can proceed as usuals
                    updateEvalThread();
                    myControl.asyncController.evalTask.evalRequest();
                }

            }
            case VIEWER -> {
                updateEvalThread();
                myControl.asyncController.evalTask.evalRequest();
                updateViewerSuggestions();
            }
            case LOCAL -> {
                // could be a new move pgn, or not
                if(isNewMoveMade){
                    if(myControl.gameHandler.currentGame.isVsComputer() && !myControl.gameHandler.currentGame.isPlayer1Turn()){
                        updateCompThread();
                        myControl.asyncController.computerTask.evalRequest();
                    }
                    localInfo.appendText(extraInfo + "\n");
                }


            }
            case ONLINE -> {

            }
        }
    }
    // flag for if a piece was selected
    boolean prevPeiceSelected = false;
    // [0] = x ,[1] =  y , [2] = (1 = white, -1 = black), [3] (only used for sandbox mode) = (1 = regular piece selected,-1 = additional piece selected), [4] = pieceselectedIndex;
    int[] selectedPeiceInfo = {0, 0, 0, 0, 0};
    // list that stores all the x,y coords that a piece can go to
    List<XYcoord> prevPieceMoves = null;

    private void setPrevPeice(int x,int y,boolean prevPieceColor,boolean isOnBoard,int selectedPieceIndex,boolean calculatePossibleMoves){
        prevPeiceSelected = true;
        selectedPeiceInfo[0] = x;
        selectedPeiceInfo[1] = y;
        selectedPeiceInfo[2] = prevPieceColor ? 1 : -1;
        selectedPeiceInfo[3] = isOnBoard ? 1 : -1;
        selectedPeiceInfo[4] = selectedPieceIndex;
        if(calculatePossibleMoves){
            setPrevPieceMoves(x,y,prevPieceColor);
        }
    }

    private void setPrevPieceMoves(int x, int y, boolean pieceIsWhite){
        prevPieceMoves = AdvancedChessFunctions.getPossibleMoves(x,y,pieceIsWhite,myControl.gameHandler.currentGame.currentPosition.board, myControl.gameHandler.currentGame.gameStates);
        for(XYcoord c : prevPieceMoves){
            myControl.chessBoardGUIHandler.highlightSquare(c.x,c.y,false);
        }
    }

    private void clearPrevPiece(boolean clearHighlights){
        prevPeiceSelected = false;
        prevPieceMoves = null;
        if(clearHighlights){
            myControl.chessBoardGUIHandler.clearHighlights();
        }


    }


    // logic for when you try to drag a piece
    // has 3 different methods that are called, one for when you actually press down on the board to drag, one where you drag the piece, and one when you release the piece
    // when you click on a piece, just like handlesquareclick(), it calculates whether you are able to click that piece or not
    // if ok then when you drag it follows you to where you eventually release the piece
    // lastly when you release it checks to see where you released, if not a valid move it sets the piece back to its origin

    ImageView selected = null;
    boolean dragging = false;

    int oldX = -1;
    int oldY = -1;

    int oldArrowX;
    int oldArrowY;

    boolean creatingArrow = false;

    int oldDragPieceIndex;

    boolean oldIsWhite = false;

    public void handleBoardPress(MouseEvent e,MainScreenState currentState){
        int[] xy = myControl.chessBoardGUIHandler.turnLayoutXyintoBoardXy(e.getX(),e.getY());
        if(e.getButton() == MouseButton.PRIMARY){
            dragging =false;
            boolean[] boardInfo = GeneralChessFunctions.checkIfContains(xy[0], xy[1], myControl.gameHandler.currentGame.currentPosition.board);
            // check if we did not click on an empty square
            if(boardInfo[0]){
                boolean isWhitePiece = boardInfo[1];
                int boardIndex = GeneralChessFunctions.getBoardWithPiece(xy[0], xy[1], isWhitePiece, myControl.gameHandler.currentGame.currentPosition.board);

                ImageView piece = myControl.chessBoardGUIHandler.piecesAtLocations[xy[0]][xy[1]];
                if(currentState.equals(MainScreenState.SANDBOX)){
                    // in sandbox mode players can move pieces anywhere
                    prepareDragSelected(piece,xy[0],xy[1],isWhitePiece,boardIndex,true);
                }
                else if(checkIfCanMakeAction(currentState)){

                    if (isWhitePiece == myControl.gameHandler.currentGame.isPlayer1Turn()) {
                        // matches the players turn
                        prepareDragSelected(piece,xy[0],xy[1],isWhitePiece,boardIndex,false);
                    }


                }


            }
        }
        else if(e.getButton() == MouseButton.SECONDARY){
            oldArrowX = xy[0];
            oldArrowY = xy[1];
        }

    }

    private void prepareDragSelected(ImageView piece,int oldX,int oldY,boolean isWhitePiece,int selectedPieceIndex,boolean isSandbox) {
        selected = piece;
        myControl.chessBoardGUIHandler.removeLayoutBindings(piece);
        this.oldX = oldX;
        this.oldY = oldY;
        this.oldIsWhite = isWhitePiece;
        this.oldDragPieceIndex = selectedPieceIndex;
        if(!isSandbox){
            setPrevPieceMoves(oldX,oldY,isWhitePiece);
        }
    }

    private void resetDragSelected(ImageView piece){
        // sets the piece back to its home square
        int[] xy = myControl.chessBoardGUIHandler.calcXY(oldX,oldY);
        piece.setLayoutX(xy[0]);
        piece.setLayoutY(xy[1]);
        myControl.chessBoardGUIHandler.putBackLayoutBindings(selected,oldX,oldY);
        myControl.chessBoardGUIHandler.piecesAtLocations[oldX][oldY] = selected;

    }

    private void placePiece(ImageView piece,int x,int y){
        int[] newLayoutxy = myControl.chessBoardGUIHandler.calcXY(x,y);
        piece.setLayoutX(newLayoutxy[0]);
        piece.setLayoutY(newLayoutxy[1]);
        myControl.chessBoardGUIHandler.putBackLayoutBindings(selected,x,y);
        myControl.chessBoardGUIHandler.piecesAtLocations[x][y] = selected;
    }

    public void handleBoardDrag(MouseEvent e){
        if(e.getButton() == MouseButton.PRIMARY) {
            if (selected != null) {
                dragging = true;
                // check if you are dragging the piece out of bounds
                if (e.getX() >= myControl.chessBoardGUIHandler.chessPieceBoard.getWidth() || e.getY() >= myControl.chessBoardGUIHandler.chessPieceBoard.getHeight() || e.getX() < 0 || e.getY() < 0) {
                    // if so then reset piece back to old location
                    resetDragSelected(selected);
                    selected = null;
                } else {
//                System.out.println(e.getX() + "," + e.getY());
                    selected.setLayoutX(e.getX() - selected.getFitWidth() / 2);
                    selected.setLayoutY(e.getY() - selected.getFitHeight() / 2);
                }
            }
        }
        else if(e.getButton() == MouseButton.SECONDARY){
            creatingArrow = true;
        }
    }



    public void handleBoardRelease(MouseEvent e,MainScreenState currentState){
        int[] newXY = myControl.chessBoardGUIHandler.turnLayoutXyintoBoardXy(e.getX(), e.getY());
        if(e.getButton() == MouseButton.PRIMARY) {
            if (selected != null && dragging) {
                myControl.chessBoardGUIHandler.clearArrows();
                myControl.chessBoardGUIHandler.clearHighlights();
                int newX = newXY[0];
                int newY = newXY[1];
                if (currentState.equals(MainScreenState.SANDBOX)) {
                    // for sandbox we dont care about rules, we just move wherever we want
                    myControl.gameHandler.currentGame.makeNewMove(new ChessMove(oldX, oldY, newX, newY, 0, oldDragPieceIndex, oldIsWhite, false, false, false), false,true);
                    placePiece(selected, newX, newY);
                } else {
                    // all we have to do is check to see if where the piece has been released is a valid square
                    // this consists of two things
                    // #1 the square must either be empty or be an enemy square
                    // #2 the square must be a move that the piece can make
                    boolean[] boardInfo = GeneralChessFunctions.checkIfContains(newX, newY, myControl.gameHandler.currentGame.currentPosition.board);
                    boolean isHit = boardInfo[0];
                    boolean isWhitePieceDroppedOn = boardInfo[1];
                    boolean[] moveInfo = checkIfMovePossible(prevPieceMoves, newX, newY);
                    boolean isMovePossible = moveInfo[0];
                    boolean isCastle = moveInfo[1];
                    if ((oldIsWhite != isWhitePieceDroppedOn || !isHit) && isMovePossible) {
                        // enemy piece or empty square, and in possible moves

                        boolean isEating = GeneralChessFunctions.checkIfContains(newX, newY, !oldIsWhite, myControl.gameHandler.currentGame.currentPosition.board);
                        placePiece(selected, newX, newY);
                        int promoSquare = oldIsWhite ? 0 : 7;
                        if (oldDragPieceIndex == ChessConstants.PAWNINDEX && newY == promoSquare) {
                            // promoting piece and eating so it looks better to remove
                            if(isEating){
                                myControl.chessBoardGUIHandler.removeFromChessBoard(newX, newY, oldIsWhite);
                            }
                            myControl.chessBoardGUIHandler.removeFromChessBoard(selected,oldX,oldY);

                        }
                        handleMakingMove(oldX, oldY, newX, newY, isEating, oldIsWhite, isCastle, false, false, ChessConstants.EMPTYINDEX, currentState,true);


                    } else {
                        resetDragSelected(selected);
                    }
                }
            }
            else{
                if(selected != null){
                    resetDragSelected(selected);
                }
            }

            selected = null;
            dragging = false;
        }
        else if(e.getButton() == MouseButton.SECONDARY){
            if(creatingArrow && (oldArrowX != newXY[0] || oldArrowY != newXY[1])){
                myControl.chessBoardGUIHandler.addArrow(new Arrow(oldArrowX,oldArrowY,newXY[0],newXY[1],"orange"));
            }
            creatingArrow = false;
        }
    }




    // logic that happens when a square is clicked
    // stores the last piece selected for a move

    public void handleSquareClick(int clickX,int clickY,boolean isHitPiece,boolean isWhiteHitPiece,MainScreenState currentState){
        if(prevPeiceSelected && selectedPeiceInfo[0] == clickX && selectedPeiceInfo[1] == clickY){
            // clicked the same piece you selected, this means you are unselecting
            clearPrevPiece(true);
        }
        else {
            int boardIndex = GeneralChessFunctions.getBoardWithPiece(clickX, clickY, isWhiteHitPiece, myControl.gameHandler.currentGame.currentPosition.board);
            if (currentState.equals(MainScreenState.SANDBOX)) {
                handleSandboxSquareClick(clickX, clickY, isHitPiece, isWhiteHitPiece);
            } else if (checkIfCanMakeAction(currentState)) {
                if (!prevPeiceSelected && !isHitPiece) {
                    // nothing to do as empty square has been clicked with no previous selection
                } else if (!prevPeiceSelected) {
                    // no prev selection, then we want to make sure you are picking a piece that is on your side
                    if (isWhiteHitPiece == myControl.gameHandler.currentGame.isPlayer1Turn()) {
                        // matches the turn
                        setPrevPeice(clickX, clickY, isWhiteHitPiece, true, boardIndex, true);
                    }
                } else {
                    // two options, you are either clicking you own piece, or attempting to make a move
                    if (isWhiteHitPiece == myControl.gameHandler.currentGame.isPlayer1Turn() && isHitPiece) {
                        // your own piece
                        clearPrevPiece(true);
                        setPrevPeice(clickX, clickY, isWhiteHitPiece, true, boardIndex, true);

                    } else {
                        // can possibly be a move, however it needs to be within prevpiecemoves
                        // [0] = is move possible [1] = is castle move
                        boolean[] moveInfo = checkIfMovePossible(prevPieceMoves, clickX, clickY);
                        if (moveInfo[0]) {
                            // move is within prev moves
                            boolean pieceSelectedIsWhite = selectedPeiceInfo[2] > 0;
                            int pieceSelectedIndex = selectedPeiceInfo[4];
                            int oldX = selectedPeiceInfo[0];
                            int oldY = selectedPeiceInfo[1];
                            boolean isCastleMove = moveInfo[1];
                            boolean isEating = GeneralChessFunctions.checkIfContains(clickX, clickY, !pieceSelectedIsWhite, myControl.gameHandler.currentGame.currentPosition.board);
                            handleMakingMove(oldX, oldY, clickX, clickY, isEating, pieceSelectedIsWhite, isCastleMove, false, false, ChessConstants.EMPTYINDEX, currentState,false);
                        } else {
                            // cannot make move
                            clearPrevPiece(true);
                        }

                    }
                }
            }
        }

    }

    private void handleSandboxSquareClick(int clickX,int clickY,boolean isHitPiece,boolean isWhiteHitPiece){
        if(prevPeiceSelected && selectedPeiceInfo[0] == clickX && selectedPeiceInfo[1] == clickY){
            // clicked the same piece you selected, this means you are unselecting
            clearPrevPiece(true);
        }
        else {
            // two different cases, either you move a piece, or you select another one (or you just clicked an empty square)
            boolean prevPeiceIsWhite = (selectedPeiceInfo[2] > 0);
            if (!isHitPiece && !prevPeiceSelected) {
                // clicked an empty square, but also no piece selected so nothing for now
            } else if (!prevPeiceSelected || isHitPiece && (prevPeiceIsWhite == isWhiteHitPiece)) {
                // two options
                // 1. clicked a new piece without no prev selection
                // 2. clicked your own piece color, so select that color(clearing previous selection)
                if (prevPeiceSelected) {
                    clearPrevPiece(true);
                }
                int boardIndex = GeneralChessFunctions.getBoardWithPiece(clickX, clickY, isWhiteHitPiece, myControl.gameHandler.currentGame.currentPosition.board);
                setPrevPeice(clickX,clickY,isWhiteHitPiece,true,boardIndex,false);
                myControl.chessBoardGUIHandler.highlightSquare(clickX, clickY, false);
            } else {
                // enemy piece or empty square, and at this point a piece has been selected
                // moving/adding a piece to the board
                boolean isBoardPieceSelected = (selectedPeiceInfo[3] > 0);

                int pieceSelectedIndex = selectedPeiceInfo[4];

                if (isBoardPieceSelected) {
                    // moving a piece
                    int oldX = selectedPeiceInfo[0];
                    int oldY = selectedPeiceInfo[1];
                    clearPrevPiece(true);
                    boolean isEating = GeneralChessFunctions.checkIfContains(clickX,clickY,prevPeiceIsWhite,myControl.gameHandler.currentGame.currentPosition.board);
                    myControl.gameHandler.currentGame.makeNewMove(new ChessMove(oldX, oldY, clickX, clickY, ChessConstants.EMPTYINDEX, pieceSelectedIndex, prevPeiceIsWhite, false, isEating, false), false,false);

                } else {
                    // adding a piece custom
                    myControl.gameHandler.currentGame.makeCustomMoveSandbox(new ChessPosition(myControl.gameHandler.currentGame.currentPosition, myControl.gameHandler.currentGame.gameStates, new ChessMove(0, 0, clickX, clickY, 0, pieceSelectedIndex, prevPeiceIsWhite, false, false, true)), prevPeiceIsWhite);

                }
                updateSidePanel(App.mainScreenController.currentState, true,"");




            }
        }
    }

    public void handleMakingMove(int startX, int startY, int endX, int endY, boolean isEating, boolean isWhitePiece, boolean isCastle, boolean isComputerMove,boolean isPawnPromoFinalized,int promoIndx,MainScreenState currentState,boolean isDragMove) {
        myControl.chessBoardGUIHandler.clearHighlights();
        myControl.chessBoardGUIHandler.clearArrows();
        int boardIndex = GeneralChessFunctions.getBoardWithPiece(startX,startY,isWhitePiece,myControl.gameHandler.currentGame.currentPosition.board);
        int endSquare = isWhitePiece ? 0 : 7;
        boolean isPawnPromo = boardIndex == ChessConstants.PAWNINDEX && endY == endSquare;
        boolean pawnPromoToggled = false;
        ChessMove moveMade = null;
        if(promoIndx == ChessConstants.EMPTYINDEX && !isPawnPromo){
            // not promo
            moveMade = new ChessMove(startX,startY,endX,endY,ChessConstants.EMPTYINDEX,boardIndex,isWhitePiece,isCastle,isEating,false);
            myControl.gameHandler.currentGame.makeNewMove(moveMade,isComputerMove,isDragMove);

        }
        else if(isComputerMove || isPawnPromoFinalized){
            // computer promoting or player chose their piece to promote
            moveMade = new ChessMove(startX, startY, endX, endY,promoIndx,boardIndex,isWhitePiece,false,isEating,false);
            myControl.gameHandler.currentGame.makeNewMove(moveMade,false,isDragMove);

        }
        else{
            pawnPromoToggled = true;
            sPawnX = startX;
            sPawnY = startY;
            ePawnX = endX;
            ePawnY = endY;
            pieceIndxPromo = boardIndex;
            isWhitePromo = isWhitePiece;
            isEatingPromo = isEating;
            App.mainScreenController.togglePromo();

        }
        if(!pawnPromoToggled){
            // means you made a move
            // else you are waiting for the player to chose their promotion
            clearPrevPiece(false);
            App.mainScreenController.updateSimpleAdvantageLabels();
            updateSidePanel(currentState,true,PgnFunctions.moveToPgn(moveMade,myControl.gameHandler.currentGame.currentPosition.board,myControl.gameHandler.currentGame.gameStates));

        }

    }

    int ePawnX;
    int ePawnY;

    int sPawnX;
    int sPawnY;

    int pieceIndxPromo;

    boolean isWhitePromo;
    boolean isEatingPromo;

    public void promoPawn(int promoIndex,MainScreenState currentState){
        handleMakingMove(sPawnX,sPawnY,ePawnX,ePawnY,isEatingPromo,isWhitePromo,false,false,true,promoIndex,currentState,false);
    }



    // checks that it is your turn in a game
    private boolean checkIfCanMakeAction(MainScreenState currentState){
        switch (currentState){
            case ONLINE -> {
                return myControl.gameHandler.currentGame.isPlayer1Turn();
            }
            case LOCAL -> {
                // either in 1v1 its the players turn or its whites turn
                if(myControl.gameHandler.currentGame.isVsComputer()){
                    return myControl.gameHandler.currentGame.isPlayer1Turn();
                }
                else{
                    return true;
                }
            }
            case VIEWER -> {
                // will be in 1v1 mode
                return true;
            }
            default -> {
                ChessConstants.mainLogger.error("checkIfCanMakeMove default case called");
                return false;
            }
        }
    }

    private boolean[] checkIfMovePossible(List<XYcoord> moves, int x, int y){
        // todo: change this to bitboard logic
        if(moves != null) {
            for (XYcoord s : moves) {
                if ((s.x == x && s.y == y) && s.isCastleMove()) {
                    return new boolean[]{true, true};
                } else if (s.x == x && s.y == y) {


                    return new boolean[]{true, false};
                }
            }
        }
        return new boolean[]{false,false};


    }

    private ChessPosition lastPosSinceRequest;
    private ChessStates lastStateSinceRequest;
    public void addBestMovesToViewer(List<ComputerOutput> bestMoves){
        for(int i = 0;i<bestMoves.size();i++){
            HBox moveGui = new HBox();
            moveGui.setAlignment(Pos.CENTER);
            moveGui.setSpacing(5);
            Label moveNumber = new Label( "#"+ (i+1));
            Label moveAsPgn = new Label(PgnFunctions.moveToPgn(bestMoves.get(i).move,lastPosSinceRequest.board,lastStateSinceRequest));
            double adv = bestMoves.get(i).advantage;
            String prefix = adv == 0 ? "" : adv > 0 ? "+" : "-";
            Label expectedAdvantage = new Label(prefix + Math.abs(adv));
            App.bindingController.bindSmallText(moveNumber,true);
            App.bindingController.bindSmallText(moveAsPgn,true);
            App.bindingController.bindSmallText(expectedAdvantage,true);
            moveGui.prefWidthProperty().bind(bestmovesBox.widthProperty());
            moveGui.getChildren().addAll(moveNumber,moveAsPgn,expectedAdvantage);
            int finalI = i;
            moveGui.setOnMouseClicked(e->{
                myControl.gameHandler.currentGame.makeNewMove(bestMoves.get(finalI).move,false,false);
                updateSidePanel(App.mainScreenController.currentState, true,moveAsPgn.getText());
            });
            bestmovesBox.getChildren().add(moveGui);


        }
    }



    private void updateEvalThread(){
        myControl.asyncController.evalTask.currentPosition = myControl.gameHandler.currentGame.currentPosition;
        myControl.asyncController.evalTask.currentGameState = myControl.gameHandler.currentGame.gameStates;
        myControl.asyncController.evalTask.currentIsWhite = myControl.gameHandler.currentGame.isPlayer1Turn();
    }
    private void updateCompThread(){
        myControl.asyncController.computerTask.currentPosition = myControl.gameHandler.currentGame.currentPosition;
        myControl.asyncController.computerTask.currentGameState = myControl.gameHandler.currentGame.gameStates;
        myControl.asyncController.computerTask.currentIsWhite = myControl.gameHandler.currentGame.isPlayer1Turn();
    }
    private void updateNMovesTask(){
        myControl.asyncController.nMovesTask.currentPosition = myControl.gameHandler.currentGame.currentPosition;
        myControl.asyncController.nMovesTask.currentGameState = myControl.gameHandler.currentGame.gameStates;
        myControl.asyncController.nMovesTask.currentIsWhite = myControl.gameHandler.currentGame.isPlayer1Turn();
        this.lastPosSinceRequest = myControl.gameHandler.currentGame.currentPosition;
        this.lastStateSinceRequest = myControl.gameHandler.currentGame.gameStates;
    }




}
