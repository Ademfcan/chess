package chessengine;

import chessserver.ChessRepresentations.ChessGame;
import chessserver.Functions.AdvancedChessFunctions;
import chessserver.Functions.BitFunctions;
import chessserver.Functions.GeneralChessFunctions;
import chessserver.Misc.ChessConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdvancedChessFunctionsTests {
    @Test void isCheckmatedTest() {
        ChessGame scholarsMate = ChessGame.createTestGame("1.e4 e5 2.Qh5 Nc6 3.Bc4 Nf6 4.Qxf7#");
        boolean isCheckmated = AdvancedChessFunctions.isCheckmated(scholarsMate.getPos(scholarsMate.getMaxIndex()), scholarsMate.getGameState());
        Assertions.assertTrue(isCheckmated);
        System.out.println("pgnout" + scholarsMate.gameToPgn());

        ChessGame foolsMate = ChessGame.createTestGame("1.f3 e5\n2.g4 Qh4#");
        boolean isCheckmated2 = AdvancedChessFunctions.isCheckmated(foolsMate.getPos(foolsMate.getMaxIndex()), foolsMate.getGameState());
        Assertions.assertTrue(isCheckmated2);
        System.out.println("pgnout" + foolsMate.gameToPgn());

        ChessGame SmotheredMate = ChessGame.createTestGame("1.e4 e5\n2.Nf3 Nc6\n3.Nc3 Nf6\n4.Nxe5 Nxe5\n5.d4 Nc6\n6.d5 Ne5\n7.f4 Ng6\n8.e5 Ng8\n9.d6 cxd6\n10.exd6 Qf6\n11.Nb5 Kd8\n12.Be3 b6\n13.Qd5 Rb8\n14.O-O-O Bb7\n15.Qd2 a6\n16.Bxb6+ Kc8\n17.Na7#");
        boolean isCheckmated3 = AdvancedChessFunctions.isCheckmated(SmotheredMate.getPos(SmotheredMate.getMaxIndex()), SmotheredMate.getGameState());
        Assertions.assertTrue(isCheckmated3);
        System.out.println("pgnout" + SmotheredMate.gameToPgn());

        ChessGame BackrankMate = ChessGame.createTestGame("1.e4 e5\n2.Nf3 Nc6\n3.Bb5 a6\n4.Ba4 d6\n5.O-O Bg4\n6.d4 b5\n7.Bb3 Nxd4\n8.Nxe5 Bxd1\n9.Bxf7+ Ke7\n10.Bg5+ Nf6\n11.Nc3 dxe5\n12.Raxd1 Kxf7\n13.f4 Bc5\n14.Kh1 h6\n15.Bh4 g5\n16.fxg5 hxg5\n17.Bxg5 Be7\n18.Nd5 Rxh2+\n19.Kxh2 Qh8+\n20.Kg1 Ne2+\n21.Kf2 Nf4\n22.Nxe7 Nxe4+\n23.Ke3 Nxg5\n24.Nd5 Nxg2+\n25.Kd3+ Nf4+\n26.Nxf4 exf4\n27.Rxf4+ Kg6\n28.Rg1 Rd8+\n29.Ke3 Qe5+\n30.Re4 Qxe4+\n31.Kf2 Rd2+\n32.Kg3 Qf3+\n33.Kh4 Rh2#");
        boolean isCheckmated4 = AdvancedChessFunctions.isCheckmated(BackrankMate.getPos(BackrankMate.getMaxIndex()), BackrankMate.getGameState());
        Assertions.assertTrue(isCheckmated4);
        System.out.println("pgnout" + BackrankMate.gameToPgn());

        ChessGame AnastasiasMate = ChessGame.createTestGame("1.e4 e5\n2.Nf3 Nc6\n3.Bc4 d6\n4.Nc3 Bg4\n5.d3 Nd4\n6.Nxe5 Bxd1\n7.Bxf7+ Ke7\n8.Nd5#");
        boolean isCheckmated5 = AdvancedChessFunctions.isCheckmated(AnastasiasMate.getPos(AnastasiasMate.getMaxIndex()), AnastasiasMate.getGameState());
        Assertions.assertTrue(isCheckmated5);
        System.out.println("pgnout" + AnastasiasMate.gameToPgn());

    }

    @Test void getMinMaxAttackerTest(){
        ChessGame game = ChessGame.createTestGame("1.e4 f6 2.Nf3 Nh6 3.Nd4 Ng4 4.Qf3 d5 5.c3 Qd6");
        game.moveToEndOfGame();
        Assertions.assertEquals(ChessConstants.valueMap[ChessConstants.QUEENINDEX],AdvancedChessFunctions.getMaxAttacker(4,3,true,game.getCurrentPosition().board));
        Assertions.assertEquals(ChessConstants.valueMap[ChessConstants.PAWNINDEX],AdvancedChessFunctions.getMinAttacker(4,3,true,game.getCurrentPosition().board));

    }

    @Test void pawnPromotionCheck(){

    }

    @Test void willResultInDeadChecks(){
        ChessGame game = ChessGame.createTestGame("1.e4 d5 2.Bb5+ c6 3.Ba4 Na6 4.b3 ");
        game.moveToEndOfGame();
        // todo
//        System.out.println(AdvancedChessFunctions.willResultInDead());
    }

    @Test void checkedFileTests(){
        String[] pgns = {
                // 1. Simple Check by a Rook
//                "1.e4 d5 2.Bb5",
                "1.e4 h6 2.e5 Na6 3.e6 dxe6 4.Qh5 Qxd2+"
        };
        for(String pgn : pgns){
            ChessGame game = ChessGame.createTestGame(pgn);
            game.moveToEndOfGame();
            GeneralChessFunctions.printBoardDetailed(game.getCurrentPosition().board);
            System.out.println(AdvancedChessFunctions.createPinMask(2,1, game.isWhiteTurn(),game.getCurrentPosition().board));
            System.out.println(BitFunctions.getBitStr(AdvancedChessFunctions.getCheckedFileMask(game.isWhiteTurn(),game.getCurrentPosition().board)));

        }

    }


}
