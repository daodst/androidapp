

package org.matrix.android.sdk.internal.session.pushers

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.internal.assertFailsWith
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test
import org.matrix.android.sdk.api.session.pushers.PusherState
import org.matrix.android.sdk.internal.database.model.PusherEntity
import org.matrix.android.sdk.test.fakes.FakeGlobalErrorReceiver
import org.matrix.android.sdk.test.fakes.FakeMonarchy
import org.matrix.android.sdk.test.fakes.FakePushersAPI
import org.matrix.android.sdk.test.fakes.FakeRequestExecutor
import java.net.SocketException

private val A_JSON_PUSHER = JsonPusher(
        pushKey = "push-key",
        kind = "http",
        appId = "m.email",
        appDisplayName = "Element",
        deviceDisplayName = null,
        profileTag = "",
        lang = "en-GB",
        data = JsonPusherData(brand = "Element")
)

@ExperimentalCoroutinesApi
class DefaultAddPusherTaskTest {

    private val pushersAPI = FakePushersAPI()
    private val monarchy = FakeMonarchy()

    private val addPusherTask = DefaultAddPusherTask(
            pushersAPI = pushersAPI,
            monarchy = monarchy.instance,
            requestExecutor = FakeRequestExecutor(),
            globalErrorReceiver = FakeGlobalErrorReceiver()
    )

    @Test
    fun `given no persisted pusher when adding Pusher then updates api and inserts result with Registered state`() {
        monarchy.givenWhereReturns<PusherEntity>(result = null)

        runTest { addPusherTask.execute(AddPusherTask.Params(A_JSON_PUSHER)) }

        pushersAPI.verifySetPusher(A_JSON_PUSHER)
        monarchy.verifyInsertOrUpdate<PusherEntity> {
            withArg { actual ->
                actual.state shouldBeEqualTo PusherState.REGISTERED
            }
        }
    }

    @Test
    fun `given a persisted pusher when adding Pusher then updates api and mutates persisted result with Registered state`() {
        val realmResult = PusherEntity(appDisplayName = null)
        monarchy.givenWhereReturns(result = realmResult)

        runTest { addPusherTask.execute(AddPusherTask.Params(A_JSON_PUSHER)) }

        pushersAPI.verifySetPusher(A_JSON_PUSHER)

        realmResult.appDisplayName shouldBeEqualTo A_JSON_PUSHER.appDisplayName
        realmResult.state shouldBeEqualTo PusherState.REGISTERED
    }

    @Test
    fun `given a persisted push entity and SetPush API fails when adding Pusher then mutates persisted result with Failed registration state and rethrows error`() {
        val realmResult = PusherEntity()
        monarchy.givenWhereReturns(result = realmResult)
        pushersAPI.givenSetPusherErrors(SocketException())

        assertFailsWith<SocketException> {
            runTest { addPusherTask.execute(AddPusherTask.Params(A_JSON_PUSHER)) }
        }

        realmResult.state shouldBeEqualTo PusherState.FAILED_TO_REGISTER
    }

    @Test
    fun `given no persisted push entity and SetPush API fails when adding Pusher then rethrows error`() {
        monarchy.givenWhereReturns<PusherEntity>(result = null)
        pushersAPI.givenSetPusherErrors(SocketException())

        assertFailsWith<SocketException> {
            runTest { addPusherTask.execute(AddPusherTask.Params(A_JSON_PUSHER)) }
        }
    }
}
