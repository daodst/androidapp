
package org.matrix.android.sdk.api.session.integrationmanager


data class IntegrationManagerConfig(
        val uiUrl: String,
        val restUrl: String,
        val kind: Kind
) {

    
    
    enum class Kind {
        
        ACCOUNT,

        
        HOMESERVER,

        
        DEFAULT
    }
}
