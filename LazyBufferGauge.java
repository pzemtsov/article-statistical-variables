
public class LazyBufferGauge extends ServerGauge
{
    private volatile int current = BufferGaugeValue.alloc ();

    @Override
    public void report (long value)
    {
        BufferGaugeValue.report (current, value);
    }

    @Override
    public GaugeValue getAndReset ()
    {
        GaugeValue result = new BufferGaugeValue (current);
        current = BufferGaugeValue.alloc ();
        return result;
    }
}
