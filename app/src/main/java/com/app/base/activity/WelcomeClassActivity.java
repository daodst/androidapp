

package com.app.base.activity;

import static com.wallet.ctc.crypto.WalletUtil.BNB_COIN;
import static com.wallet.ctc.crypto.WalletUtil.ETH_COIN;
import static com.wallet.ctc.crypto.WalletUtil.MCC_COIN;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.app.App;
import com.app.AppController;
import com.app.pojo.NodeConfigBean;
import com.google.gson.Gson;
import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.crypto.ChainsRpcsUtil;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.DBManager;
import com.wallet.ctc.db.SettingNodeEntity;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.DefaultAssetsBean;
import com.wallet.ctc.model.blockchain.RpcApi;
import com.wallet.ctc.model.me.DefValueBean;
import com.wallet.ctc.util.SettingPrefUtil;
import com.wallet.ctc.util.WalletSpUtil;

import net.gsantner.opoc.preference.OtherSpUtils;

import java.util.ArrayList;
import java.util.List;

import common.app.utils.FileUtils;
import common.app.utils.SpUtil;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;



public class WelcomeClassActivity {

    private static final String TAG = "ContentActivity";

    
    protected void onCreate(Bundle savedInstanceState) {
        String currentModifyVersionName = OtherSpUtils.APP_DESKTOP_MODIFY_VERSION;
        
        String exitsVersionName = OtherSpUtils.getInstance().getAppNotifyVersionName();
        if (!TextUtils.isEmpty(exitsVersionName) && !currentModifyVersionName.equals(exitsVersionName)) {
            OtherSpUtils.getInstance().putHomeDefaultAppNeedChange(true);
            OtherSpUtils.getInstance().putAppNotifyVersionName(currentModifyVersionName);
        } else {
            OtherSpUtils.getInstance().putAppNotifyVersionName(currentModifyVersionName);
        }
        try {
            initRequstData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public WelcomeClassActivity() {
        String currentModifyVersionName = OtherSpUtils.APP_DESKTOP_MODIFY_VERSION;
        
        String exitsVersionName = OtherSpUtils.getInstance().getAppNotifyVersionName();
        if (!TextUtils.isEmpty(exitsVersionName) && !currentModifyVersionName.equals(exitsVersionName)) {
            OtherSpUtils.getInstance().putHomeDefaultAppNeedChange(true);
            OtherSpUtils.getInstance().putAppNotifyVersionName(currentModifyVersionName);
        } else {
            OtherSpUtils.getInstance().putAppNotifyVersionName(currentModifyVersionName);
        }
    }

    public void initRequstData() {

        if (initNodeUrl()) {
            App.getInstance().dealyInit();

            
            
            
            initChainRpc();

            
            AppController.initSdkNodeAddr();

            
            initDefaultAssets();

        }
    }


    
    private boolean initNodeUrl() {
        
        String nodeJsonStr = FileUtils.getAssetsFileConent(App.getInstance(), "node_url.json");
        if (TextUtils.isEmpty(nodeJsonStr)) {
            Toast.makeText(App.getInstance(), "App can't use", Toast.LENGTH_SHORT).show();
            
            return false;
        }
        Gson gson = new Gson();
        NodeConfigBean nodeConfig = gson.fromJson(nodeJsonStr, NodeConfigBean.class);

        if (null == nodeConfig || TextUtils.isEmpty(nodeConfig.node_address) || TextUtils.isEmpty(nodeConfig.number_index) || TextUtils.isEmpty(nodeConfig.chain_id)) {
            Toast.makeText(App.getInstance(), "Illegal App can't use", Toast.LENGTH_SHORT).show();
            
            return false;
        }

        NodeConfigBean settingConfig = NodeConfigBean.prase(nodeConfig);
        if (null == settingConfig || !settingConfig.isValidate()) {
            Toast.makeText(App.getInstance(), "Illegal nodeUrl can't use", Toast.LENGTH_SHORT).show();
            
            return false;
        }

        
        String nodeUrl = SpUtil.getDefNode(MCC_COIN);


        boolean forceRefresh = BuildConfig.DEBUG;
        if (forceRefresh) {
            nodeUrl = "";
        }
        System.out.println("-------WelcomeClassActivity----------------------" + settingConfig.tts_url);
        
        String nodeInfoUrl = SpUtil.getNodeInfoUrl();

        
        if (TextUtils.isEmpty(nodeUrl) || TextUtils.isEmpty(nodeInfoUrl)) {
            WalletUtil.saveNodeInfo(MCC_COIN, settingConfig.node_address, settingConfig.number_index,
                    settingConfig.node_name, settingConfig.im_url, settingConfig.node_smart_url,
                    settingConfig.node_info_url, settingConfig.chatCall, settingConfig.chain_id, settingConfig.ws_url, settingConfig.tts_url);
        }
        return true;
    }

    
    private void initChainRpc() {
        List<Integer> coinsList = WalletSpUtil.getEnableCoinTypeList();
        if (null == coinsList || coinsList.size() == 0) {
            return;
        }
        for (Integer coinType : coinsList) {
            if (coinType == MCC_COIN) {
                
                continue;
            }
            
            List<String> appSetRpcUrls = ChainsRpcsUtil.getSuggestRpcUrls(coinType);
            if (appSetRpcUrls == null || appSetRpcUrls.size() == 0) {
                continue;
            }
            String nowRpcUrl = SpUtil.getDefNode(coinType);
            List<SettingNodeEntity> defaultList = new ArrayList<>();
            for (String rpcUrl : appSetRpcUrls) {
                if (!TextUtils.isEmpty(rpcUrl)) {
                    boolean choose = rpcUrl.equals(nowRpcUrl);
                    String coinName = WalletDBUtil.getInstent(App.getInstance()).getWalletName(coinType);
                    defaultList.add(new SettingNodeEntity(coinName, rpcUrl, coinType, choose, 0));
                }
            }

            
            DBManager.getInstance(App.getInstance()).deleteAllDefNode(coinType);
            DBManager.getInstance(App.getInstance()).insertListNode(defaultList);
        }
    }

    
    private RpcApi mRpcApi;

    
    public void initDefaultAssets() {
        if (mRpcApi == null) {
            mRpcApi = new RpcApi();
        }
        mRpcApi.getDefaultAssets().subscribeOn(Schedulers.io())
                .subscribe(new Observer<DefaultAssetsBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(DefaultAssetsBean data) {
                        if (null != data && data.isSuccess()) {
                            List<AssertBean> mustmcc = new ArrayList<>();
                            List<AssertBean> musteth = new ArrayList<>();
                            List<AssertBean> mustbnb = new ArrayList<>();
                            List<DefValueBean> coins = data.getAsserts();
                            String mccCoin = BuildConfig.EVMOS_FAKE_UNINT;
                            if (null != coins && coins.size() > 0) {
                                for (int i = 0; i < coins.size(); i++) {
                                    DefValueBean defValueBean = coins.get(i);
                                    String key = defValueBean.getKey();
                                    String contract = defValueBean.getAddress();
                                    String logo = defValueBean.getLogo();
                                    String symbol = defValueBean.getSymbol();
                                    if (TextUtils.isEmpty(logo)) {
                                        if ("usdt".equalsIgnoreCase(symbol)) {
                                            logo = "res://mipmap/usdt_logo";
                                        } else {
                                            logo = "res://drawable/coin_default";
                                        }
                                    }

                                    if (!TextUtils.isEmpty(contract)) {
                                        
                                        if (TextUtils.isEmpty(key) || mccCoin.equalsIgnoreCase(key)) {
                                            AssertBean assertBean = new AssertBean(logo, defValueBean.getSymbol(), defValueBean.getName(), defValueBean.getAddress(), "60000", defValueBean.getDecimal() + "", MCC_COIN, 0);
                                            mustmcc.add(assertBean);
                                        } else if ("eth".equalsIgnoreCase(key)) {
                                            AssertBean assertBean = new AssertBean(logo, defValueBean.getSymbol(), defValueBean.getName(), defValueBean.getAddress(), "60000", defValueBean.getDecimal() + "", ETH_COIN, 0);
                                            musteth.add(assertBean);
                                        } else if ("bsc".equalsIgnoreCase(key)) {
                                            AssertBean assertBean = new AssertBean(logo, defValueBean.getSymbol(), defValueBean.getName(), defValueBean.getAddress(), "60000", defValueBean.getDecimal() + "", BNB_COIN, 0);
                                            mustbnb.add(assertBean);
                                        }
                                    }
                                }
                                SettingPrefUtil.setMustAssets(App.getInstance(), App.getInstance().getString(com.wallet.ctc.R.string.default_token_name).toUpperCase(), mustmcc);
                                SettingPrefUtil.setMustAssets(App.getInstance(), "ETH", musteth);
                                SettingPrefUtil.setMustAssets(App.getInstance(), "BNB", mustbnb);
                                WalletDBUtil.getInstent(App.getInstance()).checkAssest();
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
