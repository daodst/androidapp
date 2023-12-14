

package org.matrix.android.sdk.api.session.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import org.matrix.android.sdk.api.auth.UserInteractiveAuthInterceptor
import org.matrix.android.sdk.api.session.identity.ThreePid
import org.matrix.android.sdk.api.session.user.model.User
import org.matrix.android.sdk.api.util.JsonDict
import org.matrix.android.sdk.api.util.Optional


interface ProfileService {

    companion object Constants {
        const val DISPLAY_NAME_KEY = "displayname"
        const val AVATAR_URL_KEY = "avatar_url"
        const val TEL_NUMBERS_KEY = "tel_numbers"
    }

    
    suspend fun getDisplayName(userId: String): Optional<String>

    
    suspend fun setDisplayName(userId: String, newDisplayName: String)

    
    suspend fun updateAvatar(userId: String, newAvatarUri: Uri, fileName: String)

    
    suspend fun getAvatarUrl(userId: String): Optional<String>

    
    suspend fun getProfile(userId: String): JsonDict

    
    fun getThreePids(): List<ThreePid>

    
    fun getThreePidsLive(refreshData: Boolean): LiveData<List<ThreePid>>

    
    fun getPendingThreePids(): List<ThreePid>

    
    fun getPendingThreePidsLive(): LiveData<List<ThreePid>>

    
    suspend fun addThreePid(threePid: ThreePid)

    
    suspend fun submitSmsCode(threePid: ThreePid.Msisdn, code: String)

    
    suspend fun finalizeAddingThreePid(threePid: ThreePid,
                                       userInteractiveAuthInterceptor: UserInteractiveAuthInterceptor)

    
    suspend fun cancelAddingThreePid(threePid: ThreePid)

    
    suspend fun deleteThreePid(threePid: ThreePid)

    
    suspend fun getProfileAsUser(userId: String): User {
        return getProfile(userId).let { dict ->
            User(
                    userId = userId,
                    displayName = dict[DISPLAY_NAME_KEY] as? String,
                    avatarUrl = dict[AVATAR_URL_KEY] as? String
            )
        }
    }
}
