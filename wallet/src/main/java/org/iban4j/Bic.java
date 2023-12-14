
package org.iban4j;



public final class Bic {

    private final String value;

    private Bic(final String value) {
        this.value = value;
    }

    
    public static Bic valueOf(final String bic) throws BicFormatException,
            UnsupportedCountryException {
        BicUtil.validate(bic);
        return new Bic(bic);
    }

    
    public String getBankCode() {
        return BicUtil.getBankCode(value);
    }

    
    public CountryCode getCountryCode() {
        return CountryCode.getByCode(BicUtil.getCountryCode(value));
    }

    
    public String getLocationCode() {
        return BicUtil.getLocationCode(value);
    }

    
    public String getBranchCode() {
        if(BicUtil.hasBranchCode(value)) {
            return BicUtil.getBranchCode(value);
        }
        return null;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Bic) {
            return value.equals(((Bic)obj).value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
