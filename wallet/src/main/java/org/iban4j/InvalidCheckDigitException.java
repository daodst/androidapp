
package org.iban4j;



public class InvalidCheckDigitException extends Iban4jException {

    private static final long serialVersionUID = -9222165415290480187L;

    private String actual;
    private String expected;

    
    public InvalidCheckDigitException() {
        super();
    }

    
    public InvalidCheckDigitException(final String s) {
        super(s);
    }

    
    public InvalidCheckDigitException(final String actual, final String expected, final String s) {
        super(s);
        this.actual = actual;
        this.expected = expected;
    }

    
    public InvalidCheckDigitException(final String s, final Throwable t) {
        super(s, t);
    }

    
    public InvalidCheckDigitException(final Throwable t) {
        super(t);
    }

    public String getActual() {
        return actual;
    }

    public String getExpected() {
        return expected;
    }
}
