

package org.matrix.android.sdk.internal.network.token

internal interface AccessTokenProvider {
    fun getToken(): String?
}
