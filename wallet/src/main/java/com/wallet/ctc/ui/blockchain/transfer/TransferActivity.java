

package com.wallet.ctc.ui.blockchain.transfer;

import static com.wallet.ctc.BuildConfig.ENABLE_MCC_ADDRESS;
import static com.wallet.ctc.crypto.WalletUtil.MCC_COIN;

import android.content.Intent;
import android.os.Handler;
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
import android.widget.Toast;

import com.google.gson.Gson;
import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.api.blockchain.BlockChainApi;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletTransctionUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.EvmosOneBalanceBean;
import com.wallet.ctc.model.blockchain.RpcApi;
import com.wallet.ctc.model.blockchain.TransferBean;
import com.wallet.ctc.ui.blockchain.addressbook.AddressBookActivity;
import com.wallet.ctc.view.dialog.TransferDialog;
import com.wallet.ctc.view.dialog.choosewallet.ChooseWalletDialog;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.app.ActivityRouter;
import common.app.base.fragment.mall.catcherror.BaseSubscriber;
import common.app.mall.util.ToastUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;



public class TransferActivity extends BaseActivity {

    
    public static final int FROM_CHAT = 11;
    
    public static final int FROM_MAIN = 12;
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
    @BindView(R2.id.fromaddress)
    TextView fromaddress;

    @BindView(R2.id.putong)
    LinearLayout putong;
    @BindView(R2.id.gasprice)
    EditText gasprice;
    @BindView(R2.id.gas)
    EditText gas;
    @BindView(R2.id.gaoji)
    LinearLayout gaoji;
    @BindView(R2.id.shecan)
    TextView shecan;
    @BindView(R2.id.gaoji_open)
    CheckBox gaojiOpen;
    @BindView(R2.id.kgfee)
    TextView kgfee;

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
    @BindView(R2.id.iv_from_address_select)
    ImageView ivFromAddressSelect;

    private Gson gson = new Gson();
    private BlockChainApi mApi = new BlockChainApi();
    private TransferDialog transferDialog;
    private String amountStr;
    
    private String tokenName = "";
    private String toAddress = "";
    private String feiyongStr;
    private String fromAddress = "";
    private String value = "0";
    private BigInteger gasCount = new BigInteger("0");
    private String MCC_BASE_FEE = BuildConfig.TRANSFER_FEE + "";
    private String num = MCC_BASE_FEE;
    private int decimal = 18;
    private int type = 0;
    private int from = 0;
    private String mGasAmount, mGasLimit;
    private WalletTransctionUtil walletTransctionUtil;
    private Handler mHandler = new Handler();
    private WalletEntity mWallet;

    private boolean dealmsg(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return false;
        }
        String realMsgName = "error_" + msg.toLowerCase()
                .replace("'", "")
                .replace("%", "")
                .replace(";", "")
                .replace("(", "")
                .replace(",", "")
                .replace(")", "")
                .replace("-", "")
                .replace(" ", "_");

