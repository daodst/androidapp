package com.wallet.ctc.ui.pay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import com.wallet.ctc.Constants;
import com.wallet.ctc.R;
import com.wallet.ctc.crypto.ChatSdk;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.CoinPriceBean;
import com.wallet.ctc.model.blockchain.DeviceGroupPageData;
import com.wallet.ctc.model.blockchain.EvmosClusterPersonVoteBean;
import com.wallet.ctc.model.blockchain.EvmosClusterVoteBean;
import com.wallet.ctc.model.blockchain.EvmosClusterVoteDetailBean;
import com.wallet.ctc.model.blockchain.EvmosClusterVoteInfoBean;
import com.wallet.ctc.model.blockchain.EvmosDvmListBean;
import com.wallet.ctc.model.blockchain.EvmosOneBalanceBean;
import com.wallet.ctc.model.blockchain.EvmosSeqGasBean;
import com.wallet.ctc.model.blockchain.RpcApi2;
import com.wallet.ctc.util.AllUtils;
import com.wallet.ctc.util.DecriptUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import common.app.mall.util.ToastUtil;
import common.app.ui.view.InputPwdDialog;
import common.app.utils.SpUtil;
import common.app.utils.digest.EAICoderUtil;
import im.wallet.router.listener.TranslationListener;
import im.wallet.router.wallet.pojo.DeviceGroupMember;
import im.wallet.router.wallet.pojo.EvmosDaoParams;
import im.wallet.router.wallet.pojo.EvmosGroupDataBean;
import im.wallet.router.wallet.pojo.EvmosMyGroupDataBean;
import im.wallet.router.wallet.pojo.EvmosMyGroupList;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


public class TransferControlApi implements ITransferControlApi {

    private RpcApi2 mRpcApi;

    public TransferControlApi() {
        mRpcApi = new RpcApi2();
    }

    @Override
    public Observable<DeviceGroupPageData> getDeviceGroupInfo(String fromAddr, String groupId) {
        return Observable.create(new ObservableOnSubscribe<DeviceGroupPageData>() {
            @Override
            public void subscribe(ObservableEmitter<DeviceGroupPageData> emitter) throws Exception {
                DeviceGroupPageData data = new DeviceGroupPageData();
                EvmosGroupDataBean groupData = ChatSdk.httpGetDeviceGroupData(groupId);
                EvmosMyGroupDataBean myData = ChatSdk.httpGetSomeGroupData(fromAddr, groupId);
                data.groupData = groupData;
                data.myData = myData;
                emitter.onNext(data);
                emitter.onComplete();
            }
        });
    }

    @Override
    public Observable<EvmosDaoParams> getDaoParams() {
        return Observable.create(new ObservableOnSubscribe<EvmosDaoParams>() {
            @Override
            public void subscribe(ObservableEmitter<EvmosDaoParams> emitter) throws Exception {
                EvmosDaoParams daoParams = ChatSdk.httpGetDaoParams();
                emitter.onNext(daoParams);
                emitter.onComplete();
            }
        });
    }

    @Override
    public Observable<EvmosDvmListBean> getMyDvmList(String accountAddr) {
        return Observable.create(new ObservableOnSubscribe<EvmosDvmListBean>() {
            @Override
            public void subscribe(ObservableEmitter<EvmosDvmListBean> emitter) throws Exception {
                EvmosDvmListBean dvmList = ChatSdk.httpGetMyDvmList(accountAddr);
                emitter.onNext(dvmList);
                emitter.onComplete();
            }
        });
    }

