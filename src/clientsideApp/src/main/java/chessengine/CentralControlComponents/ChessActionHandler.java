package chessengine.CentralControlComponents;

import chessengine.App;
import chessengine.Audio.Effect;
import chessengine.Computation.MoveGenerator;
import chessengine.Enums.MainScreenState;
import chessengine.Enums.MoveRanking;
import chessengine.Enums.Window;
import chessengine.Misc.Constants;
import chessserver.Enums.INTENT;
import chessserver.Functions.AdvancedChessFunctions;
import chessserver.Functions.GeneralChessFunctions;
import chessengine.Functions.LineLabeler;
import chessserver.Functions.PgnFunctions;
import chessengine.Graphics.Arrow;
import chessengine.Graphics.MoveArrow;
import chessserver.Misc.ChessConstants;
import chessengine.Records.MultiResult;
import chessengine.Records.PVEntry;
import chessengine.Records.SearchResult;
import chessserver.ChessRepresentations.*;
import chessserver.Enums.ComputerDifficulty;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ChessActionHandler implements Resettable{

    private final LineLabeler labeler = new LineLabeler();
    private final Label lineLabel;
    private final HBox movesPlayedBox;
    private final ScrollPane movesPlayedScrollpane;
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
    private final HBox emojiContainer;

    private final VBox p1Indicator;
    private final VBox p2Indicator;

    private final Label p1moveClk;
    private final Label p2moveClk;
    private final ComboBox<Integer> player1SimSelector;
    private final ComboBox<Integer> player2SimSelector;

    private final Button playPauseButton;
    // [0] = x ,[1] =  y , [2] = (1 = white, -1 = black), [3] (only used for sandbox mode) = (1 = regular piece selected,-1 = additional piece selected), [4] = pieceselectedIndex;
    private final int[] selectedPeiceInfo = {0, 0, 0, 0, 0};
    private final NumberFormat formatter = new DecimalFormat("#0.00");
    private final TextArea currentGamePgn;
    private final Logger logger = LogManager.getLogger(this.toString());
    public final Slider timeSlider;
    private final Button resignButton;
    private final Button offerDrawButton;
    MoveGenerator moveGenerator = new MoveGenerator();
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
    private final boolean oldPieceType = false;
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

    // logic for when you try to drag a piece
    // has 3 different methods that are called, one for when you actually press down on the board to drag, one where you drag the piece, and one when you release the piece
    // when you click on a piece, just like handlesquareclick(), it calculates whether you are able to click that piece or not
    // if ok then when you drag it follows you to where you eventually release the piece
    private boolean isEatingPromo;
    private int numRedos = 0;

    public ChessActionHandler(ChessCentralControl myControl, VBox bestmovesBox, TextArea localInfo, GridPane sandboxPieces, TextArea gameInfo, TextField chatInput, Button sendMessageButton,HBox emojiContainer,Button resignButton,Button offerDrawButton, HBox movesPlayedBox,ScrollPane movesPlayedScrollpane, Label lineLabel, Button playPauseButton,Slider timeSlider, VBox p1Indicator, VBox p2Indicator, Label p1moveClk, Label p2moveClk, ComboBox<Integer> player1SimSelector, ComboBox<Integer> player2SimSelector, TextArea currentGamePgn) {
        this.myControl = myControl;
        this.bestmovesBox = bestmovesBox;
        this.campaignInfo = localInfo;
        this.sandboxPieces = sandboxPieces;
        this.gameInfo = gameInfo;
        this.chatInput = chatInput;
        this.sendMessageButton = sendMessageButton;
        this.lineLabel = lineLabel;
        this.movesPlayedBox = movesPlayedBox;
        this.movesPlayedScrollpane = movesPlayedScrollpane;
        this.playPauseButton = playPauseButton;
        this.timeSlider = timeSlider;
        this.p1Indicator = p1Indicator;
        this.p2Indicator = p2Indicator;
        this.p1moveClk = p1moveClk;
        this.p2moveClk = p2moveClk;
        this.player1SimSelector = player1SimSelector;
        this.player2SimSelector = player2SimSelector;
        this.currentGamePgn = currentGamePgn;
        this.resignButton = resignButton;
        this.offerDrawButton = offerDrawButton;
        this.emojiContainer = emojiContainer;
    }

    public void init(){
        onlineInit();
        localInit();
        sandboxInit();
        simulationInit();
    }

    private void onlineInit() {
        // event handlers for when sending a message
        sendMessageButton.setOnMouseClicked(e -> {
            if(myControl.gameHandler.gameWrapper.isActiveWebGame()){
                if (!chatInput.getText().isEmpty() && myControl.mainScreenController.currentState == MainScreenState.ONLINE) {
                    myControl.mainScreenController.processChatInput();
                }
            }
            else{
                logger.debug("Not active web game");
            }
        });
        chatInput.setOnKeyPressed(e -> {
            if(myControl.gameHandler.gameWrapper.isActiveWebGame()){
                if (e.getCode() == KeyCode.ENTER && myControl.mainScreenController.currentState == MainScreenState.ONLINE) {
                    myControl.mainScreenController.processChatInput();
                }
            }
            else{
                logger.debug("Not active web game");
            }
        });

        final String[] emojis = new String[]{"😄","😐","😭","😡","👍","👎"};

        for(String emoji : emojis){
            Button emojiButton = new Button(emoji);
            App.bindingController.bindSmallTextCustom(emojiButton,Window.Main,"-fx-border-radius: 25");
            emojiButton.setOnMouseClicked(e->{
                if (myControl.gameHandler.gameWrapper.isActiveWebGame() && myControl.gameHandler.gameWrapper.isCurrentWebGameInitialized()) {
                    App.sendRequest(INTENT.SENDCHAT, emoji,null,true);
                }
            });

            emojiContainer.getChildren().add(emojiButton);
        }

        resignButton.setOnMouseClicked(e -> {
            if(myControl.gameHandler.gameWrapper.isActiveWebGame()){
                App.messager.createBooleanPopup("Are you sure?","Yes","No",Window.Main,true,1,() ->{
                    myControl.mainScreenController.HomeReset();
                },null);
            }
            else{
                logger.debug("Not active web game");
            }
        });
        offerDrawButton.setOnMouseClicked(e->{
            if(myControl.gameHandler.gameWrapper.isActiveWebGame()){
                App.sendRequest(INTENT.REQUESTDRAW,"",null,true);
            }
            else{
                logger.debug("Not active web game");
            }
        });
    }

    private void localInit() {
        // nothing for now
    }

    private void simulationInit() {
        playPauseButton.setOnMouseClicked(e -> {
            if (playPauseButton.getText().equals("pause")) {
                playPauseButton.setText("play");
            } else {
                playPauseButton.setText("pause");
            }
            myControl.asyncController.toggleSimPlay();
        });

        timeSlider.setMin(5);
        timeSlider.setBlockIncrement(5);
        timeSlider.setMax(50);
        timeSlider.setValue(10);



        player1SimSelector.getItems().addAll(Arrays.stream(ComputerDifficulty.values()).map(d -> d.eloRange).toList());
        player2SimSelector.getItems().addAll(Arrays.stream(ComputerDifficulty.values()).map(d -> d.eloRange).toList());
        player1SimSelector.getSelectionModel().select(1);
        player2SimSelector.getSelectionModel().select(0);

        player1SimSelector.setOnAction(e -> {
            myControl.asyncController.simTask.setPlayer1SimulationDifficulty(ComputerDifficulty.getDifficultyOffOfElo(player1SimSelector.getValue(),false));
        });

        player2SimSelector.setOnAction(e -> {
            myControl.asyncController.simTask.setPlayer2SimulationDifficulty(ComputerDifficulty.getDifficultyOffOfElo(player2SimSelector.getValue(),false));
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
                piece.fitWidthProperty().bind(myControl.mainScreenController.sidePanel.widthProperty().divide(9));
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
//                        System.out.println("*/Unselecting piece");
                        clearPrevPiece(false);
                        lastSelected.setStyle("");
                        lastSelected = null;
                    } else {
//                        System.out.println("A " + (isWhite ? "white " : "black ") + GeneralChessFunctions.getPieceType(pieceIndex));
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

    /**Reset called every move**/
    public void partialReset(){
        bestmovesBox.getChildren().clear();
        updateCurrentlyShownArrows(false);
        currentlyShownSuggestedArrows.clear();
        if(suggestionArrow != null){
            myControl.chessBoardGUIHandler.removeArrow(suggestionArrow);
            suggestionArrow = null;
        }

        clearPrevPiece(true);

    }

    public void fullReset() {
        movesPlayedBox.getChildren().clear();
        p1moveClk.setText("");
        p2moveClk.setText("");
        gameInfo.clear();
        chatInput.clear();
        campaignInfo.clear();
        numLabels = 0;
        numRedos = 0;
        partialReset();

    }

    public void clearMovesPlayed() {
        movesPlayedBox.getChildren().clear();
    }

    public void addToMovesPlayed(String pgn) {
        Label pgnDescriptor = new Label(pgn);
        pgnDescriptor.setBackground(Constants.defaultBg);

        App.bindingController.bindSmallText(pgnDescriptor, Window.Main, "Black");
        pgnDescriptor.setUserData(numLabels);
        pgnDescriptor.setAlignment(Pos.CENTER);
        int pgnLen = pgn.length();
//    }
        pgnDescriptor.minWidthProperty().bind(myControl.mainScreenController.fullScreen.widthProperty().divide(245).add(10).multiply(pgnLen+.2).multiply(App.dpiScaleFactor));
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
            numSeparator.setAlignment(Pos.CENTER);
            numSeparator.minWidthProperty().bind(myControl.mainScreenController.fullScreen.widthProperty().divide(160).add(12).multiply(Math.log10(moveNum)+0.5).multiply(App.dpiScaleFactor));
            App.bindingController.bindSmallText(numSeparator, Window.Main, "White");
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
            numLabels = curMoveIndex + 1;
            int to = movesPlayedBox.getChildren().size();
            int prevNumSpacers = (numLabels + 1) / 2;
            if (to > numLabels + prevNumSpacers) {
//                logger.debug(String.format("Clearing moves played from %d", curMoveIndex + 1));


                movesPlayedBox.getChildren().subList(numLabels + prevNumSpacers, to).clear();
            } else {
                logger.error("Clear moves played out of bounds");
            }
        }

    }

    private void adjustMovesPlayed(int currentMoveIndex) {
        if (currentMoveIndex < numLabels - 1) {
            clearMovesPlayedUpToIndex(currentMoveIndex);
        } else {
            String[] pgn = myControl.gameHandler.gameWrapper.getGame().gameToPgnArr();
            for (int i = numLabels; i <= currentMoveIndex; i++) {
                addToMovesPlayed(pgn[i]);
            }
        }
    }

    public void highlightMovesPlayedLine(int highlightIndex) {
        logger.debug("Highlighting moves played line");
        if (highlightIndex >= 0 && highlightIndex <= myControl.gameHandler.gameWrapper.getGame().getMaxIndex()) {
            int prevNumSpacers = highlightIndex / 2;
            int tot = highlightIndex + prevNumSpacers + 1;

            if (tot < movesPlayedBox.getChildren().size()) {
                if (lastHighlight != null) {
                    lastHighlight.setBackground(Constants.defaultBg);
                }
                lastHighlight = (Label) movesPlayedBox.getChildren().get(tot);
                lastHighlight.setBackground(Constants.highlightBg);
                Bounds contentBounds = lastHighlight.localToParent(lastHighlight.getBoundsInLocal());
                double scrollPosition = (Math.max(contentBounds.getMinX()-contentBounds.getWidth()/2,0)) /
                        (movesPlayedScrollpane.getContent().getBoundsInLocal().getWidth() - movesPlayedScrollpane.getViewportBounds().getWidth());
                movesPlayedScrollpane.setHvalue(scrollPosition);
            } else {
                logger.error("Should not be here, highlight index past total size h: " + highlightIndex);
            }
        } else {
            logger.debug("Invalid highlight index passed to highlight moves played line h: " + highlightIndex);
        }

    }

    public void clearMovesPlayedHighlight() {
        if (lastHighlight != null) {
            lastHighlight.setBackground(Constants.defaultBg);
            lastHighlight = null;
        }
    }

//    public void updateViewerSuggestions() {
//        System.out.println("updating suggestion");
//        bestmovesBox.getChildren().clear();
//        myControl.chessBoardGUIHandler.clearArrows();
//        if(!myControl.gameHandler.currentGame.getGameState().isGameOver()){
//            myControl.asyncController.nMovesTask.evalRequest();
//        }
//
//
//
//    }

    public void appendNewMessageToChat(String message) {
        if(myControl.gameHandler.currentlyGameActive()){
            gameInfo.appendText(message + "\n");
        }
        else{
            logger.warn("Message being appended to chat when no game is active!");
        }
    }


    // logic that happens when a square is clicked

    // stores the last piece selected for a move

    private void addCampaignMessage(String message) {
        campaignInfo.appendText(message + "\n");
        App.soundPlayer.playEffect(Effect.MESSAGE);
    }

    private void updateTurnIndicators(boolean isWhiteTurn, boolean isPlayer1White) {
        // todo figure out timers and diplay and centralized pulse etc
        VBox whiteIndicator = isPlayer1White ? p1Indicator : p2Indicator;
        VBox blackIndicator = isPlayer1White ? p2Indicator : p1Indicator;
        Label whiteLabel = isPlayer1White ? p1moveClk : p2moveClk;
        Label blackLabel = isPlayer1White ? p2moveClk : p1moveClk;
        // white on bottom(so p1)
        if (isWhiteTurn) {
            whiteIndicator.setBackground(Constants.whiteTurnActive);
            blackIndicator.setBackground(Constants.labelUnactive);
            whiteLabel.styleProperty().unbind();
            App.bindingController.bindSmallText(whiteLabel, Window.Main, "Black");


        } else {
            whiteIndicator.setBackground(Constants.labelUnactive);
            blackIndicator.setBackground(Constants.blackTurnActive);
            blackLabel.styleProperty().unbind();
            App.bindingController.bindSmallText(blackLabel, Window.Main, "White");
        }


    }

    public void makeBackendUpdate(MainScreenState currentState, boolean isNewMoveMade, boolean isInit) {
        // method called every time the board changes. Could be a move, or maybe changing to a previous move
        if(MainScreenState.isEvalAllowed(currentState)){
            myControl.checkCacheNewIndex();
            myControl.getCentralEvaluation();
        }
        // sandbox can have invalid pgn due to custom moves
        if (currentState != MainScreenState.SANDBOX) {
            highlightMovesPlayedLine(myControl.gameHandler.gameWrapper.getGame().getCurMoveIndex());

            String gamePgn = myControl.gameHandler.gameWrapper.getGame().gameToPgn(myControl.gameHandler.gameWrapper.getGame().getCurMoveIndex());
            String[] gamePgnArr = myControl.gameHandler.gameWrapper.getGame().gameToPgnArr(myControl.gameHandler.gameWrapper.getGame().getCurMoveIndex());
            currentGamePgn.setText(gamePgn);
            updateTurnIndicators(myControl.gameHandler.gameWrapper.getGame().isWhiteTurn(), myControl.gameHandler.gameWrapper.getGame().isWhiteOriented());

            if (myControl.gameHandler.gameWrapper.getGame().getCurMoveIndex() > myControl.gameHandler.gameWrapper.getGame().getMinIndex()) {
                lineLabel.setText(labeler.getLineName(gamePgnArr));
            }

            if ((numLabels - 1) != myControl.gameHandler.gameWrapper.getGame().getMaxIndex()) {
                adjustMovesPlayed(myControl.gameHandler.gameWrapper.getGame().getMaxIndex());
            }
        }
        switch (currentState) {
            case SIMULATION -> {
                if (isInit) {
//                    System.out.println("Start sim");
                    myControl.asyncController.startSimPlay(); // start sim task
                }
            }
            case SANDBOX -> {
                // if both kings are on the board we do a regular eval check
                // if any king is off the board this breaks everything so instead we just say one side checkmated
                BitBoardWrapper board = myControl.gameHandler.gameWrapper.getGame().getCurrentPosition().board;
                if (GeneralChessFunctions.getPieceCoords(board.getWhitePiecesBB()[5]).isEmpty()) {
                    // no white king but shouldnt trigger game over
                    myControl.mainScreenController.setEvalBar(-10000000, -1, false);
                } else if (GeneralChessFunctions.getPieceCoords(board.getBlackPiecesBB()[5]).isEmpty()) {
                    // no black king but shouldnt trigger game over
                    myControl.mainScreenController.setEvalBar(10000000, -1, false);
                }

            }
            case LOCAL -> {
                // could be a new move pgn, or not
                if (isNewMoveMade || isInit) {
                    if (!myControl.gameHandler.gameWrapper.getGame().getGameState().isGameOver() && myControl.gameHandler.gameWrapper.getGame().isVsComputer() && !myControl.gameHandler.gameWrapper.getGame().isWhiteTurn() == myControl.gameHandler.gameWrapper.getGame().isWhiteOriented()) {
//                        GeneralChessFunctions.printBoardDetailed(myControl.gameHandler.currentGame.getCurrentPosition().board);
                        updateCompThread();
                        myControl.asyncController.computerTask.evaluationRequest(); //todo
                    }
                }


            }
            case CAMPAIGN -> {

                if (isNewMoveMade) {
                    if (!myControl.gameHandler.gameWrapper.getGame().getGameState().isGameOver() && !myControl.gameHandler.gameWrapper.getGame().isWhiteTurn()) {

                        updateCompThread();
                        myControl.asyncController.computerTask.evaluationRequest();
                    }

                    // when playing campaign send messages to the player

                    ChessMove move = myControl.gameHandler.gameWrapper.getGame().getCurrentPosition().getMoveThatCreatedThis();
                    if (myControl.gameHandler.gameWrapper.getGame().getGameState().isCheckMated()[0]) {
                        String message = App.campaignMessager.getCheckmateMessage(myControl.gameHandler.gameWrapper.getGame().getGameState().isCheckMated()[1],myControl.gameHandler.gameWrapper.getGame().isWhiteOriented());
                        addCampaignMessage(message);

                    } else if (myControl.gameHandler.gameWrapper.getGame().getGameState().isStaleMated()) {
                        String message = App.campaignMessager.getStalemateMessage();
                        addCampaignMessage(message);
                    } else {
                        int rand = ChessConstants.generalRandom.nextInt(0, 100);
                        if (rand > 70) {
                            // 30% of the time send some message
                            if (AdvancedChessFunctions.isAnyChecked(myControl.gameHandler.gameWrapper.getGame().getCurrentPosition().board)) {
                                String message = App.campaignMessager.getCheckMessage(move.isWhite(),myControl.gameHandler.gameWrapper.getGame().isWhiteOriented());
                                addCampaignMessage(message);
                            } else {
                                if (!move.equals(ChessConstants.startMove)) {
                                    if (move.isEating()) {
                                        int curIndex = myControl.gameHandler.gameWrapper.getGame().getCurMoveIndex();
                                        int eatenAddIndex = GeneralChessFunctions.getBoardWithPiece(move.getNewX(), move.getNewY(), !move.isWhite(), myControl.gameHandler.gameWrapper.getGame().getPos(curIndex - 1).board);
                                        String message = App.campaignMessager.getEatingMessage(eatenAddIndex, move.isWhite(),myControl.gameHandler.gameWrapper.getGame().isWhiteOriented());
                                        addCampaignMessage(message);
                                    } else if (move.isPawnPromo()) {
                                        String message = App.campaignMessager.getPromoMessage(move.getPromoIndx(), move.isWhite(),myControl.gameHandler.gameWrapper.getGame().isWhiteOriented());
                                        addCampaignMessage(message);
                                    } else if (move.isCastleMove()) {
                                        String message = App.campaignMessager.getCastleMessage(move.isWhite(),myControl.gameHandler.gameWrapper.getGame().isWhiteOriented());
                                        addCampaignMessage(message);
                                    } else {
                                        String message = App.campaignMessager.getMoveMessage(AdvancedChessFunctions.getSimpleAdvantage(myControl.gameHandler.gameWrapper.getGame().getCurrentPosition().board), move.isWhite(),myControl.gameHandler.gameWrapper.getGame().isWhiteOriented());
                                        addCampaignMessage(message);
                                    }
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
        boolean isWhiteOriented = myControl.gameHandler.gameWrapper.getGame().isWhiteOriented();
        int backendY = isWhiteOriented ? y : 7 - y;
        int backendX = isWhiteOriented ? x : 7 - x;
        prevPieceMoves = AdvancedChessFunctions.getPossibleMoves(backendX, backendY, pieceIsWhite, myControl.gameHandler.gameWrapper.getGame().getCurrentPosition(), myControl.gameHandler.gameWrapper.getGame().getGameState());

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
                int backendY = myControl.gameHandler.gameWrapper.getGame().isWhiteOriented() ? xy[1] : 7 - xy[1];
                int backendX = myControl.gameHandler.gameWrapper.getGame().isWhiteOriented() ? xy[0] : 7 - xy[0];
                boolean[] boardInfo = GeneralChessFunctions.checkIfContains(backendX, backendY, myControl.gameHandler.gameWrapper.getGame().getCurrentPosition().board, "boardpress");
                // check if we did not click on an empty square
                if (boardInfo[0]) {
                    boolean isWhitePiece = boardInfo[1];
                    int boardIndex = GeneralChessFunctions.getBoardWithPiece(backendX, backendY, isWhitePiece, myControl.gameHandler.gameWrapper.getGame().getCurrentPosition().board);

                    ImageView piece = myControl.chessBoardGUIHandler.piecesAtLocations[xy[0]][xy[1]];
                    if (currentState == MainScreenState.SANDBOX) {
                        // in sandbox mode players can move pieces anywhere
                        prepareDragSelected(piece, xy[0], xy[1], isWhitePiece, boardIndex, true);
                    } else if (checkIfCanMakeAction(currentState)) {

                        if (isWhitePiece == myControl.gameHandler.gameWrapper.getGame().isWhiteTurn()) {
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
            // arrow needs to stay in bounds
            creatingArrow = !(e.getX() >= myControl.chessBoardGUIHandler.chessPieceBoard.getWidth()) && !(e.getY() >= myControl.chessBoardGUIHandler.chessPieceBoard.getHeight()) && !(e.getX() < 0) && !(e.getY() < 0);
        }
    }

    public void handleBoardRelease(MouseEvent e, MainScreenState currentState) {
        int[] newXY = myControl.chessBoardGUIHandler.turnLayoutXyintoBoardXy(e.getX(), e.getY());
        if (e.getButton() == MouseButton.PRIMARY) {
            if (selected != null && dragging) {
                int newX = newXY[0];
                int newY = newXY[1];
                int backendY = myControl.gameHandler.gameWrapper.getGame().isWhiteOriented() ? newY : 7 - newY;
                int backendX = myControl.gameHandler.gameWrapper.getGame().isWhiteOriented() ? newX : 7 - newX;
                int oldbackendY = myControl.gameHandler.gameWrapper.getGame().isWhiteOriented() ? oldY : 7 - oldY;
                int oldbackendX = myControl.gameHandler.gameWrapper.getGame().isWhiteOriented() ? oldX : 7 - oldX;
                if (currentState == MainScreenState.SANDBOX) {
                    // for sandbox we dont care about rules, we just move wherever we want
                    myControl.gameHandler.gameWrapper.makeNewMove(new ChessMove(oldbackendX, oldbackendY, backendX, backendY, ChessConstants.EMPTYINDEX,
                            oldDragPieceIndex, oldIsWhite, false, GeneralChessFunctions.checkIfContains(backendX, backendY, myControl.gameHandler.gameWrapper.getGame().getCurrentPosition().board, "nut")[0],
                            ChessConstants.EMPTYINDEX, false, false), false, true,App.userPreferenceManager.isNoAnimate(),false);
                    placePiece(selected, newX, newY);
                } else {
                    // all we have to do is check to see if where the piece has been released is a valid square
                    // this consists of two things
                    // #1 the square must either be empty or be an enemy square
                    // #2 the square must be a move that the piece can make
                    boolean[] boardInfo = GeneralChessFunctions.checkIfContains(backendX, backendY, myControl.gameHandler.gameWrapper.getGame().getCurrentPosition().board, "boardRelease");
                    boolean isHit = boardInfo[0];
                    boolean isWhitePieceDroppedOn = boardInfo[1];
                    boolean isEating = GeneralChessFunctions.checkIfContains(backendX, backendY, !oldIsWhite, myControl.gameHandler.gameWrapper.getGame().getCurrentPosition().board);
                    int[] moveInfo = checkIfMovePossible(prevPieceMoves, oldX, oldY, newX, newY, oldDragPieceIndex, oldIsWhite,isEating);
                    boolean isMovePossible = moveInfo[0] == 1;
                    boolean isCastle = moveInfo[1] > 0;
                    boolean isEnPassant = moveInfo[1] < 0;
                    if ((oldIsWhite != isWhitePieceDroppedOn || !isHit) && isMovePossible) {
                        // enemy piece or empty square, and in possible moves

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
                myControl.chessBoardGUIHandler.toggleArrow(new MoveArrow(oldArrowX, oldArrowY, newXY[0], newXY[1], ChessConstants.arrowColor));
            }
            creatingArrow = false;
        }
    }

    public void handleSquareClick(int clickX, int clickY, boolean isHitPiece, boolean isWhiteHitPiece, MainScreenState currentState) {
        int backendY = myControl.gameHandler.gameWrapper.getGame().isWhiteOriented() ? clickY : 7 - clickY;
        int backendX = myControl.gameHandler.gameWrapper.getGame().isWhiteOriented() ? clickX : 7 - clickX;
        if (prevPeiceSelected && selectedPeiceInfo[0] == clickX && selectedPeiceInfo[1] == clickY) {
            // clicked the same piece you selected, this means you are unselecting
            clearPrevPiece(true);
        } else {
            int boardIndex = GeneralChessFunctions.getBoardWithPiece(backendX, backendY, isWhiteHitPiece, myControl.gameHandler.gameWrapper.getGame().getCurrentPosition().board);
            if (currentState == MainScreenState.SANDBOX) {
                handleSandboxSquareClick(clickX, clickY, isHitPiece, isWhiteHitPiece);
            } else if (checkIfCanMakeAction(currentState)) {
                if (!prevPeiceSelected && !isHitPiece) {
                    // nothing to do as empty square has been clicked with no previous selection
                    return;
                } else if (!prevPeiceSelected) {
                    // no prev selection, then we want to make sure you are picking a piece that is on your side
                    if (isWhiteHitPiece == myControl.gameHandler.gameWrapper.getGame().isWhiteTurn()) {
                        // matches the turn
                        setPrevPeice(clickX, clickY, isWhiteHitPiece, true, boardIndex, true);
                    }
                } else {
                    // two options, you are either clicking you own piece, or attempting to make a move
                    if (isWhiteHitPiece == myControl.gameHandler.gameWrapper.getGame().isWhiteTurn() && isHitPiece) {
                        // your own piece

                        clearPrevPiece(true);
                        setPrevPeice(clickX, clickY, isWhiteHitPiece, true, boardIndex, true);

                    } else {
                        // can possibly be a move, however it needs to be within prevpiecemoves
                        // [0] = is move possible [1] = is castle move
                        boolean pieceSelectedIsWhite = selectedPeiceInfo[2] > 0;
                        int pieceSelectedIndex = selectedPeiceInfo[4];
                        int oldX = selectedPeiceInfo[0];
                        int oldY = selectedPeiceInfo[1];
                        int backendOldY = myControl.gameHandler.gameWrapper.getGame().isWhiteOriented() ? oldY : 7 - oldY;
                        int backendOldX = myControl.gameHandler.gameWrapper.getGame().isWhiteOriented() ? oldX : 7 - oldX;
                        boolean isEating = GeneralChessFunctions.checkIfContains(backendX, backendY, !pieceSelectedIsWhite, myControl.gameHandler.gameWrapper.getGame().getCurrentPosition().board);
                        int[] moveInfo = checkIfMovePossible(prevPieceMoves, oldX, oldY, clickX, clickY, pieceSelectedIndex, pieceSelectedIsWhite,isEating);
                        if (moveInfo[0] == 1) {
                            // move is within prev moves

                            boolean isCastleMove = moveInfo[1] > 0;
                            boolean isEnPassant = moveInfo[1] < 0;
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
                int boardIndex = GeneralChessFunctions.getBoardWithPiece(clickX, clickY, isWhiteHitPiece, myControl.gameHandler.gameWrapper.getGame().getCurrentPosition().board);
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
                    boolean isEating = GeneralChessFunctions.checkIfContains(clickX, clickY, prevPeiceIsWhite, myControl.gameHandler.gameWrapper.getGame().getCurrentPosition().board);
                    int eatingIndex = GeneralChessFunctions.getBoardWithPiece(clickX, clickY, !prevPeiceIsWhite, myControl.gameHandler.gameWrapper.getGame().getCurrentPosition().board);
                    myControl.gameHandler.gameWrapper.makeNewMove(new ChessMove(oldX, oldY, clickX, clickY, ChessConstants.EMPTYINDEX, pieceSelectedIndex, prevPeiceIsWhite, false, isEating, eatingIndex, false, false), false, false,App.userPreferenceManager.isNoAnimate(),false);

                } else {
                    // adding a piece custom
                    myControl.gameHandler.gameWrapper.makeCustomMoveSandbox(new ChessPosition(myControl.gameHandler.gameWrapper.getGame().getCurrentPosition(), myControl.gameHandler.gameWrapper.getGame().getGameState(), new ChessMove(0, 0, clickX, clickY, 0, pieceSelectedIndex, prevPeiceIsWhite, false, false, ChessConstants.EMPTYINDEX, false, true)));
                }
            }
        }
    }

    public void handleMakingMove(int startX, int startY, int endX, int endY, boolean isEating, boolean isWhitePiece, boolean isCastle, boolean isEnPassant, boolean isComputerMove, boolean isPawnPromoFinalized, int promoIndx, MainScreenState currentState, boolean isDragMove) {
        myControl.chessBoardGUIHandler.clearUserCreatedHighlights();
        myControl.chessBoardGUIHandler.clearArrowsAndRankings();

        int boardIndex = GeneralChessFunctions.getBoardWithPiece(startX, startY, isWhitePiece, myControl.gameHandler.gameWrapper.getGame().getCurrentPosition().board);
        int eatingIndex = GeneralChessFunctions.getBoardWithPiece(endX, endY, !isWhitePiece, myControl.gameHandler.gameWrapper.getGame().getCurrentPosition().board);
        int endSquare = isWhitePiece ? 0 : 7;
        boolean isPawnPromo = boardIndex == ChessConstants.PAWNINDEX && endY == endSquare;
        boolean pawnPromoToggled = false;
        ChessMove moveMade;

        if (promoIndx == ChessConstants.EMPTYINDEX && !isPawnPromo) {
            // not promo
            moveMade = new ChessMove(startX, startY, endX, endY, ChessConstants.EMPTYINDEX, boardIndex, isWhitePiece, isCastle, isEating, eatingIndex, isEnPassant, false);
            myControl.gameHandler.gameWrapper.makeNewMove(moveMade, isComputerMove, isDragMove,App.userPreferenceManager.isNoAnimate(),false);

        } else if (isComputerMove || isPawnPromoFinalized) {
            // computer promoting or player chose their piece to promote
            moveMade = new ChessMove(startX, startY, endX, endY, promoIndx, boardIndex, isWhitePiece, false, isEating, eatingIndex, isEnPassant, false);
            myControl.gameHandler.gameWrapper.makeNewMove(moveMade, isComputerMove, isDragMove,App.userPreferenceManager.isNoAnimate(),false);

        } else {
            pawnPromoToggled = true;
            sPawnX = startX;
            sPawnY = startY;
            ePawnX = endX;
            ePawnY = endY;
            pieceIndxPromo = boardIndex;
            isWhitePromo = isWhitePiece;
            isEatingPromo = isEating;
            myControl.mainScreenController.showPromo(endX, isWhitePiece, myControl.gameHandler.gameWrapper.getGame().isWhiteOriented());

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
                return myControl.gameHandler.gameWrapper.getGame().isWhiteTurn() == myControl.gameHandler.gameWrapper.getGame().isWhiteOriented() && myControl.gameHandler.gameWrapper.getGame().getCurMoveIndex() == myControl.gameHandler.gameWrapper.getGame().getMaxIndex();
            }
            case LOCAL -> {
                // either in 1v1 its the players turn or its whites turn
                if (myControl.gameHandler.gameWrapper.getGame().isVsComputer()) {
                    return myControl.gameHandler.gameWrapper.getGame().isWhiteTurn() == myControl.gameHandler.gameWrapper.getGame().isWhiteOriented();
                } else {
                    return true;
                }
            }
            case VIEWER -> {
                // will be in 1v1 mode
                return true;
            }
            case CAMPAIGN -> {

                if (myControl.gameHandler.gameWrapper.getGame().getCurMoveIndex() == myControl.gameHandler.gameWrapper.getGame().getMaxIndex()) {
                    // checking for new move
                    return myControl.gameHandler.gameWrapper.getGame().isWhiteTurn();
                } else {
                    // in campaign mode you can conditionaly redo moves
                    // easy = infinite redos, medium = 3 redos, hard  = no redos
                    switch (myControl.gameHandler.getGameDifficulty()) {
                        case 1:
                            // unlimited redos
                            return myControl.gameHandler.gameWrapper.getGame().isWhiteTurn();
                        case 2:
                            return numRedos <= 3 && myControl.gameHandler.gameWrapper.getGame().isWhiteTurn();
                        case 3:
                            return false;
                        default:
                            logger.error("Campaign difficulty default branch activated");
                            return false;
                    }
                }


            }
            case PUZZLE -> {
                return myControl.gameHandler.gameWrapper.getGame().isWhiteOriented() == myControl.gameHandler.gameWrapper.getGame().isWhiteTurn();
            }
            default -> {
                logger.error("checkIfCanMakeMove default case called");
                return false;
            }
        }
    }

    private int[] checkIfMovePossible(List<XYcoord> moves, int oldX, int oldY, int newX, int newY, int peiceType, boolean isWhite,boolean isEating) {
        // todo: change this to bitboard logic
        if (moves != null) {
            for (XYcoord s : moves) {
                boolean isCastle = peiceType == ChessConstants.KINGINDEX && Math.abs(oldX - s.x) > 1;
                boolean isEnPassant = peiceType == ChessConstants.PAWNINDEX && !isEating && oldX != s.x;
                if ((s.x == newX && s.y == newY) && isCastle) {
                    return new int[]{1, 1};
                } else if ((s.x == newX && s.y == newY) && isEnPassant) {
                    return new int[]{1, -1};
                } else if (s.x == newX && s.y == newY) {
                    return new int[]{1, 0};
                }
            }
        }
        return new int[]{0, 0};


    }


//    public void addBestMovesToViewer(MoveOutput[] bestMoves) {
//        if (myControl.isInViewerMove()) {
//            bestmovesBox.getChildren().clear();
//            myControl.chessBoardGUIHandler.clearArrows();
//            for (int i = 0; i < bestMoves.length; i++) {
//                ChessMove best = bestMoves[i].move();
//                double adv = bestMoves[i].advantage();
//
//                HBox moveGui = new HBox();
//                moveGui.setAlignment(Pos.CENTER);
//                moveGui.setSpacing(5);
//                Label moveNumber = new Label("#" + (i + 1));
//                // for pgn generation
//                ChessStates testState = myControl.gameHandler.currentGame.getGameState().cloneState();
//                ChessPosition testPos = new ChessPosition(myControl.gameHandler.currentGame.getCurrentPosition().clonePosition(), testState, best);
//                Label moveAsPgn = new Label(PgnFunctions.moveToPgn(best, testPos, testState));
//
//                // add arrow showing move
//                boolean isPlayer1White = myControl.gameHandler.currentGame.isWhiteOriented();
//                Arrow moveArrow = new MoveArrow(isPlayer1White ? best.getOldX() : 7 - best.getOldX(), isPlayer1White ? best.getOldY() : 7 - best.getOldY(), isPlayer1White ? best.getNewX() : 7 - best.getNewX(), isPlayer1White ? best.getNewY() : 7 - best.getNewY(), ChessConstants.getColorBasedOnAdvantage(best.isWhite(),adv,currentEval));
//                myControl.chessBoardGUIHandler.addArrow(moveArrow);
//                String advStr = formatter.format(adv);
//                Label expectedAdvantage = new Label(advStr);
//                App.bindingController.bindSmallText(moveNumber, true);
//                App.bindingController.bindSmallText(moveAsPgn, true);
//                App.bindingController.bindSmallText(expectedAdvantage, true);
//                moveGui.prefWidthProperty().bind(bestmovesBox.widthProperty());
//                moveGui.getChildren().addAll(moveNumber, moveAsPgn, expectedAdvantage);
//                moveGui.setOnMouseClicked(e -> {
//                    myControl.gameHandler.currentGame.makeNewMove(best, false, false);
//                });
//                moveGui.setStyle("-fx-background-color: darkgray");
//                bestmovesBox.getChildren().add(moveGui);
//
//
//            }
//
//        }
//        else{
//            logger.error("Invalid viewer update!");
//        }
//    }
    private final List<Arrow> currentlyShownSuggestedArrows = new ArrayList<>();
    public void addBestMovesToViewer(MultiResult results) {
        if (myControl.isInViewerActive()) {
            updateCurrentlyShownArrows(false);
            currentlyShownSuggestedArrows.clear();
            bestmovesBox.getChildren().clear();
            int cnt = 0;
            int primeEvaluation = results.results()[0].evaluation();
            PVEntry[] bestPV = results.results()[0].pV();
            for (SearchResult result : results.results()) {
                PVEntry[] pv = result.pV();
                ChessMove move = result.move();
                double adv = ((double) result.evaluation() / 100) * (myControl.gameHandler.gameWrapper.getGame().isWhiteTurn() ? 1 : -1);

                HBox moveGui = new HBox();
                moveGui.setAlignment(Pos.CENTER);
                moveGui.setSpacing(5);
                Label moveNumber = new Label("#" + (++cnt));
                // for pgn generation
                final int maxPvLength = 7;
                HBox pvsBox = new HBox();
                pvsBox.setSpacing(5);
                pvsBox.setAlignment(Pos.CENTER);
                ChessGameState testState = myControl.gameHandler.gameWrapper.getGame().getGameState().cloneState();
                ChessPosition testPos = myControl.gameHandler.gameWrapper.getGame().getCurrentPosition().clonePosition();
                for(int i = 0;i<Math.min(maxPvLength,pv.length);i++){
                    ChessMove pvMove = pv[i].pvMove();
                    testPos = new ChessPosition(testPos, testState, pvMove);
                    Label movePVPGN = new Label(PgnFunctions.moveToPgn(pvMove, testPos, testState));
                    App.bindingController.bindSmallText(movePVPGN, Window.Main);
                    pvsBox.getChildren().add(movePVPGN);
                    setupPVMouseOver(movePVPGN,pvMove,testPos.clonePosition());
                }

                // add arrow showing move
                boolean isPlayer1White = myControl.gameHandler.gameWrapper.getGame().isWhiteOriented();
                MoveRanking currentMoveRanking = MoveRanking.getMoveRanking(primeEvaluation, results.moveValues().get(move).evaluation(), bestPV, result.pV());
                Arrow moveArrow = new MoveArrow(move.getMoveWhiteOriented(isPlayer1White), currentMoveRanking.getColor().toString());
                myControl.chessBoardGUIHandler.toggleArrow(moveArrow);
                currentlyShownSuggestedArrows.add(moveArrow);
                String advStr = formatter.format(adv);
                Label expectedAdvantage = new Label(advStr);
                App.bindingController.bindSmallText(moveNumber, Window.Main);
                App.bindingController.bindSmallText(expectedAdvantage, Window.Main);
                moveGui.prefWidthProperty().bind(bestmovesBox.widthProperty());
                moveGui.getChildren().addAll(moveNumber, pvsBox, expectedAdvantage);
                moveGui.setOnMouseClicked(e -> {
                    myControl.gameHandler.gameWrapper.makeNewMove(move, false, false,App.userPreferenceManager.isNoAnimate(),false);
                });
                int curIndex = myControl.gameHandler.gameWrapper.getGame().getCurMoveIndex();
                boolean isWhiteOriented = myControl.gameHandler.gameWrapper.getGame().isWhiteOriented();
                ChessPosition currentPos = myControl.gameHandler.gameWrapper.getGame().getCurrentPosition();
                pvsBox.setOnMouseExited(e->{
                    int newIndex = myControl.gameHandler.gameWrapper.getGame().getCurMoveIndex();
                    // avoid edge case where you click the movegui button (move to next move) then you move your cursor off. In that case you shoudnt reset
                    if(curIndex == newIndex){
                        updateCurrentlyShownArrows(true);
                        myControl.chessBoardGUIHandler.reloadNewBoard(currentPos,isWhiteOriented);
                    }

                });
                moveGui.setStyle("-fx-background-color: darkgray");
                bestmovesBox.getChildren().add(moveGui);


            }

        } else {
            logger.error("Invalid viewer update");
        }
    }

    private MoveArrow suggestionArrow = null;
    private void setupPVMouseOver(Label PV,ChessMove pvMove,ChessPosition movePreview){
        boolean isWhiteOriented = myControl.gameHandler.gameWrapper.getGame().isWhiteOriented();
        PV.setOnMouseEntered(e ->{
            myControl.chessBoardGUIHandler.reloadNewBoard(movePreview,isWhiteOriented);
            suggestionArrow = new MoveArrow(pvMove.getMoveWhiteOriented(isWhiteOriented),ChessConstants.arrowColor);
            myControl.chessBoardGUIHandler.toggleArrow(suggestionArrow);
            updateCurrentlyShownArrows(false);
        });
        PV.setOnMouseExited(e->{
            if(suggestionArrow != null){
                myControl.chessBoardGUIHandler.removeArrow(suggestionArrow);
            }
        });
    }
    private void updateCurrentlyShownArrows(boolean isShow){
        for(Arrow arrow : currentlyShownSuggestedArrows){
            if(isShow){
                myControl.chessBoardGUIHandler.addArrow(arrow);
            }
            else{
                myControl.chessBoardGUIHandler.removeArrow(arrow);
            }
        }
    }


    private void updateCompThread() {
        myControl.asyncController.computerTask.currentPosition = myControl.gameHandler.gameWrapper.getGame().getCurrentPosition();
        myControl.asyncController.computerTask.currentGameState = myControl.gameHandler.gameWrapper.getGame().getGameState();
        myControl.asyncController.computerTask.currentIsWhite = myControl.gameHandler.gameWrapper.getGame().isWhiteTurn();
    }

    public void timeTick(int timeLeft) {
        if(myControl.gameHandler.currentlyGameActive() && myControl.gameHandler.gameWrapper.isActiveWebGame()){
            boolean currentIsWhite = myControl.gameHandler.gameWrapper.getGame().isWhiteTurnAtMax();
            boolean isWhiteOriented = myControl.gameHandler.gameWrapper.getGame().isWhiteOriented();

            Label moveClockToUpdate = currentIsWhite == isWhiteOriented ? p1moveClk : p2moveClk;
            moveClockToUpdate.setText(ChessConstants.formatSeconds(timeLeft));
        }
        else{
            logger.error("Time tick while no game active or not web game!");
        }
    }

    public void handleDrawRequest() {
        if(App.ChessCentralControl.gameHandler.currentlyGameActive() && myControl.gameHandler.gameWrapper.isActiveWebGame()){
            String opponentName = myControl.gameHandler.gameWrapper.getGame().isWhiteOriented() ? myControl.gameHandler.gameWrapper.getGame().getWhitePlayerName() : myControl.gameHandler.gameWrapper.getGame().getBlackPlayerName();
            App.messager.createBooleanPopup(opponentName + " is offering a draw","Accept","Regect",Window.Main,true,2,
                () ->{
                    App.sendRequest(INTENT.DRAWACCEPTANCEUPDATE,"true",null,true);
                },
                () -> {
                    App.sendRequest(INTENT.DRAWACCEPTANCEUPDATE,"false",null,true);
                });
        }
        else{
            logger.error("Asking for draw when no game currently active or not web game!");
        }
    }

//    private void updateNMovesTask() {
//        this.currentEval = ComputerHelperFunctions.getFullEval(myControl.gameHandler.currentGame.getCurrentPosition(),myControl.gameHandler.currentGame.getGameState(),myControl.gameHandler.currentGame.isWhiteTurn(),false);
//        myControl.asyncController.nMovesTask.getCurrentPosition() = myControl.gameHandler.currentGame.getCurrentPosition();
//        myControl.asyncController.nMovesTask.currentGameState = myControl.gameHandler.currentGame.getGameState();
//        myControl.asyncController.nMovesTask.currentIsWhite = myControl.gameHandler.currentGame.isWhiteTurn();
//    }
//
//    private void updateEvalThread() {
//        myControl.asyncController.evalTask.getCurrentPosition() = myControl.gameHandler.currentGame.getCurrentPosition();
//        myControl.asyncController.evalTask.currentGameState = myControl.gameHandler.currentGame.getGameState();
//        myControl.asyncController.evalTask.currentIsWhite = myControl.gameHandler.currentGame.isWhiteTurn();
//    }

}
