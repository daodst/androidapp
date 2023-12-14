

package com.wallet.ctc.view.dialog;

import android.app.Activity;
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

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.model.blockchain.TransferBean;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.app.mall.util.ToastUtil;



public class CandyBoxSendDialog {
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

    private Activity mContext;
    private Intent intent;
    private Dialog mDialog;
    private TransferBean data;
    private int type = 0;
    private TransferDialog.goTransfer goTransfer;

    
    public CandyBoxSendDialog(Activity context) {
        this.mContext = context;
        LayoutInflater layoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.dialog_candybox, null);
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
        ruAddress.setText(data.getRuaddress());
        payAddress.setText(data.getPayaddress());
        if (data.getType()== WalletUtil.DM_COIN||data.getType()== WalletUtil.MCC_COIN||data.getType()== WalletUtil.OTHER_COIN) {
            minerCosts.setText(data.getKuanggong());
            minerCostsText.setVisibility(View.GONE);
            minerCostsText.setText("");
        }else if (data.getType()==1) {
            minerCosts.setText(data.getKuanggong());
            minerCostsText.setVisibility(View.VISIBLE);
            minerCostsText.setText("≈Gas("+data.getGascount()+")*GasPrice("+data.getGasprice()+"gwei)");
        }

        amount.setText(data.getPrice());
        this.data = data;
        mDialog.show();
    }

    public interface goTransfer{
        void goTransfer(String pwd);
    }
    public void setTrans(TransferDialog.goTransfer go){
        goTransfer=go;
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
            if (type == 0) {
                type = 1;
                payInfo.setVisibility(View.GONE);
                inputpwd.setVisibility(View.VISIBLE);
                title.setText(mContext.getString(R.string.place_edit_password));
            } else {
                String pwd = password.getText().toString().trim();
                if (TextUtils.isEmpty(pwd)) {
                    ToastUtil.showToast(mContext.getResources().getString(R.string.place_edit_password));
                    return;
                }
                dismiss();
                goTransfer.goTransfer(pwd);
            }

        } else {
        }
    }
}
