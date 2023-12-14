package com.wallet.ctc.ui.me.chain_bridge2.orders;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;

import com.wallet.ctc.R;
import com.wallet.ctc.crypto.ChatSdk;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.databinding.ItemChainBridgeOrderBinding;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.ChainBridgeOrderBean;
import com.wallet.ctc.ui.me.chain_bridge2.detail.ChainBridgeDetailActivity;
import com.wallet.ctc.util.AllUtils;
import com.wallet.ctc.util.DecriptUtil;

import common.app.mall.util.ToastUtil;
import common.app.my.view.MyAlertDialog;
import common.app.ui.adapter.SimpleBaseAdapter;
import common.app.ui.adapter.SimpleBaseViewHolder;
import common.app.ui.view.InputPwdDialog;
import common.app.utils.TimeUtil;


public class ChainBridgeOrdersAdapter extends SimpleBaseAdapter<ChainBridgeOrderBean, ChainBridgeOrdersAdapter.ViewHolder> {

    private int mType;

    public ChainBridgeOrdersAdapter(Context context, int type) {
        super(context);
        this.mType = type;
    }

    interface OnRefreshDataListener{
        void onRefresh();
    }
    private OnRefreshDataListener mListener;
    public void setOnRefreshDataListener(OnRefreshDataListener listener){
        this.mListener = listener;
    }

    @Override
    protected ChainBridgeOrdersAdapter.ViewHolder createViewHolder(View convertView) {
        return new ViewHolder(convertView);
    }

    @Override
    public int getItemLayoutId() {
        return R.layout.item_chain_bridge_order;
    }

    @Override
    public void showItem(ChainBridgeOrdersAdapter.ViewHolder holder, ChainBridgeOrderBean data) {
        holder.setData(data);
    }


    public class ViewHolder extends SimpleBaseViewHolder{
        ItemChainBridgeOrderBinding viewBinding;
        public ViewHolder(View view) {
            super(view);
            viewBinding = ItemChainBridgeOrderBinding.bind(view);
        }

        public void setData(ChainBridgeOrderBean data){

            int buyWalletType = ChatSdk.chainNameToType(data.buy_chain);
            int sellWalletType = ChatSdk.chainNameToType(data.sell_chain);
            AssertBean fromChainAseert = WalletUtil.getMainChainAssert(mContext, buyWalletType);
            AssertBean fromExAssert = WalletUtil.getUsdtAssert(buyWalletType);
            AssertBean toChainAssert = WalletUtil.getMainChainAssert(mContext, sellWalletType);
            AssertBean toExAssert = WalletUtil.getUsdtAssert(sellWalletType);
            viewBinding.fromNumTv.setText(AllUtils.getTenDecimalValue(data.order_amount, 18, 6));
            viewBinding.fromCoinTv.setText(fromExAssert.getShortNameUpCase());
            viewBinding.fromCoinIv.setImageResource(fromChainAseert.getLogo());

            viewBinding.toNumTv.setText(AllUtils.getTenDecimalValue(data.receive_amount, 18, 6));
            viewBinding.toCoinTv.setText(toExAssert.getShortNameUpCase());
            viewBinding.toCoinIv.setImageResource(toChainAssert.getLogo());

            viewBinding.statusTv.setText(data.getStatusName(mContext));
            viewBinding.timeTv.setText(TimeUtil.getYYYYMMddHHMM(data.create_time*1000));

            viewBinding.optBtn.setOnClickListener(null);
            if (data.hasErrors()){
                viewBinding.errorLayout.setVisibility(View.VISIBLE);
                if (mType == ChainBridgeOrdersFragment.TYPE_ING){
                    
                    viewBinding.optBtn.setVisibility(View.VISIBLE);
                    viewBinding.alertTv.setText(R.string.chain_b_ing_order_ex1);
                    viewBinding.optBtn.setOnClickListener(view -> {
                        
                        progressError(data);
                    });
                    viewBinding.alertTv.setOnClickListener(view -> {
                        
                        showErrorDialog(data.getErrorsInfo(mContext), mContext.getString(R.string.exception_info_title));
                    });
                } else {
                    
                    viewBinding.optBtn.setVisibility(View.GONE);
                    viewBinding.alertTv.setText(R.string.chain_b_over_order_ex2);
                    viewBinding.alertTv.setOnClickListener(view -> {
                        showErrorDialog(data.getErrorsInfo(mContext), mContext.getString(R.string.exception_info_title));
                    });
                }
            } else {
                viewBinding.errorLayout.setVisibility(View.GONE);
            }


            
            viewBinding.getRoot().setOnClickListener(view -> {
                mContext.startActivity(ChainBridgeDetailActivity.getIntent(mContext, data.getMainOrderId()));
            });
        }

    }


    
    private void progressError(ChainBridgeOrderBean data) {
        if (data == null || !data.hasErrors()) {
            return;
        }
        if(data.hasNoPrivateKeyError()) {
            
            int buyWalletType = ChatSdk.chainNameToType(data.buy_chain);
            int sellWalletType = ChatSdk.chainNameToType(data.sell_chain);
            
            showAuthPrivateKey(data.payer, buyWalletType, data.buyer, sellWalletType);
        } else if(data.hasNodeConnectError()) {
            
            showErrorDialog(mContext.getString(R.string.rpc_node_net_error_alert), mContext.getString(R.string.rpc_node_net_error_title));
        } else {
            showErrorDialog(data.getErrorsInfo(mContext), mContext.getString(R.string.chain_b_exchange_exception));
        }
    }

    
    private void showErrorDialog(String errorInfo, String title) {
        if (TextUtils.isEmpty(errorInfo)){
            return;
        }
        MyAlertDialog alertDialog = new MyAlertDialog(mContext, errorInfo);
        alertDialog.setonclick(new MyAlertDialog.Onclick() {
            @Override
            public void Yes() {
                alertDialog.dismiss();
            }

            @Override
            public void No() {
                alertDialog.dismiss();
            }
        });
        alertDialog.setTitle(title);
        alertDialog.setNoBtnGone();
        alertDialog.show();
    }

