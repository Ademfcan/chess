package chessengine;

import javafx.scene.image.ImageView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.*;

public class AdvancedChessFunctions {

    private int[] peicesOnBoard = {8,2,2,2,1,1,8,2,2,2,1,1};
    private final long blackPawns = 0b0000000000000000000000000000000000000000000000001111111100000000L;
    private final long blackKnights = 0b0000000000000000000000000000000000000000000000000000000001000010L;
    private final long blackBishops = 0b00000000000000000000000000000000000000000000000000000000000100100L;
    private final long blackRooks = 0b0000000000000000000000000000000000000000000000000000000010000001L;
    private final long blackQueens = 0b0000000000000000000000000000000000000000000000000000000000001000L;
    private final long blackKings = 0b0000000000000000000000000000000000000000000000000000000000010000L;

    private final long whitePawns = 0b0000000011111111000000000000000000000000000000000000000000000000L;
    private final long whiteKnights = 0b0100001000000000000000000000000000000000000000000000000000000000L;
    private final long whiteBishops = 0b0010010000000000000000000000000000000000000000000000000000000000L;
    private final long whiteRooks = 0b1000000100000000000000000000000000000000000000000000000000000000L;
    private final long whiteQueens = 0b0000100000000000000000000000000000000000000000000000000000000000L;
    private final long whiteKings = 0b0001000000000000000000000000000000000000000000000000000000000000L;

    // Flipped positioning
    public final long[] blackPiecesC = {blackPawns, blackKnights, blackBishops, blackRooks, blackQueens, blackKings};
    public final long[] whitePiecesC = {whitePawns, whiteKnights, whiteBishops, whiteRooks, whiteQueens, whiteKings};
    XYcoord whiteKingStart = new XYcoord(4,7);
    XYcoord blackKingStart = new XYcoord(4,0);
    public final BitBoardWrapper startBoardState = new BitBoardWrapper(whitePiecesC,blackPiecesC,whiteKingStart,blackKingStart);

    public BitBoardWrapper realBoard = startBoardState.cloneBoard();


    private ArrayList<BitBoardWrapper> boardSave = new ArrayList<>();

    public int moveIndx = -1;
    public int maxIndex = -1;



    private boolean whiteCastleRight = true;
    public boolean blackCastleRight = true;

    private boolean whiteShortRookMove = true;
    private boolean whiteLongRookMove = true;
    public boolean blackShortRookMove = true;
    private boolean blackLongRookMove = true;
    private int blackCastleIndx = 1000;
    private int whiteCastleIndx = 1000;
    private int whiteShortRookIndx = 1000;
    private int whiteLongRookIndx = 1000;
    private int blackShortRookIndx = 1000;
    private int blackLongRookIndx = 1000;






    private Logger logger;
    private ChessCentralControl ChessCentralControl;

    public AdvancedChessFunctions() {
        this.logger  = LogManager.getLogger(this.toString());
        this.ChessCentralControl = App.getCentralControl();
    }







    public static List<XYcoord> getPossibleMoves(int x, int y, boolean isWhite, BitBoardWrapper board,ChessStates gameState){
        int indx = GeneralChessFunctions.getBoardWithPiece(x,y,isWhite,board);
        //System.out.println("Getting moves for peice at " + x + " " + y);
        return getPossibleMoves(x,y,isWhite,board,gameState,indx);

    }

    public static List<XYcoord> getPossibleMoves(int x, int y, boolean isWhite, BitBoardWrapper board,ChessStates gameState, int boardIndex){
        //System.out.println("Getting moves for peice at " + x + " " + y);
        List<XYcoord> baseMoves = getMoveOfType(x,y,isWhite,boardIndex,board,gameState);
        if(isChecked(isWhite,board)){;
            if(boardIndex != 5){

                baseMoves.retainAll(getCheckedFile(isWhite,board));

            }
            return baseMoves;
        }
        else{
            return baseMoves;
        }

    }

    private static List<XYcoord> getMoveOfType(int x, int y, boolean isWhite, int indx, BitBoardWrapper board,ChessStates gameState){
        if(!GeneralChessFunctions.isValidIndex(indx)){
            ChessConstants.mainLogger.error("Invalid index passed into get move of type");
        }
        return switch (indx) {
            case 0 -> calculatePawnMoves(x, y, isWhite,board,false);
            case 1 -> calculateKnightMoves(x, y, isWhite, false,board,false);
            case 2 -> calculateBishopMoves(x, y, isWhite, false,false,board,false);
            case 3 -> calculateRookMoves(x, y, isWhite, false,false,board,false);
            case 4 -> calculateQueenMoves(x, y, isWhite, false,board,false);
            case 5 -> calculateKingMoves(x, y, isWhite,board,gameState);
            default -> (null);
        };
    }

