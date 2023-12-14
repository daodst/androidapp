

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
import com.wallet.ctc.model.blockchain.TransactionBtcRecordBean;
import com.wallet.ctc.util.AllUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.app.mall.util.ToastUtil;



public class TransferBtcDetailActivity extends BaseActivity {

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
    private TransactionBtcRecordBean mBean;

    @Override
    public int initContentView() {
        return R.layout.activity_transfereth_detail;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        mBean = (TransactionBtcRecordBean) getIntent().getSerializableExtra("detail");
        if (mBean == null) {
            finish();
            return;
        }
        fakuan.setText(mBean.getFrom());
        shoukuan.setText(mBean.getTo());
        minerCosts.setText(mBean.getFee()+" BTC");
        remarks.setText(mBean.getMemo());
        ethfeiyong.setText(mBean.getValue().toPlainString());
        asseteName.setText(mBean.getCoin_name());
        jiaoyihao.setText(mBean.getTxhash());
        qukuai.setText(mBean.getBlockheight()+"");
        line1Url.setVisibility(View.GONE);
        jiaoyishijian.setText(AllUtils.times(mBean.getBlocktime().intValue()+""));

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
