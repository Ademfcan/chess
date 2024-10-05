package chessengine.ChessRepresentations;

import chessengine.Functions.GeneralChessFunctions;
import chessengine.Functions.ZobristHasher;
import chessengine.Misc.ChessConstants;

import java.util.Stack;

public class BackendChessPosition extends ChessPosition {

    private final Stack<ChessMove> movesThatCreated;
    public boolean isWhiteTurn;

    public ChessStates gameState;

    public long zobristKey;
    private final ZobristHasher zobristHasher = new ZobristHasher();
    private boolean isDraw;

    public BackendChessPosition(BackendChessPosition pos, ChessMove newMove) {
        super(pos.addMovesThatCreatedHack(pos, pos.movesThatCreated), pos.gameState, newMove);
        this.isWhiteTurn = pos.isWhiteTurn;
        this.gameState = pos.gameState;
        this.isDraw = gameState.makeNewMoveAndCheckDraw(this);
        this.movesThatCreated = cloneStack(pos.movesThatCreated);
        // remove the saved entry now
        pos.movesThatCreated.pop();
        zobristKey = zobristHasher.computeHash(this.board);

    }

    public BackendChessPosition(ChessPosition pos, ChessStates gameState, boolean isDraw, boolean isWhiteTurn) {
        super(pos.board, pos.getMoveThatCreatedThis());
        this.gameState = gameState;
        this.isDraw = isDraw;
        this.movesThatCreated = new Stack<>();
        this.isWhiteTurn = isWhiteTurn;

    }


    public BackendChessPosition(ChessPosition pos, ChessStates gameState, boolean isWhiteTurn, int peiceType, boolean isWhite, boolean isCastle, boolean isEating, int eatingIndex, boolean isEnPassant, boolean isPawnPromo, int oldX, int oldY, int newX, int newY, int promoIndex) {
        super(pos, gameState, peiceType, isWhite, isCastle, isEating, eatingIndex, isEnPassant, isPawnPromo, oldX, oldY, newX, newY, promoIndex, false);
        this.gameState = gameState;
        isDraw = this.gameState.makeNewMoveAndCheckDraw(this);
        this.movesThatCreated = new Stack<>();
        this.isWhiteTurn = isWhiteTurn;
    }

    private Stack<ChessMove> cloneStack(Stack<ChessMove> moves) {
        Stack<ChessMove> newStack = new Stack<>();

        for (ChessMove m : moves) {
            newStack.push(m);
        }
        return newStack;
    }

    @Override
    public BackendChessPosition clonePosition() {
        return new BackendChessPosition(super.clonePosition(), gameState.cloneState(), isDraw, isWhiteTurn);
    }

    private ChessPosition addMovesThatCreatedHack(ChessPosition pos, Stack<ChessMove> movesThatCreated) {
        movesThatCreated.push(pos.getMoveThatCreatedThis());
        return pos;
    }

