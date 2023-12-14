

package im.vector.app.features.analytics.plan

import im.vector.app.features.analytics.itf.VectorAnalyticsEvent



data class UnauthenticatedError(
        
        val errorCode: ErrorCode,
        
        val errorReason: String,
        
        val refreshTokenAuth: Boolean,
        
        val softLogout: Boolean,
) : VectorAnalyticsEvent {

    enum class ErrorCode {
        M_FORBIDDEN,
        M_UNKNOWN,
        M_UNKNOWN_TOKEN,
    }

    override fun getName() = "UnauthenticatedError"

    override fun getProperties(): Map<String, Any>? {
        return mutableMapOf<String, Any>().apply {
            put("errorCode", errorCode.name)
            put("errorReason", errorReason)
            put("refreshTokenAuth", refreshTokenAuth)
            put("softLogout", softLogout)
        }.takeIf { it.isNotEmpty() }
    }
}
