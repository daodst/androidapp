

package com.wallet.ctc.crypto;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.R;
import com.wallet.ctc.db.AssertEntity;
import com.wallet.ctc.db.DBManager;
import com.wallet.ctc.db.NftBean;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.ChooseNodeBean;
import com.wallet.ctc.model.blockchain.RootWalletInfo;
import com.wallet.ctc.model.blockchain.WalletLogoBean;
import com.wallet.ctc.ui.me.protocol.ProtocolActivity;
import com.wallet.ctc.util.LogUtil;
import com.wallet.ctc.util.SettingPrefUtil;

import java.util.ArrayList;
import java.util.List;

import common.app.AppApplication;



public class WalletDBUtil {
    public static String USER_ID = "4131025";
    private Context mContext;
    private static WalletDBUtil walletDBUtil;
    private Gson gson = new GsonBuilder()
            .disableHtmlEscaping() 
            .create();

    public static WalletDBUtil getInstent(Context context) {
        if (null == walletDBUtil) {
            walletDBUtil = new WalletDBUtil(context);
        }
        return walletDBUtil;
    };

    public WalletDBUtil(Context mContext) {
        this.mContext = mContext;
    }

    
    public List<WalletEntity> getWallName() {
        List<WalletEntity> list = DBManager.getInstance(mContext).queryWalletListByUsername(USER_ID);
        if (list == null || list.size() < 1) {
            return new ArrayList<>();
        }
        return list;
    }

    
    public List<Integer> getWalletType() {
        List<Integer> list = DBManager.getInstance(mContext).queryWalletTypeList(USER_ID);
        if (list == null || list.size() < 1) {
            return new ArrayList<>();
        }
        return list;
    }
    
    public List<WalletEntity> getWalletByIdentity() {
        List<WalletEntity> list = DBManager.getInstance(mContext).queryWalletListByIdentity();
        if (list == null || list.size() < 1) {
            return new ArrayList<>();
        }
        return list;
    }

    
    public List<Integer> getDefWalletType() {
        List<Integer> list = DBManager.getInstance(mContext).queryDefWalletTypeList(USER_ID);
        if (list == null || list.size() < 1) {
            return new ArrayList<>();
        }
        return list;
    }

    
    public List<WalletEntity> getWalletList(int type) {
        List<WalletEntity> list = DBManager.getInstance(mContext).queryWalletListByTypeUser(USER_ID,type);
        if (list == null || list.size() < 1) {
            return new ArrayList<>();
        }
        return list;
    }

    
    public RootWalletInfo getRootWalletInfo(int type) {
        List<WalletEntity> list = getWalletList(type);
        if (null == list || list.size() == 0) {
            return null;
        }
        WalletEntity rootWallet = null;
        List<WalletEntity> childWallets = new ArrayList<>();
        for (int i=0; i<list.size(); i++) {
            WalletEntity wallet = list.get(i);
            if (null != wallet && wallet.getLevel() == 1) {
                
                rootWallet = wallet;
                break;
            }
        }
        if (rootWallet == null) {
            return null;
        }
        String rootWalletAddr = rootWallet.getAllAddress();
        for (int i=0; i<list.size(); i++) {
            WalletEntity wallet = list.get(i);
            if (wallet.getLevel() ==0 && rootWalletAddr.equals(wallet.getWalletId()) && wallet.getDefwallet() > 0) {
                
                childWallets.add(wallet);
            }
        }
        return new RootWalletInfo(type, rootWallet, childWallets);
    }

    
    public WalletEntity getWalletInfo() {
        String address = SettingPrefUtil.getWalletAddress(mContext);
        int type = SettingPrefUtil.getWalletType(mContext);
        return getWalletInfoByType(type, address);
    }

    
    public WalletEntity getWalletInfo(int walletType) {
        String address = SettingPrefUtil.getWalletTypeAddress(mContext, walletType);
        return getWalletInfoByType(walletType, address);
    }

    
    private WalletEntity getWalletInfoByType(int type, String address){
        if (type < 0) {
            return null;
        }
        if(TextUtils.isEmpty(address)){
            
            List<WalletEntity> list = DBManager.getInstance(mContext).queryWalletListByTypeUser(USER_ID,type);
            if (null != list && list.size() > 0) {
                WalletEntity wallet = list.get(0);
                SettingPrefUtil.setWalletTypeAddress(mContext, wallet.getType(), wallet.getAllAddress());
                return wallet;
            }
            return null;
        }
        WalletEntity walletEntity = DBManager.getInstance(mContext).queryWalletDetailTypeUser(address,USER_ID,type);
        if (walletEntity == null) {
            List<WalletEntity> list = DBManager.getInstance(mContext).queryWalletListByTypeUser(USER_ID,type);
            if (null == list || list.size() == 0) {
                Intent intent = new Intent(mContext, ProtocolActivity.class);
                mContext.startActivity(intent);
                if(mContext instanceof Activity) {
                    ((Activity)mContext).finish();
                }
                AppApplication.finishAllActivity();
            } else {
                walletEntity = list.get(0);
            }
        }
        return walletEntity;
    }

