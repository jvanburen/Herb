package herb;


import cactus.Motor;
import lejos.nxt.*;
import lejos.nxt.addon.GyroSensor;
import lejos.robotics.EncoderMotor;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveListener;
import lejos.robotics.navigation.MoveProvider;
import lejos.robotics.navigation.SegowayPilot;

/**
 *
 * @author Jacob
 */
public class HerbDisplay {

    public static final boolean DEBUG = true;
    public static final int RIGHT = -1, LEFT = 1;
    public static final double WHEEL_CIRCUMFERENCE = 14; // cm
    public static final double WHEEL_DIAMETER = WHEEL_CIRCUMFERENCE / Math.PI; //cm
    public static final int SERVO_STRENGTH = 900;
    public static final int MOVE_DELAY = 500; //ms
    public static final int DETECT_DISTANCE = 60; //cm
    public static final int ARM_SWING_DISTANCE = 20; //cm
    public static final int EOPD_REVERSE_DIFF = 10; //arbtrary...
    public static final int ARC_SIZE = 50; //wheel differential in percent
    public static final int TRAVEL_SPEED = 100; //arbitrary, > 0
    public static final int TURN_DIRECTION = RIGHT; //RIGHT or LEFT
    public static final int ARM_SPEED = 200;


    //
    //sensors
    //
    private static final GyroSensor GYRO_SENSOR = new GyroSensor(SensorPort.S2);
    private static final UltrasonicSensor US_SENSOR = new UltrasonicSensor(SensorPort.S1);

    //
    //motors
    //
    private static final NXTRegulatedMotor LEFT_MOTOR = Motor.B;
    private static final NXTRegulatedMotor RIGHT_MOTOR = Motor.A;
    private static final EncoderMotor LEFT_ENCODER_MOTOR = new NXTMotor(MotorPort.B);
    private static final EncoderMotor RIGHT_ENCODER_MOTOR = new NXTMotor(MotorPort.A);

    //
    //static variables
    //
    private static SegowayPilot GYRO_CONTROLLER;
    private static ArmController ARM_CONTROLLER;
    
    private static class ArmController extends Thread implements Runnable,
            MoveListener {

        private boolean enabled = false;

        @Override
        public final void run() {
            int USDistance;
            while (true) {
                if (enabled) {
                    USDistance = US_SENSOR.getDistance();
                    if (USDistance < ARM_SWING_DISTANCE) {
                        swingArm();
                        Sound.twoBeeps();
                        sleepFor(3000);
                        
                    }
                }

                sleepFor(100);

            }
        }

        @Override
        public void moveStarted(Move move, MoveProvider mp) {
            if (!this.isAlive())
                this.start();
        }

        @Override
        public void moveStopped(Move move, MoveProvider mp) {
        }
        
        public void enable(){
            enabled = true;
        }
        
        public void disable(){
            enabled = false;
        }
        
        public static void swingArm(){
            Motor.C.setSpeed(ARM_SPEED);
            Motor.C.rotateTo(360);
            sleepFor(500);
            Motor.C.rotateTo(0);
            Motor.C.stop();
        }
    }
    public static void main(String[] a) {

        try {
            debug("Initializing...");
            init();
            debug("Initialization Complete.");
        } catch (InterruptedException ex) {
            if (DEBUG) {
                String message = ex.getMessage();
                if (message != null) {
                    print(message);
                }
            }
        }

        try {
            debug("Running...");
            run();
            debug("Program Completed Successfully.");
        } catch (Exception ex) {
            if (DEBUG) {
                String message = ex.getMessage();
                if (message != null) {
                    print(message);
                }
            }
        }
        while (GYRO_CONTROLLER.isAlive()) {
            sleepFor(250);
        }
        ARM_CONTROLLER.disable();
        ARM_CONTROLLER = null;

    }

    public synchronized static void init() throws InterruptedException {
        LEFT_MOTOR.setSpeed(SERVO_STRENGTH);
        RIGHT_MOTOR.setSpeed(SERVO_STRENGTH);

        GYRO_CONTROLLER = new SegowayPilot(
                LEFT_ENCODER_MOTOR,
                RIGHT_ENCODER_MOTOR,
                GYRO_SENSOR,
                WHEEL_DIAMETER,
                10e2);
        
        ARM_CONTROLLER = new ArmController();
        ARM_CONTROLLER.disable();

        debug("MoveListener attached");

    }

    public synchronized static void run() throws InterruptedException {
        //sleepFor(5000); //let it balance
        GYRO_CONTROLLER.addMoveListener(ARM_CONTROLLER);
        GYRO_CONTROLLER.setTravelSpeed(TRAVEL_SPEED);
        GYRO_CONTROLLER.setMoveDelay(MOVE_DELAY);
        //ARM_CONTROLLER.start();
        //ARM_CONTROLLER.enable();
       // GYRO_CONTROLLER.backward(); //actually forward
        

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
        if (DEBUG)
            System.out.println(o);
    }

    public static void debug(Object o, Character ending) {
        if (!DEBUG)
            return;

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
}
