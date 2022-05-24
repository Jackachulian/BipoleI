package lib.timing;

public class AnimatedValue extends Number {
    private final Animation animation;
    private final double start;
    private final double end;

    public AnimatedValue(Animation animation, double start, double end) {
        this.animation = animation;
        this.start = start;
        this.end = end;
    }
    public AnimatedValue(TimingFunction function, int duration, double start, double end){
        this(new Animation(function, duration), start, end);
    }
    public AnimatedValue(int duration, double start, double end){
        this(new Animation(duration), start, end);
    }

    @Override
    public double doubleValue() {
        if (animation.isFinished()){
            return end;
        } else {
            return start + animation.getValue()*(end-start);
        }
    }

    @Override
    public int intValue() {
        return (int)doubleValue();
    }

    @Override
    public long longValue() {
        return (long)doubleValue();
    }

    @Override
    public float floatValue() {
        return (float)doubleValue();
    }
}
