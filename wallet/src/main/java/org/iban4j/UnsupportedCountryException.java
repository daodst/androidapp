
package org.iban4j;


public class UnsupportedCountryException extends Iban4jException {

    private static final long serialVersionUID = -3733353745417164234L;

    private String countryCode;

    
    public UnsupportedCountryException() {
        super();
    }

    
    public UnsupportedCountryException(final String s) {
        super(s);
    }

    
    public UnsupportedCountryException(String countryCode, final String s) {
        super(s);
        this.countryCode = countryCode;
    }

    
    public UnsupportedCountryException(final String s, final Throwable t) {
        super(s, t);
    }

    
    public UnsupportedCountryException(final Throwable t) {
        super(t);
    }

    public String getCountryCode() {
        return countryCode;
    }
}