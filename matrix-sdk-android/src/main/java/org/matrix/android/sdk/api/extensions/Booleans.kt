

package org.matrix.android.sdk.api.extensions

fun Boolean?.orTrue() = this ?: true

fun Boolean?.orFalse() = this ?: false
