

package com.wallet.ctc.nft.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.nft.adapter.SkyAdapter;
import com.wallet.ctc.nft.adapter.WalletHomeAssetsAdapter;
import com.wallet.ctc.ui.blockchain.assetsdetail.AssetsBTCDetailActivity;
import com.wallet.ctc.ui.blockchain.assetsdetail.AssetsDetailActivity;
import com.wallet.ctc.ui.blockchain.assetsdetail.AssetsEthDetailActivity;
import com.wallet.ctc.ui.blockchain.assetsdetail.AssetsTRXDetailActivity;

import java.util.List;

import butterknife.BindView;
import common.app.base.BaseFragment;

public class AssetsListFragment extends BaseFragment {
    private BaseAssetsBiz biz = null;

    public static AssetsListFragment newInstance(WalletEntity wallet) {
        Bundle args = fitArguments(wallet);
        AssetsListFragment fragment = new AssetsListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static Bundle fitArguments(WalletEntity wallet) {
        Bundle args = new Bundle();
        args.putParcelable("wallet", wallet);
        return args;
    }

    @BindView(R2.id.rv_assets_list)
    RecyclerView rvAssetsList;
    public List<AssertBean> assets;
    public WalletHomeAssetsAdapter adapter = null;


    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_assets_list;
    }