    public WalletEntity getWalletInfoByAddress(String address,int type){
        WalletEntity walletEntity = DBManager.getInstance(mContext).queryWalletDetailTypeUser(address,USER_ID,type);
        return walletEntity;
    }

    
    public WalletEntity getWalletInfoByAddress(String address) {
        WalletEntity walletEntity = DBManager.getInstance(mContext).queryWalletDetailUser(address, USER_ID);
        if (walletEntity == null) {
            return null;
        }
        WalletEntity WalletEntity = new Gson().fromJson(new Gson().toJson(walletEntity), WalletEntity.class);
        return WalletEntity;
    }

    
    public AssertBean getAssetsByAddress(String address, String assetsaddress, int type) {
        AssertEntity walletEntity = DBManager.getInstance(mContext).getAssetsByAddress(address, assetsaddress,type);
        if (walletEntity == null) {
            return null;
        }
        AssertBean assertBean = new Gson().fromJson(new Gson().toJson(walletEntity), AssertBean.class);
        return assertBean;
    }
    
    public WalletEntity getWalletEthByIdentity() {
        List<WalletEntity> list = DBManager.getInstance(mContext).queryWalletETHByIdentity();
        if (list == null || list.size() < 1) {
            return null;
        }
        return list.get(0);
    }
    
    public void updateWalletInfoByAddress(WalletEntity walletEntity) {
        if(walletEntity.Logo>100){
            return;
        }
        DBManager.getInstance(mContext).updateWallet(walletEntity);
    }

    
    public void updateWalletAssets(List<AssertBean> list) {
        List<AssertEntity> data = gson.fromJson(gson.toJson(list), new TypeToken<List<AssertEntity>>() {
        }.getType());
        DBManager.getInstance(mContext).updateAssertList(data);
    }

    
    public void updateWalletAssets(AssertBean list) {
        AssertEntity data = gson.fromJson(gson.toJson(list), new TypeToken<AssertEntity>() {
        }.getType());
        DBManager.getInstance(mContext).updateAssert(data);
    }

    
    public List<AssertBean> getMustWallet() {
        List<AssertBean> mustC = new ArrayList<>();
        mustC.add(new AssertBean(R.mipmap.mcc_logo, mContext.getString(R.string.default_token_name), mContext.getString(R.string.default_token_name), "0x0", "96000", 18, 0, 0));
        mustC.add(new AssertBean(R.mipmap.tt_logo, mContext.getString(R.string.default_token_name2), mContext.getString(R.string.default_token_name2), "", "96000", 18, 0, 0));
        return mustC;
    }

    public List<WalletLogoBean> getWalletLogos(boolean hasAllLogo) {
        return getWalletLogos(hasAllLogo, -1);
    }

    
    public List<WalletLogoBean> getWalletLogos(boolean hasAllLogo, int extType) {
        List<WalletLogoBean> logos = new ArrayList<>();
        if (hasAllLogo) {
            logos.add(new WalletLogoBean(R.mipmap.choose_logo_def, R.mipmap.choose_logo_def1, 1, -1));
        }
        List<Integer> logo = walletDBUtil.getWalletType();
        if (null != logo && logo.size() > 0) {
            for (int num = 0; num < logo.size(); num++) {
                int ty = logo.get(num);
                if (ty != extType) {
                    List<WalletLogoBean> logoList = getWalletLogoByType(ty);
                    if (null != logoList && logoList.size() > 0) {
                        logos.addAll(logoList);
                    }
                }

            }
        }
        return logos;
    }



    
    public List<WalletLogoBean> getWalletLogoByType(int type) {
        List<WalletLogoBean> logos = new ArrayList<>();
        List<AssertBean> assertBeans = walletDBUtil.getMustWallet(type);
        if (null != assertBeans && assertBeans.size() > 0) {
            WalletLogoBean walletLogo = new WalletLogoBean(assertBeans.get(0).getLogo(), assertBeans.get(0).getLogo(), 0, type);
            if (type == WalletUtil.MCC_COIN) {
                walletLogo.setDefLogo(R.mipmap.tt_logo_gray);
            } else if(type == WalletUtil.ETH_COIN) {
                walletLogo.setDefLogo(R.mipmap.eth_logo_gray);
            } else if(type == WalletUtil.BNB_COIN) {
                walletLogo.setDefLogo(R.mipmap.bnb_logo_gray);
            }
            logos.add(walletLogo);
        }
        return logos;
    }

