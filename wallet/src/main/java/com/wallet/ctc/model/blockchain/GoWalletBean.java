

package com.wallet.ctc.model.blockchain;



public class GoWalletBean {
    

    private AddressBean address;
    private GoKeystory keystore;
    private String mnemonic;

    public AddressBean getAddress() {
        return address;
    }

    public void setAddress(AddressBean address) {
        this.address = address;
    }

    public GoKeystory getKeystore() {
        return keystore;
    }

    public void setKeystore(GoKeystory keystore) {
        this.keystore = keystore;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    public static class AddressBean {
        

        private String address;
        private String publickey;
        private String privatekey;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getPublickey() {
            if(null==publickey){
                publickey="";
            }
            return publickey;
        }

        public void setPublickey(String publickey) {
            this.publickey = publickey;
        }

        public String getPrivatekey() {
            return privatekey;
        }

        public void setPrivatekey(String privatekey) {
            this.privatekey = privatekey;
        }
    }
}
