package chessengine;

import chessengine.ChessRepresentations.ChessGame;
import chessengine.ChessRepresentations.XYcoord;
import chessengine.Computation.MagicBitboardGenerator;
import chessengine.Functions.AdvancedChessFunctions;
import chessengine.Functions.BitFunctions;
import chessengine.Functions.GeneralChessFunctions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

public class MagicBitboardTests {
    @Test
    void  MagicRookGenerationTest(){
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


        for (String pgn : pgns) {
            MagicBitboardGenerator m = new MagicBitboardGenerator();
            ChessGame game = ChessGame.createTestGame(pgn, false); // Create game from PGN
            game.moveToEndOfGame(false);
            for(int z = 0;z<64;z++){
                int[] xy = BitFunctions.bitindexToXY(z);
                List<XYcoord> rookMovesNormal = GeneralChessFunctions.getPieceCoords(AdvancedChessFunctions.calculateRookMoves(xy[0],xy[1],game.isWhiteTurn(),game.currentPosition.board));
                List<XYcoord> rookMovesMagic = AdvancedChessFunctions.calculateRookMovesMagicBitboard(xy[0],xy[1],game.isWhiteTurn(),game.currentPosition.board );
                Set<XYcoord> matchSet = new HashSet<>();
                matchSet.addAll(rookMovesNormal);
                for(XYcoord c : rookMovesMagic){
                    Assertions.assertTrue(matchSet.contains(c));
                }
                Assertions.assertEquals(rookMovesNormal.size(),rookMovesMagic.size());
            }
        }

    }

    @Test void  MagicBishopGenerationTest(){
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


        for (String pgn : pgns) {
            MagicBitboardGenerator m = new MagicBitboardGenerator();
            ChessGame game = ChessGame.createTestGame(pgn, false); // Create game from PGN
            game.moveToEndOfGame(false);
            for(int z = 0;z<64;z++){
                int[] xy = BitFunctions.bitindexToXY(z);
                List<XYcoord> bishopMovesNormal = GeneralChessFunctions.getPieceCoords(AdvancedChessFunctions.calculateBishopMoves(xy[0],xy[1],game.isWhiteTurn(),game.currentPosition.board));
                List<XYcoord> bishopMovesMagic = AdvancedChessFunctions.calculateBishopMovesMagicBitboard(xy[0],xy[1],game.isWhiteTurn(),game.currentPosition.board );
                Set<XYcoord> matchSet = new HashSet<>();
                System.out.println(bishopMovesMagic.size());
                Assertions.assertEquals(bishopMovesNormal.size(),bishopMovesMagic.size());
                matchSet.addAll(bishopMovesNormal);
                for(XYcoord c : bishopMovesMagic){
                    Assertions.assertTrue(matchSet.contains(c));
                }
            }
        }

    }

    @Test void rookMaskTests(){
        for(int i = 0;i<64;i++){
            System.out.println(BitFunctions.getBitStr(BitFunctions.generateRookMoveMask(i)));
//            try {
//                Thread.sleep(1000);
//            }
//            catch (Exception e){
//                System.out.println(e.getStackTrace());
//            }

        }
    }
    @Test void rookMaskNoEdgeTests(){
        for(int i = 0;i<64;i++){
            System.out.println(BitFunctions.getBitStr(BitFunctions.generateRookMoveMaskNoEdges(i)));
//            try {
//                Thread.sleep(1000);
//            }
//            catch (Exception e){
//                System.out.println(e.getStackTrace());
//            }

        }

    }
    @Test void bishopMaskTests(){
        for(int i = 0;i<64;i++){
//            try {
//                Thread.sleep(1000);
//            }
//            catch (Exception e){
//                System.out.println(e.getStackTrace());
//            }
            System.out.println(BitFunctions.getBitStr(BitFunctions.generateBishopMoveMask(i),i));

        }
    }

    @Test void bishopMaskNoEdgeTests(){
        for(int i = 0;i<64;i++){
            System.out.println(BitFunctions.getBitStr(BitFunctions.generateBishopMoveMaskNoEdges(i)));
//            try {
//                Thread.sleep(1000);
//            }
//            catch (Exception e){
//                System.out.println(e.getStackTrace());
//            }

        }
    }

    @Test void generateBlockerPositionTests(){
        for(int i = 0;i<64;i++){
            long moveMask = BitFunctions.generateRookMoveMaskNoEdges(i);
            long[] blockerPositions = BitFunctions.createAllBlockerBitMasks(moveMask);
//            for(long blockerPosition : blockerPositions){
                System.out.println(BitFunctions.getBitStr(blockerPositions[blockerPositions.length-1]));
//                try {
//                    Thread.sleep(1000);
//                }
//                catch (Exception e){
//                    System.out.println(e.getStackTrace());
//                }
//            }

        }
    }

