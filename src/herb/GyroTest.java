package herb;


import lejos.nxt.SensorPort;
import lejos.nxt.addon.GyroSensor;


/**
 *
 * @author Jacob
 */
public class GyroTest {
    public static boolean DEBUG = true;

    public static GyroSensor gyro = new GyroSensor(SensorPort.S3);

    public static void main(String[] a) {
        try {
            debug("Initializing...");
            init();
            debug("Initialization Complete.");
        } catch (InterruptedException ex) {
            if (DEBUG) {
                String message = ex.getMessage();
                if (message != null)
                    print(message);
            }
        }

        try {
            debug("Initialization Complete.");
            run();
            debug("Program completed successfully.");
        } catch (InterruptedException ex) {
            if (DEBUG) {
                String message = ex.getMessage();
                if (message != null)
                    print(message);
            }
        }
    }

    public static void init() throws InterruptedException {
        debug("Calibrating gyro,\nhold in desired position");
        Thread.sleep(500);
        debug('.', null);
        Thread.sleep(500);
        debug('.', null);
        Thread.sleep(500);
        debug('.', null);
        Thread.sleep(500);
        debug('.', null);
        Thread.sleep(500);
        debug('.', null);
        Thread.sleep(500);
        debug('.', null);
        gyro.recalibrateOffset();
        
        debug("Calibration complete");
        Thread.sleep(500);
       
    }

    public static void run() throws InterruptedException {
        String report;
        for (;;) {
            //Thread.sleep(250);
            report = "Angular Velocity\n";
            report += gyro.getAngularVelocity();
            report += "read \"Value\"\n";
            report += gyro.readValue();
            print(report);
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
