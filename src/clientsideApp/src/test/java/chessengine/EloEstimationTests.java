package chessengine;

import chessengine.Misc.EloEstimator;
import chessserver.ComputerDifficulty;
import org.junit.jupiter.api.Test;

public class EloEstimationTests {
    EloEstimator est = new EloEstimator();
//    @Test
    void estimateMaxDifficulty(){
        int estimatedElo = est.testElo(ComputerDifficulty.MaxDifficulty,ComputerDifficulty.STOCKFISHD1,30);
        System.out.println("\n\n\n\n\n\n\n");
        System.out.println("Estimated elo is: " + estimatedElo);

    }
}
