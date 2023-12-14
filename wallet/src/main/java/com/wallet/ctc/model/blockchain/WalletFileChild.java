

package com.wallet.ctc.model.blockchain;

import org.web3j.crypto.WalletFile;



public class WalletFileChild {
    private String address;
    private Crypto crypto;
    private String id;
    private int version;
    public static class Crypto {
        private String cipher;
        private String ciphertext;
        private WalletFile.CipherParams cipherparams;

        private String kdf;
        private WalletFile.ScryptKdfParams kdfparams;

        private String mac;

        public String getCipher() {
            return cipher;
        }

        public void setCipher(String cipher) {
            this.cipher = cipher;
        }

        public String getCiphertext() {
            return ciphertext;
        }

        public void setCiphertext(String ciphertext) {
            this.ciphertext = ciphertext;
        }

        public WalletFile.CipherParams getCipherparams() {
            return cipherparams;
        }

        public void setCipherparams(WalletFile.CipherParams cipherparams) {
            this.cipherparams = cipherparams;
        }

        public String getKdf() {
            return kdf;
        }

        public void setKdf(String kdf) {
            this.kdf = kdf;
        }

        public WalletFile.ScryptKdfParams getKdfparams() {
            return kdfparams;
        }

        public void setKdfparams(WalletFile.ScryptKdfParams kdfparams) {
            this.kdfparams = kdfparams;
        }

        public String getMac() {
            return mac;
        }

        public void setMac(String mac) {
            this.mac = mac;
        }
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Crypto getCrypto() {
        return crypto;
    }

    public void setCrypto(Crypto crypto) {
        this.crypto = crypto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
