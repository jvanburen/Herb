package herb;

/**
 *
 * @author Jacob Van Buren
 * 
 * usage:
 * compilation:
 *     > nxjc ComputerRobotics.java
 * linking/uploading:
 *     connect NXT first!
 *     > nxj ComputerRobotics
 */

import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.addon.GyroSensor;
import lejos.nxt.addon.OpticalDistanceSensor;
import lejos.robotics.navigation.DifferentialPilot;

public class ComputerRobotics {

    public static final int LOCK_STRENGTH = 350;
    public static final int MOVE_SPEED = 720;
    public static final int ARM_SPEED = 900;
    public static final UltrasonicSensor us  = new UltrasonicSensor(SensorPort.S1);
    public static final OpticalDistanceSensor eopd 
            = new OpticalDistanceSensor(SensorPort.S1);
    public static final GyroSensor gyro = new GyroSensor(SensorPort.S2);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        init(false);
        //System.out.println("Begin Testing...");
        //moveForward(3);
//        try {Thread.sleep(333);}
//        catch (InterruptedException ex) {}
        DifferentialPilot myNXT = new DifferentialPilot(1, 10,Motor.B, Motor.A);
        //myNXT.rotateRight();
        us.capture();
        us.continuous();
        US_THREAD usSensor = new US_THREAD();
        usSensor.start();
        myNXT.rotateRight();
        for(;;){
            try {Thread.sleep(333);}
            catch (InterruptedException ex) {}
            System.out.println("NYAHH!!!");
            try {
                //beepLoudly();
                Thread.sleep(5000);
            } catch (InterruptedException ex) { }
            swingArm();
            
       
        }
        
