

package com.wallet.ctc.ui.blockchain.home;

import static com.wallet.ctc.crypto.WalletUtil.BCH_COIN;
import static com.wallet.ctc.crypto.WalletUtil.BNB_COIN;
import static com.wallet.ctc.crypto.WalletUtil.BTC_COIN;
import static com.wallet.ctc.crypto.WalletUtil.DMF_BA_COIN;
import static com.wallet.ctc.crypto.WalletUtil.DMF_COIN;
import static com.wallet.ctc.crypto.WalletUtil.DM_COIN;
import static com.wallet.ctc.crypto.WalletUtil.DOGE_COIN;
import static com.wallet.ctc.crypto.WalletUtil.DOT_COIN;
import static com.wallet.ctc.crypto.WalletUtil.EOS_COIN;
import static com.wallet.ctc.crypto.WalletUtil.ETC_COIN;
import static com.wallet.ctc.crypto.WalletUtil.ETF_COIN;
import static com.wallet.ctc.crypto.WalletUtil.ETH_COIN;
import static com.wallet.ctc.crypto.WalletUtil.FIL_COIN;
import static com.wallet.ctc.crypto.WalletUtil.HT_COIN;
import static com.wallet.ctc.crypto.WalletUtil.LTC_COIN;
import static com.wallet.ctc.crypto.WalletUtil.MATIC_COIN;
import static com.wallet.ctc.crypto.WalletUtil.MCC_COIN;
import static com.wallet.ctc.crypto.WalletUtil.OTHER_COIN;
import static com.wallet.ctc.crypto.WalletUtil.SGB_COIN;
import static com.wallet.ctc.crypto.WalletUtil.SOL_COIN;
import static com.wallet.ctc.crypto.WalletUtil.TRX_COIN;
import static com.wallet.ctc.crypto.WalletUtil.XRP_COIN;
import static com.wallet.ctc.crypto.WalletUtil.ZEC_COIN;
import static common.app.BuildConfig.CURRENCY_UNIT;
import static common.app.my.RxNotice.MSG_WALLET_NUM_CHANGE;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.api.blockchain.BchApi;
import com.wallet.ctc.api.blockchain.BlockChainApi;
import com.wallet.ctc.api.blockchain.DogeApi;
import com.wallet.ctc.api.blockchain.DotApi;
import com.wallet.ctc.api.blockchain.EtcApi;
import com.wallet.ctc.api.blockchain.FilApi;
import com.wallet.ctc.api.blockchain.LtcApi;
import com.wallet.ctc.api.blockchain.MarketPriceApi;
import com.wallet.ctc.api.blockchain.SolApi;
import com.wallet.ctc.api.blockchain.TrxApi;
import com.wallet.ctc.api.blockchain.ZecApi;
import com.wallet.ctc.api.me.MeApi;
import com.wallet.ctc.base.BaseEntity;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.crypto.XrpTransctionUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.BandWidthBean;
import com.wallet.ctc.model.blockchain.BanlanceBean;
import com.wallet.ctc.model.blockchain.BaseTrxBanlanceBean;
import com.wallet.ctc.model.blockchain.BchBalanceBean;
import com.wallet.ctc.model.blockchain.BlockChainBtcBean;
import com.wallet.ctc.model.blockchain.BtcBanlanceBean;
import com.wallet.ctc.model.blockchain.CoinPriceBean;
import com.wallet.ctc.model.blockchain.DogeBalanceBean;
import com.wallet.ctc.model.blockchain.DotBalanceBean;
import com.wallet.ctc.model.blockchain.EtcBalanceBean;
import com.wallet.ctc.model.blockchain.EthCallBean;
import com.wallet.ctc.model.blockchain.EvmosOneBalanceBean;
import com.wallet.ctc.model.blockchain.FilBalanceBean;
import com.wallet.ctc.model.blockchain.GetBalBean;
import com.wallet.ctc.model.blockchain.InitPriceBean;
import com.wallet.ctc.model.blockchain.LtcBalanceBean;
import com.wallet.ctc.model.blockchain.MarketPriceBean;
import com.wallet.ctc.model.blockchain.RpcApi;
import com.wallet.ctc.model.blockchain.SolBalanceBean;
import com.wallet.ctc.model.blockchain.SolBaseBean;
import com.wallet.ctc.model.blockchain.TrxBanlanceBean;
import com.wallet.ctc.model.blockchain.WalletLogoBean;
import com.wallet.ctc.model.blockchain.XrpAccountInfo;
import com.wallet.ctc.model.blockchain.XrpAccountInfoBean;
import com.wallet.ctc.model.blockchain.XrpAccountLinesBean;
import com.wallet.ctc.model.blockchain.XrpLinesBean;
import com.wallet.ctc.model.blockchain.ZecBalanceBean;
import com.wallet.ctc.nft.ui.NftListFragment;
import com.wallet.ctc.ui.blockchain.addnode.AddNodeActivity;
import com.wallet.ctc.ui.blockchain.assetsdetail.AssetsBTCDetailActivity;
import com.wallet.ctc.ui.blockchain.assetsdetail.AssetsDetailActivity;
import com.wallet.ctc.ui.blockchain.assetsdetail.AssetsFilDetailActivity;
import com.wallet.ctc.ui.blockchain.assetsdetail.AssetsSgbDetailActivity;
import com.wallet.ctc.ui.blockchain.choosenode.ChooseNodeActivity;
import com.wallet.ctc.ui.blockchain.managewallet.AddWalletTypeActivity;
import com.wallet.ctc.ui.blockchain.managewallet.ChooseCreatImportTypeActivity;
import com.wallet.ctc.ui.me.about.StringUtils;
import com.wallet.ctc.util.DecriptUtil;
import com.wallet.ctc.util.HexUtils;
import com.wallet.ctc.util.LogUtil;
import com.wallet.ctc.util.SettingPrefUtil;
import com.wallet.ctc.view.dialog.choosewallet.ChooseWalletDialog;
import com.wallet.ctc.view.huakuai.WalletPageView;
import com.wallet.ctc.view.listview.NoScrollListView;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import common.app.ActivityRouter;
import common.app.base.base.BaseFragment;
import common.app.base.fragment.mall.catcherror.BaseSubscriber;
import common.app.base.them.Eyes;
import common.app.mall.util.ToastUtil;
import common.app.my.RxNotice;
import common.app.my.abstracts.LazyFragmentPagerAdapter;
import common.app.my.view.MyAlertDialog;
import common.app.ui.view.InputPwdDialog;
import common.app.utils.NetWorkUtils;
import common.app.utils.SpUtil;
import common.app.utils.ThreadManager;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import owallet.AccountInfo;
import wallet.core.jni.CoinType;

public class NewHomeFragment extends BaseFragment implements LazyFragmentPagerAdapter.Laziable {

