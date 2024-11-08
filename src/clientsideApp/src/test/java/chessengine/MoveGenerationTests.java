package chessengine;

import chessserver.ChessRepresentations.ChessGame;
import chessserver.Functions.AdvancedChessFunctions;
import chessserver.Functions.BitFunctions;
import org.junit.jupiter.api.Test;

public class MoveGenerationTests {
    @Test void fixThisCheckmate(){
        ChessGame game = ChessGame.createTestGame("1.Nc3 Nc6 2.Ne4 Nf6 3.Nxf6");
        game.moveToEndOfGame();
        System.out.println(BitFunctions.getBitStr(game.getCurrentPosition().board.getWhiteAttackTableCombined()));
        System.out.println(BitFunctions.getBitStr(AdvancedChessFunctions.getCheckedFileMask(false,game.getCurrentPosition().board)));
    }
}
