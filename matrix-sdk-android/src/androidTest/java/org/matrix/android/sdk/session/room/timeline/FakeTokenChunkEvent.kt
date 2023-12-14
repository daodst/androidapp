

package org.matrix.android.sdk.session.room.timeline

import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.internal.session.room.timeline.TokenChunkEvent

internal data class FakeTokenChunkEvent(override val start: String?,
                                        override val end: String?,
                                        override val events: List<Event> = emptyList(),
                                        override val stateEvents: List<Event> = emptyList()
) : TokenChunkEvent
