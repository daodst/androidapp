

package org.matrix.android.sdk.api.auth.wellknown

import org.matrix.android.sdk.api.auth.data.WellKnown


sealed class WellknownResult {
    
    data class Prompt(val homeServerUrl: String,
                      val identityServerUrl: String?,
                      val wellKnown: WellKnown) : WellknownResult()

    
    object Ignore : WellknownResult()

    
    data class FailPrompt(val homeServerUrl: String?, val wellKnown: WellKnown?) : WellknownResult()

    
    object FailError : WellknownResult()
}
