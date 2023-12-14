package org.matrix.android.sdk.api.session.utils.model

import android.text.TextUtils
import com.tencent.mmkv.MMKV

private const val MCC_COIN = 4 
private const val DEF_NODE = "def_node" 

private const val KEY_CALL_URL = "KEY_call_URL";

private const val A_WALLETBRIDGEIMPL = "com.wallet.ctc.WalletBridgeImpl"

private const val KEY_WS_URL = "KEY_ws_url"
private const val TTS_URL = "tts_url"
object UtilsRpcUrl {

    fun getWsUrl(): String {
        return MMKV.defaultMMKV()?.decodeString(KEY_WS_URL, "") ?: ""
    }

    fun getUrl(): String {
        return getDefNode(MCC_COIN) ?: ""
    }

    fun getTranslateUrl(): String {
        if (false){
            
            return "http://192.168.10.10:25690/"
        }
        return MMKV.defaultMMKV()?.decodeString(TTS_URL, "") ?: ""
    }

    fun getChat23478(): String {
        return MMKV.defaultMMKV()?.decodeString(KEY_CALL_URL, "") ?: ""
    }

    private fun getDefNode(type: Int): String? {
        var url: String? = MMKV.defaultMMKV()?.decodeString(DEF_NODE + type, "")
        try {
            if (TextUtils.isEmpty(url)) {
                val aClass = Class.forName(A_WALLETBRIDGEIMPL)
                val newInstance = aClass.newInstance()
                val jsonRpc = aClass.getMethod("getJsonRpc", Int::class.java)
                jsonRpc.isAccessible = true
                url = jsonRpc.invoke(newInstance, type) as String
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
        return url
    }
}
