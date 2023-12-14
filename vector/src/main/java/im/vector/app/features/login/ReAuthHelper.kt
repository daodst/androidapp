

package im.vector.app.features.login

import im.vector.app.core.utils.TemporaryStore
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ReAuthHelper @Inject constructor() : TemporaryStore<String>()
