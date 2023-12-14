

package com.app.me;

import static com.wallet.ctc.crypto.WalletUtil.MCC_COIN;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;

import com.app.R;
import com.app.did_guid.BuyDidGuidActivity;
import com.app.levelranking.SMLevelRankingActivity;
import com.app.me.destory_group.DestoryGroupActivity;
import com.app.node.NodeListsActivity;
import com.app.pojo.IAction;
import com.app.view.privatedvm.PrivateDVMActivity;
import com.app.view.privatedvm.edit.DVMEditActivity;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.databinding.SmFragmentNewMeBinding;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.ui.blockchain.addressbook.AddressBookActivity;
import com.wallet.ctc.ui.blockchain.blockchainlogin.BlockchainLoginActivity;
import com.wallet.ctc.ui.blockchain.issuance.IssuanceCoinActivity;
import com.wallet.ctc.ui.blockchain.privacy.ChatPrivacySettingActivity;
import com.wallet.ctc.ui.blockchain.setnode.SettingNodeActivity;
import com.wallet.ctc.ui.dapp.DappWebViewActivity;
import com.wallet.ctc.ui.dapp.list.DappActivity;
import com.wallet.ctc.ui.me.chain_bridge2.ChainBridgeActivity2;
import com.wallet.ctc.ui.me.virtualphone.SMVirtualPhoneActivity;
import com.wallet.ctc.util.AllUtils;
import com.wallet.ctc.util.NetUtils;
import com.wallet.ctc.util.SettingPrefUtil;
import com.wallet.ctc.view.dialog.choosewallet.ChooseWalletDialog;

import java.util.Locale;

import javax.inject.Inject;

import common.app.ActivityRouter;
import common.app.base.BaseFragment;
import common.app.base.them.Eyes;
import common.app.mall.util.ToastUtil;
import common.app.my.view.MyAlertDialog;
import common.app.utils.AppVerUtil;
import common.app.utils.LanguageUtil;
import common.app.utils.RxSchedulers;
import dagger.hilt.android.AndroidEntryPoint;
import im.vector.app.features.configuration.VectorConfiguration;
import im.vector.app.features.me.VectorMeActivity;
import im.vector.app.features.settings.VectorLocale;
import im.vector.app.features.workers.signout.SignOutUiWorker;
import im.vector.app.provide.ChatStatusProvide;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;




@AndroidEntryPoint
public class SMNewMeFragment extends BaseFragment<SMNewMeFragmentVM> {
    private static final String TAG = "SMNewMeFragment";
    private SmFragmentNewMeBinding meBinding;


