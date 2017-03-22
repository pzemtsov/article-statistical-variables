

public final class EmptyCounter extends ServerCounter
{
    @Override
    public void add (long increment)
    {
    }
    
    @Override
    public long getAndReset ()
    {
        return 0;
    }
}
