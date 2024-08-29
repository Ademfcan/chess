package chessengine;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;


public class ChessActionHandler {
    private final HBox movesPlayedBox;
    private final ChessCentralControl myControl;
    // viewer controls
    private final VBox bestmovesBox;
    // local controls
    private final TextArea campaignInfo;
    // sandbox controls
    private final GridPane sandboxPieces;
    // online controls
    private final TextArea gameInfo;
    private final TextField chatInput;
    private final Button sendMessageButton;

    private final VBox p1Indicator;
    private final VBox p2Indicator;

    private final Label p1moveClk;
    private final Label p2moveClk;

    private final Button playPauseButton;
    // [0] = x ,[1] =  y , [2] = (1 = white, -1 = black), [3] (only used for sandbox mode) = (1 = regular piece selected,-1 = additional piece selected), [4] = pieceselectedIndex;
    private final int[] selectedPeiceInfo = {0, 0, 0, 0, 0};
    private final NumberFormat formatter = new DecimalFormat("#0.00");
    private StackPane lastSelected = null;
    private int numLabels = 0;


    //
    private Label lastHighlight = null;
    // flag for if a piece was selected
    private boolean prevPeiceSelected = false;
    private List<XYcoord> prevPieceMoves = null;
    // lastly when you release it checks to see where you released, if not a valid move it sets the piece back to its origin
    private ImageView selected = null;
    private boolean dragging = false;
    private int oldX = -1;
    private int oldY = -1;
    private int oldArrowX;
    private int oldArrowY;
    private boolean creatingArrow = false;
    private int oldDragPieceIndex;
    private boolean oldIsWhite = false;
    private int overX = -1;
    private int overY = -1;
    private int ePawnX;
    private int ePawnY;
    private int sPawnX;
    private int sPawnY;
    // list that stores all the x,y coords that a piece can go to
    private int pieceIndxPromo;
    private boolean isWhitePromo;
    private boolean isEatingPromo;
    private int numRedos = 0;

    // logic for when you try to drag a piece
    // has 3 different methods that are called, one for when you actually press down on the board to drag, one where you drag the piece, and one when you release the piece
    // when you click on a piece, just like handlesquareclick(), it calculates whether you are able to click that piece or not
    // if ok then when you drag it follows you to where you eventually release the piece
    private ChessPosition lastPosSinceRequest;
    private ChessStates lastStateSinceRequest;
    public ChessActionHandler(ChessCentralControl myControl, VBox bestmovesBox, TextArea localInfo, GridPane sandboxPieces, TextArea gameInfo, TextField chatInput, Button sendMessageButton, HBox movesPlayedBox, Button playPauseButton, VBox p1Indicator, VBox p2Indicator, Label p1moveClk, Label p2moveClk) {
        this.myControl = myControl;
        this.bestmovesBox = bestmovesBox;
        this.campaignInfo = localInfo;
        this.sandboxPieces = sandboxPieces;
        this.gameInfo = gameInfo;
        this.chatInput = chatInput;
        this.sendMessageButton = sendMessageButton;
        this.movesPlayedBox = movesPlayedBox;
        this.playPauseButton = playPauseButton;
        this.p1Indicator = p1Indicator;
        this.p2Indicator = p2Indicator;
        this.p1moveClk = p1moveClk;
        this.p2moveClk = p2moveClk;
        onlineInit();
        localInit();
        sandboxInit();
        simulationInit();
    }

