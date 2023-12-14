package com.app.view.dialog;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.app.R;
import com.app.databinding.DialogRateModifyBinding;
import com.lxj.xpopup.core.BottomPopupView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Consumer;

import common.app.mall.util.ToastUtil;
import im.vector.app.core.platform.SimpleTextWatcher;


public class RateModifyPopup extends BottomPopupView {
    private DialogRateModifyBinding mBinding;

    private final int type;
    private Consumer<Double> mConsumer;
    
    private String limitMin, limitMax;

    private BigDecimal mInputRate = new BigDecimal(0);

    public RateModifyPopup(@NonNull Context context, @RateModifyType int type, String limitMin, String limitMax) {
        super(context);
        this.limitMin = limitMin;
        this.limitMax = limitMax;
        this.type = type;
    }

    public RateModifyPopup setConsumer(Consumer<Double> consumer) {
        mConsumer = consumer;
        return this;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_rate_modify;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        mBinding = DialogRateModifyBinding.bind(getPopupImplView());

        if (type == RateModifyType.TYPE_BROKERAGE) initBrokerageView();
        else if (type == RateModifyType.TYPE_SALARY) initSalaryView();

        mBinding.etRate.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(@NonNull Editable s) {
                if (TextUtils.isEmpty(s)) return;
                try {
                    mInputRate = new BigDecimal(s.toString());
                } catch (NumberFormatException e) {
                    mInputRate = new BigDecimal(0);
                    e.printStackTrace();
                }
            }
        });
        mBinding.cancel.setOnClickListener(v -> dismiss());

    }

    
    private void initBrokerageView() {
        mBinding.tvModifyTitle.setText("");
        mBinding.tvSubTitle.setText("");
        mBinding.tvModifyDesc.setText("，");
        mBinding.etRate.setHint("");
        mBinding.confirm.setOnClickListener(v -> {
            
            String rate = mInputRate.divide(new BigDecimal(100), 6, RoundingMode.HALF_UP).toPlainString();
            onConsumer(Double.parseDouble(rate));
            
        });
    }

    
    private void initSalaryView() {
        mBinding.tvModifyTitle.setText("");
        mBinding.tvSubTitle.setText("");
        mBinding.tvModifyDesc.setText("");
        mBinding.etRate.setHint("");
        mBinding.confirm.setOnClickListener(v -> {
            
            
            String rate = mInputRate.divide(new BigDecimal(100), 6, RoundingMode.HALF_UP).toPlainString();
            onConsumer(1-Double.parseDouble(rate));
            
        });
    }

    private void onConsumer(double rate) {
        double min = Double.parseDouble(limitMin);
        double max = Double.parseDouble(limitMax);

        if (rate < min) {
            ToastUtil.showToast("" + (min * 100) + "%");
        } else if (rate > max) {
            ToastUtil.showToast("" + (max * 100) + "%");
        } else {
            if (null != mConsumer) mConsumer.accept(rate);
        }
    }
}
