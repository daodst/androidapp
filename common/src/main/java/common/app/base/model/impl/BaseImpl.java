

package common.app.base.model.impl;

import java.util.Map;

import common.app.base.model.http.HttpMethods;
import common.app.base.model.http.bean.Request;
import common.app.base.pojo.BaseBean;
import retrofit2.Retrofit;



public class BaseImpl {

    protected Retrofit mRetrofit = HttpMethods.getInstance().getRetrofit();
    protected Retrofit mImRetrofit = HttpMethods.getInstance().getIMRetrofit();

    
    protected Request getRequest(BaseBean param) {
        return HttpMethods.getInstance().getRequest(param);
    }

    
    protected Request getRequest(String[] keys, String[] values) {
        return HttpMethods.getInstance().getRequest(keys, values);
    }

    
    protected Request getRequest(Map paraMap) {
        return HttpMethods.getInstance().getRequest(paraMap);
    }
}
