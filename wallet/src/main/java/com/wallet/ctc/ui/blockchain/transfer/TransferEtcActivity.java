

package com.wallet.ctc.ui.blockchain.transfer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wallet.ctc.Constants;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.api.me.MeApi;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.base.BaseEntity;
import com.wallet.ctc.base.BaseWebViewActivity;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletTransctionUtil;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.TransferBean;
import com.wallet.ctc.ui.blockchain.addressbook.AddressBookActivity;
import com.wallet.ctc.util.LogUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.app.ActivityRouter;
import common.app.base.fragment.mall.catcherror.BaseSubscriber;
import common.app.mall.util.ToastUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;
import wallet.core.jni.AnyAddress;
import wallet.core.jni.CoinType;



public class TransferEtcActivity extends BaseActivity {
    @BindView(R2.id.img_back)
    ImageView imgBack;
    @BindView(R2.id.title)
    TextView title;
    @BindView(R2.id.imgqrcode)
    ImageView imgqrcode;
    @BindView(R2.id.payee_wallet_address)
    EditText payeeWalletAddress;
    @BindView(R2.id.transfer_amount)
    EditText transferAmount;
    @BindView(R2.id.remarks)
    EditText remarks;
    @BindView(R2.id.seek_bar)
    SeekBar seekBar;
    @BindView(R2.id.feiyong)
    TextView feiyong;
    @BindView(R2.id.submit)
    TextView submit;
    
    @BindView(R2.id.putong)
    LinearLayout putong;
    @BindView(R2.id.gasprice)
    EditText gaspriceEdit;
    @BindView(R2.id.gas)
    EditText gas;
    @BindView(R2.id.gaoji)
    LinearLayout gaoji;
    @BindView(R2.id.shecan)
    TextView shecan;

    @BindView(R2.id.fromaddress)
    TextView fromaddress;

    @BindView(R2.id.gaoji_open)
    CheckBox gaojiOpen;
    @BindView(R2.id.kuanggong_price)
    TextView kuanggongPrice;
    @BindView(R2.id.kuanggong_kuaiman)
    RelativeLayout kuanggongKuaiman;
    @BindView(R2.id.kuanggong_gaoji)
    RelativeLayout kuanggongGaoji;
    private Gson gson = new Gson();
    private MeApi mApi = new MeApi();
    private String amountStr;
    private String tokenType = "";
    private String tokenName = "";
    private String toAddress = "";
    private String feiyongStr;
    private String fromAddress = "";
    private String value = "0";
    private BigDecimal gasCount = new BigDecimal("0");
    private int decimal;
    private BigDecimal gasprice;
    private int min = 1;
    private int from = 0;
    private com.wallet.ctc.db.WalletEntity WalletEntity;
    private WalletTransctionUtil walletTransctionUtil;
    private int wallettype;
    private String mustCoin;
    private TransferBean data;

    @Override
    public int initContentView() {
        return R.layout.activity_etctransfer;
    }

    @SuppressLint("StringFormatInvalid")
    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        
        WalletEntity = walletDBUtil.getWalletInfo();
        wallettype = WalletEntity.getType();
        List<AssertBean> list = WalletDBUtil.getInstent(this).getMustWallet(wallettype);
        mustCoin = list.get(0).getShort_name();
        amountStr = getIntent().getStringExtra("amountStr");
        tokenName = getIntent().getStringExtra("tokenName");
        from = getIntent().getIntExtra("from", 0);
        if (tokenName == null || tokenName.length() < 1) {
            tokenName = mustCoin.toUpperCase();
        }
        tokenType = getIntent().getStringExtra("tokenType");
        toAddress = getIntent().getStringExtra("toAddress");
        decimal = getIntent().getIntExtra("decimal", 0);
        String gas = getIntent().getStringExtra("gasCount");
        fromAddress = WalletEntity.getAllAddress();
        LogUtil.d("gas" + gas);
        if (gas != null && gas.length() > 0) {
            gasCount = new BigDecimal(gas);
        } else {
            gasCount = new BigDecimal("25200");
        }
        
       
       
        
        gasprice = Constants.GAS_PRICE;
        getprice(gasprice);
        title.setText(tokenName + "  " + getString(R.string.transfer));
        payeeWalletAddress.setText(toAddress);
        payeeWalletAddress.setHint(String.format(getString(R.string.enter_lxr_address_error),tokenName));
        if (null != amountStr && !TextUtils.isEmpty(amountStr) && new BigDecimal(amountStr).doubleValue() > 0) {
            transferAmount.setText(amountStr);
        } else {
            transferAmount.setText("");
        }

