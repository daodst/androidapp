

package common.app.model.net.okhttps;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;



public class ProgressHelper {
    
    public static OkHttpClient addProgressResponseListener(OkHttpClient client, final ProgressResponseListener progressListener){
        
        
        OkHttpClient clone = client;
        
        client.networkInterceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                
                Response originalResponse = chain.proceed(chain.request());
                
                return originalResponse.newBuilder()
                        .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                        .build();
            }
        });
        return client;
    }

    
    public static ProgressRequestBody addProgressRequestListener(RequestBody requestBody, ProgressRequestListener progressRequestListener){
        
        return new ProgressRequestBody(requestBody,progressRequestListener);
    }
}
