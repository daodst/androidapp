package org.matrix.android.sdk.internal.session.content

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.work.WorkerParameters
import com.squareup.moshi.JsonClass
import im.vector.lib.multipicker.entity.MultiPickerAudioType
import im.vector.lib.multipicker.utils.toMultiPickerAudioType
import org.matrix.android.sdk.api.session.content.ContentAttachmentData
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.utils.model.UtilsRpcUrl
import org.matrix.android.sdk.internal.SessionManager
import org.matrix.android.sdk.internal.crypto.store.IMXCryptoStore
import org.matrix.android.sdk.internal.session.DefaultFileService
import org.matrix.android.sdk.internal.session.SessionComponent
import org.matrix.android.sdk.internal.session.room.relation.EventEditor
import org.matrix.android.sdk.internal.session.room.send.CancelSendTracker
import org.matrix.android.sdk.internal.session.room.send.LocalEchoEventFactory
import org.matrix.android.sdk.internal.session.room.send.LocalEchoIdentifiers
import org.matrix.android.sdk.internal.session.room.send.LocalEchoRepository
import org.matrix.android.sdk.internal.session.room.send.MultipleEventSendingDispatcherWorker
import org.matrix.android.sdk.internal.session.tts.model.TranslateTask
import org.matrix.android.sdk.internal.util.TemporaryFileCreator
import org.matrix.android.sdk.internal.util.toMatrixErrorStr
import org.matrix.android.sdk.internal.worker.SessionSafeCoroutineWorker
import org.matrix.android.sdk.internal.worker.SessionWorkerParams
import org.matrix.android.sdk.internal.worker.WorkerParamsFactory
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject


