

package com.wallet.ctc.model.blockchain;
import wallet.core.jni.PrivateKey;
import wallet.core.jni.PublicKey;
import wallet.core.jni.StoredKey;


public class TrustWalletBean {
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public StoredKey getStoredKey() {
        return storedKey;
    }

    public void setStoredKey(StoredKey storedKey) {
        this.storedKey = storedKey;
    }

    private StoredKey storedKey;


    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }


    public String getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    private String mnemonic;

}
