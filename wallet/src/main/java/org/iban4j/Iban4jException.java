
package org.iban4j;


public abstract class Iban4jException extends RuntimeException {

    public Iban4jException() {
        super();
    }

    public Iban4jException(final String message) {
        super(message);
    }

    public Iban4jException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public Iban4jException(final Throwable cause) {
        super(cause);
    }
}
