
package org.iban4j;

import static org.iban4j.IbanFormatException.IbanFormatViolation.ACCOUNT_NUMBER_NOT_NULL;
import static org.iban4j.IbanFormatException.IbanFormatViolation.BANK_CODE_NOT_NULL;
import static org.iban4j.IbanFormatException.IbanFormatViolation.COUNTRY_CODE_NOT_NULL;
import static org.iban4j.IbanFormatException.IbanFormatViolation.IBAN_FORMATTING;

import org.iban4j.bban.BbanStructure;
import org.iban4j.bban.BbanStructureEntry;

import java.util.List;
import java.util.Random;



public final class Iban {

    static final String DEFAULT_CHECK_DIGIT = "00";

    
    private final String value;

    
    private Iban(final String value) {
        this.value = value;
    }

    
    public CountryCode getCountryCode() {
        return CountryCode.getByCode(IbanUtil.getCountryCode(value));
    }

    
    public String getCheckDigit() {
        return IbanUtil.getCheckDigit(value);
    }

    
    public String getAccountNumber() {
        return IbanUtil.getAccountNumber(value);
    }

    
    public String getBankCode() {
        return IbanUtil.getBankCode(value);
    }

    
    public String getBranchCode() {
        return IbanUtil.getBranchCode(value);
    }

    
    public String getNationalCheckDigit() {
        return IbanUtil.getNationalCheckDigit(value);
    }

    
    public String getAccountType() {
        return IbanUtil.getAccountType(value);
    }

    
    public String getOwnerAccountType() {
        return IbanUtil.getOwnerAccountType(value);
    }

    
    public String getIdentificationNumber() {
        return IbanUtil.getIdentificationNumber(value);
    }

    
    public String getBban() {
        return IbanUtil.getBban(value);
    }

    
    public static Iban valueOf(final String iban) throws IbanFormatException,
            InvalidCheckDigitException, UnsupportedCountryException {
        IbanUtil.validate(iban);
        return new Iban(iban);
    }

    
    public static Iban valueOf(final String iban, final IbanFormat format) throws IbanFormatException,
            InvalidCheckDigitException, UnsupportedCountryException {
        switch (format) {
            case Default:
                final String ibanWithoutSpaces = iban.replace(" ", "");
                final Iban ibanObj = valueOf(ibanWithoutSpaces);
                if(ibanObj.toFormattedString().equals(iban)) {
                    return ibanObj;
                }
                throw new IbanFormatException(IBAN_FORMATTING,
                        String.format("Iban must be formatted using 4 characters and space combination. " +
                                "Instead of [%s]", iban));
            default:
                return valueOf(iban);
        }
    }

    @Override
    public String toString() {
        return value;
    }

    
    public String toFormattedString() {
        return IbanUtil.toFormattedString(value);
    }

    public static Iban random() {
        return new Builder().buildRandom();
    }

