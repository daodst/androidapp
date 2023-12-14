

package com.wallet.ctc.ui.blockchain.transfer;

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
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.GasPriceBean;
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
import common.app.utils.SpUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;



public class TransferEthActivity extends BaseActivity {
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
    @BindView(R2.id.kuanggong_kuaiman)
    RelativeLayout kuanggongKuaiman;
    @BindView(R2.id.kuanggong_gaoji)
    RelativeLayout kuanggongGaoji;


    @BindView(R2.id.basTransfer_slowView)
    RelativeLayout slowView;
    @BindView(R2.id.basTransfer_slowDesc)
    TextView slowDescTv;
    @BindView(R2.id.basTransfer_slowValue)
    TextView slowValueTv;
    @BindView(R2.id.basTransfer_slowTime)
    TextView slowTimeTv;
    @BindView(R2.id.basTransfer_normalView)
    RelativeLayout normalView;
    @BindView(R2.id.basTransfer_normalDesc)
    TextView normalDescTv;
    @BindView(R2.id.basTransfer_normalValue)
    TextView normalValueTv;
    @BindView(R2.id.basTransfer_normalTime)
    TextView normalTimeTv;
    @BindView(R2.id.basTransfer_fastView)
    RelativeLayout fastView;
    @BindView(R2.id.basTransfer_fastDesc)
    TextView fastDescTv;
    @BindView(R2.id.basTransfer_fastValue)
    TextView fastValueTv;
    @BindView(R2.id.basTransfer_fastTime)
    TextView fastTimeTv;
    @BindView(R2.id.basTransfer_custom)
    TextView customTv;
    @BindView(R2.id.gaoji_gas_layout)
    LinearLayout gaojiGasLayout;
    @BindView(R2.id.remarks_parent)
    LinearLayout remarks_parent;


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
    private double num = 4;
    private int decimal;
    private BigDecimal gasprice;
    private int min = 1;
    private int from = 0;
    private com.wallet.ctc.db.WalletEntity WalletEntity;
    private WalletTransctionUtil walletTransctionUtil;
    private int wallettype;
    private String mustCoin;
    private double basefee;

    @Override
    public int initContentView() {
        return R.layout.activity_ethtransfer;
    }

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

        if (TextUtils.isEmpty(tokenType)) {
            remarks_parent.setVisibility(View.VISIBLE);
        } else {
            remarks_parent.setVisibility(View.GONE);
        }
        toAddress = getIntent().getStringExtra("toAddress");
        decimal = getIntent().getIntExtra("decimal", 0);
        String gas = getIntent().getStringExtra("gasCount");
        if (wallettype == WalletUtil.MCC_COIN) {
            fromAddress = WalletEntity.getDefaultAddress();
        } else {
            fromAddress = WalletEntity.getAllAddress();
        }
        if (gas != null && gas.length() > 0) {
            gasCount = new BigDecimal(gas);
        } else {
            gasCount = new BigDecimal("25200");
        }
        
        kuanggongKuaiman.setVisibility(View.VISIBLE);
        kuanggongGaoji.setVisibility(View.VISIBLE);
        seekBar.setVisibility(View.VISIBLE);
        gasprice = Constants.GAS_PRICE;
        getprice(gasprice);
        title.setText(tokenName + "  " + getString(R.string.transfer));
        payeeWalletAddress.setText(toAddress);
        if (null != amountStr && !TextUtils.isEmpty(amountStr) && new BigDecimal(amountStr).doubleValue() > 0) {
            transferAmount.setText(amountStr);
        } else {
            transferAmount.setText("");
        }

        seekBar.setProgress(0);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                num = progress + (int) Math.floor(min);
                gasprice = new BigDecimal(num).multiply(new BigDecimal("1000000000"));
                getprice(gasprice);
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

