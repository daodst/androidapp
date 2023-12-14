

package com.wallet.ctc.ui.blockchain.transfer;

import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletTransctionUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.GasPriceBean;
import com.wallet.ctc.model.blockchain.TransferBean;

import java.math.BigDecimal;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.app.mall.util.ToastUtil;
import common.app.utils.SpUtil;



public class ChangeTransferEthGasPriceActivity extends BaseActivity {
    @BindView(R2.id.seek_bar)
    SeekBar seekBar;
    @BindView(R2.id.title)
    TextView title;
    @BindView(R2.id.feiyong)
    TextView feiyong;
    WalletEntity mWallet;
    int wallettype;

    private WalletTransctionUtil walletTransctionUtil;
    private TransferBean data;
    private BigDecimal gasprice;
    private BigDecimal gasCount;
    private int min = 1;
    private double num = 4;
    private String tokenType = "";
    private String tokenName = "";
    private String feiyongStr;
    private String mustCoin;
    private int from;
    private double basefee;
    @Override
    public int initContentView() {
        return R.layout.activity_ethtransferchangegasprice;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        
        mWallet = walletDBUtil.getWalletInfo();
        wallettype=mWallet.getType();
        data=getIntent().getParcelableExtra("data");
        from=getIntent().getIntExtra("from",0);
        if(null==data){
            ToastUtil.showToast("");
            finish();
            return;
        }
        if(from==0){
            title.setText("");
        }
        title.setText(tokenName + "  " + getString(R.string.transfer));
        List<AssertBean> list= WalletDBUtil.getInstent(this).getMustWallet(wallettype);
        mustCoin=list.get(0).getShort_name();
        data.setType(wallettype);
        seekBar.setVisibility(View.VISIBLE);
        gasprice =new BigDecimal(data.getGasprice());
        gasCount=new BigDecimal(data.getGascount());
        min= new BigDecimal(data.getGasprice()).divide(new BigDecimal("1000000000")).intValue();
        getprice(gasprice);
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
        walletTransctionUtil=new WalletTransctionUtil(this);
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
                if(min<bean.getLow()){
                    min = bean.getLow();
                }
                seekBar.setMax((bean.getUp() - min));
            }

            @Override
            public void showDefGasprice(String defGasprice) {
                mLoadingDialog.dismiss();
                if(new BigDecimal(defGasprice).doubleValue()>min){
                    num = new BigDecimal(defGasprice).doubleValue();
                }else {
                    num=min;
                }
                int def = new BigDecimal(defGasprice).intValue();
                int nums = def - min;
                if (nums < 0) {
                    nums = 0;
                }
                seekBar.setProgress(nums);
            }

            @Override
            public void showTransctionSuccess(String hash) {
                mLoadingDialog.dismiss();
                ToastUtil.showToast(getString(R.string.caozuo_success));
                setResult(RESULT_OK);
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
                basefee=new BigDecimal(baseFeePerGas).doubleValue();
                
                walletTransctionUtil.getEthEIP1559Gas(mWallet.getAllAddress(),tokenType,baseFeePerGas,wallettype);
            }
        });
        if(SpUtil.getFeeStatus()==1&&wallettype== WalletUtil.ETH_COIN) {
            walletTransctionUtil.getMaxPriorityFeePerGas(wallettype);
        }else {
            walletTransctionUtil.getEthGas(mWallet.getAllAddress(), tokenType, wallettype,data.getData(), "");
        }
    }

    @Override
    public void initData() {

    }


    @OnClick({R2.id.img_back, R2.id.submit})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.img_back) {
            finish();

        } else if (i == R.id.submit) {
            data.setFee(feiyongStr.replace(mustCoin, ""));
            data.setKuanggong(feiyongStr);
            data.setGascount(gasCount.intValue());
            data.setGasprice(num + "");
            if(SpUtil.getFeeStatus()==1&&wallettype== WalletUtil.ETH_COIN) {
                data.setGasprice(basefee + num  + "");
                data.setMaxFeePerGas(basefee + num + "");
                data.setMaxPriorityFeePerGas(num + "");
            }
            data.setPayaddress(mWallet.getAllAddress());
            if(SpUtil.getFeeStatus()==1&&wallettype== WalletUtil.ETH_COIN) {
                data.setGasprice(basefee + num + "");
                data.setMaxFeePerGas(basefee + num + "");
                data.setMaxPriorityFeePerGas(num + "");
            }
            walletTransctionUtil.DoTransction(data,true);

        }
    }
    private void getprice(BigDecimal gasprice) {
        if (gasCount == null) {
            return;
        }
        if (gasprice.doubleValue() < min) {
            gasprice = new BigDecimal(min + "");
        }
        if(SpUtil.getFeeStatus()==1&&wallettype== WalletUtil.ETH_COIN) {
            BigDecimal basefe = new BigDecimal(basefee).multiply(new BigDecimal("1000000000"));
            gasprice=gasprice.add(basefe);
        }
        BigDecimal sumWei = gasCount.multiply(gasprice);
        BigDecimal sum = sumWei;
        BigDecimal jinzhi = new BigDecimal("1000000000000000000");
        feiyongStr = sum.divide(jinzhi).toPlainString();
        feiyong.setText(feiyongStr + mustCoin);
    }
}
