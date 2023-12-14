package com.benny.openlauncher.util;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.benny.openlauncher.R;
import com.benny.openlauncher.activity.HomeActivity;
import com.benny.openlauncher.activity.HomeActivityListener;
import com.benny.openlauncher.activity.OnBoardActivity;
import com.benny.openlauncher.model.Item;

import java.util.HashMap;
import java.util.Map;

import common.app.ActivityRouter;
import common.app.mall.util.ToastUtil;
import common.app.utils.AppVerUtil;
import common.app.utils.AppWidgetUtils;
import common.app.utils.NetUtils;


public class TabActionsHelper {
    public static final int ACTION_CALL_PHONE = AppWidgetUtils.ACTION_CALL_PHONE;
    public static final int ACTION_CHAT = AppWidgetUtils.ACTION_CHAT;
    public static final int ACTION_CHAT_DETAIL = AppWidgetUtils.ACTION_CHAT_DETAIL;
    public static final int ACTION_DEFAULT = AppWidgetUtils.ACTION_DEFAULT;
    public static final int ACTION_WALLET = AppWidgetUtils.ACTION_WALLET;
    public static final int ACTION_MINE = AppWidgetUtils.ACTION_MINE;
    public static final int ACTION_ROOT_WALLET = AppWidgetUtils.ACTION_ROOT_WALLET;

    public static final int FLAG_FM = 0;
    public static final int FLAG_DAO = 1;
    public static final int FLAG_My_QRCode = 2;
    public static final int FLAG_SCAN = 3;
    public static final int FLAG_VPN = 4;
    public static final int FLAG_TRANSFER = 5;
    public static final int FLAG_COLLECT_MONEY = 6;
    public static final int FLAG_TRADE = 7;
    public static final int FLAG_DESTROY_MINING = 8;
    public static final int FLAG_ACQUISITION_OF_VOTING_RIGHTS = 9;
    public static final int FLAG_SHARE = 10;
    public static final int FLAG_RANKINGLIST = 11;
    public static final int FLAG_DAPP = 12;
    public static final int FLAG_CHOOSEPOINT = 13;
    public static final int FLAG_ADDRESS = 14;
    public static final int FLAG_PLEDGE_MANAGEMENT = 15;
    public static final int FLAG_CROSS_CHAIN_BRIDGE = 16;
    public static final int FLAG_CHANGE_LANGUAGE = 17;
    public static final int FLAG_CHECKUPDATE = 18;
    public static final int FLAG_INFORMATION_ON_THE_CHAIN = 19;
    public static final int FLAG_VIRTUAL_PHONE_BOOK = 20;
    public static final int FLAG_PRIVACY_SETTING = 21;
    public static final int FLAG_CHAT_SETTING = 22;
    public static final int FLAG_ONE_KEY_PUBLISH_COIN = 23;
    public static final int FLAG_COMPUTING_POWER_MANAGEMENT = 24;
    public static final int FLAG_DST_NUMBER = 25;
    public static final int FLAG_EXCHANGE = 26;
    public static final int FLAG_DAPP_STORE = 27;
    public static final int FLAG_SETTING = 28;
    public static final int FLAG_RECRUIT = 29;
    public static final int FLAG_DST_REDUCE_PLAN = 30;

    public static HomeActivityListener mHomeActivity;

    public static void action(View _view, int actionId) {
        action(_view, actionId, new HashMap<>());
    }

    public static void setHomeActivity(HomeActivityListener homeActivity) {
        mHomeActivity = homeActivity;
    }

    public static void action(View _view, int actionId, Map<String, String> args) {
        Intent intent = new Intent();
        Context context = _view.getContext();
        switch (actionId) {
            case ACTION_CALL_PHONE:
                
                if (null != mHomeActivity) {
                    mHomeActivity.showCallPhoneFragment();
                }
                break;
            case ACTION_CHAT:
                
                if (null != mHomeActivity) {
                    mHomeActivity.showChatFragment();
                }
                break;
            case ACTION_CHAT_DETAIL:
                intent = ActivityRouter.getIntent(context, ActivityRouter.Im.A_DetailChatActivity);
                intent.setAction("ROOM_DETAILS_FROM_SHORTCUT");
                intent.putExtra("EXTRA_ROOM_ID", args.get("roomId"));
                context.startActivity(intent);
                break;
            case ACTION_DEFAULT:
                HomeActivity.Companion.getLauncher().openAppDrawer(_view, 0, 0);
                break;
            case ACTION_WALLET:
                
                if (null != mHomeActivity) {
                    mHomeActivity.showWalletFragment();
                }
                break;
            case ACTION_MINE:
                
                if (null != mHomeActivity) {
                    mHomeActivity.showMeFragment();
                }
                break;
            case ACTION_ROOT_WALLET:
                
                if (null != mHomeActivity && null != args) {
                    String from = args.get("from");
                    String to = args.get("to");
                    mHomeActivity.onRootWalletTouch(from, to);
                }
                break;
        }
    }

    
    public static void startIntent(Item item) {
        if (item._type != Item.Type.STABLE || null == mHomeActivity) return;
        mHomeActivity.onStableItemClick(item);
    }