    private static List<XYcoord> getMoveOfType(int x, int y, boolean isWhite, int indx, BitBoardWrapper board,ChessStates gameState,boolean isForCheck){
        if(!GeneralChessFunctions.isValidIndex(indx)){
            ChessConstants.mainLogger.error("Invalid index passed into get move of type");
        }
        return switch (indx) {
            case 0 -> calculatePawnMoves(x, y, isWhite,board,isForCheck);
            case 1 -> calculateKnightMoves(x, y, isWhite, false,board,isForCheck);
            case 2 -> calculateBishopMoves(x, y, isWhite, false,false,board,isForCheck);
            case 3 -> calculateRookMoves(x, y, isWhite, false,false,board,isForCheck);
            case 4 -> calculateQueenMoves(x, y, isWhite, false,board,isForCheck);
            case 5 -> calculateKingMoves(x, y, isWhite,board,gameState);
            default -> (null);
        };
    }







    public void promoPawn(boolean isWhite, int oldX, int oldY, int X, int Y, int newPeiceIndx, BitBoardWrapper board){
        long[] peices = isWhite ? board.getWhitePieces() : board.getBlackPieces();
        long[] enmypeices = !isWhite ? board.getWhitePieces() : board.getBlackPieces();
        peices[0] = GeneralChessFunctions.RemovePeice(oldX,oldY,peices[0]);
        int enmyIndx = GeneralChessFunctions.getBoardWithPiece(X,Y,!isWhite, board);
        System.out.println(enmyIndx);
        if(enmyIndx != -10){
            enmypeices[enmyIndx] = GeneralChessFunctions.RemovePeice(X,Y,enmypeices[enmyIndx]);

            ChessCentralControl.chessBoardGUIHandler.updateEatenPieces(enmyIndx,!isWhite);

        }
        peices[newPeiceIndx] = GeneralChessFunctions.AddPeice(X,Y,peices[newPeiceIndx]);
        createBoardEntry(board);

    }

    public static long createFullBoard(long[] whitePieces, long[] blackPieces){
        long board = 0L;
        for(long l : whitePieces){
            board = board | l;
        }
        for(long l : blackPieces){
            board = board | l;
        }
        return board;
    }

    private static long createFullBoard(boolean isWhite,long[] whitePieces, long[] blackPieces){
        long board = 0L;
        if(isWhite){
            for(long l : whitePieces){
                board = board | l;
            }
        }
        else{
            for(long l : blackPieces){
                board = board | l;
            }
        }


        return board;
    }

    public static String getPieceType(int x, int y, Boolean isWhite, BitBoardWrapper board){
       int indx = GeneralChessFunctions.getBoardWithPiece(x,y,isWhite,board);
           return switch (indx) {
            case 0 -> "Pawn";
            case 1 -> "Knight";
            case 2 -> "Bishop";
            case 3 -> "Rook";
            case 4 -> "Queen";
            case 5 -> "King";
            default -> "null";
        };
    }



    /// this




    private static int positionToBitIndex(int x, int y){
        return  x + y * 8;
    }


    private static boolean willResultInDead(int x, int y, int newX, int newY, boolean isWhite, BitBoardWrapper board){
        long[] whitePieces = board.getWhitePieces();
        long[] blackPieces = board.getBlackPieces();
        long[] mystuff = isWhite ?  whitePieces : blackPieces;
        long[] enemy = isWhite ?  blackPieces : whitePieces;
        // checking if a move will result in being checked
        // i 0-1 will be queen or bishop, and i 2-3 will be rook or queen
        int[] dx = {1,1,0,-1};
        int[] dy = {1,-1,-1,0};
        for(int i = 0; i<dx.length;i++) {
            // find edges on either side
            boolean hitking = false;
            boolean hitEnemy = false;
            // 0 = no king, 1 = king on side 1, -1 = king on side 2
            int x1 = x + dx[i];
            int y1 = y + dy[i];

            while(GeneralChessFunctions.isValidMove(x1,y1)){
                if(x1 == newX && y1 == newY){
                    break;
                }
                if(GeneralChessFunctions.checkIfContains(x1,y1,mystuff[5])){
                    hitking = true;
                    break;
                }
                else if(GeneralChessFunctions.checkIfContains(x1,y1,!isWhite,board)){
                    int peiceType = GeneralChessFunctions.getBoardWithPiece(x1,y1,!isWhite,board);
                    if(i < 2){
                        // bishop/queen possibility
                        if(peiceType == 4 || peiceType == 2){
                            hitEnemy = true;
                        }
                    }
                    else{
                        if(peiceType == 4 || peiceType == 3){
                            hitEnemy = true;
                        }
                    }
                    break;
                }
                else if(GeneralChessFunctions.checkIfContains(x1,y1,isWhite,board)){
                    break;
                }
                x1 += dx[i];
                y1 += dy[i];
            }
            int x2 = x - dx[i];
            int y2 = y - dy[i];

            while(GeneralChessFunctions.isValidMove(x2,y2)){
                if(x2 == newX && y2 == newY){
                    break;
                }
                if(GeneralChessFunctions.checkIfContains(x2,y2,mystuff[5])){
                    hitking = true;
                    break;
                }
                else if(GeneralChessFunctions.checkIfContains(x2,y2,!isWhite,board)){
                    int peiceType = GeneralChessFunctions.getBoardWithPiece(x2,y2,!isWhite,board);
                    if(i < 2){
                        // bishop/queen possibility
                        if(peiceType == 4 || peiceType == 2){
                            hitEnemy = true;
                        }
                    }
                    else{
                        if(peiceType == 4 || peiceType == 3){
                            hitEnemy = true;
                        }
                    }
                    break;
                }
                else if(GeneralChessFunctions.checkIfContains(x2,y2,isWhite,board)){
                    break;
                }
                x2 -= dx[i];
                y2 -= dy[i];
            }
            if(hitking && hitEnemy){
                return true;
            }


        }
        return false;
    }



