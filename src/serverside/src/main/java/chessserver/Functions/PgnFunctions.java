package chessserver.Functions;

import chessserver.ChessRepresentations.*;
import chessserver.Misc.ChessConstants;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class PgnFunctions {


    // all related to creating a position out of a pgn


    private static List<String> parseMoveInfo(String pgnInfo) {
        return new LinkedList<String>(Arrays.asList(pgnInfo.split("\\[.*?\\]")));
    }

    public static String[] splitPgn(String pgn) {
//        String infoPart = "";
//        String moveTextPart = "";
//        Pattern pattern = Pattern.compile("^(\\[.*?\\])\\s*((?:\\d+\\.\\s.*)+)$", Pattern.MULTILINE);
//        Matcher matcher = pattern.matcher(pgn);
//
//        if (matcher.find()) {
//            infoPart = matcher.group(1).trim();
//            moveTextPart = matcher.group(2).trim();
//
//            System.out.println("Info Part:");
//            System.out.println(infoPart);
//            System.out.println("\nMove Text Part:");
//            System.out.println(moveTextPart);
//        } else {
//            chessFunctions.mainLogger.error("Invalid PGN format: Unable to split into info and move text parts.");
//        }
//        return new String[]{infoPart,moveTextPart};
        int moveTextIndex = pgn.indexOf("1.");
        return new String[]{pgn.substring(0, moveTextIndex), pgn.substring(moveTextIndex)};
    }

    // pgn examples: 1. e4 e5 2. Nf3 Nc6 3. Bb5...
    public static String moveToPgn(ChessMove move, ChessPosition pos, ChessGameState gameStates) {
        StringBuilder sb1 = new StringBuilder();
        if (move.isCastleMove()) {
            int xDiff = move.getNewX() - move.getOldX();
            if (xDiff > 0) {
                // short castle
                return "O-O";
            } else {
                // long castle
                return "O-O-O";
            }
        }
        String start = turnPieceIndexToStr(move.getBoardIndex(), true);
        String ambiguityChar = AdvancedChessFunctions.getAmbigiousStr(move.getOldX(), move.getOldY(), move.getNewX(), move.getNewY(), move.getBoardIndex(), move.isWhite(), move.isEating(), pos);
        String eatingChar = move.isEating() || move.isEnPassant() ? "x" : "";
        char xChar = turnIntToFileStr(move.getNewX());
        // flip y
        char yChar = intToChar(7 - move.getNewY() + 1);
        String promoChar = move.isPawnPromo() ? "=" : "";
        String promoTypeChar = move.isPawnPromo() ? turnPieceIndexToStr(move.getPromoIndx(), true) : "";
        String checkedChar = AdvancedChessFunctions.isChecked(!move.isWhite(), pos.board) ? "+" : "";
        if (AdvancedChessFunctions.isAnyNotMovePossible(!move.isWhite(), pos, gameStates)) {
            if (AdvancedChessFunctions.isChecked(!move.isWhite(), pos.board)) {
                checkedChar = "#";
            } else {
                checkedChar = "";
            }

        }
        return sb1.append(start).append(ambiguityChar).append(eatingChar).append(xChar).append(yChar).append(promoChar).append(promoTypeChar).append(checkedChar).toString();

    }

    public static String moveToPgn(ChessPosition p, ChessGameState gameStates) {
        return moveToPgn(p.getMoveThatCreatedThis(), p, gameStates);

    }

    public static String invertPgn(String pgn) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pgn.length(); i++) {
            char c = pgn.charAt(i);
            if (Character.isDigit(c)) {
                sb.append(intToChar(9 - Integer.parseInt(String.valueOf(c))));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }


    public static char intToChar(int i) {
        return (char) (i + '0');
    }

    public static int charToInt(char c){
        return (int) (c - '0');
    }

    public static int turnFileStrToInt(char c) {
        return (c - 'a');
    }

    public static char turnIntToFileStr(int i) {
        return (char) (i + 'a');
    }

    /**Will give you the index of a piece given its respective character. Will match only uppercase characters**/
    public static int turnPieceCharacterToPieceIndex(char c) {
        switch (c) {
            case 'K':
                return ChessConstants.KINGINDEX;
            case 'Q':
                return ChessConstants.QUEENINDEX;
            case 'R':
                return ChessConstants.ROOKINDEX;
            case 'B':
                return ChessConstants.BISHOPINDEX;
            case 'N':
                return ChessConstants.KNIGHTINDEX;
        }
        // if none match then it is a pawn(index 0)
        return 0;


    }
    /**This function returns an int[] {[0] = color, [1] = index} with the color and piece index of a character provided. Similar to {@link #turnPieceCharacterToPieceIndex(char)} but not color independent**/
    public static int[] turnPieceCharacterToPieceIndexWithColor(char c) {
        return switch (c) {
            case 'K' -> new int[]{1, 5};
            case 'k' -> new int[]{0, 5};
            case 'Q' -> new int[]{1, 4};
            case 'q' -> new int[]{0, 4};
            case 'R' -> new int[]{1, 3};
            case 'r' -> new int[]{0, 3};
            case 'B' -> new int[]{1, 2};
            case 'b' -> new int[]{0, 2};
            case 'N' -> new int[]{1, 1};
            case 'n' -> new int[]{0, 1};
            case 'P' -> new int[]{1, 0};
            case 'p' -> new int[]{0, 0};
            default ->
                // if none match then return null
                null;
        };


    }

    public static String turnPieceIndexToStr(int c, boolean isPgn) {
        switch (c) {
            case ChessConstants.KINGINDEX:
                return "K";
            case ChessConstants.QUEENINDEX:
                return "Q";
            case ChessConstants.ROOKINDEX:
                return "R";
            case ChessConstants.BISHOPINDEX:
                return "B";
            case ChessConstants.KNIGHTINDEX:
                return "N";
        }
        // if none match then it is a pawn(empty char)
        if (isPgn) {
            return "";
        } else {
            return "P";
        }


    }

    //
    public static String positionToFEN(ChessPosition pos, ChessGameState gameStateForPos, boolean isWhiteTurn) {
        // example FEN: 8/5k2/3p4/1p1Pp2p/pP2Pp1P/P4P1K/8/8 b - - 99 50
        // go through each file in each row
        int numHalfMovesSinceCheckOrPawnMove = gameStateForPos.getMovesSinceNoCheckOrNoPawn();
        int numFullMoves = gameStateForPos.getCurrentIndex() / 2;
        StringBuilder fenBuilder = new StringBuilder();
        for (int row = 0; row < 8; row++) {
            int emptySquareCount = 0;
            for (int file = 0; file < 8; file++) {
                boolean[] boardInfo = GeneralChessFunctions.checkIfContains(file, row, pos.board, "tofen");
                if (boardInfo[0]) {
                    // means there is a piece there
                    // first put the number of empty squares before this
                    if (emptySquareCount > 0) {
                        fenBuilder.append(emptySquareCount);
                    }
                    emptySquareCount = 0;
                    // now we put the string representing the piece
                    int pieceIndex = GeneralChessFunctions.getBoardWithPiece(file, row, boardInfo[1], pos.board);
                    String pieceString;
                    if (boardInfo[1]) {
                        // means a white piece
                        pieceString = turnPieceIndexToStr(pieceIndex, false);

                    } else {
                        pieceString = turnPieceIndexToStr(pieceIndex, false).toLowerCase();

                    }
                    fenBuilder.append(pieceString);

                } else {
                    // no piece so we increment empty square count;

                    emptySquareCount++;
                }
            }
            // flush any empty squares
            if (emptySquareCount > 0) {
                fenBuilder.append(emptySquareCount);
            }


            // add a slash after every row
            if (row != 7) {
                fenBuilder.append("/");
            }
        }
        // finally add all of the extra information, such as iswhiteturn, castling rights, en passant possibilities etc
        if (isWhiteTurn) {
            fenBuilder.append(" w");
        } else {
            fenBuilder.append(" b");
        }
        fenBuilder.append(" ");

        boolean whiteCastle = gameStateForPos.isWhiteCastleRight();
        boolean blackCastle = gameStateForPos.isBlackCastleRight();
        String wksC = gameStateForPos.isWhiteKingSideRight() && whiteCastle ? "K" : "";
        String wqsC = gameStateForPos.isWhiteQueenSideRight() && whiteCastle ? "Q" : "";
        String bksC = gameStateForPos.isWhiteKingSideRight() && blackCastle ? "k" : "";
        String bqsC = gameStateForPos.isWhiteKingSideRight() && blackCastle ? "q" : "";
        if (wksC.isEmpty() && wqsC.isEmpty() && bksC.isEmpty() && bqsC.isEmpty()) {
            fenBuilder.append("-");
        } else {
            fenBuilder.append(wksC);
            fenBuilder.append(wqsC);
            fenBuilder.append(bksC);
            fenBuilder.append(bqsC);
        }
        fenBuilder.append(" ");
        boolean isPassant = false;
        // now check enpassant
        if (!pos.equals(ChessConstants.startBoardState)) {
            // means we arent at the very beginning as there is not an actual move for the start position
            ChessMove moveThatCreated = pos.getMoveThatCreatedThis();
            if (moveThatCreated.getBoardIndex() == ChessConstants.PAWNINDEX) {
                // pawn move so possibilty of enpassant
                if (Math.abs(moveThatCreated.getOldY() - moveThatCreated.getNewY()) > 1) {
                    // jumped 2 so means that there is a possibilty of en passant, so add to the FEN
                    isPassant = true;
                    char fileChar = turnIntToFileStr(moveThatCreated.getNewX());
                    int midY = (moveThatCreated.getOldY() + moveThatCreated.getNewY()) / 2;

                    fenBuilder.append(fileChar);
                    fenBuilder.append(midY);

                }
            }
        }
        if (!isPassant) {
            fenBuilder.append("-");
        }
        fenBuilder.append(" ");
        fenBuilder.append(numHalfMovesSinceCheckOrPawnMove);
        fenBuilder.append(" ");
        fenBuilder.append(numFullMoves);


        return fenBuilder.toString();
    }

    public static FullChessPosition FenToPosition(String fen){
        String[] split = fen.split(" ");
        // part one board
        BitBoardWrapper board = BitBoardWrapper.getEmptyBoard();
        int x = 0, y = 0;
        // process the board part of the pgn
        for(char c : split[0].toCharArray()){
            if(c == '/'){
                x = 0;
                y++;
                continue;
            }

            if(Character.isDigit(c)){
                x+=charToInt(c)-1;
            }
            else{
                int[] piece = turnPieceCharacterToPieceIndexWithColor(c);
                int color = piece[0];
                int idx = piece[1];
                board.addPiece(GeneralChessFunctions.positionToBitIndex(x,y),idx,color == 1);
            }
            x++;

        }
        board.updateAttackMasks();
        // part 2 white turn
        boolean isWhiteTurn = split[1].equals("w");

        // part 3 castling rights
        ChessGameState gameState = new ChessGameState();
        gameState.removeAllRightsInstantly();
        boolean whiteRight = false,blackRight=false;
        for(char c : split[2].toCharArray()){
            switch (c){
                case 'K':
                    gameState.giveRookRight(true,false);
                    whiteRight = true;
                    break;
                case 'Q':
                    gameState.giveRookRight(true,true);
                    whiteRight = true;
                    break;
                case 'k':
                    gameState.giveRookRight(false,false);
                    blackRight = true;
                    break;
                case 'q':
                    gameState.giveRookRight(false,true);
                    blackRight = true;
                    break;
            }
        }
        if(whiteRight){
            gameState.giveCastleRight(true);
        }
        if(blackRight){
            gameState.giveCastleRight(false);
        }
        // part 4 en passant
        ChessMove previousMove = ChessConstants.startMove;
        if(!split[3].equals("-")){
            boolean pawnWhiteMove = !isWhiteTurn;
            int dir = pawnWhiteMove ? -1 : 1;
            int pawnNewX = turnFileStrToInt(split[3].charAt(0));
            int pawnNewY = charToInt(split[3].charAt(1))-1 + (pawnWhiteMove ? -dir : dir);
            int pawnOldX = pawnNewX;
            int pawnOldY = pawnNewY - (2*dir); // move 2 back
            previousMove = new ChessMove(pawnOldX,pawnOldY,pawnNewX,pawnNewY,ChessConstants.EMPTYINDEX,ChessConstants.PAWNINDEX,pawnWhiteMove,false,false,ChessConstants.EMPTYINDEX,false,false);
        }
        // part 5 num half moves for 50 move rule
        int numHalfMoves = Integer.parseInt(split[4]);

        // part 6 num full moves + 1 if blacks turn to get total number of halfmoves played
//        int numTotalMoves = Integer.parseInt(split[5]) + (isWhiteTurn ? 0 : 1);
        // numtotal moves not used for these puzzles currently
        gameState.setMovesSinceNoCheckOrNoPawn(numHalfMoves);

        ChessPosition position = new ChessPosition(board,previousMove);
        return new FullChessPosition(position,gameState,isWhiteTurn);

    }

    public static ChessMove uciToChessMove(String uci, boolean isWhite, BitBoardWrapper board) {
        char[] c = uci.toCharArray();
        int startX = turnFileStrToInt(c[0]);
        int endX = turnFileStrToInt(c[2]);
        int startY = 7 - charToInt(c[1]) + 1;
        int endY = 7 - charToInt(c[3]) + 1;
        int boardIndex = GeneralChessFunctions.getBoardWithPiece(startX, startY, board);
        // eating (technically should check to see if its a correct piece color or throw error but nahh its fine)
        boolean isEating = GeneralChessFunctions.checkIfContains(endX, endY, board, "ya")[0];
        int eatingIndex = GeneralChessFunctions.getBoardWithPiece(endX, endY, board);
        boolean isCastleMove = boardIndex == ChessConstants.KINGINDEX && (uci.equals("e1g1") /* white short castle */ || uci.equals("e8g8") /*black short castle*/ || uci.equals("e1c1") /* white long*/ || uci.equals("e8c8"))/*black short*/;
        int promoIndex = c.length > 4 ? turnPieceCharacterToPieceIndex(Character.toUpperCase(c[4])) : ChessConstants.EMPTYINDEX;
        int backDir = isWhite ? 1 : -1;
        boolean isEnPassant = startX != endX && boardIndex == ChessConstants.PAWNINDEX && GeneralChessFunctions.getBoardWithPiece(endX, endY + backDir, !isWhite, board) == ChessConstants.PAWNINDEX && !isEating;
        return new ChessMove(startX, startY, endX, endY, promoIndex, boardIndex, isWhite, isCastleMove, isEating, eatingIndex, isEnPassant, false);

    }
}
