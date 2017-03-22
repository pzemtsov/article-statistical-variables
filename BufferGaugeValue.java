import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.LongBuffer;
import sun.nio.ch.DirectBuffer;


public class BufferGaugeValue extends GaugeValue
{
    private static final int SIZE_LONGS = 1024;
    
    private static final LongBuffer heap = ByteBuffer.allocateDirect (8 * SIZE_LONGS).order (ByteOrder.LITTLE_ENDIAN).asLongBuffer ();
    private static int ptr0 = 0;
    private static int ptr = 0;
    
    private static final int COUNT_OFFSET = 0;
    private static final int SUM_OFFSET = 1;
    private static final int MAX_OFFSET = 2;
    private static final int MIN_OFFSET = 3;
    
    static {
        long addr = ((DirectBuffer) heap).address ();
        int offset = (int) (addr & 63) / 8;
        if (offset != 0) ptr0 += (8 - offset);
        ptr = ptr0;
        heap.limit (heap.capacity ());
    }
    
    public static int alloc ()
    {
        if (ptr + 4 > heap.capacity ()) {
            ptr = ptr0;
        }
        return (ptr += 8) - 8;
    }
       
    public static long getCount (int addr)
    {
        return heap.get (addr + COUNT_OFFSET);
    }

    public static long getSum (int addr)
    {
        return heap.get (addr + SUM_OFFSET);
    }

    public static long getMax (int addr)
    {
        return heap.get (addr + MAX_OFFSET);
    }

    public static long getMin (int addr)
    {
        return heap.get (addr + MIN_OFFSET);
    }

    public static void setCount (int addr, long val)
    {
        heap.put (addr + COUNT_OFFSET, val);
    }
    
    public static void setSum (int addr, long val)
    {
        heap.put (addr + SUM_OFFSET, val);
    }

    public static void setMax (int addr, long val)
    {
        heap.put (addr + MAX_OFFSET, val);
    }

    public static void setMin (int addr, long val)
    {
        heap.put (addr + MIN_OFFSET, val);
    }

    public static void report (int addr, long value)
    {
        setCount (addr, getCount (addr) + 1);
        setSum (addr, getSum (addr) + value);
        long max = getMax (addr);
        long min = getMin (addr);
        if (value > max) setMax (addr, value);
        if (value < min) setMin (addr, value);
    }

    private int addr;
    
    public BufferGaugeValue (int addr)
    {
        this.addr = addr;
    }

    @Override
    public long getCount ()
    {
        return getCount (addr);
    }

    @Override
    public long getSum ()
    {
        return getSum (addr);
    }

    @Override
    public long getMax ()
    {
        return getMax (addr);
    }

    @Override
    public long getMin ()
    {
        return getMin (addr);
    }
}
