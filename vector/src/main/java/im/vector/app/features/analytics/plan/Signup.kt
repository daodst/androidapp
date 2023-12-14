

package im.vector.app.features.analytics.plan

import im.vector.app.features.analytics.itf.VectorAnalyticsEvent



data class Signup(
        
        val authenticationType: AuthenticationType,
) : VectorAnalyticsEvent {

    enum class AuthenticationType {
        
        Apple,

        
        Facebook,

        
        GitHub,

        
        GitLab,

        
        Google,

        
        Other,

        
        Password,

        
        SSO,
    }

    override fun getName() = "Signup"

    override fun getProperties(): Map<String, Any>? {
        return mutableMapOf<String, Any>().apply {
            put("authenticationType", authenticationType.name)
        }.takeIf { it.isNotEmpty() }
    }
}
