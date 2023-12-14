
package org.iban4j;

import org.iban4j.bban.BbanEntryType;


public class IbanFormatException extends Iban4jException {

    private static final long serialVersionUID = -2715142907876721085L;

    private IbanFormatViolation formatViolation;
    private Object expected;
    private Object actual;
    private BbanEntryType bbanEntryType;
    private char invalidCharacter;

    
    public IbanFormatException() {
        super();
    }

    
    public IbanFormatException(final String s) {
        super(s);
    }

    
    public IbanFormatException(final String s, final Throwable t) {
        super(s, t);
    }

    
    public IbanFormatException(final Throwable t) {
        super(t);
    }

    
    public IbanFormatException(final IbanFormatViolation violation,
                               final Object actual,
                               final Object expected,
                               final String s) {
        super(s);
        this.expected = expected;
        this.actual = actual;
        this.formatViolation = violation;
    }

    
    public IbanFormatException(final IbanFormatViolation violation,
                               final Object actual,
                               final String s) {
        super(s);
        this.actual = actual;
        this.formatViolation = violation;
    }

    
    public IbanFormatException(final IbanFormatViolation violation,
                               final BbanEntryType entryType,
                               final Object actual,
                               final char invalidCharacter,
                               final String s) {
        super(s);
        this.actual = actual;
        this.formatViolation = violation;
        this.bbanEntryType = entryType;
        this.invalidCharacter = invalidCharacter;
    }

    
    public IbanFormatException(final IbanFormatViolation violation,
                               final String s) {
        super(s);
        this.formatViolation = violation;
    }

    public IbanFormatViolation getFormatViolation() {
        return formatViolation;
    }

    public Object getExpected() {
        return expected;
    }

    public Object getActual() {
        return actual;
    }

    public char getInvalidCharacter() {
        return invalidCharacter;
    }

    public BbanEntryType getBbanEntryType() {
        return bbanEntryType;
    }

    public static enum IbanFormatViolation {
        UNKNOWN,

        IBAN_FORMATTING,
        IBAN_NOT_NULL,
        IBAN_NOT_EMPTY,
        IBAN_VALID_CHARACTERS,

        CHECK_DIGIT_ONLY_DIGITS,
        CHECK_DIGIT_TWO_DIGITS,

        COUNTRY_CODE_TWO_LETTERS,
        COUNTRY_CODE_UPPER_CASE_LETTERS,
        COUNTRY_CODE_EXISTS,
        COUNTRY_CODE_NOT_NULL,

        BBAN_LENGTH,
        BBAN_ONLY_DIGITS,
        BBAN_ONLY_UPPER_CASE_LETTERS,
        BBAN_ONLY_DIGITS_OR_LETTERS,

        BANK_CODE_NOT_NULL,
        ACCOUNT_NUMBER_NOT_NULL

    }
}