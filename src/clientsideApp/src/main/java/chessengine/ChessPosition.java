package chessengine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ChessPosition {
    public ChessMove getMoveThatCreatedThis() {
        return moveThatCreatedThis;
    }

    private ChessMove moveThatCreatedThis;

    public BitBoardWrapper board;



    public ChessPosition(BitBoardWrapper board,ChessMove moveThatCreatedThis) {
        this.board = board;
        this.moveThatCreatedThis = moveThatCreatedThis;
    }
    public ChessPosition(ChessPosition pos,ChessStates gameState,ChessMove newMove) {
        this(pos,gameState, newMove.getBoardIndex(), newMove.isWhite(), newMove.isCastleMove(), newMove.isPawnPromo(), newMove.getOldX(), newMove.getOldY(), newMove.getNewX(), newMove.getNewY(), newMove.getPromoIndx(),newMove.isCustomMove());

    }

    public ChessPosition(ChessPosition pos,ChessStates gameState, int peiceType, boolean isWhite, boolean isCastle, boolean isPawnPromo, int oldX, int oldY, int newX, int newY, int promoIndex,boolean isCustomMove) {
        pos = pos.clonePosition();
        BitBoardWrapper board = pos.board;
        long[] whitePieces = board.getWhitePieces();
        long[] blackPieces = board.getBlackPieces();
        long[] currentBoardMod = isWhite ? whitePieces : blackPieces;
        long[] enemyBoardMod = isWhite ? blackPieces : whitePieces;
        // general stuff to do wether its a custom move or not
        boolean isEating = false;
        if(GeneralChessFunctions.checkIfContains(newX,newY,!isWhite,board)){
            // eating enemyPeice
            isEating = true;
            int boardWithPiece = GeneralChessFunctions.getBoardWithPiece(newX,newY,!isWhite,board);
            enemyBoardMod[boardWithPiece] = GeneralChessFunctions.RemovePeice(newX,newY,enemyBoardMod[boardWithPiece]);
        }
        if(peiceType == ChessConstants.KINGINDEX){
            // update king location
            board.setKingLocation(isWhite,new XYcoord(newX,newY));
            gameState.removeCastlingRight(isWhite);

        }
        else if(peiceType == ChessConstants.ROOKINDEX){
            gameState.checkRemoveRookMoveRight(oldX,oldY,isWhite);
        }
        if(isCustomMove){
            // skip all logic, as these moves are for adding extra pieces in sandbox
            currentBoardMod[peiceType] = GeneralChessFunctions.AddPeice(newX,newY,currentBoardMod[peiceType]);
        }
        else{
            // normal move
            if(isCastle){
                // check if short or long castle and move appropiately
                gameState.removeCastlingRight(isWhite);
                boolean isShortCastle = newX == 6;
                if(isShortCastle){
                    currentBoardMod[3] = GeneralChessFunctions.RemovePeice(7,newY,currentBoardMod[3]);
                    currentBoardMod[3] = GeneralChessFunctions.AddPeice(newX-1,newY,currentBoardMod[3]);
                }
                else{
                    currentBoardMod[3] = GeneralChessFunctions.RemovePeice(0,newY,currentBoardMod[3]);
                    currentBoardMod[3] = GeneralChessFunctions.AddPeice(newX+1,newY,currentBoardMod[3]);
                }
            }



            // remove peice at old spot
            currentBoardMod[peiceType] = GeneralChessFunctions.RemovePeice(oldX,oldY,currentBoardMod[peiceType]);

            if(isPawnPromo){
                // promo with new peice at new location
                currentBoardMod[promoIndex] = GeneralChessFunctions.AddPeice(newX,newY,currentBoardMod[promoIndex]);
            }
            else{
                // move to new place as usual
                currentBoardMod[peiceType] = GeneralChessFunctions.AddPeice(newX,newY,currentBoardMod[peiceType]);
            }


        }
        this.board = board;


        moveThatCreatedThis = new ChessMove(oldX,oldY,newX,newY,promoIndex,peiceType,isWhite,isCastle,isEating,isCustomMove);
        // todo!!!!!!!!!!!!!! fix ischecked and make isdraw (and efficiently :))

    }



    public ChessPosition clonePosition(){
        return new ChessPosition(board.cloneBoard(),moveThatCreatedThis.cloneMove());
    }
    public List<BackendChessPosition> getAllChildPositions(boolean isWhite, ChessStates gameState){
        List<BackendChessPosition> childPositionsPriority1 = new ArrayList<>();
        List<BackendChessPosition> childPositionsPriority2 = new ArrayList<>();
        List<BackendChessPosition> childPositionsPriority3 = new ArrayList<>();
        List<XYcoord> peices = GeneralChessFunctions.getPieceCoordsForComputer(isWhite ? board.getWhitePieces() : board.getBlackPieces());
        for(XYcoord coord : peices){
            List<XYcoord> piecePossibleMoves = AdvancedChessFunctions.getPossibleMoves(coord.x,coord.y,isWhite,board,gameState);
            if(Objects.isNull(piecePossibleMoves)){
                ChessConstants.mainLogger.debug("Index of child positions error: " + GeneralChessFunctions.getPieceType(coord.peiceType));
                ChessConstants.mainLogger.debug("error board size: " + board.getWhitePieces()[0]);

            }
            int peiceType = coord.peiceType;
            for(XYcoord move : piecePossibleMoves){
                int endSquarePiece = GeneralChessFunctions.getBoardWithPiece(move.x,move.y,!isWhite,board);
                // stupid way of making sure you cant eat a king, real fix is making sure no possible moves allow this.

                if(endSquarePiece ==5){
                    String type = GeneralChessFunctions.getPieceType(GeneralChessFunctions.getBoardWithPiece(coord.x,coord.y,isWhite,board));

                    ChessConstants.mainLogger.error("A move where the king is eaten has been found |||\n" + move.toString() + " piece: " + type + "\nmove that created this position |||\n" + (moveThatCreatedThis.toString() != null ? moveThatCreatedThis.toString() : "null move :((((((((((((((((((("));
                }
                else {
                    boolean isEating = endSquarePiece != -10;
                    if (isEating) {
                        BackendChessPosition childPos = new BackendChessPosition(this, gameState.cloneState(), peiceType, isWhite, move.isCastleMove(), false, coord.x, coord.y, move.x, move.y, -10);
                        childPositionsPriority1.add(childPos);
                    } else if (move.isPawnPromo()) {
                        // pawn promo can be 4 options so have to add them all (knight, bishop,rook,queen)
                        for (int i = 1; i < 5; i++) {
                            BackendChessPosition childPos = new BackendChessPosition(this,gameState.cloneState(), peiceType, isWhite, move.isCastleMove(), true, coord.x, coord.y, move.x, move.y, i);
                            childPositionsPriority2.add(childPos);

                        }

                    } else {
                        BackendChessPosition childPos = new BackendChessPosition(this, gameState.cloneState(), peiceType, isWhite, move.isCastleMove(), false, coord.x, coord.y, move.x, move.y, -10);
                        childPositionsPriority3.add(childPos);
                    }
                }




            }
        }
        childPositionsPriority2.addAll(childPositionsPriority3);
        childPositionsPriority1.addAll(childPositionsPriority2);
        return childPositionsPriority1;
    }





}