    public void makeLocalPositionMove(ChessMove move) {
        long[] whitePieces = board.getWhitePiecesBB();
        long[] blackPieces = board.getBlackPiecesBB();
        boolean isWhite = move.isWhite();
        long[] currentBoardMod = isWhite ? whitePieces : blackPieces;
        long[] enemyBoardMod = isWhite ? blackPieces : whitePieces;
        int newX = move.getNewX();
        int newY = move.getNewY();
        int oldX = move.getOldX();
        int oldY = move.getOldY();
        int newBitIndex = GeneralChessFunctions.positionToBitIndex(newX, newY);
        int oldBitIndex = GeneralChessFunctions.positionToBitIndex(oldX, oldY);
        boolean enemyColor = !move.isWhite();
        boolean friendlyColor = move.isWhite();
        int peiceType = move.getBoardIndex();

        // general stuff to do wether its a custom move or not
        boolean isEating = move.isEating();
        int eatingIndex = move.getEatingIndex();


        if (!move.isEnPassant()) {
            if (isEating) {
                // eating enemyPeice
                if (!GeneralChessFunctions.checkIfContains(newX, newY, enemyBoardMod[eatingIndex])) {
                    ChessConstants.mainLogger.error("Eating with no piece there!!");
                    GeneralChessFunctions.printBoardDetailed(board);
                    System.out.println(move);
                    System.out.println(gameState);
                }
                board.removePiece(newBitIndex, eatingIndex, enemyColor);

                // check remove rook right if rook is eaten
                if (eatingIndex == ChessConstants.ROOKINDEX) {
                    gameState.checkRemoveRookMoveRight(newX, newY, !isWhite);
                }

            }
            if (peiceType == ChessConstants.KINGINDEX) {
                // update king location + remove castling right
                board.setKingLocation(isWhite, new XYcoord(newX, newY));
                gameState.removeCastlingRight(isWhite);

            }
            // remove rook castling right
            else if (peiceType == ChessConstants.ROOKINDEX) {
                gameState.checkRemoveRookMoveRight(oldX, oldY, isWhite);
            }
        } else {
            // en passant
            int backwardsDir = isWhite ? 1 : -1;
            if (!GeneralChessFunctions.checkIfContains(newX, newY + backwardsDir, enemyBoardMod[ChessConstants.PAWNINDEX])) {
                ChessConstants.mainLogger.error("En passant when no piece behind!!");
                GeneralChessFunctions.printBoardDetailed(board);
                System.out.println(move);
                System.out.println(gameState);
            }
            // remove pawn
            board.removePiece(GeneralChessFunctions.positionToBitIndex(newX, newY + backwardsDir), ChessConstants.PAWNINDEX, enemyColor);


        }


//         normal move
        if (move.isCastleMove()) {
            // check if short or long castle and move appropiately
            boolean isShortCastle = newX == 6;
            if (isShortCastle) {
                if (!GeneralChessFunctions.checkIfContains(7, newY, currentBoardMod[ChessConstants.ROOKINDEX])) {
                    ChessConstants.mainLogger.error("New chess position trying to castle when not possible!!!");
                    GeneralChessFunctions.printBoardDetailed(board);
                    System.out.println(move);
                    System.out.println(gameState);
                }

                board.removePiece(GeneralChessFunctions.positionToBitIndex(7, newY), ChessConstants.ROOKINDEX, friendlyColor);
                board.addPiece(GeneralChessFunctions.positionToBitIndex(newX - 1, newY), ChessConstants.ROOKINDEX, friendlyColor);
            } else {
                if (!GeneralChessFunctions.checkIfContains(0, newY, currentBoardMod[ChessConstants.ROOKINDEX])) {
                    ChessConstants.mainLogger.error("New chess position trying to castle when not possible!!!");
                    GeneralChessFunctions.printBoardDetailed(board);
                    System.out.println(move);
                    System.out.println(gameState);
                }
                board.removePiece(GeneralChessFunctions.positionToBitIndex(0, newY), ChessConstants.ROOKINDEX, friendlyColor);
                board.addPiece(GeneralChessFunctions.positionToBitIndex(newX + 1, newY), ChessConstants.ROOKINDEX, friendlyColor);
            }
            gameState.removeCastlingRight(isWhite);
        }


        // remove peice at old spot
        board.removePiece(oldBitIndex, peiceType, friendlyColor);

        if (move.isPawnPromo()) {
            // promo with new peice at new location
            int promoIndex = move.getPromoIndx();
            board.addPiece(newBitIndex, promoIndex, friendlyColor);
//            board.updateAttackMask(promoIndex,friendlyColor);
        } else {
            // move to new place as usual
            board.addPiece(newBitIndex, peiceType, friendlyColor);
        }
//        board.updateAttackMask(peiceType,friendlyColor);
//        if(move.isCastleMove()){
//            board.updateAttackMask(ChessConstants.ROOKINDEX,friendlyColor);
//        }
//        if(move.isEating()){
//            board.updateAttackMask(eatingIndex,enemyColor);
//        }
//        if(move.isEnPassant()){
//            board.updateAttackMask(ChessConstants.PAWNINDEX,enemyColor);
//
//        }
        board.updateAttackMasks();

        movesThatCreated.push(this.getMoveThatCreatedThis());
        super.setMoveThatCreatedThis(move);
        zobristKey = zobristHasher.computeHash(this.board);


        isDraw = gameState.makeNewMoveAndCheckDraw(this);
        isWhiteTurn = !isWhiteTurn;
    }


