

package com.wallet.ctc.model.blockchain;



public class TrxBlockHeardBean {

    private String blockID;
    private BlockHeaderBean block_header;

    public String getBlockID() {
        return blockID;
    }

    public void setBlockID(String blockID) {
        this.blockID = blockID;
    }

    public BlockHeaderBean getBlock_header() {
        return block_header;
    }

    public void setBlock_header(BlockHeaderBean block_header) {
        this.block_header = block_header;
    }

    public static class BlockHeaderBean {
        private RawDataBean raw_data;
        private String witness_signature;

        public RawDataBean getRaw_data() {
            return raw_data;
        }

        public void setRaw_data(RawDataBean raw_data) {
            this.raw_data = raw_data;
        }

        public String getWitness_signature() {
            return witness_signature;
        }

        public void setWitness_signature(String witness_signature) {
            this.witness_signature = witness_signature;
        }

        public static class RawDataBean {
            private Integer number;
            private String txTrieRoot;
            private String witness_address;
            private String parentHash;
            private Integer version;
            private Long timestamp;

            public Integer getNumber() {
                return number;
            }

            public void setNumber(Integer number) {
                this.number = number;
            }

            public String getTxTrieRoot() {
                return txTrieRoot;
            }

            public void setTxTrieRoot(String txTrieRoot) {
                this.txTrieRoot = txTrieRoot;
            }

            public String getWitness_address() {
                return witness_address;
            }

            public void setWitness_address(String witness_address) {
                this.witness_address = witness_address;
            }

            public String getParentHash() {
                return parentHash;
            }

            public void setParentHash(String parentHash) {
                this.parentHash = parentHash;
            }

            public Integer getVersion() {
                return version;
            }

            public void setVersion(Integer version) {
                this.version = version;
            }

            public Long getTimestamp() {
                return timestamp;
            }

            public void setTimestamp(Long timestamp) {
                this.timestamp = timestamp;
            }
        }
    }
}