    @Test void generateLegalMoveTests(){
        for(int i = 0;i<64;i++){
            long moveMask = BitFunctions.generateRookMoveMaskNoEdges(i);
            long[] blockerPositions = BitFunctions.createAllBlockerBitMasks(moveMask);
            for(long blockerPosition : blockerPositions){
                System.out.println(BitFunctions.getBitStr(blockerPosition));
                System.out.println("---------------------------");
                System.out.println(BitFunctions.getBitStr(BitFunctions.generateLegalRookMoveBitboard(i,blockerPosition)));
//                try {
//                    Thread.sleep(2000);
//                }
//                catch (Exception e){
//                    System.out.println(e.getStackTrace());
//                }
            }

        }
    }

    @Test void generateMagicRookNumbers(){
        Random r = new Random();
        int[] shifts = new int[64];
        long[] magics = new long[64];
        for(int bitIndex = 0;bitIndex<64;bitIndex++){
            long rookMoveMaskWithoutEdges = BitFunctions.generateRookMoveMaskNoEdges(bitIndex);
            long[] blockerPositions = BitFunctions.createAllBlockerBitMasks(rookMoveMaskWithoutEdges);
            shifts[bitIndex] = 64-Long.bitCount(rookMoveMaskWithoutEdges); // num blocker bits needed
            Set<Long> seen = new HashSet<>(); // we want to never see a value twice
            long goodMagic = 0;
            boolean foundGoodMagic;
            while(goodMagic == 0){
                seen.clear();
                foundGoodMagic = true;
                long randomMagic = rookMagics[bitIndex];
                for (long blockerPosition : blockerPositions) {
                    long mult = (blockerPosition * randomMagic) >>> shifts[bitIndex];
                    if (seen.contains(mult)) {
                        foundGoodMagic = false;
                        break;
                    }
                    seen.add(mult);

                }
                if(foundGoodMagic){
                    System.out.println("Found good magic #" + bitIndex + ": "  + randomMagic);
                    goodMagic = randomMagic;
                }


            }
            magics[bitIndex] = goodMagic;

        }
        System.out.println(Arrays.toString(Arrays.stream(magics).mapToObj(m-> m + "L").toArray()));

    }

    private final long[] bishopMagics = {110339990797484544L, 577041298754699266L, 1301540861678583808L,
            -6194664727612749696L, 721706375772734464L, 2534786891579425L, 2306692146260377858L, 595039201486177420L,
            48482027308033L, 36037671537344776L, 2306213012333174784L, 293034159709489152L, 1154051821350682624L,
            18016877244579840L, 1163064585541125120L, -6192723261331986944L, 5773614791310592064L, 6755419979547648L,
            6346137028303847936L, 38283088456851456L, 2306968917745141888L, 423037904363536L, 2414633100757180416L,
            2099240377130418688L, 63067991416276224L, 4612902155865712660L, 18665310752145920L, 648597513871425728L,
            4611827305940525060L, 94580265133278720L, 73219228077392929L, 283674050381057L, 6775264739205132L, 9587759112001816L,
            1155314598124981249L, 4631958815972524160L, 18033648555065604L, 360852608065339456L, 704262969754112L, 572297413001728L,
            4612266578887713280L, 291375423787041L, -7998357667902763008L, 2305845354601383940L, 4755802314940350976L,
            81126920133018112L, 2379309120630301712L, 5770814729553158L, 72343510048672258L, 4909979417323294930L, 2218552197122L,
            4504596605583680L, 4620730878243766272L, 292751705895403520L, 2884590763486758920L, 5630633422422530L, -6894935979109382608L,
            -9222225778643431388L, 7063968859418002440L, -9043006500162633216L, 180144019590938880L, 180234420096336128L, 19157907916980544L, 2308174042919609891L};

