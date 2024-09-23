package chessengine;

import chessengine.ChessRepresentations.ChessGame;
import chessengine.Functions.AdvancedChessFunctions;
import chessengine.Functions.BitFunctions;
import org.junit.jupiter.api.Test;

public class MoveGenerationTests {
    @Test void fixThisCheckmate(){
        ChessGame game = ChessGame.createTestGame("1.Nc3 Nc6 2.Ne4 Nf6 3.Nxf6",false);
        game.moveToEndOfGame(false);
        System.out.println(BitFunctions.getBitStr(game.currentPosition.board.getWhiteAttackTableCombined()));
        System.out.println(BitFunctions.getBitStr(AdvancedChessFunctions.getCheckedFileMask(false,game.currentPosition.board)));
    }
}
