

package com.app.node.add;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.app.R;
import com.app.databinding.ActivityAddFmNodeBinding;
import com.app.pojo.NodeConfigBean;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.model.blockchain.NodeData;

import common.app.base.BaseActivity;
import common.app.my.view.MyAlertDialog;
import common.app.ui.view.TitleBarView;
import common.app.utils.SpUtil;


public class AddFmNodeActivity extends BaseActivity<AddFmNodeVM> {
    private static final String TAG = "AddFmActivity";
    private ActivityAddFmNodeBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mBinding = ActivityAddFmNodeBinding.inflate(getLayoutInflater());
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

        getViewModel().mNodeDataMutableLiveData.observe(this, new Observer<NodeData>() {
            @Override
            public void onChanged(NodeData nodeData) {
                String nodeUrl = mBinding.etNodeUrl.getText().toString().trim();
                String nodeName = mBinding.etNodeName.getText().toString().trim();

                NodeConfigBean nodeConfig = NodeConfigBean.prase(nodeUrl, nodeData.num_index, nodeName);
                if (null == nodeConfig || !nodeConfig.isValidate()) {
                    showToast(R.string.data_error);
                    return;
                }
                getViewModel().addNode(nodeConfig.node_address, nodeConfig.number_index, nodeConfig.node_name, nodeData.opt_addr);
            }
        });
        
        mBinding.btnAdd.setOnClickListener(view1 -> {
            String nodeUrl = mBinding.etNodeUrl.getText().toString().trim();
            String nodeName = mBinding.etNodeName.getText().toString().trim();

            if (TextUtils.isEmpty(nodeUrl) || TextUtils.isEmpty(nodeName)) {
                showToast(R.string.please_enter_content);
                return;
            }
            if (false) {
                if (!nodeUrl.startsWith("https") && !nodeUrl.startsWith("http")) {
                    showToast("httphttps");
                    return;
                }
                String defNode = SpUtil.getDefNode(WalletUtil.MCC_COIN);
                Uri uri = Uri.parse(defNode);
                String port = String.valueOf(uri.getPort());

                
                Uri nodeUri = Uri.parse(nodeUrl);
                String host = nodeUri.getHost();
                String scheme = nodeUri.getScheme();

                String url = scheme + "://" + host + ":" + port + "/gateway/queryinfo";
            }
            getViewModel().getNodeData(nodeUrl+"/gateway/queryinfo");

        });
    }

    @Override
    public void initData() {
        super.initData();
        
        getViewModel().observe(getViewModel().mErrorToastLD, data -> {
            showToast(data);
        });

        
        getViewModel().observe(getViewModel().mAddAlertLD, msg -> {
            MyAlertDialog dialog = new MyAlertDialog(AddFmNodeActivity.this, msg);
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
                showToast(R.string.add_node_success);
                setResult(RESULT_OK);
                finish();
            }
        });

    }

}
