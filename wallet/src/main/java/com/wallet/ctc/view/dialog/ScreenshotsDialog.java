

package com.wallet.ctc.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.wallet.ctc.R;



public class ScreenshotsDialog {
    private Dialog mDialog;
    private TextView close;
    private TextView content;
    public ScreenshotsDialog(Context context,String txt) {
        View view = LayoutInflater.from(context).inflate(R.layout.screenshotsdialog,null);
        close = (TextView) view.findViewById(R.id.close_dialog);
        content= (TextView) view.findViewById(R.id.content);
        content.setText(txt);
        mDialog = new Dialog(context, R.style.dialogDim);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(true);
        final Window win = mDialog.getWindow();
        win.setWindowAnimations(R.style.dialogAnim);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               mDialog.dismiss();
            }
        });
    }


    public void show() {
        mDialog.show();
    }

    public void dismiss() {
        mDialog.dismiss();
    }
}
