package com.nathcat.messagecat_server;

import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import com.nathcat.RSA.*;
import com.nathcat.messagecat_database_entities.User;
import org.json.simple.JSONObject;

/**
 * Handles the client authentication and handshake process
 *
 * @author Nathan "Nathan" Baines
 * @deprecated No longer used in the single handler thread design
 */
public class AuthenticationHandler extends Handler {
    /**
     * Constructor method, assigns private and constant fields
     *
     * @param socket    The TCP/IP connection socket to be used by this handler
     * @param threadNum The Thread number of this handler, used in debug messages
     */
    public AuthenticationHandler(Socket socket, int threadNum) throws NoSuchAlgorithmException, IOException {
        super(socket, threadNum, "AuthenticationHandler");
    }


    /**
     * This method will be executed inside a thread when the handler thread is started
     */
    @Override
    public void run() {
        while (true) {
            this.busy = false;
            // Stop the handler process
            this.StopHandler();

            // Check that the queue object is not null
            if (this.queueObject == null) {
                continue;
            }

            // Tell the queue manager that this handler is busy and cannot be assigned to a new task yet
            this.busy = true;
            this.DebugLog("Assigned to task");
            // Get the socket from the queue object
            this.socket = (Socket) ((CloneableObject) this.queueObject).object;

            // Try to initialize I/O
            try {
                this.InitializeIO();

            } catch (IOException e) {
                this.DebugLog(e.getMessage());
                this.queueObject = null;
                continue;
            }

            // Try to generate an RSA key pair
            try {
                this.keyPair = RSA.GenerateRSAKeyPair();
            } catch (NoSuchAlgorithmException e) {
                this.DebugLog("Failed to generate RSA key pair! (" + e.getMessage() + ")");
                this.queueObject = null;
                continue;
            }

            // Perform the handshake process
            boolean handshakeSuccessful = true;

            // Try to send the server's public key to the client
            try {
                this.Send(new KeyPair(this.keyPair.pub, null));

            } catch (IOException e) {
                this.DebugLog(e.getMessage());
                handshakeSuccessful = false;
            }

            // Try to receive the client's key pair
            try {
                this.clientKeyPair = (KeyPair) this.Receive();

            } catch (IOException | ClassNotFoundException e) {
                this.DebugLog(e.getMessage());
                handshakeSuccessful = false;
            }

            // Check that the handshake was successful
            if (handshakeSuccessful) {
                this.DebugLog("Handshake successful!");

                // Perform the authentication process
                try {
                    // Get authentication data from the client
                    User authenticationData = (User) this.keyPair.decrypt((EncryptedObject) this.Receive());
                    if (authenticationData.UserID == -2) {
                        // Add as a new user
                        while (true) {
                            try {
                                JSONObject dataToPass = new JSONObject();
                                dataToPass.put("socket", this.socket);
                                dataToPass.put("oos", this.oos);
                                dataToPass.put("ois", this.ois);
                                dataToPass.put("keyPair", this.keyPair);
                                dataToPass.put("clientKeyPair", this.clientKeyPair);
                                dataToPass.put("user", null);

                                JSONObject request = new JSONObject();
                                request.put("type", RequestType.AddUser);
                                request.put("data", authenticationData);

                                dataToPass.put("request", request);

                                this.server.requestHandlerQueueManager.queue.Push(new CloneableObject(dataToPass));
                                break;

                            } catch (QueueIsLockedException | QueueIsFullException ignored) {}
                        }

                        continue;
                    }
                    // Get the user data from the database by username
                    User user = this.server.db.GetUserByUsername(authenticationData.Username);
                    // Check the data
                    if (user != null && authenticationData.Password.contentEquals(user.Password)) {
                        // Send the user data back to the client after encrypting it
                        this.Send(this.clientKeyPair.encrypt(user));


                        // Pass the connection and other relevant data to the connection handler
                        JSONObject dataToPass = new JSONObject();
                        dataToPass.put("socket", this.socket);
                        dataToPass.put("user", user);
                        dataToPass.put("keyPair", this.keyPair);
                        dataToPass.put("clientKeyPair", this.clientKeyPair);
                        dataToPass.put("oos", this.oos);
                        dataToPass.put("ois", this.ois);

                        while (true) {
                            try {
                                this.server.connectionHandlerQueueManager.queue.Push(new CloneableObject(dataToPass));
                                break;

                            } catch (QueueIsLockedException | QueueIsFullException ignored) {}
                        }

                    }
                    else {
                        this.Close();
                    }

                } catch (IOException | ClassNotFoundException e) {
                    this.DebugLog("Failed to receive auth data (" + e.getMessage() + ")");
                    this.Close();

                } catch (PrivateKeyException | PublicKeyException e) {
                    this.DebugLog(e.getMessage());
                    this.Close();
                }
            }
            else {
                this.DebugLog("Handshake failed!");
                this.Close();
            }

            this.busy = false;
        }
    }
}
