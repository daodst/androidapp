package com.app.home.ui.ver.detial.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.app.R;
import com.app.databinding.DialogPledgedTokenBinding;
import com.app.home.pojo.PledgedTokensDidListEntity;
import com.app.home.ui.ver.detial.adapter.PledgedTokensAdapter;
import com.lxj.xpopup.core.CenterPopupView;
import com.lxj.xpopup.util.XPopupUtils;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.model.blockchain.AssertBean;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import common.app.mall.util.ToastUtil;
import common.app.utils.AllUtils;
import im.vector.app.core.platform.SimpleTextWatcher;


public class PledgedTokensPopup extends CenterPopupView {
    public static final String PARAM_NAME = "param_name";
    public static final String PARAM_AMOUNT = "PARAM_amount";

    private DialogPledgedTokenBinding mBinding;
    private final String mName;
    private String mAmount;
    
    private int mDecimal = 18;

    private OnDidSegmentList mOnDidSegmentList;

    private boolean isGateway = false;

    
    private final List<PledgedTokensDidListEntity> mSelectedList = new ArrayList<>();
    private int maxSelect = 0;
    private double mTax = 0.0;
    private String mBlockHeight = "0";
    private PledgedTokensAdapter mTokensAdapter;

    public static PledgedTokensPopup getInstance(Context pContext, String name, String amount,
                                                 PledgedTokensPopup.OnDidSegmentList pOnDidSegmentList, boolean isGateway) {
        return new PledgedTokensPopup(pContext, name, amount, pOnDidSegmentList, isGateway);
    }

    public PledgedTokensPopup(@NonNull Context context, String name, String amount,
                              PledgedTokensPopup.OnDidSegmentList pOnDidSegmentList, boolean isGateway) {
        super(context);
        this.mName = name;
        this.mAmount = amount;
        this.mOnDidSegmentList = pOnDidSegmentList;
        this.isGateway = isGateway;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_pledged_token;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        mBinding = DialogPledgedTokenBinding.bind(getPopupImplView());

        initView();
        initAdapter();
    }

    @Override
    protected int getPopupHeight() {
        if (isGateway) return (int) (XPopupUtils.getScreenHeight(getContext()) * 0.8);
        else return (int) (XPopupUtils.getScreenHeight(getContext()) * 0.5);
    }

    
    public void setDataList(List<PledgedTokensDidListEntity> pDataList) {
        mTokensAdapter.setNewData(pDataList);
        updateAdapter();
    }

    
    @Deprecated
    public void setGateway(boolean pGateway) {
        isGateway = pGateway;
        if (!isGateway) {
            mBinding.includeChild.constraintLayout2.setVisibility(GONE);
        }
    }

    
    public void setMaxSelect(int pMaxSelect) {
        maxSelect = pMaxSelect;
        setRecyclerSegment();
        updateAdapter();
    }

    
    public void setTaxAndBlockHeight(double pTax, String blockHeight) {
        this.mTax = pTax;
        this.mBlockHeight = blockHeight;
        setRealMoney();
    }

    @Deprecated
    public void setOnDidSegmentList(OnDidSegmentList pOnDidSegmentList) {
        mOnDidSegmentList = pOnDidSegmentList;
    }

