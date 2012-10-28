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

public class NyanTest{

    public static final int LOCK_STRENGTH = 200;
    public static final int MOVE_SPEED = 720;
    public static final int ARM_SPEED = 500;
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        init(true);
        //System.out.println("Begin Testing...");
        //moveForward(3);
//        try {Thread.sleep(333);}
//        catch (InterruptedException ex) {}
        Sound.beepSequenceUp();
        Sound.beepSequence();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            
        }
        for(;;){
            try {
                beepLoudly();
            } catch (InterruptedException ex) {
                
            }
            try {Thread.sleep(333);}
            catch (InterruptedException ex) {}
            
            
       
        }
        //moveForward(6);
        //System.out.println("Ending test!");
//        try {Thread.sleep(2000);}
//        catch (InterruptedException ex) {}
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
 
    /**
     * beeps loudly`
     */
    public static void beepLoudly() throws InterruptedException{
        //Sound.setVolume((int)(Sound.VOL_MAX*0.7));
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
        //
    }
}