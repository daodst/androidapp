

package org.matrix.android.sdk.test.fakes

import org.amshove.kluent.shouldBeEqualTo
import org.matrix.android.sdk.internal.session.pushers.GetPushersResponse
import org.matrix.android.sdk.internal.session.pushers.JsonPusher
import org.matrix.android.sdk.internal.session.pushers.PushersAPI

internal class FakePushersAPI : PushersAPI {

    private var setRequestPayload: JsonPusher? = null
    private var error: Throwable? = null

    override suspend fun getPushers(): GetPushersResponse {
        TODO("Not yet implemented")
    }

    override suspend fun setPusher(jsonPusher: JsonPusher) {
        error?.let { throw it }
        setRequestPayload = jsonPusher
    }

    fun verifySetPusher(payload: JsonPusher) {
        this.setRequestPayload shouldBeEqualTo payload
    }

    fun givenSetPusherErrors(error: Throwable) {
        this.error = error
    }
}
