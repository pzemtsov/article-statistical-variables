
public class SimpleGaugeValue extends GaugeValue
{
    private final long count;
    private final long sum;
    private final long max;
    private final long min;

    public SimpleGaugeValue (long count, long sum, long max, long min)
    {
        this.count = count;
        this.sum = sum;
        this.max = max;
        this.min = min;
    }

    public SimpleGaugeValue ()
    {
        this (0, 0, Long.MIN_VALUE, Long.MAX_VALUE);
    }
    
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
}
