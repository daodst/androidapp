

package org.matrix.android.sdk.api.session.widgets

import android.webkit.WebView
import org.matrix.android.sdk.api.util.JsonDict
import java.lang.reflect.Type

interface WidgetPostAPIMediator {

    
    fun setWebView(webView: WebView)

    
    fun setHandler(handler: Handler?)

    
    fun clearWebView()

    
    fun injectAPI()

    
    fun sendBoolResponse(response: Boolean, eventData: JsonDict)

    
    fun sendIntegerResponse(response: Int, eventData: JsonDict)

    
    fun <T> sendObjectResponse(type: Type, response: T?, eventData: JsonDict)

    
    fun sendSuccess(eventData: JsonDict)

    
    fun sendError(message: String, eventData: JsonDict)

    interface Handler {
        
        fun handleWidgetRequest(mediator: WidgetPostAPIMediator, eventData: JsonDict): Boolean
    }
}
