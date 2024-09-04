package chessengine.Functions;

import chessengine.ChessRepresentations.*;
import chessengine.Misc.ChessConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class AdvancedChessFunctions {


    private static final int[] pieceValues = new int[]{1, 3, 3, 5, 9};
    private static final Logger logger = LogManager.getLogger("Advanced_Chess_Functions");

    public static List<XYcoord> getPossibleMoves(int x, int y, boolean isWhite, ChessPosition pos, ChessStates gameState) {
        int indx = GeneralChessFunctions.getBoardWithPiece(x, y, isWhite, pos.board);
        //System.out.println("Getting moves for peice at " + x + " " + y);
        return getPossibleMoves(x, y, isWhite, pos, gameState, indx);

    }

    public static List<XYcoord> getPossibleMoves(int x, int y, boolean isWhite, ChessPosition pos, ChessStates gameState, int boardIndex) {
        //System.out.println("Getting moves for peice at " + x + " " + y);
        List<XYcoord> baseMoves = getMoveOfType(x, y, isWhite, boardIndex, pos, gameState);
        if (boardIndex != ChessConstants.KINGINDEX && isChecked(isWhite, pos.board)) {
            baseMoves.retainAll(getCheckedFile(isWhite, pos.board));
            return baseMoves;
        } else {

            return baseMoves;
        }

    }

    private static List<XYcoord> getMoveOfType(int x, int y, boolean isWhite, int indx, ChessPosition pos, ChessStates gameState) {
        if (!GeneralChessFunctions.isValidIndex(indx)) {
            logger.error("Invalid index passed into get move of type");
        }
        return switch (indx) {
            case 0 -> calculatePawnMoves(x, y, isWhite, pos, false);
            case 1 -> calculateKnightMoves(x, y, isWhite, false, pos.board, false);
            case 2 -> calculateBishopMoves(x, y, isWhite, false, ChessConstants.EMPTYINDEX, pos.board, false);
            case 3 -> calculateRookMoves(x, y, isWhite, false, ChessConstants.EMPTYINDEX, pos.board, false);
            case 4 -> calculateQueenMoves(x, y, isWhite, false, pos.board, false);
            case 5 -> calculateKingMoves(x, y, isWhite, pos.board, gameState);
            default -> (null);
        };
    }

    private static List<XYcoord> getMoveOfType(int x, int y, boolean isWhite, int indx, ChessPosition pos, ChessStates gameState, boolean isForCheck) {
        if (!GeneralChessFunctions.isValidIndex(indx)) {
            logger.error("Invalid index passed into get move of type");
        }
        return switch (indx) {
            case 0 -> calculatePawnMoves(x, y, isWhite, pos, isForCheck);
            case 1 -> calculateKnightMoves(x, y, isWhite, false, pos.board, isForCheck);
            case 2 -> calculateBishopMoves(x, y, isWhite, false, ChessConstants.EMPTYINDEX, pos.board, isForCheck);
            case 3 -> calculateRookMoves(x, y, isWhite, false, ChessConstants.EMPTYINDEX, pos.board, isForCheck);
            case 4 -> calculateQueenMoves(x, y, isWhite, false, pos.board, isForCheck);
            case 5 -> calculateKingMoves(x, y, isWhite, pos.board, gameState);
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
    private static boolean willResultInDead(int x, int y, int newX, int newY, boolean isWhite, BitBoardWrapper board) {
        // checking if a move will result in being checked
        // i 0-1 will be queen or bishop, and i 2-3 will be rook or queen
        int[] dx = {1, 1, 0, -1};
        int[] dy = {1, -1, -1, 0};
        for (int i = 0; i < dx.length; i++) {
            // find edges on either side
            boolean hitking = false;
            boolean hitEnemy = false;
            // 0 = no king, 1 = king on side 1, -1 = king on side 2
            int x1 = x + dx[i];
            int y1 = y + dy[i];

            while (GeneralChessFunctions.isValidCoord(x1, y1)) {
                if (x1 == newX && y1 == newY) {
                    break;
                }
                int fpeiceType = GeneralChessFunctions.getBoardWithPiece(x1, y1, isWhite, board);
                if (fpeiceType != ChessConstants.EMPTYINDEX) {
                    if(fpeiceType == ChessConstants.KINGINDEX){
                        hitking = true;
                    }
                    break;
                }
                int epeiceType = GeneralChessFunctions.getBoardWithPiece(x1, y1, !isWhite, board);
                if (epeiceType != ChessConstants.EMPTYINDEX) {
                    if (epeiceType == ChessConstants.QUEENINDEX) {
                        hitEnemy = true;
                    } else if (i < 2 && epeiceType == ChessConstants.BISHOPINDEX) {
                        // bishop possibility
                        hitEnemy = true;
                    } else if (epeiceType == ChessConstants.ROOKINDEX) {
                        // rook possibility
                        hitEnemy = true;
                    }
                    break;
                } else if (GeneralChessFunctions.checkIfContains(x1, y1, isWhite, board)) {
                    break;
                }
                x1 += dx[i];
                y1 += dy[i];
            }
            int x2 = x - dx[i];
            int y2 = y - dy[i];
            // todo possible optimization: put both directions in same loop and go for max dir length
            while (GeneralChessFunctions.isValidCoord(x2, y2)) {
                if (x2 == newX && y2 == newY) {
                    break;
                }
                int fpeiceType = GeneralChessFunctions.getBoardWithPiece(x2, y2, isWhite, board);
                if (fpeiceType != ChessConstants.EMPTYINDEX) {
                    if(fpeiceType == ChessConstants.KINGINDEX){
                        hitking = true;
                    }
                    break;
                }
                int epeiceType = GeneralChessFunctions.getBoardWithPiece(x2, y2, !isWhite, board);
                if (epeiceType != ChessConstants.EMPTYINDEX) {
                    if (epeiceType == ChessConstants.QUEENINDEX) {
                        hitEnemy = true;
                    } else if (i < 2 && epeiceType == ChessConstants.BISHOPINDEX) {
                        // bishop possibility
                        hitEnemy = true;
                    } else if (epeiceType == ChessConstants.ROOKINDEX) {
                        // rook possibility
                        hitEnemy = true;
                    }
                    break;
                } else if (GeneralChessFunctions.checkIfContains(x2, y2, isWhite, board)) {
                    break;
                }
                x2 -= dx[i];
                y2 -= dy[i];
            }
            if (hitking && hitEnemy) {
                return true;
            }


        }
        return false;
    }

    /**
     * This method takes board and position information and then gives you the possible squares a pawn can move
     * The is for check flag is called when we want to just calculate all squares that a pawn can move regardless of shadowing
     **/
    private static List<XYcoord> calculatePawnMoves(int x, int y, boolean isWhite, ChessPosition pos, boolean isforcheck) {
        BitBoardWrapper board = pos.board;
        ArrayList<XYcoord> moves = new ArrayList<>();
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
                        // pawn is in the right position so add en passant
                        int midY = (moveThatCreated.getOldY() + moveThatCreated.getNewY()) / 2;
                        XYcoord passantMove = new XYcoord(moveThatCreated.getNewX(), midY);
                        passantMove.setEnPassant(true);
                        moves.add(passantMove);
                    }


                }
            }
        }

        if (GeneralChessFunctions.isValidCoord(eatX1, eatY) && GeneralChessFunctions.checkIfContains(eatX1, eatY, !isWhite, board)) {
            // pawn can capture to the right
            if (isforcheck || !willResultInDead(x, y, eatX1, eatY, isWhite, board)) {
                // ignore shadowing move if is for check
                moves.add(new XYcoord(eatX1, eatY).setPromoHack(eatY == pawnEnd));
            }

        }
        if (GeneralChessFunctions.isValidCoord(eatX2, eatY) && GeneralChessFunctions.checkIfContains(eatX2, eatY, !isWhite, board)) {
            // pawn can capture to the left
            if (isforcheck || !willResultInDead(x, y, eatX2, eatY, isWhite, board)) {
                // ignore shadowing move if is for check
                moves.add(new XYcoord(eatX2, eatY).setPromoHack(eatY == pawnEnd));
            }

        }
        int depth = y == pawnHome ? 2 : 1;

        for (int i = 1; i < depth + 1; i++) {
            int newY = y + i * move;
            // pawns cannot eat forwards
            if (GeneralChessFunctions.isValidCoord(x, newY) && !GeneralChessFunctions.checkIfContains(x, newY, board, "calcpawn")[0]) {
                // pawn can capture to the right
                if (isforcheck || !willResultInDead(x, y, x, newY, isWhite, board)) {
                    // ignore shadowing move if is for check
                    moves.add(new XYcoord(x, newY).setPromoHack(newY == pawnEnd));
                } else {
                    break;
                }
            } else {
                break;
            }
        }
        return moves;

    }

    /**
     * Given a board and square, calculate all possible moves that a knight can make. Has the same isforcheck var as above that ignores shadowing (as for checks not needed
     * The new flag is edges only. This is used when we are trying to find all possible attackers on the edges. In the knights case this is simple as we just check for enemies on all squares
     **/
    private static List<XYcoord> calculateKnightMoves(int x, int y, boolean isWhite, boolean edgesOnly, BitBoardWrapper board, boolean isforcheck) {
        ArrayList<XYcoord> moves = new ArrayList<>();

        int[] dx = {1, 2, 2, 1, -1, -2, -2, -1};
        int[] dy = {-2, -1, 1, 2, 2, 1, -1, -2};

        for (int i = 0; i < 8; i++) {
            int newX = x + dx[i];
            int newY = y + dy[i];
            if (GeneralChessFunctions.isValidCoord(newX, newY)) {
                boolean[] boardInfo = GeneralChessFunctions.checkIfContains(newX, newY, board, "knight");

                if (!boardInfo[0] || boardInfo[1] != isWhite) { //must be no hit or not your own color

                    if (edgesOnly && boardInfo[0]) { // if it is a hit, it must not be your own color
                        // enemy edge so add
                        moves.add(new XYcoord(newX, newY));
                    } else if (isforcheck || !willResultInDead(x, y, newX, newY, isWhite, board)) {
                        moves.add(new XYcoord(newX, newY));

                    }
                }
            }
        }

        return moves;
    }

    private static List<XYcoord> calculateBishopMoves(int x, int y, boolean isWhite, boolean edgesOnly, int direction, BitBoardWrapper board, boolean isForCheck) {
        ArrayList<XYcoord> moves = new ArrayList<>();
        int i = 0;
        int max = 4;
        if (direction != ChessConstants.EMPTYINDEX) {
            // isolate a single file
            if (direction < 0 || direction > 3) {
                // check if direction is in range 0-3 else index out of bounds
                logger.error("Invalid direction provided to bishop moves! Dir: " + direction);
                return null;
            }
            i = direction;
            max = direction + 1;
        }
        int[] dx = {1, 1, -1, -1};
        int[] dy = {1, -1, 1, -1};

        while (i < max) {
            int newX = x;
            int newY = y;
            while (true) {
                newX += dx[i];
                newY += dy[i];
                boolean willDie = false;
                if (!isForCheck) {
                    willDie = willResultInDead(x, y, newX, newY, isWhite, board);
                }
                if (GeneralChessFunctions.isValidCoord(newX, newY) && !willDie) {
                    boolean containsFriend = GeneralChessFunctions.checkIfContains(newX, newY, isWhite, board);
                    boolean containsEnemy = GeneralChessFunctions.checkIfContains(newX, newY, !isWhite, board);

                    XYcoord response = new XYcoord(newX, newY, i);
                    if (containsEnemy) {
                        moves.add(response);
                        break;
                    }
                    if (!containsFriend) {
                        if (!edgesOnly) {
                            moves.add(response);
                        }
                    } else {
                        break;
                    }

                } else {

                    break;
                }
            }
            i++;
        }

        return moves;
    }

    private static List<XYcoord> calculateRookMoves(int x, int y, boolean isWhite, boolean edgesOnly, int direction, BitBoardWrapper board, boolean isForCheck) {
        ArrayList<XYcoord> moves = new ArrayList<>();
        int i = 0;
        int max = 4;
        if (direction != ChessConstants.EMPTYINDEX) {
            if (direction < 0 || direction > 3) {
                // check if direction is in range 0-3 else index out of bounds
                logger.error("Invalid direction provided to bishop moves! Dir: " + direction);
                return null;
            }
            i = direction;
            max = direction + 1;
        }

        int[] dx = {1, -1, 0, 0};
        int[] dy = {0, 0, 1, -1};
        while (i < max) {
            int newX = x;
            int newY = y;
            while (true) {
                newX += dx[i];
                newY += dy[i];
                boolean willDie = false;

                if (!isForCheck) {
                    willDie = willResultInDead(x, y, newX, newY, isWhite, board);
                }

                if (GeneralChessFunctions.isValidCoord(newX, newY) && !willDie) {
                    boolean containsFriend = GeneralChessFunctions.checkIfContains(newX, newY, isWhite, board);
                    boolean containsEnemy = GeneralChessFunctions.checkIfContains(newX, newY, !isWhite, board);

                    XYcoord response = new XYcoord(newX, newY, i);
                    if (containsEnemy) {
                        moves.add(response);
                        break;
                    }
                    if (!containsFriend) {
                        if (!edgesOnly) {
                            moves.add(response);
                        }
                    } else {
                        break;
                    }


                } else {
                    break;
                }
            }
            i++;
        }

        return moves;
    }

    private static List<XYcoord> calculateQueenMoves(int x, int y, boolean isWhite, boolean edgesOnly, BitBoardWrapper board, boolean isforCheck) {
        List<XYcoord> rookMoves = calculateRookMoves(x, y, isWhite, edgesOnly, ChessConstants.EMPTYINDEX, board, isforCheck);
        List<XYcoord> bishopMoves = calculateBishopMoves(x, y, isWhite, edgesOnly, ChessConstants.EMPTYINDEX, board, isforCheck);

        rookMoves.addAll(bishopMoves);

        return rookMoves;
    }

    private static List<XYcoord> basicKingMoveCalc(int x, int y, boolean isWhite, BitBoardWrapper board) {
        ArrayList<XYcoord> moves = new ArrayList<>();
        int[] dx = {1, -1, 0, 0, 1, -1, 1, -1};
        int[] dy = {0, 0, 1, -1, 1, -1, -1, 1};

        for (int i = 0; i < 8; i++) {
            int newX = x + dx[i];
            int newY = y + dy[i];
            if (GeneralChessFunctions.isValidCoord(newX, newY) && !GeneralChessFunctions.checkIfContains(newX, newY, isWhite, board)) {
                moves.add(new XYcoord(newX, newY));
            }
        }
        return moves;
    }

    private static List<XYcoord> calculateKingMoves(int x, int y, boolean isWhite, BitBoardWrapper board, ChessStates gameState) {

        ArrayList<XYcoord> moves = new ArrayList<>();
        boolean canCastle = isWhite ? gameState.isWhiteCastleRight() : gameState.isBlackCastleRight();
        boolean shortRook = isWhite ? gameState.isWhiteShortRookRight() : gameState.isBlackShortRookRight();
        boolean longRook = isWhite ? gameState.isWhiteLongRookRight() : gameState.isBlackLongRookRight();

        if (canCastle && !isChecked(x, y, isWhite, board)) {
            // short castle // todo gamestates giving castle right even though xy is not at home location!!!
            if (shortRook && !GeneralChessFunctions.checkIfContains(x + 1, y, board, "kingCaslte")[0] && !GeneralChessFunctions.checkIfContains(x + 2, y, board, "kingCaslte")[0] && !isChecked(x + 1, y, isWhite, board) && !isChecked(x + 2, y, isWhite, board)) {
                moves.add(new XYcoord(x + 2, y, true));
            }
            // long castle
            if (longRook && !GeneralChessFunctions.checkIfContains(x - 1, y, board, "kingCaslte")[0] && !GeneralChessFunctions.checkIfContains(x - 2, y, board, "kingCaslte")[0] && !GeneralChessFunctions.checkIfContains(x - 3, y, board, "kingCaslte")[0] && !isChecked(x - 1, y, isWhite, board) && !isChecked(x - 2, y, isWhite, board) && !isChecked(x - 3, y, isWhite, board)) {
                moves.add(new XYcoord(x - 2, y, true));

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
                board.makeTempChange(x, y, newX, newY, ChessConstants.KINGINDEX, isWhite);
                boolean newSqareIsChecked = isChecked(newX, newY, isWhite, board);
                board.popTempChange();
                if (!GeneralChessFunctions.checkIfContains(newX, newY, isWhite, board) && !newSqareIsChecked) {
                    moves.add(new XYcoord(newX, newY));
                }

            }


        }
        return moves;
    }

    public static boolean isAnyNotMovePossible(boolean isWhite, ChessPosition pos, ChessStates gameState) {
        List<XYcoord> peices = GeneralChessFunctions.getPieceCoordsForComputer(isWhite ? pos.board.getWhitePieces() : pos.board.getBlackPieces());
        for (XYcoord pcoord : peices) {
            List<XYcoord> piecePossibleMoves = getPossibleMoves(pcoord.x, pcoord.y, isWhite, pos, gameState, pcoord.peiceType);
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
        // general checking if a square is checked
        List<XYcoord> possibleRookFiles = calculateRookMoves(x, y, isWhite, true, ChessConstants.EMPTYINDEX, board, true);
        List<XYcoord> possibleBishopFiles = calculateBishopMoves(x, y, isWhite, true, ChessConstants.EMPTYINDEX, board, true);
        List<XYcoord> possibleHorseJumps = calculateKnightMoves(x, y, isWhite, true, board, true);
        List<XYcoord> possibleKingMoves = basicKingMoveCalc(x, y, isWhite, board);
        // check pawns
        int jump = isWhite ? 1 : -1;
        if (GeneralChessFunctions.isValidCoord(x - jump, y - jump) && getPieceType(x - jump, y - jump, !isWhite, board).equals("Pawn")) {
            return true;
        }
        if (GeneralChessFunctions.isValidCoord(x + jump, y - jump) && getPieceType(x + jump, y - jump, !isWhite, board).equals("Pawn")) {
            return true;
        }
        for (XYcoord s : possibleKingMoves) {
            String peiceType = getPieceType(s.x, s.y, !isWhite, board);
            if (peiceType.equals("King")) {
                return true;
            }
        }
        for (XYcoord s : possibleRookFiles) {
            String peiceType = getPieceType(s.x, s.y, !isWhite, board);
            if (peiceType.equals("Rook") || peiceType.equals("Queen")) {
                return true;
            }
        }
        for (XYcoord s : possibleHorseJumps) {
            String peiceType = getPieceType(s.x, s.y, !isWhite, board);
            if (peiceType.equals("Knight")) {
                return true;
            }
        }
        for (XYcoord s : possibleBishopFiles) {
            String peiceType = getPieceType(s.x, s.y, !isWhite, board);
            if (peiceType.equals("Bishop") || peiceType.equals("Queen")) {
                return true;
            }
        }
        return false;
    }

    private static List<XYcoord> getCheckedFile(boolean isWhite, BitBoardWrapper board) {
        // general checking if a square is checked
        ArrayList<XYcoord> files = new ArrayList<>();
        XYcoord kingLocation = isWhite ? board.getWhiteKingLocation() : board.getBlackKingLocation();
//      XYcoord kingLocation = getPieceCoords(isWhite ? board.getWhitePieces()[5] : board.getBlackPieces()[5]).get(0);

        int x = kingLocation.x;
        int y = kingLocation.y;
        List<XYcoord> possibleRookFiles = calculateRookMoves(x, y, isWhite, true, ChessConstants.EMPTYINDEX, board, true);
        List<XYcoord> possibleBishopFiles = calculateBishopMoves(x, y, isWhite, true, ChessConstants.EMPTYINDEX, board, true);
        List<XYcoord> possibleHorseJumps = calculateKnightMoves(x, y, isWhite, true, board, true);
        // check pawns
        int jump = isWhite ? 1 : -1;

        if (GeneralChessFunctions.isValidCoord(x - jump, y - jump) && getPieceType(x - jump, y - jump, !isWhite, board).equals("Pawn")) {
            retainIfNotEmpty(files, new XYcoord(x - jump, y - jump));
        }

        if (GeneralChessFunctions.isValidCoord(x + jump, y - jump) && getPieceType(x + jump, y - jump, !isWhite, board).equals("Pawn")) {
            retainIfNotEmpty(files, new XYcoord(x + jump, y - jump));

        }

        for (XYcoord s : possibleRookFiles) {
            String peiceType = getPieceType(s.x, s.y, !isWhite, board);
            if (peiceType.equals("Rook") || peiceType.equals("Queen")) {
                List<XYcoord> filtered = calculateRookMoves(x, y, isWhite, false, s.direction, board, true);
                retainIfNotEmpty(files, filtered);

            }
        }
        for (XYcoord s : possibleHorseJumps) {
            String peiceType = getPieceType(s.x, s.y, !isWhite, board);
            if (peiceType.equals("Knight")) {
                retainIfNotEmpty(files, new XYcoord(s.x, s.y));


            }
        }
        for (XYcoord s : possibleBishopFiles) {
            String peiceType = getPieceType(s.x, s.y, !isWhite, board);

            if (peiceType.equals("Bishop") || peiceType.equals("Queen")) {
                List<XYcoord> filtered = calculateBishopMoves(x, y, isWhite, false, s.direction, board, true);
                retainIfNotEmpty(files, filtered);

            }
        }
        return files;
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

    public static boolean isCheckmated(boolean isWhite, ChessPosition pos, ChessStates gameState) {
        return isChecked(isWhite, pos.board) && isAnyNotMovePossible(isWhite, pos, gameState);
    }

    public static boolean isCheckmated(ChessPosition pos, ChessStates gameState) {
        return (isChecked(false, pos.board) && isAnyNotMovePossible(false, pos, gameState)) || (isChecked(true, pos.board) && isAnyNotMovePossible(true, pos, gameState));
    }

    public static int getNumAttackers(int x, int y, boolean isWhite, BitBoardWrapper board) {
        // general checking if a square is checked
        int attackerCount = 0;
        List<XYcoord> possibleRookFiles = calculateRookMoves(x, y, isWhite, true, ChessConstants.EMPTYINDEX, board, true);
        List<XYcoord> possibleBishopFiles = calculateBishopMoves(x, y, isWhite, true, ChessConstants.EMPTYINDEX, board, true);
        List<XYcoord> possibleHorseJumps = calculateKnightMoves(x, y, isWhite, true, board, true);
        List<XYcoord> possibleKingMoves = basicKingMoveCalc(x, y, isWhite, board);
        // check pawns
        int jump = isWhite ? 1 : -1;
        if (GeneralChessFunctions.isValidCoord(x - jump, y - jump) && getPieceType(x - jump, y - jump, !isWhite, board).equals("Pawn")) {
            attackerCount++;
        }
        if (GeneralChessFunctions.isValidCoord(x + jump, y - jump) && getPieceType(x + jump, y - jump, !isWhite, board).equals("Pawn")) {
            attackerCount++;
        }
        for (XYcoord s : possibleKingMoves) {
            String peiceType = getPieceType(s.x, s.y, !isWhite, board);
            if (peiceType.equals("King")) {
                attackerCount++;
            }
        }
        for (XYcoord s : possibleRookFiles) {
            String peiceType = getPieceType(s.x, s.y, !isWhite, board);
            if (peiceType.equals("Rook") || peiceType.equals("Queen")) {
                attackerCount++;
            }
        }
        for (XYcoord s : possibleHorseJumps) {
            String peiceType = getPieceType(s.x, s.y, !isWhite, board);
            if (peiceType.equals("Knight")) {
                attackerCount++;
            }
        }
        for (XYcoord s : possibleBishopFiles) {
            String peiceType = getPieceType(s.x, s.y, !isWhite, board);
            if (peiceType.equals("Bishop") || peiceType.equals("Queen")) {
                attackerCount++;
            }
        }
        return attackerCount;
    }

    public static List<String>[] getChangesNeeded(BitBoardWrapper currentBoard, BitBoardWrapper newBoard) {
        // given two boards, find what needs to be added and removed so that you can make minimal graphical changes
        long[] whitePiecesCurrent = currentBoard.getWhitePieces();
        long[] blackPiecesCurrent = currentBoard.getBlackPieces();

        long[] whitePiecesNew = newBoard.getWhitePieces();
        long[] blackPiecesNew = newBoard.getBlackPieces();


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

    public static XYcoord findOldCoordinates(int newX, int newY, int pieceType, int ambgX, int ambgY, boolean isWhite, boolean isEating, ChessPosition pos, ChessStates gameState) {
        // either will be no ambiguity, or it will both, or either x/y ambiguity
        boolean noAmbg = ambgX == ChessConstants.EMPTYINDEX && ambgY == ChessConstants.EMPTYINDEX;
        boolean bothAmbg = ambgX != ChessConstants.EMPTYINDEX && ambgY != ChessConstants.EMPTYINDEX;
        boolean xAmbg = ambgX != ChessConstants.EMPTYINDEX;
        List<XYcoord> possibleOrigins;
        if (pieceType == ChessConstants.KINGINDEX) {
            possibleOrigins = AdvancedChessFunctions.basicKingMoveCalc(newX, newY, !isWhite, pos.board);

        } else if (pieceType == ChessConstants.PAWNINDEX) {
            possibleOrigins = AdvancedChessFunctions.fullPawnMoveCalcPGN(newX, newY, !isWhite, isEating, pos.board);
        } else {
            possibleOrigins = AdvancedChessFunctions.getMoveOfType(newX, newY, !isWhite, pieceType, pos, gameState, true);

        }
        for (XYcoord c : possibleOrigins) {
            boolean isPieceThere = GeneralChessFunctions.checkIfContains(c.x, c.y, isWhite ? pos.board.getWhitePieces()[pieceType] : pos.board.getBlackPieces()[pieceType]);
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
            possibleOrigins = AdvancedChessFunctions.basicKingMoveCalc(newX, newY, !isWhiteMove, pos.board);

        } else if (pieceType == ChessConstants.PAWNINDEX) {
            possibleOrigins = AdvancedChessFunctions.fullPawnMoveCalcPGN(newX, newY, !isWhiteMove, isEatingMove, pos.board);
        } else {
            // dont need gamestate so passing in dummy variable
            possibleOrigins = AdvancedChessFunctions.getMoveOfType(newX, newY, !isWhiteMove, pieceType, pos, ChessConstants.NEWGAMESTATE, true);
        }
        List<XYcoord> originsWithPiece = possibleOrigins.stream().filter(p -> GeneralChessFunctions.checkIfContains(p.x, p.y, isWhiteMove ? pos.board.getWhitePieces()[pieceType] : pos.board.getBlackPieces()[pieceType])).toList();
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

    public static int getColumnGivenFile(int file, int columnHint, boolean isWhite, long board) {
        int dir = isWhite ? 1 : -1;
        int end = isWhite ? 8 : -1;
        int reps = 0;
        for (int column = columnHint + dir; column != end; column += dir) {
            if (GeneralChessFunctions.checkIfContains(file, column, board)) {
                return column;
            }
            reps++;
            if (reps > 9) {
                break;
            }
        }
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
        long[] whitePieces = board.getWhitePieces();
        long[] blackPieces = board.getBlackPieces();
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
