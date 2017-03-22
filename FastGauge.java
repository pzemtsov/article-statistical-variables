public class FastGauge extends LazyCompoundGauge
{
    private ThreadLocal<Gauge> Gauges = new ThreadLocal<Gauge> ();

    public FastGauge ()
    {
        super (LazyGauge.class);
    }

    @Override
    public Gauge getThreadLocalView ()
    {
        return this;
    }
    
    @Override
    public void report (long value)
    {
        Gauge Gauge = Gauges.get ();
        if (Gauge == null) {
            Gauge = super.getThreadLocalView ();
            Gauges.set (Gauge);
        }
        Gauge.report (value);
    }
}