    public String getWalletName(int type) {
        List<AssertBean> assets = getMustWallet(type);
        String showName = "";
        if (null != assets && assets.size() > 0) {
            showName =assets.get(0).getShort_name();
        }
        if (!TextUtils.isEmpty(showName)) {
            showName = showName.toUpperCase();
        }
        return showName;
    }

    
    public List<AssertBean> getMustWallet(int type) {
        List<AssertBean> mustC = new ArrayList<>();
        if (type == WalletUtil.DM_COIN) {
            mustC.add(new AssertBean(R.mipmap.dm_logo, mContext.getString(R.string.dm), mContext.getString(R.string.dm), "0x0", "96000", 18, type, 0));
        } else if (type == WalletUtil.ETH_COIN) {
            mustC.add(new AssertBean(R.mipmap.eth_logo, "ETH", "ETH", "", "25200", 18, type, 0));
        } else if (type == WalletUtil.BTC_COIN) {
            mustC.add(new AssertBean(R.mipmap.btc_logo, "BTC", "BTC", "", "25200", 8, type, 0));
            mustC.add(new AssertBean(R.mipmap.usdt_logo, "USDT", "USDT", "", "25200", 18, type, 0));
        } else if (type == WalletUtil.EOS_COIN) {
            mustC.add(new AssertBean(R.mipmap.eos_logo, "EOS", "EOS", "", "25200", 18, type, 0));
        } else if (type == WalletUtil.MCC_COIN) {
            mustC.add(new AssertBean(R.mipmap.tt_logo, mContext.getString(R.string.default_token_name2), mContext.getString(R.string.default_token_name2), "", "96000", 18, type, 0));
            mustC.add(new AssertBean(R.mipmap.mcc_logo, mContext.getString(R.string.default_token_name), mContext.getString(R.string.default_token_name), "", "96000", 18, type, 0));
        }else if (type == WalletUtil.OTHER_COIN){
            mustC.add(new AssertBean(R.mipmap.llq_other, mContext.getString(R.string.default_other_token_name), mContext.getString(R.string.default_other_token_name2), "", "96000", 18, type, 0));
        }else if (type == WalletUtil.XRP_COIN) {
            mustC.add(new AssertBean(R.mipmap.xrp_logo, "XRP", "XRP", "", "96000", 6, type, 0));
        }else if (type == WalletUtil.TRX_COIN) {
            mustC.add(new AssertBean(R.mipmap.trx_logo, "TRX", "TRX", "", "96000", 6, type, 0));
        }else if (type == WalletUtil.ETF_COIN) {
            mustC.add(new AssertBean(R.mipmap.etf_logo, mContext.getString(R.string.default_etf), mContext.getString(R.string.default_etf), "", "25200", 18, type, 0));
        }else if (type == WalletUtil.DMF_COIN) {
            mustC.add(new AssertBean(R.mipmap.hb_dmf_logo, mContext.getString(R.string.default_dmf_hb), mContext.getString(R.string.default_dmf_hb), "", "25200", 18, type, 0));
        }else if (type == WalletUtil.DMF_BA_COIN) {
            mustC.add(new AssertBean(R.mipmap.bian_dmf_logo, mContext.getString(R.string.default_dmf_ba), mContext.getString(R.string.default_dmf_ba), "", "25200", 18, type, 0));
        }else if (type == WalletUtil.HT_COIN) {
            mustC.add(new AssertBean(R.mipmap.huobi_logo, "HT", "HT", "", "25200", 18, type, 0));
        }else if (type == WalletUtil.BNB_COIN) {
            mustC.add(new AssertBean(R.mipmap.bnb_logo, "BNB", "BNB", "", "25200", 18, type, 0));
        }else if (type == WalletUtil.FIL_COIN) {
            mustC.add(new AssertBean(R.mipmap.fil_logo, "FIL", "Filecoin", "", "25200", 18, type, 0));
        }else if (type == WalletUtil.DOGE_COIN) {
            mustC.add(new AssertBean(R.mipmap.doge_logo, "DOGE", "Dogecoin", "", "25200", 8, type, 0));
        }else if (type == WalletUtil.DOT_COIN) {
            mustC.add(new AssertBean(R.mipmap.dot_logo, "DOT", "Polkadot", "", "25200", 10, type, 0));
        }else if (type == WalletUtil.LTC_COIN) {
            mustC.add(new AssertBean(R.mipmap.ltc_logo, "LTC", "Litecoin", "", "25200", 8, type, 0));
        }else if (type == WalletUtil.BCH_COIN) {
            mustC.add(new AssertBean(R.mipmap.bch_logo, "BCH", "Bitcoin Cash", "", "25200", 8, type, 0));
        }else if (type == WalletUtil.ZEC_COIN) {
            mustC.add(new AssertBean(R.mipmap.zec_logo, "ZEC", "Zcash", "", "25200", 8, type, 0));
        }else if (type == WalletUtil.ADA_COIN) {
            mustC.add(new AssertBean(R.mipmap.ada_logo, "ADA", "Cardano", "", "25200", 6, type, 0));
        }else if (type == WalletUtil.ETC_COIN) {
            mustC.add(new AssertBean(R.mipmap.etc_logo, "ETC", "Ethereum Classic", "", "25200", 18, type, 0));
        }else if (type == WalletUtil.SGB_COIN) {
            mustC.add(new AssertBean(R.mipmap.sgb_logo, "SGB", "SubGame", "", "25200", 10, type, 0));
        }else if (type == WalletUtil.MATIC_COIN) {
            mustC.add(new AssertBean(R.mipmap.matic_logo, "MATIC", "Polygon", "", "25200", 18, type, 0));
        }else if (type == WalletUtil.SOL_COIN) {
            mustC.add(new AssertBean(R.mipmap.sol_logo, "SOL", "Solana", "", "25200", 9, type, 0));
        }
        return mustC;
    }

    
    public AssertBean getWalletMainCoin(int type) {
        List<AssertBean> asserts = getMustWallet(type);
        AssertBean main = null;
        if (null != asserts && asserts.size() > 0) {
            if (asserts.size() == 1) {
                main = asserts.get(0);
            } else {
                if (type == WalletUtil.MCC_COIN) {
                    for (int i=0 ;i<asserts.size(); i++) {
                        if (BuildConfig.EVMOS_FAKE_UNINT.equalsIgnoreCase(asserts.get(i).getShort_name())) {
                            main = asserts.get(i);
                            break;
                        }
                    }
                } else {
                    main = asserts.get(0);
                }
            }
        }
        return main;
    }

    
    public AssertBean getWalletAssets(int type, String coinNameOrContract) {
        if (TextUtils.isEmpty(coinNameOrContract)) {
            return null;
        }
        
        List<AssertBean> asserts = getMustWallet(type);
        AssertBean main = null;
        if (null != asserts && asserts.size() > 0) {
            for (int i=0 ;i<asserts.size(); i++) {
                if (coinNameOrContract.equalsIgnoreCase(asserts.get(i).getShort_name()) ||
                        coinNameOrContract.equalsIgnoreCase(asserts.get(i).getContract())) {
                    main = asserts.get(i);
                    break;
                }
            }
        }
        return main;
    }

    
    public List<AssertBean> getMustAssets(int type) {
        List<AssertBean> mustC = new ArrayList<>();
        if (type == WalletUtil.DM_COIN) {
            mustC.addAll(SettingPrefUtil.getMustAssets(mContext, "DM"));
        } else if (type == WalletUtil.ETH_COIN) {
            mustC.addAll(SettingPrefUtil.getMustAssets(mContext, "ETH"));
        }else if (type == WalletUtil.ETF_COIN) {
            mustC.addAll(SettingPrefUtil.getMustAssets(mContext, mContext.getString(R.string.default_etf).toUpperCase()));
        }else if (type == WalletUtil.DMF_COIN) {
            mustC.addAll(SettingPrefUtil.getMustAssets(mContext, mContext.getString(R.string.default_dmf_hb).toUpperCase()));
        }else if (type == WalletUtil.DMF_BA_COIN) {
            mustC.addAll(SettingPrefUtil.getMustAssets(mContext, mContext.getString(R.string.default_dmf_ba).toUpperCase()));
        } else if (type == WalletUtil.BTC_COIN) {
            mustC.addAll(SettingPrefUtil.getMustAssets(mContext, "BTC"));
        } else if (type == WalletUtil.EOS_COIN) {
            mustC.addAll(SettingPrefUtil.getMustAssets(mContext, "EOS"));
        } else if (type == WalletUtil.MCC_COIN) {
            mustC.addAll(SettingPrefUtil.getMustAssets(mContext, mContext.getString(R.string.default_token_name).toUpperCase()));
        }else if (type == WalletUtil.OTHER_COIN) {
            mustC.addAll(SettingPrefUtil.getMustAssets(mContext, mContext.getString(R.string.default_other_token_name).toUpperCase()));
        }else if(type == WalletUtil.XRP_COIN) {
            mustC.addAll(SettingPrefUtil.getMustAssets(mContext, "XRP"));
        }else if(type == WalletUtil.TRX_COIN) {
            mustC.addAll(SettingPrefUtil.getMustAssets(mContext, "TRX"));
        }else if(type == WalletUtil.HT_COIN) {
            mustC.addAll(SettingPrefUtil.getMustAssets(mContext, "HT"));
        }else if(type == WalletUtil.BNB_COIN) {
            mustC.addAll(SettingPrefUtil.getMustAssets(mContext, "BNB"));
        }else if(type == WalletUtil.FIL_COIN) {
            mustC.addAll(SettingPrefUtil.getMustAssets(mContext, "FIL"));
        }else if(type == WalletUtil.DOGE_COIN) {
            mustC.addAll(SettingPrefUtil.getMustAssets(mContext, "DOGE"));
        }else if(type == WalletUtil.DOT_COIN) {
            mustC.addAll(SettingPrefUtil.getMustAssets(mContext, "DOT"));
        }else if(type == WalletUtil.LTC_COIN) {
            mustC.addAll(SettingPrefUtil.getMustAssets(mContext, "LTC"));
        }else if(type == WalletUtil.BCH_COIN) {
            mustC.addAll(SettingPrefUtil.getMustAssets(mContext, "BCH"));
        }else if(type == WalletUtil.ZEC_COIN) {
            mustC.addAll(SettingPrefUtil.getMustAssets(mContext, "ZEC"));
        }else if(type == WalletUtil.ADA_COIN) {
            mustC.addAll(SettingPrefUtil.getMustAssets(mContext, "ADA"));
        }else if(type == WalletUtil.ETC_COIN) {
            mustC.addAll(SettingPrefUtil.getMustAssets(mContext, "ETC"));
        }else if (type == WalletUtil.SGB_COIN) {
            mustC.addAll(SettingPrefUtil.getMustAssets(mContext, "SGB"));
        }else if (type == WalletUtil.SOL_COIN) {
            mustC.addAll(SettingPrefUtil.getMustAssets(mContext, "SOL"));
        }else if (type == WalletUtil.MATIC_COIN) {
            mustC.addAll(SettingPrefUtil.getMustAssets(mContext, "MATIC"));
        }
        return mustC;
    }

    
    public List<AssertBean> canChooseWallet(int type) {
        List<AssertBean> ASSERTS_LIST = new ArrayList<>();
        if (type == WalletUtil.DM_COIN) {
            ASSERTS_LIST.addAll(SettingPrefUtil.getCanAssets(mContext, "DM"));
        } else if (type == WalletUtil.ETH_COIN) {
            ASSERTS_LIST.addAll(SettingPrefUtil.getCanAssets(mContext, "ETH"));
        }else if (type == WalletUtil.ETF_COIN) {
            ASSERTS_LIST.addAll(SettingPrefUtil.getCanAssets(mContext, mContext.getString(R.string.default_etf).toUpperCase()));
        }else if (type == WalletUtil.DMF_COIN) {
            ASSERTS_LIST.addAll(SettingPrefUtil.getCanAssets(mContext, mContext.getString(R.string.default_dmf_hb).toUpperCase()));
        }else if (type == WalletUtil.DMF_BA_COIN) {
            ASSERTS_LIST.addAll(SettingPrefUtil.getCanAssets(mContext, mContext.getString(R.string.default_dmf_ba).toUpperCase()));
        } else if (type == WalletUtil.MCC_COIN) {
            ASSERTS_LIST.addAll(SettingPrefUtil.getCanAssets(mContext, mContext.getString(R.string.default_token_name).toUpperCase()));
        }else if (type == WalletUtil.OTHER_COIN) {
            ASSERTS_LIST.addAll(SettingPrefUtil.getCanAssets(mContext, mContext.getString(R.string.default_other_token_name).toUpperCase()));
        }else if(type == WalletUtil.XRP_COIN) {
            ASSERTS_LIST.addAll(SettingPrefUtil.getCanAssets(mContext, "XRP"));
        }else if(type == WalletUtil.TRX_COIN) {
            ASSERTS_LIST.addAll(SettingPrefUtil.getCanAssets(mContext, "TRX"));
        }else if(type == WalletUtil.HT_COIN) {
            ASSERTS_LIST.addAll(SettingPrefUtil.getCanAssets(mContext, "HT"));
        }else if(type == WalletUtil.BNB_COIN) {
            ASSERTS_LIST.addAll(SettingPrefUtil.getCanAssets(mContext, "BNB"));
        }else if(type == WalletUtil.EOS_COIN) {
            ASSERTS_LIST.addAll(SettingPrefUtil.getCanAssets(mContext, "EOS"));
        }else if(type == WalletUtil.FIL_COIN) {
            ASSERTS_LIST.addAll(SettingPrefUtil.getCanAssets(mContext, "FIL"));
        }else if(type == WalletUtil.DOGE_COIN) {
            ASSERTS_LIST.addAll(SettingPrefUtil.getCanAssets(mContext, "DOGE"));
        }else if(type == WalletUtil.DOT_COIN) {
            ASSERTS_LIST.addAll(SettingPrefUtil.getCanAssets(mContext, "DOT"));
        }else if(type == WalletUtil.LTC_COIN) {
            ASSERTS_LIST.addAll(SettingPrefUtil.getCanAssets(mContext, "LTC"));
        }else if(type == WalletUtil.BCH_COIN) {
            ASSERTS_LIST.addAll(SettingPrefUtil.getCanAssets(mContext, "BCH"));
        }else if(type == WalletUtil.ZEC_COIN) {
            ASSERTS_LIST.addAll(SettingPrefUtil.getCanAssets(mContext, "ZEC"));
        }else if(type == WalletUtil.ADA_COIN) {
            ASSERTS_LIST.addAll(SettingPrefUtil.getCanAssets(mContext, "ADA"));
        }else if(type == WalletUtil.ETC_COIN) {
            ASSERTS_LIST.addAll(SettingPrefUtil.getCanAssets(mContext, "ETC"));
        }else if(type == WalletUtil.SGB_COIN) {
            ASSERTS_LIST.addAll(SettingPrefUtil.getCanAssets(mContext, "SGB"));
        }else if (type == WalletUtil.SOL_COIN) {
            ASSERTS_LIST.addAll(SettingPrefUtil.getCanAssets(mContext, "SOL"));
        }else if (type == WalletUtil.MATIC_COIN) {
            ASSERTS_LIST.addAll(SettingPrefUtil.getCanAssets(mContext, "MATIC"));
        }
        ASSERTS_LIST.addAll(getCanAssets(type));
        return ASSERTS_LIST;
    }

    
    public List<AssertBean> getCanAssets(int type) {
        List<AssertBean> list = new ArrayList<>();
        List<AssertEntity> datas = DBManager.getInstance(mContext).queryAssestListByUser(type, USER_ID);
        if (datas != null && datas.size() > 0) {
            String data = gson.toJson(datas);
            list = gson.fromJson(data, new TypeToken<List<AssertBean>>() {
            }.getType());
        }
        return list;
    }

