

package common.app.im.model.base;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;


public class ResponseBody {
    @SerializedName("status")
    private int statusCode;

    @SerializedName("info")
    private String info;

    @SerializedName("data")
    private JsonElement data;

    
    public boolean isOk() {
        return 1 == statusCode && data != null;
    }


    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public JsonElement getData() {
        return data;
    }


    public String getEmptyData() {
        return 0 == statusCode ? "false" : "true";
    }


    @Override
    public String toString() {
        return "ResponseBody{" +
                "statusCode=" + statusCode +
                ", info='" + info + '\'' +
                ", data=" + data +
                '}';
    }
}
