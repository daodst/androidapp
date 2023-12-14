package com.wallet.ctc.ui.pay;

import android.content.Context;

import com.wallet.ctc.model.blockchain.DeviceGroupPageData;
import com.wallet.ctc.model.blockchain.EvmosClusterPersonVoteBean;
import com.wallet.ctc.model.blockchain.EvmosClusterVoteBean;
import com.wallet.ctc.model.blockchain.EvmosClusterVoteDetailBean;
import com.wallet.ctc.model.blockchain.EvmosDvmListBean;
import com.wallet.ctc.model.blockchain.EvmosSeqGasBean;

import java.util.List;

import im.wallet.router.listener.TranslationListener;
import im.wallet.router.wallet.pojo.DeviceGroupMember;
import im.wallet.router.wallet.pojo.EvmosDaoParams;
import im.wallet.router.wallet.pojo.EvmosMyGroupDataBean;
import io.reactivex.Observable;


public interface ITransferControlApi {

    
    Observable<DeviceGroupPageData> getDeviceGroupInfo(String fromAddr, String groupId);

    
    Observable<EvmosDaoParams> getDaoParams();

    
    Observable<EvmosDvmListBean> getMyDvmList(String accountAddr);


    
    Observable<EvmosDaoParams> getDaoCompositeParams(Context context, String fromAddr);

    
    Observable<String> getDstPancakeSwapPrice();


    
    Observable<EvmosSeqGasBean> getGasAndRewardAmount(int rewardType, String fromAddr, String groupId);


    
    Observable<EvmosMyGroupDataBean> getMyGroupData(String fromAddr, String groupId);


    
    void addDeviceMembers(Context context, String fromAddr, String groupId, List<DeviceGroupMember> members, TranslationListener callBack);


    
    void changeDeviceGroupName(Context context, String fromAddr, String groupId, String newGroupName, TranslationListener callBack);

    public void commissionVote(Context context, String policyAddress, String fromAddr, String clusterId, String radio, String title, String description, TranslationListener callBack);

    public void contributionVote(Context context, String policyAddress, String fromAddr, String clusterId, String radio, String title, String description, TranslationListener callBack);

    
    void exitDeviceGroup(Context context, String fromAddr, String groupId, TranslationListener callBack);


    
    void burnToPower(Context context, String fromAddr, String toAddr, String groupId,
                     String burnAmount, String useFreezeNum, TranslationListener callBack);

    
    void changeDeviceRatio(Context context, String fromAddr, String groupId, String deviceRatio, TranslationListener callBack);


    
    void changeSalaryRatio(Context context, String fromAddr, String groupId, String salaryRatio, TranslationListener callBack);


    
    void changeGroupId(Context context, String fromAddr, String groupId, String newGroupId, TranslationListener callBack);


    
    void withdrawBurnReward(Context context, String fromAddr, String groupId, TranslationListener callBack);

    void withdrawBurnReward(Context context, String fromAddr, String groupId, EvmosSeqGasBean gasBean,
                            TranslationListener callBack);

    
    void withdrawDeviceReward(Context context, String fromAddr, String groupId, TranslationListener callBack);

    void withdrawDeviceReward(Context context, String fromAddr, String groupId, EvmosSeqGasBean gasBean,
                              TranslationListener callBack);


    
    void withdrawOwnerReward(Context context, String fromAddr, String groupId, TranslationListener callBack);

    void withdrawOwnerReward(Context context, String fromAddr, String groupId, EvmosSeqGasBean gasBean,
                             TranslationListener callBack);

    
    void withdrawAirDropReward(Context context, String fromAddr, String serverName, TranslationListener callBack);


    
    void thawFrozenPower(Context context, String fromAddr, String groupId,
                         String thawAmount, TranslationListener callBack);

    
    void groupGovernancePoolsVote(Context context, String policyAddress, String fromAddress, String clusterId, String amount, String toAddress
            , String title, String description, TranslationListener callBack);

    
    void groupDaoAuthorizationVote(Context context, String policyAddress, String fromAddress, String clusterId, String endBlock, String toAddress
            , String title, String description, TranslationListener callBack);

    
    void groupProposalVote(Context context, String fromAddress, int proposalId, int option, String option2, TranslationListener callBack);

    Observable<EvmosClusterVoteBean> getClusterVoteList(String clusterId);

    
    void signPersonDvmApprove(Context context, String fromAddress, String approveAddress, String clusterId, String approveEndBlock, TranslationListener callBack);

    
    Observable<EvmosClusterVoteDetailBean> getClusterVoteDetail(int proposalId);

    Observable<EvmosClusterPersonVoteBean> getClusterVoteAllPersonDetail(int proposalId);

    Observable<EvmosClusterPersonVoteBean> getClusterVotePersonDetail(int proposalId, String voter);
}
