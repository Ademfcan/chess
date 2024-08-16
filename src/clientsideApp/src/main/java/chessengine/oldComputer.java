//package chessengine;
//
//import java.util.List;
//
//public class oldComputer extends Computer{
//    public oldComputer(int evalDepth) {
//        super(evalDepth);
//    }
//
//    @Override
//    protected MinimaxOutput miniMax(BackendChessPosition position, ChessStates gameState, int depth, double alpha, double beta, boolean isWhiteTurn){
//        // all recursive stop cases
////        System.out.println(cnt++);
//        if(stop.get()){
//            return Stopped;
//        }
////        int key = Objects.hash(gameState.hashCode(),position.hashCode(),isWhiteTurn);
//        long key = hasher.computeHash(position,isWhiteTurn);
//        if(position.isDraw()){
//            return new MinimaxOutput(isWhiteTurn ? drawConst : -drawConst);
//        }
//        if(AdvancedChessFunctions.isAnyNotMovePossible(true,position,gameState)){
//            // possiblity of a black winning from checkmate, else draw
//            if(AdvancedChessFunctions.isChecked(true,position.board)){
//                return new MinimaxOutput(ChessConstants.BLACKCHECKMATEVALUE);
//            }
//            return new MinimaxOutput(isWhiteTurn ? drawConst : -drawConst);
//        }
//        if(AdvancedChessFunctions.isAnyNotMovePossible(false,position,gameState)){
//            // possiblity of a white winning from checkmate, else draw
//            if(AdvancedChessFunctions.isChecked(false,position.board)){
//                return new MinimaxOutput(ChessConstants.WHITECHECKMATEVALUE);
//            }
//            return new MinimaxOutput(isWhiteTurn ? drawConst : -drawConst);
//        }
//
//        if(depth == 0){
//            return new MinimaxOutput(getFullEval(position,gameState,isWhiteTurn,true));
//        }
////        if(transTable.containsKey(key)){
//////            logger.info("Transtable value being used");
////            return new MinimaxOutput(transTable.get(key));
////        }
//
//        if(depth <= evalDepth-depthThreshold){
//            // do a check to see if there is any noticeable advantage diff.  If not then return
//            double posEval = getFullEval(position,gameState,isWhiteTurn,true);
//            double diff = posEval-callTimeEval;
//            double advtgThresholdCalc = advtgThreshold + (double) (evalDepth-depthThreshold - depth) /8;
//            if(isWhiteTurn){
//                // only stay if diff greater than advtgThreshold
//                if(diff < advtgThresholdCalc){
//                    // not worth it to go deeper
////                    System.out.println("Failed thresh: " + depth);
//                    return new MinimaxOutput(posEval);
//                }
//                // else go deeper ;)
//
//            }
//            else{
//                // only stay if diff less than advtgThreshold
//                if(diff > advtgThresholdCalc){
//                    // not worth it to go deeper
////                    System.out.println("failed thresh: "  + depth);
//                    return new MinimaxOutput(posEval);
//                }
//                // else go deeper ;)
//            }
//
//        }
//
//
//        if(stop.get()){
//            return Stopped;
//        }
//        if(isWhiteTurn){
//            MinimaxOutput maxEval = new MinimaxOutput(Double.NEGATIVE_INFINITY);
//            List<BackendChessPosition> childPositions = position.getAllChildPositions(true,gameState);
//            for(BackendChessPosition p : childPositions){
//                MinimaxOutput out = miniMax(p,p.gameState, depth - 1, alpha, beta, false);
//                if(out == Stopped){
//                    return Stopped;
//                }
//                maxEval = max(out, maxEval);
//                alpha = Math.max(alpha, out.getAdvantage());  // Update alpha after the recursive call
//                if(beta <= alpha){
//                    break;
//                }
//            }
//            transTable.put(key,maxEval.getAdvantage());
//            return maxEval.incrementAndReturn();
//        }
//        else{
//            MinimaxOutput minEval = new MinimaxOutput(Double.POSITIVE_INFINITY);
//            List<BackendChessPosition> childPositions = position.getAllChildPositions(false,gameState);
//            for(BackendChessPosition p : childPositions){
//
////                assertTrue(position.gameState.toString(),childPositions.get(i).gameState);
//                MinimaxOutput out = miniMax(p,p.gameState, depth - 1, alpha, beta, true);
//                if(out == Stopped){
//                    return Stopped;
//                }
//
//                minEval = min(out,minEval);
//                beta = Math.min(beta, out.getAdvantage());  // Update beta after the recursive call
//                if(beta <= alpha){
//                    break;
//                }
//
//            }
//            transTable.put(key,minEval.getAdvantage());
//            return minEval.incrementAndReturn();
//    }
//    }
//}
