package lib;

import lib.engine.Corners;
import lib.timing.AnimatedValue;

/** Same as lib.engine.Corners but with Numbers and used only for display instead of game logic **/
public class NumberCorners {
    public Number nw, sw, se, ne;

    public NumberCorners(Number nw, Number sw, Number se, Number ne) {
        this.nw = nw;
        this.sw = sw;
        this.se = se;
        this.ne = ne;
    }

    public NumberCorners(NumberCorners other){
        this.nw = other.nw;
        this.sw = other.sw;
        this.se = other.se;
        this.ne = other.ne;
    }

    public NumberCorners() {
        nw = -1;
        sw = -1;
        se = -1;
        ne = -1;
    }

    public double nw() {
        return nw.doubleValue();
    }
    public double sw() {
        return sw.doubleValue();
    }
    public double se() {
        return se.doubleValue();
    }
    public double ne() {
        return ne.doubleValue();
    }

    public void set(Number nw, Number sw, Number se, Number ne) {
        this.nw = nw;
        this.sw = sw;
        this.se = se;
        this.ne = ne;
    }

    public void set(Corners corners) {
        this.nw = corners.nw();
        this.sw = corners.sw();
        this.se = corners.se();
        this.ne = corners.ne();
    }

    public void easeTo(Corners corners){
        nw = new AnimatedValue(GuiConstants.CURSOR_SPEED, nw.doubleValue(), corners.nw());
        sw = new AnimatedValue(GuiConstants.CURSOR_SPEED, sw.doubleValue(), corners.sw());
        se = new AnimatedValue(GuiConstants.CURSOR_SPEED, se.doubleValue(), corners.se());
        ne = new AnimatedValue(GuiConstants.CURSOR_SPEED, ne.doubleValue(), corners.ne());
    }
}
