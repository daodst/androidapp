

package org.matrix.android.sdk.internal.session.homeserver

import com.zhuinden.monarchy.Monarchy
import io.realm.Realm
import org.matrix.android.sdk.api.session.homeserver.HomeServerCapabilities
import org.matrix.android.sdk.internal.database.mapper.HomeServerCapabilitiesMapper
import org.matrix.android.sdk.internal.database.model.HomeServerCapabilitiesEntity
import org.matrix.android.sdk.internal.database.query.get
import org.matrix.android.sdk.internal.di.SessionDatabase
import javax.inject.Inject

internal class HomeServerCapabilitiesDataSource @Inject constructor(
        @SessionDatabase private val monarchy: Monarchy
) {
    fun getHomeServerCapabilities(): HomeServerCapabilities? {
        return Realm.getInstance(monarchy.realmConfiguration).use { realm ->
            HomeServerCapabilitiesEntity.get(realm)?.let {
                HomeServerCapabilitiesMapper.map(it)
            }
        }
    }
}
