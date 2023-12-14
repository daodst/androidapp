

package im.vector.app.test.fakes

import im.vector.app.features.invite.AutoAcceptInvites

class FakeAutoAcceptInvites : AutoAcceptInvites {

    var _isEnabled: Boolean = false

    override val isEnabled: Boolean
        get() = _isEnabled
}
