package com.app.chain;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.databinding.FragmentChainSyncListBinding;
import com.app.white_black_list.WhiteBlackDelAdapter;
import com.app.white_black_list.WhiteBlackListAdapter;
import com.wallet.ctc.model.blockchain.EvmosUserBean;
import com.wallet.ctc.util.AllUtils;

import java.util.List;

import common.app.base.BaseFragment;
import common.app.my.view.MyAlertDialog;
import im.vector.app.provide.ChatStatusProvide;

public class WhiteBlackListFragment extends BaseFragment {

    FragmentChainSyncListBinding vb;

    private WhiteBlackListAdapter mAdapter;
    private WhiteBlackDelAdapter mDelAdapter;


    private String mAddr;

    private static final String KEY_MODE = "mode";
    private int mMode = ChainSyncActivity.MODE_WHITE;

    
    public static WhiteBlackListFragment getInstance(int mode) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_MODE, mode);
        WhiteBlackListFragment fragment = new WhiteBlackListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        vb = FragmentChainSyncListBinding.inflate(inflater, container, false);
        return vb.getRoot();
    }

    @Override
    public void initParam() {
        super.initParam();
        mAddr = AllUtils.getAddressByUid(ChatStatusProvide.getUserId(getContext()));
        mMode = getArguments().getInt(KEY_MODE);
    }

    @Override
    public void initView(@Nullable View view) {
        vb.tvManage.setOnClickListener(v -> {
            boolean isEdited = mAdapter.isEdited();
            mAdapter.setEdited(!isEdited);
            if (mAdapter.isEdited()) {
                vb.tvManage.setText(getString(com.wallet.ctc.R.string.delete));
            } else {
                vb.tvManage.setText(getString(com.app.R.string.sm_manager_title));
            }
        });

        
        vb.searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                String keyword = editable.toString();
                viewModel().searchBWList(keyword, mMode);
            }
        });

        mAdapter = new WhiteBlackListAdapter(getActivity(), delUser -> {
            MyAlertDialog alertDialog = new MyAlertDialog(getActivity(), getString(com.app.R.string.sm_del_user_alert));
            alertDialog.setonclick(new MyAlertDialog.Onclick() {
                @Override
                public void Yes() {
                    alertDialog.dismiss();
                    viewModel().removeUser(delUser, mMode);
                    if (delUser.isEffect) {
                        showToast(com.app.R.string.after_up_chain_useful);
                    }
                }

                @Override
                public void No() {
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
        });
        vb.listview.setAdapter(mAdapter);

        mDelAdapter = new WhiteBlackDelAdapter(getActivity(), delUser -> {
            viewModel().removeDelListUser(delUser, mMode);
        });
        vb.delRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        vb.delRecyclerview.setAdapter(mDelAdapter);
    }

    @Override
    public void initData() {

        
        if (mMode == ChainSyncActivity.MODE_WHITE) {
            
            viewModel().observe(viewModel().mWhiteListLD, userList -> {
                ((ChainSyncActivity) getActivity()).changeCount(1, ChainSyncViewModel.mUnUpWhiteList.size());
                showDatas(userList);
            });
            viewModel().observe(viewModel().mWhiteSearchListLD, userList -> {
                showDatas(userList);
            });


            
            viewModel().observe(viewModel().mWhiteToRemoveLD, userlist -> {
                showWaitDelDatas(userlist);
            });
        } else if(mMode == ChainSyncActivity.MODE_BLACK) {
            
            viewModel().observe(viewModel().mBlackListLD, userList -> {
                ((ChainSyncActivity) getActivity()).changeCount(2,ChainSyncViewModel.mUnUpBlackList.size());
                showDatas(userList);
            });
            viewModel().observe(viewModel().mBlackSearchListLD, userList -> {
                showDatas(userList);
            });

            
            viewModel().observe(viewModel().mBlackToRemoveLD, userlist -> {
                showWaitDelDatas(userlist);
            });
        }
        
    }


    
    private void showDatas(List<EvmosUserBean> userList) {
        mAdapter.bindDatas(userList);
        vb.progressBar.setVisibility(View.GONE);
        if (userList != null && userList.size() > 0) {
            vb.nodataLayout.setVisibility(View.GONE);
        } else {
            vb.nodataLayout.setVisibility(View.VISIBLE);
        }
    }

    
    private void showWaitDelDatas(List<EvmosUserBean> userlist) {
        mDelAdapter.bindDatas(userlist);
        if (userlist == null || userlist.size() == 0) {
            vb.delLayout.setVisibility(View.GONE);
        } else {
            vb.delLayout.setVisibility(View.VISIBLE);
        }
    }

    private ChainSyncViewModel viewModel() {
        return ((ChainSyncActivity) getActivity()).getViewModel();
    }
}




