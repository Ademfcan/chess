package chessserver.Functions;

import chessserver.Misc.ChessConstants;
import chessserver.ChessRepresentations.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class AdvancedChessFunctions {

    private static final int[] pieceValues = new int[]{1, 3, 3, 5, 9};
    private static final Logger logger = LogManager.getLogger("Advanced_Chess_Functions");

    public static List<XYcoord> getPossibleMoves(int x, int y, boolean isWhite, ChessPosition pos, ChessGameState gameState) {
        int indx = GeneralChessFunctions.getBoardWithPiece(x, y, isWhite, pos.board);
        //System.out.println("Getting moves for peice at " + x + " " + y);
        return getPossibleMoves(x, y, isWhite, pos, gameState, indx, false);

    }

    public static List<XYcoord> getPossibleMoves(int x, int y, boolean isWhite, ChessPosition pos, ChessGameState gameState, int boardIndex, boolean capturesOnly) {
        //System.out.println("Getting moves for peice at " + x + " " + y);
        long possibleMoves = getPossibleMoveBoard(x, y, isWhite, boardIndex, pos, gameState);
        if (boardIndex != ChessConstants.KINGINDEX && isChecked(isWhite, pos.board)) {
//            System.out.println("Checked");
            possibleMoves &= getCheckedFileMask(isWhite, pos.board);
        }
        if(capturesOnly){
            long enemyPieceMask = isWhite ? pos.board.getBlackPieceMask() : pos.board.getWhitePieceMask();
            possibleMoves &= enemyPieceMask;
        }
        return GeneralChessFunctions.getPieceCoords(possibleMoves);

    }

    private static long getPossibleMoveBoard(int x, int y, boolean isWhite, int indx, ChessPosition pos, ChessGameState gameState) {
        if (!GeneralChessFunctions.isValidIndex(indx)) {
            logger.error("Invalid index passed into get move of type");
        }
        return switch (indx) {
            case 0 -> calculatePawnMoves(x, y, isWhite, pos, false, false);
            case 1 -> calculateKnightMoves(x, y, isWhite, pos.board, false, false);
            case 2 -> calculateBishopMoves(x, y, isWhite, pos.board);
            case 3 -> calculateRookMoves(x, y, isWhite, pos.board);
            case 4 -> calculateQueenMoves(x, y, isWhite, pos.board);
            case 5 -> calculateKingMoves(x, y, isWhite, pos.board, gameState);
            default -> (0);
        };
    }

    private static List<XYcoord> getMovesWithoutLogic(int x, int y, boolean isWhite, int indx, ChessPosition pos) {
        if (!GeneralChessFunctions.isValidIndex(indx)) {
            logger.error("Invalid index passed into get move of type");
        }
        int bitIndex = GeneralChessFunctions.positionToBitIndex(x,y);
        return switch (indx) {
            case ChessConstants.PAWNINDEX -> GeneralChessFunctions.getPieceCoords(BitFunctions.calculatePawnAttackMask(bitIndex,isWhite,pos.board));
            case ChessConstants.KNIGHTINDEX -> GeneralChessFunctions.getPieceCoords(BitFunctions.calculateKnightAttackBitBoard(bitIndex,isWhite,pos.board));
            case ChessConstants.BISHOPINDEX -> GeneralChessFunctions.getPieceCoords(BitFunctions.calculateBishopAttackBitBoard(bitIndex,isWhite,false,pos.board));
            case ChessConstants.ROOKINDEX -> GeneralChessFunctions.getPieceCoords(BitFunctions.calculateRookAttackBitBoard(bitIndex,isWhite,false,pos.board));
            case ChessConstants.QUEENINDEX -> GeneralChessFunctions.getPieceCoords(BitFunctions.calculateQueenAtackBitboard(bitIndex,isWhite,false,pos.board));
            case ChessConstants.KINGINDEX -> basicKingMoveCalc(x, y, isWhite, pos.board,false);
            default -> (null);
        };
    }

    public static String getPieceType(int x, int y, Boolean isWhite, BitBoardWrapper board) {
        int indx = GeneralChessFunctions.getBoardWithPiece(x, y, isWhite, board);
        return switch (indx) {
            case 0 -> "Pawn";
            case 1 -> "Knight";
            case 2 -> "Bishop";
            case 3 -> "Rook";
            case 4 -> "Queen";
            case 5 -> "King";
            default -> "Empty";
        };
    }

    /**
     * Method used to check wether moving a piece to a certain new coordinate will result in another coordinate being "checked".
     * The importance of this is that it is to not allow moves where the king is shadowed
     **/
    public static long createPinMask(int oldX, int oldY, boolean isWhite, BitBoardWrapper board) {
        long pinMask = 0xFFFFFFFFFFFFFFFFL;
        long enemyAttack = isWhite ? board.getBlackSlidingAttackers() : board.getWhiteSlidingAttackers();
        if(!GeneralChessFunctions.checkIfContains(oldX,oldY,enemyAttack)){
            return pinMask;
        }
        int centerIndex = GeneralChessFunctions.positionToBitIndex(oldX,oldY);
        long whitePieceMask = board.getWhitePieceMask();
        long blackPieceMask = board.getBlackPieceMask();
        long allPieceMask = whitePieceMask | blackPieceMask;
        allPieceMask = GeneralChessFunctions.RemovePeice(oldX,oldY,allPieceMask);

        long rookPossibleMoves = BitFunctions.calculateRookAttackBitBoard(centerIndex,isWhite,true,allPieceMask,board);
        long bishopPossibleMoves = BitFunctions.calculateBishopAttackBitBoard(centerIndex,isWhite,true,allPieceMask,board);
        long rookVerticalFile = rookPossibleMoves & ~BitFunctions.generateHorizontalRookMask(centerIndex);
        int rookVerticalHighBit = 63-Long.numberOfLeadingZeros(rookVerticalFile);
        int rookVerticalLowBit = Long.numberOfTrailingZeros(rookVerticalFile);
        long rookHorizontalFile = rookPossibleMoves & ~BitFunctions.generateVerticalRookMask(centerIndex);
        int rookHorizontalHighBit = 63-Long.numberOfLeadingZeros(rookHorizontalFile);
        int rookHorizontalLowBit = Long.numberOfTrailingZeros(rookHorizontalFile);
        long bishopRtLFile = bishopPossibleMoves & ~BitFunctions.generateLtRBishopMask(centerIndex);
        int bishopRtLHighBit = 63-Long.numberOfLeadingZeros(bishopRtLFile);
        int bishopRtLLowBit = Long.numberOfTrailingZeros(bishopRtLFile);
        long bishopLtRFile = bishopPossibleMoves & ~BitFunctions.generateRtLBishopMask(centerIndex);
        int bishopLtRHighBit = 63-Long.numberOfLeadingZeros(bishopLtRFile);
        int bishopLtRLowBit = Long.numberOfTrailingZeros(bishopLtRFile);
        // rook files
        // vertical
        long friendlyKingBoard = isWhite ? board.getWhitePiecesBB()[ChessConstants.KINGINDEX] : board.getBlackPiecesBB()[ChessConstants.KINGINDEX];
        long enemyRookAndQueenBoard = isWhite ? (board.getBlackPiecesBB()[ChessConstants.ROOKINDEX] | board.getBlackPiecesBB()[ChessConstants.QUEENINDEX]) : (board.getWhitePiecesBB()[ChessConstants.ROOKINDEX] | board.getWhitePiecesBB()[ChessConstants.QUEENINDEX]);
        if(rookVerticalLowBit < 64 && rookVerticalHighBit > -1){
            boolean enemyLow = GeneralChessFunctions.checkIfContains(rookVerticalLowBit,enemyRookAndQueenBoard);
            boolean kingHigh = GeneralChessFunctions.checkIfContains(rookVerticalHighBit,friendlyKingBoard);

            boolean enemyHigh = GeneralChessFunctions.checkIfContains(rookVerticalHighBit,enemyRookAndQueenBoard);
            boolean kingLow = GeneralChessFunctions.checkIfContains(rookVerticalLowBit,friendlyKingBoard);

            if((enemyLow && kingHigh) || (enemyHigh && kingLow)){
                pinMask &= rookVerticalFile;
            }
        }

        // horizontal
        if(rookHorizontalLowBit < 64 && rookHorizontalHighBit > -1){
            boolean enemyLow = GeneralChessFunctions.checkIfContains(rookHorizontalLowBit,enemyRookAndQueenBoard);
            boolean kingHigh = GeneralChessFunctions.checkIfContains(rookHorizontalHighBit,friendlyKingBoard);

            boolean enemyHigh = GeneralChessFunctions.checkIfContains(rookHorizontalHighBit,enemyRookAndQueenBoard);
            boolean kingLow = GeneralChessFunctions.checkIfContains(rookHorizontalLowBit,friendlyKingBoard);

            if((enemyLow && kingHigh) || (enemyHigh && kingLow)){
                pinMask &= rookHorizontalFile;
            }
        }
        // bishop files
        // left to right
        long enemyBishopAndQueenBoard = isWhite ? (board.getBlackPiecesBB()[ChessConstants.BISHOPINDEX] | board.getBlackPiecesBB()[ChessConstants.QUEENINDEX]) : (board.getWhitePiecesBB()[ChessConstants.BISHOPINDEX] | board.getWhitePiecesBB()[ChessConstants.QUEENINDEX]);

        if(bishopLtRLowBit < 64 && bishopLtRHighBit > -1){
            boolean enemyLow = GeneralChessFunctions.checkIfContains(bishopLtRLowBit,enemyBishopAndQueenBoard);
            boolean kingHigh = GeneralChessFunctions.checkIfContains(bishopLtRHighBit,friendlyKingBoard);

            boolean enemyHigh = GeneralChessFunctions.checkIfContains(bishopLtRHighBit,enemyBishopAndQueenBoard);
            boolean kingLow = GeneralChessFunctions.checkIfContains(bishopLtRLowBit,friendlyKingBoard);

            if((enemyLow && kingHigh) || (enemyHigh && kingLow)){
                pinMask &= bishopLtRFile;
            }
        }
        // right to left
        if(bishopRtLLowBit < 64 && bishopRtLHighBit > -1){
            boolean enemyLow =  GeneralChessFunctions.checkIfContains(bishopRtLLowBit,enemyBishopAndQueenBoard);
            boolean kingHigh =  GeneralChessFunctions.checkIfContains(bishopRtLHighBit,friendlyKingBoard);

            boolean enemyHigh =  GeneralChessFunctions.checkIfContains(bishopRtLHighBit,enemyBishopAndQueenBoard);
            boolean kingLow =  GeneralChessFunctions.checkIfContains(bishopRtLLowBit,friendlyKingBoard);

            if(enemyLow && kingHigh || enemyHigh && kingLow) {
                pinMask &= bishopRtLFile;
            }

        }
        return pinMask;// & ~(isWhite ? board.getWhitePieceMask() : board.getBlackPieceMask()); // remove any friendly pieces


//        board.makeTempChange(oldX,oldY,newX,newY,boardIndex,isWhite);
//        boolean isChecked = isChecked(isWhite,board);
//        board.popTempChange();
//        return isChecked;
//        // checking if a move will result in being checked
//        // i 0-1 will be queen or bishop, and i 2-3 will be rook or queen
//        int[] dx = {1, 1, 0, -1};
//        int[] dy = {1, -1, -1, 0};
//        for (int i = 0; i < dx.length; i++) {
//            // find edges on either side
//            boolean hitking = false;
//            boolean hitEnemy = false;
//            // 0 = no king, 1 = king on side 1, -1 = king on side 2
//            int x1 = x + dx[i];
//            int y1 = y + dy[i];
//
//            while (GeneralChessFunctions.isValidCoord(x1, y1)) {
//                if (x1 == newX && y1 == newY) {
//                    break;
//                }
//                int fpeiceType = GeneralChessFunctions.getBoardWithPiece(x1, y1, isWhite, board);
//                if (fpeiceType != ChessConstants.EMPTYINDEX) {
//                    if(fpeiceType == ChessConstants.KINGINDEX){
//                        hitking = true;
//                    }
//                    break;
//                }
//                int epeiceType = GeneralChessFunctions.getBoardWithPiece(x1, y1, !isWhite, board);
//                if (epeiceType != ChessConstants.EMPTYINDEX) {
//                    if (epeiceType == ChessConstants.QUEENINDEX) {
//                        hitEnemy = true;
//                    } else if (i < 2 && epeiceType == ChessConstants.BISHOPINDEX) {
//                        // bishop possibility
//                        hitEnemy = true;
//                    } else if (epeiceType == ChessConstants.ROOKINDEX) {
//                        // rook possibility
//                        hitEnemy = true;
//                    }
//                    break;
//                } else if (GeneralChessFunctions.checkIfContains(x1, y1, isWhite, board)) {
//                    break;
//                }
//                x1 += dx[i];
//                y1 += dy[i];
//            }
//            int x2 = x - dx[i];
//            int y2 = y - dy[i];
//            // todo possible optimization: put both directions in same loop and go for max dir length
//            while (GeneralChessFunctions.isValidCoord(x2, y2)) {
//                if (x2 == newX && y2 == newY) {
//                    break;
//                }
//                int fpeiceType = GeneralChessFunctions.getBoardWithPiece(x2, y2, isWhite, board);
//                if (fpeiceType != ChessConstants.EMPTYINDEX) {
//                    if(fpeiceType == ChessConstants.KINGINDEX){
//                        hitking = true;
//                    }
//                    break;
//                }
//                int epeiceType = GeneralChessFunctions.getBoardWithPiece(x2, y2, !isWhite, board);
//                if (epeiceType != ChessConstants.EMPTYINDEX) {
//                    if (epeiceType == ChessConstants.QUEENINDEX) {
//                        hitEnemy = true;
//                    } else if (i < 2 && epeiceType == ChessConstants.BISHOPINDEX) {
//                        // bishop possibility
//                        hitEnemy = true;
//                    } else if (epeiceType == ChessConstants.ROOKINDEX) {
//                        // rook possibility
//                        hitEnemy = true;
//                    }
//                    break;
//                } else if (GeneralChessFunctions.checkIfContains(x2, y2, isWhite, board)) {
//                    break;
//                }
//                x2 -= dx[i];
//                y2 -= dy[i];
//            }
//            if (hitking && hitEnemy) {
//                return true;
//            }
//
//
//        }
//        return false;
    }

    /**
     * This method takes board and position information and then gives you the possible squares a pawn can move
     * The is for check flag is called when we want to just calculate all squares that a pawn can move regardless of shadowing
     **/
    public static long calculatePawnMoves(int x, int y, boolean isWhite, ChessPosition pos, boolean isforcheck, boolean forAttackMask) {
        BitBoardWrapper board = pos.board;
        long possibleMoveBoard = 0L;
//        ArrayList<XYcoord> moves = new ArrayList<>();
        int pawnHome = isWhite ? 6 : 1;
        int pawnEnd = isWhite ? 0 : 7;
        int move = isWhite ? -1 : 1;
        int eatY = y + move;
        int eatX1 = x + 1;
        int eatX2 = x - 1;

        // en passant
        if (!pos.equals(ChessConstants.startBoardState)) {
            // means we arent at the very beginning as there is not an actual move for the start position
            ChessMove moveThatCreated = pos.getMoveThatCreatedThis();
            if (moveThatCreated.getBoardIndex() == ChessConstants.PAWNINDEX) {
                // pawn move so possibilty of enpassant
                if (Math.abs(moveThatCreated.getOldY() - moveThatCreated.getNewY()) > 1) {
                    // jumped 2 so means that there is a possibilty of en passant
                    if (y == moveThatCreated.getNewY() && (x == moveThatCreated.getNewX() - 1 || x == moveThatCreated.getNewX() + 1)) {
                        int midY = (moveThatCreated.getOldY() + moveThatCreated.getNewY()) / 2;
                        int passantX = moveThatCreated.getNewX();
                        if(!passantMoveWillDie(x,y,passantX,midY,passantX,moveThatCreated.getNewY(),isWhite,board)){
                            // pawn is in the right position so add en passant
                            possibleMoveBoard = GeneralChessFunctions.AddPeice(moveThatCreated.getNewX(),midY,possibleMoveBoard);

                        }
                    }


                }
            }
        }

        if (GeneralChessFunctions.isValidCoord(eatX1, eatY) && ((forAttackMask) || GeneralChessFunctions.checkIfContains(eatX1, eatY, !isWhite, board))) {
            // pawn can capture to the right
            // ignore shadowing move if is for check
            possibleMoveBoard = GeneralChessFunctions.AddPeice(eatX1,eatY,possibleMoveBoard);

//          moves.add(new XYcoord(eatX1, eatY).setPromoHack(eatY == pawnEnd));

        }
        if (GeneralChessFunctions.isValidCoord(eatX2, eatY) && ((forAttackMask) || GeneralChessFunctions.checkIfContains(eatX2, eatY, !isWhite, board))) {
            // pawn can capture to the left
            // ignore shadowing move if is for check
            possibleMoveBoard = GeneralChessFunctions.AddPeice(eatX2,eatY,possibleMoveBoard);
//          moves.add(new XYcoord(eatX2, eatY).setPromoHack(eatY == pawnEnd));

        }
        if(!forAttackMask){
            int depth = y == pawnHome ? 2 : 1;

            for (int i = 1; i < depth + 1; i++) {
                int newY = y + i * move;
                // pawns cannot eat forwards
                if (GeneralChessFunctions.isValidCoord(x, newY) && !GeneralChessFunctions.checkIfContains(x, newY, board, "calcpawn")[0]) {
                    // pawn can capture to the right
                    // ignore shadowing move if is for check
                    possibleMoveBoard = GeneralChessFunctions.AddPeice(x,newY,possibleMoveBoard);
//                  moves.add(new XYcoord(x, newY).setPromoHack(newY == pawnEnd));

                } else {
                    break;
                }
            }
        }
        if(isforcheck){
            return possibleMoveBoard;
        }
//        System.out.println(BitFunctions.getBitStr(createPinMask(x, y, isWhite, board)));
        return possibleMoveBoard & createPinMask(x, y, isWhite, board);

    }

    /**
     * Given a board and square, calculate all possible moves that a knight can make. Has the same isforcheck var as above that ignores shadowing (as for checks not needed
     * The new flag is edges only. This is used when we are trying to find all possible attackers on the edges. In the knights case this is simple as we just check for enemies on all squares
     **/
    public static long calculateKnightMoves(int x, int y, boolean isWhite, BitBoardWrapper board, boolean isforcheck, boolean includeFriendlys) {
        long possibleMoves = 0L;
        ArrayList<XYcoord> moves = new ArrayList<>();

        int[] dx = {1, 2, 2, 1, -1, -2, -2, -1};
        int[] dy = {-2, -1, 1, 2, 2, 1, -1, -2};

        for (int i = 0; i < 8; i++) {
            int newX = x + dx[i];
            int newY = y + dy[i];
            if (GeneralChessFunctions.isValidCoord(newX, newY)) {
                boolean[] boardInfo = GeneralChessFunctions.checkIfContains(newX, newY, board, "knight");

                if (!boardInfo[0] || (includeFriendlys || boardInfo[1] != isWhite)) { //must be no hit or not your own color
                    possibleMoves = GeneralChessFunctions.AddPeice(newX,newY,possibleMoves);

                }
            }
        }
        if(isforcheck){
            return possibleMoves;
        }
        return possibleMoves & createPinMask(x, y, isWhite, board);
    }

    public static long calculateBishopMoves(int x, int y, boolean isWhite, BitBoardWrapper board) {
        long possibleMoves = BitFunctions.calculateBishopAttackBitBoard(GeneralChessFunctions.positionToBitIndex(x,y),isWhite,false,board);
        return possibleMoves & createPinMask(x,y, isWhite,board);
//        ArrayList<XYcoord> moves = new ArrayList<>();
//        int i = 0;
//        int max = 4;
//        if (direction != ChessConstants.EMPTYINDEX) {
//            // isolate a single file
//            if (direction < 0 || direction > 3) {
//                // check if direction is in range 0-3 else index out of bounds
//                logger.error("Invalid direction provided to bishop moves! Dir: " + direction);
//                return null;
//            }
//            i = direction;
//            max = direction + 1;
//        }
//        int[] dx = {1, 1, -1, -1};
//        int[] dy = {1, -1, 1, -1};
//
//        while (i < max) {
//            int newX = x + dx[i];
//            int newY = y + dy[i];
//            while (GeneralChessFunctions.isValidCoord(newX, newY)) {
//
//                boolean willDie = false;
//                if (!isForCheck) {
//                    willDie = willResultInDead(x, y, newX, newY,ChessConstants.BISHOPINDEX ,isWhite, board);
//                }
//                if (!willDie) {
//                    boolean containsFriend = GeneralChessFunctions.checkIfContains(newX, newY, isWhite, board);
//                    boolean containsEnemy = GeneralChessFunctions.checkIfContains(newX, newY, !isWhite, board);
//
//                    XYcoord response = new XYcoord(newX, newY, i);
//                    if (containsEnemy) {
//                        moves.add(response);
//                        break;
//                    }
//                    if (!containsFriend) {
//                        if (!edgesOnly) {
//                            moves.add(response);
//                        }
//                    } else {
//                        break;
//                    }
//
//                } else {
//
//                    break;
//                }
//                newX += dx[i];
//                newY += dy[i];
//            }
//            i++;
//        }
//
//        return moves;
    }

    public static long calculateRookMoves(int x, int y, boolean isWhite, BitBoardWrapper board) {
        long possibleMoves = BitFunctions.calculateRookAttackBitBoard(GeneralChessFunctions.positionToBitIndex(x,y),isWhite,false,board);
        return possibleMoves & createPinMask(x,y, isWhite,board);
//        ArrayList<XYcoord> moves = new ArrayList<>();
//        int i = 0;
//        int max = 4;
//        if (direction != ChessConstants.EMPTYINDEX) {
//            if (direction < 0 || direction > 3) {
//                // check if direction is in range 0-3 else index out of bounds
//                logger.error("Invalid direction provided to bishop moves! Dir: " + direction);
//                return null;
//            }
//            i = direction;
//            max = direction + 1;
//        }
//
//        int[] dx = {1, -1, 0, 0};
//        int[] dy = {0, 0, 1, -1};
//        while (i < max) {
//            int newX = x + dx[i];
//            int newY = y + dy[i];
//            while (GeneralChessFunctions.isValidCoord(newX, newY)) {
//
//                boolean willDie = false;
//
//                if (!isForCheck) {
//                    willDie = willResultInDead(x, y, newX, newY,ChessConstants.ROOKINDEX ,isWhite, board);
//                }
//
//                if (!willDie) {
//                    boolean containsFriend = GeneralChessFunctions.checkIfContains(newX, newY, isWhite, board);
//                    boolean containsEnemy = GeneralChessFunctions.checkIfContains(newX, newY, !isWhite, board);
//
//                    XYcoord response = new XYcoord(newX, newY, i);
//                    if (containsEnemy) {
//                        moves.add(response);
//                        break;
//                    }
//                    if (!containsFriend) {
//                        if (!edgesOnly) {
//                            moves.add(response);
//                        }
//                    } else {
//                        break;
//                    }
//
//
//                } else {
//                    break;
//                }
//
//                newX += dx[i];
//                newY += dy[i];
//            }
//            i++;
//        }
//
//        return moves;
    }

    public static List<XYcoord> calculateRookMovesMagicBitboard(int x, int y, boolean isWhite, BitBoardWrapper board){
        int bitIndex = GeneralChessFunctions.positionToBitIndex(x,y);
        long possibleMoves = BitFunctions.calculateRookAttackBitBoard(bitIndex,isWhite,false,board);
        return GeneralChessFunctions.getPieceCoords(possibleMoves);

    }



    public static List<XYcoord> calculateBishopMovesMagicBitboard(int x, int y, boolean isWhite, BitBoardWrapper board){
        int bitIndex = GeneralChessFunctions.positionToBitIndex(x,y);
        long possibleMoves = BitFunctions.calculateBishopAttackBitBoard(bitIndex,isWhite,false,board);
        return GeneralChessFunctions.getPieceCoords(possibleMoves);

    }

    private static long calculateQueenMoves(int x, int y, boolean isWhite, BitBoardWrapper board) {
        long rookMoves = calculateRookMoves(x, y, isWhite, board);
        long bishopMoves = calculateBishopMoves(x, y, isWhite, board);


        return rookMoves | bishopMoves;
    }

    public static List<XYcoord> basicKingMoveCalc(int x, int y, boolean isWhite, BitBoardWrapper board, boolean includeFriendlys) {
        ArrayList<XYcoord> moves = new ArrayList<>();
        int[] dx = {1, -1, 0, 0, 1, -1, 1, -1};
        int[] dy = {0, 0, 1, -1, 1, -1, -1, 1};

        for (int i = 0; i < 8; i++) {
            int newX = x + dx[i];
            int newY = y + dy[i];
            if (GeneralChessFunctions.isValidCoord(newX, newY) && (includeFriendlys || !GeneralChessFunctions.checkIfContains(newX, newY, isWhite, board))) {
                moves.add(new XYcoord(newX, newY));
            }
        }
        return moves;
    }

    private static long calculateKingMoves(int x, int y, boolean isWhite, BitBoardWrapper board, ChessGameState gameState) {
        long possibleMoves = 0L;
//        ArrayList<XYcoord> moves = new ArrayList<>();
        boolean canCastle = isWhite ? gameState.isWhiteCastleRight() : gameState.isBlackCastleRight();
        boolean shortRook = isWhite ? gameState.isWhiteKingSideRight() : gameState.isBlackKingSideRight();
        boolean longRook = isWhite ? gameState.isWhiteQueenSideRight() : gameState.isBlackQueenSideRight();

        if (canCastle && !isChecked(x, y, isWhite, board)) {
            // short castle // todo gamestates giving castle right even though xy is not at home location!!!
            if (shortRook && !GeneralChessFunctions.checkIfContains(x + 1, y, board, "kingCaslte")[0] && !GeneralChessFunctions.checkIfContains(x + 2, y, board, "kingCaslte")[0] && !isChecked(x + 1, y, isWhite, board) && !isChecked(x + 2, y, isWhite, board)) {
                possibleMoves = GeneralChessFunctions.AddPeice(x+2,y,possibleMoves);
//                moves.add(new XYcoord(x + 2, y, true));
            }
            // long castle
            if (longRook && !GeneralChessFunctions.checkIfContains(x - 1, y, board, "kingCaslte")[0] && !GeneralChessFunctions.checkIfContains(x - 2, y, board, "kingCaslte")[0] && !GeneralChessFunctions.checkIfContains(x - 3, y, board, "kingCaslte")[0] && !isChecked(x - 1, y, isWhite, board) && !isChecked(x - 2, y, isWhite, board) && !isChecked(x - 3, y, isWhite, board)) {
                possibleMoves = GeneralChessFunctions.AddPeice(x-2,y,possibleMoves);
//                moves.add(new XYcoord(x - 2, y, true));

            }
        }
        // todo fix this castling breaking computer

        int[] dx = {1, -1, 0, 0, 1, -1, 1, -1};
        int[] dy = {0, 0, 1, -1, 1, -1, -1, 1};

        for (int i = 0; i < 8; i++) {
            int newX = x + dx[i];
            int newY = y + dy[i];
            boolean isValid = GeneralChessFunctions.isValidCoord(newX, newY);
            if (isValid) {
                if (!GeneralChessFunctions.checkIfContains(newX, newY, isWhite, board) && !isChecked(newX,newY,isWhite,board) && !kingMoveWillDie(x,y,newX,newY,isWhite,board)) {
                    possibleMoves = GeneralChessFunctions.AddPeice(newX,newY,possibleMoves);
//                    moves.add(new XYcoord(newX, newY));
//                    moves.add(new XYcoord(newX, newY));
                }

            }


        }
        return possibleMoves;
    }

    private static boolean kingMoveWillDie(int oldX,int oldY,int newX,int newY,boolean isWhite,BitBoardWrapper board){
//        return false;
        int testBitIndex = GeneralChessFunctions.positionToBitIndex(newX,newY);
        long whitePieceMask = board.getWhitePieceMask();
        long blackPieceMask = board.getBlackPieceMask();
        long allPieceMask = whitePieceMask | blackPieceMask;
        allPieceMask = GeneralChessFunctions.RemovePeice(oldX,oldY,allPieceMask);
        long enemyRookAndQueenBoard = isWhite ? (board.getBlackPiecesBB()[ChessConstants.ROOKINDEX] | board.getBlackPiecesBB()[ChessConstants.QUEENINDEX]) : (board.getWhitePiecesBB()[ChessConstants.ROOKINDEX] | board.getWhitePiecesBB()[ChessConstants.QUEENINDEX]);
        long enemyBishopAndQueenBoard = isWhite ? (board.getBlackPiecesBB()[ChessConstants.BISHOPINDEX] | board.getBlackPiecesBB()[ChessConstants.QUEENINDEX]) : (board.getWhitePiecesBB()[ChessConstants.BISHOPINDEX] | board.getWhitePiecesBB()[ChessConstants.QUEENINDEX]);
        long bishopAttacks = BitFunctions.calculateBishopAttackBitBoard(testBitIndex,isWhite,false,allPieceMask,board);
        long rookAttacks = BitFunctions.calculateRookAttackBitBoard(testBitIndex,isWhite,false,allPieceMask,board);
        return ((enemyBishopAndQueenBoard & bishopAttacks) != 0) || ((enemyRookAndQueenBoard & rookAttacks) != 0);
    }
    private static boolean passantMoveWillDie(int oldX,int oldY,int newX,int newY,int passantX,int passantY,boolean isWhite,BitBoardWrapper board){
        XYcoord kingLocation = isWhite ? board.getWhiteKingLocation() : board.getBlackKingLocation();
        int kingIndex = GeneralChessFunctions.positionToBitIndex(kingLocation.x,kingLocation.y);
//        return false;
        long whitePieceMask = board.getWhitePieceMask();
        long blackPieceMask = board.getBlackPieceMask();
        long allPieceMask = whitePieceMask | blackPieceMask;
        allPieceMask = GeneralChessFunctions.RemovePeice(oldX,oldY,allPieceMask);
        allPieceMask = GeneralChessFunctions.AddPeice(newX,newY,allPieceMask);
        allPieceMask = GeneralChessFunctions.RemovePeice(passantX,passantY,allPieceMask);
        long enemyRookAndQueenBoard = isWhite ? (board.getBlackPiecesBB()[ChessConstants.ROOKINDEX] | board.getBlackPiecesBB()[ChessConstants.QUEENINDEX]) : (board.getWhitePiecesBB()[ChessConstants.ROOKINDEX] | board.getWhitePiecesBB()[ChessConstants.QUEENINDEX]);
        long enemyBishopAndQueenBoard = isWhite ? (board.getBlackPiecesBB()[ChessConstants.BISHOPINDEX] | board.getBlackPiecesBB()[ChessConstants.QUEENINDEX]) : (board.getWhitePiecesBB()[ChessConstants.BISHOPINDEX] | board.getWhitePiecesBB()[ChessConstants.QUEENINDEX]);
        long bishopAttacks = BitFunctions.calculateBishopAttackBitBoard(kingIndex,isWhite,false,allPieceMask,board);
        long rookAttacks = BitFunctions.calculateRookAttackBitBoard(kingIndex,isWhite,false,allPieceMask,board);
        return ((enemyBishopAndQueenBoard & bishopAttacks) != 0) || ((enemyRookAndQueenBoard & rookAttacks) != 0);
    }


    public static boolean isPromoPossible(int x, int y, boolean isWhite, BitBoardWrapper board){
        int forwardDir = isWhite ? -1 : 1;
        int newY = y+forwardDir;
        while (GeneralChessFunctions.isValidCoord(x,newY)){
            if(GeneralChessFunctions.checkIfContains(x,newY,board,"promo possible")[0]){
                return false;
            }
            newY+=forwardDir;
        }
        return true;

    }

    public static double getMinAttacker(int x, int y, boolean isWhite, BitBoardWrapper board) {
        long[] enemyAttackMasks = isWhite ? board.getBlackAttackTables() : board.getWhiteAttackTables();
        double minAttacker = ChessConstants.EMPTYINDEX;
        for(int i = ChessConstants.KINGINDEX;i>=0;i--){
            if(GeneralChessFunctions.checkIfContains(x,y,enemyAttackMasks[i])){
                minAttacker = ChessConstants.valueMap[i];
            }
        }
        return minAttacker;
    }


    public static double getMaxAttacker(int x, int y, boolean isWhite, BitBoardWrapper board) {
        long[] enemyAttackMasks = isWhite ? board.getBlackAttackTables() : board.getWhiteAttackTables();
        double maxAttacker = ChessConstants.EMPTYINDEX;
        for(int i = 0;i<=ChessConstants.KINGINDEX;i++){
            if(GeneralChessFunctions.checkIfContains(x,y,enemyAttackMasks[i])){
                maxAttacker = ChessConstants.valueMap[i];
            }
        }
        return maxAttacker;
    }



    public static boolean isAnyNotMovePossible(boolean isWhite, ChessPosition pos, ChessGameState gameState) {
        List<XYcoord> peices = GeneralChessFunctions.getPieceCoordsForComputer(isWhite ? pos.board.getWhitePiecesBB() : pos.board.getBlackPiecesBB());
        for (XYcoord pcoord : peices) {
            List<XYcoord> piecePossibleMoves = getPossibleMoves(pcoord.x, pcoord.y, isWhite, pos, gameState, pcoord.peiceType, false);
            if (!piecePossibleMoves.isEmpty()) {
                return false;
            }
        }
//        logger.debug("No moves possible for :" + isWhite);
        return true;
    }

    public static Boolean isAnyChecked(BitBoardWrapper board) {
        // king coordinates
        XYcoord wkingLocation = board.getWhiteKingLocation();
        XYcoord bkingLocation = board.getBlackKingLocation();
        return isChecked(wkingLocation.x, wkingLocation.y, true, board) || isChecked(bkingLocation.x, bkingLocation.y, false, board);


    }

    public static Boolean isChecked(boolean isWhite, BitBoardWrapper board) {
        XYcoord kingLocation = isWhite ? board.getWhiteKingLocation() : board.getBlackKingLocation();
        return isChecked(kingLocation.x, kingLocation.y, isWhite, board);


    }

    private static boolean isChecked(int x, int y, boolean isWhite, BitBoardWrapper board) {
        long attackMap = isWhite ? board.getBlackAttackTableCombined() : board.getWhiteAttackTableCombined();
//        System.out.println("X: " + x + " Y: " + y);
//        System.out.println(BitFunctions.getBitStr(attackMap,GeneralChessFunctions.positionToBitIndex(x,y)));
        return GeneralChessFunctions.checkIfContains(x,y,attackMap);

//        // general checking if a square is checked
//        List<XYcoord> possibleRookFiles = calculateRookMoves(x, y, isWhite, true, ChessConstants.EMPTYINDEX, board, true);
//        List<XYcoord> possibleBishopFiles = calculateBishopMoves(x, y, isWhite, true, ChessConstants.EMPTYINDEX, board, true);
//        List<XYcoord> possibleHorseJumps = calculateKnightMoves(x, y, isWhite, true, board, true);
//        List<XYcoord> possibleKingMoves = basicKingMoveCalc(x, y, isWhite, board);
//        // check pawns
//        int jump = isWhite ? 1 : -1;
//        if (GeneralChessFunctions.isValidCoord(x - jump, y - jump) && GeneralChessFunctions.getBoardWithPiece(x - jump, y - jump, !isWhite, board) == ChessConstants.PAWNINDEX) {
//            return true;
//        }
//        if (GeneralChessFunctions.isValidCoord(x + jump, y - jump) && GeneralChessFunctions.getBoardWithPiece(x + jump, y - jump, !isWhite, board) == ChessConstants.PAWNINDEX) {
//            return true;
//        }
//        for (XYcoord s : possibleKingMoves) {
//            int peiceType = GeneralChessFunctions.getBoardWithPiece(s.x, s.y, !isWhite, board);
//            if (peiceType == ChessConstants.KINGINDEX) {
//                return true;
//            }
//        }
//        for (XYcoord s : possibleRookFiles) {
//            int peiceType = GeneralChessFunctions.getBoardWithPiece(s.x, s.y, !isWhite, board);
//            if (peiceType == ChessConstants.ROOKINDEX || peiceType == ChessConstants.QUEENINDEX) {
//                return true;
//            }
//        }
//        for (XYcoord s : possibleHorseJumps) {
//            int peiceType = GeneralChessFunctions.getBoardWithPiece(s.x, s.y, !isWhite, board);
//            if (peiceType == ChessConstants.KNIGHTINDEX) {
//                return true;
//            }
//        }
//        for (XYcoord s : possibleBishopFiles) {
//            int peiceType = GeneralChessFunctions.getBoardWithPiece(s.x, s.y, !isWhite, board);
//            if (peiceType == ChessConstants.BISHOPINDEX || peiceType == ChessConstants.QUEENINDEX) {
//                return true;
//            }
//        }
//        return false;
    }

    public static long getCheckedFileMask(boolean isWhite, BitBoardWrapper board) {
        // general checking if a square is checked
        XYcoord kingLocation = isWhite ? board.getWhiteKingLocation() : board.getBlackKingLocation();
        int kingIndex = GeneralChessFunctions.positionToBitIndex(kingLocation.x,kingLocation.y);
        long rookPossibleMoves = BitFunctions.calculateRookAttackBitBoard(kingIndex,isWhite,false,board);
        long bishopPossibleMoves = BitFunctions.calculateBishopAttackBitBoard(kingIndex,isWhite,false,board);
        long rookVerticalFile = rookPossibleMoves & ~BitFunctions.generateHorizontalRookMask(kingIndex);
        int rookVerticalHighBit = 63-Long.numberOfLeadingZeros(rookVerticalFile);
        int rookVerticalLowBit = Long.numberOfTrailingZeros(rookVerticalFile);
        long rookHorizontalFile = rookPossibleMoves & ~BitFunctions.generateVerticalRookMask(kingIndex);
        int rookHorizontalHighBit = 63-Long.numberOfLeadingZeros(rookHorizontalFile);
        int rookHorizontalLowBit = Long.numberOfTrailingZeros(rookHorizontalFile);
        long bishopRtLFile = bishopPossibleMoves & ~BitFunctions.generateLtRBishopMask(kingIndex);
        int bishopRtLHighBit = 63-Long.numberOfLeadingZeros(bishopRtLFile);
        int bishopRtLLowBit = Long.numberOfTrailingZeros(bishopRtLFile);
        long bishopLtRFile = bishopPossibleMoves & ~BitFunctions.generateRtLBishopMask(kingIndex);
        int bishopLtRHighBit = 63-Long.numberOfLeadingZeros(bishopLtRFile);
        int bishopLtRLowBit = Long.numberOfTrailingZeros(bishopLtRFile);

        boolean attackerFound = false;
        long fileMask = 1L;
        // rook files
        // horizontal
        long enemyRookAndQueenBoard = isWhite ? (board.getBlackPiecesBB()[ChessConstants.ROOKINDEX] | board.getBlackPiecesBB()[ChessConstants.QUEENINDEX]) : (board.getWhitePiecesBB()[ChessConstants.ROOKINDEX] | board.getWhitePiecesBB()[ChessConstants.QUEENINDEX]);
        if(rookVerticalLowBit < 64 && GeneralChessFunctions.checkIfContains(rookVerticalLowBit,enemyRookAndQueenBoard)){
            long personalLocation = GeneralChessFunctions.positionToBitboard(rookVerticalLowBit);
            attackerFound = true;
            fileMask = (rookVerticalFile & BitFunctions.calculateRookAttackBitBoard(rookVerticalLowBit,isWhite,false,board)) | personalLocation;


        }
        if(rookVerticalLowBit != rookVerticalHighBit && rookVerticalHighBit > -1 && GeneralChessFunctions.checkIfContains(rookVerticalHighBit,enemyRookAndQueenBoard)){
            if(attackerFound){
                return 0L; // cannot block double check
            }
            long personalLocation = GeneralChessFunctions.positionToBitboard(rookVerticalHighBit);
            attackerFound = true;
            fileMask = (rookVerticalFile & BitFunctions.calculateRookAttackBitBoard(rookVerticalHighBit,isWhite,false,board)) | personalLocation;


        }
        // vertical
        if(rookHorizontalLowBit < 64 && GeneralChessFunctions.checkIfContains(rookHorizontalLowBit,enemyRookAndQueenBoard)){
            if(attackerFound){
                return 0L; // cannot block double check
            }
            long personalLocation = GeneralChessFunctions.positionToBitboard(rookHorizontalLowBit);
            attackerFound = true;
            fileMask = (rookHorizontalFile & BitFunctions.calculateRookAttackBitBoard(rookHorizontalLowBit,isWhite,false,board)) | personalLocation;

        }
        if(rookHorizontalLowBit != rookHorizontalHighBit && rookHorizontalHighBit > -1 && GeneralChessFunctions.checkIfContains(rookHorizontalHighBit,enemyRookAndQueenBoard)){
            if(attackerFound){
                return 0L; // cannot block double check
            }
            long personalLocation = GeneralChessFunctions.positionToBitboard(rookHorizontalHighBit);
            attackerFound = true;
            fileMask = (rookHorizontalFile & BitFunctions.calculateRookAttackBitBoard(rookHorizontalHighBit,isWhite,false,board)) | personalLocation;


        }
        // bishop files
        // left to right
        long enemyBishopAndQueenBoard = isWhite ? (board.getBlackPiecesBB()[ChessConstants.BISHOPINDEX] | board.getBlackPiecesBB()[ChessConstants.QUEENINDEX]) : (board.getWhitePiecesBB()[ChessConstants.BISHOPINDEX] | board.getWhitePiecesBB()[ChessConstants.QUEENINDEX]);

        if(bishopLtRLowBit < 64 && GeneralChessFunctions.checkIfContains(bishopLtRLowBit,enemyBishopAndQueenBoard)){
            if(attackerFound){
                return 0L; // cannot block double check
            }
            long personalLocation = GeneralChessFunctions.positionToBitboard(bishopLtRLowBit);
            attackerFound = true;
            fileMask = (bishopLtRFile & BitFunctions.calculateBishopAttackBitBoard(bishopLtRLowBit,isWhite,false,board)) | personalLocation;


        }
        if(bishopLtRLowBit != bishopLtRHighBit && bishopLtRHighBit > -1 && GeneralChessFunctions.checkIfContains(bishopLtRHighBit,enemyBishopAndQueenBoard)){
            if(attackerFound){
                return 0L; // cannot block double check
            }
            long personalLocation = GeneralChessFunctions.positionToBitboard(bishopLtRHighBit);
            attackerFound = true;
            fileMask = (bishopLtRFile & BitFunctions.calculateBishopAttackBitBoard(bishopLtRHighBit,isWhite,false,board)) | personalLocation;


        }
        // right to left
        if(bishopRtLLowBit < 64 && GeneralChessFunctions.checkIfContains(bishopRtLLowBit,enemyBishopAndQueenBoard)){
            if(attackerFound){
                return 0L; // cannot block double check
            }

            long personalLocation = GeneralChessFunctions.positionToBitboard(bishopRtLLowBit);
            attackerFound = true;
            fileMask = (bishopRtLFile & BitFunctions.calculateBishopAttackBitBoard(bishopRtLLowBit,isWhite,false,board)) | personalLocation;

        }
        if(bishopRtLLowBit != bishopRtLHighBit && bishopRtLHighBit > -1 && GeneralChessFunctions.checkIfContains(bishopRtLHighBit,enemyBishopAndQueenBoard)){
            if(attackerFound){
                return 0L; // cannot block double check
            }
            long personalLocation = GeneralChessFunctions.positionToBitboard(bishopRtLHighBit);
            attackerFound = true;
            fileMask = (bishopRtLFile & BitFunctions.calculateBishopAttackBitBoard(bishopRtLHighBit,isWhite,false,board)) | personalLocation;


        }
        long enemyKnightLocations = isWhite ? board.getBlackPiecesBB()[ChessConstants.KNIGHTINDEX] : board.getWhitePiecesBB()[ChessConstants.KNIGHTINDEX];
        // knights and pawns
        long knightMask = BitFunctions.calculateKnightAttackBitBoard(kingIndex,isWhite,board);
        long combinedKnightMask = knightMask & enemyKnightLocations;
        if(combinedKnightMask != 0L){
            if(attackerFound){
                return 0L; // cannot block double check
            }

            fileMask = combinedKnightMask;
        }

        long enemyPawnLocations = isWhite ? board.getBlackPiecesBB()[ChessConstants.PAWNINDEX] : board.getWhitePiecesBB()[ChessConstants.PAWNINDEX];
        long pawnMask = BitFunctions.calculatePawnAttackMask(kingIndex,isWhite,board);
        long combinedPawnMask = pawnMask & enemyPawnLocations;
        if(combinedPawnMask != 0L){
            if(attackerFound){
                return 0L; // cannot block double check
            }
            fileMask = combinedPawnMask;
        }

        return fileMask;


//
////        long attackMask = isWhite ? board.getBlackAttackTableCombined() : board.getWhiteAttackTableCombined();
////        long checkFile = (BitFunctions.calculateRookAttackBitBoard(kingIndex,isWhite,false,board) | BitFunctions.calculateBishopAttackBitBoard(kingIndex,isWhite,false,board)) & attackMask;
////        return GeneralChessFunctions.getPieceCoords(checkFile);
//
//        ArrayList<XYcoord> files = new ArrayList<>();
////      XYcoord kingLocation = getPieceCoords(isWhite ? board.getWhitePieces()[5] : board.getBlackPieces()[5]).get(0);
//
//        int x = kingLocation.x;
//        int y = kingLocation.y;
//        List<XYcoord> possibleRookFiles = calculateRookMoves(x, y, isWhite, true, ChessConstants.EMPTYINDEX, board, true);
//        List<XYcoord> possibleBishopFiles = calculateBishopMoves(x, y, isWhite, true, ChessConstants.EMPTYINDEX, board, true);
//        List<XYcoord> possibleHorseJumps = calculateKnightMoves(x, y, isWhite, true, board, true, false);
//        // check pawns
//        int jump = isWhite ? 1 : -1;
//
//        if (GeneralChessFunctions.isValidCoord(x - jump, y - jump) && GeneralChessFunctions.getBoardWithPiece(x - jump, y - jump, !isWhite, board) == ChessConstants.PAWNINDEX) {
//            retainIfNotEmpty(files, new XYcoord(x - jump, y - jump));
//        }
//
//        if (GeneralChessFunctions.isValidCoord(x + jump, y - jump) && GeneralChessFunctions.getBoardWithPiece(x + jump, y - jump, !isWhite, board) == ChessConstants.PAWNINDEX) {
//            retainIfNotEmpty(files, new XYcoord(x + jump, y - jump));
//
//        }
//
//        for (XYcoord s : possibleRookFiles) {
//            int peiceType = GeneralChessFunctions.getBoardWithPiece(s.x, s.y, !isWhite, board);
//            if (peiceType == ChessConstants.ROOKINDEX || peiceType == ChessConstants.QUEENINDEX) {
//                List<XYcoord> filtered = calculateRookMoves(x, y, isWhite, false, s.direction, board, true);
//                retainIfNotEmpty(files, filtered);
//
//            }
//        }
//        for (XYcoord s : possibleHorseJumps) {
//            int peiceType = GeneralChessFunctions.getBoardWithPiece(s.x, s.y, !isWhite, board);
//            if (peiceType == ChessConstants.KNIGHTINDEX) {
//                retainIfNotEmpty(files, new XYcoord(s.x, s.y));
//
//
//            }
//        }
//        for (XYcoord s : possibleBishopFiles) {
//            int peiceType = GeneralChessFunctions.getBoardWithPiece(s.x, s.y, !isWhite, board);
//
//            if (peiceType == ChessConstants.BISHOPINDEX || peiceType == ChessConstants.QUEENINDEX) {
//                List<XYcoord> filtered = calculateBishopMoves(x, y, isWhite, false, s.direction, board, true);
//                retainIfNotEmpty(files, filtered);
//
//            }
//        }
//        return files;
    }

    private static void retainIfNotEmpty(ArrayList<XYcoord> files, List<XYcoord> newAdditions) {
        if (files.isEmpty()) {
            files.addAll(newAdditions);
        } else {
            files.retainAll(newAdditions);
        }
    }

    private static void retainIfNotEmpty(ArrayList<XYcoord> files, XYcoord newAddition) {
        if (files.isEmpty()) {
            files.add(newAddition);
        } else {
            files.retainAll(List.of(newAddition));
        }
    }

    public static boolean isCheckmated(boolean isWhite, ChessPosition pos, ChessGameState gameState) {
        return isChecked(isWhite, pos.board) && isAnyNotMovePossible(isWhite, pos, gameState);
    }

    public static boolean isCheckmated(ChessPosition pos, ChessGameState gameState) {
        return (isChecked(false, pos.board) && isAnyNotMovePossible(false, pos, gameState)) || (isChecked(true, pos.board) && isAnyNotMovePossible(true, pos, gameState));
    }

    public static int getNumAttackers(int x, int y, boolean isWhite, BitBoardWrapper board) {
        int numAttackers = 0;
        long[] enemyAttackMasks = isWhite ? board.getBlackAttackTables() : board.getWhiteAttackTables();
        // general checking if a square is checked
        for(long attackMask : enemyAttackMasks){
            if(GeneralChessFunctions.checkIfContains(x,y,attackMask)){
                numAttackers++;
            }
        }
        return numAttackers;

//        int attackerCount = 0;
//        List<XYcoord> possibleRookFiles = calculateRookMoves(x, y, isWhite,  board, true);
//        List<XYcoord> possibleBishopFiles = calculateBishopMoves(x, y, isWhite,  board, true);
//        List<XYcoord> possibleHorseJumps = calculateKnightMoves(x, y, isWhite, true, board, true, false);
//        List<XYcoord> possibleKingMoves = basicKingMoveCalc(x, y, isWhite, board, false);
//        // check pawns
//        int jump = isWhite ? 1 : -1;
//        if (GeneralChessFunctions.isValidCoord(x - jump, y - jump) && GeneralChessFunctions.getBoardWithPiece(x - jump, y - jump, !isWhite, board) == ChessConstants.PAWNINDEX) {
//            attackerCount++;
//        }
//        if (GeneralChessFunctions.isValidCoord(x + jump, y - jump) && GeneralChessFunctions.getBoardWithPiece(x + jump, y - jump, !isWhite, board) == ChessConstants.PAWNINDEX) {
//            attackerCount++;
//        }
//        for (XYcoord s : possibleKingMoves) {
//            int peiceType = GeneralChessFunctions.getBoardWithPiece(s.x, s.y, !isWhite, board);
//            if (peiceType == ChessConstants.KINGINDEX) {
//                attackerCount++;
//            }
//        }
//        for (XYcoord s : possibleRookFiles) {
//            int peiceType = GeneralChessFunctions.getBoardWithPiece(s.x, s.y, !isWhite, board);
//            if (peiceType == ChessConstants.ROOKINDEX || peiceType == ChessConstants.QUEENINDEX) {
//                attackerCount++;
//            }
//        }
//        for (XYcoord s : possibleHorseJumps) {
//            int peiceType = GeneralChessFunctions.getBoardWithPiece(s.x, s.y, !isWhite, board);
//            if (peiceType == ChessConstants.KNIGHTINDEX) {
//                attackerCount++;
//            }
//        }
//        for (XYcoord s : possibleBishopFiles) {
//            int peiceType = GeneralChessFunctions.getBoardWithPiece(s.x, s.y, !isWhite, board);
//            if (peiceType == ChessConstants.BISHOPINDEX || peiceType == ChessConstants.QUEENINDEX) {
//                attackerCount++;
//            }
//        }
//        return attackerCount;
    }

    public static List<String>[] getChangesNeeded(BitBoardWrapper currentBoard, BitBoardWrapper newBoard) {
        // given two boards, find what needs to be added and removed so that you can make minimal graphical changes
        long[] whitePiecesCurrent = currentBoard.getWhitePiecesBB();
        long[] blackPiecesCurrent = currentBoard.getBlackPiecesBB();

        long[] whitePiecesNew = newBoard.getWhitePiecesBB();
        long[] blackPiecesNew = newBoard.getBlackPiecesBB();


        List<String> changesAdd = new ArrayList<>();
        List<String> changesRemove = new ArrayList<>();
        for (int i = 0; i < whitePiecesCurrent.length; i++) {
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
                if ((xorResult & mask) != 0 && (cur & mask) != 0) {
                    int[] coords = bitindexToXY(z);
                    changesRemove.add(coords[0] + "," + coords[1] + ",w," + i);
                }
            }
            // Find missing bit indices to remove


        }
        for (int i = 0; i < blackPiecesCurrent.length; i++) {
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
                if ((xorResult & mask) != 0 && (cur & mask) != 0) {
                    int[] coords = bitindexToXY(z);
                    changesRemove.add(coords[0] + "," + coords[1] + ",b," + i);
                }
            }
            // Find missing bit indices to remove


        }
        return new List[]{changesAdd, changesRemove};
    }

    private static int[] bitindexToXY(int bitIndex) {
        return new int[]{bitIndex % 8, bitIndex / 8};
    }

    public static List<XYcoord> fullPawnMoveCalcPGN(int x, int y, boolean isWhite, boolean isEating, BitBoardWrapper board) {
        ArrayList<XYcoord> moves = new ArrayList<>();
        int pawnEnd = isWhite ? 0 : 7;
        int pawnHome = isWhite ? 6 : 1;

        int move = isWhite ? -1 : 1;
        if (isEating) {
            int eatY = y + move;
            int eatX1 = x + 1;
            int eatX2 = x - 1;
            if(GeneralChessFunctions.isValidCoord(eatX1,eatY)){
                moves.add(new XYcoord(eatX1, eatY));
            }
            if(GeneralChessFunctions.isValidCoord(eatX2,eatY)){
                moves.add(new XYcoord(eatX2, eatY));
            }
        } else if(y != pawnEnd){
            int depth = 1;
            if (y == pawnHome) {
                depth = 2;

            }
            for (int i = 1; i < depth + 1; i++) {
                int newY = y + i * move;
                moves.add(new XYcoord(x, newY));
            }
        }


        moves.forEach(m -> {
            if (m.y == pawnEnd) {
                m.setPawnPromo(true);
            }
        });
        return moves;


    }

    public static XYcoord findOldCoordinates(int newX, int newY, int pieceType, int ambgX, int ambgY, boolean isWhite, boolean isEating, ChessPosition pos, ChessGameState gameState) {
        // either will be no ambiguity, or it will both, or either x/y ambiguity
        boolean noAmbg = ambgX == ChessConstants.EMPTYINDEX && ambgY == ChessConstants.EMPTYINDEX;
        boolean bothAmbg = ambgX != ChessConstants.EMPTYINDEX && ambgY != ChessConstants.EMPTYINDEX;
        boolean xAmbg = ambgX != ChessConstants.EMPTYINDEX;
        List<XYcoord> possibleOrigins;
        if (pieceType == ChessConstants.KINGINDEX) {
            possibleOrigins = AdvancedChessFunctions.basicKingMoveCalc(newX, newY, !isWhite, pos.board, false);

        } else if (pieceType == ChessConstants.PAWNINDEX) {
            possibleOrigins = AdvancedChessFunctions.fullPawnMoveCalcPGN(newX, newY, !isWhite, isEating, pos.board);
        } else {
            possibleOrigins = AdvancedChessFunctions.getMovesWithoutLogic(newX, newY, !isWhite, pieceType, pos);

        }
        for (XYcoord c : possibleOrigins) {
            boolean isPieceThere = GeneralChessFunctions.checkIfContains(c.x, c.y, isWhite ? pos.board.getWhitePiecesBB()[pieceType] : pos.board.getBlackPiecesBB()[pieceType]);
            if (isPieceThere) {
                if (noAmbg) {
                    return c;
                } else if (bothAmbg) {
                    if (c.x == ambgX && c.y == ambgY) {
                        return c;
                    }
                } else if (xAmbg) {
                    if (c.x == ambgX) {
                        return c;
                    }
                } else {
                    if (c.y == ambgY) {
                        return c;
                    }
                }
            }

        }
        logger.error("Failed to find old coordinates");
        return null;
    }

    // returns -1 if not ambigous, 1 for singular ambiguity, 2 for double ambiguity and 3 for triple ambiguity
    public static String getAmbigiousStr(int oldX, int oldY, int newX, int newY, int pieceType, boolean isWhiteMove, boolean isEatingMove, ChessPosition pos) {
        List<XYcoord> possibleOrigins;
        if (pieceType == ChessConstants.KINGINDEX) {
            possibleOrigins = AdvancedChessFunctions.basicKingMoveCalc(newX, newY, !isWhiteMove, pos.board, false);

        } else if (pieceType == ChessConstants.PAWNINDEX) {
            possibleOrigins = AdvancedChessFunctions.fullPawnMoveCalcPGN(newX, newY, !isWhiteMove, isEatingMove, pos.board);
        } else {
            // dont need gamestate so passing in dummy variable
            possibleOrigins = AdvancedChessFunctions.getMovesWithoutLogic(newX, newY, !isWhiteMove, pieceType, pos);
        }
        List<XYcoord> originsWithPiece = possibleOrigins.stream().filter(p -> GeneralChessFunctions.checkIfContains(p.x, p.y, isWhiteMove ? pos.board.getWhitePiecesBB()[pieceType] : pos.board.getBlackPiecesBB()[pieceType])).toList();
        int XMatchCount = 0;
        int YMatchCount = 0;
        for (XYcoord xy : originsWithPiece) {
            if (xy.x == oldX) {
                XMatchCount++;
            }
            if (xy.y == oldY) {
                YMatchCount++;
            }
        }
        String ambgStr = "";
        if (XMatchCount > 0 && YMatchCount > 0) {
            // triple ambiguity, include x and y coords
            ambgStr = Character.toString(PgnFunctions.turnIntToFileStr(oldX)) + (7 - oldY + 1);
        } else if (XMatchCount > 0) {
            // double ambiguity on the x axis so we differentiate by y
            // in pgn y is not zero indexed, thus we add 1
            // also the board is flipped in pgn so we invert the y coordinate
            ambgStr = Integer.toString(7 - oldY + 1);
        } else if (YMatchCount > 0 || !originsWithPiece.isEmpty()) {
            // two options:
            // either double ambiguity on the y axis so we differentiate by x
            // or it is simply a general ambiguity so we specify the x axis by default
            ambgStr = Character.toString(PgnFunctions.turnIntToFileStr(oldX));

        }

        return ambgStr;

    }

    public static int getPawnColumnGivenFile(int file, int rowHint, boolean isWhite, long board) {
        int backwardsDir = isWhite ? 1 : -1;
        int end = rowHint + 3*backwardsDir;
        for (int column = rowHint + backwardsDir; column != end; column += backwardsDir) {
            if (GeneralChessFunctions.checkIfContains(file, column, board)) {
                return column;
            }
        }
        System.out.println(String.format("X %d Y%d end %d",file,rowHint,end));
        System.out.println(BitFunctions.getBitStr(board));
        logger.error("No column found for given file");

        return ChessConstants.EMPTYINDEX;

    }

    public static int getEnPassantOriginX(int newX, int newY, boolean isWhite, long board) {
        int backDir = isWhite ? 1 : -1;
        // look to the left and the right of the new y
        // because its en passant it will have to be one of there

        // left
        if (GeneralChessFunctions.isValidCoord(newX - 1, newY + backDir) && GeneralChessFunctions.checkIfContains(newX - 1, newY + backDir, board)) {
            return newX - 1;
        }
        // right
        if (GeneralChessFunctions.isValidCoord(newX + 1, newY + backDir) && GeneralChessFunctions.checkIfContains(newX + 1, newY + backDir, board)) {
            return newX + 1;
        }

        logger.error("En passant origin not found");

        return ChessConstants.EMPTYINDEX;

    }

    public static int getSimpleAdvantage(BitBoardWrapper board) {
        int totalValue = 0;
        long[] whitePieces = board.getWhitePiecesBB();
        long[] blackPieces = board.getBlackPiecesBB();
        for (int i = 0; i < 5; i++) {
            // white pieces considered positive value, black negative
            totalValue += GeneralChessFunctions.getPieceCoords(whitePieces[i]).size() * pieceValues[i];
            totalValue -= GeneralChessFunctions.getPieceCoords(blackPieces[i]).size() * pieceValues[i];
        }
        return totalValue;
    }

    private List<String> getPieceCoords(boolean isWhite, long[] whitePieces, long[] blackPieces) {
        long[] pieces = isWhite ? whitePieces : blackPieces;
        List<String> summedCoords = new ArrayList<>();
        for (int i = 0; i < pieces.length; i++) {
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
                coord.add(new XYcoord(coords[0], coords[1]));
            }
        }

        return coord;
    }





}
