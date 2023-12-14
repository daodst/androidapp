

package org.matrix.android.sdk.api.session.integrationmanager


interface IntegrationManagerService {

    
    interface Listener {
        
        fun onIsEnabledChanged(enabled: Boolean) {
            
        }

        
        fun onConfigurationChanged(configs: List<IntegrationManagerConfig>) {
            
        }

        
        fun onWidgetPermissionsChanged(widgets: Map<String, Boolean>) {
            
        }
    }

    
    fun addListener(listener: Listener)

    
    fun removeListener(listener: Listener)

    
    fun getOrderedConfigs(): List<IntegrationManagerConfig>

    
    fun getPreferredConfig(): IntegrationManagerConfig

    
    fun isIntegrationEnabled(): Boolean

    
    suspend fun setIntegrationEnabled(enable: Boolean)

    
    suspend fun setWidgetAllowed(stateEventId: String, allowed: Boolean)

    
    fun isWidgetAllowed(stateEventId: String): Boolean

    
    suspend fun setNativeWidgetDomainAllowed(widgetType: String, domain: String, allowed: Boolean)

    
    fun isNativeWidgetDomainAllowed(widgetType: String, domain: String): Boolean
}