    private final long[] rookMagics = {
            36029071914722432L, 378302712332156992L, 36040960385220736L, 1224984596774916096L, 144121787362347024L, -7962345432305106432L,
            432363757844365824L, 252206821685592320L, 4796474341711708212L, 2333638666424946688L, 40813949469393472L, 288793363720708608L,
            4616330441447178368L, -1061722477677240304L, 1125951849697282L, 288371123588964480L, 2307110196368900256L, 36284957527169L,
            422762757820424L, 77126342916769856L, 2305862800523662944L, 2393088165151744L, 391426190887432L, 126314094843396225L,
            -9205357361319605088L, 5764783445968441344L, 301917099042799744L, 305171655608041762L, 36037595267859456L, 1188954701819936896L,
            281479273906180L, 288477242282492931L, 576496211561812096L, 5197156375179823168L, 18155823234551811L, 26394729910272L, 292742774044754944L,
            468726342540659856L, 288538308730946914L, 1152925903760663169L, 578782964356513792L, 4692751636362199072L, 112731005768564784L, 144203161891569696L,
            146367022534557708L, 288793343437176848L, 2305915645717643272L, 1369375763849740290L, 1153273371951112320L, 3458940453935153280L, 35219000296064L,
            141905719985792L, 576469548463587456L, 5629817370227840L, 2902571068882445312L, 216195874743665152L, 4611967641580486677L, 576777970019532818L,
            -9222808948346712054L, 4900409046791758338L, 281483634278417L, -6845189872718183935L, 4785220633003013L, 1157438298919075910L
    };

    @Test void generateMagicBishopNumbers(){
        Random r = new Random();
        int[] shifts = new int[64];
        long[] magics = new long[64];
        for(int bitIndex = 0;bitIndex<64;bitIndex++){
            long bishopMoveMaskWithoutEdges = BitFunctions.generateBishopMoveMaskNoEdges(bitIndex);
            long[] blockerPositions = BitFunctions.createAllBlockerBitMasks(bishopMoveMaskWithoutEdges);
            shifts[bitIndex] = 64-Long.bitCount(bishopMoveMaskWithoutEdges); // num blocker bits needed
            Set<Long> seen = new HashSet<>(); // we want to never see a value twice
            long goodMagic = 0;
            boolean foundGoodMagic;
            while(goodMagic == 0){
                seen.clear();
                foundGoodMagic = true;
                long randomMagic = bishopMagics[bitIndex];
                for (long blockerPosition : blockerPositions) {
                    long mult = (blockerPosition * randomMagic) >>> shifts[bitIndex];
                    if (seen.contains(mult)) {
                        foundGoodMagic = false;
                        break;
                    }
                    seen.add(mult);

                }
                if(foundGoodMagic){
                    System.out.println("Found good magic #" + bitIndex + ": "  + randomMagic);
                    goodMagic = randomMagic;
                }


            }
            magics[bitIndex] = goodMagic;

        }
        System.out.println(Arrays.toString(Arrays.stream(magics).mapToObj(m-> m + "L").toArray()));
        System.out.println(Arrays.toString(shifts));
    }

    long generateSparseMagic(Random random) {
        return random.nextLong() & random.nextLong() & random.nextLong();
    }


    int[] RBits = {
        12, 11, 11, 11, 11, 11, 11, 12,
        11, 10, 10, 10, 10, 10, 10, 11,
        11, 10, 10, 10, 10, 10, 10, 11,
        11, 10, 10, 10, 10, 10, 10, 11,
        11, 10, 10, 10, 10, 10, 10, 11,
        11, 10, 10, 10, 10, 10, 10, 11,
        11, 10, 10, 10, 10, 10, 10, 11,
        12, 11, 11, 11, 11, 11, 11, 12
    };

    int[] BBits = {
        6, 5, 5, 5, 5, 5, 5, 6,
        5, 5, 5, 5, 5, 5, 5, 5,
        5, 5, 7, 7, 7, 7, 5, 5,
        5, 5, 7, 9, 9, 7, 5, 5,
        5, 5, 7, 9, 9, 7, 5, 5,
        5, 5, 7, 7, 7, 7, 5, 5,
        5, 5, 5, 5, 5, 5, 5, 5,
        6, 5, 5, 5, 5, 5, 5, 6
    };

    @Test void verifyRookEdges(){
        for(int i = 0;i<64;i++){
            long rookMaskNoEdges = BitFunctions.generateRookMoveMaskNoEdges(i);
            System.out.println(BitFunctions.getBitStr(rookMaskNoEdges));
            Assertions.assertEquals(Long.bitCount(rookMaskNoEdges),RBits[i]);
        }
    }

    @Test void verifyBishopEdges(){
        for(int i = 0;i<64;i++){
            long bishopMaskNoEdges = BitFunctions.generateBishopMoveMaskNoEdges(i);
            Assertions.assertEquals(Long.bitCount(bishopMaskNoEdges),BBits[i]);
        }
    }
}
