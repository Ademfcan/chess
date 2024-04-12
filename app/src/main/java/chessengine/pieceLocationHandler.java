package chessengine;

import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;


import java.util.*;

public class pieceLocationHandler {

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
    public long[] blackPiecesC = {blackPawns, blackKnights, blackBishops, blackRooks, blackQueens, blackKings};
    public long[] whitePiecesC = {whitePawns, whiteKnights, whiteBishops, whiteRooks, whiteQueens, whiteKings};

    private final long[] blackPiecesStart = {blackPawns, blackKnights, blackBishops, blackRooks, blackQueens, blackKings};
    private final long[] whitePiecesStart = {whitePawns, whiteKnights, whiteBishops, whiteRooks, whiteQueens, whiteKings};


    private ArrayList<long[][]> boardSave = new ArrayList<>();

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

    private boolean gameOver;

    private HBox eatenWhites;
    private HBox eatenBlacks;

    private GridPane chessPeiceBoard;



    public pieceLocationHandler(boolean gameOver, HBox eatenWhites, HBox eatenBlacks, GridPane chessPeiceboard) {
        this.gameOver = gameOver;
        this.eatenWhites = eatenWhites;
        this.eatenBlacks = eatenBlacks;
        this.chessPeiceBoard = chessPeiceboard;
    }

    public void removePeice(boolean isWhite,int x ,int y,boolean istest, long[] whitePieces, long[] blackPieces,boolean isPromo){
        int from  = positionToBitIndex(x, y);
        long mask = ~(1L << from); // Create a mask with a 0 at the index to be cleared
        int indx = chessFunctions.getBoardWithPiece(x,y,isWhite,whitePieces,blackPieces);


        long currbitboard;

        if(isWhite){
            currbitboard = whitePieces[indx];
        }
        else{
            currbitboard = blackPieces[indx];

        }


        currbitboard = (currbitboard & mask);

        if(isWhite){
            whitePieces[indx] = currbitboard;
        }
        else{
            blackPieces[indx] = currbitboard;

        }
        if(!istest){
            createEntryForRemoved(indx,isWhite,isPromo,whitePieces,blackPieces);
        }
    }

    public void createEntryForRemoved(int indx, boolean isWhite,boolean isPromo,long[] whitePieces, long[] blackPieces){
        ImageView smallPeice = chessBoardGUIHandler.createNewPeice(indx,isWhite,chessPeiceBoard,true);
        smallPeice.setUserData(Integer.toString(indx));
        int jump = isWhite ? 0 : 6;
        if(!isPromo){
            createBoardEntry(whitePieces,blackPieces);
        }
        if(isWhite){
            eatenWhites.getChildren().add(smallPeice);
        }
        else{
            eatenBlacks.getChildren().add(smallPeice);
        }

        peicesOnBoard[jump+indx]--;
    }



    public List<XYcoord> getPossibleMoves(int x, int y, boolean isWhite, long[] whitePieces, long[] blackPieces){
        int indx = chessFunctions.getBoardWithPiece(x,y,isWhite,whitePieces,blackPieces);
        //System.out.println("Getting moves for peice at " + x + " " + y);
        List<XYcoord> baseMoves = getMoveOfType(x,y,isWhite,indx,whitePieces,blackPieces);
        if(isChecked(isWhite,whitePieces,blackPieces)){;
            if(indx != 5){

                baseMoves.retainAll(getCheckedFile(isWhite,whitePieces,blackPieces));

            }
            return baseMoves;
        }
        else{
            return baseMoves;
        }

    }

    private List<XYcoord> getMoveOfType(int x, int y, boolean isWhite, int indx, long[] whitePieces, long[] blackPieces){
        return switch (indx) {
            case 0 -> calculatePawnMoves(x, y, isWhite,whitePieces,blackPieces,false);
            case 1 -> calculateKnightMoves(x, y, isWhite, false,whitePieces,blackPieces,false);
            case 2 -> calculateBishopMoves(x, y, isWhite, false,false,whitePieces,blackPieces,false);
            case 3 -> calculateRookMoves(x, y, isWhite, false,false,whitePieces,blackPieces,false);
            case 4 -> calculateQueenMoves(x, y, isWhite, false,whitePieces,blackPieces,false);
            case 5 -> calculateKingMoves(x, y, isWhite,whitePieces,blackPieces);
            default -> null;
        };
    }


