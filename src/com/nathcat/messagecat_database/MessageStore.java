package com.nathcat.messagecat_database;

import java.io.*;
import java.util.HashMap;

/**
 * This class will handle messages.
 *
 * @author Nathan "Nathcat" Baines
 */
public class MessageStore {
    private HashMap<Integer, MessageQueue> data = null;  // Keys are the chat ids, and the values are the message queues

    /**
     * Default constructor
     */
    public MessageStore() throws IOException {
        try {
            // Try to read the data file
            data = this.ReadFromFile();

        } catch (FileNotFoundException e) {  // Thrown if the file does not exist
            // Create a new empty hash map and create a new file for it
            data = new HashMap<Integer, MessageQueue>();
            this.WriteToFile();

        } catch (IOException | ClassNotFoundException e) {  // Potentially thrown by I/O operations
            e.printStackTrace();
        }

        assert this.data != null;
    }

    /**
     * Read data from data file
     * @return The HashMap found in the data file
     * @throws IOException Can be thrown by I/O operations
     * @throws ClassNotFoundException Thrown if the Serialized class cannot be found
     */
    public HashMap<Integer, MessageQueue> ReadFromFile() throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("Assets/Data/MessageStore.bin"));
        return (HashMap<Integer, MessageQueue>) ois.readObject();
    }

    /**
     * Write data to data file
     * @throws IOException Can be thrown by I/O operations
     */
    public void WriteToFile() throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("Assets/Data/MessageStore.bin"));
        oos.writeObject(data);
    }

    /**
     * Get a message queue
     * @param ChatID The ChatID of the chat for which you are retrieve the message queue
     * @return The MessageQueue
     */
    public MessageQueue GetMessageQueue(int ChatID) {
        return this.data.get(ChatID);
    }

    /**
     * Add a message queue
     * @param queue The message queue to add
     * @return Result code
     */
    public Result AddMessageQueue(MessageQueue queue) {
        // Create a copy of the original data
        Object dataCopy = this.data.clone();
        // Add the new queue
        this.data.put(queue.ChatID, queue);

        // Try to write the new data to the data file
        try {
            this.WriteToFile();
            return Result.SUCCESS;

        } catch (IOException e) {
            // Revert the data to its original form
            e.printStackTrace();
            this.data = (HashMap<Integer, MessageQueue>) dataCopy;
            return Result.FAILED;
        }
    }

    /**
     * Remove a message queue
     * @param ChatID The ID of the chat whose message queue you are trying to delete
     * @return Result code
     */
    public Result RemoveMessageQueue(int ChatID) {
        // Create a copy of the original data
        Object dataCopy = this.data.clone();
        // Add the new queue
        this.data.remove(ChatID);

        // Try to write the new data to the data file
        try {
            this.WriteToFile();
            return Result.SUCCESS;

        } catch (IOException e) {
            // Revert the data to its original form
            e.printStackTrace();
            this.data = (HashMap<Integer, MessageQueue>) dataCopy;
            return Result.FAILED;
        }
    }
}