                    gaojiGasLayout.setVisibility(View.VISIBLE);
                } else {
                    gaojiGasLayout.setVisibility(View.GONE);
                }
            }
        });
        fromaddress.setText(fromAddress);
        walletTransctionUtil = new WalletTransctionUtil(this);
        walletTransctionUtil.setOnTransctionListen(new WalletTransctionUtil.TransctionListen() {
            @Override
            public void showLoading() {
                mLoadingDialog.show();
            }

            @Override
            public void showGasCount(String gasc) {
                gasCount = new BigDecimal(gasc);
                getprice(gasprice);
            }

            @Override
            public void showGasprice(GasPriceBean bean) {
                
                
                setGasLowUp(bean, 1);
            }

            @Override
            public void showDefGasprice(String defGasprice) {
                mLoadingDialog.dismiss();
                
            }

            @Override
            public void showTransctionSuccess(String hash) {
                mLoadingDialog.dismiss();
                ToastUtil.showToast(getString(R.string.caozuo_success));
                if (from == 1) {
                    setResult(RESULT_OK);
                    finish();
                    return;
                }
                finish();
            }

            @Override
            public void onFail(String msg) {
                mLoadingDialog.dismiss();
                ToastUtil.showToast(msg);
            }

            @Override
            public void showEip1559(String baseFeePerGas) {
                mLoadingDialog.dismiss();
                basefee = new BigDecimal(baseFeePerGas).doubleValue();
                
                walletTransctionUtil.getEthEIP1559Gas(fromAddress, tokenType, baseFeePerGas, wallettype);
            }
        });
        if (SpUtil.getFeeStatus() == 1 && wallettype == WalletUtil.ETH_COIN) {
            walletTransctionUtil.getMaxPriorityFeePerGas(wallettype);
        } else {
            walletTransctionUtil.getEthGas(fromAddress, tokenType, wallettype, "", "");
        }
    }

    @Override
    public void initData() {
        if (tokenName.toLowerCase().equals(mustCoin.toLowerCase())) {
            value = WalletEntity.getmBalance();
        } else {
            AssertBean bean = walletDBUtil.getAssetsByAddress(WalletEntity.getAllAddress(), tokenType, wallettype);
            if (null != bean) {
                value = bean.getAssertsNum();
            }
        }
        yue.setText(getString(R.string.wallet_yue) + value);

    }

    @OnClick({R2.id.img_back, R2.id.submit, R2.id.imgqrcode, R2.id.shecan, R2.id.go_adddresslist,
            R2.id.basTransfer_custom, R2.id.basTransfer_slowView, R2.id.basTransfer_normalView, R2.id.basTransfer_fastView,
            R2.id.use_all_tv})
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
                                Intent intent = new Intent(TransferEthActivity.this, BaseWebViewActivity.class);
                                intent.putExtra("url", baseEntity.getData().toString());
                                intent.putExtra("title", getString(R.string.shezhicanshu));
                                startActivity(intent);
                            } else {
                                ToastUtil.showToast(baseEntity.getInfo());
                            }
                        }
                    });
        } else if (i == R.id.basTransfer_slowView) {
            initChargeFeeView(1);
        } else if (i == R.id.basTransfer_normalView) {
            initChargeFeeView(2);
        } else if (i == R.id.basTransfer_fastView) { 
            initChargeFeeView(3);
        } else if (i == R.id.basTransfer_custom) { 
            initChargeFeeView(4);
        } else if (i == R.id.use_all_tv) { 
            transferAmount.setText(value);
        } else if (i == R.id.submit) {
            toAddress = payeeWalletAddress.getText().toString().trim();
            toAddress = toAddress.replace(" ", "");
            String rem = remarks.getText().toString().trim();
            amountStr = transferAmount.getText().toString().trim();
            
            if (TextUtils.isEmpty(toAddress) || toAddress.length() < 40 || toAddress.length() > 42) {
                ToastUtil.showToast(getString(R.string.payee_wallet_address_errpr));
                return;
            }
            if (!toAddress.startsWith("0x")) {
                ToastUtil.showToast(getString(R.string.address_is_no_invalidate));
                return;
            }

            if (TextUtils.isEmpty(amountStr)) {
                ToastUtil.showToast(getString(R.string.transfer_amount));
                return;
            }

            TransferBean data = new TransferBean(toAddress, fromAddress, amountStr, feiyongStr.replace(mustCoin, ""), wallettype, tokenName, feiyongStr);
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
            if (customTv.isSelected()) {
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
                BigDecimal jinzhi = new BigDecimal("1000000000000000000");
                String kuanggong = sum.divide(jinzhi).toPlainString();
                data.setKuanggong(kuanggong);
            } else {
                data.setKuanggong(feiyongStr);
                data.setGascount(gasCount.intValue());
                data.setGasprice(num + "");
                if (SpUtil.getFeeStatus() == 1 && wallettype == WalletUtil.ETH_COIN) {
                    data.setGasprice(basefee + num + "");
                    data.setMaxFeePerGas(basefee + num + "");
                    data.setMaxPriorityFeePerGas(num + "");
                }
            }
            data.setRuaddress(toAddress);
            data.setTokenType(tokenType);
            data.setDecimal(decimal);
            walletTransctionUtil.DoTransction(data, true);

        } else if (i == R.id.imgqrcode) {
            Intent intent = ActivityRouter.getEmptyContentIntent(this, ActivityRouter.Common.F_QRCodeFragment);
            startActivityForResult(intent, 1000);

        } else if (i == R.id.go_adddresslist) {
            Intent intent2 = new Intent(this, AddressBookActivity.class);
            intent2.putExtra("type", 1);
            startActivityForResult(intent2, 2000);

        } else {
        }
    }


    
    private void initChargeFeeView(int index) {
        String setGas = "";
        switch (index) {
            case 1:
                slowView.setSelected(true);
                normalView.setSelected(false);
                fastView.setSelected(false);
                customTv.setSelected(false);
                gaoji.setVisibility(View.GONE);
                setGas = String.valueOf(slowValueTv.getTag());
                break;
            case 2:
                normalView.setSelected(true);
                slowView.setSelected(false);
                fastView.setSelected(false);
                customTv.setSelected(false);
                gaoji.setVisibility(View.GONE);
                setGas = String.valueOf(normalValueTv.getTag());
                break;
            case 3:
                fastView.setSelected(true);
                slowView.setSelected(false);
                normalView.setSelected(false);
                customTv.setSelected(false);
                gaoji.setVisibility(View.GONE);
                setGas = String.valueOf(fastValueTv.getTag());
                break;
            case 4:
                slowView.setSelected(false);
                normalView.setSelected(false);
                fastView.setSelected(false);
                customTv.setSelected(true);
                gaoji.setVisibility(View.VISIBLE);
                break;
        }
        if (!TextUtils.isEmpty(setGas)) {
            setChargeFee(setGas);
        }
    }


    
    private void setGasLowUp(GasPriceBean bean, int defaultSet) {
        if (null == bean) {
            return;
        }
        if (bean.getLow() > 0) {
            BigDecimal gWeiBig = new BigDecimal(bean.getLow()).multiply(new BigDecimal("1000000000"));
            slowValueTv.setText(getpriceValue(gWeiBig));
            slowValueTv.setTag(bean.getLow());
            slowTimeTv.setText(getString(R.string.bql_about) + "6" + getString(R.string.bql_second));
        }
        if (bean.getCenter() > 0) {
            BigDecimal gWeiBig = new BigDecimal(bean.getCenter()).multiply(new BigDecimal("1000000000"));
            normalValueTv.setText(getpriceValue(gWeiBig));
            normalValueTv.setTag(bean.getCenter());
            normalTimeTv.setText(getString(R.string.bql_about) + "6" + getString(R.string.bql_second));


        }
        if (bean.getUp() > 0) {
            BigDecimal gWeiBig = new BigDecimal(bean.getUp()).multiply(new BigDecimal("1000000000"));
            fastValueTv.setText(getpriceValue(gWeiBig));
            fastValueTv.setTag(bean.getUp());
            fastTimeTv.setText(getString(R.string.bql_about) + "6" + getString(R.string.bql_second));
        }

        
        if (defaultSet == 1) {
            
            setChargeFee(bean.getLow() + "");
        } else if (defaultSet == 2) {
            
            setChargeFee(bean.getCenter() + "");
        } else {
            
            setChargeFee(bean.getUp() + "");
        }
        initChargeFeeView(defaultSet);

    }

    private String getpriceValue(BigDecimal gasprice) {
        BigDecimal sumWei = gasCount.multiply(gasprice);
        BigDecimal sum = sumWei;
        BigDecimal jinzhi = new BigDecimal("1000000000000000000");
        return sum.divide(jinzhi).toPlainString() + mustCoin;
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

    
    private void setChargeFee(String defGasprice) {
        num = new BigDecimal(defGasprice).doubleValue();
        gasprice = new BigDecimal(defGasprice).multiply(new BigDecimal("1000000000"));
        getprice(gasprice);
    }

    private void getprice(BigDecimal gasprice) {
        if (gasCount == null) {
            return;
        }
        
        if (SpUtil.getFeeStatus() == 1 && wallettype == WalletUtil.ETH_COIN) {
            BigDecimal basefe = new BigDecimal(basefee).multiply(new BigDecimal("1000000000"));
            gasprice = gasprice.add(basefe);
        }
        BigDecimal sumWei = gasCount.multiply(gasprice);
        BigDecimal sum = sumWei;
        BigDecimal jinzhi = new BigDecimal("1000000000000000000");
        feiyongStr = sum.divide(jinzhi).toPlainString();
    }
}
