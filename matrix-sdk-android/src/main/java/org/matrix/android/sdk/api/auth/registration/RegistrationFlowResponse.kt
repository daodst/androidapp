

package org.matrix.android.sdk.api.auth.registration

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.auth.data.LoginFlowTypes
import org.matrix.android.sdk.api.session.uia.InteractiveAuthenticationFlow
import org.matrix.android.sdk.api.util.JsonDict

@JsonClass(generateAdapter = true)
data class RegistrationFlowResponse(

        
        @Json(name = "flows")
        val flows: List<InteractiveAuthenticationFlow>? = null,

        
        @Json(name = "completed")
        val completedStages: List<String>? = null,

        
        @Json(name = "session")
        val session: String? = null,

        
        @Json(name = "params")
        val params: JsonDict? = null

        
)


fun RegistrationFlowResponse.toFlowResult(): FlowResult {
    
    val allFlowTypes = mutableSetOf<String>()

    val missingStage = mutableListOf<Stage>()
    val completedStage = mutableListOf<Stage>()

    this.flows?.forEach { it.stages?.mapTo(allFlowTypes) { type -> type } }

    allFlowTypes.forEach { type ->
        val isMandatory = flows?.all { type in it.stages.orEmpty() } == true

        val stage = when (type) {
            LoginFlowTypes.RECAPTCHA      -> Stage.ReCaptcha(isMandatory, ((params?.get(type) as? Map<*, *>)?.get("public_key") as? String)
                    ?: "")
            LoginFlowTypes.DUMMY          -> Stage.Dummy(isMandatory)
            LoginFlowTypes.TERMS          -> Stage.Terms(isMandatory, params?.get(type) as? TermPolicies ?: emptyMap<String, String>())
            LoginFlowTypes.EMAIL_IDENTITY -> Stage.Email(isMandatory)
            LoginFlowTypes.MSISDN         -> Stage.Msisdn(isMandatory)
            else                          -> Stage.Other(isMandatory, type, (params?.get(type) as? Map<*, *>))
        }

        if (type in completedStages.orEmpty()) {
            completedStage.add(stage)
        } else {
            missingStage.add(stage)
        }
    }

    return FlowResult(missingStage, completedStage)
}

fun RegistrationFlowResponse.nextUncompletedStage(flowIndex: Int = 0): String? {
    val completed = completedStages ?: emptyList()
    return flows?.getOrNull(flowIndex)?.stages?.firstOrNull { completed.contains(it).not() }
}
