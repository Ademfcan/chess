package chessengine.Enums;

import chessengine.Records.PVEntry;
import chessengine.Functions.EvaluationFunctions;
import javafx.scene.image.Image;
import javafx.scene.paint.Paint;

public enum MoveRanking {
    BESTMOVE("#248232","/MoveIcons/bestMove.png"){
        @Override
        public Image getImage() {
            return new Image(this.iconLocation);
        }

        @Override
        public Paint getColor() {
            return Paint.valueOf(this.color);
        }
    },
    BRILLIANT("#00FFF5","/MoveIcons/brilliant.png"){
        @Override
        public Image getImage() {
            return new Image(this.iconLocation);
        }

        @Override
        public Paint getColor() {
            return Paint.valueOf(this.color);
        }
    },
    GOODMOVE("#63D2FF","/MoveIcons/goodMove.png"){
        @Override
        public Image getImage() {
            return new Image(this.iconLocation);
        }

        @Override
        public Paint getColor() {
            return Paint.valueOf(this.color);
        }
    },
    OKMOVE("#2BA84A","/MoveIcons/okMove.png"){
        @Override
        public Image getImage() {
            return new Image(this.iconLocation);
        }

        @Override
        public Paint getColor() {
            return Paint.valueOf(this.color);
        }
    },
    INACCURACY("#EF7B45","/MoveIcons/inaccuracy.png"){
        @Override
        public Image getImage() {
            return new Image(this.iconLocation);
        }

        @Override
        public Paint getColor() {
            return Paint.valueOf(this.color);
        }
    },
    BLUNDER("#F71735","/MoveIcons/blunder.png"){
        @Override
        public Image getImage() {
            return new Image(this.iconLocation);
        }

        @Override
        public Paint getColor() {
            return Paint.valueOf(this.color);
        }
    };

    public abstract Image getImage();
    public abstract Paint getColor();

    String color;
    String iconLocation;
    private MoveRanking(String color,String iconLocation){
        this.color = color;
        this.iconLocation = iconLocation;
    }



    private static final int goodAdvantageThreshold = 200;
    private static final int okAdvantageThreshold = 0;

    private static final int goodDelta = 100;

    private static final int deepSearchDepth = 4;
    private static final int decentSearchDepth = 2;

    private static final int okDelta = 150;

    public static MoveRanking getMoveRanking(int bestEvaluation, int moveEvaluation, PVEntry[] bestPv, PVEntry[] movePv){
        int delta = Math.abs(moveEvaluation-bestEvaluation);
        int hasCheckmate = isPvCheckmate(bestPv); // todo look much more into pv, and also static evaluations. Ex reward static sacrifice ( need to include this in pv)

        if(delta <= goodDelta){
            // go through pv and see where you

            // check for if its a brilliant move by seeing when the pv gets a big jump in advantage. Based on that if its deep in the line its brilliant because its hard to see
            if (bestEvaluation >= goodAdvantageThreshold){
                // check main line
                for(int i = 0;i<movePv.length;i++){
                    int eval = movePv[i].pvEval() * (i % 2 == 0 ? 1 : -1);
                    if(eval >= goodAdvantageThreshold){
                        if(i >= deepSearchDepth ){
                            return MoveRanking.BRILLIANT;
                        }
                        if(i >= decentSearchDepth){
                            return MoveRanking.GOODMOVE;
                        }
                        return delta == 0 ? MoveRanking.BESTMOVE : MoveRanking.OKMOVE;
                    }
                }
            }
            return delta == 0 ? MoveRanking.BESTMOVE : MoveRanking.OKMOVE;
        }

        if(delta <= okDelta){
            // go through pv and see where you
            if(bestEvaluation >= okAdvantageThreshold){
                return MoveRanking.OKMOVE;
            }
            return MoveRanking.INACCURACY;
        }

        if(delta > 3*okDelta){
            if(bestEvaluation <= -goodAdvantageThreshold){
                return MoveRanking.BLUNDER;
            }
            return MoveRanking.INACCURACY;

        }
        return MoveRanking.INACCURACY;




    }
    // 1 black mate, 0 no mate, -1 white mate
    private static int isPvCheckmate(PVEntry[] pv){
        for(PVEntry pair : pv){
            if(EvaluationFunctions.isMateScore(pair.pvEval())){
                return pair.pvMove().isWhite() ? 1 : -1;
            }
        }
        return 0;
    }


}