    private static List<XYcoord> calculatePawnMoves(int x, int y, boolean isWhite, BitBoardWrapper board, boolean isforcheck){
        ArrayList<XYcoord> moves = new ArrayList<>();
        int pawnHome = isWhite ? 6 : 1;
        int pawnEnd = isWhite ? 0 : 7;
        int move = isWhite ? -1 : 1;
        int eatY = y + move;
        int eatX1 = x + 1;
        int eatX2 = x - 1;
        if(GeneralChessFunctions.checkIfContains(eatX1,eatY,!isWhite, board) && GeneralChessFunctions.isValidMove(eatX1,eatY)){
            // pawn can capture to the right
            if(isforcheck){
                moves.add(new XYcoord(eatX1,eatY));
            }
            else if(!willResultInDead(x,y,eatX1,eatY, isWhite,board)){
                moves.add(new XYcoord(eatX1,eatY));

            }
        }
        if(GeneralChessFunctions.checkIfContains(eatX2,eatY,!isWhite, board) && GeneralChessFunctions.isValidMove(eatX2,eatY)){
            // pawn can capture to the left
            if(isforcheck){
                moves.add(new XYcoord(eatX2,eatY));
            }
            else if(!willResultInDead(x,y,eatX2,eatY, isWhite,board)){
                moves.add(new XYcoord(eatX2,eatY));

            }
        }
        int depth = y == pawnHome ? 2 : 1;

        for(int i = 1; i< depth+1;i++){
            int newY = y + i*move;
            // pawns cannot eat forwards
            if(!GeneralChessFunctions.checkIfContains(x,newY,isWhite, board) && !GeneralChessFunctions.checkIfContains(x,newY,!isWhite, board) && GeneralChessFunctions.isValidMove(x,newY)){
                // pawn can capture to the right
                if(isforcheck){
                    moves.add(new XYcoord(x,newY));
                }
                else if(!willResultInDead(x,y,x,newY, isWhite,board)){
                    moves.add(new XYcoord(x,newY));

                }
                else{
                    break;
                }
            }
            else{
                break;
            }
        }
        moves.forEach(m-> {if(m.y == pawnEnd){ m.setPawnPromo(true); }});
        return moves;

    }

    private static List<XYcoord> calculateKnightMoves(int x, int y, boolean isWhite, boolean edgesOnly, BitBoardWrapper board, boolean isforcheck) {
        ArrayList<XYcoord> moves = new ArrayList<>();

        int[] dx = {1, 2, 2, 1, -1, -2, -2, -1};
        int[] dy = {-2, -1, 1, 2, 2, 1, -1, -2};

        for (int i = 0; i < 8; i++) {
            int newX = x + dx[i];
            int newY = y + dy[i];
            if (GeneralChessFunctions.isValidMove(newX, newY) && !GeneralChessFunctions.checkIfContains(newX, newY, isWhite, board)) {
                if(edgesOnly){
                    if(GeneralChessFunctions.checkIfContains(newX,newY,!isWhite, board)){
                        moves.add(new XYcoord(newX, newY));
                    }
                }
                else if(isforcheck || !willResultInDead(x,y,newX,newY, isWhite,board)){
                    moves.add(new XYcoord(newX,newY));

                }
            }
        }

        return moves;
    }

