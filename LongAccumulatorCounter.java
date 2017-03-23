import java.util.concurrent.atomic.LongAccumulator;

public class LongAccumulatorCounter extends ServerCounter {

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
        return accumulator.getThenReset();
    }

}
