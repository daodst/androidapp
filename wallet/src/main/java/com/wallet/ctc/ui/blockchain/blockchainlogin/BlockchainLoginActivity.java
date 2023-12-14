

package com.wallet.ctc.ui.blockchain.blockchainlogin;

import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.ui.blockchain.creatwallet.CreatWalletActivity;
import com.wallet.ctc.ui.blockchain.importwallet.ImportWalletActivity;
import com.wallet.ctc.ui.blockchain.managewallet.ChooseCreatImportTypeActivity;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import common.app.base.them.Eyes;



public class BlockchainLoginActivity extends BaseActivity {
    @Override
    public int initContentView() {
        return R.layout.activity_blockchain_login;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.LOLLIPOP_MR1) {
            Eyes.setTranslucent(this);
            findViewById(R.id.body).setPadding(0,Eyes.getStatusBarHeight(this),0,0);
        }

        TextView titleTv = findViewById(R.id.app_name);
        titleTv.setText(getString(R.string.default_token_name).toUpperCase()+getString(R.string.wallet));
    }

    @Override
    public void initData() {

    }
    @OnClick({R2.id.create_wallet, R2.id.import_wallet,R2.id.img_back, R2.id.move_wallet, R2.id.scan_move})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.create_wallet) {
            jump(0);
        } else if (i == R.id.import_wallet) {
            jump(1);
        } else if (i == R.id.img_back){
            finish();
        } else if(i == R.id.move_wallet){
            
        } else if(i == R.id.scan_move) {
            
        }
    }


    private void jump(int from){
        List<WalletEntity> wallNamelist= WalletDBUtil.getInstent(this).getWallName();
        Intent intent = new Intent(getIntent());
        intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        if (TextUtils.isEmpty(BuildConfig.ENABLE_CREAT_ALL_WALLET)||(null!=wallNamelist&&wallNamelist.size()>0&&wallNamelist.get(0).getLevel()==1)) {
            intent.setClass(BlockchainLoginActivity.this, ChooseCreatImportTypeActivity.class);
            intent.putExtra("from", from);
        }else {
            if (from == 0) {
                intent.setClass(this, CreatWalletActivity.class);
            } else {
                intent.setClass(this, ImportWalletActivity.class);
            }
            intent.putExtra("type", BuildConfig.ENABLE_CREAT_ALL_WALLET_TYPE);
        }
        startActivity(intent);
        finish();
    }
}
