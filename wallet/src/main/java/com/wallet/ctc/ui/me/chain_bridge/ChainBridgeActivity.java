

package com.wallet.ctc.ui.me.chain_bridge;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.R;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.databinding.ActivityChainBridgeBinding;
import com.wallet.ctc.databinding.ItemChainMapIvNameBinding;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.ChainMapConfigBean;
import com.wallet.ctc.model.blockchain.EvmosSeqGasBean;
import com.wallet.ctc.ui.blockchain.addressbook.AddressBookActivity;
import com.wallet.ctc.util.GlideUtil;
import com.wallet.ctc.view.dialog.TransConfirmDialogBuilder;
import com.wallet.ctc.view.dialog.choosewallet.ChooseWalletDialog;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import common.app.base.BaseActivity;
import common.app.base.listener.DefaultTextWatcher;
import common.app.my.view.MyAlertDialog;
import common.app.pojo.NameIdBean;
import common.app.ui.view.TitleBarView;


public class ChainBridgeActivity extends BaseActivity<ChainBridgeActivityVM> {
    private ActivityChainBridgeBinding mVBinding;

    private static final int SELECT_ADDR_REQUEST_CODE = 886;
    public static final String CHAIN_TO_BSC = "BSC";
    public static final String CHAIN_TO_MCC = "DST";
    private String mType = CHAIN_TO_MCC;

    private List<Map<String, AssertBean>> mMapCoinLists;
    private Map<String,AssertBean> mNowMapCoin;
    private WalletEntity mSelecteWallet;
    private String mBalance;
    private CoinListAdpater mCoinListAdpater;
    private ChainListAdpater mChainListAdpater;
    private ChainMapConfigBean mMapConfig;