    private static List<XYcoord> calculateBishopMoves(int x, int y, boolean isWhite, boolean edgesOnly, boolean directionCheck, BitBoardWrapper board, boolean isForCheck) {
        ArrayList<XYcoord> moves = new ArrayList<>();

        int[] dx = {1, 1, -1, -1};
        int[] dy = {1, -1, 1, -1};

        for (int i = 0; i < 4; i++) {
            int newX = x;
            int newY = y;
            while (true) {
                newX += dx[i];
                newY += dy[i];
                boolean willDie = false;
                if(!isForCheck){
                    willDie = willResultInDead(x,y,newX,newY, isWhite,board);
                }
                if(GeneralChessFunctions.isValidMove(newX, newY) && !willDie) {
                    boolean containsFriend = GeneralChessFunctions.checkIfContains(newX, newY, isWhite,board);
                    boolean containsEnemy = GeneralChessFunctions.checkIfContains(newX, newY, !isWhite,board);

                    XYcoord response = directionCheck ? new XYcoord(newX,newY,i) : new XYcoord(newX,newY);
                    if(containsEnemy){
                        moves.add(response);
                        break;
                    }
                    if (!containsFriend) {
                        if(!edgesOnly){
                            moves.add(response);
                        }
                    }
                    else{
                        break;
                    }

                } else {

                    break;
                }
            }
        }

        return moves;
    }

    private static List<XYcoord> calculateRookMoves(int x, int y, boolean isWhite, boolean edgesOnly, boolean directionCheck, BitBoardWrapper board, boolean isForCheck) {
        ArrayList<XYcoord> moves = new ArrayList<>();

        int[] dx = {1, -1, 0, 0};
        int[] dy = {0, 0, 1, -1};
        for (int i = 0; i < 4; i++) {
            int newX = x;
            int newY = y;
            while (true) {
                newX += dx[i];
                newY += dy[i];
                boolean willDie = false;

                if(!isForCheck){
                    willDie = willResultInDead(x,y,newX,newY, isWhite,board);
                }
                if (GeneralChessFunctions.isValidMove(newX, newY) && !willDie) {
                    boolean containsFriend = GeneralChessFunctions.checkIfContains(newX, newY, isWhite,board);
                    boolean containsEnemy = GeneralChessFunctions.checkIfContains(newX, newY, !isWhite,board);

                    XYcoord response = directionCheck ? new XYcoord(newX,newY,i) : new XYcoord(newX,newY);
                    if(containsEnemy){
                        moves.add(response);
                        break;
                    }
                    if (!containsFriend) {
                        if(!edgesOnly){
                            moves.add(response);
                        }
                    }
                    else{
                        break;
                    }



                } else {
                    break;
                }
            }
        }

        return moves;
    }

    private static List<XYcoord> calculateQueenMoves(int x, int y, boolean isWhite, boolean edgesOnly, BitBoardWrapper board, boolean isforCheck) {
        ArrayList<XYcoord> moves = new ArrayList<>();

        List<XYcoord> rookMoves = calculateRookMoves(x, y, isWhite,edgesOnly,false,board,isforCheck);
        List<XYcoord> bishopMoves = calculateBishopMoves(x, y, isWhite,edgesOnly,false,board,isforCheck);

        moves.addAll(rookMoves);
        moves.addAll(bishopMoves);

        return moves;
    }

    private static List<XYcoord> basicKingMoveCalc(int x, int y, boolean isWhite, BitBoardWrapper board){
        ArrayList<XYcoord> moves = new ArrayList<>();
        int[] dx = {1, -1, 0, 0, 1, -1, 1, -1};
        int[] dy = {0, 0, 1, -1, 1, -1, -1, 1};

        for (int i = 0; i < 8; i++) {
            int newX = x + dx[i];
            int newY = y + dy[i];
            if (GeneralChessFunctions.isValidMove(newX, newY) && !GeneralChessFunctions.checkIfContains(newX, newY, isWhite,board)) {
                moves.add(new XYcoord(newX,newY));
            }
        }
        return moves;
    }

