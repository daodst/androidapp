

package org.matrix.android.sdk.session.space

import androidx.test.filters.LargeTest
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.MethodSorters
import org.matrix.android.sdk.InstrumentedTest
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.GuestAccess
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent
import org.matrix.android.sdk.api.session.room.model.RoomGuestAccessContent
import org.matrix.android.sdk.api.session.room.model.RoomHistoryVisibility
import org.matrix.android.sdk.api.session.room.model.RoomHistoryVisibilityContent
import org.matrix.android.sdk.api.session.room.model.RoomType
import org.matrix.android.sdk.api.session.room.model.create.CreateRoomParams
import org.matrix.android.sdk.api.session.room.model.create.CreateRoomPreset
import org.matrix.android.sdk.api.session.room.model.create.RoomCreateContent
import org.matrix.android.sdk.api.session.space.JoinSpaceResult
import org.matrix.android.sdk.common.CommonTestHelper
import org.matrix.android.sdk.common.SessionTestParams

@RunWith(JUnit4::class)
@FixMethodOrder(MethodSorters.JVM)
@LargeTest
class SpaceCreationTest : InstrumentedTest {

    @Test
    fun createSimplePublicSpace() {
        val commonTestHelper = CommonTestHelper(context())
        val session = commonTestHelper.createAccount("Hubble", SessionTestParams(true))
        val roomName = "My Space"
        val topic = "A public space for test"
        var spaceId: String = ""
        commonTestHelper.waitWithLatch {
            spaceId = session.spaceService().createSpace(roomName, topic, null, true)
            
            it.countDown()
        }
        Thread.sleep(4_000)

        val syncedSpace = session.spaceService().getSpace(spaceId)
        commonTestHelper.waitWithLatch {
            commonTestHelper.retryPeriodicallyWithLatch(it) {
                syncedSpace?.asRoom()?.roomSummary()?.name != null
            }
        }
        assertEquals("Room name should be set", roomName, syncedSpace?.asRoom()?.roomSummary()?.name)
        assertEquals("Room topic should be set", topic, syncedSpace?.asRoom()?.roomSummary()?.topic)
        

        assertNotNull("Space should be found by Id", syncedSpace)
        val creationEvent = syncedSpace!!.asRoom().getStateEvent(EventType.STATE_ROOM_CREATE)
        val createContent = creationEvent?.content.toModel<RoomCreateContent>()
        assertEquals("Room type should be space", RoomType.SPACE, createContent?.type)

        var powerLevelsContent: PowerLevelsContent? = null
        commonTestHelper.waitWithLatch { latch ->
            commonTestHelper.retryPeriodicallyWithLatch(latch) {
                val toModel = syncedSpace.asRoom().getStateEvent(EventType.STATE_ROOM_POWER_LEVELS)?.content.toModel<PowerLevelsContent>()
                powerLevelsContent = toModel
                toModel != null
            }
        }
        assertEquals("Space-rooms should be created with a power level for events_default of 100", 100, powerLevelsContent?.eventsDefault)

        val guestAccess = syncedSpace.asRoom().getStateEvent(EventType.STATE_ROOM_GUEST_ACCESS)?.content
                ?.toModel<RoomGuestAccessContent>()?.guestAccess

        assertEquals("Public space room should be peekable by guest", GuestAccess.CanJoin, guestAccess)

        val historyVisibility = syncedSpace.asRoom().getStateEvent(EventType.STATE_ROOM_HISTORY_VISIBILITY)?.content
                ?.toModel<RoomHistoryVisibilityContent>()?.historyVisibility

        assertEquals("Public space room should be world readable", RoomHistoryVisibility.WORLD_READABLE, historyVisibility)

        commonTestHelper.signOutAndClose(session)
    }

    @Test
    fun testJoinSimplePublicSpace() {
        val commonTestHelper = CommonTestHelper(context())

        val aliceSession = commonTestHelper.createAccount("alice", SessionTestParams(true))
        val bobSession = commonTestHelper.createAccount("bob", SessionTestParams(true))

        val roomName = "My Space"
        val topic = "A public space for test"
        val spaceId: String
        runBlocking {
            spaceId = aliceSession.spaceService().createSpace(roomName, topic, null, true)
            
            delay(400)
        }

        

        val joinResult: JoinSpaceResult
        runBlocking {
            joinResult = bobSession.spaceService().joinSpace(spaceId)
        }

        assertEquals(JoinSpaceResult.Success, joinResult)

        val spaceBobPov = bobSession.spaceService().getSpace(spaceId)
        assertEquals("Room name should be set", roomName, spaceBobPov?.asRoom()?.roomSummary()?.name)
        assertEquals("Room topic should be set", topic, spaceBobPov?.asRoom()?.roomSummary()?.topic)

        commonTestHelper.signOutAndClose(aliceSession)
        commonTestHelper.signOutAndClose(bobSession)
    }

    @Test
    fun testSimplePublicSpaceWithChildren() {
        val commonTestHelper = CommonTestHelper(context())
        val aliceSession = commonTestHelper.createAccount("alice", SessionTestParams(true))
        val bobSession = commonTestHelper.createAccount("bob", SessionTestParams(true))

        val roomName = "My Space"
        val topic = "A public space for test"

        val spaceId: String = runBlocking { aliceSession.spaceService().createSpace(roomName, topic, null, true) }
        val syncedSpace = aliceSession.spaceService().getSpace(spaceId)

        
        var firstChild: String? = null
        commonTestHelper.waitWithLatch {
            firstChild = aliceSession.createRoom(CreateRoomParams().apply {
                this.name = "FirstRoom"
                this.topic = "Description of first room"
                this.preset = CreateRoomPreset.PRESET_PUBLIC_CHAT
            })
            it.countDown()
        }

        commonTestHelper.waitWithLatch {
            syncedSpace?.addChildren(firstChild!!, listOf(aliceSession.sessionParams.homeServerHost ?: ""), "a", suggested = true)
            it.countDown()
        }

        var secondChild: String? = null
        commonTestHelper.waitWithLatch {
            secondChild = aliceSession.createRoom(CreateRoomParams().apply {
                this.name = "SecondRoom"
                this.topic = "Description of second room"
                this.preset = CreateRoomPreset.PRESET_PUBLIC_CHAT
            })
            it.countDown()
        }

        commonTestHelper.waitWithLatch {
            syncedSpace?.addChildren(secondChild!!, listOf(aliceSession.sessionParams.homeServerHost ?: ""), "b", suggested = true)
            it.countDown()
        }

        
        var joinResult: JoinSpaceResult? = null
        commonTestHelper.waitWithLatch {
            joinResult = bobSession.spaceService().joinSpace(spaceId)
            
            it.countDown()
        }

        assertEquals(JoinSpaceResult.Success, joinResult)

        val spaceBobPov = bobSession.spaceService().getSpace(spaceId)
        assertEquals("Room name should be set", roomName, spaceBobPov?.asRoom()?.roomSummary()?.name)
        assertEquals("Room topic should be set", topic, spaceBobPov?.asRoom()?.roomSummary()?.topic)

        
        




        commonTestHelper.signOutAndClose(aliceSession)
        commonTestHelper.signOutAndClose(bobSession)
    }
}
