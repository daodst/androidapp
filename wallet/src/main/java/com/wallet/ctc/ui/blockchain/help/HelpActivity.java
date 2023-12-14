

package com.wallet.ctc.ui.blockchain.help;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.api.me.MeApi;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.base.BaseEntity;
import com.wallet.ctc.base.BaseWebViewActivity;
import com.wallet.ctc.model.blockchain.ArticleBean;
import com.wallet.ctc.model.blockchain.BaseArticleBean;

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
import io.reactivex.android.schedulers.AndroidSchedulers;



public class HelpActivity extends BaseActivity {

    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.tv_action)
    TextView tvAction;
    @BindView(R2.id.content_view)
    ListView assetsList;
    @BindView(R2.id.nodata)
    LinearLayout nodata;
    @BindView(R2.id.refresh_view)
    PullToRefreshLayout pullView;
    private HelpAdapter mAdapter;
    private Gson gson = new Gson();
    private MeApi mApi = new MeApi();
    private List<ArticleBean> list = new ArrayList<>();
    private int page = 1;
    private int ref=0;

    @Override
    public int initContentView() {
        return R.layout.activity_notice;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        tvTitle.setText(getString(R.string.help));
        tvAction.setTextColor(0xff4E569C);
        mAdapter = new HelpAdapter(this);
        mAdapter.bindData(list);
        assetsList.setAdapter(mAdapter);
        pullView.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                page=1;
                ref=0;
                initData();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                ref=1;
                initData();
            }
        });
        assetsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(HelpActivity.this, BaseWebViewActivity.class);
                intent.putExtra("url",list.get(position).getArticle_id());
                intent.putExtra("title",list.get(position).getTitle());
                intent.putExtra("type",2);
                startActivity(intent);
            }
        });
    }

    @Override
    public void initData() {
        Map<String, Object> params = new TreeMap();
        params.put("page", page);
        mApi.getHelpList(params).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseEntity>(this) {
                    @Override
                    public void onNexts(BaseEntity baseEntity) {
                        if(ref==0){
                            pullView.refreshFinish(PullToRefreshLayout.SUCCEED);
                        }else {
                            pullView.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                        }
                        if (baseEntity.getStatus() == 1) {
                            if(page==1){
                                list.clear();
                            }
                            BaseArticleBean baseAdverEntity=gson.fromJson(gson.toJson(baseEntity.getData()),BaseArticleBean.class);
                            List<ArticleBean> data=baseAdverEntity.getData();
                            if(data==null||data.size()<1){
                                if(page>1){
                                    ToastUtil.showToast(getString(R.string.nomore));
                                }
                            }else{
                                page++;
                            }
                            list.addAll(data);
                            if(list!=null&&list.size()>0){
                                pullView.setVisibility(View.VISIBLE);
                                nodata.setVisibility(View.GONE);
                            }else {
                                pullView.setVisibility(View.GONE);
                                nodata.setVisibility(View.VISIBLE);
                            }
                            mAdapter.notifyDataSetChanged();
                        } else {
                            ToastUtil.showToast(baseEntity.getInfo());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if(null==pullView){
                            return;
                        }
                        if(ref==0){
                            pullView.refreshFinish(PullToRefreshLayout.FAIL);
                        }else {
                            pullView.loadmoreFinish(PullToRefreshLayout.FAIL);
                        }
                    }
                });
    }

    @OnClick({R2.id.tv_back, R2.id.tv_action})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.tv_back) {
            finish();

        } else if (i == R.id.tv_action) {


        } else {
        }
    }


}
