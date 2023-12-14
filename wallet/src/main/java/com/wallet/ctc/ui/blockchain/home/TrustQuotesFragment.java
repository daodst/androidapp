

package com.wallet.ctc.ui.blockchain.home;

import static com.wallet.ctc.crypto.WalletUtil.BCH_COIN;
import static com.wallet.ctc.crypto.WalletUtil.BNB_COIN;
import static com.wallet.ctc.crypto.WalletUtil.BTC_COIN;
import static com.wallet.ctc.crypto.WalletUtil.DOGE_COIN;
import static com.wallet.ctc.crypto.WalletUtil.DOT_COIN;
import static com.wallet.ctc.crypto.WalletUtil.ETC_COIN;
import static com.wallet.ctc.crypto.WalletUtil.ETH_COIN;
import static com.wallet.ctc.crypto.WalletUtil.FIL_COIN;
import static com.wallet.ctc.crypto.WalletUtil.HT_COIN;
import static com.wallet.ctc.crypto.WalletUtil.LTC_COIN;
import static com.wallet.ctc.crypto.WalletUtil.MATIC_COIN;
import static com.wallet.ctc.crypto.WalletUtil.SOL_COIN;
import static com.wallet.ctc.crypto.WalletUtil.TRX_COIN;
import static com.wallet.ctc.crypto.WalletUtil.XRP_COIN;
import static com.wallet.ctc.crypto.WalletUtil.ZEC_COIN;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.api.blockchain.MarketPriceApi;
import com.wallet.ctc.base.BaseFragment;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.model.blockchain.MarketPriceBean;
import com.wallet.ctc.util.LogUtil;
import com.wallet.ctc.util.WalletSpUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import common.app.base.fragment.mall.catcherror.BaseSubscriber;
import common.app.ui.view.PullToRefreshLayout;
import common.app.ui.view.PullableListView;
import common.app.utils.SpUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import wallet.core.jni.CoinType;


public class TrustQuotesFragment extends BaseFragment {

    protected Unbinder mUnbinder;

