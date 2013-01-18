
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.Button;

/**
 *
 * @author Jacob
 */
public class RHerbTest {
    public static boolean DEBUG = true;
    public static final float MOVE_SPEED = Motor.A.getMaxSpeed();
    
    public static final UltrasonicSensor US
             = new UltrasonicSensor(SensorPort.S1);
    public static final UltrasonicSensor US2
             = new UltrasonicSensor(SensorPort.S2);

    public static void main(String[] a) {
        try {
            init();
        } catch (InterruptedException ex) {
            if (DEBUG) {
                String message = ex.getMessage();
                if (message != null)
                    print(message);
            }
        }

        try {
            run();
        } catch (InterruptedException ex) {
            if (DEBUG) {
                String message = ex.getMessage();
                if (message != null)
                    print(message);
            }
        }
    }

    public static void init() throws InterruptedException {
        Motor.A.setSpeed(MOVE_SPEED);
        Motor.B.setSpeed(MOVE_SPEED);
        US.off();
        US2.off();
//        Motor.A.smoothAcceleration(true);
//        Motor.B.smoothAcceleration(true);
//        Motor.A.setPower(-100);
//        Motor.B.setPower(-100);
    }

    public static void run() throws InterruptedException {
       Motor.C.stop();
        while (true) {
            print("Running...");
            Motor.A.backward();
            Motor.B.backward();
            while(true) {
                US.ping();
                US2.ping();
                Thread.sleep(20);
                float d1 = US.getDistance();
                float d2 = US2.getDistance();
                if (d1 <= 70 && d2 <= 70) {
                    Motor.A.forward();
                    Motor.B.forward();
                    Thread.sleep(500);
                    Motor.A.stop();
                    Motor.B.stop();
                    break;
                } else if (d1 < 70 || d2 < 70) {
                    if (d1 < 70) {
                        Motor.C.rotateTo(10, true);
                    }
                    if (d2 < 70) {
                        Motor.C.rotateTo(-10, true);
                    }
                } else {
                    Motor.C.rotateTo(0);
                }
            }
            print("Stopped");
            print("Press a button\nto restart...");
            Button.ENTER.waitForPressAndRelease();
        }

    }
    
    //<editor-fold defaultstate="collapsed" desc="Print and Debug Statements">
    public static void print(Object o){
        System.out.println(o);
    }
    public static void print(Object o, Character ending){
        if (ending == null)
            System.out.print(o.toString());
        else
            System.out.print(o.toString() + ending);
    }
    public static void debug(Object o){
        if (!DEBUG) return;

        System.out.println(o);
    }
    
    public static void debug(Object o, Character ending){
        if (!DEBUG) return;

        if (ending == null)
            System.out.print(o.toString());
        else
            System.out.print(o.toString() + ending);
    }
    //</editor-fold>
}
