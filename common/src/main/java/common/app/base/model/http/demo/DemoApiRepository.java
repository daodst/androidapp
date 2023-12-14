

package common.app.base.model.http.demo;

import android.util.Log;

import java.util.Map;

import common.app.base.fragment.mall.model.VersionBean;
import common.app.base.model.http.HttpDataRepositoryBase;
import common.app.base.model.http.api.ApiService;
import common.app.base.model.http.bean.Request;
import common.app.base.model.http.bean.Result;
import common.app.base.model.http.callback.ApiNetResponse;
import io.reactivex.functions.Consumer;
import retrofit2.Retrofit;


public class DemoApiRepository extends HttpDataRepositoryBase {

    private static DemoApiRepository mDataRepository = null;
    private final String TAG = "ApiDataRepository";

    private DemoApiRepository() {
    }

    public static DemoApiRepository getInstance() {
        if (null == mDataRepository) {
            synchronized (DemoApiRepository.class) {
                if (null == mDataRepository) {
                    mDataRepository = new DemoApiRepository();
                }
            }
        }
        return mDataRepository;
    }


    
    @Override
    protected Retrofit useRetrofit() {
        return super.useRetrofit();
    }

    
    @Override
    protected ApiService useApiService() {
        return super.useApiService();
    }

    
    @Override
    protected Request useRequest(Map params) {
        return super.useRequest(params);
    }


    
    String DEMO_API = "basic/version/GetVersion";


    
    public void demoApi(Map params, ApiNetResponse<VersionBean> netResponse) {
        toRequestApi(getApiObservable(DEMO_API, params, netResponse).doOnNext(new Consumer<Result<VersionBean>>() {
            @Override
            public void accept(Result<VersionBean> versionBeanResultData) throws Exception {
                Log.d(TAG, "doOnNext()");
                if (null != versionBeanResultData && versionBeanResultData.isSuccess()) {
                    Log.d(TAG, "doOnNext()" + versionBeanResultData.getData().toString());
                }
            }
        }), netResponse);
    }

    
    public void demoApi2(Map params, ApiNetResponse<VersionBean> netResponse) {
        toRequestApi(DEMO_API, params, netResponse);
    }

    
    public void demoApi3(Object params, ApiNetResponse<VersionBean> netResponse) {
        toRequestApi(DEMO_API, params, netResponse);
    }

    
    public void demoApi3(String[] keys, String[] values, ApiNetResponse<VersionBean> netResponse) {
        toRequestApi(DEMO_API, keys, values, netResponse);
    }
}
