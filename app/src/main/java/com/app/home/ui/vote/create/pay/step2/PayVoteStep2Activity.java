package com.app.home.ui.vote.create.pay.step2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;

import com.app.R;
import com.app.databinding.ActivityPayVoteStep2Binding;
import com.app.home.pojo.VoteDetial;
import com.app.home.ui.vote.create.CreateVoteActivity;

import java.math.BigDecimal;

import common.app.base.BaseActivity;
import common.app.mall.util.ToastUtil;
import common.app.my.RxNotice;
import common.app.ui.view.TitleBarView;

public class PayVoteStep2Activity extends BaseActivity {


    private static final String TAG = "PayVoteStep2Activity";
    ActivityPayVoteStep2Binding mBinding;

    public static final String VOTE_PARAM = "vote_param";

    public static Intent getIntent(Context context, VoteDetial.DepositAmount amount) {
        Intent intent = new Intent(context, PayVoteStep2Activity.class);
        intent.putExtra(VOTE_PARAM, amount);
        return intent;
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
    public View initBindingView(Bundle savedInstanceState) {
        mBinding = ActivityPayVoteStep2Binding.inflate(LayoutInflater.from(this));
        return mBinding.getRoot();
    }

    private VoteDetial.DepositAmount mDepositAmount;

    @Override
    public void initView(@Nullable View view) {

        mDepositAmount = getIntent().getParcelableExtra(VOTE_PARAM);
        mBinding.payVote2Title.setOnTitleBarClickListener(new TitleBarView.TitleBarClickListener() {
            @Override
            public void leftClick() {
                finish();
            }

            @Override
            public void rightClick() {

            }
        });
        String name = TextUtils.isEmpty(mDepositAmount.denom) ? "" : mDepositAmount.denom.toUpperCase();
        mBinding.payVote2CoinTitle.setText(name);
        mBinding.payVote2CoinNum.setText(String.format(getString(R.string.pay_vote_num_str), mDepositAmount.getAmount(18)));

        mBinding.payVote2Bt.setOnClickListener(v -> {
            

            String num = mBinding.payVote2Num.getText().toString().trim();
            if (TextUtils.isEmpty(num)) {
                ToastUtil.showToast(mBinding.payVote2Num.getHint().toString());
                return;
            }
            BigDecimal decimalNum = new BigDecimal(num);
            BigDecimal hasNum = new BigDecimal(mDepositAmount.getAmount(18));
            if (hasNum.compareTo(decimalNum) < 0) {
                ToastUtil.showToast(getString(R.string.pay_vote2_num_limit));
                return;
            }
            String address = mBinding.payVote2Address.getText().toString().trim();
            if (TextUtils.isEmpty(address)) {
                ToastUtil.showToast(mBinding.payVote2Address.getHint().toString());
                return;
            }
            
            startActivity(CreateVoteActivity.getPayIntent(this, num, address, name));
        });
    }
}
