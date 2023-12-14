
package com.app.levelranking

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.R
import com.wallet.ctc.model.blockchain.RpcApi
import com.wallet.ctc.model.me.SMLevelEntity
import common.app.base.BaseViewModel
import common.app.im.base.NextSubscriber
import common.app.mall.util.ToastUtil
import im.vector.app.core.error.ErrorFormatter
import im.vector.app.core.extensions.singletonEntryPoint
import im.vector.app.provide.ChatStatusProvide
import kotlinx.coroutines.launch


class SMLevelRankingActivityVM

(application: Application) : BaseViewModel(application) {

    
    var mData = MutableLiveData<List<SMLevelEntity>>()

    var mError = MutableLiveData<Boolean>()

    
    var mUserLevel = MutableLiveData<SMLevelEntity>()

    var mRpcApi: RpcApi = RpcApi()

    protected  var errorFormatter: ErrorFormatter

    init {
        val singletonEntryPoint = application.singletonEntryPoint()
        errorFormatter = singletonEntryPoint.errorFormatter()
    }

    fun getPledgeRank() {

        mRpcApi.pledgeRank.subscribeWith(object : NextSubscriber<MutableList<SMLevelEntity>>() {
            override fun dealData(value: MutableList<SMLevelEntity>) {

                val context = getApplication<Application>()
                val address = ChatStatusProvide.getAddress(context)
                val fullAddress = ChatStatusProvide.getFullAddress(context)

                var entityMySelf: SMLevelEntity? = null

                viewModelScope.launch {
                    value.forEachIndexed { index, entity ->
                        entity.ranking = (index + 1).toString()
                        val userId = "@${entity.facc}:${entity.servername}"
                        try {
                            val info = ChatStatusProvide.getUserInfo(context, userId)
                            
                            entity.userId = info.userId
                            entity.displayName = info.displayName
                            entity.avatarUrl = info.avatarUrl

                            if ((info.tel_numbers?.size ?: 0) > 0) {
                                val get = info.tel_numbers?.get(0)
                                entity.idi = get?.toBigDecimal()?.toPlainString() ?: ""
                            }
                        } catch (e: Throwable) {
                            ToastUtil.showToast(errorFormatter.toHumanReadable(e))
                        }
                        
                        if (TextUtils.equals(entity.facc, address)) {
                            entityMySelf = entity
                        }
                    }
                    
                    if (null != entityMySelf) {
                        mUserLevel.value = entityMySelf
                    } else {
                        
                        val mySelf = SMLevelEntity()
                        mySelf.ranking = context.getString(R.string.un_attach_this_level)
                        try {
                            val info = ChatStatusProvide.getUserInfo(context, fullAddress)
                            
                            mySelf.userId = info.userId
                            mySelf.displayName = info.displayName
                            mySelf.avatarUrl = info.avatarUrl
                            if ((info.tel_numbers?.size ?: 0) > 0) {
                                val get = info.tel_numbers?.get(0)
                                mySelf.idi = get?.toBigDecimal()?.toPlainString() ?: ""
                            }
                        } catch (e: Throwable) {
                            ToastUtil.showToast(errorFormatter.toHumanReadable(e))
                        }
                        mUserLevel.value = mySelf
                    }
                    mData.value = value
                }
            }

            override fun dealError(e: Throwable?) {
                super.dealError(e)
                mError.postValue(true);
            }
        })
    }
}


