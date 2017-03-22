
public final class SimpleCounter extends ServerCounter
{
    private long count = 0;
    
    @Override
    public synchronized void add (long increment)
    {
        count += increment;
    }
    
    @Override
    public synchronized long getAndReset ()
    {
        long result = count;
        count = 0;
        return result;
    }
}