    private void showAuthPrivateKey(String fromAddr, int fromWalletType, String toAddr, int toWalletType) {
        WalletEntity fromWallet = WalletDBUtil.getInstent(mContext).getWalletInfoByAddress(fromAddr, fromWalletType);
        WalletEntity toWallet = WalletDBUtil.getInstent(mContext).getWalletInfoByAddress(toAddr, toWalletType);
        if (null == fromWallet || null == toWallet){
            ToastUtil.showToast(R.string.no_found_wallet_info);
            return;
        }
        MyAlertDialog alertDialog = new MyAlertDialog(mContext, mContext.getString(R.string.chain_bridge_auth_prikey_alert));
        alertDialog.setonclick(new MyAlertDialog.Onclick() {
            @Override
            public void Yes() {
                alertDialog.dismiss();
                InputPwdDialog.show(mContext, (pwd, dialog) -> {
                    if (!fromWallet.getmPassword().equals(DecriptUtil.MD5(pwd))) {
                        ToastUtil.showToast(com.wallet.ctc.R.string.password_error2);
                        return;
                    }
                    dialog.dismiss();
                    String fromPrivateKey = fromWallet.decodePrivateKey(pwd, true);
                    String toPrivateKey = toWallet.decodePrivateKey(pwd, true);
                    if (TextUtils.isEmpty(fromPrivateKey) || TextUtils.isEmpty(toPrivateKey)){
                        ToastUtil.showToast(mContext.getString(R.string.get_private_key_fail));
                        return;
                    }
                    ChatSdk.addCrossWalletPir(fromPrivateKey);
                    ChatSdk.addCrossWalletPir(toPrivateKey);
                    ToastUtil.showToast(R.string.operate_success);
                    if(null != mListener){
                        mListener.onRefresh();
                    }
                });
            }

            @Override
            public void No() {
                alertDialog.dismiss();
            }
        });
        alertDialog.setTitle(mContext.getString(R.string.chain_bridge_auth_prikey_title));
        alertDialog.setYesText(mContext.getString(R.string.confirm_auth));
        alertDialog.setContentGravity(Gravity.LEFT);
        alertDialog.setYesTextColor(R.color.default_theme_color);
        alertDialog.show();
    }

}
