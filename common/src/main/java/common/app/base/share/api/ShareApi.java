

package common.app.base.share.api;

import java.util.Map;

import common.app.base.fragment.mall.api.RequestHelper;
import common.app.base.fragment.mall.model.BaseEntity;
import common.app.base.model.http.HttpMethods;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;



public class ShareApi {
    private ShareService mService;
    private RequestHelper mHelp=new RequestHelper();
    public ShareApi() {
        Retrofit retrofit = HttpMethods.getInstance().getRetrofit();
        mService = retrofit.create(ShareService.class);
    }

    public Observable<BaseEntity> getShare(Map<String, Object> params) {
        params=mHelp.getMapParem(params);
        return mService.getShare(params).subscribeOn(Schedulers.io());
    }
}
