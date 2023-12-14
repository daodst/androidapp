
package org.matrix.android.sdk.internal.network.parsing

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.io.IOException
import java.lang.reflect.Type
import javax.annotation.CheckReturnValue


internal class RuntimeJsonAdapterFactory<T>(
        private val baseType: Class<T>,
        private val labelKey: String,
        private val fallbackType: Class<*>
) : JsonAdapter.Factory {
    private val labelToType: MutableMap<String, Type> = LinkedHashMap()

    
    fun registerSubtype(subtype: Class<out T>?, label: String?): RuntimeJsonAdapterFactory<T> {
        if (subtype == null) throw NullPointerException("subtype == null")
        if (label == null) throw NullPointerException("label == null")
        require(!(labelToType.containsKey(label) || labelToType.containsValue(subtype))) { "Subtypes and labels must be unique." }
        labelToType[label] = subtype
        return this
    }

    override fun create(type: Type, annotations: Set<Annotation?>, moshi: Moshi): JsonAdapter<*>? {
        if (Types.getRawType(type) != baseType || !annotations.isEmpty()) {
            return null
        }
        val size = labelToType.size
        val labelToAdapter: MutableMap<String, JsonAdapter<Any>> = LinkedHashMap(size)
        val typeToLabel: MutableMap<Type, String> = LinkedHashMap(size)
        for ((label, typeValue) in labelToType) {
            typeToLabel[typeValue] = label
            labelToAdapter[label] = moshi.adapter(typeValue)
        }
        val fallbackAdapter = moshi.adapter<Any>(fallbackType)
        val objectJsonAdapter = moshi.adapter(Any::class.java)
        return RuntimeJsonAdapter(labelKey, labelToAdapter, typeToLabel,
                objectJsonAdapter, fallbackAdapter).nullSafe()
    }

    @Suppress("UNCHECKED_CAST")
    internal class RuntimeJsonAdapter(val labelKey: String,
                                      val labelToAdapter: Map<String, JsonAdapter<Any>>,
                                      val typeToLabel: Map<Type, String>,
                                      val objectJsonAdapter: JsonAdapter<Any>,
                                      val fallbackAdapter: JsonAdapter<Any>) : JsonAdapter<Any?>() {
        @Throws(IOException::class)
        override fun fromJson(reader: JsonReader): Any? {
            val peekedToken = reader.peek()
            if (peekedToken != JsonReader.Token.BEGIN_OBJECT) {
                throw JsonDataException("Expected BEGIN_OBJECT but was " + peekedToken +
                        " at path " + reader.path)
            }
            val jsonValue = reader.readJsonValue()
            val jsonObject = jsonValue as Map<String, Any>?
            val label = jsonObject!![labelKey] as? String ?: return null
            val adapter = labelToAdapter[label] ?: return fallbackAdapter.fromJsonValue(jsonValue)
            return adapter.fromJsonValue(jsonValue)
        }

        @Throws(IOException::class)
        override fun toJson(writer: JsonWriter, value: Any?) {
            val type: Class<*> = value!!.javaClass
            val label = typeToLabel[type]
                    ?: throw IllegalArgumentException("Expected one of " +
                            typeToLabel.keys +
                            " but found " +
                            value +
                            ", a " +
                            value.javaClass +
                            ". Register this subtype.")
            val adapter = labelToAdapter[label]!!
            val jsonValue = adapter.toJsonValue(value) as Map<String, Any>?
            val valueWithLabel: MutableMap<String, Any> = LinkedHashMap(1 + jsonValue!!.size)
            valueWithLabel[labelKey] = label
            valueWithLabel.putAll(jsonValue)
            objectJsonAdapter.toJson(writer, valueWithLabel)
        }

        override fun toString(): String {
            return "RuntimeJsonAdapter($labelKey)"
        }
    }

    companion object {
        
        @CheckReturnValue
        fun <T> of(baseType: Class<T>, labelKey: String, fallbackType: Class<out T>): RuntimeJsonAdapterFactory<T> {
            require(baseType != Any::class.java) { "The base type must not be Object. Consider using a marker interface." }
            return RuntimeJsonAdapterFactory(baseType, labelKey, fallbackType)
        }
    }
}
