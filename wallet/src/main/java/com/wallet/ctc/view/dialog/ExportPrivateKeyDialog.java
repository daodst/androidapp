

package com.wallet.ctc.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.wallet.ctc.R;



public class ExportPrivateKeyDialog {
    private Onclick onclick;
    public void setonclick(Onclick onclick){
        this.onclick =onclick;
    }
    private Dialog mDialog;
    private TextView privateKey;
    private TextView yes,no;
    public ExportPrivateKeyDialog(Context context, String message) {
        View view = LayoutInflater.from(context).inflate(R.layout.exportprivatekeydialog,null);
        privateKey = (TextView) view.findViewById(R.id.private_key);
        yes = (TextView) view.findViewById(R.id.yes);
        no = (TextView) view.findViewById(R.id.no);
        privateKey.setText(message);
        mDialog = new Dialog(context, R.style.dialogDim);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(true);
        final Window win = mDialog.getWindow();
        win.setWindowAnimations(R.style.dialogAnim);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onclick.No();
            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onclick.Yes(message);
            }
        });
    }

    public void setYesText(String text){
        yes.setText(text);
    }

    public void setNoText(String text){ 
        no.setText(text);
    }


    public void show() {
        mDialog.show();
    }

    public void dismiss() {
        mDialog.dismiss();
    }

    public void setNoBtnGone(){
        no.setVisibility(View.GONE);
    }

    public void setYesBtnGone(){
        yes.setVisibility(View.GONE);
    }

    public interface Onclick{
        void Yes(String pwd);
        void No();
    }
}
