

package com.wallet.ctc.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.TransferBean;
import com.wallet.ctc.util.AllUtils;
import com.wallet.ctc.util.DecriptUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.app.mall.util.ToastUtil;
import common.app.ui.view.InputPwdDialog;



public class TransferDialog {
    private final String TAG = "ShareDialog";
    @BindView(R2.id.close_dialog)
    ImageView closeDialog;
    @BindView(R2.id.title)
    TextView title;
    @BindView(R2.id.order_info)
    TextView orderInfo;
    @BindView(R2.id.ru_address)
    TextView ruAddress;
    @BindView(R2.id.pay_address)
    TextView payAddress;
    @BindView(R2.id.miner_costs)
    TextView minerCosts;
    @BindView(R2.id.miner_costs_text)
    TextView minerCostsText;
    @BindView(R2.id.amount)
    TextView amount;
    @BindView(R2.id.pay_info)
    LinearLayout payInfo;
    @BindView(R2.id.password)
    EditText password;
    @BindView(R2.id.inputpwd)
    LinearLayout inputpwd;
    @BindView(R2.id.btn_sub)
    TextView btnSub;
    @BindView(R2.id.amount_lin)
    LinearLayout amount_lin;
    @BindView(R2.id.fee_lin)
    View fee_lin;

    @BindView(R2.id.ru_address_parent)
    View ru_address_parent;


    
    @BindView(R2.id.ru_address_copy)
    View ru_address_copy;
    
    @BindView(R2.id.pay_address_copy)
    View pay_address_copy;

    
    @BindView(R2.id.miner_costs_congestion)
    View miner_costs_congestion;


    
    @BindView(R2.id.basTransfer_normalView)
    RelativeLayout basTransfer_normalView;
    @BindView(R2.id.basTransfer_normalDesc)
    TextView basTransfer_normalDesc;
    @BindView(R2.id.basTransfer_normalValue)
    TextView basTransfer_normalValue;
    @BindView(R2.id.basTransfer_normalTime)
    TextView basTransfer_normalTime;


    
    @BindView(R2.id.basTransfer_fastView)
    RelativeLayout basTransfer_fastView;
    @BindView(R2.id.basTransfer_fastDesc)
    TextView basTransfer_fastDesc;
    @BindView(R2.id.basTransfer_fastValue)
    TextView basTransfer_fastValue;
    @BindView(R2.id.basTransfer_fastTime)
    TextView basTransfer_fastTime;

    
    @BindView(R2.id.basTransfer_custom)
    TextView basTransfer_custom;

    @BindView(R2.id.gaoji_open)
    CheckBox gaoji_open;
    
    @BindView(R2.id.gaoji_gas_layout)
    View gaoji_gas_layout;

    @BindView(R2.id.gaoji)
    View gaoji;
    @BindView(R2.id.gas)
    EditText gas;


    private Context mContext;
    private Intent intent;
    private Dialog mDialog;
    private TransferBean data;
    private int type = 0;
    private goTransfer goTransfer;
    private InputPwdDialog mPwdDialog;
    private Dismiss dismiss;

    public interface Dismiss {
        void dismiss();
    }

    public void setOnDismiss(Dismiss dis) {
        dismiss = dis;
    }

    
    public TransferDialog(Context context) {
        this.mContext = context;
        LayoutInflater layoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.dialog_transfer, null);
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

