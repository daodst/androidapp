

package com.wallet.ctc.ui.blockchain.changepwd;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.RootWalletInfo;
import com.wallet.ctc.util.AllUtils;
import com.wallet.ctc.util.DecriptUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.app.mall.util.ToastUtil;
import common.app.utils.SpUtil;
import common.app.utils.ThreadManager;



public class ChangePwdActivity extends BaseActivity {

    @BindView(R2.id.tv_back)
    TextView tvBack;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.edit_old_pw)
    EditText editOldPw;
    @BindView(R2.id.edit_new_pw)
    EditText editNewPw;
    @BindView(R2.id.edit_sure_pw)
    EditText editSurePw;
    private com.wallet.ctc.db.WalletEntity WalletEntity;

    @Override
    public int initContentView() {
        return R.layout.activity_change_pwd;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        tvTitle.setText(getString(R.string.change_pwd));

        RootWalletInfo rootWalletInfo = null;
        if (!TextUtils.isEmpty(BuildConfig.ENABLE_CREAT_ALL_WALLET)) {
            rootWalletInfo = walletDBUtil.getRootWalletInfo(BuildConfig.ENABLE_CREAT_ALL_WALLET_TYPE);
        }
        if (rootWalletInfo != null && rootWalletInfo.rootWallet != null) {
            WalletEntity = rootWalletInfo.rootWallet;
        } else {
            WalletEntity=(WalletEntity)getIntent().getParcelableExtra("wallet");
            if (WalletEntity == null) {
                WalletEntity = WalletDBUtil.getInstent(this).getWalletInfo();
            }
        }
        if (null == WalletEntity) {
            ToastUtil.showToast(R.string.no_found_wallet_info);
            finish();
        }
    }

    @Override
    public void initData() {

    }

    @OnClick({R2.id.tv_back, R2.id.btn_sure})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.tv_back) {
            finish();

        } else if (i == R.id.btn_sure) {
            String oldpwd = editOldPw.getText().toString().trim();
            String newpwd = editNewPw.getText().toString().trim();
            String newpwd2 = editSurePw.getText().toString().trim();
            if (TextUtils.isEmpty(oldpwd)) {
                ToastUtil.showToast(getString(R.string.place_edit_password));
                return;
            }
            if (TextUtils.isEmpty(newpwd)) {
                ToastUtil.showToast(getString(R.string.place_edit_new_password));
                return;
            }
            if (TextUtils.isEmpty(newpwd2)) {
                ToastUtil.showToast(getString(R.string.place_edit_commit_password));
                return;
            }
            if (newpwd.length() < 8) {
                ToastUtil.showToast(getString(R.string.password_remind));
                return;
            }
            if (!newpwd.equals(newpwd2)) {
                ToastUtil.showToast(getString(R.string.password_error));
                return;
            }
            if (DecriptUtil.MD5(oldpwd).equals(WalletEntity.getmPassword())) {
                if (!oldpwd.equals(newpwd)) {
                    mLoadingDialog.show();
                    ThreadManager.getNormalPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            
                            if(WalletEntity.getLevel()==1&&!TextUtils.isEmpty(BuildConfig.ENABLE_CREAT_ALL_WALLET)){
                                List<WalletEntity> walletEntityList=walletDBUtil.getWallName();
                                for(int i=0;i<walletEntityList.size();i++){
                                    if(walletEntityList.get(i).getLevel() != -1) {
                                        WalletUtil.changeWalletPwd(ChangePwdActivity.this, walletEntityList.get(i), oldpwd, newpwd);
                                    }
                                }

                                
                                SpUtil.cleanAllBiometricPaySetting();

                            }else {
                                WalletUtil.changeWalletPwd(ChangePwdActivity.this, WalletEntity, oldpwd, newpwd);
                                
                                String fingerPay = AllUtils.getFingerPayKey(WalletEntity);
                                SpUtil.setAppBiometricOpen(fingerPay, false);
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mLoadingDialog.dismiss();
                                    ToastUtil.showToast(getString(R.string.caozuo_success));
                                    setResult(RESULT_OK);
                                    finish();
                                }
                            });
                        }
                    });

                } else {
                    ToastUtil.showToast(getString(R.string.pwd_error));
                }

            } else {
                ToastUtil.showToast(getString(R.string.password_error2));
            }


        }
    }
}
