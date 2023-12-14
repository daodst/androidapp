

package org.matrix.android.sdk.api.session.room.model.message

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class AudioWaveformInfo(
        @Json(name = "duration")
        val duration: Int? = null,

        
        @Json(name = "waveform")
        val waveform: List<Int?>? = null
)
