

package com.wallet.ctc.nft.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.db.NftBean;
import com.wallet.ctc.nft.bean.MetadataBean;
import com.wallet.ctc.util.FastClickUtils;
import com.wallet.ctc.view.TitleBarView;

import butterknife.BindView;
import butterknife.OnClick;
import common.app.BuildConfig;
import common.app.base.BaseActivity;

public class NftAssetsDetailActivity extends BaseActivity<NftAssetsDetailBiz> {

    @BindView(R2.id.title_bar)
    TitleBarView titleBar;
    @BindView(R2.id.btn_send)
    Button btnSend;
    @BindView(R2.id.iv_img)
    ImageView ivImg;
    @BindView(R2.id.tv_content)
    TextView tvContent;
    
    private NftBean nftBean = null;
    private MetadataBean metadataBean = null;
    private Gson gson = new Gson();

    public static void intent(Context context, NftBean data) {
        Intent in = new Intent(context, NftAssetsDetailActivity.class);
        in.putExtra("data", data);
        context.startActivity(in);
    }

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_nft_assets_detail;
    }

    @Override
    public void initView(@Nullable View view) {
        super.initView(view);
        titleBar.setOnTitleBarClickListener(new TitleBarView.TitleBarClickListener() {
            @Override
            public void leftClick() {
                finish();
            }

            @Override
            public void rightClick() {

            }
        });
        nftBean = (NftBean) getIntent().getSerializableExtra("data");

        
        if (!TextUtils.isEmpty(nftBean.metadata)) {
            metadataBean = gson.fromJson(nftBean.metadata, MetadataBean.class);
        } else if (!TextUtils.isEmpty(nftBean.token_uri)) {
            if(nftBean.token_uri.startsWith("http://") || nftBean.token_uri.startsWith("https://")){
                getViewModel().getMetadata(nftBean.token_uri);
            }else{
                
            }

        }
        showInfo();
        register();
    }

    private void register() {
        getViewModel().getMetadataLiveData.observe(this, new Observer<MetadataBean>() {
            @Override
            public void onChanged(MetadataBean data) {
                metadataBean = data;
                showInfo();
            }
        });
    }

    private void showInfo() {
        tvContent.setText("#" + nftBean.token_id);
        String imgUrl = null == metadataBean ? "" : metadataBean.image;

        if (!TextUtils.isEmpty(imgUrl)) {
            if (imgUrl.startsWith("http")) {
                Glide.with(this).load(imgUrl).into(ivImg);
            } else if (imgUrl.startsWith("ipfs")) {
                
                
                imgUrl = imgUrl.substring("ipfs://".length(), imgUrl.length());
                
                if (!imgUrl.startsWith("ipfs")) {
                    imgUrl = "ipfs/" + imgUrl;
                }
                imgUrl = BuildConfig.IPFS_URL + imgUrl;
                Glide.with(this).load(imgUrl).into(ivImg);
            }
        }
    }

    @OnClick(R2.id.btn_send)
    public void onClick() {
        if (FastClickUtils.isFastClick()) {
            return;
        }
        Intent intent = new Intent(this, TransferNFTActivity.class);
        intent.putExtra("tokenId", nftBean.getToken_id());
        intent.putExtra("tokenType", nftBean.getToken_address());
        intent.putExtra("toAddress", "");
        intent.putExtra("tokenName", nftBean.name);
        intent.putExtra("contractType", nftBean.getContract_type());
        startActivity(intent);
    }
}
