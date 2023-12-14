
package org.matrix.android.sdk.internal.session.pushers

import org.matrix.android.sdk.api.pushrules.rest.GetPushRulesResponse
import org.matrix.android.sdk.api.pushrules.rest.PushRule
import org.matrix.android.sdk.internal.network.NetworkConstants
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

internal interface PushRulesApi {
    
    @GET(NetworkConstants.URI_API_PREFIX_PATH_R0 + "pushrules/")
    suspend fun getAllRules(): GetPushRulesResponse

    
    @PUT(NetworkConstants.URI_API_PREFIX_PATH_R0 + "pushrules/global/{kind}/{ruleId}/enabled")
    suspend fun updateEnableRuleStatus(@Path("kind") kind: String,
                                       @Path("ruleId") ruleId: String,
                                       @Body enabledBody: EnabledBody)

    
    @PUT(NetworkConstants.URI_API_PREFIX_PATH_R0 + "pushrules/global/{kind}/{ruleId}/actions")
    suspend fun updateRuleActions(@Path("kind") kind: String,
                                  @Path("ruleId") ruleId: String,
                                  @Body actions: Any)

    
    @DELETE(NetworkConstants.URI_API_PREFIX_PATH_R0 + "pushrules/global/{kind}/{ruleId}")
    suspend fun deleteRule(@Path("kind") kind: String,
                           @Path("ruleId") ruleId: String)

    
    @PUT(NetworkConstants.URI_API_PREFIX_PATH_R0 + "pushrules/global/{kind}/{ruleId}")
    suspend fun addRule(@Path("kind") kind: String,
                        @Path("ruleId") ruleId: String,
                        @Body rule: PushRule)
}
