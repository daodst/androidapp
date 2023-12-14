

package com.wallet.ctc.ui.blockchain.creatwallet;

import static com.wallet.ctc.crypto.WalletDBUtil.USER_ID;

import android.content.Intent;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.base.BaseActivity;
import com.wallet.ctc.base.BaseWebViewActivity;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.DBManager;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.RootWalletInfo;
import com.wallet.ctc.ui.blockchain.backupwallet.BackUpActivity;
import com.wallet.ctc.ui.blockchain.importwallet.ImportWalletActivity;
import com.wallet.ctc.ui.blockchain.managewallet.ChooseCreatImportTypeActivity;
import com.wallet.ctc.util.DecriptUtil;
import com.wallet.ctc.util.SettingPrefUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.app.AppApplication;
import common.app.mall.util.ToastUtil;
import common.app.ui.view.InputPwdDialog;
import common.app.utils.SpannableUtils;
import common.app.utils.ThreadManager;



public class CreatWalletActivity extends BaseActivity {

    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.wallet_name)
    EditText walletName;
    @BindView(R2.id.password)
    EditText password;
    @BindView(R2.id.confirm_password)
    EditText confirmPassword;
    @BindView(R2.id.password_prompt)
    EditText passwordPrompt;
    @BindView(R2.id.check)
    CheckBox check;
    @BindView(R2.id.create_wallet)
    Button createWallet;
    @BindView(R2.id.pwd_qiangdu)
    TextView pwdQiangdu;
    @BindView(R2.id.pwd_errortishi)
    TextView pwdErrortishi;
    @BindView(R2.id.create_tip_tv)
    TextView createTipTv;
    @BindView(R2.id.pwdLayout)
    LinearLayout pwdLayout;


    private Intent intent;
    private Gson gson = new GsonBuilder()
            .disableHtmlEscaping() 
            .create();
    private boolean loading = false;
    private int type;
    private RootWalletInfo rootWalletInfo;
    private final int TYPE_CREATE_ROOT = 1;
    private final int TYPE_CREATE_NORMAL = 2;
    private final int TYPE_CREATE_DERIVATION = 3;
    private int mCreateType = TYPE_CREATE_NORMAL;

    @Override
    public int initContentView() {
        return R.layout.activity_creatwallet;
    }

    @Override
    public void initUiAndListener() {
        ButterKnife.bind(this);
        type = getIntent().getIntExtra("type", -1);
        if (-1 == type) {
            type = SettingPrefUtil.getWalletType(this);
        }
        tvTitle.setText(getString(R.string.create_wallet));
        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    createWallet.setEnabled(true);
                } else {
                    createWallet.setEnabled(false);
                }
            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pwd = password.getText().toString().trim();
                if (TextUtils.isEmpty(pwd)) {
                    pwdQiangdu.setText("");
                    pwdQiangdu.setTextColor(0xffff0000);
                    pwdErrortishi.setVisibility(View.GONE);
                    return;
                }
                if (pwd.length() < 8) {
                    pwdQiangdu.setText("");
                    pwdQiangdu.setTextColor(0xffff0000);
                    pwdErrortishi.setVisibility(View.VISIBLE);
                    return;
                }
                pwdErrortishi.setVisibility(View.GONE);
                int qiangdu = passwordStrong(pwd);
                if (qiangdu == 0 || qiangdu == 1) {
                    pwdQiangdu.setText(R.string.pwd_weak);
                    pwdQiangdu.setTextColor(0xffff0000);
                } else if (qiangdu == 2) {
                    pwdQiangdu.setTextColor(0xffffa700);
                    pwdQiangdu.setText(R.string.pwd_middle);
                } else if (qiangdu == 3) {
                    pwdQiangdu.setTextColor(0xff56a925);
                    pwdQiangdu.setText(R.string.pwd_strong);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void initData() {

        if (TextUtils.isEmpty(BuildConfig.ENABLE_CREAT_ALL_WALLET)) {
            
            mCreateType = TYPE_CREATE_NORMAL;
        } else {
            
            rootWalletInfo = walletDBUtil.getRootWalletInfo(type);
            if (rootWalletInfo == null || rootWalletInfo.rootWallet == null) {
                
                mCreateType = TYPE_CREATE_ROOT;
            } else {
                
                mCreateType = TYPE_CREATE_DERIVATION;
            }
        }
        if (mCreateType == TYPE_CREATE_ROOT || mCreateType == TYPE_CREATE_NORMAL) {
            
            
            String alertText = getString(R.string.create_wallet_remind_tip);
            Spannable spl = new SpannableString(getString(R.string.create_wallet_remind)+alertText);
            spl = SpannableUtils.colorizeMatchingText(spl, alertText, ContextCompat.getColor(this, R.color.default_error_color));
            createTipTv.setText(spl);
            pwdLayout.setVisibility(View.VISIBLE);
        } else {
            
            int index = rootWalletInfo.getNextIndex();
            String alertText = getString(R.string.create_wallet_remind_tip);
            Spannable spl = new SpannableString(String.format(getString(R.string.create_root_wallet_tips), index+"")+alertText);
            spl = SpannableUtils.colorizeMatchingText(spl, alertText, ContextCompat.getColor(this, R.color.default_error_color));
            createTipTv.setText(spl);
            pwdLayout.setVisibility(View.GONE);
            String suggestName = rootWalletInfo.getRootWalletName() + index;
            walletName.setText(suggestName);
        }
    }

    String pwdkey = "";

    @OnClick({R2.id.tv_back, R2.id.look_xieyi, R2.id.create_wallet, R2.id.import_wallet})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.tv_back) {
            finish();

        } else if (i == R.id.look_xieyi) {
            getUrl("service", getString(R.string.protocol));

        } else if (i == R.id.create_wallet) {
            String name = walletName.getText().toString().trim();
            String pwd = password.getText().toString().trim();
            String pwd2 = confirmPassword.getText().toString().trim();
            pwdkey = passwordPrompt.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                ToastUtil.showToast(getString(R.string.place_edit_wallet_name));
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
                if (!pwd.equals(pwd2)) {
                    ToastUtil.showToast(getString(R.string.password_error));
                    return;
                }
            }
            if (null == pwdkey) {
                pwdkey = "";
            }
            
            if (walletDBUtil.hasWallet(name, type)) {
                ToastUtil.showToast(getString(R.string.wallet_name_ishas));
                return;
            }
            if (mCreateType == TYPE_CREATE_DERIVATION) {
                
                showPwdInputDialog(name, rootWalletInfo.rootWallet);
            } else {
                
                startThreadCreateWallet(pwd, name);
            }
        } else if (i == R.id.import_wallet) {
            
            intent = new Intent(getIntent());
            intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
            intent.setClass(CreatWalletActivity.this, ImportWalletActivity.class);
            intent.putExtra("type", type);
            startActivity(intent);
            finish();

        } else {
        }
    }


    private void showPwdInputDialog(String name, WalletEntity rootWallet) {
        InputPwdDialog pwdDialog = new InputPwdDialog(this, getString(R.string.place_edit_password));
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
                
                startThreadCreateWallet(pwd, name);
            }

            @Override
            public void No() {
                pwdDialog.dismiss();
            }
        });
        pwdDialog.show();
    }


    
    private void startThreadCreateWallet(String pwd, String name) {
        if (loading) {
            return;
        }
        mLoadingDialog.show();
        loading = true;
        
        ThreadManager.getNormalPool().execute(new Runnable() {
            @Override
            public void run() {
                if (mCreateType == TYPE_CREATE_ROOT) {
                    
                    createWallet(BuildConfig.ENABLE_CREAT_ALL_WALLET_TYPE, pwd, name);
                } else {
                    
                    createWallet(type, pwd, name);
                }
                if(loading) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLoadingDialog.dismiss();
                            if (mCreateType == TYPE_CREATE_DERIVATION) {
                                
                                ToastUtil.showToast(getString(R.string.create_success));
                            } else {
                                intent = new Intent(getIntent());
                                intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                                intent.setClass(CreatWalletActivity.this, BackUpActivity.class);
                                intent.putExtra("walletPwd", pwd);
                                startActivity(intent);
                            }
                            if (null != AppApplication.findActivity(ChooseCreatImportTypeActivity.class)) {
                                AppApplication.findActivity(ChooseCreatImportTypeActivity.class).finish();
                            }
                            finish();
                        }
                    });
                    loading = false;
                }
            }
        });
    }





    private void createWallet(int type, String pwd, String name) {
        WalletEntity mWallet = null;
        if (mCreateType == TYPE_CREATE_ROOT) {
            
            mWallet = WalletUtil.CreatWallet(pwd,type);
        } else if(mCreateType == TYPE_CREATE_NORMAL){
            
            mWallet = WalletUtil.CreatWallet(pwd,type);
        } else if(mCreateType == TYPE_CREATE_DERIVATION) {
            
            if (rootWalletInfo != null && rootWalletInfo.rootWallet != null) {
                
                try {
                    String mnemoicJsonStr= DecriptUtil.Decrypt(rootWalletInfo.rootWallet.getmMnemonic(),pwd);
                    List<String> mnemoicList= gson.fromJson(mnemoicJsonStr, new TypeToken<List<String>>() {}.getType());
                    int accountIndex = rootWalletInfo.getNextIndex();
                    mWallet = WalletUtil.ImportWalletByMnemonic(mnemoicList, pwd, type, accountIndex);
                } catch (Throwable e){
                    e.printStackTrace();
                }
            }
        }
        if (null == mWallet) {
            loading=false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.showToast(getString(R.string.create_wallet_fail));
                }
            });
            return;
        }
        mWallet.setmPasswordHint(pwdkey);
        mWallet.setName(name);
        mWallet.setType(type);
        mWallet.setmPassword(DecriptUtil.MD5(pwd));
        Random rand = new Random();
        int i = rand.nextInt(5);
        mWallet.setLogo(i);
        mWallet.setUserName(USER_ID);
        
        if (mCreateType == TYPE_CREATE_ROOT) {
            mWallet.setLevel(1);
        }
        DBManager.getInstance(CreatWalletActivity.this).insertWallet(mWallet);
        creatOrInsertWallet(type, mWallet.getAllAddress());
        SettingPrefUtil.setWalletTypeAddress(CreatWalletActivity.this, mWallet.getType(), mWallet.getAllAddress());
        if (mCreateType == TYPE_CREATE_ROOT) {
            List<String> list = gson.fromJson(WalletUtil.getDecryptionKey(mWallet.getMMnemonic(), pwd), new TypeToken<List<String>>() {
            }.getType());
            String[] walletype= BuildConfig.ENABLE_CREAT_ALL_WALLET.split(",");
            if(walletype.length>0){
                for(int num=0;num<walletype.length;num++){
                    int ty=new BigDecimal(walletype[num]).intValue();
                    if(ty!=type){
                        importWallet(list, pwd, pwdkey, name,ty);
                    }
                }
            }
        }
    }

    private void importWallet(List<String> list, String pwd, String pwdkey, String names, int type) {
        WalletEntity mWallet;
        mWallet = WalletUtil.ImportWalletByMnemonic(list, pwd,type);
        mWallet.setmPasswordHint(pwdkey);
        mWallet.setName(names);
        mWallet.setmPassword(DecriptUtil.MD5(pwd));
        mWallet.setmBackup(1);
        mWallet.setMMnemonicBackup(0);
        mWallet.setType(type);
        

        Random rand = new Random();
        int i = rand.nextInt(5);
        mWallet.setLogo(i);
        mWallet.setUserName(USER_ID);
        mWallet.setType(type);
        mWallet.setLevel(1);
        creatOrInsertWallet(type, mWallet.getAllAddress());
        DBManager.getInstance(CreatWalletActivity.this).insertWallet(mWallet);

    }

    private void getUrl(String type, String title) {
        Intent intent = new Intent(CreatWalletActivity.this, BaseWebViewActivity.class);
        intent.putExtra("type", 1);
        intent.putExtra("sysName", type);
        intent.putExtra("title", title);
        startActivity(intent);
    }










    private int passwordStrong(String passwordStr) {
        if (TextUtils.equals("", passwordStr)) {
            return 0;
        }
        String regexZ = "\\d*";
        String regexS = "[a-zA-Z]+";
        String regexT = "\\W+$";
        String regexZT = "\\D*";
        String regexST = "[\\d\\W]*";
        String regexZS = "\\w*";
        String regexZST = "[\\w\\W]*";

        if (passwordStr.matches(regexZ)) {
            return 1;
        }
        if (passwordStr.matches(regexS)) {
            return 1;
        }
        if (passwordStr.matches(regexT)) {
            return 1;
        }
        if (passwordStr.matches(regexZT)) {
            return 2;
        }
        if (passwordStr.matches(regexST)) {
            return 2;
        }
        if (passwordStr.matches(regexZS)) {
            return 2;
        }
        if (passwordStr.matches(regexZST)) {
            return 3;
        }
        return 0;
    }

}
