//package herb;


import lejos.nxt.*;
import lejos.nxt.addon.EOPD;
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
public class HerbThree {

    public static final boolean DEBUG = true;
    public static final int RIGHT = -1, LEFT = 1;
    public static final float WHEEL_CIRCUMFERENCE = 14.0f; // cm
    public static final float WHEEL_DIAMETER = WHEEL_CIRCUMFERENCE / (float) Math.PI; //cm
    public static final int SERVO_STRENGTH = 500;//900;
    public static final int MOVE_DELAY = 200; //ms
    public static final int PAUSE_DELAY = 500; //ms
    public static final float DETECT_DISTANCE = 40; //cm
    public static final float REVERSE_DETECT_DISTANCE = 20; //cm
    public static final float EOPD_REVERSE_DIFF = 5; //arbtrary...
    public static final int ARC_SIZE = 75; //wheel differential in percent
    public static final int TRAVEL_SPEED = 150; //150; //arbitrary, > 0
    public static final int ROTATE_TIME = 30; //seconds to turn in a full circle
    public static final int ROTATE_SPEED = 180/25; // degrees per second
    public static final int SENSOR_DELAY = 250;
    public static final int BALANCE_TIME = 2000; // Time to balance (initial)
    public static final float REVERSE_DISTANCE = 5; // Wheel Diameters
    public static final float WHEEL_BASE = 10f;//cm
    public static final int MAX_ERRORS = 6;


    //
    //sensors
    //
    private static final GyroSensor GYRO_SENSOR = new GyroSensor(SensorPort.S2);
    private static final UltrasonicSensor US_SENSOR_R = new UltrasonicSensor(SensorPort.S1);
    private static final UltrasonicSensor US_SENSOR_L = new UltrasonicSensor(SensorPort.S4);
    private static final EOPD EOPD_SENSOR = new EOPD(SensorPort.S3, true);

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
//    private static Segoway GYRO_CONTROLLER;
    private static SegowayPilot GYRO_CONTROLLER;
    private static NavigatorThread NAVIGATOR;
    
    /**
     * Sensor Updater
     */
    private static class Distances {
        public final int
            US_L = 0, US_R = 1, EOPD = 2;
        float[] dist;
        float[] prev;
        int errCount = 0;
        
        public Distances() {
            US_SENSOR_L.setMode(UltrasonicSensor.MODE_PING);
            US_SENSOR_R.setMode(UltrasonicSensor.MODE_PING);
            dist = new float[]{255,255,0};
            dist[EOPD] = EOPD_SENSOR.processedValue();
            update();
        }
        public void update() {
            if (errCount > MAX_ERRORS +1)
                errCount = 0;
            prev = dist;
            dist = new float[3];
            dist[EOPD] = EOPD_SENSOR.processedValue();
            US_SENSOR_L.ping();
            sleepFor(50);
            US_SENSOR_R.ping();
            sleepFor(50);
            dist[US_L] = US_SENSOR_L.getRange();
            dist[US_R] = US_SENSOR_R.getRange();
            if (dist[US_L] == 255 && prev[US_L] <= REVERSE_DETECT_DISTANCE && errCount < MAX_ERRORS) {
                dist[US_L] = prev[US_L];
                ++errCount;
            }
            if (dist[US_R] == 255 && prev[US_R] <= REVERSE_DETECT_DISTANCE && errCount < MAX_ERRORS) {
                dist[US_R] = prev[US_R];
                ++errCount;
            }
            
        }
        public float dist(int sensor) {
            return dist[sensor];
        }
        public float dDist(int sensor) {
            return dist[sensor] - prev[sensor];
        }
        public int minDistSensor() {
            if (dist[US_L] < dist[US_R])
                return LEFT;
            if (dist[US_L] > dist[US_R])
                return RIGHT;
            return 0;
        }
    }
    
    private static enum STATE {
        EXPLORE,
        TURN_LEFT,
        TURN_RIGHT,
        ROTATE_LEFT,
        ROTATE_RIGHT,
        TURN_AROUND
    }
    
    
    /**
     * Steers herb away from obstacles
     */
    private static class NavigatorThread extends Thread {

        public NavigatorThread() {
            setDaemon(true);
        }
                
