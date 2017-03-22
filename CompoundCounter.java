import java.util.ArrayList;

public class CompoundCounter extends ServerCounter
{
    private ArrayList<ServerCounter> views = new ArrayList<ServerCounter> ();
    private Class<? extends ServerCounter> clazz;
    private long localValue = 0;
    
    public CompoundCounter (Class<? extends ServerCounter> clazz)
    {
        this.clazz = clazz;
    }
    
    @Override
    public synchronized Counter getThreadLocalView ()
    {
        try {
            ServerCounter view = clazz.newInstance ();
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
    public synchronized long getAndReset ()
    {
        long sum = localValue;
        localValue = 0;
        for (ServerCounter view : views) {
            sum += view.getAndReset ();
        }
        return sum;
    }
}
