

package im.vector.app

import arrow.core.Option
import im.vector.app.core.utils.BehaviorDataSource
import org.matrix.android.sdk.api.session.Session
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActiveSessionDataSource @Inject constructor() : BehaviorDataSource<Option<Session>>()
