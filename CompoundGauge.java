import java.util.ArrayList;


public class CompoundGauge extends ServerGauge
{
    private ArrayList<ServerGauge> views = new ArrayList<ServerGauge> ();
    private Class<? extends ServerGauge> clazz;
    private SimpleGauge localGauge = new SimpleGauge ();
    
    public CompoundGauge (Class<? extends ServerGauge> clazz)
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
        MutableGaugeValue sum = new MutableGaugeValue ();
        for (ServerGauge view : views) {
            sum.add (view.getAndReset ());
        }
        return sum;
    }
}
