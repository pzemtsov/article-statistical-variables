
public final class CompoundCounterValue extends CounterValue
{
    private final CounterValue [] values;
    private final long ownValue;
    
    public CompoundCounterValue (CounterValue [] values, long ownValue)
    {
        this.values = values;
        this.ownValue = ownValue;
    }

    @Override
    public long get ()
    {
        long sum = ownValue;
        for (CounterValue v : values) {
            if (v != null) {
                sum += v.get();
            }
        }
        return sum;
    }
}
