

package com.wallet.ctc.view.dialog.inputpwd;

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



public class InputPwdDialog2 {
    private Onclick onclick;
    public void setonclick(Onclick onclick){
        this.onclick =onclick;
    }
    private Dialog mDialog;
    private TextView title;
    private EditText pwd;
    private TextView yes,no;
    public InputPwdDialog2(Context context, String message) {
        View view = LayoutInflater.from(context).inflate(R.layout.inputpwddialog2,null);
        title = (TextView) view.findViewById(R.id.title);
        yes = (TextView) view.findViewById(R.id.yes);
        no = (TextView) view.findViewById(R.id.no);
        pwd= (EditText) view.findViewById(R.id.password);
        title.setText(message);
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
                String pwdStr=pwd.getText().toString().trim();
                if(TextUtils.isEmpty(pwdStr)){
                    ToastUtil.showToast(context.getResources().getString(R.string.place_edit_password));
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
        if(mDialog.isShowing()){
            return;
        }
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
