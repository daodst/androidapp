

package com.wallet.ctc.ui.blockchain.backupwallet;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.view.dialog.ScreenshotsDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;



public class BackUpKeystoreActivity extends BaseActivity {

    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.img_action)
    ImageView imgAction;
    @BindView(R2.id.main_tab)
    TabLayout mTab;
    @BindView(R2.id.main_viewpager)
    ViewPager mViewPager;
    private ArrayList<Fragment> fragmentList;
    private List<String> titleList;
    private Intent intent;
    private ScreenshotsDialog mDialog;
    public static String walletKeystore;

    @Override
    public int initContentView() {
        return R.layout.activity_import_wallet;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        walletKeystore=getIntent().getStringExtra("wallet");
        tvTitle.setText(getString(R.string.export_the_keystore));
        mDialog = new ScreenshotsDialog(this,getString(R.string.backup_keystore_dialog));
        mDialog.show();
        fragmentList = new ArrayList<>();
        
        titleList = new ArrayList<>();
        titleList.add("keystore");
        titleList.add(getString(R.string.qrcode));
        fragmentList.add(new BackupKeystoreFragment());
        fragmentList.add(new BackupKeystoryQrcodeFragment());

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

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick({R2.id.tv_back})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.tv_back) {
            finish();

        } else {
        }
    }

}
