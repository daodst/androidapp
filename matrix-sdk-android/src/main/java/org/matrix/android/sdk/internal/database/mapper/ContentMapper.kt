

package org.matrix.android.sdk.internal.database.mapper

import org.matrix.android.sdk.api.session.events.model.Content
import org.matrix.android.sdk.api.util.JSON_DICT_PARAMETERIZED_TYPE
import org.matrix.android.sdk.internal.di.MoshiProvider
import org.matrix.android.sdk.internal.network.parsing.CheckNumberType

internal object ContentMapper {

    private val moshi = MoshiProvider.providesMoshi()
    private val castJsonNumberMoshi by lazy {
        
        
        
        MoshiProvider.providesMoshi()
                .newBuilder()
                .add(CheckNumberType.JSON_ADAPTER_FACTORY)
                .build()
    }

    fun map(content: String?, castJsonNumbers: Boolean = false): Content? {
        return content?.let {
            if (castJsonNumbers) {
                castJsonNumberMoshi
            } else {
                moshi
            }.adapter<Content>(JSON_DICT_PARAMETERIZED_TYPE).fromJson(it)
        }
    }

    fun map(content: Content?): String? {
        return content?.let {
            moshi.adapter<Content>(JSON_DICT_PARAMETERIZED_TYPE).toJson(it)
        }
    }
}
