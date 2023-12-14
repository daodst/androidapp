

package com.wallet.ctc.model.blockchain;

import java.util.List;


public class TrxRawData {

    private List<ContractBean> contract;
    private String ref_block_bytes;
    private String ref_block_hash;
    private Long expiration;
    private Long timestamp;
    private Long fee_limit;

    public List<ContractBean> getContract() {
        return contract;
    }

    public void setContract(List<ContractBean> contract) {
        this.contract = contract;
    }

    public String getRef_block_bytes() {
        return ref_block_bytes;
    }

    public void setRef_block_bytes(String ref_block_bytes) {
        this.ref_block_bytes = ref_block_bytes;
    }

    public String getRef_block_hash() {
        return ref_block_hash;
    }

    public void setRef_block_hash(String ref_block_hash) {
        this.ref_block_hash = ref_block_hash;
    }

    public Long getExpiration() {
        return expiration;
    }

    public void setExpiration(Long expiration) {
        this.expiration = expiration;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getFee_limit() {
        return fee_limit;
    }

    public void setFee_limit(Long fee_limit) {
        this.fee_limit = fee_limit;
    }

    public static class ContractBean {
        private ParameterBean parameter;
        private String type;

        public ParameterBean getParameter() {
            return parameter;
        }

        public void setParameter(ParameterBean parameter) {
            this.parameter = parameter;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public static class ParameterBean {
            private TrxDappValueBean value;
            private String type_url;

            public TrxDappValueBean getValue() {
                return value;
            }

            public void setValue(TrxDappValueBean value) {
                this.value = value;
            }

            public String getType_url() {
                return type_url;
            }

            public void setType_url(String type_url) {
                this.type_url = type_url;
            }
        }
    }
}
