
public class MutableGaugeValue extends GaugeValue
{
    private long count = 0;
    private long sum = 0;
    private long max = Long.MIN_VALUE;
    private long min = Long.MAX_VALUE;
    public long dummy1;
    public long dummy2;

    @Override
    public long getCount ()
    {
        return count;
    }
    
    @Override
    public long getSum ()
    {
        return sum;
    }
    
    @Override
    public long getMax ()
    {
        return max;
    }
    
    @Override
    public long getMin ()
    {
        return min;
    }
    
    public void report (long value)
    {
        sum += value;
        max = Math.max (max, value);
        min = Math.min (min, value);
        ++ count;
    }
    
    public void add (GaugeValue val)
    {
        if (val != null) {
            this.count += val.getCount ();
            this.sum += val.getSum ();
            this.max = Math.max (this.max, val.getMax ());
            this.min = Math.min (this.min, val.getMin ());
        }
    }
}
