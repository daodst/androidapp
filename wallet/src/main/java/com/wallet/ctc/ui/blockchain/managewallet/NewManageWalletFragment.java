

package com.wallet.ctc.ui.blockchain.managewallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.ui.blockchain.mywallet.MyWalletActivity;
import com.wallet.ctc.util.SettingPrefUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import common.app.ActivityRouter;
import common.app.my.abstracts.LazyFragmentPagerAdapter;



public class NewManageWalletFragment extends Fragment implements LazyFragmentPagerAdapter.Laziable {

    @BindView(R2.id.wallet_list)
    ListView walletList;
    @BindView(R2.id.tv_back)
    TextView tvBack;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.img_action)
    ImageView imgAction;
    protected Unbinder mUnbinder;
    @BindView(R2.id.nodata)
    LinearLayout nodata;
    private Intent intent;
    private NewManageWalletListAdapter mAdapter;
    private List<WalletEntity> mWallName = new ArrayList<>();
    private WalletDBUtil walletDBUtil;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_new_manage_wallet, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        walletDBUtil = WalletDBUtil.getInstent(getActivity());
        tvBack.setVisibility(View.GONE);
        initUiAndListener();
        return view;
    }

    public void initUiAndListener() {
        tvTitle.setText(getString(R.string.me_wallet));
        imgAction.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.mipmap.wallet_add_top));
        imgAction.setVisibility(View.VISIBLE);
        imgAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChooseCreatImportTypeActivity.class);
                intent.putExtra("from", 0);
                startActivity(intent);
            }
        });
        mAdapter = new NewManageWalletListAdapter(getActivity());
        mAdapter.bindData(mWallName);
        walletList.setAdapter(mAdapter);
        mAdapter.changeDef((address, postion) -> {
            intent = new Intent(getActivity(), MyWalletActivity.class);
            intent.putExtra("from", 1);
            intent.putExtra("walletAddress", mWallName.get(postion).getAllAddress());
            intent.putExtra("type", mWallName.get(postion).getType());
            startActivity(intent);
        });
        walletList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SettingPrefUtil.setWalletTypeAddress(getActivity(), mWallName.get(position).getType(), mWallName.get(position).getAllAddress());
                Intent intent = ActivityRouter.getEmptyContentIntent(getActivity(), ActivityRouter.Wallet.A_NewHomeFragment);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        mWallName = walletDBUtil.getWallName();

        if (null == mWallName || mWallName.size() < 1) {
            nodata.setVisibility(View.VISIBLE);
            walletList.setVisibility(View.GONE);
            return;
        }
        if (null != mAdapter) {
            nodata.setVisibility(View.GONE);
            walletList.setVisibility(View.VISIBLE);
            mAdapter.bindData(mWallName);
            mAdapter.notifyDataSetChanged();
        }
    }

    @OnClick({R2.id.tv_back, R2.id.import_wallet})
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.tv_back) {
            getActivity().finish();
        } else if (i == R.id.import_wallet) {
            intent = new Intent(getActivity(), ChooseCreatImportTypeActivity.class);
            intent.putExtra("from", 1);
            startActivity(intent);
        } else if (i == R.id.img_action) {

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
