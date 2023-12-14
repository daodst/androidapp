

package common.app.base.fragment.mall.api;

import java.util.Map;

import common.app.base.model.http.HttpMethods;



public class RequestHelper {

    public Map<String,Object> getMapParem(Map<String, Object> map){
        return HttpMethods.getInstance().getRequest(map).map;
    }
}