        @Override
        public final void run() {
            Sound.twoBeeps();
            Sound.twoBeeps();
            Distances d = new Distances();
            STATE currentstate = STATE.EXPLORE;
            while (true) {
                d.update();
                if (d.dDist(d.EOPD) > EOPD_REVERSE_DIFF){
                    //debug("EOPD tripped");
                    Sound.twoBeeps();
                    currentstate = STATE.TURN_AROUND;
                } else if (d.dist(d.US_R) <= REVERSE_DETECT_DISTANCE
                         || d.dist(d.US_L) <= REVERSE_DETECT_DISTANCE){
                    //debug("US tripped");
                    Sound.beep();
                    switch(d.minDistSensor()) {
                        case (RIGHT):
                            debug("RIGHT US tripped");
                            currentstate = STATE.ROTATE_LEFT;
                            break;
                        case (LEFT):
                            debug("LEFT US tripped");
                            currentstate = STATE.ROTATE_RIGHT;
                            break;
                        default:
                            currentstate = STATE.ROTATE_RIGHT;
                    }
                    
                } else if (d.dist(d.US_R) <= DETECT_DISTANCE
                            || d.dist(d.US_L) <= DETECT_DISTANCE) {
                    switch(d.minDistSensor()) {
                        case (RIGHT):
                            currentstate = STATE.TURN_LEFT;
                            break;
                        case (LEFT):
                            currentstate = STATE.TURN_RIGHT;
                            break;
                        default:
                            //nothing
                    }
                } else {
                    currentstate = STATE.EXPLORE;
                } 
                // Finite-State Machine, Yay!
                switch (currentstate) {
                    case EXPLORE:
                        GYRO_CONTROLLER.backward();
                        break;
                    case TURN_LEFT:
//                        GYRO_CONTROLLER.arc(d.dist(d.US_L)-5, LEFT*ARC_SIZE, true);
                        GYRO_CONTROLLER.arcBackward(RIGHT*REVERSE_DETECT_DISTANCE);
                        break;
                    case TURN_RIGHT:
//                        GYRO_CONTROLLER.arc(d.dist(d.US_R)-5, RIGHT*ARC_SIZE, true);
                        GYRO_CONTROLLER.arcBackward(LEFT*REVERSE_DETECT_DISTANCE);
                        break;
                    case ROTATE_LEFT:
                        sleepFor(PAUSE_DELAY);
                        GYRO_CONTROLLER.travel(REVERSE_DISTANCE, false);
                        sleepFor(PAUSE_DELAY);
                        GYRO_CONTROLLER.rotate(LEFT * 30, true);
                        sleepFor(PAUSE_DELAY);
                        break;
                    case ROTATE_RIGHT:
                        sleepFor(PAUSE_DELAY);
                        GYRO_CONTROLLER.travel(REVERSE_DISTANCE, false);
                        sleepFor(PAUSE_DELAY);
                        GYRO_CONTROLLER.rotate(RIGHT * 30, true);
                        sleepFor(PAUSE_DELAY);
                        break;
                    case TURN_AROUND:
                        GYRO_CONTROLLER.travel(REVERSE_DISTANCE, false);
                        sleepFor(PAUSE_DELAY);
                        GYRO_CONTROLLER.rotate(180, false);
                        sleepFor(PAUSE_DELAY);
                        break;
                        
                }
                sleepFor(SENSOR_DELAY);
            }
        }
    }
    
    public static final class Alarm extends Thread {
        private static int vol = 100;
        public Alarm(int vol) {
            setDaemon(true);
            Alarm.vol = vol;
        }
        @Override
        public void run() {
            while (true) {
                Sound.playTone(4000, 1000, vol);
                Sound.playTone(1000, 1000, vol);
                sleepFor(1000);
                Sound.playTone(2500, 1000, vol);
                Sound.playTone(5500, 1000, vol);
                sleepFor(1000);
            }
        }
    }
    
    public static void main(String[] a) {

        try {
            debug("Initializing...");
            LEFT_MOTOR.setSpeed(SERVO_STRENGTH);
            RIGHT_MOTOR.setSpeed(SERVO_STRENGTH);
            
            GYRO_CONTROLLER = new SegowayPilot(LEFT_ENCODER_MOTOR,
                                               RIGHT_ENCODER_MOTOR,
                                               GYRO_SENSOR,
                                               WHEEL_DIAMETER,
                                               WHEEL_BASE);
            GYRO_CONTROLLER.setTravelSpeed(TRAVEL_SPEED);
            GYRO_CONTROLLER.setMoveDelay(MOVE_DELAY);
            GYRO_CONTROLLER.setRotateSpeed(ROTATE_SPEED);
            sleepFor(BALANCE_TIME);
            NAVIGATOR = new NavigatorThread();
            debug("Initialization Complete.");
            debug("Running...");
            
            NAVIGATOR.start();
            
        } catch (Exception ex) {
            if (DEBUG) {
                String message = ex.getMessage();
                if (message != null) {
                    print(message);
                }
            }
        }
//        Alarm alrm = new Alarm();
//        alrm.start();
        while (GYRO_CONTROLLER.isAlive()) {
            sleepFor(PAUSE_DELAY);
        }
//        alrm.stop();
        debug("Program Completed Successfully.");
        print("Press any key to restart");
        Button.waitForAnyPress();

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
