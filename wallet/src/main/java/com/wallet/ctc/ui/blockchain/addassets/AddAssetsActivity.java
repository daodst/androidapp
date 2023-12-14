

package com.wallet.ctc.ui.blockchain.addassets;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.ui.blockchain.seach.SeachActivity;
import com.wallet.ctc.util.SettingPrefUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;



public class AddAssetsActivity extends BaseActivity {

    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.img_action)
    ImageView imgAction;
    @BindView(R2.id.assets_list)
    ListView assetsList;
    @BindView(R2.id.nodata)
    LinearLayout nodata;
    private AssetsAdapter mAdapter;
    private List<AssertBean> chooseList=new ArrayList<>();
    private List<AssertBean> canAdd=new ArrayList<>();
    @Override
    public int initContentView() {
        return R.layout.activity_addassets;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        mAdapter=new AssetsAdapter(this);
        assetsList.setAdapter(mAdapter);
        tvTitle.setText(getString(R.string.add_asset));
        imgAction.setVisibility(View.VISIBLE);
        imgAction.setImageResource(R.mipmap.seach);

    }

    @Override
    protected void onResume() {
        super.onResume();
        canAdd=walletDBUtil.getMustWallet();
        canAdd.addAll(walletDBUtil.canChooseWallet(0));
        chooseList=walletDBUtil.getAssetsByWalletType(SettingPrefUtil.getWalletAddress(this),0);
        chooseList.addAll(walletDBUtil.getMustWallet());
        mAdapter.bindData(canAdd,chooseList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void initData() {

    }


    @OnClick({R2.id.tv_back, R2.id.img_action})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.tv_back) {
            finish();

        } else if (i == R.id.img_action) {
            Intent intent = new Intent(this, SeachActivity.class);
            startActivity(intent);

        } else {
        }
    }
}