    public List<AssertBean> getAssetsByWalletType(String address, int type) {
        List<AssertBean> list = new ArrayList<>();
        List<AssertEntity> datas = DBManager.getInstance(mContext).queryAssestListByUser(address, type, USER_ID);
        if (datas != null && datas.size() > 0) {
            String data = gson.toJson(datas);
            list = gson.fromJson(data, new TypeToken<List<AssertBean>>() {
            }.getType());
        }
        return list;
    }

    
    public void delWallet(String address) {
        DBManager.getInstance(mContext).deleteWalletByAddressUser(address, USER_ID);
        DBManager.getInstance(mContext).deleteAssertByWalletAndUser(address, USER_ID);
    }

    
    public void delWallet() {
        DBManager.getInstance(mContext).delAssetAll();
        DBManager.getInstance(mContext).delWalletAll();
    }

    public void delWallet(String address,int type) {
        DBManager.getInstance(mContext).deleteWalletByAddressUser(address, USER_ID,type);
        DBManager.getInstance(mContext).deleteAssertByWalletAndUser(address, USER_ID,type);
    }

    
    public void delIdentityWallet() {
        DBManager.getInstance(mContext).delWalletAll();
        DBManager.getInstance(mContext).delAssetAll();
    }

    
    public void insertWallet(WalletEntity address) {
        DBManager.getInstance(mContext).insertWallet(address);
    }

    
    public void addAssets(AssertBean bean) {
        if (null == bean) {
            return;
        }
        bean.setId(null);
        DBManager.getInstance(mContext).insertAssert(gson.fromJson(gson.toJson(bean), AssertEntity.class));
    }

    
    public void addAssetsList(List<AssertBean> bean) {
        if (null == bean) {
            return;
        }
        for(int i=0;i<bean.size();i++){
            addAssets(bean.get(i));
        }
        LogUtil.d(""+bean.size());
    }
    
