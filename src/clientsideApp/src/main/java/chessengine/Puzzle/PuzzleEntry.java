package chessengine.Puzzle;

import chessserver.ChessRepresentations.ChessGame;
import chessserver.ChessRepresentations.ChessPosition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class PuzzleEntry {
    private static final Logger logger = LogManager.getLogger("Puzzle_Entry");

    @Override
    public String toString() {
        return "PuzzleEntry{" +
                "puzzleId='" + puzzleId + '\'' +
                ", puzzleStartFen='" + puzzleStartFen + '\'' +
                ", puzzleMovesUCI=" + Arrays.toString(puzzleMovesUCI) +
                ", puzzleRating=" + puzzleRating +
                ", puzzleRatingDev=" + puzzleRatingDev +
                ", popularity=" + popularity +
                ", numPlays=" + numPlays +
                ", conciseThemes=" + Arrays.toString(conciseThemes) +
                ", gameUrl='" + gameUrl + '\'' +
                ", openingTags=" + Arrays.toString(openingTags) +
                '}';
    }

    String puzzleId;
    String puzzleStartFen;
    String[] puzzleMovesUCI;
    int puzzleRating;

    public String getPuzzleId() {
        return puzzleId;
    }

    public void setPuzzleId(String puzzleId) {
        this.puzzleId = puzzleId;
    }

    public String getPuzzleStartFen() {
        return puzzleStartFen;
    }

    public void setPuzzleStartFen(String puzzleStartFen) {
        this.puzzleStartFen = puzzleStartFen;
    }

    public String[] getPuzzleMovesUCI() {
        return puzzleMovesUCI;
    }

    public void setPuzzleMovesUCI(String[] puzzleMovesUCI) {
        this.puzzleMovesUCI = puzzleMovesUCI;
    }

    public int getPuzzleRating() {
        return puzzleRating;
    }

    public void setPuzzleRating(int puzzleRating) {
        this.puzzleRating = puzzleRating;
    }

    public int getPuzzleRatingDev() {
        return puzzleRatingDev;
    }

    public void setPuzzleRatingDev(int puzzleRatingDev) {
        this.puzzleRatingDev = puzzleRatingDev;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public int getNumPlays() {
        return numPlays;
    }

    public void setNumPlays(int numPlays) {
        this.numPlays = numPlays;
    }

    public String[] getConciseThemes() {
        return conciseThemes;
    }

    public void setConciseThemes(String[] conciseThemes) {
        this.conciseThemes = conciseThemes;
    }

    public String getGameUrl() {
        return gameUrl;
    }

    public void setGameUrl(String gameUrl) {
        this.gameUrl = gameUrl;
    }

    public String[] getOpeningTags() {
        return openingTags;
    }

    public void setOpeningTags(String[] openingTags) {
        this.openingTags = openingTags;
    }

    int puzzleRatingDev;
    int popularity;
    int numPlays;
    String[] conciseThemes;
    String gameUrl;
    String[] openingTags;
    private PuzzleEntry(){

    }
    public static PuzzleEntry getFromCSV(String csvLine){
        PuzzleEntry entry = new PuzzleEntry();
        String[] split = csvLine.split(",");
        if(split.length < 9){
            logger.warn("Invalid puzzle");
            return null;
        }

        entry.puzzleId = split[0];
        entry.puzzleStartFen = split[1];
        entry.puzzleMovesUCI = split[2].split(" ");
        entry.puzzleRating = Integer.parseInt(split[3]);
        entry.puzzleRatingDev = Integer.parseInt(split[4]);
        entry.popularity = Integer.parseInt(split[5]);
        entry.numPlays = Integer.parseInt(split[6]);
        entry.conciseThemes = split[7].split(" ");
        entry.gameUrl = split[8];
        if(split.length > 9){
            entry.openingTags = split[9].split(" ");
        }

        return entry;
    }

    public ChessGame getPuzzleGame(){
        return ChessGame.getPuzzleGame(this.puzzleStartFen,this.puzzleMovesUCI);
    }
}
