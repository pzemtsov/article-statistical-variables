
public abstract class Counter
{
    public abstract void add (long increment);

    public void add ()
    {
        add (1);
    }

    public Counter getThreadLocalView ()
    {
        return this;
    }
}
