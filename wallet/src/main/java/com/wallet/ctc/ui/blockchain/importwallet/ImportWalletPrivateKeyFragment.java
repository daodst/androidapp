

package com.wallet.ctc.ui.blockchain.importwallet;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.base.BaseFragment;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.RootWalletInfo;
import com.wallet.ctc.util.DecriptUtil;
import com.wallet.ctc.util.LogUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import common.app.mall.util.ToastUtil;
import common.app.ui.view.InputPwdDialog;
import common.app.utils.ThreadManager;



public class ImportWalletPrivateKeyFragment extends BaseFragment {

    @BindView(R2.id.password)
    EditText password;
    @BindView(R2.id.confirm_password)
    EditText confirmPassword;
    @BindView(R2.id.password_prompt)
    EditText passwordPrompt;
    @BindView(R2.id.check)
    CheckBox check;
    @BindView(R2.id.import_wallet)
    Button importWallet;
    Unbinder mUnbinder;
    @BindView(R2.id.private_key)
    EditText privateKey;
    @BindView(R2.id.wallet_name)
    EditText walletName;
    @BindView(R2.id.pwdLayout)
    LinearLayout pwdLayout;


    private Intent intent;
    protected Dialog mLoadingDialog;
    private boolean loading=false;
    private RootWalletInfo mRootWalletInfo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_import_wallet_private_key, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        setDialog();
        initView(view, inflater);
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
        if (TextUtils.isEmpty(BuildConfig.ENABLE_CREAT_ALL_WALLET)) {
            
            pwdLayout.setVisibility(View.VISIBLE);
        } else {
            
            int type = ((ImportWalletActivity) getActivity()).getType();
            mRootWalletInfo = WalletDBUtil.getInstent(getContext()).getRootWalletInfo(type);
            if (mRootWalletInfo == null || mRootWalletInfo.rootWallet == null) {
                
                pwdLayout.setVisibility(View.VISIBLE);
            } else {
                
                pwdLayout.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @OnClick({R2.id.look_xieyi, R2.id.import_wallet, R2.id.what_is_mnemonic})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.look_xieyi) {
            ((ImportWalletActivity) getActivity()).getUrl("service", getString(R.string.protocol));

        } else if (i == R.id.import_wallet) {
            if(!check.isChecked()){
                return;
            }
            String pKey = privateKey.getText().toString().trim();
            String names = walletName.getText().toString().trim();
            String pwd = password.getText().toString().trim();
            String pwd2 = confirmPassword.getText().toString().trim();
            String pwdkey = passwordPrompt.getText().toString().trim();
            if (TextUtils.isEmpty(pKey)) {
                ToastUtil.showToast(getString(R.string.private_key_eror));
                return;
            }

            if (TextUtils.isEmpty(names)) {
                ToastUtil.showToast(getString(R.string.place_edit_wallet_name));
                return;
            }
            
            int type = ((ImportWalletActivity) getActivity()).getType();
            if (WalletDBUtil.getInstent(getContext()).hasWallet(names, type)) {
                ToastUtil.showToast(getString(R.string.wallet_name_ishas));
                return;
            }

            if (pwdLayout.getVisibility() == View.VISIBLE) {
                if (TextUtils.isEmpty(pwd)) {
                    ToastUtil.showToast(getString(R.string.place_edit_password));
                    return;
                }
                if (pwd.length() < 8) {
                    ToastUtil.showToast(getString(R.string.password_remind));
                    return;
                }
                if (TextUtils.isEmpty(pwd2)) {
                    ToastUtil.showToast(getString(R.string.place_edit_commit_password));
                    return;
                }
                if (!pwd.endsWith(pwd2)) {
                    ToastUtil.showToast(getString(R.string.password_error));
                    return;
                }
                startThreadImportWallet(pKey, pwd, pwdkey, names);
            } else {
                showPwdInputDialog(pKey, pwdkey, names, mRootWalletInfo.rootWallet);
            }

        } else if (i == R.id.what_is_mnemonic) {
            try {
                Uri uri = Uri.parse("https://www.google.com/search?q=" + getString(R.string.what_is_private_key));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
        }
    }

    private void showPwdInputDialog(String pKey, String pwdKey, String names, WalletEntity rootWallet) {
        InputPwdDialog pwdDialog = new InputPwdDialog(getContext(), getString(R.string.place_edit_password));
        pwdDialog.setonclick(new InputPwdDialog.Onclick() {
            @Override
            public void Yes(String pwd) {
                if (TextUtils.isEmpty(pwd)) {
                    ToastUtil.showToast(R.string.place_edit_password);
                    return;
                }
                if (!rootWallet.getmPassword().equals(DecriptUtil.MD5(pwd))) {
                    ToastUtil.showToast(com.wallet.ctc.R.string.password_error2);
                    return;
                }
                pwdDialog.dismiss();
                
                startThreadImportWallet(pKey, pwd, pwdKey, names);
            }

            @Override
            public void No() {
                pwdDialog.dismiss();
            }
        });
        pwdDialog.show();
    }


    private void startThreadImportWallet(String pKey, String pwd, String pwdkey, String names) {
        if (loading) {
            return;
        }
        mLoadingDialog.show();
        loading = true;
        try {
            ThreadManager.getNormalPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        WalletEntity mWallet;
                        int type=((ImportWalletActivity) getActivity()).getType();
                        mWallet= WalletUtil.ImportWalletByPrivateKey(pKey, pwd,type);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loading = false;
                                if (mWallet == null) {
                                    mLoadingDialog.dismiss();
                                    ToastUtil.showToast(getString(R.string.private_key_eror2));
                                    return;
                                }
                                mWallet.setmPasswordHint(pwdkey);
                                mWallet.setName(names);
                                mWallet.setmPassword(DecriptUtil.MD5(pwd));
                                mWallet.setmBackup(1);
                                mWallet.setMMnemonicBackup(1);
                                checkWallet(mWallet);
                            }
                        });
                    } catch (Exception e) {
                        LogUtil.d(e.toString());
                        loading = false;
                        ToastUtil.showToast(getString(R.string.private_key_eror2));
                    }
                }
            });
        } catch (Exception e) {
            LogUtil.d(e.toString());
        }
    }

    private void checkWallet(WalletEntity mWallet){
        String pwd = password.getText().toString().trim();
        if(mWallet==null){
            mLoadingDialog.dismiss();
            ToastUtil.showToast(getString(R.string.private_key_error));
            return;
        }
        String data=new Gson().toJson(mWallet);
        WalletEntity walletEntity=new Gson().fromJson(data,WalletEntity.class);
        ((ImportWalletActivity)getActivity()).insert(walletEntity, pwd);
    }

}
