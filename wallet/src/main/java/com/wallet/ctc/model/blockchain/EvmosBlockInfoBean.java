package com.wallet.ctc.model.blockchain;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class EvmosBlockInfoBean {

    @SerializedName("block")
    private BlockDTO block;

    public static class BlockIdDTO {
        @SerializedName("hash")
        public String hash;
        @SerializedName("parts")
        public PartsDTO parts;

        public static class PartsDTO {
            @SerializedName("total")
            public int total;
            @SerializedName("hash")
            public String hash;
        }
    }

    public static class BlockDTO {
        @SerializedName("header")
        public HeaderDTO header;

        public static class HeaderDTO {
            @SerializedName("version")
            public VersionDTO version;
            @SerializedName("chain_id")
            public String chainId;
            @SerializedName("height")
            public String height;
            @SerializedName("time")
            public String time;
            @SerializedName("last_block_id")
            public LastBlockIdDTO lastBlockId;
            @SerializedName("last_commit_hash")
            public String lastCommitHash;
            @SerializedName("data_hash")
            public String dataHash;
            @SerializedName("validators_hash")
            public String validatorsHash;
            @SerializedName("next_validators_hash")
            public String nextValidatorsHash;
            @SerializedName("consensus_hash")
            public String consensusHash;
            @SerializedName("app_hash")
            public String appHash;
            @SerializedName("last_results_hash")
            public String lastResultsHash;
            @SerializedName("evidence_hash")
            public String evidenceHash;
            @SerializedName("proposer_address")
            public String proposerAddress;

            public static class VersionDTO {
                @SerializedName("block")
                public String block;
            }

            public static class LastBlockIdDTO {
                @SerializedName("hash")
                public String hash;
                @SerializedName("parts")
                public PartsDTO parts;

                public static class PartsDTO {
                    @SerializedName("total")
                    public int total;
                    @SerializedName("hash")
                    public String hash;
                }
            }
        }

        public static class DataDTO {
            @SerializedName("txs")
            public List<?> txs;
        }

        public static class EvidenceDTO {
            @SerializedName("evidence")
            public List<?> evidence;
        }

        public static class LastCommitDTO {
            @SerializedName("height")
            public String height;
            @SerializedName("round")
            public int round;
            @SerializedName("block_id")
            public BlockIdDTO blockId;

            public static class BlockIdDTO {
                @SerializedName("hash")
                public String hash;
                @SerializedName("parts")
                public PartsDTO parts;

                public static class PartsDTO {
                    @SerializedName("total")
                    public int total;
                    @SerializedName("hash")
                    public String hash;
                }
            }

            public static class SignaturesDTO {
                @SerializedName("block_id_flag")
                public String blockIdFlag;
                @SerializedName("validator_address")
                public String validatorAddress;
                @SerializedName("timestamp")
                public String timestamp;
                @SerializedName("signature")
                public String signature;
            }
        }
    }

    
    public String getBlockHeight() {
        String height = "";
        if (null != block && null != block.header && !TextUtils.isEmpty(block.header.height)) {
            height = block.header.height;
        }
        return height;
    }
}