    @Deprecated
    public Intent stableIntent(Context context, String name, int position) {
        if (null == mHomeActivity) return null;
        Intent intent = new Intent();
        switch (position) {
            case 0://"FM":
                intent = new Intent(context, OnBoardActivity.class);
                break;
            case 1://"DAO":
                intent = needLoginIntent(context, ActivityRouter.Launcher.A_DAO, false);
                break;
            case 2://""://com.app.my.MyQR
                intent = ActivityRouter.getIntent(context, ActivityRouter.Launcher.A_MyQrCode);
                break;
            case 3://""://im.vector.app.features.qrcode.QrCodeScannerActivity
                intent = ActivityRouter.getIntent(context, ActivityRouter.Launcher.A_QrCodeScan);
                break;
            case 4://"VPN":
                break;
            case 5://"":
                break;
            case 6://"":
                break;
            case 7://"":
                break;
            case 8://"":
                break;
            case 9://"":
                
                intent = ActivityRouter.getIntent(context, ActivityRouter.Launcher.A_ZHIYA);
                break;
            case 10://""://com.wallet.ctc.ui.me.shareapp.ShareAppActivity
                intent = ActivityRouter.getIntent(context, ActivityRouter.Launcher.A_Share);
                break;
            case 11://""://com.app.levelranking.SMLevelRankingActivity
                intent = needLoginIntent(context, ActivityRouter.Launcher.A_RankList);
                break;
            case 12://"DAPP"://com.wallet.ctc.ui.dapp.list.DappActivity
                intent = ActivityRouter.getIntent(context, ActivityRouter.Launcher.A_Dapp);
                break;
            case 13://""://com.app.node.NodeListsActivity
                intent = needLoginIntent(context, ActivityRouter.Launcher.A_NodeList);
                break;
            case 14://""://com.wallet.ctc.ui.blockchain.addressbook.AddressBookAddActivity
                intent = ActivityRouter.getIntent(context, ActivityRouter.Launcher.A_AddressBook);
                break;
            case 15://"":
                
                intent = needLoginIntent(context, ActivityRouter.Launcher.A_SM_Pledge);
                break;
            case 16://""://com.wallet.ctc.ui.me.chain_bridge.ChainBridgeActivity
                intent = needLoginIntent(context, ActivityRouter.Launcher.A_ChainBirge);
                break;
            case 17://"":
                
                break;
            case 18://"":
                if (!NetUtils.isNetworkConnected(context)) {
                    ToastUtil.showToast(context.getString(R.string.connect_failuer_toast));
                }
                new AppVerUtil(context).checkVer(null);
                break;
            case 19://"":
                intent = needLoginIntent(context, ActivityRouter.App.A_ChainSyncActivity);
                break;
            case 20://"":
                
                intent = needLoginIntent(context, ActivityRouter.Launcher.A_VirtualPhone);
                break;
            case 21://"":
                intent = ActivityRouter.getIntent(context, ActivityRouter.Launcher.A_PrivatePolicy);
                break;
            case 22://"":
                break;
            case 23://:
                break;
            default:
                intent = new Intent(context, OnBoardActivity.class);
                break;
        }
        return intent;
    }

    @Deprecated
    private Intent needLoginIntent(Context context, String path) {
        return needLoginIntent(context, path, true);
    }

    @Deprecated
    private Intent needLoginIntent(Context context, String path, boolean isActivity) {
        if (!mHomeActivity.isLogined()) {
            return ActivityRouter.getIntent(context, ActivityRouter.Launcher.A_LoginPage);
        } else {
            if (isActivity) return ActivityRouter.getIntent(context, path);
            else return ActivityRouter.getNewContentIntent(context, path);
        }
    }
}
