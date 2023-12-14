

package org.matrix.android.sdk.internal.crypto.crosssigning

import android.content.Context
import androidx.work.WorkerParameters
import com.squareup.moshi.JsonClass
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.kotlin.where
import org.matrix.android.sdk.api.extensions.orFalse
import org.matrix.android.sdk.api.session.crypto.crosssigning.MXCrossSigningInfo
import org.matrix.android.sdk.api.session.crypto.crosssigning.UserTrustResult
import org.matrix.android.sdk.api.session.crypto.crosssigning.isCrossSignedVerified
import org.matrix.android.sdk.api.session.crypto.crosssigning.isVerified
import org.matrix.android.sdk.api.session.crypto.model.RoomEncryptionTrustLevel
import org.matrix.android.sdk.internal.SessionManager
import org.matrix.android.sdk.internal.crypto.store.IMXCryptoStore
import org.matrix.android.sdk.internal.crypto.store.db.mapper.CrossSigningKeysMapper
import org.matrix.android.sdk.internal.crypto.store.db.model.CrossSigningInfoEntity
import org.matrix.android.sdk.internal.crypto.store.db.model.CrossSigningInfoEntityFields
import org.matrix.android.sdk.internal.crypto.store.db.model.CryptoMapper
import org.matrix.android.sdk.internal.crypto.store.db.model.TrustLevelEntity
import org.matrix.android.sdk.internal.crypto.store.db.model.UserEntity
import org.matrix.android.sdk.internal.crypto.store.db.model.UserEntityFields
import org.matrix.android.sdk.internal.database.awaitTransaction
import org.matrix.android.sdk.internal.database.model.RoomMemberSummaryEntity
import org.matrix.android.sdk.internal.database.model.RoomMemberSummaryEntityFields
import org.matrix.android.sdk.internal.database.model.RoomSummaryEntity
import org.matrix.android.sdk.internal.database.model.RoomSummaryEntityFields
import org.matrix.android.sdk.internal.database.query.where
import org.matrix.android.sdk.internal.di.CryptoDatabase
import org.matrix.android.sdk.internal.di.SessionDatabase
import org.matrix.android.sdk.internal.di.UserId
import org.matrix.android.sdk.internal.session.SessionComponent
import org.matrix.android.sdk.internal.session.room.membership.RoomMemberHelper
import org.matrix.android.sdk.internal.util.logLimit
import org.matrix.android.sdk.internal.worker.SessionSafeCoroutineWorker
import org.matrix.android.sdk.internal.worker.SessionWorkerParams
import timber.log.Timber
import javax.inject.Inject

