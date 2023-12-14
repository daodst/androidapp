

package common.app.base.fragment.mall.api;

import java.util.Map;

import common.app.base.fragment.mall.model.BaseEntity;
import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;



public interface CommonMallService {


    
    @FormUrlEncoded
    @POST(HttpUtil.PRODUCT_BAR_CODE)
    Observable<BaseEntity> getProductByBarCode(@FieldMap Map<String, Object> map);

    
    @FormUrlEncoded
    @POST(HttpUtil.SUPPLY_DETAIL)
    Observable<BaseEntity> getSupplyIndex(@FieldMap Map<String ,Object> map);


}
