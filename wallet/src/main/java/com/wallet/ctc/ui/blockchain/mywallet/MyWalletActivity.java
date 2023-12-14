

package com.wallet.ctc.ui.blockchain.mywallet;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.lxj.xpopup.XPopup;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.ui.blockchain.backupwallet.BackUpKeystoreActivity;
import com.wallet.ctc.ui.blockchain.backupwallet.BackUpMnemonicActivity;
import com.wallet.ctc.ui.blockchain.changepwd.ChangePwdActivity;
import com.wallet.ctc.util.AllUtils;
import com.wallet.ctc.util.DecriptUtil;
import com.wallet.ctc.util.GlideUtil;
import com.wallet.ctc.util.SettingPrefUtil;
import com.wallet.ctc.view.dialog.ExportPrivateKeyDialog;
import com.wallet.ctc.view.dialog.inputpwd.InputPwdDialog2;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.app.AppApplication;
import common.app.RxBus;
import common.app.biometric.MyBiometricHelper;
import common.app.mall.util.ToastUtil;
import common.app.my.RxNotice;
import common.app.my.view.CircularImage;
import common.app.my.view.MyAlertDialog;
import common.app.my.view.SettingDialog;
import common.app.ui.view.InputPwdDialog;
import common.app.utils.SpUtil;



