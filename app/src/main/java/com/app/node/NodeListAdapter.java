package com.app.node;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.core.content.ContextCompat;

import com.app.R;
import com.app.databinding.ItemNodeList2Binding;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.SettingNodeEntity;
import com.wallet.ctc.model.blockchain.AssertBean;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;


public class NodeListAdapter extends BaseAdapter {

    private Context mContext;
    private List<SettingNodeEntity> mDatas;
    private int mDecimal = 18;


    public NodeListAdapter(Context context) {
        this.mContext = context;
        this.mDatas = new ArrayList<>();
        
        List<AssertBean> assets = WalletDBUtil.getInstent(mContext).getMustWallet(WalletUtil.MCC_COIN);
        mDecimal = assets.get(0).getDecimal();
        if (mDecimal == 0) {
            mDecimal = 18;
        }
    }

    
    public void bindDatas(List<SettingNodeEntity> datas) {
        if (null != datas) {
            mDatas.clear();
            mDatas.addAll(datas);
            notifyDataSetChanged();
        }
    }


    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public SettingNodeEntity getItem(int i) {
        return mDatas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ItemNodeList2Binding binding;
        if (null == view) {
            binding = ItemNodeList2Binding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
            view = binding.getRoot();
            view.setTag(binding);
        } else {
            binding = (ItemNodeList2Binding) view.getTag();
        }
        setData(i, binding);
        return view;
    }


    
    private void setData(int position, ItemNodeList2Binding viewBinding) {
        SettingNodeEntity data = mDatas.get(position);
        String customTip = "";
        if (data.getIsDef() == 1) {
            customTip = mContext.getString(R.string.custom_node_title_tip);
        }
        if (null != mIClick && getCount() == 1) {
            mIClick.click(data);
        }
        viewBinding.nameTv.setText(data.getNodeName() + customTip);
        if (data.isChoose()) {
            viewBinding.nowFlagTv.setVisibility(View.VISIBLE);

        } else {
            viewBinding.nowFlagTv.setVisibility(View.GONE);
        }

        long pingTime = data.getPingTime();
        if (pingTime > 0 && pingTime < 500) {
            
            viewBinding.useTimeTv.setText(mContext.getString(R.string.item_node_delay_tips) + pingTime + "ms");
            viewBinding.useTimeTv.setTextColor(ContextCompat.getColor(mContext, R.color.default_theme_color));
            viewBinding.speedTv.setText(mContext.getString(R.string.node_speed_fast));
            viewBinding.speedTv.setBackgroundResource(R.drawable.round_green_c2);
        } else if (pingTime > 500 && pingTime < 1000) {
            viewBinding.useTimeTv.setText(mContext.getString(R.string.item_node_delay_tips) + pingTime + "ms");
            viewBinding.useTimeTv.setTextColor(Color.parseColor("#FFB319"));
            viewBinding.speedTv.setText(mContext.getString(R.string.node_speed_normal));
            viewBinding.speedTv.setBackgroundResource(R.drawable.round_yellow_c2);
        } else if (pingTime > 1000) {
            viewBinding.useTimeTv.setText(mContext.getString(R.string.item_node_delay_tips) + pingTime + "ms");
            viewBinding.useTimeTv.setTextColor(Color.parseColor("#FF5500"));
            viewBinding.speedTv.setText(mContext.getString(R.string.node_speed_slow));
            viewBinding.speedTv.setBackgroundResource(R.drawable.round_red_c2);
        } else if (pingTime < 0) {
            viewBinding.useTimeTv.setText(R.string.item_node_error);
            viewBinding.useTimeTv.setTextColor(Color.parseColor("#CCCCCC"));
            viewBinding.speedTv.setText(mContext.getString(R.string.node_offline));
            viewBinding.speedTv.setBackgroundResource(R.drawable.round_gray_c2);
        } else {
            viewBinding.useTimeTv.setText(mContext.getString(R.string.item_node_delay_tips)+"--ms");
            viewBinding.useTimeTv.setTextColor(ContextCompat.getColor(mContext, R.color.default_text_three_color));
            viewBinding.speedTv.setText("- -");
            viewBinding.speedTv.setBackgroundColor(ContextCompat.getColor(mContext, R.color.transparent));
        }

        String tokenNum = getTenDecimalValue(data.getTokenNum());
        if (!TextUtils.isEmpty(tokenNum)) {
            String coinName = mContext.getString(R.string.default_token_name).toUpperCase();
            viewBinding.pledgeNumTv.setText(tokenNum + " "+coinName);
        } else {
            viewBinding.pledgeNumTv.setText("- -");
        }
        long runTime = data.getOnLineTime();
        if (runTime > 0) {
            viewBinding.runDayTv.setText(getDayStr(runTime));
        } else {
            viewBinding.runDayTv.setText("- -");
        }

    }

    
    private String getDayStr(long seconds) {
        
        if (seconds <= 86400) {
            return "1 ";
        } else {
            long days = seconds / 86400;
            return days + "";
        }
    }

    
    private String getTenDecimalValue(String bigNum) {
        if (TextUtils.isEmpty(bigNum)) {
            return bigNum;
        }
        return new BigDecimal(bigNum).divide(new BigDecimal(Math.pow(10, mDecimal)), 2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }


    private IClick mIClick;

    public void setIClick(IClick IClick) {
        mIClick = IClick;
    }

    public interface IClick {
        void click(SettingNodeEntity entity);
    }

}
