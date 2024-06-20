package chessengine;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class PgnFunctions {




    // all related to creating a position out of a pgn












    private static List<String> parseMoveInfo(String pgnInfo){
        return new LinkedList<String>(Arrays.asList(pgnInfo.split("\\[.*?\\]")));
    }

    public static String[] splitPgn(String pgn){
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
        return new String[]{pgn.substring(0,moveTextIndex),pgn.substring(moveTextIndex)};
    }

    public static String moveToPgn(ChessMove move,BitBoardWrapper board){
        StringBuilder sb1 = new StringBuilder();
        if(move.isCastleMove()){
            int xDiff = Math.abs(move.getNewX()-move.getOldX());
            if(xDiff > 2){
                // long castle
                return "O-O-O";
            }
            else{
                // short
                return "O-O";
            }
        }
        String start = turnPieceIndexToPgn(move.getBoardIndex());
        String ambiguityChar = AdvancedChessFunctions.getAmbigiousStr(move.getOldX(),move.getOldY(),move.getNewX(),move.getNewY(),move.getBoardIndex(),move.isWhite(),move.isEating(),board);
        String eatingChar = move.isEating() ? "x" : "";
        char xChar = turnIntToFileStr(move.getNewX());
        // flip y
        char yChar = intToChar(7-move.getNewY()+1);
        String promoChar = move.isPawnPromo() ? "=" : "";
        String promoTypeChar = move.isPawnPromo() ? turnPieceIndexToPgn(move.getPromoIndx()) : "";
        String checkedChar = AdvancedChessFunctions.isChecked(!move.isWhite(),board) ? "+" : "";
        return sb1.append(start).append(ambiguityChar).append(eatingChar).append(xChar).append(yChar).append(promoChar).append(promoTypeChar).append(checkedChar).toString();

    }

    public static String moveToPgn(ChessPosition p){
        return moveToPgn(p.getMoveThatCreatedThis(),p.board);

    }

    public static String invertPgn(String pgn){
        StringBuilder sb = new StringBuilder();
        for(int i = 0;i<pgn.length();i++){
            char c = pgn.charAt(i);
            if(Character.isDigit(c)){
                sb.append(intToChar(9-Integer.parseInt(String.valueOf(c))));
            }
            else {
                sb.append(c);
            }
        }
        return sb.toString();
    }



    public static char intToChar(int i){
        return (char) (i+48);
    }

    public static int turnFileStrToInt(char c){
        int ascii = (int) c;
        return ascii-97;
    }

    public static char turnIntToFileStr(int i){
        char ascii = (char)i;
        return (char) (ascii+97);
    }






    public static int turnPgnPieceToPieceIndex(char c){
        switch (c){
            case 'K':
                return 5;
            case 'Q':
                return 4;
            case 'R':
                return 3;
            case 'B':
                return 2;
            case 'N':
                return 1;
        }
        // if none match then it is a pawn(index 0)
        return 0;


    }

    public static String turnPieceIndexToPgn(int c){
        switch (c){
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
        return "";


    }
}
