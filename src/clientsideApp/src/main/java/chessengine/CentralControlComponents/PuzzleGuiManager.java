package chessengine.CentralControlComponents;

import chessengine.App;
import chessengine.Audio.Effect;
import chessengine.Enums.MainScreenState;
import chessengine.Graphics.BindingController;
import chessengine.Graphics.MoveArrow;
import chessengine.Puzzle.ChessTactics;
import chessengine.Puzzle.PuzzleEntry;
import chessengine.Puzzle.PuzzleManager;
import chessserver.ChessRepresentations.*;
import chessserver.Functions.AdvancedChessFunctions;
import chessserver.Misc.ChessConstants;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class PuzzleGuiManager implements ResettableGame {
    private static final Logger logger = LogManager.getLogger("Puzzle_GUI_Manager");
    private PuzzleManager puzzleManager;
    private ChessCentralControl centralControl;
    private Slider puzzleEloSlider;
    private Label puzzleElo;
    private Button hintButton;
    private VBox puzzleTagsBox;
    private final NumberFormat format = new DecimalFormat("0");
    public PuzzleGuiManager(ChessCentralControl centralControl,PuzzleManager puzzleManager,Slider puzzleEloSlider,Label puzzleElo,Button nextPuzzleButton,VBox puzzleTagsBox){
        this.puzzleManager = puzzleManager;
        this.centralControl = centralControl;
        this.puzzleEloSlider = puzzleEloSlider;
        this.puzzleEloSlider.setMin(puzzleManager.getMinBin());
        this.puzzleEloSlider.setMax(puzzleManager.getMaxBin());
        this.puzzleEloSlider.setMajorTickUnit(puzzleManager.getBinSize());
        this.puzzleElo = puzzleElo;
        this.hintButton = nextPuzzleButton;
        this.puzzleTagsBox = puzzleTagsBox;


    }

    public void init(){
        BindingController.bindSmallText(puzzleElo);
        puzzleEloSlider.valueProperty().addListener((o,oldValue,newValue) ->{
            this.puzzleElo.setText(format.format(newValue));
        });

        this.hintButton.setOnMouseClicked(e->{
            updateHintState();
        });

        puzzleEloSlider.setValue(Math.min(Math.max(App.userManager.userInfoManager.getUserElo(),puzzleManager.getMinBin()),puzzleManager.getMaxBin()));

        puzzleElo.setText(format.format(puzzleEloSlider.getValue()));
    }

    public void loadInNewPuzzle(){
        PuzzleEntry newPuzzle = puzzleManager.getNewPuzzle((int)puzzleEloSlider.getValue());
        if(newPuzzle == null){
            logger.error("Out of range elo provided! no puzzles found");
            return;
        }
        ChessGame currentPuzzleGame = newPuzzle.getPuzzleGame();
        centralControl.mainScreenController.setupWithGame(currentPuzzleGame, false, MainScreenState.PUZZLE,false);
        puzzleTagsBox.getChildren().clear();
        for(String theme : newPuzzle.getConciseThemes()){
            ChessTactics.Tactic themeDescription = ChessTactics.tactics.get(theme);
            Label themeLabel = new Label(themeDescription.name());
            Tooltip tooltip = new Tooltip(themeDescription.description());
            tooltip.setShowDelay(Duration.millis(10));
            Tooltip.install(themeLabel,tooltip);
            BindingController.bindMediumTextCustom(themeLabel, "-fx-border-width: 2px;-fx-border-radius: 15;-fx-border-style: solid;-fx-border-color: black;-fx-border-insets: 2;-fx-background-color: gray;-fx-background-radius: 15");
            puzzleTagsBox.getChildren().add(themeLabel);
        }
    }


    public void handleMoveCheck(ChessMove move,boolean isDragMove,boolean animateIfPossible) {
        if(centralControl.gameHandler.gameWrapper.getGame() != null){
            ChessPosition nextPos = centralControl.gameHandler.gameWrapper.getGame().getPos(centralControl.gameHandler.gameWrapper.getGame().getCurMoveIndex()+1);
            ChessGameState newState = centralControl.gameHandler.gameWrapper.getGame().getGameState().cloneState();
            ChessPosition newPos = new ChessPosition(centralControl.gameHandler.gameWrapper.getGame().getCurrentPosition(),newState,move);
            boolean isCheckmate = AdvancedChessFunctions.isCheckmated(newPos,newState);
            if(nextPos.getMoveThatCreatedThis().equals(move) || isCheckmate){
                // valid move or a checkmating move. Eg correct choice
                boolean isChecked =  AdvancedChessFunctions.isChecked(!move.isWhite(),newPos.board);
                boolean isGameOver = isCheckmate || centralControl.gameHandler.gameWrapper.getGame().getCurMoveIndex() == centralControl.gameHandler.gameWrapper.getGame().getMaxIndex();
                App.soundPlayer.playMoveEffect(move,isChecked,isGameOver);

                // if is checkmated, there is a possibility of more than one option, and we want to make sure to play the one the user chose
                if(isCheckmate){
                    centralControl.gameHandler.gameWrapper.makeNewMove(move,false,isDragMove,animateIfPossible,true);
                }
                else{
                    // else there can only be one option anyways so follow path
                    centralControl.mainScreenController.moveToNextPuzzleMove(isDragMove);
                }

                Timeline timeline = new Timeline(new KeyFrame(Duration.millis(500),e->{
                    // if its not the last move (eg not checkmate or finished puzzle) then make next move
                    // last move stuff handled in the changetonextmove
                    if(!isCheckmate && !centralControl.gameHandler.gameWrapper.getGame().isAtEndOfGame()){
                        // play the opponent move
                        App.soundPlayer.playEffect(Effect.MOVE);
                        centralControl.mainScreenController.moveToNextPuzzleMove(false);
                    }
                }));
                timeline.setCycleCount(1);
                timeline.play();



            }
            else{
                // invalid choice
                App.soundPlayer.playEffect(Effect.ILLEGALMOVE);
                // if it was a drag move, now we need to undo that piece that graphically moved
                if(isDragMove){
                    centralControl.chessBoardGUIHandler.movePieceOnBoard(move.getNewX(),move.getNewY(),move.getOldX(),move.getOldY(),move.isWhite(),centralControl.gameHandler.gameWrapper.getGame().isWhiteOriented());
                }
                ChessMove guiMove = move.getMoveWhiteOriented(centralControl.gameHandler.gameWrapper.getGame().isWhiteOriented());
                centralControl.chessBoardGUIHandler.blinkSquare(guiMove.getOldX(),guiMove.getOldY(),Duration.millis(500), "red");
            }
        }
        else{
            logger.warn("Trying to call handle move check when currentpuzzlegame is null!");
        }
    }
    // 0 == no hint, 1 = show which piece, 2 = show move
    private int hintState = 0;

    private void updateHintState(){
        hintState = Math.min(2,hintState+1);
        showHintState();
    }

    private void resetHintState(){
        hintState = 0;
        showHintState();
    }
    private MoveArrow shownHintArrow = null;
    private void showHintState(){
        // clear any old hint
        if(shownHintArrow != null){
            centralControl.chessBoardGUIHandler.removeHiglight(shownHintArrow.startX,shownHintArrow.startY);
            centralControl.chessBoardGUIHandler.removeArrow(shownHintArrow);
        }
        if(hintState == 0 || centralControl.gameHandler.gameWrapper.getGame().isAtEndOfGame()){
            return; // nothing to add, so we cleared whatever was there and thats it
        }
        ChessPosition nextPos = centralControl.gameHandler.gameWrapper.getGame().getPos(centralControl.gameHandler.gameWrapper.getGame().getCurMoveIndex()+1);
        ChessMove nextMove = nextPos.getMoveThatCreatedThis().getMoveWhiteOriented(centralControl.gameHandler.gameWrapper.getGame().isWhiteOriented());
        shownHintArrow = new MoveArrow(nextMove, ChessConstants.arrowColor);
        switch (hintState){
            case 2:
                // show move arrow
                centralControl.chessBoardGUIHandler.addArrow(shownHintArrow);
            case 1:
                // no break for case 2 (right above) since i want that to also highlight the square
                // show move start square
                centralControl.chessBoardGUIHandler.highlightSquare(shownHintArrow.startX,shownHintArrow.startY,true);
        }
    }

    @Override
    public void fullReset(boolean isWhiteOriented) {
        puzzleTagsBox.getChildren().clear();
        partialReset(isWhiteOriented);
    }

    @Override
    public void partialReset(boolean isWhiteOriented){
        resetHintState();
    }
}
