

package com.wallet.ctc.nft.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.db.NftBean;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.nft.adapter.SkyAdapter;
import com.wallet.ctc.nft.adapter.WalletHomeNftAdapter;

import java.util.List;

import butterknife.BindView;
import common.app.base.BaseFragment;

public class NftListFragment extends BaseFragment<NftBiz> {

    @BindView(R2.id.iv_empty)
    ImageView ivEmpty;
    private DeleteDialog deleteDialog = null;

    public static NftListFragment newInstance(WalletEntity wallet) {
        Bundle args = fitArguments(wallet);
        NftListFragment fragment = new NftListFragment();
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
    private WalletHomeNftAdapter adapter = null;
    private WalletEntity currentWalelt = null;
private boolean isFirst=true;
    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_assets_list;
    }

    @Override
    public void initView(@Nullable View view) {
        super.initView(view);
        rvAssetsList.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        adapter = new WalletHomeNftAdapter();
        rvAssetsList.setAdapter(adapter);
        adapter.setClickListener(new SkyAdapter.OnAdapterItemClick<NftBean>() {
            @Override
            public void onItemClick(NftBean nftBean, int position) {
                nftBean.walletType = currentWalelt.getType();
                NftDetailActivity.intent(mContext, nftBean);
            }
        });
        adapter.setLongClickListener(new SkyAdapter.OnAdapterItemLongClick<NftBean>() {
            @Override
            public void onItemLongClick(NftBean nftBean, int position) {
                if (null != nftBean.getId() && nftBean.tokenCount == 0) {
                    
                    showDeleteDialog(nftBean);
                }
            }
        });
        resetRegister();
        loadData();
    }


    private void showDeleteDialog(NftBean nftBean) {
        if (null == deleteDialog) {
            deleteDialog = new DeleteDialog(getActivity());
        }
        deleteDialog.setCallback(new DeleteDialog.Callback() {
            @Override
            public void onDelete() {
                getViewModel().deleteNft(nftBean);
                deleteDialog.dismiss();
            }
        });
        if (!deleteDialog.isShowing()) {
            deleteDialog.show();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        Log.d("fenghl", "onHiddenChanged" + this.getClass().getName() + hidden);
        if (!hidden) {
            loadData();
        }
    }

    
    public void loadData() {
        currentWalelt = getArguments().getParcelable("wallet");
        
        getViewModel().loadData(currentWalelt);
    }


    
    public void search(String keyworld) {
        getViewModel().search(keyworld);
    }

    private void resetRegister() {
        getViewModel().getNftLiveData.removeObservers(this);
        getViewModel().getNftLiveData.observe(this, new Observer<List<NftBean>>() {
            @Override
            public void onChanged(List<NftBean> assertBeans) {

              
                setAdapterItems(assertBeans);
            }
        });

        getViewModel().getLocalNftLiveData.removeObservers(this);
        getViewModel().getLocalNftLiveData.observe(this, new Observer<List<NftBean>>() {
            @Override
            public void onChanged(List<NftBean> assertBeans) {
                setAdapterItems(assertBeans);
            }
        });
    }

    private static final int MSG_ON_DATA_CHANGED = 1001;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_ON_DATA_CHANGED:
                    adapter.setItems((List<NftBean>) msg.obj);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    private void setAdapterItems(List<NftBean> assertBeans) {
        if (rvAssetsList.isComputingLayout()) {
            Message msg = handler.obtainMessage(MSG_ON_DATA_CHANGED);
            msg.obj = assertBeans;
            handler.removeMessages(MSG_ON_DATA_CHANGED);
            handler.sendMessageDelayed(msg, 200);
        } else {
            if (null == assertBeans || assertBeans.size() == 0) {
                showEmpty();
            } else {
                showContent();
            }
            adapter.setItems(assertBeans);
            adapter.notifyDataSetChanged();
        }
    }

    private void showEmpty() {
        Log.v("fsefsfsefs","showEmpty");
        rvAssetsList.setVisibility(View.GONE);
        ivEmpty.setVisibility(View.VISIBLE);
    }

    private void showContent() {
        Log.v("fsefsfsefs","showContent");
        rvAssetsList.setVisibility(View.VISIBLE);
        ivEmpty.setVisibility(View.GONE);
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
