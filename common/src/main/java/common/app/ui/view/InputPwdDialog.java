

package common.app.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import common.app.R;
import common.app.biometric.MyBiometricHelper;
import common.app.mall.util.ToastUtil;
import common.app.utils.SpUtil;
import im.wallet.router.base.ApplicationDelegate;
import im.wallet.router.base.IApplication;
import im.wallet.router.wallet.IWalletPay;



public class InputPwdDialog {
    private Onclick onclick;
    private Context context;

    public void setonclick(Onclick onclick) {
        this.onclick = onclick;
    }

    private Dialog mDialog;
    private TextView title, hint;
    private EditText pwd;
    private Button yes, no;

    
    private MyBiometricHelper mMyBiometricHelper;

    public InputPwdDialog(Context context, String message) {
        this(context, message, "");
    }

    public InputPwdDialog(Context context, String message, String hintText) {
        this.context = context;
        View view = LayoutInflater.from(context).inflate(R.layout.inputpwddialog, null);
        title = (TextView) view.findViewById(R.id.title);
        hint = (TextView) view.findViewById(R.id.hint);
        yes = (Button) view.findViewById(R.id.yes);
        no = (Button) view.findViewById(R.id.no);
        pwd = (EditText) view.findViewById(R.id.password);
        title.setText(message);
        hint.setVisibility(TextUtils.isEmpty(hintText) ? View.GONE : View.VISIBLE);
        hint.setText(hintText);
        mDialog = new Dialog(context, R.style.dialogDim);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        mDialog.setOnShowListener(dialogInterface -> {
            if (null != pwd) {
                pwd.postDelayed(() -> {
                    
                    pwd.setFocusable(true);
                    pwd.setFocusableInTouchMode(true);
                    
                    pwd.requestFocus();
                    
                    InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }, 700);
            }
        });
        final Window win = mDialog.getWindow();
        win.setWindowAnimations(R.style.dialogAnim);
        no.setOnClickListener(v -> onclick.No());
        yes.setOnClickListener(v -> {
            String pwdStr = pwd.getText().toString().trim();
            if (TextUtils.isEmpty(pwdStr)) {
                ToastUtil.showToast(context.getResources().getString(R.string.place_edit_password));
                return;
            }
            pwd.setText("");
            onclick.Yes(pwdStr);
        });

        
        boolean hardSupport = MyBiometricHelper.checkHardSupport(context);
        if (hardSupport) {
            boolean hasFinger = MyBiometricHelper.checkHasFinger(context);
            if (hasFinger) {
                TextView biometric = view.findViewById(R.id.btnOpenBiometric);
                biometric.setVisibility(View.VISIBLE);
                biometric.setOnClickListener(v -> {
                    try {
                        Class<?> clazz = Class.forName("com.wallet.ctc.ui.blockchain.mywallet.MyWalletActivity");
                        Intent intent = new Intent(context, clazz);
                        context.startActivity(intent);

                        if (null != mDialog) mDialog.dismiss();
                    } catch (ClassNotFoundException pE) {
                        throw new RuntimeException(pE);
                    }
                });
            }

        }
    }

    public void setYesText(String text) {
        yes.setText(text);
    }

    public void setNoText(String text) { 
        no.setText(text);
    }


    public void show(boolean isOpenBiometric) {


        if ((null != mDialog && isOpenBiometric) || !MyBiometricHelper.checkFingerprintSupport(context)) {
            mDialog.show();
            return;
        }

        IApplication iApplication = (IApplication) context.getApplicationContext();
        IWalletPay iWalletPay = iApplication.getDelegate(ApplicationDelegate.MOODLE_TYPE_WALLET).getWalletPay();
        String fingerPayKey = iWalletPay.getFingerPayKey(context);
        Log.i("InputPwdDialog", "fingerPayKey="+fingerPayKey);

        boolean isOpen = SpUtil.getAppBiometricOpened(fingerPayKey);
        if (null != mDialog) {
            if (!isOpen) {
                mDialog.show();
                return;
            }
            mMyBiometricHelper = MyBiometricHelper.getInstance(isOpenBiometric, fingerPayKey);
            mMyBiometricHelper.setBiometricCallback(new MyBiometricHelper.BiometricCallback() {
                @Override
                public void error(Throwable e) {
                    
                    
                    mDialog.show();
                }

                @Override
                public void listener(int errorCode) {
                    mDialog.show();
                }

                @Override
                public void callback(String password) {
                    if (null != onclick) onclick.Yes(password);
                }
            });
            mMyBiometricHelper.start((FragmentActivity) context);
        }
    }

    public void show() {
        show(false);
    }

    public void dismiss() {
        if (null != mDialog) {
            mDialog.dismiss();
        }
    }

    public void setNoBtnGone() {
        no.setVisibility(View.GONE);
    }

    public void setYesBtnGone() {
        yes.setVisibility(View.GONE);
    }

    public interface Onclick {
        void Yes(String pwd);

        void No();
    }


    public interface PwdResultListener {
        void onGet(String pwd, InputPwdDialog dialog);
    }

    public static void show(Context context, PwdResultListener listener){
        InputPwdDialog pwdDialog = new InputPwdDialog(context, context.getString(R.string.place_edit_password));
        pwdDialog.setonclick(new Onclick() {
            @Override
            public void Yes(String pwd) {
                if (TextUtils.isEmpty(pwd)) {
                    ToastUtil.showToast(R.string.place_edit_password);
                    return;
                }
                if (null != listener){
                    listener.onGet(pwd, pwdDialog);
                }
            }

            @Override
            public void No() {
                pwdDialog.dismiss();
            }
        });
        pwdDialog.show();
    }
}
