

package com.wallet.ctc.api.blockchain;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.wallet.ctc.R;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.BandWidthBean;
import com.wallet.ctc.model.blockchain.BanlanceBean;
import com.wallet.ctc.model.blockchain.BaseBanlanceBean;
import com.wallet.ctc.model.blockchain.BaseTrxBanlanceBean;
import com.wallet.ctc.model.blockchain.BchBalanceBean;
import com.wallet.ctc.model.blockchain.BtcBanlanceBean;
import com.wallet.ctc.model.blockchain.CoinPriceCont;
import com.wallet.ctc.model.blockchain.DogeBalanceBean;
import com.wallet.ctc.model.blockchain.DotBalanceBean;
import com.wallet.ctc.model.blockchain.ETHBanlanceBean;
import com.wallet.ctc.model.blockchain.EtcBalanceBean;
import com.wallet.ctc.model.blockchain.EthAssertBean;
import com.wallet.ctc.model.blockchain.FilBalanceBean;
import com.wallet.ctc.model.blockchain.GetBalBean;
import com.wallet.ctc.model.blockchain.LtcBalanceBean;
import com.wallet.ctc.model.blockchain.MarketPriceBean;
import com.wallet.ctc.model.blockchain.NewAssertBean;
import com.wallet.ctc.model.blockchain.RpcApi;
import com.wallet.ctc.model.blockchain.XrpAssertBean;
import com.wallet.ctc.model.blockchain.XrpTokenBalanceBean;
import com.wallet.ctc.model.blockchain.ZecBalanceBean;
import com.wallet.ctc.ui.blockchain.home.NewHomeFragment;
import com.wallet.ctc.ui.me.about.StringUtils;
import com.wallet.ctc.util.LogUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import common.app.AppApplication;
import common.app.base.BaseViewModel;
import common.app.base.model.http.callback.ApiNetResponse;
import common.app.utils.SpUtil;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import wallet.core.jni.CoinType;

public class BlockViewModel extends BaseViewModel {
    BlockApiRepository mRepository;
    MccApiRepository mMccRepository;
    DmApiRepository mDmRepository;
    OtherApiRepository mOtherRepository;
    TrxApiRepository mTrxApiRepository;
    FilApiRepository mFilApiRepository;
    DogeApiRepository mDogeApiRepository;
    DotApiRepository mDotApiRepository;
    LtcApiRepository mLtcApiRepository;
    BchApiRepository mBchApiRepository;
    ZecApiRepository mZecApiRepository;
    EtcApiRepository mEtcApiRepository;
    MarketPriceApiRepository mMarketPriceApi;
    WalletDBUtil walletDBUtil;
    RpcApi mRpcApi;

    
    public BlockViewModel(@NonNull Application application) {
        super(application);
        mRepository = BlockApiRepository.getInstance();
        mMccRepository = MccApiRepository.getInstance();
        mDmRepository = DmApiRepository.getInstance();
        mOtherRepository = OtherApiRepository.getInstance();
        mTrxApiRepository = TrxApiRepository.getInstance();
        mFilApiRepository=FilApiRepository.getInstance();
        mDogeApiRepository=DogeApiRepository.getInstance();
        mDotApiRepository=DotApiRepository.getInstance();
        mLtcApiRepository=LtcApiRepository.getInstance();
        mBchApiRepository=BchApiRepository.getInstance();
        mZecApiRepository=ZecApiRepository.getInstance();
        mEtcApiRepository=EtcApiRepository.getInstance();
        mMarketPriceApi=MarketPriceApiRepository.getInstance();
        walletDBUtil = WalletDBUtil.getInstent(application);
        getBanlance = new MutableLiveData();
        seachAssets= new MutableLiveData();
        seachEthAssets= new MutableLiveData();
        seachXrpAssets= new MutableLiveData();
        showToastLD = new MutableLiveData<>();
        mRpcApi = new RpcApi();
    }


