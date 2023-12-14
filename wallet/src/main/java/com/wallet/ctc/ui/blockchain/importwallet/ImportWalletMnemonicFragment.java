

package com.wallet.ctc.ui.blockchain.importwallet;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.base.BaseFragment;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.RootWalletInfo;
import com.wallet.ctc.ui.blockchain.importwallet.adapter.WalletMnemonicAdapter;
import com.wallet.ctc.util.DecriptUtil;
import com.wallet.ctc.util.English;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import common.app.im.base.NextSubscriber;
import common.app.mall.util.ToastUtil;
import common.app.ui.view.InputPwdDialog;
import common.app.utils.SoftKeyBoardListener;
import common.app.utils.ThreadManager;
import io.reactivex.Observable;



public class ImportWalletMnemonicFragment extends BaseFragment {
    @BindView(R2.id.mnemonic)
    EditText mnemonic;
    @BindView(R2.id.password)
    EditText password;
    @BindView(R2.id.wallet_name)
    EditText walletName;
    @BindView(R2.id.confirm_password)
    EditText confirmPassword;
    @BindView(R2.id.password_prompt)
    EditText passwordPrompt;
    @BindView(R2.id.check)
    CheckBox check;
    @BindView(R2.id.import_wallet)
    Button importWallet;
    @BindView(R2.id.mnemonic_rv)
    RecyclerView mMnemonicRv;
    @BindView(R2.id.mnemonic_rv_parent)
    FrameLayout mMnemonicRvParent;
    @BindView(R2.id.pwdLayout)
    LinearLayout pwdLayout;



