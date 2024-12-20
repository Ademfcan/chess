package chessserver.Functions;

import chessserver.Functions.AdvancedChessFunctions;
import chessserver.Functions.BitFunctions;
import chessserver.Functions.GeneralChessFunctions;
import chessserver.Misc.ChessConstants;
import chessserver.Misc.pieceMapHandler;
import chessserver.ChessRepresentations.*;
import chessserver.Enums.ComputerDifficulty;

import java.util.ArrayList;
import java.util.List;

public class ComputerHelperFunctions {

    // bishop then rook
    private static final int[] bishopDx = {1, -1, -1, 1};
    private static final int[] bishopDy = {1, -1, 1, -1};
    private static final int[] rookDx = {1, -1, 0, 0};
    private static final int[] rookDy = {0, 0, -1, 1};
    private static final double squareWorth = 0.05d;

    public static boolean doesMoveFitRestrictions(BackendChessPosition newPos, ComputerDifficulty diff) {
        ChessMove moveThatCreated = newPos.getMoveThatCreatedThis();

        double agressiveness = 0;
        double defensiveness = 0;
        double risk = 0;

        Boolean isChecked = null;
        if (AdvancedChessFunctions.isAnyNotMovePossible(!moveThatCreated.isWhite(), newPos, newPos.gameState)) {
            // checkmate
            if (AdvancedChessFunctions.isChecked(!moveThatCreated.isWhite(), newPos.board)) {
                isChecked = true;
                if (!diff.canWin) {
                    // computer cannot checkmate
                    return false;
                }
            }
            // draw
            else {
                isChecked = false;
                if (diff.maxDefensiveness < .5) {
                    return false;
                }
                defensiveness += .5;
            }
        }
        if (isChecked == null) {
            isChecked = AdvancedChessFunctions.isChecked(!moveThatCreated.isWhite(), newPos.board);
        }

        agressiveness += isChecked ? .5 : 0; // check value

        double moveForwardValue = (moveThatCreated.getNewX() - moveThatCreated.getOldX()) * squareWorth;
        if (moveThatCreated.isWhite()) {
            // moving forward means moveforward value should be negative
            agressiveness += Math.max(-moveForwardValue, 0);
            defensiveness += Math.max(moveForwardValue, 0);
        } else {
            agressiveness += Math.max(moveForwardValue, 0);
            defensiveness += Math.max(-moveForwardValue, 0);
        }
        if (moveThatCreated.isEating()) {
            agressiveness += .15;
            double tradeValue = moveThatCreated.getEatingIndex() - moveThatCreated.getBoardIndex();

            risk += -Math.min(tradeValue, 0);
        }
        risk += AdvancedChessFunctions.getNumAttackers(moveThatCreated.getNewX(), moveThatCreated.getNewY(), moveThatCreated.isWhite(), newPos.board) * squareWorth;

        defensiveness += moveThatCreated.isCastleMove() ? .25 : 0;
        agressiveness += moveThatCreated.isPawnPromo() ? .2 : 0;

        // clipping
        agressiveness = Math.min(agressiveness, 1);
        defensiveness = Math.min(defensiveness, 1);
        risk = Math.min(risk, 1);

        return agressiveness <= diff.maxAgressiveness && defensiveness <= diff.maxDefensiveness && risk <= diff.maxRisk;

    }

    public static double getFullEval(ChessPosition pos, ChessGameState gameState, boolean isWhiteTurn, boolean isCheckmateKnown) {
        // todo: test against known positions
        long[] whiteP = pos.board.getWhitePiecesBB();
        long[] blackP = pos.board.getBlackPiecesBB();
        int whitePieceCount = pos.board.getWhitePieceCount();
        int blackPieceCount = pos.board.getBlackPieceCount();
        double[][][] currentMap = pieceMapHandler.getMap(whitePieceCount + blackPieceCount);

        if (!isCheckmateKnown) {
            if (AdvancedChessFunctions.isCheckmated(false, pos, gameState)) {
                return ChessConstants.WHITECHECKMATEVALUE;

            } else if (AdvancedChessFunctions.isCheckmated(true, pos, gameState)) {
                return -ChessConstants.BLACKCHECKMATEVALUE;
            }

        }

        XYcoord king1 = pos.board.getWhiteKingLocation();
        XYcoord king2 = pos.board.getBlackKingLocation();

        double sum1 = 0;
        double sum2 = 0;
        for (int i = 0; i < whiteP.length - 1; i++) {
            List<XYcoord> coordsW = getPieceCoords(whiteP[i]);
            for (XYcoord s : coordsW) {
                int Normx = s.x;
                int Normy = 7 - s.y;
                sum1 += ChessConstants.valueMap[i] + currentMap[i][Normx][Normy];
                if (i == 0) {
                    float extraPawnPushValue = (float) (16 - whitePieceCount) / 16;
                    sum1 += extraPawnPushValue;
                }
                if (i > 1) {
                    if (i == 4) {
                        sum1 += addOpenFileValue(s.x, s.y, 3, pos.board);
                        sum1 += addOpenFileValue(s.x, s.y, 2, pos.board);
                    }
                    sum1 += addOpenFileValue(s.x, s.y, i, pos.board);
                }


            }
            List<XYcoord> coordsB = getPieceCoords(blackP[i]);
            for (XYcoord s : coordsB) {
                if (i == 0) {
                    float extraPawnPushValue = (float) (16 - blackPieceCount) / 16;
                    sum2 += extraPawnPushValue;
                }
                // reverse coordinates to match white peices


                sum2 += ChessConstants.valueMap[i] + currentMap[i][s.x][s.y];
                if (i > 1) {
                    if (i == 4) {
                        sum2 += addOpenFileValue(s.x, s.y, 3, pos.board);
                        sum2 += addOpenFileValue(s.x, s.y, 2, pos.board);
                    }
                    sum2 += addOpenFileValue(s.x, s.y, i, pos.board);
                }


            }
        }
        double total = sum1 - sum2;
//        total += kingDistanceAncCornerEval(king1, king2, isWhiteTurn ? blackPieceCount : whitePieceCount, isWhiteTurn) * (isWhiteTurn ? 1 : -1);
        return total;

    }

