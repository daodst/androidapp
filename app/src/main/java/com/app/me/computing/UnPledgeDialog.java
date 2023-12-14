package com.app.me.computing;

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

import com.app.me.destory.DestoryPledgeVM;
import com.wallet.ctc.R;
import com.wallet.ctc.databinding.DialogUnPledgeBinding;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.EvmosPledgeConfigBean;
import com.wallet.ctc.model.blockchain.EvmosTotalPledgeBean;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import common.app.base.view.SimpleListDialog;
import common.app.mall.util.ToastUtil;


public class UnPledgeDialog {

    private Context mContext;
    private Dialog mContentDialog;
    private DialogUnPledgeBinding mViewBinding;
    private WalletEntity mSelecteWallet;
    private DestoryPledgeVM mViewModel;

    public UnPledgeDialog(Context context, WalletEntity selecteWallet, DestoryPledgeVM vm) {
        this.mContext = context;
        this.mContentDialog = new Dialog(context);
        this.mSelecteWallet = selecteWallet;
        this.mContentDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.mViewModel = vm;
    }

    
    private void initDialogAndShow() {
        mViewBinding = DialogUnPledgeBinding.inflate(LayoutInflater.from(mContext));
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

    EvmosTotalPledgeBean.Delegation mDelegation;
    private String mNodeNum;
    private EvmosPledgeConfigBean mConfig;

    private void initListener(EvmosPledgeConfigBean config) {
        mConfig = config;

        mDelegation = config.delegations.get(0);
        String nodeName = mDelegation.delegation.validator_name;
        mNodeNum = getTenDecimalValue(mConfig.available, config.decimal);
        String all = getTenDecimalValue(mDelegation.balance.amount, config.decimal);

        mViewBinding.nodeNameTv.setText(nodeName);
        mViewBinding.numTv.setText(mNodeNum + config.getShowTokenName());


        Context context = mViewBinding.tvUnpledgeYueAll.getContext();
        String wakuangyue = context.getString(R.string.sm_pledge_string_177) + "<font color=\"#111111\">" + all + config.getTokenName().toUpperCase() + "</font>";
        mViewBinding.tvUnpledgeYueAll.setText(Html.fromHtml(wakuangyue));
        String wakuangyue2 = context.getString(R.string.sm_pledge_string_178) + "<font color=\"#111111\">" + mNodeNum + config.getTokenName().toUpperCase() + "</font>";
        mViewBinding.tvUnpledgeYueAvailable.setText(Html.fromHtml(wakuangyue2));


        
        mViewBinding.btnAll.setOnClickListener(view -> {
            mViewBinding.editNum.setText(mNodeNum + "");
        });

        
        mViewBinding.noedLayout.setOnClickListener(view -> {
            List<String> list = new ArrayList<>();
            for (EvmosTotalPledgeBean.Delegation de : config.delegations) {
                list.add(de.delegation.validator_name + "(" + getTenDecimalValue(de.balance.amount, config.decimal) + ")");
            }
            SimpleListDialog listDialog = new SimpleListDialog(mContext, "", list);
            listDialog.setOnItemClickListener((adapterView, view1, position, l) -> {
                listDialog.dismiss();
                mDelegation = config.delegations.get(position);
                String selecteName = mDelegation.delegation.validator_name;
                mNodeNum = getTenDecimalValue(mDelegation.balance.amount, config.decimal);
                mViewBinding.nodeNameTv.setText(selecteName);
                mViewBinding.numTv.setText(mNodeNum + config.getShowTokenName());
            });
            listDialog.show();
        });

        
        mViewBinding.btnShuhui.setOnClickListener(view -> {
            String inputNum = mViewBinding.editNum.getText().toString().trim();
            if (TextUtils.isEmpty(inputNum)) {
                ToastUtil.showToast(R.string.please_input_unple_num);
                return;
            }

            if (null == mDelegation) {
                ToastUtil.showToast(mContext.getString(R.string.please_selecte_gateway));
                return;
            }

            if (new BigDecimal(inputNum).compareTo(new BigDecimal(mNodeNum)) > 0) {
                ToastUtil.showToast(mContext.getString(R.string.shuhui_num_error));
                return;
            }


            dismiss();
            mViewModel.getUnPledgeGas(mDelegation.delegation.delegator_address, mDelegation.delegation.validator_address, inputNum, mConfig);

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
