

package com.app.lg4e.ui.fragment.login;

import static common.app.im.event.Notice.LG_LOGIN_SCUESS;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.app.R;
import com.app.lg4e.ui.LanguagePopup;
import com.lxj.xpopup.XPopup;
import com.wallet.ctc.AppHolder;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.EvmosChatInfoBean;
import com.wallet.ctc.model.blockchain.RpcApi;
import com.wallet.ctc.ui.blockchain.blockchainlogin.BlockchainLoginActivity;
import com.wallet.ctc.util.AllUtils;
import com.wallet.ctc.util.DecriptUtil;
import com.wallet.ctc.view.dialog.choosewallet.ChooseWalletDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import common.app.base.base.BaseFragment;
import common.app.base.fragment.mall.catcherror.BaseSubscriber;
import common.app.base.them.Eyes;
import common.app.im.event.Notice;
import common.app.mall.util.ToastUtil;
import common.app.my.view.MyAlertDialog;
import common.app.ui.view.InputPwdDialog;
import common.app.ui.view.MyProgressDialog;
import common.app.utils.DisplayUtils;
import common.app.utils.GlideUtil;
import common.app.utils.LogUtil;
import common.app.utils.PhoneUtil;
import common.app.utils.SpUtil;
import im.vector.app.easyfloat.floatingview.FloatingView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class WalletLoginFragment extends BaseFragment {

    private static final String TAG = "LoginFragment";
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.user_nickname)
    EditText userNickname;
    @BindView(R.id.wallet_address)
    TextView walletAddress;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.go_login)
    TextView goLogin;
    @BindView(R.id.user_logo)
    ImageView userLogoIv;
    @BindView(R.id.language)
    TextView language;

    private Unbinder mUnbinder;
    private String address;
    private WalletEntity WalletEntity;
    private InputPwdDialog mDialog;
    private String publicKey;
    private MyProgressDialog myProgressDialog;
    private RpcApi mRpcApi;
    private CompositeDisposable mDisposable;

    private final int LOGIN_WALLET_TYPE = WalletUtil.MCC_COIN;
    private final boolean LOGIN_WALLET_HD = true;
    private final int REQUEST_CODE_CREATE_WALLET = 666;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallet_login, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.LOLLIPOP_MR1) {
            Eyes.setTranslucent(mActivity);
            view.findViewById(R.id.topRelativeLayout).setPadding(0, Eyes.getStatusBarHeight(mActivity), 0, 0);
        }
        return view;
    }

    public static Intent getIntent(Context context) {
        return getEmptyIntent(context, WalletLoginFragment.class.getName());
    }

    
    public WalletEntity getLoginWallet() {
        WalletEntity walletEntity = WalletDBUtil.getInstent(mActivity).getWalletInfo();
        if (null == walletEntity) {
            
            return null;
        }
        if (LOGIN_WALLET_TYPE == -1) {
            
            return walletEntity;
        }

        boolean canUseThisWallet = false;
        if (walletEntity.getType() == LOGIN_WALLET_TYPE) {
            if (LOGIN_WALLET_HD) {
                if (walletEntity.isHDWallet()) {
                    canUseThisWallet = true;
                }
            } else {
                canUseThisWallet = true;
            }
        }
        if (canUseThisWallet) {
            
            return walletEntity;
        } else {
            
            List<WalletEntity> wallets = WalletDBUtil.getInstent(mActivity).getWalletList(LOGIN_WALLET_TYPE);
            if (null == wallets || wallets.size() == 0) {
                return null;
            }
            if (LOGIN_WALLET_HD) {
                
                WalletEntity hdWallet = null;
                for (int i = 0; i < wallets.size(); i++) {
                    WalletEntity wallet = wallets.get(i);
                    if (wallet.isHDWallet()) {
                        hdWallet = wallet;
                        break;
                    }
                }
                return hdWallet;
            } else {
                if (wallets.size() == 1) {
                    
                    return wallets.get(0);
                } else {
                    
                }
            }
        }
        return null;
    }

    
    public boolean hasWallet() {
        WalletEntity walletEntity = WalletDBUtil.getInstent(mActivity).getWalletInfo();
        if (null == walletEntity) {
            
            return false;
        } else {
            return true;
        }
    }

    private String getWalletAdress() {
        if (null == WalletEntity) {
            return "";
        }
        if (WalletEntity.getType() == WalletUtil.MCC_COIN) {
            return WalletEntity.getAllAddress2();
        } else {
            return WalletEntity.getAllAddress();
        }
    }

    @Override
    protected void initViews() {
        FloatingView.get().detach(mActivity);
        mRpcApi = new RpcApi();
        mDisposable = new CompositeDisposable();

        
        language.setOnClickListener(v -> {
            new XPopup.Builder(mActivity)
                    .dismissOnBackPressed(false)
                    .dismissOnTouchOutside(false)
                    .asCustom(new LanguagePopup(mActivity, unused -> {
                        
                    })).show();
        });
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });
        goLogin.setOnClickListener(v -> mActivity.finish());

        
        getActivity().findViewById(R.id.wallet_address_lin).setOnClickListener(view -> {
            
            if (!hasWallet()) {
                showEmptyAlert();
                return;
            }
            int walletLevel = -2;
            if (LOGIN_WALLET_HD) {
                
                walletLevel = 1;
            }
            
            ChooseWalletDialog.showDialog(getActivity(), LOGIN_WALLET_TYPE, walletLevel, ((address1, walletType) -> {
                
                WalletEntity = WalletDBUtil.getInstent(mActivity).getWalletInfo();
                initShow();
            }));
        });

        
        if (!hasWallet()) {
            startCreateWallet();
        }

        btnSubmit.setOnClickListener(v -> {
            if (!hasWallet()) {
                showEmptyAlert();
                return;
            }
            if (TextUtils.isEmpty(address)) {
                ToastUtil.showToast(getString(R.string.select_login_wallet));
                return;
            }
            showPwdInputDialog();
        });
    }

    private void initShow() {
        if (null == WalletEntity) {
            return;
        }
        address = getWalletAdress();
        walletAddress.setText(WalletUtil.getSignAddress(address));
        
        GlideUtil.showImg(getContext(), AppHolder.getLogoByAddress(address), userLogoIv);
    }

    
    private void showPwdInputDialog() {
        if (null == WalletEntity) {
            return;
        }
        if (null != mDialog) {
            mDialog.dismiss();
            mDialog = null;
        }
        mDialog = new InputPwdDialog(mActivity, getString(com.wallet.ctc.R.string.place_edit_password));
        mDialog.setonclick(new InputPwdDialog.Onclick() {
            @Override
            public void Yes(String pwd) {
                mDialog.dismiss();
                loginByWallet(pwd);
            }

            @Override
            public void No() {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    
    public void loginByWallet(String pwd) {
        if (null == WalletEntity) {
            showMsg(R.string.no_found_wallet_info);
            return;
        }
        if (!WalletEntity.getmPassword().equals(DecriptUtil.MD5(pwd))) {
            ToastUtil.showToast(com.wallet.ctc.R.string.password_error2);
            return;
        }
        String name = userNickname.getText().toString().trim();

        String key = AllUtils.createRandom(false, 8);
        String params = "key=" + key + "&ver=" + PhoneUtil.getVersionName(mActivity);
        String params2 = System.currentTimeMillis() / 1000 + "";
        String hexValue = "";
        String hexValue2 = "";

        String privateKey = WalletUtil.getDecryptionKey(WalletEntity.getmPrivateKey(), pwd);
        int walletType = WalletEntity.getType();
        String chatPublickKey = "";
        String chatSign = "";
        if (LOGIN_WALLET_TYPE == WalletUtil.MCC_COIN) {
            
            publicKey = WalletUtil.getCosmosCompressPublickey(privateKey);
            String address = getWalletAdress();
            hexValue = WalletUtil.cosmosSign(privateKey, publicKey, address, params, walletType);
            hexValue2 = WalletUtil.cosmosSign(privateKey, publicKey, address, params2, walletType);


            
            String chatPrivateKey = WalletEntity.getChatPrivateKey();
            String chatAddress = WalletEntity.getChatAddress();

            
            chatPublickKey = WalletUtil.getCosmosCompressPublickey(chatPrivateKey);
            
            chatSign = WalletUtil.cosmosSign(chatPrivateKey, chatPublickKey, chatAddress, params2, walletType);

        } else {
            publicKey = new String(WalletEntity.getmPublicKey());
            
            hexValue = WalletUtil.customSign(privateKey, params, walletType);
            hexValue2 = WalletUtil.customSign(privateKey, params2, walletType);
        }
        if (TextUtils.isEmpty(hexValue2)) {
            ToastUtil.showToast(getString(R.string.login_fail_other));
            return;
        }

        String finalHexValue = hexValue;
        String finalHexValue1 = hexValue2;
        login(name, finalHexValue, key, finalHexValue1, params2, pwd, privateKey, chatPublickKey, chatSign);
    }


    interface CheckRegistCallBack {
        void onResult(boolean hasRegisted);
    }

    public void hasRegistAccount(String address, CheckRegistCallBack callBack) {
        showProgress();
        mRpcApi.getEvmosChatInfo(address).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<EvmosChatInfoBean>(mActivity) {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable.add(d);
                    }

                    @Override
                    public void onNexts(EvmosChatInfoBean data) {
                        dismissProgress();
                        if (data.isSuccess()) {
                            
                            if (null != data.data && !TextUtils.isEmpty(data.data.from_address)) {
                                if (null != callBack) {
                                    callBack.onResult(true);
                                }
                            } else {
                                if (null != callBack) {
                                    callBack.onResult(false);
                                }
                            }

                        } else {
                            ToastUtil.showToast(data.getInfo());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        e.printStackTrace();
                        dismissProgress();
                    }
                });
    }


    public void login(String nickname, String hex, String key, String hex2, String paramStr, String pwd, String privateKey, String chat_pub_key, String chatSign) {
        Intent intent = new Intent();
        String addr = address.startsWith("0x") ? address.substring(2) : address;
        intent.putExtra("address", addr.toLowerCase());
        intent.putExtra("password", pwd);
        intent.putExtra("sign", hex2);
        intent.putExtra("pubkey", publicKey);
        intent.putExtra("params", paramStr);
        intent.putExtra("chat_pub_key", chat_pub_key);
        intent.putExtra("chatSign", chatSign);

        intent.putExtra("privateKey", WalletUtil.provideEncryptionPrivateKey(privateKey));
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();

    }

    @Override
    protected void initEvents() {

        initSoftKeyBoardHeight();
    }

    
    private void showEmptyAlert() {
        MyAlertDialog dialog = new MyAlertDialog(getContext(), getString(R.string.uncreate_wallet_tip));
        dialog.setonclick(new MyAlertDialog.Onclick() {
            @Override
            public void Yes() {
                dialog.dismiss();
                startCreateWallet();
            }

            @Override
            public void No() {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    
    private void startCreateWallet() {
        startActivityForResult(new Intent(getActivity(), BlockchainLoginActivity.class), REQUEST_CODE_CREATE_WALLET);
    }

    private ViewTreeObserver.OnGlobalLayoutListener mLayoutChangeListener;

    private void initSoftKeyBoardHeight() {
        mLayoutChangeListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                
                Rect r = new Rect();
                WalletLoginFragment.this.getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                
                int screenHeight = DisplayUtils.getScreenHeight(getActivity());
                int heightDifference = screenHeight - (r.bottom - r.top);
                boolean isKeyboardShowing = heightDifference > screenHeight / 3;
                if (isKeyboardShowing) {
                    getSupportSoftInputHeight();
                }
            }
        };
        
        WalletLoginFragment.this.getActivity().getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(mLayoutChangeListener);
    }

    
    private int getSupportSoftInputHeight() {

        Rect r = new Rect();
        
        WalletLoginFragment.this.getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        
        int screenHeight = WalletLoginFragment.this.getActivity().getWindow().getDecorView().getRootView().getHeight();
        
        int softInputHeight = screenHeight - r.bottom;

        
        if (Build.VERSION.SDK_INT >= 20) {
            
            softInputHeight = softInputHeight - getSoftButtonsBarHeight();
        }

        if (softInputHeight < 0) {
            LogUtil.i(TAG, "EmotionKeyboard--Warning: value of softInputHeight is below zero!");
        }
        
        if (softInputHeight > 0) {
            SpUtil.savekeyBoardHeight(softInputHeight);
        }
        return softInputHeight;
    }

    
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private int getSoftButtonsBarHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        
        getActivity().getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }


    public void showProgress() {
        if (myProgressDialog == null) {
            myProgressDialog = new MyProgressDialog(getContext(), "");
        }
        if (!myProgressDialog.isShowing()) {
            myProgressDialog.show();
        }
    }

    public void dismissProgress() {
        if (null == getContext() || null == myProgressDialog) {
            return;
        }
        myProgressDialog.dismiss();
    }


    @Override
    public void succeed(Object obj) {
        if (obj instanceof Notice) {
            Notice notice = (Notice) obj;
            
            if (notice.mType == Notice.LG_PHONE_SCUESS) {
                getActivity().finish();
            }
            if (notice.mType == LG_LOGIN_SCUESS) {
                getActivity().finish();
            }
        }
    }


    @Override
    public void showLoading() {

    }

    @Override
    public void hindeLoading() {
        dismissProgress();
    }

    @Override
    public void setTitle(String title) {

    }

    @Override
    public void onResume() {
        super.onResume();
        new Handler().postDelayed(() -> FloatingView.get().detach(mActivity), 500);
        if (null == WalletEntity) {
            WalletEntity = getLoginWallet();
            initShow();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult(" + requestCode + ", " + resultCode + ", " + data);
        if (requestCode == REQUEST_CODE_CREATE_WALLET && resultCode == Activity.RESULT_OK) {
            
            WalletEntity = getLoginWallet();
            initShow();
            if (null != WalletEntity && null != data) {
                
                String pwd = data.getStringExtra("walletPwd");
                Log.i(TAG, "pwd=" + pwd);
                if (!TextUtils.isEmpty(pwd)) {
                    loginByWallet(pwd);
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        dismissProgress();
        super.onDestroy();
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            this.getActivity().getWindow().getDecorView().getViewTreeObserver().removeOnGlobalLayoutListener(mLayoutChangeListener);
        } else {
            this.getActivity().getWindow().getDecorView().getViewTreeObserver().removeGlobalOnLayoutListener(mLayoutChangeListener);
        }
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }

    }

    @Override
    public void onBackPressed() {
        
    }
}
