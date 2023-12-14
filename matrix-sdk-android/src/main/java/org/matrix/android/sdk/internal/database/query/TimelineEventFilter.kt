

package org.matrix.android.sdk.internal.database.query


internal object TimelineEventFilter {
    
    internal object Content {
        internal const val EDIT = """{*"m.relates_to"*"rel_type":*"m.replace"*}"""
        internal const val RESPONSE = """{*"m.relates_to"*"rel_type":*"org.matrix.response"*}"""
        internal const val REFERENCE = """{*"m.relates_to"*"rel_type":*"m.reference"*}"""
    }

    
    internal object DecryptedContent {
        internal const val URL = """{*"file":*"url":*}"""
    }

    
    internal object Unsigned {
        internal const val REDACTED = """{*"redacted_because":*}"""
    }
}
