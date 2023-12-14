

package common.app.base.fragment.mall.catcherror;



public class ApiException extends RuntimeException {

    public static final int CODE_API_STATUS_FAIL = 0;
    private int errorCode;

    public ApiException(int code, String msg) {
        super(msg);
        this.errorCode = code;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
