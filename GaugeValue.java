
public abstract class GaugeValue
{
    public abstract long getCount ();
    public abstract long getSum ();
    public abstract long getMax ();
    public abstract long getMin ();
    
    @Override
    public String toString ()
    {
        return "Count=" + getCount() + "; sum=" + getSum() + "; max=" + getMax() + "; min=" + getMin();
    }
}
