

package com.app.node.mod;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import com.app.R;
import com.app.databinding.ActivityModFmNodeBinding;
import com.app.pojo.NodeConfigBean;
import com.wallet.ctc.crypto.WalletUtil;

import common.app.base.BaseActivity;
import common.app.my.view.MyAlertDialog;
import common.app.ui.view.TitleBarView;
import common.app.utils.SpUtil;
import im.vector.app.provide.ChatStatusProvide;


public class ModFmNodeActivity extends BaseActivity<ModFmNodeVM> {
    private static final String TAG = "AddFmActivity";
    private ActivityModFmNodeBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mBinding = ActivityModFmNodeBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView(@Nullable View view) {
        super.initView(view);
        mBinding.toolbarView.setOnTitleBarClickListener(new TitleBarView.TitleBarClickListener() {
            @Override
            public void leftClick() {
                finish();
            }
            @Override
            public void rightClick() {
            }
        });

        String nowNodeUrl = SpUtil.getDefNode(WalletUtil.MCC_COIN);
        String nowNodeName = SpUtil.getNodeName();
        String nowNodePhone = SpUtil.getNodeNoSegm();

        mBinding.etNodeUrl.setText(nowNodeUrl);
        mBinding.etNodeName.setText(nowNodeName);
        mBinding.etNodePhone.setText(nowNodePhone);

        
        mBinding.btnAdd.setOnClickListener(view1 -> {
            String nodeUrl = mBinding.etNodeUrl.getText().toString().trim();
            String nodeName = mBinding.etNodeName.getText().toString().trim();
            String nodePhone = mBinding.etNodePhone.getText().toString().trim();

            if (TextUtils.isEmpty(nodeUrl) || TextUtils.isEmpty(nodeName) || TextUtils.isEmpty(nodePhone)) {
                showToast(R.string.please_enter_content);
                return;
            }

            NodeConfigBean nodeConfig = NodeConfigBean.prase(nodeUrl, nodePhone, nodeName);
            if (null == nodeConfig || !nodeConfig.isValidate()) {
                showToast(R.string.data_error);
                return;
            }

            MyAlertDialog alertDialog = new MyAlertDialog(ModFmNodeActivity.this, "？");
            alertDialog.setonclick(new MyAlertDialog.Onclick() {
                @Override
                public void Yes() {
                    alertDialog.dismiss();
                    getViewModel().modNode(nodeConfig.node_address, nodeConfig.number_index, nodeConfig.node_name);
                }

                @Override
                public void No() {
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
        });
    }

    @Override
    public void initData() {
        super.initData();
        
        getViewModel().observe(getViewModel().mErrorToastLD, data -> {
            showToast(data);
        });

        
        getViewModel().observe(getViewModel().mAddAlertLD, msg->{
            MyAlertDialog dialog = new MyAlertDialog(ModFmNodeActivity.this, msg);
            dialog.setonclick(new MyAlertDialog.Onclick() {
                @Override
                public void Yes() {
                    dialog.dismiss();
                    getViewModel().doAddNode();
                }

                @Override
                public void No() {
                    dialog.dismiss();
                }
            });
            dialog.show();
        });

        
        getViewModel().observe(getViewModel().mAddResultLD, aBoolean -> {
            if (aBoolean) {
                showToast("");
                setResult(RESULT_OK);
                finish();
                if (ChatStatusProvide.loginStatus(ModFmNodeActivity.this)) {
                    ChatStatusProvide.signOut(ModFmNodeActivity.this);
                }
            }
        });

    }

}
