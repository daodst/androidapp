

package com.app.home.ui.vote.list;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.home.net.http.HomeNetImpl;
import com.app.home.net.http.IHomeNet;
import com.app.home.pojo.VoteInfoWapper;

import common.app.base.BaseViewModel;
import common.app.im.base.NextSubscriber;
import io.reactivex.disposables.CompositeDisposable;


public class VoteListVM extends BaseViewModel {
    private final CompositeDisposable mDisposable;
    private final IHomeNet mIHomeNet;

    public VoteListVM(@NonNull Application application) {
        super(application);
        mIHomeNet = new HomeNetImpl();
        this.mDisposable = new CompositeDisposable();
    }

    private MutableLiveData<Object> mMutableErrorLiveData = new MutableLiveData();
    public LiveData<Object> mErrorLiveData = mMutableErrorLiveData;


    private MutableLiveData<VoteInfoWapper> mPageListMutableLiveData = new MutableLiveData<>();
    public LiveData<VoteInfoWapper> mLiveData = mPageListMutableLiveData;

    void getVoteInfo(VoteInfoWapper requst) {
        int size = 0;
        if (null == requst || null == requst.proposals) {
            size = 0;
        } else {
            size = requst.proposals.size();
        }
        this.mDisposable.add(mIHomeNet.getVoteInfo(size).subscribeWith(new NextSubscriber<VoteInfoWapper>() {
            @Override
            public void dealData(VoteInfoWapper value) {
                if (null != requst && null != value.proposals) {
                    value.proposals.addAll(0, requst.proposals);
                }
                if (null == value.proposals || value.proposals.size() < IHomeNet.LIMIT) {
                    value.isEnd = true;
                } else {
                    value.isEnd = false;
                }
                
                mPageListMutableLiveData.setValue(value);
            }

            @Override
            protected void dealError(Throwable e) {
                super.dealError(e);
                mMutableErrorLiveData.setValue(new Object());
            }
        }));
    }

    @Override
    protected void onCleared() {
        if (null != this.mDisposable) {
            this.mDisposable.dispose();
        }
        super.onCleared();
    }
}