    Unbinder mUnbinder;
    private WalletMnemonicAdapter mMnemonicAdapter;
    protected Dialog mLoadingDialog;
    private Intent intent;
    private boolean loading = false;
    private static final String MNEMONIC_SPLIT = " ";
    private final int TYPE_IMPORT_ROOT = 1;
    private final int TYPE_IMPORT_NORMAL = 2;
    private int mImportType = TYPE_IMPORT_NORMAL;
    private RootWalletInfo mRootWalletInfo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_import_wallet_mnemonic, container, false);
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
        mMnemonicAdapter = new WalletMnemonicAdapter();
        mMnemonicRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mMnemonicRv.setAdapter(mMnemonicAdapter);

        mMnemonicAdapter.setIClick(data -> {
            mMnemonicRvParent.setVisibility(View.GONE);
            String content = mnemonic.getText().toString().trim();
            int i = content.lastIndexOf(MNEMONIC_SPLIT);
            if (i < 0) {
                mnemonic.setText(data + MNEMONIC_SPLIT);
            } else {
                String substring = content.substring(0, i);
                mnemonic.setText(substring + MNEMONIC_SPLIT + data + MNEMONIC_SPLIT);
            }
            mnemonic.setSelection(mnemonic.getText().toString().length());

        });
        new SoftKeyBoardListener(getActivity()).setListener(getActivity(), new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                String content = mnemonic.getText().toString();
                if (mnemonic.hasFocus() && !content.endsWith(MNEMONIC_SPLIT)) {
                    showData(content.trim());
                }
            }

            @Override
            public void keyBoardHide(int height) {
                if (null != mMnemonicRvParent) {
                    mMnemonicRvParent.setVisibility(View.GONE);
                }
            }
        });
        mnemonic.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String data = s.toString();
                if (!data.endsWith(MNEMONIC_SPLIT)) {
                    showData(data.trim());
                }
            }
        });


        if (TextUtils.isEmpty(BuildConfig.ENABLE_CREAT_ALL_WALLET)) {
            
            mImportType = TYPE_IMPORT_NORMAL;
        } else {
            
            int type = ((ImportWalletActivity) getActivity()).getType();
            mRootWalletInfo = WalletDBUtil.getInstent(getContext()).getRootWalletInfo(type);
            if (mRootWalletInfo == null || mRootWalletInfo.rootWallet == null) {
                
                mImportType = TYPE_IMPORT_ROOT;
            } else {
                
                mImportType = TYPE_IMPORT_NORMAL;
            }
        }

        if (mImportType == TYPE_IMPORT_NORMAL) {
            pwdLayout.setVisibility(View.GONE);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @OnClick({R2.id.look_xieyi, R2.id.import_wallet, R2.id.what_is_mnemonic})
    public void onViewClicked(View view) {
        int i1 = view.getId();
        if (i1 == R.id.look_xieyi) {
            ((ImportWalletActivity) getActivity()).getUrl("service", getString(R.string.protocol));

        } else if (i1 == R.id.import_wallet) {
            if (!check.isChecked()) {
                return;
            }
            String mnemonicStr = mnemonic.getText().toString().trim();
            String names = walletName.getText().toString().trim();
            String pwd = password.getText().toString().trim();
            String pwd2 = confirmPassword.getText().toString().trim();
            String pwdkey = passwordPrompt.getText().toString().trim();
            if (TextUtils.isEmpty(mnemonicStr)) {
                ToastUtil.showToast(getString(R.string.mnemonic_error));
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
            }

            String[] strings = mnemonicStr.trim().split(" ");
            if (strings.length != 12 && strings.length != 24) {
                ToastUtil.showToast(getString(R.string.mnemonic_error));
                return;
            }

            List<String> list = new ArrayList<>();
            for (int i = 0; i < strings.length; i++) {
                list.add(strings[i]);
            }

            if (mImportType == TYPE_IMPORT_ROOT) {
                
                startThreadImportWallet(list, pwd, pwdkey, names);
            } else {
                
                showPwdInputDialog(list, pwdkey, names, mRootWalletInfo.rootWallet);
            }
        } else if (i1 == R.id.what_is_mnemonic) {
            try {
                Uri uri = Uri.parse("https://www.google.com/search?q=" + getString(R.string.what_is_mnemonic));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
        }
    }


    private void showPwdInputDialog(List<String> list, String pwdkey,String name, WalletEntity rootWallet) {
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
                
                startThreadImportWallet(list, pwd, pwdkey, name);
            }

            @Override
            public void No() {
                pwdDialog.dismiss();
            }
        });
        pwdDialog.show();
    }


    
    private void startThreadImportWallet(List<String> list, String pwd, String pwdkey, String names) {
        if (loading) {
            return;
        }
        mLoadingDialog.show();
        loading = true;

        ThreadManager.getNormalPool().execute(new Runnable() {
            @Override
            public void run() {
                if (mImportType == TYPE_IMPORT_ROOT) {
                    
                    String[] walletype = BuildConfig.ENABLE_CREAT_ALL_WALLET.split(",");
                    if (walletype.length > 0) {
                        for (int num = 0; num < walletype.length; num++) {
                            int ty = new BigDecimal(walletype[num]).intValue();
                            importWallet(list, pwd, pwdkey, names, ty, false);
                        }
                    }
                    ((ImportWalletActivity) getActivity()).jump(pwd);
                } else {
                    int type = ((ImportWalletActivity) getActivity()).getType();
                    importWallet(list, pwd, pwdkey, names, type, true);
                }

            }
        });
    }


    private void importWallet(List<String> list, String pwd, String pwdkey, String names, int type, boolean jump) {
        WalletEntity mWallet;
        mWallet = WalletUtil.ImportWalletByMnemonic(list, pwd, type);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loading = false;
                if (mWallet == null) {
                    mLoadingDialog.dismiss();
                    ToastUtil.showToast(getString(R.string.mnemonic_error));
                    return;
                }
                mWallet.setmPasswordHint(pwdkey);
                mWallet.setName(names);
                mWallet.setmPassword(DecriptUtil.MD5(pwd));
                mWallet.setmBackup(1);
                mWallet.setMMnemonicBackup(1);
                mWallet.setType(type);
                checkWallet(mWallet, jump, pwd);
            }
        });
    }

    private void checkWallet(WalletEntity mWallet, boolean jump, String pwd) {
        if (mWallet == null) {
            mLoadingDialog.dismiss();
            ToastUtil.showToast(getString(R.string.mnemonic_error));
            return;
        }
        String data = new Gson().toJson(mWallet);
        WalletEntity walletEntity = new Gson().fromJson(data, WalletEntity.class);
        if (jump) {
            ((ImportWalletActivity) getActivity()).insert(walletEntity, pwd);
        } else {
            ((ImportWalletActivity) getActivity()).insertAll(walletEntity, mWallet.getType());
        }
        if(null != mLoadingDialog){
            mLoadingDialog.dismiss();
        }
    }


    private void showData(String data) {
        if (TextUtils.isEmpty(data) || TextUtils.isEmpty(data.trim())) {
            mMnemonicRvParent.setVisibility(View.GONE);
            return;
        }
        
        String[] s1 = data.split(MNEMONIC_SPLIT);
        data = s1[s1.length - 1].trim();
        if (TextUtils.isEmpty(data)) {
            
            mMnemonicRvParent.setVisibility(View.GONE);
            return;
        }
        String finalData = data;
        getWords(data).subscribe(new NextSubscriber<ArrayList<String>>() {
            @Override
            public void dealData(ArrayList<String> value) {
                mMnemonicRvParent.setVisibility(value.isEmpty() || (value.size() == 1 && TextUtils.equals(value.get(0), finalData)) ? View.GONE : View.VISIBLE);
                mMnemonicRvParent.requestLayout();
                mMnemonicAdapter.setStringList(value);
            }
        });
    }

    
    Observable<ArrayList<String>> getWords(String data) {
        return Observable.fromArray(English.words).filter(s -> s.startsWith(data)).take(5).toList(ArrayList::new).toObservable();
    }


    

    
    public boolean aoutHide(float x, float y) {
        if (null == mnemonic || null == mMnemonicRvParent) {
            return true;
        }
        
        return !mnemonic.isFocused() || (mMnemonicRvParent.getVisibility() != View.VISIBLE) || !pointInView(x, y);
    }

    
    public boolean pointInView(float localX, float localY) {
        int[] location = new int[2];
        mMnemonicRvParent.getLocationOnScreen(location);
        int top = location[1];
        int bottom = top + mMnemonicRvParent.getMeasuredHeight();
        return localY >= top && localY <= bottom;
    }
}