    public void delAssets(AssertBean bean) {
        AssertEntity assertEntity=gson.fromJson(gson.toJson(bean),AssertEntity.class);
        DBManager.getInstance(mContext).deleteAssets(assertEntity);
    }
    
    public void delAssetsWallet(String address, String name) {
        DBManager.getInstance(mContext).deleteAssertByWalletAndName(address, name);
    }

    
    public void delAssetsWalletAll(String name) {
        DBManager.getInstance(mContext).deleteAssertByWalletAndName(name);
    }

    public void delAssetsWallet(String name,String contract,int type) {
        DBManager.getInstance(mContext).deleteAssertByName(name,contract,type);
    }

    
    public void delAssetsWallet(String address, String name, String assAddress) {
        DBManager.getInstance(mContext).deleteAssertByWalletAndName(address, name, assAddress);
    }

    
    public List<ChooseNodeBean> canChooseNode() {
        List<ChooseNodeBean> nodeList = new ArrayList<>();
        return nodeList;
    }


    
    public boolean hasWallet(String walletName, int type) {
        boolean can = false;
        List<WalletEntity> mWallName = getWalletList(type);
        for (int i = 0; i < mWallName.size(); i++) {
            if (walletName.equals(mWallName.get(i).getName())) {
                can = true;
                break;
            }
        }
        return can;
    }

    
    public boolean hasWalletAddress(String walletaddress) {
        boolean can = false;
        List<WalletEntity> mWallName = getWallName();
        for (int i = 0; i < mWallName.size(); i++) {
            if (walletaddress.equals(mWallName.get(i).getAllAddress())) {
                can = true;
                break;
            }
        }
        return can;
    }

    
    public List<AssertBean> getCanAssetsAll(String address,int type) {
        List<AssertBean> list = new ArrayList<>();
        List<AssertEntity> datas = DBManager.getInstance(mContext).queryAssestList(address,type);
        if (datas != null && datas.size() > 0) {
            String data = gson.toJson(datas);
            list = gson.fromJson(data, new TypeToken<List<AssertBean>>() {
            }.getType());
        }
        return list;
    }
    
