

package com.wallet.ctc.ui.blockchain.backupwallet;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.base.BaseWebViewActivity;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.util.DecriptUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.app.mall.util.ToastUtil;
import common.app.ui.view.InputPwdDialog;



public class BackUpActivity extends BaseActivity {

    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.import_wallet)
    Button importWallet;
    @BindView(R2.id.what_is_mnemonic)
    TextView whatIsMnemonic;
    private InputPwdDialog mDialog;
    private WalletEntity mWallet;
    @Override
    public int initContentView() {
        return R.layout.activity_backup_wallet;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        mWallet=walletDBUtil.getWalletInfo();
        mDialog=new InputPwdDialog(this,getString(R.string.place_edit_password));
        tvTitle.setText(getString(R.string.backup_wallet));
        mDialog.setonclick(new InputPwdDialog.Onclick() {
            @Override
            public void Yes(String pwd) {
                startBackUp(pwd, true);
            }

            @Override
            public void No() {
                mDialog.dismiss();
            }
        });

    }

    @Override
    public void initData() {

    }


    private void startBackUp(String pwd, boolean isInputPwd){
        if(DecriptUtil.MD5(pwd).equals(mWallet.getmPassword())) {
            if(isInputPwd){
                mDialog.dismiss();
            }

            Intent intent = new Intent(getIntent());
            intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
            intent.setClass(BackUpActivity.this, BackUpMnemonicActivity.class);
            intent.putExtra("pwd",pwd);
            intent.putExtra("from",2);
            intent.putExtra("wallet",mWallet);
            startActivity(intent);
            finish();
        }else {
            ToastUtil.showToast(getString(R.string.password_error2));
            if(!isInputPwd){
                mDialog.show();
            }
        }
    }

    @Override
    
    
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        finishAndSetResult();
        return false;
    }




    @OnClick({R2.id.tv_back, R2.id.import_wallet, R2.id.what_is_mnemonic})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.tv_back) {
            finishAndSetResult();

        } else if (i == R.id.import_wallet) {
            String pwd = getIntent().getStringExtra("walletPwd");
            if(!TextUtils.isEmpty(pwd)){
                startBackUp(pwd, false);
            } else {
                mDialog.show();
            }
        } else if (i == R.id.what_is_mnemonic) {
            getUrl("backup_wallet_detail", getString(R.string.backup_wallet_detail));

        } else {
        }
    }


    
    private void finishAndSetResult() {
        String pwd = getIntent().getStringExtra("walletPwd");
        Log.i("testLoing", "finishAndSetResult()"+pwd);
        Intent data = new Intent();
        data.putExtra("walletPwd", pwd);
        setResult(RESULT_OK, data);
        finish();
    }

    private void getUrl(String type,String title){
        Intent intent=new Intent(BackUpActivity.this, BaseWebViewActivity.class);
        intent.putExtra("type",1);
        intent.putExtra("sysName",type);
        intent.putExtra("title",title);
        startActivity(intent);
    }
}
