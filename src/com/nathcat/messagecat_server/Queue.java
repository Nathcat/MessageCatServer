package com.nathcat.messagecat_server;

import java.util.Arrays;

/**
 * Queue data structure implemented using an internal linked list
 *
 * @author Nathan "Nathcat" Baines
 */
public class Queue implements Cloneable {
    private Node startNode = null;  // The start node of the linked list

    /**
     * Represents a node of the linked list
     */
    private static class Node implements Cloneable {
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

    /**
     * Push a new object to the end of the queue
     * @param data The object to push
     */
    public void Push(Object data) {
        if (this.startNode == null) {
            this.startNode = new Node(data, null);
            return;
        }

        Node currentNode = this.startNode;
        while (currentNode.nextNode != null) {
            currentNode = currentNode.nextNode;
        }

        currentNode.nextNode = new Node(data, null);
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
        return data;
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
