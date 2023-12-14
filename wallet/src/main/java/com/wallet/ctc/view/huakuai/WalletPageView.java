

package com.wallet.ctc.view.huakuai;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

import com.google.gson.Gson;
import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.R;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.nft.ui.NftAddAssetsActivity;
import com.wallet.ctc.ui.blockchain.addassets.BaseAddAssetsActivity;
import com.wallet.ctc.ui.blockchain.backupwallet.BackUpCaveatActivity;
import com.wallet.ctc.ui.blockchain.collectmoney.CollectMoneyActivity;
import com.wallet.ctc.ui.blockchain.mywallet.ManageIdentityActivity;
import com.wallet.ctc.ui.blockchain.mywallet.MyWalletActivity;
import com.wallet.ctc.ui.dapp.DappWebViewActivity;
import com.wallet.ctc.util.AllUtils;
import com.wallet.ctc.util.GlideUtil;
import com.wallet.ctc.util.LogUtil;
import com.wallet.ctc.util.SettingPrefUtil;
import com.wallet.ctc.view.viewpager.NoScrollViewPager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import common.app.base.view.bannervew.MenuPagerAdapter;
import common.app.mall.util.ToastUtil;
import common.app.pojo.CurrencyBean;
import common.app.utils.SpUtil;



public class WalletPageView extends LinearLayout {

    
    private Context mContext;

    private NoScrollViewPager mMenuPager = null;
    List<WalletEntity> list;
    
    private LinearLayout mGroup;

    private List<View> mViewsList = new ArrayList<View>();

    private PageSelected mListen;
    
    private View[] mImageViews = null;

    private View addAssets;

    private View trxInfo;

    private TextView daikuan, nengliang, tvZiChan, tvNFT;
    private View vZiChan, vNFT;
    private ProgressBar daikuanBar, nengliangBar;
    private boolean isClickNFT;

    
    public WalletPageView(Context context) {
        super(context);
    }

