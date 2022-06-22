package lib.timing;

public class Animation {
    private final TimingFunction function;
    private final long duration;

    private boolean finished;
    private long startTime, endTime;

    public Animation(TimingFunction function, long duration){
        this.function = function;
        this.duration = duration*1000000;
        startTime = System.nanoTime();
        endTime = startTime + this.duration;
    }

    public Animation(long duration) {
        this(TimingFunction.EASE, duration);
    }

    public double getValue() {
        if (finished) return function.endsAtOne() ? 1.0 : 0.0;

        long time = System.nanoTime();

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

    public long getDuration() {
        return duration;
    }
}