public class MyWalletActivity extends BaseActivity {

    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.tv_action)
    TextView tvAction;
    @BindView(R2.id.wallet_logo)
    CircularImage walletLogo;
    @BindView(R2.id.wallet_name)
    TextView walletName;
    @BindView(R2.id.wallet_address)
    TextView walletAddress;
    @BindView(R2.id.export_the_private_key)
    TextView exportThePrivateKey;
    @BindView(R2.id.export_the_keystore)
    TextView exportTheKeystore;
    @BindView(R2.id.export_the_keystore_lin)
    View exportTheKeystoreLin;
    @BindView(R2.id.export_the_private_key_lin)
    View exportThePrivateKeyLin;
    @BindView(R2.id.shenfen_lin)
    View shenfen;
    @BindView(R2.id.delete_wallet)
    View delete;
    @BindView(R2.id.back_up_status)
    TextView backUpStatus;
    @BindView(R2.id.backed_up)
    View backedUp;
    @BindView(R2.id.backup_lin)
    View backedUpLin;
    @BindView(R2.id.not_backed_up)
    TextView notBackedUp;
    @BindView(R2.id.me_change_pwd)
    TextView changePwd;
    @BindView(R2.id.me_change_pwd_lin)
    View changePwdLin;

    @BindView(R2.id.open_finger)
    LinearLayout llOpenFinger;
    @BindView(R2.id.open_finger_status)
    Switch openFingerStatus;

    private InputPwdDialog mDialog;
    private InputPwdDialog2 mDialog2;
    private ExportPrivateKeyDialog mDialog3;
    private Intent intent;
    private int type = 0;
    private int from;
    private int walletType = 0;
    private String walletAddressStr;
    private boolean isChecked = true;
    private WalletEntity mWallet;
    private List<WalletEntity> mWallName;
    private MyAlertDialog alertDialog;


    @Override
    public int initContentView() {
        return R.layout.activity_mywallet;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        from = getIntent().getIntExtra("from", 0);
        walletAddressStr = getIntent().getStringExtra("walletAddress");
        walletType = getIntent().getIntExtra("type", -1);

        if (!TextUtils.isEmpty(walletAddressStr) && walletType != -1) {
            mWallet = WalletDBUtil.getInstent(this).getWalletInfoByAddress(walletAddressStr, walletType);
        } else {
            mWallet = WalletDBUtil.getInstent(this).getWalletInfo();
            walletAddressStr = mWallet.getAllAddress();
            walletType = mWallet.getType();
        }

        showData();
        tvTitle.setText(getString(R.string.manage_wallet2));
        GlideUtil.showImg(this, mWallet.getTrueLogo(), walletLogo);
        walletAddress.setText(mWallet.getAllAddress());
        walletName.setText(mWallet.getName());
        backedUp.setVisibility(View.GONE);
        backedUpLin.setVisibility(View.GONE);
        if (mWallet.getLevel() == 1) {
            shenfen.setVisibility(View.VISIBLE);
            delete.setVisibility(View.GONE);
            backedUp.setVisibility(View.GONE);
            backedUpLin.setVisibility(View.GONE);
            changePwd.setVisibility(View.GONE);
            changePwdLin.setVisibility(View.GONE);
        } else {
            shenfen.setVisibility(View.GONE);
            delete.setVisibility(View.VISIBLE);
            if (mWallet.getLevel() == 0) {
                backedUp.setVisibility(View.VISIBLE);
                backedUpLin.setVisibility(View.VISIBLE);
            }
            changePwd.setVisibility(View.GONE);
            changePwdLin.setVisibility(View.GONE);
        }
        if (null == mWallet.getmMnemonic() || mWallet.getmMnemonic().length < 10) {
            backedUp.setVisibility(View.GONE);
            backedUpLin.setVisibility(View.VISIBLE);
        }

        if (mWallet.getType() == WalletUtil.BTC_COIN || mWallet.getLevel() == -1) {
            exportTheKeystore.setVisibility(View.GONE);
            exportTheKeystoreLin.setVisibility(View.GONE);
            exportThePrivateKeyLin.setVisibility(View.GONE);
            exportThePrivateKey.setVisibility(View.GONE);
        }
        if (mWallet.getType() == WalletUtil.XRP_COIN || mWallet.getType() == WalletUtil.TRX_COIN) {
            exportTheKeystore.setVisibility(View.GONE);
            exportTheKeystoreLin.setVisibility(View.GONE);
        }
        mDialog = new InputPwdDialog(this, getString(R.string.place_edit_password));
        mDialog.setonclick(new InputPwdDialog.Onclick() {
            @Override
            public void Yes(String pwd) {
                mDialog.dismiss();
                if (!mWallet.getmPassword().equals(DecriptUtil.MD5(pwd))) {
                    ToastUtil.showToast(getString(R.string.password_error2));
                    return;
                }
                if (type == 0) {
                    intent = new Intent(MyWalletActivity.this, BackUpKeystoreActivity.class);
                    intent.putExtra("wallet", WalletUtil.getDecryptionKey(mWallet.getmKeystore(), pwd));
                    startActivity(intent);
                } else if (type == 2) {
                    intent = new Intent(MyWalletActivity.this, BackUpMnemonicActivity.class);
                    intent.putExtra("pwd", pwd);
                    intent.putExtra("wallet", mWallet);
                    intent.putExtra("from", from);
                    startActivity(intent);
                } else {
                    mDialog3 = new ExportPrivateKeyDialog(MyWalletActivity.this, WalletUtil.getDecryptionKey(mWallet.getmPrivateKey(), pwd));
                    mDialog3.setonclick(new ExportPrivateKeyDialog.Onclick() {
                        @Override
                        public void Yes(String message) {
                            mDialog3.dismiss();
                            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            
                            cm.setText(message);
                            ToastUtil.showToast(getString(R.string.copyed));
                            mWallet.setmBackup(1);
                            walletDBUtil.updateWalletInfoByAddress(mWallet);
                            initData();
                        }

                        @Override
                        public void No() {
                            mDialog3.dismiss();
                        }
                    });
                    mDialog3.show();
                }
            }

            @Override
            public void No() {
                mDialog.dismiss();
            }
        });
        mDialog2 = new InputPwdDialog2(this, getString(R.string.place_edit_password));
        mDialog2.setonclick(new InputPwdDialog2.Onclick() {
            @Override
            public void Yes(String pwd) {
                mDialog2.dismiss();
                if (!mWallet.getmPassword().equals(DecriptUtil.MD5(pwd))) {
                    ToastUtil.showToast(getString(R.string.password_error2));
                    return;
                }
                delete();
            }

            @Override
            public void No() {
                mDialog2.dismiss();
            }
        });
        shenfen.setOnClickListener(v -> {
            Intent intent = new Intent(MyWalletActivity.this, ManageIdentityActivity.class);
            intent.putExtra("walletAddress", walletAddressStr);
            intent.putExtra("type", walletType);
            intent.putExtra("from", from);
            startActivity(intent);
        });
        alertDialog = new MyAlertDialog(this, getString(R.string.del_my_address));
        alertDialog.setonclick(new MyAlertDialog.Onclick() {
            @Override
            public void Yes() {
                alertDialog.dismiss();
                delete();
            }

            @Override
            public void No() {
                alertDialog.dismiss();
            }
        });


    }

    @Override
    public void initData() {

    }


    
    private void dealBiometric() {
        
        String fingerPayKey = AllUtils.getFingerPayKey(this);
        boolean isOpenBiometric = SpUtil.getAppBiometricOpened(fingerPayKey);
        openFingerStatus.setChecked(isOpenBiometric);
        openFingerStatus.setClickable(false);
        llOpenFinger.setOnClickListener(v -> {
            
            if (MyBiometricHelper.checkHardSupport(this)) {
                
                if (!MyBiometricHelper.checkHasFinger(this)) {
                    Toast.makeText(this, R.string.biometric_string_4, Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                Toast.makeText(this, R.string.biometric_string_8, Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isOpenBiometric2 = SpUtil.getAppBiometricOpened(fingerPayKey);
            if (isOpenBiometric2) {
                new XPopup.Builder(this).asConfirm(getString(R.string.issuance_coin_title), getString(R.string.biometric_string_6), () -> {
                    openFingerStatus.setChecked(false);
                    SpUtil.setAppBiometricOpen(fingerPayKey, false);
                }).show();
                return;
            }
            WalletEntity payWallet = AllUtils.getFingerPayWallet(getApplicationContext());
            
            InputPwdDialog dialog = new InputPwdDialog(this, getString(R.string.place_edit_password));
            dialog.setonclick(new InputPwdDialog.Onclick() {
                @Override
                public void Yes(String pwd) {
                    if (null == payWallet){
                        ToastUtil.showToast(R.string.no_found_wallet_info);
                        return;
                    }
                    if (!payWallet.getmPassword().equals(DecriptUtil.MD5(pwd))) {
                        ToastUtil.showToast(getString(R.string.password_error2));
                        return;
                    }

                    dialog.dismiss();
                    MyBiometricHelper helper = MyBiometricHelper.getInstance(true, fingerPayKey);
                    helper.setPassword(pwd);
                    helper.setBiometricCallback(new MyBiometricHelper.BiometricCallback() {
                        @Override
                        public void listener(int errorCode) {
                            openFingerStatus.setChecked(false);
                        }

                        @Override
                        public void callback(String password) {
                            SpUtil.setAppBiometricOpen(fingerPayKey, true);
                            openFingerStatus.setChecked(true);
                        }

                        @Override
                        public void error(Throwable e) {
                            ToastUtil.showToast(e+":"+e.getMessage());
                        }
                    });
                    helper.start(MyWalletActivity.this);
                }

                @Override
                public void No() {
                    dialog.dismiss();
                    openFingerStatus.setChecked(false);
                }
            });
            dialog.show(true);
        });
    }

    private void delete() {
        for (int i = 0; i < mWallName.size(); i++) {
            if (mWallName.get(i).getAllAddress().equals(mWallet.getAllAddress()) && mWallName.get(i).getType() == mWallet.getType()) {
                mWallName.remove(i);
                i--;
            }
        }
        if (mWallet.getType() == WalletUtil.TRX_COIN) {
            SettingPrefUtil.setWalletLoadNum(mWallet.getAllAddress(), 0);
        }

        
        walletDBUtil.delWallet(mWallet.getAllAddress(), mWallet.getType());

        
        if (mWallName.size() > 0) {
            int walletType = mWallet.getType();
            WalletEntity newWallet = null;
            for (int i = 0; i < mWallName.size(); i++) {
                if (mWallName.get(i).getType() == walletType) {
                    
                    newWallet = mWallName.get(i);
                    break;
                }
            }
            if (null == newWallet) {
                newWallet = mWallName.get(0);
            }
            SettingPrefUtil.setWalletTypeAddress(MyWalletActivity.this, newWallet.getType(), newWallet.getAllAddress());
        } else {
            
            SettingPrefUtil.setWalletTypeAddress(MyWalletActivity.this, 0, "");
        }

        
        String fingerKey = AllUtils.getFingerPayKey(this);
        String thisWalletKey = AllUtils.getFingerPayKey(mWallet);
        if (!TextUtils.isEmpty(fingerKey) && fingerKey.equals(thisWalletKey)) {
            
            SpUtil.setAppBiometricOpen(fingerKey, false);
        }
        ToastUtil.showToast(getString(R.string.caozuo_success));
        AppApplication.finishAllActivityExMain();
        finish();

        
        String deleteAddress = mWallet.getAllAddress();
        RxNotice logOutNotice = new RxNotice(RxNotice.MSG_LOGOUT);
        logOutNotice.setData(deleteAddress);
        RxBus.getInstance().post(logOutNotice);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWallName = walletDBUtil.getWallName();
        if (!TextUtils.isEmpty(walletAddressStr)) {
            mWallet = WalletDBUtil.getInstent(this).getWalletInfoByAddress(walletAddressStr, walletType);
            showData();
        }
        dealBiometric();
    }

    private void showData() {
        if (mWallet.getMMnemonicBackup() == 1) {
            backUpStatus.setText("");
            notBackedUp.setText("");
        } else {
            backUpStatus.setText(getString(R.string.not_backed_up));
            notBackedUp.setText(getString(R.string.not_backed_up));
        }
    }

    @OnClick({R2.id.tv_back, R2.id.wallet_address, R2.id.me_change_pwd, R2.id.export_the_keystore, R2.id.export_the_private_key, R2.id.delete_wallet, R2.id.backed_up, R2.id.wallet_name})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.tv_back) {
            finish();
        } else if (i == R.id.export_the_keystore) {
            type = 0;
            mDialog.show();
        } else if (i == R.id.export_the_private_key) {
            type = 1;
            mDialog.show();
        } else if (i == R.id.wallet_address) {
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            
            cm.setText(mWallet.getAllAddress());
            ToastUtil.showToast(getString(R.string.copy_success));
        } else if (i == R.id.delete_wallet) {
            if (mWallet.getLevel() == -1) {
                alertDialog.show();
            } else {
                mDialog2.show();
            }

        } else if (i == R.id.backed_up) {
            type = 2;
            mDialog.show();
        } else if (i == R.id.wallet_name) {
            setName();
        } else if (i == R.id.me_change_pwd) {
            Intent intent = new Intent(this, ChangePwdActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            mWallet = walletDBUtil.getWalletInfoByAddress(mWallet.getAllAddress(), mWallet.getType());
        }
    }

    
    private boolean allBackUp() {
        boolean allback = true;
        for (int i = 0; i < mWallName.size(); i++) {
            WalletEntity walletEntity = mWallName.get(i);
            if (walletEntity.getMMnemonicBackup() == 0) {
                allback = false;
                break;
            }
        }
        return allback;
    }

    private SettingDialog mNameDialog;

    
    private void setName() {
        if (null == mNameDialog) {
            mNameDialog = new SettingDialog(this, getString(R.string.wallet_name));
            mNameDialog.setPositiveListnner(v -> {
                String newNick = mNameDialog.getMsg();
                if (!TextUtils.isEmpty(newNick)) {
                    mWallet.setName(newNick);
                    walletName.setText(newNick);
                    walletDBUtil.updateWalletInfoByAddress(mWallet);
                }
                mNameDialog.dismiss();
            });
        }
        String nowNic = walletName.getText().toString();
        if (!TextUtils.isEmpty(nowNic)) {
            mNameDialog.setMsg(nowNic);
        }
        mNameDialog.show();

    }

}
