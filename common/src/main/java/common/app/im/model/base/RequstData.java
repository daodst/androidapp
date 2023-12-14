

package common.app.im.model.base;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import common.app.base.model.http.HttpMethods;
import common.app.im.exception.RemoteServerException;
import common.app.im.utils.ExceptionHandle;
import io.reactivex.Observable;



public class RequstData {

    public static final String REQUST_SPIT = "=-=-=-=-=-=";
    private static final String TAG = "RequstData";

    
    
    @Deprecated
    protected Map<String, Object> getRequest(BaseRequst param) {
        return HttpMethods.getInstance().getRequest(param).map;
    }


    
    public static Map<String, Object> getRequest(String[] keys, String[] values) {
        return HttpMethods.getInstance().getRequest(keys, values).map;
    }


    
    protected static Map<String, Object> getRequest(Map paraMap) {

        return HttpMethods.getInstance().getRequest(paraMap).map;
    }


    /
    public static <T> Observable<T> getResponseBody(String url, String[] keys, String[] values, TypeToken<T> typeToken) {
        Map<String, Object> request = RequstData.getRequest(keys, values);
        Observable<ResponseBody> post = RetrofitClient.getInstance().post(url, request);
        return prase(post, typeToken.getType(), request + REQUST_SPIT + url);
    }

    public static <T> Observable<T> getGetResponseBody(String url, TypeToken<T> typeToken) {
        Observable<ResponseBody> post = RetrofitClient.getInstance().get(url);
        return prase(post, typeToken.getType(), "" + REQUST_SPIT + url);
    }

    public static <T> Observable<T> getImResponseBody(String url, String[] keys, String[] values, TypeToken<T> typeToken) {
        Map<String, Object> request = RequstData.getRequest(keys, values);
        Observable<ResponseBody> post = RetrofitClient.getInstance().postIm(url, request);
        return prase(post, typeToken.getType(), request + REQUST_SPIT + url);
    }

    
    public static <T> Observable<T> getResponseBody(String url, TypeToken<T> typeToken) {
        return getResponseBody(url, null, null, typeToken);
    }

    
    public static <T> Observable<T> getResponseBody(String url, Map paramas, TypeToken<T> typeToken) {
        Map<String, Object> request = RequstData.getRequest(paramas);
        Observable<ResponseBody> post = RetrofitClient.getInstance().post(url, request);
        return prase(post, typeToken.getType(), request + REQUST_SPIT + url);
        
    }

    
    public static <T> Observable<T> getImResponseBody(String url, TypeToken<T> typeToken) {
        return getImResponseBody(url, null, null, typeToken);
    }


    
    public static <T> Observable<T> getResponseBody(String url, Class<T> clazz) {
        return getResponseBody(url, null, null, clazz);
    }

    public static <T> Observable<T> getGetResponseBody(String url, Class<T> clazz) {
        Observable<ResponseBody> post = RetrofitClient.getInstance().get(url);
        return prase(post, clazz, "" + REQUST_SPIT + url);
    }

    
    public static <T> Observable<T> getImResponseBody(String url, Class<T> clazz) {
        return getImResponseBody(url, null, null, clazz);
    }


    
    public static <T> Observable<T> getResponseBody(String url, String[] keys, String[] values, Class<T> clazz) {
        Map<String, Object> request = RequstData.getRequest(keys, values);
        Observable<ResponseBody> post = RetrofitClient.getInstance().post(url, request);
        return prase(post, clazz, request + REQUST_SPIT + url);
        
    }


    
    public static <T> Observable<T> getImResponseBody(String url, String[] keys, String[] values, Class<T> clazz) {
        Map<String, Object> request = RequstData.getRequest(keys, values);
        Observable<ResponseBody> post = RetrofitClient.getInstance().postIm(url, request);
        return prase(post, clazz, request + REQUST_SPIT + url);
        
    }

    public static <T> Observable<T> getImResponseBody(String url, Map params, Class<T> clazz) {
        Map<String, Object> request = RequstData.getRequest(params);
        Observable<ResponseBody> post = RetrofitClient.getInstance().postIm(url, request);
        return prase(post, clazz, request + REQUST_SPIT + url);
    }


    public static <T> Observable<T> getResponseBody(String url, Map map, Class<T> clazz) {
        Map<String, Object> request = RequstData.getRequest(map);
        Observable<ResponseBody> post = RetrofitClient.getInstance().post(url, request);
        return prase(post, clazz, request + REQUST_SPIT + url);
    }


    


    
    @Deprecated
    public static Observable<ResponseBody> getResponseBody(String url, String[] keys, String[] values) {
        return RetrofitClient.getInstance().post(url, RequstData.getRequest(keys, values));
    }

    
    @Deprecated
    public static Observable<ResponseBody> getResponseBody(String url) {
        return RetrofitClient.getInstance().post(url, RequstData.getRequest(null, null));
    }
}
