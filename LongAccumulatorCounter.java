import java.util.concurrent.atomic.LongAccumulator;

public class LongAccumulatorCounter extends ServerCounter {

    private long start = 0;
    
    private final LongAccumulator accumulator = new LongAccumulator(
        (left, right) -> left + right,
        0L
    );

    @Override
    public void add(long increment) {
        accumulator.accumulate(increment);
    }

    @Override
    public long getAndReset() {
        long current = accumulator.get();
        long result = current - start;
        start = current;
        return result;
    }

}
