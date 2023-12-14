

package com.wallet.ctc.ui.blockchain.choosewallet;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.app.root.wallet.NxnWalletView;
import com.app.root.wallet.OnClickDetectorListener;
import com.app.root.wallet.WalletBubbleLayoutView;
import com.google.android.material.appbar.AppBarLayout;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.WalletLogoBean;
import com.wallet.ctc.ui.blockchain.home.NewHomeActivity;
import com.wallet.ctc.ui.blockchain.importwallet.ImportWalletActivity;
import com.wallet.ctc.ui.blockchain.managewallet.AddWalletTypeActivity;
import com.wallet.ctc.ui.blockchain.managewallet.ChooseCreatImportTypeActivity;
import com.wallet.ctc.util.SettingPrefUtil;
import com.wallet.ctc.view.dialog.choosewallet.ChooseWalletDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import common.app.base.them.Eyes;



public class ChooseManagerWalletActivity extends BaseActivity {

    @BindView(R2.id.moreIv)
    ImageView moreIv;
    @BindView(R2.id.appbar)
    AppBarLayout appBarLayout;
    @BindView(R2.id.walletBubbleView)
    WalletBubbleLayoutView walletBubbleView;
    @BindView(R2.id.walletsLayout)
    LinearLayout walletsLayout;

    private static final String KEY_TYPE = "walletType";
    private int walletType;
    private ChooseWalletDialog mChooseWalletDialog;

    public static Intent getIntent(Context context, int walletType) {
        Intent intent = new Intent(context, ChooseManagerWalletActivity.class);
        intent.putExtra(KEY_TYPE, walletType);
        return intent;
    }

    @Override
    public int initContentView() {
        return R.layout.activity_choose_m_wallet;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        Eyes.setTranslucent(this);
        int wType = getIntent().getIntExtra(KEY_TYPE, WalletUtil.MCC_COIN);
        mChooseWalletDialog = new ChooseWalletDialog(this, true);
        List<WalletEntity> allWallets = walletDBUtil.getWallName();
        List<WalletLogoBean> allLogos = walletDBUtil.getWalletLogos(true);
        mChooseWalletDialog.show(allWallets, allLogos);
        View contentView = mChooseWalletDialog.getContentView();
        walletsLayout.addView(contentView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        mChooseWalletDialog.setSelecte(wType);
        selecteWallet(wType);
        mChooseWalletDialog.setOnSelectTypeListener(wltType->{
            selecteWallet(wltType);
        });
        mChooseWalletDialog.setChooseWallet(new ChooseWalletDialog.ChooseWallet() {
            @Override
            public void onChangeWallet(String address, int type) {
                
                SettingPrefUtil.setWalletTypeAddress(ChooseManagerWalletActivity.this, type, address);
                mChooseWalletDialog.refreshState();
                startActivity(new Intent(ChooseManagerWalletActivity.this, NewHomeActivity.class));
            }

            @Override
            public void addWallet(int type) {
                Intent  intent = null;
                if (type == 1) {
                    intent = new Intent(ChooseManagerWalletActivity.this, AddWalletTypeActivity.class);
                } else {
                    intent = new Intent(ChooseManagerWalletActivity.this, ChooseCreatImportTypeActivity.class);
                    intent.putExtra("from", 0);
                }
                startActivity(intent);
            }
        });

        walletBubbleView.setOnClickDetectorListener(new OnClickDetectorListener() {
            @Override
            public void onClick(String type) {

            }

            @Override
            public void onLongClick(String type) {

            }

            @Override
            public void onHit(String fromType, String toType) {
                
                Intent intent = new Intent(ChooseManagerWalletActivity.this, ImportWalletActivity.class);
                intent.putExtra("type", walletType);
                startActivity(intent);
            }
        });
    }

    @Override
    public void initData() {

    }


    private void selecteWallet(int wType) {
        this.walletType = wType;
        List<WalletEntity> walletEntityList = walletDBUtil.getWalletList(walletType);
        int count = walletEntityList != null ? walletEntityList.size() : 0;
        if (walletType == WalletUtil.MCC_COIN) {
            walletBubbleView.setTypeCount(NxnWalletView.TYPE_DST, count);
        } else if(walletType == WalletUtil.BNB_COIN) {
            walletBubbleView.setTypeCount(NxnWalletView.TYPE_BSC, count);
        } else if(walletType == WalletUtil.ETH_COIN) {
            walletBubbleView.setTypeCount(NxnWalletView.TYPE_ETH, count);
        }
    }
}
