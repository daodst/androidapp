

package com.wallet.ctc.ui.blockchain.importwallet;

import static com.wallet.ctc.crypto.WalletDBUtil.USER_ID;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.base.BaseFragment;
import com.wallet.ctc.crypto.ChatSdk;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.DBManager;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.util.SettingPrefUtil;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import common.app.mall.util.ToastUtil;
import common.app.utils.ThreadManager;



public class ImportWalletAddressFragment extends BaseFragment {
    @BindView(R2.id.wallet_address)
    EditText walletAddress;
    @BindView(R2.id.wallet_name)
    EditText walletName;
    @BindView(R2.id.check)
    CheckBox check;
    Unbinder mUnbinder;
    protected Dialog mLoadingDialog;
    private Intent intent;
    private boolean loading = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_import_wallet_address, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        initView(view, inflater);
        setDialog();
        return view;
    }

    
    private void setDialog() {
        mLoadingDialog = new Dialog(getActivity(), R.style.progress_dialog);
        mLoadingDialog.setContentView(R.layout.dialog_commom);
        mLoadingDialog.setCancelable(true);
        mLoadingDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent);
        TextView msg = (TextView) mLoadingDialog
                .findViewById(R.id.id_tv_loadingmsg);
        msg.setText(getString(R.string.loading));
    }

    private void initView(View view, LayoutInflater inflater) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @OnClick({R2.id.look_xieyi, R2.id.import_wallet, R2.id.what_is_observe_wallet})
    public void onViewClicked(View view) {
        int i1 = view.getId();
        if (i1 == R.id.look_xieyi) {
            ((ImportWalletActivity) getActivity()).getUrl("service", getString(R.string.protocol));

        } else if (i1 == R.id.import_wallet) {
            if (!check.isChecked()) {
                return;
            }
            String mnemonicStr = walletAddress.getText().toString().trim();
            String names = walletName.getText().toString().trim();
            if (TextUtils.isEmpty(mnemonicStr)) {
                ToastUtil.showToast(getString(R.string.wallet_address_empty));
                return;
            }
            int type = ((ImportWalletActivity) getActivity()).getType();
            if (!WalletUtil.isWalletAddress(type, mnemonicStr)) {
                ToastUtil.showToast(getString(R.string.wallet_address_error));
                return;
            }
            if (TextUtils.isEmpty(names)) {
                ToastUtil.showToast(getString(R.string.place_edit_wallet_name));
                return;
            }

            ThreadManager.getNormalPool().execute(new Runnable() {
                @Override
                public void run() {

                    WalletEntity mWallet = new WalletEntity();
                    mWallet.setLevel(-1);
                    if(type == WalletUtil.MCC_COIN){
                        String dstAddr="", ethAddr = "";
                        if (mnemonicStr.startsWith("0x")) {
                            
                            dstAddr = ChatSdk.ethAddr2DstAddr(mnemonicStr);
                            ethAddr = mnemonicStr;
                        } else {
                            
                            dstAddr = mnemonicStr;
                            ethAddr = ChatSdk.dstAddr2EthAddr(mnemonicStr);
                        }
                        
                        mWallet.setmAddress(ethAddr);
                        mWallet.setmAddress2(dstAddr);
                    }else{
                        mWallet.setmAddress(mnemonicStr);
                    }

                    mWallet.setName(names);
                    mWallet.setmBackup(1);
                    mWallet.setMMnemonicBackup(1);
                    mWallet.setType(type);
                    Random rand = new Random();
                    int i = rand.nextInt(5);
                    mWallet.setLogo(i);
                    mWallet.setUserName(USER_ID);
                    WalletEntity mOldWallet = DBManager.getInstance(getActivity()).queryWalletDetail(mnemonicStr, type);
                    SettingPrefUtil.setWalletTypeAddress(getActivity(), type, mnemonicStr);
                    if (null != mOldWallet) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showToast(getString(R.string.hint_exist_wallet));
                            }
                        });
                        return;
                    }

                    ((ImportWalletActivity) getActivity()).creatOrInsertWallet(type, mWallet.getAllAddress());
                    DBManager.getInstance(getActivity()).insertWallet(mWallet);
                    ((ImportWalletActivity) getActivity()).jump("");
                }
            });

        } else if (i1 == R.id.what_is_observe_wallet) {
            try {
                Uri uri = Uri.parse("https://www.google.com/search?q=" + getString(R.string.what_is_observe_wallet));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
        }
    }
}
