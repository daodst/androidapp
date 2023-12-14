

package com.app.node.add;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.app.R;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.DBManager;
import com.wallet.ctc.db.SettingNodeEntity;
import com.wallet.ctc.model.blockchain.NodeData;
import com.wallet.ctc.model.blockchain.RpcApi;

import common.app.base.BaseViewModel;
import common.app.im.base.NextSubscriber;
import common.app.utils.NetWorkUtils;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


public class AddFmNodeVM extends BaseViewModel {
    public MutableLiveData<String> mErrorToastLD;
    public MutableLiveData<Boolean> mAddResultLD;
    public MutableLiveData<String> mAddAlertLD;
    public MutableLiveData<NodeData> mNodeDataMutableLiveData;
    private CompositeDisposable mDisposable;
    private RpcApi mRpcApi;

    
    public AddFmNodeVM(@NonNull Application application) {
        super(application);
        mDisposable = new CompositeDisposable();
        mErrorToastLD = new MutableLiveData<>();
        mAddResultLD = new MutableLiveData<>();
        mAddAlertLD = new MutableLiveData<>();
        mNodeDataMutableLiveData = new MutableLiveData<>();
        mRpcApi = new RpcApi();
    }

    
    private String mUrl, mNoSegment, mNodeName, mGateWayAddr;

    public void addNode(String url, String noSegment, String nodeName, String gateWayAddr) {
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(nodeName)) {
            return;
        }
        mUrl = url;
        mNoSegment = noSegment;
        mNodeName = nodeName;
        mGateWayAddr = gateWayAddr;
        
        checkUrlTime(url);
    }


    public void getNodeData(String url) {
        showLoadingDialog("");
        mRpcApi.getNodeData(url).subscribe(new NextSubscriber<NodeData>() {
            @Override
            public void dealData(NodeData value) {
                dismissLoadingDialog();
                mNodeDataMutableLiveData.postValue(value);
            }

            @Override
            protected void dealError(Throwable e) {
                super.dealError(e);
                dismissLoadingDialog();
            }
        });
    }


    
    public void doAddNode() {
        
        SettingNodeEntity node = new SettingNodeEntity(mNodeName, mUrl, WalletUtil.MCC_COIN, false, 1, mNoSegment, "", 0, mGateWayAddr);
        DBManager.getInstance(getApplication()).insertNode(node);
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

