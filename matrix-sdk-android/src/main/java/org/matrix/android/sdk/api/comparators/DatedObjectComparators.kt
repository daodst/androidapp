

package org.matrix.android.sdk.api.comparators

import org.matrix.android.sdk.api.interfaces.DatedObject

object DatedObjectComparators {

    
    val ascComparator by lazy {
        Comparator<DatedObject> { datedObject1, datedObject2 ->
            (datedObject1.date - datedObject2.date).toInt()
        }
    }

    
    val descComparator by lazy {
        Comparator<DatedObject> { datedObject1, datedObject2 ->
            (datedObject2.date - datedObject1.date).toInt()
        }
    }
}
