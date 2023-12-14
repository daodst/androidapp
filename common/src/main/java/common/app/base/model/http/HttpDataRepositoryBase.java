

package common.app.base.model.http;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.app.base.model.http.api.ApiService;
import common.app.base.model.http.bean.Request;
import common.app.base.model.http.bean.Result;
import common.app.base.model.http.callback.ApiNetResponse;
import common.app.base.model.http.config.HttpMethods;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;



public class HttpDataRepositoryBase {

    private static final String TAG = "HttpDataRepositoryBase";

    
    protected common.app.base.model.http.config.HttpMethods mHttpMethods;

    
    protected Retrofit mRetrofit;


    
    protected Retrofit mDownUpRetrofit;

    
    protected ApiService mApiService;


    
    private final String KEY_STATUS = Result.KEY_STATUS;
    private final String KEY_INFO = Result.KEY_INFO;
    private final String KEY_DATA = Result.KEY_DATA;
    private final String KEY_MESSAGE=Result.KEY_MESSAGE;
    private final String KEY_DOWNLOAD = Result.KEY_DOWNLOAD;

    protected Gson mGson;

    
    public HttpDataRepositoryBase() {
        mHttpMethods = common.app.base.model.http.config.HttpMethods.getInstance();
        mGson = HttpMethods.getInstance().getGson();
        mRetrofit = useRetrofit();
        mDownUpRetrofit = useDownUploadRetrofit();
        if (null == useApiService()) {
            mApiService = mRetrofit.create(ApiService.class);
        } else {
            mApiService = useApiService();
        }

    }

    
    protected Retrofit useRetrofit() {
        return mHttpMethods.getRetrofit();
    }

    
    protected ApiService useApiService() {
        return mApiService;
    }

    
    protected Retrofit useDownUploadRetrofit() {
        return mHttpMethods.getDownUpRetrofit();
    }

    
    protected Request useRequest(Map params) {
        return mHttpMethods.getRequest(params);
    }


    
    protected final <T> void toRequestApi(String api, String[] keys, String[] values, ApiNetResponse<T> netResponse) {
        
        toRequestApi(api, mHttpMethods.convertToMap(keys, values), netResponse);
    }

    
    protected final <T> void toRequestApi(String api, Object params, ApiNetResponse<T> netResponse) {
        
        toRequestApi(api, mHttpMethods.convertToMap(params), netResponse);
    }

    
    protected final <T> void toRequestApi(String api, Map params, ApiNetResponse<T> netResponse) {
        
        toRequestApi(api, params, netResponse, useRetrofit(), useApiService());
    }
    
    protected final <T> void toRequestApi(String api, Map params,Map heard, ApiNetResponse<T> netResponse) {
        
        toRequestApi(api, params,heard, netResponse, useRetrofit(), useApiService());
    }

    protected final <T> void toRequestApi(String api, RequestBody params, ApiNetResponse<T> netResponse) {
        
        toRequestApi(api, params,netResponse, useRetrofit(), useApiService());
    }
    
    protected final <T> void toGetRequestApi(String api, Map params, ApiNetResponse<T> netResponse) {
        
        toGetRequestApi(api, params,netResponse, useRetrofit(), useApiService());
    }




    
    private final <T> void toRequestApi(String api, Map params, ApiNetResponse<T> netResponse, Retrofit retrofit, ApiService apiService) {
        Request request = getAndSetRequest(api, params, retrofit, netResponse);
        
        Observable<JsonObject> sourceObservable = apiService.executePost(api, request.map);
        toDoRequestApi(sourceObservable, netResponse);
    }
    
    private final <T> void toRequestApi(String api, Map params,Map heard, ApiNetResponse<T> netResponse, Retrofit retrofit, ApiService apiService) {
        Request request = getAndSetRequest(api, params, retrofit, netResponse);
        
        Observable<JsonObject> sourceObservable = apiService.executePost(api, request.map,heard);
        toDoRequestApi(sourceObservable, netResponse);
    }
    
