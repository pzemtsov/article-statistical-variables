
public abstract class Gauge
{
    public abstract void report (long value);

    public Gauge getThreadLocalView ()
    {
        return this;
    }
}
