

package im.vector.lib.attachmentviewer

import android.content.Context
import android.view.MotionEvent
import kotlin.math.sqrt

class SwipeDirectionDetector(
        context: Context,
        private val onDirectionDetected: (SwipeDirection) -> Unit
) {

    private val touchSlop: Int = android.view.ViewConfiguration.get(context).scaledTouchSlop
    private var startX: Float = 0f
    private var startY: Float = 0f
    private var isDetected: Boolean = false

    fun handleTouchEvent(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN                          -> {
                startX = event.x
                startY = event.y
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                if (!isDetected) {
                    onDirectionDetected(SwipeDirection.NotDetected)
                }
                startY = 0.0f
                startX = startY
                isDetected = false
            }
            MotionEvent.ACTION_MOVE                          -> if (!isDetected && getEventDistance(event) > touchSlop) {
                isDetected = true
                onDirectionDetected(getDirection(startX, startY, event.x, event.y))
            }
        }
    }

    
    private fun getDirection(x1: Float, y1: Float, x2: Float, y2: Float): SwipeDirection {
        val angle = getAngle(x1, y1, x2, y2)
        return SwipeDirection.fromAngle(angle)
    }

    
    private fun getAngle(x1: Float, y1: Float, x2: Float, y2: Float): Double {
        val rad = Math.atan2((y1 - y2).toDouble(), (x2 - x1).toDouble()) + Math.PI
        return (rad * 180 / Math.PI + 180) % 360
    }

    private fun getEventDistance(ev: MotionEvent): Float {
        val dx = ev.getX(0) - startX
        val dy = ev.getY(0) - startY
        return sqrt((dx * dx + dy * dy).toDouble()).toFloat()
    }
}
