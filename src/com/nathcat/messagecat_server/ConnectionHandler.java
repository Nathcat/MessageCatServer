package com.nathcat.messagecat_server;

import com.nathcat.RSA.EncryptedObject;
import com.nathcat.RSA.KeyPair;
import com.nathcat.RSA.PrivateKeyException;
import com.nathcat.messagecat_database_entities.User;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

/**
 * Maintains a connection with the client device
 *
 * @author Nathan "Nathcat" Baines
 */
public class ConnectionHandler extends Handler {
    /**
     * Constructor method, assigns private and constant fields
     *
     * @param socket    The TCP/IP connection socket to be used by this handler
     * @param threadNum The Thread number of this handler, used in debug messages
     */
    public ConnectionHandler(Socket socket, int threadNum) throws NoSuchAlgorithmException, IOException {
        super(socket, threadNum, "ConnectionHandler");
    }

    /**
     * This method will be executed in a different thread
     */
    @Override
    public void run() {
        while (true) {
            // Stop this handler process
            this.StopHandler();

            // Check that the queue object is not null
            if (this.queueObject == null) {
                continue;
            }

            this.busy = true;

            // Get the data from the authentication handler
            JSONObject data = (JSONObject) ((CloneableObject) this.queueObject).object;
            this.socket = (Socket) data.get("socket");
            this.oos = (ObjectOutputStream) data.get("oos");
            this.ois = (ObjectInputStream) data.get("ois");
            this.keyPair = (KeyPair) data.get("keyPair");
            this.clientKeyPair = (KeyPair) data.get("clientKeyPair");
            User user = (User) data.get("user");

            // Start the connection handler loop
            while (true) {
                try {
                    // Wait for a request
                    JSONObject request = (JSONObject) this.keyPair.decrypt((EncryptedObject) this.Receive());

                    // Pass the request to the request handler queue
                    JSONObject dataToPass = new JSONObject();
                    dataToPass.putAll(data);
                    dataToPass.put("request", request);

                    try {
                        this.server.requestHandlerQueueManager.queue.Push(new CloneableObject(dataToPass));

                    } catch (QueueIsFullException | QueueIsLockedException ignored) {}

                } catch (IOException | ClassNotFoundException e) {
                    this.DebugLog("Receive failed!");
                    this.Close();
                    break;

                } catch (PrivateKeyException e) {
                    this.DebugLog("Failed to decrypt!");
                    this.Close();
                    break;
                }
            }

            this.busy = false;
        }
    }
}
