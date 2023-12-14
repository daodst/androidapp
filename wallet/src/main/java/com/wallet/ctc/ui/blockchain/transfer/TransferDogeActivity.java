

package com.wallet.ctc.ui.blockchain.transfer;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletTransctionUtil;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.TransferBean;
import com.wallet.ctc.ui.blockchain.addressbook.AddressBookActivity;

import java.math.BigDecimal;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.app.ActivityRouter;
import common.app.mall.util.ToastUtil;
import wallet.core.jni.AnyAddress;
import wallet.core.jni.CoinType;



public class TransferDogeActivity extends BaseActivity {
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
    @BindView(R2.id.feiyong)
    TextView feiyong;
    @BindView(R2.id.submit)
    TextView submit;
    @BindView(R2.id.fromaddress)
    TextView fromaddress;
    private String amountStr;
    private String tokenType = "";
    private String tokenName = "";
    private String toAddress = "";
    private String feiyongStr;
    private String fromAddress = "";
    private String value = "0";
    private BigDecimal gasCount = new BigDecimal("0");
    private int limitCount=1500000;
    private int decimal;
    private BigDecimal gasprice;
    private com.wallet.ctc.db.WalletEntity WalletEntity;
    private WalletTransctionUtil walletTransctionUtil;
    private int wallettype;
    private String mustCoin;
private TransferBean data;
    @Override
    public int initContentView() {
        return R.layout.activity_fil_transfer;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        
        WalletEntity = walletDBUtil.getWalletInfo();
        wallettype=WalletEntity.getType();
        List<AssertBean> list= WalletDBUtil.getInstent(this).getMustWallet(wallettype);
        mustCoin=list.get(0).getShort_name();
        amountStr = getIntent().getStringExtra("amountStr");
        tokenName = getIntent().getStringExtra("tokenName");
        if (tokenName == null || tokenName.length() < 1) {
            tokenName = mustCoin.toUpperCase();
        }
        tokenType = getIntent().getStringExtra("tokenType");
        toAddress = getIntent().getStringExtra("toAddress");
        decimal = getIntent().getIntExtra("decimal", 0);
        String gas = getIntent().getStringExtra("gasCount");
        fromAddress = WalletEntity.getAllAddress();
     gasCount=new BigDecimal(limitCount+"");
        title.setText(tokenName + "  " + getString(R.string.transfer));
        payeeWalletAddress.setText(toAddress);
        payeeWalletAddress.setHint(String.format(getString(R.string.enter_lxr_address_error),tokenName));
        if (null != amountStr && !TextUtils.isEmpty(amountStr) && new BigDecimal(amountStr).doubleValue() > 0) {
            transferAmount.setText(amountStr);
        } else {
            transferAmount.setText("");
        }
        fromaddress.setText(fromAddress);
        walletTransctionUtil=new WalletTransctionUtil(this);
        walletTransctionUtil.setDogetransctionListen(new WalletTransctionUtil.DogeTransctionListen() {
            @Override
            public void showLoading() {
                mLoadingDialog.show();
            }


            @Override
            public void showGasEstimateFee(String defGasprice) {
              if(mLoadingDialog!=null)  mLoadingDialog.dismiss();
                feiyongStr=defGasprice;
                feiyong.setText(feiyongStr);
                if (null != data) {
                    data.setKuanggong(feiyongStr);
                }
            }

            @Override
            public void showTransctionSuccess(String hash) {
                if(mLoadingDialog!=null)  mLoadingDialog.dismiss();
                ToastUtil.showToast(getString(R.string.caozuo_success));
                finish();
            }

            @Override
            public void onFail(String msg) {
                if(mLoadingDialog!=null)  mLoadingDialog.dismiss();
                try {
                    ToastUtil.showToast(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        walletTransctionUtil.getDogeGasFee(data);
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
            
            if (TextUtils.isEmpty(toAddress) || !AnyAddress.isValid(toAddress, CoinType.DOGECOIN)) {
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
            data = new TransferBean(toAddress, fromAddress, amountStr, "",wallettype , tokenName, feiyongStr);
            data.setPayaddress(fromAddress);
            data.setPrice(amountStr);
            data.setTokenName(tokenName);
            data.setRemark(rem);
            if (!TextUtils.isEmpty(feiyongStr)) {
                data.setKuanggong(feiyongStr);
            }
            data.setGascount(gasCount.intValue());
            

            data.setRuaddress(toAddress);
            data.setTokenType(tokenType);
            data.setDecimal(decimal);
            walletTransctionUtil.DoTransction(data,true);

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
        BigDecimal sumWei = gasCount.multiply(gasprice);
        BigDecimal sum = sumWei;
        BigDecimal jinzhi = new BigDecimal("1000000000000000000");
        feiyongStr = sum.divide(jinzhi).toPlainString();
        feiyong.setText(feiyongStr + mustCoin);
    }
}
