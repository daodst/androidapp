

package com.wallet.ctc.ui.blockchain.collectmoney;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.AssertBean;

import java.math.BigDecimal;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.app.base.share.qr.QrCodeUtils;
import common.app.mall.util.ToastUtil;



public class CollectMoneyActivity extends BaseActivity {

    @BindView(R2.id.tv_title)
    TextView title;
    @BindView(R2.id.tv_action)
    TextView tvAction;

    @BindView(R2.id.wallet_address)
    TextView walletAddress;
    @BindView(R2.id.payee_wallet_money)
    EditText payeeWalletMoney;
    @BindView(R2.id.qrcode)
    ImageView qrcode;
    private String type = "";
    private int coinType;
    private int decimal = 0;
    private WalletEntity mWallet;
    private String address;

    public static void startCollectMoneyActivity(Context mContext, int coinType, String tokenAddress, int decimal) {
        Intent intent = new Intent(mContext, CollectMoneyActivity.class);
        intent.putExtra("coinType", coinType);
        intent.putExtra("type", tokenAddress);
        intent.putExtra("decimal", decimal);
        mContext.startActivity(intent);
    }

    public static void startCollectMoneyActivity(Context mContext, int coinType, String tokenAddress, int decimal, String name) {
        Intent intent = new Intent(mContext, CollectMoneyActivity.class);
        intent.putExtra("coinType", coinType);
        intent.putExtra("type", tokenAddress);
        intent.putExtra("decimal", decimal);
        intent.putExtra("name", name);
        mContext.startActivity(intent);
    }

    @Override
    public int initContentView() {
        type = getIntent().getStringExtra("type");

        coinType = getIntent().getIntExtra("coinType", -1);
        decimal = getIntent().getIntExtra("decimal", 0);
        if (-1 == coinType) {
            mWallet = WalletDBUtil.getInstent(this).getWalletInfo();
        } else {
            mWallet = WalletDBUtil.getInstent(this).getWalletInfo(coinType);
        }
        if (null == mWallet) {
            ToastUtil.showToast(R.string.no_found_wallet_error);
            finish();
            return 0;
        }
        if (coinType == WalletUtil.MCC_COIN && !TextUtils.isEmpty(type) && type.length() > 15) {
            
            
            boolean isCoinName = false;
            List<AssertBean> list = walletDBUtil.getMustWallet(WalletUtil.MCC_COIN);
            if (list != null && list.size() > 0) {
                for (AssertBean bean : list) {
                    if (TextUtils.isEmpty(bean.getContract()) && type.equalsIgnoreCase(bean.getShort_name())) {
                        isCoinName = true;
                        break;
                    }
                }
            }
            if (!isCoinName) {
                
                address = mWallet.getDefaultAddress();
            } else {
                
                address = mWallet.getAllAddress();
            }

        } else {
            address = mWallet.getAllAddress();
        }
        return R.layout.activity_collectmoney;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        String name = getIntent().getStringExtra("name");
        if (!TextUtils.isEmpty(name)) {
            name = name.toUpperCase();
        } else {
            name = "";
        }
        title.setText(name+getString(R.string.shoukuan));
        tvAction.setVisibility(View.VISIBLE);
        tvAction.setText(getString(R.string.share));

        payeeWalletMoney.setText("");
        payeeWalletMoney.setVisibility(View.GONE);
        findViewById(R.id.payee_wallet_money_line).setVisibility(View.GONE);
        payeeWalletMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getQrCode();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void initData() {
        walletAddress.setText(address);
        getQrCode();
    }

    @OnClick({R2.id.tv_back, R2.id.tv_action, R2.id.copy_shoukuan})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.tv_back) {
            finish();

        } else if (i == R.id.tv_action) {
            Intent textIntent = new Intent(Intent.ACTION_SEND);
            textIntent.setType("text/plain");
            textIntent.putExtra(Intent.EXTRA_TEXT, address);
            startActivity(Intent.createChooser(textIntent, getString(R.string.share)));

        } else if (i == R.id.copy_shoukuan) {
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            
            cm.setText(address);
            ToastUtil.showToast(getString(R.string.copyed));
        } else {
        }
    }

    private void getQrCode() {
        String amount = payeeWalletMoney.getText().toString().trim();
        address = walletAddress.getText().toString();
        if (TextUtils.isEmpty(amount) || amount.equals(".")) {
            amount = "0";
        }
        if (amount.equals("0")) {
            Bitmap mQRBitmap = QrCodeUtils.createInstance(CollectMoneyActivity.this).getQrCode(address);
            qrcode.setImageBitmap(mQRBitmap);
        } else if (coinType == WalletUtil.BTC_COIN) {
            String qr = "bitcoin:" + address + "?amount=" + amount;
            Bitmap mQRBitmap = QrCodeUtils.createInstance(CollectMoneyActivity.this).getQrCode(qr);
            qrcode.setImageBitmap(mQRBitmap);
        } else if (coinType == WalletUtil.ETH_COIN || coinType == WalletUtil.DM_COIN || coinType == WalletUtil.MCC_COIN || coinType == WalletUtil.OTHER_COIN) {
            String qr = "";
            BigDecimal amountB;
            if (TextUtils.isEmpty(type)) {
                amountB = new BigDecimal(amount).multiply(new BigDecimal(Math.pow(10, 18)));
                qr = "ethereum:" + address + "?decimal=" + 18 + "&value=" + amountB.toPlainString();
            } else {
                amountB = new BigDecimal(amount).multiply(new BigDecimal(Math.pow(10, decimal)));
                qr = "ethereum:" + address + "?contractAddress=" + type + "&decimal=" + decimal + "&value=" + amountB.toPlainString();
            }
            Bitmap mQRBitmap = QrCodeUtils.createInstance(CollectMoneyActivity.this).getQrCode(qr);
            qrcode.setImageBitmap(mQRBitmap);
        } else {
            Bitmap mQRBitmap = QrCodeUtils.createInstance(CollectMoneyActivity.this).getQrCode(address);
            qrcode.setImageBitmap(mQRBitmap);
        }
    }

    


}
