
public final class CompoundGaugeValue extends GaugeValue
{
    private final GaugeValue [] values;
    private GaugeValue resolvedValue = null;
    
    public CompoundGaugeValue (GaugeValue [] values)
    {
        this.values = values;
    }

    private GaugeValue sum ()
    {
        MutableGaugeValue sum = new MutableGaugeValue ();
        for (GaugeValue v : values) {
            if (v != null) {
                sum.add (v);
            }
        }
        return sum;
    }

    private GaugeValue resolve ()
    {
        if (resolvedValue == null) {
            resolvedValue = sum ();
        }
        return resolvedValue;
    }
    
    @Override
    public long getCount ()
    {
        return resolve ().getCount ();
    }
    
    @Override
    public long getSum ()
    {
        return resolve ().getSum ();
    }
    
    @Override
    public long getMax ()
    {
        return resolve ().getMax ();
    }
    
    @Override
    public long getMin ()
    {
        return resolve ().getMin ();
    }
}
