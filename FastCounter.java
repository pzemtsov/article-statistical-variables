public class FastCounter extends LazyCompoundCounter
{
    private ThreadLocal<Counter> counters = new ThreadLocal<Counter> ();

    public FastCounter ()
    {
        super (LazyCounter.class);
    }
    
    @Override
    public Counter getThreadLocalView ()
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
