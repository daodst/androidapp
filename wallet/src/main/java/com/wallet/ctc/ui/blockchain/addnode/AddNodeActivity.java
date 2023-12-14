

package com.wallet.ctc.ui.blockchain.addnode;

import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.api.blockchain.BlockChainApi;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.DBManager;
import com.wallet.ctc.db.SettingNodeEntity;
import com.wallet.ctc.util.LogUtil;
import com.wallet.ctc.view.dialog.AddNodeWarnDialog;
import com.wallet.ctc.view.dialog.InputNodeDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.app.mall.util.ToastUtil;
import common.app.my.view.MyAlertDialog;
import common.app.utils.NetWorkUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;


public class AddNodeActivity extends BaseActivity {


    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.tv_action)
    TextView tvAction;
    @BindView(R2.id.def_node_list)
    ListView defNodeList;
    private int type;
    private String name;
    private List<SettingNodeEntity> mData = new ArrayList<>();
    private AddNodeAdapter defAdapter;
    private AddNodeWarnDialog addNodeWarnDialog;
    private InputNodeDialog inputNodeDialog;
    private int addNum = 0;
    private MyAlertDialog myAlertDialog;
    private int choosePos;
    private BlockChainApi mApi = new BlockChainApi();
    private Gson gson = new Gson();
    private String nodeString;

    @Override
    public int initContentView() {
        type = getIntent().getIntExtra("type", 0);
        name = getIntent().getStringExtra("name");
        return R.layout.activity_addnode;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        if (type == WalletUtil.MCC_COIN) {
            finish();
            return;
        }

        tvTitle.setText(name + getString(R.string.node_settings));
        tvAction.setVisibility(View.VISIBLE);
        tvAction.setText(getString(R.string.save));
        myAlertDialog = new MyAlertDialog(this, getString(R.string.delete_node_address));
        myAlertDialog.setonclick(new MyAlertDialog.Onclick() {
            @Override
            public void Yes() {
                myAlertDialog.dismiss();
                SettingNodeEntity bean = mData.remove(choosePos);
                if (null != bean && bean.getChoose()) {
                    addNum++;
                    mData.get(0).setChoose(true);
                    nodeString = mData.get(0).getNodeUrl();
                    WalletUtil.saveDefNode(type, nodeString);
                    DBManager.getInstance(AddNodeActivity.this).insertListNode(mData);
                }
                DBManager.getInstance(AddNodeActivity.this).deleteNode(bean);
                defAdapter.bindData(mData);
                defAdapter.notifyDataSetChanged();
            }

            @Override
            public void No() {
                myAlertDialog.dismiss();
            }
        });
        defAdapter = new AddNodeAdapter(this);
        defNodeList.setAdapter(defAdapter);
        defNodeList.setOnItemClickListener((parent, view, position, id) -> {
            for (int i = 0; i < mData.size(); i++) {
                mData.get(i).setChoose(false);
            }
            mData.get(position).setChoose(true);
            nodeString = mData.get(position).getNodeUrl();
            defAdapter.notifyDataSetChanged();
        });
        defNodeList.setOnItemLongClickListener((parent, view, position, id) -> {
            if (mData.get(position).getIsDef() == 1) {
                choosePos = position;
                myAlertDialog.show();
            }
            return true;
        });
        addNodeWarnDialog = new AddNodeWarnDialog(this);
        addNodeWarnDialog.setTrans(new AddNodeWarnDialog.AddNode() {
            @Override
            public void goAddNode() {
                inputNodeDialog.show();
            }
        });
        inputNodeDialog = new InputNodeDialog(this, getString(R.string.node_is) + name + "");
        inputNodeDialog.setonclick(new InputNodeDialog.Onclick() {
            @Override
            public void Yes(String rpcUrl) {
                
                if (defAdapter.hasAddThisNode(rpcUrl)) {
                    ToastUtil.showToast(getString(R.string.this_node_has_add));
                    return;
                }
                inputNodeDialog.dismiss();
                if (type == WalletUtil.TRX_COIN) {
                    check(rpcUrl);
                } else {
                    SettingNodeEntity bean = new SettingNodeEntity(name, rpcUrl, type, false, 1);
                    DBManager.getInstance(AddNodeActivity.this).insertNode(bean);
                    initData();
                }
            }

            @Override
            public void No() {
                inputNodeDialog.dismiss();
            }
        });
        
        if (type == WalletUtil.MCC_COIN) {
            View view = findViewById(R.id.add_node);
            view.setVisibility(View.GONE);
        }
    }

    @Override
    public void initData() {
        mData = DBManager.getInstance(this).getAllTypeNode(type);
        defAdapter.bindData(mData);
        defAdapter.notifyDataSetChanged();
        checkUrlTime();
    }

    private synchronized void updateTime(String url, long pingTime) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        mCheckingUrlMap.remove(url);
        if (null == defAdapter || isDestroyed() || isFinishing()) {
            return;
        }
        runOnUiThread(() -> {
            defAdapter.updateTime(url, pingTime);
        });

    }

    private ExecutorService mExService;
    private Map<String, Integer> mCheckingUrlMap = new ConcurrentHashMap<>();

    private void checkUrlTime() {
        if (null == mData || mData.size() == 0) {
            return;
        }
        if (isFinishing() || isDestroyed()) {
            return;
        }
        
        if (null == mExService) {
            mExService = Executors.newFixedThreadPool(3);
        }
        for (int i = 0; i < mData.size(); i++) {
            String url = mData.get(i).getNodeUrl();
            if (TextUtils.isEmpty(url) || mCheckingUrlMap.containsKey(url)) {
                LogUtil.w(url + " is checking return.");
                continue;
            }
            mCheckingUrlMap.put(url, 1);
            mExService.submit(new CheckUrlTimeTask(url));
        }
    }

    private class CheckUrlTimeTask implements Runnable {
        private String url;

        public CheckUrlTimeTask(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            try {
                long time = NetWorkUtils.pingIpAddress2(url);
                runOnUiThread(() -> {
                    updateTime(url, time);
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    updateTime(url, -1);
                });
            }

        }
    }

    
    private void stopCalculateTask() {
        if (null == mExService) {
            return;
        }
        try {
            
            mExService.shutdown();
            
            if (!mExService.awaitTermination(1, TimeUnit.SECONDS)) {
                
                mExService.shutdownNow();
            }
        } catch (Exception e) {
            e.printStackTrace();
            mExService.shutdownNow();
        }
    }

    private void check(final String url) {
        mLoadingDialog.show();
        Map<String, Object> params = new TreeMap<>();
        if (type == WalletUtil.XRP_COIN) {
            params.put("method", "server_info");
        } else {
            params.put("jsonrpc", "2.0");
            params.put("method", "eth_protocolVersion");
            params.put("id", 67);
            params.put("params", new ArrayList());
        }
        mApi.checkUrl(url, gson.toJson(params)).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        mLoadingDialog.dismiss();
                        if (gson.toJson(o).indexOf("result") > -1) {
                            SettingNodeEntity bean = new SettingNodeEntity(name, url, type, false, 1);
                            DBManager.getInstance(AddNodeActivity.this).insertNode(bean);
                            initData();
                        } else {
                            ToastUtil.showToast(getString(R.string.input_node_is_wrong));
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mLoadingDialog.dismiss();
                        ToastUtil.showToast(getString(R.string.input_node_is_wrong));
                    }
                });
    }


    @OnClick({R2.id.tv_back, R2.id.add_node, R2.id.tv_action})
    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.tv_back) {
            if (addNum > 0) {
                setResult(RESULT_OK);
            }
            finish();
        } else if (id == R.id.add_node) {
            addNodeWarnDialog.show();

        } else if (id == R.id.tv_action) {
            DBManager.getInstance(this).insertListNode(mData);
            setResult(RESULT_OK);
            WalletUtil.saveDefNode(type, nodeString);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (addNum > 0) {
            setResult(RESULT_OK);
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        
        stopCalculateTask();
        super.onDestroy();

    }
}
