package com.nathcat.messagecat_server;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Queue data structure implemented using an internal linked list
 *
 * @author Nathan "Nathcat" Baines
 */
public class Queue implements Cloneable, Serializable {
    private Node startNode = null;  // The start node of the linked list
    private int maxLength = -1;     // The maximum length of the queue
    private int length = 0;         // The current length of the queue

    /**
     * Represents a node of the linked list
     */
    private static class Node implements Cloneable, Serializable {
        public final Object data;  // The data contained by the node
        public Node nextNode;      // The next node in the linked list

        public Node(Object data, Node nextNode) {
            this.data = data;
            this.nextNode = nextNode;
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

    public Queue() {}

    public Queue(int maxLength) {
        this.maxLength = maxLength;
    }

    /**
     * Push a new object to the end of the queue
     * @param data The object to push
     */
    public void Push(Object data) {
        if (length >= maxLength && maxLength != -1) {
            this.Pop();
        }

        if (this.startNode == null) {
            this.startNode = new Node(data, null);
            length++;
            return;
        }

        Node currentNode = this.startNode;
        while (currentNode.nextNode != null) {
            currentNode = currentNode.nextNode;
        }

        currentNode.nextNode = new Node(data, null);
        this.length++;
    }

    /**
     * Remove the object from the front of the queue
     * @return The object that was at the front of the queue
     */
    public Object Pop() {
        if (this.startNode == null) {
            return null;
        }

        Object data = this.startNode.data;
        this.startNode = this.startNode.nextNode;
        this.length--;
        return data;
    }

    /**
     * Get an object from a given index
     * @param index The index of the object to get
     * @return The object at the given index
     */
    public Object Get(int index) {
        Node currentNode = this.startNode;
        if (currentNode == null) {
            return null;
        }

        for (int i = 0; i < index; i++) {
            currentNode = currentNode.nextNode;
            if (currentNode == null) {
                return null;
            }
        }

        return currentNode.data;
    }

    @Override
    public String toString() {
        if (this.startNode == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        Node currentNode = this.startNode;
        sb.append(this.startNode.data);
        while (currentNode.nextNode != null) {
            currentNode = currentNode.nextNode;
            sb.append(currentNode.data).append(" ");
        }

        return sb.toString();
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
