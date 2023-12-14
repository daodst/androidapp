

package org.matrix.android.sdk.api.session.group


interface Group {
    val groupId: String

    
    suspend fun fetchGroupData()
}
