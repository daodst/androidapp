

package com.wallet.ctc.ui.blockchain.backupwallet;

import android.content.Intent;
import android.view.View;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.ui.blockchain.mywallet.MyWalletActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;
import common.app.base.them.Eyes;



public class BackUpCaveatActivity extends BaseActivity {


    private Intent intent;

    @Override
    public int initContentView() {
        return R.layout.activity_backup_caveat;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        Eyes.setTranslucent(this);
    }

    @Override
    public void initData() {

    }


    @OnClick({R2.id.import_wallet})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.import_wallet) {
            intent = new Intent(BackUpCaveatActivity.this, MyWalletActivity.class);
            intent.putExtra("walletAddress", walletDBUtil.getWalletInfo().getAllAddress());
            intent.putExtra("type",walletDBUtil.getWalletInfo().getType());
            startActivity(intent);
            finish();

        } else {
        }
    }
}