    @Inject
    VectorConfiguration vectorConfiguration;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        meBinding = SmFragmentNewMeBinding.inflate(inflater, container, false);
        mView = meBinding.getRoot();
        Eyes.addStatusBar(requireActivity(), (ViewGroup) mView, Color.WHITE);
        return mView;
    }

    
    @Override
    public boolean isLogined() {
        return ChatStatusProvide.loginStatus(getContext());
    }

    
    private void goLoginPage() {
        startActivity(new Intent(getActivity(), im.vector.app.features.MainActivity.class));
    }

    
    private void startPage(Intent intent, boolean needLogin) {
        if (needLogin && !isLogined()) {
            goLoginPage();
        } else {
            startActivity(intent);
        }
    }

    @Override
    public SMNewMeFragmentVM getViewModel() {
        initViewModelFix(SMNewMeFragmentVM.class);
        return viewModel;
    }

    @Override
    public void initView(@Nullable View view) {
        super.initView(view);
        
        meBinding.civLogo.setOnClickListener(view1 -> {
            
            startPage(new Intent(getActivity(), VectorMeActivity.class), true);
        });
        
        meBinding.tvUsername.setOnClickListener(view1 -> {
            
            startPage(new Intent(getActivity(), VectorMeActivity.class), true);
        });
        meBinding.tvDid.setOnClickListener(view1 -> {
            
            startPage(new Intent(getActivity(), VectorMeActivity.class), true);
        });

        
        meBinding.tvSeeMore.setOnClickListener(view1 -> {
            
            String address = AllUtils.getAddressByUid(ChatStatusProvide.getUserId(getContext()));
            startPage(SMVirtualPhoneActivity.getIntent(getActivity(), address), true);
        });

        
        meBinding.tvBuyDid.setOnClickListener(view1 -> {
            startActivity(new Intent(getActivity(), BuyDidGuidActivity.class));
        });

        
        setOnItemClick();

    }

    @Override
    public void initData() {
        
        getViewModel().observe(getViewModel().nowPhoneLD, mobile -> {
            if (!TextUtils.isEmpty(mobile)) {
                
                meBinding.tvDid.setText("DID: " + mobile);
            } else {
                
                String address = ChatStatusProvide.getAddress(mContext);
                meBinding.tvDid.setText(address);
                
            }

        });
    }

    @Override
    protected void onFragmentVisible() {
        super.onFragmentVisible();
    }

    @Override
    protected void onFragmentHide() {
        super.onFragmentHide();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isAdded() || meBinding == null) {
            return;
        }
        
        ChatStatusProvide.setUserInfo(getContext(), meBinding.civLogo, meBinding.tvUsername);
        
        String userId = ChatStatusProvide.getUserId(getContext());
        getViewModel().getChatInfo(userId);

        if (isLogined()) {
            meBinding.imSettingLayout.setVisibility(View.VISIBLE);
            meBinding.btnLoginParent.setVisibility(View.VISIBLE);
            meBinding.smIconMeEdit.setVisibility(View.VISIBLE);
            meBinding.tvSeeMore.setVisibility(View.VISIBLE);
            meBinding.linPrivacyPolicy.setVisibility(View.GONE);


        } else {
            meBinding.imSettingLayout.setVisibility(View.GONE);
            meBinding.btnLoginParent.setVisibility(View.GONE);
            meBinding.smIconMeEdit.setVisibility(View.GONE);
            meBinding.tvSeeMore.setVisibility(View.GONE);
            meBinding.linPrivacyPolicy.setVisibility(View.GONE);
            meBinding.tvUsername.setText(R.string.me_login);
            meBinding.tvDid.setText(R.string.me_login_did);
            meBinding.civLogo.setImageResource(R.drawable.civ_logo);
            meBinding.tvDid.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
        }


    }

    private NotificationManagerCompat notificationManager;

    
    private void setOnItemClick() {
        notificationManager = NotificationManagerCompat.from(mContext);
        
        meBinding.linAssetsManagement.setOnClickListener(v -> {
            startActivity(DestoryGroupActivity.getIntent(getActivity(), "", "2902922029"));
        });
        
        meBinding.smMeComputing.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), DVMEditActivity.class));
        });
        
        meBinding.linDapp.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), DappActivity.class));
        });
        
        meBinding.smMeIssuanceCoin.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), IssuanceCoinActivity.class));
        });
        
        
        meBinding.linChooseNode.setOnLongClickListener(view -> {
            Intent intent = new Intent(getActivity(), NodeListsActivity.class);
            startActivity(intent);
            return true;
        });
        meBinding.linChooseNode.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SettingNodeActivity.class);
            startActivity(intent);
        });
        
        meBinding.linAddressBooks.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddressBookActivity.class);
            startActivity(intent);
        });
        
        meBinding.linQiao.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ChainBridgeActivity2.class));
        });

        
        meBinding.exchangeCenter.setOnClickListener(view -> {
            String url = "file:
            DappWebViewActivity.startDappWebViewActivity(getActivity(), url, getString(R.string.exchange_center), MCC_COIN);
        });

        
        meBinding.linPledgeSort.setOnClickListener(view -> {
            startPage(new Intent(mContext, SMLevelRankingActivity.class), true);
        });

        
        meBinding.tvCurrentLanguage.setText(LanguageUtil.getNowLanguageStr(mContext));
        
        meBinding.linLanguage.setOnClickListener(v -> {
            LanguageUtil.showSettingDialog(getActivity(), () -> {
                Locale nowLocal = LanguageUtil.getNowLocal(mContext);
                VectorLocale.INSTANCE.saveApplicationLocale(nowLocal);
                vectorConfiguration.applyToApplicationContext();
                if (null != getActivity()) {
                    getActivity().recreate();
                }
            });
        });

        
        meBinding.linCheckUpdate.setOnClickListener(v -> {
            
            if (!NetUtils.isNetworkConnected(mContext)) {
                ToastUtil.showToast(getString(R.string.connect_failuer_toast));
                return;
            }
            new AppVerUtil(getActivity()).checkVer(null);
        });


        
        meBinding.linVirtualPhone.setOnClickListener(v -> {
            String address = AllUtils.getAddressByUid(ChatStatusProvide.getUserId(getContext()));
            startPage(SMVirtualPhoneActivity.getIntent(getActivity(), address), true);
        });
        
        meBinding.linPrivacyPolicy.setOnClickListener(v -> {

            String address = AllUtils.getAddressByUid(ChatStatusProvide.getUserId(mContext));
            startPage(ChatPrivacySettingActivity.getIntent(mContext, address), true);
        });
        
        meBinding.linChatSetting.setOnClickListener(v -> {
        });
        
        meBinding.tvChainSync.setOnClickListener(v -> {
            startPage(ActivityRouter.getIntent(getActivity(), ActivityRouter.App.A_ChainSyncActivity), true);
        });
        
        meBinding.btnLoginOut.setOnClickListener(v -> {
            showExitAlert(() -> {
                notificationManager.cancelAll();
                new SignOutUiWorker(requireActivity()).perform();
            });
        });

        meBinding.btnLoginChange.setOnClickListener(v -> {

            
            if (!hasWallet()) {
                showEmptyAlert();
                return;
            }
            
            String address = ChatStatusProvide.getAddress(mContext);
            
            final int LOGIN_WALLET_TYPE = WalletUtil.MCC_COIN;

            ChooseWalletDialog.showDialog(getActivity(), LOGIN_WALLET_TYPE, ((address1, walletType) -> {
                
                WalletEntity entity = WalletDBUtil.getInstent(mContext).getWalletInfo();

                String allAddress = entity.getAllAddress();
                if (TextUtils.equals(address, allAddress)) {
                    ToastUtil.showToast(getString(R.string.the_account_no_need_change));
                    return;
                }
                addSubscription(Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
                    doCheck(allAddress);
                    emitter.onNext(true);
                }).compose(RxSchedulers.io_main()).subscribe());
                doCheck(allAddress);
            }));
        });
    }

    private void showExitAlert(IAction action) {
        MyAlertDialog dialog = new MyAlertDialog(getContext(), getString(R.string.exit_action_title));
        dialog.setonclick(new MyAlertDialog.Onclick() {
            @Override
            public void Yes() {
                dialog.dismiss();
                if (null != action) {
                    action.run();
                }
            }

            @Override
            public void No() {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    
    private void showEmptyAlert() {
        MyAlertDialog dialog = new MyAlertDialog(getContext(), getString(R.string.uncreate_wallet_tip));
        dialog.setonclick(new MyAlertDialog.Onclick() {
            @Override
            public void Yes() {
                dialog.dismiss();
                Intent intent;
                intent = new Intent(getActivity(), BlockchainLoginActivity.class);
                startActivity(intent);
            }

            @Override
            public void No() {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    
    public boolean hasWallet() {
        WalletEntity walletEntity = WalletDBUtil.getInstent(mContext).getWalletInfo();
        if (null == walletEntity) {
            
            return false;
        } else {
            return true;
        }
    }

    private void doCheck(String allAddress) {
        String address = SettingPrefUtil.getWalletLoginAddressWithType(mContext);
        if (!TextUtils.equals(address, allAddress)) {
            
            if (ChatStatusProvide.isLogin(mContext, allAddress)) {
                
                SettingPrefUtil.setWalletLoginTypeAddress(mContext, allAddress);
                
                Intent intent = new Intent(mContext, getLauncherActivity(mContext));
                restartApplicationWithIntent((Activity) mContext, intent);
            } else {

                
                
                
                ChatStatusProvide.toLogin(mContext);
            }
        }
    }

    private static Class<? extends Activity> getLauncherActivity(@NonNull Context context) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        if (intent != null && intent.getComponent() != null) {
            try {
                return (Class<? extends Activity>) Class.forName(intent.getComponent().getClassName());
            } catch (ClassNotFoundException e) {
                
                Log.e(TAG, "Failed when resolving the restart activity class via getLaunchIntentForPackage, stack trace follows!", e);
            }
        }

        return null;
    }

    public static void restartApplicationWithIntent(@NonNull Activity activity, @NonNull Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        if (intent.getComponent() != null) {
            
            
            
            
            
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
        }

        activity.finish();
        activity.startActivity(intent);
        killCurrentProcess();
    }


    private static void killCurrentProcess() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }


}
