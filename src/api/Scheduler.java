
package api;

/**
 * Denotes a class that can be scheduled using HerbBase's Event Scheduling
 * capabilities.
 * @author Jacob
 */
public interface Scheduler extends Runnable {
    public static final boolean BLOCKING = true;
    /**
     * At what time {@code run} is called again
     * @return 
     */
    public long callTime();
    
    /**
     * If this method returns true, the event scheduler will forget this
     * instance the next time it processes an event for it.
     */
    public boolean forget();
    
    /**
     * return True if the master schedule should skip calling {@code run} if it
     * won't be executed on time.
     */
    public boolean skipLate();
    
    /**
     * The method that's called when a scheduled event is ready.
     */
    @Override
    public void run();
    
}
