package org.matrix.android.sdk.internal.database.model

import io.realm.RealmObject


open class ChatPhoneLog(
        
        var bestName: String = "",
        
        var time: Long = 0L,
        
        var formattedDuration: String = "",
        
        var address: String = "",
        
        var phone: String = "",
        
        var income: Int = 0,
        
        var status: Int = 0) : RealmObject() {
    companion object

    override

    override fun toString(): String {
        return "ChatPhoneLog(bestName='$bestName', time=$time, formattedDuration='$formattedDuration', address='$address', phone='$phone', income=$income, status=$status)"
    }
}


