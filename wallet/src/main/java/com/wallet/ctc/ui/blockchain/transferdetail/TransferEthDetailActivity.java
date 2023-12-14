

package com.wallet.ctc.ui.blockchain.transferdetail;

import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.TransactionNewEthRecordBean;
import com.wallet.ctc.util.AllUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.app.base.share.qr.QrCodeUtils;
import common.app.mall.util.ToastUtil;



public class TransferEthDetailActivity extends BaseActivity {

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
    private TransactionNewEthRecordBean mBean;
    private final String BNBWEB="https://bscscan.com/";
    private final String ETHWEB="https://cn.etherscan.com/";
    private final String HECOWEB="https://hecoinfo.com/";
    private String url="";

    @Override
    public int initContentView() {
        mBean = (TransactionNewEthRecordBean) getIntent().getSerializableExtra("detail");
        if (mBean == null) {
            finish();
        }
        return R.layout.activity_transfereth_detail;
    }

    @Override
    public void initUiAndListener() {
        WalletEntity walletEntity = walletDBUtil.getWalletInfo();
        List<AssertBean> list = WalletDBUtil.getInstent(this).getMustWallet(walletEntity.getType());
        String mustCoin = list.get(0).getShort_name();

        ButterKnife.bind(this);
        fakuan.setText(mBean.getFromAllAddress());
        shoukuan.setText(mBean.getToAllAddress());
        minerCosts.setText(mBean.getSpend_eth()+" "+mustCoin);
        remarks.setText(mBean.getRemarks()+"");
        asseteName.setText(mBean.getCoin_name()+"");
        jiaoyihao.setText(mBean.getHash()+"");
        ethfeiyong.setText(mBean.getValueDecimal()+"");
        qukuai.setText(mBean.getBlockNumber()+"");
        if (walletDBUtil.getWalletInfo().getType()!= WalletUtil.ETH_COIN) {
            line1Url.setVisibility(View.GONE);
        }
        jiaoyishijian.setText(AllUtils.times(mBean.getTimestamp()+""));
        if(walletEntity.getType()== WalletUtil.BNB_COIN){
            url=BNBWEB;
        }else if(walletEntity.getType()== WalletUtil.ETH_COIN){
            url=ETHWEB;
        }else if(walletEntity.getType()== WalletUtil.HT_COIN){
            url=HECOWEB;
        } else{
            line1Url.setVisibility(View.GONE);
        }
        imgqrcode.setImageBitmap(QrCodeUtils.createInstance(this).getQrCode(url+"tx/"+mBean.getHash()));
        if (TextUtils.isEmpty(mBean.Status)) {
            imgHeadLogo.setImageResource(R.mipmap.jinxingzhong);
        }else if ("0".equals(mBean.Status)) {
            imgHeadLogo.setImageResource(R.mipmap.xiangqing_chahao);
        }
    }

    @Override
    public void initData() {

    }
    ClipboardManager cm;
    @OnClick({R2.id.img_back, R2.id.copyurl,R2.id.fakuan,R2.id.shoukuan})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.img_back) {
            finish();
        } else if (i == R.id.copyurl) {
            cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            
            cm.setText(url+"tx/"+mBean.getHash());
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
}
