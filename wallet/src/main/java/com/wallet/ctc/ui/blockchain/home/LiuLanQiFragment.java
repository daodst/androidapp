

package com.wallet.ctc.ui.blockchain.home;

import static com.wallet.ctc.crypto.WalletUtil.BNB_COIN;
import static com.wallet.ctc.crypto.WalletUtil.BTC_COIN;
import static com.wallet.ctc.crypto.WalletUtil.DM_COIN;
import static com.wallet.ctc.crypto.WalletUtil.ETH_COIN;
import static com.wallet.ctc.crypto.WalletUtil.HT_COIN;
import static com.wallet.ctc.crypto.WalletUtil.MCC_COIN;
import static com.wallet.ctc.crypto.WalletUtil.TRX_COIN;
import static com.wallet.ctc.crypto.WalletUtil.XRP_COIN;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.base.BaseFragment;
import com.wallet.ctc.base.BaseWebViewActivity;
import com.wallet.ctc.model.blockchain.WalletLogoBean;
import com.wallet.ctc.ui.blockchain.managewallet.WalletTypeAdapter;
import com.wallet.ctc.util.WalletSpUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class LiuLanQiFragment extends BaseFragment{

    protected Unbinder mUnbinder;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.wallet_list)
    ListView listview;
    private Intent intent;
    private List<WalletLogoBean> list = new ArrayList<>();
    private WalletTypeAdapter mAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_liulanqi, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        tvTitle.setText(R.string.liulanqi);
        mAdapter = new WalletTypeAdapter(getActivity());
        mAdapter.bindData(list);
        listview.setAdapter(mAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent = new Intent(getActivity(), BaseWebViewActivity.class);
                intent.putExtra("url", list.get(position).getBlockBrowser());
                intent.putExtra("title", list.get(position).getWalletName());
                startActivity(intent);
            }
        });
        if (WalletSpUtil.getEnableMcc() == 1) {
            list.add(new WalletLogoBean(R.mipmap.mcc_logo, getString(R.string.default_token_name).toUpperCase() + getString(R.string.block_browser), MCC_COIN, BuildConfig.BLOCK_BROWSER));
        }
        if (WalletSpUtil.getEnableDm() == 1) {
            list.add(new WalletLogoBean(R.mipmap.dm_logo, "DM" + " " + getString(R.string.block_browser), DM_COIN,"http://xxx.mobi/dmblock_pc.php?device=app"));
        }
        if (WalletSpUtil.getEnableEth() == 1) {
            list.add(new WalletLogoBean(R.mipmap.eth_logo, "ETH" + getString(R.string.block_browser), ETH_COIN, "https://cn.etherscan.com/"));
        }
        if (WalletSpUtil.getEnableBtc() == 1) {
            list.add(new WalletLogoBean(R.mipmap.btc_logo, "BTC" + getString(R.string.block_browser),BTC_COIN, "https://btc.com/"));
        }
        if (WalletSpUtil.getEnableXrp() == 1) {
            list.add(new WalletLogoBean(R.mipmap.xrp_logo, "XRP" + getString(R.string.block_browser), XRP_COIN, "https://xrpcharts.ripple.com/"));
        }
        if (WalletSpUtil.getEnableTrx() == 1) {
            list.add(new WalletLogoBean(R.mipmap.trx_logo, "TRX" + getString(R.string.block_browser), TRX_COIN,"https://tronscan.io/"));
        }
        if (WalletSpUtil.getEnableBnb() == 1) {
            list.add(new WalletLogoBean(R.mipmap.bnb_logo, "BNB" + getString(R.string.block_browser), BNB_COIN,"https://bscscan.com/"));
        }
        if (WalletSpUtil.getEnableHt()== 1) {
            list.add(new WalletLogoBean(R.mipmap.huobi_logo, "HT" + getString(R.string.block_browser), HT_COIN,"https://hecoinfo.com/"));
        }

        

        mAdapter.notifyDataSetChanged();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @OnClick({R2.id.tv_back})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.tv_back) {
            getActivity().finish();
        }
    }
}
