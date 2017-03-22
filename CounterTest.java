import java.util.ArrayList;
import java.util.Arrays;

public class CounterTest extends Thread
{
    private final Counter counter;
    private final int delay;
    static volatile boolean running = true;
    long N;
    long time;
    double sum;
    
    public CounterTest (Counter counter, int delay)
    {
        this.counter = counter;
        this.delay = delay;
    }
    
    @Override
    public void run ()
    {
        long N = 0;
        long t0 = System.currentTimeMillis ();
        double sum = 0;
        while (running) {
            for (int j = 1; j <= delay; j++) {
                sum += 1.0/j;
            }
            counter.add ();
            ++ N;
        }
        long t1 = System.currentTimeMillis ();
        time = t1 - t0;
        this.N = N;
        this.sum = sum;
    }
    
    private static abstract class CounterTester
    {
        public abstract double runTest (ServerCounter counter, int nthreads, int delay) throws InterruptedException;
    }
    
    private static class DirectCounterTester extends CounterTester
    {
        @Override
        public double runTest (ServerCounter counter, int nthreads, int delay) throws InterruptedException
        {
            ArrayList<CounterTest> threads = new ArrayList<CounterTest> ();
            for (int i = 0; i < nthreads; i++) {
                threads.add (new CounterTest (counter.getThreadLocalView (), delay));
            }
            running = true;
            for (CounterTest t : threads) {
                t.start ();
            }
            long sum = 0;
            for (int sec = 0; sec < 10; sec ++) {
                Thread.sleep (1000);
                long v = counter.getAndReset ();
                System.out.printf ("Counter retrieved: %11d\n", v);
                sum += v;
            }
            running = false;
            for (Thread t : threads) {
                t.join ();
            }
            long lastval = counter.getAndReset ();
            sum += lastval;
            System.out.printf ("Lastval retrieved: %11d\n", lastval);
            
            long testsum = 0;
            double nssum = 0;
            for (CounterTest test : threads) {
                double ns = test.time * 1.0E6 / test.N;
                testsum += test.N;
                nssum += ns;
            }
            System.out.printf ("Time/op avg: %6.2f ns\n", nssum / nthreads);
            System.out.printf ("Counter sum: %12d\n", sum);
            System.out.printf ("Correct sum: %12d\n", testsum);
            return nssum / nthreads;
        }
    }

    private static class DelayedCounterTester extends CounterTester
    {
        @Override
        public double runTest (ServerCounter counter, int nthreads, int delay) throws InterruptedException
        {
            IndirectCounter ic = (IndirectCounter) counter;
            
            ArrayList<CounterTest> threads = new ArrayList<CounterTest> ();
            for (int i = 0; i < nthreads; i++) {
                threads.add (new CounterTest (counter.getThreadLocalView (), delay));
            }
            running = true;
            for (CounterTest t : threads) {
                t.start ();
            }
            long sum = 0;
            for (int sec = 0; sec < 10; sec ++) {
                Thread.sleep (500);
                CounterValue val = ic.getAndResetValue ();
                Thread.sleep (500);
                long v = val.get ();
                System.out.printf ("Counter retrieved: %11d\n", v);
                sum += v;
            }
            running = false;
            for (Thread t : threads) {
                t.join ();
            }
            CounterValue lastvalue = ic.getAndResetValue ();
            long lastval = lastvalue.get ();
            sum += lastval;
            System.out.printf ("Lastval retrieved: %11d\n", lastval);
            
            long testsum = 0;
            double nssum = 0;
            for (CounterTest test : threads) {
                double ns = test.time * 1.0E6 / test.N;
                testsum += test.N;
                nssum += ns;
            }
            System.out.printf ("Time/op avg: %6.2f ns\n", nssum / nthreads);
            System.out.printf ("Counter sum: %12d\n", sum);
            System.out.printf ("Correct sum: %12d\n", testsum);
            return nssum / nthreads;
        }
    }
    
    private static abstract class CounterFactory
    {
        public abstract ServerCounter create ();
    }

