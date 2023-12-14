package com.app.home.net.http;

import com.app.home.pojo.DposInfo;
import com.app.home.pojo.DposListEntity;
import com.app.home.pojo.MyPledge;
import com.app.home.pojo.ValidatorDetailNew;
import com.app.home.pojo.ValidatorListInfo;
import com.app.home.pojo.VoteDetial;
import com.app.home.pojo.VoteInfoDetialListWapper;
import com.app.home.pojo.VoteInfoWapper;
import com.wallet.ctc.model.blockchain.ValidatorInfo;

import java.util.List;

import common.app.utils.SpUtil;
import io.reactivex.Observable;

public interface IHomeNet {

    int LIMIT = 15;

    
    default String getEvmosRpcUrl() {
        return SpUtil.getDefNode(4);
    }

    
    Observable<DposInfo> getDposInfo();

    
    Observable<DposListEntity> getDposListData(String from, int is_delegate, int sort_type, int page, int limit, String searchKeyword);

    
    Observable<MyPledge> getMyPledge(String address);

    
    Observable<VoteInfoWapper> getVoteInfo(int offset);


    
    Observable<ValidatorInfo> getValidatorInfo(String validator_address, int page, int offset);

    
    Observable<ValidatorDetailNew> getValidatorDetail(String validator_address, String delegator_address);

    
    Observable<List<ValidatorListInfo.Result>> getValidatorList();


    
    Observable<VoteInfoDetialListWapper> getVoteInfo(String vote_id, int offset);

    
    Observable<VoteDetial> getVoteDetial(String vote_id);


}