    public void show(TransferBean data, TransConfirmDialogBuilder.TransConfirmParams params) {

        
        basTransfer_normalTime.setText(mContext.getString(R.string.bql_about) + "6" + mContext.getString(R.string.bql_second));
        basTransfer_fastTime.setText(mContext.getString(R.string.bql_about) + "6" + mContext.getString(R.string.bql_second));

        basTransfer_normalValue.setText("");
        basTransfer_fastValue.setText("");

        String gas = this.gas.getText().toString().trim();
        Log.i("TransferDialog", "----------------------" + gas);

        basTransfer_normalView.setOnClickListener(v -> {
            
            basTransfer_normalView.setSelected(true);
            basTransfer_fastView.setSelected(false);
            basTransfer_custom.setSelected(false);
            gaoji.setVisibility(View.GONE);
        });
        basTransfer_fastView.setOnClickListener(v -> {
            
            basTransfer_normalView.setSelected(false);
            basTransfer_fastView.setSelected(true);
            basTransfer_custom.setSelected(false);
            gaoji.setVisibility(View.GONE);
        });
        basTransfer_custom.setOnClickListener(v -> {
            
            basTransfer_normalView.setSelected(false);
            basTransfer_fastView.setSelected(false);
            basTransfer_custom.setSelected(true);
            gaoji.setVisibility(View.VISIBLE);
        });

        gaoji_open.setChecked(false);
        gaoji_gas_layout.setVisibility(View.GONE);
        gaoji_open.setOnCheckedChangeListener((buttonView, isChecked) -> {
            
            gaoji_gas_layout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (isChecked) {
                
                basTransfer_normalView.setSelected(true);
                basTransfer_fastView.setSelected(false);
                basTransfer_custom.setSelected(false);
                gaoji.setVisibility(View.GONE);
            }
        });

        ru_address_copy.setOnClickListener(v -> {
            AllUtils.copyText(data.getAllAddress());
        });
        pay_address_copy.setOnClickListener(v -> {
            AllUtils.copyText(data.getPayaddress());
        });
        miner_costs_congestion.setVisibility(View.VISIBLE);


        ruAddress.setText(data.getAllAddress());
        payAddress.setText(data.getPayaddress());
        minerCosts.setText(data.getKuanggong());
        if (!TextUtils.isEmpty(data.getGasprice()) && data.getGascount() > 0) {
            minerCostsText.setVisibility(View.VISIBLE);
            minerCostsText.setText("≈Gas(" + data.getGascount() + ")*GasPrice(" + data.getGasprice() + "gwei)");
        } else {
            minerCostsText.setVisibility(View.GONE);
        }
        amount.setText(data.getPrice());
        if (TextUtils.isEmpty(data.getPrice())) {
            amount_lin.setVisibility(View.GONE);
        }
        if (data.getType() == WalletUtil.TRX_COIN && !TextUtils.isEmpty(data.getData())) {
            orderInfo.setText(data.getInfo());
            amount_lin.setVisibility(View.GONE);
            ruAddress.setText(data.getData());
        }

        if (data.getType() == WalletUtil.TRX_COIN && (data.getInfo().equalsIgnoreCase("FreezeBalanceContract") || data.getInfo().equalsIgnoreCase("UnfreezeBalanceContract"))) {
            orderInfo.setText(data.getInfo());
            amount_lin.setVisibility(View.GONE);
            ruAddress.setText("");
        }

        if (TextUtils.isEmpty(data.getAllAddress())) {
            ru_address_parent.setVisibility(View.GONE);
        }
        if (data.getType() != WalletUtil.TRX_COIN) {
            if (!TextUtils.isEmpty(data.getInfo())) {
                orderInfo.setText(data.getInfo());
            }
        }

        if (data.getType() == WalletUtil.TRX_COIN) {
            fee_lin.setVisibility(View.GONE);
        }
        this.data = data;
        mDialog.show();
    }

    public void show(TransferBean data) {
        show(data, null);
    }

    public interface goTransfer {
        void goTransfer(String pwd);
    }

    public void setTrans(goTransfer go) {
        goTransfer = go;
    }

    public void dismiss() {
        mDialog.dismiss();
    }

    @OnClick({R2.id.close_dialog, R2.id.btn_sub})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.close_dialog) {
            if (null != dismiss) {
                dismiss.dismiss();
            }
            dismiss();
        } else if (i == R.id.btn_sub) {
            
            dismiss();
            confirmTransfer();

        } else {
        }
    }


    
    private void confirmTransfer() {
        if (mPwdDialog == null) {
            mPwdDialog = new InputPwdDialog(mContext, mContext.getString(R.string.place_edit_password));
        }
        mPwdDialog.setonclick(new InputPwdDialog.Onclick() {
            @Override
            public void Yes(String pwd) {
                if (TextUtils.isEmpty(pwd)) {
                    ToastUtil.showToast(R.string.place_edit_password);
                    return;
                }
                
                if (null != goTransfer) {
                    WalletEntity entity = data.mWalletEntity;
                    if (null != entity) {
                        if (!entity.getmPassword().equals(DecriptUtil.MD5(pwd))) {
                            ToastUtil.showToast(com.wallet.ctc.R.string.password_error2);
                            return;
                        }
                        mPwdDialog.dismiss();
                        goTransfer.goTransfer(pwd);
                    } else {
                        mPwdDialog.dismiss();
                        goTransfer.goTransfer(pwd);
                    }
                } else {
                    mPwdDialog.dismiss();
                    ToastUtil.showToast("no set btn callback");
                }
            }

            @Override
            public void No() {
                if (null != dismiss) {
                    dismiss.dismiss();
                }
                mPwdDialog.dismiss();
            }
        });
        mPwdDialog.show();
    }

    public Dialog getDialog() {
        return mDialog;
    }
}
