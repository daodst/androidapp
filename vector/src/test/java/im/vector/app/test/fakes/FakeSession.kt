

package im.vector.app.test.fakes

import android.net.Uri
import im.vector.app.core.extensions.configureAndStart
import im.vector.app.core.extensions.startSyncing
import im.vector.app.core.extensions.vectorStore
import im.vector.app.features.session.VectorSessionStore
import im.vector.app.test.testCoroutineDispatchers
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.mockk
import io.mockk.mockkStatic
import org.matrix.android.sdk.api.session.Session

class FakeSession(
        val fakeCryptoService: FakeCryptoService = FakeCryptoService(),
        val fakeProfileService: FakeProfileService = FakeProfileService(),
        val fakeHomeServerCapabilitiesService: FakeHomeServerCapabilitiesService = FakeHomeServerCapabilitiesService(),
        val fakeSharedSecretStorageService: FakeSharedSecretStorageService = FakeSharedSecretStorageService()
) : Session by mockk(relaxed = true) {

    init {
        mockkStatic("im.vector.app.core.extensions.SessionKt")
    }

    override val myUserId: String = "@fake:server.fake"

    override fun cryptoService() = fakeCryptoService
    override val sharedSecretStorageService = fakeSharedSecretStorageService
    override val coroutineDispatchers = testCoroutineDispatchers
    override suspend fun setDisplayName(userId: String, newDisplayName: String) = fakeProfileService.setDisplayName(userId, newDisplayName)
    override suspend fun updateAvatar(userId: String, newAvatarUri: Uri, fileName: String) = fakeProfileService.updateAvatar(userId, newAvatarUri, fileName)
    override fun getHomeServerCapabilities() = fakeHomeServerCapabilitiesService.getHomeServerCapabilities()

    fun givenVectorStore(vectorSessionStore: VectorSessionStore) {
        coEvery {
            this@FakeSession.vectorStore(any())
        } coAnswers {
            vectorSessionStore
        }
    }

    fun expectStartsSyncing() {
        coJustRun {
            this@FakeSession.configureAndStart(any(), startSyncing = true)
            this@FakeSession.startSyncing(any())
        }
    }
}
