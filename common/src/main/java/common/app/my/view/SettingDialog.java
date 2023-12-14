

package common.app.my.view;


import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import common.app.R;


public class SettingDialog {

    private Dialog mDialog;
    private TextView nameTextView;
    private TextView dialog_message;
    public TextView positive;
    public TextView negative;
    private EditText msg;

    public SettingDialog(Context context, String message) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.settingdialog_layout, null);

        mDialog = new Dialog(context, R.style.dialogDim);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(true);
        final Window win = mDialog.getWindow();
        win.setWindowAnimations(R.style.dialogAnim);
        nameTextView = (TextView) view.findViewById(R.id.nameTextView);
        msg = (EditText) view.findViewById(R.id.msg);
        positive = (TextView) view.findViewById(R.id.yes);
        negative = (TextView) view.findViewById(R.id.no);
        if(!TextUtils.isEmpty(message)){
            nameTextView.setText(message);
        }
        view.findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    public void setPositiveListnner(View.OnClickListener listener){
        positive.setOnClickListener(listener);
    }

    public String getMsg(){
        return msg.getText().toString().trim();
    }

    public void setMsg(String text) {
        if (!TextUtils.isEmpty(text)) {
            msg.setText(text);
        }
    }

    public void show() {
        mDialog.show();
    }

    public void dismiss() {
        mDialog.dismiss();
    }

}
