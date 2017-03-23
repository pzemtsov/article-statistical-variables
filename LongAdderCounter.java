
import java.util.concurrent.atomic.LongAdder;

public class LongAdderCounter extends ServerCounter {

    private final LongAdder adder = new LongAdder();

    @Override
    public void add(long increment) {
        adder.add(increment);
    }

    @Override
    public long getAndReset() {
        return adder.sumThenReset();
    }

}
