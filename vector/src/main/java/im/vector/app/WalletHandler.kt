

package im.vector.app

import android.content.Context
import android.content.Intent
import android.os.Bundle


class WalletHandler {

    companion object {

        
        fun getWalletLoginIntent(context: Context): Intent {
            return Intent(context, Class.forName("common.app.base.activity.ContentActivity")).apply {
                var bundle = Bundle()
                bundle.putInt("TYPE", 0)
                bundle.putString("CLASS", "com.app.lg4e.ui.fragment.login.WalletLoginFragment")
                putExtras(bundle)
            }
        }

        
        fun getRedPacketIntent(context: Context?, userid: String?): Intent {
            return Intent(context, Class.forName("com.wallet.ctc.ui.blockchain.transfer.TransferActivity")).apply {
                putExtra("userId", userid)
                putExtra("toAddress", userid)
                putExtra("from", 11)
                putExtra("tokenName", BuildConfig.EVMOS_FAKE_UNINT.lowercase())
            }
        }
    }
}
