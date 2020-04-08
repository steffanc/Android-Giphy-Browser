package com.giphy.browser.common.network;

/**
 * Exception for a problem with the transport to the remote server.
 */
public class NetworkException extends Exception {
    public NetworkException(String message) {
        super(message);
    }

    public NetworkException(String message, Throwable cause) {
        super(message, cause);
    }

    public NetworkException(Throwable cause) {
        super(cause);
    }
}
