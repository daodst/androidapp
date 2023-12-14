

package com.wallet.ctc.model.blockchain;


public class SolHashBean {
    private ContextBean context;
    private ValueBean value;
    public static class ContextBean {
        private Integer slot;
    }

    public static class ValueBean {
        private String blockhash;
        private FeeCalculatorBean feeCalculator;

        public String getBlockhash() {
            return blockhash;
        }

        public void setBlockhash(String blockhash) {
            this.blockhash = blockhash;
        }

        public FeeCalculatorBean getFeeCalculator() {
            return feeCalculator;
        }

        public void setFeeCalculator(FeeCalculatorBean feeCalculator) {
            this.feeCalculator = feeCalculator;
        }

        public static class FeeCalculatorBean {
            private Integer lamportsPerSignature;
        }
    }

    public ContextBean getContext() {
        return context;
    }

    public void setContext(ContextBean context) {
        this.context = context;
    }

    public ValueBean getValue() {
        return value;
    }

    public void setValue(ValueBean value) {
        this.value = value;
    }
}
