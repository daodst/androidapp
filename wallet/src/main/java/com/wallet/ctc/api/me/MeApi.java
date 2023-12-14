

package com.wallet.ctc.api.me;

import static com.wallet.ctc.crypto.WalletUtil.BNB_COIN;
import static com.wallet.ctc.crypto.WalletUtil.DMF_BA_COIN;
import static com.wallet.ctc.crypto.WalletUtil.DMF_COIN;
import static com.wallet.ctc.crypto.WalletUtil.ETF_COIN;
import static com.wallet.ctc.crypto.WalletUtil.HT_COIN;

import com.wallet.ctc.Constants;
import com.wallet.ctc.R;
import com.wallet.ctc.base.BaseEntity;
import com.wallet.ctc.https.HttpMethods;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import common.app.AppApplication;
import common.app.base.fragment.mall.api.RequestHelper;
import common.app.base.model.http.bean.Result;
import common.app.utils.SpUtil;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.http.Part;
import retrofit2.http.PartMap;



public class MeApi {
    private MeService mService;
    private MeService mService2;
    public static int ETHID=0;

    public static int getETHID(){
        ETHID=ETHID+1;
        return ETHID;
    }

    private RequestHelper mHelp=new RequestHelper();
    public MeApi() {
        Retrofit retrofit = HttpMethods.getInstance().getRetrofit();
        mService = retrofit.create(MeService.class);
        Retrofit retrofit2 = HttpMethods.getPhpRetrofit(Constants.API_URL2);
        mService2 = retrofit2.create(MeService.class);

    }
    private String getChainType(int type){
        String chain_type="eth";
        if(type==ETF_COIN){
            chain_type=AppApplication.getContext().getString(R.string.default_etf);
        }else if(type==DMF_COIN){
            chain_type="dmf";
        }else if(type==HT_COIN){
            chain_type="heco";
        }else if(type==DMF_BA_COIN){
            chain_type="dmf";
        }else if(type==BNB_COIN){
            chain_type="bian_smart";
        }
        return chain_type;
    }
    public Observable<BaseEntity> getArticleList(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService2.getArticleList(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getHelpList(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService2.getHelpList(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getPostArticleList(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService2.getPostArticleList(params).subscribeOn(Schedulers.io());
    }

    
    public Observable<BaseEntity> getPostGroupArticleList(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService2.getPostGroupArticleList(params).subscribeOn(Schedulers.io());
    }
    

    public Observable<BaseEntity> addPostArticle(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService2.addPostArticle(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> loginRegist(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService2.loginRegist(params).subscribeOn(Schedulers.io());
    }


    public Observable<BaseEntity> editPostArticle(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService2.editPostArticle(params).subscribeOn(Schedulers.io());
    }
    public Observable<BaseEntity> getLihaokong(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService2.getLihaokong(params).subscribeOn(Schedulers.io());
    }

    
    public Observable<BaseEntity> addPostGroupArticle(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService2.addPostGroupArticle(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> editPostGroupArticle(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService2.editPostGroupArticle(params).subscribeOn(Schedulers.io());
    }
    

    public Observable<BaseEntity> getPostArticleDetail(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService2.getPostArticleDetail(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> delPostArticle(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService2.delPostArticle(params).subscribeOn(Schedulers.io());
    }


    public Observable<BaseEntity> getArticle(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService2.getArticle(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getShare(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService2.getShare(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getArticleInfo(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService2.getArticleInfo(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getTranfer(Map<String, Object> params,int type) {
        Map<String, Object> header=new TreeMap<>();
        header.put("chain_rpc_url", SpUtil.getDefNode(type));
        params.put("chain_type",getChainType(type));
        params=mHelp.getMapParem(params);
        return mService.getTranfer(params,header).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getTranferHash(Map<String, Object> params,int type) {
        Map<String, Object> header=new TreeMap<>();
        header.put("chain_rpc_url", SpUtil.getDefNode(type));
        params.put("chain_type",getChainType(type));
        params=mHelp.getMapParem(params);
        return mService.getTranferHash(params,header).subscribeOn(Schedulers.io());

    }

    public Observable<BaseEntity> getPrice(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService.getPrice(params).subscribeOn(Schedulers.io());
    }
    public Observable<BaseEntity> getMarketPrice(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService.getMarketPrice(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getKline(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService.getKline(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getCurrencys(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService.getCurrencys(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getlocals(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService.getlocals(params).subscribeOn(Schedulers.io());
    }


    public Observable<BaseEntity> getHelpInfo(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService2.getHelpInfo(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getAddress(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService2.getAddress(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getEthCreatPrice(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService2.getEthCreatPrice(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getSysAddress(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService2.getSysAddress(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> setAddress(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService2.setAddress(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getCandyList(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService2.getCandyList(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getLinCandyList(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService2.getLinCandyList(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getSendCandyList(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService2.getSendCandyList(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getCandyDetail(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService2.getCandyDetail(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getCandysxf(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService2.getCandysxf(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getCandysy(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService2.getCandysy(params).subscribeOn(Schedulers.io());
    }
    public Observable<BaseEntity> creatTxid(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService2.creatTxid(params).subscribeOn(Schedulers.io());
    }


    public Observable<BaseEntity> getCandyInfo(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService2.getCandyInfo(params).subscribeOn(Schedulers.io());
    }
    public Observable<BaseEntity> getCandySend(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService2.getCandySend(params).subscribeOn(Schedulers.io());
    }
    public Observable<BaseEntity> getCandy(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService2.getCandy(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getPostrList(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService2.getPostrList(params).subscribeOn(Schedulers.io());
    }


    public Observable<BaseEntity> pushCon(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService2.pushCon(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getVersion(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService2.getVersion(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getCommandList(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService2.getCommandList(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getAddArticleLike(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService2.getAddArticleLike(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> addCommand(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService2.addCommand(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> delCommand(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService2.delCommand(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> delCommandReply(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService2.delCommandReply(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> addCommandReply(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService2.addCommandReply(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getCommandDetail(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService2.getCommandDetail(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> addCommandReplyReply(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService2.addCommandReplyReply(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getCommandReplyList(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService2.getCommandReplyList(params).subscribeOn(Schedulers.io());
    }


    public Observable<BaseEntity> getAdvertList(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService2.getAdvertList(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> seachToken(Map<String, Object> params,int type) {
        Map<String, Object> header=new TreeMap<>();
        header.put("chain_rpc_url", SpUtil.getDefNode(type));
        params.put("chain_type",getChainType(type));
        params=mHelp.getMapParem(params);
        return mService.seachToken(params,header).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getBanlance(Map<String, Object> params,int type) {
        Map<String, Object> header=new TreeMap<>();
        header.put("chain_rpc_url", SpUtil.getDefNode(type));
        params.put("chain_type",getChainType(type));
        params=mHelp.getMapParem(params);
        return mService.getBanlance(params,header).subscribeOn(Schedulers.io());

    }

    public Observable<BaseEntity> getBanlances(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService.getBanlances(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getTransctionList(Map<String, Object> params,int type) {
        Map<String, Object> header=new TreeMap<>();
        header.put("chain_rpc_url", SpUtil.getDefNode(type));
        params.put("chain_type",getChainType(type));
        params=mHelp.getMapParem(params);
        return mService.getTransctionList(params,header).subscribeOn(Schedulers.io());

    }
    public Observable<BaseEntity> addAddress(Map<String, Object> params,int type) {
        Map<String, Object> header=new TreeMap<>();
        header.put("chain_rpc_url", SpUtil.getDefNode(type));
        params.put("chain_type",getChainType(type));
        params=mHelp.getMapParem(params);
        return mService.addAddress(params,header).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getChains() {
        Map<String,Object> params=mHelp.getMapParem(new TreeMap<>());
        return mService.getChains(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getQuotesList(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService.getQuotesList(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getNonce(Map<String, Object> params,int type) {
        Map<String, Object> header=new TreeMap<>();
        header.put("chain_rpc_url", SpUtil.getDefNode(type));
        params.put("chain_type",getChainType(type));
        params=mHelp.getMapParem(params);
        return mService.getNonce(params,header).subscribeOn(Schedulers.io());
    }
    public Observable<BaseEntity> getNoticeList(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService2.getNoticeList(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getNoticeDetail(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService2.getNoticeDetail(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getGasPrice(Map<String, Object> params,int type) {
        Map<String, Object> header=new TreeMap<>();
        header.put("chain_rpc_url", SpUtil.getDefNode(type));
        params.put("chain_type",getChainType(type));
        params=mHelp.getMapParem(params);
        return mService.getGasPrice(params,header).subscribeOn(Schedulers.io());

    }

    public Observable<BaseEntity> getGasDefPrice(Map<String, Object> params,int type) {
        Map<String, Object> header=new TreeMap<>();
        header.put("chain_rpc_url", SpUtil.getDefNode(type));
        params.put("chain_type",getChainType(type));
        params=mHelp.getMapParem(params);
        return mService.getGasDefPrice(params,header).subscribeOn(Schedulers.io());
    }
    public Observable<BaseEntity> getGasDefPrice2(Map<String, Object> params,int type) {
        Map<String, Object> header=new TreeMap<>();
        header.put("chain_rpc_url", SpUtil.getDefNode(type));
        params.put("chain_type",getChainType(type));
        params=mHelp.getMapParem(params);
        return mService.getGasDefPrice2(params,header).subscribeOn(Schedulers.io());

    }

    public Observable<BaseEntity> creatEth(Map<String, Object> params,int type) {
        Map<String, Object> header=new TreeMap<>();
        header.put("chain_rpc_url", SpUtil.getDefNode(type));
        params.put("chain_type",getChainType(type));
        params=mHelp.getMapParem(params);
        return mService.creatEth(params,header).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getGas(Map<String, Object> params,int type) {
        Map<String, Object> header=new TreeMap<>();
        header.put("chain_rpc_url", SpUtil.getDefNode(type));
        params.put("chain_type",getChainType(type));
        params=mHelp.getMapParem(params);
        return mService.getGas(params,header).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getDefAssert(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService2.getDefAssert(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getTransfer(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService.getTransfer(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getWeekTransfer(Map<String, Object> params,int type) {
        Map<String, Object> header=new TreeMap<>();
        header.put("chain_rpc_url", SpUtil.getDefNode(type));
        params.put("chain_type",getChainType(type));
        params=mHelp.getMapParem(params);
        return mService.getWeekTransfer(params,header).subscribeOn(Schedulers.io());
    }
    public Observable<BaseEntity> checkUserAutk(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService2.checkUserAutk(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getUserAutk(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService2.getUserAutk(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getBtcBanlance(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService.getBtcBanlance(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getBtcWeekday(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService.getBtcWeekday(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getBtcTranscationList(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService.getBtcTranscationList(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getBtcUsdtBanlance(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService.getBtcUsdtBanlance(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getBtcUsdtWeekday(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService.getBtcUsdtWeekday(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getBtcUsdtTranscationList(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService.getBtcUsdtTranscationList(params).subscribeOn(Schedulers.io());
    }


    public Observable<BaseEntity> getBtcTransfer(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService.getBtcTransfer(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getBtcFees(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService.getBtcFees(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getBtcData(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService.getBtcData(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> addBtcAddress(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService.addBtcAddress(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> addXrpAddress(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService.addXrpAddress(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getXrpTransFee(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService.getXrpTransFee(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getXrpTransSequence(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService.getXrpTransSequence(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getXrpBanlance(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService.getXrpBanlance(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getXrpTokenBanlance(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService.getXrpTokenBanlance(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getXrpTransctionList(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService.getXrpTransctionList(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getXrpWeekTransfer(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService.getXrpWeekTransfer(params).subscribeOn(Schedulers.io());
    }


    public Observable<BaseEntity> getXrpTokenWeekTransfer(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService.getXrpTokenWeekTransfer(params).subscribeOn(Schedulers.io());
    }
    public Observable<BaseEntity> sendXrpTranfer(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService.sendXrpTranfer(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> seachXrpToken(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService.seachXrpToken(params).subscribeOn(Schedulers.io());
    }


    
    public Observable<BaseEntity> getGasLimit(Map<String, Object> params,int type) {
        Map<String, Object> header=new TreeMap<>();
        header.put("chain_rpc_url", SpUtil.getDefNode(type));
        params.put("chain_type",getChainType(type));
        params=mHelp.getMapParem(params);
        return mService.getGasLimit(params,header).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getMaxPriorityFeePerGas(Map<String, Object> params,int type) {
        Map<String, Object> header=new TreeMap<>();
        header.put("chain_rpc_url", SpUtil.getDefNode(type));
        params.put("chain_type",getChainType(type));
        params=mHelp.getMapParem(params);
        return mService.getMaxPriorityFeePerGas(params,header).subscribeOn(Schedulers.io());
    }


    public Observable<Result<List<String>>> uploadFiles(@PartMap Map<String, RequestBody> map,
                                                        @Part List<MultipartBody.Part> files) {
        return mService2.uploadFiles(map,files).subscribeOn(Schedulers.io());
    }
}