    private void onlineInit() {
        // event handlers for when sending a message
        sendMessageButton.setOnMouseClicked(e -> {
            if (!chatInput.getText().isEmpty() && myControl.mainScreenController.currentState.equals(MainScreenState.ONLINE)) {
                myControl.mainScreenController.processChatInput();
            }
        });
        chatInput.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER && myControl.mainScreenController.currentState.equals(MainScreenState.ONLINE)) {
                myControl.mainScreenController.processChatInput();
            }
        });
    }

    private void localInit() {
        // nothing for now
    }

    private void simulationInit() {
        playPauseButton.setOnMouseClicked(e -> {
            myControl.asyncController.toggleSimPlay();
        });
    }

    private void sandboxInit() {
        for (int i = 0; i < 2; i++) {
            // white and black pieces
            boolean isWhite = i == 0;
            for (int j = 0; j < 5; j++) {
                int sandboxX = j % 3;
                int sandboxY = j / 3 + i * 2;
                ImageView piece = new ImageView(myControl.chessBoardGUIHandler.createPiecePath(j, isWhite));
                StackPane pieceBg = new StackPane(piece);
                pieceBg.setUserData(i + "," + j);
                piece.fitWidthProperty().bind(myControl.mainScreenController.sidePanel.widthProperty().divide(5));
                piece.fitHeightProperty().bind(piece.fitWidthProperty());
                pieceBg.prefWidthProperty().bind(piece.fitWidthProperty());
                pieceBg.prefHeightProperty().bind(piece.fitWidthProperty());
                sandboxPieces.add(pieceBg, sandboxX, sandboxY);
                int pieceIndex = j;
                piece.setOnMouseClicked(e -> {
                    ImageView source = (ImageView) e.getSource();
                    String bgUserData = source.getParent().getUserData().toString();
                    if (lastSelected != null && bgUserData.equals(lastSelected.getUserData().toString())) {
                        // clicked same piece so just unselect
                        System.out.println("Unselecting piece");
                        clearPrevPiece(false);
                        lastSelected.setStyle("");
                        lastSelected = null;
                    } else {
                        System.out.println("A " + (isWhite ? "white " : "black ") + GeneralChessFunctions.getPieceType(pieceIndex));
                        setPrevPeice(-10, -10, isWhite, false, pieceIndex, false);
                        pieceBg.setStyle("-fx-background-color: rgba(44, 212, 255, 0.25)");
                        if (lastSelected != null) {
                            // clear border of any previous selected
                            lastSelected.setStyle("");

                        }
                        lastSelected = pieceBg;
                    }


                });

            }

        }

    }

    public void reset() {
        clearPrevPiece(true);
        bestmovesBox.getChildren().clear();
        movesPlayedBox.getChildren().clear();

        gameInfo.clear();
        chatInput.clear();
        campaignInfo.clear();
        numRedos = 0;
        numLabels = 0;

    }

    public void clearMovePlayedLine() {

        if (numLabels % 2 == 1) {
            // opposite as last time, we need to remove a number separator if odd. See below addtolocalinfo method
            movesPlayedBox.getChildren().remove(movesPlayedBox.getChildren().size() - 1);

        }

        movesPlayedBox.getChildren().remove(movesPlayedBox.getChildren().size() - 1);
        numLabels--;
    }

    public void clearMovesPlayed() {
        movesPlayedBox.getChildren().clear();
    }

    public void addToMovesPlayed(String pgn) {
        Label pgnDescriptor = new Label(pgn);

        if (lastHighlight != null) {
            lastHighlight.setBackground(ChessConstants.defaultBg);
        }
        lastHighlight = pgnDescriptor;
        pgnDescriptor.setBackground(ChessConstants.highlightBg);

        pgnDescriptor.setPadding(new Insets(2, 10, 2, 10));
        App.bindingController.bindSmallText(pgnDescriptor, true, "Black");
        pgnDescriptor.setUserData(numLabels);
        pgnDescriptor.minWidthProperty().bind(myControl.mainScreenController.fullScreen.widthProperty().divide(50).add(15));
        pgnDescriptor.setOnMouseClicked(e -> {
            int absIndexToGo = (int) pgnDescriptor.getUserData();
            myControl.mainScreenController.changeToAbsoluteMoveIndex(absIndexToGo);

        });


        // also add a move number if the pgn needs it. This is found by seeing if the child count is even before adding
        // the effect is: (Without): e4 e5 d6 bb5 (With) 1. e4 e5 2. d6 bb5
        if (numLabels % 2 == 0) {
            // ready for a number
            int moveNum = (numLabels / 2) + 1; // so not zero indexed
            Label numSeparator = new Label(moveNum + ".");
            numSeparator.minWidthProperty().bind(myControl.mainScreenController.fullScreen.widthProperty().divide(80).add(7));
            App.bindingController.bindSmallText(numSeparator, true, "White");
            movesPlayedBox.getChildren().add(numSeparator);

        }

        movesPlayedBox.getChildren().add(pgnDescriptor);
        numLabels++;
    }

    public void clearMovesPlayedUpToIndex(int curMoveIndex) {
        if (curMoveIndex == -1) {
            numLabels = 0;
            clearMovesPlayed();
        } else {
            numLabels = curMoveIndex;
            int to = movesPlayedBox.getChildren().size();
            if (to > curMoveIndex + 1) {
                ChessConstants.mainLogger.debug(String.format("Clearing moves played from %d", curMoveIndex + 1));

                int prevNumSpacers = curMoveIndex / 2;

                movesPlayedBox.getChildren().subList(curMoveIndex + 1 + prevNumSpacers, to).clear();
            }
        }

    }

    public void highlightMovesPlayedLine(int highlightIndex) {
        System.out.println("Highlighting");
        int prevNumSpacers = highlightIndex / 2;
        int tot = highlightIndex + prevNumSpacers + 1;

        if (tot < movesPlayedBox.getChildren().size()) {
            if (lastHighlight != null) {
                lastHighlight.setBackground(ChessConstants.defaultBg);
            }
            lastHighlight = (Label) movesPlayedBox.getChildren().get(tot);
            lastHighlight.setBackground(ChessConstants.highlightBg);
        } else {
            ChessConstants.mainLogger.debug("Highlight index total past the size!");
        }
    }

    public void clearMovesPlayedHighlight() {
        if (lastHighlight != null) {
            lastHighlight.setBackground(ChessConstants.defaultBg);
        }
    }

    public void appendNewMessageToChat(String message) {
        gameInfo.appendText(message + "\n");
    }

    public void updateViewerSuggestions() {
        bestmovesBox.getChildren().clear();
        updateNMovesTask();
        myControl.asyncController.nMovesTask.evalRequest();

    }

    private void addCampaignMessage(String message) {
        campaignInfo.appendText(message + "\n");
        App.soundPlayer.playEffect(Effect.MESSAGE);
    }


    // logic that happens when a square is clicked

    // stores the last piece selected for a move

    private void updateTurnIndicators(boolean isWhiteTurn, boolean isWhiteOriented, boolean updateTimeLabels) {
        // todo figure out timers and diplay and centralized pulse etc
        VBox whiteIndicator = isWhiteOriented ? p1Indicator : p2Indicator;
        VBox blackIndicator = isWhiteOriented ? p2Indicator : p1Indicator;
        Label whiteLabel = isWhiteOriented ? p1moveClk : p2moveClk;
        Label blackLabel = isWhiteOriented ? p2moveClk : p1moveClk;
        // white on bottom(so p1)
        if (isWhiteTurn) {
            whiteIndicator.setBackground(ChessConstants.whiteTurnActive);
            blackIndicator.setBackground(ChessConstants.labelUnactive);
            if (updateTimeLabels) {
                whiteLabel.setText("White Turn");
                whiteLabel.styleProperty().unbind();
                App.bindingController.bindSmallText(whiteLabel, true, "Black");
                blackLabel.setText("");
            }


        } else {
            whiteIndicator.setBackground(ChessConstants.labelUnactive);
            blackIndicator.setBackground(ChessConstants.blackTurnActive);
            if (updateTimeLabels) {
                whiteLabel.setText("");
                blackLabel.styleProperty().unbind();
                App.bindingController.bindSmallText(blackLabel, true, "White");
                blackLabel.setText("Black Turn");
            }
        }


    }

    public void makeBackendUpdate(MainScreenState currentState, boolean isNewMoveMade, boolean isInit) {
        // method called every time the board changes. Could be a move, or maybe changing to a previous move
        // extrainfo contains possible stuff to add
        myControl.chessBoardGUIHandler.clearArrows();
        myControl.chessBoardGUIHandler.clearUserCreatedHighlights();
        highlightMovesPlayedLine(myControl.gameHandler.currentGame.curMoveIndex);
        if (!currentState.equals(MainScreenState.SANDBOX))
            updateTurnIndicators(myControl.gameHandler.currentGame.isWhiteTurn(), myControl.gameHandler.currentGame.isWhiteOriented(), !myControl.gameHandler.currentGame.isWebGame()); // web game will send time updates

        if (isNewMoveMade && !currentState.equals(MainScreenState.SANDBOX)) {
            String pgn = PgnFunctions.moveToPgn(myControl.gameHandler.currentGame.currentPosition.getMoveThatCreatedThis(), myControl.gameHandler.currentGame.currentPosition, myControl.gameHandler.currentGame.gameState);
            addToMovesPlayed(pgn);

        }
        switch (currentState) {
            case SIMULATION -> {
                if (isInit) {
//                    System.out.println("Start sim");
                    myControl.asyncController.startSimPlay(); // start sim task
                } else {
                    updateEvalThread();
                    myControl.asyncController.evalTask.evalRequest();
                }
            }
            case SANDBOX -> {
                // if both kings are on the board we do a regular eval check
                // if any king is off the board this breaks everything so instead we just say one side checkmated
                BitBoardWrapper board = myControl.gameHandler.currentGame.currentPosition.board;
                if (GeneralChessFunctions.getPieceCoords(board.getWhitePieces()[5]).isEmpty()) {
                    // no white king but shouldnt trigger game over
                    myControl.mainScreenController.setEvalBar(-10000000, -1, false, false);
                } else if (GeneralChessFunctions.getPieceCoords(board.getBlackPieces()[5]).isEmpty()) {
                    // no black king but shouldnt trigger game over
                    myControl.mainScreenController.setEvalBar(10000000, -1, false, false);
                } else {
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
                if (isNewMoveMade || isInit) {
                    if (!myControl.gameHandler.currentGame.gameState.isGameOver() && myControl.gameHandler.currentGame.isVsComputer() && !myControl.gameHandler.currentGame.isWhiteTurn() == myControl.gameHandler.currentGame.isWhiteOriented()) {
//                        GeneralChessFunctions.printBoardDetailed(myControl.gameHandler.currentGame.currentPosition.board);
                        updateCompThread();
                        myControl.asyncController.computerTask.evalRequest(); //todo
                    }
                }


            }
            case ONLINE -> {

            }
            case CAMPAIGN -> {

                if (isNewMoveMade) {
                    if (!myControl.gameHandler.currentGame.gameState.isGameOver() && !myControl.gameHandler.currentGame.isWhiteTurn()) {

                        updateCompThread();
                        myControl.asyncController.computerTask.evalRequest();
                    }

                    // when playing campaign send messages to the player

                    ChessMove move = myControl.gameHandler.currentGame.currentPosition.getMoveThatCreatedThis();
                    if (myControl.gameHandler.currentGame.gameState.isCheckMated()[0]) {
                        String message = App.campaignMessager.getCheckmateMessage(myControl.gameHandler.currentGame.gameState.isCheckMated()[1]);
                        addCampaignMessage(message);

                    } else if (myControl.gameHandler.currentGame.gameState.isStaleMated()) {
                        String message = App.campaignMessager.getStalemateMessage();
                        addCampaignMessage(message);
                    } else if (AdvancedChessFunctions.isAnyChecked(myControl.gameHandler.currentGame.currentPosition.board)) {
                        String message = App.campaignMessager.getCheckMessage(move.isWhite());
                        addCampaignMessage(message);
                    } else {
                        if (!move.equals(ChessConstants.startMove)) {
                            if (move.isEating()) {
                                int curIndex = myControl.gameHandler.currentGame.curMoveIndex;
                                int eatenAddIndex = GeneralChessFunctions.getBoardWithPiece(move.getNewX(), move.getNewY(), !move.isWhite(), myControl.gameHandler.currentGame.getPos(curIndex - 1).board);
                                String message = App.campaignMessager.getEatingMessage(eatenAddIndex, move.isWhite());
                                addCampaignMessage(message);
                            } else if (move.isPawnPromo()) {
                                String message = App.campaignMessager.getPromoMessage(move.getPromoIndx(), move.isWhite());
                                addCampaignMessage(message);
                            } else if (move.isCastleMove()) {
                                String message = App.campaignMessager.getCastleMessage(move.isWhite());
                                addCampaignMessage(message);
                            } else {
                                int rand = ChessConstants.generalRandom.nextInt(0, 100);
                                if (rand > 70) {
                                    // 30% of the time send some message
                                    String message = App.campaignMessager.getMoveMessage(AdvancedChessFunctions.getSimpleAdvantage(myControl.gameHandler.currentGame.currentPosition.board), move.isWhite());
                                    addCampaignMessage(message);
                                }
                            }
                        }

                    }


                } else if (isInit) {
                    String message = App.campaignMessager.getIntroductionMessage();
                    addCampaignMessage(message);
                }
            }
        }
    }

    private void setPrevPeice(int x, int y, boolean prevPieceColor, boolean isOnBoard, int selectedPieceIndex, boolean calculatePossibleMoves) {
        prevPeiceSelected = true;
        selectedPeiceInfo[0] = x;
        selectedPeiceInfo[1] = y;
        selectedPeiceInfo[2] = prevPieceColor ? 1 : -1;
        selectedPeiceInfo[3] = isOnBoard ? 1 : -1;
        selectedPeiceInfo[4] = selectedPieceIndex;
        if (calculatePossibleMoves) {
            setPrevPieceMoves(x, y, prevPieceColor);
        }
    }

    private void setPrevPieceMoves(int x, int y, boolean pieceIsWhite) {
        GeneralChessFunctions.printBoardDetailed(myControl.gameHandler.currentGame.currentPosition.board);
        boolean isWhiteOriented = myControl.gameHandler.currentGame.isWhiteOriented();
        int backendY = isWhiteOriented ? y : 7 - y;
        int backendX = isWhiteOriented ? x : 7 - x;
        System.out.println(backendY);
        prevPieceMoves = AdvancedChessFunctions.getPossibleMoves(backendX, backendY, pieceIsWhite, myControl.gameHandler.currentGame.currentPosition, myControl.gameHandler.currentGame.gameState);
        if (!isWhiteOriented) {
            // invert y.s
            prevPieceMoves.forEach(c -> {
                c.y = 7 - c.y;
                c.x = 7 - c.x;
            });
        }
        for (XYcoord c : prevPieceMoves) {
            myControl.chessBoardGUIHandler.showMoveSquare(c.x, c.y);
        }
    }

    private void clearPrevPiece(boolean clearHighlights) {
        prevPeiceSelected = false;
        if (clearHighlights) {
            if (prevPieceMoves != null) {
                for (XYcoord c : prevPieceMoves) {
                    myControl.chessBoardGUIHandler.removeMoveSquare(c.x, c.y);

                }
            }
        }
        prevPieceMoves = null;


    }

    public void handleBoardPress(MouseEvent e, MainScreenState currentState) {
        int[] xy = myControl.chessBoardGUIHandler.turnLayoutXyintoBoardXy(e.getX(), e.getY());
        if (GeneralChessFunctions.isValidCoord(xy[0], xy[1])) {
            if (e.getButton() == MouseButton.PRIMARY) {
                dragging = false;
                int backendY = myControl.gameHandler.currentGame.isWhiteOriented() ? xy[1] : 7 - xy[1];
                int backendX = myControl.gameHandler.currentGame.isWhiteOriented() ? xy[0] : 7 - xy[0];
                boolean[] boardInfo = GeneralChessFunctions.checkIfContains(backendX, backendY, myControl.gameHandler.currentGame.currentPosition.board, "boardpress");
                // check if we did not click on an empty square
                if (boardInfo[0]) {
                    boolean isWhitePiece = boardInfo[1];
                    int boardIndex = GeneralChessFunctions.getBoardWithPiece(backendX, backendY, isWhitePiece, myControl.gameHandler.currentGame.currentPosition.board);

                    ImageView piece = myControl.chessBoardGUIHandler.piecesAtLocations[xy[0]][xy[1]];
                    if (currentState.equals(MainScreenState.SANDBOX)) {
                        // in sandbox mode players can move pieces anywhere
                        prepareDragSelected(piece, xy[0], xy[1], isWhitePiece, boardIndex, true);
                    } else if (checkIfCanMakeAction(currentState)) {

                        if (isWhitePiece == myControl.gameHandler.currentGame.isWhiteTurn()) {
                            // matches the players turn
                            prepareDragSelected(piece, xy[0], xy[1], isWhitePiece, boardIndex, false);
                        }


                    }


                }
            } else if (e.getButton() == MouseButton.SECONDARY) {
                oldArrowX = xy[0];
                oldArrowY = xy[1];
            }
        }


    }

    private void prepareDragSelected(ImageView piece, int oldX, int oldY, boolean isWhitePiece, int selectedPieceIndex, boolean isSandbox) {
        selected = piece;
        myControl.chessBoardGUIHandler.removeLayoutBindings(piece);
        this.oldX = oldX;
        this.oldY = oldY;
        this.oldIsWhite = isWhitePiece;
        this.oldDragPieceIndex = selectedPieceIndex;
        // highlight the border of the square you press
        myControl.chessBoardGUIHandler.higlightBorder(oldX, oldY);
        overX = oldX;
        overY = oldY;
        if (!isSandbox) {
            if (prevPeiceSelected) {
                clearPrevPiece(true);
            }
            setPrevPieceMoves(oldX, oldY, isWhitePiece);
        }
    }

    private void resetDragSelected(ImageView piece) {
        // sets the piece back to its home square
        overX = -1;
        overY = -1;
        int[] xy = myControl.chessBoardGUIHandler.calcXY(oldX, oldY);
        myControl.chessBoardGUIHandler.removeHiglightBorder(oldX, oldY);
        piece.setLayoutX(xy[0]);
        piece.setLayoutY(xy[1]);
        myControl.chessBoardGUIHandler.putBackLayoutBindings(selected, oldX, oldY);
        myControl.chessBoardGUIHandler.piecesAtLocations[oldX][oldY] = selected;
        clearPrevPiece(true);

    }

    private void placePiece(ImageView piece, int x, int y) {
        overX = -1;
        overY = -1;
        int[] newLayoutxy = myControl.chessBoardGUIHandler.calcXY(x, y);
        piece.setLayoutX(newLayoutxy[0]);
        piece.setLayoutY(newLayoutxy[1]);
        myControl.chessBoardGUIHandler.putBackLayoutBindings(selected, x, y);
        myControl.chessBoardGUIHandler.piecesAtLocations[x][y] = selected;
    }

    public void handleBoardDrag(MouseEvent e) {
        if (e.getButton() == MouseButton.PRIMARY) {
            if (selected != null) {
                dragging = true;
                // check if you are dragging the piece out of bounds
                if (e.getX() >= myControl.chessBoardGUIHandler.chessPieceBoard.getWidth() || e.getY() >= myControl.chessBoardGUIHandler.chessPieceBoard.getHeight() || e.getX() < 0 || e.getY() < 0) {
                    // if so then reset piece back to old location
                    resetDragSelected(selected);
                    App.soundPlayer.playEffect(Effect.ILLEGALMOVE);
                    selected = null;
                } else {
//                System.out.println(e.getX() + "," + e.getY());
                    int[] overXYNew = myControl.chessBoardGUIHandler.turnLayoutXyintoBoardXy(e.getX(), e.getY());
                    if (overX == -1) {
                        // new thing
                        myControl.chessBoardGUIHandler.higlightBorder(overXYNew[0], overXYNew[1]);
                    } else if (overY != overXYNew[1] || overX != overXYNew[0]) {
                        // new square so remove old highlight aswell
                        myControl.chessBoardGUIHandler.removeHiglightBorder(overX, overY);
                        myControl.chessBoardGUIHandler.higlightBorder(overXYNew[0], overXYNew[1]);

                    }
                    // else do nothing


                    overX = overXYNew[0];
                    overY = overXYNew[1];

                    selected.setLayoutX(e.getX() - selected.getFitWidth() / 2);
                    selected.setLayoutY(e.getY() - selected.getFitHeight() / 2);
                }
            }
        } else if (e.getButton() == MouseButton.SECONDARY) {
            creatingArrow = true;
        }
    }

    public void handleBoardRelease(MouseEvent e, MainScreenState currentState) {
        int[] newXY = myControl.chessBoardGUIHandler.turnLayoutXyintoBoardXy(e.getX(), e.getY());
        if (e.getButton() == MouseButton.PRIMARY) {
            if (selected != null && dragging) {
                myControl.chessBoardGUIHandler.clearArrows();
                myControl.chessBoardGUIHandler.clearAllHighlights();
                int newX = newXY[0];
                int newY = newXY[1];
                int backendY = myControl.gameHandler.currentGame.isWhiteOriented() ? newY : 7 - newY;
                int backendX = myControl.gameHandler.currentGame.isWhiteOriented() ? newX : 7 - newX;
                int oldbackendY = myControl.gameHandler.currentGame.isWhiteOriented() ? oldY : 7 - oldY;
                int oldbackendX = myControl.gameHandler.currentGame.isWhiteOriented() ? oldX : 7 - oldX;
                if (currentState.equals(MainScreenState.SANDBOX)) {
                    // for sandbox we dont care about rules, we just move wherever we want
                    myControl.gameHandler.currentGame.makeNewMove(new ChessMove(oldbackendX, oldbackendY, backendX, backendY, 0, oldDragPieceIndex, oldIsWhite, false, false, ChessConstants.EMPTYINDEX, false, false), false, true);
                    placePiece(selected, newX, newY);
                } else {
                    // all we have to do is check to see if where the piece has been released is a valid square
                    // this consists of two things
                    // #1 the square must either be empty or be an enemy square
                    // #2 the square must be a move that the piece can make
                    boolean[] boardInfo = GeneralChessFunctions.checkIfContains(backendX, backendY, myControl.gameHandler.currentGame.currentPosition.board, "boardRelease");
                    boolean isHit = boardInfo[0];
                    boolean isWhitePieceDroppedOn = boardInfo[1];
                    int[] moveInfo = checkIfMovePossible(prevPieceMoves, newX, newY);
                    boolean isMovePossible = moveInfo[0] == 1;
                    boolean isCastle = moveInfo[1] > 0;
                    boolean isEnPassant = moveInfo[1] < 0;
                    if ((oldIsWhite != isWhitePieceDroppedOn || !isHit) && isMovePossible) {
                        // enemy piece or empty square, and in possible moves

                        boolean isEating = GeneralChessFunctions.checkIfContains(backendX, backendY, !oldIsWhite, myControl.gameHandler.currentGame.currentPosition.board);
                        placePiece(selected, newX, newY);
                        int promoSquare = oldIsWhite ? 0 : 7;
                        if (oldDragPieceIndex == ChessConstants.PAWNINDEX && newY == promoSquare) {
                            // promoting piece and eating so it looks better to remove
                            if (isEating) {
                                myControl.chessBoardGUIHandler.removeFromChessBoard(newX, newY, oldIsWhite, true);
                            }
                            myControl.chessBoardGUIHandler.removeFromChessBoard(selected, oldX, oldY, true);

                        }
                        handleMakingMove(oldbackendX, oldbackendY, backendX, backendY, isEating, oldIsWhite, isCastle, isEnPassant, false, false, ChessConstants.EMPTYINDEX, currentState, true);


                    } else {
                        if (oldX != newX || oldY != newY) {
                            // you moved off your square so take it as an illegal move
                            App.soundPlayer.playEffect(Effect.ILLEGALMOVE);
                        }
                        resetDragSelected(selected);

                    }
                }
            } else {
                if (selected != null) {
                    resetDragSelected(selected);
                }
            }

            selected = null;
            dragging = false;
        } else if (e.getButton() == MouseButton.SECONDARY) {
            if (creatingArrow && (oldArrowX != newXY[0] || oldArrowY != newXY[1])) {
                myControl.chessBoardGUIHandler.addArrow(new Arrow(oldArrowX, oldArrowY, newXY[0], newXY[1], ChessConstants.arrowColor));
            }
            creatingArrow = false;
        }
    }

    public void handleSquareClick(int clickX, int clickY, boolean isHitPiece, boolean isWhiteHitPiece, MainScreenState currentState) {
        int backendY = myControl.gameHandler.currentGame.isWhiteOriented() ? clickY : 7 - clickY;
        int backendX = myControl.gameHandler.currentGame.isWhiteOriented() ? clickX : 7 - clickX;
        if (prevPeiceSelected && selectedPeiceInfo[0] == clickX && selectedPeiceInfo[1] == clickY) {
            // clicked the same piece you selected, this means you are unselecting
            clearPrevPiece(true);
        } else {
            int boardIndex = GeneralChessFunctions.getBoardWithPiece(backendX, backendY, isWhiteHitPiece, myControl.gameHandler.currentGame.currentPosition.board);
            if (currentState.equals(MainScreenState.SANDBOX)) {
                handleSandboxSquareClick(clickX, clickY, isHitPiece, isWhiteHitPiece);
            } else if (checkIfCanMakeAction(currentState)) {
                if (!prevPeiceSelected && !isHitPiece) {
                    // nothing to do as empty square has been clicked with no previous selection
                } else if (!prevPeiceSelected) {
                    // no prev selection, then we want to make sure you are picking a piece that is on your side
                    if (isWhiteHitPiece == myControl.gameHandler.currentGame.isWhiteTurn()) {
                        // matches the turn
                        setPrevPeice(clickX, clickY, isWhiteHitPiece, true, boardIndex, true);
                    }
                } else {
                    // two options, you are either clicking you own piece, or attempting to make a move
                    if (isWhiteHitPiece == myControl.gameHandler.currentGame.isWhiteTurn() && isHitPiece) {
                        // your own piece

                        clearPrevPiece(true);
                        setPrevPeice(clickX, clickY, isWhiteHitPiece, true, boardIndex, true);

                    } else {
                        // can possibly be a move, however it needs to be within prevpiecemoves
                        // [0] = is move possible [1] = is castle move
                        int[] moveInfo = checkIfMovePossible(prevPieceMoves, clickX, clickY);
                        if (moveInfo[0] == 1) {
                            // move is within prev moves
                            boolean pieceSelectedIsWhite = selectedPeiceInfo[2] > 0;
                            int pieceSelectedIndex = selectedPeiceInfo[4];
                            int oldX = selectedPeiceInfo[0];
                            int oldY = selectedPeiceInfo[1];
                            int backendOldY = myControl.gameHandler.currentGame.isWhiteOriented() ? oldY : 7 - oldY;
                            int backendOldX = myControl.gameHandler.currentGame.isWhiteOriented() ? oldX : 7 - oldX;
                            boolean isCastleMove = moveInfo[1] > 0;
                            boolean isEnPassant = moveInfo[1] < 0;
                            boolean isEating = GeneralChessFunctions.checkIfContains(backendX, backendY, !pieceSelectedIsWhite, myControl.gameHandler.currentGame.currentPosition.board);
                            handleMakingMove(backendOldX, backendOldY, backendX, backendY, isEating, pieceSelectedIsWhite, isCastleMove, isEnPassant, false, false, ChessConstants.EMPTYINDEX, currentState, false);
                        } else {
                            // cannot make move
                            clearPrevPiece(true);
                        }

                    }
                }
            }
        }

    }

    private void handleSandboxSquareClick(int clickX, int clickY, boolean isHitPiece, boolean isWhiteHitPiece) {
        if (prevPeiceSelected && selectedPeiceInfo[0] == clickX && selectedPeiceInfo[1] == clickY) {
            // clicked the same piece you selected, this means you are unselecting
            clearPrevPiece(true);
        } else {
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
                setPrevPeice(clickX, clickY, isWhiteHitPiece, true, boardIndex, false);
                myControl.chessBoardGUIHandler.highlightSquare(clickX, clickY, true);
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
                    boolean isEating = GeneralChessFunctions.checkIfContains(clickX, clickY, prevPeiceIsWhite, myControl.gameHandler.currentGame.currentPosition.board);
                    int eatingIndex = GeneralChessFunctions.getBoardWithPiece(clickX, clickY, !prevPeiceIsWhite, myControl.gameHandler.currentGame.currentPosition.board);
                    myControl.gameHandler.currentGame.makeNewMove(new ChessMove(oldX, oldY, clickX, clickY, ChessConstants.EMPTYINDEX, pieceSelectedIndex, prevPeiceIsWhite, false, isEating, eatingIndex, false, false), false, false);

                } else {
                    // adding a piece custom
                    myControl.gameHandler.currentGame.makeCustomMoveSandbox(new ChessPosition(myControl.gameHandler.currentGame.currentPosition, myControl.gameHandler.currentGame.gameState, new ChessMove(0, 0, clickX, clickY, 0, pieceSelectedIndex, prevPeiceIsWhite, false, false, ChessConstants.EMPTYINDEX, false, true)));

                }
                makeBackendUpdate(myControl.mainScreenController.currentState, true, false);


            }
        }
    }

    public void handleMakingMove(int startX, int startY, int endX, int endY, boolean isEating, boolean isWhitePiece, boolean isCastle, boolean isEnPassant, boolean isComputerMove, boolean isPawnPromoFinalized, int promoIndx, MainScreenState currentState, boolean isDragMove) {
        myControl.chessBoardGUIHandler.clearAllHighlights();
        myControl.chessBoardGUIHandler.clearArrows();
        int boardIndex = GeneralChessFunctions.getBoardWithPiece(startX, startY, isWhitePiece, myControl.gameHandler.currentGame.currentPosition.board);
        int eatingIndex = GeneralChessFunctions.getBoardWithPiece(endX, endY, !isWhitePiece, myControl.gameHandler.currentGame.currentPosition.board);
        int endSquare = isWhitePiece ? 0 : 7;
        boolean isPawnPromo = boardIndex == ChessConstants.PAWNINDEX && endY == endSquare;
        boolean pawnPromoToggled = false;
        ChessMove moveMade = null;

        if (promoIndx == ChessConstants.EMPTYINDEX && !isPawnPromo) {
            // not promo
            moveMade = new ChessMove(startX, startY, endX, endY, ChessConstants.EMPTYINDEX, boardIndex, isWhitePiece, isCastle, isEating, eatingIndex, isEnPassant, false);
            myControl.gameHandler.currentGame.makeNewMove(moveMade, isComputerMove, isDragMove);

        } else if (isComputerMove || isPawnPromoFinalized) {
            // computer promoting or player chose their piece to promote
            moveMade = new ChessMove(startX, startY, endX, endY, promoIndx, boardIndex, isWhitePiece, false, isEating, eatingIndex, isEnPassant, false);
            myControl.gameHandler.currentGame.makeNewMove(moveMade, false, isDragMove);

        } else {
            pawnPromoToggled = true;
            sPawnX = startX;
            sPawnY = startY;
            ePawnX = endX;
            ePawnY = endY;
            pieceIndxPromo = boardIndex;
            isWhitePromo = isWhitePiece;
            isEatingPromo = isEating;
            myControl.mainScreenController.showPromo(endX, isWhitePiece, myControl.gameHandler.currentGame.isWhiteOriented());

        }
        if (!pawnPromoToggled) {
            // means you made a move
            // else you are waiting for the player to chose their promotion
            clearPrevPiece(false);


        }

    }

    public void promoPawn(int promoIndex, MainScreenState currentState) {
        handleMakingMove(sPawnX, sPawnY, ePawnX, ePawnY, isEatingPromo, isWhitePromo, false, false, false, true, promoIndex, currentState, false);
    }

    // checks that it is your turn in a game
    public void incrementNumRedos() {
        numRedos++;
    }

    private boolean checkIfCanMakeAction(MainScreenState currentState) {
        switch (currentState) {
            case ONLINE -> {
                return myControl.gameHandler.currentGame.isWhiteTurn() == myControl.gameHandler.currentGame.isWhiteOriented() && myControl.gameHandler.currentGame.curMoveIndex == myControl.gameHandler.currentGame.maxIndex;
            }
            case LOCAL -> {
                // either in 1v1 its the players turn or its whites turn
                if (myControl.gameHandler.currentGame.isVsComputer()) {
                    return myControl.gameHandler.currentGame.isWhiteTurn() == myControl.gameHandler.currentGame.isWhiteOriented();
                } else {
                    return true;
                }
            }
            case VIEWER -> {
                // will be in 1v1 mode
                return true;
            }
            case CAMPAIGN -> {

                if (myControl.gameHandler.currentGame.curMoveIndex == myControl.gameHandler.currentGame.maxIndex) {
                    // checking for new move
                    return myControl.gameHandler.currentGame.isWhiteTurn();
                } else {
                    // in campaign mode you can conditionaly redo moves
                    // easy = infinite redos, medium = 3 redos, hard  = no redos
                    switch (myControl.gameHandler.getGameDifficulty()) {
                        case 1:
                            // unlimited redos
                            return myControl.gameHandler.currentGame.isWhiteTurn();
                        case 2:
                            return numRedos <= 3 && myControl.gameHandler.currentGame.isWhiteTurn();
                        case 3:
                            return false;
                        default:
                            ChessConstants.mainLogger.error("Campaign difficulty default branch activated");
                            return false;
                    }
                }


            }
            default -> {
                ChessConstants.mainLogger.error("checkIfCanMakeMove default case called");
                return false;
            }
        }
    }

    private int[] checkIfMovePossible(List<XYcoord> moves, int x, int y) {
        // todo: change this to bitboard logic
        if (moves != null) {
            for (XYcoord s : moves) {
                if ((s.x == x && s.y == y) && s.isCastleMove()) {
                    return new int[]{1, 1};
                } else if ((s.x == x && s.y == y) && s.isEnPassant()) {
                    return new int[]{1, -1};
                } else if (s.x == x && s.y == y) {
                    return new int[]{1, 0};
                }
            }
        }
        return new int[]{0, 0};


    }

    public void addBestMovesToViewer(List<ComputerOutput> bestMoves) {
        if (myControl.mainScreenController.currentState.equals(MainScreenState.VIEWER) && myControl.gameHandler.currentGame != null) {
            bestmovesBox.getChildren().clear();
            myControl.chessBoardGUIHandler.clearArrows();
            for (int i = 0; i < bestMoves.size(); i++) {
                ChessMove best = bestMoves.get(i).move;
                double adv = bestMoves.get(i).advantage;

                HBox moveGui = new HBox();
                moveGui.setAlignment(Pos.CENTER);
                moveGui.setSpacing(5);
                Label moveNumber = new Label("#" + (i + 1));
                // for pgn generation
                ChessStates testState = myControl.gameHandler.currentGame.gameState.cloneState();
                ChessPosition testPos = new ChessPosition(myControl.gameHandler.currentGame.currentPosition.clonePosition(), testState, best);
                Label moveAsPgn = new Label(PgnFunctions.moveToPgn(bestMoves.get(i).move, testPos, testState));

                // add arrow showing move
                boolean isWhiteOriented = myControl.gameHandler.currentGame.isWhiteOriented();
                myControl.chessBoardGUIHandler.addArrow(new Arrow(isWhiteOriented ? best.getOldX() : 7 - best.getOldX(), isWhiteOriented ? best.getOldY() : 7 - best.getOldY(), isWhiteOriented ? best.getNewX() : 7 - best.getNewX(), isWhiteOriented ? best.getNewY() : 7 - best.getNewY(), ChessConstants.arrowColor));
                String advStr = formatter.format(adv);
                String prefix = adv == 0 ? "" : adv > 0 ? "+" : "-";
                Label expectedAdvantage = new Label(prefix + advStr);
                App.bindingController.bindSmallText(moveNumber, true);
                App.bindingController.bindSmallText(moveAsPgn, true);
                App.bindingController.bindSmallText(expectedAdvantage, true);
                moveGui.prefWidthProperty().bind(bestmovesBox.widthProperty());
                moveGui.getChildren().addAll(moveNumber, moveAsPgn, expectedAdvantage);
                int finalI = i;
                moveGui.setOnMouseClicked(e -> {
                    myControl.gameHandler.currentGame.makeNewMove(bestMoves.get(finalI).move, false, false);
                });
                moveGui.setStyle("-fx-background-color: darkgray");
                bestmovesBox.getChildren().add(moveGui);


            }

        }
    }


    private void updateEvalThread() {
        myControl.asyncController.evalTask.currentPosition = myControl.gameHandler.currentGame.currentPosition;
        myControl.asyncController.evalTask.currentGameState = myControl.gameHandler.currentGame.gameState;
        myControl.asyncController.evalTask.currentIsWhite = myControl.gameHandler.currentGame.isWhiteTurn();
    }

    private void updateCompThread() {
        myControl.asyncController.computerTask.currentPosition = myControl.gameHandler.currentGame.currentPosition;
        myControl.asyncController.computerTask.currentGameState = myControl.gameHandler.currentGame.gameState;
        myControl.asyncController.computerTask.currentIsWhite = myControl.gameHandler.currentGame.isWhiteTurn();
    }

    private void updateNMovesTask() {
        myControl.asyncController.nMovesTask.currentPosition = myControl.gameHandler.currentGame.currentPosition;
        myControl.asyncController.nMovesTask.currentGameState = myControl.gameHandler.currentGame.gameState;
        myControl.asyncController.nMovesTask.currentIsWhite = myControl.gameHandler.currentGame.isWhiteTurn();
        this.lastPosSinceRequest = myControl.gameHandler.currentGame.currentPosition;
        this.lastStateSinceRequest = myControl.gameHandler.currentGame.gameState;
    }

}
