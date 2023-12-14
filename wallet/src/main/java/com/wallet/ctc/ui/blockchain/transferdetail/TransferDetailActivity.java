

package com.wallet.ctc.ui.blockchain.transferdetail;

import static common.app.utils.LanguageUtil.TYPE_LAGUAGE_ENGLISH;

import android.content.ClipboardManager;
import android.content.Context;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.EvmosTransTypesBean;
import com.wallet.ctc.model.blockchain.RpcApi;
import com.wallet.ctc.model.blockchain.TransactionRecordBean;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.app.base.share.qr.QrCodeUtils;
import common.app.base.them.Eyes;
import common.app.im.base.NextSubscriber;
import common.app.mall.util.ToastUtil;
import common.app.utils.LanguageUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;



public class TransferDetailActivity extends BaseActivity {

    @BindView(R2.id.img_head_logo)
    ImageView imgHeadLogo;
    @BindView(R2.id.fakuan)
    TextView fakuan;
    @BindView(R2.id.shoukuan)
    TextView shoukuan;
    @BindView(R2.id.miner_costs)
    TextView minerCosts;
    @BindView(R2.id.remarks)
    TextView remarks;
    @BindView(R2.id.jiaoyihao)
    TextView jiaoyihao;
    @BindView(R2.id.qukuai)
    TextView qukuai;
    @BindView(R2.id.jiaoyishijian)
    TextView jiaoyishijian;
    @BindView(R2.id.eth_feiyong)
    TextView ethfeiyong;

    @BindView(R2.id.imgqrcode)
    ImageView imgqrcode;
    @BindView(R2.id.copyurl)
    TextView copyurl;
    @BindView(R2.id.assete_name)
    TextView asseteName;
    @BindView(R2.id.error)
    TextView error;
    @BindView(R2.id.jiansuohao)
    TextView jiansuohao;
    @BindView(R2.id.tradeTypeTv)
    TextView tradeTypeTv;