    private static List<XYcoord> calculateKingMoves(int x, int y, boolean isWhite, BitBoardWrapper board,ChessStates gameState) {

        ArrayList<XYcoord> moves = new ArrayList<>();
        boolean canCastle = isWhite ? gameState.isWhiteCastleRight() : gameState.isBlackCastleRight();
        boolean shortRook = isWhite ? gameState.isWhiteShortRookRight() : gameState.isBlackShortRookRight();
        boolean longRook = isWhite ? gameState.isWhiteLongRookRight() : gameState.isBlackLongRookRight();

        if(canCastle){
            // short castle
            if(!GeneralChessFunctions.checkIfContains(x+1,y,isWhite,board) && !GeneralChessFunctions.checkIfContains(x+2,y,isWhite,board) && shortRook && !isChecked(x+1,y,isWhite,board) && !isChecked(x+2,y,isWhite,board) && GeneralChessFunctions.isValidMove(x+2,y)){
                moves.add(new XYcoord(x+2,y,true));
            }
            // long castle
            if(!GeneralChessFunctions.checkIfContains(x-1,y,isWhite,board) && !GeneralChessFunctions.checkIfContains(x-2,y,isWhite,board) && !GeneralChessFunctions.checkIfContains(x-3,y,isWhite,board) && !isChecked(x-1,y,isWhite,board) && !isChecked(x-2,y,isWhite,board) && !isChecked(x-3,y,isWhite,board) && longRook && GeneralChessFunctions.isValidMove(x-3,y)){
                moves.add(new XYcoord(x-3,y,true));

            }
        }

        int[] dx = {1, -1, 0, 0, 1, -1, 1, -1};
        int[] dy = {0, 0, 1, -1, 1, -1, -1, 1};

        for (int i = 0; i < 8; i++) {
            int newX = x + dx[i];
            int newY = y + dy[i];

            boolean isValid = GeneralChessFunctions.isValidMove(newX, newY);
            if(isValid){
                board.makeTempChange(x,y,newX,newY,5,isWhite);
                boolean newSqareIsChecked = isChecked(newX,newY,isWhite,board);
                board.popTempChange();
                if (!GeneralChessFunctions.checkIfContains(newX, newY, isWhite,board) && !newSqareIsChecked) {
                    moves.add(new XYcoord(newX,newY));
                }

            }


        }
        return moves;
    }

    public static boolean isAnyNotMovePossible(boolean isWhite, BitBoardWrapper board, ChessStates gameState){
        List<XYcoord> peices = GeneralChessFunctions.getPieceCoordsForComputer(isWhite ? board.getWhitePieces() : board.getBlackPieces());
        for(XYcoord pcoord : peices){
            List<XYcoord> piecePossibleMoves = getPossibleMoves(pcoord.x,pcoord.y,isWhite,board,gameState,pcoord.peiceType);
            if(!piecePossibleMoves.isEmpty()){
                return false;
            }
        }
        return true;
    }










    public static Boolean isChecked(boolean isWhite, BitBoardWrapper board){
      XYcoord kingLocation = isWhite ? board.getWhiteKingLocation() : board.getBlackKingLocation();
//    XYcoord kingLocation = getPieceCoords(isWhite ? board.getWhitePieces()[5] : board.getBlackPieces()[5]).get(0);
        return isChecked(kingLocation.x,kingLocation.y,isWhite,board);


        // king coordinates
    }

    private static boolean isChecked(int x, int y, boolean isWhite, BitBoardWrapper board){
        // general checking if a square is checked
        List<XYcoord> possibleRookFiles = calculateRookMoves(x,y,isWhite,true,false,board,true);
        List<XYcoord> possibleBishopFiles = calculateBishopMoves(x,y,isWhite,true,false,board,true);
        List<XYcoord> possibleHorseJumps = calculateKnightMoves(x,y,isWhite,true,board,true);
        List<XYcoord> possibleKingMoves = basicKingMoveCalc(x,y,isWhite,board);

        // check pawns
        int jump = isWhite ? 1 : -1;
        if(getPieceType(x-jump,y-jump,!isWhite,board).equals("Pawn") || getPieceType(x+jump,y-jump,!isWhite,board).equals("Pawn")){
            return true;
        }
        for(XYcoord s : possibleKingMoves){
            String peiceType = getPieceType(s.x,s.y,!isWhite,board);
            if(peiceType.equals("King")){
                return true;
            }
        }
        for(XYcoord s : possibleRookFiles){
            String peiceType = getPieceType(s.x,s.y,!isWhite,board);
            if(peiceType.equals("Rook") || peiceType.equals("Queen")){
                return true;
            }
        }
        for(XYcoord s : possibleHorseJumps){
            String peiceType = getPieceType(s.x,s.y,!isWhite,board);
            if(peiceType.equals("Knight")){
                return true;
            }
        }
        for(XYcoord s : possibleBishopFiles){
            String peiceType = getPieceType(s.x,s.y,!isWhite,board);
            if(peiceType.equals("Bishop") || peiceType.equals("Queen")){
                return true;
            }
        }
        return false;
    }



