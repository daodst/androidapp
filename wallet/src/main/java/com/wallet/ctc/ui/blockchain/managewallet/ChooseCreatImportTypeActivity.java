

package com.wallet.ctc.ui.blockchain.managewallet;

import static com.wallet.ctc.crypto.WalletUtil.ADA_COIN;
import static com.wallet.ctc.crypto.WalletUtil.BCH_COIN;
import static com.wallet.ctc.crypto.WalletUtil.BNB_COIN;
import static com.wallet.ctc.crypto.WalletUtil.BTC_COIN;
import static com.wallet.ctc.crypto.WalletUtil.DMF_BA_COIN;
import static com.wallet.ctc.crypto.WalletUtil.DMF_COIN;
import static com.wallet.ctc.crypto.WalletUtil.DM_COIN;
import static com.wallet.ctc.crypto.WalletUtil.DOGE_COIN;
import static com.wallet.ctc.crypto.WalletUtil.DOT_COIN;
import static com.wallet.ctc.crypto.WalletUtil.EOS_COIN;
import static com.wallet.ctc.crypto.WalletUtil.ETC_COIN;
import static com.wallet.ctc.crypto.WalletUtil.ETF_COIN;
import static com.wallet.ctc.crypto.WalletUtil.ETH_COIN;
import static com.wallet.ctc.crypto.WalletUtil.FIL_COIN;
import static com.wallet.ctc.crypto.WalletUtil.HT_COIN;
import static com.wallet.ctc.crypto.WalletUtil.LTC_COIN;
import static com.wallet.ctc.crypto.WalletUtil.MATIC_COIN;
import static com.wallet.ctc.crypto.WalletUtil.MCC_COIN;
import static com.wallet.ctc.crypto.WalletUtil.OTHER_COIN;
import static com.wallet.ctc.crypto.WalletUtil.SGB_COIN;
import static com.wallet.ctc.crypto.WalletUtil.SOL_COIN;
import static com.wallet.ctc.crypto.WalletUtil.TRX_COIN;
import static com.wallet.ctc.crypto.WalletUtil.XRP_COIN;
import static com.wallet.ctc.crypto.WalletUtil.ZEC_COIN;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.model.blockchain.WalletLogoBean;
import com.wallet.ctc.ui.blockchain.creatwallet.CreatWalletActivity;
import com.wallet.ctc.ui.blockchain.importwallet.ImportWalletActivity;
import com.wallet.ctc.util.WalletSpUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class ChooseCreatImportTypeActivity extends BaseActivity {
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.wallet_list)
    ListView listview;
    private Intent intent;
    private int from;
    private List<WalletLogoBean> list = new ArrayList<>();
    private WalletTypeAdapter mAdapter;

    @Override
    public int initContentView() {
        return R.layout.activity_choose_creatimport_new;
    }

    @Override
    public void initUiAndListener() {
        from = getIntent().getIntExtra("from", 0);
        tvTitle.setText(getString(R.string.select_wallet_type));
        findViewById(R.id.tv_back).setOnClickListener(v -> {
            finish();
        });
        mAdapter = new WalletTypeAdapter(this);
        mAdapter.bindData(list);
        listview.setAdapter(mAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (from == 0) {
                    intent = new Intent(ChooseCreatImportTypeActivity.this, CreatWalletActivity.class);
                } else {
                    intent = new Intent(ChooseCreatImportTypeActivity.this, ImportWalletActivity.class);
                }
                intent.putExtra("type", list.get(position).getWalletType());
                startActivity(intent);
            }
        });
    }

    @Override
    public void initData() {
        if (WalletSpUtil.getEnableMcc() == 1) {
            list.add(new WalletLogoBean(R.mipmap.tt_logo, getString(R.string.default_token_name2).toUpperCase() + getString(R.string.wallet), 0, MCC_COIN, 0));
        }
        if (WalletSpUtil.getEnableDm() == 1) {
            list.add(new WalletLogoBean(R.mipmap.dm_logo, "DM" + " " + getString(R.string.wallet), 0, DM_COIN, 0));
        }
        if (WalletSpUtil.getEnableEth() == 1) {
            list.add(new WalletLogoBean(R.mipmap.eth_logo, "ETH" + getString(R.string.wallet), 0, ETH_COIN, 0));
        }
        if (WalletSpUtil.getEnableBtc() == 1) {
            list.add(new WalletLogoBean(R.mipmap.btc_logo, "BTC" + getString(R.string.wallet), 0, BTC_COIN, 0));
        }
        if (WalletSpUtil.getEnableEos() == 1) {
            list.add(new WalletLogoBean(R.mipmap.eos_logo, "EOS" + getString(R.string.wallet), 0, EOS_COIN, 0));
        }
        if (WalletSpUtil.getEnableOther() == 1) {
            list.add(new WalletLogoBean(R.mipmap.llq_other, getString(R.string.default_other_token_name).toUpperCase() + getString(R.string.wallet), 0, OTHER_COIN, 0));
        }
        if (WalletSpUtil.getEnableXrp() == 1) {
            list.add(new WalletLogoBean(R.mipmap.xrp_logo, "XRP" + getString(R.string.wallet), 0, XRP_COIN, 0));
        }
        if (WalletSpUtil.getEnableTrx() == 1) {
            list.add(new WalletLogoBean(R.mipmap.trx_logo, "TRX" + getString(R.string.wallet), 0, TRX_COIN, 0));
        }
        if (WalletSpUtil.getEnableEtf() == 1) {
            list.add(new WalletLogoBean(R.mipmap.etf_logo, getString(R.string.default_etf).toUpperCase() + getString(R.string.wallet), 0, ETF_COIN, 0));
        }
        if (WalletSpUtil.getEnableDmf() == 1) {
            list.add(new WalletLogoBean(R.mipmap.hb_dmf_logo, getString(R.string.default_dmf_hb).toUpperCase() + getString(R.string.wallet), 0, DMF_COIN, 0));
        }
        if (WalletSpUtil.getEnableDmfBa() == 1) {
            list.add(new WalletLogoBean(R.mipmap.bian_dmf_logo, getString(R.string.default_dmf_ba).toUpperCase() + getString(R.string.wallet), 0, DMF_BA_COIN, 0));
        }
        if (WalletSpUtil.getEnableHt() == 1) {
            list.add(new WalletLogoBean(R.mipmap.huobi_logo, "HECO" + getString(R.string.wallet), 0, HT_COIN, 0));
        }

        if (WalletSpUtil.getEnableBnb() == 1) {
            list.add(new WalletLogoBean(R.mipmap.bnb_logo, "BSC" + getString(R.string.wallet), 0, BNB_COIN, 0));
        }
        
        if (WalletSpUtil.getEnableFIL() == 1) {
            list.add(new WalletLogoBean(R.mipmap.fil_logo, "FIL" + getString(R.string.wallet), 0, FIL_COIN, 0));
        }
        if (WalletSpUtil.getEnableDOGE() == 1) {
            list.add(new WalletLogoBean(R.mipmap.doge_logo, "DOGE" + getString(R.string.wallet), 0, DOGE_COIN, 0));
        }
        if (WalletSpUtil.getEnableDOT() == 1) {
            list.add(new WalletLogoBean(R.mipmap.dot_logo, "DOT" + getString(R.string.wallet), 0, DOT_COIN, 0));
        }
        if (WalletSpUtil.getEnableLTC() == 1) {
            list.add(new WalletLogoBean(R.mipmap.ltc_logo, "LTC" + getString(R.string.wallet), 0, LTC_COIN, 0));
        }
        if (WalletSpUtil.getEnableBCH() == 1) {
            list.add(new WalletLogoBean(R.mipmap.bch_logo, "BCH" + getString(R.string.wallet), 0, BCH_COIN, 0));
        }
        if (WalletSpUtil.getEnableZEC() == 1) {
            list.add(new WalletLogoBean(R.mipmap.zec_logo, "ZEC" + getString(R.string.wallet), 0, ZEC_COIN, 0));
        }
        if (WalletSpUtil.getEnableADA() == 1) {
            list.add(new WalletLogoBean(R.mipmap.ada_logo, "ADA" + getString(R.string.wallet), 0, ADA_COIN, 0));
        }
        if (WalletSpUtil.getEnableETC() == 1) {
            list.add(new WalletLogoBean(R.mipmap.etc_logo, "ETC" + getString(R.string.wallet), 0, ETC_COIN, 0));
        }
        if (WalletSpUtil.getEnableSGB() == 1) {
            list.add(new WalletLogoBean(R.mipmap.sgb_logo, "SGB" + getString(R.string.wallet), 0, SGB_COIN, 0));
        }
        if (WalletSpUtil.getEnableSOL() == 1) {
            list.add(new WalletLogoBean(R.mipmap.sol_logo, "SOL" + getString(R.string.wallet), 0, SOL_COIN, 0));
        }
        if (WalletSpUtil.getEnableMATIC() == 1) {
            list.add(new WalletLogoBean(R.mipmap.matic_logo, "POLYGON" + getString(R.string.wallet), 0, MATIC_COIN, 0));
        }
        mAdapter.notifyDataSetChanged();
    }
}