    public void undoLocalPositionMove() {

        gameState.moveBackward(this);
        gameState.clearIndexes(gameState.getCurrentIndex());
        ChessMove move = super.getMoveThatCreatedThis();


        // reverse everything
        boolean isWhite = move.isWhite();
        int newX = move.getNewX();
        int newY = move.getNewY();
        int oldX = move.getOldX();
        int oldY = move.getOldY();
        int newBitIndex = GeneralChessFunctions.positionToBitIndex(newX, newY);
        int oldBitIndex = GeneralChessFunctions.positionToBitIndex(oldX, oldY);
        boolean enemyColor = !isWhite;
        boolean friendlyColor = isWhite;
        int peiceType = move.getBoardIndex();

        // general stuff to do wether its a custom move or not
        boolean isEating = move.isEating();
        int eatingIndex = move.getEatingIndex();


        if (!move.isEnPassant()) {
            if (isEating) {
                // reverse eating enemyPeice
                board.addPiece(newBitIndex, eatingIndex, enemyColor);


            }
            if (peiceType == ChessConstants.KINGINDEX) {
                // update king location to old pos
                board.setKingLocation(isWhite, new XYcoord(oldX, oldY));

            }

        } else {
            //undo en passant
            int backwardsDir = isWhite ? 1 : -1;
            // remove pawn
            board.addPiece(GeneralChessFunctions.positionToBitIndex(newX, newY + backwardsDir), ChessConstants.PAWNINDEX, enemyColor);


        }


//         castle move
        if (move.isCastleMove()) {
            // check if short or long castle and undo move appropiately
//            System.out.println("Undoing castling");
//            System.out.println("CurMove: " + move.toString());
//            System.out.println("OldMove: " + movesThatCreated.peek().toString());
            boolean isShortCastle = newX == 6;
            if (isShortCastle) {
                board.addPiece(GeneralChessFunctions.positionToBitIndex(7, newY), ChessConstants.ROOKINDEX, friendlyColor);
                board.removePiece(GeneralChessFunctions.positionToBitIndex(newX - 1, newY), ChessConstants.ROOKINDEX, friendlyColor);
            } else {
                board.addPiece(GeneralChessFunctions.positionToBitIndex(0, newY), ChessConstants.ROOKINDEX, friendlyColor);
                board.removePiece(GeneralChessFunctions.positionToBitIndex(newX + 1, newY), ChessConstants.ROOKINDEX, friendlyColor);
            }
        }

        // add peice at old spot
        board.addPiece(oldBitIndex, peiceType, friendlyColor);

        if (move.isPawnPromo()) {
            // remove promo with at new location
            int promoIndex = move.getPromoIndx();
            board.removePiece(newBitIndex, promoIndex, friendlyColor);
//            board.updateAttackMask(promoIndex,friendlyColor);
        } else {
            // remove new place
            board.removePiece(newBitIndex, peiceType, friendlyColor);
        }
//        board.updateAttackMask(peiceType,friendlyColor);
//        if(move.isCastleMove()){
//            board.updateAttackMask(ChessConstants.ROOKINDEX,friendlyColor);
//        }
//        if(move.isEating()){
//            board.updateAttackMask(eatingIndex,enemyColor);
//        }
//        if(move.isEnPassant()){
//            board.updateAttackMask(ChessConstants.PAWNINDEX,enemyColor);
//        }
        board.updateAttackMasks();
        setMoveThatCreatedThis(movesThatCreated.pop());
        isDraw = false;
        zobristKey = zobristHasher.computeHash(this.board);
        isWhiteTurn = !isWhiteTurn;
    }


    public boolean isDraw() {
//        if(isDrawRepetition){
//            System.out.println("Draw position");
//        }
        return isDraw;
    }


}
