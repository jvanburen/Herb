
package api;

/**
 *
 * @author Jacob
 */
public class Test extends HerbBase implements Event {
    static long count = 0;
    long wait;
    
    public Test(){
        wait = 500;
    }
    public Test(long waitTime) {
        wait = waitTime;
    }
    
    @Override
    public long callTime() {
        wait += 10;
        return System.currentTimeMillis() + wait;
    }

    @Override
    public boolean forget() {
        return false;
    }

    @Override
    public void run() {
        System.out.print("(" + wait + "ms) run #");
        System.out.println(++count);
        System.out.flush();
    }

    @Override
    public boolean skipLate() {
        return (count & 1) == 0;
    }
    
    public static void main(String[] args) {
        System.out.println((int)Float.POSITIVE_INFINITY);
        System.out.println((int)Float.NaN);
        System.out.println(Float.NaN == Float.NaN);

        HerbBase base = new Test(0);
        Event s = new Test(200);
        Event t = new Test(333);
        base.addScheduler(s);
        base.addScheduler(t);
        while (System.currentTimeMillis() > 0);
    }
    
}
