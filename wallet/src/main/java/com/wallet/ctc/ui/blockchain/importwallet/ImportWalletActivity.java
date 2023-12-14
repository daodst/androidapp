

package com.wallet.ctc.ui.blockchain.importwallet;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.base.BaseWebViewActivity;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.DBManager;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.ui.blockchain.managewallet.ChooseCreatImportTypeActivity;
import com.wallet.ctc.util.SettingPrefUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.app.ActivityRouter;
import common.app.AppApplication;
import common.app.mall.util.ToastUtil;



public class ImportWalletActivity extends BaseActivity {

    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.div_view)
    View divView;
    @BindView(R2.id.img_action)
    ImageView imgAction;
    @BindView(R2.id.main_tab)
    TabLayout mTab;
    @BindView(R2.id.main_viewpager)
    ViewPager mViewPager;
    private ArrayList<Fragment> fragmentList;
    private List<String> titleList;
    private Intent intent;
    private ImportWalletOfficialWalletFragment keystoreF;
    private int type = -1;
    private ImportWalletMnemonicFragment  mnemonicFragment;

    @Override
    public int initContentView() {
        return R.layout.activity_import_wallet;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        tvTitle.setText(getString(R.string.import_wallet));
        divView.setVisibility(View.GONE);

        type = getIntent().getIntExtra("type", -1);
        if (-1 == type) {
            type = SettingPrefUtil.getWalletType(this);
        }
        if (type != WalletUtil.BTC_COIN && type != WalletUtil.XRP_COIN && type != WalletUtil.TRX_COIN) {
            imgAction.setVisibility(View.VISIBLE);
            imgAction.setImageResource(R.mipmap.saoyisao);
        }
        fragmentList = new ArrayList<>();
        
        titleList = new ArrayList<>();
        titleList.add(getString(R.string.mnemonic));
        mnemonicFragment=new ImportWalletMnemonicFragment();
        fragmentList.add(mnemonicFragment);
        List<WalletEntity> wallNamelist= WalletDBUtil.getInstent(this).getWallName();
        if (TextUtils.isEmpty(BuildConfig.ENABLE_CREAT_ALL_WALLET )||(null!=wallNamelist&&wallNamelist.size()>0&&wallNamelist.get(0).getLevel()==1)) {
            if(type!= WalletUtil.SGB_COIN) {
                titleList.add(getString(R.string.private_key));
                fragmentList.add(new ImportWalletPrivateKeyFragment());
            }
            if (type != WalletUtil.BTC_COIN && type != WalletUtil.XRP_COIN && type != WalletUtil.TRX_COIN&&type!= WalletUtil.SGB_COIN) {
                titleList.add(getString(R.string.official_wallet));
                keystoreF = new ImportWalletOfficialWalletFragment();
                fragmentList.add(keystoreF);
            }
            titleList.add(getString(R.string.observe_wallet));
            fragmentList.add(new ImportWalletAddressFragment());
        }else {
            mTab.setVisibility(View.GONE);
            imgAction.setVisibility(View.GONE);
        }

    }

    @Override
    public void initData() {
        
        for (int i = 0; i < titleList.size(); i++) {
            mTab.addTab(mTab.newTab().setText(titleList.get(i)));
        }
        
        mTab.setupWithViewPager(mViewPager);
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return titleList.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return titleList.get(position);
            }
        });
        mViewPager.setOffscreenPageLimit(titleList.size());
    }

    @OnClick({R2.id.tv_back, R2.id.img_action})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.tv_back) {
            finish();

        } else if (i == R.id.img_action) {
            Intent intent = ActivityRouter.getEmptyContentIntent(this,ActivityRouter.Common.F_QRCodeFragment);
            startActivityForResult(intent, 2000);

        } else {
        }
    }
    @Override
    
    public boolean aoutHide(float x, float y) {
        return mnemonicFragment.aoutHide(x,y);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2000 && resultCode == RESULT_OK) {
            if (null != keystoreF) {
                mViewPager.setCurrentItem(1);
                keystoreF.setKey(data.getStringExtra("data"));
            }
        }
    }

    public int getType() {
        return type;
    }

    public void insert(WalletEntity walletEntity, String pwd) {
        WalletEntity mOldWallet = DBManager.getInstance(this).queryWalletDetail(walletEntity.getAllAddress(), type);
        SettingPrefUtil.setWalletTypeAddress(this, type, walletEntity.getAllAddress());
        
        if (null != mOldWallet) {
            if(mOldWallet.getLevel()==1){
                ToastUtil.showToast(getString(R.string.wallet_isfen));
                return;
            }
            ToastUtil.showToast(getString(R.string.hint_exist_wallet));
            
        } else {
            Random rand = new Random();
            int i = rand.nextInt(5);
            walletEntity.setLogo(i);
            walletEntity.setUserName(WalletDBUtil.USER_ID);
            walletEntity.setType(type);
            creatOrInsertWallet(type, walletEntity.getAllAddress());
            DBManager.getInstance(ImportWalletActivity.this).insertWallet(walletEntity);
            jump(pwd);
        }
    }

    public void insertAll(WalletEntity walletEntity,int type) {
        if (BuildConfig.ENABLE_CREAT_ALL_WALLET_TYPE == type) {
            SettingPrefUtil.setWalletTypeAddress(this, type, walletEntity.getAllAddress());
        }
        
        Random rand = new Random();
        int i = rand.nextInt(5);
        walletEntity.setLogo(i);
        walletEntity.setUserName(WalletDBUtil.USER_ID);
        walletEntity.setType(type);
        walletEntity.setLevel(1);
        creatOrInsertWallet(type, walletEntity.getAllAddress());
        DBManager.getInstance(ImportWalletActivity.this).insertWallet(walletEntity);
    }

    public void jump(String pwd) {
        Intent data = new Intent();
        data.putExtra("walletPwd", pwd);
        setResult(RESULT_OK, data);
        finish();
        if(null!=AppApplication.findActivity(ChooseCreatImportTypeActivity.class)) {
            AppApplication.findActivity(ChooseCreatImportTypeActivity.class).finish();
        }
    }

    


    public void getUrl(String type, String title) {
        Intent intent = new Intent(ImportWalletActivity.this, BaseWebViewActivity.class);
        intent.putExtra("type", 1);
        intent.putExtra("sysName", type);
        intent.putExtra("title", title);
        startActivity(intent);
    }
}
