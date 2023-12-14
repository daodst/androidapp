

package com.wallet.ctc.ui.blockchain.seach;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.api.blockchain.BlockChainApi;
import com.wallet.ctc.api.me.MeApi;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.base.BaseEntity;
import com.wallet.ctc.base.BaseWebViewActivity;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.NewAssertBean;
import com.wallet.ctc.ui.blockchain.choosenode.ChooseNodeActivity;
import com.wallet.ctc.ui.blockchain.creattoken.CreatTokenOne;
import com.wallet.ctc.util.SettingPrefUtil;
import com.wallet.ctc.view.popwindow.SeachTypePopWindow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.app.base.fragment.mall.catcherror.BaseSubscriber;
import common.app.mall.util.ToastUtil;
import common.app.ui.view.PullToRefreshLayout;
import common.app.ui.view.PullableListView;
import io.reactivex.android.schedulers.AndroidSchedulers;



public class SeachActivity extends BaseActivity {

    @BindView(R2.id.tv_title)
    EditText tvTitle;
    @BindView(R2.id.notoken)
    TextView notoken;
    @BindView(R2.id.nodatatxt)
    TextView nodatatxt;
    @BindView(R2.id.nodata)
    LinearLayout nodata;
    @BindView(R2.id.choose_type)
    TextView chooseType;
    @BindView(R2.id.wallet_list)
    PullableListView walletList;
    @BindView(R2.id.refresh_view)
    PullToRefreshLayout refreshView;
    private SeachAdapter mAdapter;
    private SeachEthAdapter mEthAdapter;
    private List<NewAssertBean> seachList = new ArrayList<>();
    private List<AssertBean> seachethList = new ArrayList<>();
    private Gson gson = new Gson();
    private SeachTypePopWindow mPop;
    private BlockChainApi mApi = new BlockChainApi();
    private MeApi meApi = new MeApi();
    private int type = 0;
    private int page=1;

    @Override
    public int initContentView() {
        return R.layout.activity_seach;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        mAdapter = new SeachAdapter(this);
        mEthAdapter = new SeachEthAdapter(this);
        List<AssertBean> list = walletDBUtil.canChooseWallet(0);
        list.addAll(walletDBUtil.getMustWallet(0));
        mAdapter.bindAddedData(list);
        List<AssertBean> list1 = walletDBUtil.canChooseWallet(1);
        list1.addAll(walletDBUtil.getMustWallet(1));
        mEthAdapter.bindAddedData(list1);
        mPop = new SeachTypePopWindow(this);
        walletList.setAdapter(mAdapter);
        tvTitle.setOnKeyListener(new View.OnKeyListener() {

            @Override

            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(SeachActivity.this.getCurrentFocus()
                                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    
                    if (type == 0) {
                        seach();
                    } else {
                        seachEth();
                    }
                }
                return false;
            }
        });
        refreshView.releaseNotPull();
        refreshView.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                page=1;
                seachEth();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                seachEth();
            }
        });
        notoken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        mPop.setItemListener(new SeachTypePopWindow.IOnItemSelectListener() {
            @Override
            public void choose(int position) {
                mPop.dismiss();
                if (position == 0) {
                    type = 0;
                    chooseType.setText("DM");
                    refreshView.releaseNotPull();
                    seachList.clear();
                    page=1;
                    mAdapter.notifyDataSetChanged();
                    walletList.setAdapter(mAdapter);
                } else {
                    type = 1;
                    chooseType.setText("");
                    refreshView.releaseCanPull();
                    page=1;
                    seachethList.clear();
                    mEthAdapter.notifyDataSetChanged();
                    walletList.setAdapter(mEthAdapter);
                }
            }
        });
    }

    @Override
    public void initData() {

    }

    private void seach() {
        String key = tvTitle.getText().toString().trim();
        if (TextUtils.isEmpty(key)) {
            return;
        }
        Map<String, Object> params2 = new TreeMap();
        params2.put("arg", key);
        mApi.getTkList(params2,4).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseEntity>(this) {
                    @Override
                    public void onNexts(BaseEntity baseEntity) {
                        if (baseEntity.getStatus() == 1) {
                            seachList = gson.fromJson(gson.toJson(baseEntity.getData()), new TypeToken<List<NewAssertBean>>() {
                            }.getType());
                            if (seachList == null || seachList.size() < 1) {
                                refreshView.setVisibility(View.GONE);
                                nodata.setVisibility(View.VISIBLE);
                            } else {
                                mAdapter.bindData(seachList, type);
                                mAdapter.notifyDataSetChanged();
                                refreshView.setVisibility(View.VISIBLE);
                                nodata.setVisibility(View.GONE);
                            }

                        } else {
                            ToastUtil.showToast(baseEntity.getInfo());
                        }
                    }
                });
    }

    private void seachEth() {
        String key = tvTitle.getText().toString().trim();
        if (TextUtils.isEmpty(key)) {
            return;
        }
        Map<String, Object> params2 = new TreeMap();
        params2.put("search", key);
        params2.put("page",page);
        meApi.seachToken(params2,type).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseEntity>(this) {
                    @Override
                    public void onNexts(BaseEntity baseEntity) {
                        if(page==1){
                            refreshView.refreshFinish(PullToRefreshLayout.SUCCEED);
                        }else {
                            refreshView.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                        }
                        if (baseEntity.getStatus() == 1) {
                            if(page==1){
                                seachethList.clear();
                            }
                            List<AssertBean> list= gson.fromJson(gson.toJson(baseEntity.getData()), new TypeToken<List<AssertBean>>() {
                            }.getType());
                            if(null!=list&&list.size()>0){
                                page++;
                            }
                            seachethList.addAll(list);
                            if (seachethList == null || seachethList.size() < 1) {
                                refreshView.setVisibility(View.GONE);
                                nodata.setVisibility(View.VISIBLE);
                            } else {
                                mEthAdapter.notifyDataSetChanged();
                                refreshView.setVisibility(View.VISIBLE);
                                nodata.setVisibility(View.GONE);
                            }

                        } else {
                            ToastUtil.showToast(baseEntity.getInfo());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if(null==refreshView){
                            return;
                        }
                        if(page==1){
                            refreshView.refreshFinish(PullToRefreshLayout.FAIL);
                        }else {
                            refreshView.loadmoreFinish(PullToRefreshLayout.FAIL);
                        }
                    }
                });
    }


    private Intent intent;
    @OnClick({R2.id.tv_back, R2.id.push_token, R2.id.choose_type})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.tv_back) {
            finish();

        } else if (i == R.id.push_token) {
            if (type == 1) {
                Intent intent = new Intent(SeachActivity.this, BaseWebViewActivity.class);
                intent.putExtra("type", 1);
                intent.putExtra("sysName", "token");
                intent.putExtra("title", getString(R.string.push_new_token));
                startActivity(intent);
                return;
            }
            if (SettingPrefUtil.getNodeType(this) == 1) {
                Intent intent = new Intent(this, CreatTokenOne.class);
                startActivity(intent);
                finish();
            } else {
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setNegativeButton("", null).setPositiveButton("", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                intent = new Intent(SeachActivity.this, ChooseNodeActivity.class);
                                intent.putExtra("type", 1);
                                startActivity(intent);
                            }
                        }).setMessage("，，。，。").create();
                dialog.show();
            }

        } else if (i == R.id.choose_type) {
            mPop.showPopupWindow(chooseType);

        } else {
        }
    }
}
