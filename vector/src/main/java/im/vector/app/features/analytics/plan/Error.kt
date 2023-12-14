

package im.vector.app.features.analytics.plan

import im.vector.app.features.analytics.itf.VectorAnalyticsEvent



data class Error(
        
        val context: String? = null,
        val domain: Domain,
        val name: Name,
) : VectorAnalyticsEvent {

    enum class Domain {
        E2EE,
        VOIP,
    }

    enum class Name {
        OlmIndexError,
        OlmKeysNotSentError,
        OlmUnspecifiedError,
        UnknownError,
        VoipIceFailed,
        VoipIceTimeout,
        VoipInviteTimeout,
        VoipUserHangup,
        VoipUserMediaFailed,
    }

    override fun getName() = "Error"

    override fun getProperties(): Map<String, Any>? {
        return mutableMapOf<String, Any>().apply {
            context?.let { put("context", it) }
            put("domain", domain.name)
            put("name", name.name)
        }.takeIf { it.isNotEmpty() }
    }
}
