

package com.wallet.ctc.view.dialog.candybox;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wallet.ctc.R;
import com.wallet.ctc.util.GlideUtil;

import common.app.my.view.CircularImage;




public class CandyBoxDialog extends Dialog {
    private ImageView closeView;
    private TextView candyContent;
    private Context mContext;
    private LinearLayout candyTochat;
    private CircularImage userlogo;
    private String content="";
    private String logo;
    private changestateListener changestateListener;

    public CandyBoxDialog(Context context, int theme) {
        super(context, theme);

    }
    
    public CandyBoxDialog(Context context,String content,String logo) {
        super(context, R.style.CommonDialog);
        mContext=context;
        this.logo=logo;
        this.content=content;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.candybox_dialog);
        closeView=(ImageView)findViewById(R.id.close_dialog);
        candyContent=(TextView)findViewById(R.id.candy_content);
        candyTochat=(LinearLayout) findViewById(R.id.candy_tochat);
        userlogo=(CircularImage)findViewById(R.id.img_head_logo);
        setCancelable(true);
        this.getWindow().setGravity(Gravity.CENTER);
        LayoutParams lp = this.getWindow().getAttributes();
        
        lp.alpha = 1.0f;
        candyContent.setText(content);
        GlideUtil.showImg(mContext,logo,userlogo);
        this.getWindow().setAttributes(lp);
        closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        candyTochat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                changestateListener.change(0);
            }
        });

    }
    public interface changestateListener {
        void change(int position);
    }
    public void setChangeStateListener(changestateListener mIDeleteBtnClickListener) {
        this.changestateListener = mIDeleteBtnClickListener;
    }
}
