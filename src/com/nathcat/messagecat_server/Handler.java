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
    public final int threadNum;            // The thread number, used in debug messages
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
}
