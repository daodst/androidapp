

package im.vector.app.features.login

import org.matrix.android.sdk.api.auth.registration.Stage


fun Stage.isSupported(): Boolean {
    return this is Stage.ReCaptcha ||
            this is Stage.Dummy ||
            this is Stage.Msisdn ||
            this is Stage.Terms ||
            this is Stage.Email
}
