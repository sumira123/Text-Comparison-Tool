package App.com;

import java.util.concurrent.BlockingQueue;

public class PoolThreadRunnable {


    private Thread thread = null;
    private BlockingQueue taskQueue = null;
    private boolean isStopped = false;

    public PoolThreadRunnable(BlockingQueue queue) {
        taskQueue = queue;
    }

    public void run() {
        this.thread = Thread.currentThread();
        while (!isStopped()) {
            try {
                //https://stackoverflow.com/questions/56406481/how-to-change-a-threads-runnable-target-during-runtime
                Runnable runnable = (Runnable) taskQueue.take();
                runnable.run();
            } catch (Exception e) {
                //System.err.println(e.getMessage());
            }
        }
    }

    /**
     * stopping thread
     */
    public synchronized void doStop() {
        isStopped = true;
        // break pool thread out of dequeue() call.
        this.thread.interrupt();
    }

    public synchronized boolean isStopped() {
        return isStopped;
    }
}
