

package common.app.base.fragment.mall.api;

import java.util.Map;

import common.app.base.fragment.mall.model.BaseEntity;
import common.app.base.model.http.HttpMethods;
import common.app.utils.SpUtil;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;



public class VerApi {
    private VerService mService;
    private RequestHelper mHelp = new RequestHelper();

    public VerApi() {
        Retrofit retrofit = HttpMethods.getInstance().getRetrofit();
        mService = retrofit.create(VerService.class);
    }

    public Observable<BaseEntity> getVersion(Map<String, Object> params) {
        params = mHelp.getMapParem(params);
        return mService.getVersion(params).subscribeOn(Schedulers.io());
    }

    public Observable<BaseEntity> getVersion2() {
        String url = getEvmosRpcUrl() + "gateway/apk/version";
        return mService.getVersion2(url).subscribeOn(Schedulers.io());
    }

    
    private String getEvmosRpcUrl() {
        return SpUtil.getDefNode(4);
    }

}
