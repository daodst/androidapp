

package com.wallet.ctc.model.blockchain;



public class SgbTransHistory {
    private String amount;
    private Integer block_num;
    private Integer block_timestamp;
    private String extrinsic_index;
    private String fee;
    private String from;
    private String hash;
    private String module;
    private Integer nonce;
    private Boolean success;
    private String to;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Integer getBlock_num() {
        return block_num;
    }

    public void setBlock_num(Integer block_num) {
        this.block_num = block_num;
    }

    public Integer getBlock_timestamp() {
        return block_timestamp;
    }

    public void setBlock_timestamp(Integer block_timestamp) {
        this.block_timestamp = block_timestamp;
    }

    public String getExtrinsic_index() {
        return extrinsic_index;
    }

    public void setExtrinsic_index(String extrinsic_index) {
        this.extrinsic_index = extrinsic_index;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public Integer getNonce() {
        return nonce;
    }

    public void setNonce(Integer nonce) {
        this.nonce = nonce;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
