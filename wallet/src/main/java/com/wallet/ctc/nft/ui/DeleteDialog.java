

package com.wallet.ctc.nft.ui;

import android.app.Dialog;
import android.content.Context;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.util.FastClickUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeleteDialog extends Dialog {
    public interface Callback {
        void onDelete();
    }

    @BindView(R2.id.ll_delete)
    LinearLayout llDelete;

    private Callback callback = null;

    public DeleteDialog(@NonNull Context context) {
        super(context, R.style.dialog);
        init(context);
    }

    public DeleteDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    protected DeleteDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    public void setCallback(Callback c) {
        callback = c;
    }

    private void init(Context context) {
        setContentView(R.layout.layout_dialog_delete);
        ButterKnife.bind(this);
        setCanceledOnTouchOutside(true);
    }

    @OnClick(R2.id.ll_delete)
    public void onClick() {
        if (FastClickUtils.isFastClick()) {
            return;
        }

        if (null != callback) {
            callback.onDelete();
        }
    }
}
