
public class MutableVolatileCounterValue extends CounterValue
{
    private volatile long value = 0;
    public long v1 = 0;
    public long v2 = 0;
    public long v3 = 0;
    public long v4 = 0;
    public long v5 = 0;

    public void add (long increment)
    {
        value += increment;
    }

    @Override
    public long get ()
    {
        return value;
    }
}