    @BindView(R2.id.nodata)
    LinearLayout nodata;
    @BindView(R2.id.content_view)
    PullableListView quotesList;
    @BindView(R2.id.refresh_view)
    PullToRefreshLayout refreshView;
    private TrustQuotesListAdapter mAdapter;
    private List<MarketPriceBean.TickersBean> list = new ArrayList<>();
    private Gson gson = new Gson();
    private MarketPriceApi mApi = new MarketPriceApi();
    private String dcu;
    private CompositeDisposable mDisposable;
    private WalletDBUtil walletDBUtil;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quotes, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mAdapter = new TrustQuotesListAdapter(getActivity());
        dcu = SpUtil.getDcu();
        mDisposable = new CompositeDisposable();
        mAdapter.bindType(dcu);
        mAdapter.bindData(list);
        quotesList.setAdapter(mAdapter);
        walletDBUtil = new WalletDBUtil(getActivity());
        refreshView.onlyPullDown();
        refreshView.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {

                getData();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                getData();
            }
        });
        getData();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    
    @Override
    public void getData() {
        List<String> idList = getCoinId();
        Map<String, Object> params = new TreeMap<>();
        params.put("currency", dcu);
        params.put("assets", idList);
        mApi.getPrice(gson.toJson(params)).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<MarketPriceBean>(getActivity()) {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable.add(d);
                    }

                    @Override
                    public void onNexts(MarketPriceBean baseEntity) {
                        refreshView.refreshFinish(PullToRefreshLayout.SUCCEED);
                        list.clear();
                        List<MarketPriceBean.TickersBean> tickers = baseEntity.getTickers();
                        for (int i = 0; i < tickers.size(); i++) {
                            tickers.get(i).setCoin(getCoin(tickers.get(i).getId()));
                        }
                        list.addAll(tickers);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        refreshView.refreshFinish(PullToRefreshLayout.FAIL);
                        Log.v("fsfesfsefsefes", e.getMessage() + "---");
                        LogUtil.d(e.toString());
                    }
                });
    }

    public void setRMBType(String type) {
        if (null != mAdapter) {
            dcu = type;
            mAdapter.bindType(type);
            getData();
        }
    }

    private List<String> getCoinId() {
        List<String> coinList = new ArrayList<>();
        if (WalletSpUtil.getEnableFIL() == 1) {
            coinList.add("c" + CoinType.FILECOIN.value());
        }
        if (WalletSpUtil.getEnableDOGE() == 1) {
            coinList.add("c" + CoinType.DOGECOIN.value());
        }
        if (WalletSpUtil.getEnableDOT() == 1) {
            coinList.add("c" + CoinType.POLKADOT.value());
        }
        if (WalletSpUtil.getEnableLTC() == 1) {
            coinList.add("c" + CoinType.LITECOIN.value());
        }
        if (WalletSpUtil.getEnableBCH() == 1) {
            coinList.add("c" + CoinType.BITCOINCASH.value());
        }
        if (WalletSpUtil.getEnableZEC() == 1) {
            coinList.add("c" + CoinType.ZCASH.value());
        }
        if (WalletSpUtil.getEnableETC() == 1) {
            coinList.add("c" + CoinType.ETHEREUMCLASSIC.value());
        }
        if (WalletSpUtil.getEnableXrp() == 1) {
            coinList.add("c" + CoinType.XRP.value());
        }
        if (WalletSpUtil.getEnableBtc() == 1) {
            coinList.add("c" + CoinType.BITCOIN.value());
        }
        if (WalletSpUtil.getEnableTrx() == 1) {
            coinList.add("c" + CoinType.TRON.value());
        }
        if (WalletSpUtil.getEnableEth() == 1) {
            coinList.add("c" + CoinType.ETHEREUM.value());
        }
        if (WalletSpUtil.getEnableBnb() == 1) {
            coinList.add("c" + CoinType.SMARTCHAIN.value());
        }
        if (WalletSpUtil.getEnableHt() == 1) {
            coinList.add("c" + CoinType.ECOCHAIN.value());
        }
        if (WalletSpUtil.getEnableSOL() == 1) {
            coinList.add("c" + CoinType.SOLANA.value());
        }
        if (WalletSpUtil.getEnableMATIC() == 1) {
            coinList.add("c" + CoinType.POLYGON.value());
        }
        return coinList;
    }

    private String getCoin(String coinId) {
        int id;
        if (coinId.startsWith("c")) {
           coinId= coinId.replaceAll("c", "");
        }

        id = new BigDecimal(coinId).intValue();
        if (id == CoinType.FILECOIN.value()) {
            id = FIL_COIN;
        } else if (id == CoinType.DOGECOIN.value()) {
            id = DOGE_COIN;
        } else if (id == CoinType.POLKADOT.value()) {
            id = DOT_COIN;
        } else if (id == CoinType.LITECOIN.value()) {
            id = LTC_COIN;
        } else if (id == CoinType.BITCOINCASH.value()) {
            id = BCH_COIN;
        } else if (id == CoinType.ZCASH.value()) {
            id = ZEC_COIN;
        } else if (id == CoinType.ETHEREUMCLASSIC.value()) {
            id = ETC_COIN;
        } else if (id == CoinType.XRP.value()) {
            id = XRP_COIN;
        } else if (id == CoinType.BITCOIN.value()) {
            id = BTC_COIN;
        } else if (id == CoinType.TRON.value()) {
            id = TRX_COIN;
        } else if (id == CoinType.ETHEREUM.value()) {
            id = ETH_COIN;
        } else if (id == CoinType.SMARTCHAIN.value()) {
            id = BNB_COIN;
        } else if (id == CoinType.ECOCHAIN.value()) {
            id = HT_COIN;
        } else if (id == CoinType.SOLANA.value()) {
            id = SOL_COIN;
        } else if (id == CoinType.POLYGON.value()) {
            id = MATIC_COIN;
        }
        coinId = walletDBUtil.getMustWallet(id).get(0).getShort_name();
        return coinId;
    }
}
