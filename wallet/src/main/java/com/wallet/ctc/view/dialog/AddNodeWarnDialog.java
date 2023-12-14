

package com.wallet.ctc.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;

import butterknife.ButterKnife;
import butterknife.OnClick;



public class AddNodeWarnDialog {
    private Context mContext;
    private Dialog mDialog;
    private AddNode addNode;


    
    public AddNodeWarnDialog(Context context) {
        this.mContext = context;
        LayoutInflater layoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.dialog_addnode_warn, null);
        ButterKnife.bind(this, layout);

        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(layout, layoutParams);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        Window win = dialog.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        win.getDecorView().setBackgroundColor(Color.TRANSPARENT);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        lp.windowAnimations = R.style.dialogAnim;
        win.setAttributes(lp);
        mDialog = dialog;
    }


    public void show() {
        mDialog.show();
    }

    public interface AddNode{
        void goAddNode();
    }
    public void setTrans(AddNode go){
        addNode=go;
    }

    public void dismiss() {
        mDialog.dismiss();
    }

    @OnClick({R2.id.close_dialog, R2.id.btn_sub})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.close_dialog) {
            dismiss();
        } else if (i == R.id.btn_sub) {
            dismiss();
            if(null!=addNode){
                addNode.goAddNode();
            }
        } else {
        }
    }
}
