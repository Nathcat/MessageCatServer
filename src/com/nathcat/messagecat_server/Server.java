package com.nathcat.messagecat_server;

import com.nathcat.messagecat_database.Database;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

/**
 * This is Server object, it will initialise the Server and prepare the program to receive connections.
 *
 * @author Nathan "Nathcat" Baines
 */
public class Server {
    private final int port;
    private final int maxThreadCount;
    private final int maxQueueCapacity;
    public final Handler[] authenticationHandlerPool;
    public final Handler[] connectionHandlerPool;
    public final Handler[] requestHandlerPool;
    public final QueueManager authenticationHandlerQueueManager;
    public final QueueManager connectionHandlerQueueManager;
    public final QueueManager requestHandlerQueueManager;
    public final Database db;

    /**
     * Main entry point for the application
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        // Create a new server
        Server server = new Server();
        // Create a new server socket
        ServerSocket serverSocket = null;

        // Attempt to open the server socket
        try {
            serverSocket = new ServerSocket(server.port);

        } catch (IOException e) {
            server.DebugLog("Failed to create server socket! (" + e.getMessage() + ")");
            System.exit(1);
        }

        assert serverSocket != null;

        // Add a shutdown hook so that the server socket is correctly closed when the program is terminated
        Runtime.getRuntime().addShutdownHook(new ShutdownProcess(serverSocket, server));

        server.DebugLog("Server start complete, ready to receive connections!");

        Socket clientSocket = null;
        while (true) {
            try {
                // Accept any incoming connections
                clientSocket = serverSocket.accept();
                server.DebugLog("Received connection: " + clientSocket.getInetAddress().toString());

                // Push the connection to the queue
                server.authenticationHandlerQueueManager.queue.Push(new CloneableObject(clientSocket));

            } catch (IOException e) {
                server.DebugLog("An error occurred when accepting a connection: " + e.getMessage());

            } catch (QueueIsFullException e) {
                // This error means that the queue is full, so the connection should be declined
                server.DebugLog("AuthenticationHandler queue is full!");
                assert clientSocket != null;
                try {
                    clientSocket.close();

                } catch (IOException ex) {
                    server.DebugLog("There was an error when closing a client socket! (" + ex.getMessage() + ")");
                }

            } catch (QueueIsLockedException e) {
                while (server.authenticationHandlerQueueManager.queue.locked) {
                    try {
                        server.authenticationHandlerQueueManager.queue.Push(new CloneableObject(clientSocket));

                    } catch (QueueIsLockedException ignored) {
                        //server.DebugLog("Queue is locked");

                    } catch (QueueIsFullException ex) {
                        server.DebugLog("AuthenticationHandler queue is full!");
                        assert clientSocket != null;
                        try {
                            clientSocket.close();

                        } catch (IOException exc) {
                            server.DebugLog("There was an error when closing a client socket! (" + exc.getMessage() + ")");
                        }

                        break;
                    }
                }
            }
        }
    }

    /**
     * Constructor method
     */
    public Server() {
        this.DebugLog("Getting config file");

        // Get the config file and set constant fields
        JSONObject config = this.GetConfigFile();

        // Define the constant fields
        this.port = Integer.parseInt((String) config.get("port"));
        this.maxThreadCount = Integer.parseInt((String) config.get("maxThreadCount"));
        this.maxQueueCapacity = Integer.parseInt((String) config.get("maxQueueCapacity"));

        this.DebugLog("Starting database");
        this.db = new Database();

        this.DebugLog("Creating thread pools");

        // Create the thread pools
        authenticationHandlerPool = new Handler[this.maxThreadCount];
        connectionHandlerPool = new Handler[this.maxThreadCount];
        requestHandlerPool = new Handler[this.maxThreadCount];

        this.DebugLog("Creating handlers (" + this.maxThreadCount + " handlers to create)");
        // Populate the thread pools with handlers
        for (int i = 0; i < this.maxThreadCount; i++) {
            try {
                authenticationHandlerPool[i] = new AuthenticationHandler(null, i);
                connectionHandlerPool[i] = new ConnectionHandler(null, i);
                requestHandlerPool[i] = new RequestHandler(null, i);

                authenticationHandlerPool[i].server = this;
                connectionHandlerPool[i].server = this;
                requestHandlerPool[i].server = this;

                authenticationHandlerPool[i].start();
                connectionHandlerPool[i].start();
                requestHandlerPool[i].start();

            } catch (NoSuchAlgorithmException | IOException e) {
                this.DebugLog("Failed to create handler pools! (" + e.getMessage() + ")");
                System.exit(1);
            }
        }

        this.DebugLog("Starting queue managers");
        // Start the queue managers
        authenticationHandlerQueueManager = new QueueManager(this, new Queue(this.maxQueueCapacity), this.authenticationHandlerPool);
        connectionHandlerQueueManager = new QueueManager(this, new Queue(this.maxQueueCapacity), this.connectionHandlerPool);
        requestHandlerQueueManager = new QueueManager(this, new Queue(this.maxQueueCapacity), this.requestHandlerPool);

        authenticationHandlerQueueManager.start();
        connectionHandlerQueueManager.start();
        requestHandlerQueueManager.start();

        this.DebugLog("Initial setup complete");
    }

    /**
     * Get the server config file located at Assets/Server_Config.json
     * @return A JSON object parsed from the file's contents
     */
    private JSONObject GetConfigFile() {
        Scanner file = null;
        try {
            file = new Scanner(new File("Assets/Server_Config.json"));

        } catch (FileNotFoundException e) {
            this.DebugLog("Couldn't find config file at \"Assets/Server_Config.json\".");
            System.exit(1);
        }

        StringBuilder sb = new StringBuilder();
        while (file.hasNextLine()) {
            sb.append(file.nextLine());
        }

        try {
            return (JSONObject) new JSONParser().parse(sb.toString());
        } catch (ParseException e) {
            this.DebugLog("JSON Syntax error present in config file (" + e.getMessage() + ").");
            System.exit(1);
        }

        return null;
    }

    /**
     * Output a debug message to the console
     * @param message The message to output
     */
    public void DebugLog(String message) {
        System.out.println("Server: " + message);
    }
}

/**
 * This will be used with a shutdown hook so that the server is shutdown correctly when the program is terminated
 */
class ShutdownProcess extends Thread {
    private final ServerSocket ss;
    private final Server s;

    public ShutdownProcess(ServerSocket ss, Server s) {
        this.ss = ss;
        this.s = s;
    }

    @Override
    public void run() {
        try {
            this.s.DebugLog(this.s.authenticationHandlerQueueManager.queue.toString());
            this.s.DebugLog(this.s.connectionHandlerQueueManager.queue.toString());
            this.s.DebugLog(this.s.requestHandlerQueueManager.queue.toString());

            for (Handler h : this.s.authenticationHandlerPool) {
                System.out.print(h.busy + " ");
            }
            System.out.println();
            for (Handler h : this.s.connectionHandlerPool) {
                System.out.print(h.busy + " ");
            }
            System.out.println();
            for (Handler h : this.s.requestHandlerPool) {
                System.out.print(h.busy + " ");
            }

            this.ss.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}