    @Override
    public Observable<EvmosDaoParams> getDaoCompositeParams(Context context, String fromAddr) {
        return Observable.create(new ObservableOnSubscribe<EvmosDaoParams>() {
            @Override
            public void subscribe(ObservableEmitter<EvmosDaoParams> emitter) throws Exception {
                String coinName = context.getString(R.string.default_token_name2);
                AssertBean assertBean = WalletDBUtil.getInstent(context).getWalletAssets(WalletUtil.MCC_COIN, coinName);
                int decimal = 18;
                if (null != assertBean) {
                    decimal = assertBean.getDecimal();
                }
                EvmosDaoParams daoParams = ChatSdk.httpGetDaoParams();
                if (daoParams == null) {
                    daoParams = new EvmosDaoParams();
                    daoParams.setStatus(0);
                    daoParams.setInfo("get daoParams is null");
                }
                EvmosMyGroupList groupInfo = ChatSdk.httpGetMyDeviceGroups(fromAddr);
                if (groupInfo != null && groupInfo.isSuccess()) {
                    daoParams.freezeNum = null != groupInfo.data ? groupInfo.data.getFreezePower() : "0";
                } else {
                    daoParams.setStatus(0);
                    String errorInfo = groupInfo != null ? groupInfo.getInfo() : "get group info fail";
                    daoParams.setInfo(errorInfo);
                }
                EvmosOneBalanceBean result = ChatSdk.getOneBalance(fromAddr, coinName);
                if (result != null && result.isSuccess()) {
                    String tenNum = result.getBalance(decimal);
                    if (TextUtils.isEmpty(tenNum)) {
                        tenNum = "0";
                    }
                    daoParams.balance = tenNum;
                } else {
                    daoParams.setStatus(0);
                    String errorInfo = result != null ? result.getInfo() : "get balance fail";
                    daoParams.setInfo(errorInfo);
                }
                emitter.onNext(daoParams);
                emitter.onComplete();
            }
        });
    }

    @Override
    public Observable<EvmosSeqGasBean> getGasAndRewardAmount(int rewardType, String fromAddr, String groupId) {
        Observable<EvmosSeqGasBean> gasObservable = null;
        if (rewardType == 0) {
            
            gasObservable = mRpcApi.getWithdrawDeviceRewardGas(fromAddr, groupId);
        } else if (rewardType == 1) {
            
            gasObservable = mRpcApi.getWithdrawBurnRewardGas(fromAddr, groupId);
        } else if (rewardType == 2) {
            
            gasObservable = mRpcApi.getWithdrawOwnerRewardGas(fromAddr, groupId);
        }
        return Observable.zip(gasObservable,
                getMyGroupData(fromAddr, groupId),
                (gasBean, groupData) -> {
                    if (gasBean == null) {
                        gasBean = new EvmosSeqGasBean();
                    }
                    if (groupData == null || groupData.data == null || !groupData.isSuccess()) {
                        String errorInfo = groupData != null ? groupData.getInfo() : "get reward amount fail";
                        if (groupData.isSuccess() && groupData.data == null) {
                            errorInfo = "get my group data is null";
                        }
                        gasBean.setStatus(0);
                        gasBean.setInfo(errorInfo);
                    } else {
                        if (rewardType == 0) {
                            
                            gasBean.rewardAmount = groupData.data.device_reward;
                        } else if (rewardType == 1) {
                            
                            gasBean.rewardAmount = groupData.data.power_reward;
                        } else if (rewardType == 2) {
                            
                            gasBean.rewardAmount = groupData.data.owner_reward;
                        }
                    }
                    return gasBean;
                });
    }


    @Override
    public Observable<EvmosMyGroupDataBean> getMyGroupData(String fromAddr, String groupId) {
        return Observable.create(new ObservableOnSubscribe<EvmosMyGroupDataBean>() {
            @Override
            public void subscribe(ObservableEmitter<EvmosMyGroupDataBean> emitter) throws Exception {
                EvmosMyGroupDataBean groupData = ChatSdk.httpGetSomeGroupData(fromAddr, groupId);
                if (null == groupData) {
                    groupData = new EvmosMyGroupDataBean();
                }
                emitter.onNext(groupData);
                emitter.onComplete();
            }
        });
    }

    
    @Override
    public Observable<String> getDstPancakeSwapPrice() {
        AssertBean dstAsserts = Constants.getDstBscAssets();
        AssertBean usdtAsserts = Constants.getUSDTBscAssets();
        return mRpcApi.getPancakeSwapAmountsOut("1", dstAsserts, usdtAsserts)
                .map(new Function<CoinPriceBean, String>() {
                    @Override
                    public String apply(CoinPriceBean coinPriceBean) throws Exception {
                        if (coinPriceBean != null) {
                            return coinPriceBean.price;
                        }
                        return "";
                    }
                });
    }

