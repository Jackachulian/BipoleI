package lib;

import lib.engine.Corners;
import lib.timing.AnimatedValue;

/** Same as lib.engine.Corners but with Numbers and used only for display instead of game logic **/
public class NumberCorners {
    /** All four diangonal cardinal direction corners. [NW, SW, SE, NE] **/
    public Number[] values;

    public NumberCorners(int nw, int sw, int se, int ne) {
        values = new Number[]{nw, sw, se, ne};
    }

    public NumberCorners() {
        this(-1, -1, -1, -1);
    }

    private Number indexedCorner(int index) {
        return values[(index + Camera.cornerShift) % 4];
    }

    /** Return the back corner according to current rotation. (NW when facing 45 degrees.) **/
    public double back() { return indexedCorner(0).doubleValue(); }
    /** Return the left corner according to current rotation. (NE when facing 45 degrees.) **/
    public double left() { return indexedCorner(1).doubleValue(); }
    /** Return the back corner according to current rotation. (SE when facing 45 degrees.) **/
    public double front() { return indexedCorner(2).doubleValue(); }
    /** Return the right corner according to current rotation. (SW when facing 45 degrees.) **/
    public double right() { return indexedCorner(3).doubleValue(); }

    public double nw() { return values[0].doubleValue(); }
    public double ne() { return values[1].doubleValue(); }
    public double se() { return values[2].doubleValue(); }
    public double sw() { return values[3].doubleValue(); }

    public void setNw(int value) { values[0] = value; }
    public void setNe(int value) { values[1] = value; }
    public void setSe(int value) { values[2] = value; }
    public void setSw(int value) { values[3] = value; }

    private static final Corners FLAT = new Corners(0, 0, 0, 0);
    public static Corners flatCorners() {return FLAT;}

    public void set(Corners corners) {
        for (int i=0; i<4; i++) {
            values[i] = corners.values[i];
        }
    }

    public void easeTo(Corners corners){
        for (int i=0; i<4; i++) {
            values[i] = new AnimatedValue(GuiConstants.CURSOR_SPEED, values[i].doubleValue(), corners.values[i]);
        }
    }
}
