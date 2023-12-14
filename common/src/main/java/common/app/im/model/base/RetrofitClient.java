

package common.app.im.model.base;

import java.util.Map;

import common.app.base.model.http.HttpMethods;
import io.reactivex.Observable;
import retrofit2.Retrofit;



public class RetrofitClient {

    public static final String BASE_SITE = HttpMethods.BASE_SITE;

    public static final String BASE_URL = HttpMethods.BASE_URL;

    private static final int DEFAULT_TIMEOUT = 5;

    private static Retrofit mRetrofit;
    private static Retrofit mImRetrofit;

    private ApiService mApiService;
    private ApiService mImApiService;


    
    private RetrofitClient() {
        
        mRetrofit = HttpMethods.getInstance().getRetrofit();
        mApiService = mRetrofit.create(ApiService.class);

        mImRetrofit = HttpMethods.getInstance().getIMRetrofit();
        mImApiService = mImRetrofit.create(ApiService.class);
    }

    
    private static class SingletonHolder {
        private static final RetrofitClient INSTANCE = new RetrofitClient();
    }

    public static RetrofitClient getInstance() {
        return SingletonHolder.INSTANCE;
    }


    
    public Observable<ResponseBody> post(String url, Map<String, Object> parameters) {
        return mApiService.executePost(url, parameters);
    }

    public Observable<ResponseBody> get(String url) {
        return mApiService.executeGet(url);
    }


    
    public Observable<ResponseBody> postIm(String url, Map<String, Object> parameters) {
        return mImApiService.executePost(url, parameters);
    }


}
