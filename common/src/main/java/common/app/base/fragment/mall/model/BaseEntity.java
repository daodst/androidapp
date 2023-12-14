

package common.app.base.fragment.mall.model;



public class BaseEntity {
    private int status;
    private String message;
    private String info;
    private Object data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getInfo() {
        if((info==null||info.length()<1)&&(message!=null&&message.length()>0)){
            return message;
        }
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
