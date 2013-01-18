/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import lejos.nxt.Motor;
/**
 *
 * @author Jacob Van Buren
 */
public class RHerb {
    public static final int MOVE_SPEED = 720;
    
    public static void main(String[] args) {
        Motor.A.backward();
        Motor.B.backward();
        while(true);
    }
}
