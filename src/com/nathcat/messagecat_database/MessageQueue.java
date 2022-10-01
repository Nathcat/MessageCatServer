package com.nathcat.messagecat_database;

import com.nathcat.messagecat_database_entities.Message;

import java.io.Serializable;

/**
 * Queue data structure for storing messages.
 *
 * @author Nathan "Nathcat" Baines
 */
public class MessageQueue implements Serializable {
    public final int ChatID;       // The ID of the chat this queue is linked to
    private Message[] data;  // The array of messages in this chat

    /**
     * Default constructor
     * @param ChatID The ID of the chat this queue is to be linked to
     */
    public MessageQueue(int ChatID) {
        this.ChatID = ChatID;
        data = new Message[50];  // Create an array of size 50
    }

    /**
     * Push a new message to the back of the queue
     * @param message The new message
     */
    public void Push(Message message) {
        // Pop an item off the front of the queue
        this.Pop();

        // Assign the new message to the first index of the array
        this.data[0] = message;
    }

    /**
     * Remove the item at the front of the queue
     */
    public void Pop() {
        // Create a copy of the data array, without any references
        Message[] oldData = new Message[50];
        System.arraycopy(this.data, 0, oldData, 0, 50);

        // Copy the old data into the array, with an offset of one index
        this.data = new Message[50];
        System.arraycopy(oldData, 0, this.data, 1, 49);
    }

    /**
     * Get the message at index i from the data array
     * @param i The index to get
     * @return The message object at that index
     */
    public Message Get(int i) {
        return this.data[i];
    }

    /**
     * Return an array of JSON strings for all messages in the queue
     * @return An array of JSON strings for all the messages in the queue
     */
    public String[] GetJSONString() {
        String[] result = new String[50];
        for (int i = 0; i < 50; i++) {
            if (this.data[i] == null) {
                continue;
            }

            result[i] = this.data[i].GetJSONObject().toJSONString();
        }

        return result;
    }

    public Object Clone() throws CloneNotSupportedException {
        return this.clone();
    }
}
