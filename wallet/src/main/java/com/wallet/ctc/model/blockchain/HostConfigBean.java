

package com.wallet.ctc.model.blockchain;

import java.util.List;

public class HostConfigBean {

    

    private String name;
    private String desc;
    private String logo;
    private String type;
    private int chain_id;
    private int network_id;
    private String chain_base;
    private String symbol;
    private String address_prifix;
    private List<String> rpc_urls;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getChain_id() {
        return chain_id;
    }

    public void setChain_id(int chain_id) {
        this.chain_id = chain_id;
    }

    public int getNetwork_id() {
        return network_id;
    }

    public void setNetwork_id(int network_id) {
        this.network_id = network_id;
    }

    public String getChain_base() {
        return chain_base;
    }

    public void setChain_base(String chain_base) {
        this.chain_base = chain_base;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getAddress_prifix() {
        return address_prifix;
    }

    public void setAddress_prifix(String address_prifix) {
        this.address_prifix = address_prifix;
    }

    public List<String> getRpc_urls() {
        return rpc_urls;
    }

    public void setRpc_urls(List<String> rpc_urls) {
        this.rpc_urls = rpc_urls;
    }
}
