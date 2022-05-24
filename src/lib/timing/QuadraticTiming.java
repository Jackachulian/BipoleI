package lib.timing;

public class QuadraticTiming implements TimingFunction {
    private final double c;

    public QuadraticTiming(double c){
        this.c = c;
    }

    @Override
    public double valueAtTime(double t) {
        return 2 * (1-t) * t * c + t*t;
    }

    @Override
    public boolean endsAtOne() {
        return false;
    }


}
