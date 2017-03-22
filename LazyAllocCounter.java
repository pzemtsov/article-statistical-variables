
public class LazyAllocCounter extends IndirectCounter
{
    private volatile MutableCounterValue current = null;
    
    @Override
    public void add (long increment)
    {
        MutableCounterValue cur = current;
        if (cur == null) {
            cur = new MutableCounterValue ();
            current = cur;
        }
        cur.add (increment);
    }

    @Override
    public CounterValue getAndResetValue ()
    {
        CounterValue result = current;
        current = null;
        return result;
    }
}
