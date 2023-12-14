

package com.wallet.ctc.ui.blockchain.backupwallet;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.base.BaseFragment;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.db.WalletEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import common.app.mall.util.ToastUtil;



public class BackupKeystoreFragment extends BaseFragment {


    Unbinder mUnbinder;
    @BindView(R2.id.keystore)
    TextView keystore;

    private com.wallet.ctc.db.WalletEntity WalletEntity;
    private List<WalletEntity> mWallName;
    private WalletDBUtil walletDBUtil;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_backup_keystory, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        walletDBUtil=WalletDBUtil.getInstent(getActivity());
        WalletEntity=walletDBUtil.getWalletInfo();
        mWallName=walletDBUtil.getWallName();
        keystore.setText(BackUpKeystoreActivity.walletKeystore);
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @OnClick(R2.id.copy_keystore)
    public void onViewClicked() {
        ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        
        cm.setText(keystore.getText().toString().trim());
        ToastUtil.showToast(getString(R.string.copy_success));
        WalletEntity.setmBackup(1);
        for(int i=0;i<mWallName.size();i++){
            if(mWallName.get(i).getAllAddress().equals(WalletEntity.getAllAddress())){
                mWallName.get(i).setmBackup(1);
                break;
            }
        }
        walletDBUtil.updateWalletInfoByAddress(WalletEntity);
    }
}
