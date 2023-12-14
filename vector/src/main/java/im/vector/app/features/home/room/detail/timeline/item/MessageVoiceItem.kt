

package im.vector.app.features.home.room.detail.timeline.item

import android.content.res.ColorStateList
import android.graphics.Color
import android.text.format.DateUtils
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R
import im.vector.app.R2
import im.vector.app.core.epoxy.ClickListener
import im.vector.app.features.home.room.detail.timeline.helper.AudioMessagePlaybackTracker
import im.vector.app.features.home.room.detail.timeline.helper.ContentDownloadStateTrackerBinder
import im.vector.app.features.home.room.detail.timeline.helper.ContentUploadStateTrackerBinder
import im.vector.app.features.home.room.detail.timeline.style.TimelineMessageLayout
import im.vector.app.features.themes.ThemeUtils
import im.vector.app.features.voice.AudioWaveformView

@EpoxyModelClass(layout = R2.layout.item_timeline_event_base)
abstract class MessageVoiceItem : AbsMessageItem<MessageVoiceItem.Holder>() {

    interface WaveformTouchListener {
        fun onWaveformTouchedUp(percentage: Float)
        fun onWaveformMovedTo(percentage: Float)
    }

    @EpoxyAttribute
    var mxcUrl: String = ""

    @EpoxyAttribute
    var duration: Int = 0

    @EpoxyAttribute
    var waveform: List<Int> = emptyList()

    @EpoxyAttribute
    @JvmField
    var isLocalFile = false

    @EpoxyAttribute
    lateinit var contentUploadStateTrackerBinder: ContentUploadStateTrackerBinder

    @EpoxyAttribute
    lateinit var contentDownloadStateTrackerBinder: ContentDownloadStateTrackerBinder

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var playbackControlButtonClickListener: ClickListener? = null

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var waveformTouchListener: WaveformTouchListener? = null

    @EpoxyAttribute
    lateinit var audioMessagePlaybackTracker: AudioMessagePlaybackTracker

    override fun bind(holder: Holder) {
        super.bind(holder)
        val izGroup = baseAttributes.informationData.messageLayout.izGroup

        renderSendState(holder.voiceLayout, null)
        if (!attributes.informationData.sendState.hasFailed()) {
            contentUploadStateTrackerBinder.bind(attributes.informationData.eventId, isLocalFile, holder.progressLayout)
        } else {
            holder.voicePlaybackControlButton.setImageResource(R.drawable.ic_cross)
            holder.voicePlaybackControlButton.contentDescription = holder.view.context.getString(R.string.error_voice_message_unable_to_play)
            holder.progressLayout.isVisible = false
        }
        val context = holder.view.context
        holder.voicePlaybackControlButton.backgroundTintList = if (izGroup) {
            ColorStateList.valueOf(ContextCompat.getColor(context, R.color.color_group_pill))
        } else {
            ColorStateList.valueOf(ThemeUtils.getColor(context, android.R.attr.colorBackground))
        }
        

        val color = if (izGroup) {
            ContextCompat.getColor(context, R.color.black)
        } else {
            ThemeUtils.getColor(context, R.attr.vctr_content_secondary)
        }
        holder.voicePlaybackControlButton.setColorFilter(color)

        holder.voicePlaybackWaveform.doOnLayout {
            onWaveformViewReady(holder)
        }

        val backgroundTint = if (attributes.informationData.messageLayout is TimelineMessageLayout.Bubble) {
            Color.TRANSPARENT
        } else {
            ThemeUtils.getColor(holder.view.context, R.attr.vctr_content_quinary)
        }
        val colorTime = if (izGroup) {
            ContextCompat.getColor(context, R.color.black)
        } else {
            ThemeUtils.getColor(context, R.attr.vctr_content_secondary)
        }
        holder.voicePlaybackTime.setTextColor(colorTime)
        holder.voicePlaybackLayout.backgroundTintList = ColorStateList.valueOf(backgroundTint)
    }

