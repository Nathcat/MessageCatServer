package com.nathcat.messagecat_server;

/**
 * Acts a request packet, will be passed to the RequestHandler by the ConnectionHandler
 *
 * @author Nathan "Nathcat" Baines
 */
public class Request {
    public final RequestType type;
    public final Object data;

    public Request(RequestType type, Object data) {
        this.type = type;
        this.data = data;
    }
}
