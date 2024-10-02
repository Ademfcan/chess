package chessengine.Computation;

import chessengine.Functions.BitFunctions;

import java.util.Random;

public class MagicBitboardGenerator {

    public final long[][] rookMap = new long[64][];
    public final long[][] bishopMap = new long[64][];
    private final int[] RBits = {
            12, 11, 11, 11, 11, 11, 11, 12,
            11, 10, 10, 10, 10, 10, 10, 11,
            11, 10, 10, 10, 10, 10, 10, 11,
            11, 10, 10, 10, 10, 10, 10, 11,
            11, 10, 10, 10, 10, 10, 10, 11,
            11, 10, 10, 10, 10, 10, 10, 11,
            11, 10, 10, 10, 10, 10, 10, 11,
            12, 11, 11, 11, 11, 11, 11, 12
    };
    private final int[] BBits = {
            6, 5, 5, 5, 5, 5, 5, 6,
            5, 5, 5, 5, 5, 5, 5, 5,
            5, 5, 7, 7, 7, 7, 5, 5,
            5, 5, 7, 9, 9, 7, 5, 5,
            5, 5, 7, 9, 9, 7, 5, 5,
            5, 5, 7, 7, 7, 7, 5, 5,
            5, 5, 5, 5, 5, 5, 5, 5,
            6, 5, 5, 5, 5, 5, 5, 6
    };
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
    private final Random random;

    public MagicBitboardGenerator() {
        random = new Random();
        generateRookDict();
        generateBishopDict();
    }

    public long getRookMagicKey(long blockerBoard, int bitIndex) {
        return (blockerBoard * rookMagics[bitIndex]) >>> (64 - RBits[bitIndex]);
    }

    public long getBishopMagicKey(long blockerBoard, int bitIndex) {
        return (blockerBoard * bishopMagics[bitIndex]) >>> (64 - BBits[bitIndex]);
    }

    private void generateRookDict() {
        for (int bitIndex = 0; bitIndex < 64; bitIndex++) {
            long rookMoveMaskWithoutEdges = BitFunctions.generateRookMoveMaskNoEdges(bitIndex);
            long[] blockerPositions = BitFunctions.createAllBlockerBitMasks(rookMoveMaskWithoutEdges);
//            System.out.println(RBits[bitIndex]);
            rookMap[bitIndex] = new long[1 << RBits[bitIndex]];
            for (long blockerPosition : blockerPositions) {
                long magicKey = (blockerPosition * rookMagics[bitIndex]) >>> (64 - RBits[bitIndex]);
                long legalMoveBitBoard = BitFunctions.generateLegalRookMoveBitboard(bitIndex, blockerPosition);
                rookMap[bitIndex][(int) magicKey] = legalMoveBitBoard;
            }
        }
    }

    private void generateBishopDict() {
        for (int bitIndex = 0; bitIndex < 64; bitIndex++) {
            long bishopMoveMaskWithoutEdges = BitFunctions.generateBishopMoveMaskNoEdges(bitIndex);
            long[] blockerPositions = BitFunctions.createAllBlockerBitMasks(bishopMoveMaskWithoutEdges);
            bishopMap[bitIndex] = new long[1 << BBits[bitIndex]];
            for (long blockerPosition : blockerPositions) {
                long magicKey = (blockerPosition * bishopMagics[bitIndex]) >>> (64 - BBits[bitIndex]);
                long legalMoveBitBoard = BitFunctions.generateLegalBishopMoveBitboard(bitIndex, blockerPosition);
                bishopMap[bitIndex][(int) magicKey] = legalMoveBitBoard;
            }
        }
    }


}

