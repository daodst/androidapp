

package common.app.base.fragment.mall.api;

import java.util.Map;

import common.app.base.fragment.mall.model.BaseEntity;
import common.app.base.model.http.HttpMethods;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;



public class CommonMallApi {
    private CommonMallService mService;
    private CommonMallService mService2;
    private RequestHelper mHelp=new RequestHelper();
    public CommonMallApi() {
        Retrofit retrofit = HttpMethods.getInstance().getRetrofit();
        mService = retrofit.create(CommonMallService.class);
        Retrofit retrofit2 = HttpMethods.getInstance().getRetrofit2();
        mService2 = retrofit2.create(CommonMallService.class);
    }

    public Observable<BaseEntity> getProductByBarCode(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService.getProductByBarCode(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getSupplyIndex(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService.getSupplyIndex(params).subscribeOn(Schedulers.io());
    }



}
