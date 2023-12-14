

package org.matrix.android.sdk.internal.session.widgets

import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.squareup.moshi.Moshi
import org.matrix.android.sdk.api.session.widgets.WidgetPostAPIMediator
import org.matrix.android.sdk.api.util.JSON_DICT_PARAMETERIZED_TYPE
import org.matrix.android.sdk.api.util.JsonDict
import org.matrix.android.sdk.internal.util.createUIHandler
import timber.log.Timber
import java.lang.reflect.Type
import javax.inject.Inject

internal class DefaultWidgetPostAPIMediator @Inject constructor(private val moshi: Moshi,
                                                                private val widgetPostMessageAPIProvider: WidgetPostMessageAPIProvider) :
        WidgetPostAPIMediator {

    private val jsonAdapter = moshi.adapter<JsonDict>(JSON_DICT_PARAMETERIZED_TYPE)

    private var handler: WidgetPostAPIMediator.Handler? = null
    private var webView: WebView? = null

    private val uiHandler = createUIHandler()

    override fun setWebView(webView: WebView) {
        this.webView = webView
        webView.addJavascriptInterface(this, "Android")
    }

    override fun clearWebView() {
        webView?.removeJavascriptInterface("Android")
        webView = null
    }

    override fun setHandler(handler: WidgetPostAPIMediator.Handler?) {
        this.handler = handler
    }

    override fun injectAPI() {
        val js = widgetPostMessageAPIProvider.get()
        if (js != null) {
            uiHandler.post {
                webView?.loadUrl("javascript:$js")
            }
        }
    }

    @JavascriptInterface
    fun onWidgetEvent(jsonEventData: String) {
        Timber.d("BRIDGE onWidgetEvent : $jsonEventData")
        try {
            val dataAsDict = jsonAdapter.fromJson(jsonEventData)

            @Suppress("UNCHECKED_CAST")
            val eventData = (dataAsDict?.get("event.data") as? JsonDict) ?: return
            onWidgetMessage(eventData)
        } catch (e: Exception) {
            Timber.e(e, "## onWidgetEvent() failed")
        }
    }

    private fun onWidgetMessage(eventData: JsonDict) {
        try {
            if (handler?.handleWidgetRequest(this, eventData) == false) {
                sendError("", eventData)
            }
        } catch (e: Exception) {
            Timber.e(e, "## onWidgetMessage() : failed")
            sendError("", eventData)
        }
    }

    

    
    override fun sendBoolResponse(response: Boolean, eventData: JsonDict) {
        val jsString = if (response) "true" else "false"
        sendResponse(jsString, eventData)
    }

    
    override fun sendIntegerResponse(response: Int, eventData: JsonDict) {
        sendResponse(response.toString() + "", eventData)
    }

    
    override fun <T> sendObjectResponse(type: Type, response: T?, eventData: JsonDict) {
        var jsString: String? = null
        if (response != null) {
            val objectAdapter = moshi.adapter<T>(type)
            try {
                jsString = "JSON.parse('${objectAdapter.toJson(response)}')"
            } catch (e: Exception) {
                Timber.e(e, "## sendObjectResponse() : toJson failed ")
            }
        }
        sendResponse(jsString ?: "null", eventData)
    }

    
    override fun sendSuccess(eventData: JsonDict) {
        val successResponse = mapOf("success" to true)
        sendObjectResponse(Map::class.java, successResponse, eventData)
    }

    
    override fun sendError(message: String, eventData: JsonDict) {
        Timber.e("## sendError() : eventData $eventData failed $message")

        
        val params = HashMap<String, Map<String, String>>()
        val subMap = HashMap<String, String>()
        subMap["message"] = message
        params["error"] = subMap
        sendObjectResponse(Map::class.java, params, eventData)
    }

    
    private fun sendResponse(jsString: String, eventData: JsonDict) = uiHandler.post {
        try {
            val functionLine = "sendResponseFromRiotAndroid('" + eventData["_id"] + "' , " + jsString + ");"
            Timber.v("BRIDGE sendResponse: $functionLine")
            
            webView?.evaluateJavascript(functionLine, null)
        } catch (e: Exception) {
            Timber.e(e, "## sendResponse() failed ")
        }
    }
}
