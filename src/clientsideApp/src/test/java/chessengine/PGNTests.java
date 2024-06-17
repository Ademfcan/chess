package chessengine;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PGNTests {
    @Test
    void pgnCreationTest() {
        String pgn =
                "1.e4 c5 2.Nf3 a6 3.d3 g6 4.g3 Bg7 5.Bg2 b5 6.O-O Bb7 7.c3 e5 8.a3 Ne7 9.b4 d6\n" +
                "10.Nbd2 O-O 11.Nb3 Nd7 12.Be3 Rc8 13.Rc1 h6 14.Nfd2 f5 15.f4 Kh7 16.Qe2 cxb4\n" +
                "17.axb4 exf4 18.Bxf4 Rxc3 19.Rxc3 Bxc3 20.Bxd6 Qb6+ 21.Bc5 Nxc5 22.bxc5 Qe6\n" +
                "23.d4 Rd8 24.Qd3 Bxd2 25.Nxd2 fxe4 26.Nxe4 Nf5 27.d5 Qe5 28.g4 Ne7 29.Rf7+ Kg8\n" +
                "30.Qf1 Nxd5 31.Rxb7 Qd4+ 32.Kh1 Rf8 33.Qg1 Ne3 34.Re7 a5 35.c6 a4 36.Qxe3 Qxe3\n" +
                "37.Nf6+ Rxf6 38.Rxe3 Rd6 39.h4 Rd1+ 40.Kh2 b4 41.c7";

        String secondPgn = "1.Nf3 Nf6 2.c4 g6 3.Nc3 d5 4.cxd5 Nxd5 5.g3 Bg7 6.Nxd5 Qxd5 7.Bg2 O-O 8.O-O Nc6\n" +
                "9.d3 Qd8 10.a3 e5 11.Bg5 Qd6 12.Qc2 Bg4 13.Be3 Rfe8 14.Rac1 Rac8 15.Rfe1 Ne7\n" +
                "16.Ng5 Nd5 17.Qb3 c6 18.Bxa7 Qe7 19.h4 h6 20.Bc5 Qd7 21.Ne4 b6 22.Bb4 Be6\n" +
                "23.Qa4 Red8 24.Bd2 f5 25.Nc3 Ne7 26.Red1 Kh7 27.Be3 Rb8 28.b4 Ra8 29.Qc2 Rxa3\n" +
                "30.Bxb6 Rb8 31.Bc5 Nd5 32.Nxd5 cxd5 33.Ra1 Raa8 34.Rxa8 Rxa8 35.d4 f4 36.dxe5 fxg3\n" +
                "37.fxg3 Bxe5 38.h5 Qf7 39.hxg6+ Qxg6 40.Qxg6+ Kxg6 41.Bxd5 Bxd5 42.Rxd5 Bxg3\n" +
                "43.Kg2 Bf4 44.b5 Ra2 45.Kf3 Bh2 46.b6 Rb2 47.Be3 Rb4 48.Rd8 Rb5 49.Rh8 h5\n" +
                "50.Kg2 Be5 51.Rh6+ Kf5 52.Rxh5+ Ke4 53.Bf2 Rb2 54.Rh4+ Kd5 55.e4+ Kc4 56.Kf3 Rb3+\n" +
                "57.Kg4 Rb5 58.Rh6 Bc3 59.Rc6+ Kd3 60.Kf3 Rb1 61.Rd6+ Kc4 62.Rd7 Kb5 63.Rc7 Ba5\n"+
                "64.Kg4 Rb2 65.Bd4 Rb4 66.Rd7 Kc6 67.Rd8 Bxb6 68.Bxb6 Rxe4+ 69.Kf3 Re7 70.Be3 Kc7\n" +
                "71.Ra8 Re6 72.Ke2 Kc6 73.Kd3 Rd6+ 74.Kc4 Rd1 75.Ra6+ Kb7 76.Rf6 Re1 77.Bc5 Rc1+\n" +
                "78.Kb5 Rb1+ 79.Bb4 Rc1 80.Bc5 Rb1+ 81.Kc4 Rh1 82.Rb6+ Kc7 83.Ra6 Kb7 84.Re6 Rd1\n" +
                "85.Kb5 Rb1+ 86.Bb4 Rc1 87.Ra6 Rc2 88.Ra1 Rh2 89.Bc5 Rb2+ 90.Kc4 Rh2 91.Ra7+ Kc6\n" +
                "92.Ra6+ Kb7 93.Rb6+ Kc7 94.Rg6 Rd2 95.Bd4 Kb7 96.Kd5 Rc2 97.Rb6+ Kc7 98.Ra6 Kd7\n" +
                "99.Rg6 Re2 100.Rd6+ Ke7 101.Rg6 Kd7 102.Rh6 Rc2 103.Rd6+ Kc7 104.Rg6 Kb7\n" +
                "105.Bc5 Rd2+ 106.Ke5 Rd1 107.Rg7+ Ka6 108.Bd4 Rc1 109.Kd5 Rc8 110.Ra7+ Kb5\n" +
                "111.Rb7+ Ka6 112.Rb6+ Ka5 113.Bc5 Rd8+ 114.Kc4 Rd1 115.Rb2 Rc1+ 116.Kd5 Ka4\n" +
                "117.Rb4+ Ka5 118.Kc6 Rc3 119.Rb8 Ka4 120.Rb7 Rc2 121.Kd5 Rd2+ 122.Bd4 Rc2\n" +
                "123.Ke4 Ka5 124.Rb6 Rc4 125.Rb2 Rc1 126.Kd5 Ka4 127.Bc5 Ka5 128.Kc6 Rc4 129.Rb3 Rg4\n" +
                "130.Ra3+ Ra4 131.Rd3 Rg4 132.Kd5 Kb5 133.Rb3+ Ka4 134.Rb2 Rh4 135.Bd4 Ka3\n" +
                "136.Rb7 Ka2 137.Kc4 Rg4 138.Rb2+ Ka3 139.Rb1 Rg2 140.Rb7 ";

        String pgn3 = "1.e4 e5 2.Nf3 Nc6 3.Bb5 Nf6 4.O-O Nxe4 5.Re1 Nd6 6.Nxe5 Be7 7.Bf1 Nxe5 8.Rxe5 O-O\n" +
                "9.d4 Bf6 10.Re1 Re8 11.c3 Rxe1 12.Qxe1 Qe8 13.Qxe8+ Nxe8 14.Bf4 d5 15.Bd3 Nd6\n" +
                "16.Nd2 Bg4 17.a4 a5 18.f3 Bh5 19.Kf2 Re8 20.Nb3 b6 21.Bxd6 cxd6 22.f4 Bg4\n" +
                "23.Nd2 Bd7 24.Nf1 g5 25.f5 Rb8 26.Ne3 Bc6 27.Bb5 Ba8 28.Be2 Kg7 29.Ra3 Bc6\n" +
                "30.h3 Bd8 31.g4 Bf6 32.Nc2 Bd8 33.b4 b5 34.axb5 axb4 35.Nxb4 Bxb5 36.Nxd5 Bc6\n" +
                "37.Nb4 Be8 38.Ra6 Bc7 39.Nd5 Rc8 40.f6+ Kf8 41.Nxc7 Rxc7 42.Ra8 Rb7 43.d5 Rb2\n" +
                "44.Ke3";
        ChessGame game = new ChessGame(pgn,"test",false);
        ChessGame game2 = new ChessGame(secondPgn,"test",false);
        ChessGame game3 = new ChessGame(pgn3,"test",false);
        System.out.println(game.maxIndex);
        System.out.println(game2.maxIndex);
        System.out.println(game3.maxIndex);

    }

    @Test void pgnConsistencyTest(){
//        String pgn = "1.e4 e5 2.Nf3 Nc6 3.Bb5 Nf6 4.O-O Nxe4 5.Re1 Nd6 6.Nxe5 Be7 7.Bf1 Nxe5 8.Rxe5 O-O \n" +
//                "9.d4 Bf6 10.Re1 Re8 11.c3 Rxe1 12.Qxe1 Qe8 13.Qxe8+ Nxe8 14.Bf4 d5 15.Bd3 Nd6 \n" +
//                "16.Nd2 Bg4 17.a4 a5 18.f3 Bh5 19.Kf2 Re8 20.Nb3 b6 21.Bxd6 cxd6 22.f4 Bg4 \n" +
//                "23.Nd2 Bd7 24.Nf1 g5 25.f5 Rb8 26.Ne3 Bc6 27.Bb5 Ba8 28.Be2 Kg7 29.Ra3 Bc6 \n" +
//                "30.h3 Bd8 31.g4 Bf6 32.Nc2 Bd8 33.b4 b5 34.axb5 axb4 35.Nxb4 Bxb5 36.Nxd5 Bc6 \n" +
//                "37.Nb4 Be8 38.Ra6 Bc7 39.Nd5 Rc8 40.f6+ Kf8 41.Nxc7 Rxc7 42.Ra8 Rb7 43.d5 Rb2 \n" +
//                "44.Ke3 ";
//        ChessGame game = new ChessGame(pgn);
//        System.out.println(game.gameToPgn());
//        String pgnOut = game.gameToPgn();
//        System.out.println(normalizeString(pgn));
//        System.out.println(normalizeString(pgnOut));
//        String thirdPgn = "1.Nf3 Nf6 2.c4 g6 3.Nc3 d5 4.cxd5 Nxd5 5.g3 Bg7 6.Nxd5 Qxd5 7.Bg2 O-O 8.O-O Nc6 \n" +
//                "9.d3 Qd8 10.a3 e5 11.Bg5 Qd6 12.Qc2 Bg4 13.Be3 Rfe8 14.Rac1 Rac8 15.Rfe1 Ne7 \n" +
//                "16.Ng5 Nd5 17.Qb3 c6 18.Bxa7 Qe7 19.h4 h6 20.Bc5 Qd7 21.Ne4 b6 22.Bb4 Be6 \n" +
//                "23.Qa4 Red8 24.Bd2 f5 25.Nc3 Ne7 26.Red1 Kh7 27.Be3 Rb8 28.b4 Ra8 29.Qc2 Rxa3 \n" +
//                "30.Bxb6 Rb8 31.Bc5 Nd5 32.Nxd5 cxd5 33.Ra1 Raa8 34.Rxa8 Rxa8 35.d4 f4 36.dxe5 fxg3 \n" +
//                "37.fxg3 Bxe5 38.h5 Qf7 39.hxg6+ Qxg6 40.Qxg6+ Kxg6 41.Bxd5 Bxd5 42.Rxd5 Bxg3 \n" +
//                "43.Kg2 Bf4 44.b5 Ra2 45.Kf3 Bh2 46.b6 Rb2 47.Be3 Rb4 48.Rd8 Rb5 49.Rh8 h5 \n" +
//                "50.Kg2 Be5 51.Rh6+ Kf5 52.Rxh5+ Ke4 53.Bf2 Rb2 54.Rh4+ Kd5 55.e4+ Kc4 56.Kf3 Rb3+ \n" +
//                "57.Kg4 Rb5 58.Rh6 Bc3 59.Rc6+ Kd3 60.Kf3 Rb1 61.Rd6+ Kc4 62.Rd7 Kb5 63.Rc7 Ba5 \n" +
//                "64.Kg4 Rb2 65.Bd4 Rb4 66.Rd7 Kc6 67.Rd8 Bxb6 68.Bxb6 Rxe4+ 69.Kf3 Re7 70.Be3 Kc7 \n" +
//                "71.Ra8 Re6 72.Ke2 Kc6 73.Kd3 Rd6+ 74.Kc4 Rd1 75.Ra6+ Kb7 76.Rf6 Re1 77.Bc5 Rc1+ \n" +
//                "78.Kb5 Rb1+ 79.Bb4 Rc1 80.Bc5 Rb1+ 81.Kc4 Rh1 82.Rb6+ Kc7 83.Ra6 Kb7 84.Re6 Rd1 \n" +
//                "85.Kb5 Rb1+ 86.Bb4 Rc1 87.Ra6 Rc2 88.Ra1 Rh2 89.Bc5 Rb2+ 90.Kc4 Rh2 91.Ra7+ Kc6 \n" +
//                "92.Ra6+ Kb7 93.Rb6+ Kc7 94.Rg6 Rd2 95.Bd4 Kb7 96.Kd5 Rc2 97.Rb6+ Kc7 98.Ra6 Kd7 \n" +
//                "99.Rg6 Re2 100.Rd6+ Ke7 101.Rg6 Kd7 102.Rh6 Rc2 103.Rd6+ Kc7 104.Rg6 Kb7 \n" +
//                "105.Bc5 Rd2+ 106.Ke5 Rd1 107.Rg7+ Ka6 108.Bd4 Rc1 109.Kd5 Rc8 110.Ra7+ Kb5 \n" +
//                "111.Rb7+ Ka6 112.Rb6+ Ka5 113.Bc5 Rd8+ 114.Kc4 Rd1 115.Rb2 Rc1+ 116.Kd5 Ka4 \n" +
//                "117.Rb4+ Ka5 118.Kc6 Rc3 119.Rb8 Ka4 120.Rb7 Rc2 121.Kd5 Rd2+ 122.Bd4 Rc2 \n" +
//                "123.Ke4 Ka5 124.Rb6 Rc4 125.Rb2 Rc1 126.Kd5 Ka4 127.Bc5 Ka5 128.Kc6 Rc4 129.Rb3 Rg4 \n" +
//                "130.Ra3+ Ra4 131.Rd3 Rg4 132.Kd5 Kb5 133.Rb3+ Ka4 134.Rb2 Rh4 135.Bd4 Ka3 \n" +
//                "136.Rb7 Ka2 137.Kc4 Rg4 138.Rb2+ Ka3 139.Rb1 Rg2 140.Rb7 ";
//
//        ChessGame game2 = new ChessGame(secondPgn);
//        for(int i = 0;i<game.maxIndex+1;i++){
//            System.out.println(game.getPos(i).getMoveThatCreatedThis().toString());
//        }
//        System.out.println(game2.gameToPgn());
//        String pgnOut2 = game2.gameToPgn();
//        System.out.println(normalizeString(secondPgn));
//        System.out.println(normalizeString(pgnOut2));
//        System.out.println(findDifferenceBetweenStrings(normalizeString(secondPgn),normalizeString(pgnOut2)));
//        Assertions.assertEquals(normalizeString(secondPgn),normalizeString(pgnOut2));
        String thirdPgn = "1.e4 d5 2.Nf3 dxe4 3.Nd4 e3 4.fxe3 e5 5.Nb3 Bb4 6.c3 Nf6 7.cxb4 Bg4 8." + "Be2 Bxe2 9.Qxe2 Ne4 10.O-O O-O 11.d3 Nc5 12.bxc5 c6 13.d4 Nd7 14.dxe5 Nxe5 " +
                "15.N1d2 Qd7 16.e4 Rfd8 17.Nc4 b6 18.Nxe5 bxc5 19.Nxd7 Rxd7 20.Rd1 Rb8 21." +
                "Rxd7 g6 22.Bg5 Kg7 23.Qc2 Re8 24.Qc3+ Kg8 25.Rad1 Rxe4 26.Rd8+ Re8 27." +
                "Rxe8#";

        ChessGame game3 = new ChessGame(thirdPgn,"test",false);
        boolean isCheckmated = AdvancedChessFunctions.isCheckmated(game3.currentPosition.board,game3.gameStates);
        System.out.println("Is checkmated: " + isCheckmated);
    }


    private String normalizeString(String input) {
        // Remove leading and trailing whitespace characters
        String trimmed = input.trim();
        // Remove internal whitespace characters
        String withoutWhitespace = trimmed.replaceAll("\\s+", "");
        // Remove newline characters
        String normalized = withoutWhitespace.replaceAll("\\n", "");
        return normalized;
    }

    private List<Character> findDifferenceBetweenStrings(String expected, String actual){
        List<Character> diffs = new ArrayList<>();
        int i = 0;
        int j = 0;
        while(i < expected.length() && j < actual.length()){
            char eC = expected.charAt(i);
            char aC = actual.charAt(j);
            if(eC != aC){
                diffs.add(aC);
            }
            i++;
            j++;

        }

        return diffs;
    }
}