    private static double kingDistanceAncCornerEval(XYcoord king1, XYcoord king2, int piecesOnBoard, boolean isWhite) {
        XYcoord enemyKingCoord = isWhite ? king2 : king1;
        int xDistFromCenter = Math.abs(enemyKingCoord.x - 3);
        int yDistFromCenter = Math.abs(enemyKingCoord.y - 3);
        int distFromCenterValue = 6 - (xDistFromCenter + yDistFromCenter);
        int xDist = Math.abs(king1.x - king2.x);
        int yDist = Math.abs(king1.y - king2.y);
        int distanceKingsvalue = 14 - (xDist + yDist);
        double weight = (1 - ((double) piecesOnBoard / ChessConstants.ONESIDEPIECECOUNT)) / 2;
        return (distanceKingsvalue + distFromCenterValue) * weight;
    }

    private static List<XYcoord> getPieceCoords(long board) {
        List<XYcoord> coord = new ArrayList<>();
        for (int z = 0; z < 64; z++) {
            long mask = 1L << z;

            if ((board & mask) != 0) {
                int[] coords = BitFunctions.bitindexToXY(z);
                coord.add(new XYcoord(coords[0], coords[1]));
            }
        }

        return coord;
    }


    private static double addOpenFileValue(int x, int y, int piecetype, BitBoardWrapper board) {
        double totalValue = 0d;
        int[] dxs = piecetype == 2 ? bishopDx : rookDx;
        int[] dys = piecetype == 2 ? bishopDy : rookDy;
        for (int i = 0; i < 4; i++) {
            int dx = dxs[i] + x;
            int dy = dys[i] + y;
            while (GeneralChessFunctions.isValidCoord(dx, dy)) {
                if (GeneralChessFunctions.checkIfContains(dx, dy, board, "fileVals")[0]) {
                    // hit peice
                    break;
                }
                dx += dxs[i];
                dy += dys[i];
                totalValue += squareWorth;
            }
        }
        return totalValue;
    }

    /**
     * 0,1 returned wether a move extension is needed to search deeper
     **/
    public static int calculateMoveExtension(ChessPosition pos, boolean isWhiteTurn, boolean isChecked) {
        // todo
        ChessMove moveThatCreated = pos.getMoveThatCreatedThis();
        if (isChecked) {
//            System.out.println("Extending check");
            return 1;
        }
        if (moveThatCreated.isShouldRequireExtension()) {
//            System.out.println("Extending eating");
            return 1;
        }
        double minAttacker = AdvancedChessFunctions.getMinAttacker(moveThatCreated.getNewX(), moveThatCreated.getNewY(), moveThatCreated.isWhite(), pos.board);
        if (minAttacker != ChessConstants.EMPTYINDEX && minAttacker < ChessConstants.valueMap[moveThatCreated.getBoardIndex()]) {
            return 1;
        }
        if (moveThatCreated.getBoardIndex() == ChessConstants.PAWNINDEX && AdvancedChessFunctions.isPromoPossible(moveThatCreated.getNewX(), moveThatCreated.getNewY(), moveThatCreated.isWhite(), pos.board)) {
            return 1;
        }
        return 0;
    }

    public static int getMoveValue(ChessMove m) {
        int moveValueGuess = 0;
        if (m.isEating()) {
            moveValueGuess = 10 * (m.getEatingIndex() - m.getBoardIndex());
        }
        if (m.isPawnPromo()) {
            moveValueGuess += m.getPromoIndx();
        }

        return moveValueGuess;// todo
    }

    public static boolean isNotQuiet(BitBoardWrapper board, ChessMove m) {
        return m.isEating();
    }
}
