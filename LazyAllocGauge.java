public class LazyAllocGauge extends ServerGauge
{
    private volatile MutableGaugeValue current = null;
    
    @Override
    public void report (long value)
    {
        MutableGaugeValue cur = current;
        if (cur == null) {
            cur = new MutableGaugeValue ();
            current = cur;
        }
        cur.report (value);
    }

    @Override
    public GaugeValue getAndReset ()
    {
        GaugeValue result = current;
        current = null;
        return result;
    }
}
