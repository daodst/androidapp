

package common.app.base.model.http.bean;

import com.google.gson.annotations.SerializedName;


public class Result<T> {

    
    public static final String KEY_STATUS = "status";
    public static final String KEY_INFO = "info";
    public static final String KEY_DATA = "data";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_DOWNLOAD = "download";


    @SerializedName("status")
    private int status;

    @SerializedName("info")
    private String info;

    
    @SerializedName("data")
    private T data;
    
    @SerializedName("message")
    private T message;

    
    private boolean isDownload = false;


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public T getData() {
        if(null==data&&null!=getMessage()){
            data=getMessage();
        }
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Boolean isSuccess() {
        return status == 0 ? false : true;
    }

    public boolean isDownload() {
        return isDownload;
    }

    public void setDownload(boolean download) {
        isDownload = download;
    }

    public T getMessage() {
        return message;
    }

    public void setMessage(T message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Result{" +
                "status=" + status +
                ", info='" + info + '\'' +
                ", data=" + data +
                '}';
    }
}