    @Override
    public void initView(@Nullable View view) {
        super.initView(view);
        rvAssetsList.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        adapter = new WalletHomeAssetsAdapter();
        adapter.setItems(assets);
        rvAssetsList.setAdapter(adapter);
        adapter.setClickListener(new SkyAdapter.OnAdapterItemClick<AssertBean>() {
            @Override
            public void onItemClick(AssertBean assertBean, int position) {
                int type = assertBean.getType();
                Intent intent = null;
                if (type == WalletUtil.ETH_COIN || type == WalletUtil.ETF_COIN || type == WalletUtil.DMF_COIN || type == WalletUtil.DMF_BA_COIN || type == WalletUtil.HT_COIN || type == WalletUtil.BNB_COIN) {
                    intent = new Intent(getActivity(), AssetsEthDetailActivity.class);
                    intent.putExtra("type", assertBean.getShort_name());
                    intent.putExtra("address", assertBean.getContract());
                    intent.putExtra("gasCount", assertBean.getGas());
                    intent.putExtra("decimal", assertBean.getDecimal());
                    intent.putExtra("logo", assertBean.getImg_path());
                    intent.putExtra("DbNum", assertBean.getAssertsNum());
                    intent.putExtra("DbPrice", assertBean.getAssertsSumPrice());
                    intent.putExtra("coinImg", assertBean.getLogo());
                } else if (type == WalletUtil.DM_COIN || type == WalletUtil.MCC_COIN || type == WalletUtil.OTHER_COIN) {
                    intent = AssetsDetailActivity.getIntent(getActivity(), assertBean);

                } else if (type == WalletUtil.BTC_COIN) {
                    intent = new Intent(getActivity(), AssetsBTCDetailActivity.class);
                    intent.putExtra("type", assertBean.getShort_name());
                    intent.putExtra("address", assertBean.getContract());
                    intent.putExtra("gasCount", assertBean.getGas());
                    intent.putExtra("decimal", assertBean.getDecimal());
                    intent.putExtra("logo", assertBean.getImg_path());
                    intent.putExtra("DbNum", assertBean.getAssertsNum());
                    intent.putExtra("DbPrice", assertBean.getAssertsSumPrice());
                    intent.putExtra("coinImg", assertBean.getLogo());
                } else if (type == WalletUtil.TRX_COIN) {
                    intent = new Intent(getActivity(), AssetsTRXDetailActivity.class);
                    intent.putExtra("type", assertBean.getShort_name());
                    intent.putExtra("address", assertBean.getContract());
                    intent.putExtra("gasCount", assertBean.getGas());
                    intent.putExtra("decimal", assertBean.getDecimal());
                    intent.putExtra("logo", assertBean.getImg_path());
                    intent.putExtra("DbNum", assertBean.getAssertsNum());
                    intent.putExtra("DbPrice", assertBean.getAssertsSumPrice());
                    intent.putExtra("coinImg", assertBean.getLogo());
                } else {
                    return;
                }
                startActivity(intent);
            }
        });

        loadData();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            loadData();
        }
    }

    
    public void loadData() {
        WalletEntity walletEntity = getArguments().getParcelable("wallet");
        int type = walletEntity.getType();
        if (type == WalletUtil.ETH_COIN || type == WalletUtil.ETF_COIN || type == WalletUtil.DMF_COIN || type == WalletUtil.DMF_BA_COIN || type == WalletUtil.HT_COIN || type == WalletUtil.BNB_COIN) {
            initBiz(walletEntity, AssetsEthBiz.class);
        } else if (type == WalletUtil.TRX_COIN) {
            initBiz(walletEntity, AssetsTrxBiz.class);
        }
    }

    
    public void search(String keyworld) {
        if (null == biz) {
            return;
        }
        biz.search(keyworld);
    }

    private static final int MSG_ON_DATA_CHANGED = 1001;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_ON_DATA_CHANGED:
                    setAdapterItems((List<AssertBean>) msg.obj);
                    break;
            }
        }
    };

    
    private void setAdapterItems(List<AssertBean> assertBeans) {
        
        if (rvAssetsList.isComputingLayout()) {
            Message msg = handler.obtainMessage(MSG_ON_DATA_CHANGED);
            msg.obj = assertBeans;
            handler.removeMessages(MSG_ON_DATA_CHANGED);
            handler.sendMessageDelayed(msg, 200);
        } else {
            adapter.setItems(assertBeans);
            adapter.notifyDataSetChanged();
        }
    }

    
    public <T extends Fragment> T findFragment(@NonNull Class<T> fragmentClass) {
        List<Fragment> fragments = getActivity().getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment.getClass() == fragmentClass) {
                return (T) fragment;
            }
        }
        return null;
    }

    private void initBiz(WalletEntity walletEntity, Class bizClass) {
        if (null == biz) {
            biz = (BaseAssetsBiz) createViewModel(this, bizClass);
            register();
        } else {
            if (biz.getClass() != bizClass) {
                unRegister();
                biz = (BaseAssetsBiz) createViewModel(this, bizClass);
                register();
            }
        }

        biz.loadData(walletEntity);
    }

    private Observer<List<AssertBean>> assetsOb = null;
    private Observer<List<AssertBean>> localAssetsOb = null;
    private Observer<WalletEntity> walletInfoChangeOb = null;

    private void unRegister() {
        biz.getAssertsLiveData.removeObservers(this);
        biz.getLocalAssertsLiveData.removeObservers(this);
        biz.onWalletInfoChangeLiveData.removeObservers(this);
    }

    
    private void register() {
        if (null == assetsOb) {
            assetsOb = new Observer<List<AssertBean>>() {
                @Override
                public void onChanged(List<AssertBean> assertBeans) {

                }
            };
        }

        
        biz.getAssertsLiveData.observe(this, assetsOb);

        if (null == localAssetsOb) {
            localAssetsOb = new Observer<List<AssertBean>>() {
                @Override
                public void onChanged(List<AssertBean> assertBeans) {
                    setAdapterItems(assertBeans);
                }
            };
        }
        biz.getLocalAssertsLiveData.observe(this, localAssetsOb);

        if (null == walletInfoChangeOb) {
            walletInfoChangeOb = new Observer<WalletEntity>() {
                @Override
                public void onChanged(WalletEntity entity) {

                }
            };
        }
        biz.onWalletInfoChangeLiveData.observe(this, walletInfoChangeOb);
    }

    @Override
    public void onAttach(Activity activity) {
        Log.d("fenghl", this.getClass().getName()+ " onAttach");
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        Log.d("fenghl", this.getClass().getName()+ " onDetach");
        super.onDetach();
    }
}
