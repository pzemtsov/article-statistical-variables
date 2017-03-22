import java.util.ArrayList;

public class LazyCompoundCounter extends IndirectCounter
{
    private ArrayList<IndirectCounter> views = new ArrayList<IndirectCounter> ();
    private Class<? extends IndirectCounter> clazz;
    private long localValue = 0;

    public LazyCompoundCounter (Class<? extends IndirectCounter> clazz)
    {
        this.clazz = clazz;
    }
    
    @Override
    public synchronized Counter getThreadLocalView ()
    {
        try {
            IndirectCounter view = clazz.newInstance ();
            views.add (view);
            return view;
        } catch (Exception e) {
            throw new RuntimeException (e);
        }
    }
    
    @Override
    public synchronized void add (long increment)
    {
        localValue += increment;
    }

    @Override
    public synchronized CounterValue getAndResetValue ()
    {
        CounterValue [] values = new CounterValue [views.size ()];
        for (int i = 0; i < values.length; i++) {
            values [i] = views.get (i).getAndResetValue ();
        }
        long ownValue = localValue;
        localValue = 0;
        return new CompoundCounterValue (values, ownValue);
    }
}
