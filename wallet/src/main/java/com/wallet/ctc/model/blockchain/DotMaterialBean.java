

package com.wallet.ctc.model.blockchain;


public class DotMaterialBean {

    private DotBalanceBean.AtBean at;

    private String genesisHash;
    private String chainName;
    private String specName;
    private String specVersion;
    private String txVersion;

    public DotBalanceBean.AtBean getAt() {
        return at;
    }

    public void setAt(DotBalanceBean.AtBean at) {
        this.at = at;
    }

    public String getGenesisHash() {
        return genesisHash;
    }

    public void setGenesisHash(String genesisHash) {
        this.genesisHash = genesisHash;
    }

    public String getChainName() {
        return chainName;
    }

    public void setChainName(String chainName) {
        this.chainName = chainName;
    }

    public String getSpecName() {
        return specName;
    }

    public void setSpecName(String specName) {
        this.specName = specName;
    }

    public String getSpecVersion() {
        return specVersion;
    }

    public void setSpecVersion(String specVersion) {
        this.specVersion = specVersion;
    }

    public String getTxVersion() {
        return txVersion;
    }

    public void setTxVersion(String txVersion) {
        this.txVersion = txVersion;
    }
}
