

package com.app.view;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.R;
import com.wallet.ctc.model.me.OneClickLoginBean;

import common.app.my.view.CircularImage;
import common.app.utils.GlideUtil;



public class ChooseUserDialog {
    private Onclick onclick;

    private Dialog mDialog;
    private LinearLayout oneUser,twoUser;
    private CircularImage logined_logo,userLogo;
    private TextView loginedNickname,userNickname,LoginedName,userName;
    public ChooseUserDialog(Context context, OneClickLoginBean oneClickLoginBean) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_choose_user,null);
        oneUser=(LinearLayout)view.findViewById(R.id.one_user);
        twoUser=(LinearLayout)view.findViewById(R.id.two_user);
        logined_logo=(CircularImage)view.findViewById(R.id.logind_logo);
        userLogo=(CircularImage)view.findViewById(R.id.user_logo);
        loginedNickname = (TextView) view.findViewById(R.id.logined_nickname);
        userNickname = (TextView) view.findViewById(R.id.user_nickname);
        LoginedName = (TextView) view.findViewById(R.id.logined_phone);
        userName = (TextView) view.findViewById(R.id.user_name);
        mDialog = new Dialog(context, R.style.dialogDim);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(true);
        final Window win = mDialog.getWindow();
        win.setWindowAnimations(R.style.dialogAnim);

        if(null!=oneClickLoginBean){
            GlideUtil.showImg(context,oneClickLoginBean.ico,userLogo);
            userNickname.setText(oneClickLoginBean.nickName);
            if(!TextUtils.isEmpty(oneClickLoginBean.account)){
                userName.setText(oneClickLoginBean.account);
            }else {
                userName.setText(oneClickLoginBean.userName);
            }
        }
        oneUser.setOnClickListener(v -> {
            onclick.Yes();
        });
        twoUser.setOnClickListener(v -> {
            onclick.No();
        });
    }



    public void show() {
        mDialog.show();
    }

    public void dismiss() {
        mDialog.dismiss();
    }
    public void setonclick(Onclick onclick){
        this.onclick =onclick;
    }
    public interface Onclick{
        void Yes();
        void No();
    }
}
