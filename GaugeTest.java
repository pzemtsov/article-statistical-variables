import java.util.ArrayList;
import java.util.Arrays;

public class GaugeTest extends Thread
{
    private final Gauge gauge;
    private final int delay;
    static volatile boolean running = true;
    long time;
    long N;
    long sum;
    long max;
    long min;
    
    public GaugeTest (Gauge Gauge, int delay)
    {
        this.gauge = Gauge;
        this.delay = delay;
    }
    
    @Override
    public void run ()
    {
        long N = 0;
        long sum = 0;
        long max = Long.MIN_VALUE;
        long min = Long.MAX_VALUE;
        long t0 = System.currentTimeMillis ();
        double s = 0;
        while (running) {
            s += 10.0;
            for (int j = 1; j <= delay; j++) {
                s += 1.0/j;
            }
            long value = (long) s;
            gauge.report (value);
            ++ N;
            sum += value;
            max = Math.max (max,  value);
            min = Math.min (min,  value);
        }
        long t1 = System.currentTimeMillis ();
        time = t1 - t0;
        this.N = N;
        this.sum = sum;
        this.max = max;
        this.min = min;
    }
    
    private static abstract class GaugeTester
    {
        public abstract double runTest (ServerGauge Gauge, int nthreads, int delay) throws InterruptedException;
    }
    
    private static class DirectGaugeTester extends GaugeTester
    {
        @Override
        public double runTest (ServerGauge gauge, int nthreads, int delay) throws InterruptedException
        {
            ArrayList<GaugeTest> threads = new ArrayList<GaugeTest> ();
            for (int i = 0; i < nthreads; i++) {
                threads.add (new GaugeTest (gauge.getThreadLocalView (), delay));
            }
            running = true;
            for (GaugeTest t : threads) {
                t.start ();
            }
            MutableGaugeValue sum = new MutableGaugeValue ();
            for (int sec = 0; sec < 10; sec ++) {
                Thread.sleep (1000);
                GaugeValue v = gauge.getAndReset ();
                System.out.println ("Gauge retrieved: " + v);
                sum.add (v);
            }
            running = false;
            for (Thread t : threads) {
                t.join ();
            }
            GaugeValue lastval = gauge.getAndReset ();
            sum.add (lastval);
            System.out.println ("Lastval retrieved: " + lastval);
            
            double nssum = 0;
            MutableGaugeValue testsum = new MutableGaugeValue ();
            for (GaugeTest test : threads) {
                double ns = test.time * 1.0E6 / test.N;
                testsum.add (new SimpleGaugeValue (test.N, test.sum, test.max, test.min));
                nssum += ns;
            }
            System.out.printf ("Time/op avg: %6.2f ns\n", nssum / nthreads);
            System.out.println ("Gauge sum  : " + sum);
            System.out.println ("Correct sum: " + testsum);
            return nssum / nthreads;
        }
    }

    private static class DelayedGaugeTester extends GaugeTester
    {
        @Override
        public double runTest (ServerGauge gauge, int nthreads, int delay) throws InterruptedException
        {
            ArrayList<GaugeTest> threads = new ArrayList<GaugeTest> ();
            for (int i = 0; i < nthreads; i++) {
                threads.add (new GaugeTest (gauge.getThreadLocalView (), delay));
            }
            running = true;
            for (GaugeTest t : threads) {
                t.start ();
            }
            MutableGaugeValue sum = new MutableGaugeValue ();
            for (int sec = 0; sec < 10; sec ++) {
                Thread.sleep (500);
                GaugeValue val = gauge.getAndReset ();
                Thread.sleep (500);
                sum.add (val);
                System.out.println ("Gauge retrieved: " + val);
            }
            running = false;
            for (Thread t : threads) {
                t.join ();
            }
            GaugeValue lastvalue = gauge.getAndReset ();
            sum.add (lastvalue);
            System.out.println ("Lastval retrieved: " + lastvalue);
            
            long testsum = 0;
            double nssum = 0;
            for (GaugeTest test : threads) {
                double ns = test.time * 1.0E6 / test.N;
                testsum += test.N;
                nssum += ns;
            }
            System.out.printf ("Time/op avg: %6.2f ns\n", nssum / nthreads);
            System.out.println ("Gauge sum  : " + sum);
            System.out.println ("Correct sum: " + testsum);
            return nssum / nthreads;
        }
    }
    
    private static abstract class GaugeFactory
    {
        public abstract ServerGauge create ();
    }

    private static void gaugeTest (GaugeFactory factory, GaugeTester tester, int nthreads, int delay) throws InterruptedException
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
    
    private static void runTests (GaugeFactory factory, GaugeTester tester, int delay) throws InterruptedException
    {
        gaugeTest (factory, tester, 1, delay);
        gaugeTest (factory, tester, 2, delay);
        gaugeTest (factory, tester, 6, delay);
        gaugeTest (factory, tester, 12, delay);
    }
    
    private static void runTests (final Class<? extends ServerGauge> clazz, GaugeTester tester, int delay) throws InterruptedException
    {
        runTests (new GaugeFactory () {
            @Override
            public ServerGauge create ()
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

    private static void runCompoundTests (final Class<? extends ServerGauge> c, GaugeTester tester, int delay) throws InterruptedException
    {
        runTests (new GaugeFactory () {
            @Override
            public ServerGauge create ()
            {
                try {
                    return new CompoundGauge (c);
                } catch (Exception e) {
                    return null;
                }
            }
            
            @Override
            public String toString ()
            {
                return "CompoundGauge (" + c.getName () + ")";
            }
        }, tester, delay);
    }

    private static void runLazyCompoundTests (final Class<? extends ServerGauge> c, GaugeTester tester, int delay) throws InterruptedException
    {
        runTests (new GaugeFactory () {
            @Override
            public ServerGauge create ()
            {
                try {
                    return new LazyCompoundGauge (c);
                } catch (Exception e) {
                    return null;
                }
            }
            
            @Override
            public String toString ()
            {
                return "LazyCompoundGauge (" + c.getName () + ")";
            }
        }, tester, delay);
    }
    
    private static void runTests (int delay) throws InterruptedException
    {
        runTests (EmptyGauge.class, new DirectGaugeTester (), delay);
        runTests (SimpleGauge.class, new DirectGaugeTester (), delay);
        runCompoundTests (SimpleGauge.class, new DirectGaugeTester (), delay);
        runLazyCompoundTests (LazyGauge.class, new DelayedGaugeTester (), delay);
        runTests (FastGauge.class, new DelayedGaugeTester (), delay);
        runLazyCompoundTests (LazyVolatileGauge.class, new DelayedGaugeTester (), delay);
        runLazyCompoundTests (LazyAllocGauge.class, new DelayedGaugeTester (), delay);
        runLazyCompoundTests (LazyBufferGauge.class, new DelayedGaugeTester (), delay);
    }
    
    public static void main (String [] args) throws InterruptedException
    {
        runTests (0);
        runTests (10);
        runTests (50);
        runTests (100);
    }
}
