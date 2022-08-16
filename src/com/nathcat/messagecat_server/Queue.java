package com.nathcat.messagecat_server;

import java.util.Arrays;

/**
 * Queue data structure
 *
 * @author Nathan "Nathcat" Baines
 */
public class Queue implements Cloneable {
    private final int maxCapacity;  // The maximum capacity of the queue
    private Object[] data;          // The data array
    public boolean locked = false;  // Determines whether new objects can be pushed to the queue

    /**
     * Constructor method
     * @param maxCapacity The maximum capacity of the queue
     */
    public Queue(int maxCapacity) {
        this.maxCapacity = maxCapacity;
        data = new Object[this.maxCapacity];
    }

    public String toString() {
        return Arrays.toString(this.data);
    }

    /**
     * Pop an object off the front of the queue
     * @return The item that was just removed from the queue
     */
    public Object Pop() {
        Object lastItem = this.data[this.maxCapacity - 1];

        // The item at the front of the queue will be at the end of the array
        // Create a copy of the data array
        Object[] oldData = new Object[this.maxCapacity];
        System.arraycopy(this.data, 0, oldData, 0, this.maxCapacity);

        this.data = new Object[this.maxCapacity];

        // Copy the copy back into the data array, but with an offset of one index
        // This will remove the item at the end of the array, i.e. the front of the queue
        System.arraycopy(oldData, 0, this.data, 1, this.maxCapacity - 1);

        return lastItem;
    }

    /**
     * Push an object to the end of the queue
     * @param obj The item to add
     */
    public void Push(Object obj) throws QueueIsFullException, QueueIsLockedException {
        if (this.locked) {
            throw new QueueIsLockedException();
        }

        // First determine the index of the end of the queue, i.e. the index of the first non-null item in the data array
        int indexOfEnd = -1;
        for (int i = 0; i < this.maxCapacity; i++) {
            if (this.data[i] != null) {
                indexOfEnd = i;
                break;
            }
        }

        if (indexOfEnd == -1) {  // The queue is empty in this case
            indexOfEnd = this.maxCapacity;
        }
        else if (indexOfEnd == 0) {  // The queue is full in this case
            throw new QueueIsFullException();
        }

        // Set the last null item to the new object
        this.data[indexOfEnd - 1] = obj;
    }

    /**
     * Create an identical copy of this object
     * @return The clone of this object
     */
    @Override
    public Object clone() {
        try {
            return super.clone();

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return null;
    }
}
