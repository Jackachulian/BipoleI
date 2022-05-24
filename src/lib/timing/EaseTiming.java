package lib.timing;

/** BezierTiming but the points are 0 and 1. (uses fewer calculations than using CubicTiming(0, 1) but same result) **/
public class EaseTiming implements TimingFunction {
    @Override
    public double valueAtTime(double t) {
        return 3 * (1-t) * t * t  +  t * t * t;
    }

    @Override
    public boolean endsAtOne() {
        return true;
    }
}
