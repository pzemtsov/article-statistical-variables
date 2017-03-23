
import java.util.concurrent.atomic.LongAdder;

public class LongAdderCounter extends ServerCounter {

    private long start = 0;

    private final LongAdder adder = new LongAdder();

    @Override
    public void add(long increment) {
        adder.add(increment);
    }

    @Override
    public long getAndReset() {
        long current = adder.sum ();
        long result = current - start;
        start = current;
        return result;
    }
}
