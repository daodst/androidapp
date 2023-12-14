package com.app.home.ui.vote.create.params;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.R;
import com.app.databinding.ActivityCreateParamsVoteBinding;
import com.app.databinding.ItemParamsVoteInputBinding;
import com.app.home.ui.vote.create.CreateVoteActivity;
import com.app.pojo.VoteParamsBean;
import com.app.pojo.VoteParamsInfoBean;

import java.util.ArrayList;
import java.util.List;

import common.app.base.BaseActivity;
import common.app.my.RxNotice;
import common.app.ui.view.TitleBarView;


public class CreateParamsVoteActivity extends BaseActivity<ParamsVoteViewModel> {

    private String mSubSpaceName;
    private static final String KEY_SPACE = "space";
    private MenuInputAdapter mAdapter;
    public static Intent getIntent(Context from, String subSpaceName) {
        Intent intent = new Intent(from, CreateParamsVoteActivity.class);
        intent.putExtra(KEY_SPACE, subSpaceName);
        return intent;
    }

    @Override
    public void initParam() {
        mSubSpaceName = getIntent().getStringExtra(KEY_SPACE);
        if (TextUtils.isEmpty(mSubSpaceName)) {
            showToast(R.string.data_error);
            setForceIntercept(true);
            finish();
            return;
        }
    }

    ActivityCreateParamsVoteBinding mViewB;
    @Override
    public View initBindingView(Bundle savedInstanceState) {
        mViewB = ActivityCreateParamsVoteBinding.inflate(LayoutInflater.from(this));
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
        
        
        mViewB.nextBtn.setOnClickListener(view1 -> {
            ArrayList<VoteParamsBean> list = mAdapter.getInputParams();
            if (list == null || list.size() == 0) {
                showToast(R.string.vote_input_params_tips);
                return;
            }
            startActivity(CreateVoteActivity.getParamsIntent(CreateParamsVoteActivity.this, list));
        });

        mAdapter = new MenuInputAdapter();
        mViewB.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mViewB.recyclerView.setAdapter(mAdapter);
        addSubscription();
    }

    @Override
    public void initData() {
        getViewModel().observe(getViewModel().mListLD, list->{
            mAdapter.setDatas(list);
        });
        getViewModel().getDatas(mSubSpaceName);
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

    public class MenuInputAdapter extends RecyclerView.Adapter<MenuViewHolder>{
        private List<VoteParamsInfoBean> mDatas;
        public MenuInputAdapter() {
            mDatas = new ArrayList<>();
        }

        public void setDatas(List<VoteParamsInfoBean> datas) {
            if (datas != null) {
                mDatas.clear();
                mDatas.addAll(datas);
                notifyDataSetChanged();
            }
        }

        public void onInputText(int position, String text) {
            if (position < mDatas.size()) {
                mDatas.get(position).inputValue = text;
            }
        }

        public ArrayList<VoteParamsBean> getInputParams() {
            ArrayList<VoteParamsBean> list = new ArrayList<>();
            if (null == mDatas || mDatas.size() == 0) {
                return list;
            }
            for (int i=0; i<mDatas.size(); i++) {
                VoteParamsInfoBean info = mDatas.get(i);
                if (info.isChangeSeeting()) {
                    list.add(new VoteParamsBean(mSubSpaceName, info.key, info.getInputValue()));
                }
            }
            return list;
        }

        @NonNull
        @Override
        public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MenuViewHolder(ItemParamsVoteInputBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
            holder.setData(mDatas.get(position), position);
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder{
        ItemParamsVoteInputBinding vbinding;
        TextWatcher textWatcher;
        public MenuViewHolder(@NonNull ItemParamsVoteInputBinding binding) {
            super(binding.getRoot());
            vbinding = binding;
        }

        public void setData(VoteParamsInfoBean data, int position) {
            vbinding.titleLayout.setOnClickListener(view -> {
                if (vbinding.editLayout.getVisibility() == View.GONE) {
                    vbinding.editLayout.setVisibility(View.VISIBLE);
                } else {
                    vbinding.editLayout.setVisibility(View.GONE);
                }
            });
            vbinding.spaceTv.setText(mSubSpaceName+" > ");
            vbinding.subSpaceTv.setText(data.key);
            vbinding.subSpace2Tv.setText(data.key);
            vbinding.nowValueTv.setText(data.getNowValue());
            vbinding.valueEdit.setText(data.inputValue);
            vbinding.keyDescTv.setText(data.keyDesc);
            vbinding.valueDesc.setText(data.valueDesc);
            if (data.isBigAmount) {
                vbinding.valueEdit.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
            } else {
                vbinding.valueEdit.setInputType(InputType.TYPE_CLASS_TEXT);
            }
            if (null != textWatcher) {
                vbinding.valueEdit.removeTextChangedListener(textWatcher);
                textWatcher = null;
            }
            textWatcher = new InputWatcher(position);
            vbinding.valueEdit.addTextChangedListener(textWatcher);

        }
    }


    public class InputWatcher implements TextWatcher{

        private int mPosition;
        public InputWatcher(int position) {
            this.mPosition = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String text = editable.toString().trim();
            mAdapter.onInputText(mPosition, text);
        }
    }


}