    public void movePiece(boolean isWhite, int OldX, int OldY, int NewX, int NewY,int indx ,boolean isRemove,boolean isTest, long[] whitePieces, long[] blackPieces){
        int from  = positionToBitIndex(OldX, OldY);
        int to  = positionToBitIndex(NewX, NewY);
        long clearMask = ~(1L << from);

        // Set the bit at 'to' position to 1 using a left shift.
        long setBit = 1L << to;
        long currbitboard;

        if(isWhite){
            currbitboard = whitePieces[indx];
        }
        else{
            currbitboard = blackPieces[indx];

        }


        // Use the mask to clear the 'from' bit and then set the 'to' bit.
        currbitboard = (currbitboard & clearMask) | setBit;

        if(isWhite){
            whitePieces[indx] = currbitboard;
        }
        else{
            blackPieces[indx] = currbitboard;

        }

        if(!isRemove && !isTest){
            createBoardEntry(whitePieces,blackPieces);
        }

    }


    public void movePiece(boolean isWhite, int OldX, int OldY, int NewX, int NewY, boolean isRemove,boolean isTest, long[] whitePieces, long[] blackPieces){
        int indx = chessFunctions.getBoardWithPiece(OldX,OldY,isWhite,whitePieces,blackPieces);
        movePiece(isWhite,OldX,OldY,NewX,NewY,indx,isRemove,isTest,whitePieces,blackPieces);




    }

    public void promoPawn(boolean isWhite,int oldX, int oldY,int X, int Y,int newPeiceIndx,long[] whitePieces,long[] blackPieces){
        long[] peices = isWhite ? whitePieces : blackPieces;
        long[] enmypeices = isWhite ? blackPieces : whitePieces;
        peices[0] = chessFunctions.RemovePeice(oldX,oldY,peices[0]);
        int enmyIndx = chessFunctions.getBoardWithPiece(X,Y,!isWhite,whitePieces,blackPieces);
        System.out.println(enmyIndx);
        if(enmyIndx != -10){
            enmypeices[enmyIndx] = chessFunctions.RemovePeice(X,Y,enmypeices[enmyIndx]);

            ImageView smallPeice = chessBoardGUIHandler.createNewPeice(enmyIndx,!isWhite,chessPeiceBoard,true);
            smallPeice.setUserData(Integer.toString(enmyIndx));
            if(!isWhite){
                eatenWhites.getChildren().add(smallPeice);
            }
            else{
                eatenBlacks.getChildren().add(smallPeice);
            }
        }
        peices[newPeiceIndx] = chessFunctions.AddPeice(X,Y,peices[newPeiceIndx]);
        createBoardEntry(whitePieces,blackPieces);

    }

    public long createFullBoard(long[] whitePieces, long[] blackPieces){
        long board = 0L;
        for(long l : whitePieces){
            board = board | l;
        }
        for(long l : blackPieces){
            board = board | l;
        }
        return board;
    }