    public WalletPageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.view_wallet_viewpager, this);
        mMenuPager = (NoScrollViewPager) findViewById(R.id.pager_banner);
        addAssets = findViewById(R.id.add_assets);
        trxInfo = findViewById(R.id.trx_info);
        daikuan = findViewById(R.id.daikuan);
        nengliang = findViewById(R.id.nengliang);
        daikuanBar = findViewById(R.id.daikuan_bar);
        nengliangBar = findViewById(R.id.nengliang_bar);
        tvZiChan = findViewById(R.id.tvZiChan);
        tvNFT = findViewById(R.id.tvNFT);
        vZiChan = findViewById(R.id.vZiChan);
        vNFT = findViewById(R.id.vNFT);
        findViewById(R.id.add_assets).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                List<WalletEntity> wallName = WalletDBUtil.getInstent(mContext).getWallName();
                if (null == wallName || wallName.size() == 0) {
                    ToastUtil.showToast(mContext.getString(R.string.no_more_wallet_can_do));
                    return;
                }
                if (isClickNFT) {
                    NftAddAssetsActivity.intent(context);
                } else {
                    Intent intent = new Intent(context, BaseAddAssetsActivity.class);
                    context.startActivity(intent);
                }
            }
        });
        findViewById(R.id.nengliang_lin).setOnClickListener(v -> DappWebViewActivity.startDappWebViewActivity(mContext, "https://tronscan.io/#/wallet/resources?from=tronlink"));
        findViewById(R.id.daikuan_lin).setOnClickListener(v -> DappWebViewActivity.startDappWebViewActivity(mContext, "https://tronscan.io/#/wallet/resources?from=tronlink"));
        findViewById(R.id.lZiChan).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListen != null) {
                    isClickNFT = false;
                    tvZiChan.setTextSize(18);
                    vZiChan.setVisibility(GONE);
                    tvNFT.setTextSize(15);
                    vNFT.setVisibility(INVISIBLE);
                    mListen.changBottomItem(0);
                }
            }
        });
        findViewById(R.id.lNFT).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListen != null) {
                    isClickNFT = true;
                    tvNFT.setTextSize(18);
                    vNFT.setVisibility(VISIBLE);
                    tvZiChan.setTextSize(15);
                    vZiChan.setVisibility(INVISIBLE);
                    mListen.changBottomItem(1);
                }
            }
        });
        mMenuPager.addOnPageChangeListener(new GuidePageChangeListener());
        
        mGroup = (LinearLayout) findViewById(R.id.viewGroup);
        mMenuPager.setAdapter(new MenuPagerAdapter(mViewsList));

    }

    public interface PageSelected {
        void onPageSelected(int position);

        void changBottomItem(int pos);
    }

    public void setPageSelected(PageSelected l) {
        mListen = l;
    }

    public void changeZichan(int position) {
        try {
            WalletEntity walletBean = list.get(position);
            View view = mViewsList.get(position);
            if (null != walletBean && null != view) {
                TextView walletsumPriceTv = ((TextView) view.findViewById(R.id.wallet_sumprice));
                if (null != walletsumPriceTv) {
                    walletsumPriceTv.setText(walletBean.getSumPrice());
                }
                if (walletBean.getType() == WalletUtil.TRX_COIN) {
                    BigDecimal wlan = new BigDecimal(walletBean.getBroadband());
                    wlan = wlan.divide(new BigDecimal("1024"), 2, BigDecimal.ROUND_HALF_UP);
                    daikuan.setText(wlan.toPlainString());
                    nengliang.setText(walletBean.getEnergy());
                    daikuanBar.setMax(walletBean.getBroadbandIntMax());
                    nengliangBar.setMax(walletBean.getEnergyIntMax());
                    daikuanBar.setProgress(new BigDecimal(walletBean.broadband).intValue());
                    nengliangBar.setProgress(new BigDecimal(walletBean.energy).intValue());
                }
            }

        } catch (Exception e) {
            LogUtil.d(e.toString());
        }
    }

    
    public void setWalletData(List<WalletEntity> list) {
        
        this.list = list;
        if ((list.get(0).getType() == WalletUtil.BTC_COIN) && !isClickNFT) {
            addAssets.setVisibility(GONE);
        } else {
            addAssets.setVisibility(VISIBLE);
        }
        if (list.get(0).getType() == WalletUtil.TRX_COIN) {
            trxInfo.setVisibility(VISIBLE);
        } else {
            trxInfo.setVisibility(GONE);
        }
        mViewsList.clear();
        bindData(list);
        mMenuPager.removeAllViews();
        mImageViews = new View[mViewsList.size()];
        if (mViewsList.size() < 2) {
            mGroup.setVisibility(View.GONE);
        } else {
            mGroup.removeAllViews();
            mGroup.setVisibility(View.GONE);
            for (int i = 0; i < mViewsList.size(); i++) {
                View imageView = new View(mContext);
                LayoutParams layout = new LayoutParams(AllUtils.dp2px(mContext, 29), AllUtils.dp2px(mContext, 2));
                layout.setMargins(10, 0, 12, 0);
                imageView.setLayoutParams(layout);
                if (i == 0) {
                    imageView.setBackgroundColor(0xffffffff);
                } else {
                    imageView.setBackgroundColor(0xff1A2C54);
                }
                mImageViews[i] = imageView;
                mGroup.addView(mImageViews[i]);
            }
        }
        mMenuPager.setAdapter(new MenuPagerAdapter(mViewsList));
        String address = SettingPrefUtil.getWalletAddress(mContext);
        int wtype = SettingPrefUtil.getWalletType(mContext);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getAllAddress().equals(address) && list.get(i).getType() == wtype) {
                mMenuPager.setCurrentItem(i);
                break;
            }
        }
    }


    private void bindData(List<WalletEntity> list) {
        for (int i = 0; i < list.size(); i++) {
            WalletEntity mBean = list.get(i);
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            View view = inflater.inflate(R.layout.item_home_wallet_page, null);
            View body = view.findViewById(R.id.body_bg);
            ImageView walletLogo = (ImageView) view.findViewById(R.id.wallet_logo);
            TextView walletAddress = (TextView) view.findViewById(R.id.wallet_address);
            TextView walletAddress2 = (TextView) view.findViewById(R.id.wallet_address2);
            TextView walletName = (TextView) view.findViewById(R.id.wallet_name);
            TextView walletPrice = (TextView) view.findViewById(R.id.wallet_sumprice);
            TextView walletBeifen = (TextView) view.findViewById(R.id.go_beifen);
            TextView hbfh = (TextView) view.findViewById(R.id.hbfuhao);

            String dcu = SpUtil.getDcu();
            if (dcu.equals(common.app.BuildConfig.CURRENCY_UNIT)) {
                dcu = BuildConfig.CURRENCY_SYMBOL;
            } else {
                Gson gson = new Gson();
                CurrencyBean bean = gson.fromJson(dcu, CurrencyBean.class);
                if (null != bean) {
                    dcu = bean.getCurrency_symbol();
                }
            }
            hbfh.setText(dcu);
            GlideUtil.showImg(mContext, mBean.getTrueLogo(), walletLogo);
            walletName.setText(mBean.getName());
            walletAddress.setText(mBean.getmAddress());
            if (mBean.getType() == WalletUtil.MCC_COIN && !TextUtils.isEmpty(mBean.getDefaultAddress())) {
                walletAddress2.setVisibility(VISIBLE);
                walletAddress2.setText(mBean.getmDefaultAddress());
            }
            walletPrice.setText(mBean.getSumPrice());

            if(mBean.getType() == WalletUtil.MCC_COIN){
                body.setBackgroundResource(R.mipmap.wallet_home_dst_bg);
            } else if(mBean.getType() == WalletUtil.ETH_COIN){
                body.setBackgroundResource(R.mipmap.wallet_home_eth_bg);
            } else if(mBean.getType() == WalletUtil.BNB_COIN){
                body.setBackgroundResource(R.mipmap.wallet_home_bsc_bg);
            } else {
                body.setBackgroundResource(R.mipmap.wallet_home_bg);
            }

            if (mBean.getType() == WalletUtil.BNB_COIN){
                
                hbfh.setTextColor(ContextCompat.getColor(mContext, R.color.goin_theme_color));
                walletPrice.setTextColor(ContextCompat.getColor(mContext, R.color.goin_theme_color));
                walletAddress.setTextColor(ContextCompat.getColor(mContext, R.color.goin_theme_color));
                walletAddress2.setTextColor(ContextCompat.getColor(mContext, R.color.goin_theme_color));
                walletName.setTextColor(ContextCompat.getColor(mContext, R.color.goin_theme_color));
                walletAddress.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getDrawable(R.mipmap.copy_wallet_address_yellow), null);
                walletAddress2.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getDrawable(R.mipmap.copy_wallet_address_yellow), null);
                walletPrice.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getDrawable(R.mipmap.home_erweima_goind), null);
            } else {
                
                hbfh.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                walletPrice.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                walletAddress.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                walletAddress2.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                walletName.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                walletAddress.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getDrawable(R.mipmap.home_wallet_addresscopy), null);
                walletAddress2.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getDrawable(R.mipmap.home_wallet_addresscopy), null);
                walletPrice.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getDrawable(R.mipmap.home_erweima), null);
            }

            body.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MyWalletActivity.class);
                    intent.putExtra("walletAddress", mBean.getAllAddress());
                    intent.putExtra("type", mBean.getType());
                    mContext.startActivity(intent);
                }
            });
            walletPrice.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mBean.getmBackup() == 1) {
                        String type = "";
                        if (mBean.getType() == WalletUtil.DM_COIN) {
                            type = "dm";
                        } else if (mBean.getType() == WalletUtil.MCC_COIN) {
                            type = "dst";
                        } else if (mBean.getType() == WalletUtil.OTHER_COIN) {
                            type = mContext.getString(R.string.default_other_token_name);
                        }
                        CollectMoneyActivity.startCollectMoneyActivity(mContext, mBean.getType(), type, 18);
                    } else {
                        Intent intent = new Intent(mContext, BackUpCaveatActivity.class);
                        mContext.startActivity(intent);
                    }
                }
            });
            walletBeifen.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent;
                    if (mBean.getLevel() == 1) {
                        
                        intent = new Intent(mContext, ManageIdentityActivity.class);
                        intent.putExtra("walletAddress", mBean.getAllAddress());
                        intent.putExtra("type", mBean.getType());
                        intent.putExtra("from", 0);
                    } else {
                        
                        intent = new Intent(mContext, MyWalletActivity.class);
                        intent.putExtra("walletAddress", mBean.getAllAddress());
                        intent.putExtra("type", mBean.getType());
                        mContext.startActivity(intent);
                    }
                }
            });
            walletAddress.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                    
                    cm.setText(mBean.getAllAddress());
                    ToastUtil.showToast(mContext.getResources().getString(R.string.copy_success));
                }
            });
            walletAddress2.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                    
                    cm.setText(mBean.getDefaultAddress());
                    ToastUtil.showToast(mContext.getResources().getString(R.string.copy_success));
                }
            });
            mViewsList.add(view);
        }
    }

    
    private final class GuidePageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if ((list.get(position).getType() == WalletUtil.BTC_COIN) && !isClickNFT) {
                addAssets.setVisibility(GONE);
            } else {
                addAssets.setVisibility(VISIBLE);
            }
            if (list.get(position).getType() == WalletUtil.TRX_COIN) {
                trxInfo.setVisibility(VISIBLE);
            } else {
                trxInfo.setVisibility(GONE);
            }
            mListen.onPageSelected(position);
            mImageViews[position].setBackgroundColor(0xffffffff);
            for (int i = 0; i < mImageViews.length; i++) {
                if (position != i) {
                    mImageViews[i].setBackgroundColor(0xff1A2C54);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
