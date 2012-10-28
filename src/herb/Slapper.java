package herb;


import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.nxt.Sound;

/**
 *
 * @author Jacob
 */
public class Slapper {

    public static boolean DEBUG = true;
    public static final float ARM_SPEED = Motor.C.getMaxSpeed();

    public static void main(String[] a) {
        try {
            init();
        } catch (InterruptedException ex) {
            if (DEBUG) {
                String message = ex.getMessage();
                if (message != null) {
                    print(message);
                }
            }
        }

        try {
            run();
        } catch (InterruptedException ex) {
            if (DEBUG) {
                String message = ex.getMessage();
                if (message != null) {
                    print(message);
                }
            }
        }
    }

    public static void init() throws InterruptedException {
    }

    public static void run() throws InterruptedException {
        NyanThread awesomeness = new NyanThread();
        awesomeness.start();
        for (;;) {
            Button.waitForAnyPress();
            swingArm();
        }

    }

    //<editor-fold defaultstate="collapsed" desc="Print and Debug Statements">
    public static void print(Object o) {
        System.out.println(o);
    }

    public static void print(Object o, Character ending) {
        if (ending == null) {
            System.out.print(o.toString());
        } else {
            System.out.print(o.toString() + ending);
        }
    }

    public static void debug(Object o) {
        if (!DEBUG) {
            return;
        }

        System.out.println(o);
    }

    public static void debug(Object o, Character ending) {
        if (!DEBUG) {
            return;
        }

        if (ending == null) {
            System.out.print(o.toString());
        } else {
            System.out.print(o.toString() + ending);
        }
    }

    public static void sleepFor(long milliseconds) {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < milliseconds) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
                break;
            }
        }
    }

    //</editor-fold>
    public static void swingArm() {


        Motor.C.setSpeed(ARM_SPEED);
        Motor.C.rotateTo(360);

        Motor.C.rotateTo(0);
        Motor.C.stop();
    }

    private static class NyanThread extends Thread implements Runnable {

        @Override
        public void run(){
            for (;;) {
                play();
            }
        }
        public void play() {

            Sound.setVolume((int) (Sound.VOL_MAX * 0.7));
            int c = 523;
            int c1 = 554;
            int d = 587;
            int d1 = 311;
            int e = 330;
            int f = 698;
            int f1 = 370;
            int g = 784;
            int g1 = 415;
            int a = 440;
            int a1 = 466;
            int b = 247;
            int q = 400 / 3;
            int h = 800 / 3;

            Sound.playTone(f, h);
            sleepFor(h);

            Sound.playTone(g, h);
            sleepFor(h);

            Sound.playTone(c1, q);
            sleepFor(q);

            Sound.playTone(d, h);
            sleepFor(h);

            Sound.playTone(c, q);
            sleepFor(q);

            Sound.playTone(c1, q);
            sleepFor(q);

            Sound.playTone(c, q);
            sleepFor(q);

            Sound.playTone(a1, h);
            sleepFor(h);

            Sound.playTone(a1, h);
            sleepFor(h);

            Sound.playTone(c, h);
            sleepFor(h);

            Sound.playTone(c1, h);
            sleepFor(h);

            Sound.playTone(c1, q);
            sleepFor(q);

            Sound.playTone(c, q);
            sleepFor(q);

            Sound.playTone(a1, q);
            sleepFor(q);

            Sound.playTone(c, q);
            sleepFor(q);

            Sound.playTone(d, q);
            sleepFor(q);

            Sound.playTone(f, q);
            sleepFor(q);

            Sound.playTone(g, q);
            sleepFor(q);

            Sound.playTone(d, q);
            sleepFor(q);

            Sound.playTone(f, q);
            sleepFor(q);

            Sound.playTone(c, q);
            sleepFor(q);

            Sound.playTone(d, q);
            sleepFor(q);


            Sound.playTone(a1, q);
            sleepFor(q);

            Sound.playTone(c, q);
            sleepFor(q);

            Sound.playTone(a1, q);
            sleepFor(q);

            Sound.playTone(d, h);
            sleepFor(h);

            Sound.playTone(f, h);
            sleepFor(h);

            Sound.playTone(g, q);
            sleepFor(q);

            Sound.playTone(d, q);
            sleepFor(q);

            Sound.playTone(f, q);
            sleepFor(q);

            Sound.playTone(c, q);
            sleepFor(q);

            Sound.playTone(d, q);
            sleepFor(q);

            Sound.playTone(a1, q);
            sleepFor(q);

            Sound.playTone(c1, q);
            sleepFor(q);

            Sound.playTone(d, q);
            sleepFor(q);

            Sound.playTone(c1, q);
            sleepFor(q);

            Sound.playTone(c, q);
            sleepFor(q);

            Sound.playTone(a1, q);
            sleepFor(q);

            Sound.playTone(c, q);
            sleepFor(q);
////////////////////////////////
            Sound.playTone(d, h);
            sleepFor(q);

            Sound.playTone(a1, q);
            sleepFor(q);

            Sound.playTone(c, q);
            sleepFor(q);

            Sound.playTone(d, q);
            sleepFor(q);

            Sound.playTone(c, q);
            sleepFor(q);

            Sound.playTone(c1, q);
            sleepFor(q);

            Sound.playTone(c, q);
            sleepFor(q);

            Sound.playTone(c1, q);
            sleepFor(q);

            Sound.playTone(c, h);
            sleepFor(q);

            Sound.playTone(a1, h);
            sleepFor(q);

            Sound.playTone(c, h);
            sleepFor(q);

            Sound.playTone(c, q);
            sleepFor(q);

            Sound.playTone(c, h);
            sleepFor(h);
            //


        }
    }
}

