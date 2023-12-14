package com.wallet.ctc.ui.blockchain.issuance.rec.adapter;

import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.R;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.databinding.ItemIssuanceCoinRecBinding;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.EthAssertBean;
import com.wallet.ctc.ui.blockchain.issuance.pojo.IssuanceCoinItem;
import com.wallet.ctc.util.SettingPrefUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import common.app.mall.util.ToastUtil;




public class IssuanceCoinAdapter extends RecyclerView.Adapter<IssuanceCoinAdapter.ViewHolder> {


    private List<IssuanceCoinItem> mCoinItems;
    
    private List<AssertBean> mAddList = new ArrayList<>();
    private List<AssertBean> mChooseList = new ArrayList<>();
    private WalletDBUtil walletDBUtil;

    public IssuanceCoinAdapter(Context context) {
        walletDBUtil = WalletDBUtil.getInstent(context);
    }

    public void setAddList(List<AssertBean> addList, List<AssertBean> choose) {
        mAddList = addList;
        mChooseList = choose;
        notifyDataSetChanged();
    }

    public void setCoinItems(List<IssuanceCoinItem> coinItems) {
        mCoinItems = coinItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_issuance_coin_rec, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        IssuanceCoinItem item = mCoinItems.get(position);
        Context context = holder.itemView.getContext();
        ItemIssuanceCoinRecBinding binding = holder.mBinding;
        binding.itemIssuanceCoinName.setText(item.symbol);
        binding.itemIssuanceCoinExchange.setText("/" + BuildConfig.EVMOS_FAKE_UNINT);

        binding.itemIssuanceCoinTime.setText(formatTime(item.create_timestamp));
        binding.itemIssuanceCoinAddress.setText(item.token_address);
        binding.itemIssuanceCoinCopy.setOnClickListener(v -> {
            clipboardText(context, item.token_address);
        });
        if (null == item.mEthAssertBean) {
            EthAssertBean bean = new EthAssertBean();
            bean.setSymbol(item.symbol);
            bean.setAddress(item.token_address);

            String logo = item.logo;
            if (TextUtils.isEmpty(logo)) {
                logo = "res://drawable/coin_default";
            }
            bean.setLogo(logo);
            bean.setName(item.name);
            bean.setDecimals(item.decimal);
            item.mEthAssertBean = bean;
        }


        binding.itemIssuanceCoinAddWallet.setVisibility(isAdd(item.mEthAssertBean, mChooseList) ? View.GONE : View.VISIBLE);
        binding.itemIssuanceCoinAddWallet.setOnClickListener(v -> {

            EthAssertBean bean = item.mEthAssertBean;
            AssertBean assbean = new AssertBean(bean.getLogo(), bean.getSymbol(), bean.getName(), bean.getAddress(), "", bean.getDecimals(), WalletUtil.MCC_COIN, 2);
            if (isAdd(bean, mAddList)) {
                
                mChooseList.add(assbean);
                assbean.setWalletAddress(SettingPrefUtil.getWalletAddress(context));
                walletDBUtil.addAssets(assbean);
            } else {
                
                assbean.setWalletAddress("");
                mAddList.add(assbean);
                walletDBUtil.addAssets(assbean);
                AssertBean assbean2 = new AssertBean(bean.getLogo(), bean.getSymbol(), bean.getName(), bean.getAddress(), "", bean.getDecimals(), WalletUtil.MCC_COIN, 2);
                assbean2.setWalletAddress(SettingPrefUtil.getWalletAddress(context));
                walletDBUtil.addAssets(assbean2);
                ToastUtil.showToast(context.getString(R.string.issuance_coin_add_success));

                
                mChooseList.add(assbean);
            }
            notifyDataSetChanged();
        });
        

        try {
            
            BigDecimal yi = new BigDecimal("100000000");
            
            BigDecimal million = new BigDecimal("10000");

            BigDecimal value = new BigDecimal(item.amount);

            if (value.compareTo(yi) >= 0) {
                String plainString = value.divide(yi, 2, RoundingMode.DOWN).stripTrailingZeros().toPlainString();
                binding.itemIssuanceCoinNum.setText(plainString + context.getString(R.string.yi_unit));
            } else if (value.compareTo(million) >= 0) {
                String plainString = value.divide(million, 2, RoundingMode.DOWN).stripTrailingZeros().toPlainString();
                binding.itemIssuanceCoinNum.setText(plainString + context.getString(R.string.million_unint));
            } else {
                binding.itemIssuanceCoinNum.setText(item.amount + context.getString(R.string.only_unint));
            }
        } catch (Exception e) {
            binding.itemIssuanceCoinNum.setText(item.amount + context.getString(R.string.only_unint));
        }
    }

    private boolean isAdd(EthAssertBean mBean, List<AssertBean> base) {
        if (null == base) {
            return false;
        }
        boolean isadd = false;
        for (int i = 0; i < base.size(); i++) {
            String address = base.get(i).getContract();
            if (!TextUtils.isEmpty(address) && address.equalsIgnoreCase(mBean.getAddress())) {
                mBean.setSymbol(base.get(i).getShort_name());
                
                mBean.setAddress(address);
                isadd = true;
                break;
            }
        }
        return isadd;
    }

    public static final String formatTime(long timeStr) {
        
        try {
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("MM/dd HH:mm");
            return dateFormat2.format(timeStr * 1000);
        } catch (Exception e) {
        }
        return "";
    }

    protected final void clipboardText(Context context, String content) {
        
        
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        
        cm.setText(content);
        Toast.makeText(context, context.getString(common.app.R.string.clip_over), Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return null == mCoinItems ? 0 : mCoinItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemIssuanceCoinRecBinding mBinding;

        public ViewHolder(View view) {
            super(view);
            mBinding = ItemIssuanceCoinRecBinding.bind(view);

        }
    }
}