internal class UpdateTrustWorker(context: Context, params: WorkerParameters, sessionManager: SessionManager) :
        SessionSafeCoroutineWorker<UpdateTrustWorker.Params>(context, params, sessionManager, Params::class.java) {

    @JsonClass(generateAdapter = true)
    internal data class Params(
            override val sessionId: String,
            override val lastFailureMessage: String? = null,
            
            val updatedUserIds: List<String>? = null,
            
            
            val filename: String? = null
    ) : SessionWorkerParams

    @Inject lateinit var crossSigningService: DefaultCrossSigningService

    
    @CryptoDatabase
    @Inject lateinit var cryptoRealmConfiguration: RealmConfiguration

    @SessionDatabase
    @Inject lateinit var sessionRealmConfiguration: RealmConfiguration

    @UserId
    @Inject lateinit var myUserId: String
    @Inject lateinit var crossSigningKeysMapper: CrossSigningKeysMapper
    @Inject lateinit var updateTrustWorkerDataRepository: UpdateTrustWorkerDataRepository

    
    @Inject lateinit var cryptoStore: IMXCryptoStore

    override fun injectWith(injector: SessionComponent) {
        injector.inject(this)
    }

    override suspend fun doSafeWork(params: Params): Result {
        val userList = params.filename
                ?.let { updateTrustWorkerDataRepository.getParam(it) }
                ?.userIds
                ?: params.updatedUserIds.orEmpty()

        
        if (userList.isNotEmpty()) {
            
            
            Timber.v("## CrossSigning - Updating trust for users: ${userList.logLimit()}")
            updateTrust(userList)
        }

        cleanup(params)
        return Result.success()
    }

    private suspend fun updateTrust(userListParam: List<String>) {
        var userList = userListParam
        var myCrossSigningInfo: MXCrossSigningInfo? = null

        
        
        awaitTransaction(cryptoRealmConfiguration) { cryptoRealm ->
            
            
            myCrossSigningInfo = getCrossSigningInfo(cryptoRealm, myUserId)

            var myTrustResult: UserTrustResult? = null

            if (userList.contains(myUserId)) {
                Timber.d("## CrossSigning - Clear all trust as a change on my user was detected")
                
                
                
                userList = cryptoRealm.where(CrossSigningInfoEntity::class.java)
                        .findAll()
                        .mapNotNull { it.userId }

                
                val myDevices = cryptoRealm.where<UserEntity>()
                        .equalTo(UserEntityFields.USER_ID, myUserId)
                        .findFirst()
                        ?.devices
                        ?.map { CryptoMapper.mapToModel(it) }

                myTrustResult = crossSigningService.checkSelfTrust(myCrossSigningInfo, myDevices)
                updateCrossSigningKeysTrust(cryptoRealm, myUserId, myTrustResult.isVerified())
                
                myCrossSigningInfo = getCrossSigningInfo(cryptoRealm, myUserId)
            }

            val otherInfos = userList.associateWith { userId ->
                getCrossSigningInfo(cryptoRealm, userId)
            }

            val trusts = otherInfos.mapValues { entry ->
                when (entry.key) {
                    myUserId -> myTrustResult
                    else     -> {
                        crossSigningService.checkOtherMSKTrusted(myCrossSigningInfo, entry.value).also {
                            Timber.v("## CrossSigning - user:${entry.key} result:$it")
                        }
                    }
                }
            }

            
            
            trusts.forEach {
                val verified = it.value?.isVerified() == true
                updateCrossSigningKeysTrust(cryptoRealm, it.key, verified)
            }

            
            Timber.v("## CrossSigning - Updating devices cross trust users: ${trusts.keys.logLimit()}")
            trusts.keys.forEach { userId ->
                val devicesEntities = cryptoRealm.where<UserEntity>()
                        .equalTo(UserEntityFields.USER_ID, userId)
                        .findFirst()
                        ?.devices

                val trustMap = devicesEntities?.associateWith { device ->
                    
                    val otherInfo = getCrossSigningInfo(cryptoRealm, userId)
                    crossSigningService.checkDeviceTrust(myCrossSigningInfo, otherInfo, CryptoMapper.mapToModel(device))
                }

                
                devicesEntities?.forEach { device ->
                    val crossSignedVerified = trustMap?.get(device)?.isCrossSignedVerified()
                    Timber.v("## CrossSigning - Trust for ${device.userId}|${device.deviceId} : cross verified: ${trustMap?.get(device)}")
                    if (device.trustLevelEntity?.crossSignedVerified != crossSignedVerified) {
                        Timber.d("## CrossSigning - Trust change detected for ${device.userId}|${device.deviceId} : cross verified: $crossSignedVerified")
                        
                        val trustEntity = device.trustLevelEntity
                        if (trustEntity == null) {
                            device.trustLevelEntity = cryptoRealm.createObject(TrustLevelEntity::class.java).also {
                                it.locallyVerified = false
                                it.crossSignedVerified = crossSignedVerified
                            }
                        } else {
                            trustEntity.crossSignedVerified = crossSignedVerified
                        }
                    }
                }
            }
        }

        
        
        updateTrustStep2(userList, myCrossSigningInfo)
    }

    private suspend fun updateTrustStep2(userList: List<String>, myCrossSigningInfo: MXCrossSigningInfo?) {
        Timber.d("## CrossSigning - Updating shields for impacted rooms...")
        awaitTransaction(sessionRealmConfiguration) { sessionRealm ->
            Realm.getInstance(cryptoRealmConfiguration).use { cryptoRealm ->
                sessionRealm.where(RoomMemberSummaryEntity::class.java)
                        .`in`(RoomMemberSummaryEntityFields.USER_ID, userList.toTypedArray())
                        .distinct(RoomMemberSummaryEntityFields.ROOM_ID)
                        .findAll()
                        .map { it.roomId }
                        .also { Timber.d("## CrossSigning -  ... impacted rooms ${it.logLimit()}") }
                        .forEach { roomId ->
                            RoomSummaryEntity.where(sessionRealm, roomId)
                                    .equalTo(RoomSummaryEntityFields.IS_ENCRYPTED, true)
                                    .findFirst()
                                    ?.let { roomSummary ->
                                        Timber.v("## CrossSigning - Check shield state for room $roomId")
                                        val allActiveRoomMembers = RoomMemberHelper(sessionRealm, roomId).getActiveRoomMemberIds()
                                        try {
                                            val updatedTrust = computeRoomShield(
                                                    myCrossSigningInfo,
                                                    cryptoRealm,
                                                    allActiveRoomMembers,
                                                    roomSummary
                                            )
                                            if (roomSummary.roomEncryptionTrustLevel != updatedTrust) {
                                                Timber.d("## CrossSigning - Shield change detected for $roomId -> $updatedTrust")
                                                roomSummary.roomEncryptionTrustLevel = updatedTrust
                                            }
                                        } catch (failure: Throwable) {
                                            Timber.e(failure)
                                        }
                                    }
                        }
            }
        }
    }

    private fun getCrossSigningInfo(cryptoRealm: Realm, userId: String): MXCrossSigningInfo? {
        return cryptoRealm.where(CrossSigningInfoEntity::class.java)
                .equalTo(CrossSigningInfoEntityFields.USER_ID, userId)
                .findFirst()
                ?.let { mapCrossSigningInfoEntity(it) }
    }

    private fun cleanup(params: Params) {
        params.filename
                ?.let { updateTrustWorkerDataRepository.delete(it) }
    }

    private fun updateCrossSigningKeysTrust(cryptoRealm: Realm, userId: String, verified: Boolean) {
        cryptoRealm.where(CrossSigningInfoEntity::class.java)
                .equalTo(CrossSigningInfoEntityFields.USER_ID, userId)
                .findFirst()
                ?.crossSigningKeys
                ?.forEach { info ->
                    
                    if (info.trustLevelEntity?.isVerified() != verified) {
                        Timber.d("## CrossSigning - Trust change for $userId : $verified")
                        val level = info.trustLevelEntity
                        if (level == null) {
                            info.trustLevelEntity = cryptoRealm.createObject(TrustLevelEntity::class.java).also {
                                it.locallyVerified = verified
                                it.crossSignedVerified = verified
                            }
                        } else {
                            level.locallyVerified = verified
                            level.crossSignedVerified = verified
                        }
                    }
                }
    }

    private fun computeRoomShield(myCrossSigningInfo: MXCrossSigningInfo?,
                                  cryptoRealm: Realm,
                                  activeMemberUserIds: List<String>,
                                  roomSummaryEntity: RoomSummaryEntity): RoomEncryptionTrustLevel {
        Timber.v("## CrossSigning - computeRoomShield ${roomSummaryEntity.roomId} -> ${activeMemberUserIds.logLimit()}")
        
        
        
        val listToCheck = if (roomSummaryEntity.isDirect || activeMemberUserIds.size <= 2) {
            activeMemberUserIds.filter { it != myUserId }
        } else {
            activeMemberUserIds
        }

        val allTrustedUserIds = listToCheck
                .filter { userId ->
                    getCrossSigningInfo(cryptoRealm, userId)?.isTrusted() == true
                }

        return if (allTrustedUserIds.isEmpty()) {
            RoomEncryptionTrustLevel.Default
        } else {
            
            
            
            allTrustedUserIds
                    .mapNotNull { userId ->
                        cryptoRealm.where<UserEntity>()
                                .equalTo(UserEntityFields.USER_ID, userId)
                                .findFirst()
                                ?.devices
                                ?.map { CryptoMapper.mapToModel(it) }
                    }
                    .flatten()
                    .let { allDevices ->
                        Timber.v("## CrossSigning - computeRoomShield ${roomSummaryEntity.roomId} devices ${allDevices.map { it.deviceId }.logLimit()}")
                        if (myCrossSigningInfo != null) {
                            allDevices.any { !it.trustLevel?.crossSigningVerified.orFalse() }
                        } else {
                            
                            allDevices.any { !it.isVerified }
                        }
                    }
                    .let { hasWarning ->
                        if (hasWarning) {
                            RoomEncryptionTrustLevel.Warning
                        } else {
                            if (listToCheck.size == allTrustedUserIds.size) {
                                
                                RoomEncryptionTrustLevel.Trusted
                            } else {
                                RoomEncryptionTrustLevel.Default
                            }
                        }
                    }
        }
    }

    private fun mapCrossSigningInfoEntity(xsignInfo: CrossSigningInfoEntity): MXCrossSigningInfo {
        val userId = xsignInfo.userId ?: ""
        return MXCrossSigningInfo(
                userId = userId,
                crossSigningKeys = xsignInfo.crossSigningKeys.mapNotNull {
                    crossSigningKeysMapper.map(userId, it)
                }
        )
    }

    override fun buildErrorParams(params: Params, message: String): Params {
        return params.copy(lastFailureMessage = params.lastFailureMessage ?: message)
    }
}
