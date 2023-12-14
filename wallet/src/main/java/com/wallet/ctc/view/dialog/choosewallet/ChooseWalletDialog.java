

package com.wallet.ctc.view.dialog.choosewallet;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.WalletLogoBean;
import com.wallet.ctc.ui.blockchain.managewallet.AddWalletTypeActivity;
import com.wallet.ctc.ui.blockchain.managewallet.ChooseCreatImportTypeActivity;
import com.wallet.ctc.util.SettingPrefUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.app.mall.util.ToastUtil;



public class ChooseWalletDialog {
    @BindView(R2.id.top_view)
    View topView;
    @BindView(R2.id.close_dialog)
    ImageView closeDialog;
    @BindView(R2.id.title)
    TextView title;
    @BindView(R2.id.wallet_type_logo)
    ListView walletTypeLogo;
    @BindView(R2.id.wallet_list)
    ListView walletList;
    @BindView(R2.id.walletTypeIv)
    ImageView walletTypeIv;


    private Context mContext;
    private Intent intent;
    private Dialog mDialog;
    private List<WalletEntity> mWallName = new ArrayList<>();
    private List<WalletLogoBean> walletLogoBeans = new ArrayList<>();
    private int type = 0;
    private ChooseWalletLogoDialogAdapter logoDialogAdapter;
    private ChooseWalletDialogAdapter walletDialogAdapter;
    private View mContentView;
    private OnSelectTypeListener mSelectTypeListener;