    @Override
    public View initBindingView(Bundle savedInstanceState) {
        mVBinding = ActivityChainBridgeBinding.inflate(getLayoutInflater());
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
            }
        });

        
        mVBinding.mapNumEdit.addTextChangedListener(new DefaultTextWatcher(){
            @Override
            public void afterTextChanged(Editable editable) {
                calculateMapNum();
            }
        });

        
        

        
        mVBinding.fromAddrTv.setOnClickListener(view1 -> {
            if (null == mSelecteWallet) {
                return;
            }
            
            ChooseWalletDialog.showDialog(ChainBridgeActivity.this, mSelecteWallet.getType(), (address, walletType) -> {
                mSelecteWallet = WalletDBUtil.getInstent(ChainBridgeActivity.this).getWalletInfoByAddress(address, walletType);
                initTypeShow();
            });
        });

        
        mVBinding.selectAddressImg.setOnClickListener(view1 -> {
            startActivityForResult(AddressBookActivity.getAddrIntent(ChainBridgeActivity.this),  SELECT_ADDR_REQUEST_CODE);
        });

        
        mCoinListAdpater = new CoinListAdpater(this);
        mVBinding.fromCoinSpinner.setAdapter(mCoinListAdpater);
        mVBinding.fromCoinSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (mMapCoinLists != null && mMapCoinLists.size() > 0 && i < mMapCoinLists.size()) {
                    mNowMapCoin = mMapCoinLists.get(i);
                    initTypeShow();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        mChainListAdpater = new ChainListAdpater(this);
        mVBinding.fromChainSpinner.setAdapter(mChainListAdpater);
        mVBinding.fromChainSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mType = mChainListAdpater.getItem(i).id;
                initTypeShow();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        
        mVBinding.confirmBtn.setOnClickListener(view1 -> {
            doChainMap(true, "", false);
        });

        
        initTypeShow();
    }

    @Override
    public void initData() {

        
        getViewModel().observe(getViewModel().mMapAssetsLD, mapList->{
            mMapCoinLists = mapList;
            if (mMapCoinLists != null && mMapCoinLists.size() > 0) {
                mNowMapCoin = mMapCoinLists.get(0);
                mCoinListAdpater.bindDatas(mMapCoinLists);
            }
            initTypeShow();
        });

        
        getViewModel().observe(getViewModel().mMapConfigLD, config->{
            mMapConfig = config;
            
            if (null != mMapConfig) {
                mVBinding.mapAlertTv.setText(String.format(getString(R.string.map_gas_tip), mMapConfig.getGasPercentStr(), mMapConfig.getMinNum()));
                calculateMapNum();
            }
        });

        
        getViewModel().observe(getViewModel().mBalanceLD, remain->{
            mBalance = remain;
            mVBinding.balanceTv.setText(mBalance);
        });


        
        getViewModel().observe(getViewModel().mResultLD, evmosPledgeResultBean -> {
            if (evmosPledgeResultBean.success) {
                showToast(R.string.operate_success_and_wait);
                finish();
            } else {
                showToast(evmosPledgeResultBean.info);
            }
        });

        
        getViewModel().observe(getViewModel().mShowApproveDialogLD, evmosSeqGasBean -> {
            
            MyAlertDialog dialog = new MyAlertDialog(ChainBridgeActivity.this, getString(R.string.first_pay_approve_tip));
            dialog.setonclick(new MyAlertDialog.Onclick() {
                @Override
                public void Yes() {
                    dialog.dismiss();
                    showGasAndTranscation(evmosSeqGasBean, getString(R.string.to_approve_do), true);
                }
                @Override
                public void No() {
                    dialog.dismiss();
                }
            });
            dialog.setNoText(getString(R.string.cancel));
            dialog.setYesText(getString(R.string.ex_shouquan));
            dialog.show();
        });

        
        getViewModel().observe(getViewModel().mApproveResultLD, evmosPledgeResultBean -> {
            if (evmosPledgeResultBean.success) {
                showToast(R.string.approve_success_continue);
            } else {
                showToast(evmosPledgeResultBean.info);
            }
        });

        
        getViewModel().observe(getViewModel().mShowGasDialogLD, evmosSeqGasBean -> {
            showGasAndTranscation(evmosSeqGasBean, getString(R.string.confirm_yingshe), false);
        });

        
        getViewModel().getAssets();

        
        getViewModel().getMapConfig();
    }

    
    private void calculateMapNum() {
        String inputNum = mVBinding.mapNumEdit.getText().toString().trim();
        if (null == mMapConfig || TextUtils.isEmpty(mMapConfig.getGasPercent())) {
            mVBinding.receiveNumTv.setText("- -");
            return;
        }
        if (!TextUtils.isEmpty(inputNum)) {
            try {
                BigDecimal num = new BigDecimal(inputNum);
                BigDecimal gasPercent = new BigDecimal(mMapConfig.getGasPercent()).divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
                BigDecimal gasNum = num.multiply(gasPercent);
                String receiveNum = num.subtract(gasNum).toPlainString();
                if (mType == CHAIN_TO_BSC) {
                    mVBinding.receiveNumTv.setText(receiveNum);
                } else {
                    mVBinding.receiveNumTv.setText(num.toPlainString());
                }
            } catch (NumberFormatException e){
                e.printStackTrace();
            }
        } else {
            mVBinding.receiveNumTv.setText("0.00");
        }
    }

    
    private void showGasAndTranscation(EvmosSeqGasBean evmosSeqGasBean, String desc, boolean isApprove) {
        if (null == evmosSeqGasBean) {
            showToast("get gas fail");
            return;
        }
        TransConfirmDialogBuilder.builder(this, mSelecteWallet).amount(mVBinding.mapNumEdit.getText().toString())
                
                .fromAddress(mSelecteWallet.getAllAddress())
                
                .toAddress(mVBinding.mapAddressEdit.getText().toString())
                .type(mSelecteWallet.getType())
                .orderDesc(desc)
                
                .gasFeeWithToken(evmosSeqGasBean.getShowFee())
                .gasCount(evmosSeqGasBean.gasCount+"")
                .gasPrice(evmosSeqGasBean.gasPrice)
                
                .goTransferListener(pwd -> {
                    
                    if (isApprove) {
                        
                        doChainMap(false, pwd, true);
                    } else {
                        
                        doChainMap(false, pwd, false);
                    }

                }).show();

    }

    
    private void doChainMap(boolean showGasAlert, String pwd, boolean isApprove) {
        String amount = mVBinding.mapNumEdit.getText().toString().trim();
        String toAddr = mVBinding.mapAddressEdit.getText().toString().trim();
        if (TextUtils.isEmpty(amount)) {
            showToast(R.string.chain_yingshe_count_hint);
            return;
        }
        if (TextUtils.isEmpty(toAddr)) {
            showToast(R.string.chian_yingshe_address_hint);
            return;
        }

        
        if (null == mSelecteWallet) {
            showToast(R.string.no_found_wallet_error);
            return;
        }

        
        if (new BigDecimal(amount).compareTo(new BigDecimal(0)) <= 0) {
            showToast(R.string.transfer_num_must_above0);
            return;
        }

        
        if (CHAIN_TO_BSC.equals(mType) && null == mMapConfig) {
            showToast(R.string.req_config_info_please);
            getViewModel().getMapConfig();
            return;
        }

        
        if (CHAIN_TO_BSC.equals(mType)) {
            if (new BigDecimal(amount).compareTo(new BigDecimal(mMapConfig.getMinNum())) < 0) {
                showToast(getString(R.string.min_map_num_hint)+mMapConfig.getMinNum());
                return;
            }

            if (!toAddr.startsWith("0x") || toAddr.length() < 30) {
                
                showToast(R.string.input_address_error);
                return;
            }
        } else if (CHAIN_TO_MCC.equals(mType)) {
            if (!toAddr.startsWith(BuildConfig.ENABLE_MCC_ADDRESS) || toAddr.length() < 30) {
                
                showToast(R.string.input_address_error);
                return;
            }
        }

        AssertBean from = null, to = null;
        AssertBean[] fromToArray = getFromTo(mNowMapCoin);
        if (null != fromToArray && fromToArray.length == 2) {
            from = fromToArray[0];
            to = fromToArray[1];
        }
        if (null == from || null == to) {
            showToast(R.string.pleas_selecte_chain_coin);
            return;
        }

        
        if (TextUtils.isEmpty(mBalance)) {
            showToast(getString(R.string.req_balance_info));
            refreshBalanceInfo(from, true);
            return;
        }

        if (!TextUtils.isEmpty(mBalance) && new BigDecimal(amount).compareTo(new BigDecimal(mBalance)) > 0) {
            showToast(R.string.insufficient_balance);
            return;
        }


        String fromAddr = mSelecteWallet.getAllAddress();
        int decimal = from.getDecimal();
        String bigAmount = new BigDecimal(amount).multiply(new BigDecimal(Math.pow(10, decimal))).stripTrailingZeros().toPlainString();
        String coinSymbol = from.getShort_name();
        if (showGasAlert) {
            getViewModel().showGasAlert(fromAddr, toAddr, bigAmount, from, mType);
        } else {
            if (TextUtils.isEmpty(pwd)) {
                showToast(getString(R.string.pwd_error));
                return;
            }
            getViewModel().chainMap(fromAddr, toAddr, bigAmount, from, mType, mSelecteWallet, pwd, isApprove);
        }

    }


    
    private void initTypeShow() {
        
        mSelecteWallet = WalletDBUtil.getInstent(this).getWalletInfo(getWalletTypeByDirect());
        if (null == mSelecteWallet || TextUtils.isEmpty(mSelecteWallet.getAllAddress())) {
            showToast(R.string.no_found_wallet_error);
            finish();
            return;
        }
        mVBinding.fromAddrTv.setText(mSelecteWallet.getAllAddress());


        
        AssertBean from = null, to = null;


        AssertBean[] fromToArray = getFromTo(mNowMapCoin);
        if (fromToArray != null && fromToArray.length == 2) {
            from = fromToArray[0];
            to = fromToArray[1];
        }

        
        NameIdBean fromChain = null, toChain = null;
        NameIdBean[] chainFromToArray = getChainFromTo(mType);
        if (chainFromToArray != null && chainFromToArray.length == 2) {
            fromChain = chainFromToArray[0];
            toChain = chainFromToArray[1];
        }

        if (from != null) {
            refreshBalanceInfo(from, false);
        }

        if (to != null) {
            showCoinLogo(to, mVBinding.toCoinImg);
            mVBinding.toCoinNameTv.setText(to.getShort_name().toUpperCase());
        }

        if (toChain != null) {
            mVBinding.toChainTv.setText(toChain.name);
            mVBinding.toChainImg.setImageResource(toChain.logoRes);
        }

        if (CHAIN_TO_MCC.equals(mType)) {
            
            mVBinding.mapAlertTv.setVisibility(View.GONE);
        } else if(CHAIN_TO_BSC.equals(mType)){
            
            mVBinding.mapAlertTv.setVisibility(View.VISIBLE);
        }
    }


    
    private AssertBean[] getFromTo(Map<String,AssertBean> map) {
        if (null == map || map.isEmpty()) {
            return null;
        }
        AssertBean from = null, to =null;
        if (CHAIN_TO_BSC.equals(mType)) {
            from = map.get(CHAIN_TO_MCC);
            to = map.get(CHAIN_TO_BSC);
        } else if (CHAIN_TO_MCC.equals(mType)) {
            from = map.get(CHAIN_TO_BSC);
            to = map.get(CHAIN_TO_MCC);
        }
        return new AssertBean[]{from, to};
    }

    
    private NameIdBean[] getChainFromTo(String type) {
        String CHAIN_TITLE = getString(R.string.main_chain_title);
        String mccChainName = getString(R.string.default_token_name2).toUpperCase()+CHAIN_TITLE;
        NameIdBean mccChain = new NameIdBean(CHAIN_TO_BSC, mccChainName, R.mipmap.tt_logo);

        String bscChainName = "BSC"+CHAIN_TITLE;
        NameIdBean bscChain = new NameIdBean(CHAIN_TO_MCC, bscChainName, R.mipmap.bnb_logo);

        NameIdBean from, to;
        if (CHAIN_TO_BSC.equals(type)) {
            
            return new NameIdBean[]{mccChain, bscChain};
        } else if (CHAIN_TO_MCC.equals(type)) {
            
            return new NameIdBean[]{bscChain, mccChain};
        }
        return null;
    }



    
    private void refreshBalanceInfo(AssertBean from, boolean showLoading) {
        
        mVBinding.balanceTv.setText("- -");
        mBalance = null;
        getViewModel().getBalance(mSelecteWallet, from, showLoading);
    }

    
    private void showCoinLogo(AssertBean info, ImageView imageView) {
        if (null == info || imageView == null) {
            return;
        }
        if (info.getLogo() > 0) {
            GlideUtil.showImg(this, info.getLogo(), imageView);
        } else {
            GlideUtil.showImg(this, info.getImg_path(), imageView);
        }
    }

    
    private int getWalletTypeByDirect() {
        if (CHAIN_TO_BSC.equals(mType)) {
            return WalletUtil.MCC_COIN;
        } else if(CHAIN_TO_MCC.equals(mType)){
            return WalletUtil.BNB_COIN;
        }
        return -1;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_ADDR_REQUEST_CODE && resultCode == Activity.RESULT_OK && null != data) {
            String toAddress = data.getStringExtra(AddressBookActivity.KEY_DATA);
            if (!TextUtils.isEmpty(toAddress)) {
                mVBinding.mapAddressEdit.setText(toAddress);
            }
        }
    }

    
    public class CoinListAdpater extends BaseAdapter {
        private List<Map<String,AssertBean>> mDatas;
        private Context mContext;
        public CoinListAdpater(Context context) {
            mDatas = new ArrayList<>();
            this.mContext = context;
        }

        public void bindDatas(List<Map<String,AssertBean>> datas) {
            if (null != datas) {
                mDatas.clear();
                mDatas.addAll(datas);
                notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public Object getItem(int i) {
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
            AssertBean from = new AssertBean();
            AssertBean[] fromTo = getFromTo(mDatas.get(i));
            if (null != fromTo && fromTo.length > 0) {
                from = fromTo[0];
            }
            showCoinLogo(from, viewBinding.img);
            viewBinding.name.setText(from.getShort_name().toUpperCase());
            return view;
        }
    }

    
    public class ChainListAdpater extends BaseAdapter {
        List<NameIdBean> mDatas;
        private Context mContext;
        public ChainListAdpater(Context context) {
            this.mContext = context;
            mDatas = new ArrayList<>();
            mDatas.add(getChainFromTo(CHAIN_TO_MCC)[0]);
            mDatas.add(getChainFromTo(CHAIN_TO_BSC)[0]);
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
