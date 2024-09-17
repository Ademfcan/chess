package chessengine.ChessRepresentations;

import chessengine.Computation.ComputerHelperFunctions;
import chessengine.Functions.AdvancedChessFunctions;
import chessengine.Functions.GeneralChessFunctions;
import chessengine.Misc.ChessConstants;

import java.util.*;

public class ChessPosition {
    public BitBoardWrapper board;
    private ChessMove moveThatCreatedThis;


    public ChessPosition(BitBoardWrapper board, ChessMove moveThatCreatedThis) {
        this.board = board;
        this.moveThatCreatedThis = moveThatCreatedThis;
    }

    public ChessPosition(ChessPosition curPos, ChessStates gameState, ChessMove newMove) {
        this(curPos.clonePosition(), gameState, newMove.getBoardIndex(), newMove.isWhite(), newMove.isCastleMove(),newMove.isEating(),newMove.getEatingIndex(), newMove.isEnPassant(), newMove.isPawnPromo(), newMove.getOldX(), newMove.getOldY(), newMove.getNewX(), newMove.getNewY(), newMove.getPromoIndx(), newMove.isCustomMove());

    }


    public ChessPosition(ChessPosition curPos, ChessStates curGamestate, int pieceType, boolean isWhite, boolean isCastle,boolean isEating,int eatingIndex, boolean isEnPassant, boolean isPawnPromo, int oldX, int oldY, int newX, int newY, int promoIndex, boolean isCustomMove) {
        BitBoardWrapper board = curPos.board;
        int newBitIndex = GeneralChessFunctions.positionToBitIndex(newX,newY);
        int oldBitIndex = GeneralChessFunctions.positionToBitIndex(oldX,oldY);
        boolean enemyColor = !isWhite;
        boolean friendlyColor = isWhite;
        // general stuff to do wether its a custom move or not
        if (!isEnPassant) {
            if (isEating) {
                // eating enemyPeice
                board.removePiece(newBitIndex,eatingIndex,enemyColor);

                // check remove rook right if rook is eaten
                if (eatingIndex == ChessConstants.ROOKINDEX) {
                    curGamestate.checkRemoveRookMoveRight(newX, newY);
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
            // remove pawn
            board.removePiece(GeneralChessFunctions.positionToBitIndex(newX,newY+backwardsDir),ChessConstants.PAWNINDEX,enemyColor);


        }

        if (isCustomMove) {
            // skip all logic, as these moves are for adding extra pieces in sandbox
            board.addPiece(newBitIndex,pieceType,friendlyColor);
            board.removePiece(oldBitIndex,pieceType,friendlyColor);
        } else {
            // normal move
            if (isCastle) {
                // check if short or long castle and move appropiately
                curGamestate.removeCastlingRight(isWhite);
                boolean isShortCastle = newX == 6;
                if (isShortCastle) {
                    board.removePiece(GeneralChessFunctions.positionToBitIndex(7,newY),ChessConstants.ROOKINDEX,friendlyColor);
                    board.addPiece(GeneralChessFunctions.positionToBitIndex(newX-1,newY),ChessConstants.ROOKINDEX,friendlyColor);


                } else {
                    board.removePiece(GeneralChessFunctions.positionToBitIndex(0,newY),ChessConstants.ROOKINDEX,friendlyColor);
                    board.addPiece(GeneralChessFunctions.positionToBitIndex(newX+1,newY),ChessConstants.ROOKINDEX,friendlyColor);
                }
            }


            // remove peice at old spot
            board.removePiece(oldBitIndex,pieceType,friendlyColor);

            if (isPawnPromo) {
                // promo with new peice at new location
                board.addPiece(newBitIndex,promoIndex,friendlyColor);
            } else {
                // move to new place as usual
                board.addPiece(newBitIndex,pieceType,friendlyColor);
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

    public BackendChessPosition toBackend(ChessStates gameState, boolean isDraw) {
        return new BackendChessPosition(this, gameState, isDraw);
    }

    public ChessPosition clonePosition() {
        return new ChessPosition(board.cloneBoard(), moveThatCreatedThis.cloneMove());
    }

    public List<BackendChessPosition> getAllChildPositions(boolean isWhite, ChessStates gameState) {
        List<BackendChessPosition> childPositionsPriority1 = new ArrayList<>();
        List<XYcoord> peices = GeneralChessFunctions.getPieceCoordsForComputer(isWhite ? board.getWhitePiecesBB() : board.getBlackPiecesBB());
        for (XYcoord coord : peices) {
            List<XYcoord> piecePossibleMoves = AdvancedChessFunctions.getPossibleMoves(coord.x, coord.y, isWhite, this, gameState,coord.peiceType);
            if (Objects.isNull(piecePossibleMoves)) {
                ChessConstants.mainLogger.debug("Index of child positions error: " + GeneralChessFunctions.getPieceType(coord.peiceType));
                return null;
            }
            int peiceType = coord.peiceType;
            for (XYcoord move : piecePossibleMoves) {
                int endSquarePiece = GeneralChessFunctions.getBoardWithPiece(move.x, move.y, !isWhite, board);

                boolean isEating = endSquarePiece != ChessConstants.EMPTYINDEX;
                if (!move.isPawnPromo()) {
                    BackendChessPosition childPos = new BackendChessPosition(this.clonePosition(), gameState.cloneState(), peiceType, isWhite, move.isCastleMove(),isEating,endSquarePiece, move.isEnPassant(), move.isPawnPromo(), coord.x, coord.y, move.x, move.y, -10);
                    childPositionsPriority1.add(childPos);
                } else {
                    // pawn promo can be 4 options so have to add them all (knight, bishop,rook,queen)
                    for (int i = ChessConstants.KNIGHTINDEX; i < ChessConstants.KINGINDEX; i++) {
                        BackendChessPosition childPos = new BackendChessPosition(this.clonePosition(), gameState.cloneState(), peiceType, isWhite, move.isCastleMove(),isEating,endSquarePiece, move.isEnPassant(), move.isPawnPromo(), coord.x, coord.y, move.x, move.y, i);
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
            List<XYcoord> piecePossibleMoves = AdvancedChessFunctions.getPossibleMoves(coord.x, coord.y, isWhite, this, gameState,coord.peiceType);
            if (Objects.isNull(piecePossibleMoves)) {
                ChessConstants.mainLogger.debug("Index of child positions error: " + GeneralChessFunctions.getPieceType(coord.peiceType));
                return null;
            }
            for (XYcoord move : piecePossibleMoves) {
                int endSquarePiece = GeneralChessFunctions.getBoardWithPiece(move.x, move.y, !isWhite, board);
                boolean isEating = endSquarePiece != ChessConstants.EMPTYINDEX;
                if (!move.isPawnPromo()) {
                    ChessMove childMove = new ChessMove(coord.x, coord.y, move.x, move.y, ChessConstants.EMPTYINDEX, coord.peiceType, isWhite, move.isCastleMove(), isEating, endSquarePiece, move.isEnPassant(), false);
                    childPositionsPriority1.add(childMove);
                }
                else {
                    // pawn promo can be 4 options so have to add them all (knight, bishop,rook,queen)
                    for (int i = ChessConstants.KNIGHTINDEX; i < ChessConstants.KINGINDEX; i++) {
                        ChessMove childMove = new ChessMove(coord.x, coord.y, move.x, move.y, i, coord.peiceType, isWhite, move.isCastleMove(), isEating, endSquarePiece, move.isEnPassant(), false);
                        childPositionsPriority1.add(childMove);

                    }

                }
            }


        }
        childPositionsPriority1.sort(Comparator.comparing(ComputerHelperFunctions::getMoveValue));
        return childPositionsPriority1;
    }
}
