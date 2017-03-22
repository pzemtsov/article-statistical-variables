
public class LazyGauge extends ServerGauge
{
    private volatile MutableGaugeValue current = new MutableGaugeValue ();

    @Override
    public void report (long value)
    {
        current.report (value);
    }

    @Override
    public GaugeValue getAndReset ()
    {
        GaugeValue result = current;
        current = new MutableGaugeValue ();
        return result;
    }
}
