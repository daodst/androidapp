

package com.wallet.ctc.model.blockchain;



public class WalletLogoBean {

    private int chooseLogo;
    private int defLogo;
    private int choose;
    private int isdef;
    private String walletName;
    private String blockBrowser;
    private int walletType;

    public WalletLogoBean(int chooseLogo,int defLogo,int choose,int walletType){
        this.chooseLogo=chooseLogo;
        this.defLogo=defLogo;
        this.choose=choose;
        this.walletType=walletType;
    }
    public WalletLogoBean(int defLogo,String walletName,int walletType,String blockBrowser){
        this.chooseLogo=defLogo;
        this.defLogo=defLogo;
        this.walletName=walletName;
        this.blockBrowser=blockBrowser;
        this.walletType=walletType;
    }
    public WalletLogoBean(int chooseLogo,String walletName,int choose,int walletType,int isdef){
        this.chooseLogo=chooseLogo;
        this.defLogo=chooseLogo;
        this.walletName=walletName;
        this.choose=choose;
        this.walletType=walletType;
        this.isdef=isdef;
    }

    public WalletLogoBean(){

    }

    public int getLogo() {
        if(choose==1){
            return chooseLogo;
        }
        return defLogo;
    }

    public int getChoose() {
        return choose;
    }

    public void setChoose(int choose) {
        this.choose = choose;
    }

    public int getWalletType() {
        return walletType;
    }

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public void setWalletType(int walletType) {
        this.walletType = walletType;
    }

    public int getIsdef() {
        return isdef;
    }

    public void setIsdef(int isdef) {
        this.isdef = isdef;
    }

    public String getBlockBrowser() {
        return blockBrowser;
    }

    public void setBlockBrowser(String blockBrowser) {
        this.blockBrowser = blockBrowser;
    }

    public void setDefLogo(int defLogo) {
        this.defLogo = defLogo;
    }
}
