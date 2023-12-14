

package com.wallet.ctc.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.app.mall.util.ToastUtil;



public class FindCommandDialog {
    private final String TAG = "ShareDialog";

    @BindView(R2.id.command_detail)
    EditText commandDetail;
    @BindView(R2.id.btn_sub)
    TextView btnSub;
    private Activity mContext;
    private Dialog mDialog;
    private int type = 0;
    private String content = "";
    private String id = "";

    
    public FindCommandDialog(Activity context) {
        this.mContext = context;
        LayoutInflater layoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.dialog_find_command, null);
        ButterKnife.bind(this, layout);

        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(layout, layoutParams);

        Window win = dialog.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        win.getDecorView().setBackgroundColor(Color.TRANSPARENT);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        lp.windowAnimations = R.style.dialogAnim;
        win.setAttributes(lp);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            public void onShow(DialogInterface dialog) {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(commandDetail, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        mDialog = dialog;
    }
    public void show(int type,String content,String id) {
        this.type=type;
        this.content=content;
        this.id=id;
        if(type==1){
            commandDetail.setHint(mContext.getString(R.string.reply)+": "+content);
            btnSub.setText(mContext.getString(R.string.reply));
        }else{
            commandDetail.setHint(mContext.getString(R.string.say_something));
            btnSub.setText(mContext.getString(R.string.release));
        }
        mDialog.show();
    }

    private SendCom mSendCom;

    public void SetSendCom(SendCom mSendCom){
        this.mSendCom=mSendCom;
    }

    public interface SendCom{
        void sendCom(String content);
        void sendReply(String id,String content);
    }

    public void dismiss() {
        mDialog.dismiss();
    }

    @OnClick({R2.id.btn_sub})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.btn_sub) {
            String data=commandDetail.getText().toString();
            if(null==data||TextUtils.isEmpty(data)){
                ToastUtil.showToast(mContext.getResources().getString(R.string.say_something));
                return;
            }
            commandDetail.setHint(R.string.say_something);
            if(type==1){
                mSendCom.sendReply(id,data);
            }else {
                mSendCom.sendCom(data);
            }
            commandDetail.setText("");
        } else {
        }
    }
}
