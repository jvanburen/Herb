
package api;

/**
 *
 * @author Jacob
 */
public interface Schedule {
    /**
     * Adds a Scheduler to this Schedule.
     * @param s The scheduler to add.
     */
    public void addScheduler(Scheduler s);
    /**
     * Removes a Scheduler from this Schedule.
     * @param s The scheduler to remove.
     */
    public void removeScheduler(Scheduler s);
}
