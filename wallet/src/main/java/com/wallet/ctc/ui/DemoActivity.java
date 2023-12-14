

package com.wallet.ctc.ui;

import com.wallet.ctc.R;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.crypto.WalletTransctionUtil;
import com.wallet.ctc.model.blockchain.SgbDappBean;

import butterknife.ButterKnife;


public class DemoActivity extends BaseActivity {


    private WalletTransctionUtil sgbTransctionListen;
    @Override
    public int initContentView() {
        return R.layout.demo;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {

    }

    private void dodata(SgbDappBean data){

    }
}
