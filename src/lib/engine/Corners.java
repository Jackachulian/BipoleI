package lib.engine;

/** Data format to store the heights of corners of a tile or around a tile. **/
public class Corners {
    public int nw, sw, se, ne;

    public Corners(int nw, int sw, int se, int ne) {
        this.nw = nw;
        this.sw = sw;
        this.se = se;
        this.ne = ne;
    }

    public Corners(Corners other){
        this.nw = other.nw;
        this.sw = other.sw;
        this.se = other.se;
        this.ne = other.ne;
    }

    public Corners() {
        nw = -1;
        sw = -1;
        se = -1;
        ne = -1;
    }
}
