

package org.matrix.android.sdk.internal.session.sync.model.accountdata


internal typealias DirectMessagesContent = Map<String, List<String>>


internal fun DirectMessagesContent.toMutable(): MutableMap<String, MutableList<String>> {
    return map { it.key to it.value.toMutableList() }
            .toMap()
            .toMutableMap()
}
