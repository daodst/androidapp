

package common.app.router_reflex;


public interface IWalletOperate {

    
    public String getAllAddress();

    
    public int getWalletType();

    
    public String getWalletEncriptPwd();

    
    public String getDecriptMd5(String pwd);

    
    public void tuiJianPay();
}
