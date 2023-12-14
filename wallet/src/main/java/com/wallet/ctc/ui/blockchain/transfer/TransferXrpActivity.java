

package com.wallet.ctc.ui.blockchain.transfer;

import android.content.Intent;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
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
import com.wallet.ctc.crypto.WalletTransctionUtil;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.TransferBean;
import com.wallet.ctc.model.blockchain.XrpTransFee;
import com.wallet.ctc.ui.blockchain.addressbook.AddressBookActivity;

import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.app.ActivityRouter;
import common.app.base.fragment.mall.catcherror.BaseSubscriber;
import common.app.mall.util.ToastUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;



public class TransferXrpActivity extends BaseActivity {
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
    @BindView(R2.id.yue)
    TextView yue;
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
    @BindView(R2.id.tips_tv)
    TextView tipsTv;
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
    private double num = 4;
    private int decimal;
    private BigDecimal gasprice;
    private int min = 1;
    private int from = 0;
    private com.wallet.ctc.db.WalletEntity WalletEntity;
    private WalletTransctionUtil walletTransctionUtil;

    private XrpTransFee mXrpTransFeeConfig;
    private Handler mHandler;
    private Runnable mDelayFinishTask;

    @Override
    public int initContentView() {
        return R.layout.activity_xrptransfer;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        amountStr = getIntent().getStringExtra("amountStr");
        tokenName = getIntent().getStringExtra("tokenName");
        from = getIntent().getIntExtra("from", 0);
        if (tokenName == null || tokenName.length() < 1) {
            tokenName = "XRP";
        }
        tokenType = getIntent().getStringExtra("tokenType");
        toAddress = getIntent().getStringExtra("toAddress");
        decimal = getIntent().getIntExtra("decimal", 0);
        fromAddress = walletDBUtil.getWalletInfo().getAllAddress();

        
        seekBar.setVisibility(View.VISIBLE);
        gasprice = Constants.GAS_PRICE;
        title.setText(tokenName + "  " + getString(R.string.transfer));
        payeeWalletAddress.setText(toAddress);
        if (null != amountStr && !TextUtils.isEmpty(amountStr) && new BigDecimal(amountStr).doubleValue() > 0) {
            transferAmount.setText(amountStr);
        } else {
            transferAmount.setText("");
        }

        mHandler = new Handler();

        seekBar.setProgress(0);
        seekBar.setMax(100);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                num = progress + min;
                getprice(progress);
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
        walletTransctionUtil.setOnXrpTransctionListen(new WalletTransctionUtil.XrpTransctionListen() {
            @Override
            public void showLoading() {
                mLoadingDialog.show();
            }

            @Override
            public void showDefDee(XrpTransFee mbean) {
                mLoadingDialog.dismiss();
                seekBar.setProgress(mbean.getDefProgress());
                mXrpTransFeeConfig = mbean;
                feiyongStr = mXrpTransFeeConfig.getMedian_fee();
                feiyong.setText(feiyongStr + "xrp");
            }


            @Override
            public void showTransctionSuccess(String hash) {
                
                
                Log.d("xccTest", "showTransactionSuccess()" + hash);
                mHandler.postDelayed(mDelayFinishTask = new Runnable() {
                    @Override
                    public void run() {

                        Log.d("xccTest", "showTransactionSuccess()" + mLoadingDialog);
                        if (null != mLoadingDialog) {
                            mLoadingDialog.dismiss();
                        }
                        ToastUtil.showToast(getString(R.string.caozuo_success));
                        if (from == 1) {
                            setResult(RESULT_OK);
                            finish();
                            return;
                        }
                        finish();
                    }
                }, 2000);
            }

            @Override
            public void onFail(String msg) {
                mLoadingDialog.dismiss();
                ToastUtil.showToast(msg);
            }
        });
        walletTransctionUtil.getXrpFee(fromAddress, tokenType);

        if (!TextUtils.isEmpty(tokenType) && !TextUtils.isEmpty(tokenName)) {
            
            tipsTv.setText(getString(R.string.xpy_token_tip_begin) + tokenName.toUpperCase() + getString(R.string.xpy_token_tip_end) + tokenName.toUpperCase());
        } else {
            
            tipsTv.setText(R.string.xpy_transfer_tip);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mHandler && mDelayFinishTask != null) {
            mHandler.removeCallbacks(mDelayFinishTask);
        }
    }

    @Override
    public void initData() {
        
        WalletEntity = walletDBUtil.getWalletInfo();
        if (tokenName.toLowerCase().equals("xrp")) {
            value = WalletEntity.getmBalance();
        } else {
            AssertBean bean = walletDBUtil.getAssetsByAddress(WalletEntity.getAllAddress(), tokenType,WalletEntity.getType());
            if (null != bean) {
                value = bean.getAssertsNum();
            }
        }
        yue.setText(getData(getString(R.string.wallet_yue), value, R.color.default_text_three_color, R.color.default_text_two_color));
    }

    public SpannableString getData(String startStr, String endStr, int startColor, int endColor) {
        SpannableString ss = new SpannableString(startStr + endStr);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(getResources().getColor(startColor));
        ss.setSpan(colorSpan, 0, startStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        colorSpan = new ForegroundColorSpan(getResources().getColor(endColor));
        ss.setSpan(colorSpan, startStr.length(), ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
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
                                Intent intent = new Intent(TransferXrpActivity.this, BaseWebViewActivity.class);
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
            
            if (TextUtils.isEmpty(toAddress) || toAddress.length() < 30) {
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

            String feiyong = feiyongStr;
            if (!TextUtils.isEmpty(feiyongStr)) {
                feiyong = feiyongStr.replace("xrp", "");
            }
            TransferBean data = new TransferBean(toAddress, fromAddress, amountStr, feiyong, 5, tokenName, feiyongStr);
            data.setPayaddress(fromAddress);
            data.setPrice(amountStr);
            data.setTokenName(tokenName);
            data.setRemark(rem);
            data.setRuaddress(toAddress);
            data.setTokenType(tokenType);
            data.setKuanggong(feiyongStr);
            walletTransctionUtil.DoTransction(data, true);

        } else if (i == R.id.imgqrcode) {
            Intent intent = ActivityRouter.getEmptyContentIntent(this,ActivityRouter.Common.F_QRCodeFragment);
            startActivityForResult(intent, 1000);

        } else if (i == R.id.go_adddresslist) {
            Intent intent2 = new Intent(this, AddressBookActivity.class);
            intent2.putExtra("type", 1);
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

    private void getprice(int progress) {
        if (null == mXrpTransFeeConfig) {
            return;
        }
        feiyongStr = mXrpTransFeeConfig.getFee(progress);
        feiyong.setText(feiyongStr + "xrp");
    }


}
