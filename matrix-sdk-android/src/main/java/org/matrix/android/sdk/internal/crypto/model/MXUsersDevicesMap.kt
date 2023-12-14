

package org.matrix.android.sdk.internal.crypto.model

import org.matrix.android.sdk.api.session.crypto.model.MXUsersDevicesMap

internal fun <T> MXUsersDevicesMap<T>.toDebugString() =
        map.entries.joinToString { "${it.key} [${it.value.keys.joinToString { it }}]" }

internal fun <T> MXUsersDevicesMap<T>.toDebugCount() =
        map.entries.fold(0) { acc, new ->
            acc + new.value.keys.size
        }
