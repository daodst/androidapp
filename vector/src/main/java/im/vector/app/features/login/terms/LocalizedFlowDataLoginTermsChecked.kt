

package im.vector.app.features.login.terms

import org.matrix.android.sdk.api.auth.data.LocalizedFlowDataLoginTerms

data class LocalizedFlowDataLoginTermsChecked(val localizedFlowDataLoginTerms: LocalizedFlowDataLoginTerms,
                                              var checked: Boolean = false)
