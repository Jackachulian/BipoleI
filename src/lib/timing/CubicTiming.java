package lib.timing;

public class CubicTiming implements TimingFunction {
    private final double c1, c2;

    public CubicTiming(double c1, double c2){
        this.c1 = c1;
        this.c2 = c2;
    }

    @Override
    public double valueAtTime(double t) {
        return 3 * (1-t)*(1-t) * t * c1  +  3 * (1-t) * t * t * c2  +  t * t * t;
    }

    @Override
    public boolean endsAtOne() {
        return true;
    }


}