    public MutableLiveData<BaseBanlanceBean> getBanlance;
    private boolean ethBanlance = false;
    List<AssertBean> list;

    
    public void getBanlance(WalletEntity mWallet) {
        ethBanlance = false;
        sum = new BigDecimal("0");
        list = walletDBUtil.getMustWallet(mWallet.getType());
        list.addAll(walletDBUtil.getAssetsByWalletType(mWallet.getAllAddress(), mWallet.getType()));
        int type = mWallet.getType();
        if (type == WalletUtil.ETH_COIN || type == WalletUtil.ETF_COIN || type == WalletUtil.DMF_COIN || type == WalletUtil.DMF_BA_COIN || type == WalletUtil.HT_COIN || type == WalletUtil.BNB_COIN) {
            ethBanlance = true;
            getEthBanlance(0, mWallet);
        } else if (type == WalletUtil.BTC_COIN) {
            getBTCBanlance(mWallet, list);
        } else if (type == WalletUtil.EOS_COIN) {
        } else if (type == WalletUtil.MCC_COIN) {
            getMccBanlance(mWallet, list);
        } else if (type == WalletUtil.DM_COIN) {
            getDmBanlance(mWallet, list);
        } else if (type == WalletUtil.OTHER_COIN) {
            getOtherBanlance(mWallet, list);
        } else if (type == WalletUtil.XRP_COIN) {
            getXrpBanlance(mWallet, list);
        } else if (type == WalletUtil.TRX_COIN) {
            getTrxBanlance(mWallet, list);
        } else if (type == WalletUtil.FIL_COIN) {
            getFilBanlance(mWallet, list);
        } else if (type == WalletUtil.DOGE_COIN) {
            getDogeBalance(mWallet, list);
        } else if (type == WalletUtil.DOT_COIN) {
            getDotBalance(mWallet, list);
        } else if (type == WalletUtil.LTC_COIN) {
            getLtcBalance(mWallet, list);
        } else if (type == WalletUtil.BCH_COIN) {
            getBchBalance(mWallet, list);
        } else if (type == WalletUtil.ZEC_COIN) {
            getZecBalance(mWallet, list);
        } else if (type == WalletUtil.ETC_COIN) {
            getEtcBanlance(mWallet, list);
        } else {
            LogUtil.d("" + type);
        }
    }

    private BigDecimal sum;

    
    private void getEthBanlance(int position, WalletEntity mWallet) {
        if (!ethBanlance) {
            return;
        }
        int type = mWallet.getType();
        Map<String, Object> params = new TreeMap();
        params.put("addr", mWallet.getAllAddress());
        params.put("contract_addr", list.get(position).getContract());
        Map<String, Object> header = new TreeMap<>();
        header.put("chain_rpc_url", SpUtil.getDefNode(type));
        putChainType(type,params);
        mRepository.getBanlance(params, header, new ApiNetResponse<ETHBanlanceBean>(this, true) {
            @Override
            public void onSuccess(ETHBanlanceBean bean) {
                if (!ethBanlance) {
                    return;
                }
                list.get(position).setAssertsNum(bean.getRemain().toPlainString());
                list.get(position).setAssertsSumPrice(bean.getSumPrice().toPlainString());
                if (TextUtils.isEmpty(list.get(position).getContract())) {
                    mWallet.setmBalance(bean.getRemain().setScale(8, BigDecimal.ROUND_HALF_UP).toPlainString());
                    mWallet.setmPrice(bean.getSumPrice().setScale(8, BigDecimal.ROUND_HALF_UP).toPlainString());
                    walletDBUtil.updateWalletInfoByAddress(mWallet);
                } else {
                    walletDBUtil.updateWalletAssets(list.get(position));
                }
                sum = sum.add(bean.getSumPrice());
                
                if (position == list.size() - 1) {
                    mWallet.setSumPrice(sum.toPlainString());
                    walletDBUtil.updateWalletInfoByAddress(mWallet);
                    BaseBanlanceBean banlanceBean = new BaseBanlanceBean();
                    banlanceBean.setAssertBeans(list);
                    banlanceBean.setWalletEntity(mWallet);
                    getBanlance.postValue(banlanceBean);
                } else {
                    int po = 1 + position;
                    getEthBanlance(po, mWallet);
                }
            }

            @Override
            public void onFaile(String info, ETHBanlanceBean data, Throwable throwable) {
                getBanlance.postValue(null);
            }
        });
    }

    
    private void getMccBanlance(WalletEntity mWallet, List<AssertBean> list) {
        String coinlist = getCoinList(mWallet, list);
        Map<String, Object> params = new TreeMap();
        params.put("arg", coinlist);
        mMccRepository.getBanlance(params, new ApiNetResponse<Map<String, String>>(this, true) {
            @Override
            public void onSuccess(Map<String, String> bean) {
                showBanlance(mWallet, list, bean);
            }
        });
    }

    
    private void getDmBanlance(WalletEntity mWallet, List<AssertBean> list) {
        String coinlist = getCoinList(mWallet, list);
        Map<String, Object> params = new TreeMap();
        params.put("arg", coinlist);
        mDmRepository.getBanlance(params, new ApiNetResponse<Map<String, String>>(this, true) {
            @Override
            public void onSuccess(Map<String, String> bean) {
                showBanlance(mWallet, list, bean);
            }
        });
    }

    
    private void getOtherBanlance(WalletEntity mWallet, List<AssertBean> list) {
        String coinlist = getCoinList(mWallet, list);
        Map<String, Object> params = new TreeMap();
        params.put("arg", coinlist);
        mOtherRepository.getBanlance(params, new ApiNetResponse<Map<String, String>>(this, true) {
            @Override
            public void onSuccess(Map<String, String> bean) {
                showBanlance(mWallet, list, bean);
            }
        });
    }

