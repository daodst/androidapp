

package com.wallet.ctc.ui.blockchain.setfee;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import common.app.my.Web;
import common.app.utils.SpUtil;


public class SettingFeeActivity extends BaseActivity {

    @BindView(R2.id.tv_back)
    TextView tvBack;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.fee_status)
    ImageView feeStatus;
    @BindView(R2.id.about)
    View about;

    int fee=0;
    @Override
    public int initContentView() {
        return R.layout.activity_settingfee;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        tvBack.setOnClickListener(v -> {
            finish();
        });
        tvTitle.setText(getString(R.string.fee_moshi));
        feeStatus.setOnClickListener(v -> {
            fee=1-fee;
            SpUtil.saveFeeStatus(fee);
            changeFee();
        });
        about.setOnClickListener(v -> {
            Web.startWebActivity(this,"https://tphelp.gitbook.io/cn/faq/ethwallet/eip-1559?utm_source=tokenpocket","EIP-1559",null);
        });
    }

    @Override
    public void initData() {
        fee=SpUtil.getFeeStatus();
        changeFee();
    }
    private void changeFee(){
        if(fee==1){
            feeStatus.setImageResource(R.drawable.anniuxuanzhong);
        }else {
            feeStatus.setImageResource(R.drawable.anniu);
        }
    }
}
