

package com.wallet.ctc.ui.blockchain.transferdetail;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
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
import com.wallet.ctc.model.blockchain.SgbTransHistory;
import com.wallet.ctc.ui.me.about.StringUtils;
import com.wallet.ctc.util.AllUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.app.base.share.qr.QrCodeUtils;
import common.app.base.them.Eyes;
import common.app.mall.util.ToastUtil;



public class TransferSgbDetailActivity extends BaseActivity {

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
    private SgbTransHistory mBean;
    private final String SGBWEB="https://www.subgamescan.io/";

    @Override
    public int initContentView() {

        return R.layout.activity_transfereth_detail;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP_MR1){
            Eyes.setTranslucent(this);
            findViewById(R.id.title_bar).setPadding(0, Eyes.getStatusBarHeight(this),0,0);
        }
        WalletEntity walletEntity = walletDBUtil.getWalletInfo();
        List<AssertBean> list = WalletDBUtil.getInstent(this).getMustWallet(walletEntity.getType());
        String mustCoin = list.get(0).getShort_name();
        int decimal=list.get(0).getDecimal();
        mBean =  new Gson().fromJson(getIntent().getStringExtra("detail"),new TypeToken<SgbTransHistory>(){}.getType());
     
        if (mBean == null) {
            finish();
            return;
        }
        fakuan.setText(mBean.getFrom());
        shoukuan.setText(mBean.getTo());
        if(StringUtils.isEmpty(mBean.getFee())){
            mBean.setFee("0");
        }
        minerCosts.setText(mBean.getFee()+" "+mustCoin);
        remarks.setText("");
        ethfeiyong.setText(mBean.getAmount());
        asseteName.setText(mustCoin);
        jiaoyihao.setText(mBean.getHash());
        qukuai.setText(mBean.getBlock_num()+"");
        jiaoyishijian.setText(AllUtils.times(mBean.getBlock_timestamp()+""));
        if (mBean.getSuccess()) {
            imgHeadLogo.setImageResource(R.mipmap.xiangqing_duihao);
        } else {
            imgHeadLogo.setImageResource(R.mipmap.jinxingzhong);
        }
        imgqrcode.setImageBitmap(QrCodeUtils.createInstance(this).getQrCode(SGBWEB+"#/extrinsicsdetail?hash="+mBean.getHash()));
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
            cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            
            cm.setText(SGBWEB+"#/extrinsicsdetail?hash="+mBean.getHash());
            ToastUtil.showToast(getString(R.string.copy_success));

        } else if (i == R.id.fakuan) {
            cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            
            cm.setText(mBean.getFrom());
            ToastUtil.showToast(getString(R.string.copy_success));

        } else if (i == R.id.shoukuan) {
            cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            
            cm.setText(mBean.getTo());
            ToastUtil.showToast(getString(R.string.copy_success));

        } else {
        }
    }
}
