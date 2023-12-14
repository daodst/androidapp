

package com.wallet.ctc.ui.blockchain.transferdetail;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.model.blockchain.TrxTransferHistoryBean;
import com.wallet.ctc.model.blockchain.TrxTrc20TransferHistoryBean;
import com.wallet.ctc.util.AllUtils;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.app.base.share.qr.QrCodeUtils;
import common.app.base.them.Eyes;
import common.app.mall.util.ToastUtil;



public class TransferTrxDetailActivity extends BaseActivity {

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

    @BindView(R2.id.title_bar)
    RelativeLayout title_bar;


    @BindView(R2.id.imgqrcode)
    ImageView imgqrcode;
    @BindView(R2.id.copyurl)
    TextView copyurl;
    @BindView(R2.id.assete_name)
    TextView asseteName;
    @BindView(R2.id.line1_url)
    LinearLayout line1Url;
    private TrxTransferHistoryBean mBean;
    private TrxTrc20TransferHistoryBean mTrxBean;
    private String type;
    private String from;
    private String to;
    private String hashUrl;
    private BigDecimal decimal;
    private Gson gson=new Gson();


    @Override
    public int initContentView() {
        return R.layout.activity_transfereth_detail;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        if(Build.VERSION.SDK_INT !=Build.VERSION_CODES.LOLLIPOP_MR1) {
            Eyes.setTranslucent(this);
            title_bar.setPadding(0,Eyes.getStatusBarHeight(this),0,0);
        }
        type=getIntent().getStringExtra("type");
        if (type == null) {
            finish();
            return;
        }
        String data=getIntent().getStringExtra("detail");
        if(type.toUpperCase().equals("TRX")){
            mBean =gson.fromJson(data,TrxTransferHistoryBean.class);
            if (mBean == null) {
                finish();
                return;
            }
            decimal=new BigDecimal(Math.pow(10, 6));
            TrxTransferHistoryBean.RawDataBean.ContractBean.ParameterBean.ValueBean value=mBean.getRaw_data().getContract().get(0).getParameter().getValue();
            BigDecimal amount=value.getAmount().divide(decimal,2,BigDecimal.ROUND_HALF_UP);
            BigDecimal fee=mBean.getRet().get(0).getFee().divide(decimal,2,BigDecimal.ROUND_HALF_UP);
            from=value.getOwner_address();
            to=value.getTo_address();
            hashUrl="https://tronscan.io/#/transaction/"+mBean.getTxID();
            fakuan.setText(from);
            shoukuan.setText(to);
            minerCosts.setText(fee+" TRX");
            remarks.setVisibility(View.GONE);
            ethfeiyong.setText(amount.toPlainString());
            asseteName.setText("TRX");
            jiaoyihao.setText(mBean.getTxID());
            qukuai.setText(mBean.getBlockNumber().toPlainString());
            jiaoyishijian.setText(AllUtils.getTimeJavaNYR(mBean.getRaw_data().getTimestamp()));
            imgqrcode.setImageBitmap(QrCodeUtils.createInstance(this).getQrCode(hashUrl));
        }else {
            mTrxBean =gson.fromJson(data,TrxTrc20TransferHistoryBean.class);
            if (mTrxBean == null) {
                finish();
                return;
            }
            decimal=new BigDecimal(Math.pow(10, mTrxBean.getToken_info().getDecimals()));
            BigDecimal amount=mTrxBean.getValue().divide(decimal,2,BigDecimal.ROUND_HALF_UP);
            from=mTrxBean.getFrom();
            to=mTrxBean.getTo();
            hashUrl="https://tronscan.io/#/transaction/"+mTrxBean.getTransaction_id();
            fakuan.setText(from);
            shoukuan.setText(to);
            minerCosts.setVisibility(View.GONE);
            remarks.setVisibility(View.GONE);
            ethfeiyong.setText(amount.toPlainString());
            asseteName.setText(mTrxBean.getToken_info().getSymbol());
            jiaoyihao.setText(mTrxBean.getTransaction_id());
            qukuai.setText("");
            jiaoyishijian.setText(AllUtils.getTimeJavaNYR(mTrxBean.getBlock_timestamp()));
            imgqrcode.setImageBitmap(QrCodeUtils.createInstance(this).getQrCode(hashUrl));
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
            
            cm.setText(hashUrl);
            ToastUtil.showToast(getString(R.string.copy_success));

        } else if (i == R.id.fakuan) {
            cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            
            cm.setText(from);
            ToastUtil.showToast(getString(R.string.copy_success));

        } else if (i == R.id.shoukuan) {
            cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            
            cm.setText(to);
            ToastUtil.showToast(getString(R.string.copy_success));

        } else {
        }
    }
}