    public ChooseWalletDialog(Context context, boolean noDialog) {
        this.mContext = context;
        LayoutInflater layoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.dialog_choose_wallet, null);
        mContentView = layout;
        ButterKnife.bind(this, layout);
        if (!noDialog) {
            topView.setVisibility(View.VISIBLE);
            final Dialog dialog = new Dialog(mContext);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.setContentView(layout, layoutParams);

            Window win = dialog.getWindow();
            win.getDecorView().setPadding(0, 0, 0, 0);
            win.getDecorView().setBackgroundColor(Color.TRANSPARENT);
            WindowManager.LayoutParams lp = win.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.BOTTOM;
            lp.windowAnimations = R.style.dialogAnim;
            win.setAttributes(lp);
            mDialog = dialog;
        } else {
            topView.setVisibility(View.GONE);
        }
        logoDialogAdapter = new ChooseWalletLogoDialogAdapter(mContext);
        walletDialogAdapter = new ChooseWalletDialogAdapter(mContext);
        walletDialogAdapter.bindData(mWallName);
        logoDialogAdapter.bindData(walletLogoBeans);
        walletTypeLogo.setAdapter(logoDialogAdapter);
        walletList.setAdapter(walletDialogAdapter);
        walletTypeLogo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selecteNewWallet(position, false);
            }
        });
        walletDialogAdapter.setChooseWallet(new ChooseWalletDialogAdapter.ChooseWallet() {
            @Override
            public void onChangeWallet(String address, int type) {
                if (null != mChooseWallet) {
                    mChooseWallet.onChangeWallet(address, type);
                }
            }

            @Override
            public void addWallet(int type) {
                if (null != mChooseWallet) {
                    mChooseWallet.addWallet(type);
                }
            }
        });
    }

    
    public ChooseWalletDialog(Context context) {
        this(context, false);
    }

    public void setOnSelectTypeListener(OnSelectTypeListener listener){
        this.mSelectTypeListener = listener;
    }

    
    private void selecteNewWallet(int position, boolean onlyThis) {
        WalletLogoBean selecteLogo = null;
        for (int i = 0; i < walletLogoBeans.size(); i++) {
            walletLogoBeans.get(i).setChoose(0);
        }
        walletLogoBeans.get(position).setChoose(1);
        selecteLogo = walletLogoBeans.get(position);
        int walletType = selecteLogo.getWalletType();

        
        if (onlyThis && null != selecteLogo) {
            walletLogoBeans.clear();
            walletLogoBeans.add(selecteLogo);
        }

        logoDialogAdapter.notifyDataSetChanged();
        if (walletType == -1) {
            
            walletDialogAdapter.bindType(1);
            walletDialogAdapter.bindData(mWallName);
            walletTypeIv.setVisibility(View.INVISIBLE);
        } else {
            
            walletDialogAdapter.bindType(0);
            List<WalletEntity> list = WalletDBUtil.getInstent(mContext).getWalletList(walletType);
            walletDialogAdapter.bindData(list);
            if (walletType == WalletUtil.MCC_COIN) {
                walletTypeIv.setImageResource(R.mipmap.ic_dst_type);
                walletTypeIv.setVisibility(View.VISIBLE);
            } else if(walletType == WalletUtil.BNB_COIN){
                walletTypeIv.setImageResource(R.mipmap.ic_bsc_type);
                walletTypeIv.setVisibility(View.VISIBLE);
            } else if(walletType == WalletUtil.ETH_COIN){
                walletTypeIv.setImageResource(R.mipmap.ic_eth_type);
                walletTypeIv.setVisibility(View.VISIBLE);
            } else {
                walletTypeIv.setVisibility(View.INVISIBLE);
            }
        }
        walletDialogAdapter.notifyDataSetChanged();
        if (null != mSelectTypeListener) {
            mSelectTypeListener.onSelecte(walletType);
        }
    }

    
    public void setSelecte(int walletType) {
        int position = -1;
        if (null == walletLogoBeans || walletLogoBeans.size() == 0) {
            return;
        }
        for (int i = 0; i < walletLogoBeans.size(); i++) {
            if (walletLogoBeans.get(i).getWalletType() == walletType) {
                position = i;
                break;
            }
        }
        if (position != -1) {
            selecteNewWallet(position, false);
        }
    }

    public void refreshState() {
        if (null != walletDialogAdapter){
            walletDialogAdapter.refresh();
        }
    }


    public void show(List<WalletEntity> data, List<WalletLogoBean> walletLogoBeans) {
        this.mWallName.clear();
        this.walletLogoBeans.clear();
        this.mWallName.addAll(data);
        if (data.size() >0 && data.get(data.size() - 1).getLevel() == 1) {
            WalletEntity w = new WalletEntity();
            w.setLevel(0);
            this.mWallName.add(w);
        }
        this.walletLogoBeans.addAll(walletLogoBeans);
        walletDialogAdapter.bindData(mWallName);
        logoDialogAdapter.bindData(walletLogoBeans);
        if(null != walletLogoBeans && walletLogoBeans.size() >0 ){
            for(int i = 0; i < walletLogoBeans.size(); i ++ ){
                WalletLogoBean wlb = walletLogoBeans.get(i);
                if(wlb.getChoose() == 1){
                    selecteNewWallet(i, false);
                    break;
                }
            }
        }
        if (null != mDialog) {
            mDialog.show();
        }
    }


    public View getContentView(){
        return mContentView;
    }


    public void dismiss() {
        if (null != mDialog) {
            mDialog.dismiss();
        }
    }

    private ChooseWallet mChooseWallet;

    public interface ChooseWallet {
        void onChangeWallet(String address, int type);

        void addWallet(int type);
    }

    public void setChooseWallet(ChooseWallet chooseWallet) {
        mChooseWallet = chooseWallet;
    }

    @OnClick({R2.id.top_view, R2.id.close_dialog})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.top_view) {
            dismiss();
        }
        if (i == R.id.close_dialog) {
            dismiss();
        }
    }

    public interface OnWalletChooseListener {
        void onSelecte(String address, int walletType);
    }


    
    public static void showExtDialog(Context context, int extWalletyType, int defaultSelectType, OnWalletChooseListener listener) {
        WalletDBUtil walletDBUtil = WalletDBUtil.getInstent(context);
        List<WalletLogoBean> walletLogoBeans = walletDBUtil.getWalletLogos(false, extWalletyType);
        if (null == walletLogoBeans || walletLogoBeans.size() == 0){
            ToastUtil.showToast(R.string.no_found_wallet_info);
            return;
        }
        showDialog(context, walletLogoBeans, defaultSelectType, -2, listener);
    }

    
    public static void showDialog(Context context, int walletyType, OnWalletChooseListener listener) {
        showDialog(context, walletyType, -2, listener);
    }

    public static void showDialog(Context context, int walletyType, int walletLevel, OnWalletChooseListener listener) {
        List<WalletLogoBean> walletLogoBeans = null;
        WalletDBUtil walletDBUtil = WalletDBUtil.getInstent(context);
        if (walletyType == -1) {
            walletLogoBeans = walletDBUtil.getWalletLogos(false);
        } else {
            walletLogoBeans = walletDBUtil.getWalletLogoByType(walletyType);
        }
        if (null == walletLogoBeans || walletLogoBeans.size() == 0){
            ToastUtil.showToast(R.string.no_found_wallet_info);
            return;
        }
        showDialog(context, walletLogoBeans, walletyType, walletLevel, listener);
    }

    
    public static void showDialog(Context context, List<WalletLogoBean> walletLogoBeans, int defaultSelecteType,
                                  int walletLevel, OnWalletChooseListener listener) {
        WalletDBUtil walletDBUtil = WalletDBUtil.getInstent(context);
        List<WalletEntity> wallNames = new ArrayList<>();
        List<WalletEntity> allWallets = walletDBUtil.getWallName();
        if(walletLevel == -2) {
            wallNames.addAll(allWallets);
        } else {
            for(WalletEntity wallet : allWallets){
                if(wallet.getLevel() == walletLevel){
                    wallNames.add(wallet);
                }
            }
        }

        ChooseWalletDialog chooseWalletDialog = new ChooseWalletDialog(context);
        chooseWalletDialog.setChooseWallet(new ChooseWalletDialog.ChooseWallet() {
            @Override
            public void onChangeWallet(String address, int type) {
                int position = 0;
                for (int i = 0; i < wallNames.size(); i++) {
                    WalletEntity walletEntity = wallNames.get(i);
                    if (walletEntity.getAllAddress().equals(address) && walletEntity.getType() == type) {
                        position = i;
                        break;
                    }
                }
                String selecteAddress = wallNames.get(position).getAllAddress();
                int selecteWalletType = wallNames.get(position).getType();
                SettingPrefUtil.setWalletTypeAddress(context, selecteWalletType, selecteAddress);
                chooseWalletDialog.dismiss();
                if (null != listener) {
                    listener.onSelecte(selecteAddress, selecteWalletType);
                }
            }

            @Override
            public void addWallet(int walletLevel) {
                Intent intent = null;
                if (walletLevel == 1) {
                    
                    intent = new Intent(context, AddWalletTypeActivity.class);
                } else {
                    
                    intent = new Intent(context, ChooseCreatImportTypeActivity.class);
                    intent.putExtra("from", 0);
                }
                context.startActivity(intent);
                chooseWalletDialog.dismiss();
            }
        });
        chooseWalletDialog.show(wallNames, walletLogoBeans);
        if (defaultSelecteType != -1) {
            chooseWalletDialog.setSelecte(defaultSelecteType);
        } else {
            int defType0 = walletLogoBeans.get(0).getWalletType();
            chooseWalletDialog.setSelecte(defType0);
        }
    }


    public interface OnSelectTypeListener {
        void onSelecte(int walletType);
    }
}
