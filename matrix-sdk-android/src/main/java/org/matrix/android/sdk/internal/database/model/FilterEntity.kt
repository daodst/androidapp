

package org.matrix.android.sdk.internal.database.model

import io.realm.RealmObject


internal open class FilterEntity(
        
        var filterBodyJson: String = "",
        
        var roomEventFilterJson: String = "",
        
        var filterId: String = ""

) : RealmObject() {

    companion object
}
