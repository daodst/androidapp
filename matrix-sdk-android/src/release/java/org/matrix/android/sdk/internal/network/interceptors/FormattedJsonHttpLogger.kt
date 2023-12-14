

package org.matrix.android.sdk.internal.network.interceptors

import androidx.annotation.NonNull
import okhttp3.logging.HttpLoggingInterceptor


internal class FormattedJsonHttpLogger : HttpLoggingInterceptor.Logger {

    @Synchronized
    override fun log(@NonNull message: String) {
    }
}
