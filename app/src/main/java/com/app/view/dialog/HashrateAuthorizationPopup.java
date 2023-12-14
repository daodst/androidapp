package com.app.view.dialog;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.app.R;
import com.app.databinding.DialogHashrateAuthorizationBinding;
import com.lxj.xpopup.core.CenterPopupView;

import java.math.BigDecimal;
import java.util.function.Consumer;

import common.app.base.listener.DefaultTextWatcher;
import common.app.pojo.ConsumerData;


public class HashrateAuthorizationPopup extends CenterPopupView {
    private DialogHashrateAuthorizationBinding mBinding;

    private String address, authorizationTIme, blockHeight;
    private Consumer<ConsumerData> mConsumer;

    public HashrateAuthorizationPopup(@NonNull Context context, Consumer<ConsumerData> consumer) {
        super(context);
        this.mConsumer = consumer;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_hashrate_authorization;
    }

    @Override
    protected void onCreate() {
        mBinding = DialogHashrateAuthorizationBinding.bind(getPopupImplView());
        mBinding.btnCancel.setOnClickListener(v -> dismiss());
        mBinding.etAuthorizationTime.addTextChangedListener(new DefaultTextWatcher(){
            @Override
            public void afterTextChanged(Editable editable) {
                String inputTx = editable.toString();
                if (TextUtils.isEmpty(inputTx)) {
                    mBinding.etAuthorizationBlockHeight.setText("0");
                    return;
                }
                long height = new BigDecimal(inputTx).multiply(new BigDecimal(14400)).longValue();
                mBinding.etAuthorizationBlockHeight.setText(height+"");
            }
        });

        mBinding.btnConfirm.setOnClickListener(v -> {
            address = mBinding.etContract.getText().toString();
            authorizationTIme = mBinding.etAuthorizationTime.getText().toString();
            blockHeight = mBinding.etAuthorizationBlockHeight.getText().toString();

            if (TextUtils.isEmpty(address)) {
                showToast(mBinding.etContract.getHint().toString());
                return;
            }
            if (TextUtils.isEmpty(authorizationTIme)) {
                showToast(mBinding.etAuthorizationTime.getHint().toString());
                return;
            }
            if (TextUtils.isEmpty(blockHeight)) {
                showToast(mBinding.etAuthorizationBlockHeight.getHint().toString());
                return;
            }
            authorization();
        });
    }

    
    private void authorization() {
        
        dismiss();
        mConsumer.accept(new ConsumerData(address, blockHeight));

    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
