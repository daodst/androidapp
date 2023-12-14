

package com.wallet.ctc.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.model.blockchain.TransferBean;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.app.mall.util.ToastUtil;
import common.app.ui.view.InputPwdDialog;
import common.app.utils.SpUtil;



public class TransferEthDialog {
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
    private Context mContext;
    private Intent intent;
    private Dialog mDialog;
    private TransferBean data;
    private int type = 0;
    private goTransfer goTransfer;
    private InputPwdDialog mPwdDialog;
    private Dismiss dismiss;

    public interface Dismiss{
        void dismiss();
    }
    public void setOnDismiss(Dismiss dis){
        dismiss=dis;
    }

    
    public TransferEthDialog(Context context) {
        this.mContext = context;
        LayoutInflater layoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.dialog_ethtransfer, null);
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
        mDialog = dialog;
    }


    public void show(TransferBean data) {
        String tokenName=data.getTokenName();
        ruAddress.setText(data.getAllAddress());
        payAddress.setText(data.getPayaddress());
        if(data.getType()== WalletUtil.ETH_COIN) {
            minerCosts.setText(data.getKuanggong() + "ETH");
        }else if(data.getType()== WalletUtil.ETF_COIN){
            minerCosts.setText(data.getKuanggong() + mContext.getString(R.string.default_etf).toUpperCase());
        }else if(data.getType()== WalletUtil.DMF_COIN){
            minerCosts.setText(data.getKuanggong() + mContext.getString(R.string.default_dmf_hb).toUpperCase());
        }else if(data.getType()== WalletUtil.DMF_BA_COIN){
            minerCosts.setText(data.getKuanggong() + mContext.getString(R.string.default_dmf_ba).toUpperCase());
        }else if(data.getType()== WalletUtil.HT_COIN){
            minerCosts.setText(data.getKuanggong() + "HT");
        }else if(data.getType()== WalletUtil.BNB_COIN){
            minerCosts.setText(data.getKuanggong() + "BNB");
        }else if(data.getType() == WalletUtil.MCC_COIN) {
            minerCosts.setText(data.getKuanggong() + BuildConfig.EVMOS_FAKE_UNINT);
        }
        String remark="≈Gas("+data.getGascount()+")*GasPrice("+data.getGasprice()+"gwei)";
        if(SpUtil.getFeeStatus()==1&&data.getType()== WalletUtil.ETH_COIN) {
            remark=remark+"\n*"+data.getMaxPriorityFeePerGas()+"gwei";
        }
        minerCostsText.setText(remark);
        amount.setText(data.getPrice());
        if(TextUtils.isEmpty(data.getPrice())){
            amount_lin.setVisibility(View.GONE);
        }
        this.data = data;
        mDialog.show();
    }

    public interface goTransfer{
        void goTransfer(String pwd);
    }
    public void setTrans(goTransfer go){
        goTransfer=go;
    }

    public void dismiss() {
        mDialog.dismiss();
    }

    @OnClick({R2.id.close_dialog, R2.id.btn_sub})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.close_dialog) {
            if(null!=dismiss){
                dismiss.dismiss();
            }
            dismiss();

        } else if (i == R.id.btn_sub) {
            
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
                dismiss();
                mPwdDialog.dismiss();
                
                if (null != goTransfer) {
                    goTransfer.goTransfer(pwd);
                }
            }

            @Override
            public void No() {
                mPwdDialog.dismiss();
            }
        });
        mPwdDialog.show();
    }
}
