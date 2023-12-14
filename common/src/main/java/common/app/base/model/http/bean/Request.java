

package common.app.base.model.http.bean;

import java.util.Map;

import okhttp3.RequestBody;



public class Request {

    public String sign;
    public String arg;
    public Map<String, Object> map;
    public String api;

    
    public RequestBody requestBody;

    public Request(String arg, String sign) {
        this.arg = arg;
        this.sign = sign;
    }

    public Request() {

    }

    @Override
    public String toString() {
        return "Request{" +
                "sign='" + sign + '\'' +
                ", arg='" + arg + '\'' +
                ", map=" + map +
                ", api='" + api + '\'' +
                '}';
    }
}
