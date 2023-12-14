

package common.app.my.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import common.app.R;




public class MyAlertDialog {
    private Onclick onclick;

    public void setonclick(Onclick onclick) {
        this.onclick = onclick;
    }

    private Dialog mDialog;
    private TextView title, descTextView;
    private Button yes, no;
    private View noView;
    private Context mContext;

    public MyAlertDialog(Context context, String message) {
        View view = LayoutInflater.from(context).inflate(R.layout.myalertdialog, null);
        mContext = context;
        title = (TextView) view.findViewById(R.id.title);
        descTextView = (TextView) view.findViewById(R.id.descTextView);
        yes = (Button) view.findViewById(R.id.yes);
        no = (Button) view.findViewById(R.id.no);
        noView = view.findViewById(R.id.no_view);
        descTextView.setText(message);
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
                onclick.Yes();
            }
        });
    }

    public void setYesText(String text) {
        yes.setText(text);
    }

    public void setYesTextColor(int color) {
        yes.setTextColor(mContext.getResources().getColor(color));
    }

    public void setNoText(String text) { 
        no.setText(text);
    }


    public void show() {
        mDialog.show();
    }

    public void dismiss() {
        mDialog.dismiss();
    }

    public void setNoBtnGone() {
        no.setVisibility(View.GONE);
        noView.setVisibility(View.GONE);
    }

    public void setTitleGone() {
        title.setVisibility(View.GONE);
        descTextView.setTextSize(15);
        descTextView.setTextColor(mContext.getResources().getColor(R.color.default_text_color));

    }

    public void setYesBtnGone() {
        yes.setVisibility(View.GONE);
    }

    public interface Onclick {
        void Yes();

        void No();
    }

    public void setCanceledOnTouchOutside(boolean cacel) {
        if (null != mDialog) {
            mDialog.setCanceledOnTouchOutside(cacel);
        }
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
        if (null != mDialog) {
            mDialog.setOnDismissListener(listener);
        }
    }

    public void setCancelable(boolean cacel) {
        if (null != mDialog) {
            mDialog.setCancelable(cacel);
        }
    }

    public boolean isShowing() {
        return mDialog.isShowing();
    }

    public void setDesc(String text) {
        if (!TextUtils.isEmpty(text)) {
            descTextView.setVisibility(View.VISIBLE);
            descTextView.setText(text);
        }
    }

    public void setTitle(String message) {
        if (null != title) {
            title.setVisibility(View.VISIBLE);
            title.setText(message);
        }
    }

    
    public void setContentGravity(int gravity){
        if (null != descTextView){
            descTextView.setGravity(gravity);
        }
    }

}