    @BindView(R2.id.tv_back)
    TextView tvBack;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.img_action)
    ImageView imgAction;
    @BindView(R2.id.left_tv)
    TextView leftTv;
    @BindView(R2.id.heard)
    WalletPageView heard;
    @BindView(R2.id.wallet_list)
    NoScrollListView walletList;
    
    @BindView(R2.id.scan_it)
    TextView scanIt;
    @BindView(R2.id.wallet)
    TextView wallet;
    @BindView(R2.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R2.id.xrp_tip)
    TextView xrpTip;
    @BindView(R2.id.refresh_view)
    SwipeRefreshLayout refreshView;
    @BindView(R2.id.fNftContainer)
    FrameLayout fNftContainer;
    protected Unbinder mUnbinder;
    private int chooseWallet = 0;
    private Intent intent;
    private WalletEntity mWallet;
    private WalletDBUtil walletDBUtil;
    private BlockChainApi mApi = new BlockChainApi();
    private RpcApi rpcApi = new RpcApi();
    private TrxApi mTApi = new TrxApi();
    private FilApi mFilApi = new FilApi();
    private DogeApi mDogeApi = new DogeApi();
    private DotApi mDotApi = new DotApi();
    private LtcApi mLtcApi = new LtcApi();
    private BchApi mBchApi = new BchApi();
    private ZecApi mZecApi = new ZecApi();
    private EtcApi mEtcApi = new EtcApi();
    private SolApi mSolApi = new SolApi();
    private MeApi meApi = new MeApi();
    private MarketPriceApi priceApi = new MarketPriceApi();
    private List<AssertBean> mustC;
    private List<AssertBean> list = new ArrayList<>();
    private Map<String, String> assnum = new HashMap<>();
    private List<WalletEntity> mWallName;
    private List<WalletLogoBean> walletLogoBeans = new ArrayList<>();
    private HomeWalletListAdapter mAdapter;
    private HomeMenuWalletListAdapter mMenuAdapter;
    private Gson gson = new Gson();
    private boolean isLoad = false;
    private int type;
    private BigDecimal dmprice = new BigDecimal("3.00");
    public static BigDecimal mccprice = new BigDecimal("0.42");
    private final String KEY_NO_TRUST = "noTrust";
    private final String KEY_HAS_TRUST = "hasTrust";
    private CompositeDisposable mDisposable;
    private ChooseWalletDialog chooseWalletDialog;
    private String curCoin = CURRENCY_UNIT;
    private NftListFragment nftListFragment = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_newhome, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        if (ActivityRouter.isInstanceof(getActivity(), ActivityRouter.getMainActivityName())){
            View viewById = view.findViewById(R.id.fragment_newhome_root);
            Eyes.addStatusBar(requireActivity(), (ViewGroup) viewById, Color.WHITE);
        }
        initUiAndListener();
        return view;
    }

    @Override
    public void succeed(Object obj) {
        if (obj instanceof RxNotice) {
            if (((RxNotice) obj).mType == MSG_WALLET_NUM_CHANGE) {
                onResume();
            }
        }
    }

    private void initUiAndListener() {

        mDisposable = new CompositeDisposable();
        walletDBUtil = WalletDBUtil.getInstent(getActivity());
        mAdapter = new HomeWalletListAdapter(getActivity());
        mMenuAdapter = new HomeMenuWalletListAdapter(getActivity());
        mAdapter.bindData(list);
        walletList.setAdapter(mAdapter);
        tvTitle.setOnClickListener(v -> {
        });
        refreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mDisposable.clear();
                getData2();
            }
        });
        leftTv.setVisibility(View.VISIBLE);
        leftTv.setOnClickListener(view -> {
            if (null != mWallet) {
                if (mWallet.getType() == MCC_COIN) {
                    return;
                }
                Intent intent = new Intent(getActivity(), AddNodeActivity.class);
                intent.putExtra("type", mWallet.getType());
                List<AssertBean> mustList = walletDBUtil.getMustWallet(mWallet.getType());
                if (null != mustList && mustList.size() > 0) {
                    AssertBean assertBean = mustList.get(0);
                    intent.putExtra("name", assertBean.getShort_name());
                }
                startActivity(intent);
            }
        });
        chooseWalletDialog = new ChooseWalletDialog(mActivity);
        chooseWalletDialog.setChooseWallet(new ChooseWalletDialog.ChooseWallet() {
            @Override
            public void onChangeWallet(String address, int type) {
                chooseWalletDialog.dismiss();
                int position = 0;
                for (int i = 0; i < mWallName.size(); i++) {
                    WalletEntity walletEntity = mWallName.get(i);
                    if (walletEntity.getAllAddress().equals(address) && walletEntity.getType() == type) {
                        position = i;
                        break;
                    }
                }
                if (chooseWallet == position) {
                    return;
                }
                chooseWallet = position;
                SettingPrefUtil.setWalletTypeAddress(getActivity(), mWallName.get(position).getType(), mWallName.get(position).getAllAddress());
                mWallet = walletDBUtil.getWalletInfo();
                heard.setWalletData(mWallName);
                initTitle();
                isLoad = true;
                getData2();

            }

            @Override
            public void addWallet(int type) {
                if (type == 1) {
                    intent = new Intent(mActivity, AddWalletTypeActivity.class);
                } else {
                    intent = new Intent(mActivity, ChooseCreatImportTypeActivity.class);
                    intent.putExtra("from", 0);
                }
                startActivity(intent);
                chooseWalletDialog.dismiss();
            }
        });
        imgAction.setOnClickListener(v -> {
            if (TextUtils.isEmpty(BuildConfig.ENABLE_CREAT_ALL_WALLET)) {
                drawerLayout.openDrawer(Gravity.RIGHT);
            } else {
                intent = ActivityRouter.getEmptyContentIntent(mActivity, ActivityRouter.Common.F_QRCodeFragment);
                startActivityForResult(intent, 1000);
            }
        });
        if (TextUtils.isEmpty(BuildConfig.ENABLE_CREAT_ALL_WALLET)) {
            imgAction.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.mipmap.home_more));
            tvBack.setOnClickListener(v -> {
                mActivity.finish();
            });
        } else {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            imgAction.setImageResource(R.mipmap.saoyisao);
            Drawable drawable = ContextCompat.getDrawable(getContext(), R.mipmap.show);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tvBack.setCompoundDrawables(drawable, null, null, null);
            tvBack.setOnClickListener(v -> {
                if (null == mWallName || mWallName.size() == 0) {
                    ToastUtil.showToast(getString(R.string.no_more_wallet_can_change));
                    return;
                }
                chooseWalletDialog.show(mWallName, walletLogoBeans);
            });
        }
        imgAction.setVisibility(View.VISIBLE);
        scanIt.setOnClickListener(v -> {
            intent = ActivityRouter.getEmptyContentIntent(mActivity, ActivityRouter.Common.F_QRCodeFragment);
            startActivityForResult(intent, 1000);
            drawerLayout.closeDrawer(Gravity.RIGHT);
        });
        wallet.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChooseCreatImportTypeActivity.class);
            intent.putExtra("from", 0);
            startActivity(intent);
            drawerLayout.closeDrawer(Gravity.RIGHT);
        });

        heard.setPageSelected(new WalletPageView.PageSelected() {
            @Override
            public void onPageSelected(int position) {
                if (chooseWallet == position) {
                    return;
                }
                chooseWallet = position;
                mWallet = walletDBUtil.getWalletInfoByAddress(mWallName.get(position).getAllAddress(), mWallName.get(position).getType());
                SettingPrefUtil.setWalletTypeAddress(getActivity(), mWallet.getType(), mWallet.getAllAddress());
                initTitle();
                isLoad = true;
                getData2();
                drawerLayout.closeDrawer(Gravity.RIGHT);
            }

            @Override
            public void changBottomItem(int pos) {
                if (pos == 0) {
                    walletList.setVisibility(View.VISIBLE);
                    fNftContainer.setVisibility(View.GONE);
                } else if (pos == 1) {
                    if (nftListFragment != null) nftListFragment.loadData();
                    walletList.setVisibility(View.GONE);
                    fNftContainer.setVisibility(View.VISIBLE);
                }
            }
        });
        walletList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (type == ETH_COIN || type == ETF_COIN || type == MATIC_COIN || type == SOL_COIN || type == DMF_COIN || type == DMF_BA_COIN || type == HT_COIN || type == BNB_COIN) {
                    intent = AssetsFilDetailActivity.getIntent(getActivity(), list.get(position), mWallet.getAllAddress(), mWallet.getType());
                } else if (type == DM_COIN || type == MCC_COIN || type == OTHER_COIN) {
                    String contract = list.get(position).getContract();
                    if (!TextUtils.isEmpty(contract)) {
                        
                        intent = AssetsFilDetailActivity.getIntent(getActivity(), list.get(position), mWallet.getDefaultAddress(), mWallet.getType());
                    } else {
                        
                        intent = AssetsDetailActivity.getIntent(getActivity(), list.get(position));
                    }
                } else if (type == BTC_COIN) {
                    if (position == 0) {
                        intent = AssetsFilDetailActivity.getIntent(getActivity(), list.get(position), mWallet.getAllAddress(), mWallet.getType());
                    } else {
                        intent = new Intent(getActivity(), AssetsBTCDetailActivity.class);
                        intent.putExtra("walletAddress", mWallet.getAllAddress());
                        intent.putExtra("walletType", mWallet.getType());
                        intent.putExtra("type", list.get(position).getShort_name());
                        intent.putExtra("address", list.get(position).getContract());
                        intent.putExtra("gasCount", list.get(position).getGas());
                        intent.putExtra("decimal", list.get(position).getDecimal());
                        intent.putExtra("logo", list.get(position).getImg_path());
                        intent.putExtra("DbNum", list.get(position).getAssertsNum());
                        intent.putExtra("DbPrice", list.get(position).getAssertsSumPrice());
                    }

                } else if (type == TRX_COIN) {
                    intent = AssetsFilDetailActivity.getIntent(getActivity(), list.get(position), mWallet.getAllAddress(), mWallet.getType());
                } else if (type == FIL_COIN || type == DOGE_COIN || type == DOT_COIN || type == LTC_COIN || type == BCH_COIN || type == ZEC_COIN || type == ETC_COIN) {
                    intent = AssetsFilDetailActivity.getIntent(getActivity(), list.get(position), mWallet.getAllAddress(), mWallet.getType());
                } else if (type == SGB_COIN) {
                    intent = new Intent(getActivity(), AssetsSgbDetailActivity.class);
                    intent.putExtra("walletAddress", mWallet.getAllAddress());
                    intent.putExtra("walletType", mWallet.getType());
                    intent.putExtra("type", list.get(position).getShort_name());
                    intent.putExtra("address", list.get(position).getContract());
                    intent.putExtra("gasCount", list.get(position).getGas());
                    intent.putExtra("decimal", list.get(position).getDecimal());
                    intent.putExtra("logo", list.get(position).getImg_path());
                    intent.putExtra("DbNum", list.get(position).getAssertsNum());
                    intent.putExtra("DbPrice", list.get(position).getAssertsSumPrice());
                } else if (type == XRP_COIN) {
                    AssertBean assertBean = list.get(position);
                    if (assertBean.getShort_name().equals("XRP")) {
                        
                        goXrpAssetsDetail(position);
                    } else {
                        
                        String desc = assertBean.getDesc();
                        if (!TextUtils.isEmpty(desc) && desc.equals(KEY_HAS_TRUST)) {
                            
                            goXrpAssetsDetail(position);
                        } else if (!TextUtils.isEmpty(desc) && desc.equals(KEY_NO_TRUST)) {
                            
                            showXrpTrustDialog(position);
                        } else {
                            if (list.get(position).getDesc() == KEY_HAS_TRUST) {
                                goXrpAssetsDetail(position);
                            } else {
                                
                                showXrpTrustDialog(position);
                            }
                        }
                    }
                    return;
                } else {
                    return;
                }
                startActivity(intent);
            }
        });
    }

    
    private void showXrpTrustDialog(int position) {
        MyAlertDialog dialog = new MyAlertDialog(getActivity(), getString(R.string.xpy_dm_add_tip));
        dialog.setonclick(new MyAlertDialog.Onclick() {
            @Override
            public void Yes() {
                dialog.dismiss();
                showXrpPayDialog(position);
            }

            @Override
            public void No() {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showXrpPayDialog(int position) {
        InputPwdDialog dialog = new InputPwdDialog(getActivity(), getString(R.string.place_edit_password));
        dialog.setonclick(new InputPwdDialog.Onclick() {
            @Override
            public void Yes(String pwd) {
                dialog.dismiss();
                if (!WalletDBUtil.getInstent(getActivity()).getWalletInfo().getmPassword().equals(DecriptUtil.MD5(pwd))) {
                    ToastUtil.showToast(getString(R.string.password_error2));
                    return;
                }

                XrpTransctionUtil util = new XrpTransctionUtil(getActivity(), null);
                AssertBean assertBean = list.get(position);
                util.trustToken(mWallet.getAllAddress(), pwd, assertBean.getContract(), assertBean.getShort_name(), assertBean.getTotal(), new XrpTransctionUtil.TrustTokenCallBack() {
                    @Override
                    public void onTrustSuccess(String data) {
                        
                        ToastUtil.showToast(getString(R.string.caozuo_success));
                        goXrpAssetsDetail(position);
                    }

                    @Override
                    public void onFail(String info) {
                        ToastUtil.showToast(info);
                    }
                });
            }

            @Override
            public void No() {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void goXrpAssetsDetail(int position) {
        intent = AssetsFilDetailActivity.getIntent(getActivity(), list.get(position), "", -1);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null == getActivity()) {
            return;
        }
        mWallName = walletDBUtil.getWallName();
        mWallet = walletDBUtil.getWalletInfo();
        if (mWallet == null) {
            return;
        }
        walletLogoBeans.clear();
        
        walletLogoBeans.addAll(walletDBUtil.getWalletLogos(true));

        mDisposable.clear();
        for (int i = 0; i < mWallName.size(); i++) {
            if (mWallName.get(i).getAllAddress().equals(mWallet.getAllAddress()) && mWallName.get(i).getType() == mWallet.getType()) {
                chooseWallet = i;
                break;
            }
        }
        heard.setWalletData(mWallName);
        initTitle();
        isLoad = false;
        if (chooseWallet >= mWallName.size()) {
            chooseWallet = 0;
        }
        getData2();
    }

    private void initTitle() {
        tvTitle.setText(mWallet.getName());
    }

    BigDecimal sum;
    private int ethcount;

    public void getData2() {
        if (isLoad) {
            mDisposable.clear();
        }
        if (mWallet == null) {
            return;
        }
        
        checkNodeState();

        
        mMenuAdapter.bindData(mWallName);
        mMenuAdapter.notifyDataSetChanged();
        mustC = walletDBUtil.getMustWallet(mWallet.getType());
        list.clear();
        if (list.size() == 0) {
            mustC.get(0).setAssertsNum(mWallet.getmBalance());
            mustC.get(0).setAssertsSumPrice(mWallet.getmPrice());
            list.addAll(mustC);
        }
        list.addAll(walletDBUtil.getAssetsByWalletType(mWallet.getAllAddress(), mWallet.getType()));
        mAdapter.bindData(list);
        mAdapter.notifyDataSetChanged();
        sum = new BigDecimal("0");
        type = mWallet.getType();
        xrpTip.setVisibility(View.GONE);

        if (type == ETH_COIN || type == ETF_COIN || type == DMF_COIN || type == DMF_BA_COIN
                || type == HT_COIN || type == BNB_COIN || type == MATIC_COIN) {
            ethcount = 0;
            for (int i = 0; i < list.size(); i++) {
                getEthBanlance(i);
            }
        } else if (type == MCC_COIN) {
            ethcount = 0;
            for (int i = 0; i < list.size(); i++) {
                String contract = list.get(i).getContract();
                if (!TextUtils.isEmpty(contract)) {
                    
                    getEthBanlance(i);
                } else {
                    
                    getMccBanlance(i);
                }
            }
        } else if (type == BTC_COIN) {
            getBTCBanlance();
        } else if (type == EOS_COIN) {
        } else if (type == OTHER_COIN || type == DM_COIN) {
            getBanlance();
        } else if (type == XRP_COIN) {
            getXrpBanlance();
            getXrpTokenBalance();
            xrpTip.setVisibility(View.VISIBLE);
        } else if (type == TRX_COIN) {
            getTrxBanlance();
        } else if (type == FIL_COIN) {
            getFilBanlance();
        } else if (type == DOGE_COIN) {
            getDogeBalance();
        } else if (type == DOT_COIN) {
            getDotBalance();
        } else if (type == LTC_COIN) {
            getLtcBalance();
        } else if (type == BCH_COIN) {
            getBchBalance();
        } else if (type == ZEC_COIN) {
            getZecBalance();
        } else if (type == ETC_COIN) {
            getEtcBanlance();
        } else if (type == SGB_COIN) {
            getSgbBanlance();
        } else if (type == SOL_COIN) {
            getSolBanlance();
        } else {
            LogUtil.d("" + type);
        }
        refreshView.setRefreshing(false);
    }

    private void getXrpTokenBalance() {

        
        Map<String, Object> params = new TreeMap();
        params.put("method", "account_info");
        params.put("id", MeApi.getETHID());
        List<Object> addr = new ArrayList<>();
        addr.add(new XrpAccountInfo(mWallet.getAllAddress()));
        params.put("params", addr);
        rpcApi.getXrpTokenBalance(gson.toJson(params), type).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<XrpAccountLinesBean>(getActivity()) {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable.add(d);
                    }

                    @Override
                    public void onNexts(XrpAccountLinesBean baseEntity) throws Exception {
                        if (null != baseEntity && baseEntity.getResult().getStatus().equals("success")) {
                            List<XrpLinesBean> beans = baseEntity.getResult().getLines();
                            list.get(0).setDesc(KEY_HAS_TRUST);
                            for (int i = 1; i < list.size(); i++) {
                                list.get(i).setDesc(KEY_NO_TRUST);
                                for (int j = 0; j < beans.size(); j++) {
                                    if (list.get(i).getShort_name().toUpperCase().equals(beans.get(j).getCurrency().toUpperCase())) {
                                        list.get(i).setDesc(KEY_HAS_TRUST);
                                        list.get(i).setAssertsNum(beans.get(j).getBalance());
                                        list.get(i).setAssertsSumPrice("0.00");
                                        walletDBUtil.updateWalletAssets(list.get(i));
                                    }
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                        } else {

                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        LogUtil.d(e.toString());
                    }
                });

    }

    private void getXrpBanlance() {
        Map<String, Object> params = new TreeMap<>();
        params.put("method", "account_info");
        params.put("id", MeApi.getETHID());
        List<Object> addr = new ArrayList<>();
        addr.add(new XrpAccountInfo(mWallet.getAllAddress()));
        params.put("params", addr);
        rpcApi.getXrpBalance(gson.toJson(params), type).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<XrpAccountInfoBean>(getActivity()) {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable.add(d);
                    }

                    @Override
                    public void onNexts(XrpAccountInfoBean baseEntity) {
                        LogUtil.d(baseEntity.toString());
                        if (null == baseEntity.getResult() && baseEntity.getResult().getStatus().equals("success")) {
                            String result = baseEntity.getResult().getAccount_data().getBalance();
                            LogUtil.d("" + result);
                            BigDecimal Remain = new BigDecimal(result).divide(new BigDecimal(Math.pow(10, list.get(0).getDecimal())), 8, RoundingMode.HALF_UP);
                            list.get(0).setAssertsNum(Remain.toPlainString());
                            list.get(0).setAssertsSumPrice("0.00");
                            mWallet.setmBalance(Remain.toPlainString());
                            walletDBUtil.updateWalletInfoByAddress(mWallet);
                            mAdapter.notifyDataSetChanged();
                            getCoinPrice();
                        } else {
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        LogUtil.d(e.toString());
                    }
                });
    }

    private void getBanlance() {
        String coinlist = getCoinList(list);
        Map<String, Object> params2 = new TreeMap();
        params2.put("arg", coinlist);
        mApi.getBanlance(params2, type).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseEntity>(getActivity()) {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable.add(d);
                    }

                    @Override
                    public void onNexts(BaseEntity baseEntity) {
                        if (baseEntity.getStatus() == 1) {
                            String s2 = gson.toJson(baseEntity.getData());
                            Map<String, String> map = gson.fromJson(s2, new TypeToken<Map<String, String>>() {
                            }.getType());
                            showBanlance(map);
                        } else {
                            ToastUtil.showToast(baseEntity.getInfo());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        LogUtil.d(e.toString());
                    }
                });
    }

    private void getTrxBanlance() {
        Map<String, Object> params = new TreeMap<>();
        params.put("address", mWallet.getAllAddress());
        mTApi.getAccounts(params).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseTrxBanlanceBean>(getActivity()) {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable.add(d);
                    }

                    @Override
                    public void onNexts(BaseTrxBanlanceBean baseEntity) {
                        if (null != baseEntity.getBandwidth()) {
                            BandWidthBean mBean = baseEntity.getBandwidth();
                            LogUtil.d("" + mBean.getEnergyRemaining() + "");
                            mWallName.get(chooseWallet).setEnergy(mBean.getEnergyRemaining().setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
                            mWallName.get(chooseWallet).setBroadband(mBean.getFreeNetRemaining().toPlainString());
                            mWallName.get(chooseWallet).setEnergyMax(mBean.getEnergyLimit().setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
                            mWallName.get(chooseWallet).setBroadbandMax(mBean.getFreeNetLimit().toPlainString());
                            LogUtil.d("" + mBean.getFreeNetRemaining() + "");
                        }
                        
                        if (null == baseEntity.getTokens() || baseEntity.getTokens().size() < 1) {
                            for (int i = 0; i < list.size(); i++) {
                                list.get(i).setAssertsNum("0.00");
                                list.get(i).setAssertsSumPrice("0.00");
                                if (!list.get(i).getShort_name().equals("TRX")) {
                                    walletDBUtil.updateWalletAssets(list.get(i));
                                }
                            }
                            mWallet.setmBalance("0.00");
                            mWallet.setmPrice("0.00");
                            walletDBUtil.updateWalletInfoByAddress(mWallet);
                        } else {
                            if (SettingPrefUtil.getWalletLoadNum(mWallet.getAllAddress()) == 0) {
                                addTrxCoin(baseEntity.getTokens());
                            }
                            BigDecimal sum = new BigDecimal("0");
                            
                            for (int position = 0; position < list.size(); position++) {
                                list.get(position).setAssertsNum("0.00");
                                list.get(position).setAssertsSumPrice("0.00");
                                for (int j = 0; j < baseEntity.getTokens().size(); j++) {
                                    TrxBanlanceBean mBean = baseEntity.getTokens().get(j);
                                    String tokenaddress = mBean.getTokenId();
                                    String tokenName = mBean.getTokenAbbr().toLowerCase();
                                    BigDecimal tokenBalance = new BigDecimal(mBean.getBalance());
                                    tokenBalance = tokenBalance.divide(new BigDecimal(mBean.getTokenDecimal()), 2, BigDecimal.ROUND_HALF_UP);
                                    if ((TextUtils.isEmpty(tokenaddress) || tokenaddress.length() < 5) && tokenName.equalsIgnoreCase(list.get(position).getShort_name())) {
                                        list.get(position).setTokenPriceInTrx(mBean.getTokenPriceInTrx());
                                        list.get(position).setAssertsNum(tokenBalance.toPlainString());
                                        mWallet.setmBalance(tokenBalance.toPlainString());
                                    } else if (tokenaddress.equalsIgnoreCase(list.get(position).getContract())) {
                                        list.get(position).setTokenPriceInTrx(mBean.getTokenPriceInTrx());
                                        list.get(position).setAssertsNum(tokenBalance.toPlainString());
                                        walletDBUtil.updateWalletAssets(list.get(position));
                                    }
                                }

                            }

                            walletDBUtil.updateWalletInfoByAddress(mWallet);
                            mAdapter.notifyDataSetChanged();
                            getCoinPrice();

                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        LogUtil.d(e.toString());
                    }
                });
    }

    
    private void addTrxCoin(List<TrxBanlanceBean> tokens) {
        List<AssertBean> aslist = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            
            if (tokens.get(i).getTokenPriceInTrx().floatValue() != 0 && !assetsHas(tokens.get(i).getTokenId())) {
                TrxBanlanceBean mBean = tokens.get(i);
                AssertBean assbean2 = new AssertBean(mBean.getTokenLogo(), mBean.getTokenAbbr(), mBean.getTokenName(), mBean.getTokenId(), "", mBean.tokenDecimal + "", mWallet.getType(), 2);
                assbean2.setWalletAddress(mWallet.getAllAddress());
                aslist.add(assbean2);
            }
        }
        if (null != aslist && aslist.size() > 0) {

            walletDBUtil.addAssetsList(aslist);
            list.clear();
            if (list.size() == 0) {
                mustC.get(0).setAssertsNum(mWallet.getmBalance());
                mustC.get(0).setAssertsSumPrice(mWallet.getmPrice());
                list.addAll(mustC);
            }

            list.addAll(walletDBUtil.getAssetsByWalletType(mWallet.getAllAddress(), mWallet.getType()));
            SettingPrefUtil.setWalletLoadNum(mWallet.getAllAddress(), 1);
            LogUtil.d("" + list.size());

        }
    }

    private boolean assetsHas(String tokenaddress) {
        boolean ishave = false;
        for (int i = 0; i < list.size(); i++) {
            if (TextUtils.isEmpty(tokenaddress) || tokenaddress.length() < 5) {
                ishave = true;
                break;
            } else if (tokenaddress.equalsIgnoreCase(list.get(i).getContract())) {
                ishave = true;
                break;
            }
        }
        return ishave;
    }

    private void getBTCBanlance() {
        Map<String, Object> params2 = new TreeMap();
        params2.put("addr", mWallet.getAllAddress());
        rpcApi.getBtcBalance(mWallet.getAllAddress(), type).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BlockChainBtcBean>(getActivity()) {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable.add(d);
                    }

                    @Override
                    public void onNexts(BlockChainBtcBean baseEntity) {
                        if (null == baseEntity.getError()) {
                            String result = baseEntity.getBalance();
                            BigDecimal remain = new BigDecimal(result).divide(new BigDecimal(Math.pow(10, list.get(0).getDecimal())), 8, RoundingMode.HALF_UP);
                            list.get(0).setAssertsNum(remain.toPlainString());
                            mAdapter.notifyDataSetChanged();
                            mWallet.setmBalance(remain.toPlainString());
                            heard.changeZichan(chooseWallet);
                            getCoinPrice();
                        } else {
                            ToastUtil.showToast(baseEntity.getError());
                        }
                        getUsdtBanlance();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        LogUtil.d(e.toString());
                    }
                });
    }

    private void getUsdtBanlance() {
        Map<String, Object> params2 = new TreeMap();
        params2.put("addr", mWallet.getAllAddress());
        meApi.getBtcUsdtBanlance(params2).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseEntity>(getActivity()) {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable.add(d);
                    }

                    @Override
                    public void onNexts(BaseEntity baseEntity) {
                        if (baseEntity.getStatus() == 1) {
                            BtcBanlanceBean mBean = gson.fromJson(gson.toJson(baseEntity.getData()), BtcBanlanceBean.class);
                            BigDecimal bigDecimal = new BigDecimal(mBean.getRemain()).multiply(new BigDecimal(mBean.getCprice()));
                            list.get(1).setAssertsNum(mBean.getRemain());
                            list.get(1).setAssertsSumPrice(bigDecimal.toPlainString());
                            mAdapter.notifyDataSetChanged();
                            BigDecimal sum = new BigDecimal(mWallet.getSumPrice()).add(bigDecimal);
                            mWallet.setSumPrice(sum.toPlainString());
                            mWallName.get(chooseWallet).setSumPrice(sum.toPlainString());
                            walletDBUtil.updateWalletInfoByAddress(mWallet);
                            heard.changeZichan(chooseWallet);
                        } else {
                            ToastUtil.showToast(baseEntity.getInfo());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        LogUtil.d(e.toString());
                    }
                });
    }

    private void getMccBanlance(final int position) {
        String coinName = list.get(position).getShort_name();
        String address = mWallet.getAllAddress2();
        rpcApi.getEvmosOneBalance(address, coinName).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<EvmosOneBalanceBean>(getActivity()) {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable.add(d);
                    }

                    @Override
                    public void onNexts(EvmosOneBalanceBean balanceBean) {
                        ethcount++;
                        if (balanceBean.isSuccess()) {
                            int decimal = list.get(position).getDecimal();
                            String remain = balanceBean.getBalance(decimal);
                            LogUtil.i("remain=" + remain + ",coinName=" + coinName);
                            if (!TextUtils.isEmpty(remain)) {
                                list.get(position).setAssertsNum(remain);
                                mAdapter.notifyDataSetChanged();
                            }
                        } else {
                            ToastUtil.showToast(balanceBean.getInfo());
                        }

                        if (ethcount >= list.size()) {
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        ethcount++;
                        if (ethcount >= list.size()) {
                        }
                    }
                });
    }


    private void getEthBanlance(int position) {
        Map<String, Object> params = new TreeMap<>();
        if (TextUtils.isEmpty(list.get(position).getContract())) {
            params.put("jsonrpc", "2.0");
            params.put("method", "eth_getBalance");
            params.put("id", MeApi.getETHID());
            List<String> addr = new ArrayList<>();
            addr.add(mWallet.getAllAddress());
            addr.add("latest");
            params.put("params", addr);
        } else {
            String walletAddress = mWallet.getAllAddress();
            if (mWallet.getType() == MCC_COIN) {
                walletAddress = mWallet.getDefaultAddress();
            }
            if (TextUtils.isEmpty(walletAddress)) {
                Log.e("Wallet", "getEthBanlance error walletAddress is null");
                return;
            }
            String dataAddr = walletAddress.substring(2);

            dataAddr = "0x70a08231000000000000000000000000" + dataAddr;
            params.put("addr", walletAddress);
            params.put("jsonrpc", "2.0");
            params.put("method", "eth_call");
            params.put("id", MeApi.getETHID());
            List<Object> addr = new ArrayList<>();
            addr.add(new EthCallBean(list.get(position).getContract(), dataAddr));
            addr.add("latest");
            params.put("params", addr);
        }
        rpcApi.getBalance(gson.toJson(params), type).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<FilBalanceBean>(getActivity()) {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable.add(d);
                    }

                    @Override
                    public void onNexts(FilBalanceBean baseEntity) {
                        ethcount++;
                        if (null == baseEntity.getError()) {
                            String result = HexUtils.hextoTen(baseEntity.getResult());
                            BigDecimal Remain = new BigDecimal(result).divide(new BigDecimal(Math.pow(10, list.get(position).getDecimal())), 8, RoundingMode.HALF_UP);
                            list.get(position).setAssertsNum(Remain.toPlainString());
                            list.get(position).setAssertsSumPrice("0.00");
                            if (TextUtils.isEmpty(list.get(position).getContract())) {
                                mWallet.setmBalance(Remain.toPlainString());
                                walletDBUtil.updateWalletInfoByAddress(mWallet);
                            } else {
                                walletDBUtil.updateWalletAssets(list.get(position));
                            }
                            mAdapter.notifyDataSetChanged();

                            if (type == BNB_COIN || type == ETH_COIN) {
                                getPancakeSwapPrice(list.get(position));
                            } else {
                                if (ethcount >= list.size()) {
                                    getCoinPrice();
                                }
                            }
                        } else {
                            ToastUtil.showToast(baseEntity.getError().getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        ethcount++;
                        if (ethcount >= list.size()) {
                            getCoinPrice();
                        }
                    }
                });
    }

    private void showBanlance(Map<String, String> bean) {
        assnum = bean;
        String address = mWallet.getAllAddress();
        if (chooseWallet >= mWallName.size()) {
            return;
        }
        String mastName = "";
        if (type == MCC_COIN) {
            mastName = getString(R.string.default_token_name).toLowerCase();
        } else if (type == OTHER_COIN) {
            mastName = getString(R.string.default_other_token_name).toLowerCase();
        } else if (type == DM_COIN) {
            mastName = "dm";
        } else if (type == TRX_COIN) {
            mastName = "trx";
        }
        for (int i = 0; i < list.size(); i++) {
            String name = list.get(i).getShort_name().toLowerCase();
            String num = assnum.get(name + "_" + address);
            if (TextUtils.isEmpty(num)) {
                
                num = assnum.get(name + "_" + address.toLowerCase());
            }
            list.get(i).setAssertsNum(num);
            list.get(i).setAssertsSumPrice("0.00");
            if (name.equals(mastName)) {
                BigDecimal bnum = new BigDecimal(num);
                BigDecimal bsum = bnum.multiply(mccprice).setScale(2, BigDecimal.ROUND_HALF_UP);
                mWallet.setmPrice(bsum.toPlainString());
                list.get(i).setAssertsSumPrice(bsum.toPlainString());
                sum = sum.add(bsum);
                mWallName.get(chooseWallet).setSumPrice(sum.toPlainString());
                heard.changeZichan(chooseWallet);
            } else {
                walletDBUtil.updateWalletAssets(list.get(i));
            }
        }
        String balance = assnum.get(mastName + "_" + address);
        if (TextUtils.isEmpty(balance)) {
            
            balance = assnum.get(mastName + "_" + address.toLowerCase());
        }
        mWallet.setmBalance(balance);
        walletDBUtil.updateWalletInfoByAddress(mWallet);
        mAdapter.bindData(list);
        mAdapter.notifyDataSetChanged();
    }

    private String getCoinList(List<AssertBean> list) {
        String conlist = "";
        GetBalBean bean = new GetBalBean();
        String allAddress = mWallet.getAllAddress();
        List<BanlanceBean> banlanceBeanList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            AssertBean assertBean = list.get(i);
            banlanceBeanList.add(new BanlanceBean(assertBean.getShort_name(), allAddress));
        }
        bean.setCclist(banlanceBeanList);
        conlist = gson.toJson(bean);
        return conlist;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK && data != null) {
            if (SettingPrefUtil.getNodeType(getActivity()) == 1) {
                String amountStr = data.getStringExtra("amountStr");
                String toAddress = data.getStringExtra("toAddress");
                String tokenName = data.getStringExtra("tokenName");
                LogUtil.d("" + tokenName);
                if (null == list || null == mustC) {
                    return;
                }
                AssertBean dex = WalletUtil.getdes(tokenName, list, mustC);
                WalletUtil.goTrans(getContext(), dex, amountStr, toAddress);
            } else {
                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setNegativeButton(getString(R.string.cancel), null).setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                intent = new Intent(getActivity(), ChooseNodeActivity.class);
                                intent.putExtra("type", 1);
                                startActivity(intent);
                            }
                        }).setMessage(getString(R.string.is_normal_not_transefer)).create();
                dialog.show();
            }
        }
    }

    
    private void loadPrice() {
        Map<String, Object> params2 = new TreeMap();
        meApi.getPrice(params2).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseEntity>(getActivity()) {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable.add(d);
                    }

                    @Override
                    public void onNexts(BaseEntity baseEntity) {
                        if (baseEntity.getStatus() == 1) {
                            InitPriceBean initPriceBean = gson.fromJson(gson.toJson(baseEntity.getData()), InitPriceBean.class);
                            if (null != initPriceBean.getDm()) {
                                dmprice = initPriceBean.getDm();
                            }
                            if (null != initPriceBean.getDm()) {
                                mccprice = initPriceBean.getMcc();
                            }
                        } else {
                            LogUtil.d(baseEntity.getInfo());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                });
    }

    private void getFilBanlance() {

        Map<String, Object> params = new TreeMap<>();
        params.put("jsonrpc", "2.0");
        params.put("method", "Filecoin.WalletBalance");
        params.put("id", 20);
        List<String> addr = new ArrayList<>();
        addr.add(mWallet.getAllAddress());
        params.put("params", addr);
        mFilApi.getBalance(new Gson().toJson(params)).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<FilBalanceBean>(getActivity()) {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable.add(d);
                    }

                    @Override
                    public void onNexts(FilBalanceBean baseEntity) {
                        if (baseEntity.getId() == 20) {
                            String remain = baseEntity.getResult();
                            if (remain == null) remain = "0";
                            BigDecimal bigDecimal = new BigDecimal(remain).divide(new BigDecimal(Math.pow(10, list.get(0).getDecimal())), 8, RoundingMode.HALF_UP);
                            list.get(0).setAssertsNum(bigDecimal.toPlainString());
                            mAdapter.notifyDataSetChanged();
                            mWallet.setmBalance(bigDecimal.toPlainString());
                            walletDBUtil.updateWalletInfoByAddress(mWallet);
                            heard.changeZichan(chooseWallet);
                            getCoinPrice();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        LogUtil.d(e.toString());
                    }
                });
    }

    private void getDogeBalance() {
        mDogeApi.getDogeBalance(new String(mWallet.getmPublicKey())).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<DogeBalanceBean>(getActivity()) {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable.add(d);
                    }

                    @Override
                    public void onNexts(DogeBalanceBean baseEntity) {
                        if (baseEntity != null) {
                            String remain = baseEntity.getBalance();
                            if (remain == null) remain = "0";
                            BigDecimal bigDecimal = new BigDecimal(remain).divide(new BigDecimal(Math.pow(10, list.get(0).getDecimal())), list.get(0).getDecimal(), RoundingMode.HALF_UP);
                            list.get(0).setAssertsNum(bigDecimal.toPlainString());
                            mAdapter.notifyDataSetChanged();
                            mWallet.setmBalance(bigDecimal.toPlainString());
                            walletDBUtil.updateWalletInfoByAddress(mWallet);
                            heard.changeZichan(chooseWallet);
                            getCoinPrice();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        LogUtil.d(e.toString());
                    }
                });
    }

    private void getDotBalance() {
        mDotApi.getDotBalance(new String(mWallet.getAllAddress())).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<DotBalanceBean>(getActivity()) {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable.add(d);
                    }

                    @Override
                    public void onNexts(DotBalanceBean baseEntity) {
                        if (baseEntity != null) {
                            String remain = baseEntity.getFree();
                            if (remain == null) remain = "0";
                            BigDecimal bigDecimal = new BigDecimal(remain).divide(new BigDecimal(Math.pow(10, list.get(0).getDecimal())), list.get(0).getDecimal(), RoundingMode.HALF_UP);
                            list.get(0).setAssertsNum(bigDecimal.toPlainString());
                            mAdapter.notifyDataSetChanged();
                            mWallet.setmBalance(bigDecimal.toPlainString());
                            walletDBUtil.updateWalletInfoByAddress(mWallet);
                            heard.changeZichan(chooseWallet);
                            getCoinPrice();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        LogUtil.d(e.toString());
                    }
                });
    }

    private void getLtcBalance() {
        mLtcApi.getLtcBalance(mWallet.getAllAddress()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<LtcBalanceBean>(getActivity()) {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable.add(d);
                    }

                    @Override
                    public void onNexts(LtcBalanceBean baseEntity) {
                        if (baseEntity != null) {
                            String remain = baseEntity.getBalance();
                            if (remain == null) remain = "0";
                            BigDecimal bigDecimal = new BigDecimal(remain).divide(new BigDecimal(Math.pow(10, list.get(0).getDecimal())), list.get(0).getDecimal(), RoundingMode.HALF_UP);
                            list.get(0).setAssertsNum(bigDecimal.toPlainString());
                            mAdapter.notifyDataSetChanged();
                            mWallet.setmBalance(bigDecimal.toPlainString());
                            walletDBUtil.updateWalletInfoByAddress(mWallet);
                            heard.changeZichan(chooseWallet);
                            getCoinPrice();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        LogUtil.d(e.toString());
                    }
                });
    }

    private void getBchBalance() {
        mBchApi.getBchBalance(new String(mWallet.getmPublicKey())).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BchBalanceBean>(getActivity()) {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable.add(d);
                    }

                    @Override
                    public void onNexts(BchBalanceBean baseEntity) {
                        if (baseEntity != null) {
                            String remain = baseEntity.getBalance();
                            if (remain == null) remain = "0";
                            BigDecimal bigDecimal = new BigDecimal(remain).divide(new BigDecimal(Math.pow(10, list.get(0).getDecimal())), list.get(0).getDecimal(), RoundingMode.HALF_UP);
                            list.get(0).setAssertsNum(bigDecimal.toPlainString());
                            mAdapter.notifyDataSetChanged();
                            mWallet.setmBalance(bigDecimal.toPlainString());
                            walletDBUtil.updateWalletInfoByAddress(mWallet);
                            heard.changeZichan(chooseWallet);
                            getCoinPrice();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        LogUtil.d(e.toString());
                    }
                });
    }

    private void getZecBalance() {
        mZecApi.getZecBalance(new String(mWallet.getmPublicKey())).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<ZecBalanceBean>(getActivity()) {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable.add(d);
                    }

                    @Override
                    public void onNexts(ZecBalanceBean baseEntity) {
                        if (baseEntity != null) {
                            String remain = baseEntity.getBalance();
                            if (remain == null) remain = "0";
                            BigDecimal bigDecimal = new BigDecimal(remain).divide(new BigDecimal(Math.pow(10, list.get(0).getDecimal())), list.get(0).getDecimal(), RoundingMode.HALF_UP);
                            list.get(0).setAssertsNum(bigDecimal.toPlainString());
                            mAdapter.notifyDataSetChanged();
                            mWallet.setmBalance(bigDecimal.toPlainString());
                            walletDBUtil.updateWalletInfoByAddress(mWallet);
                            heard.changeZichan(chooseWallet);
                            getCoinPrice();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        LogUtil.d(e.toString());
                    }
                });
    }

    private void getEtcBanlance() {
        Map<String, Object> params = new TreeMap<>();
        params.put("jsonrpc", "2.0");
        params.put("method", "eth_getBalance");
        params.put("id", 21);
        List<String> addr = new ArrayList<>();
        addr.add(mWallet.getAllAddress());
        addr.add("latest");
        params.put("params", addr);
        mEtcApi.getBalance(new Gson().toJson(params)).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<EtcBalanceBean>(getActivity()) {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable.add(d);
                    }

                    @Override
                    public void onNexts(EtcBalanceBean baseEntity) {
                        if (baseEntity.getId() == 21) {
                            String remain = baseEntity.getResult();
                            if (remain == null) remain = "0";
                            if (remain.startsWith("0x")) {
                                remain = remain.substring(2, remain.length());
                            }
                            if (StringUtils.isEmpty(remain)) remain = "0";
                            remain = "" + new BigInteger(remain, 16).toString(10);
                            BigDecimal bigDecimal = new BigDecimal(remain).divide(new BigDecimal(Math.pow(10, list.get(0).getDecimal())), 8, RoundingMode.HALF_UP);
                            list.get(0).setAssertsNum(bigDecimal.toPlainString());

                            mAdapter.notifyDataSetChanged();

                            mWallet.setmBalance(bigDecimal.toPlainString());
                            walletDBUtil.updateWalletInfoByAddress(mWallet);
                            heard.changeZichan(chooseWallet);
                            getCoinPrice();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        LogUtil.d(e.toString());
                    }
                });
    }

    private void getSgbBanlance() {
        ThreadManager.getNormalPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    LogUtil.d("");
                    AccountInfo accountInfo = WalletUtil.getDotSdk().getAccount().getAccountInfo(mWallet.getAllAddress());
                    BigDecimal bigDecimal = new BigDecimal(accountInfo.getFree()).divide(new BigDecimal(Math.pow(10, list.get(0).getDecimal())), list.get(0).getDecimal(), RoundingMode.HALF_UP);
                    list.get(0).setAssertsNum(bigDecimal.toPlainString());
                    list.get(0).setAssertsSumPrice("0.00");
                    LogUtil.d("" + bigDecimal.toPlainString());
                    mActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                            mWallet.setmBalance(bigDecimal.toPlainString());
                            walletDBUtil.updateWalletInfoByAddress(mWallet);
                            heard.changeZichan(chooseWallet);
                            mWallet.setSumPrice("0.00");
                            mWallName.get(chooseWallet).setSumPrice("0.00");
                            mAdapter.notifyDataSetChanged();
                            walletDBUtil.updateWalletInfoByAddress(mWallet);
                            heard.changeZichan(chooseWallet);
                        }
                    });

                } catch (Exception e) {
                    LogUtil.d("" + e.toString());
                }
            }
        });
    }

    private void getSolBanlance() {
        Map<String, Object> params = new TreeMap<>();
        params.put("jsonrpc", "2.0");
        params.put("method", "getBalance");
        params.put("id", 1);
        List<String> addr = new ArrayList<>();
        addr.add(mWallet.getAllAddress());
        params.put("params", addr);
        mSolApi.getBalance(new Gson().toJson(params)).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<SolBaseBean>(getActivity()) {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable.add(d);
                    }

                    @Override
                    public void onNexts(SolBaseBean baseEntity) {
                        if (baseEntity.getId() == 1) {
                            SolBalanceBean balanceBean = gson.fromJson(baseEntity.getResult(), SolBalanceBean.class);
                            String remain = balanceBean.getValue().toPlainString();
                            if (TextUtils.isEmpty(remain)) remain = "0";
                            BigDecimal bigDecimal = new BigDecimal(remain).divide(new BigDecimal(Math.pow(10, list.get(0).getDecimal())), 8, RoundingMode.HALF_UP);
                            list.get(0).setAssertsNum(bigDecimal.toPlainString());

                            mAdapter.notifyDataSetChanged();

                            mWallet.setmBalance(bigDecimal.toPlainString());
                            walletDBUtil.updateWalletInfoByAddress(mWallet);
                            heard.changeZichan(chooseWallet);
                            getCoinPrice();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        LogUtil.d(e.toString());
                    }
                });
    }

    private void getPancakeSwapPrice(AssertBean assertBean) {
        rpcApi.getPancakeSwapUsdtPrice(assertBean).subscribe(new Observer<CoinPriceBean>() {
            @Override
            public void onSubscribe(Disposable d) {
                mDisposable.add(d);
            }

            @Override
            public void onNext(CoinPriceBean priceBean) {
                String price = priceBean.price;
                if (!TextUtils.isEmpty(price)) {
                    assertBean.setPrice(price);
                    
                    String remain = assertBean.getAssertsNum();
                    
                    BigDecimal amount = new BigDecimal(remain).multiply(new BigDecimal(price)).setScale(2, RoundingMode.HALF_UP);
                    assertBean.setAssertsSumPrice(amount.toPlainString());
                    if (!TextUtils.isEmpty(assertBean.getContract())) {
                        walletDBUtil.updateWalletAssets(assertBean);
                    }
                    calculateAllAmount();
                }
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
            }
        });
    }

    
    private void calculateAllAmount() {
        BigDecimal sum = new BigDecimal(0);
        for (AssertBean assertBean : list) {
            String assertSum = assertBean.getAssertsSumPrice();
            if (!TextUtils.isEmpty(assertSum)) {
                sum = sum.add(new BigDecimal(assertSum));
            }
        }
        String price = sum.setScale(2, RoundingMode.HALF_UP).toPlainString();
        mWallet.setSumPrice(price);
        mWallName.get(chooseWallet).setSumPrice(price);
        mAdapter.notifyDataSetChanged();
        walletDBUtil.updateWalletInfoByAddress(mWallet);
        heard.changeZichan(chooseWallet);
    }


    private void getCoinPrice() {
        if (true) {
            return;
        }
        List<String> idList = getCoinId();
        Map<String, Object> params = new TreeMap<>();
        params.put("currency", curCoin);
        params.put("assets", idList);
        priceApi.getPrice(gson.toJson(params)).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<MarketPriceBean>(getActivity()) {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable.add(d);
                    }

                    @Override
                    public void onNexts(MarketPriceBean baseEntity) {
                        List<MarketPriceBean.TickersBean> tickers = baseEntity.getTickers();
                        BigDecimal sum = new BigDecimal("0");

                        for (int i = 0; i < tickers.size(); i++) {
                            MarketPriceBean.TickersBean mBean = tickers.get(i);
                            String id = mBean.getId();
                            BigDecimal price = new BigDecimal(mBean.getPrice());
                            for (int j = 0; j < list.size(); j++) {
                                String coinaddress = list.get(j).getContract();
                                String remain = list.get(j).getAssertsNum();
                                if (TextUtils.isEmpty(coinaddress)) {
                                    if (id.indexOf("_") < 0) {
                                        BigDecimal amount = new BigDecimal(remain).multiply(price);
                                        sum = sum.add(amount);
                                        String p = amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
                                        list.get(j).setAssertsSumPrice(p);
                                        mWallet.setmPrice(p);
                                    }
                                } else {
                                    if (mWallet.getType() == TRX_COIN) {
                                        BigDecimal asprice = list.get(j).getTokenPriceInTrx().multiply(price);
                                        BigDecimal amount = new BigDecimal(remain).multiply(asprice);
                                        String p = amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
                                        list.get(j).setAssertsSumPrice(p);
                                        sum = sum.add(amount);
                                        walletDBUtil.updateWalletAssets(list.get(j));
                                    } else {
                                        if (coinaddress.startsWith("0x")) {
                                            coinaddress = coinaddress.substring(2);
                                        }
                                        if (id.endsWith(coinaddress)) {
                                            BigDecimal amount = new BigDecimal(remain).multiply(price);
                                            String p = amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
                                            list.get(j).setAssertsSumPrice(p);
                                            sum = sum.add(amount);
                                            walletDBUtil.updateWalletAssets(list.get(j));
                                        }
                                    }
                                }
                            }
                        }

                        String price = sum.setScale(2, RoundingMode.HALF_UP).toPlainString();
                        mWallet.setSumPrice(price);
                        mWallName.get(chooseWallet).setSumPrice(price);
                        mAdapter.notifyDataSetChanged();
                        walletDBUtil.updateWalletInfoByAddress(mWallet);
                        heard.changeZichan(chooseWallet);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        Log.v("fsfesfsefsefes", e.getMessage() + "---");
                        LogUtil.d(e.toString());
                    }
                });
    }

    private List<String> getCoinId() {
        List<String> coinList = new ArrayList<>();
        String mustCoinId = "";
        int walletType = mWallet.getType();
        if (walletType == FIL_COIN) {
            mustCoinId = "c" + CoinType.FILECOIN.value();
        } else if (walletType == DOGE_COIN) {
            mustCoinId = "c" + CoinType.DOGECOIN.value();
        } else if (walletType == DOT_COIN) {
            mustCoinId = "c" + CoinType.POLKADOT.value();
        } else if (walletType == LTC_COIN) {
            mustCoinId = "c" + CoinType.LITECOIN.value();
        } else if (walletType == BCH_COIN) {
            mustCoinId = "c" + CoinType.BITCOINCASH.value();
        } else if (walletType == ZEC_COIN) {
            mustCoinId = "c" + CoinType.ZCASH.value();
        } else if (walletType == ETC_COIN) {
            mustCoinId = "c" + CoinType.ETHEREUMCLASSIC.value();
        } else if (walletType == XRP_COIN) {
            mustCoinId = "c" + CoinType.XRP.value();
        } else if (walletType == BTC_COIN) {
            mustCoinId = "c" + CoinType.BITCOIN.value();
        } else if (walletType == TRX_COIN) {
            mustCoinId = "c" + CoinType.TRON.value();
        } else if (walletType == ETH_COIN) {
            mustCoinId = "c" + CoinType.ETHEREUM.value();
        } else if (walletType == BNB_COIN) {
            mustCoinId = "c" + CoinType.SMARTCHAIN.value();
        } else if (walletType == HT_COIN) {
            mustCoinId = "c" + CoinType.ECOCHAIN.value();
        } else if (walletType == SOL_COIN) {
            mustCoinId = "c" + CoinType.SOLANA.value();
        } else if (walletType == MATIC_COIN) {
            mustCoinId = "c" + CoinType.POLYGON.value();
        }
        if (walletType != TRX_COIN) {
            for (int i = 0; i < list.size(); i++) {
                String address = list.get(i).getContract();
                if (!TextUtils.isEmpty(address)) {
                    address = "_t" + address;
                }
                coinList.add(mustCoinId + address);
            }
        } else {
            coinList.add(mustCoinId);
        }

        return coinList;
    }


    @Override
    public void onDestroyView() {
        if (null != mTimerDisposable) {
            mTimerDisposable.dispose();
            mTimerDisposable = null;
        }
        super.onDestroyView();
    }

    private CompositeDisposable mTimerDisposable;

    private void checkNodeState() {
        if (null == mTimerDisposable) {
            mTimerDisposable = new CompositeDisposable();
        }
        mTimerDisposable.clear();
        if (null == mWallet) {
            return;
        }

        
        Observable.interval(0, 10, TimeUnit.SECONDS)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mTimerDisposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull Long aLong) {
                        if (null == mWallet) {
                            if (null != mTimerDisposable) {
                                mTimerDisposable.clear();
                            }
                            return;
                        }
                        String nodeUrl = SpUtil.getDefNode(mWallet.getType());
                        if (!TextUtils.isEmpty(nodeUrl)) {
                            long pingTime = NetWorkUtils.pingIpAddress2(nodeUrl);
                            if (null != leftTv) {
                                leftTv.post(() -> {
                                    if (pingTime > 0 && pingTime < 500) {
                                        
                                        leftTv.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null,
                                                ContextCompat.getDrawable(mActivity, R.drawable.circle_green_c10), null);
                                    } else if (pingTime > 500) {
                                        
                                        leftTv.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null,
                                                ContextCompat.getDrawable(mActivity, R.drawable.circle_yellow_c10), null);
                                    } else if (pingTime < 0) {
                                        
                                        leftTv.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null,
                                                ContextCompat.getDrawable(mActivity, R.drawable.circle_red_c10), null);
                                    } else {
                                        leftTv.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
                                    }
                                });
                            }
                        }

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }
}
