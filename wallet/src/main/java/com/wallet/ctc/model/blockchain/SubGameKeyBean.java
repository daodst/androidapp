

package com.wallet.ctc.model.blockchain;

import java.util.List;



public class SubGameKeyBean {

    private String pubKey;
    private String mnemonic;
    private String rawSeed;
    private String encoded;
    private EncodingBean encoding;
    private String address;
    private Object meta;

    public static class EncodingBean {
        private List<String> content;
        private List<String> type;
        private String version;
    }

    public String getPubKey() {
        return pubKey;
    }

    public void setPubKey(String pubKey) {
        this.pubKey = pubKey;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    public String getRawSeed() {
        return rawSeed;
    }

    public void setRawSeed(String rawSeed) {
        this.rawSeed = rawSeed;
    }

    public String getEncoded() {
        return encoded;
    }

    public void setEncoded(String encoded) {
        this.encoded = encoded;
    }

    public EncodingBean getEncoding() {
        return encoding;
    }

    public void setEncoding(EncodingBean encoding) {
        this.encoding = encoding;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Object getMeta() {
        return meta;
    }

    public void setMeta(Object meta) {
        this.meta = meta;
    }
}
