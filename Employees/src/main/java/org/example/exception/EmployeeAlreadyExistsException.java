package org.example.exception;

public class EmployeeAlreadyExistsException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EmployeeAlreadyExistsException(String message) {
        super(message);
    }

    public EmployeeAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
