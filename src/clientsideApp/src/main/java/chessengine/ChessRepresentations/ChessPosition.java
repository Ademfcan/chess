package chessengine.ChessRepresentations;

import chessengine.Computation.ComputerHelperFunctions;
import chessengine.Functions.AdvancedChessFunctions;
import chessengine.Functions.GeneralChessFunctions;
import chessengine.Misc.ChessConstants;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ChessPosition {
    public BitBoardWrapper board;
    private ChessMove moveThatCreatedThis;


    public ChessPosition(BitBoardWrapper board, ChessMove moveThatCreatedThis) {
        this.board = board;
        this.moveThatCreatedThis = moveThatCreatedThis;
    }

    public ChessPosition(ChessPosition curPos, ChessStates gameState, ChessMove newMove) {
        this(curPos.clonePosition(), gameState, newMove.getBoardIndex(), newMove.isWhite(), newMove.isCastleMove(), newMove.isEating(), newMove.getEatingIndex(), newMove.isEnPassant(), newMove.isPawnPromo(), newMove.getOldX(), newMove.getOldY(), newMove.getNewX(), newMove.getNewY(), newMove.getPromoIndx(), newMove.isCustomMove());

    }


    public ChessPosition(ChessPosition curPos, ChessStates curGamestate, int pieceType, boolean isWhite, boolean isCastle, boolean isEating, int eatingIndex, boolean isEnPassant, boolean isPawnPromo, int oldX, int oldY, int newX, int newY, int promoIndex, boolean isCustomMove) {
        BitBoardWrapper board = curPos.board;
        long[] whitePieces = board.getWhitePiecesBB();
        long[] blackPieces = board.getBlackPiecesBB();
        long[] currentBoardMod = isWhite ? whitePieces : blackPieces;
        long[] enemyBoardMod = isWhite ? blackPieces : whitePieces;
        int newBitIndex = GeneralChessFunctions.positionToBitIndex(newX, newY);
        int oldBitIndex = GeneralChessFunctions.positionToBitIndex(oldX, oldY);
        boolean enemyColor = !isWhite;
        boolean friendlyColor = isWhite;
        // general stuff to do wether its a custom move or not
        if (!isEnPassant) {
            if (isEating) {
                // eating enemyPeice
                if (!GeneralChessFunctions.checkIfContains(newX, newY, enemyBoardMod[eatingIndex])) {
                    ChessConstants.mainLogger.error("Eating with no piece there!!");
                    GeneralChessFunctions.printBoardDetailed(board);
                }
                board.removePiece(newBitIndex, eatingIndex, enemyColor);

                // check remove rook right if rook is eaten
                if (eatingIndex == ChessConstants.ROOKINDEX) {
                    curGamestate.checkRemoveRookMoveRight(newX, newY, !isWhite);
                }
            }
            if (pieceType == ChessConstants.KINGINDEX) {
                // update king location + remove castling right
                board.setKingLocation(isWhite, new XYcoord(newX, newY));
                curGamestate.removeCastlingRight(isWhite);

            }
            // remove rook castling right
            else if (pieceType == ChessConstants.ROOKINDEX) {
                curGamestate.checkRemoveRookMoveRight(oldX, oldY, isWhite);
            }
        } else {
            // en passant
            int backwardsDir = isWhite ? 1 : -1;
            if (!GeneralChessFunctions.checkIfContains(newX, newY + backwardsDir, enemyBoardMod[ChessConstants.PAWNINDEX])) {
                ChessConstants.mainLogger.error("En passant when no piece behind!!");
                GeneralChessFunctions.printBoardDetailed(board);
            }
            // remove pawn
            board.removePiece(GeneralChessFunctions.positionToBitIndex(newX, newY + backwardsDir), ChessConstants.PAWNINDEX, enemyColor);

        }

        if (isCustomMove) {
            // skip all logic, as these moves are for adding extra pieces in sandbox
            board.addPiece(newBitIndex, pieceType, friendlyColor);
            board.removePiece(oldBitIndex, pieceType, friendlyColor);
        } else {
            // normal move
            if (isCastle) {
                // check if short or long castle and move appropiately
                curGamestate.removeCastlingRight(isWhite);
                boolean isShortCastle = newX == 6;
                if (isShortCastle) {
                    if (!GeneralChessFunctions.checkIfContains(7, newY, currentBoardMod[ChessConstants.ROOKINDEX])) {
                        ChessConstants.mainLogger.error("New chess position trying to castle when not possible!!!");
                        GeneralChessFunctions.printBoardDetailed(board);
                    }
                    board.removePiece(GeneralChessFunctions.positionToBitIndex(7, newY), ChessConstants.ROOKINDEX, friendlyColor);
                    board.addPiece(GeneralChessFunctions.positionToBitIndex(newX - 1, newY), ChessConstants.ROOKINDEX, friendlyColor);


                } else {
                    if (!GeneralChessFunctions.checkIfContains(0, newY, currentBoardMod[ChessConstants.ROOKINDEX])) {
                        ChessConstants.mainLogger.error("New chess position trying to castle when not possible!!!");
                        GeneralChessFunctions.printBoardDetailed(board);
                    }
                    board.removePiece(GeneralChessFunctions.positionToBitIndex(0, newY), ChessConstants.ROOKINDEX, friendlyColor);
                    board.addPiece(GeneralChessFunctions.positionToBitIndex(newX + 1, newY), ChessConstants.ROOKINDEX, friendlyColor);
                }
            }


            // remove peice at old spot
            board.removePiece(oldBitIndex, pieceType, friendlyColor);

            if (isPawnPromo) {
                // promo with new peice at new location
                board.addPiece(newBitIndex, promoIndex, friendlyColor);
            } else {
                // move to new place as usual
                board.addPiece(newBitIndex, pieceType, friendlyColor);
            }


        }
        board.updateAttackMasks();
        this.board = board;


        moveThatCreatedThis = new ChessMove(oldX, oldY, newX, newY, promoIndex, pieceType, isWhite, isCastle, isEating, eatingIndex, isEnPassant, isCustomMove);

    }

    public ChessMove getMoveThatCreatedThis() {
        return moveThatCreatedThis;
    }

    public void setMoveThatCreatedThis(ChessMove moveThatCreatedThis) {
        this.moveThatCreatedThis = moveThatCreatedThis;
    }

    public BackendChessPosition toBackend(ChessStates gameState, boolean isWhiteTurn) {
        return new BackendChessPosition(this, gameState, gameState.isStaleMated(), isWhiteTurn);
    }

    public ChessPosition clonePosition() {
        return new ChessPosition(board.cloneBoard(), moveThatCreatedThis.cloneMove());
    }

    public List<BackendChessPosition> getAllChildPositions(boolean isWhite, ChessStates gameState) {
        List<BackendChessPosition> childPositionsPriority1 = new ArrayList<>();
        List<XYcoord> peices = GeneralChessFunctions.getPieceCoordsForComputer(isWhite ? board.getWhitePiecesBB() : board.getBlackPiecesBB());
        for (XYcoord coord : peices) {
            List<XYcoord> piecePossibleMoves = AdvancedChessFunctions.getPossibleMoves(coord.x, coord.y, isWhite, this, gameState, coord.peiceType, false);
            if (Objects.isNull(piecePossibleMoves)) {
                ChessConstants.mainLogger.debug("Index of child positions error: " + GeneralChessFunctions.getPieceType(coord.peiceType));
                return null;
            }
            int peiceType = coord.peiceType;
            for (XYcoord move : piecePossibleMoves) {
                int endSquarePiece = GeneralChessFunctions.getBoardWithPiece(move.x, move.y, !isWhite, board);
                if (endSquarePiece == ChessConstants.KINGINDEX) {
                    System.out.println("You fucked up childpositions");
                    System.out.println(coord.peiceType);
                    System.out.println(this.getMoveThatCreatedThis() != null ? this.getMoveThatCreatedThis() : "null move");
                    System.out.println(coord);
                    System.out.println(move);
                    System.out.println(GeneralChessFunctions.getBoardDetailedString(this.board));
                }
                int pawnEnd = isWhite ? 0 : 7;
                boolean isCastle = coord.peiceType == ChessConstants.KINGINDEX && Math.abs(coord.x - move.x) > 1;
                boolean isPromo = coord.peiceType == ChessConstants.PAWNINDEX && move.y == pawnEnd;
                boolean isEating = endSquarePiece != ChessConstants.EMPTYINDEX;
                boolean isEnPassant = coord.peiceType == ChessConstants.PAWNINDEX && !isEating && coord.x != move.x;
                if (!isPromo) {
                    BackendChessPosition childPos = new BackendChessPosition(this.clonePosition(), gameState.cloneState(), !isWhite, peiceType, isWhite, isCastle, isEating, endSquarePiece, isEnPassant, false, coord.x, coord.y, move.x, move.y, -10);
                    childPositionsPriority1.add(childPos);
                } else {
                    // pawn promo can be 4 options so have to add them all (knight, bishop,rook,queen)
                    for (int i = ChessConstants.KNIGHTINDEX; i < ChessConstants.KINGINDEX; i++) {
                        BackendChessPosition childPos = new BackendChessPosition(this.clonePosition(), gameState.cloneState(), !isWhite, peiceType, isWhite, false, isEating, endSquarePiece, false, true, coord.x, coord.y, move.x, move.y, i);
                        childPositionsPriority1.add(childPos);

                    }


                }
            }
        }
        childPositionsPriority1.sort(Comparator.comparing(m -> ComputerHelperFunctions.getMoveValue(m.getMoveThatCreatedThis()))); // Sort in descending order of MVV-LVA
        return childPositionsPriority1;
    }


    public List<ChessMove> getAllChildMoves(boolean isWhite, ChessStates gameState) {
        List<ChessMove> childPositionsPriority1 = new ArrayList<>();
        List<XYcoord> peices = GeneralChessFunctions.getPieceCoordsForComputer(isWhite ? board.getWhitePiecesBB() : board.getBlackPiecesBB());
        for (XYcoord coord : peices) {
            List<XYcoord> piecePossibleMoves = AdvancedChessFunctions.getPossibleMoves(coord.x, coord.y, isWhite, this, gameState, coord.peiceType, false);
            if (Objects.isNull(piecePossibleMoves)) {
                ChessConstants.mainLogger.debug("Index of child positions error: " + GeneralChessFunctions.getPieceType(coord.peiceType));
                return null;
            }
            for (XYcoord move : piecePossibleMoves) {
                int endSquarePiece = GeneralChessFunctions.getBoardWithPiece(move.x, move.y, !isWhite, board);
                if (endSquarePiece == ChessConstants.KINGINDEX) {
                    System.out.println("You fucked up childMoves");
                    System.out.println(coord.peiceType);
                    System.out.println(this.getMoveThatCreatedThis() != null ? this.getMoveThatCreatedThis() : "null move");
                    System.out.println(coord);
                    System.out.println(move);
                    System.out.println(GeneralChessFunctions.getBoardDetailedString(this.board));
                }
                int pawnEnd = isWhite ? 0 : 7;
                boolean isCastle = coord.peiceType == ChessConstants.KINGINDEX && Math.abs(coord.x - move.x) > 1;
                boolean isPromo = coord.peiceType == ChessConstants.PAWNINDEX && move.y == pawnEnd;
                boolean isEating = endSquarePiece != ChessConstants.EMPTYINDEX;
                boolean isEnPassant = coord.peiceType == ChessConstants.PAWNINDEX && !isEating && coord.x != move.x;
                if (!isPromo) {
                    ChessMove childMove = new ChessMove(coord.x, coord.y, move.x, move.y, ChessConstants.EMPTYINDEX, coord.peiceType, isWhite, isCastle, isEating, endSquarePiece, isEnPassant, false);
                    childPositionsPriority1.add(childMove);
                } else {
                    // pawn promo can be 4 options so have to add them all (knight, bishop,rook,queen)
                    for (int i = ChessConstants.KNIGHTINDEX; i < ChessConstants.KINGINDEX; i++) {
                        ChessMove childMove = new ChessMove(coord.x, coord.y, move.x, move.y, i, coord.peiceType, isWhite, false, isEating, endSquarePiece, false, false);
                        childPositionsPriority1.add(childMove);

                    }

                }
            }


        }
        childPositionsPriority1.sort(Comparator.comparing(ComputerHelperFunctions::getMoveValue));
        return childPositionsPriority1;
    }
}
