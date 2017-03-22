
public class FastAtomicCounter extends CompoundCounter
{
    private ThreadLocal<Counter> counters = new ThreadLocal<Counter> ();
    
    public FastAtomicCounter ()
    {
        super (AtomicCounter.class);
    }

    @Override
    public synchronized Counter getThreadLocalView ()
    {
        return this;
    }

    @Override
    public void add (long increment)
    {
        Counter counter = counters.get ();
        if (counter == null) {
            counter = super.getThreadLocalView ();
            counters.set (counter);
        }
        counter.add (increment);
    }
}
