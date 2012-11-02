
package api;

import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Provides a base class for programs built for Herb the wonder robot.
 * @author Jacob
 */
public abstract class HerbBase {
    private static final MasterSchedule schedule = MasterSchedule.getInstance();
    public final void addScheduler(Scheduler s) {
        EventQueue.insert(new Event(s));
        System.out.println("interrupting");
        System.out.flush();
        schedule.interrupt();
        for (int i = 0; i < 10000000; ++i);
        System.out.println("interrupting again");
        System.out.flush();
        schedule.interrupt();
    }

    public final void removeScheduler(Scheduler s) {
        EventQueue.deleteScheduler(s);
        schedule.interrupt();
    }

    private static class Event {
        private final Scheduler handler;
        private final long time;

        Event(Scheduler handler) {
            this.handler = handler;
            this.time = System.currentTimeMillis() + handler.refreshTime();
        }

        @Override
        public String toString() {
            return "(" + ((Object)handler).toString() + "@" + time + "ms)";
        }
    }

    /**
     * The master schedule Thread.
     */
    private static final class MasterSchedule extends Thread {
        private static final class MasterScheduleHolder {
            private static final MasterSchedule instance
                    = new MasterSchedule();
        }
        /** Only one instance of this class should ever exist */
        private MasterSchedule() {
            setDaemon(true);
            this.setPriority(Thread.MIN_PRIORITY);
            start();
        }

        public static MasterSchedule getInstance() {
            return MasterScheduleHolder.instance;
        }

        @Override
        public final void run() {
            while(true) {
                try {
                    Event e = EventQueue.get();
                }
            }
            while (true) {
                Event e;
                long timestamp = EventQueue.timestamp();
                boolean onTime = false; 
                try {
                    if (EventQueue.N == 0) {
                        // wait until modified
                        System.out.println("Waiting a while");
                        this.wait();
                    } else if (EventQueue.min().time
                            < System.currentTimeMillis()) {

                        System.out.println("Waiting some time");
                        try {
                            this.wait(EventQueue.min().time - System.currentTimeMillis());
                            onTime = true;
                        } catch (IllegalArgumentException ex) {
                            onTime = false;
                        }

                    }
                    // Otherwise the next event is late (probably due to a
                    // blocking handler taking too long or a schedule conflict)
                    e = EventQueue.delMinIfUnmodified(timestamp);
                    if (!e.handler.skipLate() || onTime) {
                        try {
                            if (Scheduler.BLOCKING)
                                e.handler.run();
                            else
                                new Thread(e.handler).start();
                        } catch (Exception ex) {
                            throw new RuntimeException(
                                    "An unexpexted Exception was thrown in "
                                    + "the execution of a handler. ("
                                    + ex.getMessage() + ")");
                        }
                    }

                } catch ( InterruptedException | IllegalStateException ex) {
                    // If something else happens while this is sleeping, the
                    // event queue has been changed and this has to update its
                    // variables.
                    continue;
                }
            }
        }
    }

    private static final class EventQueue {
        // store items at indices 1 to N
        public static final int INITIAL_SIZE = 9;
        private static volatile AtomicReferenceArray<Event> pq
                = new AtomicReferenceArray<>(INITIAL_SIZE);
        private static volatile int N = 0; // number of events in queue
        private static volatile int modifications = 0;
        private static volatile Lock arrayLock = new ReentrantLock();

        /** Adds a new Event to the queue. */
        private static void insert(Event e) {
            ++modifications;
            // double size of array if necessary
            if (N == pq.length() - 1)
                resize(pq.length() << 1);

            pq.set(++N, e);
            swim(N);
        }

        /** Deletes and returns the first event on the queue. */
        public static Event delMinIfUnmodified(long timestamp) {
            if (timestamp != modifications)
                throw new IllegalStateException(
                        "The Event Queue has been modified");
            ++modifications;
            //exch(1, N--);
            exch(1, N);
            Event min = pq.get(N--);
            sink(1);
            // avoid loitering and help with garbage collection
            pq.set(N + 1, null);
            if ((N > 0) && (N == (pq.length() - 1) >>> 2))
                resize(pq.length() >>> 1);
            return min;
        }

        public static Event get() {
            if (N==0)

            while (N == 0)
                try {
                    Thread.currentThread().wait();
                } catch (InterruptedException ex) {
                    
                }
            arrayLock.lock();
            try {
                ++modifications;
                //exch(1, N--);
                exch(1, N);
                Event min = pq.getAndSet(N--, null);
                sink(1);
                // avoid loitering and help with garbage collection
                //pq.set(N + 1, null);
                if ((N > 0) && (N == (pq.length() - 1) >>> 2))
                    resize(pq.length() >>> 1);
                return min;
            } finally {
                arrayLock.unlock();
            }
        }

        public static void put(Event e) {
            arrayLock.lock();
            try {
                insert(e);
            } finally {
                arrayLock.unlock();
            }
        }

        /** Gets the Event at the front of the queue. */
        private static Event min() {
            return pq.get(1);
        }

        public static void deleteScheduler(Scheduler s) {
            arrayLock.lock();
            try {
                for (int i = 1; i < N; ++i) {
                    if (pq.get(i).handler == s) {
                        exch(N--, i);
                        sink(i);
                        swim(i);
                        pq.set(N + 1, null);
                        ++modifications;
                        return;
                    }
                }
                if (pq.get(N).handler == s)
                    pq.set(N--, null);
                else
                    throw new IllegalArgumentException("Scheduler not found");
                ++modifications;
            } finally {
                arrayLock.unlock();
            }
        }

        /** helper function to double the size of the heap array */
        private static void resize(int capacity) {
             AtomicReferenceArray<Event> temp
                = new AtomicReferenceArray<>(capacity);
            for (int i = 1; i <= N; i++)
                temp.set(i, pq.get(i));
            pq = temp;
        }

        /** Helper function to restore the heap invariant. */
        private static void swim(int k) {
            while (k > 1 && greater(k >>> 1, k)) {
                exch(k, k >>> 1);
                k >>>= 1;
            }
        }

        /** Helper function to restore the heap invariant. */
        private static void sink(int k) {
            while (k << 1 <= N) {
                int j = k << 1;
                if (j < N && greater(j, j+1)) j++;
                if (!greater(k, j)) break;
                exch(k, j);
                k = j;
            }
        }

        /** Helper function for compares. */
        private static boolean greater(int i, int j) {
            return pq.get(i).time > pq.get(j).time;
        }

        /** Helper function for swaps. */
        private static void exch(int i, int j) {
            pq.set(i, pq.getAndSet(j, pq.get(i)));
        }

        public static long timestamp() {
            arrayLock.lock();
            try {
                return modifications;
            } finally {
                arrayLock.unlock();
            }
        }

        public static boolean modifiedSince(long timestamp) {
            arrayLock.lock();
            try {
                return modifications != timestamp;
            } finally {
                arrayLock.unlock();
            }
        }

        public static String toString(Object nothing) {
            arrayLock.lock();
            try {
                return pq.toString();
            } finally {
                arrayLock.unlock();
            }
        }
    }

}
