

package org.matrix.android.sdk.api.util

import java.net.URLEncoder


fun StringBuilder.appendParamToUrl(param: String, value: String): StringBuilder {
    if (contains("?")) {
        append("&")
    } else {
        append("?")
    }

    append(param)
    append("=")
    append(URLEncoder.encode(value, "utf-8"))

    return this
}

fun StringBuilder.appendParamsToUrl(params: Map<String, String>): StringBuilder {
    params.forEach { (param, value) ->
        appendParamToUrl(param, value)
    }
    return this
}
