

package org.matrix.android.sdk.internal.session.identity

import org.matrix.android.sdk.internal.session.SessionScope
import javax.inject.Inject

@SessionScope
internal class IdentityApiProvider @Inject constructor() {

    var identityApi: IdentityAPI? = null
}
