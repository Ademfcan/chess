package chessengine.Computation;

import chessengine.App;
import chessengine.Records.SearchResult;
import chessserver.ChessRepresentations.BackendChessPosition;
import chessserver.ChessRepresentations.ChessMove;
import chessserver.Enums.ComputerDifficulty;
import chessserver.Functions.PgnFunctions;
import chessserver.Misc.ChessConstants;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Computer {
    private static final Logger logger = LogManager.getLogger(Computer.class);
    private final Searcher searcher;
    private final CustomMultiSearcher multiSearcher;
    private final Stockfish stockfish;
    public Computer() {
        this(new Stockfish());
    }

    public Computer(Stockfish stockfish){
        this.searcher = new Searcher();
        this.multiSearcher = new CustomMultiSearcher();
        this.stockfish = stockfish;

    }

    public ChessMove getMove(ComputerDifficulty difficulty, BackendChessPosition currentPosition) {
        logger.info("Starting a best move evaluation");

        if (difficulty.isStockfishBased) {
            logger.debug("Getting stockfish move");
            String moveUci = stockfish.getBestMove(PgnFunctions.positionToFEN(currentPosition, currentPosition.gameState, currentPosition.isWhiteTurn), difficulty.stockfishElo, ChessConstants.DefaultWaitTime);
            return PgnFunctions.uciToChessMove(moveUci, currentPosition.isWhiteTurn, currentPosition.board);

        } else if (difficulty == ComputerDifficulty.MaxDifficulty) {
            SearchResult searchResult = searcher.search(currentPosition.toBackend(currentPosition.gameState, currentPosition.isWhiteTurn), ChessConstants.DefaultWaitTime);
            if (searchResult != null) {
                return searchResult.move();
            }
        } else {
            return multiSearcher.search(currentPosition, ChessConstants.DefaultWaitTime, 1, difficulty).results()[0].move();
        }

        return null;
    }
}
