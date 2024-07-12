package chessengine;

import chessserver.ProfilePicture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ComputerTests {
    @Test void getFullEvalTests(){
        // not using eval depth
        Computer c = new Computer(5);

        ChessGame equalGame = new ChessGame("1.e4 e5","","test",0, ProfilePicture.DEFAULT.urlString,false);
        System.out.println(c.getFullEval(equalGame.currentPosition, equalGame.gameStates,false,false));

    }



    @Test
    void evaluateGamesWithStockfish() {
        // Array of PGNs (example games or real games)
        String[] pgns = {
                // Good test cases
                "1.e4 e5 2.Nf3 Nc6 3.Bb5 a6 4.Ba4 Nf6 5.O-O Nxe4", // Ruy Lopez, Berlin Defense
                "1.d4 d5 2.c4 e6 3.Nc3 Nf6 4.Bg5 Be7", // Queen's Gambit Declined
                "1.e4 e5 2.Nf3 Nc6 3.Bb5 f5", // Schliemann Defense
                // Common openings
                "1.e4 e5 2.Nf3 Nc6 3.Bb5 a6 4.Bxc6 dxc6", // Ruy Lopez, Exchange Variation
                "1.d4 d5 2.c4 e6 3.Nc3 Nf6 4.Bf4", // Queen's Gambit Declined, Exchange Variation
                "1.e4 c5 2.Nf3 Nc6 3.d4 cxd4 4.Nxd4 g6", // Sicilian Defense, Accelerated Dragon
                // Edge cases
                "1.e4 e5 2.Nf3 Nc6 3.Bb5 Qe7", // Uncommon move order in Ruy Lopez
                "1.d4 Nf6 2.c4 c5 3.d5 b5", // Benko Gambit
                "1.e4 d6 2.d4 Nf6 3.Nc3 g6 4.Bg5 Bg7 5.Qd2 h6 6.Bf4 g5 7.Be3 Ng4" // Pirc Defense, Byrne Variation
        };

        Stockfish stockfish = new Stockfish();
        Computer computer = new Computer(6); // Example depth for minimax evaluation

        if (stockfish.startEngine()) {
            for (String pgn : pgns) {
                ChessGame game = new ChessGame(pgn, "","test",0, ProfilePicture.DEFAULT.urlString, false); // Create game from PGN
                game.moveToEndOfGame();
                String fen = PgnFunctions.positionToFEN(game.currentPosition, game.gameStates, game.isPlayer1Turn(), 0, game.curMoveIndex/2);

                // Calculate evaluation with Computer
                float computerEval = (float) computer.getFullEvalMinimax(game.currentPosition, game.gameStates, 5, false).getAdvantage();

                // Calculate evaluation with Stockfish
                float stockfishEval = stockfish.getEvalScore(fen, 1000); // 1000 milliseconds time limit

                // Compare evaluations within a percentage difference (10%)
                float tolerance = 0.1f; // 10%
                float difference = Math.abs(computerEval - stockfishEval);
                float average = (computerEval + stockfishEval) / 2;
                float percentageDifference = (difference / average) * 100;

                System.out.println("PGN: " + pgn);
                System.out.println("FEN: " + fen);
                System.out.println("Computer Eval: " + computerEval);
                System.out.println("Stockfish Eval: " + stockfishEval);
                System.out.println("Percentage Difference: " + percentageDifference + "%");

                // Assert that the percentage difference is within tolerance
//                Assertions.assertTrue(percentageDifference <= tolerance * 100);
            }

            stockfish.stopEngine();
        } else {
            Assertions.fail("Failed to start Stockfish engine");
        }
    }

}