    private long createFullBoard(boolean isWhite,long[] whitePieces, long[] blackPieces){
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

    public String getPieceType(int x, int y, Boolean isWhite,long[] whitePieces, long[] blackPieces){
       int indx = chessFunctions.getBoardWithPiece(x,y,isWhite,whitePieces,blackPieces);
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




    private int positionToBitIndex(int x, int y){
        return  x + y * 8;
    }


    private boolean willResultInDead(int x, int y, int newX, int newY, boolean isWhite, long[] whitePieces, long[] blackPieces){
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

            while(chessFunctions.isValidMove(x1,y1)){
                if(x1 == newX && y1 == newY){
                    break;
                }
                if(chessFunctions.checkIfContains(x1,y1,mystuff[5])){
                    hitking = true;
                    break;
                }
                else if(chessFunctions.checkIfContains(x1,y1,!isWhite,whitePieces,blackPieces)){
                    int peiceType = chessFunctions.getBoardWithPiece(x1,y1,!isWhite,whitePieces,blackPieces);
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
                else if(chessFunctions.checkIfContains(x1,y1,isWhite,whitePieces,blackPieces)){
                    break;
                }
                x1 += dx[i];
                y1 += dy[i];
            }
            int x2 = x - dx[i];
            int y2 = y - dy[i];

            while(chessFunctions.isValidMove(x2,y2)){
                if(x2 == newX && y2 == newY){
                    break;
                }
                if(chessFunctions.checkIfContains(x2,y2,mystuff[5])){
                    hitking = true;
                    break;
                }
                else if(chessFunctions.checkIfContains(x2,y2,!isWhite,whitePieces,blackPieces)){
                    int peiceType = chessFunctions.getBoardWithPiece(x2,y2,!isWhite,whitePieces,blackPieces);
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
                else if(chessFunctions.checkIfContains(x2,y2,isWhite,whitePieces,blackPieces)){
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


    int pawnHome;
    int pawnEnd;
    int move;
    int eatY;
    int eatX1;
    int eatX2;
    private List<XYcoord> calculatePawnMoves(int x, int y, boolean isWhite, long[] whitePieces, long[] blackPieces,boolean isforcheck){
        ArrayList<XYcoord> moves = new ArrayList<>();
        pawnHome = isWhite ? 6 : 1;
        pawnEnd = isWhite ? 0 : 7;
        move = isWhite ? -1 : 1;
        eatY = y + move;
        eatX1 = x + 1;
        eatX2 = x - 1;
        if(chessFunctions.checkIfContains(eatX1,eatY,!isWhite, whitePieces,blackPieces) && chessFunctions.isValidMove(eatX1,eatY)){
            // pawn can capture to the right
            if(isforcheck){
                moves.add(new XYcoord(eatX1,eatY));
            }
            else if(!willResultInDead(x,y,eatX1,eatY, isWhite,whitePieces,blackPieces)){
                moves.add(new XYcoord(eatX1,eatY));

            }
        }
        if(chessFunctions.checkIfContains(eatX2,eatY,!isWhite, whitePieces,blackPieces) &&chessFunctions.isValidMove(eatX2,eatY)){
            // pawn can capture to the left
            if(isforcheck){
                moves.add(new XYcoord(eatX2,eatY));
            }
            else if(!willResultInDead(x,y,eatX2,eatY, isWhite,whitePieces,blackPieces)){
                moves.add(new XYcoord(eatX2,eatY));

            }
        }
        int depth = y == pawnHome ? 2 : 1;

        for(int i = 1; i< depth+1;i++){
            int newY = y + i*move;
            // pawns cannot eat forwards
            if(!chessFunctions.checkIfContains(x,newY,isWhite, whitePieces,blackPieces) && !chessFunctions.checkIfContains(x,newY,!isWhite, whitePieces,blackPieces) && chessFunctions.isValidMove(x,newY)){
                // pawn can capture to the right
                if(isforcheck){
                    moves.add(new XYcoord(x,newY));
                }
                else if(!willResultInDead(x,y,x,newY, isWhite,whitePieces,blackPieces)){
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

    private List<XYcoord> calculateKnightMoves(int x, int y, boolean isWhite, boolean edgesOnly,long[] whitePieces, long[] blackPieces,boolean isforcheck) {
        ArrayList<XYcoord> moves = new ArrayList<>();

        int[] dx = {1, 2, 2, 1, -1, -2, -2, -1};
        int[] dy = {-2, -1, 1, 2, 2, 1, -1, -2};

        for (int i = 0; i < 8; i++) {
            int newX = x + dx[i];
            int newY = y + dy[i];
            if (chessFunctions.isValidMove(newX, newY) && !chessFunctions.checkIfContains(newX, newY, isWhite, whitePieces,blackPieces)) {
                if(edgesOnly){
                    if(chessFunctions.checkIfContains(newX,newY,!isWhite, whitePieces,blackPieces)){
                        moves.add(new XYcoord(newX, newY));
                    }
                }
                else if(isforcheck || !willResultInDead(x,y,newX,newY, isWhite,whitePieces,blackPieces)){
                    moves.add(new XYcoord(newX,newY));

                }
            }
        }

        return moves;
    }

    private List<XYcoord> calculateBishopMoves(int x, int y, boolean isWhite, boolean edgesOnly, boolean directionCheck, long[] whitePieces, long[] blackPieces, boolean isForCheck) {
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
                    willDie = willResultInDead(x,y,newX,newY, isWhite,whitePieces,blackPieces);
                }
                if(chessFunctions.isValidMove(newX, newY) && !willDie) {
                    boolean result = chessFunctions.checkIfContains(newX, newY, isWhite,whitePieces,blackPieces);
                    boolean result2 = chessFunctions.checkIfContains(newX, newY, !isWhite,whitePieces,blackPieces);

                    XYcoord response = directionCheck ? new XYcoord(newX,newY,i) : new XYcoord(newX,newY);
                    if(result2){
                        moves.add(response);
                        break;
                    }
                    if (!result) {
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

    private List<XYcoord> calculateRookMoves(int x, int y, boolean isWhite, boolean edgesOnly, boolean directionCheck, long[] whitePieces, long[] blackPieces,boolean isForCheck) {
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
                    willDie = willResultInDead(x,y,newX,newY, isWhite,whitePieces,blackPieces);
                }
                if (chessFunctions.isValidMove(newX, newY) && !willDie) {
                    boolean result = chessFunctions.checkIfContains(newX, newY, isWhite,whitePieces,blackPieces);
                    boolean result2 = chessFunctions.checkIfContains(newX, newY, !isWhite,whitePieces,blackPieces);

                    XYcoord response = directionCheck ? new XYcoord(newX,newY,i) : new XYcoord(newX,newY);
                    if(result2){
                        moves.add(response);
                        break;
                    }
                    if (!result) {
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

    private List<XYcoord> calculateQueenMoves(int x, int y, boolean isWhite, boolean edgesOnly, long[] whitePieces, long[] blackPieces,boolean isforCheck) {
        ArrayList<XYcoord> moves = new ArrayList<>();

        List<XYcoord> rookMoves = calculateRookMoves(x, y, isWhite,edgesOnly,false,whitePieces,blackPieces,isforCheck);
        List<XYcoord> bishopMoves = calculateBishopMoves(x, y, isWhite,edgesOnly,false,whitePieces,blackPieces,isforCheck);

        moves.addAll(rookMoves);
        moves.addAll(bishopMoves);

        return moves;
    }

    private List<XYcoord> basicKingMoveCalc(int x, int y, boolean isWhite, long[] whitePieces, long[] blackPieces){
        ArrayList<XYcoord> moves = new ArrayList<>();
        int[] dx = {1, -1, 0, 0, 1, -1, 1, -1};
        int[] dy = {0, 0, 1, -1, 1, -1, -1, 1};

        for (int i = 0; i < 8; i++) {
            int newX = x + dx[i];
            int newY = y + dy[i];
            if (chessFunctions.isValidMove(newX, newY) && !chessFunctions.checkIfContains(newX, newY, isWhite,whitePieces,blackPieces)) {
                moves.add(new XYcoord(newX,newY));
            }
        }
        return moves;
    }

    private List<XYcoord> calculateKingMoves(int x, int y, boolean isWhite,long[] whitePieces,long[] blackPieces) {

        ArrayList<XYcoord> moves = new ArrayList<>();
        boolean canCastle = isWhite ? whiteCastleRight : blackCastleRight;
        boolean shortRook = isWhite ? whiteShortRookMove : blackShortRookMove;
        boolean longRook = isWhite ? whiteLongRookMove : blackLongRookMove;

        if(canCastle){
            // short castle
            if(!chessFunctions.checkIfContains(x+1,y,isWhite,whitePieces,blackPieces) && !chessFunctions.checkIfContains(x+2,y,isWhite,whitePieces,blackPieces) && shortRook && !isChecked(x+1,y,isWhite,whitePieces,blackPieces) && !isChecked(x+2,y,isWhite,whitePieces,blackPieces)){
                moves.add(new XYcoord(x+2,y,true));
            }
            if(!chessFunctions.checkIfContains(x-1,y,isWhite,whitePieces,blackPieces) && !chessFunctions.checkIfContains(x-2,y,isWhite,whitePieces,blackPieces) && !chessFunctions.checkIfContains(x-3,y,isWhite,whitePieces,blackPieces) && !isChecked(x-1,y,isWhite,whitePieces,blackPieces) && !isChecked(x-2,y,isWhite,whitePieces,blackPieces) && !isChecked(x-3,y,isWhite,whitePieces,blackPieces) && longRook){
                moves.add(new XYcoord(x-3,y,true));

            }
        }

        int[] dx = {1, -1, 0, 0, 1, -1, 1, -1};
        int[] dy = {0, 0, 1, -1, 1, -1, -1, 1};

        for (int i = 0; i < 8; i++) {
            int newX = x + dx[i];
            int newY = y + dy[i];
            if (chessFunctions.isValidMove(newX, newY) && !chessFunctions.checkIfContains(newX, newY, isWhite,whitePieces,blackPieces) && !isChecked(newX,newY,isWhite,whitePieces,blackPieces)) {
                moves.add(new XYcoord(newX,newY));
            }
        }
        return moves;
    }

    public boolean isKingNotMovePossible(boolean isWhite, long[] whitePieces, long[] blackPieces){
        List<XYcoord> peices = chessFunctions.getPieceCoordsForComputer(isWhite ? whitePieces : blackPieces);
        for(XYcoord pcoord : peices){
            List<XYcoord> piecePossibleMoves = getPossibleMoves(pcoord.x,pcoord.y,isWhite,whitePieces,blackPieces);
            if(!piecePossibleMoves.isEmpty()){
                return false;
            }
        }
        return true;
    }





    private final String[] rookLocations = {"7,7,s,w","0,7,l,w","0,0,l,b","7,0,s,b"};
    public void removeRookMoveRight(int x, int y){
        System.out.println("Removing rook right at x: " + x + ", y: " + y);
        for(String s : rookLocations){
            String[] rInfo = s.split(",");
            int rookX = Integer.parseInt(rInfo[0]);
            int rookY = Integer.parseInt(rInfo[1]);
            if(rookX == x && rookY == y){
                if(rInfo[2].equals("s")){
                    if(rInfo[3].equals("w")){
                        whiteShortRookIndx = moveIndx;
                        whiteShortRookMove = false;
                    }
                    else{
                        blackShortRookIndx = moveIndx;
                        blackShortRookMove = false;
                    }
                }
                else{
                    if(rInfo[3].equals("w")){
                        whiteLongRookIndx = moveIndx;
                        whiteLongRookMove = false;
                    }
                    else{
                        blackShortRookIndx = moveIndx;
                        blackShortRookMove = false;
                    }
                }
            }
        }
    }

    public void removeCastlingRight(boolean isWhite){
        if(isWhite){
            if(whiteCastleRight){
                whiteCastleRight = false;
                whiteCastleIndx = moveIndx;
            }

        }
        else{
            if(blackCastleRight){
                blackCastleRight = false;
                blackCastleIndx = moveIndx;
            }

        }

    }


    public boolean isChecked(boolean isWhite, long[] whitePieces, long[] blackPieces){
        List<XYcoord> kingLocation = getPieceCoords(isWhite ? whitePieces[5] : blackPieces[5]);
        XYcoord coord = kingLocation.get(0);
        // king coordinates
        return isChecked(coord.x,coord.y,isWhite,whitePieces,blackPieces);
    }

    private boolean isChecked(int x, int y, boolean isWhite, long[] whitePieces, long[] blackPieces){
        // general checking if a square is checked
        List<XYcoord> possibleRookFiles = calculateRookMoves(x,y,isWhite,true,false,whitePieces,blackPieces,true);
        List<XYcoord> possibleBishopFiles = calculateBishopMoves(x,y,isWhite,true,false,whitePieces,blackPieces,true);
        List<XYcoord> possibleHorseJumps = calculateKnightMoves(x,y,isWhite,true,whitePieces,blackPieces,true);
        List<XYcoord> possibleKingMoves = basicKingMoveCalc(x,y,isWhite,whitePieces,blackPieces);

        // check pawns
        int jump = isWhite ? 1 : -1;
        if(getPieceType(x-jump,y-jump,!isWhite,whitePieces,blackPieces).equals("Pawn") || getPieceType(x+jump,y-jump,!isWhite,whitePieces,blackPieces).equals("Pawn")){
            return true;
        }
        for(XYcoord s : possibleKingMoves){
            String peiceType = getPieceType(s.x,s.y,!isWhite,whitePieces,blackPieces);
            if(peiceType.equals("King")){
                return true;
            }
        }
        for(XYcoord s : possibleRookFiles){
            String peiceType = getPieceType(s.x,s.y,!isWhite,whitePieces,blackPieces);
            if(peiceType.equals("Rook") || peiceType.equals("Queen")){
                return true;
            }
        }
        for(XYcoord s : possibleHorseJumps){
            String peiceType = getPieceType(s.x,s.y,!isWhite,whitePieces,blackPieces);
            if(peiceType.equals("Knight")){
                return true;
            }
        }
        for(XYcoord s : possibleBishopFiles){
            String peiceType = getPieceType(s.x,s.y,!isWhite,whitePieces,blackPieces);
            if(peiceType.equals("Bishop") || peiceType.equals("Queen")){
                return true;
            }
        }
        return false;
    }
    List<XYcoord> specialMoves = new ArrayList<>();



    private List<XYcoord> getCheckedFile(boolean isWhite,long[] whitePieces,long[] blackPieces){
        // general checking if a square is checked
        List<XYcoord> kingLocation = getPieceCoords(isWhite ? whitePieces[5] : blackPieces[5]);
        XYcoord coord = kingLocation.get(0);
        int x = coord.x;
        int y = coord.y;
        specialMoves.clear();
        List<XYcoord> possibleRookFiles = calculateRookMoves(x,y,isWhite,true,true,whitePieces,blackPieces,true);
        List<XYcoord> possibleBishopFiles = calculateBishopMoves(x,y,isWhite,true,true,whitePieces,blackPieces,true);
        List<XYcoord> possibleHorseJumps = calculateKnightMoves(x,y,isWhite,true,whitePieces,blackPieces,true);
        // check pawns
        int jump = isWhite ? 1 : -1;

        if(getPieceType(x-jump,y-jump,!isWhite,whitePieces,blackPieces).equals("Pawn")){
            specialMoves.add(new XYcoord(x-jump,y-jump));
            return specialMoves;
        }

        if(getPieceType(x+jump,y-jump,!isWhite,whitePieces,blackPieces).equals("Pawn")){
            specialMoves.add(new XYcoord(x+jump,y-jump));
            return specialMoves;

        }

        for(XYcoord s : possibleRookFiles){
            String peiceType = getPieceType(s.x,s.y,!isWhite,whitePieces,blackPieces);
            if(peiceType.equals("Rook") || peiceType.equals("Queen")){
                List<XYcoord> filtered = calculateRookMoves(x,y,isWhite,false,true,whitePieces,blackPieces,true).stream().filter(g -> g.direction == s.direction).toList();
                filtered.forEach(g -> g.direction = -10);
                return filtered;

            }
        }
        for(XYcoord s : possibleHorseJumps){
            String peiceType = getPieceType(s.x,s.y,!isWhite,whitePieces,blackPieces);
            if(peiceType.equals("Knight")){
                specialMoves.add(new XYcoord(s.x,s.y));
                return specialMoves;



            }
        }
        for(XYcoord s : possibleBishopFiles){
            String peiceType = getPieceType(s.x,s.y,!isWhite,whitePieces,blackPieces);

            if(peiceType.equals("Bishop") || peiceType.equals("Queen")){
                List<XYcoord> filtered = calculateBishopMoves(x,y,isWhite,false,true,whitePieces,blackPieces,true).stream().filter(g -> g.direction == s.direction).toList();
                filtered.forEach(g -> g.direction = -10);
                return filtered;


            }
        }
        return Collections.emptyList();
    }




    int[] valueMap = {1,3,3,5,9,100000};

    public int getSimpleEval(){
        int eval = 0;
        for(int i =0 ;i<peicesOnBoard.length;i++){
            if(i < 6){
                eval += peicesOnBoard[i] * valueMap[i];
            }
            else{
                eval -= peicesOnBoard[i] * valueMap[i-6];
            }
        }
        return eval;

    }


    public boolean isCheckmated(boolean isWhite,long[] whitePieces,long[] blackPieces){
        return isChecked(isWhite,whitePieces,blackPieces) && isKingNotMovePossible(isWhite, whitePieces, blackPieces);
    }

    public boolean isCheckmated(long[] whitePieces,long[] blackPieces){
        return (isChecked(false,whitePieces,blackPieces) && isKingNotMovePossible(false, whitePieces, blackPieces))||( isChecked(true,whitePieces,blackPieces) && isKingNotMovePossible(true, whitePieces, blackPieces));
    }




    private void createBoardEntry(long[] whitePieces, long[] blackPieces){
        if (moveIndx != boardSave.size() - 1) {
            clearIndx();
        }
        System.out.println("Saving..");

        boardSave.add(new long[][]{Arrays.copyOf(whitePieces,whitePieces.length),Arrays.copyOf(blackPieces,blackPieces.length)});
        //System.out.println("Save size: " + boardSave.size());

        moveIndx ++;
        maxIndex = moveIndx;

    }

    public void clearIndx(){
        int to = boardSave.size();
        if (to > moveIndx + 1) {
            System.out.println("Removing old entries");

            boardSave.subList(moveIndx + 1, to).clear();
        }


    }

    private long[][] getPeicesFromSave(){
        long[] whitePeicesOld;
        long[] blackPeicesOld;
        if(moveIndx < 0){
            whitePeicesOld = whitePiecesStart;
            blackPeicesOld = blackPiecesStart;
        }
        else{

            whitePeicesOld = boardSave.get(moveIndx)[0];
            blackPeicesOld = boardSave.get(moveIndx)[1];
        }


        return new long[][] {whitePeicesOld,blackPeicesOld};


    }



    public void ChangeBoard(ImageView[][] pieceLocations, Boolean isWhiteTurn, long[] whitePieces, long[] blackPieces){
        whiteCastleRight = moveIndx <= whiteCastleIndx;
        blackCastleRight = moveIndx <= blackCastleIndx;
        whiteLongRookMove = moveIndx <= whiteLongRookIndx;
        whiteShortRookMove = moveIndx <= whiteShortRookIndx;
        blackLongRookMove = moveIndx <= blackLongRookIndx;
        blackShortRookMove = moveIndx <= blackShortRookIndx;


        List<String>[] changes = getChangesNeeded(whitePieces,blackPieces,false);
        List<String> thingsToAdd = changes[0];
        List<String> thingsToRemove = changes[1];
        MatrixToString(pieceLocations);

//        System.out.println("Things to add size : " + thingsToAdd.size());
//        System.out.println("Things to remove size : " + thingsToRemove.size());
//        for(int i = 0; i<thingsToAdd.size();i++){
//            System.out.println(i + " Add: " + thingsToAdd.get(i));
//        }for(int i = 0; i<thingsToRemove.size();i++){
//            System.out.println(i + " Rem: " + thingsToRemove.get(i));
//        }

        int i = 0;
        int z = 0;

        while(z < thingsToRemove.size()){
            // edge case where you need to remove more to the board
            String[] Delinfo = thingsToRemove.get(z).split(",");
            int OldX = Integer.parseInt(Delinfo[0]);
            int OldY = Integer.parseInt(Delinfo[1]);
            boolean isWhite = Delinfo[2].equals("w");
            int brdRmvIndex = Integer.parseInt(Delinfo[3]);
            chessBoardGUIHandler.removeFromGridPane(OldX,OldY,chessPeiceBoard);
            pieceLocations[OldX][OldY] = null;
            removePeice(brdRmvIndex,positionToBitIndex(OldX,OldY),isWhite,whitePieces,blackPieces);
            ImageView smallPeice = chessBoardGUIHandler.createNewPeice(brdRmvIndex,isWhite,chessPeiceBoard,true);
            smallPeice.setUserData(Integer.toString(brdRmvIndex));
            if(isWhite){
                eatenWhites.getChildren().add(smallPeice);
            }
            else{
                eatenBlacks.getChildren().add(smallPeice);
            }
            z++;

        }
        while(i < thingsToAdd.size()){
            // edge case where you need to add more to the board
            String[] Moveinfo = thingsToAdd.get(i).split(",");
            int NewX = Integer.parseInt(Moveinfo[0]);
            int NewY = Integer.parseInt(Moveinfo[1]);
            int brdAddIndex = Integer.parseInt(Moveinfo[3]);
            boolean isWhite = Moveinfo[2].equals("w");
            ImageView peice = chessBoardGUIHandler.createNewPeice(brdAddIndex,isWhite,chessPeiceBoard,false);
            chessPeiceBoard.add(peice,NewX,NewY);
            pieceLocations[NewX][NewY] = peice;
            addPiece(brdAddIndex,positionToBitIndex(NewX,NewY),isWhite,whitePieces,blackPieces);
            removeFromEatenPeices(Moveinfo[3],isWhite ? eatenWhites : eatenBlacks);


            i++;


        }
        isWhiteTurn = moveIndx % 2 != 0;
        App.controller.isWhiteTurn = isWhiteTurn;

        MatrixToString(pieceLocations);

    }


    public void ChangeBoard(long[] whitePieces, long[] blackPieces){
        List<String>[] changes = getChangesNeeded(whitePieces,blackPieces,true);
        List<String> thingsToAdd = changes[0];
        List<String> thingsToRemove = changes[1];


        int i = 0;
        int z = 0;

        while(z < thingsToRemove.size()){
            // edge case where you need to remove more to the board
            String[] Delinfo = thingsToRemove.get(z).split(",");
            int OldX = Integer.parseInt(Delinfo[0]);
            int OldY = Integer.parseInt(Delinfo[1]);
            boolean isWhite = Delinfo[2].equals("w");
            int brdRmvIndex = Integer.parseInt(Delinfo[3]);
            chessBoardGUIHandler.removeFromGridPane(OldX,OldY,chessPeiceBoard);

            z++;

        }
        while(i < thingsToAdd.size()){
            // edge case where you need to add more to the board
            String[] Moveinfo = thingsToAdd.get(i).split(",");
            int NewX = Integer.parseInt(Moveinfo[0]);
            int NewY = Integer.parseInt(Moveinfo[1]);
            int brdAddIndex = Integer.parseInt(Moveinfo[3]);
            boolean isWhite = Moveinfo[2].equals("w");
            ImageView peice = chessBoardGUIHandler.createNewPeice(brdAddIndex,isWhite,chessPeiceBoard,false);
            chessPeiceBoard.add(peice,NewX,NewY);
            i++;


        }

    }


    private List<String>[] getChangesNeeded(long[] whitePieces, long[] blackPieces, boolean isTest){
        long[] whitePeicesOld = whitePieces;
        long[] blackPeicesOld = blackPieces;
        if(!isTest){
            long[][] save = getPeicesFromSave();
            whitePeicesOld = save[0];
            blackPeicesOld = save[1];
        }
        List<String> changesAdd = new ArrayList<>();
        List<String> changesRemove = new ArrayList<>();
        for(int i = 0; i<whitePieces.length;i++){
            long old = whitePeicesOld[i];
            long cur = whitePieces[i];
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
        for(int i = 0; i<blackPieces.length;i++){
            long old = blackPeicesOld[i];
            long cur = blackPieces[i];

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



    public void updateMoveIndex(int amnt){
        moveIndx += amnt;
    }



    private int[] bitindexToXY(int bitIndex){
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
    private List<XYcoord> getPieceCoords(long board) {
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







    private void removeFromEatenPeices(String BoardId, HBox eatenPeices){
        Iterator<Node> it = eatenPeices.getChildren().iterator();
        while(it.hasNext()){
            ImageView v = (ImageView) it.next();
            if(v.getUserData().equals(BoardId)){
                it.remove();
                break;
            }
        }
    }











}
