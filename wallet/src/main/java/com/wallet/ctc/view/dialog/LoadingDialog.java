

package com.wallet.ctc.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.wallet.ctc.R;



public class LoadingDialog{
    private Dialog mDialog;
    public LoadingDialog(Context context, String txt) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_commom,null);
        mDialog  = new Dialog(context, R.style.progress_dialog);
        mDialog.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent);
        TextView msg = (TextView) mDialog
                .findViewById(R.id.id_tv_loadingmsg);
        msg.setText(txt);
    }


    public void show() {
        mDialog.show();
    }

    public void dismiss() {
        mDialog.dismiss();
    }

}
