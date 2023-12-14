package com.app;

import static com.wallet.ctc.crypto.WalletUtil.MCC_COIN;

import android.text.TextUtils;
import android.util.Log;

import com.wallet.ctc.crypto.ChatSdk;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.DBManager;
import com.wallet.ctc.db.SettingNodeEntity;
import com.wallet.ctc.model.blockchain.EvmosGatewayBean;
import com.wallet.ctc.model.blockchain.EvmosGatewayListBean;
import com.wallet.ctc.model.blockchain.RpcApi;

import java.util.ArrayList;
import java.util.List;

import common.app.utils.AllUtils;
import common.app.utils.NetWorkUtils;
import common.app.utils.SpUtil;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AppController {
    private static final String TAG = "AppController";

    
    public static void initSdkNodeAddr() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                String nodeUrl = SpUtil.getDefNode(WalletUtil.MCC_COIN);
                Log.i(TAG, "setSdkAddr...." + nodeUrl);
                ChatSdk.setNodeAddr(nodeUrl);
                String sdkNodeUrl = AllUtils.getHomeUrl(nodeUrl);
                if (!TextUtils.isEmpty(sdkNodeUrl)) {
                    Log.i(TAG, "createClient..." + sdkNodeUrl);
                    ChatSdk.createClient(sdkNodeUrl);
                } else {
                    Log.e(TAG, "sdk nodeurl is error");
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    
    public static void checkHostAndRefreshGateWayListCache() {
        checkHostAndRefreshGateWayListCache(false);
    }

    public static void checkHostAndRefreshGateWayListCache(boolean showToast) {
        checkHostStatsAndAlert(showToast);
        refreshFmNodeListCache();
    }

    
    public static void checkHostStatsAndAlert(boolean showToast) {
        
        if (showToast) {
            NetWorkUtils.checkHostStatsAndAlertToast(App.getInstance().getApplicationContext(), SpUtil.getDefNode(MCC_COIN));
        } else {
            NetWorkUtils.checkHostStatsAndAlert(App.getInstance().getApplicationContext(), SpUtil.getDefNode(MCC_COIN));
        }
    }

    
    public static void refreshFmNodeListCache() {
        RpcApi rpcApi = new RpcApi();
        rpcApi.getEvmosGatewayList(1, 0, 0)
                .observeOn(Schedulers.io()).subscribe(new Observer<EvmosGatewayListBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(EvmosGatewayListBean evmosGatewayListBean) {
                        if (null != evmosGatewayListBean && evmosGatewayListBean.isSuccess() && null != evmosGatewayListBean.data && evmosGatewayListBean.data.size() > 0) {
                            List<SettingNodeEntity> saveNodeList = new ArrayList<>();
                            for (int i = 0; i < evmosGatewayListBean.data.size(); i++) {
                                EvmosGatewayBean.Data gateWay = evmosGatewayListBean.data.get(i);
                                
                                String mainPhoneIndex = "";
                                if (null != gateWay.gateway_num && gateWay.gateway_num.size() > 0) {
                                    mainPhoneIndex = gateWay.gateway_num.get(0).number_index;
                                }
                                String gateWayAddr = gateWay.gateway_address;
                                saveNodeList.add(new SettingNodeEntity(gateWay.gateway_name, gateWay.gateway_url, MCC_COIN, false,
                                        0, mainPhoneIndex, gateWay.token, gateWay.online, gateWayAddr));
                            }
                            if (saveNodeList.size() > 0) {
                                
                                DBManager.getInstance(App.getInstance().getApplicationContext()).deleteAllDefNode(MCC_COIN);
                                
                                DBManager.getInstance(App.getInstance().getApplicationContext()).insertListNode(saveNodeList);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }


}
