
package api;

/**
 * Denotes a class that can be scheduled using HerbBase's Event Scheduling
 * capabilities.
 * @author Jacob
 */
public interface Scheduler extends Runnable {
    static final Schedule schedule = HerbBase.getSchedule();
    long refreshTime = 100;
    /**
     * How long to wait until {@code process} is called again
     * @return 
     */
    public long refreshTime();
    
    /**
     * If this method returns true, the event scheduler will forget this
     * instance the next time it processes an event for it.
     */
    public boolean forget();
    
    /**
     * The method that's called when a scheduled event is ready.
     */
    @Override
    public void run();
    
}
