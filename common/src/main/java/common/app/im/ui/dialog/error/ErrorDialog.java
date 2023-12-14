

package common.app.im.ui.dialog.error;

import android.app.Dialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import common.app.R;
import common.app.R2;
import common.app.base.base.BaseDialogFragment;



public class ErrorDialog extends BaseDialogFragment {

    @BindView(R2.id.error_title)
    TextView mErrorTitle;
    @BindView(R2.id.error_message)
    TextView mErrorMessage;
    @BindView(R2.id.resent_cancel)
    TextView mResentCancel;

    private static final String PARAM_TITLE = "PARAM_TITLE";
    private static final String PARAM_MESSAGE = "PARAM_MESSAGE";
    private static final String PARAM_CANCELABLE = "PARAM_CANCELABLE";
    private static final String PARAM_ERROR_CODE = "PARAM_ERROR_CODE";

    private String mTitle;
    private String mMesage;
    private int mErrorCode;

    private IErrorClick mIErrorClick;

    public void setIErrorClick(IErrorClick IErrorClick) {
        mIErrorClick = IErrorClick;
    }

    private boolean mCancelable;


    public static Bundle getBundle(String title, String message, boolean cancelable, int errorCode) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_TITLE, title);
        bundle.putString(PARAM_MESSAGE, message);
        bundle.putBoolean(PARAM_CANCELABLE, cancelable);
        bundle.putInt(PARAM_ERROR_CODE, errorCode);
        return bundle;
    }


    private void iniData() {
        Bundle bundle = getArguments();
        if (null != bundle) {
            mTitle = bundle.getString(PARAM_TITLE);
            mMesage = bundle.getString(PARAM_MESSAGE);
            mErrorCode = bundle.getInt(PARAM_ERROR_CODE);
            this.mCancelable = bundle.getBoolean(PARAM_CANCELABLE);
        }
        mTitle = null == mTitle ? "" : mTitle;
        mMesage = null == mMesage ? "" : mMesage;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.75), ViewGroup.LayoutParams.WRAP_CONTENT);
            
            
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_error, container, false);
        iniData();
        this.setCancelable(this.mCancelable);
        mUnbinder = ButterKnife.bind(this, view);
        super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    @Override
    protected void initEvents() {
        mResentCancel.setOnClickListener(v -> {
            
            mIErrorClick.onClik(mErrorCode);
        });
    }

    @Override
    protected void initViews() {
        mErrorTitle.setText(mTitle);
        mErrorMessage.setText(mMesage);
    }

    public interface IErrorClick {
        
        void onClik(int errorCode);
    }
}
