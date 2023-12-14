

package org.matrix.android.sdk.internal.session

import okhttp3.Interceptor

internal interface TestInterceptor : Interceptor {
    var sessionId: String?
}
