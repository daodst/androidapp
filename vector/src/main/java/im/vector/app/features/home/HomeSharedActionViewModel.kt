

package im.vector.app.features.home

import im.vector.app.core.platform.VectorSharedActionViewModel
import org.matrix.android.sdk.api.session.Session
import javax.inject.Inject

class HomeSharedActionViewModel @Inject constructor(val session: Session) : VectorSharedActionViewModel<HomeActivitySharedAction>()