    private WalletEntity checkWalletValidate(Context context, String fromAddr, TranslationListener callBack) {
        WalletEntity walletEntity = WalletDBUtil.getInstent(context).getWalletInfoByAddress(fromAddr, WalletUtil.MCC_COIN);
        if (null == walletEntity) {
            String errorinfo = context.getString(R.string.no_found_wallet_info);
            ToastUtil.showToast(errorinfo);
            if (null != callBack) {
                callBack.onFail(errorinfo);
            }
            return null;
        }
        return walletEntity;
    }

    @Override
    public void addDeviceMembers(Context context, String fromAddr, String groupId,
                                 List<DeviceGroupMember> members, TranslationListener callBack) {
        WalletEntity wallet = checkWalletValidate(context, fromAddr, callBack);
        if (null == wallet) {
            return;
        }
        TranslationControlDialog dialog = new TranslationControlDialog(context, true);
        dialog.doTranslate(wallet, context.getResources().getString(R.string.gas_alert_add_group_member), "", "",
                mRpcApi.getAddDeviceMembersGas(fromAddr, groupId, members),
                new DefaultTransferListener(callBack) {
                    @Override
                    public byte[] signData() {
                        return ChatSdk.signDaoClusterAddMembers(groupId, members);
                    }
                });

    }

    @Override
    public void changeDeviceGroupName(Context context, String fromAddr, String groupId,
                                      String newGroupName, TranslationListener callBack) {
        WalletEntity wallet = checkWalletValidate(context, fromAddr, callBack);
        if (null == wallet) {
            return;
        }
        TranslationControlDialog dialog = new TranslationControlDialog(context, true);
        dialog.doTranslate(wallet, context.getResources().getString(R.string.gas_alert_change_group_name), "", "",
                mRpcApi.getChangGroupNameGas(fromAddr, groupId, newGroupName),
                new DefaultTransferListener(callBack) {
                    @Override
                    public byte[] signData() {
                        return ChatSdk.signDaoClusterChangeName(groupId, newGroupName);
                    }
                });
    }

    @Override
    public void exitDeviceGroup(Context context, String fromAddr, String groupId, TranslationListener callBack) {
        WalletEntity wallet = checkWalletValidate(context, fromAddr, callBack);
        if (null == wallet) {
            return;
        }
        TranslationControlDialog dialog = new TranslationControlDialog(context, true);
        dialog.doTranslate(wallet, context.getResources().getString(R.string.gas_alert_exit_device_group), "", "",
                mRpcApi.getExitDeviceGas(fromAddr, groupId),
                new DefaultTransferListener(callBack) {
                    @Override
                    public byte[] signData() {
                        return ChatSdk.signDaoClusterMemberExit(groupId);
                    }
                });
    }

