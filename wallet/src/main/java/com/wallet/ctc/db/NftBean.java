

package com.wallet.ctc.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;

@Entity
public class NftBean implements Serializable {
    private static final long serialVersionUID = 1001;
    @Id(autoincrement = true)
    private Long id;  
    
    public String walletAddress;

    
    public int walletType;

    
    public String token_address;

    
    public String token_id;

    public String block_number_minted;
    public String owner_of;
    public String block_number;
    public String amount;
    public String contract_type;
    public String name;
    public String symbol;
    public String token_uri;
    @Transient
    public int tokenCount;
    @Transient
    public String metadata;
    @Transient
    public String synced_at;
    @Transient
    public String message;
    @Transient
    public int is_valid;
    @Transient
    public int syncing;
    @Transient
    public int frozen;









    @Generated(hash = 629291531)
    public NftBean(Long id, String walletAddress, int walletType,
            String token_address, String token_id, String block_number_minted,
            String owner_of, String block_number, String amount,
            String contract_type, String name, String symbol, String token_uri) {
        this.id = id;
        this.walletAddress = walletAddress;
        this.walletType = walletType;
        this.token_address = token_address;
        this.token_id = token_id;
        this.block_number_minted = block_number_minted;
        this.owner_of = owner_of;
        this.block_number = block_number;
        this.amount = amount;
        this.contract_type = contract_type;
        this.name = name;
        this.symbol = symbol;
        this.token_uri = token_uri;
    }

    @Generated(hash = 1846952440)
    public NftBean() {
    }









    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWalletAddress() {
        return this.walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public int getWalletType() {
        return this.walletType;
    }

    public void setWalletType(int walletType) {
        this.walletType = walletType;
    }

    public String getToken_address() {
        return this.token_address;
    }

    public void setToken_address(String token_address) {
        this.token_address = token_address;
    }

    public String getToken_id() {
        return this.token_id;
    }

    public void setToken_id(String token_id) {
        this.token_id = token_id;
    }

    public String getBlock_number_minted() {
        return this.block_number_minted;
    }

    public void setBlock_number_minted(String block_number_minted) {
        this.block_number_minted = block_number_minted;
    }

    public String getOwner_of() {
        return this.owner_of;
    }

    public void setOwner_of(String owner_of) {
        this.owner_of = owner_of;
    }

    public String getBlock_number() {
        return this.block_number;
    }

    public void setBlock_number(String block_number) {
        this.block_number = block_number;
    }

    public String getAmount() {
        return this.amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getContract_type() {
        return this.contract_type;
    }

    public void setContract_type(String contract_type) {
        this.contract_type = contract_type;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getToken_uri() {
        return this.token_uri;
    }

    public void setToken_uri(String token_uri) {
        this.token_uri = token_uri;
    }

    @Override
    public String toString() {
        return "NftBean{" +
                "id=" + id +
                ", walletAddress='" + walletAddress + '\'' +
                ", walletType=" + walletType +
                ", token_address='" + token_address + '\'' +
                ", token_id='" + token_id + '\'' +
                ", block_number_minted='" + block_number_minted + '\'' +
                ", owner_of='" + owner_of + '\'' +
                ", block_number='" + block_number + '\'' +
                ", amount='" + amount + '\'' +
                ", contract_type='" + contract_type + '\'' +
                ", name='" + name + '\'' +
                ", symbol='" + symbol + '\'' +
                ", token_uri='" + token_uri + '\'' +
                ", metadataBean=" + metadata +
                ", synced_at='" + synced_at + '\'' +
                ", message='" + message + '\'' +
                ", is_valid=" + is_valid +
                ", syncing=" + syncing +
                ", frozen=" + frozen +
                '}';
    }
}
