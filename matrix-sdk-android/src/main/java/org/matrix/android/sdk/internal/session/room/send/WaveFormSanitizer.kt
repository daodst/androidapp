

package org.matrix.android.sdk.internal.session.room.send

import timber.log.Timber
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.ceil

internal class WaveFormSanitizer @Inject constructor() {
    private companion object {
        const val MIN_NUMBER_OF_VALUES = 30
        const val MAX_NUMBER_OF_VALUES = 120

        const val MAX_VALUE = 1024
    }

    
    fun sanitize(waveForm: List<Int>?): List<Int>? {
        if (waveForm.isNullOrEmpty()) {
            return null
        }

        
        val sizeInRangeList = mutableListOf<Int>()
        when {
            waveForm.size < MIN_NUMBER_OF_VALUES -> {
                
                val repeatTimes = ceil(MIN_NUMBER_OF_VALUES / waveForm.size.toDouble()).toInt()
                waveForm.map { value ->
                    repeat(repeatTimes) {
                        sizeInRangeList.add(value)
                    }
                }
            }
            waveForm.size > MAX_NUMBER_OF_VALUES -> {
                val keepOneOf = ceil(waveForm.size.toDouble() / MAX_NUMBER_OF_VALUES).toInt()
                waveForm.mapIndexed { idx, value ->
                    if (idx % keepOneOf == 0) {
                        sizeInRangeList.add(value)
                    }
                }
            }
            else                                 -> {
                sizeInRangeList.addAll(waveForm)
            }
        }

        
        val positiveList = sizeInRangeList.map {
            abs(it)
        }

        
        val max = positiveList.maxOrNull() ?: MAX_VALUE

        val finalList = if (max > MAX_VALUE) {
            
            positiveList.map {
                it * MAX_VALUE / max
            }
        } else {
            positiveList
        }

        Timber.d("Sanitize from ${waveForm.size} items to ${finalList.size} items. Max value was $max")
        return finalList
    }
}
