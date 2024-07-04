package chessengine;

import org.junit.jupiter.api.Test;

public class ComputerTests {
    @Test void getFullEvalTests(){
        // not using eval depth
        Computer c = new Computer(5);

        ChessGame equalGame = new ChessGame("1.e4 e5","",false);
        System.out.println(c.getFullEval(equalGame.currentPosition.board, equalGame.gameStates,false,false));

    }



    @Test void getFullEvalMinimaxTests(){
        // not using eval depth
        Computer c = new Computer(5);

        ChessGame equalGame = new ChessGame("1.e4 e5 2.Nf3 Nc6 3.Bb5 a6 4.Ba4 Nf6 5.O-O Nxe4 6.d4 b5 7.Bb3 d5 8.dxe5 Be6 9.c3 Be7 10.Nbd2 O-O 11.Qe2 Nc5 12.Bc2 d4 13.Ne4 d3 14.Qe3 dxc2 15.Nxc5 Bxc5 16.Qxc5 Qd3 17.Qxc6 Bd5 18.Qxc7 Bxf3 19.gxf3 Qg6+ 20.Kh1 Qd3 21.Be3 Rad8 22.Qc6 Qd1 23.Raxd1 cxd1=Q 24.Rxd1 Rxd1+ 25.Kg2 Rfd8 26.Qxa6 h6 27.Qxb5 R8d5 28.Qe8+ Kh7 29.e6 fxe6 30.Qxe6 Rd6 31.Qe4+ Rg6+","",false);
        equalGame.moveToEndOfGame();
        System.out.println(c.getFullEvalMinimax(equalGame.currentPosition, equalGame.gameStates,5,false));

    }
}
