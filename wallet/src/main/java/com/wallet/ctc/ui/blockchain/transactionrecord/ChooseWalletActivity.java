

package com.wallet.ctc.ui.blockchain.transactionrecord;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.db.WalletEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;



public class ChooseWalletActivity extends BaseActivity {

    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.wallet_list_menu)
    ListView walletListMenu;
    private List<WalletEntity> mWallName;
    private String address;
    private ChooseWalletListAdapter mAdapter;

    @Override
    public int initContentView() {
        return R.layout.activity_choose_wallet;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        mAdapter=new ChooseWalletListAdapter(this);
        tvTitle.setText(getString(R.string.switch_wallet));
        mWallName=walletDBUtil.getWallName();
        address=getIntent().getStringExtra("address");
        walletListMenu.setAdapter(mAdapter);
        mAdapter.bindData(mWallName,address);
        mAdapter.notifyDataSetChanged();
        walletListMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(ChooseWalletActivity.this,TransactionRecordActivity.class);
                intent.putExtra("address",mWallName.get(position).getAllAddress());
                setResult(RESULT_OK,intent);
                finish();
            }
        });



    }

    @Override
    public void initData() {

    }

    @OnClick(R2.id.tv_back)
    public void onViewClicked() {
       finish();
    }
}
