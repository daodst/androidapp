

package org.matrix.android.sdk.api.auth.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class LocalizedFlowDataLoginTerms(
        val policyName: String?,
        val version: String?,
        val localizedUrl: String?,
        val localizedName: String?
) : Parcelable
