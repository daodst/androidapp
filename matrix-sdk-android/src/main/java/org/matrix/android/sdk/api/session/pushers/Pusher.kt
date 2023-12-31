
package org.matrix.android.sdk.api.session.pushers

data class Pusher(
        val pushKey: String,
        val kind: String,
        val appId: String,
        val appDisplayName: String?,
        val deviceDisplayName: String?,
        val profileTag: String? = null,
        val lang: String?,
        val data: PusherData,

        val state: PusherState
) {
    companion object {

        const val KIND_EMAIL = "email"
        const val KIND_HTTP = "http"
        const val APP_ID_EMAIL = "m.email"
    }
}

enum class PusherState {
    UNREGISTERED,
    REGISTERING,
    UNREGISTERING,
    REGISTERED,
    FAILED_TO_REGISTER
}

data class PusherData(
        val url: String? = null,
        val format: String? = null
)