    public List<AssertBean> getMustAssetsAll() {
        List<AssertBean> list = new ArrayList<>();
        List<AssertEntity> datas = DBManager.getInstance(mContext).queryMustAssestList();
        if (datas != null && datas.size() > 0) {
            String data = gson.toJson(datas);
            List<AssertBean> alllist = gson.fromJson(data, new TypeToken<List<AssertBean>>() {
            }.getType());
            for(int i=0;i<alllist.size();i++){
                boolean has=false;
                AssertBean assertBean=alllist.get(i);
                for(int m=0;m<list.size();m++){
                    if(list.get(m).getType()==assertBean.getType()&&list.get(m).getShort_name().equalsIgnoreCase(assertBean.getShort_name())&&list.get(m).getContract().equalsIgnoreCase(assertBean.getContract())){
                        has=true;
                        break;
                    }
                }
                if(!has){
                    list.add(assertBean);
                }
            }
        }
        return list;
    }

    
    public void checkAssest() {
        List<AssertBean> must = new ArrayList<>();
        must.addAll(SettingPrefUtil.getMustAssets(mContext, "DM"));
        must.addAll(SettingPrefUtil.getMustAssets(mContext, "ETH"));
        must.addAll(SettingPrefUtil.getMustAssets(mContext, mContext.getString(R.string.default_token_name).toUpperCase()));
        must.addAll(SettingPrefUtil.getMustAssets(mContext, mContext.getString(R.string.default_other_token_name).toUpperCase()));
        must.addAll(SettingPrefUtil.getMustAssets(mContext, "TRX"));
        must.addAll(SettingPrefUtil.getMustAssets(mContext, "XRP"));
        must.addAll(SettingPrefUtil.getMustAssets(mContext, "HT"));
        must.addAll(SettingPrefUtil.getMustAssets(mContext, "BNB"));
        must.addAll(SettingPrefUtil.getMustAssets(mContext, mContext.getString(R.string.default_etf).toUpperCase()));
        must.addAll(SettingPrefUtil.getMustAssets(mContext, mContext.getString(R.string.default_dmf_hb).toUpperCase()));
        must.addAll(SettingPrefUtil.getMustAssets(mContext, mContext.getString(R.string.default_dmf_ba).toUpperCase()));
        must.addAll(SettingPrefUtil.getMustAssets(mContext, "EOS"));
        must.addAll(SettingPrefUtil.getMustAssets(mContext, "FIL"));
        must.addAll(SettingPrefUtil.getMustAssets(mContext, "DOGE"));
        must.addAll(SettingPrefUtil.getMustAssets(mContext, "DOT"));
        must.addAll(SettingPrefUtil.getMustAssets(mContext, "LTC"));
        must.addAll(SettingPrefUtil.getMustAssets(mContext, "BCH"));
        must.addAll(SettingPrefUtil.getMustAssets(mContext, "ZEC"));
        must.addAll(SettingPrefUtil.getMustAssets(mContext, "ADA"));
        must.addAll(SettingPrefUtil.getMustAssets(mContext, "ETC"));
        must.addAll(SettingPrefUtil.getMustAssets(mContext, "SGB"));
        must.addAll(SettingPrefUtil.getMustAssets(mContext, "SOL"));
        must.addAll(SettingPrefUtil.getMustAssets(mContext, "MATIC"));

        List<AssertBean> mustAssest = getMustAssetsAll();
        isMustAssets(must,mustAssest);
        List<WalletEntity> list = getWallName();
        for (int i = 0; i < list.size(); i++) {
            WalletEntity WalletEntity = list.get(i);
            List<AssertBean> listAssest = getCanAssetsAll(WalletEntity.getAllAddress(),WalletEntity.getType());
            for (int lis = 0; lis < listAssest.size(); lis++) {
                if(listAssest.get(lis).getLevel()==0) {
                    listAssest.get(lis).setLevel(2);
                    updateWalletAssets(listAssest.get(lis));
                }
            }
            if (null == must || must.size() < 1) {
                continue;
            }
            if (null == listAssest || listAssest.size() < 1) {
                for (int m = 0; m < must.size(); m++) {
                    if(must.get(m).getType()==WalletEntity.getType()) {
                        must.get(m).setWalletAddress(WalletEntity.getAllAddress());
                        addAssets(must.get(m));
                    }
                }
                continue;
            }

            for (int m = 0; m < must.size(); m++) {
                AssertBean mustBean=must.get(m);
                if(!isHaveAssets(mustBean,listAssest)){
                    mustBean.setLevel(0);
                    mustBean.setWalletAddress(WalletEntity.getAllAddress());
                    if(must.get(m).getType()==WalletEntity.getType()) {
                        addAssets(mustBean);
                    }
                }
            }

        }
    }

    
    private boolean isHaveAssets(AssertBean mustBean,List<AssertBean> listAssest){
        boolean bool=false;
        for (int m = 0; m < listAssest.size(); m++) {
            AssertBean chooseBean=listAssest.get(m);
            
            if(chooseBean.getShort_name().equals(mustBean.getShort_name())&&chooseBean.getContract().equals(mustBean.getContract())){
                chooseBean.setLevel(0);
                chooseBean.setDecimal(mustBean.getDecimal()+"");
                chooseBean.setGas(mustBean.getGas());
                chooseBean.setImg_path(mustBean.getImg_path());
                updateWalletAssets(chooseBean);
                return true;
            }
        }
        return bool;
    }
    
