package com.nathcat.messagecat_server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.net.*;

import com.nathcat.RSA.*;

/**
 * Parent class for the three handler classes
 *
 * @author Nathan "Nathcat" Baines
 */
public class Handler extends Thread {
    public Socket socket;                  // The TCP/IP connection socket
    public ObjectOutputStream oos;         // Stream to output objects to through the socket
    public ObjectInputStream ois;          // Stream to receive objects from the socket
    private final int threadNum;           // The thread number, used in debug messages
    private final String className;        // The name of the class to be used in debug messages
    public KeyPair keyPair;                // The encryption key pair to be used in communications
    public KeyPair clientKeyPair;          // The client's encryption key pair (will only contain the public key)
    public boolean busy = false;           // Indicates whether the handler is busy
    public Server server;                  // Parent server object
    public Object queueObject;             // The object supplied to the handler from the QueueManager
    public boolean authenticated;          // Whether the connection is authenticated or not

    /**
     * Constructor method, assigns private and constant fields
     * @param socket The TCP/IP connection socket to be used by this handler
     * @param threadNum The Thread number of this handler, used in debug messages
     * @param className The name of the class, used in debug messages
     */
    public Handler(Socket socket, int threadNum, String className) {
        this.socket = socket;
        this.threadNum = threadNum;
        this.className = className;

        // Make this thread a daemon of the main process
        // This means that this thread will quit when the main program quits.
        this.setDaemon(true);
    }

    /**
     * Starts I/O streams and creates RSA key pair
     * @throws IOException Thrown by I/O operations
     */
    public void InitializeIO() throws IOException {
        this.oos = new ObjectOutputStream(this.socket.getOutputStream());
        this.ois = new ObjectInputStream(this.socket.getInputStream());
    }

    /**
     * Stop the handler thread using the Thread.wait() method
     */
    public synchronized void StopHandler() {
        try {
            this.wait();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a debug log message using the identification data supplied to this handler
     * @param message The message to output
     */
    public void DebugLog(String message) {
        System.out.println(className + " (" + threadNum + "): " + message);
    }

    /**
     * Send an object over the socket
     * @param obj The object to send
     * @throws IOException Thrown if there is an I/O issue
     */
    public void Send(Object obj) throws IOException {
        this.oos.writeObject(obj);
        this.oos.flush();
    }

    /**
     * Receive an object from the socket
     * @return The object that is received
     * @throws IOException Thrown if there is an I/O issue
     * @throws ClassNotFoundException Thrown if a requested class cannot be found
     */
    public Object Receive() throws IOException, ClassNotFoundException {
        return this.ois.readObject();
    }

    /**
     * Try to close the socket
     */
    public void Close() {
        try {
            this.socket.close();

        } catch (IOException e) {
            this.DebugLog("Failed to close socket (" + e.getMessage() + ")");
        }

        boolean emptyPass = false;
        while (!emptyPass) {
            emptyPass = true;

            for (int i = 0 ; i < this.server.listenRules.size(); i++) {
                if (this.server.listenRules.get(i).handler.equals(this)) {
                    this.server.listenRules.remove(i);
                    emptyPass = false;
                    break;
                }
            }
        }
    }

    /**
     * Parse an RSA string, i.e a string of RSA encrypted chars separated by a ',' character.
     * @param s The RSA string to parse
     * @return BigInteger array of encrypted chars
     * @deprecated
     */
    public BigInteger[] ParseRSAString(String s) {
        String[] splitString = s.split(",");
        BigInteger[] result = new BigInteger[splitString.length];
        for (int i = 0; i < splitString.length; i++) {
            result[i] = new BigInteger(splitString[i]);
        }

        return result;
    }

    /**
     * Encrypt all the string fields of an object
     * @param obj The object to encrypt
     * @param out The object to output to
     * @param keyPair The RSA key pair to encrypt with
     * @param <T> The type of the object
     * @deprecated No longer required as RSA library can now encrypt objects fully
     */
    public <T> void EncryptObjectFields(T obj, T out, KeyPair keyPair) {
        // Get the fields of the object
        Field[] fields = obj.getClass().getDeclaredFields();

        for (Field field : fields) {
            // Check that this field is a string type (otherwise it can't be encrypted)
            try {
                if (field.getType() == String.class && field.get(obj) != null) {
                    try {
                        // Encrypt the string to array of big integers
                        BigInteger[] cipherArray = keyPair.encrypt(RSA.StringToBigIntegerArray((String) field.get(obj)));
                        // Compile the result into a string
                        StringBuilder result = new StringBuilder();
                        for (BigInteger num : cipherArray) {
                            result.append(num.toString());
                            result.append(",");
                        }

                        result.deleteCharAt(result.lastIndexOf(","));

                        // Assign the field
                        field.set(out, result.toString());

                    } catch (IllegalAccessException | PublicKeyException e) {
                        this.DebugLog(e.getMessage());
                    }
                }
            } catch (IllegalAccessException e) {
                this.DebugLog(e.getMessage());
            }
        }
    }

    /**
     * Decrypt all the string fields of an object
     * @param obj The object to Decrypt
     * @param out The object to output to
     * @param keyPair The RSA key pair to Decrypt with
     * @param <T> The type of the object
     * @deprecated No longer required as RSA library can now decrypt objects fully
     */
    public <T> void DecryptObjectFields(T obj, T out, KeyPair keyPair) {
        // Get the fields of the object
        Field[] fields = obj.getClass().getDeclaredFields();

        for (Field field : fields) {
            // Check that this field is a string type (otherwise it can't be decrypted)
            try {
                if (field.getType() == String.class && field.get(obj) != null) {
                    try {
                        // Parse the RSA encrypted string
                        BigInteger[] cipherArray = this.ParseRSAString((String) field.get(obj));

                        // Decrypt the cipher array
                        BigInteger[] plainTextArray = keyPair.decrypt(cipherArray);

                        // Assign the field
                        field.set(out, RSA.BigIntegerArrayToString(plainTextArray));

                    } catch (IllegalAccessException | PrivateKeyException e) {
                        this.DebugLog(e.getMessage());
                    }
                }
            } catch (IllegalAccessException e) {
                this.DebugLog(e.getMessage());
            }
        }
    }
}
