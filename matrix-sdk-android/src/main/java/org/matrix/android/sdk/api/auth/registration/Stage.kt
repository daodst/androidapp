

package org.matrix.android.sdk.api.auth.registration

sealed class Stage(open val mandatory: Boolean) {

    
    data class ReCaptcha(override val mandatory: Boolean, val publicKey: String) : Stage(mandatory)

    
    data class Email(override val mandatory: Boolean) : Stage(mandatory)

    
    data class Msisdn(override val mandatory: Boolean) : Stage(mandatory)

    
    
    data class Dummy(override val mandatory: Boolean) : Stage(mandatory)

    
    data class Terms(override val mandatory: Boolean, val policies: TermPolicies) : Stage(mandatory)

    
    data class Other(override val mandatory: Boolean, val type: String, val params: Map<*, *>?) : Stage(mandatory)
}

typealias TermPolicies = Map<*, *>
