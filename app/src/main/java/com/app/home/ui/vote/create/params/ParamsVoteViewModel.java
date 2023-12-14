package com.app.home.ui.vote.create.params;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.app.pojo.VoteParamsInfoBean;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.wallet.ctc.model.blockchain.RpcApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import common.app.base.BaseViewModel;
import common.app.im.base.NextSubscriber;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


public class ParamsVoteViewModel extends BaseViewModel {
    public MutableLiveData<List<VoteParamsInfoBean>> mListLD;
    private RpcApi mRpcApi;
    private CompositeDisposable mDisposable;

    
    public ParamsVoteViewModel(@NonNull Application application) {
        super(application);
        mListLD = new MutableLiveData<>();
        mRpcApi = new RpcApi();
        mDisposable = new CompositeDisposable();
    }

    public void getDatas(String subSpace) {
        showLoadingDialog("");
        Disposable disposable = mRpcApi.getParamsVoteKeys(subSpace)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new NextSubscriber<JsonObject>() {
                    @Override
                    public void dealData(JsonObject value) {
                        dismissLoadingDialog();
                        if (null != value) {
                            parseResult(subSpace, value.toString());
                        } else {
                            String error = null == value ? "get data is null" : value.toString();
                            showToast(error);
                        }
                    }

                    @Override
                    protected void dealError(Throwable e) {
                        super.dealError(e);
                        dismissLoadingDialog();
                    }
                });
        mDisposable.add(disposable);
    }


    
    private void parseResult(String subSpace, String data) {
        List<VoteParamsInfoBean> list = new ArrayList<>();
        Gson gson = new Gson();
        JsonObject jsonData = gson.fromJson(data, JsonObject.class);
        JsonElement resultJson = jsonData.get("params");
        if (null == resultJson) {
            showToast("result get is null");
            return;
        }
        if ("gov".equals(subSpace)) {
            VoteParamsInfoBean.GovParams govBean = gson.fromJson(resultJson, VoteParamsInfoBean.GovParams.class);
            if (null == govBean) {
                showToast("parse gov result map is empty");
                return;
            }
            Map<String,JsonElement> allMaps = govBean.getAllMaps();
            if (null == allMaps || allMaps.isEmpty()) {
                showToast("parse gov result map is still empty");
                return;
            }
            list.addAll(fromMap(subSpace, allMaps));
        } else {
            Map<String,JsonElement> resultMap = gson.fromJson(resultJson, new TypeToken<Map<String,JsonElement>>(){}.getType());
            if (null != resultMap && !resultMap.isEmpty()) {
                list.addAll(fromMap(subSpace, resultMap));
            } else {
                showToast("parse result map is empty");
            }
        }
        mListLD.setValue(list);
    }

    private List<VoteParamsInfoBean> fromMap(String subSpace, Map<String,JsonElement> resultMap) {
        List<VoteParamsInfoBean> list = VoteParamsData.getKeys(subSpace);

        if (null != list && list.size() > 0 && null != resultMap && !resultMap.isEmpty()) {
            for (int i =0; i<list.size(); i++) {
                String keyAlise = list.get(i).keyAlias;
                String key = list.get(i).key;
                boolean isBigAmount = list.get(i).isBigAmount;
                JsonElement value = null;
                if (resultMap.containsKey(keyAlise)) {
                    value = resultMap.get(keyAlise);
                } else if(resultMap.containsKey(key)) {
                    value = resultMap.get(key);
                }
                if (null != value) {
                    if (value.isJsonPrimitive()) {
                        list.get(i).setValue(value.getAsJsonPrimitive().getAsString());
                    } else if(value.isJsonObject()) {
                        JsonObject obj = value.getAsJsonObject();
                        if (obj.has("denom") && obj.has("amount")) {
                            String denom = obj.get("denom").getAsString();
                            String amount = obj.get("amount").getAsString();
                            list.get(i).setAmountDenom(denom, amount);
                            list.get(i).isBigAmount = true;
                        }
                    }
                }
            }
        }
        return list;
    }



    @Override
    protected void onCleared() {
        super.onCleared();
        mDisposable.clear();
    }
}
