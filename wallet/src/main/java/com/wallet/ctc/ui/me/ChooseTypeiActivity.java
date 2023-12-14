

package com.wallet.ctc.ui.me;

import static com.wallet.ctc.crypto.WalletUtil.BNB_COIN;
import static com.wallet.ctc.crypto.WalletUtil.DMF_BA_COIN;
import static com.wallet.ctc.crypto.WalletUtil.DMF_COIN;
import static com.wallet.ctc.crypto.WalletUtil.DM_COIN;
import static com.wallet.ctc.crypto.WalletUtil.ETF_COIN;
import static com.wallet.ctc.crypto.WalletUtil.ETH_COIN;
import static com.wallet.ctc.crypto.WalletUtil.HT_COIN;
import static com.wallet.ctc.crypto.WalletUtil.MCC_COIN;
import static com.wallet.ctc.crypto.WalletUtil.OTHER_COIN;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.WalletLogoBean;
import com.wallet.ctc.ui.blockchain.blockchainlogin.BlockchainLoginActivity;
import com.wallet.ctc.ui.blockchain.creattoken.CreatEthTokenActivity;
import com.wallet.ctc.ui.blockchain.creattoken.CreatTokenHistoryActivity;
import com.wallet.ctc.ui.blockchain.creattoken.CreatTokenOne;
import com.wallet.ctc.ui.blockchain.managewallet.WalletTypeAdapter;
import com.wallet.ctc.util.WalletSpUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class ChooseTypeiActivity extends BaseActivity {

    protected Unbinder mUnbinder;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.tv_back)
    TextView tvBack;
    @BindView(R2.id.tv_action)
    TextView tvAction;
    @BindView(R2.id.wallet_list)
    ListView listview;
    private Intent intent;
    private List<WalletLogoBean> list = new ArrayList<>();
    private WalletTypeAdapter mAdapter;

    @Override
    public int initContentView() {
        return R.layout.fragment_liulanqi;
    }

    @Override
    public void initUiAndListener() {
        mUnbinder = ButterKnife.bind(this);
        tvAction.setText(getString(R.string.release_record));
        tvAction.setVisibility(View.VISIBLE);
        tvTitle.setText(getString(R.string.release_token));
        mAdapter = new WalletTypeAdapter(this);
        mAdapter.bindData(list);
        listview.setAdapter(mAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int type=list.get(position).getWalletType();
                List<WalletEntity> wallets=walletDBUtil.getWalletList(type);
                if(wallets==null||wallets.size()<1){
                        intent = new Intent(ChooseTypeiActivity.this, BlockchainLoginActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
                if(type==MCC_COIN||type==DM_COIN||type==OTHER_COIN){
                    intent = new Intent(ChooseTypeiActivity.this, CreatTokenOne.class);
                    intent.putExtra("type", type);
                    startActivity(intent);
                }else {
                    intent = new Intent(ChooseTypeiActivity.this, CreatEthTokenActivity.class);
                    intent.putExtra("type",type);
                    startActivity(intent);
                }
            }
        });
        if (WalletSpUtil.getEnableMcc() == 1) {
            list.add(new WalletLogoBean(R.mipmap.mcc_logo, getString(R.string.default_token_name).toUpperCase() + getString(R.string.release), MCC_COIN, BuildConfig.BLOCK_BROWSER));
        }
        if (WalletSpUtil.getEnableDm() == 1) {
            list.add(new WalletLogoBean(R.mipmap.dm_logo, "DM" + " " + getString(R.string.release), DM_COIN,"http://dm.mobi/dmblock_pc.php?device=app"));
        }
        if (WalletSpUtil.getEnableOther() == 1) {
            list.add(new WalletLogoBean(R.mipmap.llq_other, getString(R.string.default_other_token_name).toUpperCase() + getString(R.string.release), OTHER_COIN,""));
        }
        if (WalletSpUtil.getEnableEth() == 1) {
            list.add(new WalletLogoBean(R.mipmap.eth_logo, "ETH" + getString(R.string.release), ETH_COIN, "https://cn.etherscan.com/"));
        }
        if (WalletSpUtil.getEnableEtf() == 1) {
            list.add(new WalletLogoBean(R.mipmap.etf_logo, getString(R.string.default_etf).toUpperCase() + getString(R.string.release), ETF_COIN, ""));
        }
        if (WalletSpUtil.getEnableDmf() == 1) {
            list.add(new WalletLogoBean(R.mipmap.hb_dmf_logo, getString(R.string.default_dmf_hb).toUpperCase() + getString(R.string.release),  DMF_COIN,""));
        }
        if (WalletSpUtil.getEnableDmfBa() == 1) {
            list.add(new WalletLogoBean(R.mipmap.bian_dmf_logo, getString(R.string.default_dmf_ba).toUpperCase() + getString(R.string.release), DMF_BA_COIN,""));
        }
        if (WalletSpUtil.getEnableHt() == 1) {
            list.add(new WalletLogoBean(R.mipmap.huobi_logo, "HT" + getString(R.string.release), HT_COIN,""));
        }

        if (WalletSpUtil.getEnableBnb() == 1) {
            list.add(new WalletLogoBean(R.mipmap.bnb_logo, "BNB" + getString(R.string.release),  BNB_COIN,""));
        }
    }

    @Override
    public void initData() {

    }

    @OnClick({R2.id.tv_back,R2.id.tv_action})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.tv_back) {
            finish();
            return;
        }else if (i == R.id.tv_action) {
            intent = new Intent(this, CreatTokenHistoryActivity.class);
            startActivity(intent);
            return;
        }
    }
}
