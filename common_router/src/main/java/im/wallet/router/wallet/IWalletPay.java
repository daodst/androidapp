package im.wallet.router.wallet;

import android.app.Activity;
import android.content.Context;

import java.util.List;

import im.wallet.router.listener.TranslationListener;
import im.wallet.router.util.Consumer;
import im.wallet.router.wallet.pojo.DeviceGroupMember;
import im.wallet.router.wallet.pojo.EvmosDaoParams;
import im.wallet.router.wallet.pojo.EvmosGroupDataBean;
import im.wallet.router.wallet.pojo.EvmosMyGroupDataBean;
import im.wallet.router.wallet.pojo.EvmosMyGroupList;
import im.wallet.router.wallet.pojo.SignInfo;

public interface IWalletPay {


    public static final int UNKNOW_WALLET_TYPE = -1;

    
    void showPayDialog(Activity activity, String fromAddr, String toAddress, String amount, String coinName, Consumer<Boolean> consumer);

    
    void disPay();

    void showPayDialog(Activity activity, String address, Consumer<String> consumer);

    SignInfo getSignInfo(Activity activity, String address);

    
    EvmosMyGroupList httpGetMyDeviceGroups(String account);

    
    EvmosGroupDataBean httpGetDeviceGroupData(String deviceGroupId);

    
    EvmosMyGroupDataBean httpGetSomeGroupData(String account, String deviceGroupId);


    
    EvmosDaoParams httpGetDaoParams(Context context, String fromAddr, String coinName);

    
    void createDeviceGroup(Context context, String fromAddr, String groupId, String deviceRatio, String salaryRatio,
                           String burnAmount, String clusterName, String freezeAmount, List<DeviceGroupMember> members, TranslationListener callBack);

    
    void addDeviceMembers(Context context, String fromAddr, String groupId,
                                 List<DeviceGroupMember> members, TranslationListener callBack);

    
    void changeGroupId(Context context, String fromAddr, String groupId, String newGroupId,
                              TranslationListener callBack);


    
    String getFingerPayKey(Context context);

    
    void changeDeviceGroupName(Context context, String fromAddr, String groupId, String newGroupName, TranslationListener callBack);
}
