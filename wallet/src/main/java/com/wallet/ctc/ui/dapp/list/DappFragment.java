package com.wallet.ctc.ui.dapp.list;

import static android.app.Activity.RESULT_OK;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.VpnService;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.bumptech.glide.Glide;
import com.wallet.IChatInfo;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.db.DappHistoryEntity;
import com.wallet.ctc.ui.dapp.DappWebViewActivity;
import com.wallet.ctc.ui.dapp.search.DappSearchHistoryActivity;
import com.wallet.ctc.vpn.service.SimpleVpnService;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import common.app.ActivityRouter;
import common.app.base.BaseFragment;
import common.app.base.fragment.mall.model.AdvertEntity;
import common.app.base.view.bannervew.ImageCycleView;
import common.app.im.ui.fragment.qrcode.QRCodeFragment;
import common.app.utils.GlideUtil;


public class DappFragment extends BaseFragment<DappViewModel> {
    @BindView(R2.id.back_iv)
    ImageView backIv;
    @BindView(R2.id.ll_search)
    LinearLayout searchLayout;
    @BindView(R2.id.scan_iv)
    ImageView scanIv;
    @BindView(R2.id.banner_view)
    ImageCycleView bannerView;
    @BindView(R2.id.dapp_listview)
    ListView dappListView;
    @BindView(R2.id.btn_vpn)
    TextView btn_vpn;

