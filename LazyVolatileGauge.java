public class LazyVolatileGauge extends ServerGauge
{
    private volatile MutableVolatileGaugeValue current = new MutableVolatileGaugeValue ();

    @Override
    public void report (long value)
    {
        current.report (value);
    }

    @Override
    public GaugeValue getAndReset ()
    {
        GaugeValue result = current;
        current = new MutableVolatileGaugeValue ();
        return result;
    }
}
