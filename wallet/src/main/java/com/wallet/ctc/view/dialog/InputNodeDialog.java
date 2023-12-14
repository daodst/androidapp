

package com.wallet.ctc.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.wallet.ctc.R;

import common.app.mall.util.ToastUtil;



public class InputNodeDialog {
    private Onclick onclick;
    public void setonclick(Onclick onclick){
        this.onclick =onclick;
    }
    private Dialog mDialog;
    private TextView title;
    private EditText pwd;
    private TextView yes,no;
    public InputNodeDialog(Context context, String message) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_input_node,null);
        title = (TextView) view.findViewById(R.id.title);
        yes = (TextView) view.findViewById(R.id.yes);
        no = (TextView) view.findViewById(R.id.no);
        pwd= (EditText) view.findViewById(R.id.password);
        title.setText(message);
        mDialog = new Dialog(context, R.style.dialogDim);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
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
                String pwdStr=pwd.getText().toString().trim();
                if(TextUtils.isEmpty(pwdStr)||!pwdStr.startsWith("http")){
                    ToastUtil.showToast(context.getString(R.string.valid_node_address));
                    return;
                }
                pwd.setText("");
                onclick.Yes(pwdStr);
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
