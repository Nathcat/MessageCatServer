package com.nathcat.messagecat_server;

/**
 * Handles a connection queue
 *
 * @author Nathan "Nathcat" Baines
 */
public class QueueManager extends Thread{
    private final Server server;   // The Server object
    public Queue queue;            // The queue assigned to this manager
    private final Handler[] pool;  // The handler pool assigned to this manager

    /**
     * Constructor method
     * @param server The Server object
     * @param queue The queue assigned to this manager
     * @param pool The pool assigned to this manager
     */
    public QueueManager(Server server, Queue queue, Handler[] pool) {
        this.server = server;
        this.queue = queue;
        this.pool = pool;
        // Make this thread a daemon to the program
        // This means that this thread will quit when the program quits
        this.setDaemon(true);
    }

    /**
     * This method will be executed in a separate thread once Thread.start() method is called on this object
     */
    @Override
    public void run() {
        while (true) {
            // Get the object at the front of the queue
            Object frontObj = this.queue.Pop();

            // Check if the front object is null or not
            if (frontObj == null) {
                try {
                    Thread.sleep(1000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                continue;
            }

            // Check if any of the handlers in the queue are not busy
            boolean threadAvailable = false;
            for (int i = 0; i < this.pool.length; i++) {
                // If the current handler is not busy, assign it to the front object
                if (!this.pool[i].busy) {
                    threadAvailable = true;
                    this.pool[i].queueObject = frontObj;

                    synchronized (this.pool[i]) {
                        this.pool[i].notify();
                    }

                    break;
                }
            }

            // If no handler is available, push the object back into the queue
            if (!threadAvailable) {
                this.queue.Push(frontObj);
            }

            try {
                Thread.sleep(1000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