    @SuppressLint("CheckResult")
    @Override
    public void burnToPower(Context context, String fromAddr, String toAddr, String groupId,
                            String burnAmount, String useFreezeNum, TranslationListener callBack) {
        WalletEntity wallet = checkWalletValidate(context, fromAddr, callBack);
        if (null == wallet) {
            return;
        }
        String dstCoinName = context.getString(R.string.default_token_name2);
        AssertBean assertBean = WalletDBUtil.getInstent(context).getWalletAssets(WalletUtil.MCC_COIN, dstCoinName);
        int decimal = 18;
        if (null != assertBean) {
            decimal = assertBean.getDecimal();
        }
        String bigburnAmount = AllUtils.getBigDecimalValue(burnAmount, decimal);
        String chatAddress = wallet.getChatAddress();
        TranslationControlDialog dialog = new TranslationControlDialog(context, true);
        dialog.showLoadingDialog();
        mRpcApi.getEvmosGateway(SpUtil.getNodeNoSegm()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(evmosGatewayBean -> {
                            if (evmosGatewayBean != null && evmosGatewayBean.isSuccess() &&
                                    evmosGatewayBean.data != null && !TextUtils.isEmpty(evmosGatewayBean.data.gateway_address)) {
                                String gateWayAddr = evmosGatewayBean.data.gateway_address;
                                dialog.doTranslate(wallet, context.getResources().getString(R.string.gas_alert_burn_to_power), toAddr, burnAmount,
                                        mRpcApi.getBurnPowerGas(fromAddr, gateWayAddr, toAddr, groupId, bigburnAmount, useFreezeNum, chatAddress),
                                        new DefaultTransferListener(callBack) {
                                            @Override
                                            public byte[] signData() {
                                                return ChatSdk.signDaoBurnToPower(toAddr, groupId, gateWayAddr, chatAddress, bigburnAmount, useFreezeNum);
                                            }
                                        });
                            } else {
                                dialog.dismissLoadingDialog();
                                dialog.destroy();
                                String errorInfo = evmosGatewayBean != null ? evmosGatewayBean.getInfo() : "get gateWay info error";
                                ToastUtil.showToast(errorInfo);
                                if (null != callBack) {
                                    callBack.onFail(errorInfo);
                                }
                            }
                        },
                        throwable -> {
                            dialog.dismissLoadingDialog();
                            dialog.destroy();
                            throwable.printStackTrace();
                            if (null != callBack) {
                                callBack.onFail(throwable + ":" + throwable.getMessage());
                            }
                        });
    }

    @Override
    public void changeDeviceRatio(Context context, String fromAddr, String groupId, String deviceRatio,
                                  TranslationListener callBack) {
        WalletEntity wallet = checkWalletValidate(context, fromAddr, callBack);
        if (null == wallet) {
            return;
        }
        TranslationControlDialog dialog = new TranslationControlDialog(context, true);
        dialog.doTranslate(wallet, context.getResources().getString(R.string.gas_alert_change_yongjin_radio), "", "",
                mRpcApi.getChangeDeviceRatioGas(fromAddr, groupId, deviceRatio),
                new DefaultTransferListener(callBack) {
                    @Override
                    public byte[] signData() {
                        return ChatSdk.signDaoClusterChangeDeviceRatio(groupId, deviceRatio);
                    }
                });
    }

    @Override
    public void changeSalaryRatio(Context context, String fromAddr, String groupId, String salaryRatio,
                                  TranslationListener callBack) {
        WalletEntity wallet = checkWalletValidate(context, fromAddr, callBack);
        if (null == wallet) {
            return;
        }
        TranslationControlDialog dialog = new TranslationControlDialog(context, true);
        dialog.doTranslate(wallet, context.getResources().getString(R.string.gas_alert_change_gongzi_radio), "", "",
                mRpcApi.getChangeSalaryRatioGas(fromAddr, groupId, salaryRatio),
                new DefaultTransferListener(callBack) {
                    @Override
                    public byte[] signData() {
                        return ChatSdk.signDaoClusterChangeSalaryRatio(groupId, salaryRatio);
                    }
                });
    }

    @Override
    public void changeGroupId(Context context, String fromAddr, String groupId, String newGroupId,
                              TranslationListener callBack) {
        WalletEntity wallet = checkWalletValidate(context, fromAddr, callBack);
        if (null == wallet) {
            return;
        }
        TranslationControlDialog dialog = new TranslationControlDialog(context, true);
        dialog.doTranslate(wallet, context.getResources().getString(R.string.gas_alert_move_group), "", "",
                mRpcApi.getChangeGroupIdGas(fromAddr, groupId, newGroupId),
                new DefaultTransferListener(callBack) {
                    @Override
                    public byte[] signData() {
                        return ChatSdk.signDaoClusterChangeId(groupId, newGroupId);
                    }
                });
    }

    @Override
    public void withdrawBurnReward(Context context, String fromAddr, String groupId,
                                   TranslationListener callBack) {
        WalletEntity wallet = checkWalletValidate(context, fromAddr, callBack);
        if (null == wallet) {
            return;
        }
        TranslationControlDialog dialog = new TranslationControlDialog(context, true);
        dialog.doTranslate(wallet, context.getResources().getString(R.string.gas_alert_ling_power_reward), "", "",
                mRpcApi.getWithdrawBurnRewardGas(fromAddr, groupId),
                new DefaultTransferListener(callBack) {
                    @Override
                    public byte[] signData() {
                        return ChatSdk.signDaoWithdrawBurnReward(groupId);
                    }
                });
    }

    @Override
    public void withdrawBurnReward(Context context, String fromAddr, String groupId, EvmosSeqGasBean gasBean,
                                   TranslationListener callBack) {
        WalletEntity wallet = checkWalletValidate(context, fromAddr, callBack);
        if (null == wallet) {
            return;
        }
        if (gasBean == null || gasBean.seqAccount == null) {
            if (callBack != null) {
                callBack.onFail("gas data can not null");
            }
            return;
        }
        TranslationControlDialog dialog = new TranslationControlDialog(context, true);
        dialog.doTranlste(wallet, gasBean, new DefaultTransferListener(callBack) {
            @Override
            public byte[] signData() {
                return ChatSdk.signDaoWithdrawBurnReward(groupId);
            }
        });
    }

    @Override
    public void withdrawDeviceReward(Context context, String fromAddr, String groupId,
                                     TranslationListener callBack) {
        WalletEntity wallet = checkWalletValidate(context, fromAddr, callBack);
        if (null == wallet) {
            return;
        }
        TranslationControlDialog dialog = new TranslationControlDialog(context, true);
        dialog.doTranslate(wallet, context.getResources().getString(R.string.gas_alert_lingqu_pos), "", "",
                mRpcApi.getWithdrawDeviceRewardGas(fromAddr, groupId),
                new DefaultTransferListener(callBack) {
                    @Override
                    public byte[] signData() {
                        return ChatSdk.signDaoWithdrawDeviceReward(groupId);
                    }
                });
    }

    @Override
    public void withdrawDeviceReward(Context context, String fromAddr, String groupId, EvmosSeqGasBean gasBean, TranslationListener callBack) {
        WalletEntity wallet = checkWalletValidate(context, fromAddr, callBack);
        if (null == wallet) {
            return;
        }
        if (gasBean == null || gasBean.seqAccount == null) {
            if (callBack != null) {
                callBack.onFail("gas data can not null");
            }
            return;
        }
        TranslationControlDialog dialog = new TranslationControlDialog(context, true);
        dialog.doTranlste(wallet, gasBean, new DefaultTransferListener(callBack) {
            @Override
            public byte[] signData() {
                return ChatSdk.signDaoWithdrawDeviceReward(groupId);
            }
        });
    }

    @Override
    public void withdrawOwnerReward(Context context, String fromAddr, String groupId, TranslationListener callBack) {
        WalletEntity wallet = checkWalletValidate(context, fromAddr, callBack);
        if (null == wallet) {
            return;
        }
        TranslationControlDialog dialog = new TranslationControlDialog(context, true);
        dialog.doTranslate(wallet, context.getResources().getString(R.string.gas_alert_lingqu_gongzi), "", "",
                mRpcApi.getWithdrawOwnerRewardGas(fromAddr, groupId),
                new DefaultTransferListener(callBack) {
                    @Override
                    public byte[] signData() {
                        return ChatSdk.signDaoWithdrawOwnerReward(groupId);
                    }
                });
    }

    @Override
    public void withdrawOwnerReward(Context context, String fromAddr, String groupId, EvmosSeqGasBean gasBean, TranslationListener callBack) {
        WalletEntity wallet = checkWalletValidate(context, fromAddr, callBack);
        if (null == wallet) {
            return;
        }
        if (gasBean == null || gasBean.seqAccount == null) {
            if (callBack != null) {
                callBack.onFail("gas data can not null");
            }
            return;
        }
        TranslationControlDialog dialog = new TranslationControlDialog(context, true);
        dialog.doTranlste(wallet, gasBean, new DefaultTransferListener(callBack) {
            @Override
            public byte[] signData() {
                return ChatSdk.signDaoWithdrawOwnerReward(groupId);
            }
        });
    }

    @Override
    public void withdrawAirDropReward(Context context, String fromAddr, String serverName, TranslationListener callBack) {
        WalletEntity wallet = checkWalletValidate(context, fromAddr, callBack);
        if (null == wallet) {
            return;
        }
        InputPwdDialog mPwdDialog = new InputPwdDialog(context, context.getString(R.string.place_edit_password));
        mPwdDialog.setonclick(new InputPwdDialog.Onclick() {
            @SuppressLint("CheckResult")
            @Override
            public void Yes(String pwd) {
                if (TextUtils.isEmpty(pwd)) {
                    ToastUtil.showToast(R.string.place_edit_password);
                    return;
                }
                if (!wallet.getmPassword().equals(DecriptUtil.MD5(pwd))) {
                    ToastUtil.showToast(com.wallet.ctc.R.string.password_error2);
                    return;
                }
                mPwdDialog.dismiss();

                String privateKey = WalletUtil.getDecryptionKey(wallet.getmPrivateKey(), pwd);
                String timestamp = System.currentTimeMillis() / 1000 + "";
                String publicKey = WalletUtil.getCosmosCompressPublickey(privateKey);
                String walletSign = WalletUtil.cosmosSign(privateKey, publicKey, fromAddr, timestamp, WalletUtil.MCC_COIN);
                StringBuilder signStr = new StringBuilder();
                signStr.append(fromAddr).append("_")
                        .append(publicKey).append("_")
                        .append(serverName).append("_")
                        .append(timestamp).append("_")
                        .append(walletSign).append(publicKey);
                String apiSign = EAICoderUtil.getMD5Code(signStr.toString());
                JSONObject paramsJson = new JSONObject();
                try {
                    paramsJson.put("addr", fromAddr);
                    paramsJson.put("servername", serverName);
                    paramsJson.put("timestamp", timestamp);
                    paramsJson.put("wallet_sign", walletSign);
                    paramsJson.put("wallet_pub", publicKey);
                    paramsJson.put("api_sign", apiSign);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String hexSignStr = EAICoderUtil.getAESToHexCode(paramsJson.toString(), "At$Tr_ibu$*tasdfAt$Tr_edv$*tFg8d");
                TranslationControlDialog dialog = new TranslationControlDialog(context, true);
                dialog.showLoadingDialog();
                mRpcApi.doGetAirDrop(hexSignStr).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result -> {
                            dialog.dismissLoadingDialog();
                            if (null != result && !TextUtils.isEmpty(result.id)) {
                                ToastUtil.showToast(context.getString(R.string.operate_success_waiting));
                                if (null != callBack) {
                                    callBack.setData(result.amount);
                                    callBack.onTransSuccess();
                                }
                            } else {
                                String errorMsg = result != null ? result.msg : context.getString(R.string.caozuo_fail);
                                ToastUtil.showToast(errorMsg);
                            }
                        }, throwable -> {
                            dialog.dismissLoadingDialog();
                            throwable.printStackTrace();
                            ToastUtil.showToast(throwable + ":" + throwable.getMessage());
                        });
            }

            @Override
            public void No() {
                mPwdDialog.dismiss();
            }
        });
        mPwdDialog.show();

    }

    @SuppressLint("CheckResult")
    @Override
    public void thawFrozenPower(Context context, String fromAddr, String groupId, String thawAmount,
                                TranslationListener callBack) {
        WalletEntity wallet = checkWalletValidate(context, fromAddr, callBack);
        if (null == wallet) {
            return;
        }
        String chatAddress = wallet.getChatAddress();
        TranslationControlDialog dialog = new TranslationControlDialog(context, true);
        dialog.showLoadingDialog();
        mRpcApi.getEvmosGateway(SpUtil.getNodeNoSegm()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(evmosGatewayBean -> {
                            if (evmosGatewayBean != null && evmosGatewayBean.isSuccess() &&
                                    evmosGatewayBean.data != null && !TextUtils.isEmpty(evmosGatewayBean.data.gateway_address)) {
                                String gateWayAddr = evmosGatewayBean.data.gateway_address;
                                dialog.doTranslate(wallet, context.getResources().getString(R.string.gas_alert_unfree_power), "", "",
                                        mRpcApi.getThawFrozenPowerGas(fromAddr, gateWayAddr, groupId, thawAmount, chatAddress),
                                        new DefaultTransferListener(callBack) {
                                            @Override
                                            public byte[] signData() {
                                                return ChatSdk.signDaoThawFrozenPower(groupId, gateWayAddr, chatAddress, thawAmount);
                                            }
                                        });
                            } else {
                                dialog.dismissLoadingDialog();
                                dialog.destroy();
                                String errorInfo = evmosGatewayBean != null ? evmosGatewayBean.getInfo() : "get gateWay info error";
                                ToastUtil.showToast(errorInfo);
                                if (null != callBack) {
                                    callBack.onFail(errorInfo);
                                }
                            }
                        },
                        throwable -> {
                            dialog.dismissLoadingDialog();
                            dialog.destroy();
                            throwable.printStackTrace();
                            if (null != callBack) {
                                callBack.onFail(throwable + ":" + throwable.getMessage());
                            }
                        });
    }

    
    @Override
    public void groupGovernancePoolsVote(Context context, String policyAddress, String fromAddr, String clusterId, String amount, String toAddress,
                                         String title, String description, TranslationListener callBack) {
        WalletEntity wallet = checkWalletValidate(context, fromAddr, callBack);
        if (null == wallet) return;

        TranslationControlDialog dialog = new TranslationControlDialog(context, true);
        String dstCoinName = context.getString(R.string.default_token_name2);
        AssertBean assertBean = WalletDBUtil.getInstent(context).getWalletAssets(WalletUtil.MCC_COIN, dstCoinName);
        int decimal = 18;
        if (null != assertBean) decimal = assertBean.getDecimal();
        String bigburnAmount = AllUtils.getBigDecimalValue(amount, decimal);
        dialog.doTranslate(wallet, context.getResources().getString(R.string.gas_alert_group_vote_zhichu), amount, toAddress,
                mRpcApi.getGroupVoteGas(fromAddr, policyAddress, title, description),
                new DefaultTransferListener(callBack) {
                    @Override
                    public byte[] signData() {
                        return ChatSdk.signGroupSubmitTransferProposal(clusterId, bigburnAmount, toAddress, title, description);
                    }
                });
    }


    
    @Override
    public void commissionVote(Context context, String policyAddress, String fromAddr, String clusterId, String radio, String title, String description, TranslationListener callBack) {
        WalletEntity wallet = checkWalletValidate(context, fromAddr, callBack);
        if (null == wallet) return;

        TranslationControlDialog dialog = new TranslationControlDialog(context, true);
        dialog.doTranslate(wallet, title, "", "",
                mRpcApi.getGroupVoteGas(fromAddr, policyAddress, title, description),
                new DefaultTransferListener(callBack) {
                    @Override
                    public byte[] signData() {
                        return ChatSdk.groupSubmitChangeDeviceRatioProposal(clusterId, radio, title, description);
                    }
                });
    }

    
    @Override
    public void contributionVote(Context context, String policyAddress, String fromAddr, String clusterId, String radio, String title, String description, TranslationListener callBack) {
        WalletEntity wallet = checkWalletValidate(context, fromAddr, callBack);
        if (null == wallet) return;

        TranslationControlDialog dialog = new TranslationControlDialog(context, true);
        dialog.doTranslate(wallet, "", "", "",
                mRpcApi.getGroupVoteGas(fromAddr, policyAddress, title, description),
                new DefaultTransferListener(callBack) {
                    @Override
                    public byte[] signData() {
                        return ChatSdk.groupSubmitChangeSalaryRatioProposal(clusterId, radio, title, description);
                    }
                });
    }

    
    @Override
    public void groupDaoAuthorizationVote(Context context, String policyAddress, String fromAddr, String clusterId, String endBlock, String toAddress, String title, String description, TranslationListener callBack) {
        WalletEntity wallet = checkWalletValidate(context, fromAddr, callBack);
        if (null == wallet) return;

        TranslationControlDialog dialog = new TranslationControlDialog(context, true);
        dialog.doTranslate(wallet, context.getResources().getString(R.string.gas_alert_dao_power_auth), "", toAddress,
                mRpcApi.getGroupVoteGas(fromAddr, policyAddress, title, description),
                new DefaultTransferListener(callBack) {
                    @Override
                    public byte[] signData() {
                        return ChatSdk.signGroupDaoAuthorizationProposal(clusterId, endBlock, toAddress, title, description);
                    }
                });
    }

    
    @Override
    public void groupProposalVote(Context context, String fromAddress, int proposalId, int option, String option2, TranslationListener callBack) {
        WalletEntity wallet = checkWalletValidate(context, fromAddress, callBack);
        if (null == wallet) return;
        TranslationControlDialog dialog = new TranslationControlDialog(context, true);
        dialog.doTranslate(wallet, context.getResources().getString(R.string.gas_alert_group_vote), "", "",
                mRpcApi.getGroupVote(fromAddress, proposalId, option),
                new DefaultTransferListener(callBack) {
                    @Override
                    public byte[] signData() {
                        return ChatSdk.signGroupProposalVote(proposalId, option2);
                    }
                });
    }

    @Override
    public Observable<EvmosClusterVoteBean> getClusterVoteList(String clusterId) {
        return Observable.create(emitter -> {
            EvmosClusterVoteBean dvmList = ChatSdk.httpGetClusterVoteList(clusterId);
            emitter.onNext(dvmList);
            emitter.onComplete();
        });
    }

    
    @Override
    public void signPersonDvmApprove(Context context, String fromAddress, String approveAddress, String clusterId, String approveEndBlock, TranslationListener callBack) {
        WalletEntity wallet = checkWalletValidate(context, fromAddress, callBack);
        if (null == wallet) return;

        TranslationControlDialog dialog = new TranslationControlDialog(context, true);
        dialog.doTranslate(wallet, context.getResources().getString(R.string.gas_alert_dvm_auth), "", "",
                mRpcApi.personDvmApproveGas(fromAddress, approveAddress, clusterId, approveEndBlock),
                new DefaultTransferListener(callBack) {
                    @Override
                    public byte[] signData() {
                        return ChatSdk.signPersonDvmApprove(approveAddress, clusterId, approveEndBlock);
                    }
                });
    }

    
    @Override
    public Observable<EvmosClusterVoteDetailBean> getClusterVoteDetail(int proposalId) {
        return Observable.create(emitter -> {
            EvmosClusterVoteDetailBean dvmList = ChatSdk.httpGetClusterVoteDetail(proposalId);
            emitter.onNext(dvmList);
            emitter.onComplete();
        });
    }

    public Observable<EvmosClusterVoteInfoBean> getVoteDetail(int proposalId) {
        return Observable.create(emitter -> {
            EvmosClusterVoteInfoBean dvmList = ChatSdk.httpGetVoteDetail(proposalId);
            emitter.onNext(dvmList);
            emitter.onComplete();
        });
    }

    @Override
    public Observable<EvmosClusterPersonVoteBean> getClusterVoteAllPersonDetail(int proposalId) {
        return Observable.create(emitter -> {
            EvmosClusterPersonVoteBean dvmList = ChatSdk.httpGetClusterAllPersonVote(proposalId);
            emitter.onNext(dvmList);
            emitter.onComplete();
        });
    }

    @Override
    public Observable<EvmosClusterPersonVoteBean> getClusterVotePersonDetail(int proposalId, String voter) {
        return Observable.create(emitter -> {
            EvmosClusterPersonVoteBean dvmList = ChatSdk.httpGetClusterPersonVote(proposalId, voter);
            emitter.onNext(dvmList);
            emitter.onComplete();
        });
    }
}
