

package org.matrix.android.sdk.api.util

import com.squareup.moshi.Types
import java.lang.reflect.ParameterizedType

typealias JsonDict = Map<String, @JvmSuppressWildcards Any>

val emptyJsonDict = emptyMap<String, Any>()

internal val JSON_DICT_PARAMETERIZED_TYPE: ParameterizedType = Types.newParameterizedType(Map::class.java, String::class.java, Any::class.java)
