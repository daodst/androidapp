package com.app.home.ui.vote.create.pay.setp1;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;

import com.app.R;
import com.app.databinding.ActivityPayVoteBinding;
import com.app.home.pojo.VoteDetial;
import com.app.home.ui.vote.create.pay.step2.PayVoteStep2Activity;

import common.app.base.BaseActivity;
import common.app.my.RxNotice;
import common.app.ui.view.TitleBarView;


public class PayVoteStep1Activity extends BaseActivity<PayVoteStep1VM> {


    ActivityPayVoteBinding mBinding;

    @Override
    public View initBindingView(Bundle savedInstanceState) {
        mBinding = ActivityPayVoteBinding.inflate(LayoutInflater.from(this));
        return mBinding.getRoot();
    }

    @Override
    public void succeed(Object obj) {
        if (obj instanceof RxNotice) {
            RxNotice notice = (RxNotice) obj;
            if (notice.mType == RxNotice.MSG_SUBMIT_VOTE) {
                finish();
            }
        }
    }

    @Override
    public void initView(@Nullable View view) {
        mBinding.payVoteTitle.setOnTitleBarClickListener(new TitleBarView.TitleBarClickListener() {
            @Override
            public void leftClick() {
                finish();
            }

            @Override
            public void rightClick() {

            }
        });

        getViewModel().mLiveData.observe(this, info -> {
            if (null == info.result) {
                return;
            }
            for (int i = 0; i < info.result.size(); i++) {
                VoteDetial.DepositAmount amount = info.result.get(i);
                if (null == amount) {
                    continue;
                }
                if (i == 0) {
                    String name = TextUtils.isEmpty(amount.denom) ? "" : amount.denom.toUpperCase();
                    mBinding.payVoteDstName.setText(name);
                    mBinding.payVoteDstTips.setText(String.format(getString(R.string.pay_vote_tips_str), name));
                    mBinding.payVoteDstNum.setText(String.format(getString(R.string.pay_vote_num_str), amount.getAmount(18)));
                    mBinding.payVoteDst.setOnClickListener(v -> {
                        startActivity(PayVoteStep2Activity.getIntent(this, amount));
                    });
                } else if (i == 1) {
                    String name = TextUtils.isEmpty(amount.denom) ? "" : amount.denom.toUpperCase();
                    mBinding.payVoteFmName.setText(name);
                    mBinding.payVoteFmTips.setText(String.format(getString(R.string.pay_vote_tips_str), name));
                    mBinding.payVoteFmNum.setText(String.format(getString(R.string.pay_vote_num_str), amount.getAmount(18)));
                    mBinding.payVoteFm.setOnClickListener(v -> {
                        startActivity(PayVoteStep2Activity.getIntent(this, amount));
                    });
                }
            }
        });
        getViewModel().getPayVoteStep1Info();

    }
}