    private void isMustAssets(List<AssertBean> mustBean,List<AssertBean> listAssest){
        
        for(int i=0;i<listAssest.size();i++){
            boolean bo=false;
            AssertBean old=listAssest.get(i);
            for (int m = 0; m < mustBean.size(); m++) {
                AssertBean chooseBean=mustBean.get(m);
                
                if(chooseBean.getShort_name().equals(old.getShort_name())&&chooseBean.getContract().equals(old.getContract())){
                    bo=true;
                    break;
                }
            }
            if(!bo){
                AssertBean bean=new AssertBean(old.getImg_path(), old.getShort_name(), old.getFull_name(), old.getContract(), "60000", old.getDecimal() + "", old.getType(), 2);
                bean.setWalletAddress("");
                walletDBUtil.addAssets(bean);
            }
        }
    }
    
    public NftBean getNftBeanByAddress(String walletAddress, String nftAddress, int walletType) {
        NftBean bean = DBManager.getInstance(mContext).queryNftAssets(walletAddress, nftAddress, walletType);
        return bean;
    }

    
    public List<NftBean> getNftListByAddress(String walletAddress, int WalletType) {
        return DBManager.getInstance(mContext).queryNftAssets(walletAddress, WalletType);
    }

    
    public void addNftToken(NftBean data, WalletEntity entity) {
        
        data.walletType = entity.getType();
        data.walletAddress = entity.getAllAddress();
        DBManager.getInstance(mContext).insertNftToken(data);
    }

    
    public void deleteNftToken(NftBean data) {
        DBManager.getInstance(mContext).deleteNftToken(data);
    }
}
