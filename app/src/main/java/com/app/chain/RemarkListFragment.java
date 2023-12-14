package com.app.chain;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.chain.adapter.RemarkListAdapter;
import com.app.databinding.FragmentChainSyncRemarkBinding;
import com.wallet.ctc.util.AllUtils;

import org.matrix.android.sdk.internal.session.remark.Remark;

import java.util.List;

import common.app.base.BaseFragment;
import common.app.base.adapter.SpacingItemDecoration;

public class RemarkListFragment extends BaseFragment {

    FragmentChainSyncRemarkBinding vb;
    private int mMode = ChainSyncActivity.CHAIN_SYNC_TYPE_WHITE_LIST;

    private RemarkListAdapter mAdapter = null;

    
    public static RemarkListFragment getInstance() {
        Bundle bundle = new Bundle();
        RemarkListFragment fragment = new RemarkListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        vb = FragmentChainSyncRemarkBinding.inflate(inflater, container, false);
        return vb.getRoot();
    }

    @Override
    public void initParam() {
        super.initParam();
    }

    @Override
    public void initView(@Nullable View view) {
        mAdapter = new RemarkListAdapter();
        vb.rvCommon.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        vb.rvCommon.addItemDecoration(new SpacingItemDecoration(AllUtils.dip2px(getActivity(), 12), true));
        vb.rvCommon.setAdapter(mAdapter);
    }

    @Override
    public void initData() {

        viewModel().observe(viewModel().mRemarkListLD, remarks -> {
            showDatas(remarks);
        });
    }


    private void showDatas(List<Remark> datas) {
        mAdapter.setItems(datas);
        mAdapter.notifyDataSetChanged();
        vb.progressBar.setVisibility(View.GONE);
        if (datas != null && datas.size() > 0) {
            vb.nodataLayout.setVisibility(View.GONE);
        } else {
            vb.nodataLayout.setVisibility(View.VISIBLE);
        }
        ((ChainSyncActivity) getActivity()).changeCount(0,mAdapter.getUnUpChainCount());
    }

    private ChainSyncViewModel viewModel() {
        return ((ChainSyncActivity) getActivity()).getViewModel();
    }
}




