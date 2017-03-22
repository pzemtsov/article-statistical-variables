
public final class LazyCounter extends IndirectCounter
{
    private volatile MutableCounterValue current = new MutableCounterValue ();
    
    @Override
    public void add (long increment)
    {
        current.add (increment);
    }

    @Override
    public CounterValue getAndResetValue ()
    {
        CounterValue result = current;
        current = new MutableCounterValue ();
        return result;
    }
}
