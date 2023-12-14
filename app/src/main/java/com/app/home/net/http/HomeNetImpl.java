package com.app.home.net.http;

import com.app.home.pojo.DposInfo;
import com.app.home.pojo.DposListEntity;
import com.app.home.pojo.MyPledge;
import com.app.home.pojo.ValidatorDetailNew;
import com.app.home.pojo.ValidatorListInfo;
import com.app.home.pojo.VoteDetial;
import com.app.home.pojo.VoteInfoDetialListWapper;
import com.app.home.pojo.VoteInfoWapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wallet.ctc.model.blockchain.RpcApi;
import com.wallet.ctc.model.blockchain.ValidatorInfo;

import java.util.List;

import common.app.im.model.base.RequstData;
import common.app.utils.RxSchedulers;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class HomeNetImpl implements IHomeNet {
    private static final String TAG = "HomeNetImpl";

    private Gson mGson = new Gson();

    @Override
    public Observable<DposInfo> getDposInfo() {
        return RequstData.getResponseBody(getEvmosRpcUrl() + "fm/dpos_info", DposInfo.class).compose(RxSchedulers.io_main());
    }

    
    @Override
    public Observable<DposListEntity> getDposListData(String from, int is_delegate, int sort_type, int page, int limit, String searchKeyword) {
        String url = getEvmosRpcUrl() + "val/list?from=" + from + "&is_delegate=" + is_delegate + "&sort_type=" + sort_type + "&page=" + page + "&limit=" + limit + "&name=" + searchKeyword;
        return RequstData.getResponseBody(url, DposListEntity.class).compose(RxSchedulers.io_main());
    }

    @Override
    public Observable<MyPledge> getMyPledge(String address) {
        String url = getEvmosRpcUrl() + "fm/my_pledge?address=" + address;
        return RequstData.getGetResponseBody(url, MyPledge.class).compose(RxSchedulers.io_main());
    }

    @Override
    public Observable<VoteInfoWapper> getVoteInfo(int offset) {
        String url = getEvmosRpcUrl() + "fm/vote_list?pagination.limit=" + IHomeNet.LIMIT + "&pagination.offset=" + offset;
        return RequstData.getGetResponseBody(url, String.class).flatMap((Function<String, ObservableSource<VoteInfoWapper>>) s -> {
            VoteInfoWapper wapper = mGson.fromJson(s, VoteInfoWapper.class);
            return Observable.just(wapper);
        }).compose(RxSchedulers.io_main());
    }


    @Override
    public Observable<ValidatorInfo> getValidatorInfo(String validator_address, int page, int offset) {
        
        RpcApi rpcApi = new RpcApi();
        return rpcApi.getValidatorInfo(validator_address, page, offset)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<ValidatorDetailNew> getValidatorDetail(String validator_address, String delegator_address) {
        
        String url = getEvmosRpcUrl() + "val/detail?from=" + delegator_address + "&val=" + validator_address;
        return RequstData.getGetResponseBody(url, ValidatorDetailNew.class).compose(RxSchedulers.io_main());
    }


    @Override
    public Observable<List<ValidatorListInfo.Result>> getValidatorList() {
        String url = getEvmosRpcUrl() + "fm/validator_list";
        return RequstData.getGetResponseBody(url, String.class).map((Function<String, List<ValidatorListInfo.Result>>) s -> mGson.fromJson(s, new TypeToken<List<ValidatorListInfo.Result>>() {
        }.getType())).compose(RxSchedulers.io_main());
    }

    @Override
    public Observable<VoteInfoDetialListWapper> getVoteInfo(String vote_id, int page) {
        String url = getEvmosRpcUrl() + "gov/proposal_votes?limit=" + IHomeNet.LIMIT + "&page=" + page + "&vote_id=" + vote_id;
        return RequstData.getGetResponseBody(url, new TypeToken<List<VoteInfoDetialListWapper.VoteInfoDetialList>>() {
        }).flatMap((Function<List<VoteInfoDetialListWapper.VoteInfoDetialList>, ObservableSource<VoteInfoDetialListWapper>>) voteInfoDetialLists -> {
            VoteInfoDetialListWapper wapper =new  VoteInfoDetialListWapper();
            wapper.page = page;
            wapper.result = voteInfoDetialLists;
            return Observable.just(wapper);
        }).compose(RxSchedulers.io_main());
    }

    @Override
    public Observable<VoteDetial> getVoteDetial(String vote_id) {
        String url = getEvmosRpcUrl() + "gov/vote_detail?vote_id=" + vote_id;
        return RequstData.getGetResponseBody(url, VoteDetial.class).compose(RxSchedulers.io_main());
    }


}
