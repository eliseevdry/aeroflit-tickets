package org.eliseev.aeroflot.tickets.exception;

public class ServiceException extends RuntimeException {
    public ServiceException(String cause) {
        super(cause);
    }
}
