

package com.wallet.ctc.ui.blockchain.backupwallet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
import common.app.base.share.qr.QrCodeUtils;



public class BackupKeystoryQrcodeFragment extends BaseFragment {


    Unbinder mUnbinder;
    @BindView(R2.id.zhezhao)
    LinearLayout zhezhao;
    @BindView(R2.id.qrcode)
    ImageView qrcode;
    private com.wallet.ctc.db.WalletEntity WalletEntity;
    private List<WalletEntity> mWallName;
    private WalletDBUtil walletDBUtil;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_backup_keystory_qrcode, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        walletDBUtil=WalletDBUtil.getInstent(getActivity());
        WalletEntity=walletDBUtil.getWalletInfo();
        mWallName=walletDBUtil.getWallName();
        initView(view, inflater);
        qrcode.setImageBitmap(QrCodeUtils.createInstance(getActivity()).getQrCode(BackUpKeystoreActivity.walletKeystore));
        return view;
    }

    private void initView(View view, LayoutInflater inflater) {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @OnClick(R2.id.show_qrcode)
    public void onViewClicked() {
        zhezhao.setVisibility(View.GONE);
        qrcode.setVisibility(View.VISIBLE);
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
