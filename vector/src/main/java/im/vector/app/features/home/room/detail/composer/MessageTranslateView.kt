package im.vector.app.features.home.room.detail.composer

import android.content.Context
import android.text.format.DateUtils
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import dagger.hilt.android.AndroidEntryPoint
import im.vector.app.R
import im.vector.app.core.epoxy.onClick
import im.vector.app.core.hardware.vibrate
import im.vector.app.core.time.Clock
import im.vector.app.databinding.ComposerTranslateLayoutBinding
import im.vector.lib.core.utils.timer.CountUpTimer
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint class MessageTranslateView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(
        context,
        attrs,
        defStyleAttr
) {

    val views: ComposerTranslateLayoutBinding

    interface Callback {

        fun onLangChange(isChinese: Boolean)

        
        fun onVoiceRecordingStarted()

        
        fun onVoiceRecordingEnded()

        
        fun onVoiceRecordingTransEnded()

        
        fun onRecordingLimitReached()

        
        fun onVoiceRecordingTransClear()

        
        fun onSendVoiceMessage(content: String)

        
        fun onDeleteVoiceMessage()
    }

    var callback: Callback? = null
        set(value) {
            value?.onLangChange(isChinese)
            field = value
        }
        get() {
            return field
        }

    sealed interface TranslateRecordingUiState {
        
        object Idle : TranslateRecordingUiState

        
        data class Recording(val recordingStartTimestamp: Long) : TranslateRecordingUiState

        
        object Edit : TranslateRecordingUiState

        
        object Sending : TranslateRecordingUiState
    }

    init {
        orientation = VERTICAL
        inflate(context, R.layout.composer_translate_layout, this)

        views = ComposerTranslateLayoutBinding.bind(this)

        isClickable = true

        
        views.composerTranslateClose.onClick {
            
            callback?.onDeleteVoiceMessage()
        }

        
        views.composerTranslateArrow.onClick {
            callback?.onLangChange(!isChinese)
        }

        views.composerTranslateClear.onClick {
            callback?.onVoiceRecordingTransClear()
        }
        views.composerTranslateSend.onClick {
            callback?.onSendVoiceMessage(views.composerTranslateContent.text.toString().trim())
        }

        
        views.composerTranslateAction.onClick {
            if (lastKnownState == null) {
                callback?.onVoiceRecordingStarted()
                return@onClick
            }
            Timber.i("----MessageTranslateView-------------------------$lastKnownState---------")
            when (lastKnownState) {
                TranslateRecordingUiState.Idle         -> {
                    views.composerTranslateLoading.root.isVisible = true
                    views.composerTranslateLoading.loadingText.text = context.getString(R.string.translate_recording_init)
                    views.composerTranslateTips.text = context.getString(R.string.translate_recording_init)
                    callback?.onVoiceRecordingStarted()
                }
                is TranslateRecordingUiState.Recording -> {
                    callback?.onVoiceRecordingEnded()
                }
                
                is TranslateRecordingUiState.Edit      -> {
                }
                else                                   -> {}
            }
        }
    }

    private var isChinese: Boolean = true
    fun renderText(izChinese: Boolean) {
        isChinese = izChinese
        views.composerTranslateFrom.text = context.getString(if (izChinese) R.string.composer_translate_chinese else R.string.composer_translate_english)
        views.composerTranslateTo.text = context.getString(if (izChinese) R.string.composer_translate_english else R.string.composer_translate_chinese)
    }

    fun renderContent(content: String?) {
        views.composerTranslateContent.setText(content ?: "")
    }

    private var lastKnownState: TranslateRecordingUiState? = null

    fun render(recordingState: TranslateRecordingUiState) {

        if (lastKnownState == recordingState) return
        when (recordingState) {
            TranslateRecordingUiState.Idle         -> {
                views.composerTranslateContent.isEnabled = false
                reset()
            }
            is TranslateRecordingUiState.Recording -> {
                vibrate(context)
                
                views.composerTranslateArrow.isEnabled = false
                startRecordingTicker(startAt = recordingState.recordingStartTimestamp)
                views.composerTranslateLoading.root.isVisible = false
                views.composerTranslateContent.setText("")
                views.composerTranslateSend.isVisible = false
                views.composerTranslateClear.isVisible = false
                views.composerTranslateAction.setImageResource(R.drawable.composer_translate_recoding)
                
                views.composerTranslateTips.text = context.getString(R.string.translate_recording_idle)
                views.composerTranslateContent.isEnabled = false
            }
            is TranslateRecordingUiState.Edit      -> {
                views.composerTranslateContent.isEnabled = true
                stopRecordingTicker()
                views.composerTranslateLoading.root.isVisible = false
                views.composerTranslateSend.isVisible = true
                views.composerTranslateClear.isVisible = true
                views.composerTranslateAction.setImageResource(R.drawable.composer_translate_start)
                views.composerTranslateTips.text = context.getString(R.string.translate_recording_edit)
            }
            is TranslateRecordingUiState.Sending   -> {
                views.composerTranslateContent.isEnabled = false
                views.composerTranslateLoading.root.isVisible = true
                views.composerTranslateLoading.loadingText.isInvisible = true
                views.composerTranslateContent.setText("")
                views.composerTranslateSend.isVisible = false
                views.composerTranslateClear.isVisible = false
                views.composerTranslateAction.setImageResource(R.drawable.composer_translate_start)
                views.composerTranslateTips.text = context.getString(R.string.translate_recording_sending)
            }
        }

        lastKnownState = recordingState
    }

    @Inject lateinit var clock: Clock
    private var recordingDuration: Long = 0
    private var recordingTicker: CountUpTimer? = null
    private fun startRecordingTicker(startAt: Long) {
        val startMs = ((clock.epochMillis() - startAt)).coerceAtLeast(0)
        recordingTicker?.stop()
        recordingTicker = CountUpTimer().apply {
            tickListener = object : CountUpTimer.TickListener {
                override fun onTick(milliseconds: Long) {
                    onRecordingTick(milliseconds + startMs)
                }
            }
            resume()
        }
        onRecordingTick(milliseconds = startMs)
    }

    private fun onRecordingTick(milliseconds: Long) {
        renderRecordingTimer(milliseconds / 1_000)
    }

    fun renderRecordingTimer(recordingTimeMillis: Long) {
        val formattedTimerText = DateUtils.formatElapsedTime(recordingTimeMillis)
        views.composerTranslateTips.post {
            views.composerTranslateTips.text = formattedTimerText
        }
    }

    private fun stopRecordingTicker() {
        recordingDuration = recordingTicker?.elapsedTime() ?: 0
        recordingTicker?.stop()
        recordingTicker = null
    }

    private fun reset() {
        stopRecordingTicker()
        views.composerTranslateArrow.isEnabled = true
        views.composerTranslateLoading.root.isVisible = false
        views.composerTranslateLoading.loadingText.text = context.getString(R.string.create_loading_text)
        views.composerTranslateContent.setText("")
        views.composerTranslateSend.isVisible = false
        views.composerTranslateClear.isVisible = false
        views.composerTranslateAction.setImageResource(R.drawable.composer_translate_start)
        views.composerTranslateTips.text = context.getString(R.string.translate_recording_idle)
    }
}
