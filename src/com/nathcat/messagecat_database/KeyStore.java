package com.nathcat.messagecat_database;

import com.nathcat.RSA.*;

import java.io.*;
import java.util.HashMap;

/**
 * Stores Public and Private encryption keys
 *
 * @author Nathan "Nathcat" Baines
 */
public class KeyStore {
    private HashMap<Integer, KeyPair> data;

    /**
     * Default constructor
     */
    public KeyStore() throws IOException {
        try {
            // Try to read the data file
            data = this.ReadFromFile();

        } catch (FileNotFoundException e) {  // Thrown if the file does not exist
            // Create a new empty hash map and create a new file for it
            data = new HashMap<Integer, KeyPair>();
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
    public HashMap<Integer, KeyPair> ReadFromFile() throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("Assets/Data/KeyStore.bin"));
        return (HashMap<Integer, KeyPair>) ois.readObject();
    }

    /**
     * Write data to data file
     * @throws IOException Can be thrown by I/O operations
     */
    public void WriteToFile() throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("Assets/Data/KeyStore.bin"));
        oos.writeObject(data);
    }

    /**
     * Get a key pair object given its identifier
     * @param keyID The identifier to search for
     * @return The KeyPair object found, note that this KeyPair con contain one or both key, depending on the use case
     */
    public KeyPair GetKeyPair(int keyID) {
        return this.data.get(keyID);
    }

    /**
     * Add a new key pair, using the hash code of the KeyPair object as the id
     * @param pair The KeyPair to add
     * @return The result code
     */
    public Result AddKeyPair(KeyPair pair) {
        // Create a copy of the original data
        Object oldData = this.data.clone();

        // Make the changes to the hash map
        this.data.put(pair.hashCode(), pair);

        // Try to write the changes to the data file, or revert to the original state
        try {
            this.WriteToFile();
            return Result.SUCCESS;

        } catch (IOException e) {
            this.data = (HashMap<Integer, KeyPair>) oldData;
            return Result.FAILED;
        }
    }

    /**
     * Remove a key pair
     * @param keyID The ID of the KeyPair object
     * @return The result code
     */
    public Result RemoveKeyPair(int keyID) {
        // Create a copy of the original data
        Object oldData = this.data.clone();

        // Make the changes to the hash map
        this.data.remove(keyID);

        // Try to write the changes to the data file, or revert to the original state
        try {
            this.WriteToFile();
            return Result.SUCCESS;

        } catch (IOException e) {
            this.data = (HashMap<Integer, KeyPair>) oldData;
            return Result.FAILED;
        }
    }
}
