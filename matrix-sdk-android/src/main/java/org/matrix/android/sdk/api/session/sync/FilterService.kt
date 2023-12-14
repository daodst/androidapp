

package org.matrix.android.sdk.api.session.sync

interface FilterService {

    enum class FilterPreset {
        NoFilter,

        
        ElementFilter
    }

    
    fun setFilter(filterPreset: FilterPreset)
}