    private final <T> void toRequestApi(String api, RequestBody params,ApiNetResponse<T> netResponse, Retrofit retrofit, ApiService apiService) {
        
        Observable<JsonObject> sourceObservable = apiService.executePost(api, params);
        toDoRequestApi(sourceObservable, netResponse);
    }
    
    private final <T> void toGetRequestApi(String api, Map params,ApiNetResponse<T> netResponse, Retrofit retrofit, ApiService apiService) {
        Request request = getAndSetRequest(api, params, retrofit, netResponse);
        
        Observable<JsonObject> sourceObservable = apiService.executeGet(api, request.map);
        toDoRequestApi(sourceObservable, netResponse);
    }

    
    protected final <T> void toRequestApi(Observable<Result<T>> observable, final ApiNetResponse<T> response) {
        toSubscribe(observable, response);
    }

    
    protected final <T> Observable<Result<T>> getApiObservable(String api, String[] keys, String[] values, ApiNetResponse<T> netResponse) {
        return getApiObservable(api, mHttpMethods.convertToMap(keys, values), netResponse);
    }

    
    protected final <T> Observable<Result<T>> getApiObservable(String api, Object params, ApiNetResponse<T> netResponse) {
        return getApiObservable(api, mHttpMethods.convertToMap(params), netResponse);
    }

    
    protected <T> Observable<Result<T>> getApiObservable(String api, Map params, ApiNetResponse<T> netResponse) {
        return getApiObservable(api, params, netResponse, useRetrofit(), useApiService());
    }


    
    private final <T> Observable<Result<T>> getApiObservable(String api, Map params, ApiNetResponse<T> netResponse, Retrofit retrofit, ApiService apiService) {
        
        Request request = getAndSetRequest(api, params, retrofit, netResponse);
        
        Observable<JsonObject> sourceObservable = apiService.executePost(api, request.map);
        
        TypeToken token = getTypeToken(netResponse);
        return getObservable(sourceObservable, token);
    }


    
    private Request getAndSetRequest(String api, Map params, Retrofit retrofit, ApiNetResponse netResponse) {
        Request request = useRequest(params);
        request.api = retrofit.baseUrl().toString() + api;
        if (null != netResponse) {
            netResponse.setRequestInfo(request);
        }
        return request;
    }


    
    private final <T> Observable<Result<T>> toDoRequestApi(Observable<JsonObject> sourceObservable, ApiNetResponse<T> netResponse) {

        
        TypeToken token = getTypeToken(netResponse);

        
        return toSubscribe(getObservable(sourceObservable, token), netResponse);
    }

    
    private <T> TypeToken<T> getTypeToken(ApiNetResponse<T> netResponse) {
        
        Type type = null;
        Type genericType = netResponse.getClass().getGenericSuperclass();
        if (null != genericType && genericType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            if (parameterizedType.getActualTypeArguments() != null && parameterizedType.getActualTypeArguments().length > 0) {
                
                type = parameterizedType.getActualTypeArguments()[0];
            }
        }
        TypeToken token = null;
        if (null == type) {
            token = new TypeToken<Object>() {
            };
        } else {
            token = TypeToken.get(type);
        }
        return token;
    }


    
    private <T> Observable<Result<T>> getObservable(Observable<JsonObject> requsetObservable, final TypeToken<T> typeToken) {
        return requsetObservable.flatMap(new Function<JsonObject, ObservableSource<Result<T>>>() {
            @Override
            public ObservableSource<Result<T>> apply(JsonObject netResultData) throws Exception {
                Result<T> resultData = new Result<>();
                if (null == netResultData) {
                    resultData.setStatus(0);
                    resultData.setData(null);
                    resultData.setInfo("http data is null");
                    return Observable.just(resultData);
                }
                
                int statusCode = 0;
                if (netResultData.has(KEY_STATUS)) {
                    statusCode = netResultData.get(KEY_STATUS).getAsInt();
                }
                resultData.setStatus(statusCode);
                
                String info = "";
                if (netResultData.has(KEY_INFO)) {
                    JsonElement infoElement = netResultData.get(KEY_INFO);
                    if (null != infoElement && !(infoElement instanceof JsonNull)) {
                        info = infoElement.getAsString();
                    }
                }
                resultData.setInfo(info);

                if (netResultData.has(KEY_DOWNLOAD)) {
                    resultData.setDownload(true);
                }
                
                JsonElement jsonData = null;
                if (netResultData.has(KEY_DATA)) {
                    jsonData = netResultData.get(KEY_DATA);
                } else if (netResultData.has(KEY_MESSAGE)) {
                    jsonData = netResultData.get(KEY_MESSAGE);
                }

                
                T data = null;
                if (null != jsonData && null != typeToken) {
                    try {
                        data = mGson.fromJson(jsonData, typeToken.getType());
                    } catch (JsonSyntaxException e) {
                        
                        if (resultData.isSuccess()) {
                            
                            return Observable.error(e);
                        }
                    }
                }
                resultData.setData(data);

                
                return Observable.just(resultData);
            }
        });
    }
    
