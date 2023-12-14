package com.app.view.groupinformation.ratio.contribution;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.app.view.groupinformation.vote.votelist.GovernancePoolsVoteListActivity;
import com.wallet.ctc.model.blockchain.EvmosVoteDuringBean;
import com.wallet.ctc.model.blockchain.RpcApi;
import com.wallet.ctc.ui.pay.TransferControlApi;

import common.app.base.BaseViewModel;
import common.app.im.base.NextSubscriber;
import common.app.utils.TimeUtil;
import im.vector.app.provide.ChatStatusProvide;
import im.wallet.router.listener.TranslationListener;
import im.wallet.router.util.Consumer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;


public class ContributionVM extends BaseViewModel {
    TransferControlApi controlApi = new TransferControlApi();

    
    public ContributionVM(@NonNull Application application) {
        super(application);
    }


    public void onSubmit(Context context, String policyAddress, String fromAddr, String groupId, String rate, String title, String description) {

        getVoteDuring(during -> {
            controlApi.contributionVote(context, policyAddress, fromAddr, groupId, rate, title, description, new TranslationListener() {
                @Override
                public void onFail(String errorInfo) {

                }

                @Override
                public void onTransSuccess() {
                    long currentTime = System.currentTimeMillis();
                    long endTime = System.currentTimeMillis() + during * 1000;
                    
                    String period = TimeUtil.getYYYYMMdd2(currentTime) + "-" + TimeUtil.getYYYYMMddHHMM2(endTime);
                    ChatStatusProvide.sendCusVoteMessage(context, groupId, "", title, period);

                    Intent intent = new Intent(context, GovernancePoolsVoteListActivity.class);
                    intent.putExtra("groupId", groupId);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                }
            });
        });

    }


    
    protected void getVoteDuring(Consumer<Integer> consumer) {
        RpcApi mRpcApi = new RpcApi();
        Disposable disposable = mRpcApi.getVoteDuring().observeOn(AndroidSchedulers.mainThread()).subscribeWith(new NextSubscriber<EvmosVoteDuringBean>() {
            @Override
            public void dealData(EvmosVoteDuringBean value) {
                if (null != value) {
                    String votingPeriod = value.votingParams.votingPeriod;
                    if (votingPeriod.endsWith("s")) {
                        votingPeriod = votingPeriod.substring(0, votingPeriod.length() - 2);
                    }
                    int period = Integer.parseInt(votingPeriod);
                    consumer.accept(period);
                }
            }

            @Override
            protected void dealError(Throwable e) {
                super.dealError(e);
            }
        });
    }
}
