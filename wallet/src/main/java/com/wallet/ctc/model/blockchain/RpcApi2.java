package com.wallet.ctc.model.blockchain;

import com.wallet.ctc.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import im.wallet.router.wallet.pojo.DeviceGroupMember;
import io.reactivex.Observable;
import retrofit2.http.POST;


public class RpcApi2 extends RpcApi {

    public RpcApi2() {
        super();
    }


    
    public Observable<EvmosSeqGasBean> getAddDeviceMembersGas(String fromAddr, String groupId, List<DeviceGroupMember> members) {
        
        JSONObject msgsJsonObject = new JSONObject();
        try {
            msgsJsonObject.put("from_address", fromAddr);
            msgsJsonObject.put("cluster_id", groupId);
            if (null != members && members.size() > 0) {
                JSONArray array = new JSONArray();
                for (DeviceGroupMember member : members) {
                    JSONObject jsonItem = new JSONObject();
                    jsonItem.put("member_address", member.memberAddress);
                    jsonItem.put("index_num", member.indexNum);
                    jsonItem.put("chat_address", member.chatAddress);
                    array.put(jsonItem);
                }
                msgsJsonObject.put("members", array);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getEvmosSeqGas(fromAddr, EvmosMsgTypes.MSG_ADD_DEVICE_MEMBERS, msgsJsonObject);
    }


    
    public Observable<EvmosSeqGasBean> getChangGroupNameGas(String fromAddr, String groupId, String newGroupName) {
        
        JSONObject msgsJsonObject = new JSONObject();
        try {
            msgsJsonObject.put("from_address", fromAddr);
            msgsJsonObject.put("cluster_id", groupId);
            msgsJsonObject.put("cluster_name", newGroupName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getEvmosSeqGas(fromAddr, EvmosMsgTypes.MSG_CHANGE_DEVICE_NAME, msgsJsonObject);
    }


    
    public Observable<EvmosSeqGasBean> getExitDeviceGas(String fromAddr, String groupId) {
        
        JSONObject msgsJsonObject = new JSONObject();
        try {
            msgsJsonObject.put("from_address", fromAddr);
            msgsJsonObject.put("cluster_id", groupId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getEvmosSeqGas(fromAddr, EvmosMsgTypes.MSG_EXIT_DEVICE_GROUP, msgsJsonObject);
    }


    
    public Observable<EvmosSeqGasBean> getBurnPowerGas(String fromAddr, String gateWayAddr, String toAddr, String groupId,
                                                       String bigBurnAmount, String useFreezeNum, String chatAddr) {
        
        JSONObject msgsJsonObject = new JSONObject();
        try {
            msgsJsonObject.put("from_address", fromAddr);
            msgsJsonObject.put("to_address", toAddr);
            msgsJsonObject.put("gate_address", gateWayAddr);
            msgsJsonObject.put("cluster_id", groupId);
            msgsJsonObject.put("burn_amount", bigBurnAmount);
            msgsJsonObject.put("use_freeze_amount", useFreezeNum);
            msgsJsonObject.put("chat_address", chatAddr);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getEvmosSeqGas(fromAddr, EvmosMsgTypes.MSG_BURN_TO_POWER, msgsJsonObject);
    }


    
    public Observable<EvmosSeqGasBean> getChangeDeviceRatioGas(String fromAddr, String groupId, String deviceRatio) {
        
        JSONObject msgsJsonObject = new JSONObject();
        try {
            msgsJsonObject.put("from_address", fromAddr);
            msgsJsonObject.put("cluster_id", groupId);
            msgsJsonObject.put("device_ratio", deviceRatio);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getEvmosSeqGas(fromAddr, EvmosMsgTypes.MSG_CHANGE_DEVICE_RATIO, msgsJsonObject);
    }

    
    public Observable<EvmosSeqGasBean> getChangeSalaryRatioGas(String fromAddr, String groupId, String salaryRatio) {
        
        JSONObject msgsJsonObject = new JSONObject();
        try {
            msgsJsonObject.put("from_address", fromAddr);
            msgsJsonObject.put("cluster_id", groupId);
            msgsJsonObject.put("salary_ratio", salaryRatio);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getEvmosSeqGas(fromAddr, EvmosMsgTypes.MSG_CHANGE_SALARY_RATIO, msgsJsonObject);
    }


    
    public Observable<EvmosSeqGasBean> getChangeGroupIdGas(String fromAddr, String groupId, String newGroupId) {
        
        JSONObject msgsJsonObject = new JSONObject();
        try {
            msgsJsonObject.put("from_address", fromAddr);
            msgsJsonObject.put("cluster_id", groupId);
            msgsJsonObject.put("new_cluster_id", newGroupId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getEvmosSeqGas(fromAddr, EvmosMsgTypes.MSG_CHANGE_GROUP_ID, msgsJsonObject);
    }


    
    public Observable<EvmosSeqGasBean> getWithdrawBurnRewardGas(String fromAddr, String groupId) {
        
        JSONObject msgsJsonObject = new JSONObject();
        try {
            msgsJsonObject.put("member_address", fromAddr);
            msgsJsonObject.put("cluster_id", groupId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getEvmosSeqGas(fromAddr, EvmosMsgTypes.MSG_WITHDRAW_BURN_REWARD, msgsJsonObject);
    }

    
    public Observable<EvmosSeqGasBean> getWithdrawDeviceRewardGas(String fromAddr, String groupId) {
        
        JSONObject msgsJsonObject = new JSONObject();
        try {
            msgsJsonObject.put("member_address", fromAddr);
            msgsJsonObject.put("cluster_id", groupId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getEvmosSeqGas(fromAddr, EvmosMsgTypes.MSG_WITHDRAW_DEVICE_REWARD, msgsJsonObject);
    }


    
    public Observable<EvmosSeqGasBean> getWithdrawOwnerRewardGas(String fromAddr, String groupId) {
        
        JSONObject msgsJsonObject = new JSONObject();
        try {
            msgsJsonObject.put("address", fromAddr);
            msgsJsonObject.put("cluster_id", groupId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getEvmosSeqGas(fromAddr, EvmosMsgTypes.MSG_WITHDRAW_OWNER_REWARD, msgsJsonObject);
    }


    
    public Observable<EvmosSeqGasBean> getThawFrozenPowerGas(String fromAddr, String gateWayAddr, String groupId,
                                                             String thawAmount, String chatAddr) {
        
        JSONObject msgsJsonObject = new JSONObject();
        try {
            msgsJsonObject.put("from_address", fromAddr);
            msgsJsonObject.put("cluster_id", groupId);
            msgsJsonObject.put("thaw_amount", thawAmount);
            msgsJsonObject.put("gateway_address", gateWayAddr);
            msgsJsonObject.put("chat_address", chatAddr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getEvmosSeqGas(fromAddr, EvmosMsgTypes.MSG_THAW_FROZEN_POWER, msgsJsonObject);
    }

    
    public Observable<EvmosSeqGasBean> getGroupVoteGas(String fromAddr, String policyAddress, String voteTitle, String voteContent) {
        
        JSONObject msgsJsonObject = new JSONObject();
        try {
            
            msgsJsonObject.put("group_policy_address", policyAddress);
            JSONArray proposers = new JSONArray();
            proposers.put(fromAddr);
            msgsJsonObject.put("proposers", proposers);
            
            msgsJsonObject.put("messages", new JSONArray());
            msgsJsonObject.put("metadata", voteTitle + voteContent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getEvmosSeqGas(fromAddr, EvmosMsgTypes.MSG_GROUP_VOTE_GAS, msgsJsonObject);
    }

    
    public Observable<EvmosSeqGasBean> getGroupVote(String fromAddr, int proposal_id, int voter) {
        
        JSONObject msgsJsonObject = new JSONObject();
        try {
            msgsJsonObject.put("proposal_id", proposal_id);
            msgsJsonObject.put("voter", fromAddr);
            msgsJsonObject.put("option", voter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getEvmosSeqGas(fromAddr, EvmosMsgTypes.MSG_GROUP_VOTE, msgsJsonObject);
    }

    public Observable<EvmosSeqGasBean> personDvmApproveGas(String fromAddr, String approveAddress, String clusterId, String approveEndBlock) {
        
        JSONObject msgsJsonObject = new JSONObject();
        try {
            msgsJsonObject.put("approve_address", approveAddress);
            msgsJsonObject.put("from_address", fromAddr);
            msgsJsonObject.put("cluster_id", clusterId);
            msgsJsonObject.put("approve_end_block", approveEndBlock);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getEvmosSeqGas(fromAddr, EvmosMsgTypes.MSG_PERSON_DVM_APPROVE, msgsJsonObject);
    }


    
    public Observable<Boolean> checkCanAirDrop(String userAddr) {
        String url =  BuildConfig.AIRDROP_HOST + "airdrop/valid";
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("addr", userAddr);
        return mService.checkCanAirDrop(url, getJsonRequestBody(paramsMap));
    }


    
    @POST()
    public Observable<AirDropRedPackate> doGetAirDrop(String hexSignStr){
        String url = BuildConfig.AIRDROP_HOST + "airdrop/get";
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("params", hexSignStr);
        return mService.doGetAirDrop(url, getJsonRequestBody(paramsMap));
    }

}
