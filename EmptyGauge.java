
public class EmptyGauge extends ServerGauge
{

    @Override
    public void report (long value)
    {
    }

    @Override
    public GaugeValue getAndReset ()
    {
        return new SimpleGaugeValue ();
    }
}
