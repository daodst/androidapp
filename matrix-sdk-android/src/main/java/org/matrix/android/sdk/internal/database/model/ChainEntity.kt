package org.matrix.android.sdk.internal.database.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


open class ChainEntity(
        @PrimaryKey var id: String = "",
        var md5: String = "",
        var json: String = "") : RealmObject()  {

    companion object

    fun toStr(): String {
        return "ChainEntity(id='$id', md5='$md5', json='$json')"
    }
}