    private fun onWaveformViewReady(holder: Holder) {
        holder.voicePlaybackWaveform.setOnLongClickListener(attributes.itemLongClickListener)
        holder.voicePlaybackControlButton.setOnClickListener { playbackControlButtonClickListener?.invoke(it) }

        val izGroup = baseAttributes.informationData.messageLayout.izGroup
        val waveformColorIdle = if (izGroup) {
            ContextCompat.getColor(holder.view.context, R.color.color_C2AE83)
        } else if (attributes.informationData.sentByMe) {
            ContextCompat.getColor(holder.view.context, R.color.voice_color_out)
        } else {
            ContextCompat.getColor(holder.view.context, R.color.voice_color_in)
        }
        val waveformColorPlayed = if (izGroup) {
            ContextCompat.getColor(holder.view.context, R.color.color_F1C15C)
        } else if (attributes.informationData.sentByMe) {
            ContextCompat.getColor(holder.view.context, R.color.voice_color_out_play)
        } else {
            ContextCompat.getColor(holder.view.context, R.color.voice_color_in_play)
        }


        holder.voicePlaybackWaveform.clear()
        waveform.forEach { amplitude ->
            holder.voicePlaybackWaveform.add(AudioWaveformView.FFT(amplitude.toFloat(), waveformColorIdle))
        }
        holder.voicePlaybackWaveform.summarize()

        holder.voicePlaybackWaveform.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_UP   -> {
                    val percentage = getTouchedPositionPercentage(motionEvent, view)
                    waveformTouchListener?.onWaveformTouchedUp(percentage)
                }
                MotionEvent.ACTION_MOVE -> {
                    val percentage = getTouchedPositionPercentage(motionEvent, view)
                    waveformTouchListener?.onWaveformMovedTo(percentage)
                }
            }
            true
        }

        audioMessagePlaybackTracker.track(attributes.informationData.eventId, object : AudioMessagePlaybackTracker.Listener {
            override fun onUpdate(state: AudioMessagePlaybackTracker.Listener.State) {
                when (state) {
                    is AudioMessagePlaybackTracker.Listener.State.Idle      -> renderIdleState(holder, waveformColorIdle, waveformColorPlayed)
                    is AudioMessagePlaybackTracker.Listener.State.Playing   -> renderPlayingState(holder, state, waveformColorIdle, waveformColorPlayed)
                    is AudioMessagePlaybackTracker.Listener.State.Paused    -> renderPausedState(holder, state, waveformColorIdle, waveformColorPlayed)
                    is AudioMessagePlaybackTracker.Listener.State.Recording -> Unit
                }
            }
        })
    }

    private fun getTouchedPositionPercentage(motionEvent: MotionEvent, view: View) = (motionEvent.x / view.width).coerceIn(0f, 1f)

    private fun renderIdleState(holder: Holder, idleColor: Int, playedColor: Int) {
        holder.voicePlaybackControlButton.setImageResource(R.drawable.ic_play_pause_play)
        holder.voicePlaybackControlButton.contentDescription = holder.view.context.getString(R.string.a11y_play_voice_message)
        holder.voicePlaybackTime.text = formatPlaybackTime(duration)
        holder.voicePlaybackWaveform.updateColors(0f, playedColor, idleColor)
    }

    private fun renderPlayingState(holder: Holder, state: AudioMessagePlaybackTracker.Listener.State.Playing, idleColor: Int, playedColor: Int) {
        holder.voicePlaybackControlButton.setImageResource(R.drawable.ic_play_pause_pause)
        holder.voicePlaybackControlButton.contentDescription = holder.view.context.getString(R.string.a11y_pause_voice_message)
        holder.voicePlaybackTime.text = formatPlaybackTime(state.playbackTime)
        holder.voicePlaybackWaveform.updateColors(state.percentage, playedColor, idleColor)
    }

    private fun renderPausedState(holder: Holder, state: AudioMessagePlaybackTracker.Listener.State.Paused, idleColor: Int, playedColor: Int) {
        holder.voicePlaybackControlButton.setImageResource(R.drawable.ic_play_pause_play)
        holder.voicePlaybackControlButton.contentDescription = holder.view.context.getString(R.string.a11y_play_voice_message)
        holder.voicePlaybackTime.text = formatPlaybackTime(state.playbackTime)
        holder.voicePlaybackWaveform.updateColors(state.percentage, playedColor, idleColor)
    }

    private fun formatPlaybackTime(time: Int) = DateUtils.formatElapsedTime((time / 1000).toLong())

    override fun unbind(holder: Holder) {
        super.unbind(holder)
        contentUploadStateTrackerBinder.unbind(attributes.informationData.eventId)
        contentDownloadStateTrackerBinder.unbind(mxcUrl)
        audioMessagePlaybackTracker.untrack(attributes.informationData.eventId)
    }

    override fun getViewStubId() = STUB_ID

    class Holder : AbsMessageItem.Holder(STUB_ID) {
        val voicePlaybackLayout by bind<View>(R.id.voicePlaybackLayout)
        val voiceLayout by bind<ViewGroup>(R.id.voiceLayout)
        val voicePlaybackControlButton by bind<ImageButton>(R.id.voicePlaybackControlButton)
        val voicePlaybackTime by bind<TextView>(R.id.voicePlaybackTime)
        val voicePlaybackWaveform by bind<AudioWaveformView>(R.id.voicePlaybackWaveform)
        val progressLayout by bind<ViewGroup>(R.id.messageFileUploadProgressLayout)
    }

    companion object {
        
        private var STUB_ID = R.id.messageContentVoiceStub
    }
}