    private static void counterTest (CounterFactory factory, CounterTester tester, int nthreads, int delay) throws InterruptedException
    {
        int N = 8;
        double results[] = new double [N+2];
        for (int i = 0; i < N+2; i++) {
            results[i] = tester.runTest (factory.create (), nthreads, delay);
        }
        Arrays.sort (results);
        double sum = 0;
        double sum2 = 0;
        for (int i = 0; i < N; i++) {
            double x = results[i];
            sum += x;
            sum2 += x * x;
            System.out.printf ("%.2f ", x);
        }
        System.out.println ();
        double avg = sum / N;
        double d = sum2 / N - avg * avg;
        double s = Math.sqrt (d);
        System.out.printf ("%40s @ %4d : %2d: avg=%8.2f s=%10.4f    %6.2f%%\n",
                           factory.toString (), delay, nthreads, avg, s, s * 100.0 / avg);
    }
    
    private static void runTests (CounterFactory factory, CounterTester tester, int delay) throws InterruptedException
    {
        counterTest (factory, tester, 1, delay);
        counterTest (factory, tester, 2, delay);
        counterTest (factory, tester, 6, delay);
        counterTest (factory, tester, 12, delay);
    }
    
    private static void runTests (final Class<? extends ServerCounter> clazz, CounterTester tester, int delay) throws InterruptedException
    {
        runTests (new CounterFactory () {
            @Override
            public ServerCounter create ()
            {
                try {
                    return clazz.newInstance ();
                } catch (Exception e) {
                    return null;
                }
            }
            
            @Override
            public String toString ()
            {
                return clazz.getName ();
            }
        }, tester, delay);
    }

    private static void runCompoundTests (final Class<? extends ServerCounter> c, CounterTester tester, int delay) throws InterruptedException
    {
        runTests (new CounterFactory () {
            @Override
            public ServerCounter create ()
            {
                try {
                    return new CompoundCounter (c);
                } catch (Exception e) {
                    return null;
                }
            }
            
            @Override
            public String toString ()
            {
                return "CompoundCounter (" + c.getName () + ")";
            }
        }, tester, delay);
    }
    
    private static void runLazyCompoundTests (final Class<? extends IndirectCounter> c, CounterTester tester, int delay) throws InterruptedException
    {
        runTests (new CounterFactory () {
            @Override
            public ServerCounter create ()
            {
                try {
                    return new LazyCompoundCounter (c);
                } catch (Exception e) {
                    return null;
                }
            }
            
            @Override
            public String toString ()
            {
                return "LazyCompoundCounter (" + c.getName () + ")";
            }
        }, tester, delay);
    }

    private static void runTests (int delay) throws InterruptedException
    {
        runTests (EmptyCounter.class, new DirectCounterTester (), delay);
        runTests (TrivialCounter.class, new DirectCounterTester (), delay);
        runTests (VolatileCounter.class, new DirectCounterTester (), delay);
        runTests (SimpleCounter.class, new DirectCounterTester (), delay);
        runTests (AtomicCounter.class, new DirectCounterTester (), delay);
        runCompoundTests (TrivialCounter.class, new DirectCounterTester (), delay);
        runCompoundTests (VolatileCounter.class, new DirectCounterTester (), delay);
        runCompoundTests (SimpleCounter.class, new DirectCounterTester (), delay);
        runCompoundTests (AtomicCounter.class, new DirectCounterTester (), delay);
        runCompoundTests (LazyCounter.class, new DirectCounterTester (), delay);
        runTests (LazyCounter.class, new DelayedCounterTester (), delay);
        runLazyCompoundTests (LazyCounter.class, new DelayedCounterTester (), delay);
        runTests (FastCounter.class, new DelayedCounterTester (), delay);
        runTests (FastAtomicCounter.class, new DirectCounterTester (), delay);
        runLazyCompoundTests (LazyVolatileCounter.class, new DelayedCounterTester (), delay);
        runLazyCompoundTests (LazyAllocCounter.class, new DelayedCounterTester (), delay);
    }
    
    public static void main (String [] args) throws InterruptedException
    {
        runTests (0);
        runTests (10);
        runTests (50);
        runTests (100);
    }
}
