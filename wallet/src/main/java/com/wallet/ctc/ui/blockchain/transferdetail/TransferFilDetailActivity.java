

package com.wallet.ctc.ui.blockchain.transferdetail;

import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.FilTransRecordBean;
import com.wallet.ctc.ui.me.about.StringUtils;
import com.wallet.ctc.util.AllUtils;

import java.math.BigDecimal;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.app.base.them.Eyes;
import common.app.mall.util.ToastUtil;



public class TransferFilDetailActivity extends BaseActivity {

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
    @BindView(R2.id.line1_url)
    LinearLayout line1Url;
    @BindView(R2.id.error)
    TextView error;

    private FilTransRecordBean.DocsBean mBean;

    public static final String KEY_WALLET_TYPE = "walletType";
    private int walletType;

    @Override
    public int initContentView() {
        Eyes.setTranslucent(this);
        return R.layout.activity_transfereth_detail;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        findViewById(R.id.title_bar).setPadding(0, Eyes.getStatusBarHeight(this), 0, 0);

        walletType = getIntent().getIntExtra(KEY_WALLET_TYPE, -1);
        if (walletType == -1) {
            WalletEntity walletEntity = walletDBUtil.getWalletInfo();
            walletType = walletEntity.getType();
        }
        List<AssertBean> list = WalletDBUtil.getInstent(this).getMustWallet(walletType);
        String mustCoin = list.get(0).getShort_name();
        int decimal = list.get(0).getDecimal();
        mBean = new Gson().fromJson(getIntent().getStringExtra("detail"), new TypeToken<FilTransRecordBean.DocsBean>() {
        }.getType());
        
        if (mBean == null) {
            finish();
            return;
        }

        fakuan.setText(mBean.getFrom());
        if ("token_transfer".equals(mBean.getType()) && null != mBean.getMetadata() && !TextUtils.isEmpty(mBean.getMetadata().getTo())) {
            
            shoukuan.setText(mBean.getMetadata().getTo());
        } else {
            shoukuan.setText(mBean.getTo());
        }
        if (StringUtils.isEmpty(mBean.getFee())) {
            mBean.setFee("0");
        }

        


        int coin_decimal = mBean.getMetadata().getDecimals();
        if (coin_decimal == 0) {
            
            coin_decimal = decimal;
        }

        BigDecimal amount = new BigDecimal(mBean.getMetadata().getValue()).divide(new BigDecimal(Math.pow(10, coin_decimal)), 6, BigDecimal.ROUND_HALF_UP);
        String direction = "";
        if ("outgoing".equals(mBean.getDirection())) {
            direction = "-";
        } else if ("incoming".equals(mBean.getDirection())) {
            direction = "+";
        }

        
        minerCosts.setText(getFeeStr(decimal, mustCoin));
        remarks.setText(mBean.getMemo());
        ethfeiyong.setText(direction + amount.stripTrailingZeros().toPlainString());
        String symbol = mBean.getMetadata().getSymbol();
        if (!TextUtils.isEmpty(symbol)) {
            symbol = symbol.toUpperCase();
        }
        asseteName.setText(symbol);
        jiaoyihao.setText(mBean.getId());
        qukuai.setText(mBean.getBlock() + "");
        line1Url.setVisibility(View.GONE);
        jiaoyishijian.setText(AllUtils.times(mBean.getDate() + ""));
        if ("completed".equals(mBean.getStatus())) {
            imgHeadLogo.setImageResource(R.mipmap.xiangqing_duihao);
            error.setText(getString(R.string.transfer_success));
        } else {
            imgHeadLogo.setImageResource(R.mipmap.jinxingzhong);
            error.setText(R.string.is_loading);
        }
    }

    
    public String getFeeStr(int mainCoinDecimal, String mustCoinName) {
        if (!TextUtils.isEmpty(mustCoinName)) {
            mustCoinName = mustCoinName.toUpperCase();
        }
        String feeStr = mBean.getFee();
        if (TextUtils.isEmpty(feeStr) || "0".equals(feeStr)) {
            List<FilTransRecordBean.DocsBean.EvmosTokenFee> fees = mBean.getFees();
            if (fees != null && fees.size() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                int size = fees.size();
                for (int i = 0; i < fees.size(); i++) {
                    String coinname = fees.get(i).coinname;
                    if (!TextUtils.isEmpty(coinname)) {
                        coinname = coinname.toUpperCase();
                    }
                    if (i < size) {
                        stringBuilder.append(getTenValueFee(fees.get(i).value, mainCoinDecimal) + " " +coinname + "\n");
                    } else {
                        stringBuilder.append(getTenValueFee(fees.get(i).value, mainCoinDecimal) + " " +coinname);
                    }
                }
                return stringBuilder.toString();
            } else {
                return "0 " + mustCoinName;
            }

        } else {
            return getTenValueFee(feeStr, mainCoinDecimal) + " " + mustCoinName;
        }
    }

    public String getTenValueFee(String feeBigNum, int decimal) {
        if (TextUtils.isEmpty(feeBigNum)) {
            return feeBigNum;
        }
        String fee = new BigDecimal(feeBigNum).divide(new BigDecimal(Math.pow(10, decimal)), 6, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString();
        return fee;
    }


    @Override
    public void initData() {

    }

    ClipboardManager cm;

    @OnClick({R2.id.img_back, R2.id.copyurl, R2.id.fakuan, R2.id.shoukuan})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.img_back) {
            finish();

        } else if (i == R.id.copyurl) {

        } else if (i == R.id.fakuan) {
            cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            
            cm.setText(mBean.getFrom());
            ToastUtil.showToast(getString(R.string.copy_success));

        } else if (i == R.id.shoukuan) {
            cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            
            cm.setText(shoukuan.getText().toString().trim());
            ToastUtil.showToast(getString(R.string.copy_success));

        } else {
        }
    }
}
