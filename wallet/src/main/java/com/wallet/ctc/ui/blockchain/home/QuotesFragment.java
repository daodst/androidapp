

package com.wallet.ctc.ui.blockchain.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.api.me.MeApi;
import com.wallet.ctc.base.BaseEntity;
import com.wallet.ctc.base.BaseFragment;
import com.wallet.ctc.model.blockchain.NewQuotesBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import common.app.base.fragment.mall.catcherror.BaseSubscriber;
import common.app.mall.util.ToastUtil;
import common.app.ui.view.PullToRefreshLayout;
import common.app.ui.view.PullableListView;
import io.reactivex.android.schedulers.AndroidSchedulers;


public class QuotesFragment extends BaseFragment{

    protected Unbinder mUnbinder;

    @BindView(R2.id.nodata)
    LinearLayout nodata;
    @BindView(R2.id.content_view)
    PullableListView quotesList;
    @BindView(R2.id.refresh_view)
    PullToRefreshLayout refreshView;
    private QuotesListAdapter mAdapter;
    private List<NewQuotesBean> list=new ArrayList<>();
    private String type="huobi";
    private Gson gson=new Gson();
    private MeApi mApi=new MeApi();
    private int page=1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quotes, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mAdapter = new QuotesListAdapter(getActivity());
        String dcu="";
        mAdapter.bindType(dcu);
        mAdapter.bindData(list);
        quotesList.setAdapter(mAdapter);
        quotesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        refreshView.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                page=1;
                getData();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                getData();
            }
        });
        getData();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    
    @Override
    public void getData() {
        Map<String,Object> param=new TreeMap<>();
        param.put("tag",type);
        param.put("start",((page-1)*50)+"");
        param.put("limit",50);
        mApi.getQuotesList(param).observeOn(AndroidSchedulers.mainThread())
            .subscribe(new BaseSubscriber<BaseEntity>(getActivity()){
                @Override
                public void onNexts(BaseEntity baseEntity) throws Exception {
                    if(page==1){
                        refreshView.refreshFinish(PullToRefreshLayout.SUCCEED);
                    }else {
                        refreshView.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                    }
                    if(baseEntity.getStatus()==1){
                        List<NewQuotesBean> data=gson.fromJson(gson.toJson(baseEntity.getData()), new TypeToken<List<NewQuotesBean>>() {
                        }.getType());
                        if(page==1){
                            list.clear();
                        }
                        if(null!=data&&data.size()>0){
                            page++;
                        }
                        list.addAll(data);
                        if(null==list||list.size()<1){
                            nodata.setVisibility(View.VISIBLE);
                            refreshView.setVisibility(View.GONE);
                        }else {
                            nodata.setVisibility(View.GONE);
                            refreshView.setVisibility(View.VISIBLE);
                        }
                        mAdapter.bindData(list);
                        mAdapter.notifyDataSetChanged();
                    }else {
                        ToastUtil.showToast(baseEntity.getInfo());
                    }
                }

                @Override
                public void onError(Throwable e) {
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

    public void setRMBType(String type) {
        if(null!=mAdapter) {
            mAdapter.bindType(type);
            mAdapter.notifyDataSetChanged();
        }
    }

    public void setType(String type) {
            this.type = type;

    }
}
