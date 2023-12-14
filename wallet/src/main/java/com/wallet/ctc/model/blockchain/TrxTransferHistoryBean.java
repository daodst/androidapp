

package com.wallet.ctc.model.blockchain;

import com.wallet.ctc.crypto.WalletUtil;

import java.math.BigDecimal;
import java.util.List;


public class TrxTransferHistoryBean{


    

    private String txID;
    private double net_usage;
    private String raw_data_hex;
    private String coin_name;
    private double net_fee;
    private double energy_usage;
    private BigDecimal blockNumber;
    private double block_timestamp;
    private double energy_fee;
    private double energy_usage_total;
    private RawDataBean raw_data;
    private List<RetBean> ret;
    private List<String> signature;
    private List<?> internal_transactions;

    public String getTxID() {
        return txID;
    }

    public void setTxID(String txID) {
        this.txID = txID;
    }

    public double getNet_usage() {
        return net_usage;
    }

    public void setNet_usage(double net_usage) {
        this.net_usage = net_usage;
    }

    public String getRaw_data_hex() {
        return raw_data_hex;
    }

    public String getCoin_name() {
        return coin_name;
    }

    public void setCoin_name(String coin_name) {
        this.coin_name = coin_name;
    }

    public void setRaw_data_hex(String raw_data_hex) {
        this.raw_data_hex = raw_data_hex;
    }

    public double getNet_fee() {
        return net_fee;
    }

    public void setNet_fee(double net_fee) {
        this.net_fee = net_fee;
    }

    public double getEnergy_usage() {
        return energy_usage;
    }

    public void setEnergy_usage(double energy_usage) {
        this.energy_usage = energy_usage;
    }

    public BigDecimal getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(BigDecimal blockNumber) {
        this.blockNumber = blockNumber;
    }

    public double getBlock_timestamp() {
        return block_timestamp;
    }

    public void setBlock_timestamp(double block_timestamp) {
        this.block_timestamp = block_timestamp;
    }

    public double getEnergy_fee() {
        return energy_fee;
    }

    public void setEnergy_fee(double energy_fee) {
        this.energy_fee = energy_fee;
    }

    public double getEnergy_usage_total() {
        return energy_usage_total;
    }

    public void setEnergy_usage_total(double energy_usage_total) {
        this.energy_usage_total = energy_usage_total;
    }

    public RawDataBean getRaw_data() {
        return raw_data;
    }

    public void setRaw_data(RawDataBean raw_data) {
        this.raw_data = raw_data;
    }

    public List<RetBean> getRet() {
        return ret;
    }

    public void setRet(List<RetBean> ret) {
        this.ret = ret;
    }

    public List<String> getSignature() {
        return signature;
    }

    public void setSignature(List<String> signature) {
        this.signature = signature;
    }

    public List<?> getInternal_transactions() {
        return internal_transactions;
    }

    public void setInternal_transactions(List<?> internal_transactions) {
        this.internal_transactions = internal_transactions;
    }

    public static class RawDataBean {
        

        private String ref_block_bytes;
        private String ref_block_hash;
        private double expiration;
        private long timestamp;
        private List<ContractBean> contract;

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

        public double getExpiration() {
            return expiration;
        }

        public void setExpiration(double expiration) {
            this.expiration = expiration;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public List<ContractBean> getContract() {
            return contract;
        }

        public void setContract(List<ContractBean> contract) {
            this.contract = contract;
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
                

                private ValueBean value;
                private String type_url;

                public ValueBean getValue() {
                    return value;
                }

                public void setValue(ValueBean value) {
                    this.value = value;
                }

                public String getType_url() {
                    return type_url;
                }

                public void setType_url(String type_url) {
                    this.type_url = type_url;
                }

                public static class ValueBean {
                    

                    private BigDecimal amount;
                    private String owner_address;
                    private String to_address;

                    public BigDecimal getAmount() {
                        return amount;
                    }

                    public void setAmount(BigDecimal amount) {
                        this.amount = amount;
                    }

                    public String getOwner_address() {
                        return WalletUtil.getBase58Address(owner_address);
                    }

                    public void setOwner_address(String owner_address) {
                        this.owner_address = owner_address;
                    }

                    public String getTo_address() {
                        return WalletUtil.getBase58Address(to_address);
                    }

                    public void setTo_address(String to_address) {
                        this.to_address = to_address;
                    }
                }
            }
        }
    }

    public static class RetBean {
        

        private String contractRet;
        private BigDecimal fee;

        public String getContractRet() {
            return contractRet;
        }

        public void setContractRet(String contractRet) {
            this.contractRet = contractRet;
        }

        public BigDecimal getFee() {
            return fee;
        }

        public void setFee(BigDecimal fee) {
            this.fee = fee;
        }
    }
}
