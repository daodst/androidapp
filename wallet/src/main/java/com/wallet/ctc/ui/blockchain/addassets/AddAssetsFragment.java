

package com.wallet.ctc.ui.blockchain.addassets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.base.BaseFragment;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.EthAssertBean;
import com.wallet.ctc.model.blockchain.NewAssertBean;
import com.wallet.ctc.model.blockchain.TrxSeachBean;
import com.wallet.ctc.model.blockchain.XrpAssertBean;
import com.wallet.ctc.ui.blockchain.seach.SeachAdapter;
import com.wallet.ctc.ui.blockchain.seach.SeachEthAdapter;
import com.wallet.ctc.ui.blockchain.seach.SeachTrxAdapter;
import com.wallet.ctc.ui.blockchain.seach.SeachXrpAdapter;
import com.wallet.ctc.util.SettingPrefUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import common.app.ui.view.PullToRefreshLayout;
import common.app.ui.view.PullableListView;



public class AddAssetsFragment extends BaseFragment {

    @BindView(R2.id.refresh_view)
    PullToRefreshLayout refreshView;
    @BindView(R2.id.assets_list)
    PullableListView assetsList;
    @BindView(R2.id.nodata)
    LinearLayout nodata;
    private AssetsAdapter mAdapter;
    private List<AssertBean> chooseList=new ArrayList<>();
    private List<AssertBean> canAdd=new ArrayList<>();
    private List<EthAssertBean> seachList=new ArrayList<>();
    private List<XrpAssertBean> seachXrpList=new ArrayList<>();
    private List<TrxSeachBean> seachTrxList=new ArrayList<>();

    private SeachAdapter mAdapter2;
    private SeachEthAdapter mEthAdapter;
    private SeachXrpAdapter mXrpAdapter;
    private SeachTrxAdapter mTrxAdapter;
    private int type=1;
    private WalletDBUtil walletDBUtil;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_addassets, container, false);
        ButterKnife.bind(this,view);
        walletDBUtil=WalletDBUtil.getInstent(getActivity());
        mAdapter=new AssetsAdapter(context);
        mAdapter2 = new SeachAdapter(context);
        mEthAdapter = new SeachEthAdapter(context);
        mXrpAdapter = new SeachXrpAdapter(context);
        mTrxAdapter=new SeachTrxAdapter(context);
        refreshView.releaseNotPull();
        mEthAdapter.bindData(seachList,type);
        mXrpAdapter.bindData(seachXrpList);
        mTrxAdapter.bindData(seachTrxList);
        initData();
        return view;
    }

    public void setType(int type){
        this.type=type;
    }

    public void setSeachList(List<NewAssertBean> seachList, int type){
        refreshView.releaseNotPull();
        assetsList.setAdapter(mAdapter2);
        if(null==seachList||seachList.size()<1){
            nodata.setVisibility(View.VISIBLE);
            refreshView.setVisibility(View.GONE);
        }else{
            nodata.setVisibility(View.GONE);
            refreshView.setVisibility(View.VISIBLE);
        }
        mAdapter2.bindData(seachList,type);
        mAdapter2.notifyDataSetChanged();
    }

    public void setSeachEthList(List<EthAssertBean> seach,int page){
        this.seachList.clear();
        this.seachList.addAll(seach);
        if(page==1||page==2) {
            assetsList.setAdapter(mEthAdapter);
        }
        refreshView.releaseNotPull();
        refreshView.refreshFinish(PullToRefreshLayout.SUCCEED);
        refreshView.loadmoreFinish(PullToRefreshLayout.SUCCEED);
        if(null==seachList||seachList.size()<1){
            nodata.setVisibility(View.VISIBLE);
            refreshView.setVisibility(View.GONE);
        }else{
            nodata.setVisibility(View.GONE);
            refreshView.setVisibility(View.VISIBLE);
        }
        mEthAdapter.notifyDataSetChanged();
    }

    public void setSeachXrpList(List<XrpAssertBean> seach, int page){
        this.seachXrpList.clear();
        this.seachXrpList.addAll(seach);
        assetsList.setAdapter(mXrpAdapter);
        refreshView.releaseNotPull();
        refreshView.refreshFinish(PullToRefreshLayout.SUCCEED);
        refreshView.loadmoreFinish(PullToRefreshLayout.SUCCEED);
        if(null==seachXrpList||seachXrpList.size()<1){
            nodata.setVisibility(View.VISIBLE);
            refreshView.setVisibility(View.GONE);
        }else{
            nodata.setVisibility(View.GONE);
            refreshView.setVisibility(View.VISIBLE);
        }
        mXrpAdapter.notifyDataSetChanged();
    }

    public void setSeachTrxList(List<TrxSeachBean> seach){
        this.seachTrxList.clear();
        this.seachTrxList.addAll(seach);
        assetsList.setAdapter(mTrxAdapter);
        refreshView.releaseNotPull();
        refreshView.refreshFinish(PullToRefreshLayout.SUCCEED);
        refreshView.loadmoreFinish(PullToRefreshLayout.SUCCEED);
        if(null==seachTrxList||seachTrxList.size()<1){
            nodata.setVisibility(View.VISIBLE);
            refreshView.setVisibility(View.GONE);
        }else{
            nodata.setVisibility(View.GONE);
            refreshView.setVisibility(View.VISIBLE);
        }
        mTrxAdapter.notifyDataSetChanged();
    }

    public void initData() {
        if(null==nodata){
            return;
        }
        nodata.setVisibility(View.GONE);
        refreshView.setVisibility(View.VISIBLE);
        canAdd=walletDBUtil.getMustWallet(type);
        canAdd.addAll(walletDBUtil.getMustAssets(type));
        canAdd.addAll(walletDBUtil.canChooseWallet(type));
        chooseList=walletDBUtil.getAssetsByWalletType(SettingPrefUtil.getWalletAddress(context),type);
        chooseList.addAll(walletDBUtil.getMustWallet(type));
        mAdapter.bindData(canAdd,chooseList);
        assetsList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(null==context){
            return;
        }
        canAdd=walletDBUtil.getMustWallet(type);
        canAdd.addAll(walletDBUtil.getMustAssets(type));
        canAdd.addAll(walletDBUtil.canChooseWallet(type));
        chooseList=walletDBUtil.getAssetsByWalletType(SettingPrefUtil.getWalletAddress(context),type);
        chooseList.addAll(walletDBUtil.getMustWallet(type));
        mAdapter.bindData(canAdd,chooseList);
        mAdapter2.bindAddedData(canAdd);
        mEthAdapter.bindAddedData(canAdd);
        mTrxAdapter.bindAddedData(canAdd);
        mAdapter.notifyDataSetChanged();
    }
}
