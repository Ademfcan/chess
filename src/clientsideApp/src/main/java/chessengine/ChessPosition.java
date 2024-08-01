package chessengine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ChessPosition {
    public ChessMove getMoveThatCreatedThis() {
        return moveThatCreatedThis;
    }

    public void setMoveThatCreatedThis(ChessMove moveThatCreatedThis) {
        this.moveThatCreatedThis = moveThatCreatedThis;
    }


    private ChessMove moveThatCreatedThis;

    public BitBoardWrapper board;



    public ChessPosition(BitBoardWrapper board,ChessMove moveThatCreatedThis) {
        this.board = board;
        this.moveThatCreatedThis = moveThatCreatedThis;
    }
    public ChessPosition(ChessPosition curPos,ChessStates gameState,ChessMove newMove) {
        this(curPos.clonePosition(),gameState, newMove.getBoardIndex(), newMove.isWhite(), newMove.isCastleMove(),newMove.isEnPassant(),newMove.isPawnPromo(), newMove.getOldX(), newMove.getOldY(), newMove.getNewX(), newMove.getNewY(), newMove.getPromoIndx(),newMove.isCustomMove());

    }

    public BackendChessPosition toBackend(ChessStates gameState,boolean isDraw){
        return new BackendChessPosition(this,gameState,isDraw);
    }


    public ChessPosition(ChessPosition curPos,ChessStates curGamestate, int peiceType, boolean isWhite, boolean isCastle,boolean isEnPassant, boolean isPawnPromo, int oldX, int oldY, int newX, int newY, int promoIndex,boolean isCustomMove) {
        BitBoardWrapper board = curPos.board;
        long[] whitePieces = board.getWhitePieces();
        long[] blackPieces = board.getBlackPieces();
        long[] currentBoardMod = isWhite ? whitePieces : blackPieces;
        long[] enemyBoardMod = isWhite ? blackPieces : whitePieces;
        // general stuff to do wether its a custom move or not
        boolean isEating = false;
        int eatingIndex = ChessConstants.EMPTYINDEX;
        if(!isEnPassant){
            if(GeneralChessFunctions.checkIfContains(newX,newY,!isWhite,board)){
                // eating enemyPeice
                isEating = true;
                int boardWithPiece = GeneralChessFunctions.getBoardWithPiece(newX,newY,!isWhite,board);
                eatingIndex = boardWithPiece;
                enemyBoardMod[boardWithPiece] = GeneralChessFunctions.RemovePeice(newX,newY,enemyBoardMod[boardWithPiece]);

                // check remove rook right if rook is eaten
                if(eatingIndex == ChessConstants.ROOKINDEX) {
                    curGamestate.checkRemoveRookMoveRight(newX, newY);
                }
            }
            if(peiceType == ChessConstants.KINGINDEX){
                // update king location + remove castling right
                board.setKingLocation(isWhite,new XYcoord(newX,newY));
                curGamestate.removeCastlingRight(isWhite);

            }
            // remove rook castling right
            else if(peiceType == ChessConstants.ROOKINDEX) {
                curGamestate.checkRemoveRookMoveRight(oldX, oldY, isWhite);
            }
        }
        else{
            // en passant
            int backwardsDir = isWhite ? 1 : -1;
            // remove pawn
            enemyBoardMod[ChessConstants.PAWNINDEX] = GeneralChessFunctions.RemovePeice(newX,newY+backwardsDir,enemyBoardMod[ChessConstants.PAWNINDEX]);


        }

        if(isCustomMove){
            // skip all logic, as these moves are for adding extra pieces in sandbox
            currentBoardMod[peiceType] = GeneralChessFunctions.AddPeice(newX,newY,currentBoardMod[peiceType]);
        }
        else{
            // normal move
            if(isCastle){
                // check if short or long castle and move appropiately
                curGamestate.removeCastlingRight(isWhite);
                boolean isShortCastle = newX == 6;
                if(isShortCastle){
                    currentBoardMod[ChessConstants.ROOKINDEX] = GeneralChessFunctions.RemovePeice(7,newY,currentBoardMod[ChessConstants.ROOKINDEX]);
                    currentBoardMod[ChessConstants.ROOKINDEX] = GeneralChessFunctions.AddPeice(newX-1,newY,currentBoardMod[ChessConstants.ROOKINDEX]);
                }
                else{
                    currentBoardMod[ChessConstants.ROOKINDEX] = GeneralChessFunctions.RemovePeice(0,newY,currentBoardMod[ChessConstants.ROOKINDEX]);
                    currentBoardMod[ChessConstants.ROOKINDEX] = GeneralChessFunctions.AddPeice(newX+1,newY,currentBoardMod[ChessConstants.ROOKINDEX]);
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


        moveThatCreatedThis = new ChessMove(oldX,oldY,newX,newY,promoIndex,peiceType,isWhite,isCastle,isEating,eatingIndex,isEnPassant,isCustomMove);

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
            List<XYcoord> piecePossibleMoves = AdvancedChessFunctions.getPossibleMoves(coord.x,coord.y,isWhite,this,gameState);
            if(Objects.isNull(piecePossibleMoves)){
                ChessConstants.mainLogger.debug("Index of child positions error: " + GeneralChessFunctions.getPieceType(coord.peiceType));

            }
            int peiceType = coord.peiceType;
            for(XYcoord move : piecePossibleMoves){
                int endSquarePiece = GeneralChessFunctions.getBoardWithPiece(move.x,move.y,!isWhite,board);
                // stupid way of making sure you cant eat a king, real fix is making sure no possible moves allow this.

                if(endSquarePiece ==ChessConstants.KINGINDEX){
                    String type = GeneralChessFunctions.getPieceType(GeneralChessFunctions.getBoardWithPiece(coord.x,coord.y,isWhite,board));

                    ChessConstants.mainLogger.error("A move where the king is eaten has been found |||\n" + move.toString() + " piece: " + type + "\nmove that created this position |||\n" + (moveThatCreatedThis.toString() != null ? moveThatCreatedThis.toString() : "null move :(((((((((((((((((((") + "\nPOS:" + GeneralChessFunctions.getBoardDetailedString(this.board));
                }
                else {
                    boolean isEating = endSquarePiece != ChessConstants.EMPTYINDEX;
                    if (isEating && !move.isPawnPromo()) {
                        BackendChessPosition childPos = new BackendChessPosition(this.clonePosition(), gameState.cloneState(), peiceType, isWhite, move.isCastleMove(),move.isEnPassant(), move.isPawnPromo(), coord.x, coord.y, move.x, move.y, -10);
                        childPositionsPriority1.add(childPos);
                    } else if (move.isPawnPromo()) {
                        // pawn promo can be 4 options so have to add them all (knight, bishop,rook,queen)
                        for (int i = ChessConstants.KNIGHTINDEX; i < ChessConstants.KINGINDEX; i++) {
                            BackendChessPosition childPos = new BackendChessPosition(this.clonePosition(),gameState.cloneState(), peiceType, isWhite, move.isCastleMove(),move.isEnPassant(), move.isPawnPromo(), coord.x, coord.y, move.x, move.y, i);
                            childPositionsPriority2.add(childPos);

                        }

                    } else {
                        BackendChessPosition childPos = new BackendChessPosition(this.clonePosition(), gameState.cloneState(), peiceType, isWhite, move.isCastleMove(),move.isEnPassant(), move.isPawnPromo(), coord.x, coord.y, move.x, move.y, -10);
                        childPositionsPriority3.add(childPos);
                    }
                }




            }
        }
        childPositionsPriority1.sort((a, b) -> {
            int attackerValueA = ChessConstants.valueMap[a.getMoveThatCreatedThis().getBoardIndex()];
            int victimValueA = ChessConstants.valueMap[a.getMoveThatCreatedThis().getEatingIndex()];

            int attackerValueB = ChessConstants.valueMap[b.getMoveThatCreatedThis().getBoardIndex()];
            int victimValueB = ChessConstants.valueMap[b.getMoveThatCreatedThis().getEatingIndex()];

            int valueA = victimValueA - attackerValueA;
            int valueB = victimValueB - attackerValueB;
            return Integer.compare(valueB, valueA); // Sort in descending order of MVV-LVA
        });


        childPositionsPriority2.addAll(childPositionsPriority3);
        childPositionsPriority1.addAll(childPositionsPriority2);
        return childPositionsPriority1;
    }


    public List<ChessMove> getAllChildMoves(boolean isWhite, ChessStates gameState){
        List<ChessMove> childPositionsPriority1 = new ArrayList<>();
        List<ChessMove> childPositionsPriority2 = new ArrayList<>();
        List<ChessMove> childPositionsPriority3 = new ArrayList<>();
        List<XYcoord> peices = GeneralChessFunctions.getPieceCoordsForComputer(isWhite ? board.getWhitePieces() : board.getBlackPieces());
        for(XYcoord coord : peices){
            List<XYcoord> piecePossibleMoves = AdvancedChessFunctions.getPossibleMoves(coord.x,coord.y,isWhite,this,gameState);
            if(Objects.isNull(piecePossibleMoves)){
                ChessConstants.mainLogger.debug("Index of child positions error: " + GeneralChessFunctions.getPieceType(coord.peiceType));

            }
            int peiceType = coord.peiceType;
            for(XYcoord move : piecePossibleMoves){
                int endSquarePiece = GeneralChessFunctions.getBoardWithPiece(move.x,move.y,!isWhite,board);
                // stupid way of making sure you cant eat a king, real fix is making sure no possible moves allow this.

                if(endSquarePiece ==ChessConstants.KINGINDEX){
                    String type = GeneralChessFunctions.getPieceType(GeneralChessFunctions.getBoardWithPiece(coord.x,coord.y,isWhite,board));

                    ChessConstants.mainLogger.error("A move where the king is eaten has been found |||\n" + move.toString() + " piece: " + type + "\nmove that created this position |||\n" + (moveThatCreatedThis.toString() != null ? moveThatCreatedThis.toString() : "null move :(((((((((((((((((((")+ "\nPOS:" + GeneralChessFunctions.getBoardDetailedString(this.board));
                }
                else {
                    boolean isEating = endSquarePiece != ChessConstants.EMPTYINDEX;
                    if (isEating && !move.isPawnPromo()) {
                        ChessMove childMove = new ChessMove(coord.x, coord.y, move.x, move.y,ChessConstants.EMPTYINDEX,peiceType,isWhite,move.isCastleMove(),isEating,endSquarePiece,move.isEnPassant(),false);
                        childPositionsPriority1.add(childMove);
                    } else if (move.isPawnPromo()) {
                        // pawn promo can be 4 options so have to add them all (knight, bishop,rook,queen)
                        for (int i = ChessConstants.KNIGHTINDEX; i < ChessConstants.KINGINDEX; i++) {
                            ChessMove childMove = new ChessMove(coord.x, coord.y, move.x, move.y,i,peiceType,isWhite,move.isCastleMove(),isEating,endSquarePiece,move.isEnPassant() , false);
                            childPositionsPriority2.add(childMove);

                        }

                    } else {
                        ChessMove childMove = new ChessMove(coord.x, coord.y, move.x, move.y,ChessConstants.EMPTYINDEX,peiceType,isWhite,move.isCastleMove(),isEating,endSquarePiece,move.isEnPassant(),false);
                        childPositionsPriority3.add(childMove);
                    }
                }




            }
        }
        childPositionsPriority1.sort((a, b) -> {
            int attackerValueA = ChessConstants.valueMap[a.getBoardIndex()];
            int victimValueA = ChessConstants.valueMap[a.getEatingIndex()];

            int attackerValueB = ChessConstants.valueMap[b.getBoardIndex()];
            int victimValueB = ChessConstants.valueMap[b.getEatingIndex()];

            int valueA = victimValueA - attackerValueA;
            int valueB = victimValueB - attackerValueB;
            return Integer.compare(valueB, valueA); // Sort in descending order of MVV-LVA
        });


        childPositionsPriority2.addAll(childPositionsPriority3);
        childPositionsPriority1.addAll(childPositionsPriority2);
        return childPositionsPriority1;
    }





}
