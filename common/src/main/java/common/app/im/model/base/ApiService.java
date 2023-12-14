

package common.app.im.model.base;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;


public interface ApiService {

    
    @FormUrlEncoded
    @POST()
    Observable<ResponseBody> executePost(@Url String url, @FieldMap Map<String, Object> maps);


    @GET()
    Observable<ResponseBody> executeGet(@Url String url);
}
