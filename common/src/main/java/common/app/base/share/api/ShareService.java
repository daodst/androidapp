

package common.app.base.share.api;

import java.util.Map;

import common.app.base.fragment.mall.model.BaseEntity;
import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;



public interface ShareService {

    
    @FormUrlEncoded
    @POST("user/share/GetShareData")
    Observable<BaseEntity> getShare(@FieldMap Map<String ,Object> map);
}
