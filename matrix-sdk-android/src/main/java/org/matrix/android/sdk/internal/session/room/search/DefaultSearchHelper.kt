package org.matrix.android.sdk.internal.session.room.search

import io.realm.Realm
import org.matrix.android.sdk.api.session.room.timeline.TimelineSettings
import org.matrix.android.sdk.internal.session.room.timeline.TimelineEventDecryptor
import java.util.concurrent.atomic.AtomicReference


@Deprecated(message = "useless")
internal class DefaultSearchHelper(
    private val settings: TimelineSettings,
    eventDecryptor: TimelineEventDecryptor,
) {
    private val backgroundRealm = AtomicReference<Realm>()


}
