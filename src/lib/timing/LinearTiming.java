package lib.timing;

/** Literally just a line **/
public class LinearTiming implements TimingFunction {
    @Override
    public double valueAtTime(double t) {
        return t;
    }

    @Override
    public boolean endsAtOne() {
        return true;
    }
}
