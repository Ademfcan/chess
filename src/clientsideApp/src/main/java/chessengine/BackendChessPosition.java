package chessengine;

import java.util.Stack;

public class BackendChessPosition extends ChessPosition{
    public ChessStates gameState;

    private boolean isDraw;

    private Stack<ChessMove> movesThatCreated;

    public BackendChessPosition(ChessPosition pos,ChessStates gameState,boolean isDraw){
        super(pos.board,pos.getMoveThatCreatedThis());
        this.gameState = gameState;
        this.isDraw = isDraw;
        this.movesThatCreated = new Stack<>();

    }

    public BackendChessPosition(ChessPosition pos,ChessStates gameState, int peiceType, boolean isWhite, boolean isCastle,boolean isEnPassant, boolean isPawnPromo, int oldX, int oldY, int newX, int newY, int promoIndex){
        super( pos, gameState,  peiceType,  isWhite,  isCastle,isEnPassant,  isPawnPromo,  oldX,  oldY,  newX,  newY,  promoIndex,false);
        this.gameState = gameState;
        isDraw = this.gameState.makeNewMoveAndCheckDraw(this,true,false);
        this.movesThatCreated = new Stack<>();
    }


    public void makeLocalPositionMove(ChessMove move){
        long[] whitePieces = board.getWhitePieces();
        long[] blackPieces = board.getBlackPieces();
        boolean isWhite = move.isWhite();
        long[] currentBoardMod = isWhite ? whitePieces : blackPieces;
        long[] enemyBoardMod = isWhite ? blackPieces : whitePieces;
        int newX = move.getNewX();
        int newY = move.getNewY();
        int oldX = move.getOldX();
        int oldY = move.getOldY();

        int peiceType = move.getBoardIndex();

        // general stuff to do wether its a custom move or not
        boolean isEating = move.isEating();
        int eatingIndex = move.getEatingIndex();



        if(!move.isEnPassant()){
            if(isEating){
                // eating enemyPeice
                enemyBoardMod[eatingIndex] = GeneralChessFunctions.RemovePeice(newX,newY,enemyBoardMod[eatingIndex]);

                // check remove rook right if rook is eaten
                if(eatingIndex == ChessConstants.ROOKINDEX){
                    gameState.checkRemoveRookMoveRight(newX, newY);
                }

            }
            if(peiceType == ChessConstants.KINGINDEX){
                // update king location + remove castling right
                board.setKingLocation(isWhite,new XYcoord(newX,newY));
                gameState.removeCastlingRight(isWhite);

            }
            // remove rook castling right
            else if(peiceType == ChessConstants.ROOKINDEX) {
                gameState.checkRemoveRookMoveRight(oldX, oldY, isWhite);
            }
        }
        else{
            // en passant
            int backwardsDir = isWhite ? 1 : -1;
            // remove pawn
            enemyBoardMod[ChessConstants.PAWNINDEX] = GeneralChessFunctions.RemovePeice(newX,newY+backwardsDir,enemyBoardMod[ChessConstants.PAWNINDEX]);


        }


//         normal move
        if(move.isCastleMove()){
            // check if short or long castle and move appropiately
            gameState.removeCastlingRight(isWhite);
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

        if(move.isPawnPromo()){
            // promo with new peice at new location
            int promoIndex = move.getPromoIndx();
            currentBoardMod[promoIndex] = GeneralChessFunctions.AddPeice(newX,newY,currentBoardMod[promoIndex]);
        }
        else{
            // move to new place as usual
            currentBoardMod[peiceType] = GeneralChessFunctions.AddPeice(newX,newY,currentBoardMod[peiceType]);
        }

        movesThatCreated.push(this.getMoveThatCreatedThis());
        super.setMoveThatCreatedThis(move);
        isDraw = gameState.makeNewMoveAndCheckDraw(this,true,false);



    }


    public void undoLocalPositionMove(ChessMove move){
        gameState.moveBackward(this);
        gameState.clearIndexes(gameState.getCurrentIndex());
        // reverse everything
        long[] whitePieces = board.getWhitePieces();
        long[] blackPieces = board.getBlackPieces();
        boolean isWhite = move.isWhite();
        long[] currentBoardMod = isWhite ? whitePieces : blackPieces;
        long[] enemyBoardMod = isWhite ? blackPieces : whitePieces;
        int newX = move.getNewX();
        int newY = move.getNewY();
        int oldX = move.getOldX();
        int oldY = move.getOldY();

        int peiceType = move.getBoardIndex();

        // general stuff to do wether its a custom move or not
        boolean isEating = move.isEating();
        int eatingIndex = move.getEatingIndex();



        if(!move.isEnPassant()){
            if(isEating){
                // reverse eating enemyPeice
                enemyBoardMod[eatingIndex] = GeneralChessFunctions.AddPeice(newX,newY,enemyBoardMod[eatingIndex]);


            }
            if(peiceType == ChessConstants.KINGINDEX){
                // update king location to old pos
                board.setKingLocation(isWhite,new XYcoord(oldX,oldY));

            }

        }
        else{
            //undo en passant
            int backwardsDir = isWhite ? 1 : -1;
            // remove pawn
            enemyBoardMod[ChessConstants.PAWNINDEX] = GeneralChessFunctions.AddPeice(newX,newY+backwardsDir,enemyBoardMod[ChessConstants.PAWNINDEX]);


        }


//         normal move
        if(move.isCastleMove()){
            // check if short or long castle and undo move appropiately
            boolean isShortCastle = newX == 6;
            if(isShortCastle){
                currentBoardMod[ChessConstants.ROOKINDEX] = GeneralChessFunctions.AddPeice(7,newY,currentBoardMod[ChessConstants.ROOKINDEX]);
                currentBoardMod[ChessConstants.ROOKINDEX] = GeneralChessFunctions.RemovePeice(newX-1,newY,currentBoardMod[ChessConstants.ROOKINDEX]);
            }
            else{
                currentBoardMod[ChessConstants.ROOKINDEX] = GeneralChessFunctions.AddPeice(0,newY,currentBoardMod[ChessConstants.ROOKINDEX]);
                currentBoardMod[ChessConstants.ROOKINDEX] = GeneralChessFunctions.RemovePeice(newX+1,newY,currentBoardMod[ChessConstants.ROOKINDEX]);
            }
        }



        // add peice at old spot
        currentBoardMod[peiceType] = GeneralChessFunctions.AddPeice(oldX,oldY,currentBoardMod[peiceType]);

        if(move.isPawnPromo()){
            // remove promo with at new location
            int promoIndex = move.getPromoIndx();
            currentBoardMod[promoIndex] = GeneralChessFunctions.RemovePeice(newX,newY,currentBoardMod[promoIndex]);
        }
        else{
            // remove new place
            currentBoardMod[peiceType] = GeneralChessFunctions.RemovePeice(newX,newY,currentBoardMod[peiceType]);
        }
        setMoveThatCreatedThis(movesThatCreated.pop());
        isDraw = false;
    }






    public boolean isDraw() {
//        if(isDrawRepetition){
//            System.out.println("Draw position");
//        }
        return isDraw;
    }


}