    private static List<XYcoord> getCheckedFile(boolean isWhite, BitBoardWrapper board){
        // general checking if a square is checked
        XYcoord kingLocation = isWhite ? board.getWhiteKingLocation() : board.getBlackKingLocation();
//      XYcoord kingLocation = getPieceCoords(isWhite ? board.getWhitePieces()[5] : board.getBlackPieces()[5]).get(0);

        int x = kingLocation.x;
        int y = kingLocation.y;
        List<XYcoord> specialMoves = new LinkedList<>();
        List<XYcoord> possibleRookFiles = calculateRookMoves(x,y,isWhite,true,true,board,true);
        List<XYcoord> possibleBishopFiles = calculateBishopMoves(x,y,isWhite,true,true,board,true);
        List<XYcoord> possibleHorseJumps = calculateKnightMoves(x,y,isWhite,true,board,true);
        // check pawns
        int jump = isWhite ? 1 : -1;

        if(getPieceType(x-jump,y-jump,!isWhite,board).equals("Pawn")){
            specialMoves.add(new XYcoord(x-jump,y-jump));
            return specialMoves;
        }

        if(getPieceType(x+jump,y-jump,!isWhite,board).equals("Pawn")){
            specialMoves.add(new XYcoord(x+jump,y-jump));
            return specialMoves;

        }

        for(XYcoord s : possibleRookFiles){
            String peiceType = getPieceType(s.x,s.y,!isWhite,board);
            if(peiceType.equals("Rook") || peiceType.equals("Queen")){
                List<XYcoord> filtered = calculateRookMoves(x,y,isWhite,false,true,board,true).stream().filter(g -> g.direction == s.direction).toList();
                filtered.forEach(g -> g.direction = -10);
                return filtered;

            }
        }
        for(XYcoord s : possibleHorseJumps){
            String peiceType = getPieceType(s.x,s.y,!isWhite,board);
            if(peiceType.equals("Knight")){
                specialMoves.add(new XYcoord(s.x,s.y));
                return specialMoves;



            }
        }
        for(XYcoord s : possibleBishopFiles){
            String peiceType = getPieceType(s.x,s.y,!isWhite,board);

            if(peiceType.equals("Bishop") || peiceType.equals("Queen")){
                List<XYcoord> filtered = calculateBishopMoves(x,y,isWhite,false,true,board,true).stream().filter(g -> g.direction == s.direction).toList();
                filtered.forEach(g -> g.direction = -10);
                return filtered;


            }
        }
        return Collections.emptyList();
    }




    int[] valueMap = {1,3,3,5,9,100000};




    public static boolean isCheckmated(boolean isWhite, BitBoardWrapper board, ChessStates gameState){
        return isChecked(isWhite,board) && isAnyNotMovePossible(isWhite, board,gameState);
    }

    public static boolean isCheckmated(BitBoardWrapper board, ChessStates gameState ){
        return (isChecked(false,board) && isAnyNotMovePossible(false, board,gameState))||( isChecked(true,board) && isAnyNotMovePossible(true, board,gameState));
    }




    private void createBoardEntry(BitBoardWrapper board){
        if (moveIndx != boardSave.size() - 1) {
            clearIndx();
        }
        logger.info("Creating board entry");

        boardSave.add(board.cloneBoard());
        //System.out.println("Save size: " + boardSave.size());

        moveIndx ++;
        maxIndex = moveIndx;

    }

    public void clearIndx(){
        int to = boardSave.size();
        if (to > moveIndx + 1) {
            logger.debug(String.format("Clearing board entries from %d",moveIndx+1));

            boardSave.subList(moveIndx + 1, to).clear();
        }


    }

    private BitBoardWrapper getPeicesFromSave(){
        BitBoardWrapper boardFromSave = null;
        if(moveIndx < 0){
            boardFromSave = startBoardState;
        }
        else{
            boardFromSave = boardSave.get(moveIndx);
        }


        return boardFromSave;


    }
