    private void initView() {
        
        List<AssertBean> assets = WalletDBUtil.getInstent(getContext()).getMustWallet(WalletUtil.MCC_COIN);
        mDecimal = assets.get(0).getDecimal();
        if (mDecimal == 0) mDecimal = 18;

        
        mBinding.walletVeryReleaseDiss.setOnClickListener(v -> smartDismiss());
        
        mBinding.walletVeryReleaseOk.setOnClickListener(v -> {
            
            String balance = mBinding.includeChild.walletVeryReleaseEd.getText().toString().trim();
            if (TextUtils.isEmpty(balance)) {
                ToastUtil.showToast(mBinding.includeChild.walletVeryReleaseEd.getHint().toString().trim());
                return;
            }
            List<String> indexLists = new ArrayList<>();
            for (PledgedTokensDidListEntity entity : mSelectedList) {
                indexLists.add(entity.number);
            }

            if (indexLists.size() != maxSelect) {
                ToastUtil.showToast(getContext().getString(R.string.redeem_tips_2, maxSelect));
                return;
            }

            mOnDidSegmentList.onDoposGas(balance, indexLists);
        });

        mBinding.includeChild.walletVeryReleaseName.setText(mName);
        if (TextUtils.isEmpty(mAmount)) {
            mAmount = "--";
        } else {
            mAmount = AllUtils.getTenDecimalValue(mAmount, mDecimal, 4);
        }
        String coinName = getContext().getString(R.string.default_token_name).toUpperCase();
        mBinding.includeChild.walletVeryReleaseBalance.setText(mAmount + coinName);

        if (isGateway) {
            
            setRealMoney();
            
            setRecyclerSegment();
        } else {
            mBinding.includeChild.constraintLayout2.setVisibility(GONE);
        }


        
        String remindStr = "1.FM，，。\n" +
                "2.，1，0%，。";
        remindStr = getContext().getString(R.string.redeem_tips_3, "0%");
        mBinding.includeChild.tvRemind.setText(remindStr);

        
        mBinding.includeChild.walletVeryReleaseEd.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(@NonNull Editable s) {
                if (!isGateway || null == mOnDidSegmentList || TextUtils.isEmpty(s)) {
                    mBinding.includeChild.walletRealAmount.setText("0");
                    setRecyclerSegment();
                    return;
                }
                
                mOnDidSegmentList.onGatewayNumberCount(s.toString());

                
                try {
                    BigDecimal inputAmount = new BigDecimal(s.toString()).stripTrailingZeros();
                    BigDecimal rate = new BigDecimal(mTax).stripTrailingZeros();
                    BigDecimal realAmount = inputAmount.subtract(inputAmount.multiply(rate)).stripTrailingZeros().setScale(4, RoundingMode.HALF_UP);

                    mBinding.includeChild.walletRealAmount.setText(realAmount.stripTrailingZeros().toPlainString());
                } catch (Exception pE) {
                    pE.printStackTrace();
                }
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initAdapter() {
        mTokensAdapter = new PledgedTokensAdapter(new ArrayList<>());
        mBinding.includeChild.recyclerView.setAdapter(mTokensAdapter);

        mTokensAdapter.setOnItemClickListener((adapter, view, position) -> {
            PledgedTokensDidListEntity entity = (PledgedTokensDidListEntity) adapter.getData().get(position);
            if (entity.canSelect) {
                entity.selected = !entity.selected;
                if (entity.selected) {
                    mSelectedList.add(entity);
                } else {
                    mSelectedList.remove(entity);
                }
            }

            
            if (maxSelect <= 0) {
                adapter.notifyDataSetChanged();
                return;
            }
            for (PledgedTokensDidListEntity didList : mTokensAdapter.getData()) {
                if (maxSelect != mTokensAdapter.getData().size() && didList.isDefaultSegment) {
                    didList.setCanSelect(false);
                    continue;
                }
                didList.setCanSelect(mSelectedList.size() != maxSelect);
            }
            adapter.notifyDataSetChanged();
        });

        mOnDidSegmentList.onGetDidSegment();
    }

    
    @SuppressLint("NotifyDataSetChanged")
    private void updateAdapter() {
        
        for (PledgedTokensDidListEntity didList : mTokensAdapter.getData()) {
            
            if (maxSelect != mTokensAdapter.getData().size() && didList.isDefaultSegment) {
                didList.setCanSelect(false);
                continue;
            }
            didList.setCanSelect(maxSelect > 0);
            didList.selected = false;
        }
        mSelectedList.clear();
        
        mTokensAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDismiss() {
        super.onDismiss();
    }

    private void setRealMoney() {
        int height = 0;
        try {
            height = Integer.parseInt(mBlockHeight);
        } catch (NumberFormatException pE) {
            pE.printStackTrace();
        }
        String taxStr = (mTax * 100) + "%";
        if (height == 0) {
            String realTitle = getContext().getString(R.string.wallet_very_real_amount);
            mBinding.includeChild.tvRealAmountTitle.setText(realTitle);
        } else {
            
            String realTitle = getContext().getString(R.string.wallet_very_real_amount);
            String realTitleTip = getContext().getString(R.string.wallet_very_real_amount_tips);
            String realTitleTip2 = getContext().getString(R.string.wallet_very_real_amount_tips2);
            String realTitleTip3 = getContext().getString(R.string.wallet_very_real_amount_tips3);
            String text = realTitle + "<small><font color='#666666'>" + realTitleTip + mBlockHeight + realTitleTip2 + "</font><font color='#FF5500'>" + taxStr + "</font><font>" + realTitleTip3 + "</font></small>";
            mBinding.includeChild.tvRealAmountTitle.setText(Html.fromHtml(text));
        }
        String remindStr = "1.FM，，。\n" +
                "2.，1，" + taxStr + "，。";
        remindStr = getContext().getString(R.string.redeem_tips_3, taxStr);
        mBinding.includeChild.tvRemind.setText(remindStr);
    }

    private void setRecyclerSegment() {
        String balance = mBinding.includeChild.walletVeryReleaseEd.getText().toString().trim();
        String didTitle = getContext().getString(R.string.wallet_very_redeem);
        String didTitleTip = getContext().getString(R.string.wallet_very_redeem_tips);
        String didTitleTip2 = getContext().getString(R.string.wallet_very_redeem_tips2);
        String didTitleTip3 = getContext().getString(R.string.wallet_very_redeem_tips3);
        String didText = "" + didTitle + "<small><font color='#666666'>" + didTitleTip + "</font><font color='#FF5500'> " + balance + "FM </font>" + didTitleTip2 + "<font color='#FF5500'>" + maxSelect + "</font><font>" + didTitleTip3 + "</font></small>";
        mBinding.includeChild.tvDidRecycleTitle.setText(Html.fromHtml(didText));
    }

    
    public interface OnDidSegmentList {
        void onGetDidSegment();

        void onDoposGas(String pAmount, List<String> pIndexNum);

        void onGatewayNumberCount(String amount);
    }
}
