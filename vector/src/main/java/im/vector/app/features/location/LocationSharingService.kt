

package im.vector.app.features.location

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.os.Parcelable
import dagger.hilt.android.AndroidEntryPoint
import im.vector.app.core.di.ActiveSessionHolder
import im.vector.app.core.services.VectorService
import im.vector.app.core.time.Clock
import im.vector.app.features.notifications.NotificationUtils
import im.vector.app.features.session.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toContent
import org.matrix.android.sdk.api.session.room.model.livelocation.BeaconInfo
import org.matrix.android.sdk.api.session.room.model.livelocation.LiveLocationBeaconContent
import timber.log.Timber
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject

@AndroidEntryPoint
class LocationSharingService : VectorService(), LocationTracker.Callback {

    @Parcelize
    data class RoomArgs(
            val sessionId: String,
            val roomId: String,
            val durationMillis: Long
    ) : Parcelable

    @Inject lateinit var notificationUtils: NotificationUtils
    @Inject lateinit var locationTracker: LocationTracker
    @Inject lateinit var activeSessionHolder: ActiveSessionHolder
    @Inject lateinit var clock: Clock

    private val binder = LocalBinder()

    private var roomArgsList = mutableListOf<RoomArgs>()
    private var timers = mutableListOf<Timer>()

    override fun onCreate() {
        super.onCreate()
        Timber.i("### LocationSharingService.onCreate")

        
        locationTracker.addCallback(this)
        locationTracker.start()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val roomArgs = intent?.getParcelableExtra(EXTRA_ROOM_ARGS) as? RoomArgs

        Timber.i("### LocationSharingService.onStartCommand. sessionId - roomId ${roomArgs?.sessionId} - ${roomArgs?.roomId}")

        if (roomArgs != null) {
            roomArgsList.add(roomArgs)

            
            val notification = notificationUtils.buildLiveLocationSharingNotification()
            startForeground(roomArgs.roomId.hashCode(), notification)

            
            scheduleTimer(roomArgs.roomId, roomArgs.durationMillis)

            
            activeSessionHolder
                    .getSafeActiveSession()
                    ?.let { session ->
                        session.coroutineScope.launch(session.coroutineDispatchers.io) {
                            sendLiveBeaconInfo(session, roomArgs)
                        }
                    }
        }

        return START_STICKY
    }

    private suspend fun sendLiveBeaconInfo(session: Session, roomArgs: RoomArgs) {
        val beaconContent = LiveLocationBeaconContent(
                unstableBeaconInfo = BeaconInfo(
                        timeout = roomArgs.durationMillis,
                        isLive = true
                ),
                unstableTimestampAsMilliseconds = clock.epochMillis()
        ).toContent()

        val stateKey = session.myUserId
        session
                .getRoom(roomArgs.roomId)
                ?.sendStateEvent(
                        eventType = EventType.STATE_ROOM_BEACON_INFO.first(),
                        stateKey = stateKey,
                        body = beaconContent
                )
    }

    private fun scheduleTimer(roomId: String, durationMillis: Long) {
        Timer()
                .apply {
                    schedule(object : TimerTask() {
                        override fun run() {
                            stopSharingLocation(roomId)
                            timers.remove(this@apply)
                        }
                    }, durationMillis)
                }
                .also {
                    timers.add(it)
                }
    }

    fun stopSharingLocation(roomId: String) {
        Timber.i("### LocationSharingService.stopSharingLocation for $roomId")

        
        sendStoppedBeaconInfo(roomId)

        synchronized(roomArgsList) {
            roomArgsList.removeAll { it.roomId == roomId }
            if (roomArgsList.isEmpty()) {
                Timber.i("### LocationSharingService. Destroying self, time is up for all rooms")
                destroyMe()
            }
        }
    }

    private fun sendStoppedBeaconInfo(roomId: String) {
        activeSessionHolder
                .getSafeActiveSession()
                ?.let { session ->
                    session.coroutineScope.launch(session.coroutineDispatchers.io) {
                        session.getRoom(roomId)?.stopLiveLocation(session.myUserId)
                    }
                }
    }

    override fun onLocationUpdate(locationData: LocationData) {
        Timber.i("### LocationSharingService.onLocationUpdate. Uncertainty: ${locationData.uncertainty}")

        val session = activeSessionHolder.getSafeActiveSession()
        
        session?.coroutineScope?.launch(session.coroutineDispatchers.io) {
            roomArgsList.toList().forEach { roomArg ->
                sendLiveLocation(roomArg.roomId, locationData)
            }
        }
    }

    private suspend fun sendLiveLocation(roomId: String, locationData: LocationData) {
        val session = activeSessionHolder.getSafeActiveSession()
        val room = session?.getRoom(roomId)
        val userId = session?.myUserId

        if (room == null || userId == null) {
            return
        }

        room
                .getLiveLocationBeaconInfo(userId, true)
                ?.eventId
                ?.let {
                    room.sendLiveLocation(
                            beaconInfoEventId = it,
                            latitude = locationData.latitude,
                            longitude = locationData.longitude,
                            uncertainty = locationData.uncertainty
                    )
                }
    }

    override fun onLocationProviderIsNotAvailable() {
        stopForeground(true)
        stopSelf()
    }

    private fun destroyMe() {
        locationTracker.removeCallback(this)
        timers.forEach { it.cancel() }
        timers.clear()
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.i("### LocationSharingService.onDestroy")
        destroyMe()
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    inner class LocalBinder : Binder() {
        fun getService(): LocationSharingService = this@LocationSharingService
    }

    companion object {
        const val EXTRA_ROOM_ARGS = "EXTRA_ROOM_ARGS"
    }
}
