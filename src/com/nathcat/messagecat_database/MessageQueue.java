package com.nathcat.messagecat_database;

import com.nathcat.messagecat_database_entities.Message;
import com.nathcat.messagecat_server.Queue;

import java.io.Serializable;

/**
 * Queue data structure for storing messages.
 *
 * @author Nathan "Nathcat" Baines
 */
public class MessageQueue implements Serializable {
    public final int ChatID;       // The ID of the chat this queue is linked to
    private Queue data;            // The Queue which will be used to store data

    /**
     * Default constructor
     * @param ChatID The ID of the chat this queue is to be linked to
     */
    public MessageQueue(int ChatID) {
        this.ChatID = ChatID;
        data = new Queue(10);
    }

    /**
     * Push a new message to the back of the queue
     * @param message The new message
     */
    public void Push(Message message) {
        this.data.Push(message);
    }

    /**
     * Remove the item at the front of the queue
     */
    public void Pop() {
        this.data.Pop();
    }

    /**
     * Get the message at index i from the data array
     * @param i The index to get
     * @return The message object at that index
     */
    public Message Get(int i) {
        return (Message) this.data.Get(i);
    }

    /**
     * Return an array of JSON strings for all messages in the queue
     * @return An array of JSON strings for all the messages in the queue
     */
    public String[] GetJSONString() {
        String[] result = new String[50];
        for (int i = 0; i < 50; i++) {
            if (this.data.Get(i) == null) {
                continue;
            }

            result[i] = ((Message) this.data.Get(i)).GetJSONObject().toJSONString();
        }

        return result;
    }

    public Object Clone() throws CloneNotSupportedException {
        return this.clone();
    }
}
