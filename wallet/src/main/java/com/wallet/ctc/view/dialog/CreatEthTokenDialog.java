

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
import android.widget.TextView;

import com.wallet.ctc.R;



public class CreatEthTokenDialog {
    private Onclick onclick;
    public void setonclick(Onclick onclick){
        this.onclick =onclick;
    }
    private Dialog mDialog;
    private TextView shouxufeiText;
    private View yes,no;
    public CreatEthTokenDialog(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.creat_eth_tokendialog,null);
        shouxufeiText = (TextView) view.findViewById(R.id.shouxufei);
        yes =  view.findViewById(R.id.yes);
        no = view.findViewById(R.id.no);
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(view, layoutParams);

        Window win = dialog.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        win.getDecorView().setBackgroundColor(Color.TRANSPARENT);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        lp.windowAnimations = R.style.dialogAnim;
        win.setAttributes(lp);
        mDialog = dialog;
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onclick.No();
            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onclick.Yes();
            }
        });
    }

    public void setYesText(String text){
        ((TextView)yes).setText(text);
        yes.setEnabled(false);
        yes.setBackgroundColor(0xffe6e6e6);
    }

    public void setNoText(String text){ 
    }


    public void show(String data) {
        shouxufeiText.setText(data);
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
        void Yes();
        void No();
    }
}