internal class TranslateBeforeUploadWorker(val context: Context, params: WorkerParameters, sessionManager: SessionManager) : SessionSafeCoroutineWorker<TranslateBeforeUploadWorker.Params>(
        context, params, sessionManager, Params::class.java
) {

    
    @JsonClass(generateAdapter = true) internal data class Params(
            override val sessionId: String,
            
            val appId: String,
            val roomId: String,
            val fromText: String, val toText: String,
            val fromLan: String,
            val toLan: String,
            override val lastFailureMessage: String? = null,
            val sendStatus: ContentAttachmentData.SendStatus = ContentAttachmentData.SendStatus.INIT,
            val event: Event?) : SessionWorkerParams

    @Inject lateinit var translateTask: TranslateTask

    @Inject lateinit var fileService: DefaultFileService

    @Inject lateinit var cancelSendTracker: CancelSendTracker

    @Inject lateinit var localEchoRepository: LocalEchoRepository

    @Inject lateinit var temporaryFileCreator: TemporaryFileCreator

    @Inject lateinit var contentUploadStateTracker: DefaultContentUploadStateTracker
    @Inject lateinit var localEchoEventFactory: LocalEchoEventFactory
    @Inject lateinit var cryptoStore: IMXCryptoStore
    @Inject lateinit var eventEditor: EventEditor
    override fun injectWith(injector: SessionComponent) {
        injector.inject(this)
    }

    override suspend fun doSafeWork(params: Params): Result {
        Timber.v("Starting upload media work with params $params")
        
        return internalDoWork(params)
    }

    private val amplitudeList = mutableListOf<Int>()

    private fun initList() {
        amplitudeList.add(735)
        amplitudeList.add(521)
        amplitudeList.add(116)
        amplitudeList.add(59)
        amplitudeList.add(93)
        amplitudeList.add(85)
        amplitudeList.add(1574)
        amplitudeList.add(790)
        amplitudeList.add(987)
        amplitudeList.add(247)
        amplitudeList.add(1427)
        amplitudeList.add(1150)
        amplitudeList.add(885)
        amplitudeList.add(476)
        amplitudeList.add(138)
        amplitudeList.add(7300)
        amplitudeList.add(4219)
        amplitudeList.add(7716)
        amplitudeList.add(5995)
        amplitudeList.add(6355)
        amplitudeList.add(4903)
        amplitudeList.add(5800)
        amplitudeList.add(6238)
        amplitudeList.add(5055)
        amplitudeList.add(2841)
        amplitudeList.add(3616)
        amplitudeList.add(4271)
        amplitudeList.add(4727)
        amplitudeList.add(5787)
        amplitudeList.add(4776)
        amplitudeList.add(4093)
        amplitudeList.add(6689)
        amplitudeList.add(5598)
        amplitudeList.add(656)
        amplitudeList.add(278)
        amplitudeList.add(164)
        amplitudeList.add(5371)
        amplitudeList.add(7931)
        amplitudeList.add(4155)
        amplitudeList.add(4727)
        amplitudeList.add(5787)
        amplitudeList.add(4776)
        amplitudeList.add(4093)
        amplitudeList.add(6689)
        amplitudeList.add(5598)
        amplitudeList.add(656)
        amplitudeList.add(278)
        amplitudeList.add(164)
        amplitudeList.add(5371)
        amplitudeList.add(7931)
        amplitudeList.add(7590)
        amplitudeList.add(4155)
        amplitudeList.add(8485)
        amplitudeList.add(9232)
        amplitudeList.add(8192)
        amplitudeList.add(5185)
        amplitudeList.add(5943)
        amplitudeList.add(6364)
        amplitudeList.add(4059)
        amplitudeList.add(4049)
        amplitudeList.add(8476)
        amplitudeList.add(4299)
        amplitudeList.add(397)
        amplitudeList.add(287)
        amplitudeList.add(537)
    }

    
    private fun initContentAttachmentData(params: Params): ContentAttachmentData {
        return ContentAttachmentData(
                appid = params.appId,
                fromLan = params.fromLan,
                toLan = params.toLan,
                
                mimeType = "audio/wav",
                type = ContentAttachmentData.Type.CUS_VOICE_MESSAGE,
                formText = params.fromText,
                waveform = amplitudeList,
                
                
                sendStatus = params.sendStatus,
                
                toText = params.fromText,
                
                size = 0,
                
                name = "",
                
                duration = 0,
                
                queryUri = Uri.EMPTY,
        )
    }

    
    private suspend fun internalDoWork(params: Params): Result {

        
        initList()
        var attachment = initContentAttachmentData(params)
        Timber.i("----DefaultSendService--------------internalDoWork---------------------${params.sendStatus == ContentAttachmentData.SendStatus.INIT}------------")
        val fakeEvent = if (params.sendStatus == ContentAttachmentData.SendStatus.INIT && null == params.event) {
            
            localEchoEventFactory.createMediaEvent(
                    roomId = params.roomId, attachment = attachment, rootThreadEventId = null
            ).also { event ->
                createLocalEcho(event)
            }
        } else {
            params.event!!
        }

        val localEchoIdentifiers = LocalEchoIdentifiers(fakeEvent.roomId!!, fakeEvent.eventId!!)

        if (params.sendStatus == ContentAttachmentData.SendStatus.INIT) {
            
            val translate: String = try {
                translateTask.execute(TranslateTask.Params(params.fromText, params.fromLan))
            } catch (e: Throwable) {
                
                val uploadMediaWorkerParams = getErrorUploadParams(localEchoIdentifiers, params, attachment)
                return handleFailure(uploadMediaWorkerParams, e)
            }
            attachment = attachment.copy(
                    sendStatus = ContentAttachmentData.SendStatus.TRANSLATE,
                    
                    toText = translate,
            )

            
            val localEchoes = localEchoEventFactory.createMediaEvent(
                    roomId = params.roomId, attachment = attachment, rootThreadEventId = null
            )
            
            eventEditor.updateEchoWithEvent(roomId = params.roomId, fakeEvent.eventId, localEchoes)
        }



        try {
            val url: String = UtilsRpcUrl.getTranslateUrl() + "tts"
            val result = fileService.downloadCusFile(
                    fileName = UUID.randomUUID().toString() + ".wav",
                    mimeType = "audio/wav",
                    text = attachment.toText,
                    lan = params.toLan,
                    url = url,
            )

            val outputFileUri = FileProvider.getUriForFile(context, params.appId + ".fileProvider", result, "Voice message.${result.extension}")
            val type = outputFileUri.toMultiPickerAudioType(context)?.apply {
                waveform = amplitudeList
            }
            if (type == null) {
                val uploadMediaWorkerParams = getErrorUploadParams(localEchoIdentifiers, params, attachment)
                return handleFailure(uploadMediaWorkerParams, RuntimeException("outputFileUri can not get data "))
            }

            
            return handleSuccess(params, type.toContentAttachmentData(params.fromText, attachment.toText), localEchoIdentifiers)
        } catch (e: Throwable) {
            e.printStackTrace()
            val uploadMediaWorkerParams = getErrorUploadParams(localEchoIdentifiers, params, attachment)
            return handleFailure(uploadMediaWorkerParams, e)
        }
    }

    private fun getErrorUploadParams(localEchoIdentifiers: LocalEchoIdentifiers, params: Params, attachment: ContentAttachmentData): UploadContentWorker.Params {
        val localEchoIds = mutableListOf<LocalEchoIdentifiers>(localEchoIdentifiers)
        val isRoomEncrypted = cryptoStore.roomWasOnceEncrypted(params.roomId)
        return UploadContentWorker.Params(
                params.sessionId, localEchoIds = localEchoIds, attachment = attachment, isEncrypted = isRoomEncrypted, compressBeforeSending = false
        )
    }



    fun MultiPickerAudioType.toContentAttachmentData(formText: String, toText: String): ContentAttachmentData {
        if (mimeType == null) Timber.w("No mimeType")
        return ContentAttachmentData(
                mimeType = mimeType,
                type = ContentAttachmentData.Type.CUS_VOICE_MESSAGE,
                formText = formText,
                toText = toText,
                size = size,
                name = displayName,
                duration = duration,
                queryUri = contentUri,
                waveform = waveform,
                sendStatus = ContentAttachmentData.SendStatus.DOWNLOAD,
        )
    }

    private fun createLocalEcho(event: Event) {
        localEchoEventFactory.createLocalEcho(event)
    }

    private suspend fun handleSuccess(
            params: Params,
            attachment: ContentAttachmentData,
            localEchoIdentifiers: LocalEchoIdentifiers,
    ): Result {
        notifyTracker(params) { contentUploadStateTracker.setSuccess(it) }

        val localEchoes = localEchoEventFactory.createMediaEvent(
                roomId = params.roomId, attachment = attachment, rootThreadEventId = null
        )
        
        eventEditor.updateEchoWithEvent(roomId = params.roomId, localEchoIdentifiers.eventId, localEchoes)

        val isRoomEncrypted = cryptoStore.roomWasOnceEncrypted(params.roomId)

        val localEchoIds = mutableListOf(localEchoIdentifiers)
        val uploadMediaWorkerParams = UploadContentWorker.Params(
                params.sessionId, localEchoIds = localEchoIds, attachment = attachment, isEncrypted = isRoomEncrypted, compressBeforeSending = false
        )
        return Result.success(WorkerParamsFactory.toData(uploadMediaWorkerParams)).also {
            Timber.v("## handleSuccess ${attachment.queryUri}, work is stopped $isStopped")
        }
    }

    override fun buildErrorParams(params: Params, message: String): Params {
        return params.copy(lastFailureMessage = params.lastFailureMessage ?: message)
    }

    private fun handleFailure(params: UploadContentWorker.Params, failure: Throwable): Result {
        Timber.v("## handleFailure ${params}, work is stopped $isStopped")
        return Result.success(WorkerParamsFactory.toData(
                params.copy(
                        lastFailureMessage = failure.toMatrixErrorStr()
                )
        ).also {
            Timber.v("## handleFailure ${params}, work is stopped $isStopped")
        })
    }

    private fun notifyTracker(params: TranslateBeforeUploadWorker.Params, function: (String) -> Unit) {
    }
}
