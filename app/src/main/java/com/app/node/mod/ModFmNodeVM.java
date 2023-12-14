

package com.app.node.mod;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.app.AppController;
import com.app.R;
import com.app.pojo.NodeConfigBean;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.model.blockchain.RpcApi;

import common.app.base.BaseViewModel;
import common.app.mall.util.ToastUtil;
import common.app.utils.NetWorkUtils;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


public class ModFmNodeVM extends BaseViewModel {
    public MutableLiveData<String> mErrorToastLD;
    public MutableLiveData<Boolean> mAddResultLD;
    public MutableLiveData<String> mAddAlertLD;
    private CompositeDisposable mDisposable;
    private RpcApi mRpcApi;

    
    public ModFmNodeVM(@NonNull Application application) {
        super(application);
        mDisposable = new CompositeDisposable();
        mErrorToastLD = new MutableLiveData<>();
        mAddResultLD = new MutableLiveData<>();
        mAddAlertLD = new MutableLiveData<>();
        mRpcApi = new RpcApi();
    }

    
    private String mUrl, mNoSegment, mNodeName, mGateWayAddr;

    public void modNode(String url, String noSegment, String nodeName) {
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(nodeName)) {
            return;
        }
        mUrl = url;
        mNoSegment = noSegment;
        mNodeName = nodeName;
        mGateWayAddr = "";
        
        checkUrlTime(url);
    }


    
    public void doAddNode() {
        
        NodeConfigBean nodeConfig = new NodeConfigBean(mUrl, mNoSegment, mNodeName);
        NodeConfigBean settingConfig = NodeConfigBean.prase(nodeConfig);
        if (null == settingConfig || !settingConfig.isValidate()) {
            ToastUtil.showToast("Illegal nodeUrl can't use");
            return;
        }
        WalletUtil.saveNodeInfo(WalletUtil.MCC_COIN, settingConfig.node_address, settingConfig.number_index,
                settingConfig.node_name, settingConfig.im_url, settingConfig.node_smart_url,
                settingConfig.node_info_url, settingConfig.chatCall, settingConfig.chain_id, settingConfig.ws_url, settingConfig.tts_url);
        
        AppController.initSdkNodeAddr();
        mAddResultLD.setValue(true);
    }

    private void checkUrlTime(String url) {
        showLoadingDialog("");
        NetWorkUtils.checkUrlReachableOb(getApplication(), url).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable.add(d);
                    }

                    @Override
                    public void onNext(Integer state) {
                        dismissLoadingDialog();
                        if (null == state) {
                            showToast(getApplication().getString(R.string.data_error));
                            return;
                        }
                        if (state == NetWorkUtils.HOST_UN_REACHABLE) {
                            
                            showToast("，");
                            return;
                        } else if (state == NetWorkUtils.HOST_REACHABLE) {
                            
                            doAddNode();
                        } else {
                            mAddAlertLD.postValue(getApplication().getString(R.string.add_node_un_tong));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        dismissLoadingDialog();
                        showToast(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }


    public void onDestroy() {
        if (null != mDisposable) {
            mDisposable.dispose();
        }
    }
}