    public static Iban random(CountryCode cc) {
        return new Builder().countryCode(cc).buildRandom();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Iban) {
            return value.equals(((Iban)obj).value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    
    public final static class Builder {

        private CountryCode countryCode;
        private String bankCode;
        private String branchCode;
        private String nationalCheckDigit;
        private String accountType;
        private String accountNumber;
        private String ownerAccountType;
        private String identificationNumber;

        private final Random random = new Random();

        
        public Builder() {
        }

        
        public Builder countryCode(final CountryCode countryCode) {
            this.countryCode = countryCode;
            return this;
        }

        
        public Builder bankCode(final String bankCode) {
            this.bankCode = bankCode;
            return this;
        }

        
        public Builder branchCode(final String branchCode) {
            this.branchCode = branchCode;
            return this;
        }

        
        public Builder accountNumber(final String accountNumber) {
            this.accountNumber = accountNumber;
            return this;
        }

        
        public Builder nationalCheckDigit(final String nationalCheckDigit) {
            this.nationalCheckDigit = nationalCheckDigit;
            return this;
        }

        
        public Builder accountType(final String accountType) {
            this.accountType = accountType;
            return this;
        }

        
        public Builder ownerAccountType(final String ownerAccountType) {
            this.ownerAccountType = ownerAccountType;
            return this;
        }

        
        public Builder identificationNumber(final String identificationNumber) {
            this.identificationNumber = identificationNumber;
            return this;
        }

        
        public Iban build() throws IbanFormatException,
                IllegalArgumentException, UnsupportedCountryException {
            return build(true);
        }

        
        public Iban build(boolean validate) throws IbanFormatException,
                IllegalArgumentException, UnsupportedCountryException {

            
            require(countryCode, bankCode, accountNumber);

            
            final String formattedIban = formatIban();

            final String checkDigit = IbanUtil.
                    calculateCheckDigit(formattedIban);

            
            final String ibanValue = IbanUtil.replaceCheckDigit(formattedIban, checkDigit);

            if (validate) {
                IbanUtil.validate(ibanValue);
            }
            return new Iban(ibanValue);
        }

        
        public Iban buildRandom() throws IbanFormatException,
                IllegalArgumentException, UnsupportedCountryException {
            if (countryCode == null) {
                List<CountryCode> countryCodes = BbanStructure.supportedCountries();
                this.countryCode(countryCodes.get(random.nextInt(countryCodes.size())));
            }
            fillMissingFieldsRandomly();
            return build();
        }

        
        private String formatBban() {
            final StringBuilder sb = new StringBuilder();
            final BbanStructure structure = BbanStructure.forCountry(countryCode);

            if (structure == null) {
                throw new UnsupportedCountryException(countryCode.toString(),
                        "Country code is not supported.");
            }

            for(final BbanStructureEntry entry : structure.getEntries()) {
                switch (entry.getEntryType()) {
                    case bank_code:
                        sb.append(bankCode);
                        break;
                    case branch_code:
                        sb.append(branchCode);
                        break;
                    case account_number:
                        sb.append(accountNumber);
                        break;
                    case national_check_digit:
                        sb.append(nationalCheckDigit);
                        break;
                    case account_type:
                        sb.append(accountType);
                        break;
                    case owner_account_number:
                        sb.append(ownerAccountType);
                        break;
                    case identification_number:
                        sb.append(identificationNumber);
                        break;
                }
            }
            return sb.toString();
        }

        
        private String formatIban() {
            final StringBuilder sb = new StringBuilder();
            sb.append(countryCode.getAlpha2());
            sb.append(DEFAULT_CHECK_DIGIT);
            sb.append(formatBban());
            return sb.toString();
        }

        private void require(final CountryCode countryCode,
                             final String bankCode,
                             final String accountNumber)
                throws IbanFormatException {
            if(countryCode == null) {
                throw new IbanFormatException(COUNTRY_CODE_NOT_NULL,
                        "countryCode is required; it cannot be null");
            }

            if(bankCode == null) {
                throw new IbanFormatException(BANK_CODE_NOT_NULL,
                        "bankCode is required; it cannot be null");
            }

            if(accountNumber == null) {
                throw new IbanFormatException(ACCOUNT_NUMBER_NOT_NULL,
                        "accountNumber is required; it cannot be null");
            }
        }

        private void fillMissingFieldsRandomly() {
            final BbanStructure structure = BbanStructure.forCountry(countryCode);

            if (structure == null) {
                throw new UnsupportedCountryException(countryCode.toString(),
                        "Country code is not supported.");
            }

            for(final BbanStructureEntry entry : structure.getEntries()) {
                switch (entry.getEntryType()) {
                    case bank_code:
                        if (bankCode == null) {
                            bankCode = entry.getRandom();
                        }
                        break;
                    case branch_code:
                        if (branchCode == null) {
                            branchCode = entry.getRandom();
                        }
                        break;
                    case account_number:
                        if (accountNumber == null) {
                            accountNumber = entry.getRandom();
                        }
                        break;
                    case national_check_digit:
                        if (nationalCheckDigit == null) {
                            nationalCheckDigit = entry.getRandom();
                        }
                        break;
                    case account_type:
                        if (accountType == null) {
                            accountType = entry.getRandom();
                        }
                        break;
                    case owner_account_number:
                        if (ownerAccountType == null) {
                            ownerAccountType = entry.getRandom();
                        }
                        break;
                    case identification_number:
                        if (identificationNumber == null) {
                            identificationNumber = entry.getRandom();
                        }
                        break;
                }
            }
        }

    }

}
