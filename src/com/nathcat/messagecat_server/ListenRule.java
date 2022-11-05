package com.nathcat.messagecat_server;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * A listening rule to be supplied to a client when they want to listen for actions performed
 * on the server by other clients.
 *
 * @author Nathan "Nathcat" Baines
 */

public class ListenRule implements Serializable {
    public class IDAlreadySetException extends Exception { }

    private int id = -1;                // Unique identifier for this listening rule
    public int connectionHandlerId = -1;
    public ConnectionHandler handler;   // Handler which is handling the client this listen rule was created by
    private RequestType listenForType;  // The request type which this listen rule is listening for

    // The listen rule will only be triggered if the data in fieldToMatch matches the data in objectToMatch
    private String fieldNameToMatch;
    private Object objectToMatch;
    private Object[] objectsToMatch;

    public ListenRule(ConnectionHandler handler, RequestType listenForType, String fieldNameToMatch, Object objectToMatch) {
        this.handler = handler;
        this.listenForType = listenForType;
        this.fieldNameToMatch = fieldNameToMatch;
        this.objectToMatch = objectToMatch;
    }

    public ListenRule(int connectionHandlerId, RequestType listenForType, String fieldNameToMatch, Object objectToMatch) {
        this.listenForType = listenForType;
        this.fieldNameToMatch = fieldNameToMatch;
        this.objectToMatch = objectToMatch;
        this.connectionHandlerId = connectionHandlerId;
    }

    public ListenRule(RequestType listenForType, String fieldNameToMatch, Object objectToMatch) {
        this.listenForType = listenForType;
        this.fieldNameToMatch = fieldNameToMatch;
        this.objectToMatch = objectToMatch;
        this.objectsToMatch = null;
    }
    public ListenRule(RequestType listenForType, String fieldNameToMatch, Object[] objectsToMatch) {
        this.listenForType = listenForType;
        this.fieldNameToMatch = fieldNameToMatch;
        this.objectToMatch = null;
        this.objectsToMatch = objectsToMatch;
    }

    public ListenRule(RequestType listenForType) {
        this.listenForType = listenForType;
    }

    public ListenRule(int connectionHandlerId, RequestType listenForType) {
        this.listenForType = listenForType;
        this.connectionHandlerId = connectionHandlerId;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) throws IDAlreadySetException {
        if (this.id != -1) {
            throw new IDAlreadySetException();
        }

        this.id = id;
    }

    /**
     * Checks if a request matches the listen rule's criteria, and send the request to the client if it does
     * @param type The type of request
     * @param data The data object to compare to
     * @return True if the listen rule criteria is met, False if not.
     */
    public boolean CheckRequest(RequestType type, Object data) throws NoSuchFieldException, IllegalAccessException {
        if (fieldNameToMatch == null) {
            return type == listenForType;
        }

        if (objectToMatch != null) {
            return type == listenForType && data.getClass()  // Compare request type to the type we are listening for and get the class object of data
                    .getField(fieldNameToMatch)              // Get the field we are comparing
                    .get(data).equals(objectToMatch);        // Get the data from the field in the instance of data and compare to objectToMatch
        }
        else {
            boolean contains = false;
            for (Object toMatch : objectsToMatch) {
                if (data.getClass().getField(fieldNameToMatch).get(data).equals(toMatch)) {
                    contains = true;
                    break;
                }
            }

            return type == listenForType && contains;
        }
    }
}