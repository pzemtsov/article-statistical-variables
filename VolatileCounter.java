
public final class VolatileCounter extends ServerCounter
{
    private volatile long count = 0;
    
    @Override
    public void add (long increment)
    {
        count += increment;
    }
    
    @Override
    public long getAndReset ()
    {
        long result = count;
        count = 0;
        return result;
    }
}
