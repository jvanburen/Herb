
package api;

import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Provides a base class for programs built for Herb the wonder robot.
 * @author Jacob
 */
public abstract class HerbBase {
    private static final MasterSchedule schedule = new MasterSchedule();
    private static final EventQueue eq = new EventQueue();
    private static final Lock scheduleLock = new ReentrantLock();
    
    public final void addScheduler(api.Event s) {
        scheduleLock.lock();
        try {
            eq.put(new Event(s));
        } finally {
            scheduleLock.unlock();
        }
    }

    public final void removeScheduler(api.Event s) {
        scheduleLock.lock();
        try {
            eq.deleteScheduler(s);
        } finally {
            scheduleLock.unlock();
        }
    }

    private static class Event {
        /** The {@code Event} Object that this event was created from. */
        private final api.Event handler;
        /** The time at which this handler's run method should be called. */ 
        private final long time;

        public Event(api.Event handler) {
            if (handler == null)
                throw new NullPointerException("Cannot pass a null handler");
            this.handler = handler;
            this.time = handler.callTime();
            if (this.time <= 0)
                throw new IllegalStateException("Cannot wait for a non-positive"
                        + " amount of time");
        }

        @Override
        public String toString() {
            return "(" + ((Object)handler).toString() + "@" + time + "ms)";
        }
    }

    /** The master schedule Thread. */
    private static final class MasterSchedule extends Thread {
        private static int threadCount = 0;
        private static ThreadGroup handlerThreads = new ThreadGroup("Handlers");
        
        /** Only one instance of this class should ever exist. */
        private MasterSchedule() {
            setDaemon(true);
            this.setPriority(Thread.MIN_PRIORITY);
            start();
        }
        
        /** Gets a new handler Thread name. */
        private static String threadName() {
            return "Handler Thread #" + threadCount++;
        }

        /** Processes events added to the EventQueue. */
        @Override
        public final void run() {
            while(true) {
                Event e = eq.min();
                boolean onTime;
                synchronized (eq) {
                    try {
                        long waitTime = e.time - System.currentTimeMillis();
                        if (waitTime <= 0) {
                            onTime = false;
                            // Don't allow interruptions because we're
                            // already late
                            eq.arrayLock.lock();

                        } else {
                            eq.wait(waitTime);
                            if (!eq.arrayLock.tryLock()) {
                                eq.arrayLock.lockInterruptibly();
                                onTime = false;
                            } else {
                                onTime = true;
                            }
                        }
                        eq.processLockedMin();
                        if (!e.handler.skipLate() || onTime) {
                            if (api.Event.BLOCKING)
                                e.handler.run();
                            else
                                new Thread(handlerThreads,
                                        e.handler, threadName()).start();
                        }
                    } catch (InterruptedException ex) {
                        // Queue has been modified, check again
                        continue;
                    }
                }
            }
        }
        
    }

    private static final class EventQueue {
        /** The initial size of the array. */
        public static final int INITIAL_SIZE = 9;
        /** Stores references at indices 1 to N. */
        private volatile AtomicReferenceArray<Event> pq
                = new AtomicReferenceArray<>(INITIAL_SIZE);
        /** The number of events in the queue. */
        private volatile int N = 0;

        /** 
         * A lock to prevent concurrent access to the array while the invariant
         * is not preserved.
         */
        private volatile Lock arrayLock = new ReentrantLock();

        /** Adds a new Event to the queue. */
        private void insert(Event e) {
            // double size of array if necessary
            if (N == pq.length() - 1)
                resize(pq.length() << 1);

            pq.set(++N, e);
            swim(N);
        }

        
        /**
         * Gets the event at the front of the queue.
         * If no event is available, this waits for one to become available
         * @return the Event at the front of the queue.
         */
        public Event get() {
            synchronized (this) {
                while (N == 0) {
                    try {
                        wait();
                    } catch (InterruptedException ex) {
                        // acquire the lock and check again
                        arrayLock.lock();
                        if (N <= 0) {
                            arrayLock.unlock();
                            continue;
                        } else {
                            break;
                        }
                    }
                }
            }
            
            try {
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
        
        /** 
         * "Processes" (i.e., pops the first element and inserts the next
         * event).
         * Precondition: arrayLock is held by the calling thread.
         * This method will not check the lock status
         */
        private void processLockedMin() {
            //exch(1, N--);
            exch(1, N);
            Event min = pq.getAndSet(N--, null);
            sink(1);
            // insert the next event

            insert(new Event(min.handler));
        }

        /** Puts an event at the front of the queue. */
        public void put(Event e) {
            arrayLock.lock();
            try {
                insert(e);
            } finally {
                arrayLock.unlock();
                synchronized (this) {
                    notify();
                }
            }
        }

        /** Gets the Event at the front of the queue. */
        private Event min() {
            synchronized (this) {
                while (N == 0)
                    try {
                        wait();
                    } catch (InterruptedException ex)
                        { /* Do Nothing */ }
            }
            arrayLock.lock();
            try {
                return pq.get(1);
            } finally {
                arrayLock.unlock();
            }
        }

        public void deleteScheduler(api.Event s) {
            arrayLock.lock();
            try {
                for (int i = 1; i < N; ++i) {
                    if (pq.get(i).handler == s) {
                        exch(N--, i);
                        sink(i);
                        swim(i);
                        pq.set(N + 1, null);
                        return;
                    }
                }
                if (pq.get(N).handler == s)
                    pq.set(N--, null);
                else
                    throw new IllegalArgumentException("Scheduler not found");
            } finally {
                arrayLock.unlock();
                synchronized (this) {
                    notifyAll();
                }
            }
        }

        /** helper function to double the size of the heap array */
        private void resize(int capacity) {
             AtomicReferenceArray<Event> temp
                = new AtomicReferenceArray<>(capacity);
            for (int i = 1; i <= N; i++)
                temp.set(i, pq.get(i));
            pq = temp;
        }

        /** Helper function to restore the heap invariant. */
        private void swim(int k) {
            while (k > 1 && greater(k >>> 1, k)) {
                exch(k, k >>> 1);
                k >>>= 1;
            }
        }

        /** Helper function to restore the heap invariant. */
        private void sink(int k) {
            while (k << 1 <= N) {
                int j = k << 1;
                if (j < N && greater(j, j+1)) j++;
                if (!greater(k, j)) break;
                exch(k, j);
                k = j;
            }
        }

        /** Helper function for compares. */
        private boolean greater(int i, int j) {
            return pq.get(i).time > pq.get(j).time;
        }

        /** Helper function for swaps. */
        private void exch(int i, int j) {
            pq.set(i, pq.getAndSet(j, pq.get(i)));
        }

        @Override
        public String toString() {
            arrayLock.lock();
            try {
                return pq.toString();
            } finally {
                arrayLock.unlock();
            }
        }
    }

}
