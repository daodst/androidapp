package com.app.base.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.app.R;
import com.app.databinding.DialogDidApplyBinding;
import com.lxj.xpopup.core.PositionPopupView;
import com.lxj.xpopup.util.XPopupUtils;

import common.app.base.them.Eyes;


@SuppressLint("ViewConstructor")
@Deprecated
public class DIDApplyPopup extends PositionPopupView {
    public DialogDidApplyBinding mBinding;
    private final View.OnClickListener mApplyListener;

    public DIDApplyPopup(@NonNull Context context, OnClickListener applyListener) {
        super(context);
        mApplyListener = applyListener;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_did_apply;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        mBinding = DialogDidApplyBinding.bind(getPopupImplView());
        mBinding.appCompatButton.setOnClickListener(mApplyListener);
        mBinding.imgTop.setOnClickListener(v -> smartDismiss());

        ViewGroup.LayoutParams params = mBinding.llTop.getLayoutParams();
        params.height = Eyes.getStatusBarHeight(getContext());
        mBinding.llTop.setLayoutParams(params);
    }

    @Override
    protected void onDismiss() {
        super.onDismiss();
    }

    @Override
    public int getMinimumHeight() {
        return XPopupUtils.dp2px(getContext(), 120) + Eyes.getStatusBarHeight(getContext());
    }

    @Override
    protected int getMaxHeight() {
        return XPopupUtils.dp2px(getContext(), 200) + Eyes.getStatusBarHeight(getContext());
    }

    @Override
    protected int getPopupWidth() {
        return XPopupUtils.getAppWidth(getContext());
    }
}

