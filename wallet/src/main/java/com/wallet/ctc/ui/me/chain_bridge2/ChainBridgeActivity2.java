package com.wallet.ctc.ui.me.chain_bridge2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import androidx.annotation.Nullable;

import com.wallet.ctc.R;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.databinding.ActivityChainBridge2Binding;
import com.wallet.ctc.databinding.ItemChainMapIvNameBinding;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.ui.blockchain.setnode.SettingNodeActivity;
import com.wallet.ctc.ui.me.chain_bridge2.orders.ChainBridgeOrdersActivity;
import com.wallet.ctc.ui.me.chain_bridge2.submit_confirm.ChainBridgeConfirmActivity;
import com.wallet.ctc.ui.realize.USDTRealizeActivity;
import com.wallet.ctc.util.WalletSpUtil;
import com.wallet.ctc.view.dialog.choosewallet.ChooseWalletDialog;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import common.app.base.BaseActivity;
import common.app.pojo.NameIdBean;
import common.app.ui.view.TitleBarView;


public class ChainBridgeActivity2 extends BaseActivity<ChainBridgeVM2> {

    ActivityChainBridge2Binding mVBinding;
    public static final String ASSET_USDT = "USDT";
    private String mAssetCoin = ASSET_USDT;
    private WalletEntity fromWallet, toWallet;
    private String mBalance;


    private static final String KEY_FROM_TYPE = "fromType";
    private static final String KEY_TO_TYPE = "toType";

    public static Intent getIntent(Context from, int fromType, int toType) {
        Intent intent = new Intent(from, ChainBridgeActivity2.class);
        intent.putExtra(KEY_FROM_TYPE, fromType);
        intent.putExtra(KEY_TO_TYPE, toType);
        return intent;
    }

    @Override
    public void initParam() {
        int fromType = getIntent().getIntExtra(KEY_FROM_TYPE, -1);
        int toType = getIntent().getIntExtra(KEY_TO_TYPE, -1);
        if (fromType != -1 && toType != -1) {
            fromWallet = WalletDBUtil.getInstent(this).getWalletInfo(fromType);
            toWallet = WalletDBUtil.getInstent(this).getWalletInfo(toType);
        }
        
        if (fromWallet == null) {
            fromWallet = WalletSpUtil.getChainBridgeFrom(this);
        }
        if (toWallet == null) {
            toWallet = WalletSpUtil.getChainBridgeTo(this);
        }
    }

    @Override
    public View initBindingView(Bundle savedInstanceState) {
        mVBinding = ActivityChainBridge2Binding.inflate(getLayoutInflater());
        return mVBinding.getRoot();
    }

