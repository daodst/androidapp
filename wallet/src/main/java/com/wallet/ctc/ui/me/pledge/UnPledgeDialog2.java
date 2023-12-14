package com.wallet.ctc.ui.me.pledge;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.wallet.ctc.R;
import com.wallet.ctc.databinding.DialogUnPledge2Binding;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.EvmosPledgeConfigBean;

import java.math.BigDecimal;
import java.math.RoundingMode;

import common.app.mall.util.ToastUtil;


public class UnPledgeDialog2 {

    private Context mContext;
    private Dialog mContentDialog;
    private DialogUnPledge2Binding mViewBinding;
    private WalletEntity mSelecteWallet;
    private SMPledgeActivityVM mViewModel;

    public UnPledgeDialog2(Context context, WalletEntity selecteWallet, SMPledgeActivityVM vm) {
        this.mContext = context;
        this.mContentDialog = new Dialog(context);
        this.mSelecteWallet = selecteWallet;
        this.mContentDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.mViewModel = vm;
    }

    
    private void initDialogAndShow() {
        mViewBinding = DialogUnPledge2Binding.inflate(LayoutInflater.from(mContext));
        View contentView = mViewBinding.getRoot();
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mContentDialog.setContentView(contentView, layoutParams);
        Window win = mContentDialog.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        win.getDecorView().setBackgroundColor(Color.TRANSPARENT);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        lp.windowAnimations = R.style.dialogAnim;
        win.setAttributes(lp);
        mContentDialog.show();
    }

    private String mNodeNum;
    private EvmosPledgeConfigBean mConfig;

    private void initListener(EvmosPledgeConfigBean config) {
        mConfig = config;
        mNodeNum = config.hasNum;
        
        mViewBinding.btnAll2.setOnClickListener(view -> {
            mViewBinding.editNum2.setText(mNodeNum + "");
        });
        Context context = mViewBinding.tvPledge2Yue.getContext();
        String wakuangyue = context.getString(R.string.sm_pledge_string_17) + "<font color=\"#111111\">" + mNodeNum + config.getTokenName().toUpperCase() + "</font>";
        mViewBinding.tvPledge2Yue.setText(Html.fromHtml(wakuangyue));
        
        mViewBinding.btnShuhui2.setOnClickListener(view -> {
            String inputNum = mViewBinding.editNum2.getText().toString().trim();
            if (TextUtils.isEmpty(inputNum)) {
                ToastUtil.showToast(R.string.please_input_ple_num);
                return;
            }

            if (new BigDecimal(inputNum).compareTo(new BigDecimal(mNodeNum)) > 0) {
                ToastUtil.showToast(mContext.getString(R.string.shuhui_num_error));
                return;
            }

            dismiss();
            mViewModel.getHashPledgeGas(mSelecteWallet.getAllAddress(), inputNum, mConfig, mSelecteWallet.getAllAddress());
        });
    }


    public void show(EvmosPledgeConfigBean config) {
        if (null == config || !config.isSuccess || null == config.delegations || config.delegations.size() == 0) {
            return;
        }
        initDialogAndShow();
        initListener(config);
    }

    public void dismiss() {
        if (null != mContentDialog) {
            mContentDialog.dismiss();
        }
    }

    
    private String getTenDecimalValue(String bigNum, int decimal) {
        if (TextUtils.isEmpty(bigNum)) {
            return bigNum;
        }
        return new BigDecimal(bigNum).divide(new BigDecimal(Math.pow(10, decimal)), 6, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }
}
