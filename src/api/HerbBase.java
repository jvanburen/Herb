
package api;

/**
 * Provides a base class for programs built for Herb the wonder robot.
 * @author Jacob
 */
public abstract class HerbBase {
    private static final MasterSchedule schedule = new MasterSchedule();
    public void addScheduler(Scheduler s) {
        schedule.insert(new MasterSchedule.Event(s));
        schedule.interrupt();
    }
    
    public void removeScheduler(Scheduler s) {
        schedule.deleteScheduler(s);
        schedule.interrupt();
    }
    
    /**
     * The master schedule Thread.
     */
    private static class MasterSchedule extends Thread {
        
        private static class Event {
            private final Scheduler handler;
            private final long time;
            
            Event(Scheduler handler) {
                this.handler = handler;
                this.time = System.currentTimeMillis() + handler.refreshTime();
            }
        }
        
        /** Only one instance of this class should ever exist */
        private MasterSchedule() {
            setDaemon(true);
            start();
        }
        
        @Override
        public synchronized final void run() {
            while (true) {
                if (min() == null) {
                    try {
                        this.wait();
                    } catch (InterruptedException ex) {
                        
                    }
                    continue;
                }
                Event e = min();
                try {
                    this.wait(e.time - System.currentTimeMillis());
                    delMin();
                } catch (InterruptedException ex) {
                    
                }
             }
        }
        
        // store items at indices 1 to N
        private Event[] pq = (Event[]) new Object[9];
        private int N = 0; // number of items on priority queue

        /** Adds a new Event to the queue. */
        public synchronized void insert(Event e) {
            // double size of array if necessary
            if (N == pq.length - 1)
                resize(pq.length << 1);

            pq[++N] = e;
            swim(N);
        }

        /** Deletes and return the smallest key on the priority queue. */
        public synchronized void delMin() {
            exch(1, N--);
            // exch(1, N);
            // Event min = pq[N--];
            sink(1);
            pq[N+1] = null; // avoid loitering and help with garbage collection
            if ((N > 0) && (N == (pq.length - 1) >>> 2))
                resize(pq.length  >>> 1);

        }

        /** Gets the Event at the front of the queue. */
        public synchronized Event min() {
            return pq[N];
        }

        public synchronized void deleteScheduler(Scheduler s) {
            for (int i = 1; i < N; ++i) {
                if (pq[i].handler == s) {
                    exch(N--, i);
                    sink(i);
                    swim(i);
                    pq[N+1] = null;
                    return;
                }
            }
            if (pq[N].handler == s)
                pq[N--] = null;
            else
                throw new IllegalArgumentException("Scheduler not found");
        }
        
        // helper function to double the size of the heap array
        private void resize(int capacity) {
            Event[] temp = (Event[]) new Object[capacity];
            for (int i = 1; i <= N; i++)
                temp[i] = pq[i];
            pq = temp;
        }

        /** Helper function to restore the heap invariant. */
        private void swim(int k) {
            while (k > 1 && greater(k/2, k)) {
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
            return pq[i].time > pq[j].time;
        }

        /** Helper function for swaps. */
        private void exch(int i, int j) {
            Event swap = pq[i];
            pq[i] = pq[j];
            pq[j] = swap;
        }
    }
    

}



