
public class SimpleGauge extends ServerGauge
{
    private long count = 0;
    private long sum = 0;
    private long max = Long.MIN_VALUE;
    private long min = Long.MAX_VALUE;
    
    @Override
    public synchronized void report (long value)
    {
        sum += value;
        max = Math.max (max, value);
        min = Math.min (min, value);
        ++ count;
    }
    
    @Override
    public synchronized GaugeValue getAndReset ()
    {
        GaugeValue result = new SimpleGaugeValue (count, sum, max, min);
        count = sum = 0;
        max = Long.MIN_VALUE;
        min = Long.MAX_VALUE;
        return result;
    }
}
