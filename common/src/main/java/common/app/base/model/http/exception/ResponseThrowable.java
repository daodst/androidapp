

package common.app.base.model.http.exception;



public class ResponseThrowable extends Exception {
    private int code;
    private String errorInfo;
    private boolean needPush;

    
    public ResponseThrowable(Throwable throwable, String errorInfo, int code, boolean needPush) {
        super(throwable);
        this.code = code;
        this.errorInfo = errorInfo;
        this.needPush = needPush;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public boolean isNeedPush() {
        return needPush;
    }

    public void setNeedPush(boolean needPush) {
        this.needPush = needPush;
    }
}
