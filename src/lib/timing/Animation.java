package lib.timing;

public class Animation {
    private final TimingFunction function;
    private final int duration;

    private boolean finished;
    private long startTime, endTime;

    public Animation(TimingFunction function, int duration){
        this.function = function;
        this.duration = duration;
        startTime = System.currentTimeMillis();
        endTime = startTime + duration;
    }

    public Animation(int duration) {
        this(TimingFunction.EASE, duration);
    }

    public double getValue() {
        if (finished) return function.endsAtOne() ? 1.0 : 0.0;

        long time = System.currentTimeMillis();

        if (time > endTime) {
            finished = true;
            return function.endsAtOne() ? 1.0 : 0.0;
        }

        double t = 1.0*(time - startTime)/duration;
        return function.valueAtTime(t);
    }

    public boolean isFinished() {
        return finished;
    }
}
