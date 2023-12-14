

package com.wallet.ctc.ui.blockchain.transferdetail;

import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.model.blockchain.TransactionXrpRecordBean;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.app.base.share.qr.QrCodeUtils;
import common.app.mall.util.ToastUtil;



public class TransferXrpDetailActivity extends BaseActivity {

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
    @BindView(R2.id.error)
    TextView error;
    @BindView(R2.id.imgqrcode)
    ImageView imgqrcode;
    @BindView(R2.id.copyurl)
    TextView copyurl;
    @BindView(R2.id.assete_name)
    TextView asseteName;
    @BindView(R2.id.line1_url)
    LinearLayout line1Url;
    private TransactionXrpRecordBean mBean;

    @Override
    public int initContentView() {
        mBean = (TransactionXrpRecordBean) getIntent().getSerializableExtra("detail");
        if (mBean == null) {
            finish();
        }
        return R.layout.activity_transfer_xrp_detail;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        fakuan.setText(mBean.getFrom_account());
        shoukuan.setText(mBean.getTo_account());
        minerCosts.setText(mBean.getFee()+" xrp");
        remarks.setText(mBean.getTag()+"");
        asseteName.setText(mBean.getCoin_name()+"");
        jiaoyihao.setText(mBean.getHash()+"");
        ethfeiyong.setText(mBean.getAmount()+"");
        qukuai.setText(mBean.getSequence()+"");
        if (mBean.getCoin_name().equals("BTC") || mBean.getCoin_name().equals("LTC") || mBean.getCoin_name().equals("DOGE")) {
            line1Url.setVisibility(View.GONE);
        }
        jiaoyishijian.setText(mBean.getTx_time());

            


            
        imgqrcode.setImageBitmap(QrCodeUtils.createInstance(this).getQrCode("https://livenet.xrpl.org/transactions/"+mBean.getHash()));


        if(mBean.isIs_success()){
            
            error.setVisibility(View.VISIBLE);
            imgHeadLogo.setImageResource(R.mipmap.xiangqing_duihao);
            error.setText(getString(R.string.transfer_success));
            error.setBackgroundResource(R.drawable.lin_green_bg);
        } else {
            
            error.setVisibility(View.VISIBLE);
            imgHeadLogo.setImageResource(R.mipmap.xiangqing_chahao);
            error.setText(getString(R.string.transfer_error) + mBean.getTx_result());
            error.setBackgroundResource(R.drawable.lin_red_f64a45_bg);
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
            
            cm.setText("https://livenet.xrpl.org/transactions/"+mBean.getHash());
            ToastUtil.showToast(getString(R.string.copy_success));

        } else if (i == R.id.fakuan) {
            cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            
            cm.setText(mBean.getFrom_account());
            ToastUtil.showToast(getString(R.string.copy_success));

        } else if (i == R.id.shoukuan) {
            cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            
            cm.setText(mBean.getTo_account());
            ToastUtil.showToast(getString(R.string.copy_success));

        } else {
        }
    }
}
