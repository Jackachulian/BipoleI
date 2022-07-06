package lib.engine;

import lib.Camera;

/** Data format to store the heights of corners of a tile or around a tile. **/
public class Corners {
    /** All four diangonal cardinal direction corners. [NW, SW, SE, NE] **/
    public int[] values;

    public Corners(int nw, int sw, int se, int ne) {
        values = new int[]{nw, sw, se, ne};
    }

    public Corners(Corners other){
        this.values = other.values;
    }

    public Corners() {
       this(-1, -1, -1, -1);
    }

    private int indexedCorner(int index) {
        return values[(index + Camera.cornerShift) % 4];
    }

    /** Return the back corner according to current rotation. (NW when facing 45 degrees.) **/
    public int back() { return indexedCorner(0); }
    /** Return the left corner according to current rotation. (NE when facing 45 degrees.) **/
    public int left() { return indexedCorner(1); }
    /** Return the back corner according to current rotation. (SE when facing 45 degrees.) **/
    public int front() { return indexedCorner(2); }
    /** Return the right corner according to current rotation. (SW when facing 45 degrees.) **/
    public int right() { return indexedCorner(3); }

    public int nw() { return values[0]; }
    public int ne() { return values[1]; }
    public int se() { return values[2]; }
    public int sw() { return values[3]; }

    public void setNw(int value) { values[0] = value; }
    public void setNe(int value) { values[1] = value; }
    public void setSe(int value) { values[2] = value; }
    public void setSw(int value) { values[3] = value; }

    public static final Corners FLAT = new Corners(0, 0, 0, 0);
    public static Corners flatCorners() {return FLAT;}
}
