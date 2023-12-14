

package common.app.ui.view.update;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

import common.app.R;
import common.app.base.fragment.mall.model.NewVersionBean;
import common.app.ui.view.update.adapter.UpdateAdapter;



public class UpdatingDialog extends Dialog {
    private static final String TAG = "UpdatingDialog";
    private TextView closeView;
    private TextView verName;
    private TextView updateView;
    private Context mContext;
    private NewVersionBean mEntity;
    private ListView listView;
    private UpdateAdapter mAdapter;
    
    private TextView mTextView = null;
    
    private ProgressBar mProgressBar = null;
    private changestateListener changestateListener;
    private String[] MUST_PERMISSION = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public UpdatingDialog(Context context, int theme) {
        super(context, theme);

    }

    
    public UpdatingDialog(Context context, NewVersionBean mEntity) {
        super(context, R.style.CommonDialog);
        mContext = context;
        this.mEntity = mEntity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updata_dialog);
        closeView = (TextView) findViewById(R.id.close_dialog);
        updateView = (TextView) findViewById(R.id.start_upload);
        mProgressBar = (ProgressBar) findViewById(R.id.status_progressBar);
        mTextView = (TextView) findViewById(R.id.dialog_status_edittext);
        listView = (ListView) findViewById(R.id.change_list);
        verName = (TextView) findViewById(R.id.ver_name);
        verName.setText(mEntity.getNewVersion());
        setCancelable(false);
        this.getWindow().setGravity(Gravity.CENTER);
        LayoutParams lp = this.getWindow().getAttributes();
        
        lp.alpha = 1.0f;
        this.getWindow().setAttributes(lp);
        closeView.setOnClickListener(v -> {
            dismiss();
            changestateListener.change(1);
        });
        updateView.setOnClickListener(v -> {
            
            changestateListener.change(0);
        });
        if (mEntity.getClientForceUpdate(mContext.getPackageName())) {
            closeView.setVisibility(View.GONE);
        }
        List<String> list = null;
        try {
            String[] change = mEntity.tips(mContext).substring(1).split("#");
            list = new ArrayList<>();
            for (int i = 0; i < change.length; i++) {
                list.add(change[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mAdapter = new UpdateAdapter(list);
        listView.setAdapter(mAdapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions((Activity) mContext, MUST_PERMISSION, 200);
        }
    }

    public interface changestateListener {
        void change(int position);
    }

    public void setChangeStateListener(changestateListener mIDeleteBtnClickListener) {
        this.changestateListener = mIDeleteBtnClickListener;
    }

    
    public void setProgress(int progressNum) {
        this.mTextView.setText("" + progressNum + "%");
        this.mProgressBar.setProgress(progressNum);
    }

    
    public void setUI(int progressNum) {
        if (progressNum == 0) {
            listView.setVisibility(View.GONE);
            mTextView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
            updateView.setText("");
            updateView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changestateListener.change(2);
                }
            });
        }
        if (progressNum == 1) {
            listView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            mTextView.setVisibility(View.GONE);
            updateView.setText("");
            updateView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listView.setVisibility(View.GONE);
                    mTextView.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    changestateListener.change(0);
                }
            });
        }
    }
}
