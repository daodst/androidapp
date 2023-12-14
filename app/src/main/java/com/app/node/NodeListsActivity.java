

package com.app.node;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.databinding.ActivityNodesList2Binding;
import com.app.node.add.AddFmNodeActivity;
import com.wallet.ctc.R;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.DBManager;
import com.wallet.ctc.db.SettingNodeEntity;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.EvmosPledgeResultBean;
import com.wallet.ctc.util.AllUtils;
import com.wallet.ctc.util.DecriptUtil;
import com.wallet.ctc.view.dialog.TransConfirmDialogBuilder;
import com.wallet.ctc.view.dialog.choosewallet.ChooseWalletDialog;

import java.util.List;

import common.app.base.BaseActivity;
import common.app.mall.util.ToastUtil;
import common.app.my.view.MyAlertDialog;
import common.app.ui.view.InputPwdDialog;
import common.app.ui.view.TitleBarView;
import common.app.utils.AppWidgetUtils;
import common.app.utils.SpUtil;
import im.vector.app.provide.ChatStatusProvide;


public class NodeListsActivity extends BaseActivity<NodeCheckVM> {
    private ActivityNodesList2Binding mBinding;
    private int mNowPage = 1;
    private NodeListAdapter mAdapter;
    private InputPwdDialog mPwdDialog;
    private WalletEntity mSelecteWallet;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mBinding = ActivityNodesList2Binding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        super.onCreate(savedInstanceState);
    }


    private boolean isLogin = false;
    private boolean isShowding = false;

    @Override
    public void initView(@Nullable View view) {
        super.initView(view);

        if (null != getIntent()) {
            isLogin = getIntent().getBooleanExtra("isLogin", false);
        }
        mBinding.defNodeList.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                
                
                if (view.getChildCount() > 0 && view.getFirstVisiblePosition() == 0
                        && view.getChildAt(0).getTop() >= view.getPaddingTop()) {
                    
                    mBinding.refreshView.setEnabled(true);
                } else {
                    mBinding.refreshView.setEnabled(false);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });


        mBinding.titleBar.setOnTitleBarClickListener(new TitleBarView.TitleBarClickListener() {
            @Override
            public void leftClick() {
                finish();
            }

            @Override
            public void rightClick() {
            }
        });

        mBinding.refreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });

        mAdapter = new NodeListAdapter(this);
        mBinding.defNodeList.setAdapter(mAdapter);
        mAdapter.setIClick(entity -> {
            if (isLogin && !isShowding) {
                isShowding = true;
                showChange(entity);
            }
        });
        
        mBinding.defNodeList.setOnItemClickListener((adapterView, view1, i, l) -> {
            SettingNodeEntity gateWay = mAdapter.getItem(i);
            showChange(gateWay);
        });

        mBinding.defNodeList.setOnItemLongClickListener((parent, view12, position, id) -> {
            SettingNodeEntity gateWay = mAdapter.getItem(position);

            if (gateWay.getIsDef() == 1) {
                if (gateWay.isChoose()) {
                    ToastUtil.showToast(getString(com.app.R.string.node_del_toast));
                } else {
                    showDel(gateWay);
                }
                return true;
            }
            return false;
        });

        
        ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Intent data = result.getData();
                int resultCode = result.getResultCode();
                if (resultCode == RESULT_OK) {
                    getData();
                }
            }
        });
        mBinding.addNode.setOnClickListener(view1 -> {
            resultLauncher.launch(new Intent(NodeListsActivity.this, AddFmNodeActivity.class));
        });

        
        mBinding.pledgeNumTitle.setOnClickListener(view1 -> {
            getViewModel().sort(isLogin, 1, 0);
        });

        
        mBinding.onlineTimeTitle.setOnClickListener(view1 -> {
            getViewModel().sort(isLogin, 0, 1);
        });
    }

    private void showDel(SettingNodeEntity gateWay) {
        MyAlertDialog dialog = new MyAlertDialog(NodeListsActivity.this, getString(com.app.R.string.node_del_tips));
        dialog.setTitle(getString(com.app.R.string.node_del_title));
        dialog.setonclick(new MyAlertDialog.Onclick() {
            @Override
            public void Yes() {
                dialog.dismiss();
                DBManager.getInstance(NodeListsActivity.this).deleteNode(gateWay);
                
                getData();
            }

            @Override
            public void No() {
                dialog.dismiss();
            }
        });
        dialog.setNoText(getString(com.app.R.string.close_dialog_text));
        dialog.setYesText(getString(com.app.R.string.node_del_text));
        dialog.show();
    }

    private void showChange(SettingNodeEntity gateWay) {
        
        MyAlertDialog dialog = new MyAlertDialog(NodeListsActivity.this,
                getString(com.app.R.string.switch_node_alert_msg));
        dialog.setTitle(getString(com.app.R.string.switch_node_text));
        dialog.setonclick(new MyAlertDialog.Onclick() {
            @Override
            public void Yes() {
                dialog.dismiss();
                isShowding = false;
                switchNode(gateWay);
            }

            @Override
            public void No() {
                isShowding = false;
                dialog.dismiss();
            }
        });
        dialog.setNoText(getString(com.app.R.string.close_dialog_text));
        dialog.setYesText(getString(com.app.R.string.now_switch_node_text));
        dialog.show();
    }


    @Override
    public void initData() {
        super.initData();

        
        getViewModel().observe(getViewModel().mRefreshLD, success -> {
            mBinding.refreshView.setRefreshing(false);
        });

        getViewModel().observe(getViewModel().mGateWaysLD, data -> {
            mAdapter.bindDatas(data);
        });

        getViewModel().observe(getViewModel().mChangeNodeResultLD, data -> {
            
            if (null != data && data.success) {
                if (ChatStatusProvide.loginStatus(NodeListsActivity.this)) {
                    showToast(com.app.R.string.switch_node_success_relogin);
                    ChatStatusProvide.signOut(NodeListsActivity.this);
                    AppWidgetUtils.gatewayName.setValue(SpUtil.getNodeName());
                    finish();
                } else {
                    showToast(com.app.R.string.switch_node_success);
                    AppWidgetUtils.gatewayName.setValue(SpUtil.getNodeName());
                    finish();
                }
            }
        });

        
        getViewModel().observe(getViewModel().mShowGasDialogLD, evmosSeqGasBean -> {
            TransConfirmDialogBuilder.builder(this, mSelecteWallet).amount("0")
                    
                    .fromAddress(mSelecteWallet.getAllAddress())
                    
                    .toAddress("")
                    .type(WalletUtil.MCC_COIN)
                    .orderDesc(getString(com.app.R.string.switch_node_text))
                    
                    .gasFeeWithToken(evmosSeqGasBean.getShowFee())
                    
                    .goTransferListener(pwd -> {
                        
                        String nowAddress = mSelecteWallet.getAllAddress();
                        getViewModel().swithNode(nowAddress, mNewGateWay, mSelecteWallet, pwd);
                    }).show();
        });


        
        getViewModel().observe(getViewModel().mShowPwdLD, status->{
            if (null == mSelecteWallet) {
                return;
            }
            InputPwdDialog mPwdDialog = new InputPwdDialog(NodeListsActivity.this, getString(R.string.place_edit_password));
            mPwdDialog.setonclick(new InputPwdDialog.Onclick() {
                @Override
                public void Yes(String pwd) {
                    if (TextUtils.isEmpty(pwd)) {
                        ToastUtil.showToast(R.string.place_edit_password);
                        return;
                    }
                    if (!mSelecteWallet.getmPassword().equals(DecriptUtil.MD5(pwd))) {
                        ToastUtil.showToast(com.wallet.ctc.R.string.password_error2);
                        return;
                    }
                    mPwdDialog.dismiss();
                    getViewModel().postTxReslult(new EvmosPledgeResultBean(true, ""), mNewGateWay);
                }

                @Override
                public void No() {
                    mPwdDialog.dismiss();
                }
            });
            mPwdDialog.show();
        });

        getData();
    }


    
    private void getData() {
        getViewModel().refreshData(isLogin);
    }

    
    private void switchNode(SettingNodeEntity newGateWay) {
        if (null == newGateWay) {
            return;
        }
        String newNodeAddress = newGateWay.getGateWayAddr();
        if (TextUtils.isEmpty(newNodeAddress)) {
            showToast(com.app.R.string.switch_node_error);
            return;
        }

        
        String address = "";
        String userId = ChatStatusProvide.getUserId(NodeListsActivity.this);
        if (!TextUtils.isEmpty(userId)) {
            address = AllUtils.getAddressByUid(userId);
            showPwdDialogAndSwith(address, newGateWay);
        } else {
            List<WalletEntity> wallets = WalletDBUtil.getInstent(NodeListsActivity.this).getWalletList(WalletUtil.MCC_COIN);
            if (null == wallets || wallets.size() == 0) {
                showToast(R.string.please_create_wallet);
                return;
            }
            if (wallets.size() == 1) {
                address = wallets.get(0).getAllAddress();
                showPwdDialogAndSwith(address, newGateWay);
            } else {
                
                ChooseWalletDialog.showDialog(NodeListsActivity.this, WalletUtil.MCC_COIN, (address1, walletType) -> {
                    showPwdDialogAndSwith(address1, newGateWay);
                });
            }
        }

    }


    private SettingNodeEntity mNewGateWay;

    private void showPwdDialogAndSwith(String nowAddress, SettingNodeEntity newGateWay) {
        mSelecteWallet = WalletDBUtil.getInstent(NodeListsActivity.this).getWalletInfoByAddress(nowAddress, WalletUtil.MCC_COIN);
        if (null == mSelecteWallet) {
            showToast(R.string.no_found_wallet_info);
            return;
        }
        mNewGateWay = newGateWay;
        getViewModel().showGasAlert(nowAddress, newGateWay);
    }
}
