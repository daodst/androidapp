

package org.matrix.android.sdk.api.logger


open class LoggerTag(_value: String, parentTag: LoggerTag? = null) {

    object SYNC : LoggerTag("SYNC")
    object VOIP : LoggerTag("VOIP")
    object CRYPTO : LoggerTag("CRYPTO")

    val value: String = if (parentTag == null) {
        _value
    } else {
        "${parentTag.value}/$_value"
    }
}
