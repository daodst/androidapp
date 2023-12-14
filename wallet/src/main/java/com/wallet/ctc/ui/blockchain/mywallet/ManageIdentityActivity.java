

package com.wallet.ctc.ui.blockchain.mywallet;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.WalletLogoBean;
import com.wallet.ctc.ui.blockchain.backupwallet.BackUpMnemonicActivity;
import com.wallet.ctc.ui.blockchain.changepwd.ChangePwdActivity;
import com.wallet.ctc.ui.blockchain.managewallet.AddWalletTypeActivity;
import com.wallet.ctc.util.DecriptUtil;
import com.wallet.ctc.util.SettingPrefUtil;
import com.wallet.ctc.util.WalletSpUtil;
import com.wallet.ctc.view.dialog.IdentityOutDialog;
import com.wallet.ctc.view.dialog.inputpwd.InputPwdDialog2;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.app.AppApplication;
import common.app.RxBus;
import common.app.mall.util.ToastUtil;
import common.app.my.RxNotice;
import common.app.my.view.CircularImage;
import common.app.ui.view.InputPwdDialog;
import common.app.utils.GlideUtil;
import common.app.utils.SpUtil;



public class ManageIdentityActivity extends BaseActivity {
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.wallet_logo)
    CircularImage walletLogo;
    @BindView(R2.id.wallet_name)
    TextView walletName;
    @BindView(R2.id.not_backed_up)
    TextView notBackedUp;
    @BindView(R2.id.coin_type)
    TextView coinType;
    private int walletType = 0;
    private String walletAddressStr;
    private WalletEntity mWallet;
    private InputPwdDialog mDialog;
    private InputPwdDialog2 mDialog2;
    private int from;
    private List<WalletEntity> mWallName;
    private IdentityOutDialog outDialog;
    private List<WalletLogoBean> list = new ArrayList<>();

    @Override
    public int initContentView() {
        return R.layout.activity_manage_identity;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        from = getIntent().getIntExtra("from", 0);
        walletAddressStr = getIntent().getStringExtra("walletAddress");
        walletType = getIntent().getIntExtra("type", -1);
        mWallet = WalletDBUtil.getInstent(this).getWalletInfoByAddress(walletAddressStr, walletType);
        tvTitle.setText(getString(R.string.manage_identity_wallet));
        GlideUtil.showImg(this, mWallet.getTrueLogo(), walletLogo);
        walletName.setText(mWallet.getName());
        mDialog = new InputPwdDialog(this, getString(R.string.place_edit_password));
        mDialog.setonclick(new InputPwdDialog.Onclick() {
            @Override
            public void Yes(String pwd) {
                mDialog.dismiss();
                if (!mWallet.getmPassword().equals(DecriptUtil.MD5(pwd))) {
                    ToastUtil.showToast(getString(R.string.password_error2));
                    return;
                }
                Intent intent = new Intent(ManageIdentityActivity.this, BackUpMnemonicActivity.class);
                intent.putExtra("pwd", pwd);
                intent.putExtra("wallet", mWallet);
                intent.putExtra("from", from);
                startActivity(intent);
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
                for (int i = 0; i < mWallName.size(); i++) {
                    if (mWallName.get(i).getType() == WalletUtil.TRX_COIN) {
                        SettingPrefUtil.setWalletLoadNum(mWallet.getAllAddress(), 0);
                    }
                }
                SettingPrefUtil.setWalletAddress(ManageIdentityActivity.this, "");
                SettingPrefUtil.setWalletType(ManageIdentityActivity.this, -1);
                
                RxBus.getInstance().post(new RxNotice(RxNotice.MSG_DELETE_WALLET));
                WalletDBUtil.getInstent(ManageIdentityActivity.this).delWallet();
                AppApplication.finishAllActivityExMain();

                
                SpUtil.cleanAllBiometricPaySetting();
                

                finish();
                
                RxBus.getInstance().post(new RxNotice(RxNotice.MSG_LOGOUT));
            }

            @Override
            public void No() {
                mDialog2.dismiss();
            }
        });
        outDialog = new IdentityOutDialog(this);
        outDialog.setClick(new IdentityOutDialog.Click() {
            @Override
            public void goOut() {
                mDialog2.show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWallName = walletDBUtil.getWallName();
        showData();
        initData();
    }

    @Override
    public void initData() {
        List<Integer> deflist = walletDBUtil.getDefWalletType();
        list.clear();
        if (WalletSpUtil.getEnableMcc() == 1) {
            list.add(new WalletLogoBean(R.mipmap.mcc_logo, getString(R.string.default_token_name).toUpperCase(), 0, WalletUtil.MCC_COIN, 0));
        }
        if (WalletSpUtil.getEnableDm() == 1) {
            list.add(new WalletLogoBean(R.mipmap.dm_logo, "DM", 0, WalletUtil.DM_COIN, 0));
        }
        if (WalletSpUtil.getEnableEth() == 1) {
            list.add(new WalletLogoBean(R.mipmap.eth_logo, "ETH", 0, WalletUtil.ETH_COIN, 0));
        }
        if (WalletSpUtil.getEnableBtc() == 1) {
            list.add(new WalletLogoBean(R.mipmap.btc_logo, "BTC", 0, WalletUtil.BTC_COIN, 0));
        }
        if (WalletSpUtil.getEnableEos() == 1) {
            list.add(new WalletLogoBean(R.mipmap.eos_logo, "EOS", 0, WalletUtil.EOS_COIN, 0));
        }
        if (WalletSpUtil.getEnableOther() == 1) {
            list.add(new WalletLogoBean(R.mipmap.llq_other, getString(R.string.default_other_token_name).toUpperCase(), 0, WalletUtil.OTHER_COIN, 0));
        }
        if (WalletSpUtil.getEnableXrp() == 1) {
            list.add(new WalletLogoBean(R.mipmap.xrp_logo, "XRP", 0, WalletUtil.XRP_COIN, 0));
        }
        if (WalletSpUtil.getEnableTrx() == 1) {
            list.add(new WalletLogoBean(R.mipmap.trx_logo, "TRX", 0, WalletUtil.TRX_COIN, 0));
        }
        if (WalletSpUtil.getEnableEtf() == 1) {
            list.add(new WalletLogoBean(R.mipmap.etf_logo, getString(R.string.default_etf).toUpperCase(), 0, WalletUtil.ETF_COIN, 0));
        }
        if (WalletSpUtil.getEnableDmf() == 1) {
            list.add(new WalletLogoBean(R.mipmap.hb_dmf_logo, getString(R.string.default_dmf_hb).toUpperCase(), 0, WalletUtil.DMF_COIN, 0));
        }
        if (WalletSpUtil.getEnableDmfBa() == 1) {
            list.add(new WalletLogoBean(R.mipmap.bian_dmf_logo, getString(R.string.default_dmf_ba).toUpperCase(), 0, WalletUtil.DMF_BA_COIN, 0));
        }
        if (WalletSpUtil.getEnableHt() == 1) {
            list.add(new WalletLogoBean(R.mipmap.huobi_logo, "HECO", 0, WalletUtil.HT_COIN, 0));
        }

        if (WalletSpUtil.getEnableBnb() == 1) {
            list.add(new WalletLogoBean(R.mipmap.bnb_logo, "BSC", 0, WalletUtil.BNB_COIN, 0));
        }

        if (WalletSpUtil.getEnableFIL() == 1) {
            list.add(new WalletLogoBean(R.mipmap.fil_logo, "FIL", 0, WalletUtil.FIL_COIN, 0));
        }
        if (WalletSpUtil.getEnableDOGE() == 1) {
            list.add(new WalletLogoBean(R.mipmap.doge_logo, "DOGE", 0, WalletUtil.DOGE_COIN, 0));
        }
        if (WalletSpUtil.getEnableDOT() == 1) {
            list.add(new WalletLogoBean(R.mipmap.dot_logo, "DOT", 0, WalletUtil.DOT_COIN, 0));
        }
        if (WalletSpUtil.getEnableLTC() == 1) {
            list.add(new WalletLogoBean(R.mipmap.ltc_logo, "LTC", 0, WalletUtil.LTC_COIN, 0));
        }
        if (WalletSpUtil.getEnableBCH() == 1) {
            list.add(new WalletLogoBean(R.mipmap.bch_logo, "BCH", 0, WalletUtil.BCH_COIN, 0));
        }
        if (WalletSpUtil.getEnableZEC() == 1) {
            list.add(new WalletLogoBean(R.mipmap.zec_logo, "ZEC", 0, WalletUtil.ZEC_COIN, 0));
        }
        if (WalletSpUtil.getEnableADA() == 1) {
            list.add(new WalletLogoBean(R.mipmap.ada_logo, "ADA", 0, WalletUtil.ADA_COIN, 0));
        }
        if (WalletSpUtil.getEnableETC() == 1) {
            list.add(new WalletLogoBean(R.mipmap.etc_logo, "ETC", 0, WalletUtil.ETC_COIN, 0));
        }
        if (WalletSpUtil.getEnableSGB() == 1) {
            list.add(new WalletLogoBean(R.mipmap.sgb_logo, "SGB", 0, WalletUtil.SGB_COIN, 0));
        }
        if (WalletSpUtil.getEnableSOL() == 1) {
            list.add(new WalletLogoBean(R.mipmap.sol_logo, "SOL", 0, WalletUtil.SOL_COIN, 0));
        }
        if (WalletSpUtil.getEnableMATIC() == 1) {
            list.add(new WalletLogoBean(R.mipmap.matic_logo, "POLYGON", 0, WalletUtil.MATIC_COIN, 0));
        }

        for (int i = 0; i < deflist.size(); i++) {
            int choosetype = deflist.get(i);
            for (int num = 0; num < list.size(); num++) {
                if (list.get(num).getWalletType() == choosetype) {
                    list.get(num).setIsdef(1);
                    break;
                }
            }
        }
        String coinName = "";
        Log.d("zzz", new Gson().toJson(list));
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getIsdef() == 0) {
                    coinName = coinName + list.get(i).getWalletName() + ",";
                }
            }
            if (coinName.length() > 1) {
                coinName = coinName.substring(0, coinName.length() - 1);
            }
        }
        if (TextUtils.isEmpty(coinName)) {
            coinType.setText("");
            coinType.setVisibility(View.GONE);
        } else {
            coinType.setText(getString(R.string.coin_support) + " " + coinName);
            coinType.setVisibility(View.VISIBLE);
        }
    }

    private void showData() {
        mWallet = WalletDBUtil.getInstent(this).getWalletInfoByAddress(walletAddressStr, walletType);
        if (mWallet.getMMnemonicBackup() == 1) {
            notBackedUp.setText("");
        } else {
            notBackedUp.setText(getString(R.string.not_backed_up));
        }
    }

    @OnClick({R2.id.tv_back, R2.id.me_change_pwd, R2.id.not_backed_up, R2.id.drop_out, R2.id.add_coin})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.tv_back) {
            finish();
        } else if (i == R.id.not_backed_up) {
            mDialog.show();
        } else if (i == R.id.drop_out) {
            if (allBackUp()) {
                outDialog.show();
            } else {
                ToastUtil.showToast(getString(R.string.backup_your_wallet_first) + ":" + error);
            }
        } else if (i == R.id.add_coin) {
            Intent intent = new Intent(ManageIdentityActivity.this, AddWalletTypeActivity.class);
            startActivity(intent);
        } else if (i == R.id.me_change_pwd) {
            Intent intent = new Intent(this, ChangePwdActivity.class);
            startActivity(intent);
        }
    }

    private String error;

    
    private boolean allBackUp() {
        boolean allback = true;
        Log.d("zzz", new Gson().toJson(mWallName));
        for (int i = 0; i < mWallName.size(); i++) {
            WalletEntity walletEntity = mWallName.get(i);
            if (walletEntity.getMMnemonicBackup() == 0) {
                error = walletEntity.getName();
                allback = false;
                break;
            }
        }
        return allback;
    }
}
