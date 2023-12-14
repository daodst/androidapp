

package org.matrix.android.sdk.internal.auth.login

import org.matrix.android.sdk.api.auth.login.LoginProfileInfo
import org.matrix.android.sdk.api.session.content.ContentUrlResolver
import org.matrix.android.sdk.api.session.profile.ProfileService
import org.matrix.android.sdk.internal.auth.AuthAPI
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task

internal interface GetProfileTask : Task<GetProfileTask.Params, LoginProfileInfo> {
    data class Params(
            val userId: String
    )
}

internal class DefaultGetProfileTask(
        private val authAPI: AuthAPI,
        private val contentUrlResolver: ContentUrlResolver
) : GetProfileTask {

    override suspend fun execute(params: GetProfileTask.Params): LoginProfileInfo {
        val info = executeRequest(null) {
            authAPI.getProfile(params.userId)
        }

        return LoginProfileInfo(
                matrixId = params.userId,
                displayName = info[ProfileService.DISPLAY_NAME_KEY] as? String,
                fullAvatarUrl = contentUrlResolver.resolveFullSize(info[ProfileService.AVATAR_URL_KEY] as? String)
        )
    }
}
