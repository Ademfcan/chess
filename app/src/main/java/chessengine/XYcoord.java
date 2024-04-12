package chessengine;

public class XYcoord {
    int x;
    int y;

    public boolean isCastleMove() {
        return isCastleMove;
    }

    public boolean isPawnPromo() {
        return isPawnPromo;
    }

    public void setPawnPromo(boolean pawnPromo) {
        isPawnPromo = pawnPromo;
    }

    private boolean isPawnPromo;
    private boolean isCastleMove;
    int direction = -10;

    int peiceType = -10;


    public XYcoord(int x, int y, int direction) {
        this.x = x;
        this.y = y;
        this.isCastleMove = false;
        this.direction = direction;
    }


    public XYcoord(int x, int y, boolean isCastleMove) {
        this.x = x;
        this.y = y;
        this.isCastleMove = isCastleMove;
    }



    public XYcoord(int x, int y) {
        this.x = x;
        this.y = y;
        this.isCastleMove = false;
    }
    @Override
    public boolean equals(Object obj){
        if(this == obj){
            return true;
        }
        if(obj == null || obj.getClass() != getClass() ){
            return false;

        }
        XYcoord xy = (XYcoord) obj;
        return (this.x == xy.x && this.y == xy.y);

    }

    public String toString(){
        return "X: " + x + " Y: " + y + " Is castle move?: " + isCastleMove;
    }

}
