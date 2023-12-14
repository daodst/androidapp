

package com.wallet.ctc.ui.me.setting;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.api.me.MeApi;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.db.WalletEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;



public class SettingDefWalletTypeActivity extends BaseActivity{
    private int type;
    @BindView(R2.id.wallet_list)
    ListView walletList;
    @BindView(R2.id.tv_back)
    TextView tvBack;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.img_action)
    ImageView imgAction;
    @BindView(R2.id.nodata)
    View nodata;
    private SettingDefWalletAdapter mAdapter;
    private List<WalletEntity> mWallName = new ArrayList<>();
    private Gson gson=new Gson();
    private MeApi mApi=new MeApi();
    private String address;

    @Override
    public int initContentView() {
        type=getIntent().getIntExtra("type",-1);
        address=getIntent().getStringExtra("address");
        return R.layout.activity_new_manage_wallet;
    }

    @Override
    public void initUiAndListener() {
        tvTitle.setText(getString(R.string.select_wallet));
        mAdapter = new SettingDefWalletAdapter(this);

        if(getIntent().getIntExtra("from",0)==-1) {
            mWallName = walletDBUtil.getWallName();
        }else {
            mWallName = walletDBUtil.getWalletList(type);
        }
        mAdapter.bindData(mWallName,address,type);
        walletList.setAdapter(mAdapter);
        if(null==mWallName||mWallName.size()<1){
            nodata.setVisibility(View.VISIBLE);
            walletList.setVisibility(View.GONE);
        }
        walletList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent();
                intent.putExtra("type",mWallName.get(position).getType());
                intent.putExtra("address",mWallName.get(position).getAllAddress());
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        tvBack.setOnClickListener(v->{
            finish();
        });
    }

    @Override
    public void initData() {

    }
}