    private TransactionRecordBean mBean;
    private int type;
    private int decimal;


    
    public int getDecimal() {
        if (decimal > 0) {
            return decimal;
        }
        List<AssertBean> assets = WalletDBUtil.getInstent(this).getMustWallet(type);
        decimal = assets.get(0).getDecimal();
        if (decimal == 0) {
            
            decimal = 18;
        }
        return decimal;
    }

    
    public String getTransferAmount(String amount) {
        if (TextUtils.isEmpty(amount)) {
            return amount;
        }
        int decimal = getDecimal();
        try {
            String transferAmount = new BigDecimal(amount).divide(new BigDecimal(Math.pow(10, decimal)), 6, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
            return transferAmount;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return amount;
    }

    @Override
    public int initContentView() {
        Eyes.setTranslucent(this);
        type = getIntent().getIntExtra("type", type);
        mBean = (TransactionRecordBean) getIntent().getSerializableExtra("detail");
        if (mBean == null) {
            finish();
        }
        return R.layout.activity_transfer_detail;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        findViewById(R.id.title_bar).setPadding(0, Eyes.getStatusBarHeight(this), 0, 0);
    }

    @Override
    public void initData() {
        showData(mBean);
    }

    private void showData(TransactionRecordBean data) {
        mBean = data;
        if (null == mBean) {
            return;
        }
        fakuan.setText(mBean.getFromAllAddress());
        shoukuan.setText(mBean.getToAllAddress());

        String amount = getTransferAmount(mBean.getBigIntFeeAmount());
        String showTips = "";
        String costCoinName = getString(R.string.default_token_name2);
        if (!TextUtils.isEmpty(costCoinName)) {
            if (!amount.contains(costCoinName)) {
                showTips = amount + " " + BuildConfig.EVMOS_FAKE_UNINT;
            } else {
                showTips = amount;
            }
        }


        
        minerCosts.setText(showTips);

        remarks.setText(mBean.getRemarks());
        
        
        String amountStr = getTransferAmount(mBean.getBigIntTransferAmount());
        String suffix = mBean.getCoin_name().toUpperCase();
        String str = "";
        if (!TextUtils.isEmpty(amountStr) && amountStr.contains(suffix)) {
            suffix = "";
        }
        if (isSendTransfer(mBean.getFromAllAddress())) {
            str = "-" + amountStr + " " + suffix;
        } else {
            str = "+" + amountStr + " " + suffix;
        }
        SpannableString spannableString = new SpannableString(str);
        spannableString.setSpan(new AbsoluteSizeSpan(45), amountStr.length() + 1, str.length(), 0);
        ethfeiyong.setText(spannableString);

        jiaoyihao.setText(mBean.getAllTransaction_no());
        qukuai.setText(mBean.getBlock());
        jiaoyishijian.setText(mBean.getTransferTime());
        imgqrcode.setImageBitmap(QrCodeUtils.getQrCode(mBean.getAllTransaction_no()));
        if (mBean.getStatus() == 1 && null != mBean.getAuthority_sign() && mBean.getAuthority_sign().length() > 1) {
            jiansuohao.setText(getString(R.string.jiansuohao) + mBean.getAuthority_sign());
            jiansuohao.setVisibility(View.VISIBLE);
        }
        if (mBean.getStatus() == 1 || mBean.getStatus() == 4) {
            error.setVisibility(View.VISIBLE);
            error.setText(getString(R.string.transfer_success));
        }
        if (mBean.getStatus() == 2) {
            error.setVisibility(View.VISIBLE);
            imgHeadLogo.setImageResource(R.mipmap.xiangqing_chahao);
            error.setText(getString(R.string.transfer_error) + mBean.getError());
        }
        if (mBean.getStatus() == 0 || mBean.getStatus() == 3) {
            error.setVisibility(View.VISIBLE);
            imgHeadLogo.setImageResource(R.mipmap.lalla);
            error.setText(R.string.is_loading);
        }

        getTransTypes(mBean.trade_type);
    }


    ClipboardManager cm;

    @OnClick({R2.id.img_back, R2.id.copyurl, R2.id.jiaoyihao, R2.id.fakuan, R2.id.shoukuan})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.img_back) {
            finish();

        } else if (i == R.id.copyurl) {
            cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            
            cm.setText(mBean.getUrl());
            ToastUtil.showToast(getString(R.string.copy_success));

        } else if (i == R.id.jiaoyihao) {
            cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            
            cm.setText(mBean.getAllTransaction_no());
            ToastUtil.showToast(getString(R.string.copy_success));

        } else if (i == R.id.fakuan) {
            cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            
            cm.setText(mBean.getFromAllAddress());
            ToastUtil.showToast(getString(R.string.copy_success));

        } else if (i == R.id.shoukuan) {
            cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            
            cm.setText(mBean.getToAllAddress());
            ToastUtil.showToast(getString(R.string.copy_success));

        } else {
        }
    }

    
    public boolean isSendTransfer(String fromAddress) {
        WalletDBUtil walletDBUtil = WalletDBUtil.getInstent(getApplicationContext());
        String myWalletAddress = walletDBUtil.getWalletInfo().getAllAddress2();
        if (!TextUtils.isEmpty(myWalletAddress) && myWalletAddress.equalsIgnoreCase(fromAddress)) {
            return true;
        } else {
            return false;
        }
    }

    public void getTransTypes(String tradeType) {
        if (TextUtils.isEmpty(tradeType)) {
            return;
        }
        String local = LanguageUtil.getNowLocalStr(this);
        String lng = "CHC";
        if (TYPE_LAGUAGE_ENGLISH.equals(local)) {
            
            lng = "EN";
        } else {
            
            lng = "CHC";
        }
        RpcApi rpcApi = new RpcApi();
        rpcApi.getEvmosTransfTypes(lng).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NextSubscriber<EvmosTransTypesBean>() {
                    @Override
                    public void dealData(EvmosTransTypesBean value) {
                        if (null != value && value.isSuccess()) {
                            String tradeTypeDesc = value.getTypeStr(tradeType);
                            tradeTypeTv.setText(tradeTypeDesc);
                        } else {
                            String error = value != null ? value.getInfo() : "get type data is null";
                            ToastUtil.showToast(error);
                        }
                    }

                    @Override
                    protected void dealError(Throwable e) {
                        tradeTypeTv.setText(tradeType);
                    }
                });
    }

}