    private String getCoinList(WalletEntity mWallet, List<AssertBean> list) {
        String conlist = "";
        GetBalBean bean = new GetBalBean();
        String allAddress = mWallet.getAllAddress();
        List<BanlanceBean> banlanceBeanList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            AssertBean assertBean = list.get(i);
            banlanceBeanList.add(new BanlanceBean(assertBean.getShort_name(), allAddress));
        }
        bean.setCclist(banlanceBeanList);
        conlist = new Gson().toJson(bean);
        return conlist;
    }
    
    private void getBTCBanlance(WalletEntity mWallet, List<AssertBean> list) {
        Map<String, Object> params = new TreeMap();
        params.put("addr", mWallet.getAllAddress());
        mRepository.getBtcBanlance(params, new ApiNetResponse<BtcBanlanceBean>(this, true) {
            @Override
            public void onSuccess(BtcBanlanceBean mBean) {
                BigDecimal bigDecimal = new BigDecimal(mBean.getRemain()).multiply(new BigDecimal(mBean.getCprice()));
                list.get(0).setAssertsNum(mBean.getRemain());
                list.get(0).setAssertsSumPrice(bigDecimal.toPlainString());
                mWallet.setSumPrice(bigDecimal.toPlainString());
                mWallet.setmBalance(mBean.getRemain());
                mWallet.setmPrice(bigDecimal.toPlainString());
                walletDBUtil.updateWalletInfoByAddress(mWallet);
                getUsdtBanlance(mWallet, list);
            }
        });
    }
    
    private void getUsdtBanlance(WalletEntity mWallet, List<AssertBean> list) {
        Map<String, Object> params = new TreeMap();
        params.put("addr", mWallet.getAllAddress());
        mRepository.getBtcUsdtBanlance(params, new ApiNetResponse<BtcBanlanceBean>(this, true) {
            @Override
            public void onSuccess(BtcBanlanceBean mBean) {
                BigDecimal bigDecimal = new BigDecimal(mBean.getRemain()).multiply(new BigDecimal(mBean.getCprice()));
                list.get(1).setAssertsNum(mBean.getRemain());
                list.get(1).setAssertsSumPrice(bigDecimal.toPlainString());
                BigDecimal sum = new BigDecimal(mWallet.getSumPrice()).add(bigDecimal);
                mWallet.setSumPrice(sum.toPlainString());
                walletDBUtil.updateWalletInfoByAddress(mWallet);
                BaseBanlanceBean banlanceBean = new BaseBanlanceBean();
                banlanceBean.setAssertBeans(list);
                banlanceBean.setWalletEntity(mWallet);
                getBanlance.postValue(banlanceBean);
            }
        });
    }
    
    private void getXrpBanlance(WalletEntity mWallet, List<AssertBean> list) {
        Map<String, Object> params = new TreeMap();
        params.put("addr", mWallet.getAllAddress());
        mRepository.getXrpBanlance(params, new ApiNetResponse<ETHBanlanceBean>(this, true) {
            @Override
            public void onSuccess(ETHBanlanceBean bean) {
                list.get(0).setAssertsNum(bean.getRemain().toPlainString());
                list.get(0).setAssertsSumPrice(bean.getSumPrice().toPlainString());
                mWallet.setmBalance(bean.getRemain().toPlainString());
                mWallet.setmPrice(bean.getSumPrice().toPlainString());
                sum = sum.add(bean.getSumPrice());
                mWallet.setSumPrice(sum.toPlainString());
                walletDBUtil.updateWalletInfoByAddress(mWallet);
                if (list.size() > 1) {
                    getXrpTokenBalance(mWallet, list, 1);
                } else {
                    BaseBanlanceBean banlanceBean = new BaseBanlanceBean();
                    banlanceBean.setAssertBeans(list);
                    banlanceBean.setWalletEntity(mWallet);
                    getBanlance.postValue(banlanceBean);
                }
            }
        });
    }

    private final String KEY_NO_TRUST = "noTrust";
    private final String KEY_HAS_TRUST = "hasTrust";
    
    private void getXrpTokenBalance(WalletEntity mWallet, List<AssertBean> list, int position) {
        AssertBean bean = list.get(position);
        if (null == bean || TextUtils.isEmpty(bean.getContract()) || TextUtils.isEmpty(bean.getShort_name())) {
            LogUtil.w("xrp   ");
            return;
        }
        
        Map<String, Object> params = new TreeMap();
        params.put("addr", mWallet.getAllAddress());
        params.put("issuer", bean.getContract());
        params.put("currency", bean.getShort_name());
        mRepository.getXrpTokenBanlance(params, new ApiNetResponse<List<XrpTokenBalanceBean>>(this, true) {
            @Override
            public void onSuccess(List<XrpTokenBalanceBean> beans) {
                if (null == beans || beans.size() == 0) {
                    LogUtil.w("xrp " + bean.getShort_name() + "");
                    list.get(position).setDesc(KEY_NO_TRUST);
                    return;
                }

                list.get(position).setDesc(KEY_HAS_TRUST);
                XrpTokenBalanceBean balanceBean = beans.get(0);

                
                if (!TextUtils.isEmpty(CoinPriceCont.getCPrice(balanceBean.currency))) {
                    balanceBean.setCprice(CoinPriceCont.getCPrice(balanceBean.currency));
                }
                sum = sum.add(balanceBean.getSumPrice());
                list.get(position).setAssertsNum(balanceBean.balance);
                list.get(position).setAssertsSumPrice(balanceBean.getSumPrice().toPlainString());
                sum.add(balanceBean.getSumPrice());
                walletDBUtil.updateWalletAssets(list.get(position));
                if (position == list.size() - 1) {
                    BaseBanlanceBean banlanceBean = new BaseBanlanceBean();
                    banlanceBean.setAssertBeans(list);
                    banlanceBean.setWalletEntity(mWallet);
                    getBanlance.postValue(banlanceBean);
                } else {
                    int po = 1 + position;
                    getXrpTokenBalance(mWallet, list, po);
                }
            }
        });
    }

    
    private void getTrxBanlance(WalletEntity mWallet, List<AssertBean> list) {
        Map<String, Object> params = new TreeMap<>();
        params.put("address", mWallet.getAllAddress());
        mTrxApiRepository.getBanlance(params, new ApiNetResponse<BaseTrxBanlanceBean>(this, true) {
            @Override
            public void onSuccess(BaseTrxBanlanceBean baseEntity) {
                if (null != baseEntity.getBandwidth()) {
                    BandWidthBean mBean = baseEntity.getBandwidth();
                    LogUtil.d("" + mBean.getEnergyRemaining() + "");
                    mWallet.setEnergy(mBean.getEnergyRemaining().setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
                    mWallet.setBroadband(mBean.getFreeNetRemaining().toPlainString());
                    mWallet.setEnergyMax(mBean.getEnergyLimit().setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
                    mWallet.setBroadbandMax(mBean.getFreeNetLimit().toPlainString());
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
                    BigDecimal sum = new BigDecimal("0");
                    for (int position = 0; position < list.size(); position++) {
                        list.get(position).setAssertsNum("0.00");
                        list.get(position).setAssertsSumPrice("0.00");
                        for (int j = 0; j < baseEntity.getTokens().size(); j++) {
                            String tokenaddress = baseEntity.getTokens().get(j).getTokenId();
                            String tokenName = baseEntity.getTokens().get(j).getTokenAbbr().toLowerCase();
                            BigDecimal tokenBalance = new BigDecimal(baseEntity.getTokens().get(j).getBalance());
                            tokenBalance = tokenBalance.divide(new BigDecimal(baseEntity.getTokens().get(j).getTokenDecimal()), 2, BigDecimal.ROUND_HALF_UP);
                            
                            if ((TextUtils.isEmpty(tokenaddress) || tokenaddress.length() < 5) && tokenName.equals(list.get(position).getShort_name().toLowerCase())) {
                                list.get(position).setAssertsNum(tokenBalance.toPlainString());
                                mWallet.setmBalance(tokenBalance.toPlainString());
                                break;
                            } else if (!TextUtils.isEmpty(tokenaddress) && tokenaddress.toLowerCase().equals(list.get(position).getContract().toLowerCase())) {
                                list.get(position).setAssertsNum(tokenBalance.toPlainString());
                                BigDecimal bigDecimal = new BigDecimal("0");
                                if (list.get(position).getShort_name().toUpperCase().equals("USDT")) {
                                }
                                list.get(position).setAssertsSumPrice(bigDecimal.toPlainString());
                                mWallet.setmPrice(bigDecimal.toPlainString());
                                sum = sum.add(bigDecimal);
                                walletDBUtil.updateWalletAssets(list.get(position));
                                break;
                            }
                        }
                    }
                    LogUtil.d(list.toString());
                    mWallet.setSumPrice(sum.toPlainString());
                    walletDBUtil.updateWalletInfoByAddress(mWallet);

                }
            }
        });
    }

    private void getFilBanlance(WalletEntity mWallet, List<AssertBean> list) {
        Map<String, Object> params = new TreeMap<>();
        params.put("jsonrpc", "2.0");
        params.put("method", "Filecoin.WalletBalance");
        params.put("id", 20);
        List<String> addr = new ArrayList<>();
        addr.add(mWallet.getAllAddress());
        params.put("params", addr);
        mFilApiRepository.getBanlance(params, new ApiNetResponse<FilBalanceBean>(this, true) {
            @Override
            public void onSuccess(FilBalanceBean baseEntity) {
                if (baseEntity.getId() == 20) {
                    String remain = baseEntity.getResult();
                    if (remain == null) remain = "0";
                    BigDecimal bigDecimal = new BigDecimal(remain).divide(new BigDecimal(Math.pow(10, list.get(0).getDecimal())), 8, RoundingMode.HALF_UP);
                    list.get(0).setAssertsNum(bigDecimal.toPlainString());
                    mWallet.setmBalance(bigDecimal.toPlainString());
                    walletDBUtil.updateWalletInfoByAddress(mWallet);
                    getCoinPrice(mWallet,list,"" + CoinType.FILECOIN.value(), bigDecimal.toPlainString());
                }
            }
        });
    }

    private void getDogeBalance(WalletEntity mWallet, List<AssertBean> list) {
        mDogeApiRepository.getBanlance(new String(mWallet.getmPublicKey()), new ApiNetResponse<DogeBalanceBean>(this, true) {
            @Override
            public void onSuccess(DogeBalanceBean baseEntity) {
                String remain = baseEntity.getBalance();
                if (remain == null) remain = "0";
                BigDecimal bigDecimal = new BigDecimal(remain).divide(new BigDecimal(Math.pow(10, list.get(0).getDecimal())), list.get(0).getDecimal(), RoundingMode.HALF_UP);
                list.get(0).setAssertsNum(bigDecimal.toPlainString());
                mWallet.setmBalance(bigDecimal.toPlainString());
                walletDBUtil.updateWalletInfoByAddress(mWallet);
                getCoinPrice(mWallet,list,"" + CoinType.DOGECOIN.value(), bigDecimal.toPlainString());
            }
        });
    }

    private void getDotBalance(WalletEntity mWallet, List<AssertBean> list) {
        mDotApiRepository.getBanlance(mWallet.getAllAddress(), new ApiNetResponse<DotBalanceBean>(this, true) {
            @Override
            public void onSuccess(DotBalanceBean baseEntity) {
                String remain = baseEntity.getFree();
                if (remain == null) remain = "0";
                BigDecimal bigDecimal = new BigDecimal(remain).divide(new BigDecimal(Math.pow(10, list.get(0).getDecimal())), list.get(0).getDecimal(), RoundingMode.HALF_UP);
                list.get(0).setAssertsNum(bigDecimal.toPlainString());
                mWallet.setmBalance(bigDecimal.toPlainString());
                walletDBUtil.updateWalletInfoByAddress(mWallet);
                getCoinPrice(mWallet,list,"" + CoinType.POLKADOT.value(), bigDecimal.toPlainString());
            }
        });
    }

    private void getLtcBalance(WalletEntity mWallet, List<AssertBean> list) {
        mLtcApiRepository.getBanlance(mWallet.getAllAddress(), new ApiNetResponse<LtcBalanceBean>(this, true) {
            @Override
            public void onSuccess(LtcBalanceBean baseEntity) {
                String remain = baseEntity.getBalance();
                if (remain == null) remain = "0";
                BigDecimal bigDecimal = new BigDecimal(remain).divide(new BigDecimal(Math.pow(10, list.get(0).getDecimal())), list.get(0).getDecimal(), RoundingMode.HALF_UP);
                list.get(0).setAssertsNum(bigDecimal.toPlainString());
                mWallet.setmBalance(bigDecimal.toPlainString());
                walletDBUtil.updateWalletInfoByAddress(mWallet);
                getCoinPrice(mWallet,list,"" + CoinType.LITECOIN.value(), bigDecimal.toPlainString());
            }
        });
    }

    private void getBchBalance(WalletEntity mWallet, List<AssertBean> list) {
        mBchApiRepository.getBanlance(new String(mWallet.getmPublicKey()), new ApiNetResponse<BchBalanceBean>(this, true) {
            @Override
            public void onSuccess(BchBalanceBean baseEntity) {
                String remain = baseEntity.getBalance();
                if (remain == null) remain = "0";
                BigDecimal bigDecimal = new BigDecimal(remain).divide(new BigDecimal(Math.pow(10, list.get(0).getDecimal())), list.get(0).getDecimal(), RoundingMode.HALF_UP);
                list.get(0).setAssertsNum(bigDecimal.toPlainString());
                mWallet.setmBalance(bigDecimal.toPlainString());
                walletDBUtil.updateWalletInfoByAddress(mWallet);
                getCoinPrice(mWallet,list,"" + CoinType.BITCOINCASH.value(), bigDecimal.toPlainString());
            }
        });
    }

    private void getZecBalance(WalletEntity mWallet, List<AssertBean> list) {
        mZecApiRepository.getBanlance(new String(mWallet.getmPublicKey()), new ApiNetResponse<ZecBalanceBean>(this, true) {
            @Override
            public void onSuccess(ZecBalanceBean baseEntity) {
                String remain = baseEntity.getBalance();
                if (remain == null) remain = "0";
                BigDecimal bigDecimal = new BigDecimal(remain).divide(new BigDecimal(Math.pow(10, list.get(0).getDecimal())), list.get(0).getDecimal(), RoundingMode.HALF_UP);
                list.get(0).setAssertsNum(bigDecimal.toPlainString());
                mWallet.setmBalance(bigDecimal.toPlainString());
                walletDBUtil.updateWalletInfoByAddress(mWallet);
                getCoinPrice(mWallet,list,"" + CoinType.ZCASH.value(), bigDecimal.toPlainString());
            }
        });
    }

    private void getEtcBanlance(WalletEntity mWallet, List<AssertBean> list) {
        Map<String, Object> params = new TreeMap<>();
        params.put("jsonrpc", "2.0");
        params.put("method", "eth_getBalance");
        params.put("id", 21);
        List<String> addr = new ArrayList<>();
        addr.add(mWallet.getAllAddress());
        addr.add("latest");
        params.put("params", addr);
        mEtcApiRepository.getBanlance(params, new ApiNetResponse<EtcBalanceBean>(this, true) {
            @Override
            public void onSuccess(EtcBalanceBean baseEntity) {
                String remain = baseEntity.getResult();
                if (remain == null) remain = "0";
                if (remain.startsWith("0x")) {
                    remain = remain.substring(2, remain.length());
                }
                if (StringUtils.isEmpty(remain)) remain = "0";
                remain = "" + new BigInteger(remain, 16).toString(10);
                BigDecimal bigDecimal = new BigDecimal(remain).divide(new BigDecimal(Math.pow(10, list.get(0).getDecimal())), 8, RoundingMode.HALF_UP);
                list.get(0).setAssertsNum(bigDecimal.toPlainString());
                mWallet.setmBalance(bigDecimal.toPlainString());
                walletDBUtil.updateWalletInfoByAddress(mWallet);
                getCoinPrice(mWallet,list,"" + CoinType.ETHEREUMCLASSIC.value(), bigDecimal.toPlainString());
            }
        });
    }

    private void showBanlance(WalletEntity mWallet, List<AssertBean> list, Map<String, String> bean) {
        String address = mWallet.getAllAddress();
        String mastName = "";
        int type = mWallet.getType();
        if (type == WalletUtil.MCC_COIN) {
            mastName = AppApplication.getContext().getString(R.string.default_token_name).toLowerCase();
        } else if (type == WalletUtil.OTHER_COIN) {
            mastName = AppApplication.getContext().getString(R.string.default_other_token_name).toLowerCase();
        } else if (type == WalletUtil.DM_COIN) {
            mastName = "dm";
        } else if (type == WalletUtil.TRX_COIN) {
            mastName = "trx";
        }
        for (int i = 0; i < list.size(); i++) {
            String name = list.get(i).getShort_name().toLowerCase();
            String num = bean.get(name + "_" + address);
            if (TextUtils.isEmpty(num)) {
                
                num = bean.get(name + "_" + address.toLowerCase());
            }
            list.get(i).setAssertsNum(num);
            list.get(i).setAssertsSumPrice("0.00");
            if (name.equals(mastName)) {
                BigDecimal bnum = new BigDecimal(num);
                BigDecimal bsum = bnum.multiply(NewHomeFragment.mccprice).setScale(2, BigDecimal.ROUND_HALF_UP);
                mWallet.setmPrice(bsum.toPlainString());
                list.get(i).setAssertsSumPrice(bsum.toPlainString());
                sum = sum.add(bsum);
            } else {
                walletDBUtil.updateWalletAssets(list.get(i));
            }
        }
        String balance = bean.get(mastName + "_" + address);
        if (TextUtils.isEmpty(balance)) {
            
            balance = bean.get(mastName + "_" + address.toLowerCase());
        }
        mWallet.setmBalance(balance);
        walletDBUtil.updateWalletInfoByAddress(mWallet);
        BaseBanlanceBean banlanceBean = new BaseBanlanceBean();
        banlanceBean.setAssertBeans(list);
        banlanceBean.setWalletEntity(mWallet);
        getBanlance.postValue(banlanceBean);
    }

    private void getCoinPrice(WalletEntity mWallet, List<AssertBean> list,String coinId, String remain) {
        Map<String, String> params = new TreeMap<>();
        params.put("currency", remain);
        mMarketPriceApi.getPrice(coinId,params, new ApiNetResponse<MarketPriceBean>(this, true) {
            @Override
            public void onSuccess(MarketPriceBean baseEntity) {
                BigDecimal bigDecimal = new BigDecimal(remain).multiply(new BigDecimal(baseEntity.getTickers().get(0).getPrice()));
                String price = bigDecimal.setScale(2, RoundingMode.HALF_UP).toPlainString();
                list.get(0).setAssertsSumPrice(price);
                mWallet.setSumPrice(price);
                mWallet.setmPrice(price);
                walletDBUtil.updateWalletInfoByAddress(mWallet);
                BaseBanlanceBean banlanceBean = new BaseBanlanceBean();
                banlanceBean.setAssertBeans(list);
                banlanceBean.setWalletEntity(mWallet);
                getBanlance.postValue(banlanceBean);
            }
        });
    }
    public MutableLiveData<List<NewAssertBean>> seachAssets;
    public MutableLiveData<List<EthAssertBean>> seachEthAssets;
    public MutableLiveData<List<XrpAssertBean>> seachXrpAssets;
    
    public MutableLiveData<String> showToastLD;
    
    
    public void seachAssets(int type,String key){
        if(type== WalletUtil.DM_COIN){
            seachDm(type,key);
        }else if(type== WalletUtil.OTHER_COIN){
            seachother(type,key);
        }else if(type== WalletUtil.ETH_COIN||type== WalletUtil.ETF_COIN||type== WalletUtil.DMF_COIN||type== WalletUtil.DMF_BA_COIN||
                type== WalletUtil.HT_COIN||type== WalletUtil.BNB_COIN||type == WalletUtil.MCC_COIN) {
            seachEth(type,key);
        }else if(type== WalletUtil.XRP_COIN) {
            seachXrp(type,key);
        }
    }
    private void seach(int type,String key){
        Map<String, Object> params = new TreeMap();
        params.put("arg", key);
        mMccRepository.getTkList(params, new ApiNetResponse<List<NewAssertBean>>(this, true) {
            @Override
            public void onSuccess(List<NewAssertBean> baseEntity) {
                seachAssets.postValue(baseEntity);
            }
        });
    }
    private void seachDm(int type,String key){
        Map<String, Object> params = new TreeMap();
        params.put("arg", key);
        mDmRepository.getTkList(params, new ApiNetResponse<List<NewAssertBean>>(this, true) {
            @Override
            public void onSuccess(List<NewAssertBean> baseEntity) {
                seachAssets.postValue(baseEntity);
            }
        });
    }
    private void seachother(int type,String key){
        Map<String, Object> params = new TreeMap();
        params.put("arg", key);
       mOtherRepository.getTkList(params, new ApiNetResponse<List<NewAssertBean>>(this, true) {
            @Override
            public void onSuccess(List<NewAssertBean> baseEntity) {
                seachAssets.postValue(baseEntity);
            }
        });
    }
    private void seachEth(int type,String key){
        showLoadingDialog("");
        mRpcApi.searchTokenInfoByContract(type, key).subscribe(new Observer<EthAssertBean>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(EthAssertBean ethAssertBean) {
                dismissLoadingDialog();
                List<EthAssertBean> list = new ArrayList<>();
                if (null != ethAssertBean && !TextUtils.isEmpty(ethAssertBean.getDecimals())) {
                    list.add(ethAssertBean);
                } else {
                    showToastLD.postValue(getApplication().getString(R.string.no_found_token_info));
                }
                seachEthAssets.postValue(list);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                dismissLoadingDialog();
            }

            @Override
            public void onComplete() {

            }
        });
        
    }
    private void seachXrp(int type,String key){
        Map<String, Object> params = new TreeMap();
        params.put("name", key);
        mRepository.seachXrpToken(params,new ApiNetResponse<List<XrpAssertBean>>(this, true) {
            @Override
            public void onSuccess(List<XrpAssertBean> baseEntity) {
                seachXrpAssets.postValue(baseEntity);
            }
        });
    }

    private void putChainType(int type,Map<String, Object> params){
        if (type == WalletUtil.ETF_COIN) {
            params.put("chain_type", AppApplication.getContext().getString(R.string.default_etf));
        } else if (type == WalletUtil.DMF_COIN) {
            params.put("chain_type", "dmf");
        } else if (type == WalletUtil.HT_COIN) {
            params.put("chain_type", "heco");
        } else if (type == WalletUtil.DMF_BA_COIN) {
            params.put("chain_type", "dmf");
        } else if (type == WalletUtil.BNB_COIN) {
            params.put("chain_type", "bian_smart");
        } else {
            params.put("chain_type", "eth");
        }
    }



}
