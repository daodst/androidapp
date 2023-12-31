

package org.matrix.android.sdk.internal.worker

import androidx.work.Data
import com.squareup.moshi.Moshi
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.internal.di.MoshiProvider
import org.matrix.android.sdk.internal.network.parsing.CheckNumberType

internal object WorkerParamsFactory {

    private val moshi: Moshi by lazy {
        
        
        
        MoshiProvider.providesMoshi()
                .newBuilder()
                .add(CheckNumberType.JSON_ADAPTER_FACTORY)
                .build()
    }

    private const val KEY = "WORKER_PARAMS_JSON"

    inline fun <reified T> toData(params: T) = toData(T::class.java, params)

    fun <T> toData(clazz: Class<T>, params: T): Data {
        val adapter = moshi.adapter(clazz)
        val json = adapter.toJson(params)
        return Data.Builder().putString(KEY, json).build()
    }

    inline fun <reified T> fromData(data: Data) = fromData(T::class.java, data)

    fun <T> fromData(clazz: Class<T>, data: Data): T? = tryOrNull<T?>("Unable to parse work parameters") {
        val json = data.getString(KEY)
        return if (json == null) {
            null
        } else {
            val adapter = moshi.adapter(clazz)
            adapter.fromJson(json)
        }
    }
}
