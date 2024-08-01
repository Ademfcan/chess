//package chessengine;
//
//import java.util.List;
//
//public class ChessboardMoveMaker {
//    private final ChessBoardGUIHandler guiHandler;
//
//    public ChessboardMoveMaker(ChessBoardGUIHandler guiHandler){
//        this.guiHandler = guiHandler;
//    }
//
//    /** This method tried to animate a chess move. There are two types of moves reverse moves and normal moves. If its a reverse move, you must also provide the chessmove as a reversed move(call the method) **/
//    public void makeAnimatedMove(ChessMove move, boolean isReverse, ChessPosition currentPosition, ChessPosition newPos){
////        GeneralChessFunctions.printBoardDetailed(newPos.board);
//        if (move.isEating() && !isReverse) {
//            // needs to be before move
//            int eatenAddIndex = GeneralChessFunctions.getBoardWithPiece(move.getNewX(), move.getNewY(), !move.isWhite(), currentPosition.board);
//            guiHandler.updateEatenPieces(eatenAddIndex, !move.isWhite());
//            guiHandler.removeFromChessBoard(move.getNewX(), move.getNewY(), !move.isWhite(),);
//        }
//        if(move.isEnPassant()){
//            if(!isReverse){
//                int backDir = move.isWhite() ? 1: -1;
//                int eatY = move.getNewY()+backDir;
//                int eatenAddIndex = GeneralChessFunctions.getBoardWithPiece(move.getNewX(), eatY, !move.isWhite(), currentPosition.board);
//                guiHandler.updateEatenPieces(eatenAddIndex, !move.isWhite());
//                guiHandler.removeFromChessBoard(move.getNewX(), eatY, !move.isWhite());
//            }
//            else{
//                int backDir = move.isWhite() ? 1: -1;
//                int eatY = move.getOldY()+backDir;
//                int eatenAddIndex = GeneralChessFunctions.getBoardWithPiece(move.getOldX(), eatY, !move.isWhite(), newPos.board);
//                guiHandler.removeFromEatenPeices(Integer.toString(eatenAddIndex), !move.isWhite());
//                guiHandler.addToChessBoard(move.getOldX(), eatY,eatenAddIndex, !move.isWhite());
//            }
//
//        }
//        if (move.isCastleMove()) {
//            // shortcastle is +x dir longcastle = -2x dir
//            if (isReverse) {
//                int dirFrom = move.getOldX() == 6 ? 1 : -2;
//                int dirTo = move.getOldX() == 6 ? -1 : 1;
//                // uncastle
//                guiHandler.movePieceOnBoard(move.getOldX() + dirTo, move.getOldY(), move.getOldX() + dirFrom, move.getNewY(), move.isWhite());
//            } else {
//                int dirFrom = move.getNewX() == 6 ? 1 : -2;
//                int dirTo = move.getNewX() == 6 ? -1 : 1;
//                guiHandler.movePieceOnBoard(move.getNewX() + dirFrom, move.getOldY(), move.getNewX() + dirTo, move.getNewY(), move.isWhite());
//            }
//
//        }
//        // this is where the piece actually moves
//        if (!move.isPawnPromo()) {
//            // in pawn promo we need to handle differently as the piece changes
//            guiHandler.movePieceOnBoard(move.getOldX(), move.getOldY(), move.getNewX(), move.getNewY(), move.isWhite());
//
//        }
//        // move
//        else {
//            if (isReverse) {
//                guiHandler.removeFromChessBoard(move.getOldX(), move.getOldY(), move.isWhite());
//                guiHandler.moveNewPieceOnBoard(move.getOldX(), move.getOldY(), move.getNewX(), move.getNewY(), ChessConstants.PAWNINDEX, move.isWhite());
//
//            } else {
//                guiHandler.removeFromChessBoard(move.getOldX(), move.getOldY(), move.isWhite());
//                guiHandler.moveNewPieceOnBoard(move.getOldX(), move.getOldY(), move.getNewX(), move.getNewY(), move.getPromoIndx(), move.isWhite());
//
//
//            }
//        }
//        if (move.isEating() && isReverse) {
//            // need to create a piece there to undo eating
//            // must be after moving
//            int pieceIndex = GeneralChessFunctions.getBoardWithPiece(move.getOldX(), move.getOldY(), !move.isWhite(), newPos.board);
//            guiHandler.addToChessBoard(move.getOldX(), move.getOldY(), pieceIndex, !move.isWhite());
//            guiHandler.removeFromEatenPeices(Integer.toString(pieceIndex), !move.isWhite());
//
//        }
//        if (!isReverse) {
//            guiHandler.highlightMove(move);
//        }
//
//
//
//    }
//
//    /** This method updates the chessboard from one position to another. NOTES: the board must already be at current position, and this will NOT animate**/
//    public void updateChessBoardGui(ChessPosition newPos, ChessPosition currentPos){
////        GeneralChessFunctions.printBoardDetailed(newPos.board);
//            List<String>[] changes = AdvancedChessFunctions.getChangesNeeded(currentPos.board,newPos.board);
//            List<String> thingsToAdd = changes[0];
//            List<String> thingsToRemove = changes[1];
//
//
//            // all of this is to update the pieces on the gui
//
//            int i = 0;
//            int z = 0;
//
//            while(z < thingsToRemove.size()){
//                // edge case where you need to remove more to the board
//                String[] Delinfo = thingsToRemove.get(z).split(",");
//                int OldX = Integer.parseInt(Delinfo[0]);
//                int OldY = Integer.parseInt(Delinfo[1]);
//                boolean isWhite = Delinfo[2].equals("w");
//                int brdRmvIndex = Integer.parseInt(Delinfo[3]);
//                guiHandler.removeFromChessBoard(OldX,OldY,isWhite);
//                guiHandler.updateEatenPieces(brdRmvIndex,isWhite);
//
//
//                z++;
//
//            }
//            while(i < thingsToAdd.size()){
//                // edge case where you need to add more to the board
//                String[] Moveinfo = thingsToAdd.get(i).split(",");
//                int NewX = Integer.parseInt(Moveinfo[0]);
//                int NewY = Integer.parseInt(Moveinfo[1]);
//                int brdAddIndex = Integer.parseInt(Moveinfo[3]);
//                boolean isWhite = Moveinfo[2].equals("w");
//                guiHandler.addToChessBoard(NewX,NewY,brdAddIndex,isWhite);
//                guiHandler.removeFromEatenPeices(Moveinfo[3],isWhite);
//
//
//
//                i++;
//
//
//            }
//
//
//
//    }
//
//    public void handleChangingPositions(ChessPosition newPosition,ChessPosition currentPosition,int dir){
//
//    }
//
//
//}
