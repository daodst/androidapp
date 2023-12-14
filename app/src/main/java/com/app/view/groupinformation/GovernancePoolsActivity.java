package com.app.view.groupinformation;

import static com.wallet.ctc.BuildConfig.ENABLE_MCC_ADDRESS;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import com.app.R;
import com.app.databinding.ActivityGovenancePoolsBinding;
import com.wallet.ctc.view.TitleBarView;

import java.math.BigDecimal;

import common.app.base.BaseActivity;
import common.app.mall.util.ToastUtil;


public class GovernancePoolsActivity extends BaseActivity<GovernancePoolsActivityVM> {
    private ActivityGovenancePoolsBinding mBinding;
    private String groupId;
    private String clusterVotePolicy;
    private String mOutAmount, mAddress, mDescription;
    private String amount;

    @Override
    public View initBindingView(Bundle savedInstanceState) {
        mBinding = ActivityGovenancePoolsBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }

    @Override
    public void initParam() {
        super.initParam();
        groupId = getIntent().getStringExtra("groupId");
        clusterVotePolicy = getIntent().getStringExtra("clusterVotePolicy");
        amount = getIntent().getStringExtra("amount");
    }

    @Override
    public void initView(@Nullable View view) {
        super.initView(view);
        mBinding.titleBarView.setOnTitleBarClickListener(new TitleBarView.TitleBarClickListener() {
            @Override
            public void leftClick() {
                finish();
            }

            @Override
            public void rightClick() {

            }
        });

        mBinding.tvRemind.setText(R.string.governance_pools_remind);
        mBinding.tvKuCun.setText(getString(R.string.governance_pools_string_1, amount));

        mBinding.button.setOnClickListener(v -> {
            if (checkInput()) {
                getViewModel().onSubmit(this, clusterVotePolicy, groupId, mOutAmount, mAddress, mDescription);
            }
        });
    }

    private boolean checkInput() {
        mOutAmount = mBinding.etOutAmount.getText().toString();
        mAddress = mBinding.etReceiverAddress.getText().toString();
        mDescription = mBinding.etDescription.getText().toString();
        

        if (TextUtils.isEmpty(mOutAmount)) {
            showToast(mBinding.etOutAmount.getHint().toString());
            return false;
        }

        try {
            if (TextUtils.isEmpty(amount) || new BigDecimal(amount).compareTo(BigDecimal.ZERO) <=0 ||
                    new BigDecimal(mOutAmount).compareTo(new BigDecimal(amount)) >0 ){
                showToast(R.string.insufficient_balance);
                return false;
            }
        } catch (NumberFormatException e){
            e.printStackTrace();
            showToast(R.string.please_input_legal_number);
            return false;
        }


        if (TextUtils.isEmpty(mAddress)) {
            showToast(mBinding.etReceiverAddress.getHint().toString());
            return false;
        }

        if (mAddress.length() < 15) {
            ToastUtil.showToast(getString(com.wallet.ctc.R.string.payee_wallet_address_errpr));
            return false;
        }
        if (!mAddress.startsWith(ENABLE_MCC_ADDRESS)) {
            ToastUtil.showToast(getString(com.wallet.ctc.R.string.address_is_no_invalidate));
            return false;
        }



        if (TextUtils.isEmpty(mDescription)) {
            showToast(mBinding.etDescription.getHint().toString());
            return false;
        }

        return true;
    }
}
