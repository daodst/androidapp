

package org.matrix.android.sdk.internal.session.widgets

import dagger.Lazy
import okhttp3.OkHttpClient
import org.matrix.android.sdk.internal.di.Unauthenticated
import org.matrix.android.sdk.internal.network.RetrofitFactory
import org.matrix.android.sdk.internal.session.SessionScope
import javax.inject.Inject

@SessionScope
internal class WidgetsAPIProvider @Inject constructor(@Unauthenticated private val okHttpClient: Lazy<OkHttpClient>,
                                                      private val retrofitFactory: RetrofitFactory) {

    
    private val widgetsAPIs = mutableMapOf<String, WidgetsAPI>()

    fun get(serverUrl: String): WidgetsAPI {
        return widgetsAPIs.getOrPut(serverUrl) {
            retrofitFactory.create(okHttpClient, serverUrl).create(WidgetsAPI::class.java)
        }
    }
}
