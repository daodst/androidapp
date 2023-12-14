

package im.vector.app.provide.log

import com.zhuinden.monarchy.Monarchy
import io.realm.Sort
import io.realm.kotlin.where
import org.matrix.android.sdk.internal.database.model.ChatPhoneLog
import org.matrix.android.sdk.internal.database.model.ChatPhoneLogFields
import org.matrix.android.sdk.internal.di.SessionDatabase
import org.matrix.android.sdk.internal.session.log.ChatPhoneLogService
import timber.log.Timber
import javax.inject.Inject


class ChatPhoneLogSource @Inject constructor(@SessionDatabase private val monarchy: Monarchy) :
        ChatPhoneLogService {

    override fun insert(log: ChatPhoneLog) {
        monarchy.doWithRealm {
            it?.executeTransaction { transition ->
                Timber.i("====ChatPhoneLogSource======executeTransaction=========log=========")
                transition.insert(log)
            }
        }
    }

    
    override fun queryAll(status: Int): List<ChatPhoneLog> {
        return monarchy.fetchAllCopiedSync { realm ->
            if (status != 0) {
                
                val income = 2
                return@fetchAllCopiedSync realm.where<ChatPhoneLog>()
                        .beginGroup()
                        .equalTo(
                                ChatPhoneLogFields.STATUS,
                                status
                        ).and().isNotNull(ChatPhoneLogFields.ADDRESS)
                        .and().equalTo(
                                ChatPhoneLogFields.INCOME,
                                income
                        )
                        .endGroup()
                        .sort(ChatPhoneLogFields.TIME, Sort.DESCENDING)
            } else {
                return@fetchAllCopiedSync realm.where<ChatPhoneLog>().isNotNull(ChatPhoneLogFields.ADDRESS).sort(ChatPhoneLogFields.TIME, Sort.DESCENDING)
            }
        }
    }
}
