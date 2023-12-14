

package com.wallet.ctc.ui.blockchain.transfer;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.crypto.WalletTransctionUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.TransferBean;
import com.wallet.ctc.ui.blockchain.addressbook.AddressBookActivity;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.app.ActivityRouter;
import common.app.mall.util.ToastUtil;



public class TransferTrxActivity extends BaseActivity {

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
    @BindView(R2.id.submit)
    TextView submit;
    @BindView(R2.id.yue)
    TextView yue;
    @BindView(R2.id.fromaddress)
    TextView fromaddress;
    private String amountStr;
    private String value = "0";
    private String toAddress = "";
    private String tokenType = "";
    private int decimal;
    private String feiyongStr;
    private String fromAddress = "";
    private String tokenName;
    private BigInteger gasCount = new BigInteger("0");
    private String num = BuildConfig.TRANSFER_FEE + "";
    private List<String> list=new ArrayList<>();
    private int choosePosition=1;
    private BtcFeesAdapter mAdapter;
    private WalletTransctionUtil walletTransctionUtil;
    @Override
    public int initContentView() {
        return R.layout.activity_trx_transfer;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        amountStr = getIntent().getStringExtra("amountStr");
        toAddress = getIntent().getStringExtra("toAddress");
        tokenName=getIntent().getStringExtra("tokenName");
        tokenType = getIntent().getStringExtra("tokenType");
        decimal = getIntent().getIntExtra("decimal", 0);
        title.setText(tokenName.toUpperCase()+"  " + getString(R.string.transfer));
        payeeWalletAddress.setText(toAddress);
        if (null != amountStr && !TextUtils.isEmpty(amountStr)) {
            if (new BigDecimal(amountStr).doubleValue() > 0) {
                transferAmount.setText(amountStr);
            }
        }
        fromAddress = walletDBUtil.getWalletInfo().getAllAddress();

        fromaddress.setText(fromAddress);
        mAdapter=new BtcFeesAdapter(this);
        mAdapter.bindData(choosePosition);
        mAdapter.bindData(list);

        walletTransctionUtil=new WalletTransctionUtil(this);
        walletTransctionUtil.setTrxTransctionListen(new WalletTransctionUtil.TrxTransctionListen() {
            @Override
            public void showLoading() {
                mLoadingDialog.show();
            }
            @Override
            public void showTransctionSuccess(String hash) {
                mLoadingDialog.dismiss();
                ToastUtil.showToast(getString(R.string.caozuo_success));
                finish();
            }

            @Override
            public void onFail(String msg) {
                mLoadingDialog.dismiss();
                ToastUtil.showToast(msg);
            }
        });
    }

    @Override
    public void initData() {
        if (tokenName.toLowerCase().equals("trx")) {
            value = new BigDecimal(walletDBUtil.getWalletInfo().getmBalance()).toPlainString();
        } else {
            AssertBean bean = walletDBUtil.getAssetsByAddress(fromAddress, tokenType,walletDBUtil.getWalletInfo().getType());
            if (null != bean) {
                value = bean.getAssertsNum();
            }
        }
        yue.setText(getString(R.string.wallet_yue) + value);

    }

    @OnClick({R2.id.img_back, R2.id.submit, R2.id.imgqrcode, R2.id.go_adddresslist})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.img_back) {
            finish();
        } else if (i == R.id.submit) {

            toAddress = payeeWalletAddress.getText().toString().trim();
            toAddress = toAddress.replace(" ", "");
            String rem = remarks.getText().toString().trim();
            amountStr = transferAmount.getText().toString().trim();
            if (TextUtils.isEmpty(toAddress) || toAddress.length() != 34) {
                ToastUtil.showToast(getString(R.string.payee_wallet_address_errpr));
                return;
            }
            if (TextUtils.isEmpty(amountStr)||amountStr.equals(".")) {
                ToastUtil.showToast(getString(R.string.transfer_assest_num));
                return;
            }
            BigDecimal price1=new BigDecimal(Math.pow(10,decimal)).multiply(new BigDecimal(amountStr));
            if (price1.compareTo(BigDecimal.ZERO)<=0) {
                ToastUtil.showToast(getString(R.string.transfer_assest_num_error));
                return;
            }
            if(tokenName.toUpperCase().equals("TRX")) {
                BigDecimal bigDecimal = new BigDecimal(amountStr).subtract(new BigDecimal(value));
                if (bigDecimal.doubleValue() > 0 || new BigDecimal(value).doubleValue() == 0) {
                    ToastUtil.showToast(getString(R.string.no_money));
                    return;
                }
            }else if(new BigDecimal(walletDBUtil.getWalletInfo().getmBalance()).doubleValue()<10){
                ToastUtil.showToast("TRX"+getString(R.string.insufficient_balance));
                return;
            }
            feiyongStr="";
            TransferBean data = new TransferBean(toAddress,fromAddress,amountStr, WalletUtil.TRX_COIN,tokenName.toUpperCase(),tokenType,decimal);
            walletTransctionUtil.DoTransction(data,true);

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
            transferAmount.setText(amountStr);
        }
        if (requestCode == 2000 && resultCode == RESULT_OK && Data != null) {
            toAddress = Data.getStringExtra("toAddress");
            payeeWalletAddress.setText(toAddress);
        }
    }
}