    private <T> Observable<Result<T>> getOssObservable(Observable<JsonObject> requsetObservable, final TypeToken<T> typeToken) {
        return requsetObservable.flatMap(new Function<JsonObject, ObservableSource<Result<T>>>() {
            @Override
            public ObservableSource<Result<T>> apply(JsonObject netResultData) throws Exception {
                Result<T> resultData = new Result<>();
                resultData.setStatus(1);
                resultData.setData(null);
                resultData.setInfo("OSS");
                return Observable.just(resultData);
            }
        });
    }


    
    private <T> Observable<Result<T>> toSubscribe(Observable<Result<T>> observable, final ApiNetResponse<T> response) {
        Observable<Result<T>> ob = observable.subscribeOn(Schedulers.io())
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.d(TAG, "onDispose()");
                        if (null != response) {
                            response.onDispose();
                        }
                    }
                })
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        
        if (null != response.getViewModel() && null != response.getViewModel().getTrasnform()) {
            ob.compose(response.getViewModel().getTrasnform())
                    .subscribe(response);
        } else {
            ob.subscribe(response);
        }
        return ob;
    }




    

    
    private Map<String, Call> mDownloadManagers = new HashMap<>();

    
    public void cancelAllDownloads() {
        for (Map.Entry<String, Call> entry : mDownloadManagers.entrySet()) {
            entry.getValue().cancel();
        }
        mDownloadManagers.clear();
    }

    
    public void cancelDownload(String downloadId) {
        if (TextUtils.isEmpty(downloadId)) {
            return;
        }
        if (mDownloadManagers.containsKey(downloadId)) {
            mDownloadManagers.get(downloadId).cancel();
            mDownloadManagers.remove(downloadId);
        }
    }

    
    public void downloadFile(final String downlodId, final String url, final String savePath, ApiNetResponse<String> netResponse) {
        Observable<JsonObject> observable = Observable.create(new ObservableOnSubscribe<JsonObject>() {
            @Override
            public void subscribe(final ObservableEmitter<JsonObject> sub) throws Exception {
                final JsonObject result = new JsonObject();
                result.addProperty(KEY_DOWNLOAD, 1);

                
                ApiService apiService = useDownUploadRetrofit().create(ApiService.class);

                Call<ResponseBody> call = apiService.downloadFile(url);
                
                Response<ResponseBody> response = null;
                try {
                    response = call.execute();
                } catch (IOException e) {
                    result.addProperty(KEY_STATUS, 0);
                    result.addProperty(KEY_INFO, e.toString());
                    sub.onNext(result);
                    sub.onComplete();
                    return;
                }

                
                if (null != response && response.isSuccessful()) {

                    if (!TextUtils.isEmpty(downlodId)) {
                        mDownloadManagers.put(downlodId, call);
                    }

                    ResponseBody body = response.body();
                    try {
                        downloadFile(body, savePath, new DownloadProcessCallBack() {
                            @Override
                            public void onProgress(int progress, boolean success) {
                                if (!success) {
                                    result.addProperty(KEY_STATUS, 2);
                                    result.addProperty(KEY_DATA, String.valueOf(progress).trim());
                                    sub.onNext(result);
                                } else {
                                    if (!TextUtils.isEmpty(downlodId) && mDownloadManagers.containsKey(downlodId)) {
                                        mDownloadManagers.remove(downlodId);
                                    }
                                    result.addProperty(KEY_STATUS, 1);
                                    result.addProperty(KEY_DATA, String.valueOf(progress).trim());
                                    sub.onNext(result);
                                    sub.onComplete();
                                }

                            }
                        });
                    } catch (IOException e) {
                        result.addProperty(KEY_STATUS, 0);
                        result.addProperty(KEY_INFO, e.getMessage());
                        sub.onNext(result);
                        sub.onComplete();

                        if (!TextUtils.isEmpty(downlodId) && mDownloadManagers.containsKey(downlodId)) {
                            mDownloadManagers.remove(downlodId);
                        }
                    }

                } else {
                    result.addProperty(KEY_STATUS, 0);
                    result.addProperty(KEY_INFO, "server contact failed");
                    sub.onNext(result);
                    sub.onComplete();
                }
            }
        });

        Request request = new Request();
        request.api = url;
        if (null != netResponse) {
            netResponse.setRequestInfo(request);
        }
        TypeToken<String> typeToken = new TypeToken<String>() {
        };
        toSubscribe(getObservable(observable, typeToken), netResponse);
    }


    interface DownloadProcessCallBack {
        void onProgress(int progress, boolean success);
    }

    private void downloadFile(ResponseBody body, String savePath, DownloadProcessCallBack callBack) throws IOException {
        int count;
        byte[] data = new byte[1024 * 4];
        long fileSize = body.contentLength();
        InputStream bis = new BufferedInputStream(body.byteStream(), 1024 * 8);
        File outputFile = new File(savePath);
        OutputStream output = new FileOutputStream(outputFile);
        long total = 0;
        long startTime = System.currentTimeMillis();
        int timeCount = 1;
        while ((count = bis.read(data)) != -1) {
            total += count;
            int progress = (int) ((total * 100) / fileSize);
            long currentTime = System.currentTimeMillis() - startTime;
            if (currentTime > 1000 * timeCount) {
                
                if (null != callBack) {
                    callBack.onProgress(progress, false);
                }
                timeCount++;
            }

            output.write(data, 0, count);
        }
        if (null != callBack) {
            callBack.onProgress(100, true);
        }
        output.flush();
        output.close();
        bis.close();
    }


    
    public <T> void uploadFile(String type, String filePath, ApiNetResponse<T> netResponse) {
        String[] keys = {"type", "formname"};
        String[] values = {type, "file"};
        Request request = mHttpMethods.getRequest(keys, values);

        File file = new File(filePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), requestFile);


        Map<String, RequestBody> map = new HashMap<>();

        for (Map.Entry<String, Object> entry : request.map.entrySet()) {
            RequestBody arg = RequestBody.create(MediaType.parse("text/plain"), entry.getValue().toString());
            map.put(entry.getKey(), arg);
        }

        
        ApiService apiService = useDownUploadRetrofit().create(ApiService.class);
        Observable<JsonObject> observable = apiService.uploadFile(ApiService.UPLOAD_FILE, map, filePart);
        request.api = useDownUploadRetrofit().baseUrl().toString() + ApiService.UPLOAD_FILE;
        if (null != netResponse) {
            netResponse.setRequestInfo(request);
        }
        TypeToken token = getTypeToken(netResponse);
        toSubscribe(getObservable(observable, token), netResponse);
    }

    
    public <T> void uploadOSSFile(String filePath,Map<String,Object> params,String host, ApiNetResponse<T> netResponse) {


        File file = new File(filePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), requestFile);


        Map<String, RequestBody> map = new HashMap<>();

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            RequestBody arg = RequestBody.create(MediaType.parse("text/plain"), entry.getValue().toString());
            map.put(entry.getKey(), arg);
        }

        
        ApiService apiService =mHttpMethods.getDownUpRetrofit().create(ApiService.class);
        Observable<JsonObject> observable = apiService.uploadFile(host, map, filePart);
        if (null != netResponse) {
            netResponse.setRequestInfo(mHttpMethods.getRequest(params));
        }
        TypeToken token = getTypeToken(netResponse);
        toSubscribe(getOssObservable(observable, token), netResponse);
    }

    
    public void uploadFiles(String type, List<String> filePaths, ApiNetResponse<List<String>> netResponse) {
        String[] keys = {"type", "formname"};
        String[] values = {type, "file"};
        Request request = mHttpMethods.getRequest(keys, values);
        Map<String, RequestBody> map = new HashMap<>();

        for (Map.Entry<String, Object> entry : request.map.entrySet()) {
            RequestBody arg = RequestBody.create(MediaType.parse("text/plain"), entry.getValue().toString());
            map.put(entry.getKey(), arg);
        }

        List<MultipartBody.Part> files = new ArrayList<>();
        File file = null;
        for (String filePath : filePaths) {
            file = new File(filePath);
            if (file.exists()) {
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                MultipartBody.Part filePart = MultipartBody.Part.createFormData("file[]", file.getName(), requestFile);
                files.add(filePart);
            }
        }
        
        ApiService apiService = useDownUploadRetrofit().create(ApiService.class);
        Observable<JsonObject> observable = apiService.uploadFiles(ApiService.UPLOAD_FILE, map, files);
        request.api = useDownUploadRetrofit().baseUrl().toString() + ApiService.UPLOAD_FILE;
        TypeToken<List<String>> typeToken = new TypeToken<List<String>>() {
        };
        if (null != netResponse) {
            netResponse.setRequestInfo(request);
        }
        toSubscribe(getObservable(observable, typeToken), netResponse);
    }
    
    
    public <T> void uploadVideoFile(String type, String filePath, ApiNetResponse<T> netResponse) {
        String[] keys = {"type", "formname"};
        String[] values = {type, "file"};
        Request request = mHttpMethods.getRequest(keys, values);

        File file = new File(filePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), requestFile);


        Map<String, RequestBody> map = new HashMap<>();

        for (Map.Entry<String, Object> entry : request.map.entrySet()) {
            RequestBody arg = RequestBody.create(MediaType.parse("text/plain"), entry.getValue().toString());
            map.put(entry.getKey(), arg);
        }

        
        ApiService apiService = useDownUploadRetrofit().create(ApiService.class);
        Observable<JsonObject> observable = apiService.uploadFile(ApiService.UPLOAD_VIDEO_FILE, map, filePart);
        request.api = useDownUploadRetrofit().baseUrl().toString() + ApiService.UPLOAD_VIDEO_FILE;
        if (null != netResponse) {
            netResponse.setRequestInfo(request);
        }
        TypeToken token = getTypeToken(netResponse);
        toSubscribe(getObservable(observable, token), netResponse);
    }

    public String escapeString(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return str;
        }
    }

    
    protected Map<String, Object> objectToMap(Object obj) {
        if (obj == null) {
            return null;
        }
        Map<String, Object> map;
        map = new HashMap<String, Object>();
        try {
            Field[] declaredFields = obj.getClass().getDeclaredFields();
            for (Field field : declaredFields) {
                field.setAccessible(true);
                map.put(field.getName(), field.get(obj));
            }
        } catch (Exception e) {

        }


        return map;
    }
}
