

package common.app.base.model.http.api;


import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;



public interface ApiService {


    
    @FormUrlEncoded
    @POST()
    Observable<JsonObject> executePost(@Url String url, @FieldMap Map<String, Object> maps);

    @FormUrlEncoded
    @POST()
    Observable<JsonObject> executePost(@Url String url, @FieldMap Map<String, Object> maps,@HeaderMap Map<String, Object> headermap);

    @POST()
    Observable<JsonObject> executePost(@Url String url, @Body RequestBody requestBody);

    @GET
    Observable<JsonObject> executeGet(@Url String fileUrl, @QueryMap Map<String, Object> maps);

    @Streaming
    @GET
    Call<ResponseBody> downloadFile(@Url String fileUrl);


    @Multipart
    @POST()
    Observable<JsonObject> uploadFile(@Url String url,
                                      @PartMap Map<String, RequestBody> map,
                                      @Part MultipartBody.Part file);

    @Multipart
    @POST()
    Observable<JsonObject> uploadFiles(@Url String url,
                                       @PartMap Map<String, RequestBody> map,
                                       @Part List<MultipartBody.Part> files);

    

    
    String UPLOAD_FILE = "user/file/Upload";

    String UPLOAD_VIDEO_FILE = "user/file/UploadVideo";

}
