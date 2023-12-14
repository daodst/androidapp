package com.app.home.ui.vote.create.params;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.databinding.ActivityStartParamsVoteBinding;
import com.app.databinding.ItemParamsVoteMenuBinding;

import java.util.ArrayList;
import java.util.List;

import common.app.base.BaseActivity;
import common.app.my.RxNotice;
import common.app.pojo.NameIdBean;
import common.app.ui.view.TitleBarView;


public class StartParamsVoteActivity extends BaseActivity {

    ActivityStartParamsVoteBinding mViewB;
    @Override
    public View initBindingView(Bundle savedInstanceState) {
        mViewB = ActivityStartParamsVoteBinding.inflate(LayoutInflater.from(this));
        return mViewB.getRoot();
    }

    @Override
    public void initView(@Nullable View view) {
        mViewB.titleBar.setOnTitleBarClickListener(new TitleBarView.TitleBarClickListener() {
            @Override
            public void leftClick() {
                finish();
            }

            @Override
            public void rightClick() {

            }
        });
        MenuAdapter adapter = new MenuAdapter();
        mViewB.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mViewB.recyclerView.setAdapter(adapter);
        addSubscription();
    }

    @Override
    public void succeed(Object obj) {
        if (obj instanceof RxNotice) {
            RxNotice notice = (RxNotice) obj;
            if (notice.mType == RxNotice.MSG_SUBMIT_VOTE) {
                finish();
            }
        }
    }


    public class MenuAdapter extends RecyclerView.Adapter<MenuViewHolder>{
        private List<NameIdBean> mDatas;
        public MenuAdapter() {
            mDatas = new ArrayList<>();
            mDatas.addAll(VoteParamsData.getDatas());
        }

        @NonNull
        @Override
        public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MenuViewHolder(ItemParamsVoteMenuBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
            holder.setData(mDatas.get(position));
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder{
        ItemParamsVoteMenuBinding vbinding;
        public MenuViewHolder(@NonNull ItemParamsVoteMenuBinding binding) {
            super(binding.getRoot());
            vbinding = binding;
        }

        public void setData(NameIdBean data) {
            vbinding.titleTv.setText(data.id);
            vbinding.descTv.setText(data.name);
            itemView.setOnClickListener(view -> {
                startActivity(CreateParamsVoteActivity.getIntent(StartParamsVoteActivity.this, data.id));
            });
        }
    }
}
