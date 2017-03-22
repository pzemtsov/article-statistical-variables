import java.util.concurrent.atomic.AtomicLong;


public final class AtomicCounter extends ServerCounter
{
    private AtomicLong count = new AtomicLong (0);
    
    @Override
    public void add (long increment)
    {
        count.addAndGet (increment);
    }
    
    @Override
    public long getAndReset ()
    {
        return count.getAndSet (0);
    }
}
