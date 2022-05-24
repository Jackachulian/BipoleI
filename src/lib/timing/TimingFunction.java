package lib.timing;

public interface TimingFunction {
    /** Given the time fron 0.0 - 1.0, return the value the function should be.
     * (Function starts at 0.0 and ends at 1.0.
     */
    double valueAtTime(double t);
    /** If true, ends at one. If false, ends at 0. **/
    boolean endsAtOne();

    TimingFunction EASE = new EaseTiming();
    TimingFunction LINEAR = new LinearTiming();
}