    public static List<String>[] getChangesNeeded(BitBoardWrapper currentBoard,BitBoardWrapper newBoard){
        // given two boards, find what needs to be added and removed so that you can make minimal graphical changes
        long[] whitePiecesCurrent = currentBoard.getWhitePieces();
        long[] blackPiecesCurrent = currentBoard.getBlackPieces();

        long[] whitePiecesNew = newBoard.getWhitePieces();
        long[] blackPiecesNew = newBoard.getBlackPieces();


        List<String> changesAdd = new ArrayList<>();
        List<String> changesRemove = new ArrayList<>();
        for(int i = 0; i<whitePiecesCurrent.length;i++){
            long old = whitePiecesNew[i];
            long cur = whitePiecesCurrent[i];
            //System.out.println(whitePeicesOld[i]);
            //System.out.println(whitePieces[i]);
            long xorResult = old ^ cur;

            // Find missing bit indices to add
            for (int z = 0; z < 64; z++) {
                long mask = 1L << z;
                if ((xorResult & mask) != 0 && (old & mask) != 0) {
                    int[] coords = bitindexToXY(z);
                    changesAdd.add(coords[0] + "," + coords[1] + ",w," + i);
                }
                if ((xorResult & mask) != 0 && (cur  & mask) != 0) {
                    int[] coords = bitindexToXY(z);
                    changesRemove.add(coords[0] + "," + coords[1] + ",w," + i);
                }
            }
            // Find missing bit indices to remove


        }
        for(int i = 0; i<blackPiecesCurrent.length;i++){
            long old = blackPiecesNew[i];
            long cur = blackPiecesCurrent[i];

            long xorResult = old ^ cur;

            // Find missing bit indices to add
            for (int z = 0; z < 64; z++) {
                long mask = 1L << z;
                if ((xorResult & mask) != 0 && (old & mask) != 0) {
                    int[] coords = bitindexToXY(z);
                    changesAdd.add(coords[0] + "," + coords[1] + ",b," + i);
                }
                if ((xorResult & mask) != 0 && (cur  & mask) != 0) {
                    int[] coords = bitindexToXY(z);
                    changesRemove.add(coords[0] + "," + coords[1] + ",b," + i);
                }
            }
            // Find missing bit indices to remove


        }
        return new List[]{changesAdd,changesRemove};
    }







    private static int[] bitindexToXY(int bitIndex){
        return new int[] {bitIndex%8, bitIndex/8};
    }

    private List<String> getPieceCoords(boolean isWhite,long[] whitePieces,long[] blackPieces){
        long[] pieces = isWhite ? whitePieces : blackPieces;
        List<String> summedCoords = new ArrayList<>();
        for(int i = 0; i< pieces.length;i++){
            int j = i;
            summedCoords.addAll(getPieceCoords(pieces[i]).stream().map(s -> s + "," + j).toList());
        }
        return summedCoords;
    }
    public List<XYcoord> getPieceCoords(long board) {
        List<XYcoord> coord = new ArrayList<>();

        for (int z = 0; z < 64; z++) {
            long mask = 1L << z;

            if ((board & mask) != 0) {
                int[] coords = bitindexToXY(z);
                coord.add(new XYcoord(coords[0],coords[1]));
            }
        }

        return coord;
    }

    private void addPiece(int boardIndx, int bitIndex, boolean isWhite, long[] whitePieces, long[] blackPieces) {
        // Create a mask with the bit at bitIndex set to 1
        long board = isWhite ? whitePieces[boardIndx] : blackPieces[boardIndx];

        long mask = 1L << bitIndex;
        long result = board | mask;
        if(isWhite){
            whitePieces[boardIndx] = result;
        }
        else{
            blackPieces[boardIndx] =result;
        }
        int jump = isWhite ? 0 : 6;
        peicesOnBoard[jump+boardIndx]++;
        System.out.println("Adding peice");
        // Use bitwise OR to add the piece to the board
    }
    private void removePeice(int boardIndx, int bitIndex, boolean isWhite, long[] whitePieces, long[] blackPieces) {
        // Create a mask with the bit at bitIndex set to 1
        long board = isWhite ? whitePieces[boardIndx] : blackPieces[boardIndx];


        long mask = 1L << bitIndex;
        long result = board & ~mask;
        if(isWhite){
            whitePieces[boardIndx] = result;
        }
        else{
            blackPieces[boardIndx] =result;
        }
        int jump = isWhite ? 0 : 6;
        peicesOnBoard[jump+boardIndx]--;
        System.out.println("Removing peice");

    }

    private void MatrixToString(ImageView[][] matrix){
        for(int i = 0; i< matrix.length;i++){
            for(int j = 0; j< matrix[i].length;j++){
                System.out.print((matrix[j][i] != null ? "X" : "_" )+ " ");
            }
            System.out.println();
        }
    }

    public int[] parseStrCoord(String s){
        return Arrays.stream(s.split(",")).mapToInt(Integer::parseInt).toArray();
    }

