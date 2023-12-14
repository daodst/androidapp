

package common.app.im.ui.view;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import common.app.R;
import common.app.im.base.Loading;
import common.app.utils.DisplayUtils;


public class QQDialogLoading implements Loading {

    public Dialog mDialog;
    private AnimationDrawable mAnimationDrawable = null;
    private TextView mTitle;

    public QQDialogLoading(Context context, String message) {

        View view = LayoutInflater.from(context).inflate(R.layout.qq_dialog_loading, null);

        mTitle = (TextView) view.findViewById(R.id.qq_loading_tv);
        LinearLayout pLayout = (LinearLayout) view.findViewById(R.id.qq_loading_parent);

        int width = DisplayUtils.getScreenWidthPixels((Activity) context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int)(width*0.75), ViewGroup.LayoutParams.WRAP_CONTENT);
        pLayout.setLayoutParams(layoutParams);
        mTitle.setText(message);
        ImageView loadingImage = (ImageView) view.findViewById(R.id.qq_loading_iv);
        loadingImage.setImageResource(R.drawable.waitting);
        mAnimationDrawable = (AnimationDrawable) loadingImage.getDrawable();
        if (mAnimationDrawable != null) {
            mAnimationDrawable.setOneShot(false);
            mAnimationDrawable.start();
        }

        mDialog = new Dialog(context, R.style.dialog);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(false);

    }


    public void setCanceledOnTouchOutside(boolean cancel) {
        mDialog.setCanceledOnTouchOutside(cancel);
    }

    public boolean isShowing() {
        if (mDialog.isShowing()) {
            return true;
        }
        return false;
    }

    @Override
    public void showLoading() {
        try {
            if (!mDialog.isShowing()) {
                mDialog.show();
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void hindeLoading() {
        if (mDialog.isShowing()) {
            mDialog.dismiss();
            mAnimationDrawable.stop();
        }
    }

    @Override
    public void setTitle(String title) {
        mTitle.setText(title);
    }
}
