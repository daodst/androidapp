

package com.wallet.ctc.ui.blockchain.addressbook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.db.AddressBookEntity;
import com.wallet.ctc.db.DBManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.app.ui.view.PullToRefreshLayout;

;



public class AddressBookActivity extends BaseActivity {
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.img_action)
    ImageView tvAction;
    @BindView(R2.id.content_view)
    ListView assetsList;
    @BindView(R2.id.nodata)
    LinearLayout nodata;
    @BindView(R2.id.refresh_view)
    PullToRefreshLayout pullView;
    private AddressBookAdapter mAdapter;
    private List<AddressBookEntity> list = new ArrayList<>();
    private int from=0;
    private Intent intent;

    public static final String KEY_DATA = "toAddress";

    
    public static Intent getAddrIntent(Context from) {
        Intent intent = new Intent(from, AddressBookActivity.class);
        intent.putExtra("type", 1);
        return intent;
    }

    @Override
    public int initContentView() {
        from=getIntent().getIntExtra("type",0);
        return R.layout.activity_notice;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        tvTitle.setText(getString(R.string.addressbook));
        tvAction.setVisibility(View.VISIBLE);
        tvAction.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.add_huise));
        mAdapter = new AddressBookAdapter(this);

        pullView.releaseNotPull();
        assetsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(from==1){
                    intent = new Intent();
                    intent.putExtra(KEY_DATA, list.get(position).getAddress());
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }else {
                    intent = new Intent(AddressBookActivity.this,AddressBookAddActivity.class);
                    intent.putExtra("type",1);
                    intent.putExtra("data", new Gson().toJson(list.get(position)));
                    startActivity(intent);
                }
            }
        });
        mAdapter.bindData(list);
        assetsList.setAdapter(mAdapter);
    }

    @Override
    public void initData() {


    }

    @Override
    protected void onResume() {
        super.onResume();
        list= DBManager.getInstance(this).queryAdressBook();
        if(null!=list&&list.size()>0){
            pullView.setVisibility(View.VISIBLE);
            nodata.setVisibility(View.GONE);
        }else {
            pullView.setVisibility(View.GONE);
            nodata.setVisibility(View.VISIBLE);
        }
        mAdapter.bindData(list);
        mAdapter.notifyDataSetChanged();
    }

    @OnClick({R2.id.tv_back,R2.id.img_action})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.tv_back) {
            finish();

        } else if (i == R.id.img_action) {
            intent = new Intent(AddressBookActivity.this, AddressBookAddActivity.class);
            startActivity(intent);

        } else {
        }
    }

}
