
public class LazyVolatileCounter extends IndirectCounter
{
    private volatile MutableVolatileCounterValue current = new MutableVolatileCounterValue ();
    
    @Override
    public void add (long increment)
    {
        current.add (increment);
    }

    @Override
    public CounterValue getAndResetValue ()
    {
        CounterValue result = current;
        current = new MutableVolatileCounterValue ();
        return result;
    }
}