    public  void printBitboard(long bitboard) {
        System.out.println("bitboard:");
        for (int row = 7; row >= 0; row--) {
            for (int col = 0; col < 8; col++) {
                int index = row * 8 + col;
                long mask = 1L << index;
                char square = ((bitboard & mask) != 0) ? '1' : '0';
                System.out.print(square + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public static List<XYcoord> fullPawnMoveCalcPGN(int x, int y, boolean isWhite,boolean isEating, BitBoardWrapper board){
        ArrayList<XYcoord> moves = new ArrayList<>();
        int pawnEnd = isWhite ? 0 : 7;
        int pawnHome = isWhite ? 6 : 1;

        int move = isWhite ? -1 : 1;
        if(isEating){
            int eatY = y + move;
            int eatX1 = x + 1;
            int eatX2 = x - 1;
            moves.add(new XYcoord(eatX1,eatY));
            moves.add(new XYcoord(eatX2,eatY));
        }
        else{
            int depth = 1;
            if(y == pawnHome){
                depth = 2;

            }
            for(int i = 1; i< depth+1;i++) {
                int newY = y + i * move;
                moves.add(new XYcoord(x, newY));
            }
        }




        moves.forEach(m-> {if(m.y == pawnEnd){ m.setPawnPromo(true); }});
        return moves;


    }

    public static XYcoord findOldCoordinates(int newX, int newY, int pieceType,int AmbigousFile,boolean isWhite,boolean isEating,BitBoardWrapper board,ChessStates gameState){
        List<XYcoord> possibleOrigins;
        if(pieceType == ChessConstants.KINGINDEX){
            possibleOrigins = AdvancedChessFunctions.basicKingMoveCalc(newX,newY,!isWhite,board);

        }
        else if(pieceType == ChessConstants.PAWNINDEX){
            possibleOrigins = AdvancedChessFunctions.fullPawnMoveCalcPGN(newX,newY,!isWhite,isEating,board);
        }
        else {
            possibleOrigins = AdvancedChessFunctions.getMoveOfType(newX,newY,!isWhite,pieceType,board,gameState,true);

        }
        for(XYcoord c : possibleOrigins){
            boolean isPieceThere = GeneralChessFunctions.checkIfContains(c.x,c.y,isWhite ? board.getWhitePieces()[pieceType] : board.getBlackPieces()[pieceType]);
            if(isPieceThere){
                if(AmbigousFile == ChessConstants.EMPTYINDEX){
                    return c;
                }
                else if(c.x == AmbigousFile){
                    return c;
                }
            }

        }
        ChessConstants.mainLogger.error("Failed to find old coordinates");
        return null;
    }
    // returns -1 if not ambigous, 1 for singular ambiguity, 2 for double ambiguity and 3 for triple ambiguity
    public static int isAmbigousMove(int moveX,int moveY,int pieceType,boolean isWhiteMove,boolean isEatingMove,BitBoardWrapper board){
        List<XYcoord> possibleOrigins;
        if(pieceType == ChessConstants.KINGINDEX){
            possibleOrigins = AdvancedChessFunctions.basicKingMoveCalc(moveX,moveY,!isWhiteMove,board);

        }
        else if(pieceType == ChessConstants.PAWNINDEX){
            possibleOrigins = AdvancedChessFunctions.fullPawnMoveCalcPGN(moveX,moveY,!isWhiteMove,isEatingMove,board);
        }
        else {
            // dont need gamestate so passing in dummy variable
            possibleOrigins = AdvancedChessFunctions.getMoveOfType(moveX,moveY,!isWhiteMove,pieceType,board,ChessConstants.NEWGAMESTATE,true);
        }
        long count = possibleOrigins.stream().filter(p -> GeneralChessFunctions.checkIfContains(p.x,p.y,isWhiteMove ? board.getWhitePieces()[pieceType] : board.getBlackPieces()[pieceType])).count();
        return (int) count;

    }

    public static int getColumnGivenFile(int file,int columnHint,boolean isWhite,long board){
        int dir = isWhite ? 1 : -1;
        int end = isWhite ? 8 : -1;
        int reps = 0;
        for(int column = columnHint+dir;column != end;column+=dir){
            if(GeneralChessFunctions.checkIfContains(file,column,board)){
                return column;
            }
            reps++;
            if(reps > 9){
                break;
            }
        }
        ChessConstants.mainLogger.error("No coulmn found for given file");

        return ChessConstants.EMPTYINDEX;

    }


    private static final int[] pieceValues = new int[]{1,3,3,5,9};
    public static int getSimpleAdvantage(BitBoardWrapper board){
        int totalValue = 0;
        long[] whitePieces = board.getWhitePieces();
        long[] blackPieces = board.getBlackPieces();
        for(int i = 0;i<5;i++){
            // white pieces considered positive value, black negative
            totalValue += GeneralChessFunctions.getPieceCoords(whitePieces[i]).size()*pieceValues[i];
            totalValue -= GeneralChessFunctions.getPieceCoords(blackPieces[i]).size()*pieceValues[i];
        }
        return totalValue;
    }

    // more pgn related stuff, all used by the chessgame class




















}
