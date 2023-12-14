

package org.matrix.android.sdk.internal.network.parsing

import androidx.annotation.Nullable
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import java.io.IOException
import java.lang.reflect.Type
import java.math.BigDecimal


internal interface CheckNumberType {

    companion object {
        val JSON_ADAPTER_FACTORY = object : JsonAdapter.Factory {
            @Nullable
            override fun create(type: Type, annotations: Set<Annotation?>?, moshi: Moshi): JsonAdapter<*>? {
                if (type !== Any::class.java) {
                    return null
                }
                val delegate: JsonAdapter<Any> = moshi.nextAdapter(this, Any::class.java, emptySet())
                return object : JsonAdapter<Any?>() {
                    @Nullable
                    @Throws(IOException::class)
                    override fun fromJson(reader: JsonReader): Any? {
                        return if (reader.peek() !== JsonReader.Token.NUMBER) {
                            delegate.fromJson(reader)
                        } else {
                            val numberAsString = reader.nextString()
                            val decimal = BigDecimal(numberAsString)
                            if (decimal.scale() <= 0) {
                                decimal.longValueExact()
                            } else {
                                decimal.toDouble()
                            }
                        }
                    }

                    override fun toJson(writer: JsonWriter, value: Any?) {
                        delegate.toJson(writer, value)
                    }
                }
            }
        }
    }
}