    @Override
    public void initView(@Nullable View view) {
        mVBinding.titleBar.setOnTitleBarClickListener(new TitleBarView.TitleBarClickListener() {
            @Override
            public void leftClick() {
                finish();
            }

            @Override
            public void rightClick() {
                startActivity(new Intent(ChainBridgeActivity2.this, ChainBridgeOrdersActivity.class));
            }
        });

        
        mVBinding.exAssetSpinner.setAdapter(new AssetListAdpater(this));
        mVBinding.exAssetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                AssetListAdpater assetListAdpater = (AssetListAdpater) adapterView.getAdapter();
                NameIdBean asset = assetListAdpater.getItem(i);
                if (!TextUtils.isEmpty(asset.id) && !asset.id.equals(mAssetCoin)) {
                    mAssetCoin = asset.id;
                    initMap();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        
        mVBinding.fromCoinLayout.setOnClickListener(view1 -> {
            showSelectFromWalletDialog();
        });

        
        mVBinding.toCoinLayout.setOnClickListener(view1 -> {
            showSelectToWalletDialog();
        });

        
        mVBinding.selectToAddrIv.setOnClickListener(view1 -> {
            showSelectToWalletDialog();
        });

        
        mVBinding.changeMapIv.setOnClickListener(view1 -> {
            WalletEntity wallet = fromWallet;
            fromWallet = toWallet;
            toWallet = wallet;
            initMap();
        });


        
        mVBinding.confirmBtn.setOnClickListener(view1 -> {
            if (fromWallet == null) {
                showToast(R.string.chain_bridge_please_from);
                return;
            }
            if (toWallet == null) {
                showToast(R.string.chain_bridge_please_to);
                return;
            }
            if (fromWallet.getType() == toWallet.getType()) {
                showToast(R.string.chain_bridge_please_diff);
                return;
            }
            
            WalletSpUtil.saveChainBridgeFromTo(fromWallet.getAllAddress(), fromWallet.getType(), toWallet.getAllAddress(), toWallet.getType());
            AssertBean fromAssertBean = getExAssert(fromWallet);
            if (fromAssertBean == null) {
                showToast(String.format(getString(R.string.chain_bridge_no_support), mAssetCoin));
                return;
            }

            
            String amount = mVBinding.mapNumEdit.getText().toString().trim();
            if (TextUtils.isEmpty(amount)) {
                showToast(R.string.chain_yingshe_count_hint);
                return;
            }
            int amountInt = new BigDecimal(amount).intValue();
            if (amountInt < 100 || new BigDecimal(amount).compareTo(new BigDecimal(amountInt)) != 0 || amountInt % 100 != 0) {
                showToast(R.string.chain_bridge_100_beishu);
                return;
            }
            
            if (TextUtils.isEmpty(mBalance)) {
                showToast(getString(R.string.req_balance_info));
                getBalance(true);
                return;
            }
            if (!TextUtils.isEmpty(mBalance) && new BigDecimal(amount).compareTo(new BigDecimal(mBalance)) > 0) {
                showToast(R.string.insufficient_balance);
                return;
            }

            
            getViewModel().chainMap(fromWallet, toWallet, amount, fromAssertBean, getExAssert(toWallet));
        });

    }

    @Override
    public void initData() {
        
        getViewModel().observe(getViewModel().mBalanceLD, remain -> {
            mBalance = remain;
            mVBinding.balanceTv.setText(mBalance);
        });

        
        getViewModel().observe(getViewModel().mSuccessLD, result -> {
            String amount = mVBinding.mapNumEdit.getText().toString().trim();
            startActivity(ChainBridgeConfirmActivity.getIntent(ChainBridgeActivity2.this, fromWallet, toWallet, amount, mAssetCoin));
        });

        getViewModel().observe(getViewModel().mErrorLD, errorInfo -> {
            showAlertDialog(errorInfo, ok -> {
                dismissDialog();
            }, cancel -> {
                dismissDialog();
            });
            mAlertDialog.setNoBtnGone();
        });


        
        getViewModel().observe(getViewModel().mErrorTipLD, error -> {
            if (null != error && !TextUtils.isEmpty(error.id) && !TextUtils.isEmpty(error.name)) {
                String errorCode = error.id;
                showAlertDialog(error.name, ok -> {
                    dismissDialog();
                    if ("1".equals(errorCode)) {
                        
                        startActivity(new Intent(ChainBridgeActivity2.this, SettingNodeActivity.class));
                    } else if ("2".equals(errorCode)) {
                        
                        getViewModel().timerStartService();
                    } else if ("3".equals(errorCode)) {
                        
                        startActivity(new Intent(ChainBridgeActivity2.this, ChainBridgeOrdersActivity.class));
                    } else if ("4".equals(errorCode)) {
                        
                    }
                }, cancel -> {
                    dismissDialog();
                });

                if ("4".equals(errorCode) && null != mAlertDialog) {
                    mAlertDialog.setYesText(getString(R.string.continue_exchange));
                }
            }
        });

        initMap();
    }


    @Override
    protected void onResume() {
        super.onResume();
        
        getViewModel().checkServiceStatus();
    }

    
    private void showSelectFromWalletDialog() {
        int extType = -1;
        int nowType = fromWallet != null ? fromWallet.getType() : -1;
        ChooseWalletDialog.showExtDialog(ChainBridgeActivity2.this, extType, nowType, (address, walletType) -> {
            fromWallet = WalletDBUtil.getInstent(ChainBridgeActivity2.this).getWalletInfoByAddress(address, walletType);
            initMap();
        });
    }

    
    private void showSelectToWalletDialog() {
        int extType = -1; 
        int nowType = toWallet != null ? toWallet.getType() : -1;
        ChooseWalletDialog.showExtDialog(ChainBridgeActivity2.this, extType, nowType, (address, walletType) -> {
            WalletEntity selecteToWallet = WalletDBUtil.getInstent(ChainBridgeActivity2.this).getWalletInfoByAddress(address, walletType);
            if (null == selecteToWallet) {
                showToast(R.string.no_found_wallet_error);
            }
            if (fromWallet != null && fromWallet.getType() == selecteToWallet.getType()) {
                showToast(R.string.chain_bridge_please_diff);
                return;
            }
            toWallet = selecteToWallet;
            initMap();
        });
    }


    
    private void initMap() {

        
        AssertBean from = getShowAssert(fromWallet);
        if (null == from) {
            mVBinding.fromCoinImg.setImageResource(R.drawable.coin_default);
            mVBinding.fromCoinNameTv.setText(R.string.select_wallet);
        } else {
            mVBinding.fromCoinImg.setImageResource(from.getLogo());
            mVBinding.fromCoinNameTv.setText(from.getShort_name().toUpperCase() + " chain");
        }
        
        if (fromWallet != null && toWallet != null && fromWallet.getType() == toWallet.getType()) {
            
            toWallet = null;
        }
        AssertBean to = getShowAssert(toWallet);
        if (null == to) {
            mVBinding.toCoinImg.setImageResource(R.drawable.coin_default);
            mVBinding.toCoinNameTv.setText(R.string.select_wallet);
        } else {
            mVBinding.toCoinImg.setImageResource(to.getLogo());
            mVBinding.toCoinNameTv.setText(to.getShort_name().toUpperCase() + " chain");
        }

        if (toWallet != null) {
            mVBinding.mapAddressEdit.setText(toWallet.getAllAddress());
            mVBinding.walletNameTv.setText("(" + toWallet.getName() + ")");
        }


        
        mVBinding.balanceTv.setText("- -");
        mBalance = null;
        getBalance(false);

        
        mVBinding.bianXianTv.setText(mAssetCoin + getString(R.string.chain_bridge_bianxian));
        mVBinding.bianXianTv.setOnClickListener(view -> {
            startActivity(new Intent(this, USDTRealizeActivity.class));
        });

    }


    
    private AssertBean getShowAssert(WalletEntity wallet) {
        if (null == wallet) {
            return null;
        }
        List<AssertBean> mustAsset = WalletDBUtil.getInstent(this).getMustWallet(wallet.getType());
        if (null != mustAsset && mustAsset.size() > 0) {
            return mustAsset.get(0);
        }
        return null;
    }

    
    private void getBalance(boolean showLoading) {
        if (null != fromWallet) {
            AssertBean assertBean = getExAssert(fromWallet);
            if (null != assertBean) {
                getViewModel().getBalance(fromWallet, assertBean, showLoading);
            }
        }
    }

    
    private AssertBean getExAssert(WalletEntity wallet) {
        AssertBean assertBean = null;
        if (null != wallet) {
            if (ASSET_USDT.equals(mAssetCoin)) {
                assertBean = WalletUtil.getUsdtAssert(wallet.getType());
            }
        }
        return assertBean;
    }

    
    public class AssetListAdpater extends BaseAdapter {
        List<NameIdBean> mDatas;
        private Context mContext;

        public AssetListAdpater(Context context) {
            this.mContext = context;
            mDatas = new ArrayList<>();
            mDatas.add(new NameIdBean(ASSET_USDT, ASSET_USDT, R.mipmap.usdt_logo));
        }

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public NameIdBean getItem(int i) {
            return mDatas.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ItemChainMapIvNameBinding viewBinding;
            if (view == null) {
                viewBinding = ItemChainMapIvNameBinding.inflate(LayoutInflater.from(mContext), viewGroup, false);
                view = viewBinding.getRoot();
                view.setTag(viewBinding);
            } else {
                viewBinding = (ItemChainMapIvNameBinding) view.getTag();
            }
            viewBinding.img.setImageResource(mDatas.get(i).logoRes);
            viewBinding.name.setText(mDatas.get(i).name);
            return view;
        }
    }
}
