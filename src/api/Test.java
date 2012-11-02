/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package api;

/**
 *
 * @author Jacob
 */
public class Test extends HerbBase implements Scheduler {
    long count = 0;
    
    @Override
    public long refreshTime() {
        return 500;
    }

    @Override
    public boolean forget() {
        return false;
    }

    @Override
    public void run() {
        System.out.print("run #");
        System.out.println(++count);
    }

    @Override
    public boolean skipLate() {
        return (count & 1) == 0;
    }
    
    public static void main(String[] args) {
        HerbBase base = new Test();
        Scheduler s = new Test();
        base.addScheduler(s);
        while (System.currentTimeMillis() > 0);
    }
    
}