        int id = getResources().getIdentifier(realMsgName, "string", getPackageName());
        String realMsg = "";
        if (0 != id) {
            realMsg = getString(id);
        } else {
            if (msg.contains("insufficient funds")) {
                realMsg = getString(common.app.R.string.balance_no_enough);
            } else if (msg.contains("insufficient level to transfer")) {
                int strid = getResources().getIdentifier("error_insufficient_level_to_transfer", "string", getPackageName());
                realMsg = getString(strid);
            }
        }
        if (!TextUtils.isEmpty(realMsg)) {
            String finalRealMsg = realMsg;
            runOnUiThread(() -> {
                Toast.makeText(this, finalRealMsg, Toast.LENGTH_SHORT).show();
            });
            return true;
        }
        return false;
    }

    @Override
    public int initContentView() {
        return R.layout.activity_transfer;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        amountStr = getIntent().getStringExtra("amountStr");
        tokenName = getIntent().getStringExtra("tokenName");
        type = getIntent().getIntExtra("type", 0);
        from = getIntent().getIntExtra("from", 0);
        if (from == FROM_MAIN) {
            ivFromAddressSelect.setVisibility(View.VISIBLE);
        } else if (from == FROM_CHAT) {
            type = WalletUtil.MCC_COIN;
            ivFromAddressSelect.setVisibility(View.VISIBLE);
        } else {
            ivFromAddressSelect.setVisibility(View.GONE);
        }
        if (tokenName == null || tokenName.length() < 1) {
            tokenName = getString(R.string.default_token_name);
        }
        
        transferAmount.addTextChangedListener(new MoneyTextWatcher(transferAmount).setDigits(18));
        title.setText(tokenName.toUpperCase() + "  " + getString(R.string.transfer));
        if (type == WalletUtil.DM_COIN) {
            feiyongStr = "0.01 DM";
            kgfee.setText(getString(R.string.miner_costs1dm) + feiyongStr);
        } else if (type == WalletUtil.MCC_COIN) {
            feiyongStr = MCC_BASE_FEE + " " + getString(R.string.default_token_name2).toUpperCase();
            feiyong.setText(feiyongStr);
            seekBar.setVisibility(View.VISIBLE);
            findViewById(R.id.seek_bar_rel).setVisibility(View.VISIBLE);
            setGasLowUp(num, 1);
        } else if (type == WalletUtil.OTHER_COIN) {
            feiyongStr = BuildConfig.TRANSFER_OTHER_FEE + " " + getString(R.string.default_other_token_name).toUpperCase();
            feiyong.setText(feiyongStr);
            seekBar.setVisibility(View.VISIBLE);
            findViewById(R.id.seek_bar_rel).setVisibility(View.VISIBLE);
        }
        toAddress = getIntent().getStringExtra("toAddress");
        payeeWalletAddress.setText(toAddress);
        if (null != amountStr && !TextUtils.isEmpty(amountStr)) {
            if (new BigDecimal(amountStr).doubleValue() > 0) {
                transferAmount.setText(amountStr);
            }
        }
        if (from == FROM_MAIN) {
            
            mWallet = WalletDBUtil.getInstent(this).getWalletInfo(MCC_COIN);
            fromAddress = mWallet.getAllAddress2();
        } else if (from != FROM_CHAT) {
            
            mWallet = walletDBUtil.getWalletInfo();
            fromAddress = walletDBUtil.getWalletInfo().getAllAddress2();
        }

        seekBar.setProgress(0);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (type == WalletUtil.MCC_COIN) {
                    num = new BigDecimal((progress + 10) + "").multiply(new BigDecimal(MCC_BASE_FEE + "").divide(new BigDecimal("10"))).toString();
                    feiyongStr = num + " " + getString(R.string.default_token_name2).toUpperCase();
                } else {
                    num = new BigDecimal((progress + 10) + "").multiply(new BigDecimal(BuildConfig.TRANSFER_OTHER_FEE + "").divide(new BigDecimal("10"))).toString();
                    feiyongStr = num + " " + getString(R.string.default_other_token_name).toUpperCase();
                }
                feiyong.setText(feiyongStr);
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
        
        List<AssertBean> assets = walletDBUtil.getMustWallet(type);
        decimal = assets.get(0).getDecimal();
        if (decimal == 0) {
            decimal = 18;
        }

        fromaddress.setText(fromAddress);
        walletTransctionUtil = new WalletTransctionUtil(this);
        walletTransctionUtil.setOnDMTransctionListen(new WalletTransctionUtil.DMTransctionListen() {
            @Override
            public void showLoading() {
                mLoadingDialog.show();
            }

            @Override
            public void showTransctionSuccess(String hash) {
                if (from == 1) {
                    mLoadingDialog.dismiss();
                    ToastUtil.showToast(getString(R.string.caozuo_success2));
                    setResult(RESULT_OK);
                    finish();
                    return;
                } else if (from == FROM_CHAT) {
                    mLoadingDialog.dismiss();
                    ToastUtil.showToast(getString(R.string.caozuo_success2));
                    Intent data = new Intent();
                    data.putExtra("transferNum", amountStr);
                    data.putExtra("symbol", tokenName);
                    setResult(RESULT_OK, data);
                    finish();
                    return;
                }

                

                mHandler.postDelayed(() -> {
                    mLoadingDialog.dismiss();
                    ToastUtil.showToast(getString(R.string.caozuo_success2));
                    finish();
                }, 7000);
            }

            @Override
            public void onFail(String msg) {
                mLoadingDialog.dismiss();

                if (!dealmsg(msg)) {
                    ToastUtil.showToast(msg);
                }
            }

            @Override
            public void showGasInfo(String gasPrice, String gasLimit) {
                mGasAmount = gasPrice;
                mGasLimit = gasLimit;
                if (type == WalletUtil.MCC_COIN) {
                    if (!TextUtils.isEmpty(gasPrice)) {
                        seekBar.setProgress(0);
                        MCC_BASE_FEE = new BigDecimal(gasPrice).divide(new BigDecimal(Math.pow(10, decimal)), 18, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
                        num = MCC_BASE_FEE;
                        feiyongStr = num + " " + getString(R.string.default_token_name2).toUpperCase();
                    }
                }

                setGasLowUp(num, 1);

            }
        });

        getMccGas();
    }

    private void getMccGas() {
        if (type == WalletUtil.MCC_COIN) {
            String toAddress = payeeWalletAddress.getText().toString().trim();
            walletTransctionUtil.getMccGas(fromAddress, toAddress, tokenName);
        }
    }

    @Override
    public void initData() {
        if (from != FROM_CHAT) {
            
            getBanlance();
        }

    }

    RpcApi mRpcApi = null;

    private void getBanlance() {
        if (null == mRpcApi) {
            mRpcApi = new RpcApi();
        }

        String address = null == mWallet ? fromAddress : mWallet.getAllAddress();
        mRpcApi.getEvmosOneBalance(address, tokenName).observeOn(AndroidSchedulers.mainThread()).subscribe(new BaseSubscriber<EvmosOneBalanceBean>(this) {
            @Override
            public void onNexts(EvmosOneBalanceBean baseEntity) {
                if (baseEntity.isSuccess()) {
                    List<AssertBean> assetsList = walletDBUtil.getMustWallet(type);
                    int decimal = 18;
                    for (int i = 0; i < assetsList.size(); i++) {
                        if (assetsList.get(i).getShort_name().equals(type)) {
                            decimal = assetsList.get(i).getDecimal();
                            break;
                        }
                    }
                    String balance = baseEntity.getBalance(decimal);
                    if (!TextUtils.isEmpty(balance)) {
                        value = balance;
                        yue.setText(getString(R.string.wallet_yue) + value);
                    }
                } else {
                }
            }

            @Override
            public void onError(Throwable e) {
            }
        });

    }

    @OnClick({R2.id.img_back, R2.id.submit, R2.id.imgqrcode, R2.id.shecan, R2.id.go_adddresslist, R2.id.basTransfer_custom, R2.id.basTransfer_slowView, R2.id.basTransfer_normalView, R2.id.basTransfer_fastView, R2.id.use_all_tv, R2.id.ll_from_address})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.img_back) {
            finish();

        } else if (i == R.id.shecan) {

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
            if (TextUtils.isEmpty(toAddress) || toAddress.length() < 30) {
                ToastUtil.showToast(getString(R.string.payee_wallet_address_errpr));
                return;
            }
            if (type == WalletUtil.MCC_COIN && !toAddress.startsWith(ENABLE_MCC_ADDRESS)) {
                ToastUtil.showToast(getString(R.string.address_is_no_invalidate));
                return;
            }

            if (TextUtils.isEmpty(fromAddress)) {
                ToastUtil.showToast(getString(R.string.pay_wallet_address_empty));
                return;
            }

            if (TextUtils.isEmpty(amountStr)) {
                ToastUtil.showToast(getString(R.string.transfer_amount));
                return;
            }

            try {
                if (new BigDecimal(amountStr).compareTo(new BigDecimal(value)) > 0) {
                    ToastUtil.showToast(getString(R.string.insufficient_balance));
                    return;
                }
            } catch (Exception e) {
                ToastUtil.showToast(getString(R.string.please_input_legal_number));
                return;
            }

            if (customTv.isSelected()) {
                String gass = gas.getText().toString().trim();
                String gasspr = gasprice.getText().toString().trim();
                if (TextUtils.isEmpty(gass)) {
                    ToastUtil.showToast(getString(R.string.gaoji_gas));
                    return;
                }
                if (TextUtils.isEmpty(gasspr)) {
                    ToastUtil.showToast(getString(R.string.gaoji_gasprice));
                    return;
                }
                mGasLimit = gass;
                num = gasspr;
            }
            try {
                String num1 = getNum(feiyongStr);
                if (new BigDecimal(value).equals(new BigDecimal(amountStr)) && !TextUtils.equals(num1, "-1")) {
                    amountStr = new BigDecimal(amountStr).subtract(new BigDecimal(num1).multiply(new BigDecimal("2"))).toPlainString();
                }
            } catch (Exception e) {
                ToastUtil.showToast(getString(R.string.please_input_legal_number));
                e.printStackTrace();
                return;
            }

            TransferBean data = new TransferBean(toAddress, fromAddress, amountStr, num, type, tokenName, feiyongStr);

            data.setRemark(rem);
            data.setGascount(gasCount.intValue());
            String gasAmount = new BigDecimal(num).multiply(new BigDecimal(Math.pow(10, decimal))).stripTrailingZeros().toPlainString();
            data.setGasprice(gasAmount);
            data.setGasFeeCap(mGasLimit);
            walletTransctionUtil.DoTransction(data, true);

        } else if (i == R.id.imgqrcode) {
            Intent intent = ActivityRouter.getEmptyContentIntent(this, ActivityRouter.Common.F_QRCodeFragment);
            startActivityForResult(intent, 1000);

        } else if (i == R.id.go_adddresslist) {
            Intent intent2 = new Intent(this, AddressBookActivity.class);
            intent2.putExtra("type", 1);
            startActivityForResult(intent2, 2000);

        } else if (i == R.id.ll_from_address) {
            if (from == FROM_CHAT || from == FROM_MAIN) {
                ChooseWalletDialog.showDialog(TransferActivity.this, WalletUtil.MCC_COIN, (address, walletType) -> {
                    if (from == FROM_MAIN) {
                        
                        mWallet = WalletDBUtil.getInstent(this).getWalletInfo(MCC_COIN);
                        fromAddress = mWallet.getAllAddress2();
                    }
                    fromAddress = address;
                    fromaddress.setText(fromAddress);
                    getMccGas();
                    getBanlance();
                });
            }
        }
    }

    Pattern mPattern = Pattern.compile("\\d+(\\.\\d+)?");

    public String getNum(String data) {
        try {
            Matcher m = mPattern.matcher(data);
            if (m.find()) {
                return m.group();
            } else {
                
                return "-1";
            }
        } catch (Exception e) {
            e.printStackTrace();
            
            return "-1";
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
                setGas = (String) slowValueTv.getTag();
                break;
            case 2:
                normalView.setSelected(true);
                slowView.setSelected(false);
                fastView.setSelected(false);
                customTv.setSelected(false);
                gaoji.setVisibility(View.GONE);
                setGas = (String) normalValueTv.getTag();
                break;
            case 3:
                fastView.setSelected(true);
                slowView.setSelected(false);
                normalView.setSelected(false);
                customTv.setSelected(false);
                gaoji.setVisibility(View.GONE);
                setGas = (String) fastValueTv.getTag();
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


    
    private void setGasLowUp(String base, int defaultSet) {
        if (TextUtils.isEmpty(base)) {
            return;
        }
        String low = base;
        slowValueTv.setText(getpriceValue(base));
        slowValueTv.setTag(base);
        slowTimeTv.setText(getString(R.string.bql_about) + "6" + getString(R.string.bql_second));

        String center = new BigDecimal(base).multiply(new BigDecimal("2")).toPlainString();
        normalValueTv.setText(getpriceValue(center));
        normalValueTv.setTag(center);
        normalTimeTv.setText(getString(R.string.bql_about) + "6" + getString(R.string.bql_second));

        String fast = new BigDecimal(base).multiply(new BigDecimal("4")).toPlainString();
        fastValueTv.setText(getpriceValue(fast));
        fastValueTv.setTag(fast);
        fastTimeTv.setText(getString(R.string.bql_about) + "6" + getString(R.string.bql_second));

        
        if (defaultSet == 1) {
            
            setChargeFee(low + "");
        } else if (defaultSet == 2) {
            
            setChargeFee(center + "");
        } else {
            
            setChargeFee(fast + "");
        }
        initChargeFeeView(defaultSet);

    }

    private String getpriceValue(String gasprice) {
        return gasprice + getString(R.string.default_token_name2).toUpperCase();
    }

    
    private void setChargeFee(String defGasprice) {
        num = defGasprice;
        feiyongStr = defGasprice + " " + getString(R.string.default_token_name2).toUpperCase();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent Data) {
        super.onActivityResult(requestCode, resultCode, Data);
        if (requestCode == 1000 && resultCode == RESULT_OK && Data != null) {
            amountStr = Data.getStringExtra("amountStr");
            toAddress = Data.getStringExtra("toAddress");
            payeeWalletAddress.setText(toAddress);
            transferAmount.setText(amountStr);
        }
        if (requestCode == 2000 && resultCode == RESULT_OK && Data != null) {
            toAddress = Data.getStringExtra("toAddress");
            payeeWalletAddress.setText(toAddress);
        }
    }
}