        seekBar.setProgress(0);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                
                
                
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        gaojiOpen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    putong.setVisibility(View.GONE);
                    gaoji.setVisibility(View.VISIBLE);
                    shecan.setVisibility(View.VISIBLE);
                } else {
                    putong.setVisibility(View.VISIBLE);
                    gaoji.setVisibility(View.GONE);
                    shecan.setVisibility(View.GONE);
                }
            }
        });
        fromaddress.setText(fromAddress);
        walletTransctionUtil = new WalletTransctionUtil(this);
        walletTransctionUtil.setEtctransctionListen(new WalletTransctionUtil.EtcTransctionListen() {
            @Override
            public void showLoading() {
                mLoadingDialog.show();
            }

            @Override
            public void showGasCount(String gasCounts) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                gasCount = new BigDecimal(gasCounts);
                getprice(gasprice);
                if (!gaojiOpen.isChecked()) {
                    data.setKuanggong(feiyongStr);
                    data.setGascount(gasCount.intValue());
                    data.setGasprice(gasprice.toPlainString());
                }
                walletTransctionUtil.DoTransction(data, true);
            }

            @Override
            public void showDefGasprice(String defGasprice) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                gasprice = new BigDecimal(defGasprice);

            }

            @Override
            public void showTransctionSuccess(String hash) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                ToastUtil.showToast(getString(R.string.caozuo_success));
                finish();
            }

            @Override
            public void onFail(String msg) {
                if (mLoadingDialog != null) mLoadingDialog.dismiss();
                ToastUtil.showToast(msg);
            }
        });
        walletTransctionUtil.getEtcDefPrice(wallettype);
    }

    @Override
    public void initData() {
        if (tokenName.toLowerCase().equals(mustCoin.toLowerCase())) {
            value = WalletEntity.getmBalance();
        } else {
            AssertBean bean = walletDBUtil.getAssetsByAddress(WalletEntity.getAllAddress(), tokenType,WalletEntity.getType());
            if (null != bean) {
                value = bean.getAssertsNum();
            }
        }
        transferAmount.setHint(value);

    }

    @OnClick({R2.id.img_back, R2.id.submit, R2.id.imgqrcode, R2.id.shecan, R2.id.go_adddresslist})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.img_back) {
            finish();

        } else if (i == R.id.shecan) {
            Map<String, Object> params2 = new TreeMap();
            params2.put("type", "shezhicanshu");
            mLoadingDialog.show();
            mApi.getArticle(params2).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new BaseSubscriber<BaseEntity>(this) {
                        @Override
                        public void onNexts(BaseEntity baseEntity) {
                            mLoadingDialog.dismiss();
                            if (baseEntity.getStatus() == 1) {
                                Intent intent = new Intent(TransferEtcActivity.this, BaseWebViewActivity.class);
                                intent.putExtra("url", baseEntity.getData().toString());
                                intent.putExtra("title", getString(R.string.shezhicanshu));
                                startActivity(intent);
                            } else {
                                ToastUtil.showToast(baseEntity.getInfo());
                            }
                        }
                    });

        } else if (i == R.id.submit) {
            toAddress = payeeWalletAddress.getText().toString().trim();
            toAddress = toAddress.replace(" ", "");
            String rem = remarks.getText().toString().trim();
            amountStr = transferAmount.getText().toString().trim();
            
            if (TextUtils.isEmpty(toAddress) || !AnyAddress.isValid(toAddress, CoinType.ETHEREUMCLASSIC)) {
                ToastUtil.showToast(getString(R.string.payee_wallet_address_errpr));
                return;
            }

            if (TextUtils.isEmpty(amountStr)) {
                ToastUtil.showToast(getString(R.string.transfer_amount));
                return;
            }
            
            BigDecimal bigDecimal = new BigDecimal(amountStr).subtract(new BigDecimal(value));
            if (bigDecimal.doubleValue() > 0 || new BigDecimal(value).doubleValue() == 0) {
                ToastUtil.showToast(getString(R.string.no_money));
                return;
            }
            data = new TransferBean(toAddress, fromAddress, amountStr, feiyongStr.replace(mustCoin, ""), wallettype, tokenName, feiyongStr);
            data.setPayaddress(fromAddress);
            data.setPrice(amountStr);
            data.setTokenName(tokenName);
            data.setRemark(rem);
            try {
                if (toAddress.startsWith("0x")) {
                    BigInteger add = new BigInteger(toAddress.substring(2, toAddress.length()), 16);
                } else {
                    BigInteger add = new BigInteger(toAddress, 16);
                }
            } catch (Exception e) {
                LogUtil.d(toAddress + "\n" + e.toString());
                ToastUtil.showToast(getString(R.string.payee_wallet_address_errpr));
                return;
            }
            if (gaojiOpen.isChecked()) {
                String gass = gas.getText().toString().trim();
                String gasspr = gaspriceEdit.getText().toString().trim();
                if (TextUtils.isEmpty(gass)) {
                    ToastUtil.showToast(getString(R.string.gaoji_gas));
                    return;
                }
                if (TextUtils.isEmpty(gasspr)) {
                    ToastUtil.showToast(getString(R.string.gaoji_gasprice));
                    return;
                }
                data.setGascount(Integer.parseInt(gass));
                data.setGasprice(gasspr);
                BigDecimal sum = new BigDecimal(gass).multiply(new BigDecimal(gasspr));
                BigDecimal jinzhi = new BigDecimal(Math.pow(10, decimal));
                String kuanggong = sum.divide(jinzhi).toPlainString();
                data.setKuanggong(kuanggong);
            }

            data.setRuaddress(toAddress);
            data.setTokenType(tokenType);
            data.setDecimal(decimal);
            walletTransctionUtil.getEtcGasCount(data);

        } else if (i == R.id.imgqrcode) {
            Intent intent = ActivityRouter.getEmptyContentIntent(this,ActivityRouter.Common.F_QRCodeFragment);
            startActivityForResult(intent, 1000);
        } else if (i == R.id.go_adddresslist) {
            Intent intent2 = new Intent(this, AddressBookActivity.class);
            intent2.putExtra("type", wallettype);
            startActivityForResult(intent2, 2000);

        } else {
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent Data) {
        super.onActivityResult(requestCode, resultCode, Data);
        if (requestCode == 1000 && resultCode == RESULT_OK && Data != null) {
            amountStr = Data.getStringExtra("amountStr");
            toAddress = Data.getStringExtra("toAddress");
            payeeWalletAddress.setText(toAddress);
            if (null != amountStr && !TextUtils.isEmpty(amountStr) && new BigDecimal(amountStr).doubleValue() > 0) {
                transferAmount.setText(amountStr);
            } else {
                transferAmount.setText("");
            }
        }
        if (requestCode == 2000 && resultCode == RESULT_OK && Data != null) {
            toAddress = Data.getStringExtra("toAddress");
            payeeWalletAddress.setText(toAddress);
        }
    }

    private void getprice(BigDecimal gasprice) {
        if (gasCount == null) {
            return;
        }
        if (gasprice.doubleValue() < min) {
            gasprice = new BigDecimal(min + "");
        }
        BigDecimal sumWei = gasCount.multiply(gasprice);
        BigDecimal sum = sumWei;
        BigDecimal jinzhi = new BigDecimal(Math.pow(10, decimal));
        feiyongStr = sum.divide(jinzhi).toPlainString();
        feiyong.setText(feiyongStr + mustCoin);
    }
}