        //moveForward(6);
        //System.out.println("Ending test!");
//        try {Thread.sleep(2000);}
//        catch (InterruptedException ex) {}
    }
    private static class US_THREAD extends Thread implements Runnable{

        @Override
        public void run() {
            for(;;){
                try{
                    Thread.sleep(200);
                } catch (InterruptedException ex) {}
                int distance = us.getDistance();
                if ( distance<29)
                    swingArm();
                else if (distance<4){
                    Motor.C.setSpeed(900);
                    swingArm();
                    Motor.C.setSpeed(ARM_SPEED);
                }
            }
        }
    }
    
    public static boolean objectSensed(){
        return us.getDistance()<29;
    }
    public static void init(boolean printDebug){
        if (printDebug) System.out.println("Initializing");
        Motor.A.setSpeed(MOVE_SPEED);
        Motor.B.setSpeed(MOVE_SPEED);
        Motor.C.setSpeed(ARM_SPEED);
        Motor.A.lock(LOCK_STRENGTH);
        Motor.B.lock(LOCK_STRENGTH);
        Motor.C.lock(LOCK_STRENGTH);
        if (printDebug) System.out.println("Initialization Complete!");
    }
    public static void moveForward(double rotations){
        int degrees = (int) (rotations*360);
        Motor.A.resetTachoCount();
        Motor.B.resetTachoCount();
        Motor.A.forward();
        Motor.B.forward();
        boolean stopA=false, stopB=false;
        while(!stopA || !stopB){
            if (!stopA && Motor.A.getTachoCount()>=degrees){
                Motor.A.stop();
                Motor.A.lock(LOCK_STRENGTH);
                stopA = true;
            }

            if (Motor.B.getTachoCount()>=degrees){
                Motor.B.stop();
                Motor.B.lock(LOCK_STRENGTH);
                stopB = true;
            }

                //stop and lock
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {}
        }
//        Motor.A.stop();
//        Motor.A.lock(LOCK_STRENGTH);
//        Motor.B.stop();
//        Motor.B.lock(LOCK_STRENGTH);
        //Motor.A.rotateTo((int)(360*rotations));
        //Motor.B.rotateTo((int)(360*rotations));


    }
    /**
     * beeps loudly`
     */
    public static void beepLoudly() throws InterruptedException{
        Sound.setVolume((int)(Sound.VOL_MAX*0.7));
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
        int q = 400/3;
        int h = 800/3;
        
        Sound.playTone(f,h);
        Thread.sleep(h);

        Sound.playTone(g,h);
        Thread.sleep(h);

        Sound.playTone(c1,q);
        Thread.sleep(q);

        Sound.playTone(d,h);
        Thread.sleep(h);

        Sound.playTone(c,q);
        Thread.sleep(q);

        Sound.playTone (c1,q);
        Thread.sleep(q);

        Sound.playTone(c,q);
        Thread.sleep(q);

        Sound.playTone(a1,h);
        Thread.sleep(h);

        Sound.playTone(a1,h);
        Thread.sleep(h);

        Sound.playTone(c,h);
        Thread.sleep(h);

        Sound.playTone(c1,h);
        Thread.sleep(h);

        Sound.playTone(c1,q);
        Thread.sleep(q);

        Sound.playTone(c,q);
        Thread.sleep(q);

        Sound.playTone(a1,q);
        Thread.sleep(q);

        Sound.playTone(c,q);
        Thread.sleep(q);

        Sound.playTone(d,q);
        Thread.sleep(q);

        Sound.playTone(f,q);
        Thread.sleep(q);

        Sound.playTone(g,q);
        Thread.sleep(q);

        Sound.playTone(d,q);
        Thread.sleep(q);

        Sound.playTone(f,q);
        Thread.sleep(q);

        Sound.playTone(c,q);
        Thread.sleep(q);

        Sound.playTone(d,q);
        Thread.sleep(q);
        

        Sound.playTone(a1,q);
        Thread.sleep(q);

        Sound.playTone(c,q);
        Thread.sleep(q);

        Sound.playTone(a1,q);
        Thread.sleep(q);

        Sound.playTone(d,h);
        Thread.sleep(h);

        Sound.playTone(f,h);
        Thread.sleep(h);

        Sound.playTone(g,q);
        Thread.sleep(q);

        Sound.playTone(d,q);
        Thread.sleep(q);

        Sound.playTone(f,q);
        Thread.sleep(q);

        Sound.playTone(c,q);
        Thread.sleep(q);

        Sound.playTone(d,q);
        Thread.sleep(q);

        Sound.playTone(a1,q);
        Thread.sleep(q);

        Sound.playTone(c1,q);
        Thread.sleep(q);

        Sound.playTone(d,q);
        Thread.sleep(q);

        Sound.playTone(c1,q);
        Thread.sleep(q);

        Sound.playTone(c,q);
        Thread.sleep(q);
        
        Sound.playTone(a1,q);
        Thread.sleep(q);

        Sound.playTone(c,q);
        Thread.sleep(q);
////////////////////////////////
        Sound.playTone(d,h);
        Thread.sleep(q);

        Sound.playTone(a1,q);
        Thread.sleep(q);

        Sound.playTone(c,q);
        Thread.sleep(q);
        
        Sound.playTone(d,q);
        Thread.sleep(q);

        Sound.playTone(c,q);
        Thread.sleep(q);

        Sound.playTone(c1,q);
        Thread.sleep(q);
        
        Sound.playTone(c,q);
        Thread.sleep(q);

        Sound.playTone(c1,q);
        Thread.sleep(q);

        Sound.playTone(c,h);
        Thread.sleep(q);
        
        Sound.playTone(a1,h);
        Thread.sleep(q);

        Sound.playTone(c,h);
        Thread.sleep(q);

        Sound.playTone(c,q);
        Thread.sleep(q);
        
        Sound.playTone(c,h);
        Thread.sleep(h);
        //
    }
    public static void swingArm(){
        
        
        Motor.C.setSpeed(ARM_SPEED);
        Motor.C.rotateTo(360);
        
        
        Motor.C.rotateTo(0);
        Motor.C.stop();
        Motor.C.lock(LOCK_STRENGTH);
    }
    ////sound stuff
    
}
