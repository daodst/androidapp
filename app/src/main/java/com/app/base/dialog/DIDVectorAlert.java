package com.app.base.dialog;

import android.app.Activity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.R;
import com.app.databinding.DialogDidApplyBinding;

import im.vector.app.features.popup.DefaultVectorAlert;
import im.vector.app.features.popup.VectorAlert;
import kotlin.jvm.functions.Function1;


public class DIDVectorAlert extends DefaultVectorAlert {
    public DIDVectorAlert(@NonNull String uid, @NonNull String title, @NonNull String description, @Nullable Integer iconId, @NonNull Function1<? super Activity, Boolean> shouldBeDisplayedIn) {
        super(uid, title, description, iconId, shouldBeDisplayedIn);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.dialog_did_apply;
    }

    public static class DidApplyViewBinder implements VectorAlert.ViewBinder {
        private final View.OnClickListener mApplyListener;
        
        private final View.OnClickListener mCollapseListener;
        public DialogDidApplyBinding mBinding;

        public DidApplyViewBinder(View.OnClickListener applyListener, View.OnClickListener collapseListener) {
            mApplyListener = applyListener;
            mCollapseListener = collapseListener;
        }

        @Override
        public void bind(@NonNull View view) {
            mBinding = DialogDidApplyBinding.bind(view);
            mBinding.appCompatButton.setOnClickListener(mApplyListener);
            mBinding.imgTop.setOnClickListener(mCollapseListener);
        }
    }
}
