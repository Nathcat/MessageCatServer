package com.nathcat.messagecat_server;

import com.nathcat.RSA.*;
import com.nathcat.messagecat_database_entities.User;
import jdk.jshell.spi.ExecutionControl;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
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
            this.busy = false;
            this.StopHandler();

            if (this.queueObject == null) {
                continue;
            }

            this.busy = true;
            this.authenticated = false;
            this.DebugLog("Assigned to task");

            this.socket = (Socket) ((CloneableObject) this.queueObject).object;

            try {
                this.InitializeIO();

            } catch (IOException e) {
                this.DebugLog("Failed to initialise I/O (" + e.getMessage() + ").");
                this.Close();
                continue;
            }

            // Perform handshake
            if (this.DoHandshake()) {
                // Start connection main loop
                this.MainLoop();
            }
            else {
                this.DebugLog("Handshake failed!");
                this.Close();
            }
        }
    }

    /**
     * Perform the handshake between the server and the client
     * @return Whether the handshake was successful or not
     */
    private boolean DoHandshake() {
        // Try to generate an RSA key pair
        try {
            this.keyPair = RSA.GenerateRSAKeyPair();
        } catch (NoSuchAlgorithmException e) {
            this.DebugLog("Failed to generate RSA key pair! (" + e.getMessage() + ")");
            this.queueObject = null;
            return false;
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

        return handshakeSuccessful;
    }

    /**
     * The main handler loop for the connection
     */
    private void MainLoop() {
        while (true) {
            try {
                JSONObject request = (JSONObject) this.keyPair.decrypt((EncryptedObject) this.Receive());
                this.Send(this.clientKeyPair.encrypt(this.HandleRequest(request)));

            } catch (PrivateKeyException | PublicKeyException | IOException | ClassNotFoundException e) {
                this.DebugLog("Exception in non-authenticated protocol: " + e.getMessage());
            }
        }
    }

    /**
     * Handle a JSON request object
     * @param request The JSON request object
     * @return The response object
     */
    private Object HandleRequest(JSONObject request) {
        // TODO Handle request, the general process for this can be found in the original RequestHandler, although it will need some tweaking
        return null;
    }
}
