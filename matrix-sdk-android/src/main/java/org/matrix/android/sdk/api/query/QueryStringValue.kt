

package org.matrix.android.sdk.api.query


sealed interface QueryStringValue {
    sealed interface ContentQueryStringValue : QueryStringValue {
        val string: String
        val case: Case
    }

    object NoCondition : QueryStringValue
    object IsNull : QueryStringValue
    object IsNotNull : QueryStringValue
    object IsEmpty : QueryStringValue
    object IsNotEmpty : QueryStringValue

    data class Equals(override val string: String, override val case: Case = Case.SENSITIVE) : ContentQueryStringValue
    data class Contains(override val string: String, override val case: Case = Case.SENSITIVE) : ContentQueryStringValue

    enum class Case {
        
        SENSITIVE,

        
        INSENSITIVE,

        
        NORMALIZED
    }
}

internal fun QueryStringValue.isNormalized() = this is QueryStringValue.ContentQueryStringValue && case == QueryStringValue.Case.NORMALIZED