    private LikeDappAdapter mDappAdapter;

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_dapp;
    }

    @Override
    public void initView(@Nullable View view) {
        if (ActivityRouter.isInstanceof(getActivity(), ActivityRouter.getMainActivityName())) {
            backIv.setVisibility(View.GONE);
        }
        backIv.setOnClickListener(v -> {
            getActivity().finish();
        });


        
        searchLayout.setOnClickListener(v -> {
            goDappHistory("");
        });

        
        scanIv.setOnClickListener(v -> {
            
            Intent intent = ActivityRouter.getStringContentIntent(getActivity(), ActivityRouter.Common.F_QRCodeFragment, "getScan");
            startActivityForResult(intent, 1000);
        });

        
        bannerView.setImageResources(getBanner(), new ImageCycleView.ImageCycleViewListener() {
            @Override
            public void displayImage(String imageURL, ImageView imageView) {
                GlideUtil.showAllImg(getActivity(), imageURL, imageView);
            }

            @Override
            public void onImageClick(AdvertEntity info, int postion, View imageView) {
            }
        });

        mDappAdapter = new LikeDappAdapter(getActivity());
        dappListView.setAdapter(mDappAdapter);
        dappListView.setOnItemClickListener((parent, view1, position, id) -> {
            DappHistoryEntity dapp = mDappAdapter.getItem(position);
            DappWebViewActivity.startDappWebViewActivity(getActivity(), dapp.url);
        });

        
        getActivity().getLifecycle().addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onResume(@NonNull LifecycleOwner owner) {
                getViewModel().getMyLikeDapp();
            }
        });

        initVpn();
    }

    @Override
    public void initData() {
        getViewModel().observe(getViewModel().mDappsLD, new Observer<List<DappHistoryEntity>>() {
            @Override
            public void onChanged(List<DappHistoryEntity> dapps) {
                mDappAdapter.bindDatas(dapps);
            }
        });
        getViewModel().getMyLikeDapp();
    }


    public List<AdvertEntity> getBanner() {
        List<AdvertEntity> banners = new ArrayList<>();
        
        banners.add(new AdvertEntity("file:
        return banners;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().sendBroadcast(new Intent(ACTION_PING));
    }

    
    private void goDappHistory(String url) {
        DappSearchHistoryActivity.intent(getActivity(), url);
    }

    private static final String ACTION_VPN_STOPPED = "vpn_stopped";
    private static final String ACTION_VPN_STARTED = "vpn_started";
    private static final String ACTION_VPN_START_ERR = "vpn_start_err";
    private static final String ACTION_VPN_START_ERR_DNS = "vpn_start_err_dns";
    private static final String ACTION_VPN_START_ERR_CONFIG = "vpn_start_err_config";
    private static final String ACTION_PONG = "pong";
    private static final String ACTION_PING = "ping";
    private static final String ACTION_STOP_VPN = "stop_vpn";
    private boolean vpnRunning = false;
    private boolean vpnStarting = false;
    private boolean vpnStoping = false;

    private BroadcastReceiver vpnReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_VPN_STOPPED:
                    onVpnStopped();
                    break;
                case ACTION_VPN_STARTED:
                    onVpnStarted();
                    break;
                case ACTION_VPN_START_ERR:
                    onVpnStartErr();
                    break;
                case ACTION_VPN_START_ERR_DNS:
                    onVpnStartErr();
                    break;
                case ACTION_VPN_START_ERR_CONFIG:
                    onVpnStartErr();
                    break;
                case ACTION_PONG:
                    onVpnStarted();
                    break;
            }
        }
    };

    private void initVpn() {
        IntentFilter vpnfilter = new IntentFilter();
        vpnfilter.addAction(ACTION_VPN_STOPPED);
        vpnfilter.addAction(ACTION_VPN_STARTED);
        vpnfilter.addAction(ACTION_VPN_START_ERR);
        vpnfilter.addAction(ACTION_VPN_START_ERR_DNS);
        vpnfilter.addAction(ACTION_VPN_START_ERR_CONFIG);
        vpnfilter.addAction(ACTION_VPN_STOPPED);
        vpnfilter.addAction(ACTION_PONG);
        getActivity().registerReceiver(vpnReceiver, vpnfilter);

        Context context = mContext.getApplicationContext();
        IChatInfo chatInfo = null;
        
        btn_vpn.setVisibility(View.GONE);
        btn_vpn.setOnClickListener(v -> {
            if (!vpnStarting && !vpnRunning) {
                vpnStarting = true;
                getActivity().sendBroadcast(new Intent("stop_vpn"));
                Intent in = VpnService.prepare(getActivity());
                if (in != null) {
                    getActivity().startActivityForResult(in, 1);
                } else {
                    onActivityResult(1, RESULT_OK, null);
                }
            } else if (vpnRunning && !vpnStoping) {
                vpnStoping = true;
                getActivity().sendBroadcast(new Intent(ACTION_STOP_VPN));
            }
        });
    }

    private void onVpnStarted() {
        vpnRunning = true;
        vpnStarting = false;
        btn_vpn.setText(R.string.btn_vpn_close);
    }

    private void onVpnStopped() {
        vpnRunning = false;
        vpnStoping = false;
        btn_vpn.setText(R.string.btn_vpn_open);
    }

    private void onVpnStartErr() {
        showToast(getString(R.string.hint_vpn_start_failed));
        vpnStarting = false;
        vpnRunning = false;
        btn_vpn.setText(R.string.btn_vpn_open);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == RESULT_OK && data != null) {
            String qrContent = data.getStringExtra(QRCodeFragment.KEY_CONTENT);
            if (TextUtils.isEmpty(qrContent) || (!qrContent.startsWith("http://") && !qrContent.startsWith("https://"))) {
                showToast(R.string.js_hint_error_url);
                return;
            }
            goDappHistory(qrContent);
        } else if (requestCode == 1 && resultCode == RESULT_OK) {
            getActivity().startService(new Intent(getActivity(), SimpleVpnService.class));
        }
    }

    public class LikeDappAdapter extends BaseAdapter {

        private List<DappHistoryEntity> mDatas;
        private Context mContext;

        public LikeDappAdapter(Context context) {
            mDatas = new ArrayList<>();
            this.mContext = context;
        }

        public void bindDatas(List<DappHistoryEntity> dapps) {
            if (null != dapps) {
                mDatas.clear();
                mDatas.addAll(dapps);
                notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public DappHistoryEntity getItem(int position) {
            return mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_dapp_like, parent, false);
            }
            TextView tvTitle = convertView.findViewById(R.id.tv_title);
            ImageView ivLogo = convertView.findViewById(R.id.iv_logo);
            TextView tvUrl = convertView.findViewById(R.id.tv_url);
            DappHistoryEntity data = mDatas.get(position);
            tvTitle.setText(TextUtils.isEmpty(data.title) ? mContext.getString(R.string.js_un_known) : data.title);
            tvUrl.setText(data.url);
            if (data.iconRes > 0) {
                GlideUtil.showImg(mContext, data.iconRes, ivLogo);
            } else {
                Glide.with(mContext).load(data.iconPath).placeholder(R.mipmap.js_ic_bowse_web_icon_default).
                        error(R.mipmap.js_ic_bowse_web_icon_default).into(ivLogo);
            }
            return convertView;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(vpnReceiver);
    }
}
