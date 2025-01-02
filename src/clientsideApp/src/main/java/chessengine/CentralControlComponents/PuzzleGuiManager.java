package chessengine.CentralControlComponents;

import chessengine.App;
import chessengine.Audio.Effect;
import chessengine.Enums.MainScreenState;
import chessengine.Enums.Window;
import chessengine.Puzzle.ChessTactics;
import chessengine.Puzzle.PuzzleEntry;
import chessengine.Puzzle.PuzzleManager;
import chessserver.ChessRepresentations.*;
import chessserver.Functions.AdvancedChessFunctions;
import chessserver.Functions.GeneralChessFunctions;
import chessserver.Misc.ChessConstants;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.text.NumberFormatter;
import java.io.BufferedReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class PuzzleGuiManager {
    private static final Logger logger = LogManager.getLogger("Puzzle_GUI_Manager");
    private PuzzleManager puzzleManager;
    private ChessCentralControl centralControl;
    private Slider puzzleEloSlider;
    private Label puzzleElo;
    private Button nextPuzzleButton;
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
        this.nextPuzzleButton = nextPuzzleButton;
        this.puzzleTagsBox = puzzleTagsBox;


    }

    public void init(){
        App.bindingController.bindSmallText(puzzleElo, Window.Main);
        puzzleEloSlider.valueProperty().addListener((o,oldValue,newValue) ->{
            this.puzzleElo.setText(format.format(newValue));
        });

        this.nextPuzzleButton.setOnMouseClicked(e->{
            loadInNewPuzzle();
        });

        puzzleEloSlider.setValue(Math.min(Math.max(App.userManager.getUserElo(),puzzleManager.getMinBin()),puzzleManager.getMaxBin()));

        puzzleElo.setText(format.format(puzzleEloSlider.getValue()));
    }

    public void loadInNewPuzzle(){
        PuzzleEntry newPuzzle = puzzleManager.getNewPuzzle((int)puzzleEloSlider.getValue());
        if(newPuzzle == null){
            logger.error("Out of range elo provided! no puzzles found");
            return;
        }
        puzzleTagsBox.getChildren().clear();
        for(String theme : newPuzzle.getConciseThemes()){
            ChessTactics.Tactic themeDescription = ChessTactics.tactics.get(theme);
            Label themeLabel = new Label(themeDescription.name());
            Tooltip tooltip = new Tooltip(themeDescription.description());
            Tooltip.install(themeLabel,tooltip);
            App.bindingController.bindSmallText(themeLabel,Window.Main,"Black");
            puzzleTagsBox.getChildren().add(themeLabel);
        }
        ChessGame currentPuzzleGame = newPuzzle.getPuzzleGame();
        centralControl.mainScreenController.setupWithGame(currentPuzzleGame, MainScreenState.PUZZLE,false);
    }


    public void handleMoveCheck(ChessMove move) {
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
                centralControl.mainScreenController.moveToNextPuzzleMove();


                Timeline timeline = new Timeline(new KeyFrame(Duration.millis(500),e->{
                    // if its the last move (eg checkmate or finished puzzle) then move on to another puzzle
                    if(isCheckmate || centralControl.gameHandler.gameWrapper.getGame().getCurMoveIndex() == centralControl.gameHandler.gameWrapper.getGame().getMaxIndex()){
                        // puzzle finished
                        loadInNewPuzzle();
                    }
                    else{
                        // else play the opponent move
                        App.soundPlayer.playEffect(Effect.MOVE);
                        centralControl.mainScreenController.moveToNextPuzzleMove();
                    }
                }));
                timeline.setCycleCount(1);
                timeline.play();



            }
            else{
                // invalid choice
                App.soundPlayer.playEffect(Effect.ILLEGALMOVE);
            }
        }
        else{
            logger.warn("Trying to call handle move check when currentpuzzlegame is null!");
        }
    }
}
