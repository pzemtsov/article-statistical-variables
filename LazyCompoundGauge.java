import java.util.ArrayList;

public class LazyCompoundGauge extends ServerGauge
{
    private ArrayList<ServerGauge> views = new ArrayList<ServerGauge> ();
    private Class<? extends ServerGauge> clazz;
    private SimpleGauge localGauge = new SimpleGauge ();

    public LazyCompoundGauge (Class<? extends ServerGauge> clazz)
    {
        this.clazz = clazz;
        views.add (localGauge);
    }
    
    @Override
    public synchronized Gauge getThreadLocalView ()
    {
        try {
            ServerGauge view = clazz.newInstance ();
            views.add (view);
            return view;
        } catch (Exception e) {
            throw new RuntimeException (e);
        }
    }

    @Override
    public void report (long value)
    {
        localGauge.report (value);
    }

    @Override
    public synchronized GaugeValue getAndReset ()
    {
        GaugeValue [] values = new GaugeValue [views.size ()];
        for (int i = 0; i < values.length; i++) {
            values [i] = views.get (i).getAndReset ();
        }
        return new CompoundGaugeValue (values);
    }
}
