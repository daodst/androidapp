

package com.app.me;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.wallet.ctc.model.blockchain.EvmosChatInfoBean;
import com.wallet.ctc.model.blockchain.RpcApi;
import com.wallet.ctc.util.AllUtils;
import com.wallet.ctc.util.LogUtil;

import java.util.List;

import common.app.base.BaseViewModel;
import common.app.utils.SpUtil;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


public class SMNewMeFragmentVM extends BaseViewModel {
    private RpcApi mRpcApi;

    private CompositeDisposable mDisposable;
    public MutableLiveData<String> nowPhoneLD;

    
    public SMNewMeFragmentVM(@NonNull Application application) {
        super(application);
        nowPhoneLD = new MutableLiveData<>();
        mRpcApi = new RpcApi();
        mDisposable = new CompositeDisposable();
    }

    public void getChatInfo(String userId) {
        
        String address = AllUtils.getAddressByUid(userId);
        if (TextUtils.isEmpty(address)) {
            return;
        }
        String nowPhone = SpUtil.getNowPhone(address);
        if (!TextUtils.isEmpty(nowPhone)) {
            nowPhoneLD.setValue(nowPhone);
        }

        mRpcApi.getEvmosChatInfo(address).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<EvmosChatInfoBean>() {
            @Override
            public void onSubscribe(Disposable d) {
                mDisposable.add(d);
            }

            @Override
            public void onNext(EvmosChatInfoBean chatInfoBean) {
                if (null != chatInfoBean && chatInfoBean.isSuccess() && null != chatInfoBean.data) {
                    List<String> myPhonelist = chatInfoBean.data.mobile;
                    SpUtil.saveMyPhoneList(address, myPhonelist);
                    String nowPhone = SpUtil.getNowPhone(address);
                    if (null != myPhonelist && myPhonelist.size() > 0) {
                        if (TextUtils.isEmpty(nowPhone)) {
                            nowPhone = myPhonelist.get(0);
                        } else if (!TextUtils.isEmpty(nowPhone) && !myPhonelist.contains(nowPhone)) {
                            nowPhone = myPhonelist.get(0);
                        }
                        SpUtil.saveNowPhone(address, nowPhone);
                    } else {
                        nowPhone = "";
                        SpUtil.saveNowPhone(address, "");
                    }
                    nowPhoneLD.setValue(nowPhone);
                } else {
                    String erroInfo = chatInfoBean == null ? "getChatInfo error" : chatInfoBean.getInfo();
                    LogUtil.e(erroInfo);
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
