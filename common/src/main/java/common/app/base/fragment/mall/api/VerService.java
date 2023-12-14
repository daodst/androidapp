

package common.app.base.fragment.mall.api;

import java.util.Map;

import common.app.base.fragment.mall.model.BaseEntity;
import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;



public interface VerService {
    
    @FormUrlEncoded
    @POST("basic/version/GetVersion")
    Observable<BaseEntity> getVersion(@FieldMap Map<String, Object> map);

    @GET
    Observable<BaseEntity> getVersion2(@Url String url);

}
