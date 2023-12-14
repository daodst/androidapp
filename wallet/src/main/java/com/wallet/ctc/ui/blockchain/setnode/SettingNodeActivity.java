

package com.wallet.ctc.ui.blockchain.setnode;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.SettingNodeEntity;
import com.wallet.ctc.ui.blockchain.addnode.AddNodeActivity;
import com.wallet.ctc.util.WalletSpUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import common.app.utils.SpUtil;


public class SettingNodeActivity extends BaseActivity {

    @BindView(R2.id.tv_back)
    TextView tvBack;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.choose_node_list)
    ListView chooseNodeList;
    private SettingNodeAdapter mAdapter;
    private List<SettingNodeEntity> mData=new ArrayList<>();
    private final int CODE=1001;

    @Override
    public int initContentView() {
        return R.layout.activity_settingnode;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        tvBack.setOnClickListener(v -> {
            finish();
        });
        tvTitle.setText(getString(R.string.node_settings));
        mAdapter=new SettingNodeAdapter(this);
        mAdapter.bindData(mData);
        chooseNodeList.setAdapter(mAdapter);
        chooseNodeList.setOnItemClickListener((parent, view, position, id) -> {
            SettingNodeEntity mBean=mData.get(position);
            Intent intent=new Intent(SettingNodeActivity.this, AddNodeActivity.class);
            intent.putExtra("name",mBean.getNodeName());
            intent.putExtra("type",mBean.getType());
            startActivityForResult(intent,CODE);
        });
    }

    @Override
    public void initData() {
        showData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            showData();
        }
    }

    
    private void showData() {
        mData.clear();
        mData.addAll(getNodesList());
        mAdapter.notifyDataSetChanged();
    }

    private List<SettingNodeEntity> getNodesList() {
        List<SettingNodeEntity> nodes = new ArrayList<>();

        List<Integer> coinsList = WalletSpUtil.getEnableCoinTypeList();
        if (null == coinsList || coinsList.size() == 0) {
            return nodes;
        }
        for (Integer coinType : coinsList) {
            if (coinType == WalletUtil.MCC_COIN) {
                
                continue;
            }
            String nowRpcUrl = SpUtil.getDefNode(coinType);
            if (!TextUtils.isEmpty(nowRpcUrl)) {
                String coinName = WalletDBUtil.getInstent(getApplicationContext()).getWalletName(coinType);
                nodes.add(new SettingNodeEntity(coinName, nowRpcUrl, coinType, false, 0));
            }
        }
        return nodes;
    }
}
