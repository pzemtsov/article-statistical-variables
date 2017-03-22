
public abstract class IndirectCounter extends ServerCounter
{
    @Override
    public abstract void add (long increment);

    public abstract CounterValue getAndResetValue ();
    
    @Override
    public long getAndReset ()
    {
        return getAndResetValue ().get ();
    }
}
