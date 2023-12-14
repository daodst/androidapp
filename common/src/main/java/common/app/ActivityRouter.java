

package common.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

import java.util.ArrayList;

import common.app.base.activity.ContentActivity;
import common.app.base.activity.NewContentActivity;
import common.app.base.base.BaseFragment;
import common.app.base.base.FragmentContract;
import common.app.router_reflex.IWalletOperate;
import common.app.utils.LogUtil;
import common.app.wallet.WalletBridge;



public class ActivityRouter {

    public interface App {
        String A_NodeUnableDialogActivity = "com.app.node.NodeUnableDialogActivity";
        String A_ChainSyncActivity = "com.app.chain.ChainSyncActivity";
        
        String A_ChainBridgeOrdersActivity = "com.wallet.ctc.ui.me.chain_bridge2.orders.ChainBridgeOrdersActivity";
    }

    public interface Im {

        
        String A_DetailChatActivity = "im.vector.app.features.home.room.detail.RoomDetailActivity";
    }

    public interface Lg4e {
        String F_LoginFragment = "com.app.lg4e.ui.fragment.login.LoginFragment";
        String F_SplashFragment = "com.app.lg4e.ui.fragment.splash.SplashFragment";
    }






    public interface Common {
        String A_Web = "common.app.my.Web";
        String F_QRCodeFragment = "common.app.im.ui.fragment.qrcode.QRCodeFragment";
    }

    public interface Wallet {
        String SMPledgeActivity = "com.app.me.destory_group.DestoryGroupActivity";
        String A_NewHomeFragment = "com.wallet.ctc.ui.blockchain.home.NewHomeFragment";
        String A_WALLETBRIDGEIMPL = "com.wallet.ctc.WalletBridgeImpl";
        String A_DappWebViewActivity = "com.wallet.ctc.ui.dapp.DappWebViewActivity";
    }

    public interface Launcher {
        String A_MyQrCode = "im.vector.app.features.usercode.ShowUserCodeFragment";
        String A_QrCodeScan = "im.vector.app.features.qrcode.QrCodeScannerActivity";
        String A_Share = "com.wallet.ctc.ui.me.shareapp.ShareAppActivity";
        String A_RankList = "com.app.levelranking.SMLevelRankingActivity";
        String A_Dapp = "com.wallet.ctc.ui.dapp.list.DappActivity";
        String A_NodeList = "com.wallet.ctc.ui.blockchain.setnode.SettingNodeActivity";
        String A_AddressBook = "com.wallet.ctc.ui.blockchain.addressbook.AddressBookActivity";
        String A_ChainBirge = "com.wallet.ctc.ui.me.chain_bridge2.ChainBridgeActivity2";
        String A_DAO = "com.app.home.ui.VHomeFragment";
        String A_ZHIYA = "com.app.view.dposmarket.DPosMarketActivity";
        String A_SM_Pledge = Wallet.SMPledgeActivity;
        String A_VirtualPhone = "com.wallet.ctc.ui.me.virtualphone.SMVirtualPhoneActivity";
        String A_PrivatePolicy = "com.app.me.policy.PrivacyPolicyActivity";
        
        String A_LoginPage = "im.vector.app.features.MainActivity";

        
        String A_IssuanceCoinActivity = "com.wallet.ctc.ui.blockchain.issuance.IssuanceCoinActivity";
        String A_SettingActivity = "im.vector.app.features.settings.VectorSettingsActivity";
    }

    
    public static boolean isInstanceof(Object obj, String objName) {
        if (null == obj || objName == null) {
            return false;
        }
        if (obj.getClass().getName().equals(objName)) {
            return true;
        } else {
            return false;
        }
    }

    public static String getMainActivityName() {
        return "com.app.base.activity.MainActivity";
    }

    public static Intent getMainActivityIntent(Context from) {

        try {
            Class clazz = Class.forName("com.app.base.activity.MainActivity");
            Intent intent = new Intent(from, clazz);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            return intent;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }


    public static void startActivity(Context from, Intent intent) {
        from.startActivity(intent);
    }

    public static Intent getIntent(Context from, String activityName) {
        try {
            Class clazz = Class.forName(activityName);
            Intent intent = new Intent(from, clazz);
            return intent;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void startActivity(Context from, String activityName) {
        try {
            Class clazz = Class.forName(activityName);
            Intent intent = new Intent(from, clazz);
            from.startActivity(intent);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Intent getNewContentIntent(Context from, String fragmentName) {
        try {
            Intent intent = new Intent(from, NewContentActivity.class);
            intent.putExtra(FragmentContract.CLASS, fragmentName);
            return intent;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void startActivityForResult(Activity from, String activityName, int requestCode) {
        try {
            Class clazz = Class.forName(activityName);
            Intent intent = new Intent(from, clazz);
            from.startActivityForResult(intent, requestCode);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static WalletBridge getWalletBridge(String activityName) {
        try {
            Class clazz = Class.forName(activityName);
            WalletBridge iShopcartService = null;
            try {
                iShopcartService = (WalletBridge) clazz.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.d("zzz", e.toString());
            }
            return iShopcartService;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    
    public static IWalletOperate getWalletOperate() {
        try {
            Class walletOperate = Class.forName("com.wallet.ctc.router_reflex.WalletOprate");
            return (IWalletOperate) walletOperate.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Intent getEmptyContentIntent(Context from, String fragmentName) {
        Intent intent = new Intent(from, ContentActivity.class);
        intent.putExtras(BaseFragment.setEmptyBundle(fragmentName));
        return intent;
    }

    public static Intent getStringContentIntent(Context from, String fragmentName, String paramData) {
        Intent intent = new Intent(from, ContentActivity.class);
        intent.putExtras(BaseFragment.setBundle(paramData, fragmentName));
        return intent;
    }

    
    public static void startEmptyContentActivity(Context from, String fragmentName) {
        Intent intent = new Intent(from, ContentActivity.class);
        intent.putExtras(BaseFragment.setEmptyBundle(fragmentName));
        from.startActivity(intent);
    }

    public static void startStrContentActivity(Context from, String fragmentName, String paramData) {
        Intent intent = new Intent(from, ContentActivity.class);
        intent.putExtras(BaseFragment.setBundle(paramData, fragmentName));
        from.startActivity(intent);
    }

    public static void startArrayStrContentActivity(Context from, String fragmentName, ArrayList<String> paramData) {
        Intent intent = new Intent(from, ContentActivity.class);
        intent.putExtras(BaseFragment.setArrayStrBundle(paramData, fragmentName));
        from.startActivity(intent);
    }

    public static void startArrayParContentActivity(Context from, String fragmentName, ArrayList<? extends Parcelable> paramData) {
        Intent intent = new Intent(from, ContentActivity.class);
        intent.putExtras(BaseFragment.setArrayParBundle(paramData, fragmentName));
        from.startActivity(intent);
    }

    public static void startPContentActivity(Context from, String fragmentName, Parcelable paramData) {
        Intent intent = new Intent(from, ContentActivity.class);
        intent.putExtras(BaseFragment.setBundle(paramData, fragmentName));
        from.startActivity(intent);
    }


}
