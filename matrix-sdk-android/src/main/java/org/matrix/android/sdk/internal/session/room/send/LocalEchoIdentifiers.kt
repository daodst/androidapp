

package org.matrix.android.sdk.internal.session.room.send

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
internal data class LocalEchoIdentifiers(val roomId: String, val eventId: String)
