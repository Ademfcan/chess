//protected MinimaxEvalOutput miniMax(BackendChessPosition position, int depth, double alpha, double beta, boolean isWhiteTurn) {
//        // all recursive stop cases
//        if (stop.get()) {
//        logger.info("Stopping Minimax due to flag");
//        return Stopped;
//        }
//        if (position.isDraw()) {
//        return new MinimaxEvalOutput(isWhiteTurn ? currentDifficulty.drawConst : -currentDifficulty.drawConst);
//        }
//        boolean isAnyMoveNotPossible = AdvancedChessFunctions.isAnyNotMovePossible(isWhiteTurn, position, position.gameState);
//        boolean isChecked = AdvancedChessFunctions.isChecked(isWhiteTurn, position.board);
//        if (isAnyMoveNotPossible) {
//        // possiblity of a black winning from checkmate, else draw
//        if (isChecked) {
//        return new MinimaxEvalOutput(isWhiteTurn ? ChessConstants.BLACKCHECKMATEVALUE : ChessConstants.WHITECHECKMATEVALUE);
//        }
//        return new MinimaxEvalOutput(isWhiteTurn ? currentDifficulty.drawConst : -currentDifficulty.drawConst);
//        }
////
//        depth+= ComputerHelperFunctions.calculateMoveExtension(position,isWhiteTurn,isChecked);
//        if (depth == 0) {
//        // first check move extensions
//        return new MinimaxEvalOutput(ComputerHelperFunctions.getFullEval(position, position.gameState, isWhiteTurn, true));
//        }
//
//        long key = hasher.computeHash(position.board, isWhiteTurn);
//        // flip board for inverse position check
//        position.board.flipBoard();
//        long flippedKey = hasher.computeHash(position.board, !isWhiteTurn);
//        position.board.flipBoard(); // flip back
//
//
////        // todo fix transtable erratic behaviour
//        if (transTable.containsKey(key)) {
//        return new MinimaxEvalOutput(transTable.get(key));
//        }
//        if (transTable.containsKey(flippedKey)) {
//        return new MinimaxEvalOutput(-transTable.get(flippedKey));
//        }
//
//        if (depth <= evalDepth - currentDifficulty.depthThreshold) {
//        // do a check to see if there is any noticeable advantage diff.  If not then return
//        double posEval = ComputerHelperFunctions.getFullEval(position, position.gameState, isWhiteTurn, true);
//        double diff = posEval - callTimeEval;
//        double advtgThresholdCalc = currentDifficulty.advantageThreshold + (double) (evalDepth - currentDifficulty.depthThreshold - depth) / 8;
//        if (isWhiteTurn) {
//        // only stay if diff greater than advtgThreshold
//        if (diff < advtgThresholdCalc) {
//        // not worth it to go deeper
//        return new MinimaxEvalOutput(posEval);
//        }
//        // else go deeper ;)
//
//        } else {
//        // only stay if diff less than advtgThreshold
//        if (diff > -advtgThresholdCalc) {
//        // not worth it to go deeper
//        return new MinimaxEvalOutput(posEval);
//        }
//        // else go deeper ;)
//        }
//
//        }
//
//        // once again  check stop flag
//        if (stop.get()) {
//        logger.info("Stopping Minimax due to flag");
//        return Stopped;
//        }
////        String og = GeneralChessFunctions.getBoardDetailedString(position.board);
////        String ogMove = position.getMoveThatCreatedThis().toString();
//
//        // recursive part
//        if (isWhiteTurn) {
//        MinimaxEvalOutput maxEval = new MinimaxEvalOutput(Double.NEGATIVE_INFINITY);
//        List<ChessMove> childMoves = position.getAllChildMoves(true, position.gameState);
//        if(childMoves == null){
//        logger.error("Childmoves error!");
//        return Stopped;
//        }
////            List<BackendChessPosition> childPositions = position.getAllChildPositions(true,position.gameState);
////            assertTrue(childMoves.size(),childPositions.size(),true);
//        for (int i = 0; i < childMoves.size(); i++) {
//        ChessMove c = childMoves.get(i);
//        position.makeLocalPositionMove(c);
////                if(!assertTrue(position,childPositions.get(i),true,og + "\n" + ogMove)){
////                    System.out.println("Expected index: " + (App.ChessCentralControl.gameHandler.currentGame.curMoveIndex + (currentDifficulty.depth-depth)+1));
////                    assertTrue(position.gameState.toString().trim(),childPositions.get(i).gameState.toString().trim(),true,"Before");
////                    System.out.println("whywhywhywhywhywhywhy");
////                    return Stopped;
//
//
////                }
////                assertTrue(position.getMoveThatCreatedThis(),childPositions.get(i).getMoveThatCreatedThis(),true);
////                assertTrue(position.gameState.toString().trim(),childPositions.get(i).gameState.toString(),true,"Before");
//        MinimaxEvalOutput out = miniMax(position, depth - 1, alpha, beta, false);
//        position.undoLocalPositionMove();
//        if (out == Stopped) {
////                    System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA:    " + depth);
////                    System.out.println(c.toString());
////                    String outStr = GeneralChessFunctions.getBoardDetailedString(position.board);
////                    System.out.println(outStr);
//        return Stopped;
//        }
//        maxEval = max(out, maxEval);
//        alpha = Math.max(alpha, out.getAdvantage());  // Update alpha after the recursive call
//        if (beta <= alpha) {
//        break;
//        }
//
//        }
//        if(depth == evalDepth-1){
//        transTable.put(key,maxEval.getAdvantage());
//        }
//        return maxEval.incrementAndReturn();
//        } else {
//        MinimaxEvalOutput minEval = new MinimaxEvalOutput(Double.POSITIVE_INFINITY);
//        List<ChessMove> childMoves = position.getAllChildMoves(false, position.gameState);
//        if(childMoves == null){
//        logger.error("Childmoves error!");
//        return Stopped;
//        }
////           List<BackendChessPosition> childPositions = position.getAllChildPositions(false,position.gameState);
////            assertTrue(childMoves.size(),childPositions.size(),true);
//        for (int i = 0; i < childMoves.size(); i++) {
//        ChessMove c = childMoves.get(i);
//        position.makeLocalPositionMove(c);
////                if(!assertTrue(position,childPositions.get(i),false,og + "\n" + ogMove)){
////                    System.out.println("Expected index: " + (App.ChessCentralControl.gameHandler.currentGame.curMoveIndex + (currentDifficulty.depth-depth)+1));
////                    assertTrue(position.gameState.toString().trim(),childPositions.get(i).gameState.toString().trim(),true,"Before");
////                    System.out.println("whywhywhywhywhywhywhy");
////                    return Stopped;
////                }
//
////                assertTrue(position.getMoveThatCreatedThis(),childPositions.get(i).getMoveThatCreatedThis(),true);
//        MinimaxEvalOutput out = miniMax(position, depth - 1, alpha, beta, true);
//        position.undoLocalPositionMove();
//        if (out == Stopped) {
////                    System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA:  " + depth);
////                    System.out.println(c.toString());
////                    String outStr = GeneralChessFunctions.getBoardDetailedString(position.board);
////                    System.out.println(outStr);
//        return Stopped;
//
//        }
//
//        minEval = min(out, minEval);
//        beta = Math.min(beta, out.getAdvantage());  // Update beta after the recursive call
//        if (beta <= alpha) {
//        break;
//        }
//
//        }
//        if(depth == evalDepth-1){
//        transTable.put(key,minEval.getAdvantage());
//        }
//
//        return minEval.incrementAndReturn();
//        }
//
//
//        }


//private <T> void assertTrue(T a, T b, boolean isSerious, String s) {
//        if (isSerious && !a.equals(b)) {
//            int n = randomComp.nextInt(0, 100);
//            System.out.println(s + "\nERRORRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR!!!!\na" + n + "\n" + a + "\nb" + n + "\n" + b);
//        }
//
//    }
//
//    private boolean assertTrue(ChessPosition a, ChessPosition b, boolean isWhite, String og) {
//        String aboard = GeneralChessFunctions.getBoardDetailedString(a.board);
//        String bboard = GeneralChessFunctions.getBoardDetailedString(b.board);
//        if (!aboard.equals(bboard) && !a.getMoveThatCreatedThis().isCastleMove()) {
//            System.out.println("OG:\n" + og);
//            System.out.println("IsWhite: " + isWhite);
//            System.out.println("amove\n" + a.getMoveThatCreatedThis().toString());
//            System.out.println("a\n" + aboard);
//            System.out.println("bmove\n" + b.getMoveThatCreatedThis().toString());
//            System.out.println("b\n" + bboard);
//            System.out.println("ERRORRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR!!!!");
//            return false;
//        }
//        return true;
//
//    }