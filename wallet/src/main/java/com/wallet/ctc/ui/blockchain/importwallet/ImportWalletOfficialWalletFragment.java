

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
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.base.BaseFragment;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.GoKeystory;
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



public class ImportWalletOfficialWalletFragment extends BaseFragment {


    Unbinder mUnbinder;
    @BindView(R2.id.content_edit)
    EditText contentEdit;
    @BindView(R2.id.password)
    EditText password;
    @BindView(R2.id.check)
    CheckBox check;
    @BindView(R2.id.look_xieyi)
    TextView lookXieyi;
    @BindView(R2.id.import_wallet)
    Button importWallet;
    @BindView(R2.id.what_is_mnemonic)
    TextView whatIsMnemonic;
    @BindView(R2.id.wallet_name)
    EditText walletName;
    private Intent intent;
    private Gson gson=new GsonBuilder()
            .disableHtmlEscaping() 
            .create();
    protected Dialog mLoadingDialog;
    private boolean loading=false;
    private RootWalletInfo mRootWalletInfo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_import_wallet_offocoal, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        initView(view, inflater);
        setDialog();
        return view;
    }

    private void initView(View view, LayoutInflater inflater) {
        if (!TextUtils.isEmpty(BuildConfig.ENABLE_CREAT_ALL_WALLET)) {
            
            int type = ((ImportWalletActivity) getActivity()).getType();
            mRootWalletInfo = WalletDBUtil.getInstent(getContext()).getRootWalletInfo(type);
        }
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    public void setKey(String key){
        contentEdit.setText(key);
    }
    String keystore;
    @OnClick({R2.id.look_xieyi, R2.id.import_wallet, R2.id.what_is_mnemonic})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.look_xieyi) {
            ((ImportWalletActivity) getActivity()).getUrl("service", getString(R.string.protocol));

        } else if (i == R.id.import_wallet) {
            if(!check.isChecked()){
                return;
            }
            keystore = contentEdit.getText().toString().trim();
            String name = walletName.getText().toString().trim();
            String pwd = password.getText().toString().trim();
            if (TextUtils.isEmpty(keystore)) {
                ToastUtil.showToast(getString(R.string.keystore_empty));
                return;
            }
            if (TextUtils.isEmpty(name)) {
                ToastUtil.showToast(getString(R.string.place_edit_wallet_name));
                return;
            }

            
            int type = ((ImportWalletActivity) getActivity()).getType();
            if (WalletDBUtil.getInstent(getContext()).hasWallet(name, type)) {
                ToastUtil.showToast(getString(R.string.wallet_name_ishas));
                return;
            }


            try {
                keystore = keystore.replace(" ", "");
                LogUtil.d("keystore" + keystore);
                gson.fromJson(keystore, GoKeystory.class);
            } catch (Exception e) {
                LogUtil.d("" + e.toString());
                ToastUtil.showToast(getString(R.string.keystore_error));
                return;
            }
            if (TextUtils.isEmpty(pwd)) {
                ToastUtil.showToast(getString(R.string.password_error2));
                return;
            }

            if (null != mRootWalletInfo && mRootWalletInfo.rootWallet != null) {
                showPwdInputDialog(keystore, pwd, name, mRootWalletInfo.rootWallet);
            } else {
                startThreadImportWallet(keystore, pwd, pwd, name);
            }
        } else if (i == R.id.what_is_mnemonic) {
            try {
                Uri uri = Uri.parse("https://www.google.com/search?q=" + getString(R.string.what_is_official_wallet));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
        }
    }

    private void showPwdInputDialog(String keystore, String srcPwd, String names, WalletEntity rootWallet) {
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
                
                startThreadImportWallet(keystore, srcPwd, pwd, names);
            }

            @Override
            public void No() {
                pwdDialog.dismiss();
            }
        });
        pwdDialog.show();
    }


    private void startThreadImportWallet(String keystore, String pwd, String newPwd, String name) {
        if (loading) {
            return;
        }
        mLoadingDialog.show();
        loading = true;
        ThreadManager.getNormalPool().execute(new Runnable() {
            @Override
            public void run() {
                int type=((ImportWalletActivity) getActivity()).getType();
                WalletEntity mWallet = WalletUtil.ImportWalletByKeystore(keystore, pwd,newPwd, type);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loading = false;
                        if (mWallet == null) {
                            mLoadingDialog.dismiss();
                            ToastUtil.showToast(getString(R.string.password_error2));
                            return;
                        }
                        mWallet.setmPasswordHint("");
                        mWallet.setName(name);
                        mWallet.setmPassword(DecriptUtil.MD5(pwd));
                        mWallet.setmBackup(1);
                        mWallet.setMMnemonicBackup(1);
                        checkWallet(mWallet, pwd);
                    }
                });
            }
        });
    }




    private void checkWallet(WalletEntity mWallet, String pwd){
        if(mWallet==null){
            mLoadingDialog.dismiss();
            ToastUtil.showToast(getString(R.string.keystore_error));
            return;
        }
        String data=new Gson().toJson(mWallet);
        WalletEntity walletEntity=new Gson().fromJson(data,WalletEntity.class);
        ((ImportWalletActivity)getActivity()).insert(walletEntity, pwd);
    }
}
