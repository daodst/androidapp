

@file:Suppress("UNUSED_PARAMETER")

package im.vector.app.features.badge

import android.content.Context
import android.os.Build
import me.leolin.shortcutbadger.ShortcutBadger
import org.matrix.android.sdk.api.session.Session


object BadgeProxy {

    
    private fun useShortcutBadger() = Build.VERSION.SDK_INT < Build.VERSION_CODES.O

    
    fun updateBadgeCount(context: Context, badgeValue: Int) {
        if (!useShortcutBadger()) {
            return
        }

        ShortcutBadger.applyCount(context, badgeValue)
    }

    
    fun specificUpdateBadgeUnreadCount(aSession: Session?, aContext: Context?) {
        if (!useShortcutBadger()) {
            return
        }

        
    }

    
    private fun updateBadgeCount(aSession: Session?, aContext: Context?) {
        if (!useShortcutBadger()) {
            return
        }

        
    }
}
